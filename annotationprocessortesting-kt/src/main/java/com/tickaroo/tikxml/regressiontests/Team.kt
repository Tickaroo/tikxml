package com.tickaroo.tikxml.regressiontests

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

/**
 * A Element that skips some inner elements
 *
 * @author Hannes Dorfmann
 */
@Xml
class Team {

    @Attribute
    var id: Int = 0
    @Attribute
    var countryId: String? = null
    @Attribute
    var shortName: String? = null
    @Attribute
    var longName: String? = null
    @Attribute
    var token: String? = null
    @Attribute
    var iconSmall: String? = null
    @Attribute
    var iconBig: String? = null
    @Attribute
    var defaultLeagueId: Int = 0
    @Attribute
    var lat: Double = 0.toDouble()
    @Attribute
    var lng: Double = 0.toDouble()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Team) return false

        val team = other as Team?

        if (id != team!!.id) return false
        if (defaultLeagueId != team.defaultLeagueId) return false
        if (java.lang.Double.compare(team.lat, lat) != 0) return false
        if (java.lang.Double.compare(team.lng, lng) != 0) return false
        if (if (countryId != null) countryId != team.countryId else team.countryId != null)
            return false
        if (if (shortName != null) shortName != team.shortName else team.shortName != null)
            return false
        if (if (longName != null) longName != team.longName else team.longName != null) return false
        if (if (token != null) token != team.token else team.token != null) return false
        if (if (iconSmall != null) iconSmall != team.iconSmall else team.iconSmall != null)
            return false
        return if (iconBig != null) iconBig == team.iconBig else team.iconBig == null
    }

    override fun hashCode(): Int {
        var result: Int
        var temp: Long
        result = id
        result = 31 * result + if (countryId != null) countryId!!.hashCode() else 0
        result = 31 * result + if (shortName != null) shortName!!.hashCode() else 0
        result = 31 * result + if (longName != null) longName!!.hashCode() else 0
        result = 31 * result + if (token != null) token!!.hashCode() else 0
        result = 31 * result + if (iconSmall != null) iconSmall!!.hashCode() else 0
        result = 31 * result + if (iconBig != null) iconBig!!.hashCode() else 0
        result = 31 * result + defaultLeagueId
        temp = java.lang.Double.doubleToLongBits(lat)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(lng)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        return result
    }
}
