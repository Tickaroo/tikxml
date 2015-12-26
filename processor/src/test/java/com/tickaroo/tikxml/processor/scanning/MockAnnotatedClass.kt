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
import com.tickaroo.tikxml.processor.model.AnnotatedClass
import com.tickaroo.tikxml.processor.model.Field
import org.mockito.Mockito
import java.util.*
import javax.lang.model.element.TypeElement

/**
 *
 * @author Hannes Dorfmann
 */
class MockAnnotatedClass(override val element: TypeElement, override val scanMode: ScanMode, override val inheritance: Boolean = true, override val nameAsRoot: String = "", override val simpleClassName: String = "", override val qualifiedClassName: String = "") : AnnotatedClass {

    constructor(scanMode: ScanMode) : this(Mockito.mock(TypeElement::class.java) as TypeElement, scanMode)

    override val fields: Map<String, Field> = HashMap()
}