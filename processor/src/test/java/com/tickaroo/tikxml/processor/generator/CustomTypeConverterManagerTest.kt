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

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 * @author Hannes Dorfmann
 */
class CustomTypeConverterManagerTest {

    @Test
    fun getAndInsert() {
        val manager = CustomTypeConverterManager()
        assertEquals(0, manager.converterMap.size)
        assertEquals("typeConverter1", manager.getFieldNameForConverter("com.foo.Converter1"))
        assertEquals(1, manager.converterMap.size)
        assertEquals("typeConverter1", manager.getFieldNameForConverter("com.foo.Converter1"))
        assertEquals(1, manager.converterMap.size)
        assertTrue(manager.converterMap.containsKey("com.foo.Converter1"))

        assertEquals("typeConverter2", manager.getFieldNameForConverter("com.foo.Converter2"))
        assertEquals(2, manager.converterMap.size)
        assertEquals("typeConverter1", manager.getFieldNameForConverter("com.foo.Converter1"))
        assertEquals(2, manager.converterMap.size)
        assertEquals("typeConverter2", manager.getFieldNameForConverter("com.foo.Converter2"))
        assertEquals(2, manager.converterMap.size)
        assertTrue(manager.converterMap.containsKey("com.foo.Converter1"))
        assertTrue(manager.converterMap.containsKey("com.foo.Converter2"))
    }
}