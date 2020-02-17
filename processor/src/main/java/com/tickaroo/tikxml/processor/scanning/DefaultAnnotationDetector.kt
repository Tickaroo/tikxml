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

package com.tickaroo.tikxml.processor.scanning

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.ElementNameMatcher
import com.tickaroo.tikxml.annotation.GenericAdapter
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.processor.ProcessingException
import com.tickaroo.tikxml.processor.converter.AttributeConverterChecker
import com.tickaroo.tikxml.processor.converter.PropertyElementConverterChecker
import com.tickaroo.tikxml.processor.field.AttributeField
import com.tickaroo.tikxml.processor.field.ElementField
import com.tickaroo.tikxml.processor.field.ListElementField
import com.tickaroo.tikxml.processor.field.NamedField
import com.tickaroo.tikxml.processor.field.PolymorphicElementField
import com.tickaroo.tikxml.processor.field.PolymorphicListElementField
import com.tickaroo.tikxml.processor.field.PolymorphicTypeElementNameMatcher
import com.tickaroo.tikxml.processor.field.PropertyField
import com.tickaroo.tikxml.processor.field.TextContentField
import com.tickaroo.tikxml.processor.utils.getSurroundingClassQualifiedName
import com.tickaroo.tikxml.processor.utils.hasEmptyParameters
import com.tickaroo.tikxml.processor.utils.hasMinimumPackageVisibilityModifiers
import com.tickaroo.tikxml.processor.utils.hasSuperClass
import com.tickaroo.tikxml.processor.utils.hasTikXmlAnnotation
import com.tickaroo.tikxml.processor.utils.isAbstract
import com.tickaroo.tikxml.processor.utils.isClass
import com.tickaroo.tikxml.processor.utils.isInterface
import com.tickaroo.tikxml.processor.utils.isList
import com.tickaroo.tikxml.processor.utils.isPrivate
import com.tickaroo.tikxml.processor.utils.isProtected
import com.tickaroo.tikxml.processor.utils.isPublic
import com.tickaroo.tikxml.processor.utils.isSamePackageAs
import com.tickaroo.tikxml.processor.utils.isString
import java.util.Locale
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.WildcardType
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * A [AnnotationScanner] that scans the element by checking for TikXml annotations
 * @author Hannes Dorfmann
 */
@ExperimentalStdlibApi
open class DefaultAnnotationDetector(protected val elementUtils: Elements, protected val typeUtils: Types) : AnnotationDetector {

  override val genericTypes = mutableMapOf<String, Set<String>?>()

  protected fun isTextContentAnnotated(element: VariableElement): Boolean {
    val textContentAnnotated = element.getAnnotation(TextContent::class.java) != null
    if (textContentAnnotated && element.getAnnotation(Path::class.java) != null) {
      throw ProcessingException(element,
        "@${Path::class.simpleName} on @${TextContent::class.simpleName} is not allowed. Use @${PropertyElement::class.simpleName} and @${Path::class.simpleName} instead on field '$element' in class ${element.getSurroundingClassQualifiedName()}")
    } else {
      return textContentAnnotated
    }
  }

  override fun addGenericType(key: String, value: String?) {
    genericTypes[key] = (genericTypes[key]?.toMutableSet() ?: mutableSetOf()).apply { value?.run { add(this) } }
  }

  override fun containsGenericType(key: String) = genericTypes.containsKey(key)

  override fun isXmlTextContent(element: VariableElement): TextContentField? =
    if (isTextContentAnnotated(element) && isXmlField(element) == null) {
      if (!element.asType().isString()) {
        throw ProcessingException(element,
          "Only type String is supported for @${TextContent::class.simpleName} but field '$element' in class ${element.getSurroundingClassQualifiedName()} is not of type String")
      }

      val annotation = element.getAnnotation(TextContent::class.java)
      TextContentField(element, annotation.writeAsCData)
    } else
      null

