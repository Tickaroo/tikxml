package com.tickaroo.tikxml.annotationprocessing.textcontent.autovalue

import com.google.auto.value.AutoValue
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
@AutoValue
abstract class Week {
    @Element
    abstract fun days(): List<Day>
}
