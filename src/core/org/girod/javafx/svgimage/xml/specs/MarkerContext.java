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
package org.girod.javafx.svgimage.xml.specs;

import javafx.scene.Node;
import javafx.scene.paint.Paint;

/**
 * The context of the usage of a marker element.
 *
 * @since 1.0
 */
public class MarkerContext {
   private Node contextNode = null;
   private MarkerSpec markerStart = null;
   private MarkerSpec markerMid = null;
   private MarkerSpec markerEnd = null;
   private Paint contextFill = null;
   private Paint contextStroke = null;

   /**
    * Create an empty marker context.
    */
   public MarkerContext() {
   }

   /**
    * Return true if no markers are configured.
    *
    * @return true if empty
    */
   public boolean isEmpty() {
      return markerStart == null && markerMid == null && markerEnd == null;
   }

   /**
    * Set the start marker.
    *
    * @param markerStart the start marker
    */
   public void setMarkerStart(MarkerSpec markerStart) {
      this.markerStart = markerStart;
   }

   /**
    * Return true if a start marker is set.
    *
    * @return true if a start marker exists
    */
   public boolean hasMarkerStart() {
      return markerStart != null;
   }

   /**
    * Return the start marker.
    *
    * @return the start marker
    */
   public MarkerSpec getMarkerStart() {
      return markerStart;
   }

   /**
    * Set the mid marker.
    *
    * @param markerMid the mid marker
    */
   public void setMarkerMid(MarkerSpec markerMid) {
      this.markerMid = markerMid;
   }

   /**
    * Return true if a mid marker is set.
    *
    * @return true if a mid marker exists
    */
   public boolean hasMarkerMid() {
      return markerMid != null;
   }

   /**
    * Return the mid marker.
    *
    * @return the mid marker
    */
   public MarkerSpec getMarkerMid() {
      return markerMid;
   }

   /**
    * Return true if an end marker is set.
    *
    * @return true if an end marker exists
    */
   public boolean hasMarkerEnd() {
      return markerEnd != null;
   }

   /**
    * Return the end marker.
    *
    * @return the end marker
    */
   public MarkerSpec getMarkerEnd() {
      return markerEnd;
   }

   /**
    * Set the end marker.
    *
    * @param markerEnd the end marker
    */
   public void setMarkerEnd(MarkerSpec markerEnd) {
      this.markerEnd = markerEnd;
   }

   /**
    * Set the context fill used by markers.
    *
    * @param fill the fill paint
    */
   public void setContextFill(Paint fill) {
      this.contextFill = fill;
   }

   /**
    * Return the context fill used by markers.
    *
    * @return the fill paint
    */
   public Paint getContextFill() {
      return contextFill;
   }

   /**
    * Set the context node used for marker evaluation.
    *
    * @param node the context node
    */
   public void setContextNode(Node node) {
      this.contextNode = node;
   }

   /**
    * Return the context node used for marker evaluation.
    *
    * @return the context node
    */
   public Node getContextNode() {
      return contextNode;
   }

   /**
    * Set the context stroke used by markers.
    *
    * @param stroke the stroke paint
    */
   public void setContextStroke(Paint stroke) {
      this.contextStroke = stroke;
   }

   /**
    * Return the context stroke used by markers.
    *
    * @return the stroke paint
    */
   public Paint getContextStroke() {
      return contextStroke;
   }
}
