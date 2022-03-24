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

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * A converter which convert a Shape3D.
 *
 * @since 1.0
 */
public class Shape3DConverter extends AbstractImageConverter {
   private Shape3D shape = null;

   public Shape3DConverter(ConverterDelegate delegate, Shape3D shape, XMLNode xmlParent) {
      super(delegate, shape, xmlParent);
      this.shape = shape;
   }

   /**
    * Return the Shape3D.
    *
    * @return the Shape3D
    */
   public Shape3D getShape() {
      return shape;
   }

   @Override
   public void applyStyle(XMLNode node, String clipID) {
      if (clipID != null) {
         StringBuilder buf = new StringBuilder();
         String style = buf.toString();
         node.addAttribute("style", style);
      }
   }

   /**
    * Convert a Shape3D by serializing the Shape as an Image.
    *
    * @return the node
    */
   @Override
   public XMLNode convert() {
      SnapshotParameters params = new SnapshotParameters();
      Color transparent = new Color(0, 0, 0, 0);
      params.setFill(transparent);
      params.setDepthBuffer(true);
      params.setCamera(shape.getScene().getCamera());

      XMLNode node = new XMLNode("image");
      node.addAttribute("x", shape.getLayoutX());
      node.addAttribute("y", shape.getLayoutY());
      double width = shape.getBoundsInLocal().getWidth();
      double height = shape.getBoundsInLocal().getHeight();      
      node.addAttribute("width", width);
      node.addAttribute("height", height);
      WritableImage image = shape.snapshot(params, null);

      writeImage(shape, node, image, width, height);
      xmlParent.addChild(node);
      return node;
   }
}
