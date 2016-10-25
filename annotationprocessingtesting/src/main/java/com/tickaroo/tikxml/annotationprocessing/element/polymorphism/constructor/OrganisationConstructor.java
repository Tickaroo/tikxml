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

package com.tickaroo.tikxml.annotationprocessing.element.polymorphism.constructor;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class OrganisationConstructor extends WriterConstructor {

  private String address;

  public OrganisationConstructor(@PropertyElement String name, @PropertyElement String address) {
    super(name);
    this.address = address;
  }

  public String getAddress() {
    return address;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrganisationConstructor)) return false;

    OrganisationConstructor that = (OrganisationConstructor) o;

    return address != null ? address.equals(that.address) : that.address == null;
  }

  @Override public int hashCode() {
    return address != null ? address.hashCode() : 0;
  }
}
