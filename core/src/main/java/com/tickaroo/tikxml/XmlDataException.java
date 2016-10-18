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

import java.io.IOException;

/**
 * Thrown when the data in a XML document doesn't match the data expected by the caller. For
 * example, suppose the application expects an xml element attribute value but the XML document
 * contains no attribute or rather is currently pointing a xml element text content. When the call
 * to {@link XmlReader#nextAttributeName()} is made, a {@code XmlDataException} is thrown.
 *
 * <p>Exceptions of this type should be fixed by either changing the application code to accept the
 * unexpected XML, or by changing the XML to conform to the application's expectations.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public final class XmlDataException extends IOException {

  public XmlDataException(String message) {
    super(message);
  }
}
