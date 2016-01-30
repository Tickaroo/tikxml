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

package com.tickaroo.jackson.medium;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;

public class JacksonMediumXmlBenchmark {

    public void parse(String xml) throws Exception {
        XmlMapper mapper = new XmlMapper();
        Feed feed = mapper.readValue(xml, Feed.class);
        System.out.println(getClass().getSimpleName() + " " + feed);
    }

    public static class Feed {
        public String id;
        public Title title;
        public String updated;
        public Author author;
        public String logo;
        public Link link;
        public String generator;
        @JacksonXmlProperty(localName = "entry")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Entry> entries;

        @Override
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

    public static class Author {
        public String name;

        @Override
        public String toString() {
            return "Author{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static class Entry {
        public String id;
        public Title title;
        public Summary summary;
        public String updated;
        @JacksonXmlProperty(localName = "link")
        @JacksonXmlElementWrapper(useWrapping = false)
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

    public static class Title {
        public String type;

        @JacksonXmlText(value = true)
        public String title;

        @Override
        public String toString() {
            return title;
        }
    }

    public static class Summary {
        public String type;

        @JacksonXmlText(value = true)
        public String summary;

        @Override
        public String toString() {
            return summary;
        }
    }

    public static class Link {
        public String href;
        public String title;
        public String rel;
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
