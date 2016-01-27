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

package com.tickaroo.tikxml.retrofit;

import com.tickaroo.tikxml.TikXml;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

/**
 * Responsible to serialize data to xml format for outgoing http requests
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
final class TikXmlRequestBodyConverter<T> implements Converter<T, RequestBody> {

  private static final MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=UTF-8");
  private final TikXml tikXml;

  TikXmlRequestBodyConverter(TikXml tikXml) {
    this.tikXml = tikXml;
  }

  @Override public RequestBody convert(T value) throws IOException {
    Buffer buffer = new Buffer();
    tikXml.write(buffer, value);
    return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
  }
}
