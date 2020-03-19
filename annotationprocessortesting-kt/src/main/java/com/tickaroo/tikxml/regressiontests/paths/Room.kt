package com.tickaroo.tikxml.regressiontests.paths

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml
class Room {
  @PropertyElement
  var number: String? = null

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Room

    if (number != other.number) return false

    return true
  }

  override fun hashCode(): Int {
    return number?.hashCode() ?: 0
  }

}