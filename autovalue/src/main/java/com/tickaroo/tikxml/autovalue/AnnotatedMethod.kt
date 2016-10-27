package com.tickaroo.tikxml.autovalue

import com.tickaroo.tikxml.annotation.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

/**
 * Represents an auto value annotated method
 */
sealed class AnnotatedMethod<A : Annotation>(val element: ExecutableElement, val type: TypeMirror, val methodName: String, val annotation: A, val pathAnnotation: Path?) {

    /**
     * A auto value annotated method that has been  annbotated with [Attribute]
     */
    class AttributeMethod(element: ExecutableElement, type: TypeMirror, methodName: String, annotation: Attribute, path: Path?) : AnnotatedMethod<Attribute>(element, type, methodName, annotation, path)

    /**
     * A auto value annotated method that has been  annbotated with [PropertyElement]
     */
    class PropertyElementMethod(element: ExecutableElement, type: TypeMirror, methodName: String, annotation: PropertyElement, path: Path?) : AnnotatedMethod<PropertyElement>(element, type, methodName, annotation, path)

    /**
     * A auto value annotated method that has been  annbotated with [Element]
     */
    class ElementMethod(element: ExecutableElement, type: TypeMirror, methodName: String, annotation: Element, path: Path?) : AnnotatedMethod<Element>(element, type, methodName, annotation, path)

    /**
     * A auto value annotated method that has been  annbotated with [com.tickaroo.tikxml.annotation.TextContent]
     */
    class TextContentMethod(element: ExecutableElement, type: TypeMirror, methodName: String, annotation: TextContent) : AnnotatedMethod<TextContent>(element, type, methodName, annotation, null)
}