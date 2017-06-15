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

import com.squareup.javapoet.*
import com.tickaroo.tikxml.TikXmlConfig
import com.tickaroo.tikxml.TypeConverterNotFoundException
import com.tickaroo.tikxml.XmlReader
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.field.PolymorphicSubstitutionField
import com.tickaroo.tikxml.processor.field.PolymorphicSubstitutionListField
import com.tickaroo.tikxml.processor.field.PolymorphicTypeElementNameMatcher
import com.tickaroo.tikxml.processor.field.access.FieldAccessResolver
import com.tickaroo.tikxml.processor.utils.*
import com.tickaroo.tikxml.processor.xml.XmlChildElement
import com.tickaroo.tikxml.processor.xml.XmlElement
import com.tickaroo.tikxml.typeadapter.AttributeBinder
import com.tickaroo.tikxml.typeadapter.ChildElementBinder
import com.tickaroo.tikxml.typeadapter.NestedChildElementBinder
import java.io.IOException
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 *
 * @author Hannes Dorfmann
 */
class CodeGeneratorHelper(val customTypeConverterManager: CustomTypeConverterManager, val typeConvertersForPrimitives: Set<String>, val valueType: ClassName, val elementUtils: Elements, val typeUtils: Types) {


    // Constants
    companion object PARAMS {
        const val valueParam = "value"
        const val tikConfigParam = "config"
        const val tikErrorsParam = "errors"
        const val tikConfigMethodExceptionOnUnreadXml = "exceptionOnUnreadXml"
        const val textContentParam = "textContent"
        const val readerParam = "reader"
        const val writerParam = "writer"
        const val attributeBindersParam = "attributeBinders"
        const val childElementBindersParam = "childElementBinders"
    }


