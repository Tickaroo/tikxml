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

package com.tickaroo.tikxml.annotationprocessing.propertyelement.constructor;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.ScanMode;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@Xml(scanMode = ScanMode.ANNOTATIONS_ONLY)
public class PropertyItemConstructor {
  private String aString;
  private int anInt;
  private boolean aBoolean;
  private double aDouble;
  private long aLong;
  private Date aDate;

  private Integer intWrapper;
  private Boolean booleanWrapper;
  private Double doubleWrapper;
  private Long longWrapper;

  public PropertyItemConstructor(@PropertyElement String aString, @PropertyElement int anInt, @PropertyElement boolean aBoolean, @PropertyElement double aDouble,
      @PropertyElement long aLong, @PropertyElement(converter = DateConverter.class) Date aDate, @PropertyElement Integer intWrapper, @PropertyElement Boolean booleanWrapper,
      @PropertyElement Double doubleWrapper, @PropertyElement Long longWrapper) {
    this.aString = aString;
    this.anInt = anInt;
    this.aBoolean = aBoolean;
    this.aDouble = aDouble;
    this.aLong = aLong;
    this.aDate = aDate;
    this.intWrapper = intWrapper;
    this.booleanWrapper = booleanWrapper;
    this.doubleWrapper = doubleWrapper;
    this.longWrapper = longWrapper;
  }

  public String getaString() {
    return aString;
  }

  public int getAnInt() {
    return anInt;
  }

  public boolean isaBoolean() {
    return aBoolean;
  }

  public double getaDouble() {
    return aDouble;
  }

  public long getaLong() {
    return aLong;
  }

  public Date getaDate() {
    return aDate;
  }

  public Integer getIntWrapper() {
    return intWrapper;
  }

  public Boolean getBooleanWrapper() {
    return booleanWrapper;
  }

  public Double getDoubleWrapper() {
    return doubleWrapper;
  }

  public Long getLongWrapper() {
    return longWrapper;
  }
}
