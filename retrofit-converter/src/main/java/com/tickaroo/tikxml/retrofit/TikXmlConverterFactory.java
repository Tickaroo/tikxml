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
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * {@linkplain Converter.Factory} for serializing and deserializing xml to java objects and vice
 * versa.
 *
 * Use {@link #create()} to use create a converter factory that uses a default {@link TikXml}
 * instance or use {@link #create(TikXml)} and pass your customized and properly configured {@link
 * TikXml} instance that should be used for this converter.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public final class TikXmlConverterFactory extends Converter.Factory {

  public static TikXmlConverterFactory create() {
    return create(new TikXml.Builder().build());
  }

  public static TikXmlConverterFactory create(TikXml tikXml) {
    return new TikXmlConverterFactory(tikXml);
  }

  private final TikXml tikXml;

  private TikXmlConverterFactory(TikXml tikXml) {
    if (tikXml == null) {
      throw new NullPointerException("TikXml (passed as parameter) is null");
    }
    this.tikXml = tikXml;
  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {

    if (!(type instanceof Class)) {
      return null;
    }
    Class<?> cls = (Class<?>) type;

    return new TikXmlResponseBodyConverter<>(tikXml, cls);
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type,
      Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    return new TikXmlRequestBodyConverter<>(tikXml);
  }
}
