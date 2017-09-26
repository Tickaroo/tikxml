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
import java.lang.reflect.Type;
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

    /**
     * Specify if an exception should be thrown if parts of the currently reading xml element are
     * not read (not mapped to java class)
     *
     * @param throwException true if exception should be thrown, otherwise false
     * @return The Builder itself
     */
    public Builder exceptionOnUnreadXml(boolean throwException) {
      config.exceptionOnUnreadXml = throwException;
      return this;
    }

    /**
     * Should the default xml declaration be written at the beginning of the xml document?
     * {@code <?xml version="1.0" encoding="UTF-8"?>}
     */
    public Builder writeDefaultXmlDeclaration(boolean writeDeclaration) {
      config.writeDefaultXmlDeclaration = writeDeclaration;
      return this;
    }

    /**
     * Adds an type converter for the given class
     *
     * @param clazz The class you want to register a TypeConverter for
     * @param converterForClass The converter for this class
     * @return The Builder itself
     */
    public <T> Builder addTypeConverter(Type clazz, TypeConverter<T> converterForClass) {
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
    public <T> Builder addTypeAdapter(Type clazz, TypeAdapter<T> adapterForClass) {
      config.typeAdapters.add(clazz, adapterForClass);
      return this;
    }

    /**
     * @return {@link TikXml} instance with the specified config
     */
    public TikXml build() {
      return new TikXml(config);
    }
  }

  // Visible for testing
  final TikXmlConfig config;

  private TikXml(TikXmlConfig config) {
    this.config = config;
  }

  public <T> T read(BufferedSource source, Type clazz) throws IOException {

    XmlReader reader = XmlReader.of(source);

    reader.beginElement();
    reader.nextElementName(); // We don't care about the name of the root tag

    T value = (T)config.getTypeAdapter(clazz).fromXml(reader, config);

    reader.endElement();

    return value;
  }

  public <T> void write(BufferedSink sink, T valueToWrite) throws IOException {
    write(sink, valueToWrite, valueToWrite.getClass());
  }

  public <T> void write(BufferedSink sink, T valueToWrite, Type typeOfValueToWrite) throws IOException {

    XmlWriter writer = XmlWriter.of(sink);

    TypeAdapter<T> adapter = config.getTypeAdapter(typeOfValueToWrite);
    if (config.writeDefaultXmlDeclaration()) {
      writer.xmlDeclaration();
    }
    adapter.toXml(writer, config, valueToWrite, null);
  }
}
