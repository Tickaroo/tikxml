package com.tickaroo.tikxml.annotationprocessing.attribute.constructor;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@Xml
public class ItemConstructor {

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

  public ItemConstructor(@Attribute String aString, @Attribute int anInt, @Attribute boolean aBoolean, @Attribute double aDouble, @Attribute long aLong,
      @Attribute(converter = DateConverter.class) Date aDate, @Attribute Integer intWrapper, @Attribute Boolean booleanWrapper, @Attribute Double doubleWrapper,
      @Attribute Long longWrapper) {
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

  public String getAString() {
    return aString;
  }

  public int getAnInt() {
    return anInt;
  }

  public boolean isABoolean() {
    return aBoolean;
  }

  public double getADouble() {
    return aDouble;
  }

  public long getALong() {
    return aLong;
  }

  public Date getADate() {
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
