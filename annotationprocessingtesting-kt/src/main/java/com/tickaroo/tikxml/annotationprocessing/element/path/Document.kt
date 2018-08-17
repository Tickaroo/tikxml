package com.tickaroo.tikxml.annotationprocessing.element.path

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Document {
    @Path("toSkip")
    @Element
    var image: Image? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Document) return false

        val document = other as Document?

        return if (image != null) image == document!!.image else document!!.image == null
    }

    override fun hashCode(): Int {
        return if (image != null) image!!.hashCode() else 0
    }
}
