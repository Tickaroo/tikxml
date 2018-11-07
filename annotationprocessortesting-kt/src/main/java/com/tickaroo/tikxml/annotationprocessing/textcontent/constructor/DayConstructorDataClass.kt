package com.tickaroo.tikxml.annotationprocessing.textcontent.constructor

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "day")
data class DayConstructorDataClass(@TextContent val name: String?)
