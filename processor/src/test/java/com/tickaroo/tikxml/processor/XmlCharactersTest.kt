package com.tickaroo.tikxml.processor

import org.junit.Assert
import org.junit.Test

/**
 *
 * @author Hannes Dorfmann
 */
class XmlCharactersTest {


    @Test
    fun test(){
        Assert.assertTrue(XmlCharacters.containsXmlCharacter("a<"));
        Assert.assertTrue(XmlCharacters.containsXmlCharacter("<a"));
        Assert.assertTrue(XmlCharacters.containsXmlCharacter("<"));


        Assert.assertTrue(XmlCharacters.containsXmlCharacter("a>"));
        Assert.assertTrue(XmlCharacters.containsXmlCharacter(">a"));
        Assert.assertTrue(XmlCharacters.containsXmlCharacter(">"));


        Assert.assertTrue(XmlCharacters.containsXmlCharacter("a\""));
        Assert.assertTrue(XmlCharacters.containsXmlCharacter("\"a"));
        Assert.assertTrue(XmlCharacters.containsXmlCharacter("\""));

        Assert.assertTrue(XmlCharacters.containsXmlCharacter("a'"));
        Assert.assertTrue(XmlCharacters.containsXmlCharacter("'a"));
        Assert.assertTrue(XmlCharacters.containsXmlCharacter("'"));
    }
}