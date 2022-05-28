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
package org.girod.javafx.svgimage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

/**
 * Unit tests for a polygon or polyline.
 *
 * @since 1.0
 */
public class SVGLoaderPolygonTest {

   public SVGLoaderPolygonTest() {
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
    * Test of load method, of class SVGLoader. Test with a Polyline.
    */
   @Test
   public void testLoadPolyline() throws Exception {
      System.out.println("SVGLoaderPolygonTest : testLoadPolyline");
      URL url = this.getClass().getResource("polyline-not-rendered.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Polyline", child instanceof Polyline);
      Polyline polyline = (Polyline) child;
   }

   /**
    * Test of load method, of class SVGLoader. Test with a Polygon.
    */
   @Test
   public void testLoadPolygon() throws Exception {
      System.out.println("SVGLoaderPolygonTest : testLoadPolygon");
      URL url = this.getClass().getResource("polygon-not-rendered.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Polygon", child instanceof Polygon);
      Polygon polygon = (Polygon) child;
   }
}
