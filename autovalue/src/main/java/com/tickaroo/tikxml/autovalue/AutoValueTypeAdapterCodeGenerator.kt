package com.tickaroo.tikxml.autovalue

import com.squareup.javapoet.*
import com.tickaroo.tikxml.TikXmlConfig
import com.tickaroo.tikxml.XmlReader
import com.tickaroo.tikxml.XmlWriter
import com.tickaroo.tikxml.annotation.*
import com.tickaroo.tikxml.processor.converter.AttributeConverterChecker
import com.tickaroo.tikxml.processor.converter.PropertyElementConverterChecker
import com.tickaroo.tikxml.typeadapter.TypeAdapter
import java.io.IOException
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.util.Elements

/**
 * Generates the temporarily value holder that holdes the data in it's own data structure.
 * This will copy the annotation from autoValue annotateted class to the value holder class
 * @author Hannes Dorfmann
 */
fun generateValueHolder(annotatedClass: AutoValueAnnotatedClass, elementUtils: Elements): TypeSpec =
        TypeSpec.classBuilder(annotatedClass.valueHolderClassName)
                .addAnnotation(AnnotationSpec.builder(Xml::class.java)
                        .apply {
                            val annotation = annotatedClass.xmlAnnotation
                            if (annotation.name.isEmpty()) {
                                addMember("name", "\$S", annotatedClass.autoValueClass.simpleName.toString())
                            } else {
                                addMember("name", "\$S", annotation.name)
                            }

                            addMember("inheritance", "false")

                            val namespaces = annotation.writeNamespaces
                            if (namespaces.isNotEmpty()) {
                                val strBuilder = StringBuilder("{")
                                namespaces.forEachIndexed { i, ns ->
                                    if (i > 0) strBuilder.append(", ")
                                    strBuilder.append("\"")
                                            .append(ns)
                                            .append("\"")
                                            .append("}")
                                }
                                addMember("writeNamespaces", strBuilder.toString())
                            }

                        }
                        .build())
                .apply {
                    annotatedClass.propertyMethods.forEach {
                        addField(FieldSpec.builder(ClassName.get(it.type), it.methodName)
                                .apply { if (it.pathAnnotation != null) addAnnotation(AnnotationSpec.get(it.pathAnnotation)) }
                                .addAnnotation(rewriteAnnotation(it, annotatedClass, elementUtils))
                                .build())
                    }
                }
                .build()

/**
 * Generates a TypeAdapter for the auto value class by delegting the work to the value holder Type Adapter
 */
fun generateTypeAdapter(annotatedClass: AutoValueAnnotatedClass) =
        TypeSpec.classBuilder(annotatedClass.autoValueClass.simpleName.toString() + TypeAdapter.GENERATED_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(TypeAdapter::class.java), ClassName.get(annotatedClass.autoValueClass.asType())))
                .apply {
                    // read Xml
                    val reader = "reader"
                    val config = "config"
                    val value = "value"
                    val valueHolderClass = ClassName.get(annotatedClass.packageName, annotatedClass.valueHolderClassName)

                    addMethod(MethodSpec.methodBuilder("fromXml")
                            .returns(ClassName.get(annotatedClass.autoValueClass.asType()))
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override::class.java)
                            .addParameter(XmlReader::class.java, reader)
                            .addParameter(TikXmlConfig::class.java, config)
                            .addException(IOException::class.java)
                            .addStatement("\$T $value = $config.getTypeAdapter(\$T.class).fromXml($reader, $config)", valueHolderClass, valueHolderClass)
                            .addCode("return new \$T(", ClassName.get(annotatedClass.packageName, "AutoValue_" + annotatedClass.autoValueClass.simpleName))
                            .apply {
                                //fill constructor parameters with values from value holder instance
                                annotatedClass.propertyMethods.forEachIndexed { i, annotatedMethod ->
                                    if (i == 0)
                                        addCode("$value.${annotatedMethod.methodName}")
                                    else
                                        addCode(", $value.${annotatedMethod.methodName}")
                                }
                            }
                            .addCode(");\n")
                            .build()
                    )
                }
                .apply {
                    // write xml
                    // TODO optimize this without value holder
                    val writer = "writer"
                    val config = "config"
                    val value = "value"
                    val overridingXmlElementTagName = "overridingXmlElementTagName"
                    val tmpValue = "tmp";
                    val valueHolderClass = ClassName.get(annotatedClass.packageName, annotatedClass.valueHolderClassName)

                    addMethod(MethodSpec.methodBuilder("toXml")
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override::class.java)
                            .returns(Void.TYPE)
                            .addParameter(XmlWriter::class.java, writer)
                            .addParameter(TikXmlConfig::class.java, config)
                            .addParameter(ClassName.get(annotatedClass.autoValueClass.asType()), value)
                            .addParameter(String::class.java, overridingXmlElementTagName)
                            .addException(IOException::class.java)
                            .addStatement("\$T $tmpValue = new \$T()", valueHolderClass, valueHolderClass)
                            .apply {
                                // Save values in temporarly value
                                annotatedClass.propertyMethods.forEach {
                                    addStatement("$tmpValue.${it.methodName} = $value.${it.methodName}()")
                                }
                            }
                            .addStatement("$config.getTypeAdapter(\$T.class).toXml($writer, $config, $tmpValue, $overridingXmlElementTagName)", valueHolderClass)
                            .build()
                    )
                }
                .build()

