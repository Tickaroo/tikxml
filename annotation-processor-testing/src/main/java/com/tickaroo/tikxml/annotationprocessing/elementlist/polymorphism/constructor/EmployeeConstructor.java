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

package com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism.constructor;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism.Person;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "employee")
public class EmployeeConstructor implements Person {

  private String name;

  public EmployeeConstructor(@PropertyElement String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmployeeConstructor)) return false;

    EmployeeConstructor that = (EmployeeConstructor) o;

    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
