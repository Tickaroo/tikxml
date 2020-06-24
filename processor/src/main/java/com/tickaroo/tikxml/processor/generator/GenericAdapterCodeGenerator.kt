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
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.field.PolymorphicTypeElementNameMatcher
import com.tickaroo.tikxml.processor.scanning.getXmlElementName
import com.tickaroo.tikxml.processor.utils.getSurroundingClassQualifiedName
import com.tickaroo.tikxml.typeadapter.TypeAdapter
import java.io.IOException
import java.util.Optional
import javax.annotation.processing.Filer
import javax.annotation.processing.FilerException
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@ExperimentalStdlibApi
class GenericAdapterCodeGenerator(
  private val filer: Filer,
  private val typeUtils: Types,
  private val elementUtils: Elements) {

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
            .addMethod(generateToXml(typeAdapterObject, implementationNames))

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
      .beginControlFlow("if (!${CodeGeneratorHelper.readerParam}.hasElement())")
      .addStatement("return null")
      .endControlFlow()
      .addStatement("${CodeGeneratorHelper.readerParam}.beginElement()")
      .addStatement("elementName = ${CodeGeneratorHelper.readerParam}.nextElementName()")
      /*.nextControlFlow("else if (${CodeGeneratorHelper.readerParam}.peek() == XmlReader.XmlToken.ELEMENT_END)")
      .addStatement("return null")*/
      .nextControlFlow("else")
      .addStatement("elementName = reader.getCurrentElementName()")
      .endControlFlow()

    implementationNames.forEachIndexed { index, implementationName ->
      val implTypeName = elementUtils.getTypeElement(implementationName)
      val xmLElementName = (implTypeName as TypeElement).getXmlElementName()

      codeBlockBuilder.run {
        val equalsCheck = "elementName.equals(\"$xmLElementName\")"
        if (index == 0) beginControlFlow("if ($equalsCheck)") else nextControlFlow("else if ($equalsCheck)")
        addStatement("${CodeGeneratorHelper.valueParam} = (\$T) config.getTypeAdapter(\$T.class).fromXml(reader, config, false)",
          implTypeName, implTypeName)
      }
    }

    val codeBlock = codeBlockBuilder
      .nextControlFlow("else if (${CodeGeneratorHelper.tikConfigParam}.exceptionOnUnreadXml())")
      .addStatement(
        "throw new \$T(\"Could not map the xml element with the tag name <\" + elementName + \"> at path '\" + reader.getPath()+\"' to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().\")",
        IOException::class.java)
      .nextControlFlow("else")
      .beginControlFlow("while (${CodeGeneratorHelper.readerParam}.hasAttribute())")
      .addStatement("${CodeGeneratorHelper.readerParam}.skipAttribute()")
      .endControlFlow()
      .beginControlFlow("if (${CodeGeneratorHelper.readerParam}.hasElement())")
      .addStatement("${CodeGeneratorHelper.readerParam}.beginElement()")
      .addStatement("${CodeGeneratorHelper.readerParam}.skipRemainingElement()")
      .endControlFlow()
      .addStatement("${CodeGeneratorHelper.readerParam}.endElement()")
      .endControlFlow()
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

  private fun generateToXml(valueTypeName: TypeName, implementationNames: Set<String>): MethodSpec {
    val codeBlockBuilder = CodeBlock.builder()

    val typeElementNameMatcher = mutableListOf<PolymorphicTypeElementNameMatcher>()
    implementationNames.forEach {
      val implTypeName = elementUtils.getTypeElement(it)
      val xmlElementName = (implTypeName as TypeElement).getXmlElementName()
      // type element name matcher for name already exists
      val duplicateTypElementNameMatcher = typeElementNameMatcher.firstOrNull { nameMatcher -> nameMatcher.xmlElementName == xmlElementName }
      if (duplicateTypElementNameMatcher != null) {
        throw ProcessingException(null, "The xmlElementName '$xmlElementName' for field '$it' is already used by '${duplicateTypElementNameMatcher.type}'")
      }
      typeElementNameMatcher.add(PolymorphicTypeElementNameMatcher(xmlElementName, implTypeName.asType()))
    }

    val orderElements = orderByInheritanceHierarchy(typeElementNameMatcher, elementUtils, typeUtils)

    orderElements.forEachIndexed { index, elementNameMatcher ->
      if (index == 0) {
        codeBlockBuilder.beginControlFlow("if (value instanceof \$T)", elementNameMatcher.type)
      } else {
        codeBlockBuilder.nextControlFlow("else if (value instanceof \$T)", elementNameMatcher.type)
      }

      codeBlockBuilder.addStatement(
        "${CodeGeneratorHelper.tikConfigParam}.getTypeAdapter(\$T.class).toXml(${CodeGeneratorHelper.writerParam}, ${CodeGeneratorHelper.tikConfigParam}, (\$T) ${CodeGeneratorHelper.valueParam}, \$T.ofNullable(overridingXmlElementTagName).orElse(\$S))",
        elementNameMatcher.type, elementNameMatcher.type, Optional::class.java, elementNameMatcher.xmlElementName)
    }

    val codeBlock = codeBlockBuilder
      .nextControlFlow("else")
      .addStatement("throw new \$T(\$S + value + \$S)", ClassName.get(IOException::class.java),
        "Don't know how to write the element of type ",
        " as XML. Most likely you have forgotten to register for this type with @${ElementNameMatcher::class.simpleName} when resolving polymorphism.")
      .endControlFlow()
      .build()

    return MethodSpec.methodBuilder("toXml")
      .addAnnotation(Override::class.java)
      .addModifiers(PUBLIC)
      .addParameter(XmlWriter::class.java, "writer")
      .addParameter(TikXmlConfig::class.java, "config")
      .addParameter(valueTypeName, "value")
      .addParameter(String::class.java, "overridingXmlElementTagName")
      .addException(IOException::class.java)
      .addCode(codeBlock)
      .build()
  }

}