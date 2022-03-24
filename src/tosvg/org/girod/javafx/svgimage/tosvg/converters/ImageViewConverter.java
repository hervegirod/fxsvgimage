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
import java.net.URL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * A converter which convert ImageViews.
 *
 * @since 1.0
 */
public class ImageViewConverter extends AbstractImageConverter {
   private ImageView view = null;

   /**
    * Constructor.
    *
    * @param delegate the ConverterDelegate
    * @param view the ImageView
    * @param xmlParent the parent xml node
    */
   public ImageViewConverter(ConverterDelegate delegate, ImageView view, XMLNode xmlParent) {
      super(delegate, view, xmlParent);
      this.view = view;
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
    * Convert the ImageView.
    *
    * @return the node
    */
   @Override
   public XMLNode convert() {
      URL url = null;
      Image image = view.getImage();
      if (allProperties.containsKey(IMAGE)) {
         url = (URL) allProperties.get(IMAGE);
         if (url != null) {
            image = new Image(url.toString());
         }
      }
      if (url != null || image != null) {
         XMLNode node = new XMLNode("image");
         node.addAttribute("x", view.getX());
         node.addAttribute("y", view.getY());
         node.addAttribute("width", view.getFitWidth());
         node.addAttribute("height", view.getFitHeight());
         if (url != null) {
            File theFile = new File(url.getFile());
            File parentDir = delegate.getSVGFile().getParentFile();
            String path = Utilities.getRelativePath(parentDir, theFile, true);
            node.addAttribute("xlink:href", path);
         } else {
            writeImage(view, node, image, view.getFitWidth(), view.getFitHeight());
         }
         xmlParent.addChild(node);
         return node;
      } else {
         return null;
      }
   }
}
