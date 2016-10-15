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

import static com.tickaroo.tikxml.XmlScope.NONEMPTY_DOCUMENT;

/**
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class XmlWriter implements Closeable{

  /** The output data, containing at most one top-level array or object. */
  private final BufferedSink sink;

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

  private void push(int newTop) {
    if (stackSize == stack.length) {
      int[] newStack = new int[stackSize * 2];
      System.arraycopy(stack, 0, newStack, 0, stackSize);
      stack = newStack;
    }
    stack[stackSize++] = newTop;
  }


  /**
   * Returns the value on the top of the stack.
   */
  private int peek() {
    if (stackSize == 0) {
      throw new IllegalStateException("XML Writer is closed.");
    }
    return stack[stackSize - 1];
  }

  /**
   * Replace the value on the top of the stack with the given value.
   */
  private void replaceTop(int topOfStack) {
    stack[stackSize - 1] = topOfStack;
  }


  @Override public void close() throws IOException {
    sink.close();

    int size = stackSize;
    if (size > 1 || size == 1 && stack[size - 1] != NONEMPTY_DOCUMENT) {
      throw new IOException("Incomplete document");
    }
    stackSize = 0;
  }

  public void beginElement(String elementTagName) throws IOException{

  }

  public void endElement() throws IOException{

  }

  public void textContent() throws IOException {

  }

  public void attribute(String attributeName, String value) throws IOException {
    // TODO others
  }

}
