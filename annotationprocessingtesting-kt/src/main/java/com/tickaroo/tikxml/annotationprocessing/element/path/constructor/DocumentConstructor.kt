package com.tickaroo.tikxml.annotationprocessing.element.path.constructor

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.element.path.Image

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "document")
class DocumentConstructor internal constructor(@param:Path("toSkip") @param:Element val image: Image?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DocumentConstructor) return false

        val that = other as DocumentConstructor?

        return if (image != null) image == that!!.image else that!!.image == null
    }

    override fun hashCode(): Int {
        return image?.hashCode() ?: 0
    }
}
