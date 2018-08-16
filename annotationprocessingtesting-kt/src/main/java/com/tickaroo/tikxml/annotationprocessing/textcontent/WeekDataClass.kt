package com.tickaroo.tikxml.annotationprocessing.textcontent

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "week")
data class WeekDataClass (
    @field:Element
    var days: List<DayDataClass>? = null
)