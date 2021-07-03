/*
Copyright (c) 2021, Hervé Girod
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
package org.girod.javafx.svgimage.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This utility class parse a length value.
 *
 * @since 0.1
 */
public class LengthParser {
   private static final Pattern NUMBER = Pattern.compile("\\d+(\\.\\d+)?");
   private static final Pattern NUMBER_UNIT = Pattern.compile("(\\d+)(\\.\\d+)?([\\w+])");
   private static final double INCH = 1 / 96d;

   private LengthParser() {
   }

   public static double parseLength(XMLNode node, String attrName) {
      String valueAsString = node.getAttributeValue(attrName);
      if (attrName != null) {
         return parseLength(valueAsString);
      } else {
         return 0;
      }
   }

   public static double parseLength(String lengthValue) {
      lengthValue = lengthValue.trim();
      Matcher m = NUMBER.matcher(lengthValue);
      if (m.matches()) {
         return Double.parseDouble(lengthValue);
      }
      m = NUMBER_UNIT.matcher(lengthValue);
      if (m.matches()) {
         String unitS = m.group(m.groupCount());
         String startDigits = m.group(1);
         String endDigit = m.group(2);
         double parsedValue;
         if (endDigit == null) {
            parsedValue = Double.parseDouble(startDigits);
         } else {
            parsedValue = Double.parseDouble(startDigits + "." + endDigit);
         }
         switch (unitS) {
            case "px":
               return parsedValue;
            case "pt":
               return parsedValue / INCH * 72d / 96d;
            case "in":
               return parsedValue / INCH;
            default:
               return parsedValue;
         }
      }
      return 0d;
   }
}
