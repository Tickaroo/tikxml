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

/**
 * Lexical scoping elements within a XML reader or writer.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
final class XmlScope {

  /** No object or array has been started. */
  static final int EMPTY_DOCUMENT = 0;

  /** A document at least one object */
  static final int NONEMPTY_DOCUMENT = 1;

  // /** XML declaration like {@code <?xml version="1.0" encoding="UTF-8"?>} */
  // static final int XML_DECLARATION = 2;

  /** We are in the opening xml tag like {@code <element>} */
  static final int ELEMENT_OPENING = 3;

  /** We are in the scope of reading attributes of a given element */
  static final int ELEMENT_ATTRIBUTE = 4;

  /**
   * We are in an elment's content (between opening and closing xml element tag) like {@code
   * <element>HERE WE ARE</element>}
   */
  static final int ELEMENT_CONTENT = 5;

  /**
   * A document that's been closed and cannot be accessed.
   */
  static final int CLOSED = 6;

  /**
   * Prints the XmlScope (mainly for debugging) for the element that is on top of the stack
   *
   * @param stackSize The size of the stack
   * @param stack The stack itself
   * @return String representing the XmlScope on top of the stack
   */
  static String getTopStackElementAsToken(int stackSize, int stack[]) throws IOException {
    switch (stack[stackSize - 1]) {
      case ELEMENT_OPENING:
        return "ELEMENT_OPENING";
      case EMPTY_DOCUMENT:
        return "EMPTY_DOCUMENT";
      case NONEMPTY_DOCUMENT:
        return "NONEMPTY_DOCUMENT";
      case ELEMENT_ATTRIBUTE:
        return "ELEMENT_ATTRIBUTE";
      case ELEMENT_CONTENT:
        return "ELEMENT_CONTENT";
      case CLOSED:
        return "CLOSED";
      default:
        throw new IOException("Unexpected token on top of the stack. Was " + stack[stackSize - 1]);
    }
  }

  /**
   * Renders the path in a JSON document to a string. The {@code pathNames} and {@code pathIndices}
   * parameters corresponds directly to stack: At indices where the stack contains an object
   * (EMPTY_OBJECT, DANGLING_NAME or NONEMPTY_OBJECT), pathNames contains the name at this scope.
   * Where it contains an array (EMPTY_ARRAY, NONEMPTY_ARRAY) pathIndices contains the current index
   * in that array. Otherwise the value is undefined, and we take advantage of that by incrementing
   * pathIndices when doing so isn't useful.
   */
  static String getPath(int stackSize, int[] stack, String[] pathNames, int[] pathIndices) {
    StringBuilder result = new StringBuilder();
    for (int i = 0, size = stackSize; i < size; i++) {
      switch (stack[i]) {
        case ELEMENT_OPENING:
          result.append('/');
          if (pathNames[i] != null) {
            result.append(pathNames[i]);
          }
          break;

        case ELEMENT_CONTENT:
          result.append('/');
          if (pathNames[i] != null) {
            result.append(pathNames[i]);
            if (i == stackSize - 1) {
              result.append("/text()");
            }
          }
          break;

        case ELEMENT_ATTRIBUTE:
          if (pathNames[i] != null) {
            result.append("[@");
            result.append(pathNames[i]);
            result.append(']');
          }
          break;
        case NONEMPTY_DOCUMENT:
        case EMPTY_DOCUMENT:
        case CLOSED:
          break;
      }
    }
    return result.length() == 0 ? "/" : result.toString();
  }
}
