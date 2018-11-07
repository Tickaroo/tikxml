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

package com.tickaroo.tikxml.annotationprocessing.attribute;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class Item {
  @Attribute String aString;
  @Attribute int anInt;
  @Attribute boolean aBoolean;
  @Attribute double aDouble;
  @Attribute long aLong;
  @Attribute(converter = DateConverter.class) Date aDate;

  @Attribute Integer intWrapper;
  @Attribute Boolean booleanWrapper;
  @Attribute Double doubleWrapper;
  @Attribute Long longWrapper;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Item)) return false;

    Item item = (Item) o;

    if (anInt != item.anInt) return false;
    if (aBoolean != item.aBoolean) return false;
    if (Double.compare(item.aDouble, aDouble) != 0) return false;
    if (aLong != item.aLong) return false;
    if (aString != null ? !aString.equals(item.aString) : item.aString != null) return false;
    if (aDate != null ? !aDate.equals(item.aDate) : item.aDate != null) return false;
    if (intWrapper != null ? !intWrapper.equals(item.intWrapper) : item.intWrapper != null) {
      return false;
    }
    if (booleanWrapper != null ? !booleanWrapper.equals(item.booleanWrapper)
        : item.booleanWrapper != null) {
      return false;
    }
    if (doubleWrapper != null ? !doubleWrapper.equals(item.doubleWrapper)
        : item.doubleWrapper != null) {
      return false;
    }
    return longWrapper != null ? longWrapper.equals(item.longWrapper) : item.longWrapper == null;
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
