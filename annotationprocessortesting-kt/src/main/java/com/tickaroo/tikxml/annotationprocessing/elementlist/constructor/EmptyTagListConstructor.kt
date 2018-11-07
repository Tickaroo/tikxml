package com.tickaroo.tikxml.annotationprocessing.elementlist.constructor

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.element.EmptyTag

/**
 * @author Hannes Dorfmann
 */
@Xml
class EmptyTagListConstructor(
        @param:Element val tags: List<EmptyTag>
)
