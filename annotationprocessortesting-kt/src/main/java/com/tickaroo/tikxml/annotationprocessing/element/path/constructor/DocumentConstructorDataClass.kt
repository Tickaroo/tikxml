package com.tickaroo.tikxml.annotationprocessing.element.path.constructor

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.element.path.Image

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "document")
data class DocumentConstructorDataClass (
        @Path("toSkip") @Element val image: Image?
)
