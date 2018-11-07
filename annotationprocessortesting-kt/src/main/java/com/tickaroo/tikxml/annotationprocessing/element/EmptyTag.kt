package com.tickaroo.tikxml.annotationprocessing.element

import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "empty")
class EmptyTag {
    override fun equals(other: Any?): Boolean {
        return other is EmptyTag
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}