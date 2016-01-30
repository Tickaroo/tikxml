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

package com.tickaroo;

import com.google.caliper.BeforeExperiment;
import com.google.caliper.Benchmark;
import com.google.caliper.api.VmOptions;
import com.google.caliper.runner.CaliperMain;
import com.tickaroo.jackson.medium.JacksonMediumXmlBenchmark;
import com.tickaroo.jackson.small.JacksonSmallXmlBenchmark;
import com.tickaroo.simple.medium.SimpleFrameworkMediumXmlBenchmark;
import com.tickaroo.simple.small.SimpleFrameworkSmallXmlBenchmark;
import com.tickaroo.tikxml.medium.TikXmlMediumXmlBenchmark;
import com.tickaroo.tikxml.small.TikXmlSmallXmlBenchmark;
import java.io.IOException;
import java.io.InputStream;

public class XmlParsingBenchmark {

    public static void main(String[] args) {
        CaliperMain.main(XmlBenchmark.class, args);
    }

    @VmOptions("-XX:-TieredCompilation")
    public static class XmlBenchmark {

        private String xmlSmall;
        private String xmlMedium;

        @BeforeExperiment
        public void setUp() throws Exception {
            xmlSmall = loadResource("small.xml");
            xmlMedium = loadResource("medium.xml");
        }

        @Benchmark
        public void jackson_InputSmall() throws Exception {
            new JacksonSmallXmlBenchmark().parse(xmlSmall);
        }

      //  @Benchmark
        public void jackson_InputMedium() throws Exception {
            new JacksonMediumXmlBenchmark().parse(xmlMedium);
        }

        @Benchmark
        public void tikxml_InputSmall() throws Exception {
            new TikXmlSmallXmlBenchmark().parse(xmlSmall);
        }

      //  @Benchmark
        public void tikxml_InputMedium() throws Exception {
            new TikXmlMediumXmlBenchmark().parse(xmlMedium);
        }

        @Benchmark
        public void simpleframework_InputSmall() throws Exception {
            new SimpleFrameworkSmallXmlBenchmark().parse(xmlSmall);
        }

     //   @Benchmark
        public void simpleframework_InputMedium() throws Exception {
            new SimpleFrameworkMediumXmlBenchmark().parse(xmlMedium);
        }

        private String loadResource(String resourceName) throws IOException {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try (InputStream stream = classLoader.getResourceAsStream(resourceName)) {
                return convertStreamToString(stream);
            }
        }

        private static String convertStreamToString(InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
}
