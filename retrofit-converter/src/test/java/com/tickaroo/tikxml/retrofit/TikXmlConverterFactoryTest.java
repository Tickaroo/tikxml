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

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author Hannes Dorfmann
 */
public class TikXmlConverterFactoryTest {

  @Rule public final MockWebServer server = new MockWebServer();

  interface Service {
    @POST("/") Call<Person> postPerson(@Body Person aPerson);
  }

  private Service service;

  @Before public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(TikXmlConverterFactory.create())
        .build();
    service = retrofit.create(Service.class);
  }

  @Test
  public void test() throws InterruptedException, IOException, UnsupportedOperationException {
    server.enqueue(new MockResponse().setBody("<<?xml version=\"1.0\" encoding=\"UTF-8\"?>person><name>Hannes</name></person>"));

    Person person = new Person();
    person.name = "outgoingName";
    Call<Person> call = service.postPerson(person);
    Response<Person> response = call.execute();
    Person responsePerson = response.body();

    Assert.assertNotNull(responsePerson);
    Assert.assertEquals("Hannes", responsePerson.name);

    RecordedRequest request = server.takeRequest();
    Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><person><name>outgoingName</name></person>", request.getBody().readUtf8());
    Assert.assertEquals("application/xml; charset=UTF-8", request.getHeader("Content-Type"));
  }
}
