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

package com.tickaroo.tikxml.annotationprocessing.propertyelement;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
public class PropertyItem {
  @PropertyElement String aString;
  @PropertyElement int anInt;
  @PropertyElement boolean aBoolean;
  @PropertyElement double aDouble;
  @PropertyElement long aLong;
  @PropertyElement(converter = DateConverter.class) Date aDate;

  @PropertyElement Integer intWrapper;
  @PropertyElement Boolean booleanWrapper;
  @PropertyElement Double doubleWrapper;
  @PropertyElement Long longWrapper;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PropertyItem)) return false;

    PropertyItem that = (PropertyItem) o;

    if (anInt != that.anInt) return false;
    if (aBoolean != that.aBoolean) return false;
    if (Double.compare(that.aDouble, aDouble) != 0) return false;
    if (aLong != that.aLong) return false;
    if (aString != null ? !aString.equals(that.aString) : that.aString != null) return false;
    if (aDate != null ? !aDate.equals(that.aDate) : that.aDate != null) return false;
    if (intWrapper != null ? !intWrapper.equals(that.intWrapper) : that.intWrapper != null) {
      return false;
    }
    if (booleanWrapper != null ? !booleanWrapper.equals(that.booleanWrapper)
        : that.booleanWrapper != null) {
      return false;
    }
    if (doubleWrapper != null ? !doubleWrapper.equals(that.doubleWrapper)
        : that.doubleWrapper != null) {
      return false;
    }
    if (longWrapper != null ? !longWrapper.equals(that.longWrapper) : that.longWrapper != null) {
      return false;
    }

    return true;
  }

  @Override public int hashCode() {
    int result;
    long temp;
    result = aString != null ? aString.hashCode() : 0;
    result = 31 * result + anInt;
    result = 31 * result + (aBoolean ? 1 : 0);
    temp = Double.doubleToLongBits(aDouble);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (int) (aLong ^ (aLong >>> 32));
    result = 31 * result + (aDate != null ? aDate.hashCode() : 0);
    result = 31 * result + (intWrapper != null ? intWrapper.hashCode() : 0);
    result = 31 * result + (booleanWrapper != null ? booleanWrapper.hashCode() : 0);
    result = 31 * result + (doubleWrapper != null ? doubleWrapper.hashCode() : 0);
    result = 31 * result + (longWrapper != null ? longWrapper.hashCode() : 0);
    return result;
  }
}
