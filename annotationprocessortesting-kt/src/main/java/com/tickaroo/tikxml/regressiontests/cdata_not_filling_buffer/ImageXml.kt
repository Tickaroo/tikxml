package com.tickaroo.tikxml.regressiontests.cdata_not_filling_buffer

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml
internal class ImageXml {

    @Attribute
    var href: String? = null

}