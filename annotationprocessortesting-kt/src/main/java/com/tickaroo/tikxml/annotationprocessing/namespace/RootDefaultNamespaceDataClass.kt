package com.tickaroo.tikxml.annotationprocessing.namespace

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Mariusz Saramak
 */
@Xml(writeNamespaces = ["http://www.w3.org/1998/Math/MathML"], name = "rootDefaultNamespace")
data class RootDefaultNamespaceDataClass (
    @field:PropertyElement(name = "name")
    var name: String? = null,
    @field:Element(name = "child")
    var child: ChildDataClass? = null
)
