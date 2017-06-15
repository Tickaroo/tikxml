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

package com.tickaroo.tikxml.converter.date.rfc3339;

import com.tickaroo.tikxml.converters.date.rfc3339.DateRfc3339TypeConverter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class DateRfc3339ConverterTypeTest {

  DateRfc3339TypeConverter adapter = new DateRfc3339TypeConverter();

  @Test public void readWithTwoDigitMillis() throws Exception {
    Assert.assertEquals(newDate(1985, 4, 12, 23, 20, 50, 520, 0),
        adapter.read("1985-04-12T23:20:50.52Z"));
  }

  @Test public void read() throws Exception {
    Assert.assertEquals(newDate(1970, 1, 1, 0, 0, 0, 0, 0),
        adapter.read("1970-01-01T00:00:00.000Z"));
    Assert.assertEquals(newDate(1985, 4, 12, 23, 20, 50, 520, 0),
        adapter.read("1985-04-12T23:20:50.520Z"));

    Assert.assertEquals(newDate(1996, 12, 19, 16, 39, 57, 0, -8 * 60),
        adapter.read("1996-12-19T16:39:57-08:00"));
    Assert.assertEquals(newDate(1990, 12, 31, 23, 59, 59, 0, 0),
        adapter.read("1990-12-31T23:59:60Z"));
    Assert.assertEquals(newDate(1990, 12, 31, 15, 59, 59, 0, -8 * 60),
        adapter.read("1990-12-31T15:59:60-08:00"));
    Assert.assertEquals(newDate(1937, 1, 1, 12, 0, 27, 870, 20),
        adapter.read("1937-01-01T12:00:27.870+00:20"));
  }

  @Test public void write() throws Exception {
    Assert.assertEquals("1970-01-01T00:00:00.000Z",
        adapter.write(newDate(1970, 1, 1, 0, 0, 0, 0, 0)));
    Assert.assertEquals("1985-04-12T23:20:50.520Z",
        adapter.write(newDate(1985, 4, 12, 23, 20, 50, 520, 0)));
    Assert.assertEquals("1996-12-20T00:39:57.000Z",
        adapter.write(newDate(1996, 12, 19, 16, 39, 57, 0, -8 * 60)));
    Assert.assertEquals("1990-12-31T23:59:59.000Z",
        adapter.write(newDate(1990, 12, 31, 23, 59, 59, 0, 0)));

    Assert.assertEquals("1990-12-31T23:59:59.000Z",
        adapter.write(newDate(1990, 12, 31, 15, 59, 59, 0, -8 * 60)));
    Assert.assertEquals("1937-01-01T11:40:27.870Z",
        adapter.write(newDate(1937, 1, 1, 12, 0, 27, 870, 20)));
  }

  private Date newDate(
      int year, int month, int day, int hour, int minute, int second, int millis, int offset) {
    Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    calendar.set(year, month - 1, day, hour, minute, second);
    calendar.set(Calendar.MILLISECOND, millis);
    return new Date(calendar.getTimeInMillis() - TimeUnit.MINUTES.toMillis(offset));
  }
}
