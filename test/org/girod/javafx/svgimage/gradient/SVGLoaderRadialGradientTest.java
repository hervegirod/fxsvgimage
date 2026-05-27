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
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the radialGradient.
 *
 * @since 1.7.3
 */
public class SVGLoaderRadialGradientTest {
   private static final double DELTA = 0.0001d;

   public SVGLoaderRadialGradientTest() {
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
    * Test of load method, of class SVGLoader. Test with a radialGradient.
    */
   @Test
   public void testRadialGradient() throws Exception {
      System.out.println("SVGLoaderRadialGradientTest : testRadialGradient");
      URL url = this.getClass().getResource("radialgradient3.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("SVGImage should have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Circle", child instanceof Circle);
      Circle circle = (Circle) child;
      Paint fill = circle.getFill();
      assertNotNull("fill should not be null", fill);
      assertTrue("fill must be a RadialGradient", fill instanceof RadialGradient);
      RadialGradient gradient = (RadialGradient) fill;
      assertEquals("cx", 0.5d, gradient.getCenterX(), DELTA);
      assertEquals("cy", 0.5d, gradient.getCenterY(), DELTA);
      assertEquals("fx", 0d, gradient.getFocusAngle(), DELTA);
      assertEquals("fy", 0d, gradient.getFocusDistance(), DELTA);
      assertEquals("r", 0.5d, gradient.getRadius(), DELTA);
      List<Stop> stops = gradient.getStops();
      assertEquals("Stops", 4, stops.size());
      Stop stop = stops.get(0);
      double offset = stop.getOffset();
      assertEquals("offset", 0d, offset, DELTA);
      Color color = stop.getColor();
      assertEquals("Color", Color.color(1, 0.84313726d, 0), color);

      stop = stops.get(1);
      offset = stop.getOffset();
      assertEquals("offset", 0.1d, offset, DELTA);
      color = stop.getColor();
      assertEquals("Color", Color.color(1, 0.84313726, 0), color);

      stop = stops.get(2);
      offset = stop.getOffset();
      assertEquals("offset", 0.95d, offset, DELTA);
      color = stop.getColor();
      assertEquals("Color", Color.color(1, 0, 0), color);

      stop = stops.get(3);
      offset = stop.getOffset();
      assertEquals("offset", 1d, offset, DELTA);
      color = stop.getColor();
      assertEquals("Color", Color.color(1, 0, 0), color);
   }
}
