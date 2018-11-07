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

package com.tickaroo.tikxml.annotationprocessing.attribute.autovalue

import com.google.auto.value.AutoValue
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.annotationprocessing.DateConverter
import java.util.Date

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
@AutoValue
abstract class ItemAV {
    @Attribute
    abstract fun aString(): String

    @Attribute
    abstract fun anInt(): Int

    @Attribute
    abstract fun aBoolean(): Boolean

    @Attribute
    abstract fun aDouble(): Double

    @Attribute
    abstract fun aLong(): Long

    @Attribute(converter = DateConverter::class)
    abstract fun aDate(): Date

    @Attribute
    abstract fun intWrapper(): Int?

    @Attribute
    abstract fun booleanWrapper(): Boolean?

    @Attribute
    abstract fun doubleWrapper(): Double?

    @Attribute
    abstract fun longWrapper(): Long?
}
