/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tickaroo.tikxml;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

/**
 * A class to read and parse an xml stream.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class XmlReader implements Closeable {

  //private static final ByteString LINEFEED_OR_CARRIAGE_RETURN = ByteString.encodeUtf8("\n\r");

  private static final ByteString UNQUOTED_STRING_TERMINALS
      = ByteString.encodeUtf8(" >/=");

  private static final Byte DOUBLE_QUOTE = (byte) '"';
  private static final Byte SINGLE_QUOTE = (byte) '\'';
  private static final Byte OPENING_XML_ELEMENT = (byte) '<';

  //
  // Peek states
  //
  /** Nothing peeked */
  private static final int PEEKED_NONE = 0;
  /** Peeked an xml element / object */
  private static final int PEEKED_ELEMENT_BEGIN = 1;
  /** Peeked the closing xml tag which indicates the end of an object */
  private static final int PEEKED_ELEMENT_END = 2;
  /** Peeked the closing xml header tag, hence we are inner xml tag object body */
  private static final int PEEKED_ELEMENT_TEXT_CONTENT = 3;
  /** Peeked the end of the stream */
  private static final int PEEKED_EOF = 4;
  /** Peeked an unquoted value which can be either xml element name or element attribute name */
  private static final int PEEKED_ELEMENT_NAME = 5;
  /** Peeked a quoted value which is the value of an xml attribute */
  private static final int PEEKED_DOUBLE_QUOTED = 6;
  /** Peeked a single quote which is the value of an xml attribute */
  private static final int PEEKED_SINGLE_QUOTED = 7;
  /** Peeked an attribute name (of a xml element) */
  private static final int PEEKED_ATTRIBUTE_NAME = 8;

  /** The input XML. */
  private int peeked = PEEKED_NONE;


  private String[] pathNames = new String[32];
  private int[] pathIndices = new int[32];


  /*
   * The nesting stack. Using a manual array rather than an ArrayList saves 20%.
   */
  private int[] stack = new int[32];
  private int stackSize = 0;

  {
    stack[stackSize++] = XmlScope.EMPTY_DOCUMENT;
  }

  private final BufferedSource source;
  private final Buffer buffer;


  private XmlReader(BufferedSource source) {
    if (source == null) {
      throw new NullPointerException("source == null");
    }
    this.source = source;
    this.buffer = source.buffer();
  }

  /**
   * Returns a new instance that reads a XML-encoded stream from {@code source}.
   */
  public static XmlReader of(BufferedSource source) {
    return new XmlReader(source);
  }

  /**
   * Get the next token without consuming it.
   *
   * @return {@link XmlToken}
   */
  public XmlToken peek() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    switch (p) {
      case PEEKED_ELEMENT_BEGIN:
        return XmlToken.ELEMENT_BEGIN;

      case PEEKED_ELEMENT_NAME:
        return XmlToken.ELEMENT_NAME;

      case PEEKED_ELEMENT_END:
        return XmlToken.ELEMENT_END;

      case PEEKED_ATTRIBUTE_NAME:
        return XmlToken.ATTRIBUTE_NAME;

      case PEEKED_DOUBLE_QUOTED:
      case PEEKED_SINGLE_QUOTED:
        return XmlToken.ATTRIBUTE_VALUE;

      case PEEKED_ELEMENT_TEXT_CONTENT:
        return XmlToken.ELEMENT_TEXT_CONTENT;

      case PEEKED_EOF:
        return XmlToken.END_OF_DOCUMENT;
      default:
        throw new AssertionError("Unknown XmlToken: Peeked = " + p);
    }
  }

  /**
   * Actually do a peek. This method will return the peeked token and updates the internal varible
   * {@link #peeked}
   *
   * @return The peeked token
   * @throws IOException
   */
  private int doPeek() throws IOException {

    int peekStack = stack[stackSize - 1];

    if (peekStack == XmlScope.ELEMENT_OPENING) {
      int c = nextNonWhitespace(true);
      if (isLiteral((char) c)) {
        return peeked = PEEKED_ELEMENT_NAME;
      } else {
        throw syntaxError("Expected xml element name (literal expected)");
      }

    } else if (peekStack == XmlScope.ELEMENT_ATTRIBUTE) {
      int c = nextNonWhitespace(true);

      if (isLiteral(c)) {
        return peeked = PEEKED_ATTRIBUTE_NAME;
      }

      switch (c) {
        case '>':
          // remove XmlScope.ELEMENT_ATTRIBUTE from top of the stack
          popStack();

          // set previous stack from XmlScope.ELEMENT_OPENING to XmlScope.ELEMENT_CONTENT
          stack[stackSize - 1] = XmlScope.ELEMENT_CONTENT;
          buffer.readByte(); // consume '>'

          int nextChar = nextNonWhitespace(true);

          if (nextChar != '<') {
            return peeked = PEEKED_ELEMENT_TEXT_CONTENT;
          }
          break;

        case '/':
          // Self closing />

          if (fillBuffer(2) && buffer.getByte(1) == '>') {
            // remove XmlScope.ELEMENT_ATTRIBUTE from top of the stack
            popStack();

            // correct closing xml tag
            buffer.readByte(); // consuming '/'
            buffer.readByte(); // consuming '>'
            return peeked = PEEKED_ELEMENT_END;
          } else {
            throw syntaxError("Expected closing />");
          }

        case '=':
          buffer.readByte(); // consume '='

          // Read next char which should be a quote
          c = nextNonWhitespace(true);

          switch (c) {
            case '"':
              buffer.readByte(); // consume "
              return peeked = PEEKED_DOUBLE_QUOTED;
            case '\'':
              buffer.readByte(); // consume '
              return peeked = PEEKED_SINGLE_QUOTED;

            default:
              throw syntaxError("Expected double quote (\") or single quote (') while reading xml elements attribute");
          }

        default:
          throw syntaxError("Unexpected character '" + ((char) c) + "' while trying to read xml elements attribute");
      }

    } else if (peekStack == XmlScope.ELEMENT_CONTENT) {


      // TODO CDATA


    } else if (peekStack == XmlScope.EMPTY_DOCUMENT) {
      stack[stackSize - 1] = XmlScope.NONEMPTY_DOCUMENT;
    } else if (peekStack == XmlScope.NONEMPTY_DOCUMENT) {
      int c = nextNonWhitespace(false);
      if (c == -1) {
        return peeked = PEEKED_EOF;
      }
    } else if (peekStack == XmlScope.CLOSED) {
      throw new IllegalStateException("XmlReader is closed");
    }


    int c = nextNonWhitespace(true);
    switch (c) {

      // Handling open < and closing </
      case '<':
        buffer.readByte(); // consume '<'.

        // Check if </ which means end of element
        if (fillBuffer(1) && buffer.getByte(0) == '/') {

          buffer.readByte(); // consume /

          // Check if it is the corresponding xml element name
          String closingElementName = nextUnquotedValue();
          if (closingElementName != null && closingElementName.equals(pathNames[stackSize - 1])) {

            if (nextNonWhitespace(false) == '>') {
              buffer.readByte(); // consume >
              return peeked = PEEKED_ELEMENT_END;
            } else {
              syntaxError("Missing closing '>' character in </" + pathNames[stackSize - 1]);
            }

          } else {
            syntaxError("Expected a closing element tag </" + pathNames[stackSize - 1] + "> but found </" + closingElementName + ">");
          }

        }
        // its just a < which means begin of the element
        return peeked = PEEKED_ELEMENT_BEGIN;

      case '"':
        buffer.readByte(); // consume '"'.
        return peeked = PEEKED_DOUBLE_QUOTED;

      case '\'':
        buffer.readByte(); // consume '
        return peeked = PEEKED_SINGLE_QUOTED;
    }


    return PEEKED_NONE;
  }

  /**
   * Consumes the next token from the JSON stream and asserts that it is the beginning of a new
   * object.
   */
  public void beginElement() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p == PEEKED_ELEMENT_BEGIN) {
      pushStack(XmlScope.ELEMENT_OPENING);
      peeked = PEEKED_NONE;
    } else {
      throw new XmlDataException("Expected " + XmlToken.ELEMENT_BEGIN + " but was " + peek()
          + " at path " + getPath());
    }
  }

  /**
   * Consumes the next token from the JSON stream and asserts that it is the end of the current
   * object.
   */
  public void endElement() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p == PEEKED_ELEMENT_END) {
      popStack();
      peeked = PEEKED_NONE;
    } else {
      throw syntaxError("Expected end of element but was " + peek());
    }
  }

  /**
   * Checks if there is one more unconsumed xml element that can be consumed afterwards with {@link
   * #beginElement()}
   *
   * @return true if there is at least one more unconsumed xml element, otherwise false
   */
  public boolean hasElement() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    return p == PEEKED_ELEMENT_BEGIN;
  }


  /**
   * Returns true if the current xml element has an unparsed attribute. {@link #beginElement()} must
   * be called before invoking this method
   *
   * @return true if at least one more attribute available, otherwise false
   */
  public boolean hasAttribute() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    return p == PEEKED_ATTRIBUTE_NAME;
  }

  /**
   * Consumes the next token attribute of a xml element. Assumes that {@link #beginElement()} has
   * been called before
   *
   * @return The name of the attribute
   */
  public String nextAttributeName() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p != PEEKED_ATTRIBUTE_NAME) {
      throw syntaxError("Expected xml element attribute name but was " + peek());
    }

    String result = nextUnquotedValue();
    peeked = PEEKED_NONE;
    pathNames[stackSize - 1] = result;
    return result;
  }

  /**
   * Consumes the next attribute's value. Assumes that {@link #nextAttributeName()} has been called
   * before invoking this method
   *
   * @return The value of the attribute as string
   */
  public String nextAttributeValue() throws IOException {

    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    if (p == PEEKED_DOUBLE_QUOTED || p == PEEKED_SINGLE_QUOTED) {
      String attributeValue = nextQuotedValue(p == PEEKED_DOUBLE_QUOTED ? DOUBLE_QUOTE : SINGLE_QUOTE);

      peeked = PEEKED_NONE;
      pathNames[stackSize - 1] = null; // Remove attribute name from stack, do that after nextQuotedValue() to ensure that xpath is correctly in case that nextQuotedValue() fails
      return attributeValue;
    } else {
      throw new XmlDataException("Expected xml element attribute value (in double quotes or single quotes) but was " + peek()
          + " at path " + getPath());
    }

  }


  /**
   * Skip the value of an attribute if you don't want to read the value. {@link
   * #nextAttributeName()} must be called before invoking this method
   *
   * @throws IOException
   */
  public void skipAttributeValue() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    if (p == PEEKED_DOUBLE_QUOTED || p == PEEKED_SINGLE_QUOTED) {
      peeked = PEEKED_NONE;
      pathNames[stackSize - 1] = null; // Remove attribute name from stack
      skipQuotedValue(p == PEEKED_DOUBLE_QUOTED ? DOUBLE_QUOTE : SINGLE_QUOTE);
    } else {
      throw new XmlDataException("Expected xml element attribute value (in double quotes or single quotes) but was " + peek()
          + " at path " + getPath());
    }
  }


  /**
   * Returns true if the current xml element  has another a body which contains either a value or
   * other child xml elements ( objects )
   */
  public boolean hasTextContent() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    return p == PEEKED_ELEMENT_TEXT_CONTENT;
  }

  /**
   * Get the next text content of an xml element. Text content is {@code <element>text
   * content</element>}
   *
   * @return The xml element's text content
   * @throws IOException
   */
  public String nextTextContent() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    if (p == PEEKED_ELEMENT_TEXT_CONTENT) {
      peeked = PEEKED_NONE;

      // Read text until '<' found
      long index = source.indexOf(OPENING_XML_ELEMENT);
      if (index == -1L)
        throw syntaxError("Unterminated element text content. Expected </" + pathNames[stackSize - 1] + "> but haven't found");

      return buffer.readUtf8(index);
    } else {
      throw new XmlDataException("Expected xml element text content but was " + peek()
          + " at path " + getPath());
    }
  }

  /**
   * Skip the text content. Text content is {@code <element>text content</element>}
   *
   * @throws IOException
   */
  public void skipTextContent() throws IOException {

    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }

    if (p == PEEKED_ELEMENT_TEXT_CONTENT) {
      peeked = PEEKED_NONE;

      // Read text until '<' found
      long index = source.indexOf(OPENING_XML_ELEMENT);
      if (index == -1L)
        throw syntaxError("Unterminated element text content. Expected </" + pathNames[stackSize - 1] + "> but haven't found");

      buffer.skip(index);
    } else {
      throw new XmlDataException("Expected xml element text content but was " + peek()
          + " at path " + getPath());
    }
  }

  /**
   * Push a new scope on top of the scope stack
   *
   * @param newTop The scope that should be pushed on top of the stack
   */
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
   * Returns a XPath to the current location in the XML value.
   */
  public String getPath() {
    return XmlScope.getPath(stackSize, stack, pathNames, pathIndices);
  }


  @Override
  public void close() throws IOException {
    peeked = PEEKED_NONE;
    buffer.clear();
    source.close();
  }

  /**
   * @param toFind a string to search for. Must not contain a newline.
   */
  private boolean skipTo(String toFind) throws IOException {
    outer:
    for (; fillBuffer(toFind.length()); ) {
      for (int c = 0; c < toFind.length(); c++) {
        if (buffer.getByte(c) != toFind.charAt(c)) {
          buffer.readByte();
          continue outer;
        }
      }
      return true;
    }
    return false;
  }


  /**
   * Returns true once {@code limit - pos >= minimum}. If the data is exhausted before that many
   * characters are available, this returns false.
   */
  private boolean fillBuffer(int minimum) throws IOException {
    return source.request(minimum);
  }

  /**
   * Returns the next character in the stream that is neither whitespace nor a part of a comment.
   * When this returns, the returned character is always at {@code buffer[pos-1]}; this means the
   * caller can always pushStack back the returned character by decrementing {@code pos}.
   */
  private int nextNonWhitespace(boolean throwOnEof) throws IOException {
    /*
     * This code uses ugly local variables 'p' and 'l' representing the 'pos'
     * and 'limit' fields respectively. Using locals rather than fields saves
     * a few field reads for each whitespace character in a pretty-printed
     * document, resulting in a 5% speedup. We need to flush 'p' to its field
     * before any (potentially indirect) call to fillBuffer() and reread both
     * 'p' and 'l' after any (potentially indirect) call to the same method.
     */
    int p = 0;
    while (fillBuffer(p + 1)) {
      int c = buffer.getByte(p++);
      if (c == '\n' || c == ' ' || c == '\r' || c == '\t') {
        continue;
      }

      buffer.skip(p - 1);
      if (c == '<' && fillBuffer(4)) {

        byte peek = buffer.getByte(1);
        if (peek == '!') {
          // skip xml comments <!-- comment -->
          // consume opening comment chars
          buffer.readByte(); // '<'
          buffer.readByte(); // '!'
          buffer.readByte(); // '-'
          buffer.readByte(); // '-'
          if (!skipTo("-->")) {
            throw syntaxError("Unterminated comment");
          }

          // Consume closing comment chars
          buffer.readByte(); // '-'
          buffer.readByte(); // '-'
          buffer.readByte(); // '>'
          p = 0;
          continue;
        }
      }

      return c;
    }

    if (throwOnEof) {
      throw new EOFException("Unexpected end of input at path " + getPath());
    } else {
      return -1;
    }
  }

  /**
   * Throws a new IO exception with the given message and a context snippet with this reader's
   * content.
   */
  private IOException syntaxError(String message) throws IOException {
    throw new IOException(message + " at path " + getPath());
  }

  /**
   * Get the name of the opening xml name
   *
   * @return The name
   * @throws IOException
   */
  public String nextElementName() throws IOException {
    int p = peeked;
    if (p == PEEKED_NONE) {
      p = doPeek();
    }
    if (p != PEEKED_ELEMENT_NAME) {
      throw syntaxError("Expected XML Tag Element name, but have " + peek());
    }

    String result = nextUnquotedValue();

    peeked = PEEKED_NONE;
    pathNames[stackSize - 1] = result;


    // Next we expect element attributes block
    pushStack(XmlScope.ELEMENT_ATTRIBUTE);
    return result;
  }


  /** Returns an unquoted value as a string. */
  private String nextUnquotedValue() throws IOException {
    long i = source.indexOfElement(UNQUOTED_STRING_TERMINALS);
    return i != -1 ? buffer.readUtf8(i) : buffer.readUtf8();
  }

  /**
   * Returns the string up to but not including {@code quote}, unescaping any character escape
   * sequences encountered along the way. The opening quote should have already been read. This
   * consumes the closing quote, but does not include it in the returned string.
   *
   * @throws IOException if any unicode escape sequences are malformed.
   */
  private String nextQuotedValue(byte runTerminator) throws IOException {
    StringBuilder builder = null;
    while (true) {
      long index = source.indexOf(runTerminator);
      if (index == -1L)
        throw syntaxError("Unterminated string (" + (runTerminator == DOUBLE_QUOTE ? "double quote \"" : "single quote '") + " is missing)");

      // If we've got an escape character, we're going to need a string builder.
      if (buffer.getByte(index) == '\\') {
        if (builder == null) builder = new StringBuilder();
        builder.append(buffer.readUtf8(index));
        buffer.readByte(); // '\'
        builder.append(readEscapeCharacter());
        continue;
      }

      // If it isn't the escape character, it's the quote. Return the string.
      if (builder == null) {
        String result = buffer.readUtf8(index);
        buffer.readByte(); // Consume the quote character.
        return result;
      } else {
        builder.append(buffer.readUtf8(index));
        buffer.readByte(); // Consume the quote character.
        return builder.toString();
      }
    }
  }


  /**
   * Checks wheter the passed character is a literal or not
   *
   * @param c the character to check
   * @return true if literal, otherwise false
   */
  private boolean isLiteral(int c) {
    switch (c) {
      case '=':
      case '<':
      case '>':
      case '/':
      case ' ':
        return false;
      default:
        return true;
    }
  }

  /**
   * Unescapes the character identified by the character or characters that immediately follow a
   * backslash. The backslash '\' should have already been read. This supports both unicode escapes
   * "u000A" and two-character escapes "\n".
   *
   * @throws IOException if any unicode escape sequences are malformed.
   */
  private char readEscapeCharacter() throws IOException {
    if (!fillBuffer(1)) {
      throw syntaxError("Unterminated escape sequence");
    }

    byte escaped = buffer.readByte();
    switch (escaped) {
      case 'u':
        if (!fillBuffer(4)) {
          throw new EOFException("Unterminated escape sequence at path " + getPath());
        }
        // Equivalent to Integer.parseInt(stringPool.get(buffer, pos, 4), 16);
        char result = 0;
        for (int i = 0, end = i + 4; i < end; i++) {
          byte c = buffer.getByte(i);
          result <<= 4;
          if (c >= '0' && c <= '9') {
            result += (c - '0');
          } else if (c >= 'a' && c <= 'f') {
            result += (c - 'a' + 10);
          } else if (c >= 'A' && c <= 'F') {
            result += (c - 'A' + 10);
          } else {
            throw syntaxError("\\u" + buffer.readUtf8(4));
          }
        }
        buffer.skip(4);
        return result;

      case 't':
        return '\t';

      case 'b':
        return '\b';

      case 'n':
        return '\n';

      case 'r':
        return '\r';

      case 'f':
        return '\f';

      case '\n':
      case '\'':
      case '"':
      case '\\':
      default:
        return (char) escaped;
    }
  }

  /**
   * Skip a quoted value
   *
   * @param runTerminator The terminator to skip
   * @throws IOException
   */
  private void skipQuotedValue(Byte runTerminator) throws IOException {
    while (true) {
      long index = source.indexOf(runTerminator);
      if (index == -1L) throw syntaxError("Unterminated string");

      if (buffer.getByte(index) == '\\') {
        buffer.skip(index + 1);
        readEscapeCharacter();
      } else {
        buffer.skip(index + 1);
        return;
      }
    }
  }

