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

import com.tickaroo.tikxml.typeadapter.TypeAdapter;
import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;

/**
 * The main facade class to write or read xml. Use {@link TikXml.Builder} to instantiate a new
 * instance
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public final class TikXml {

  /**
   * A builder to create and configure an instance of a {@link TikXml}
   *
   * @author Hannes Dorfmann
   * @since 1.0
   */
  public static final class Builder {

    private TikXmlConfig config = new TikXmlConfig();


    public Builder throwExceptionOnMissingMapping(boolean throwException) {
      config.throwExceptionOnMissingMapping = throwException;
      return this;
    }

    /**
     * Adds an type converter for the given class
     *
     * @param clazz The class you want to register a TypeConverter for
     * @param converterForClass The converter for this class
     * @return The Builder itself
     */
    public <T> Builder addTypeConverter(Class<T> clazz, TypeConverter<T> converterForClass) {
      config.typeConverters.add(clazz, converterForClass);
      return this;
    }

    /**
     * Add / register a {@link TypeAdapter} for the given class
     *
     * @param clazz The class you want to register a type adapter for
     * @param adapterForClass The {@link TypeAdapter} for the given class
     * @return The builder itself
     */
    public <T> Builder addTypeAdapter(Class<T> clazz, TypeAdapter<T> adapterForClass) {
      config.typeAdapters.add(clazz, adapterForClass);
      return this;
    }


    public TikXml build() {
      return new TikXml(config);
    }

  }


  // Visible for testing
  final TikXmlConfig config;

  private TikXml(TikXmlConfig config) {
    this.config = config;
  }

  public <T> T read(BufferedSource source, Class<T> clazz) throws IOException {

    XmlReader reader = XmlReader.of(source);

    reader.beginElement();
    reader.nextElementName(); // We don't care about the name of the root tag

    T value = config.getTypeAdapter(clazz).fromXml(reader, config);

    reader.endElement();
    
    return value;
  }


  public <T> void write(BufferedSink sink, T valueToWrite) throws IOException {

  }


}
