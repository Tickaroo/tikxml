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
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * @author Hannes Dorfmann
 */
@Xml
class Server {
    @Attribute
    var name: String? = null

    @Element
    var config: ServerConfig? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Server) return false

        val server = other as Server?

        if (if (name != null) name != server!!.name else server!!.name != null) return false
        return if (config != null) config == server.config else server.config == null
    }

    override fun hashCode(): Int {
        var result = if (name != null) name!!.hashCode() else 0
        result = 31 * result + if (config != null) config!!.hashCode() else 0
        return result
    }
}
