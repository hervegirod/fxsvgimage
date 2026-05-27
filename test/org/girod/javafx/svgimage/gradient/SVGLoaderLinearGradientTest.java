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
package org.girod.javafx.svgimage.gradient;

import static org.junit.Assert.assertNotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.net.URL;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the linearGradient.
 *
 * @since 1.7.3
 */
public class SVGLoaderLinearGradientTest {
   private static final double DELTA = 0.0001d;

   public SVGLoaderLinearGradientTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of load method, of class SVGLoader. Test with a linearGradient.
    */
   @Test
   public void testLinearGradient() throws Exception {
      System.out.println("SVGLoaderLinearGradientTest : testLinearGradient");
      URL url = this.getClass().getResource("linearGradient.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("SVGImage should have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Rectangle", child instanceof Rectangle);
      Rectangle rect = (Rectangle) child;
      Paint fill = rect.getFill();
      assertNotNull("fill should not be null", fill);
      assertTrue("fill must be a RadialGradient", fill instanceof LinearGradient);
      LinearGradient gradient = (LinearGradient) fill;
      assertEquals("x1", 106.82127d, gradient.getStartX(), DELTA);
      assertEquals("y1", 151.89787d, gradient.getStartY(), DELTA);
      assertEquals("x2", 459.70099d, gradient.getEndX(), DELTA);
      assertEquals("y2", 150.4693d, gradient.getEndY(), DELTA);
      List<Stop> stops = gradient.getStops();
      assertEquals("Stops", 3, stops.size());
      Stop stop = stops.get(0);
      double offset = stop.getOffset();
      assertEquals("offset", 0d, offset, DELTA);
      Color color = stop.getColor();
      assertEquals("Color", Color.color(0d, 0d, 1d), color);

      stop = stops.get(1);
      offset = stop.getOffset();
      assertEquals("offset", 0.5d, offset, DELTA);
      color = stop.getColor();
      assertEquals("Color", Color.color(1d, 0d, 0d), color);

      stop = stops.get(2);
      offset = stop.getOffset();
      assertEquals("offset", 1d, offset, DELTA);
      color = stop.getColor();
      assertEquals("Color", Color.color(0d, 0.5019608d, 0d), color);
   }
}
