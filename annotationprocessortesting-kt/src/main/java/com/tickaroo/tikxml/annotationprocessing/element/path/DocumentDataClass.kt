package com.tickaroo.tikxml.annotationprocessing.element.path

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "document")
data class DocumentDataClass (
    @field:Path("toSkip")
    @field:Element
    var image: Image? = null
)