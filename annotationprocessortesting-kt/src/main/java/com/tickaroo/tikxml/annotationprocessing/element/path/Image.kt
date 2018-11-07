package com.tickaroo.tikxml.annotationprocessing.element.path

import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Image {
    override fun equals(other: Any?): Boolean {
        return other is Image
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
