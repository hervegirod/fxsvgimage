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
package org.girod.javafx.svgimage.tosvg.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Various utilities concerning XML content.
 *
 * @since 1.0
 */
public class XMLNodeUtilities {
   private XMLNodeUtilities() {
   }

   private static void writeEncoding(StringBuilder buf, String encoding) {
      if (encoding != null) {
         buf.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\"?>");
         buf.append("\n");
      }
   }

   private static void writeEncoding(BufferedWriter writer, String encoding) throws IOException {
      if (encoding != null) {
         writer.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
         writer.newLine();
      }
   }

   /**
    * Save in a File the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param tab the tabulation for the XML file for each child node
    * @param outputFile the output file
    */
   public static void print(XMLNode node, int tab, File outputFile) throws IOException {
      print(node, tab, outputFile, null);
   }

   /**
    * Save in a File the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param tab the tabulation for the XML file for each child node
    * @param outputFile the output file
    * @param encoding the encoding (can be null)
    */
   public static void print(XMLNode node, int tab, File outputFile, String encoding) throws IOException {
      char[] chars = new char[tab];
      Arrays.fill(chars, ' ');
      String tabS = new String(chars);

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
         if (node instanceof XMLRoot && encoding == null) {
            XMLRoot root = (XMLRoot) node;
            encoding = root.getEncoding();
         }
         if (encoding != null) {
            writeEncoding(writer, encoding);
         }
         printNode(writer, node, "", tabS, false, true);
         writer.flush();
      }
   }

   /**
    * Save in n URL the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param tab the tabulation for the XML file for each child node
    * @param outputURL the output URL
    */
   public static void print(XMLNode node, int tab, URL outputURL) throws IOException {
      File file = new File(outputURL.getFile());
      print(node, tab, file);
   }

   private static void printNode(BufferedWriter writer, XMLNode node, String tabTotal, String tab, boolean isFirst, boolean isLast) throws IOException {
      boolean parentHasCData = false;
      XMLNode parent = node.getParent();
      if (parent != null) {
         parentHasCData = parent.hasCDATA();
      }
      if (!parentHasCData | (parentHasCData && !isFirst)) {
         writer.write(tabTotal);
      }
      writer.write("<");
      writer.write(node.getName());
      Iterator<Entry<String, String>> it = node.getAttributes().entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         writer.write(" ");
         writer.write(entry.getKey());
         writer.write("=\"");
         writer.write(entry.getValue());
         writer.write("\"");
      }
      if (node.hasChildren()) {
         writer.write(">");
         boolean hasCData = false;
         if (node.getCDATA() != null) {
            writer.write(node.getCDATA());
            hasCData = true;
         } else {
            writer.newLine();
         }
         String tabChildren = tabTotal + tab;
         List<XMLNode> children = node.getChildren();
         boolean hasMoreThanOneChild = children.size() > 1;
         boolean isFirstForParent = true;
         Iterator<XMLNode> it2 = children.iterator();
         while (it2.hasNext()) {
            XMLNode child = it2.next();
            printNode(writer, child, tabChildren, tab, isFirstForParent, false);
            isFirstForParent = false;
         }
         if (!hasCData || hasMoreThanOneChild) {
            writer.write(tabTotal);
         }
         writer.write("</");
         writer.write(node.getName());
         writer.write(">");
         if (!isLast) {
            writer.newLine();
         }
      } else {
         if (node.getCDATA() != null) {
            writer.write(">");
            writer.write(node.getCDATA());
            writer.write("</");
            writer.write(node.getName());
            writer.write(">");
         } else {
            writer.write("/>");
         }
         if (!isLast) {
            writer.newLine();
         }
      }
   }

   /**
    * Print as a String the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param tab the tabulation for the XML file for each child node
    * @return the String
    */
   public static String print(XMLNode node, int tab) {
      return print(node, tab, (String) null);
   }

   /**
    * Print as a String the content of an XML file under a root Node.
    *
    * @param node the root node
    * @param tab the tabulation for the XML file for each child node
    * @param encoding the encoding (vcan be null)
    * @return the String
    */
   public static String print(XMLNode node, int tab, String encoding) {
      char[] chars = new char[tab];
      Arrays.fill(chars, ' ');
      String tabS = new String(chars);

      StringBuilder buf = new StringBuilder();
      if (node instanceof XMLRoot && encoding == null) {
         XMLRoot root = (XMLRoot) node;
         encoding = root.getEncoding();
      }
      if (encoding != null) {
         writeEncoding(buf, encoding);
      }
      printNode(buf, node, "", tabS, true, true);
      return buf.toString();
   }

   private static void printNode(StringBuilder buf, XMLNode node, String tabTotal, String tab, boolean isFirst, boolean isLast) {
      boolean parentHasCData = false;
      XMLNode parent = node.getParent();
      if (parent != null) {
         parentHasCData = parent.hasCDATA();
      }
      if (!parentHasCData | (parentHasCData && !isFirst)) {
         buf.append(tabTotal);
      }
      buf.append("<").append(node.getName());
      Iterator<Entry<String, String>> it = node.getAttributes().entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         buf.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
      }
      if (node.hasChildren()) {
         if (node.hasCDATA()) {
            buf.append(">").append(node.getCDATA());
         } else {
            buf.append(">\n");
         }
         String tabChildren = tabTotal + tab;
         boolean isFirstInParent = true;
         Iterator<XMLNode> it2 = node.getChildren().iterator();
         while (it2.hasNext()) {
            XMLNode child = it2.next();
            printNode(buf, child, tabChildren, tab, isFirstInParent, false);
            isFirstInParent = false;
         }
         if (node.hasCDATA()) {
            buf.append(tabTotal);
         }
         buf.append("</").append(node.getName()).append(">");
         if (!isLast) {
            buf.append("\n");
         }
      } else {
         if (node.getCDATA() != null) {
            buf.append(">").append(node.getCDATA());
            buf.append("</").append(node.getName()).append(">");
         } else {
            buf.append("/>");
         }
         if (!isLast) {
            buf.append("\n");
         }
      }
   }
}
