package com.tickaroo.tikxml.regressiontests.unterminatedcomment

import com.tickaroo.tikxml.TestUtils
import com.tickaroo.tikxml.TikXml
import org.junit.Assert
import org.junit.Test

/**
 * Regression test for
 * https://github.com/Tickaroo/tikxml/issues/144
 */
class UnterminateCommentTest {

    @Test
    fun parse() {
        val xml = TikXml.Builder().exceptionOnUnreadXml(false).build()

        val rss: RssXml = xml.read(TestUtils.sourceForFile("regression/unterminated_comment.xml"), RssXml::class.java)

        Assert.assertEquals("Rádiofobia Classics", rss.detail.title)
        Assert.assertEquals("pt-BR", rss.detail.language)
        Assert.assertEquals(64, rss.detail.episodesXml.size)

        rss.detail.episodesXml.forEach {
            Assert.assertNotNull(it.audioFile)
            Assert.assertNotNull(it.guid)
            Assert.assertFalse(it.audioFile!!.url.isNullOrEmpty())
        }

    }
}