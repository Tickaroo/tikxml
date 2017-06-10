package com.tickaroo.tikxml.annotationprocessing.attribute.constructor;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.annotationprocessing.DateConverter;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@Xml(name = "item")
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

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemConstructor)) return false;

    ItemConstructor that = (ItemConstructor) o;

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
