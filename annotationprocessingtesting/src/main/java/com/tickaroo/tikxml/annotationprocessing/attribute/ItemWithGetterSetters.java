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
}
