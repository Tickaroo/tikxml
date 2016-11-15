package com.tickaroo.tikxml.autovalue

import com.tickaroo.tikxml.annotation.*
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.converter.AttributeConverterChecker
import com.tickaroo.tikxml.processor.converter.PropertyElementConverterChecker
import com.tickaroo.tikxml.processor.utils.hasTikXmlAnnotation
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

@Throws(ProcessingException::class)
fun extractAutoValueProperties(autoValueClass: TypeElement, properties: Map<String, ExecutableElement>, types: Types, elements: Elements): List<AnnotatedMethod<*>> {

    if (properties.isEmpty())
        return emptyList()

    val annotatedPropropertiesCount = properties.entries.filter { it.value.hasTikXmlAnnotation() }.size

    if (annotatedPropropertiesCount == 0) {
        // AutoValue class doesn't contain TikXml annotated properties methods
        return emptyList()
    }


    //
    // Some checks for android parcelable
    //
    var parcelable = false
    try {
        val parcelableType = elements.getTypeElement("android.os.Parcelable")
        if (parcelableType == null) {
            // android.os.Parcelable not in class path, hence no android project
            parcelable = false
        } else {
            parcelable = types.isAssignable(autoValueClass.asType(), parcelableType.asType())
        }
    } catch (t: Throwable) {
        throw ProcessingException(autoValueClass, "An unexpected error has occurred while trying to scan ${autoValueClass.qualifiedName} inheritance hierarchy (incl. interfaces) to determine whether or not this class implements Parcelable (android)")
    }
    val containsParcelableDescribeContentMethod = parcelable && properties["describeContents"] != null


    val propertiesSize = if (containsParcelableDescribeContentMethod) properties.size - 1 else properties.size

    if (annotatedPropropertiesCount != propertiesSize) {
        throw ProcessingException(autoValueClass, "class ${autoValueClass.qualifiedName} must have " +
                "all methods (auto value properties methods) annotated with TikXml annotations " +
                "like @${Attribute::class.simpleName}, @${PropertyElement::class.simpleName}, " +
                "@${Element::class.simpleName} or @${TextContent::class.simpleName}. " +
                "It's not allowed to annotate just some of the property methods "+
        "(incl. implemented interface methods that are also auto value property methods).")
    }

    return if (containsParcelableDescribeContentMethod)
    // describeContents() has to be implemented by another auto-value-parcelable plugin
        properties.filter { it.key != "describeContents" }.map { toAnnotatedMethod(it.key, it.value) }
    else
        properties.map { toAnnotatedMethod(it.key, it.value) }
}

/**
 * Transforms an auto value annotated method into an internal [AutoValueAnnotatedClass] structure
 */
fun toAnnotatedMethod(propertyName: String, element: ExecutableElement): AnnotatedMethod<*> {
    var annotationFound = 0;

    // MAIN ANNOTATIONS
    val attributeAnnotation = element.getAnnotation(Attribute::class.java)
    val propertyAnnotation = element.getAnnotation(PropertyElement::class.java)
    val elementAnnotation = element.getAnnotation(Element::class.java)
    val textContent = element.getAnnotation(TextContent::class.java)
    val pathAnnotation = element.getAnnotation(Path::class.java)

    if (attributeAnnotation != null) {
        annotationFound++;
    }

    if (propertyAnnotation != null) {
        annotationFound++
    }

    if (elementAnnotation != null) {
        annotationFound++
    }

    if (textContent != null) {
        annotationFound++
    }

    // No annotations
    if (annotationFound == 0) {
        throw ProcessingException(element, "$element in class ${(element.enclosingElement as TypeElement).qualifiedName} doesn't have a TikXml annotation, but the annotation processor tries to process this method. This should not be the case and is an issue of TikXml. Please fill an issue at https://github.com/Tickaroo/tikxml/issues");
    }

    if (annotationFound > 1) {
        // More than one annotation is not allowed
        throw ProcessingException(element, "Methods can ONLY be annotated with one of the "
                + "following annotations @${Attribute::class.simpleName}, "
                + "@${PropertyElement::class.simpleName}, @${Element::class.simpleName} or @${TextContent::class.simpleName}  "
                + "and not multiple of them! The field ${element.simpleName.toString()} in class "
                + "${(element.enclosingElement as TypeElement).qualifiedName} is annotated with more than one of these annotations. You must annotate a field with exactly one of these annotations (not multiple)!")
    }

    // In the case that only text content annotation has been found
    if (textContent != null) {
        if (pathAnnotation != null) {
            throw ProcessingException(element, "@${Path::class.simpleName} can't be used with @${TextContent::class.simpleName} at $element in class ${(element.enclosingElement as TypeElement).qualifiedName}")
        }

        return AnnotatedMethod.TextContentMethod(element, element.returnType, propertyName, textContent)
    }


    if (attributeAnnotation != null) {

        // Checks if the converter is valid, return value is not needed to proceed
        AttributeConverterChecker().getQualifiedConverterName(element, attributeAnnotation)
        return AnnotatedMethod.AttributeMethod(element, element.returnType, propertyName, attributeAnnotation, pathAnnotation)
    }

    if (propertyAnnotation != null) {
        // Checks if the converter is valid, return value is not needed to proceed
        PropertyElementConverterChecker().getQualifiedConverterName(element, propertyAnnotation)
        return AnnotatedMethod.PropertyElementMethod(element, element.returnType, propertyName, propertyAnnotation, pathAnnotation)
    }


    if (elementAnnotation != null) {
        return AnnotatedMethod.ElementMethod(element, element.returnType, propertyName, elementAnnotation, pathAnnotation)
    }

    throw ProcessingException(element, "Unknown annotation detected! I'm sorry, this should not happen. Please file an issue on github https://github.com/Tickaroo/tikxml/issues ")
}

