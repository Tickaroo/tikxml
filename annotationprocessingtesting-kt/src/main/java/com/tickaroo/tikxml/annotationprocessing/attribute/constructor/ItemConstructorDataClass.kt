package com.tickaroo.tikxml.annotationprocessing.attribute.constructor

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
data class ItemConstructorDataClass(
        @Attribute var aString: String?,
        @Attribute var anInt: Int,
        @Attribute var aBoolean: Boolean,
        @Attribute var aDouble: Double,
        @Attribute var aLong: Long,
        @Attribute(converter = DateConverter::class) var aDate: Date?,
        @Attribute var intWrapper: Int?,
        @Attribute var booleanWrapper: Boolean?,
        @Attribute var doubleWrapper: Double?,
        @Attribute var longWrapper: Long?
)
