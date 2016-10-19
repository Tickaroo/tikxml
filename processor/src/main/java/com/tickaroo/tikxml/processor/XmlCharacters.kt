package com.tickaroo.tikxml.processor

/**
 * Simple helper class
 * @author Hannes Dorfmann
 */
object XmlCharacters {

    private val preservedXmlCharacters = Regex(".*(<|>|\"|')+.*") // TODO & char

    fun containsXmlCharacter(toCheck: String) = toCheck.contains(preservedXmlCharacters)
}