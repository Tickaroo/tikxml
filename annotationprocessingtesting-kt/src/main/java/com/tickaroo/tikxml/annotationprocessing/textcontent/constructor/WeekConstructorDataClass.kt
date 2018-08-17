package com.tickaroo.tikxml.annotationprocessing.textcontent.constructor

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "week")
data class WeekConstructorDataClass(@Element val days: List<DayConstructorDataClass>?)