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
package org.girod.javafx.svgimage.fromjfx.awt.converters;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.Stack;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import org.girod.javafx.svgimage.fromjfx.ConverterDelegate;
import org.girod.javafx.svgimage.fromjfx.awt.utils.JFXShapeUtilities;
import org.girod.javafx.svgimage.fromjfx.ConverterParameters;

/**
 * The ConverterDelegate class allows handle the effective conversion.
 *
 * Note that it is preferable to use the {@link org.jfxconverter.JFXConverter} class rather than this one. This class is called internally
 * by the {@link org.jfxconverter.JFXConverter} class.
 *
 * @since 1.8
 */
public class AwtConverterDelegate extends ConverterDelegate {
   private Graphics2D g2d = null;
   private ConverterListener listener = null;
   private final Stack<TransformWrapper> transforms = new Stack<>();
   private final Stack<PaintWrapper> paints = new Stack<>();
   private final Stack<FontWrapper> fonts = new Stack<>();
   private final Stack<StrokeWrapper> strokes = new Stack<>();
   private final Stack<ClipWrapper> clips = new Stack<>();

   /**
    * Create a converter delegate.
    *
    * @param params the converter parameters
    */
   public AwtConverterDelegate(ConverterParameters params) {
      super(params);
   }

   /**
    * Create a converter delegate.
    *
    * @param g2d the graphics2D
    * @param params the converter parameters
    */
   public AwtConverterDelegate(Graphics2D g2d, ConverterParameters params) {
      super(params);
      this.g2d = g2d;
   }

   /**
    * Resets the AwtConverterDelegate.
    */
   public void reset() {
      clips.clear();
      strokes.clear();
      fonts.clear();
      paints.clear();
      transforms.clear();
   }

   /**
    * Set the ConverterListener to use for the conversion. The listener will be called at the beginning and end of each converted Node.
    *
    * @param listener the ConverterListener
    */
   public void setListener(ConverterListener listener) {
      this.listener = listener;
   }

   /**
    * Return the ConverterListener used for the conversion (may be null).
    *
    * @return the ConverterListener
    */
   public ConverterListener getListener() {
      return listener;
   }

   /**
    * Set the Graphics2D.
    *
    * @param g2d the Graphics2D
    */
   public void setGraphics2D(Graphics2D g2d) {
      this.g2d = g2d;
   }

   /**
    * Return the Graphics2D.
    *
    * @return the Graphics2D
    */
   public Graphics2D getGraphics2D() {
      return g2d;
   }

   /**
    * Set the CLip of a Node.
    *
    * @param node the Node
    */
   public void clip(Node node) {
      if (node.getClip() != null) {
         java.awt.Shape awtShape = JFXShapeUtilities.getShape(node.getClip());
         awtShape = JFXShapeUtilities.transformShape(node.getClip(), awtShape);
         clips.peek().setClip(awtShape);
         g2d.clip(awtShape);
      }
   }

   void startNode(Node node) {
      if (listener != null) {
         listener.startNode(g2d, node);
      }
      transforms.push(new TransformWrapper(g2d.getTransform()));
      paints.push(new PaintWrapper(g2d.getColor()));
      fonts.push(new FontWrapper(g2d.getFont()));
      strokes.push(new StrokeWrapper(g2d.getStroke()));
      clips.push(new ClipWrapper(g2d.getClip()));
   }

   void setStroke(Stroke stroke) {
      if (stroke != null) {
         strokes.peek().setStroke(stroke);
         g2d.setStroke(stroke);
      }
   }

   void setFont(Font font) {
      if (font != null) {
         fonts.peek().setFont(font);
         g2d.setFont(font);
      }
   }

   void setPaint(Paint paint) {
      if (paint != null) {
         paints.peek().setPaint(paint);
         g2d.setPaint(paint);
      } else {
         g2d.setPaint(null);
      }
   }

   void endNode(Node node) {
      if (listener != null) {
         listener.endNode(g2d, node);
      }
      if (!transforms.empty()) {
         TransformWrapper wrapper = transforms.pop();
         g2d.setTransform(wrapper.getTransform());
      }
      PaintWrapper wrapper = paints.pop();
      g2d.setPaint(wrapper.getOldPaint());
      FontWrapper fwrapper = fonts.pop();
      g2d.setFont(fwrapper.getOldFont());
      StrokeWrapper swrapper = strokes.pop();
      g2d.setStroke(swrapper.getOldStroke());
      ClipWrapper cwrapper = clips.pop();
      g2d.setClip(cwrapper.getOldClip());
   }

   void applyTranslation(double tX, double tY) {
      AffineTransform tr = AffineTransform.getTranslateInstance(tX, tY);
      g2d.transform(tr);
   }

