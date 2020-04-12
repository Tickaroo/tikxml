package com.tickaroo.tikxml.regressiontests.unterminatedcomment

import com.tickaroo.tikxml.TestUtils
import com.tickaroo.tikxml.TikXml
import org.junit.Assert
import org.junit.Test

/**
 * Regression test for
 * https://github.com/Tickaroo/tikxml/issues/144
 *
 * The problem was that if a <![CDATA]]> is starting but buffer didn't get filled in isCDATA() check
 * the XmlReader treated it as <! comment instead of CDATA just because the buffer's current
 * segment (8192 bytes) and position was at the end of the segment but filling buffer with the next
 * segment hasn't been called ( buffer.request(CDATA.size) --> this was the bug), therefore XmlReader
 * didnt know that the already read <! is followed by CDATA]]> in the next buffer's segment.
 */
class CDATA_BufferNotFilledTest {

    @Test
    fun parse() {
        val xml = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val rss: RssXml = xml.read(TestUtils.sourceForFile("regression/podcast_feed.rss"), RssXml::class.java)

        Assert.assertEquals("Podcast &#8211; Software Engineering Daily", rss.detail.title)
        Assert.assertEquals("en-US", rss.detail.language)
        Assert.assertEquals(100, rss.detail.episodesXml.size)

        rss.detail.episodesXml.forEach {
            Assert.assertNotNull(it.audioFile)
            Assert.assertNotNull(it.guid)
            Assert.assertFalse(it.audioFile!!.url.isNullOrEmpty())
        }
    }
}