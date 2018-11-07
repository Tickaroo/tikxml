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

package com.tickaroo.tikxml.annotationprocessing.element.polymorphism;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Journalist extends Writer {

  @PropertyElement int age;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Journalist)) return false;
    if (!super.equals(o)) return false;

    Journalist that = (Journalist) o;

    return age == that.age;
  }

  @Override public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + age;
    return result;
  }
}