  override fun isXmlField(element: VariableElement): NamedField? {
    var annotationFound = 0

    // MAIN ANNOTATIONS
    val attributeAnnotation = element.getAnnotation(Attribute::class.java)
    val propertyAnnotation = element.getAnnotation(PropertyElement::class.java)
    val elementAnnotation = element.getAnnotation(Element::class.java)
    val textContent = element.getAnnotation(TextContent::class.java)

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
      return null
    }

    if (annotationFound > 1) {
      // More than one annotation is not allowed
      throw ProcessingException(element, "Fields can ONLY be annotated with ONE of the "
          + "following annotations @${Attribute::class.java.simpleName}, "
          + "@${PropertyElement::class.java.simpleName}, @${Element::class.java.simpleName} or @${TextContent::class.java.simpleName} "
          + "and not multiple of them! The field ${element.simpleName} in class "
          + "${(element.enclosingElement as TypeElement).qualifiedName} is annotated with more than one of these annotations. You must annotate a field with exactly one of these annotations (not multiple)!")
    }

    // In the case that only text content annotation has been found
    if (textContent != null && annotationFound == 1)
      return null;


    if (attributeAnnotation != null) {

      val converterChecker = AttributeConverterChecker()
      return AttributeField(element,
        nameFromAnnotationOrField(attributeAnnotation.name, element),
        converterChecker.getQualifiedConverterName(element, attributeAnnotation))
    }

    if (propertyAnnotation != null) {
      val converterChecker = PropertyElementConverterChecker()
      return PropertyField(element,
        nameFromAnnotationOrField(propertyAnnotation.name, element),
        propertyAnnotation.writeAsCData,
        converterChecker.getQualifiedConverterName(element, propertyAnnotation))
    }

    if (elementAnnotation != null) {
      val nameMatchers = elementAnnotation.typesByElement

      // Check for primitives
      if (element.asType().kind != TypeKind.DECLARED) {
        throw ProcessingException(element,
          "The type of field '${element.simpleName}' in class ${element.enclosingElement} is not a class nor a interface. Only classes or interfaces can be annotated with @${Element::class.simpleName} annotation. If you try to annotate primitives than @${PropertyElement::class.simpleName}")
      }

      val checkTypeElement = fun(typeElement: TypeElement) {
        if (elementAnnotation.compileTimeChecks) {
          if (typeElement.isInterface()) {
            throw ProcessingException(element,
              "The type of field '${element.simpleName}' in class ${element.getSurroundingClassQualifiedName()} is an interface. Hence polymorphism must be resolved by annotating this interface with @${GenericAdapter::class.simpleName}!")
          }

          if (typeElement.isAbstract()) {
            throw ProcessingException(element,
              "The type of field '${element.simpleName}' in class ${element.getSurroundingClassQualifiedName()} is an abstract class. Hence polymorphism must be resolved by annotating this abstract class with @${GenericAdapter::class.simpleName}!")
          } else if (typeElement.isClass()) {
            checkNameMatchers(nameMatchers, element)
          }
        }
      }

      if (isList(element)) {
        val genericListType = getGenericTypeFromList(element)
        val genericListTypeElement = typeUtils.asElement(genericListType) as TypeElement
        val genericTypeNames = genericTypes[genericListTypeElement.toString()]

        if (genericTypeNames == null) {
          checkTypeElement(genericListTypeElement)

          val elementName = if (elementAnnotation.name.isEmpty()) {
            getXmlElementNameOrThrowException(element, genericListTypeElement, elementAnnotation.compileTimeChecks, true)
          } else {
            elementAnnotation.name
          }

          return ListElementField(
            element,
            elementName,
            genericListType
          )
        } else {
          checkNameMatchers(nameMatchers, element)

          return PolymorphicListElementField(
            element,
            "placeholderToSubstituteWithPolymorphicListElement",
            getPolymorphicTypes(element, nameMatchers, genericTypeNames),
            genericListType
          )
        }

      } else {
        val genericTypeNames = genericTypes[element.asType().toString()]
        val typeElement = (element.asType() as DeclaredType).asElement() as TypeElement

        return if (genericTypeNames == null) {
          checkTypeElement(typeElement)

          val elementName = if (elementAnnotation.name.isEmpty()) {
            getXmlElementNameOrThrowException(element, typeElement, elementAnnotation.compileTimeChecks, true)
          } else {
            elementAnnotation.name
          }

          ElementField(element, elementName)
        } else {
          checkNameMatchers(nameMatchers, element)

          PolymorphicElementField(element, "placeholderToSubstituteWithPolymorphicElement",
            getPolymorphicTypes(element, nameMatchers, genericTypeNames))
        }
      }
    }


