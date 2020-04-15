package com.tickaroo.tikxml.regressiontests.cdata_not_filling_buffer

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml
internal class EpisodeXml {
    @PropertyElement
    var guid: String? = null

    @PropertyElement
    var title: String? = null

    @PropertyElement
    var pubDate: String? = null

    @Element(name = "enclosure")
    var audioFile: AudioXml? = null

    @PropertyElement
    var description: String? = null

    @PropertyElement(name = "itunes:duration")
    var duration: String? = null

    @PropertyElement(name = "itunes:explicit")
    var explicit: Boolean = false

    @PropertyElement(name = "itunes:summary")
    var summary: String? = null

    @PropertyElement(name = "itunes:subtitle")
    var subtitle: String? = null

    @Element(name = "itunes:image")
    var image: ImageXml? = null

}