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

package com.tickaroo.tikxml.reading;

import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import okio.Buffer;
import org.junit.Test;
import org.junit.Assert;

public class JsonArrayReaderTest {

    @Test
    public void jsonArray() throws IOException {
        String xml = "<jsonelement><json><![CDATA[[{a:'b'}, {b:'c'}]]]></json></jsonelement>";
        JsonElement json = readerFrom(xml);
        Assert.assertNotNull(json.json);
        Assert.assertTrue(json.json.length() > 0);
    }

    public static JsonElement readerFrom(String xml) throws IOException {
        TikXml tikXml = new TikXml.Builder()
                .addTypeAdapter(JsonElement.class, new JsonTypeAdapter())
                .build();
        Buffer buffer = new Buffer();
        buffer.writeUtf8(xml);
        return tikXml.read(buffer, JsonElement.class);
    }
}