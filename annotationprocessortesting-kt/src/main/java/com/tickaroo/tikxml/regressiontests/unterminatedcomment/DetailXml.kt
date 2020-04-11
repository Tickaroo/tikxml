package com.tickaroo.tikxml.regressiontests.unterminatedcomment

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml
internal class DetailXml {

    @PropertyElement
    lateinit var title: String

    @PropertyElement
    lateinit var language: String

    @PropertyElement
    var description: String? = null

    @PropertyElement
    var summary: String? = null

    @Element(name = "item")
    lateinit var episodesXml: List<EpisodeXml>
}