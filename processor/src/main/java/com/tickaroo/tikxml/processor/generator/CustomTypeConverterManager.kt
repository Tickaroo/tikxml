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

package com.tickaroo.tikxml.processor.generator

import java.util.*

/**
 * Instance that manages custom
 * @author Hannes Dorfmann
 */
class CustomTypeConverterManager {

    /**
     * Map from qualified class name to java field name
     */
    val converterMap: Map<String, String> = HashMap()
    private var fieldNameCounter = 1

    fun getFieldNameForConverter(qualifiedConverterClassName: String): String = (converterMap as MutableMap).getOrPut(qualifiedConverterClassName) {
        "typeConverter${fieldNameCounter++}"
    }
}