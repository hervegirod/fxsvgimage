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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A Node in an XML File.
 *
 * @since 1.0
 */
public class XMLNode {
   /**
    * The node name.
    */
   protected String name = null;
   /**
    * The node parent.
    */
   protected XMLNode nodeParent = null;
   /**
    * The node index in the parent node.
    */
   private int index = -1;
   /**
    * The children nodes.
    */
   protected final List<XMLNode> children = new ArrayList<>();
   /**
    * The attributes.
    */
   protected final Map<String, String> attributes = new TreeMap<>();

   /**
    * The CDATA content.
    */
   private String cData = null;

   /**
    * Create the Node.
    *
    * @param nodeName the Node name
    */
   public XMLNode(String nodeName) {
      this.name = nodeName;
   }

   /**
    * Create a Node.
    *
    * @param parent the Node parent
    * @param nodeName the Node name
    */
   public XMLNode(XMLNode parent, String nodeName) {
      this.name = nodeName;
      this.nodeParent = parent;
   }

   XMLNode createCopyInstanceImpl() {
      XMLNode node = new XMLNode(nodeParent, name);
      return node;
   }

   /**
    * Create a copy of this node. It will also copy all the children under this node.
    *
    * @return the Node copy.
    */
   public XMLNode copy() {
      XMLNode node = createCopyInstanceImpl();
      node.cData = cData;
      Iterator<Entry<String, String>> it = attributes.entrySet().iterator();
      while (it.hasNext()) {
         Entry<String, String> entry = it.next();
         node.attributes.put(entry.getKey(), entry.getValue());
      }
      Iterator<XMLNode> it2 = children.iterator();
      int index = 0;
      while (it2.hasNext()) {
         XMLNode child = it2.next();
         child = child.copy();
         child.setParent(node, index);
         node.children.add(child);
         index++;
      }

      return node;
   }

   /**
    * Return the list of all children of this node having a specified name. The list will include all children, including children of children Nodes.
    *
    * @param name the name
    * @return the list
    */
   public List<XMLNode> getAllChildren(String name) {
      List<XMLNode> list = new ArrayList<>();
      Iterator<XMLNode> it = children.iterator();
      while (it.hasNext()) {
         XMLNode node = it.next();
         if (node.getName().equals(name)) {
            list.add(node);
         }
         List<XMLNode> childList = node.getAllChildren(name);
         list.addAll(childList);
      }
      return list;
   }

   /**
    * Return the Node parent (or null if the Node is the root of the XML File).
    *
    * @return the Node parent (or null if the Node is the root of the XML File)
    */
   public XMLNode getParent() {
      return nodeParent;
   }

   /**
    * Set the Node parent.
    *
    * @param parent the Node parent
    * @param index the index in the parent
    */
   private void setParent(XMLNode parent, int index) {
      this.nodeParent = parent;
      this.index = index;
   }

   /**
    * Return the Node qualified name.
    *
    * @return the Node qualified name.
    */
   public String getName() {
      return name;
   }

   /**
    * Return the ordered list of children of this Node.
    *
    * @return the ordered list of children of this Node
    */
   public List<XMLNode> getChildren() {
      return children;
   }

   /**
    * Return the first child of the Node.
    *
    * @return the first child of the Node
    */
   public XMLNode getFirstChild() {
      if (children.isEmpty()) {
         return null;
      } else {
         return children.get(0);
      }
   }

   /**
    * Return the last child of the Node.
    *
    * @return the last child of the Node
    */
   public XMLNode getLastChild() {
      if (children.isEmpty()) {
         return null;
      } else {
         return children.get(children.size() - 1);
      }
   }

   /**
    * Return the next sibling of the Node.
    *
    * @return the next sibling of the Node
    */
   public XMLNode getNextSibling() {
      if (nodeParent == null) {
         return null;
      } else {
         List<XMLNode> siblings = nodeParent.getChildren();
         if (siblings.size() < 2) {
            return null;
         } else {
            int index = siblings.indexOf(this);
            if (siblings.size() > index + 1) {
               return siblings.get(index + 1);
            } else {
               return null;
            }
         }
      }
   }

   /**
    * Return the previous sibling of the Node.
    *
    * @return the previous sibling of the Node
    */
   public XMLNode getPreviousSibling() {
      if (nodeParent == null) {
         return null;
      } else {
         List<XMLNode> siblings = nodeParent.getChildren();
         if (siblings.size() < 2) {
            return null;
         } else {
            int index = siblings.indexOf(this);
            if (index > 0) {
               return siblings.get(index - 1);
            } else {
               return null;
            }
         }
      }
   }

