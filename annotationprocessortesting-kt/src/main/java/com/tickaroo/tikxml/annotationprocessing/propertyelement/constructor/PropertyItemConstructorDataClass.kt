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

package com.tickaroo.tikxml.annotationprocessing.propertyelement.constructor

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
data class PropertyItemConstructorDataClass(
        @PropertyElement val aString: String?,
        @PropertyElement val anInt: Int,
        @PropertyElement val aBoolean: Boolean,
        @PropertyElement val aDouble: Double,
        @PropertyElement val aLong: Long,
        @PropertyElement(converter = DateConverter::class) val aDate: Date?,
        @PropertyElement val intWrapper: Int?,
        @PropertyElement val booleanWrapper: Boolean?,
        @PropertyElement val doubleWrapper: Double?,
        @PropertyElement val longWrapper: Long?
)