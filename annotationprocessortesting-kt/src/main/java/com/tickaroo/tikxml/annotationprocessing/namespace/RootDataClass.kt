package com.tickaroo.tikxml.annotationprocessing.namespace

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "root")
data class RootDataClass (
    @field:PropertyElement(name = "x:name")
    var name: String? = null,
    @field:Element(name = "m:child")
    var child: ChildDataClass? = null
)