    /**
     * Get the parameterized (generics) type name for [AttributeBinder]
     */
    val attributeBinderType = ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), valueType)

    /**
     * Get the parameterized (generics) type name for [ChildElementBinder]
     */
    val childElementBinderType = ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), valueType)

    /**
     * Get the parameterized (generics) type name for [NestedChildElementBinder]
     */
    val nestedChildElementBinderType = ParameterizedTypeName.get(ClassName.get(NestedChildElementBinder::class.java), valueType)

    /**
     */
    private var temporaryVaribaleCounter = 0

    /**
     * Sometime during codegenetation we need unique variable names (i.e. for temporary variables).
     * This function generates one for you by simply adding a unique number
     * (a counter that will be incremented everytime you call this method) at the end of the original
     * variable name
     */
    fun uniqueVariableName(originalVariableName: String) = originalVariableName + (temporaryVaribaleCounter++)

    /**
     * Generate the attribute binders
     */
    fun generateAttributeBinders(currentElement: XmlElement): CodeBlock {

        // TODO optimize it for one single attribute

        val builder = CodeBlock.builder()
        for ((xmlElementName, attributeField) in currentElement.attributes) {

            val fromXmlMethodBuilder = fromXmlMethodBuilder()
            fromXmlMethodBuilder.addCode(assignViaTypeConverterOrPrimitive(attributeField.element, AssignmentType.ATTRIBUTE, attributeField.accessResolver, attributeField.converterQualifiedName))


            val anonymousAttributeBinder = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(AttributeBinder::class.java), valueType))
                    .addMethod(fromXmlMethodBuilder.build())
                    .build()


            builder.addStatement("$attributeBindersParam.put(\$S, \$L)", xmlElementName, anonymousAttributeBinder)
        }

        return builder.build()
    }

    /**
     * get the assignment statement for reading attributes
     */
    fun assignViaTypeConverterOrPrimitive(element: Element, assignmentType: AssignmentType, accessResolver: FieldAccessResolver, customTypeConverterQualifiedClassName: String?): CodeBlock {

        val type = element.asType()
        val xmlReaderMethodPrefix = assignmentType.xmlReaderMethodPrefix()


        val surroundWithTryCatch = fun(assignmentStatement: String) = CodeBlock.builder()
                .beginControlFlow("try")
                .add(accessResolver.resolveAssignment(assignmentStatement))
                .nextControlFlow("catch(\$T e)", ClassName.get(TypeConverterNotFoundException::class.java))
                .addStatement("throw e")
                .nextControlFlow("catch(\$T e)", ClassName.get(Exception::class.java))
                .addStatement("throw new \$T(e)", ClassName.get(IOException::class.java))
                .endControlFlow()
                .build()

        //
        // Use custom type converter?
        //
        if (customTypeConverterQualifiedClassName != null) {
            return surroundWithTryCatch("${customTypeConverterManager.getFieldNameForConverter(customTypeConverterQualifiedClassName)}.read($readerParam.$xmlReaderMethodPrefix())")
        }

        //
        // Primitives
        //
        if (type.isString()) {
            if (typeConvertersForPrimitives.contains(String::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.String.class).read($readerParam.$xmlReaderMethodPrefix())")
            }

            return accessResolver.resolveAssignment("$readerParam.$xmlReaderMethodPrefix()")
        }

        if (type.isBoolean()) {
            if (typeConvertersForPrimitives.contains("java.lang.Boolean")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Boolean.class).read($readerParam.$xmlReaderMethodPrefix())")
            }

            if (typeConvertersForPrimitives.contains("boolean")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(boolean.class).read($readerParam.$xmlReaderMethodPrefix())")
            }

            if (typeConvertersForPrimitives.contains("kotlin.Boolean") || typeConvertersForPrimitives.contains(Boolean::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Boolean.class).read($readerParam.$xmlReaderMethodPrefix())")
            }

            return accessResolver.resolveAssignment("$readerParam.${xmlReaderMethodPrefix}AsBoolean()")
        }

        if (type.isDouble()) {

            if (typeConvertersForPrimitives.contains("java.lang.Double")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Double.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("double")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(double.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("kotlin.Double") || typeConvertersForPrimitives.contains(Double::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Double.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            return accessResolver.resolveAssignment("$readerParam.${xmlReaderMethodPrefix}AsDouble()")
        }

        if (type.isInt()) {

            if (typeConvertersForPrimitives.contains("java.lang.Integer")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Integer.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("int")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(int.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("kotlin.Int") || typeConvertersForPrimitives.contains(Int::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Integer.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            return accessResolver.resolveAssignment("$readerParam.${xmlReaderMethodPrefix}AsInt()")
        }

        if (type.isLong()) {

            if (typeConvertersForPrimitives.contains("java.lang.Long")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Long.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("long")) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(long.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }

            if (typeConvertersForPrimitives.contains("kotlin.Long") || typeConvertersForPrimitives.contains(Long::class.java.canonicalName)) {
                return surroundWithTryCatch("$tikConfigParam.getTypeConverter(java.lang.Long.class).read($readerParam.${xmlReaderMethodPrefix}())")
            }


            return accessResolver.resolveAssignment("$readerParam.${xmlReaderMethodPrefix}AsLong()")
        }

        //
        // Use typeconveter from TikConfig
        //
        return surroundWithTryCatch("$tikConfigParam.getTypeConverter($type.class).read($readerParam.${xmlReaderMethodPrefix}())")
    }

    /**
     * Generate a [NestedChildElementBinder] and recursively calls [com.tickaroo.tikxml.processor.xml.XmlChildElement] to generate its code
     */
    fun generateNestedChildElementBinder(element: XmlElement): TypeSpec {

        val initializerBuilder = CodeBlock.builder()
        if (element.hasAttributes()) {
            val attributeMapType = ParameterizedTypeName.get(ClassName.get(HashMap::class.java), ClassName.get(String::class.java), attributeBinderType)
            initializerBuilder.addStatement("$attributeBindersParam = new \$T()", attributeMapType);
            initializerBuilder.add(generateAttributeBinders(element))
        }

        if (element.hasChildElements()) {
            val childBinderTypeMap = ParameterizedTypeName.get(ClassName.get(HashMap::class.java), ClassName.get(String::class.java), childElementBinderType)
            initializerBuilder.addStatement("$childElementBindersParam = new \$T()", childBinderTypeMap);
            for ((xmlName, xmlElement) in element.childElements) {
                initializerBuilder.addStatement("${CodeGeneratorHelper.childElementBindersParam}.put(\$S, \$L)", xmlName, xmlElement.generateReadXmlCode(this))
            }
        }


        // TODO text content?
        return TypeSpec.anonymousClassBuilder("false")
                .addSuperinterface(nestedChildElementBinderType)
                .addInitializerBlock(initializerBuilder.build())
                .build()
    }

    fun ignoreAttributes() = CodeBlock.builder()
            .beginControlFlow("while(\$L.hasAttribute())", readerParam)
            .addStatement("String attributeName = \$L.nextAttributeName()", readerParam)
            .beginControlFlow("if (\$L.exceptionOnUnreadXml() && !attributeName.startsWith(\$S))", tikConfigParam, "xmlns")
            .addStatement("throw new \$T(\"Unread attribute '\"+ attributeName +\"' at path \"+ $readerParam.getPath())", ClassName.get(IOException::class.java))
            .endControlFlow()
            .beginControlFlow("if(\$L != null)", tikErrorsParam)
            .addStatement("\$L.add(\"Unread attribute '\"+ attributeName +\"' at path \"+ $readerParam.getPath())", tikErrorsParam)
            .endControlFlow()
            .addStatement("\$L.skipAttributeValue()", readerParam)
            .endControlFlow()
            .build()

    fun fromXmlMethodBuilder() = MethodSpec.methodBuilder("fromXml")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(XmlReader::class.java, readerParam)
            .addParameter(TikXmlConfig::class.java, tikConfigParam)
            .addParameter(ParameterizedTypeName.get(List::class.java, String::class.java), tikErrorsParam)
            .addParameter(valueType, valueParam)
            .addException(IOException::class.java)

    /**
     * Generates the code to write attributes as xml
     */
    fun writeAttributesAsXml(currentElement: XmlElement): CodeBlock {

        val builder = CodeBlock.builder()
        for ((_, attributeField) in currentElement.attributes) {
            builder.add(writeAttributeViaTypeConverterOrPrimitive(attributeField.name, attributeField.element, attributeField.accessResolver, attributeField.converterQualifiedName))
        }

        return builder.build()
    }

    /**
     * write the value of an attribute or
     */
    fun writeAttributeViaTypeConverterOrPrimitive(attributeName: String, element: Element, accessResolver: FieldAccessResolver, customTypeConverterQualifiedClassName: String?): CodeBlock {

        val type = element.asType()
        val xmlWriterMethod = "attribute"


        val surroundWithTryCatch = fun(writeStatement: String): CodeBlock {
            val builder = CodeBlock.builder()

            if (!element.asType().isPrimitive()) {
                // Only write values if they are not null, otherwise don't write values as xml
                builder.beginControlFlow("if (${accessResolver.resolveGetterForWritingXml()} != null)")
            }

            builder.beginControlFlow("try")
                    .addStatement(writeStatement)
                    .nextControlFlow("catch(\$T e)", ClassName.get(TypeConverterNotFoundException::class.java))
                    .addStatement("throw e")
                    .nextControlFlow("catch(\$T e)", ClassName.get(Exception::class.java))
                    .addStatement("throw new \$T(e)", ClassName.get(IOException::class.java))
                    .endControlFlow()

            if (!element.asType().isPrimitive()) {
                // Only write values if they are not null, otherwise don't write values as xml
                builder.endControlFlow()
            }
            return builder.build()
        }

        fun writeValueWithoutConverter(): CodeBlock {
            val builder = CodeBlock.builder()

            if (!element.asType().isPrimitive()) {
                // Only write values if they are not null, otherwise don't write values as xml
                builder.beginControlFlow("if (${accessResolver.resolveGetterForWritingXml()} != null)")
            }

            builder.addStatement("$writerParam.$xmlWriterMethod(\"$attributeName\", ${accessResolver.resolveGetterForWritingXml()})")

            if (!element.asType().isPrimitive()) {
                // Only write values if they are not null, otherwise don't write values as xml
                builder.endControlFlow()
            }
            return builder.build();
        }

        //
        // Use custom type converter?
        //
        if (customTypeConverterQualifiedClassName != null) {
            return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", ${customTypeConverterManager.getFieldNameForConverter(customTypeConverterQualifiedClassName)}.write(${accessResolver.resolveGetterForWritingXml()}))")
        }

        //
        // Primitives
        //
        if (type.isString()) {
            if (typeConvertersForPrimitives.contains(String::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.String.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        if (type.isBoolean()) {
            if (typeConvertersForPrimitives.contains("java.lang.Boolean")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.Boolean.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("boolean")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(boolean.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("kotlin.Boolean") || typeConvertersForPrimitives.contains(Boolean::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.Boolean.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        if (type.isDouble()) {

            if (typeConvertersForPrimitives.contains("java.lang.Double")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.Double.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("double")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(double.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }


            if (typeConvertersForPrimitives.contains("kotlin.Double") || typeConvertersForPrimitives.contains(Double::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.Double.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }


            return writeValueWithoutConverter()
        }

        if (type.isInt()) {

            if (typeConvertersForPrimitives.contains("java.lang.Integer")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.Integer.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("int")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(int.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("kotlin.Int") || typeConvertersForPrimitives.contains(Int::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.Integer.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        if (type.isLong()) {

            if (typeConvertersForPrimitives.contains("java.lang.Long")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.Long.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("long")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(long.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("kotlin.Long") || typeConvertersForPrimitives.contains(Long::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter(java.lang.Long.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        //
        // Use typeconveter from TikConfig
        //
        return surroundWithTryCatch("$writerParam.$xmlWriterMethod(\"$attributeName\", $tikConfigParam.getTypeConverter($type.class).write(${accessResolver.resolveGetterForWritingXml()}))")
    }

    /**
     * Writes the typical <foo attr="1" other="asd" opeining xml stuff
     */
    fun writeBeginElementAndAttributes(childElement: XmlChildElement) =
            CodeBlock.builder()
                    .add(writeBeginElement(childElement.name))
                    .apply { add(writeAttributesAsXml(childElement)) }
                    .build()

    /**
     * writes the typical <foo  xml opening stuff
     */
    fun writeBeginElement(elementName: String) =
            CodeBlock.builder().addStatement("$writerParam.beginElement(\$S)", elementName).build()

    /**
     * Writes the code to continue to delegate reading sub elements from TypeAdapter via tikConfig
     */
    fun writeDelegateToTypeAdapters(type: TypeMirror, accessResolver: FieldAccessResolver, overridingXmlElementName: String?) =
            CodeBlock.builder().addStatement("$tikConfigParam.getTypeAdapter(\$T.class).toXml($writerParam, $tikConfigParam, ${accessResolver.resolveGetterForWritingXml()}, ${if (overridingXmlElementName == null) "null" else "\"$overridingXmlElementName\""})", ClassName.get(type))
                    .build()

    /**
     * Writes the code to generate xml by generating to the corresponding type adapter depending on the type of the element
     */
    fun writeResolvePolymorphismAndDelegteToTypeAdpters(variableName: String, typeElementNameMatcher: List<PolymorphicTypeElementNameMatcher>) =
            CodeBlock.builder()
                    .apply {


                        // Cannot be done with instanceof because then the inheritance hierarchy matters and so matters the order of the if checks
                        val orderdByInheritanceHierarchy = orderByInheritanceHierarchy(typeElementNameMatcher, elementUtils, typeUtils)
                        if (orderdByInheritanceHierarchy.size != typeElementNameMatcher.size) {
                            throw ProcessingException(null, "Oops: an unexpected exception has occurred while determining the correct order for inheritance hierarchy. Please file an issue at https://github.com/Tickaroo/tikxml/issues . Some debug information: ordered hierarchy elements: ${orderdByInheritanceHierarchy.size} ;  TypeElementMatcher size ${typeElementNameMatcher.size} ; ordered hierarchy list: ${orderdByInheritanceHierarchy} ; TypeElementMatcher list ${typeElementNameMatcher}")
                        }
                        orderdByInheritanceHierarchy.forEachIndexed { i, nameMatcher ->
                            if (i == 0) {
                                beginControlFlow("if ($variableName instanceof \$T)", ClassName.get(nameMatcher.type))
                            } else {
                                nextControlFlow("else if ($variableName instanceof \$T)", ClassName.get(nameMatcher.type))
                            }
                            addStatement("$tikConfigParam.getTypeAdapter(\$T.class).toXml($writerParam, $tikConfigParam, (\$T) $variableName, \$S)", ClassName.get(nameMatcher.type), ClassName.get(nameMatcher.type), nameMatcher.xmlElementName)
                        }


                        if (typeElementNameMatcher.isNotEmpty()) {
                            nextControlFlow("else")
                            addStatement("throw new \$T(\$S + $variableName + \$S)", ClassName.get(IOException::class.java), "Don't know how to write the element of type ", " as XML. Most likely you have forgotten to register for this type with @${ElementNameMatcher::class.simpleName} when resolving polymorphism.")
                            endControlFlow()
                        }

                    }
                    .build()

    /**
     * Writes the text content via type adapter. This is used i.e. for property fields and or textContent fields
     */
    fun writeTextContentViaTypeConverterOrPrimitive(element: Element, accessResolver: FieldAccessResolver, customTypeConverterQualifiedClassName: String?, asCData: Boolean): CodeBlock {

        val type = element.asType()
        val xmlWriterMethod = if (asCData && element.asType().isString()) "textContentAsCData" else "textContent"


        val surroundWithTryCatch = fun(writeStatement: String): CodeBlock {
            val builder = CodeBlock.builder()

            if (!element.asType().isPrimitive()) {
                // Only write values if they are not null, otherwise don't write values as xml
                builder.beginControlFlow("if (${accessResolver.resolveGetterForWritingXml()} != null)")
            }

            builder.beginControlFlow("try")
                    .addStatement(writeStatement)
                    .nextControlFlow("catch(\$T e)", ClassName.get(TypeConverterNotFoundException::class.java))
                    .addStatement("throw e")
                    .nextControlFlow("catch(\$T e)", ClassName.get(Exception::class.java))
                    .addStatement("throw new \$T(e)", ClassName.get(IOException::class.java))
                    .endControlFlow()

            if (!element.asType().isPrimitive()) {
                // Only write values if they are not null, otherwise don't write values as xml
                builder.endControlFlow()
            }
            return builder.build()
        }

        fun writeValueWithoutConverter(): CodeBlock {
            val builder = CodeBlock.builder()

            if (!element.asType().isPrimitive()) {
                // Only write values if they are not null, otherwise don't write values as xml
                builder.beginControlFlow("if (${accessResolver.resolveGetterForWritingXml()} != null)")
            }

            builder.addStatement("$writerParam.$xmlWriterMethod(${accessResolver.resolveGetterForWritingXml()})")

            if (!element.asType().isPrimitive()) {
                // Only write values if they are not null, otherwise don't write values as xml
                builder.endControlFlow()
            }
            return builder.build();
        }

        //
        // Use custom type converter?
        //
        if (customTypeConverterQualifiedClassName != null) {
            return surroundWithTryCatch("$writerParam.$xmlWriterMethod(${customTypeConverterManager.getFieldNameForConverter(customTypeConverterQualifiedClassName)}.write(${accessResolver.resolveGetterForWritingXml()}))")
        }

        //
        // Primitives
        //
        if (type.isString()) {
            if (typeConvertersForPrimitives.contains(String::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(java.lang.String.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        if (type.isBoolean()) {
            if (typeConvertersForPrimitives.contains(String::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(java.lang.Boolean.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("boolean")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(boolean.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        if (type.isDouble()) {

            if (typeConvertersForPrimitives.contains(Double::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(java.lang.Double.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("double")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(double.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        if (type.isInt()) {

            if (typeConvertersForPrimitives.contains(Integer::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(java.lang.Integer.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("int")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(int.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        if (type.isLong()) {

            if (typeConvertersForPrimitives.contains(Long::class.java.canonicalName)) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(java.lang.Long.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            if (typeConvertersForPrimitives.contains("long")) {
                return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter(long.class).write(${accessResolver.resolveGetterForWritingXml()}))")
            }

            return writeValueWithoutConverter()
        }

        //
        // Use typeconveter from TikConfig
        //
        return surroundWithTryCatch("$writerParam.$xmlWriterMethod($tikConfigParam.getTypeConverter($type.class).write(${accessResolver.resolveGetterForWritingXml()}))")
    }

    /**
     * Generates the code tat is able to resolve polymorphism for lists, polymorphic elements or by simply forwarding code generation to the child.
     */
    fun writeChildrenByResolvingPolymorphismElementsOrFieldsOrDelegateToChildCodeGenerator(xmlElement: XmlElement) =
            CodeBlock.builder().apply {
                xmlElement.childElements.values.groupBy { it.element }.forEach {

                    val first = it.value[0]
                    if (first is PolymorphicSubstitutionListField) {

                        // Resolve polymorphism on list items
                        val listType = ClassName.get(first.originalElementTypeMirror)
                        val sizeVariableName = "listSize"
                        val listVariableName = "list"
                        val itemVariableName = "item";
                        val elementTypeMatchers: List<PolymorphicTypeElementNameMatcher> = it.value.map {
                            val i = it as PolymorphicSubstitutionListField
                            PolymorphicTypeElementNameMatcher(i.name, i.typeMirror)
                        }

                        beginControlFlow("if (${first.accessResolver.resolveGetterForWritingXml()}!= null)")
                        addStatement("\$T $listVariableName = ${first.accessResolver.resolveGetterForWritingXml()}", listType)
                        addStatement("int $sizeVariableName = $listVariableName.size()")
                        beginControlFlow("for (int i =0; i<$sizeVariableName; i++)")
                        addStatement("\$T $itemVariableName = $listVariableName.get(i)", ClassName.get(Object::class.java))
                        add(writeResolvePolymorphismAndDelegteToTypeAdpters(itemVariableName, elementTypeMatchers)) // does the if instance of checks
                        endControlFlow() // end for loop
                        endControlFlow() // end != null check

                    } else if (first is PolymorphicSubstitutionField) {
                        // Resolve polymorphism for fields
                        val elementTypeMatchers: List<PolymorphicTypeElementNameMatcher> = it.value.map {
                            val i = it as PolymorphicSubstitutionField
                            PolymorphicTypeElementNameMatcher(i.name, i.typeMirror)
                        }
                        beginControlFlow("if (${first.accessResolver.resolveGetterForWritingXml()} != null)")
                        addStatement("\$T element = ${first.accessResolver.resolveGetterForWritingXml()}", ClassName.get(first.originalElementTypeMirror))  // does the if instance of checks
                        add(writeResolvePolymorphismAndDelegteToTypeAdpters("element", elementTypeMatchers))
                        endControlFlow() // end != null check

                    } else {
                        it.value.forEach { add(it.generateWriteXmlCode(this@CodeGeneratorHelper)) }
                    }
                }
            }.build()

    /**
     * Used to specify whether we are going to assign an xml attribute or an xml element text content
     */
    enum class AssignmentType {
        ATTRIBUTE,
        ELEMENT;

        fun xmlReaderMethodPrefix() = when (this) {
            ATTRIBUTE -> "nextAttributeValue"
            ELEMENT -> "nextTextContent"
        }
    }
}