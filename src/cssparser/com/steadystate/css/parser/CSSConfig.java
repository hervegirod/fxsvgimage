/*
 * Copyright (C) 2026 Herve Girod.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.steadystate.css.parser;

/**
 *
 * @since 1.6
 */
public class CSSConfig {
   public static final char SILENT = 0;
   public static final char SIGNAL_ERRORS = 1;
   public static final char DEBUG = 2;
   private static char MODE = SILENT;

   private CSSConfig() {
   }

   public static void setDebugMode(char mode) {
      MODE = mode;
   }

   public static boolean signalErrors() {
      return MODE != SILENT;
   }

   public static boolean isDebugMode() {
      return MODE == DEBUG;
   }
}
