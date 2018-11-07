package com.tickaroo.tikxml.regressiontests

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

/**
 * A Element that skips some inner elements
 *
 * @author Hannes Dorfmann
 */
@Xml(name = "team")
data class TeamDataClass(
        @field:Attribute
        var id: Int = 0,
        @field:Attribute
        var countryId: String? = null,
        @field:Attribute
        var shortName: String? = null,
        @field:Attribute
        var longName: String? = null,
        @field:Attribute
        var token: String? = null,
        @field:Attribute
        var iconSmall: String? = null,
        @field:Attribute
        var iconBig: String? = null,
        @field:Attribute
        var defaultLeagueId: Int = 0,
        @field:Attribute
        var lat: Double = 0.toDouble(),
        @field:Attribute
        var lng: Double = 0.toDouble()
)