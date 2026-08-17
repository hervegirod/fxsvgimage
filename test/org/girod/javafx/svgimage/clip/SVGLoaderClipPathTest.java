/*
Copyright (c) 2026 Herve Girod
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
package org.girod.javafx.svgimage.clip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for clip-path with polyline, polygon, and path elements.
 *
 * @since 1.8
 */
public class SVGLoaderClipPathTest {
   private static double DELTA = 0.001d;

   public SVGLoaderClipPathTest() {
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
    * Test of load method, of class SVGLoader. Test with clip-path on a group.
    */
   @Test
   public void testClip() throws Exception {
      System.out.println("SVGLoaderClipPathTest : testClip");
      URL url = this.getClass().getResource("clipWithPath.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 4, children.size());
      Node child = children.get(0);
      assertTrue("Child must be a Rectangle", child instanceof Rectangle);
      Rectangle rect = (Rectangle) child;
      Node clip = rect.getClip();
      assertNull("Clip should not be set", clip);
      
      child = children.get(1);
      assertTrue("Child must be a Rectangle", child instanceof Rectangle);
      rect = (Rectangle) child;
      clip = rect.getClip();
      assertNotNull("Clip should be set", clip);      
      assertTrue("Clip must be a Polygon", clip instanceof Polygon);
      
      child = children.get(2);
      assertTrue("Child must be a Rectangle", child instanceof Rectangle);
      rect = (Rectangle) child;
      clip = rect.getClip();
      assertNotNull("Clip should be set", clip);    
      assertTrue("Clip must be a Polygon", clip instanceof Polygon);

      child = children.get(3);
      assertTrue("Child must be a Rectangle", child instanceof Rectangle);
      rect = (Rectangle) child;
      clip = rect.getClip();
      assertNotNull("Clip should be set", clip);   
      assertTrue("Clip must be a SVGPath", clip instanceof SVGPath);
   }
}
