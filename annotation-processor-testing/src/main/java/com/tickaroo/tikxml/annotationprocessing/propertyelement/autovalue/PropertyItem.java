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

package com.tickaroo.tikxml.annotationprocessing.propertyelement.autovalue;

import com.google.auto.value.AutoValue;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
@AutoValue
public abstract class PropertyItem {
  @PropertyElement public abstract String aString();
  @PropertyElement public abstract int anInt();
  @PropertyElement public abstract boolean aBoolean();
  @PropertyElement public abstract double aDouble();
  @PropertyElement public abstract long aLong();
  @PropertyElement(converter = DateConverter.class) public abstract Date aDate();

  @PropertyElement public abstract  Integer intWrapper();
  @PropertyElement public abstract Boolean booleanWrapper();
  @PropertyElement public abstract Double doubleWrapper();
  @PropertyElement public abstract Long longWrapper();
}
