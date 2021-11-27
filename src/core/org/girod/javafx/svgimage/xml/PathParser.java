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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parse a path content specification.
 *
 * @since 0.6
 */
public class PathParser {
   private static final short PATH_NONE = -1;
   private static final short MOVE_TO = 0;
   private static final short CLOSE_PATH = 1;
   private static final short LINE_TO = 2;
   private static final short HORIZONTAL_LINE_TO = 3;
   private static final short VERTICAL_LINE_TO = 4;
   private static final short CUBIC_CURVE = 5;
   private static final short SMOOTH_CUBIC_CURVE = 6;
   private static final short QUADRATIC_CURVE = 7;
   private static final short SMOOTH_QUADRATIC_CURVE = 8;
   private static final short ELLIPTICAL_CURVE = 9;
   private static final Pattern LETTER = Pattern.compile("[sSlLhHvVmMcCqQtTaAzZ]");
   private static final Pattern PLUSMINUS = Pattern.compile("[+-]?\\d*([eE][+-]\\d+)?(\\.\\d+)?");

   private PathParser() {
   }

   private static void decomposePart(List<String> list, String token) {
      int offset = 0;
      Matcher m = PLUSMINUS.matcher(token);
      String part = token;
      while (true) {
         boolean found = m.find(offset);
         if (!found) {
            list.add(part);
            break;
         } else {
            int start = m.start();
            int end = m.end();
            String value = token.substring(start, end);
            list.add(value);
            offset = end;
            if (offset < token.length()) {
               part = token.substring(offset);
            } else {
               break;
            }
         }
      }
   }

   /**
    * Parse a path content.
    *
    * @param content the path content
    * @param viewport the viewport
    * @return
    */
   public static String parsePathContent(String content, Viewport viewport) {
      short type = PATH_NONE;
      int index = 0;
      boolean isFirst = true;
      StringBuilder buf = new StringBuilder();
      List<String> list = new ArrayList<>();
      StringTokenizer tok = new StringTokenizer(content, " ,");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         Matcher m = LETTER.matcher(tk);
         int offset = 0;
         String part = tk;
         while (true) {
            boolean found = m.find(offset);
            if (!found) {
               decomposePart(list, part);
               break;
            }
            int start = m.start();
            int end = m.end();
            if (start > 0) {
               String previousPart = tk.substring(offset, start);
               decomposePart(list, previousPart);
            }
            String letter = tk.substring(start, end);
            list.add(letter);
            offset = end;
            if (offset < tk.length()) {
               part = tk.substring(offset);
            } else {
               break;
            }
         }
      }
      for (int i = 0; i < list.size(); i++) {
         String tk = list.get(i);
         switch (tk) {
            case "m":
            case "M":
               type = MOVE_TO;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "h":
            case "H":
               type = HORIZONTAL_LINE_TO;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "v":
            case "V":
               type = VERTICAL_LINE_TO;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "l":
            case "L":
               type = LINE_TO;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "c":
            case "C":
               type = CUBIC_CURVE;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "q":
            case "Q":
               type = QUADRATIC_CURVE;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "s":
            case "S":
               type = SMOOTH_CUBIC_CURVE;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "t":
            case "T":
               type = SMOOTH_QUADRATIC_CURVE;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "a":
            case "A":
               type = ELLIPTICAL_CURVE;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "z":
            case "Z":
               index = -1;
               addPathCommand(buf, tk, isFirst);
               type = CLOSE_PATH;
               break;
            default:
               switch (type) {
                  case MOVE_TO:
                  case LINE_TO:
                     index++;
                     if (index == 2) {
                        index = 0;
                     }
                     if (index == 0) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, d);
                     } else if (index == 1) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, d);
                     } else {
                        addPathCommand(buf, tk);
                     }
                     break;
                  case HORIZONTAL_LINE_TO: {
                     index++;
                     double d = LengthParser.parseLength(tk, true, viewport);
                     addPathCommand(buf, d);
                     break;
                  }
                  case VERTICAL_LINE_TO: {
                     index++;
                     double d = LengthParser.parseLength(tk, false, viewport);
                     addPathCommand(buf, d);
                     break;
                  }
                  case CUBIC_CURVE:
                     index++;
                     if (index == 6) {
                        index = 0;
                     }
                     if (index == 0 || index == 2 || index == 4) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, d);
                     } else if (index == 1 || index == 3 || index == 5) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, d);
                     } else {
                        addPathCommand(buf, tk);
                     }
                     break;
                  case SMOOTH_CUBIC_CURVE:
                  case QUADRATIC_CURVE:
                     index++;
                     if (index == 4) {
                        index = 0;
                     }
                     if (index == 0 || index == 2) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, d);
                     } else if (index == 1 || index == 3) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, d);
                     } else {
                        addPathCommand(buf, tk);
                     }
                     break;
                  case SMOOTH_QUADRATIC_CURVE:
                     index++;
                     if (index == 3) {
                        index = 0;
                     }
                     if (index == 0) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, d);
                     } else if (index == 1) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, d);
                     } else {
                        addPathCommand(buf, tk);
                     }
                     break;
                  case ELLIPTICAL_CURVE:
                     index++;
                     if (index == 0) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, d);
                     } else if (index == 1) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, d);
                     } else if (index == 2) {
                        double d = ParserUtils.parseDoubleProtected(tk);
                        addPathCommand(buf, d);
                     } else if (index == 3 || index == 4) {
                        addPathCommand(buf, tk);
                     } else if (index == 5) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, d);
                     } else if (index == 6) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, d);
                     } else {
                        addPathCommand(buf, tk);
                     }
                     break;
                  default:
                     break;
               }
         }
         isFirst = false;
      }
      return buf.toString();
   }

   private static void addPathCommand(StringBuilder buf, double value) {
      buf.append(" ");
      buf.append(Double.toString(value));
   }

   private static void addPathCommand(StringBuilder buf, String command) {
      addPathCommand(buf, command, false);
   }

   private static void addPathCommand(StringBuilder buf, String command, boolean isFirst) {
      if (!isFirst) {
         buf.append(" ");
      }
      buf.append(command);
   }
}
