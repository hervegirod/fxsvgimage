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
package org.girod.javafx.tosvg;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.girod.javafx.svgimage.xml.parsers.xmltree.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.xmltree.XMLRoot;

/**
 * This class compares two svg files.
 *
 * @since 1.7.2
 */
public class XMLComparator {
   private final XMLRoot root1;
   private final XMLRoot root2;

   public XMLComparator(URL refSVG, File svgFile) {
      this.root1 = XMLParserUtils.parse(refSVG);
      this.root2 = XMLParserUtils.parse(svgFile);
   }

   public XMLComparator(XMLRoot root1, XMLRoot root2) {
      this.root1 = root1;
      this.root2 = root2;
   }

   public boolean compare() {
      boolean compareAttrs = compareAttributes(root1, root2, true);
      if (!compareAttrs) {
         return false;
      }
      boolean compareChildren = compareChildren(root1, root2);
      return compareChildren;
   }

   private boolean compareChildren(XMLNode node1, XMLNode node2) {
      List<XMLNode> children1 = node1.getChildren();
      List<XMLNode> children2 = node2.getChildren();
      if (children1.size() != children2.size()) {
         System.err.println(getPath(node2) + " does not have the same number of children as " + getPath(node1));
         return false;
      }
      for (int i = 0; i < children1.size(); i++) {
         XMLNode child1 = children1.get(i);
         XMLNode child2 = children2.get(i);
         if (!child1.getName().equals(child2.getName())) {
            System.err.println("Child " + i + "of XMLNode " + getPath(node2) + " does not have the same name as child " + i + " of XMLNode " + getPath(node1));
            return false;
         }
         boolean compareAttrs = compareAttributes(child1, child2, false);
         if (!compareAttrs) {
            return false;
         }
         boolean compareChildren = compareChildren(child1, child2);
         if (!compareChildren) {
            return false;
         }
      }
      return true;
   }

   private String getPath(XMLNode node) {
      List<String> list = new ArrayList<>();
      list.add(node.getName());
      XMLNode parent = node.getParent();
      while (parent != null) {
         list.add(parent.getName());
         parent = parent.getParent();
      }
      StringBuilder buf = new StringBuilder();
      for (int i = list.size() - 1; i >= 0; i--) {
         buf.append(list.get(i));
         if (i > 0) {
            buf.append(".");
         }
      }
      return buf.toString();
   }

   private boolean compareAttributes(XMLNode node1, XMLNode node2, boolean acceptTransform) {
      Map<String, String> attr1 = node1.getAttributes();
      Map<String, String> attr2 = node2.getAttributes();
      if (attr1.size() != attr2.size()) {
         if (!acceptTransform) {
            System.err.println(getPath(node2) + " does not have the same number of attributes as " + getPath(node1));
            return false;
         } else {
            int size1 = attr1.size();
            int size2 = attr2.size();
            if (size1 > size2 || size2 > size1 + 1) {
               System.err.println(getPath(node2) + " does not have the same number of attributes as " + getPath(node1));
               return false;
            }
            if (! (attr2.containsKey("transform") && ! attr1.containsKey("transform"))) {
               System.err.println(getPath(node2) + " does not have the same number of attributes as " + getPath(node1));
               return false;
            }
         }
      }
      Iterator<Entry<String, String>> it = attr1.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         if (!attr2.containsKey(entry.getKey())) {
            System.err.println(getPath(node2) + " does not have attribute " + entry.getKey());
            return false;
         }
         if (!attr2.get(entry.getKey()).equals(entry.getValue())) {
            String effectiveValue = attr2.get(entry.getKey());
            System.err.println(getPath(node2) + " attribute " + entry.getKey() + " value " + effectiveValue + " is not the expected " + entry.getValue());
            return false;
         }
      }
      if ((!node1.hasCDATA() && !node2.hasCDATA())) {
         return true;
      } else if ((node1.hasCDATA() && !node2.hasCDATA())) {
         System.err.println(getPath(node2) + " CDATA is empty");
         return false;
      } else if ((!node1.hasCDATA() && node2.hasCDATA())) {
         System.err.println(getPath(node2) + " CDATA is not empty");
         return false;
      } else if (!node1.getCDATA().equals(node2.getCDATA())) {
         String effectiveValue = node1.getCDATA();
         System.err.println(getPath(node2) + " CDATA " + effectiveValue + " is not the expected " + node1.getCDATA());
         return false;
      }
      return true;
   }
}
