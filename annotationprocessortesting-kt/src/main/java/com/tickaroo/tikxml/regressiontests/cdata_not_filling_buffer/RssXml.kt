package com.tickaroo.tikxml.regressiontests.cdata_not_filling_buffer

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "rss" )
internal class RssXml {

    @Element(name = "channel")
    lateinit var detail: DetailXml

}