   /**
    * Add a child to this Node.
    *
    * @param child the Node child
    */
   public void addChild(XMLNode child) {
      child.setParent(this, children.size());
      children.add(child);
   }

   /**
    * Return true if this Node has children.
    *
    * @return true if this Node has children
    */
   public boolean hasChildren() {
      return !children.isEmpty();
   }

   /**
    * Return the number of children of the Node.
    *
    * @return the number of children of the Node
    */
   public int countChildren() {
      return children.size();
   }

   /**
    * Return the number of attributes of the Node.
    *
    * @return the number of attributes of the Node
    */
   public int countAttributes() {
      return attributes.size();
   }

   /**
    * Return the Map of attributes for this node.
    *
    * @return the Map of attributes for this node
    */
   public Map<String, String> getAttributes() {
      return attributes;
   }

   /**
    * Return the value of an attribute of a specified name.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public String getAttributeValue(String attrName) {
      return attributes.get(attrName);
   }

   /**
    * Return the value of an attribute of a specified name as a boolean.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public boolean getAttributeValueAsBoolean(String attrName) {
      return getAttributeValueAsBoolean(attrName, false);
   }

   /**
    * Return the value of an attribute of a specified name as a boolean.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public boolean getAttributeValueAsBoolean(String attrName, boolean defaultValue) {
      if (attributes.containsKey(attrName)) {
         String attrvalue = attributes.get(attrName);
         try {
            return attrvalue.equals("true");
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return the value of an attribute of a specified name as a float.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public float getAttributeValueAsFloat(String attrName) {
      return getAttributeValueAsFloat(attrName, 0f);
   }

   /**
    * Return the value of an attribute of a specified name as a float.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public float getAttributeValueAsFloat(String attrName, float defaultValue) {
      if (attributes.containsKey(attrName)) {
         String attrvalue = attributes.get(attrName);
         try {
            float f = Float.parseFloat(attrvalue);
            return f;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Return the value of an attribute of a specified name as an int.
    *
    * @param attrName the attribute name
    * @return the value of the attribute
    */
   public int getAttributeValueAsInt(String attrName) {
      return getAttributeValueAsInt(attrName, 0);
   }

   /**
    * Return the value of an attribute of a specified name as an int.
    *
    * @param attrName the attribute name
    * @param defaultValue the default value
    * @return the value of the attribute
    */
   public int getAttributeValueAsInt(String attrName, int defaultValue) {
      if (attributes.containsKey(attrName)) {
         String attrvalue = attributes.get(attrName);
         try {
            int i = Integer.parseInt(attrvalue);
            return i;
         } catch (NumberFormatException e) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, int value) {
      attributes.put(attrName, Integer.toString(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, String value) {
      attributes.put(attrName, value);
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, float value) {
      attributes.put(attrName, format(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, double value) {
      attributes.put(attrName, format(value));
   }

   /**
    * Add an attribute for this Node.
    *
    * @param attrName the attribute name
    * @param value the attribute value
    */
   public void addAttribute(String attrName, boolean value) {
      attributes.put(attrName, value ? "true" : "false");
   }

   /**
    * Set the CDATA content for the node.
    *
    * @param cData the CDATA content
    */
   public void setCDATA(String cData) {
      this.cData = cData;
   }

   /**
    * Return the CDATA content for the node.
    *
    * @return the CDATA content
    */
   public String getCDATA() {
      return cData;
   }

   /**
    * Return true if there is a the CDATA content for the node.
    *
    * @return true if there is a the CDATA content for the nodet
    */
   public boolean hasCDATA() {
      return cData != null;
   }

   /**
    * Return true if the Node has an attribute of a specified name.
    *
    * @param attrName the attribute name
    * @return true if the Node has an attribute of the specified name
    */
   public boolean hasAttribute(String attrName) {
      return attributes.containsKey(attrName);
   }

   private String format(float f) {
      String s = String.format("%.1f", f);
      s = s.replace(",", ".");
      return s;
   }

   private String format(double f) {
      String s = String.format("%.1f", f);
      s = s.replace(",", ".");
      return s;
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 23 * hash + Objects.hashCode(this.name);
      hash = 23 * hash + Objects.hashCode(this.nodeParent);
      hash = 23 * hash + this.index;
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final XMLNode other = (XMLNode) obj;
      if (this.index != other.index) {
         return false;
      }
      if (!Objects.equals(this.name, other.name)) {
         return false;
      }
      if (!Objects.equals(this.nodeParent, other.nodeParent)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      String str = XMLNodeUtilities.print(this, 2);
      return str;
   }
}
