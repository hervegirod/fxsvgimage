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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Unit tests for several basic shapes.
 *
 * @since 1.0
 */
public class SVGLoaderTransformsTest {
   private static double DELTA = 0.001d;

   public SVGLoaderTransformsTest() {
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
      System.out.println("SVGLoaderTransformsTest : testLoadRect");
      URL url = this.getClass().getResource("space-transforms.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Rectangle", child instanceof Rectangle);
      Rectangle rect = (Rectangle) child;
      ObservableList<Transform> transforms = rect.getTransforms();
      assertEquals("Must have 3 transforms", 3, transforms.size());
      Transform tr = transforms.get(0);
      assertTrue("Transform must be a Scale", tr instanceof Scale);
      Scale scale = (Scale) tr;
      assertEquals("Scale value", 0.5d, DELTA, scale.getX());
      assertEquals("Scale value", 0.5d, DELTA, scale.getY());

      tr = transforms.get(1);
      assertTrue("Transform must be a Translate", tr instanceof Translate);
      Translate translate = (Translate) tr;
      assertEquals("Translate value", 16d, DELTA, translate.getX());
      assertEquals("Translate value", 16d, DELTA, translate.getY());

      tr = transforms.get(2);
      assertTrue("Transform must be a Rotate", tr instanceof Rotate);
      Rotate rotate = (Rotate) tr;
      assertEquals("Rotate value", 16d, DELTA, rotate.getPivotX());
      assertEquals("Rotate value", 16d, DELTA, rotate.getPivotY());
      assertEquals("Rotate value", 45d, DELTA, rotate.getAngle());
   }
}