fun rewriteAnnotation(annotatedMethod: AnnotatedMethod<*>, annotatedClass: AutoValueAnnotatedClass, elements: Elements): AnnotationSpec =
        when (annotatedMethod) {

            is AnnotatedMethod.AttributeMethod -> {
                val annotation = annotatedMethod.annotation
                val converterQualifiedName = AttributeConverterChecker().getQualifiedConverterName(annotatedMethod.element, annotation)
                AnnotationSpec.builder(Attribute::class.java)
                        .apply {
                            if (annotation.name.isNotEmpty())
                                addMember("name", "\$S", annotation.name)

                            if (converterQualifiedName != null) {
                                addMember("converter", "\$T.class", ClassName.get(elements.getTypeElement(converterQualifiedName).asType()))
                            }
                        }.build()
            }

            is AnnotatedMethod.PropertyElementMethod -> {
                val annotation = annotatedMethod.annotation
                val converterQualifiedName = PropertyElementConverterChecker().getQualifiedConverterName(annotatedMethod.element, annotation)
                AnnotationSpec.builder(PropertyElement::class.java).apply {
                    if (annotation.name.isNotEmpty())
                        addMember("name", "\$S", annotation.name)

                    addMember("writeAsCData", "${annotation.writeAsCData}")
                    if (converterQualifiedName != null) {
                        addMember("converter", "\$T.class", ClassName.get(elements.getTypeElement(converterQualifiedName).asType()))
                    }
                }.build()
            }

            is AnnotatedMethod.ElementMethod -> {
                val annotation = annotatedMethod.annotation
                AnnotationSpec.builder(Element::class.java).apply {

                    if (annotation.name.isNotEmpty())
                        addMember("name", "\$S", annotation.name)

                    val elementNameMatcher = annotation.typesByElement
                    if (elementNameMatcher.isNotEmpty()) {
                        val varargs = ArrayList<TypeName>()
                        val strBuilder = StringBuilder("{")

                        elementNameMatcher.forEachIndexed { i, matcher ->
                            if (i > 0) strBuilder.append(", ")
                            strBuilder.append("@\$T(name=\"")
                                    .append(matcher.name)
                                    .append('"')
                                    .append(" ,type=\$T.class)")

                            val typeClassName: TypeName = try {
                                ClassName.get(matcher.type.java)
                            } catch (e: MirroredTypeException) {
                                ClassName.get(e.typeMirror)
                            }

                            varargs.add(ClassName.get(ElementNameMatcher::class.java))
                            varargs.add(typeClassName)
                        }
                        strBuilder.append('}')

                        addMember("typesByElement", strBuilder.toString(), varargs.toTypedArray())
                    }
                }.build()
            }

            is AnnotatedMethod.TextContentMethod -> AnnotationSpec.get(annotatedMethod.annotation)
        }
