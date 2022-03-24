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
package org.girod.javafx.svgimage.tosvg.converters;

import java.io.File;
import java.util.Iterator;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;
import org.girod.javafx.svgimage.tosvg.xml.XMLRoot;

/**
 * The ConverterDelegate class allows handle the effective conversion.
 *
 * @since 1.0
 */
public class ConverterDelegate {
   private File file = null;
   private Node root = null;
   private final ClipConstructor clipConstructor = new ClipConstructor();
   private XMLNode defsNode = null;

   public ConverterDelegate() {
   }

   /**
    * Set the SVG file.
    *
    * @param file the file
    */
   public void setSVGFile(File file) {
      this.file = file;
   }

   /**
    * Return the SVG file.
    *
    * @return the file
    */
   public File getSVGFile() {
      return file;
   }

   /**
    * Return the defs Node.
    *
    * @return the defs Node
    */
   public XMLNode getDefsNode() {
      return defsNode;
   }

   /**
    * Return the root Node.
    *
    * @return the root Node
    */
   public Node getRoot() {
      return root;
   }

   private String applyClip(Node node) {
      Node clip = node.getClip();
      if (clip != null) {
         XMLNode xmlClip = clipConstructor.createClip(clip);
         if (xmlClip != null) {
            String clipID = clipConstructor.getClipID();
            XMLNode clipPath = new XMLNode("clipPath");
            clipPath.addAttribute("id", clipID);
            defsNode.addChild(clipPath);
            clipPath.addChild(xmlClip);
            return clipID;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param root the root Node
    * @param xmlRoot the xml root
    */
   public void convertRoot(Node root, XMLRoot xmlRoot) {
      this.root = root;

      defsNode = new XMLNode("defs");
      xmlRoot.addChild(defsNode);

      AbstractConverter conv = getConverter(root, xmlRoot);
      if (conv != null) {
         conv.applyTransforms(xmlRoot);
         XMLNode xmlNode = conv.convert();
         if (xmlNode != null) {
            String clipID = applyClip(root);
            conv.applyStyle(xmlNode, clipID);
         }
         if (root instanceof Parent) {
            Parent parent = (Parent) root;
            Iterator<Node> it = parent.getChildrenUnmodifiable().iterator();
            while (it.hasNext()) {
               Node child = it.next();
               if (child.isVisible()) {
                  xmlNode = convert(child, xmlRoot);
                  if (xmlNode != null) {
                     String clipID = applyClip(child);
                     conv.applyStyle(xmlNode, clipID);
                  }
               }
            }
         }
      }
   }

   private AbstractConverter getConverter(Node node, XMLNode xmlParent) {
      AbstractConverter conv = null;
      if (node instanceof Shape) {
         Shape shape = (Shape) node;
         conv = new ShapeConverter(this, shape, xmlParent);
      } else if (node instanceof Control) {
         Control control = (Control) node;
         conv = new ControlConverter(this, control, xmlParent);
      } else if (node instanceof Region) {
         Region region = (Region) node;
         conv = new RegionConverter(this, region, xmlParent);
      } else if (node instanceof ImageView) {
         ImageView view = (ImageView) node;
         conv = new ImageViewConverter(this, view, xmlParent);
      } else if (node instanceof Group) {
         Group group = (Group) node;
         conv = new GroupConverter(this, group, xmlParent);
      } else if (node instanceof SubScene) {
         SubScene subScene = (SubScene) node;
         conv = new SubSceneConverter(this, subScene, xmlParent);
      } else if (node instanceof Shape3D) {
         Shape3D shape = (Shape3D) node;
         conv = new Shape3DConverter(this, shape, xmlParent);
      }
      return conv;
   }

   private XMLNode convert(Node node, XMLNode xmlParent) {
      AbstractConverter conv = getConverter(node, xmlParent);
      if (conv != null) {
         boolean isVisible = node.isVisible();
         if (conv.hasVisibility()) {
            if (!conv.isVisible()) {
               return null;
            } else {
               isVisible = true;
            }
         }
         if (isVisible) {
            XMLNode xmlNode = conv.convert();
            conv.applyTransforms(xmlNode);
            String clipID = applyClip(node);
            conv.applyStyle(xmlNode, clipID);
            Node additionalNode = conv.getAdditionalNode();
            if (additionalNode != null && additionalNode.isVisible()) {
               convert(additionalNode, xmlParent);
            }
            Parent parent = conv.getParent();
            if (parent != null) {
               Iterator<Node> it = parent.getChildrenUnmodifiable().iterator();
               while (it.hasNext()) {
                  Node child = it.next();
                  convert(child, xmlNode);
               }
            }
            return xmlNode;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
