/*
Copyright (c) 2026 Hervé Girod
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
package org.girod.javafx.svgimage.xml.parsers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.girod.javafx.svgimage.Viewport;
import org.girod.javafx.svgimage.xml.parsers.xmltree.XMLNode;

/**
 * Store the tags of the attributes which are natively inherited.
 *
 * @since 1.9
 */
public class NativeInheritance implements SVGTags {
   private static NativeInheritance inheritance = null;
   private final Set<String> set = new HashSet<>();
   private static Pattern RULE = Pattern.compile("([a-zA-Z_][a-zA-Z0-9\\-_]*)\\s*\\:\\s*([a-zA-Z_#\\(\\)][a-zA-Z0-9%'\\-_\\(\\) ]*)");

   private NativeInheritance() {
      set.add(CLIP_RULE);
      set.add(FILL);
      set.add(STROKE);
      set.add(STOP_COLOR);
      set.add(FLOOD_COLOR);
      set.add(LIGHTING_COLOR);
      set.add(COLOR_INTERPOLATION);
      set.add(FILL_OPACITY);
      set.add(STROKE_OPACITY);
      set.add(FILL_RULE);
      set.add(FONT_FAMILY);
      set.add(FONT_SIZE);
      set.add(FONT_WEIGHT);
      set.add(FONT_STYLE);
      set.add(MARKER_END);
      set.add(MARKER_START);
      set.add(MARKER_MID);
      set.add(STROKE_DASHARRAY);
      set.add(STROKE_DASHOFFSET);
      set.add(STROKE_LINECAP);
      set.add(STROKE_LINEJOIN);
      set.add(STROKE_MITERLIMIT);      
      set.add(STROKE_WIDTH);
      set.add(TEXT_ANCHOR);
      set.add(VISIBILITY);
   }

   /**
    * Return true if an attribute is natively inherited. See <a href="https://www.w3.org/TR/2011/REC-SVG11-20110816/propidx.html" />.
    *
    * @param tag the attribute tag
    * @return true if the attribute is natively inherited
    */
   public static boolean nativeInherit(String tag) {
      if (inheritance == null) {
         inheritance = new NativeInheritance();
      }
      return inheritance.set.contains(tag);
   }

   public static boolean hasAttribute(XMLNode node, String tag) {
      XMLNode parentNode = node.getParent();
      if (node.hasAttribute(tag)) {
         String attributeValue = node.getAttributeValue(tag);
         if (attributeValue.equals(INHERIT)) {
            return parentNode.hasAttribute(tag);
         } else {
            return true;
         }
      } else if (nativeInherit(tag)) {
         return parentNode.hasAttribute(tag);
      } else {
         return false;
      }
   }

   public static String getAttributeValue(XMLNode node, String tag) {
      XMLNode parentNode = node.getParent();
      if (node.hasAttribute(tag)) {
         String attributeValue = node.getAttributeValue(tag);
         if (attributeValue.equals(INHERIT)) {
            if (parentNode != null && parentNode.hasAttribute(tag)) {
               attributeValue = parentNode.getAttributeValue(tag);
            } else {
               attributeValue = null;
            }
         }
         return attributeValue;
      } else if (nativeInherit(tag)) {
         if (parentNode != null && parentNode.hasAttribute(tag)) {
            String attributeValue = parentNode.getAttributeValue(tag);
            return attributeValue;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String composeStyles(String styles, String parentStyles) {
      Map<String, String> nodeStyles = new TreeMap<>();
      Map<String, String> parentNodeStyles = new HashMap<>();
      StringTokenizer tok = new StringTokenizer(styles, ";");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().trim();
         Matcher m = RULE.matcher(tk);
         if (m.matches()) {
            String tag = m.group(1);
            String value = m.group(2);
            nodeStyles.put(tag, value);
         }
      }
      StringTokenizer tokParent = new StringTokenizer(parentStyles, ";");
      while (tokParent.hasMoreTokens()) {
         String tk = tokParent.nextToken().trim();
         Matcher m = RULE.matcher(tk);
         if (m.matches()) {
            String tag = m.group(1);
            String value = m.group(2);
            parentNodeStyles.put(tag, value);
         }
      }
      Iterator<Entry<String, String>> it = parentNodeStyles.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         String tag = entry.getKey();
         if (!nodeStyles.containsKey(tag) && nativeInherit(tag)) {
            nodeStyles.put(tag, entry.getValue());
         }
      }
      StringBuilder buf = new StringBuilder();
      it = nodeStyles.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         String tag = entry.getKey();
         buf.append(tag).append(":").append(entry.getValue()).append(";");
      }
      return buf.toString();
   }

   public static String getStyleAttributeValue(XMLNode node) {
      XMLNode parentNode = node.getParent();
      if (node.hasAttribute(STYLE)) {
         String attributeValue = node.getAttributeValue(STYLE);
         if (attributeValue.equals(INHERIT)) {
            if (parentNode != null && parentNode.hasAttribute(STYLE)) {
               attributeValue = parentNode.getAttributeValue(STYLE);
            } else {
               attributeValue = null;
            }
         } else if (parentNode.hasAttribute(STYLE)) {
            attributeValue = composeStyles(attributeValue, parentNode.getAttributeValue(STYLE));
         }
         return attributeValue;
      } else if (nativeInherit(STYLE)) {
         if (parentNode != null && parentNode.hasAttribute(STYLE)) {
            String attributeValue = parentNode.getAttributeValue(STYLE);
            return attributeValue;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static double getLineWidthValue(XMLNode node, Viewport viewport, String tag) {
      double lineWidth = -1;
      XMLNode parentNode = node.getParent();
      if (node.hasAttribute(tag)) {
         String attributeValue = node.getAttributeValue(tag);
         if (attributeValue.equals(INHERIT)) {
            if (parentNode != null && parentNode.hasAttribute(tag)) {
               lineWidth = parentNode.getLineWidthValue(tag, viewport, 1);
            }
         } else {
            lineWidth = node.getLineWidthValue(tag, viewport, 1);
         }
         return lineWidth;
      } else if (nativeInherit(tag)) {
         if (parentNode != null && parentNode.hasAttribute(tag)) {
            lineWidth = parentNode.getLineWidthValue(tag, viewport, 1);
         }
      }
      return lineWidth;
   }
}
