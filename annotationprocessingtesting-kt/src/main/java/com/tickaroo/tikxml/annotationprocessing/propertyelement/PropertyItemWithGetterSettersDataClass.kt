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

package com.tickaroo.tikxml.annotationprocessing.propertyelement

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
data class PropertyItemWithGetterSettersDataClass(
        @field:PropertyElement
        var aString: String? = null,
        @field:PropertyElement
        var anInt: Int = 0,
        @field:PropertyElement
        var aBoolean: Boolean = false,
        @field:PropertyElement
        var aDouble: Double = 0.toDouble(),
        @field:PropertyElement
        var aLong: Long = 0,
        @field:PropertyElement(converter = DateConverter::class)
        var aDate: Date? = null,
        @field:PropertyElement
        var intWrapper: Int? = null,
        @field:PropertyElement
        var booleanWrapper: Boolean? = null,
        @field:PropertyElement
        var doubleWrapper: Double? = null,
        @field:PropertyElement
        var longWrapper: Long? = null
)
