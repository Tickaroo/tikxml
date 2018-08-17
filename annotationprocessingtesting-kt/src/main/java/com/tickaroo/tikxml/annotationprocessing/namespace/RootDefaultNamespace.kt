package com.tickaroo.tikxml.annotationprocessing.namespace

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Mariusz Saramak
 */
@Xml(writeNamespaces = ["http://www.w3.org/1998/Math/MathML"])
class RootDefaultNamespace {
    @PropertyElement(name = "name")
    var name: String? = null
    @Element(name = "child")
    var child: Child? = null
}
