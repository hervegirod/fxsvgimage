/*
Copyright (c) 2026, Hervé Girod
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
package org.girod.javafx.svgimage.fromjfx.awt.converters;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.fromjfx.awt.utils.AwtUtilities;
import org.girod.javafx.svgimage.fromjfx.awt.utils.ShapeUtilities;

/**
 * A delegate which convert ImageViews.
 *
 * @since 1.8
 */
public class ImageViewConverter extends AbstractAwtConverter {
   private ImageView view = null;

   /**
    * Constructor.
    *
    * @param converter the AwtConverterDelegate
    * @param view the ImageView
    */
   public ImageViewConverter(AwtConverterDelegate converter, ImageView view) {
      super(converter, view);
      this.view = view;
   }

   /**
    * Convert the ImageView.
    */
   @Override
   public Boolean convert(boolean isDisabled) {
      Image image = view.getImage();
      if (allProperties.containsKey(IMAGE)) {
         URL url = (URL) allProperties.get(IMAGE);
         if (url == null) {
            image = null;
         } else {
            image = new Image(url.toString());
         }
      }
      if (image != null) {
         Graphics2D g2D = delegate.getGraphics2D();
         int x = (int) view.getX();
         int y = (int) view.getY();
         double width = image.getWidth();
         double height = image.getHeight();
         double fitWidth = view.getFitWidth();
         // this is just to avoid any exception
         if (fitWidth <= 0) {
            fitWidth = width;
         }
         double fitHeight = view.getFitHeight();
         // this is just to avoid any exception
         if (fitHeight <= 0) {
            fitHeight = height;
         }
         double dstWidth = fitWidth;
         double dstHeight = fitHeight;
         if (view.isPreserveRatio()) {
            if (fitHeight < fitWidth) {
               dstWidth = width / height * fitHeight;
            } else {
               dstHeight = height / width * fitWidth;
            }
         }
         BufferedImage awtImage = new BufferedImage((int) dstWidth, (int) dstHeight, BufferedImage.TYPE_INT_ARGB);
         java.awt.Image awtimage2 = SwingFXUtils.fromFXImage(image, awtImage);
         if (view.isDisabled()) {
            awtimage2 = AwtUtilities.createDisabledImage(awtimage2, delegate.getGrayScale());
         }
         awtimage2 = awtimage2.getScaledInstance((int) dstWidth, (int) dstHeight, java.awt.Image.SCALE_SMOOTH);
         Transform fromAncestorTransform = this.getTransformFromAncestor(view);
         AffineTransform awtTransform = this.getTransform(fromAncestorTransform);
         double angle = ShapeUtilities.getRotationAngle(awtTransform);
         if (angle != 0) {
            BufferedImage awtImage3 = new BufferedImage((int) dstWidth, (int) dstHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = awtImage3.createGraphics();

            AffineTransform at = new AffineTransform();
            at.translate(dstWidth / 2, dstHeight / 2);
            at.rotate(angle);
            at.scale(0.5, 0.5);
            at.translate(-dstWidth / 2, -dstHeight / 2);
            g2d.drawImage(awtimage2, at, null);
            awtimage2 = awtImage3;

         }
         g2D.drawImage(awtimage2, x, y, x + (int) dstWidth, y + (int) dstHeight, 0, 0, (int) dstWidth, (int) dstHeight, null);
         return true;
      }
      return false;
   }
}
