package com.tickaroo.tikxml.regressiontests.cdata_not_filling_buffer

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml
internal class AudioXml {

    @Attribute
    var length: String? = null

    @Attribute
    var type: String? = null

    @Attribute
    var url: String? = null

}