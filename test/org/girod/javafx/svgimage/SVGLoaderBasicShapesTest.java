/*
Copyright (c) 2021, Hervé Girod
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Unit tests for several basic shapes.
 *
 * @since 0.3.1
 */
public class SVGLoaderBasicShapesTest {

   public SVGLoaderBasicShapesTest() {
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
    * Test of load method, of class SVGLoader. Test with a rect.
    */
   @Test
   public void testLoadRect() throws Exception {
      System.out.println("SVGLoaderBasicShapesTest : testLoadRect");
      URL url = this.getClass().getResource("rect.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Rectangle", child instanceof Rectangle);
      Rectangle rect = (Rectangle) child;
   }

   /**
    * Test of load method, of class SVGLoader. Test with a circle.
    */
   @Test
   public void testLoadCircle() throws Exception {
      System.out.println("SVGLoaderBasicShapesTest : testLoadCircle");
      URL url = this.getClass().getResource("circle2.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Circle", child instanceof Circle);
      Circle circle = (Circle) child;
   }

   /**
    * Test of load method, of class SVGLoader. Test with an Ellipse.
    */
   @Test
   public void testLoadEllipse() throws Exception {
      System.out.println("SVGLoaderBasicShapesTest : testLoadEllipse");
      URL url = this.getClass().getResource("ellipse.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be an Ellipse", child instanceof Ellipse);
      Ellipse circle = (Ellipse) child;
   }

   /**
    * Test of load method, of class SVGLoader. Test with a Polyline.
    */
   @Test
   public void testLoadPolyline() throws Exception {
      System.out.println("SVGLoaderBasicShapesTest : testLoadPolyline");
      URL url = this.getClass().getResource("polyline.svg");
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
      System.out.println("SVGLoaderBasicShapesTest : testLoadPolygon");
      URL url = this.getClass().getResource("polygon.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Polygon", child instanceof Polygon);
      Polygon polygon = (Polygon) child;
   }

   /**
    * Test of load method, of class SVGLoader. Test with a Line.
    */
   @Test
   public void testLoadLine() throws Exception {
      System.out.println("SVGLoaderBasicShapesTest : testLoadLine");
      URL url = this.getClass().getResource("line.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Line", child instanceof Line);
      Line line = (Line) child;
   }

   /**
    * Test of load method, of class SVGLoader. Test with a Path.
    */
   @Test
   public void testLoadPath() throws Exception {
      System.out.println("SVGLoaderBasicShapesTest : testLoadPath");
      URL url = this.getClass().getResource("path.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Path", child instanceof SVGPath);
      SVGPath path = (SVGPath) child;
      assertEquals(FillRule.NON_ZERO, path.getFillRule());
      assertEquals(StrokeLineJoin.MITER, path.getStrokeLineJoin());
      assertEquals(StrokeLineCap.BUTT, path.getStrokeLineCap());
      assertEquals(4.0, path.getStrokeMiterLimit(), 0);
   }
}
