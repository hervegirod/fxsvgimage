/*
Copyright (c) 2021, 2022, 2025 Hervé Girod
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
package org.girod.javafx.svgimage.xml.parsers.xmltree;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Parse an XML File and return the associated tree of Nodes.
 *
 * @version 1.3
 */
public class XMLTreeHandler extends DefaultHandler2 {
   private static final Pattern TRIM = Pattern.compile("(?<right>\\s+)?\\S.*\\S(?<left>\\s+)?");
   private XMLNode node = null;
   private final Stack<XMLNode> nodes = new Stack<>();
   private XMLRoot root = null;
   private String encoding = null;
   private StringBuilder buf = null;

   /**
    * Constructor.
    */
   public XMLTreeHandler() {
   }

   /**
    * Constructor.
    *
    * @param encoding the encoding of the XML file
    */
   public XMLTreeHandler(String encoding) {
      this.encoding = encoding;
   }

   /**
    * Return the root node.
    *
    * @return the root node
    */
   public XMLRoot getRoot() {
      return root;
   }

   /**
    * Receive notification of the beginning of an element.
    *
    * @param uri the Namespace URI
    * @param localname the local name (without prefix), or the empty string if Namespace processing is not being
    * performed
    * @param qname The qualified name (with prefix), or the empty string if qualified names are not available
    * @param attr the specified or defaulted attributes
    */
   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
      if (!nodes.empty() && buf != null) {
         XMLNode _node = nodes.peek();
         String cdata = buf.toString();
         String trimmed = getTrimmedTextContent(cdata);
         if (!trimmed.isEmpty()) {
            _node.addChild(new XMLTextNode(_node, trimmed));
            if (!node.hasCDATA()) {
               node.setCDATA(trimmed);
            }
         }
         buf = null;
      }
      parseElement(qname, attr);
   }

   @Override
   public void endElement(String uri, String localname, String qname) {
      if (!nodes.empty()) {
         node = nodes.pop();
         if (buf != null) {
            String cdata = buf.toString();
            String trimmed = getTrimmedTextContent(cdata);
            if (!trimmed.isEmpty()) {
               node.addChild(new XMLTextNode(node, trimmed));
               if (!node.hasCDATA()) {
                  node.setCDATA(trimmed);
               }
            }
            buf = null;
         }
         node = node.getParent();
      }
   }

   @Override
   public void characters(char[] characters, int start, int length) {
      if (buf != null) {
         buf.append(characters, start, length);
      } else {
         buf = new StringBuilder();
         buf.append(characters, start, length);
      }
   }

   private String getTrimmedTextContent(String cdata) {
      String trimmed = cdata.trim();
      Matcher m = TRIM.matcher(cdata);
      if (m.matches()) {
         if (m.group("left") != null) {
            trimmed = trimmed + " ";
         }
      }
      return trimmed;
   }

   /**
    * Parse a node.
    *
    * @param qname the node qualified name
    * @param attr the node attributes
    */
   private void parseElement(String qname, Attributes attr) {
      XMLNode childNode;
      if (buf != null && node != null) {
         String cdata = buf.toString();
         String trimmed = getTrimmedTextContent(cdata);
         if (!trimmed.isEmpty()) {
            if (!node.hasCDATA()) {
               node.setCDATA(trimmed);
            }
         }
         buf = null;
      }
      if (node == null) {
         root = new XMLRoot(qname);
         root.setEncoding(encoding);
         childNode = root;
      } else {
         childNode = new XMLNode(node, qname);
      }
      buf = new StringBuilder();
      if (node != null) {
         node.addChild(childNode);
      }
      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         childNode.addAttribute(attrname, attrvalue);
      }
      if (node != null) {
         // Propagate style attributes from parent nodes to child nodes
         ParserUtils.propagateStyleAttributes(node, childNode);
      }
      nodes.push(childNode);
      node = childNode;
   }
}
