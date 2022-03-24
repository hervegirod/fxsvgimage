/*
Copyright (c) 2022, Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/fxsvgimage
 */
package org.girod.javafx.tosvg.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exceptions utilities.
 *
 * @since 1.0
 */
public class ExceptionsUtils {
   /**
    * We use this Pattern for Groovy runtime errors, just to get the files from which originates the error.
    */
   private static final Pattern EX_PAT = Pattern.compile("^script\\d+\\.groovy$");

   private ExceptionsUtils() {
   }

   /**
    * Return the line number for which a Script Exception originates.
    *
    * @param st the StackTraceElement
    * @return the line number
    */
   public static String getFileName(StackTraceElement st) {
      String fileName = null;
      String _fileName = st.getFileName();
      if (fileName == null) {
         fileName = _fileName;
      }
      Matcher m = EX_PAT.matcher(fileName);
      if (m.matches()) {
         fileName = _fileName;
      }
      return fileName;
   }

   /**
    * Return the line number for which a StackTraceElement originates.
    *
    * @param st the StackTraceElement
    * @return the line number
    */
   public static int getLineNumber(StackTraceElement st) {
      int lineNumber = -1;
      String fileName = st.getFileName();
      if (fileName != null) {
         Matcher m = EX_PAT.matcher(fileName);
         if (m.matches()) {
            lineNumber = st.getLineNumber();
         }
      }
      return lineNumber;
   }

   /**
    * Return the line number for which a Script Exception originates.
    *
    * @param e the Exception
    * @return the line number
    */
   public static int getLineNumber(Exception e) {
      int lineNumber = -1;
      StackTraceElement[] stArray = e.getStackTrace();
      if (stArray != null && stArray.length > 0) {
         for (StackTraceElement st : stArray) {
            String fileName = st.getFileName();
            if (fileName != null) {
               Matcher m = EX_PAT.matcher(fileName);
               if (m.matches()) {
                  lineNumber = st.getLineNumber();
                  // we should stop at the first StackTraceElement in the StackTrace, else we will go up the StackTrace
                  break;
               }
            }
         }
      }
      return lineNumber;
   }

   /**
    * Return the line number for which a Script Exception originates.
    *
    * @param e the Exception
    * @return the line number
    */
   public static String getFileName(Exception e) {
      String fileName = null;
      StackTraceElement[] stArray = e.getStackTrace();
      if (stArray != null && stArray.length > 0) {
         for (StackTraceElement st : stArray) {
            String _fileName = st.getFileName();
            if (fileName == null) {
               fileName = _fileName;
            }
            Matcher m = EX_PAT.matcher(fileName);
            if (m.matches()) {
               fileName = _fileName;
            }
         }
      }
      return fileName;
   }
}
