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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Several utilitiers for shape parsing.
 *
 * @version 0.3.2
 */
public class ParserUtils {
   private static final Pattern ZERO = Pattern.compile("[\\-−+]?0+");

   private ParserUtils() {
   }

   public static double parseDoubleProtected(String valueS) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      if (m.matches()) {
         return 0d;
      } else {
         try {
            double valueD = Double.parseDouble(valueS);
            return valueD;
         } catch (NumberFormatException e) {
            return 0d;
         }
      }
   }

   public static double parseDoubleProtected(String valueS, boolean isWidth, Viewport viewport) {
      valueS = valueS.replace('−', '-');
      Matcher m = ZERO.matcher(valueS);
      if (m.matches()) {
         return 0d;
      } else {
         return LengthParser.parseLength(valueS, isWidth, viewport);
      }
   }

   public static void parseDoubleArgument(List<Double> args, String value) {
      value = value.replace('−', '-');
      Matcher m = ZERO.matcher(value);
      if (m.matches()) {
         args.add(0d);
      } else {
         try {
            double valueD = Double.parseDouble(value);
            args.add(valueD);
         } catch (NumberFormatException e) {
         }
      }
   }
}
