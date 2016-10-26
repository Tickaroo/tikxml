package com.tickaroo.tikxml.autovalue

import com.tickaroo.tikxml.annotation.*
import javax.lang.model.type.TypeMirror

/**
 * Represents an auto value annotated method
 */
sealed class AnnotatedMethod<A : Annotation>(val type: TypeMirror, val methodName: String, val annotation: A, val pathAnnotation: Path?) {

    /**
     * A auto value annotated method that has been  annbotated with [Attribute]
     */
    class AttributeMethod(type: TypeMirror, methodName: String, annotation: Attribute, path: Path?) : AnnotatedMethod<Attribute>(type, methodName, annotation, path)

    /**
     * A auto value annotated method that has been  annbotated with [PropertyElement]
     */
    class PropertyElementMethod(type: TypeMirror, methodName: String, annotation: PropertyElement, path: Path?) : AnnotatedMethod<PropertyElement>(type, methodName, annotation, path)

    /**
     * A auto value annotated method that has been  annbotated with [Element]
     */
    class ElementMethod(type: TypeMirror, methodName: String, annotation: Element, path: Path?) : AnnotatedMethod<Element>(type, methodName, annotation, path)

    /**
     * A auto value annotated method that has been  annbotated with [com.tickaroo.tikxml.annotation.TextContent]
     */
    class TextContentMethod(type: TypeMirror, methodName: String, annotation: TextContent) : AnnotatedMethod<TextContent>(type, methodName, annotation, null)
}