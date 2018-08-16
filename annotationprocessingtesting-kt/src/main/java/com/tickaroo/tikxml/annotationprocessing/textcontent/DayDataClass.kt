package com.tickaroo.tikxml.annotationprocessing.textcontent

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "day")
data class DayDataClass(
        @field:TextContent
        var name: String? = null
)
