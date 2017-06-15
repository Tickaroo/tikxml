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

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.elementlist.polymorphism.Person;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "boss")
public class BossConstructor implements Person {
  private String firstName;
  private String lastName;

  public BossConstructor(@Attribute String firstName, @Attribute String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BossConstructor)) return false;

    BossConstructor that = (BossConstructor) o;

    if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
      return false;
    return lastName != null ? lastName.equals(that.lastName) : that.lastName == null;
  }

  @Override public int hashCode() {
    int result = firstName != null ? firstName.hashCode() : 0;
    result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
    return result;
  }
}
