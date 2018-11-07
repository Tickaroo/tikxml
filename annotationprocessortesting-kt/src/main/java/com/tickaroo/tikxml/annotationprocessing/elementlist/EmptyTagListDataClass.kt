package com.tickaroo.tikxml.annotationprocessing.elementlist

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.element.EmptyTag

/**
 * @author Hannes Dorfmann
 */
@Xml
data class EmptyTagListDataClass(
        @field:Element
        var tags: List<EmptyTag>? = null
)