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

package com.tickaroo.simple.small;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class SimpleFrameworkSmallXmlBenchmark {

    public void parse(String xml) throws Exception {
        Serializer serializer = new Persister();
        Employee employee = serializer.read(Employee.class, xml);
        System.out.println(getClass().getSimpleName() + " " + employee.name);
    }

    @Root(name = "employee", strict = false)
    public static class Employee {
        @Element(required = true)
        public String name;
    }
}
