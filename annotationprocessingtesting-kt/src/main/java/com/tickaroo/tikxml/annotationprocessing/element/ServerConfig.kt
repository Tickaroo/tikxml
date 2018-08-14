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

package com.tickaroo.tikxml.annotationprocessing.element

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class ServerConfig {

    @Attribute
    var enabled: Boolean = false

    @PropertyElement
    var ip: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServerConfig) return false

        val that = other as ServerConfig?

        if (enabled != that!!.enabled) return false
        return if (ip != null) ip == that.ip else that.ip == null
    }

    override fun hashCode(): Int {
        var result = if (enabled) 1 else 0
        result = 31 * result + if (ip != null) ip!!.hashCode() else 0
        return result
    }
}
