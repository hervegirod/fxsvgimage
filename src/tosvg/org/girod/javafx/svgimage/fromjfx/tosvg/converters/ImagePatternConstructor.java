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
package org.girod.javafx.svgimage.fromjfx.tosvg.converters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import static java.awt.image.ImageObserver.ALLBITS;
import java.awt.image.VolatileImage;
import java.util.Iterator;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import static org.girod.javafx.svgimage.fromjfx.tosvg.converters.AbstractImageConverter.imgToBase64String;
import org.girod.javafx.svgimage.fromjfx.utils.Utilities;
import org.girod.javafx.svgimage.fromjfx.tosvg.xml.SVGConstants;
import org.girod.javafx.svgimage.fromjfx.tosvg.xml.XMLNode;

/**
 * Builds SVG image pattern elements.
 *
 * @since 1.7.3
 */
public class ImagePatternConstructor {
   private int imagePatternID = 0;

   /**
    * Constructor.
    */
   public ImagePatternConstructor() {
   }

   /**
    * Return the next image pattern id.
    *
    * @return the image pattern id
    */
   public String getImagePatternID() {
      return "imagePattern_" + imagePatternID;
   }

   /**
    * Create an SVG pattern element for the provided ImagePattern paint.
    *
    * @param paint the ImagePattern paint
    * @param dstWidth the destination width
    * @param dstHeight the destination height
    * @return the SVG pattern element
    */
   public XMLNode createImagePattern(ImagePattern paint, int dstWidth, int dstHeight) {
      XMLNode patternNode = new XMLNode("pattern");
      patternNode.addAttribute("width", "100%");
      patternNode.addAttribute("height", "100%");
      XMLNode imageNode = new XMLNode("image");
      Image image = paint.getImage();
      int imageWidth = (int)image.getWidth();
      int imageHeight = (int)image.getHeight();
      imageNode.addAttribute("width", dstWidth);
      imageNode.addAttribute("height", dstHeight);
      imageNode.addAttribute("preserveAspectRatio", "xMaxYMax slice");
      patternNode.addChild(imageNode);
      writeImage(imageNode, paint, imageWidth, imageHeight);

      imagePatternID++;
      return patternNode;
   }

   private void writeImage(XMLNode xmlNode, ImagePattern paint, int dstWidth, int dstHeight) {
      Image image = paint.getImage();
      BufferedImage awtImage = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_ARGB);
      java.awt.Image awtimage2 = SwingFXUtils.fromFXImage(image, awtImage);
      awtimage2 = awtimage2.getScaledInstance( dstWidth, dstHeight, java.awt.Image.SCALE_SMOOTH);
      BufferedImage bimg = toBufferedImage(awtimage2, BufferedImage.TYPE_INT_ARGB);
      String content = imgToBase64String(bimg);
      List<String> parts = Utilities.splitString(content, 100);
      StringBuilder buf = new StringBuilder();
      Iterator<String> it = parts.iterator();
      while (it.hasNext()) {
         String splitted = it.next();
         buf.append(splitted);
         if (it.hasNext()) {
            buf.append("\n");
         }
      }
      content = "data:image/png;base64," + buf.toString();
      xmlNode.addAttribute("xmlns:xlink", SVGConstants.XLINK);
      xmlNode.addAttribute("xlink:href", content);
   }

   /**
    * Convert an AWT image to a {@link BufferedImage} of the requested type.
    *
    * @param image the source image
    * @param type the buffered image type
    * @return the resulting buffered image
    */
   private BufferedImage toBufferedImage(java.awt.Image image, int type) {
      if (image instanceof BufferedImage) {
         return (BufferedImage) image;
      } else if (image instanceof VolatileImage) {
         return ((VolatileImage) image).getSnapshot();
      }
      loadImage(image);
      final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
      final Graphics2D g2 = buffImg.createGraphics();
      g2.drawImage(image, null, null);
      g2.dispose();
      return buffImg;
   }

   private void loadImage(java.awt.Image image) {
      class StatusObserver implements ImageObserver {
         boolean imageLoaded = false;

         @Override
         public boolean imageUpdate(java.awt.Image img, final int infoflags, final int x, final int y, final int width, final int height) {
            if (infoflags == ALLBITS) {
               synchronized (this) {
                  imageLoaded = true;
                  notify();
               }
               return true;
            }
            return false;
         }
      }
      final StatusObserver imageStatus = new StatusObserver();
      synchronized (imageStatus) {
         if (image.getWidth(imageStatus) == -1 || image.getHeight(imageStatus) == -1) {
            while (!imageStatus.imageLoaded) {
               try {
                  imageStatus.wait();
               } catch (InterruptedException ex) {
               }
            }
         }
      }
   }
}
