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

package com.tickaroo.tikxml.regressiontests;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import java.text.ParseException;
import okio.Buffer;
import org.junit.*;

/**
 * Skip some internal elements
 *
 * @author Hannes Dorfmann
 */
public class TeamTest {

  @Test
  public void simple() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    Team team = xml.read(TestUtils.sourceForFile("regression/team.xml"), Team.class);

    Assert.assertEquals(team.id, 14);
    Assert.assertEquals(team.countryId, "D");
    Assert.assertEquals(team.shortName, "Bayern");
    Assert.assertEquals(team.longName, "Bayern München");
    Assert.assertEquals(team.iconSmall, "smallIcon");
    Assert.assertEquals(team.iconBig, "bigIcon");
    Assert.assertEquals(team.defaultLeagueId, 1);
    Assert.assertEquals(team.defaultLeagueId, 1);
    Assert.assertEquals(team.lat, 48.101861, 0);
    Assert.assertEquals(team.lng, 11.572654, 0);

    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, team);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><team lng=\"11.572654\" iconBig=\"bigIcon\" id=\"14\" shortName=\"Bayern\" iconSmall=\"smallIcon\" countryId=\"D\" lat=\"48.101861\" longName=\"Bayern München\" defaultLeagueId=\"1\"/>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Team week2 = xml.read(TestUtils.sourceFrom(xmlStr), Team.class);

    Assert.assertEquals(team, week2);
  }
}
