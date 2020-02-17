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

package com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import java.util.List;
import java.util.Objects;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Company {

  @PropertyElement String name;

  @Element List<Person> persons;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company)) return false;

    Company company = (Company) o;

    if (!Objects.equals(name, company.name)) return false;
    return Objects.equals(persons, company.persons);
  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (persons != null ? persons.hashCode() : 0);
    return result;
  }
}
