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

package com.tickaroo.tikxml.annotationprocessing.element.constructor;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import okio.Buffer;
import org.junit.*;

/**
 * @author Hannes Dorfmann
 */
public class ServerConstructorTest {

  @Test
  public void test() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(true).writeDefaultXmlDeclaration(false).build();
    ServerConstructor server = xml.read(TestUtils.sourceForFile("server.xml"), ServerConstructor.class);

    Assert.assertEquals("fooServer", server.getName());
    Assert.assertTrue(server.getConfig().isEnabled());
    Assert.assertEquals("127.0.0.1", server.getConfig().getIp());

    // Writing xml test

    Buffer buffer = new Buffer();
    xml.write(buffer, server);

    String xmlStr =
        "<server name=\"fooServer\"><serverConfig enabled=\"true\"><ip>127.0.0.1</ip></serverConfig></server>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    ServerConstructor server2 = xml.read(TestUtils.sourceFrom(xmlStr), ServerConstructor.class);
    Assert.assertEquals(server, server2);

  }
}
