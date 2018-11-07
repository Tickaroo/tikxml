/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tickaroo.tikxml.annotationprocessing.attribute

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
data class ItemWithGetterSettersDataClass (
    @field:Attribute
    var aString: String? = null,

    @field:Attribute
    var anInt: Int = 0,

    @field:Attribute
    var aBoolean: Boolean = false,

    @field:Attribute
    var aDouble: Double = 0.toDouble(),

    @field:Attribute
    var aLong: Long = 0,

    @field:Attribute(converter = DateConverter::class)
    var aDate: Date? = null,

    @field:Attribute
    var intWrapper: Int? = null,

    @field:Attribute
    var booleanWrapper: Boolean? = null,

    @field:Attribute
    var doubleWrapper: Double? = null,

    @field:Attribute
    var longWrapper: Long? = null
)
