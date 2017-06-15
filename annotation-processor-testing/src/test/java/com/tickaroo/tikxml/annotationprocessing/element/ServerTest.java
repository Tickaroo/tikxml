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

package com.tickaroo.tikxml.annotationprocessing.element;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class ServerTest {

  @Test
  public void test() throws IOException {

    // Test reading
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).writeDefaultXmlDeclaration(false).build();
    Server server = xml.read(TestUtils.sourceForFile("server.xml"), Server.class);

    Assert.assertEquals("fooServer", server.name);
    Assert.assertTrue(server.config.enabled);
    Assert.assertEquals("127.0.0.1", server.config.ip);


    // Writing xml test

    Buffer buffer = new Buffer();
    xml.write(buffer, server);

    String xmlStr =
        "<server name=\"fooServer\"><serverConfig enabled=\"true\"><ip>127.0.0.1</ip></serverConfig></server>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Server server2 = xml.read(TestUtils.sourceFrom(xmlStr), Server.class);
    Assert.assertEquals(server, server2);

  }
}
