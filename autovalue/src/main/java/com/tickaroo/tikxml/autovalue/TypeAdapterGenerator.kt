package com.tickaroo.tikxml.autovalue

import com.squareup.javapoet.*
import com.tickaroo.tikxml.TikXmlConfig
import com.tickaroo.tikxml.XmlReader
import com.tickaroo.tikxml.XmlWriter
import com.tickaroo.tikxml.typeadapter.TypeAdapter
import java.io.IOException
import javax.lang.model.element.Modifier

/**
 * Generates the temporarily value holder that holdes the data in it's own data structure.
 * This will copy the annotation from autoValue annotateted class to the value holder class
 * @author Hannes Dorfmann
 */
fun generateValueHolder(annotatedClass: AutoValueAnnotatedClass): TypeSpec =
        TypeSpec.classBuilder(annotatedClass.valueHolderClassName)
                .addAnnotation(AnnotationSpec.get(annotatedClass.xmlAnnotation))
                .apply {
                    annotatedClass.propertyMethods.forEach {
                        addField(FieldSpec.builder(ClassName.get(it.type), it.methodName)
                                .apply { if (it.pathAnnotation != null) addAnnotation(AnnotationSpec.get(it.pathAnnotation)) }
                                .addAnnotation(AnnotationSpec.get(it.annotation))
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
                    // Field definition
                    val typeAdapter = ClassName.get(annotatedClass.packageName, annotatedClass.valueHolderClassName)
                    addField(FieldSpec.builder(typeAdapter, "delegatingTypeAdapter", Modifier.FINAL, Modifier.PRIVATE)
                            .initializer("new \$T()", typeAdapter)
                            .build()
                    )
                }
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
                            .addStatement("\$T $value = delegatingTypeAdapter.fromXml($reader, $config)", valueHolderClass)
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
                            .addStatement("\$T $tmpValue = new \$T()", valueHolderClass)
                            .apply {
                                // Save values in temporarly value
                                annotatedClass.propertyMethods.forEach {
                                    addStatement("$tmpValue.${it.methodName} = $value.${it.methodName}()")
                                }
                            }
                            .addStatement("delegatingTypeAdapter.toXml($writer, $config, $tmpValue, $overridingXmlElementTagName)")
                            .build()
                    )
                }
                .build()