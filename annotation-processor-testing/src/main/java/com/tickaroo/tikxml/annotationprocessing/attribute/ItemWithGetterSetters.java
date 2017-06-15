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
@Xml(name = "item")
public class ItemWithGetterSetters {

  @Attribute private String aString;
  @Attribute private int anInt;
  @Attribute private boolean aBoolean;
  @Attribute private double aDouble;
  @Attribute private long aLong;
  @Attribute(converter = DateConverter.class) private Date aDate;
  @Attribute private Integer intWrapper;
  @Attribute private Boolean booleanWrapper;
  @Attribute private Double doubleWrapper;
  @Attribute private Long longWrapper;

  public String getAString() {
    return aString;
  }

  public void setAString(String aString) {
    this.aString = aString;
  }

  public int getAnInt() {
    return anInt;
  }

  public void setAnInt(int anInt) {
    this.anInt = anInt;
  }

  public boolean isABoolean() {
    return aBoolean;
  }

  public void setABoolean(boolean aBoolean) {
    this.aBoolean = aBoolean;
  }

  public double getADouble() {
    return aDouble;
  }

  public void setADouble(double aDouble) {
    this.aDouble = aDouble;
  }

  public long getALong() {
    return aLong;
  }

  public void setALong(long aLong) {
    this.aLong = aLong;
  }

  public Date getADate() {
    return aDate;
  }

  public void setADate(Date aDate) {
    this.aDate = aDate;
  }

  public Integer getIntWrapper() {
    return intWrapper;
  }

  public void setIntWrapper(Integer intWrapper) {
    this.intWrapper = intWrapper;
  }

  public Boolean getBooleanWrapper() {
    return booleanWrapper;
  }

  public void setBooleanWrapper(Boolean booleanWrapper) {
    this.booleanWrapper = booleanWrapper;
  }

  public Double getDoubleWrapper() {
    return doubleWrapper;
  }

  public void setDoubleWrapper(Double doubleWrapper) {
    this.doubleWrapper = doubleWrapper;
  }

  public Long getLongWrapper() {
    return longWrapper;
  }

  public void setLongWrapper(Long longWrapper) {
    this.longWrapper = longWrapper;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemWithGetterSetters)) return false;

    ItemWithGetterSetters that = (ItemWithGetterSetters) o;

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
    return longWrapper != null ? longWrapper.equals(that.longWrapper) : that.longWrapper == null;
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
