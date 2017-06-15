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

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.ElementNameMatcher;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism.Person;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "company")
public class CompanyConstructor {

  private String name;
  private List<Person> persons;

  public CompanyConstructor(@PropertyElement String name,
      @Element(typesByElement = {
          @ElementNameMatcher(type = BossConstructor.class, name = "boss"),
          @ElementNameMatcher(type = EmployeeConstructor.class, name = "employee"),
      }) List<Person> persons) {
    this.name = name;
    this.persons = persons;
  }

  public String getName() {
    return name;
  }

  public List<Person> getPersons() {
    return persons;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CompanyConstructor)) return false;

    CompanyConstructor that = (CompanyConstructor) o;

    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    return persons != null ? persons.equals(that.persons) : that.persons == null;
  }

  @Override public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (persons != null ? persons.hashCode() : 0);
    return result;
  }
}
