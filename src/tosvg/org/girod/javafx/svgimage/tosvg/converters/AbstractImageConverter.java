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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import static java.awt.image.ImageObserver.ALLBITS;
import java.awt.image.RenderedImage;
import java.awt.image.VolatileImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.transform.Transform;
import javax.imageio.ImageIO;
import org.girod.javafx.svgimage.tosvg.utils.AwtImageUtilities;
import org.girod.javafx.svgimage.tosvg.utils.CSSProperties;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.SVGConstants;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

/**
 * Base converter for JavaFX nodes that render images.
 *
 * @since 1.0
 */
public abstract class AbstractImageConverter extends AbstractConverter {
   /**
    * Constructor.
    *
    * @param delegate the ConverterDelegate
    * @param node the Node
    * @param xmlParent the parent xml node
    */
   public AbstractImageConverter(ConverterDelegate delegate, Node node, XMLNode xmlParent) {
      super(delegate, node, xmlParent);
   } 
   
   /**
    * Writes an image.
    * @param theNode the node
    * @param xmlNode the xml node
    * @param image the image
    * @param dstWidth the width
    * @param dstHeight  the height
    */
   protected void writeImage(Node theNode, XMLNode xmlNode, Image image, double dstWidth, double dstHeight) {
      BufferedImage awtImage = new BufferedImage((int) dstWidth, (int) dstHeight, BufferedImage.TYPE_INT_ARGB);
      java.awt.Image awtimage2 = SwingFXUtils.fromFXImage(image, awtImage);
      if (theNode.isDisabled()) {
         awtimage2 = AwtImageUtilities.createDisabledImage(awtimage2);
      }
      awtimage2 = awtimage2.getScaledInstance((int) dstWidth, (int) dstHeight, java.awt.Image.SCALE_SMOOTH);

      Transform fromAncestorTransform = this.getTransformFromAncestor(theNode);
      AffineTransform awtTransform = AwtImageUtilities.getTransform(fromAncestorTransform);
      double angle = AwtImageUtilities.getRotationAngle(awtTransform);
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
      if (allProperties.containsKey(CSSProperties.OPACITY)) {
         double opacity = (Double) allProperties.get(CSSProperties.OPACITY);
         if (opacity < 1d) {
            awtimage2 = AwtImageUtilities.createOpacifiedImage(awtimage2, opacity, false);
         }
      }
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
   public BufferedImage toBufferedImage(java.awt.Image image, int type) {
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

   /**
    * Encode a rendered image as a base64 PNG string.
    *
    * @param img the rendered image
    * @return the base64-encoded PNG string, or null on error
    */
   public static String imgToBase64String(final RenderedImage img) {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      try (final OutputStream b64os = Base64.getEncoder().wrap(os)) {
         ImageIO.write(img, "png", b64os);
      } catch (IOException ioe) {
         return null;
      }
      return os.toString();
   }   
}
