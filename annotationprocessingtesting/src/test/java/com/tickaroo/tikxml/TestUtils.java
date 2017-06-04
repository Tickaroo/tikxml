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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class TestUtils {

  private TestUtils() {

  }


  public static XmlReader readerFrom(String xml) {
    return XmlReader.of(new Buffer().writeUtf8(xml));
  }

  public static XmlReader readerFromFile(String filePath) throws IOException {
    return XmlReader.of(Okio.buffer(Okio.source(new File(getResourcePath(filePath)))));
  }

  public static BufferedSource sourceForFile(String filePath) throws IOException {
    return Okio.buffer(Okio.source(new File(getResourcePath(filePath))));
  }

  public static BufferedSource sourceFrom(String xml) {
    return new Buffer().writeUtf8(xml);
  }


  /**
   * Get the resource path
   */
  private static String getResourcePath(String resPath) {
    URL resource = TestUtils.class.getClassLoader().getResource(resPath);
    Assert.assertNotNull("Could not open the resource " + resPath, resource);
    return resource.getFile();
  }


  /**
   * Converts the buffers content to a String
   * @param buffer
   * @return
   */
  public static String bufferToString(Buffer buffer) {
    return buffer.readUtf8();
  }
}
