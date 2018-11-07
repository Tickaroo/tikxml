package com.tickaroo.tikxml.annotationprocessing.element.path.autovalue

import com.google.auto.value.AutoValue
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
@AutoValue
abstract class Document {
    @Path("toSkip")
    @Element
    abstract fun image(): Image
}
