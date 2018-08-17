package com.tickaroo.tikxml.annotationprocessing.namespace

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Child {
    @Attribute(name = "m:id")
    var id: Int = 0
}
