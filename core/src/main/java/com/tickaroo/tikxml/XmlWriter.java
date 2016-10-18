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

import java.io.Closeable;
import java.io.IOException;
import okio.BufferedSink;
import okio.ByteString;

import static com.tickaroo.tikxml.XmlScope.ELEMENT_CONTENT;
import static com.tickaroo.tikxml.XmlScope.ELEMENT_OPENING;
import static com.tickaroo.tikxml.XmlScope.NONEMPTY_DOCUMENT;
import static com.tickaroo.tikxml.XmlScope.getTopStackElementAsToken;

/**
 * With this class you can write xml with a convinient API.
 * Use {@link #of(BufferedSink)} to create a new instance.
 *
 * <p>
 * Example usage:
 * <pre>
 *     {@code
 *      Buffer buffer = new Buffer();
 *      XmlWriter writer = XmlWriter.of(buffer);
 *      writer.xmlDeclaration()
 *            .beginElement("company")
 *            .beginElement("employee")
 *            .attribute("id", 1)
 *            .beginElement("firstname")
 *            .textContent("Hannes")
 *            .endElement()
 *            .beginElement("lastname")
 *            .textContent("Dorfmann")
 *            .endElement()
 *            .endElement()
 *            .endElement();
 *     }
 *   </pre>
 *
 * produces the xml:
 * <pre>
 *     {@code
 *      <?xml version="1.0" encoding="UTF-8"?>
 *      <company>
 *        <employee id="1">
 *          <firstname>Hannes</firstname>
 *          <lastname>Dorfmann</lastname>
 *        </employee>
 *      </company>
 *    }
 *   </pre>
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class XmlWriter implements Closeable {

  private static final Byte DOUBLE_QUOTE = (byte) '"';
  private static final Byte OPENING_XML_ELEMENT = (byte) '<';
  private static final Byte CLOSING_XML_ELEMENT = (byte) '>';
  private static final ByteString CLOSING_XML_ELEMENT_START = ByteString.encodeUtf8("</");
  private static final ByteString INLINE_CLOSING_XML_ELEMENT = ByteString.encodeUtf8("/>");
  private static final ByteString ATTRIBUTE_ASSIGNMENT_BEGIN = ByteString.encodeUtf8("=\"");
  private static final ByteString OPENING_CDATA = ByteString.encodeUtf8("<![CDATA[");
  private static final ByteString CLOSING_CDATA = ByteString.encodeUtf8("]]>");
  private static final ByteString XML_DECLARATION =
      ByteString.encodeUtf8("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

  /** The output data, containing at most one top-level array or object. */
  private final BufferedSink sink;
  private boolean xmlDeclarationWritten = false;

  private int[] stack = new int[32];
  private int stackSize = 0;

  private String[] pathNames = new String[32];
  private int[] pathIndices = new int[32];

  {
    stack[stackSize++] = XmlScope.EMPTY_DOCUMENT;
  }

  private XmlWriter(BufferedSink sink) {
    if (sink == null) {
      throw new NullPointerException("sink == null");
    }
    this.sink = sink;
  }

  /**
   * Returns a new instance.
   */
  public static XmlWriter of(BufferedSink source) {
    return new XmlWriter(source);
  }

  private void pushStack(int newTop) {
    if (stackSize == stack.length) {
      int[] newStack = new int[stackSize * 2];
      int[] newPathIndices = new int[stackSize * 2];
      String[] newPathNames = new String[stackSize * 2];
      System.arraycopy(stack, 0, newStack, 0, stackSize);
      System.arraycopy(pathIndices, 0, newPathIndices, 0, stackSize);
      System.arraycopy(pathNames, 0, newPathNames, 0, stackSize);
      stack = newStack;
      pathIndices = newPathIndices;
      pathNames = newPathNames;
    }
    stack[stackSize++] = newTop;
  }

  /**
   * Removes the top element of the stack
   */
  private void popStack() {
    stack[stackSize - 1] = 0;
    stackSize--;
    pathNames[stackSize] = null; // Free the last path name so that it can be garbage collected!
    pathIndices[stackSize - 1]++;
  }

  /**
   * Returns the value on the top of the stack.
   */
  private int peekStack() {
    if (stackSize == 0) {
      throw new IllegalStateException("XML Writer is closed.");
    }
    return stack[stackSize - 1];
  }

  /**
   * Replace the value on the top of the stack with the given value.
   */
  private void replaceTopOfStack(int topOfStack) {
    stack[stackSize - 1] = topOfStack;
  }

  @Override public void close() throws IOException {
    sink.close();

    int size = stackSize;
    if (size > 1 || size == 1 && stack[size - 1] != NONEMPTY_DOCUMENT) {
      throw new IOException(
          "Incomplete document. Abrupt end at " + XmlScope.getPath(stackSize, stack, pathNames,
              pathIndices) + " in scope " + XmlScope.getTopStackElementAsToken(stackSize, stack));
    }
    stackSize = 0;
  }

  /**
   * Throws a new IO exception with the given message and a context snippet with this reader's
   * content.
   */
  private IOException syntaxError(String message) throws IOException {
    throw new IOException(
        message + " at path " + XmlScope.getPath(stackSize, stack, pathNames, pathIndices));
  }

  /**
   * Begin a new xml element. Must be closed with {@link #endElement()}
   *
   * @param elementTagName The name of the xml element tag
   * @throws IOException
   */
  public XmlWriter beginElement(String elementTagName) throws IOException {

    int topOfStack = peekStack();

    switch (topOfStack) {
      case XmlScope.EMPTY_DOCUMENT: // begining with root xml element
        replaceTopOfStack(XmlScope.NONEMPTY_DOCUMENT);
        pushStack(XmlScope.ELEMENT_OPENING);
        pathNames[stackSize - 1] = elementTagName;
        sink.writeByte(OPENING_XML_ELEMENT)
            .writeUtf8(elementTagName);
        break;

      case XmlScope.ELEMENT_CONTENT: // write a nested xml element <parent> Some optional text <nested>
        pushStack(XmlScope.ELEMENT_OPENING);
        pathNames[stackSize - 1] = elementTagName;
        sink.writeByte(OPENING_XML_ELEMENT)
            .writeUtf8(elementTagName);
        break;

      case XmlScope.ELEMENT_OPENING: // write a nested xml element by closing the parent's xml opening header
        replaceTopOfStack(XmlScope.ELEMENT_CONTENT);
        pushStack(XmlScope.ELEMENT_OPENING);
        pathNames[stackSize - 1] = elementTagName;
        sink.writeByte(CLOSING_XML_ELEMENT)
            .writeByte(OPENING_XML_ELEMENT)
            .writeUtf8(elementTagName);
        break;

      case XmlScope.NONEMPTY_DOCUMENT:
        throw new IOException(
            "A xml document can only have one root xml element. There is already one but you try to add another one <"
                + elementTagName
                + ">");

      default:
        throw syntaxError("Unexpected begin of a new xml element <"
            + elementTagName
            + ">. New xml elements can only begin on a empty document or in a text content but tried to insert a element on scope "
            + XmlScope.getTopStackElementAsToken(stackSize, stack));
    }

    return this;
  }

  /**
   * Closes a xml element previously opened with {@link #beginElement(String)}
   *
   * @throws IOException
   */
  public XmlWriter endElement() throws IOException {
    int topOfStack = peekStack();
    switch (topOfStack) {
      case XmlScope.ELEMENT_OPENING:
        sink.write(INLINE_CLOSING_XML_ELEMENT);
        popStack();
        break;
      case XmlScope.ELEMENT_CONTENT:
        sink.write(CLOSING_XML_ELEMENT_START)
            .writeUtf8(pathNames[stackSize - 1])
            .writeByte(CLOSING_XML_ELEMENT);
        popStack();
        break;

      default:
        String elementName = pathNames[stackSize - 1];
        if (elementName != null) {
          throw syntaxError("Trying to close the xml element </"
              + elementName
              + "> but I'm in xml scope "
              + XmlScope.getTopStackElementAsToken(stackSize, stack));
        } else {
          throw syntaxError(
              "Trying to close the xml element, but all xml elements are already closed properly. Xml scope is "
                  + XmlScope.getTopStackElementAsToken(stackSize, stack));
        }
    }
    return this;
  }

  /**
   * Writes the text content into an element: {@code <element>text content</element>}
   *
   * @param textContentValue The text content
   * @throws IOException
   */
  public XmlWriter textContent(String textContentValue) throws IOException {

    int topOfStack = peekStack();
    switch (topOfStack) {
      case ELEMENT_OPENING:
        sink.writeByte(CLOSING_XML_ELEMENT);
        replaceTopOfStack(XmlScope.ELEMENT_CONTENT);
        sink.writeUtf8(textContentValue);
        break;

      case ELEMENT_CONTENT:
        sink.writeUtf8(textContentValue);
        break;

      default:
        String elementName = pathNames[stackSize - 1];
        if (elementName != null) {
          throw syntaxError("Error while trying to write text content into xml element <"
              + elementName
              + ">"
              + textContentValue
              + "</"
              + elementName
              + ">. Xml scope was " + XmlScope.getTopStackElementAsToken(stackSize, stack));
        } else {
          throw syntaxError("Error while trying to write text content \""
              + textContentValue
              + "\". Xml scope was " + XmlScope.getTopStackElementAsToken(stackSize, stack));
        }
    }
    return this;
  }

  /**
   * Writes the text content into an element: {@code <element>123</element>}
   *
   * @param textContentValue The text content
   * @throws IOException
   */
  public XmlWriter textContent(int textContentValue) throws IOException {
    return textContent(Integer.toString(textContentValue));
  }

  /**
   * Writes the text content into an element: {@code <element>123</element>}
   *
   * @param textContentValue The text content
   * @throws IOException
   */
  public XmlWriter textContent(long textContentValue) throws IOException {
    return textContent(Long.toString(textContentValue));
  }

  /**
   * Writes the text content into an element: {@code <element>123.45</element>}
   *
   * @param textContentValue The text content
   * @throws IOException
   */
  public XmlWriter textContent(double textContentValue) throws IOException {
    return textContent(Double.toString(textContentValue));
  }

  /**
   * Writes the text content into an element: {@code <element>true</element>}
   *
   * @param textContentValue The text content
   * @throws IOException
   */
  public XmlWriter textContent(boolean textContentValue) throws IOException {
    return textContent(Boolean.toString(textContentValue));
  }

  public XmlWriter textContentAsCData(String textContentValue) throws IOException {
    int topOfStack = peekStack();
    switch (topOfStack) {
      case ELEMENT_OPENING:
        replaceTopOfStack(XmlScope.ELEMENT_CONTENT);
        sink.writeByte(CLOSING_XML_ELEMENT)
            .write(OPENING_CDATA)
            .writeUtf8(textContentValue)
            .write(CLOSING_CDATA);
        break;

      case ELEMENT_CONTENT:
        sink.write(OPENING_CDATA)
            .writeUtf8(textContentValue)
            .write(CLOSING_CDATA);
        break;

      default:
        String elementName = pathNames[stackSize - 1];
        if (elementName != null) {
          throw syntaxError("Error while trying to write text content into xml element <"
              + elementName
              + ">"
              + textContentValue
              + "</"
              + elementName
              + ">. Xml scope was " + XmlScope.getTopStackElementAsToken(stackSize, stack));
        } else {
          throw syntaxError("Error while trying to write text content \""
              + textContentValue
              + "\". Xml scope was " + XmlScope.getTopStackElementAsToken(stackSize, stack));
        }
    }
    return this;
  }

  /**
   * Writes a xml attribute and the corresponding value. Must be called after {@link
   * #beginElement(String)} and before {@link #endElement()} or {@link #textContent(String)}
   *
   * @param attributeName The name of the attribute
   * @param value the value
   * @throws IOException
   */
  public XmlWriter attribute(String attributeName, String value) throws IOException {
    if (XmlScope.ELEMENT_OPENING == peekStack()) {
      sink.writeByte(' ') // Write a whitespace
          .writeUtf8(attributeName)
          .write(ATTRIBUTE_ASSIGNMENT_BEGIN)
          .writeUtf8(value)
          .writeByte(DOUBLE_QUOTE);
    } else {
      throw syntaxError("Error while trying to write attribute "
          + attributeName
          + "=\""
          + value
          + "\". Attributes can only be written in a opening xml element but was in xml scope "
          + getTopStackElementAsToken(stackSize, stack));
    }

    return this;
  }

  /**
   * Writes a xml attribute and the corresponding value. Must be called after {@link
   * #beginElement(String)} and before {@link #endElement()} or {@link #textContent(String)}
   *
   * @param attributeName The name of the attribute
   * @param value the value
   * @throws IOException
   */
  public XmlWriter attribute(String attributeName, int value) throws IOException {
    return attribute(attributeName, Integer.toString(value));
  }

  /**
   * Writes a xml attribute and the corresponding value. Must be called after {@link
   * #beginElement(String)} and before {@link #endElement()} or {@link #textContent(String)}
   *
   * @param attributeName The name of the attribute
   * @param value the value
   * @throws IOException
   */
  public XmlWriter attribute(String attributeName, long value) throws IOException {
    return attribute(attributeName, Long.toString(value));
  }

  /**
   * Writes a xml attribute and the corresponding value. Must be called after {@link
   * #beginElement(String)} and before {@link #endElement()} or {@link #textContent(String)}
   *
   * @param attributeName The name of the attribute
   * @param value the value
   * @throws IOException
   */
  public XmlWriter attribute(String attributeName, boolean value) throws IOException {
    return attribute(attributeName, Boolean.toString(value));
  }

  /**
   * Writes a xml attribute and the corresponding value. Must be called after {@link
   * #beginElement(String)} and before {@link #endElement()} or {@link #textContent(String)}
   *
   * @param attributeName The name of the attribute
   * @param value the value
   * @throws IOException
   */
  public XmlWriter attribute(String attributeName, double value) throws IOException {
    return attribute(attributeName, Double.toString(value));
  }

  /**
   * Writes the xml declaration {@code <?xml version="1.0" encoding="UTF-8"?>}
   *
   * @throws IOException
   */
  public XmlWriter xmlDeclaration() throws IOException {

    if (!xmlDeclarationWritten) {
      if (peekStack() == XmlScope.EMPTY_DOCUMENT) {
        sink.write(XML_DECLARATION);
        xmlDeclarationWritten = true;
      } else {
        throw syntaxError("Xml Declatraion "
            + XML_DECLARATION.utf8()
            + " can only be written at the beginning of a xml document! You are not at the beginning of a xml document: current xml scope is "
            + XmlScope.getTopStackElementAsToken(stackSize, stack));
      }
    } else {
      throw new IOException("Xml declaration "
          + XML_DECLARATION.utf8()
          + " has already been written in this xml document. Xml declaration can only be written once at the beginning of the document.");
    }

    return this;
  }

  /**
   * Writes a namespace definition
   *
   * @param prefix The prefix like "android" in {@code xmlns:android="http://schemas.android.com/apk/res/android"}.
   * Null or empty string means, no prefix. In that case the default namespace definition like
   * {@code xmlns="http://example.com"} will be written.
   * @param uri the uri like "http://schemas.android.com/apk/res/android"
   * @throws IOException
   * @see #namespace(String)
   */
  public XmlWriter namespace(String prefix, String uri) throws IOException {
    if (prefix != null && prefix.length() > 0) {
      return attribute("xmlns:" + prefix, uri);
    } else {
      return attribute("xmlns", uri);
    }
  }

  /**
   * Writes the default namespace definition like {@code xmlns="http://example.com"} (without any
   * prefix)
   *
   * @param uri The uri to write
   * @throws IOException
   * @see #namespace(String, String)
   */
  public XmlWriter namespace(String uri) throws IOException {
    return namespace(null, uri);
  }
}
