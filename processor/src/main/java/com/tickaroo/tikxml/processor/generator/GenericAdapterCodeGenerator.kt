package com.tickaroo.tikxml.processor.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.tickaroo.tikxml.TikXmlConfig
import com.tickaroo.tikxml.XmlReader
import com.tickaroo.tikxml.XmlWriter
import com.tickaroo.tikxml.typeadapter.TypeAdapter
import java.io.IOException
import java.util.Locale
import javax.annotation.processing.Filer
import javax.annotation.processing.FilerException
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.util.Elements

@ExperimentalStdlibApi
class GenericAdapterCodeGenerator(private val filer: Filer, private val elementUtils: Elements) {

  fun generateCode(genericTypes: Map<String, Set<String>?>) {
    genericTypes
      .filter { (it.value?.size ?: 0) > 0 } // only generate generic adapters if at least one implementation is defined
      .forEach { (genericTypeName, implementationNames) ->
        val typeAdapterObject = ClassName.get(elementUtils.getTypeElement(genericTypeName))
        val typeAdapterInterface = ParameterizedTypeName.get(ClassName.get(TypeAdapter::class.java), typeAdapterObject)

        val adapterClassBuilder =
          TypeSpec
            .classBuilder("${typeAdapterObject.simpleName()}${TypeAdapter.GENERATED_GENERIC_CLASS_SUFFIS}")
            .addSuperinterface(typeAdapterInterface)
            .addModifiers(PUBLIC)
            .addMethod(generateFromXml(typeAdapterObject, implementationNames!!))
            .addMethod(generateTooXml(typeAdapterObject))

        try {
          val javaFile = JavaFile.builder(typeAdapterObject.packageName(), adapterClassBuilder.build()).build()
          javaFile.writeTo(filer)
        } catch (e: FilerException) {

        }
      }
  }

  private fun generateFromXml(returnTypeName: TypeName, implementationNames: Set<String>): MethodSpec {
    val codeBlockBuilder = CodeBlock.builder()
      .addStatement("\$T ${CodeGeneratorHelper.valueParam} = null", returnTypeName)
      .addStatement("\$T elementName", String::class.java)
      .beginControlFlow("if (isGenericList)")
      .beginControlFlow("while (${CodeGeneratorHelper.readerParam}.hasAttribute())")
      .addStatement("${CodeGeneratorHelper.readerParam}.skipAttribute()")
      .endControlFlow()
      .addStatement("${CodeGeneratorHelper.readerParam}.beginElement()")
      .addStatement("elementName = reader.nextElementName()")
      .nextControlFlow("else")
      .addStatement("elementName = reader.getCurrentElementName()")
      .endControlFlow()

    implementationNames.forEachIndexed { index, implementationName ->
      val lastIndexOfPoint = implementationName.lastIndexOf(".")
      val implPackageName = implementationName.substring(0, lastIndexOfPoint)
      val implClassName = implementationName.substring(lastIndexOfPoint + 1, implementationName.length)
      val implTypeName = ClassName.get(implPackageName, implClassName)

      codeBlockBuilder.run {
        val equalsCheck = "elementName.equals(\"${implClassName.decapitalize(Locale.GERMANY)}\")"
        if (index == 0) beginControlFlow("if ($equalsCheck)") else nextControlFlow("else if ($equalsCheck)")
        addStatement("${CodeGeneratorHelper.valueParam} = (\$T) config.getTypeAdapter(\$T.class).fromXml(reader, config, false)", implTypeName, implTypeName)
      }
      /*val lastIndexOfPoint = implementationName.lastIndexOf(".")
      val packageName = implementationName.substring(0, lastIndexOfPoint)
      val className = implementationName.substring(lastIndexOfPoint + 1, implementationName.length)

      val typeAdapterRetrieverImpl = CodeBlock.builder()
        .add("this.\$N.put(\"\$N\", new \$T() {\$W", GENERIC_TYPEADAPTER_MAP, className.decapitalize(Locale.GERMANY),
          TypeAdapterRetriever::class.java)
        .beginControlFlow("@\$T public \$T getTypeAdapter(\$T config) throws \$T", Override::class.java,
          TypeAdapter::class.java, TikXmlConfig::class.java, TypeAdapterNotFoundException::class.java)
        .addStatement("return config.getTypeAdapter(\$T.class)", ClassName.get(packageName, className))
        .endControlFlow()
        .addStatement("})")
        .build()

      constructorBuilder
        .addCode(typeAdapterRetrieverImpl)*/
    }

    val codeBlock = codeBlockBuilder
      .nextControlFlow("else if (${CodeGeneratorHelper.tikConfigParam}.exceptionOnUnreadXml())")
      .addStatement("throw new \$T(\"Could not map the xml element with the tag name <\" + elementName + \"> at path '\" + reader.getPath()+\"' to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().\")", IOException::class.java)
      .nextControlFlow("else")
      //.addStatement("${CodeGeneratorHelper.readerParam}.beginElement()")
      .addStatement("${CodeGeneratorHelper.readerParam}.skipRemainingElement()")
      //.addStatement("${CodeGeneratorHelper.readerParam}.endElement()")
      .endControlFlow()
      //.endControlFlow()
      .build()

    return MethodSpec.methodBuilder("fromXml")
      .addAnnotation(Override::class.java)
      .addModifiers(PUBLIC)
      .addParameter(XmlReader::class.java, CodeGeneratorHelper.readerParam)
      .addParameter(TikXmlConfig::class.java, "config")
      .addParameter(Boolean::class.java, "isGenericList")
      .addException(IOException::class.java)
      .addCode(codeBlock)
      .addStatement("return ${CodeGeneratorHelper.valueParam}")
      .returns(returnTypeName)
      .build()
  }

  private fun generateTooXml(valueTypeName: TypeName): MethodSpec {
    return MethodSpec.methodBuilder("toXml")
      .addAnnotation(Override::class.java)
      .addModifiers(PUBLIC)
      .addParameter(XmlWriter::class.java, "writer")
      .addParameter(TikXmlConfig::class.java, "config")
      .addParameter(valueTypeName, "value")
      .addParameter(String::class.java, "overridingXmlElementTagName")
      .addException(IOException::class.java)
      .build()
  }

}