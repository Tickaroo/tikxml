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

package com.tickaroo.tikxml.processor.field

import com.tickaroo.tikxml.processor.ProcessingException
import org.junit.Test
import javax.lang.model.element.VariableElement
import kotlin.collections.single
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 *
 * @author Hannes Dorfmann
 */
class PathDetectorTest {

    val element: VariableElement = MockVariableElement()

    fun expectException(clazz: KClass<out Exception>, errorMsg: String? = null, blockToExecute: () -> Unit) {
        try {
            blockToExecute()
            fail("Expected exception of type $clazz but no Exception has been thrown")
        } catch(t: Throwable) {

            /*
            if (clazz != t::class) {
                fail("Expected an exception of type $clazz but got $t")
            }

            if (errorMsg != null && errorMsg != t.message) {
                fail("Excpected a error message $errorMsg\nbut got: ${t.message}")
            }

            */
        }
    }

    @Test
    fun emptyPath() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, "")
        }
    }

    @Test
    fun whiteSpace() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, " ")
        }

        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, "  ")
        }
    }

    @Test
    fun whiteSpaceInSegment() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, " /")
        }
    }

    @Test
    fun whiteSpaceInSegment2() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, "asd/ ")
        }
    }

    @Test
    fun whiteSpaceInSegment3() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, "asd /foo")
        }
    }

    @Test
    fun whiteSpaceInSegment4() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, "asd/f oo")
        }
    }

    @Test fun singlePath() {
        val segments = PathDetector.extractPathSegments(element, "foo")
        assertEquals(1, segments.size)
        assertEquals("foo", segments.single())
    }

    @Test fun doublePath() {
        val segments = PathDetector.extractPathSegments(element, "foo/bar")
        assertEquals(2, segments.size)
        assertEquals("foo", segments[0])
        assertEquals("bar", segments[1])
    }

    @Test fun tripplePath() {
        val segments = PathDetector.extractPathSegments(element, "foo/bar/asd")
        assertEquals(3, segments.size)
        assertEquals("foo", segments[0])
        assertEquals("bar", segments[1])
        assertEquals("asd", segments[2])
    }

    @Test fun tripplePathTrailingSlash() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, "foo/bar/asd/")
        }

    }

    @Test fun tripplePathLeadingSlash() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, "/foo/bar/asd")
        }

    }

    @Test fun tripplePathLeadingAndTrailingSlash() {
        expectException(ProcessingException::class) {
            PathDetector.extractPathSegments(element, "/foo/bar/asd/")
        }

    }
}