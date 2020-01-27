/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tickaroo.tikxml.processor.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.tickaroo.tikxml.TikXmlConfig
import com.tickaroo.tikxml.XmlReader
import com.tickaroo.tikxml.XmlWriter
import com.tickaroo.tikxml.processor.field.AnnotatedClass
import com.tickaroo.tikxml.processor.field.Namespace
import com.tickaroo.tikxml.processor.field.PolymorphicSubstitutionField
import com.tickaroo.tikxml.processor.field.PolymorphicSubstitutionListField
import com.tickaroo.tikxml.processor.utils.isList
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import com.tickaroo.tikxml.typeadapter.AttributeBinder
import com.tickaroo.tikxml.typeadapter.ChildElementBinder
import com.tickaroo.tikxml.typeadapter.TypeAdapter
import java.io.IOException
import java.util.Locale
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * This class takes an [com.tickaroo.tikxml.processor.field.AnnotatedClass] as input
 * and generates a [com.tickaroo.tikxml.TypeAdapter] (java code) for it
 * @author Hannes Dorfmann
 * @since 1.0
 */
@ExperimentalStdlibApi
class TypeAdapterCodeGenerator(
  private val filer: Filer,
  private val elementUtils: Elements,
  private val typeUtils: Types,
  private val typeConvertersForPrimitives: Set<String>,
  private val mapImpl: String?) {

  /**
   * The name of the class that holds some value when we have to parse xml into a constructor
   */
  private val VALUE_HOLDER_CLASS_NAME = "ValueHolder"
  private val namespaceDefinitionPrefix = "xmlns"

  /**
   * Generates an [com.tickaroo.tikxml.TypeAdapter] for the given class
   */
  fun generateCode(annotatedClass: AnnotatedClass) {

    val parseIntoValueType = getClassToParseInto(annotatedClass)
    val genericParamTypeAdapter =
      ParameterizedTypeName.get(ClassName.get(TypeAdapter::class.java), ClassName.get(annotatedClass.element))

    val customTypeConverterManager = CustomTypeConverterManager()
    val codeGenUtils =
      CodeGeneratorHelper(customTypeConverterManager, typeConvertersForPrimitives, parseIntoValueType, elementUtils, typeUtils)

    val constructorBuilder = MethodSpec.constructorBuilder()
      .addModifiers(PUBLIC)
      .addCode(codeGenUtils.generateAttributeBinders(annotatedClass))


    for ((xmlName, xmlElement) in annotatedClass.childElements) {
      if (xmlElement is PolymorphicSubstitutionField) {
        val childElementBinderPrefix = if (xmlElement is PolymorphicSubstitutionListField) {
          xmlElement.element.simpleName
        } else {
          val orginalElementTypeName = xmlElement.originalElementTypeMirror.toString().split(".").last()
          "${orginalElementTypeName.substring(0, 1).toLowerCase(Locale.GERMANY)}${orginalElementTypeName.substring(1,
            orginalElementTypeName.length)}"
        }
        constructorBuilder.addStatement("${CodeGeneratorHelper.childElementBindersParam}.put(\$S, \$N)", xmlName,
          "${childElementBinderPrefix}ChildElementBinder")
      } else {
        constructorBuilder.addStatement("${CodeGeneratorHelper.childElementBindersParam}.put(\$S, \$L)", xmlName,
          xmlElement.generateReadXmlCode(codeGenUtils))
      }
    }

    //
    // Generate code
    //
    val adapterClassBuilder = TypeSpec.classBuilder(annotatedClass.simpleClassName + TypeAdapter.GENERATED_CLASS_SUFFIX)
      .addModifiers(PUBLIC)
      .addSuperinterface(genericParamTypeAdapter)

    // Uses an annotated constructor, so generate a value holder class
    val annotatedConstructor = annotatedClass.annotatedConstructor
    if (annotatedConstructor != null) {
      val valueHolderBuilder = TypeSpec.classBuilder(VALUE_HOLDER_CLASS_NAME).addModifiers(Modifier.STATIC)
      annotatedConstructor.parameters.forEach {
        valueHolderBuilder.addField(ClassName.get(it.asType()), it.simpleName.toString())
      }
      adapterClassBuilder.addType(valueHolderBuilder.build())
    }

    generateFields(annotatedClass, adapterClassBuilder, customTypeConverterManager)

    adapterClassBuilder.addMethod(constructorBuilder.build())
      .addMethod(generateFromXmlMethod(annotatedClass).build())
      .addMethod(generateToXmlMethod(annotatedClass, codeGenUtils).build())

    val packageElement = elementUtils.getPackageOf(annotatedClass.element)
    val packageName = if (packageElement.isUnnamed) "" else packageElement.qualifiedName.toString()

    val javaFile = JavaFile.builder(packageName, adapterClassBuilder.build()).build()
    javaFile.writeTo(filer)
  }

  /**
   * Generates the fields
   */
  private fun generateFields(annotatedClass: AnnotatedClass, adapterClassBuilder: TypeSpec.Builder,
    customTypeConverterManager: CustomTypeConverterManager) {

    val targetClassToParseInto = getClassToParseInto(annotatedClass)

    val mapImplClass: Class<*> = if (mapImpl != null) {
      // TODO improve that one.
      // What if class is not compiled yet so that Class.forName() throws not found exception?
      // Now compiler will crash.
      Class.forName(mapImpl)
    } else {
      HashMap::class.java
    }

    if (annotatedClass.hasAttributes()) {
      val attributeBinderMapField = ParameterizedTypeName.get(ClassName.get(java.util.Map::class.java),
        ClassName.get(String::class.java),
        ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), targetClassToParseInto))
      val attributeBinderHashMapField = ParameterizedTypeName.get(ClassName.get(mapImplClass),
        ClassName.get(String::class.java),
        ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), targetClassToParseInto))

      adapterClassBuilder.addField(
        FieldSpec.builder(attributeBinderMapField, CodeGeneratorHelper.attributeBindersParam, PRIVATE)
          .initializer("new  \$T()", attributeBinderHashMapField)
          .build())
    }

    if (annotatedClass.hasChildElements()) {
      val childElementBinder = ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), targetClassToParseInto)
      val childElementBinderMapField = ParameterizedTypeName.get(ClassName.get(java.util.Map::class.java),
        ClassName.get(String::class.java), childElementBinder)
      val childElementBinderHashMapField = ParameterizedTypeName.get(ClassName.get(mapImplClass),
        ClassName.get(String::class.java), childElementBinder)

      adapterClassBuilder.addField(
        FieldSpec.builder(childElementBinderMapField, CodeGeneratorHelper.childElementBindersParam, PRIVATE)
          .initializer("new  \$T()", childElementBinderHashMapField)
          .build())

      // create fields for generic types
      generateGenericFields(annotatedClass, targetClassToParseInto, childElementBinder, adapterClassBuilder)
    }

    // Add fields from TypeConverter
    for ((qualifiedConverterClass, fieldName) in customTypeConverterManager.converterMap) {
      val converterClassName = ClassName.get(elementUtils.getTypeElement(qualifiedConverterClass))
      adapterClassBuilder.addField(
        FieldSpec.builder(converterClassName, fieldName, PRIVATE).initializer("new \$T()", converterClassName).build())
    }
  }

  private fun generateGenericFields(annotatedClass: AnnotatedClass, targetClassToParseInto: ClassName,
    childElementBinder: ParameterizedTypeName, adapterClassBuilder: TypeSpec.Builder) {
    annotatedClass.getAllChildElementsRecursive()
      .filter { it is PolymorphicSubstitutionField || it is PolymorphicSubstitutionListField }
      .groupBy {
        (it as? PolymorphicSubstitutionField)?.originalElementTypeMirror
          ?: (it as? PolymorphicSubstitutionListField)?.originalElementTypeMirror
      }
      .forEach { (genericType, concreteTypes) ->

        val firstConcreteType = concreteTypes.first()
        val genericName = firstConcreteType.element.simpleName.toString()
        val isList = firstConcreteType.element.isList()
        val isRootList = annotatedClass.childElements.containsValue(firstConcreteType)

        val fromXmlMethodSpecBuilder: MethodSpec.Builder =
          MethodSpec.methodBuilder("fromXml").addAnnotation(Override::class.java)
            .addException(IOException::class.java).addModifiers(PUBLIC)
            .addParameter(ClassName.get(XmlReader::class.java), CodeGeneratorHelper.readerParam)
            .addParameter(ClassName.get(TikXmlConfig::class.java), CodeGeneratorHelper.tikConfigParam)
            .addParameter(targetClassToParseInto, CodeGeneratorHelper.valueParam)

        val fieldName: String?
        when {
          isList -> {
            val genericListType = (firstConcreteType as PolymorphicSubstitutionListField).genericListTypeMirror
            fieldName = "${genericName}ChildElementBinder"
            fromXmlMethodSpecBuilder
              .beginControlFlow("if (${CodeGeneratorHelper.valueParam}.$genericName == null)")
              .addStatement("${CodeGeneratorHelper.valueParam}.$genericName = new \$T()",
                ParameterizedTypeName.get(ClassName.get(ArrayList::class.java), ClassName.get(genericListType)))
              .endControlFlow()

            if (!isRootList) {
              fromXmlMethodSpecBuilder
                .beginControlFlow("while (true)")
            }
            fromXmlMethodSpecBuilder.addStatement("\$T v", genericListType)

            // check if you have to use different namings (e.g. ElementNameMatcher name attribute)
            generateSpecialMapping(concreteTypes, { concreteType ->
              typeUtils.asElement(
                (concreteType as PolymorphicSubstitutionListField).typeMirror).simpleName.toString().toLowerCase(
                Locale.GERMANY) != concreteType.name.toLowerCase(Locale.GERMANY)
            }, fromXmlMethodSpecBuilder, genericListType, "v", !isRootList)

            fromXmlMethodSpecBuilder
              .beginControlFlow("if (v != null)")
              .addStatement("${CodeGeneratorHelper.valueParam}.$genericName.add(v)")
            if (!isRootList) {
              fromXmlMethodSpecBuilder
                .addStatement("${CodeGeneratorHelper.readerParam}.endElement()")
            }
            fromXmlMethodSpecBuilder.endControlFlow()

            if (!isRootList) {
              fromXmlMethodSpecBuilder
                .beginControlFlow("if (!${CodeGeneratorHelper.readerParam}.hasElement())")
                .addStatement("break")
                .endControlFlow()
                .endControlFlow()
            }
          }
          else -> {
            generateSpecialMapping(concreteTypes, { concreteType ->
              typeUtils.asElement(
                (concreteType as PolymorphicSubstitutionField).typeMirror).simpleName.toString().toLowerCase(
                Locale.GERMANY) != concreteType.name.toLowerCase(Locale.GERMANY)
            }, fromXmlMethodSpecBuilder, genericType!!, "${CodeGeneratorHelper.valueParam}.$genericName", false)
            fieldName =
              "${(genericType as DeclaredType).asElement().simpleName.toString().decapitalize(Locale.GERMANY)}ChildElementBinder"
          }
        }

        val typeSpec = TypeSpec.anonymousClassBuilder("")
          .addSuperinterface(childElementBinder)
          .addMethod(fromXmlMethodSpecBuilder.build())
          .build()

        val fieldSpec = FieldSpec
          .builder(ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), targetClassToParseInto),
            fieldName, PRIVATE)
          .initializer("\$L", typeSpec)
          .build()

        adapterClassBuilder.addField(fieldSpec)
      }
  }

  private fun generateSpecialMapping(
    concreteTypes: List<XmlChildElement>,
    filter: (XmlChildElement) -> Boolean,
    fromXmlMethodSpecBuilder: MethodSpec.Builder,
    genericType: TypeMirror,
    valueParam: String,
    isGenericListAndNotRoot: Boolean) {

    var hasSpecialMapping = false
    concreteTypes
      .filter { concreteType -> filter(concreteType) }
      .takeIf { it.isNotEmpty() }
      ?.forEachIndexed { index, specialNamingType ->
        if (!hasSpecialMapping) {
          hasSpecialMapping = true
          if (isGenericListAndNotRoot) {
            fromXmlMethodSpecBuilder
              .addStatement("\$T elementName", String::class.java)
              .beginControlFlow("if (${CodeGeneratorHelper.readerParam}.hasElement())")
              .addStatement("${CodeGeneratorHelper.readerParam}.beginElement()")
              .addStatement("elementName = ${CodeGeneratorHelper.readerParam}.nextElementName()")
              .nextControlFlow("else")
              .addStatement("elementName = ${CodeGeneratorHelper.readerParam}.getCurrentElementName()")
              .endControlFlow()
          } else {
            fromXmlMethodSpecBuilder
              .addStatement("\$T elementName = ${CodeGeneratorHelper.readerParam}.getCurrentElementName()", String::class.java)
          }
        }
        if (index == 0) {
          fromXmlMethodSpecBuilder
            .beginControlFlow("if (elementName.equals(\"${specialNamingType.name}\"))")
        } else {
          fromXmlMethodSpecBuilder.nextControlFlow("else if (elementName.equals(\"${specialNamingType.name}\"))")
        }

        val specialType = (specialNamingType as? PolymorphicSubstitutionField)?.typeMirror
          ?: (specialNamingType as? PolymorphicSubstitutionListField)?.typeMirror ?: genericType
        fromXmlMethodSpecBuilder.addStatement(
          "$valueParam = (\$T) ${CodeGeneratorHelper.tikConfigParam}.getTypeAdapter(\$T.class).fromXml(${CodeGeneratorHelper.readerParam}, ${CodeGeneratorHelper.tikConfigParam})",
          specialType, specialType)
      }

    if (hasSpecialMapping) {
      fromXmlMethodSpecBuilder.nextControlFlow("else")
    } else if (isGenericListAndNotRoot) {
      fromXmlMethodSpecBuilder
        .beginControlFlow("if (${CodeGeneratorHelper.readerParam}.hasElement())")
        .addStatement("${CodeGeneratorHelper.readerParam}.beginElement()")
        .addStatement("${CodeGeneratorHelper.readerParam}.nextElementName()")
        .endControlFlow()
    }

    fromXmlMethodSpecBuilder
      .addStatement(
        "$valueParam = (\$T) ${CodeGeneratorHelper.tikConfigParam}.getTypeAdapter(\$T.class, true).fromXml(${CodeGeneratorHelper.readerParam}, ${CodeGeneratorHelper.tikConfigParam})",
        genericType, genericType)

    if (hasSpecialMapping) {
      fromXmlMethodSpecBuilder.endControlFlow()
    }
  }

  /**
   * Generates the method to parse xml.
   */
  private fun generateFromXmlMethod(annotatedClass: AnnotatedClass): MethodSpec.Builder {
    val reader = CodeGeneratorHelper.readerParam
    val config = CodeGeneratorHelper.tikConfigParam
    val value = CodeGeneratorHelper.valueParam
    val targetClassToParseInto = getClassToParseInto(annotatedClass)
    val textContentStringBuilder = "textContentBuilder"

    val builder = MethodSpec.methodBuilder("fromXml")
      .returns(ClassName.get(annotatedClass.element))
      .addModifiers(PUBLIC)
      .addAnnotation(Override::class.java)
      .addParameter(XmlReader::class.java, reader)
      .addParameter(TikXmlConfig::class.java, config)
      .addException(IOException::class.java)
      .addStatement("\$T \$L = new \$T()", targetClassToParseInto, value, targetClassToParseInto)


    if (annotatedClass.hasTextContent()) {
      builder.addStatement("\$T \$L = new \$T()", StringBuilder::class.java, textContentStringBuilder, StringBuilder::class.java)
    }

    //
    // Read attributes
    //
    if (annotatedClass.hasAttributes()) {
      // consume attributes
      builder.beginControlFlow("while(\$L.hasAttribute())", reader)
        .addStatement("String attributeName = \$L.nextAttributeName()", reader)
        .addStatement("\$T attributeBinder = ${CodeGeneratorHelper.attributeBindersParam}.get(attributeName)",
          ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), targetClassToParseInto))
        .beginControlFlow("if (attributeBinder != null)")
        .addStatement("attributeBinder.fromXml(\$L, \$L, \$L)", reader, config, value)
        .nextControlFlow("else")
        .beginControlFlow("if (\$L.exceptionOnUnreadXml() && !attributeName.startsWith(\$S))", config, namespaceDefinitionPrefix)
        .addStatement("throw new \$T(\$S+attributeName+\$S+\$L.getPath()+\$S)", IOException::class.java,
          "Could not map the xml attribute with the name '",
          "' at path ",
          reader,
          " to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
        .endControlFlow() // End if
        .addStatement("\$L.skipAttributeValue()", reader)
        .endControlFlow() // end if attributeBinder != null
        .endControlFlow() // end while hasAttribute()

    } else {
      // Skip attributes if there are any
      builder.beginControlFlow("while(\$L.hasAttribute())", reader)
        .addStatement("String attributeName = \$L.nextAttributeName()", reader)
        .beginControlFlow("if (\$L.exceptionOnUnreadXml() && !attributeName.startsWith(\$S))", config, namespaceDefinitionPrefix)
        .addStatement("throw new \$T(\$S+attributeName+\$S+\$L.getPath()+\$S)", IOException::class.java,
          "Could not map the xml attribute with the name '",
          "' at path ",
          reader,
          " to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
        .endControlFlow()
        .addStatement("\$L.skipAttributeValue()", reader)
        .endControlFlow()
    }

    //
    // Read child elements and text content
    //
    if (annotatedClass.hasChildElements() && annotatedClass.hasTextContent()) {

      builder.beginControlFlow("while(true)")
        .beginControlFlow("if (\$L.hasElement())", reader)

        .addStatement("\$L.beginElement()", reader)
        .addStatement("String elementName = \$L.nextElementName()", reader)
        .addStatement("\$T childElementBinder = \$L.get(elementName)",
          ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), targetClassToParseInto),
          CodeGeneratorHelper.childElementBindersParam)
        .beginControlFlow("if (childElementBinder != null)")
        .addStatement("childElementBinder.fromXml(\$L, \$L, \$L)", reader, config, value)
        .addStatement("\$L.endElement()", reader)
        .nextControlFlow("else if (\$L.exceptionOnUnreadXml())", config)
        .addStatement("throw new \$T(\$S + \$L + \$S + \$L.getPath()+\$S)", IOException::class.java,
          "Could not map the xml element with the tag name <", "elementName", "> at path '",
          reader,
          "' to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
        .nextControlFlow("else")
        .addStatement("\$L.skipRemainingElement()", reader)
        .endControlFlow() // end else skip remaining element

        .nextControlFlow("else if (\$L.hasTextContent())", reader)

      if (annotatedClass.hasTextContent()) {
        builder.addStatement("\$L.append(\$L.nextTextContent())", textContentStringBuilder, reader)
      } else {
        builder.beginControlFlow("if (\$L.exceptionOnUnreadXml())", config)
          .addStatement("throw new \$T(\$S+\$L.getPath()+\$S)", IOException::class.java,
            "Could not map the xml element's text content at path '",
            reader,
            " to java class. Have you annotated such a field in your java class to map the xml element's text content? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
          .endControlFlow()
          .addStatement("\$L.skipTextContent()", reader)
      }

      builder.nextControlFlow("else")
        .addStatement("break") // quite while loop
        .endControlFlow() // End else
        .endControlFlow() // End while

    } else if (annotatedClass.hasChildElements()) { // Only Child elements, no text content

      builder.beginControlFlow("while(true)")
        .beginControlFlow("if (\$L.hasElement())", reader)

        .addStatement("\$L.beginElement()", reader)
        .addStatement("String elementName = \$L.nextElementName()", reader)
        .addStatement("\$T childElementBinder = \$L.get(elementName)",
          ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), targetClassToParseInto),
          CodeGeneratorHelper.childElementBindersParam)
        .beginControlFlow("if (childElementBinder != null)")
        .addStatement("childElementBinder.fromXml(\$L, \$L, \$L)", reader, config, value)
        .addStatement("\$L.endElement()", reader)
        .nextControlFlow("else if (\$L.exceptionOnUnreadXml())", config)
        .addStatement("throw new \$T(\$S + \$L + \$S + \$L.getPath()+\$S)", IOException::class.java,
          "Could not map the xml element with the tag name <", "elementName", "> at path '",
          reader,
          "' to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
        .nextControlFlow("else")
        .addStatement("\$L.skipRemainingElement()", reader)
        .endControlFlow() // end else skip remaining element

        .nextControlFlow("else if (\$L.hasTextContent())", reader)
        .beginControlFlow("if (\$L.exceptionOnUnreadXml())", config)
        .addStatement("throw new \$T(\$S+\$L.getPath()+\$S)", IOException::class.java,
          "Could not map the xml element's text content at path '",
          reader,
          " to java class. Have you annotated such a field in your java class to map the xml element's text content? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
        .endControlFlow()
        .addStatement("\$L.skipTextContent()", reader)
        .nextControlFlow("else")
        .addStatement("break") // quite while loop
        .endControlFlow() // End else
        .endControlFlow() // End while

    } else if (annotatedClass.hasTextContent()) { // Text Content only , no Child elements

      builder.beginControlFlow("while(true)")

        .beginControlFlow("if (\$L.hasElement())", reader)
        .addStatement("\$L.beginElement()", reader)
        .addStatement("String elementName = \$L.nextElementName()", reader)
        .beginControlFlow("if (\$L.exceptionOnUnreadXml())", config)
        .addStatement("throw new \$T(\$S + \$L + \$S + \$L.getPath()+\$S)", IOException::class.java,
          "Could not map the xml element with the tag name <", "elementName", "> at path '",
          reader,
          "' to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
        .nextControlFlow("else")
        .addStatement("\$L.skipRemainingElement()", reader)
        .endControlFlow() // end else skip remaining element

        .nextControlFlow("else if (\$L.hasTextContent())", reader)
        .addStatement("\$L.append(\$L.nextTextContent())", textContentStringBuilder, reader)

        .nextControlFlow("else")
        .addStatement("break") // quite while loop
        .endControlFlow() // End else
        .endControlFlow() // End while

    } else {
      // No child elements and no text content (so skip both)
      builder.beginControlFlow("while (\$L.hasElement() || \$L.hasTextContent())", reader, reader)
        .beginControlFlow("if (\$L.hasElement())", reader)
        .beginControlFlow("if (\$L.exceptionOnUnreadXml())", config)
        .addStatement("throw new \$T(\$S+\$L.nextElementName()+\$S+\$L.getPath()+\$S)", IOException::class.java,
          "Could not map the xml element with the tag name '",
          reader,
          "' at path ",
          reader,
          " to java class. Have you annotated such a field in your java class to map this xml attribute? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
        .endControlFlow() // End if throw exception
        .beginControlFlow("while(\$L.hasElement())", reader)
        .addStatement("\$L.beginElement()", reader)
        .addStatement("\$L.skipRemainingElement()", reader)
        .endControlFlow() // End while skiping element
        // Skip Text Content
        .nextControlFlow("else if (\$L.hasTextContent())", reader)
        .beginControlFlow("if (\$L.exceptionOnUnreadXml())", config)
        .addStatement("throw new \$T(\$S+\$L.getPath()+\$S)", IOException::class.java,
          "Could not map the xml element's text content at path '",
          reader,
          " to java class. Have you annotated such a field in your java class to map the xml element's text content? Otherwise you can turn this error message off with TikXml.Builder().exceptionOnUnreadXml(false).build().")
        .endControlFlow() // End if throw exception
        .addStatement("\$L.skipTextContent()", reader)
        .endControlFlow() // End  hasTextContent()
        .endControlFlow() // end while

    }

    // assign Text Content
    if (annotatedClass.hasTextContent()) {
      val field = annotatedClass.textContentField!!
      builder.addCode(field.accessResolver.resolveAssignment("$textContentStringBuilder.toString()"))
      // TODO constructor support
    }

    val annotatedConstructor = annotatedClass.annotatedConstructor
    if (annotatedConstructor != null) {
      val stringBuilder = StringBuilder("return new \$T(");
      annotatedConstructor.parameters.forEachIndexed { i, parameter ->
        stringBuilder.append("$value.${parameter.simpleName}")
        if (i < annotatedConstructor.parameters.size - 1) {
          stringBuilder.append(", ")
        }
      }
      stringBuilder.append(")")
      builder.addStatement(stringBuilder.toString(), ClassName.get(annotatedClass.element))
    } else {
      builder.addStatement("return \$L", value)
    }
    return builder;
  }

  /**
   * Generates the method that is responsible to a object as xml
   */
  private fun generateToXmlMethod(annotatedClass: AnnotatedClass, codeGenHelper: CodeGeneratorHelper): MethodSpec.Builder {

    val writer = CodeGeneratorHelper.writerParam
    val config = CodeGeneratorHelper.tikConfigParam
    val value = CodeGeneratorHelper.valueParam
    val overridingXmlElementTagName = "overridingXmlElementTagName"

    val builder = MethodSpec.methodBuilder("toXml")
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(Override::class.java)
      .returns(Void.TYPE)
      .addParameter(XmlWriter::class.java, writer)
      .addParameter(TikXmlConfig::class.java, config)
      .addParameter(ClassName.get(annotatedClass.element), value)
      .addParameter(String::class.java, overridingXmlElementTagName)
      .addException(IOException::class.java)
      .beginControlFlow("if ($value != null)")
      // Only write values if they are not null
      .beginControlFlow("if ($overridingXmlElementTagName == null)")
      .addStatement("$writer.beginElement(\$S)", annotatedClass.nameAsRoot)
      .nextControlFlow("else")
      .addStatement("$writer.beginElement($overridingXmlElementTagName)")
      .endControlFlow()
      .apply {
        // Write the namespace
        for (namespace in annotatedClass.writeNamespaces) {
          when (namespace) {
            is Namespace.DefaultNamespace -> addStatement("$writer.namespace(\$S)", namespace.uri)
            is Namespace.PrefixedNamespace -> addStatement("$writer.namespace(\$S, \$S)", namespace.prefix, namespace.uri)
          }
        }
      }
      .apply {
        // write xml attributes
        addCode(codeGenHelper.writeAttributesAsXml(annotatedClass))
      }
      .apply {
        // Generate code for child elements
        addCode(codeGenHelper.writeChildrenByResolvingPolymorphismElementsOrFieldsOrDelegateToChildCodeGenerator(annotatedClass))
      }
      .apply {
        // TextContent
        val textContentField = annotatedClass.textContentField
        if (textContentField != null) {
          // TODO support for textcontent and type converters
          addCode(
            codeGenHelper.writeTextContentViaTypeConverterOrPrimitive(textContentField.element, textContentField.accessResolver,
              null, textContentField.writeAsCData))
        }
      }
      .addStatement("$writer.endElement()")
      .endControlFlow() // End only write values if they are not null


    return builder
  }

  /**
   * Get the Type that is used for generic types like [AttributeBinder] and [ChildElementBinder] to parse into that.
   */
  private fun getClassToParseInto(annotatedClass: AnnotatedClass) =
    if (annotatedClass.annotatedConstructor != null) {
      // ClassName.get(annotatedClass.element.g)get(annotatedClass.element.qualifiedName.toString() + "$" + VALUE_HOLDER_CLASS_NAME)
      //val packageElement =  elementUtils.getPackageOf(annotatedClass.element)
      // ClassName.get(if (packageElement == null) "" else packageElement.toString(), annotatedClass.simpleClassName + TypeAdapter.GENERATED_CLASS_SUFFIX + "." + VALUE_HOLDER_CLASS_NAME)
      ClassName.get("", VALUE_HOLDER_CLASS_NAME)
    } else {
      ClassName.get(annotatedClass.element)
    }
}