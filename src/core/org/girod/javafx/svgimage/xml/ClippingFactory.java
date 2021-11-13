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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

/**
 * This class handles tha list of defined clipping paths.
 *
 * @version 0.5.1
 */
public class ClippingFactory implements SVGTags {
   private final Map<String, XMLNode> clipSpecs = new HashMap<>();

   public ClippingFactory() {
   }

   public void addClipSpec(String id, XMLNode node) {
      clipSpecs.put(id, node);
   }

   public boolean hasClip(String id) {
      return clipSpecs.containsKey(id);
   }

   /**
    * Creates a clip.
    *
    * @param id the clip id
    * @param viewport the viewport
    * @return the clip
    */
   public Shape createClip(String id, Viewport viewport) {
      XMLNode xmlNode = clipSpecs.get(id);
      if (clipSpecs.containsKey(id)) {
         Shape theShape = null;
         Iterator<XMLNode> it = xmlNode.getChildren().iterator();
         while (it.hasNext()) {
            XMLNode childNode = it.next();
            Shape shape = null;
            String name = childNode.getName();
            switch (name) {
               case CIRCLE:
                  shape = SVGShapeBuilder.buildCircle(childNode, viewport);
                  break;
               case PATH:
                  shape = SVGShapeBuilder.buildPath(childNode, viewport);
                  FillRule rule = ParserUtils.getClipRule(childNode);
                  if (rule != null) {
                     ((SVGPath) shape).setFillRule(rule);
                  }
                  break;
               case POLYLINE:
                  shape = SVGShapeBuilder.buildPolyline(xmlNode, viewport);
                  break;
               case POLYGON:
                  shape = SVGShapeBuilder.buildPolygon(xmlNode, viewport);
                  break;
               case ELLIPSE:
                  shape = SVGShapeBuilder.buildEllipse(childNode, viewport);
                  break;
               case RECT:
                  shape = SVGShapeBuilder.buildRect(childNode, viewport);
                  break;
               case LINE:
                  shape = SVGShapeBuilder.buildLine(childNode, viewport);
                  break;
               case TEXT:
                  shape = SVGShapeBuilder.buildText(childNode, viewport);
                  break;
            }
            if (theShape == null) {
               theShape = shape;
            } else {
               theShape = Shape.union(theShape, shape);
            }
            if (xmlNode.hasAttribute(TRANSFORM)) {
               ParserUtils.setTransform(theShape, xmlNode, viewport);
            }
         }
         return theShape;
      } else {
         return null;
      }
   }
}
