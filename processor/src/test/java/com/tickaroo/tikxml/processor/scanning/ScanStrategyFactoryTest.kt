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

package com.tickaroo.tikxml.processor.scanning

import com.tickaroo.tikxml.annotation.ScanMode
import org.junit.Test
import org.mockito.Mockito
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.test.assertTrue

/**
 *
 * @author Hannes Dorfmann
 */
class ScanStrategyFactoryTest {


    @Test
    fun useDefaultStrategy() {

        // We have to mock this, even if that is never called
        val listTypeMirror = Mockito.mock(TypeMirror::class.java)
        val types = Mockito.mock(Types::class.java)
        Mockito.doReturn(listTypeMirror).`when`(types).erasure(Mockito.any())

        val mockElement = Mockito.mock(TypeElement::class.java)
        val elements = Mockito.mock(Elements::class.java)
        Mockito.doReturn(mockElement).`when`(elements).getTypeElement("java.util.List")
        Mockito.doReturn(listTypeMirror).`when`(mockElement).asType()

        val scanStrategyFactory = ScanStrategyFactory(elements, types, AnnotationBasedRequiredDetector())

        val annotatedClass = MockAnnotatedClass(ScanMode.DEFAULT)
        assertTrue(scanStrategyFactory.getStrategy(annotatedClass, ScanMode.COMMON_CASE) is CommonCaseScanStrategy)
        assertTrue(scanStrategyFactory.getStrategy(annotatedClass, ScanMode.ANNOTATIONS_ONLY) is AnnotationOnlyScanStrategy)
    }
}
