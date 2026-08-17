/*
Copyright (c) 2026 Hervé Girod
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
package org.girod.javafx.svgimage.fromjfx.awt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javafx.scene.Node;
import org.girod.javafx.svgimage.fromjfx.AbstractJFXConverter;
import org.girod.javafx.svgimage.fromjfx.awt.converters.AwtConverterDelegate;
import org.girod.javafx.svgimage.fromjfx.awt.converters.ConverterListener;
import org.girod.javafx.svgimage.fromjfx.awt.utils.AwtUtilities;
import org.girod.javafx.svgimage.fromjfx.ConverterParameters;
import org.girod.javafx.svgimage.fromjfx.JFXConverterException;
import org.girod.javafx.svgimage.fromjfx.utils.Utilities;

/**
 * The AwtConverter class allows to convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
 *
 * @since 1.8
 */
public class Graphics2DConverter extends AbstractJFXConverter<AwtConverterDelegate> {

   /**
    * Constructor. Note that you will need to set the Graphics2D by {@link #setGraphics2D(java.awt.Graphics2D)} before or during the conversion.
    */
   public Graphics2DConverter() {
      super();
      this.delegate = new AwtConverterDelegate(defaultParams);
   }

   /**
    * Constructor. Note that you will need to set the Graphics2D by {@link #setGraphics2D(java.awt.Graphics2D)} before or during the conversion.
    *
    * @param params the converter parameters
    */
   public Graphics2DConverter(ConverterParameters params) {
      super(params);
      this.delegate = new AwtConverterDelegate(params);
   }

   /**
    * Constructor.
    *
    * @param g2d the Graphics2D
    * @param params the converter parameters
    */   
   public Graphics2DConverter(Graphics2D g2d, ConverterParameters params) {
      super(params);
      this.delegate = new AwtConverterDelegate(g2d, params);
   }

   /**
    * Constructor.
    *
    * @param g2d the Graphics2D
    */      
   public Graphics2DConverter(Graphics2D g2d) {
      super();
      this.delegate = new AwtConverterDelegate(g2d, defaultParams);
   }

   /**
    * Set the ConverterListener to use for the conversion. The listener will be called at the beginning and end of each converted Node.
    *
    * @param listener the ConverterListener
    */
   public void setListener(ConverterListener listener) {
      delegate.setListener(listener);
   }

   /**
    * Return the ConverterListener used for the conversion (may be null).
    *
    * @return the ConverterListener
    */
   public ConverterListener getListener() {
      return delegate.getListener();
   }

   /**
    * Set the Graphics2D.
    *
    * @param g2d the Graphics2D
    */
   public void setGraphics2D(Graphics2D g2d) {
      delegate.setGraphics2D(g2d);
   }

   /**
    * Return the Graphics2D.
    *
    * @return the Graphics2D
    */
   public Graphics2D getGraphics2D() {
      return delegate.getGraphics2D();
   }

   private void fillBackground(Node root, ConverterParameters params) {
      if (params.background != null) {
         Color background = Utilities.getAWTColor(params.background);
         if (background != null) {
            Rectangle2D rec = AwtUtilities.getBounds(root);
            Graphics2D g2d = delegate.getGraphics2D();
            g2d.setBackground(background);
            g2d.fillRect(0, 0, (int) rec.getWidth(), (int) rec.getHeight());
         }
      }
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders. 
    *
    * @param root the root Node
    * @throws JFXConverterException if writing the SVG fails
    */
   @Override
   public void convert(Node root) throws JFXConverterException {
      if (delegate.getGraphics2D() == null) {
         throw new JFXConverterException("Graphics2D is null");
      }
      delegate.reset();
      ConverterParameters params = delegate.getParameters();
      fillBackground(root, params);
      applyTranslate(root, params);
      delegate.convert(root);
   }

   private void applyTranslate(Node root, ConverterParameters params) {
      if (params.allowTransformForRoot) {
         Graphics2D g2d = delegate.getGraphics2D();
         Rectangle2D rect = getViewbox(root);
         if (params.insets != 0) {
            g2d.translate(-rect.getMinX() + params.insets, -rect.getMinY() + params.insets);
         } else {
            g2d.translate(-rect.getMinX(), -rect.getMinY());
         }
      }
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param g2D the Graphics2D
    * @param root the root Node
    * @throws JFXConverterException if writing the Graphics2D fails
    */
   public void convert(Graphics2D g2D, Node root) throws JFXConverterException {
      this.delegate.setGraphics2D(g2D);
      this.convert(root);
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param g2D the Graphics2D
    * @param root the root Node
    * @param params the parameters
    * @throws JFXConverterException if writing the Graphics2D fails
    */
   public void convert(Graphics2D g2D, Node root, ConverterParameters params) throws JFXConverterException {
      this.delegate.setGraphics2D(g2D);
      this.convert(root, params);
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param root the root Node
    * @param params the parameters
    * @throws JFXConverterException if writing the Graphics2D fails
    */
   @Override
   public void convert(Node root, ConverterParameters params) throws JFXConverterException {
      if (delegate.getGraphics2D() == null) {
         throw new JFXConverterException("Graphics2D is null");
      }
      delegate.reset();
      fillBackground(root, params);
      applyTranslate(root, params);

      delegate.convert(root, params);
   }
}
