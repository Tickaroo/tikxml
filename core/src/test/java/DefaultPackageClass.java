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

import com.tickaroo.tikxml.TikXmlConfig;
import com.tickaroo.tikxml.XmlReader;
import com.tickaroo.tikxml.XmlWriter;
import com.tickaroo.tikxml.typeadapter.TypeAdapter;
import java.io.IOException;

/**
 * Simple class used to test if things still work as expected if the class is not in a package
 *
 * @author Hannes Dorfmann
 */
public class DefaultPackageClass {
  String aString;
  int anInt;

  public static class $TypeAdapter implements TypeAdapter<DefaultPackageClass> {
    @Override
    public DefaultPackageClass fromXml(XmlReader reader, TikXmlConfig config) throws IOException {
      return null;
    }

    @Override
    public void toXml(XmlWriter writer, TikXmlConfig config, DefaultPackageClass value,
        String overridingXmlElementTagNames) throws IOException {

    }
  }
}