/*
  public void skipElement() throws IOException {

    int count = 0;
    do {
      int p = peeked;
      if (p == PEEKED_NONE) {
        p = doPeek();
      }

      if (p == PEEKED_ELEMENT_BEGIN) {
        beginElement();
        count++;
      } else if (p == PEEKED_ELEMENT_END) {
        stackSize--;
        count--;
      } else if (p == PEEKED_UNQUOTED_NAME || p == PEEKED_UNQUOTED) {
        skipUnquotedValue();
      } else if (p == PEEKED_DOUBLE_QUOTED || p == PEEKED_DOUBLE_QUOTED_NAME) {
        skipQuotedValue(DOUBLE_QUOTE_OR_SLASH);
      } else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_SINGLE_QUOTED_NAME) {
        skipQuotedValue(SINGLE_QUOTE_OR_SLASH);
      } else if (p == PEEKED_NUMBER) {
        buffer.skip(peekedNumberLength);
      }
      peeked = PEEKED_NONE;
    } while (count != 0);

    pathIndices[stackSize - 1]++;
    pathNames[stackSize - 1] = "null";
  }
  */


  /**
   * Skip an unquoted value
   *
   * @throws IOException
   *
   * private void skipUnquotedValue() throws IOException { long i = source.indexOfElement(UNQUOTED_STRING_TERMINALS);
   * buffer.skip(i != -1L ? i : buffer.size()); }
   */

  public enum XmlToken {
    /**
     * Indicates that an xml element begins.
     */
    ELEMENT_BEGIN,

    /**
     * xml element name
     */
    ELEMENT_NAME,

    /**
     * Indicates that an xml element ends
     */
    ELEMENT_END,

    /**
     * Indicates that we are reading an attribute name (of an xml element)
     */
    ATTRIBUTE_NAME,

    /**
     * Indicates that we are reading a xml elements attribute value
     */
    ATTRIBUTE_VALUE,

    /**
     * Indicates that we are reading the text content of an xml element like this {@code <element>
     * This is the text content </element>}
     */
    ELEMENT_TEXT_CONTENT,

    /**
     * Indicates that we have reached the end of the document
     */
    END_OF_DOCUMENT

  }

}
