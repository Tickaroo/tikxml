/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tickaroo.tikxml;

import java.io.IOException;

import okio.Buffer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.tickaroo.tikxml.TestUtils.readerFrom;

/**
 * @author Hannes Dorfmann
 */
public class XmlReaderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public void nullBuffer() {
        exception.expect(NullPointerException.class);
        XmlReader.of(null);
    }

    @Test
    public void readObjectWithAttributes() throws IOException {

        String elementText =
                "I'm an ElementText\n with multiple lines and special \n chars @ \" üäöß?1§$%&&/()=*'";
        String xml = "<element a=\"qwe\" b='123' c=\"skipMe\">" + elementText + "</element>";
        XmlReader reader = readerFrom(xml);

        try {

            Assert.assertEquals(reader.peek(), XmlReader.XmlToken.ELEMENT_BEGIN);
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();

            Assert.assertEquals(XmlReader.XmlToken.ELEMENT_NAME, reader.peek());
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());

            Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_NAME, reader.peek());
            Assert.assertEquals("a", reader.nextAttributeName());

            Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_VALUE, reader.peek());
            Assert.assertEquals("qwe", reader.nextAttributeValue());

            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_NAME, reader.peek());
            Assert.assertEquals("b", reader.nextAttributeName());

            Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_VALUE, reader.peek());
            Assert.assertEquals("123", reader.nextAttributeValue());

            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals(XmlReader.XmlToken.ATTRIBUTE_NAME, reader.peek());
            Assert.assertEquals("c", reader.nextAttributeName());

            reader.skipAttributeValue();

            Assert.assertFalse(reader.hasAttribute());

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals(XmlReader.XmlToken.ELEMENT_TEXT_CONTENT, reader.peek());
            Assert.assertEquals(elementText, reader.nextTextContent());
            Assert.assertFalse(reader.hasTextContent());

            Assert.assertEquals(XmlReader.XmlToken.ELEMENT_END, reader.peek());
            reader.endElement();

            Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
        } finally {
            reader.close();
        }
    }

    @Test
    public void attributeWithDoubleEqualsSign() throws IOException {
        String xml = "<element a==\"qwe\"></element>";
        XmlReader reader = readerFrom(xml);
        try {
            reader.beginElement();
            reader.nextElementName();
            reader.nextAttributeName();
            exception.expect(IOException.class);
            reader.nextAttributeValue();
        } finally {
            reader.close();
        }
    }

    @Test
    public void attributeNoQuotes() throws IOException {
        String xml = "<element a=qwe></element>";
        XmlReader reader = readerFrom(xml);

        try {
            reader.beginElement();
            reader.nextElementName();
            reader.nextAttributeName();
            exception.expect(IOException.class);
            reader.nextAttributeValue();
        } finally {
            reader.close();
        }
    }

    @Test
    public void validWithWhitespaces() throws IOException {
        String xml = "<  element    a = \"qwe\"  ></element>";
        XmlReader reader = readerFrom(xml);

        try {
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertEquals("a", reader.nextAttributeName());
            Assert.assertEquals("qwe", reader.nextAttributeValue());
            Assert.assertFalse(reader.hasTextContent());
            reader.endElement();
        } finally {
            reader.close();
        }
    }

    @Test
    public void readObjectWithMultilineComment() throws IOException {
        String xml = "<element><!-- comment \n multiline \n -->Value</element>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals(reader.nextTextContent(), "Value");
            reader.endElement();

            Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
        } finally {
            reader.close();
        }
    }

    @Test
    public void emptyTextContent() throws IOException {
        String xml = "<element></element>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertFalse(reader.hasTextContent());
            reader.endElement();
            Assert.assertFalse(reader.hasElement());
            Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
        } finally {
            reader.close();
        }
    }

    @Test
    public void jsonArrayTextContent() throws IOException {
        String xml = "<element><![CDATA[[{a:'b'}, {b:'c'}]]]></element>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("[{a:'b'}, {b:'c'}]", reader.nextTextContent());
            reader.endElement();
            Assert.assertFalse(reader.hasElement());
            Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
        } finally {
            reader.close();
        }
    }

    @Test
    public void inlineClosing() throws IOException {
        String xml = "<element a='foo' />";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("a", reader.nextAttributeName());
            Assert.assertEquals("foo", reader.nextAttributeValue());

            Assert.assertEquals(XmlReader.XmlToken.ELEMENT_END, reader.peek());
            reader.endElement();
            Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
        } finally {
            reader.close();
        }
    }

    @Test
    public void incompleteSingleQuoteAttribute() throws IOException {
        String xml = "<element a='foo />";
        XmlReader reader = readerFrom(xml);

        try {
            reader.hasElement();
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("a", reader.nextAttributeName());
            exception.expect(IOException.class);
            exception.expectMessage(
                    "Unterminated string (single quote ' is missing) at path /element[@a]");
            reader.nextAttributeValue();
        } finally {
            reader.close();
        }
    }

    @Test
    public void incompleteDoubleQuoteAttribute() throws IOException {
        String xml = "<element a=\"foo ></element>";
        XmlReader reader = readerFrom(xml);

        try {
            reader.hasElement();
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("a", reader.nextAttributeName());
            exception.expect(IOException.class);
            exception.expectMessage(
                    "Unterminated string (double quote \" is missing) at path /element[@a]");
            reader.nextAttributeValue();
        } finally {
            reader.close();
        }
    }

    @Test
    public void missingClosingTag() throws IOException {
        String xml = "<element a='foo' >";
        XmlReader reader = readerFrom(xml);
        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("a", reader.nextAttributeName());
            Assert.assertEquals("foo", reader.nextAttributeValue());

            exception.expect(IOException.class);
            exception.expectMessage("Unexpected end of input at path /element/text()");
            reader.endElement();
        } finally {
            reader.close();
        }
    }

    @Test
    public void unclosedElement() throws IOException {
        String xml = "<fooElement a=\"qwe\">This is the text";
        XmlReader reader = readerFrom(xml);

        try {
            reader.beginElement();
            reader.nextElementName();
            reader.nextAttributeName();
            reader.nextAttributeValue();
            exception.expect(IOException.class);
            exception.expectMessage(
                    "Unterminated element text content. Expected </fooElement> but haven't found at path /fooElement/text()");
            reader.nextTextContent();
        } finally {
            reader.close();
        }
    }

    @Test
    public void noAttributesNoTextContent() throws IOException {
        String xml = "<foo></foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());
            Assert.assertFalse(reader.hasAttribute());
            Assert.assertFalse(reader.hasTextContent());
            reader.endElement();

            Assert.assertFalse(reader.hasElement());
            Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
        } finally {
            reader.close();
        }
    }

    @Test
    public void noAttributesButTextContent() throws IOException {
        String xml = "<foo>Value</foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());
            Assert.assertFalse(reader.hasAttribute());
            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("Value", reader.nextTextContent());
            reader.endElement();

            Assert.assertFalse(reader.hasElement());
            Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
        } finally {
            reader.close();
        }
    }

    @Test
    public void skipTextContent() throws IOException {
        String xml = "<foo>Value</foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());
            Assert.assertFalse(reader.hasAttribute());
            Assert.assertTrue(reader.hasTextContent());
            reader.skipTextContent();
            reader.endElement();

            Assert.assertFalse(reader.hasElement());
            Assert.assertEquals(XmlReader.XmlToken.END_OF_DOCUMENT, reader.peek());
        } finally {
            reader.close();
        }
    }

    @Test
    public void skipUncompleteTextContent() throws IOException {
        String xml = "<foo>Value";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());
            Assert.assertFalse(reader.hasAttribute());
            Assert.assertTrue(reader.hasTextContent());
            exception.expect(IOException.class);
            exception.expectMessage(
                    "Unterminated element text content. Expected </foo> but haven't found at path /foo/text()");
            reader.skipTextContent();
        } finally {
            reader.close();
        }
    }

    @Test
    public void noElementName() throws IOException {
        String xml = "<></>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();

            exception.expect(IOException.class);
            exception.expectMessage("Expected xml element name (literal expected) at path /");
            reader.nextElementName();
        } finally {
            reader.close();
        }
    }

    @Test
    public void nestedElements() throws IOException {
        String xml = "<foo a='1'> <bar b='2'></bar> <bar b='3'> <other c='4' /> </bar> </foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("a", reader.nextAttributeName());
            Assert.assertEquals("1", reader.nextAttributeValue());

            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("bar", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("b", reader.nextAttributeName());
            Assert.assertEquals("2", reader.nextAttributeValue());
            reader.endElement();

            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("bar", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("b", reader.nextAttributeName());
            Assert.assertEquals("3", reader.nextAttributeValue());

            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("other", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("c", reader.nextAttributeName());
            Assert.assertEquals("4", reader.nextAttributeValue());
            reader.endElement(); // end other

            reader.endElement(); // end bar

            reader.endElement(); // end foo
        } finally {
            reader.close();
        }
    }

    @Test
    public void mixingTextContentChildElements() throws IOException {
        String xml =
                "<foo a='1'>Value1 first part<bar b='2'></bar>Value2 second part<bar b='3'> <other c='4' /> </bar>Value3 third part</foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("a", reader.nextAttributeName());
            Assert.assertEquals("1", reader.nextAttributeValue());

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("Value1 first part", reader.nextTextContent());

            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("bar", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("b", reader.nextAttributeName());
            Assert.assertEquals("2", reader.nextAttributeValue());
            reader.endElement();

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("Value2 second part", reader.nextTextContent());

            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("bar", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("b", reader.nextAttributeName());
            Assert.assertEquals("3", reader.nextAttributeValue());

            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("other", reader.nextElementName());
            Assert.assertTrue(reader.hasAttribute());
            Assert.assertEquals("c", reader.nextAttributeName());
            Assert.assertEquals("4", reader.nextAttributeValue());
            reader.endElement(); // end other

            reader.endElement(); // end bar

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("Value3 third part", reader.nextTextContent());

            reader.endElement(); // end foo
        } finally {
            reader.close();
        }
    }

    @Test
    public void cdata() throws IOException {
        String cdata = "< hello <> & cdata</foo>";
        String xml = "<foo>NormalValue<![CDATA[" + cdata + "]]>nextvalue</foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());

            Assert.assertFalse(reader.hasAttribute());

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("NormalValue", reader.nextTextContent());

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals(cdata, reader.nextTextContent());

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("nextvalue", reader.nextTextContent());

            reader.endElement();
            Assert.assertFalse(reader.hasElement());
        } finally {
            reader.close();
        }
    }

    @Test
    public void missingClosingCDATA() throws IOException {
        String cdata = "< hello <> & cdata</foo>";
        String xml = "<foo>NormalValue<![CDATA[" + cdata + "nextvalue</foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());

            Assert.assertFalse(reader.hasAttribute());

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("NormalValue", reader.nextTextContent());

            exception.expect(IOException.class);
            exception.expectMessage("<![CDATA[ at /foo/text() has never been closed with ]]>");
            Assert.assertEquals(cdata, reader.nextTextContent());
        } finally {
            reader.close();
        }
    }

    @Test
    public void skipCDATA() throws IOException {
        String cdata = "< hello <> & cdata</foo>";
        String xml = "<foo>NormalValue<![CDATA[" + cdata + "]]>nextvalue</foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());

            Assert.assertFalse(reader.hasAttribute());

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("NormalValue", reader.nextTextContent());

            Assert.assertTrue(reader.hasTextContent()); // CDATA
            reader.skipTextContent();

            Assert.assertTrue(reader.hasTextContent());
            Assert.assertEquals("nextvalue", reader.nextTextContent());

            reader.endElement();
            Assert.assertFalse(reader.hasElement());
        } finally {
            reader.close();
        }
    }

    @Test
    public void skipRemainingElement() throws IOException {
        String xml =
                "<foo><e1></e1><bar a='1' b='2'>TextContent<child a='1'>Child text Value<other b='123'>Text<inline /></other></child> <![CDATA[some <cdata></> &]]> </bar>TextAfterSkippedElement<element></element></foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());

            reader.beginElement();
            Assert.assertEquals("e1", reader.nextElementName());
            reader.endElement();

            reader.beginElement(); // <bar> element
            reader.skipRemainingElement(); // skip <bar>

            Assert.assertEquals("TextAfterSkippedElement", reader.nextTextContent());

            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            reader.endElement();

            reader.endElement(); // end <foo>
        } finally {
            reader.close();
        }
    }

    @Test
    public void skipRemainingElementUnclosed() throws IOException {
        String xml =
                "<foo><e1></e1><bar a='1' b='2'>TextContent<child a='1'>Child text Value<other b='123'>Text<inline /></other> <![CDATA[some <cdata></> &]]>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());

            reader.beginElement();
            Assert.assertEquals("e1", reader.nextElementName());
            reader.endElement();

            reader.beginElement(); // <bar> element
            exception.expect(IOException.class);
            exception.expectMessage("Unexpected end of input at path /foo/bar/child/text()");
            reader.skipRemainingElement(); // skip <bar>
        } finally {
            reader.close();
        }
    }

    @Test
    public void callingSkipRemainingElementInWrongPlace() throws IOException {
        String xml =
                "<foo><e1></e1><bar a='1' b='2'>TextContent<child a='1'>Child text Value<other b='123'>Text<inline /></other></child> <![CDATA[some <cdata></> &]]> </bar>TextAfterSkippedElement<element></element></foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());

            reader.beginElement();
            Assert.assertEquals("e1", reader.nextElementName());
            reader.endElement();

            exception.expect(AssertionError.class);
            exception.expectMessage(
                    "This method can only be invoked after having consumed the opening element via beginElement()");
            reader.skipRemainingElement(); // Forgot to call beginElement() before
        } finally {
            reader.close();
        }
    }

    @Test
    public void readAttributeAsPrimitiveTypes() throws IOException {
        long longMax = Long.MAX_VALUE;
        int intMax = Integer.MAX_VALUE;
        double doubleMax = Double.MAX_VALUE;

        String xml = "<foo aString=\"a very short string\"  aString2='a very short string' anInt=\""
                + intMax
                + "\" anInt2='"
                + intMax
                + "' aLong=\""
                + longMax
                + "\"  aLong2='"
                + longMax
                + "' aBool=\"true\" aBool2=\"false\" aBool3='true' aBool4='false'  aDouble=\""
                + doubleMax
                + "\" aDouble2='"
                + doubleMax
                + "' />";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());

            Assert.assertEquals("aString", reader.nextAttributeName());
            Assert.assertEquals("a very short string", reader.nextAttributeValue());

            Assert.assertEquals("aString2", reader.nextAttributeName());
            Assert.assertEquals("a very short string", reader.nextAttributeValue());

            Assert.assertEquals("anInt", reader.nextAttributeName());
            Assert.assertEquals(intMax, reader.nextAttributeValueAsInt());

            Assert.assertEquals("anInt2", reader.nextAttributeName());
            Assert.assertEquals(intMax, reader.nextAttributeValueAsInt());

            Assert.assertEquals("aLong", reader.nextAttributeName());
            Assert.assertEquals(longMax, reader.nextAttributeValueAsLong());

            Assert.assertEquals("aLong2", reader.nextAttributeName());
            Assert.assertEquals(longMax, reader.nextAttributeValueAsLong());

            Assert.assertEquals("aBool", reader.nextAttributeName());
            Assert.assertTrue(reader.nextAttributeValueAsBoolean());

            Assert.assertEquals("aBool2", reader.nextAttributeName());
            Assert.assertFalse(reader.nextAttributeValueAsBoolean());

            Assert.assertEquals("aBool3", reader.nextAttributeName());
            Assert.assertTrue(reader.nextAttributeValueAsBoolean());

            Assert.assertEquals("aBool4", reader.nextAttributeName());
            Assert.assertFalse(reader.nextAttributeValueAsBoolean());

            Assert.assertEquals("aDouble", reader.nextAttributeName());
            Assert.assertEquals(doubleMax, reader.nextAttributeValueAsDouble(), 0);

            Assert.assertEquals("aDouble2", reader.nextAttributeName());
            Assert.assertEquals(doubleMax, reader.nextAttributeValueAsDouble(), 0);

            reader.endElement();
        } finally {
            reader.close();
        }
    }

    @Test
    public void readTextContentAsPrimitiveValues() throws IOException {

        String xml = "<foo>" +
                "   <aString>a very short string</aString>" +
                "   <intMax>" + Integer.MAX_VALUE + "</intMax>" +
                "   <intMin>" + Integer.MIN_VALUE + "</intMin>" +
                "   <longMax>" + Long.MAX_VALUE + "</longMax>" +
                "   <longMin>" + Long.MIN_VALUE + "</longMin>" +
                "   <doubleMax>" + Double.MAX_VALUE + "</doubleMax>" +
                "   <doubleMin>" + Double.MIN_VALUE + "</doubleMin>" +
                "   <boolTrue>true</boolTrue>" +
                "   <boolFalse>false</boolFalse>" +
                "</foo>";
        XmlReader reader = readerFrom(xml);

        try {
            Assert.assertTrue(reader.hasElement());
            reader.beginElement();
            Assert.assertEquals("foo", reader.nextElementName());

            reader.beginElement();
            Assert.assertEquals("aString", reader.nextElementName());
            Assert.assertEquals("a very short string", reader.nextTextContent());
            reader.endElement();

            reader.beginElement();
            Assert.assertEquals("intMax", reader.nextElementName());
            Assert.assertEquals(Integer.MAX_VALUE, reader.nextTextContentAsInt());
            reader.endElement();

            reader.beginElement();
            Assert.assertEquals("intMin", reader.nextElementName());
            Assert.assertEquals(Integer.MIN_VALUE, reader.nextTextContentAsInt());
            reader.endElement();

            reader.beginElement();
            Assert.assertEquals("longMax", reader.nextElementName());
            Assert.assertEquals(Long.MAX_VALUE, reader.nextTextContentAsLong());
            reader.endElement();

            reader.beginElement();
            Assert.assertEquals("longMin", reader.nextElementName());
            Assert.assertEquals(Long.MIN_VALUE, reader.nextTextContentAsLong());
            reader.endElement();

            reader.beginElement();
            Assert.assertEquals("doubleMax", reader.nextElementName());
            Assert.assertEquals(Double.MAX_VALUE, reader.nextTextContentAsDouble(), 0);
            reader.endElement();

            reader.beginElement();
            Assert.assertEquals("doubleMin", reader.nextElementName());
            Assert.assertEquals(Double.MIN_VALUE, reader.nextTextContentAsDouble(), 0);
            reader.endElement();

            reader.beginElement();
            Assert.assertEquals("boolTrue", reader.nextElementName());
            Assert.assertTrue(reader.nextTextContentAsBoolean());
            reader.endElement();

            reader.beginElement();
            Assert.assertEquals("boolFalse", reader.nextElementName());
            Assert.assertFalse(reader.nextTextContentAsBoolean());
            reader.endElement();

            reader.endElement();
        } finally {
            reader.close();
        }
    }

    @Test
    public void skipAttribute() throws IOException {

        String xml = "<foo abc=\"123\" />";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        reader.nextElementName();
        reader.skipAttribute();
        reader.endElement();
    }

    @Test
    public void failSkipAttribute() throws IOException {

        String xml = "<foo />";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        reader.nextElementName();
        exception.expect(IOException.class);
        exception.expectMessage("Expected xml element attribute name but was ELEMENT_END at path /foo");
        reader.skipAttribute();
        reader.endElement();
    }

    @Test
    public void emptyTag() throws IOException {

        String xml = "<empty />";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertFalse(reader.hasAttribute());
        reader.nextElementName();
        reader.endElement();
    }

    @Test
    public void emptyTextContentString() throws IOException {

        String xml = "<empty />";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertFalse(reader.hasAttribute());
        reader.nextElementName();
        Assert.assertEquals("", reader.nextTextContent());
        reader.endElement();

        String xml2 = "<empty></empty>";
        XmlReader reader2 = readerFrom(xml2);

        reader2.beginElement();
        Assert.assertFalse(reader2.hasAttribute());
        reader2.nextElementName();
        Assert.assertEquals("", reader2.nextTextContent());
        reader2.endElement();
    }

    @Test
    public void emptyTextContentInteger() throws IOException {

        String xml = "<empty />";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertFalse(reader.hasAttribute());
        reader.nextElementName();
        Assert.assertEquals(0, reader.nextTextContentAsInt());
        reader.endElement();

        String xml2 = "<empty></empty>";
        XmlReader reader2 = readerFrom(xml2);

        reader2.beginElement();
        Assert.assertFalse(reader2.hasAttribute());
        reader2.nextElementName();
        Assert.assertEquals(0, reader2.nextTextContentAsInt());
        reader2.endElement();
    }

    @Test
    public void emptyTextContentDouble() throws IOException {

        String xml = "<empty />";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertFalse(reader.hasAttribute());
        reader.nextElementName();
        Assert.assertEquals(0.0, 0.0, reader.nextTextContentAsDouble());
        reader.endElement();

        String xml2 = "<empty></empty>";
        XmlReader reader2 = readerFrom(xml2);

        reader2.beginElement();
        Assert.assertFalse(reader2.hasAttribute());
        reader2.nextElementName();
        Assert.assertEquals(0.0, 0.0, reader2.nextTextContentAsDouble());
        reader2.endElement();
    }

    @Test
    public void emptyTextContentLong() throws IOException {

        String xml = "<empty />";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertFalse(reader.hasAttribute());
        reader.nextElementName();
        Assert.assertEquals(0, reader.nextTextContentAsLong());
        reader.endElement();

        String xml2 = "<empty></empty>";
        XmlReader reader2 = readerFrom(xml2);

        reader2.beginElement();
        Assert.assertFalse(reader2.hasAttribute());
        reader2.nextElementName();
        Assert.assertEquals(0, reader2.nextTextContentAsLong());
        reader2.endElement();
    }

    @Test
    public void emptyTextContentBoolean() throws IOException {

        String xml = "<empty />";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertFalse(reader.hasAttribute());
        reader.nextElementName();
        Assert.assertEquals(false, reader.nextTextContentAsBoolean());
        reader.endElement();

        String xml2 = "<empty></empty>";
        XmlReader reader2 = readerFrom(xml2);

        reader2.beginElement();
        Assert.assertFalse(reader2.hasAttribute());
        reader2.nextElementName();
        Assert.assertEquals(false, reader2.nextTextContentAsBoolean());
        reader2.endElement();
    }

    @Test
    public void readingLongXmlCausesCopyingStackArray() throws IOException {
        String xml =
                "<root><e0><e1><e2><e3><e4><e5><e6><e7><e8><e9><e10><e11><e12><e13><e14><e15><e16><e17><e18><e19><e20><e21><e22><e23><e24><e25><e26><e27><e28><e29><e30><e31><e32><e33><e34><e35><e36><e37><e38><e39><e40><e41><e42><e43><e44><e45><e46><e47><e48><e49><e50><e51><e52><e53><e54><e55><e56><e57><e58><e59><e60><e61><e62><e63><e64><e65><e66><e67><e68><e69/></e68></e67></e66></e65></e64></e63></e62></e61></e60></e59></e58></e57></e56></e55></e54></e53></e52></e51></e50></e49></e48></e47></e46></e45></e44></e43></e42></e41></e40></e39></e38></e37></e36></e35></e34></e33></e32></e31></e30></e29></e28></e27></e26></e25></e24></e23></e22></e21></e20></e19></e18></e17></e16></e15></e14></e13></e12></e11></e10></e9></e8></e7></e6></e5></e4></e3></e2></e1></e0></root>";
        int elementsCount = 70;
        XmlReader reader = readerFrom(xml);
        reader.beginElement();
        Assert.assertEquals("root", reader.nextElementName());

        for (int i = 0; i < elementsCount; i++) {
            reader.beginElement();
            Assert.assertEquals("e" + i, reader.nextElementName());
        }

        for (int i = 0; i < elementsCount; i++) {
            reader.endElement();
        }

        reader.endElement();
    }

    @Test
    public void xmlTagsAndAttributesWithNewLines() throws IOException {

        String xml = "<root\nanAttribute=\"1\"\n attributeWithWhiteSpace=\"2\" \n/>";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertEquals("root", reader.nextElementName());
        Assert.assertEquals("anAttribute", reader.nextAttributeName());
        Assert.assertEquals(1, reader.nextAttributeValueAsInt());
        Assert.assertEquals("attributeWithWhiteSpace", reader.nextAttributeName());
        Assert.assertEquals("2", reader.nextAttributeValue());
        reader.endElement();
    }

    @Test
    public void xmlTagsAndAttributesWithNewLinesAndMultilineTextContent() throws IOException {

        String xml =
                "<root\nanAttribute=\"1\"\n attributeWithWhiteSpace=\"2\" \n   \t\tattributeWithTabs=\"20.2\">\n<child>Contains\nmulitlines\n</child>\n</root>";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertEquals("root", reader.nextElementName());
        Assert.assertEquals("anAttribute", reader.nextAttributeName());
        Assert.assertEquals(1, reader.nextAttributeValueAsInt());
        Assert.assertEquals("attributeWithWhiteSpace", reader.nextAttributeName());
        Assert.assertEquals("2", reader.nextAttributeValue());
        Assert.assertEquals("attributeWithTabs", reader.nextAttributeName());
        Assert.assertEquals(20.2, reader.nextAttributeValueAsDouble(), 0);
        Assert.assertTrue(reader.hasElement());
        reader.beginElement();
        Assert.assertEquals("child", reader.nextElementName());
        Assert.assertEquals("Contains\nmulitlines\n", reader.nextTextContent());
        reader.endElement();
        reader.endElement();
        Assert.assertFalse(reader.hasElement());
    }


    @Test
    public void xmlWithDoctypes() throws IOException {

        String xml =
                "<!DOCTYPE rootelement SYSTEM \"file.dtd\"><root\nanAttribute=\"1\"\n attributeWithWhiteSpace=\"2\" \n   \t\tattributeWithTabs=\"20.2\">\n<child>Contains\nmulitlines\n</child>\n</root>";
        XmlReader reader = readerFrom(xml);

        reader.beginElement();
        Assert.assertEquals("root", reader.nextElementName());
        Assert.assertEquals("anAttribute", reader.nextAttributeName());
        Assert.assertEquals(1, reader.nextAttributeValueAsInt());
        Assert.assertEquals("attributeWithWhiteSpace", reader.nextAttributeName());
        Assert.assertEquals("2", reader.nextAttributeValue());
        Assert.assertEquals("attributeWithTabs", reader.nextAttributeName());
        Assert.assertEquals(20.2, reader.nextAttributeValueAsDouble(), 0);
        Assert.assertTrue(reader.hasElement());
        reader.beginElement();
        Assert.assertEquals("child", reader.nextElementName());
        Assert.assertEquals("Contains\nmulitlines\n", reader.nextTextContent());
        reader.endElement();
        reader.endElement();
        Assert.assertFalse(reader.hasElement());
    }

    @Test
    public void notADocTypeDefinitionButSameDoctypeAsTag() throws IOException {

        String xml = "<!DOCTYPE foo><!DOCTYPE bar><DOCTYPE />";
        XmlReader reader = readerFrom(xml);
        reader.beginElement();
        Assert.assertEquals("DOCTYPE", reader.nextElementName());
        Assert.assertFalse(reader.hasElement());
    }


    @Test
    public void elementNamedDOCTYPEisRegularElement() throws IOException {
        String xml = "<root><DOCTYPE /></root>";
        XmlReader reader = readerFrom(xml);
        reader.beginElement();
        Assert.assertEquals("root", reader.nextElementName());
        reader.beginElement();
        Assert.assertEquals("DOCTYPE", reader.nextElementName());
        reader.endElement();
        reader.endElement();
        Assert.assertFalse(reader.hasElement());
    }


    @Test
    public void doctypeAsChildIsNotAllowed() throws IOException {
        String xml = "<root><!DOCTYPE foo></root>";
        try {
            XmlReader reader = readerFrom(xml);
            reader.beginElement();
            Assert.assertEquals("root", reader.nextElementName());
            reader.beginElement();
            Assert.fail("Excpetion expected");
        } catch (IOException e) {
            // TODO Should doctype gets it's own error message?
            Assert.assertEquals("Unterminated comment at path /root/text()", e.getMessage());
        }
    }

    @Test
    public void readXmlWithUtf8Bom() throws IOException {
        String xml = "<element a=\"qwe\"></element>";
        Buffer xmlWithUtf8Bom = new Buffer();
        xmlWithUtf8Bom.writeByte(0xEF);
        xmlWithUtf8Bom.writeByte(0xBB);
        xmlWithUtf8Bom.writeByte(0xBF);
        xmlWithUtf8Bom.writeUtf8(xml);
        XmlReader reader = XmlReader.of(xmlWithUtf8Bom);

        try {
            reader.beginElement();
            Assert.assertEquals("element", reader.nextElementName());
            Assert.assertEquals("a", reader.nextAttributeName());
            Assert.assertEquals("qwe", reader.nextAttributeValue());
            Assert.assertFalse(reader.hasTextContent());
            reader.endElement();
        } finally {
            reader.close();
        }
    }
}