   void applyScale(double scaleX, double scaleY) {
      AffineTransform tr = AffineTransform.getScaleInstance(scaleX, scaleY);
      g2d.transform(tr);
   }

   void applyRotation(double angle, double aX, double aY) {
      AffineTransform tr = AffineTransform.getRotateInstance(angle, aX, aY);
      g2d.transform(tr);
   }

   void applyTransform(AffineTransform tr) {
      g2d.transform(tr);
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param root the root Node
    * @param params the converter parameters
    */
   public void convert(Node root, ConverterParameters params) {
      this.params = params;
      convert(root, true, root.isDisabled());
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param node the root Node
    */
   public void convert(Node node) {
      convert(node, true, node.isDisabled());
   }

   /**
    * Convert a JavaFX Node hierarchy to a series of {@link java.awt.Graphics2D} orders.
    *
    * @param node the root Node
    * @param isRoot true if the node is the root node
    * @param isDisabled true if the node or one of its ancestors is disabled
    */
   public void convert(Node node, boolean isRoot, boolean isDisabled) {
      if (isRoot) {
         this.root = node;
      }
      AbstractAwtConverter conv = getConverter(node);
      if (conv != null) {
         this.startNode(node);
         conv.applyTransforms(!isRoot);
         conv.convert(isDisabled);
         if (listener != null && node.getEffect() != null) {
            listener.applyEffect(g2d, node, node.getEffect());
         }
         Node additionalNode = conv.getAdditionalNode();
         if (additionalNode != null && additionalNode.isVisible()) {
            convert(additionalNode);
         }
         if (node instanceof Parent) {
            Parent parent = (Parent) node;
            Iterator<Node> it = parent.getChildrenUnmodifiable().iterator();
            while (it.hasNext()) {
               Node child = it.next();
               if (child.isVisible()) {
                  convert(child, false, isDisabled || child.isDisabled());
               }
            }
         }
         if (listener != null && node.getEffect() != null) {
            listener.endEffect(g2d, node);
         }
         this.endNode(node);
      }
   }

   private AbstractAwtConverter getConverter(Node node) {
      AbstractAwtConverter conv = null;
      if (node instanceof Shape) {
         Shape shape = (Shape) node;
         conv = new ShapeConverter(this, shape);
      } else if (node instanceof Control) {
         Control control = (Control) node;
         conv = new ControlConverter(this, control);
      } else if (node instanceof Region) {
         Region region = (Region) node;
         conv = new RegionConverter(this, region);
      } else if (node instanceof ImageView) {
         ImageView view = (ImageView) node;
         conv = new ImageViewConverter(this, view);
      } else if (node instanceof Group) {
         Group group = (Group) node;
         conv = new GroupConverter(this, group);
      } else if (node instanceof SubScene) {
         SubScene subScene = (SubScene) node;
         conv = new SubSceneConverter(this, subScene);
      } else if (node instanceof Shape3D) {
         Shape3D shape = (Shape3D) node;
         conv = new Shape3DConverter(this, shape);
      }
      return conv;
   }

   private static class StrokeWrapper {
      private Stroke stroke = null;
      private Stroke oldStroke = null;

      private StrokeWrapper(Stroke oldStroke) {
         this.oldStroke = oldStroke;
      }

      void setStroke(Stroke stroke) {
         this.stroke = stroke;
      }

      Stroke getOldStroke() {
         return oldStroke;
      }

      Stroke getStroke() {
         return stroke;
      }
   }

   private static class FontWrapper {
      private Font font = null;
      private Font oldFont = null;

      private FontWrapper(Font oldFont) {
         this.oldFont = oldFont;
      }

      void setFont(Font font) {
         this.font = font;
      }

      Font getOldFont() {
         return oldFont;
      }

      Font getFont() {
         return font;
      }
   }

   private static class ClipWrapper {
      private java.awt.Shape oldClip = null;
      private java.awt.Shape clip = null;

      private ClipWrapper(java.awt.Shape oldClip) {
         this.oldClip = oldClip;
      }

      java.awt.Shape getOldClip() {
         return oldClip;
      }

      void setClip(java.awt.Shape clip) {
         this.clip = clip;
      }

      java.awt.Shape getClip() {
         return clip;
      }
   }

   private static class TransformWrapper {
      private AffineTransform transform = null;

      private TransformWrapper(AffineTransform transform) {
         this.transform = transform;
      }

      AffineTransform getTransform() {
         return transform;
      }
   }

   private static class PaintWrapper {
      private Paint paint = null;
      private Paint oldPaint = null;

      private PaintWrapper(Paint oldPaint) {
         this.oldPaint = oldPaint;
      }

      void setPaint(Paint paint) {
         this.paint = paint;
      }

      Paint getOldPaint() {
         return oldPaint;
      }

      Paint getPaint() {
         return paint;
      }
   }
}
