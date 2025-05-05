/*
Copyright (c) 2022, 2025 Hervé Girod
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
package org.girod.javafx.svgimage.xml.specs;

import org.girod.javafx.svgimage.Viewbox;
import org.girod.javafx.svgimage.Viewport;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.xmltree.XMLNode;

/**
 * Represents a marker specifiation.
 *
 * @version 1.3
 */
public class MarkerSpec implements SVGTags {
   public final static short SPEC_ORIENT_NONE = 0;
   public final static short SPEC_ORIENT_ANGLE = 1;
   public final static short SPEC_ORIENT_AUTO = 2;
   public final static short SPEC_ORIENT_AUTO_REVERSE = 3;
   private Viewbox viewbox = null;
   private final XMLNode node;
   private double refX = 0;
   private double refY = 0;
   private double width = -1;
   private double height = -1;
   private short orient = SPEC_ORIENT_NONE;
   private double orientAngle = 0;

   public MarkerSpec(XMLNode node) {
      this.node = node;
      computeOrientation();
   }
   
   public short getOrientType() {
      return orient;
   }
   
   public boolean hasOrientation() {
      return orient != SPEC_ORIENT_NONE;
   }
   
   public double getOrientationAngle() {
      return orientAngle;
   }   
   
   private void computeOrientation() {
      if (node.hasAttribute(ORIENT)) {
         String value = node.getAttributeValue(ORIENT);
         switch (value) {
            case ORIENT_AUTO:
               orient = SPEC_ORIENT_AUTO;
               break;
            case SVGTags.ORIENT_AUTO_REVERSE:
               orient = SPEC_ORIENT_AUTO_REVERSE;
               break;      
            default:
               orientAngle = ParserUtils.getAngleDegrees(value);
         }
      }
   }

   public void computeRefPosition(Viewport viewport) {
      if (node.hasAttribute(REFX)) {
         refX = -node.getLengthValue(REFX, viewport);
      }
      if (node.hasAttribute(REFY)) {
         refY = -node.getLengthValue(REFY, viewport);
      }
      if (node.hasAttribute(MARKER_WIDTH)) {
         width = node.getLengthValue(MARKER_WIDTH, viewport);
      }
      if (node.hasAttribute(MARKER_HEIGHT)) {
         height = node.getLengthValue(MARKER_HEIGHT, viewport);
      }
   }

   public double getWidth() {
      return width;
   }

   public double getHeight() {
      return height;
   }

   public double getRefX() {
      return refX;
   }

   public double getRefY() {
      return refY;
   }

   public void setViewbox(Viewbox viewbox) {
      this.viewbox = viewbox;
      if (node.hasAttribute(PRESERVE_ASPECT_RATIO)) {
         String value = node.getAttributeValue(PRESERVE_ASPECT_RATIO);
         boolean preserveAspectRatio = ParserUtils.getPreserveAspectRatio(value);
         viewbox.setPreserveAspectRatio(preserveAspectRatio);
      }
   }

   public boolean hasViewbox() {
      return viewbox != null;
   }

   public Viewbox getViewbox() {
      return viewbox;
   }

   public XMLNode getXMLNode() {
      return node;
   }

   public double scaleWidth(double coord) {
      return coord * 1;
   }

   public double scaleHeight(double coord) {
      return coord * 1;
   }
}
