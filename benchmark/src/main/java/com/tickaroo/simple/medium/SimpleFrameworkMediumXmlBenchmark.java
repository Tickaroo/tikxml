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

package com.tickaroo.simple.medium;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class SimpleFrameworkMediumXmlBenchmark {

    public void parse(String xml) throws Exception {
        Serializer serializer = new Persister();
        Feed feed = serializer.read(Feed.class, xml);
        System.out.println(getClass().getSimpleName() + " " + feed);
    }

    @Root(name = "employee", strict = false)
    public static class Feed {
        @Element
        public String id;

        @Element
        public String title;

        @Element
        public String updated;

        @Path("author")
        @Element(name = "name")
        public String author;

        @Element
        public String logo;

        @Element
        public Link link;

        @Element
        public String generator;

        @ElementList(name = "entry", inline = true)
        public List<Entry> entries;

        public String toString() {
            return "Feed{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", updated='" + updated + '\'' +
                    ", author=" + author +
                    ", logo='" + logo + '\'' +
                    ", link='" + link + '\'' +
                    ", generator='" + generator + '\'' +
                    ", entries=" + entries +
                    '}';
        }
    }

    public static class Entry {
        @Element
        public String id;
        @Element
        public String title;
        @Element
        public String summary;
        @Element
        public String updated;
        @ElementList(name = "link", inline = true)
        public List<Link> links;

        @Override
        public String toString() {
            return "Entry{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", summary='" + summary + '\'' +
                    ", updated='" + updated + '\'' +
                    ", links=" + links +
                    '}';
        }
    }

    public static class Link {
        @Attribute
        public String href;
        @Attribute(required = false)
        public String title;
        @Attribute
        public String rel;
        @Attribute
        public String type;

        @Override
        public String toString() {
            return "Link{" +
                    "url='" + href + '\'' +
                    ", title='" + title + '\'' +
                    ", rel='" + rel + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

}
