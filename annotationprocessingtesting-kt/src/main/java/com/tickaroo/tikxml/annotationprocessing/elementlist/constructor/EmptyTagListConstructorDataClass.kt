package com.tickaroo.tikxml.annotationprocessing.elementlist.constructor

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.element.EmptyTag

/**
 * @author Hannes Dorfmann
 */
@Xml
data class EmptyTagListConstructorDataClass(
        @Element val tags: List<EmptyTag>
)
