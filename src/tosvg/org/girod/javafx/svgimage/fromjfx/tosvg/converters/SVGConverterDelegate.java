/*
Copyright (c) 2022, 2025, 2026 Hervé Girod
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
package org.girod.javafx.svgimage.fromjfx.tosvg.converters;

import java.io.File;
import java.util.Iterator;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import org.girod.javafx.svgimage.fromjfx.ConverterDelegate;
import org.girod.javafx.svgimage.fromjfx.ConverterParameters;
import org.girod.javafx.svgimage.fromjfx.tosvg.xml.XMLNode;
import org.girod.javafx.svgimage.fromjfx.tosvg.xml.XMLRoot;

/**
 * The SVGConverterDelegate class allows to handle the effective conversion.
 *
 * @version 1.8
 */
public class SVGConverterDelegate extends ConverterDelegate {
   private File file = null;
   private final ClipConstructor clipConstructor = new ClipConstructor();
   private final GradientConstructor gradientConstructor = new GradientConstructor();
   private final ImagePatternConstructor imagePatternConstructor = new ImagePatternConstructor();
   private final FilterConstructor filterConstructor = new FilterConstructor();
   private XMLNode defsNode = null;

   /**
    * Create a converter delegate.
    *
    * @param params the converter parameters
    */
   public SVGConverterDelegate(ConverterParameters params) {
      super(params);
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

   public void applyImagePattern(XMLNode xmlNode, int dstWidth, int dstHeight, ImagePattern pattern) {
      XMLNode xmlPattern = imagePatternConstructor.createImagePattern(pattern, dstWidth, dstHeight);
      String patternID = imagePatternConstructor.getImagePatternID();
      xmlPattern.addAttribute("id", patternID);
      defsNode.addChild(xmlPattern);
      xmlNode.addAttribute("fill", "url(#" + patternID + ")");
   }
   
   public void applyFillGradient(XMLNode xmlNode, LinearGradient gradient) {
      applyFillGradient(xmlNode, gradient, false);
   }   

   public void applyFillGradient(XMLNode xmlNode, LinearGradient gradient, boolean isDisabled) {
      XMLNode xmlGradient = gradientConstructor.createGradient(gradient, isDisabled, getGrayScale());
      String gradientID = gradientConstructor.getGradientID();
      if (xmlGradient != null) {
         xmlGradient.addAttribute("id", gradientID);
         defsNode.addChild(xmlGradient);
      }
      xmlNode.addAttribute("fill", "url(#" + gradientID + ")");
   }

   public void applyFillGradient(XMLNode xmlNode, RadialGradient gradient) {
      XMLNode xmlGradient = gradientConstructor.createGradient(gradient);
      String gradientID = gradientConstructor.getGradientID();
      if (xmlGradient != null) {
         xmlGradient.addAttribute("id", gradientID);
         defsNode.addChild(xmlGradient);
      }
      xmlNode.addAttribute("fill", "url(#" + gradientID + ")");
   }

   public void applyFilter(XMLNode xmlNode, Effect effect) {
      XMLNode xmlFilter = filterConstructor.createFilter(effect);
      String filterID = filterConstructor.getFilterID();
      if (xmlFilter != null) {
         xmlFilter.addAttribute("id", filterID);
         defsNode.addChild(xmlFilter);
         xmlNode.addAttribute("filter", "url(#" + filterID + ")");
      }
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param root the root Node
    * @param xmlRoot the xml root
    */
   public void convertRoot(Node root, XMLRoot xmlRoot) {
      ConverterParameters _params = new ConverterParameters();
      convertRoot(root, xmlRoot, _params);
   }

   /**
    * Convert a JavaFX Node hierarchy to a svg tree.
    *
    * @param root the root Node
    * @param xmlRoot the xml root
    * @param params the converter parameters
    */
   public void convertRoot(Node root, XMLRoot xmlRoot, ConverterParameters params) {
      this.root = root;
      if (params.title != null) {
         XMLNode titleNode = new XMLNode("title");
         titleNode.setCDATA(params.title);
         xmlRoot.addChild(titleNode);
      }

      defsNode = new XMLNode("defs");
      xmlRoot.addChild(defsNode);
      XMLNode gRoot = xmlRoot;
      if (params.insets > 0) {
         gRoot = new XMLNode("g");
         StringBuilder buf = new StringBuilder();
         buf.append("translate(");
         String insetsValue = Double.toString(params.insets);
         buf.append(insetsValue).append(" ").append(insetsValue);
         buf.append(")");
         gRoot.addAttribute("transform", buf.toString());
         xmlRoot.addChild(gRoot);
      }

      AbstractSVGConverter conv = getConverter(root, gRoot);
      if (conv != null) {
         if (params.allowTransformForRoot) {
            conv.applyTransforms(xmlRoot);
         }
         XMLNode xmlNode = conv.convert(root.isDisabled());
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

   private AbstractSVGConverter getConverter(Node node, XMLNode xmlParent) {
      AbstractSVGConverter conv = null;
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
      AbstractSVGConverter conv = getConverter(node, xmlParent);
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
            XMLNode xmlNode = conv.convert(node.isDisabled());
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
