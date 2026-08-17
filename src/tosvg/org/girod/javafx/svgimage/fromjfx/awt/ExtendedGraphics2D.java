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
package org.girod.javafx.svgimage.fromjfx.awt;

import javafx.scene.Node;
import javafx.scene.effect.Effect;

/**
 * An interface which allows to detect Groups in the Node structure. This can be used by specific
 * {@link java.awt.Graphics2D} to allow to keep the original Node structure. For example, a SVG Graphics2D converter might
 * create a "g" Node for each Node in the tree to regroup its children.
 *
 * <h1>Usage</h1>
 * The {@link org.girod.javafx.svgimage.fromjfx.awt.Graphics2DConverter} does not use this interface by default when converting a Node tree. However,
 * it is possible to use a {@link org.girod.javafx.svgimage.fromjfx.awt.converters.ConverterListener} to process specificaly the Groups.
 *
 * An example of usage could be:
 * <pre>
 * public void startNode(Graphics2D g2D, Node node) {
 *   if (node instanceof Parent) {
 *     if (g2D instanceof ExtendedGraphics2D) {
 *       ExtendedGraphics2D group2D = (ExtendedGraphics2D)g2D;
 *       group2D.startGroup(node.getId(), node);
 *     }
 *   }
 * }
 *
 * public void endNode(Graphics2D g2D, Node node) {
 *   if (node instanceof Parent) {
 *     if (g2D instanceof ExtendedGraphics2D) {
 *       ExtendedGraphics2D group2D = (ExtendedGraphics2D)g2D;
 *       group2D.endGroup(node);
 *     }
 *   }
 * }
 * </pre>
 *
 * @since 1.8
 */
public interface ExtendedGraphics2D {
   /**
    * Start a Group. Do nothing by default.
    *
    * @param name the group name
    * @param node the Node
    */
   public default void startGroup(String name, Node node) {
   }

   /**
    * End a Group. Do nothing by default.
    *
    * @param node the Node
    */
   public default void endGroup(Node node) {
   }

   /**
    * Apply an Effect on the next graphics object to be rendered. Do nothing by default.
    *
    * @param node the Node
    * @param effect the Effect
    */
   public default void applyEffect(Node node, Effect effect) {
   }
}
