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
 * This kind of exception will be thrown if {@link TypeAdapters} try to load an {@link TypeAdapter}
 * for a certain class, but no such {@link TypeAdapter} could be found
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class TypeAdapterNotFoundException extends IOException {

  public TypeAdapterNotFoundException(String message) {
    super(message);
  }

  public TypeAdapterNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
