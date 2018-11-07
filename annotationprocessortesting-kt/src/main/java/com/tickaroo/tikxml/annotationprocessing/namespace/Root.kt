package com.tickaroo.tikxml.annotationprocessing.namespace

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Root {
    @PropertyElement(name = "x:name")
    var name: String? = null
    @Element(name = "m:child")
    var child: Child? = null
}