    throw ProcessingException(element,
      "Unknown annotation detected! I'm sorry, this should not happen. Please file an issue on github https://github.com/Tickaroo/tikxml/issues ")
  }

  /**
   * Checks whether or not thy element is of type (or subtype) java.util.List
   */
  protected fun isList(element: VariableElement): Boolean = element.isList()

  val checkNameMatchers = fun(nameMatchers: Array<ElementNameMatcher>, element: VariableElement) {
    for (matcher in nameMatchers) {
      try {
        val typeClass = matcher.type
        val typeElement = elementUtils.getTypeElement(typeClass.qualifiedName)
        checkPublicClassWithEmptyConstructorOrAnnotatedConstructor(element, typeElement)
        checkTargetClassXmlAnnotated(element, typeElement)
        checkDuplicateNameMatchers(element, nameMatchers, matcher)
      } catch (mte: MirroredTypeException) {
        val typeMirror = mte.typeMirror
        val typeElement = (typeMirror as DeclaredType).asElement() as TypeElement

        checkPublicClassWithEmptyConstructorOrAnnotatedConstructor(element, typeElement)
        checkTargetClassXmlAnnotated(element, typeElement)
        checkDuplicateNameMatchers(element, nameMatchers, matcher)
      }
    }
  }

  val checkDuplicateNameMatchers =
    fun(element: VariableElement, nameMatchers: Array<ElementNameMatcher>, matcher: ElementNameMatcher) {
      val (matcherType, matcherName) = try {
        Pair(matcher.type, matcher.name)
      } catch (mte: MirroredTypeException) {
        Pair(mte.typeMirror, matcher.name.takeIf { it.isNotBlank() } ?: ((mte.typeMirror as DeclaredType).asElement() as TypeElement).getXmlElementName())
      }

      val conflictingNameMatcher = nameMatchers.firstOrNull { nameMatcher ->
        val (nameMatcherType, nameMatcherName) = try {
          Pair(nameMatcher.type, matcher.name)
        } catch (mte: MirroredTypeException) {
          Pair(mte.typeMirror, nameMatcher.name.takeIf { it.isNotBlank() } ?: ((mte.typeMirror as DeclaredType).asElement() as TypeElement).getXmlElementName())
        }

        nameMatcherName == matcherName && nameMatcherType != matcherType
      }

      if (conflictingNameMatcher != null) {
        val conflictingNameMatcherType = try {
          conflictingNameMatcher.type
        } catch (mte: MirroredTypeException) {
          mte.typeMirror
        }

        throw ProcessingException(element,
          "Conflict: A @${ElementNameMatcher::class.simpleName} with the name \"${matcher.name}\" is already mapped to the type $matcherType to resolve polymorphism. Hence it cannot be mapped to $conflictingNameMatcherType as well.")
      }
    }

  val checkTargetClassXmlAnnotated = fun(element: VariableElement, typeElement: TypeElement) {
    if (typeElement.getAnnotation(Xml::class.java) == null && (typeUtils.asElement(
        element.asType()) as TypeElement).hasSuperClass()) {
      throw ProcessingException(element,
        "The class ${typeElement.qualifiedName} is not annotated with @${Xml::class.simpleName}, but is used in '$element' in class @${element.getSurroundingClassQualifiedName()} to resolve polymorphism. Please annotate @${element.getSurroundingClassQualifiedName()} with @${Xml::class.simpleName}")
    }
  }

  private fun getPolymorphicTypes(
    element: VariableElement,
    matcherAnnotations: Array<ElementNameMatcher>,
    genericTypes: Set<String>? = null): List<PolymorphicTypeElementNameMatcher> {
    if (genericTypes == null && matcherAnnotations.isEmpty()) {
      throw ProcessingException(element,
        "Neither @${ElementNameMatcher::class.simpleName} nor @${GenericAdapter::class.simpleName} specified to resolve polymorphism!")
    }

    val namingMap = hashMapOf<String, PolymorphicTypeElementNameMatcher>()

    // add generic types first
    genericTypes?.forEach { qualifiedName ->
      val simpleName = elementUtils.getTypeElement(qualifiedName).getXmlElementName()
      namingMap[simpleName] = PolymorphicTypeElementNameMatcher(simpleName, elementUtils.getTypeElement(qualifiedName).asType())
    }

    // maybe override with matcher annotations
    for (matcher in matcherAnnotations) {
      try {
        val typeClass = matcher.type
        val typeElement = elementUtils.getTypeElement(typeClass.qualifiedName)

        checkPublicClassWithEmptyConstructorOrAnnotatedConstructor(element, typeElement)
        checkTargetClassXmlAnnotated(element, typeElement)
        val xmlElementName = if (matcher.name.isEmpty()) typeElement.getXmlElementName() else matcher.name

        namingMap.values.firstOrNull { elementNameMatcher -> elementNameMatcher.type == typeElement.asType() }
          ?.also { elementNameMatcher ->
            namingMap.remove(elementNameMatcher.xmlElementName)
          } // delete common generic type if already in list
        namingMap[xmlElementName] = PolymorphicTypeElementNameMatcher(xmlElementName, typeElement.asType())
      } catch (mte: MirroredTypeException) {
        val typeMirror = mte.typeMirror
        if (typeMirror.kind != TypeKind.DECLARED) {
          throw ProcessingException(element, "Only classes can be specified as type in @${ElementNameMatcher::class.simpleName}")
        }

        val typeElement = (typeMirror as DeclaredType).asElement() as TypeElement

        checkPublicClassWithEmptyConstructorOrAnnotatedConstructor(element, typeElement)
        checkTargetClassXmlAnnotated(element, typeElement)

        val xmlElementName = if (matcher.name.isEmpty()) typeElement.getXmlElementName() else matcher.name

        namingMap.values.firstOrNull { elementNameMatcher -> elementNameMatcher.type == typeMirror }
          ?.also { elementNameMatcher ->
            namingMap.remove(elementNameMatcher.xmlElementName)
          }
        namingMap[xmlElementName] = PolymorphicTypeElementNameMatcher(xmlElementName, typeElement.asType())
      }
    }

    return namingMap.values.toList()
  }

  /**
   * Checks if a the typeElement is a public class (or default package visibility if in same package as variableElement)
   */
  private fun checkPublicClassWithEmptyConstructorOrAnnotatedConstructor(variableElement: VariableElement,
    typeElement: TypeElement) {

    if (!typeElement.isClass()) {
      throw ProcessingException(variableElement,
        "@${ElementNameMatcher::class.simpleName} only allows classes. $typeElement is a not a class!")
    }

    if (typeElement.isPrivate()) {
      throw ProcessingException(variableElement,
        "@${ElementNameMatcher::class.simpleName} does not allow private classes. $typeElement is a private class!")
    }

    if (typeElement.isProtected()) {
      throw ProcessingException(variableElement,
        "@${ElementNameMatcher::class.simpleName} does not allow protected classes. $typeElement is a protected class!")
    }

    val hasSamePackage = typeElement.isSamePackageAs(variableElement.enclosingElement, elementUtils)
    val classIsPublic = typeElement.isPublic()

    if (!classIsPublic && !hasSamePackage) {
      throw ProcessingException(variableElement,
        "@${ElementNameMatcher::class.simpleName} does not allow package visiblity on classes outside of this package. Make $typeElement is a public class or move this class into the same package")
    }

    // Check for subtype
    val variableType = if (isList(variableElement)) getGenericTypeFromList(variableElement) else variableElement.asType()
    if (!typeUtils.isAssignable(typeElement.asType(), variableType)) {
      throw ProcessingException(variableElement,
        "The type $typeElement must be a sub type of ${variableType}. Otherwise this type cannot be used in @${ElementNameMatcher::class.simpleName} to resolve polymorphism.")
    }

    for (constructor in ElementFilter.constructorsIn(typeElement.enclosedElements)) {
      if (constructor.hasEmptyParameters()) {
        if (hasSamePackage and constructor.hasMinimumPackageVisibilityModifiers()) {
          return
        }

        if (classIsPublic and constructor.isPublic()) {
          return
        }
      } else if (constructor.parameters.filter { !(it as VariableElement).hasTikXmlAnnotation() }.isEmpty()) {
        return
      }
    }


    throw ProcessingException(variableElement, "Class $typeElement used in @${ElementNameMatcher::
    class.simpleName} must provide an public empty (parameter-less) constructor")
  }

  /**
   * Get the genric type of a List
   */
  protected fun getGenericTypeFromList(listVariableElement: VariableElement): TypeMirror {

    if (listVariableElement.asType().kind != TypeKind.DECLARED) {
      throw ProcessingException(listVariableElement, "Element must be of type java.util.List")
    }

    val typeMirror = listVariableElement.asType() as DeclaredType
    return when (typeMirror.typeArguments.size) {
      0 -> elementUtils.getTypeElement("java.lang.Object").asType() // Raw types
      1 -> if (typeMirror.typeArguments[0].kind == TypeKind.WILDCARD) {
        val wildCardMirror = typeMirror.typeArguments[0] as WildcardType
        when {
          wildCardMirror.extendsBound != null -> wildCardMirror.extendsBound
          wildCardMirror.superBound != null -> wildCardMirror.superBound
          else -> elementUtils.getTypeElement("java.lang.Object").asType()
        } // in case of List<?>
      } else typeMirror.typeArguments[0]

      else -> throw ProcessingException(listVariableElement,
        "Seems that you have annotated a List with more than one generic argument? How is this possible?")
    }
  }

  /**
   * Get the name of a annotated field from annotation or use the field name if no name set
   */
  private fun nameFromAnnotationOrField(name: String, element: VariableElement) =
    if (name.isEmpty()) {
      element.simpleName.toString()
    } else name

  /**
   * Get the xmlElement name which is either @Xml(name = "foo") property or the class name decapitalize (first letter in lower case)
   */
  private fun getXmlElementNameOrThrowException(field: VariableElement, typeElement: TypeElement,
    compileTimeChecks: Boolean, allowJavaObject: Boolean): String {

    val xmlAnnotation = typeElement.getAnnotation(Xml::class.java)
    val genericAdapterAnnotation = typeElement.getAnnotation(GenericAdapter::class.java)
    val annotationName =
      if (typeElement.isClass() && !typeElement.isAbstract()) "@${Xml::class.simpleName}" else "@${GenericAdapter::class.simpleName}"
    val checkAnnotation = (typeElement.isClass() && typeElement.hasSuperClass()) || !allowJavaObject

    if (xmlAnnotation == null && genericAdapterAnnotation == null && compileTimeChecks && checkAnnotation) {
      throw ProcessingException(field,
        "The type ${typeElement.qualifiedName} used for field '$field' in ${field.getSurroundingClassQualifiedName()} can't be used, because it is not annotated with $annotationName. Annotate ${typeElement.qualifiedName} with $annotationName!")
    } else {
      return typeElement.getXmlElementName()
    }
  }
}