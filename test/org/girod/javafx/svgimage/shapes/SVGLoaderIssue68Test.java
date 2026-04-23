/*
Copyright (c) 2025, 2026 Hervé Girod
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
package org.girod.javafx.svgimage.shapes;

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
import javafx.scene.shape.SVGPath;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;
import org.girod.javafx.svgimage.Viewport;
import org.girod.javafx.svgimage.xml.parsers.SVGPathParser;

/**
 * Test for issue #68: Implicit line-to commands in SVG paths are not parsed
 * correctly since version 1.4.
 *
 * The SVG path "M 10 10 90 10 90 90 10 90 Z" contains one MoveTo (M 10 10)
 * followed by three implicit LineTo commands (90 10, 90 90, 10 90) and a
 * ClosePath (Z). This should draw a square.
 *
 * @version 1.7.1
 */
public class SVGLoaderIssue68Test {

   public SVGLoaderIssue68Test() {
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
    * Test that the exact path data from issue #68 is parsed correctly at the
    * parser level. "M 10 10 90 10 90 90 10 90 Z" should expand to
    * "M 10.0 10.0 L 90.0 10.0 L 90.0 90.0 L 10.0 90.0 Z".
    */
   @Test
   public void testIssue68PathParsing() {
      System.out.println("SVGLoaderIssue68Test : testIssue68PathParsing");
      SVGPathParser parser = new SVGPathParser();
      Viewport viewport = new Viewport();

      parser.parse("M 10 10 90 10 90 90 10 90 Z", viewport);
      assertEquals("M 10.0 10.0 L 90.0 10.0 L 90.0 90.0 L 10.0 90.0 Z", parser.getContent());
   }

   /**
    * Test loading the exact SVG from issue #68 as a file.
    */
   @Test
   public void testIssue68LoadFile() throws Exception {
      System.out.println("SVGLoaderIssue68Test : testIssue68LoadFile");
      URL url = this.getClass().getResource("issue68.svg");
      SVGImage result = SVGLoader.load(url);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be an SVGPath", child instanceof SVGPath);

      String content = ((SVGPath) child).getContent();
      assertNotNull("SVGPath content should not be null", content);
      // The path should contain implicit L commands expanded from the M command
      assertTrue("Content should start with M", content.startsWith("M "));
      assertTrue("Content should contain implicit L commands", content.contains("L "));
      assertTrue("Content should end with Z", content.endsWith("Z"));
   }

   /**
    * Test loading the exact SVG from issue #68 as a string.
    */
   @Test
   public void testIssue68LoadString() throws Exception {
      System.out.println("SVGLoaderIssue68Test : testIssue68LoadString");
      String svg = "<svg height=\"100\" width=\"100\" xmlns=\"http://www.w3.org/2000/svg\">"
              + "<path d=\"M 10 10 90 10 90 90 10 90 Z\" />"
              + "</svg>";

      SVGImage result = SVGLoader.load(svg);
      assertNotNull("SVGImage should not be null", result);

      ObservableList<Node> children = result.getChildren();
      assertEquals("Must have one child", 1, children.size());
      Node child = children.get(0);
      assertTrue("Child must be an SVGPath", child instanceof SVGPath);

      String content = ((SVGPath) child).getContent();
      assertNotNull("SVGPath content should not be null", content);
      assertTrue("Content should start with M", content.startsWith("M "));
      assertTrue("Content should contain implicit L commands", content.contains("L "));
      assertTrue("Content should end with Z", content.endsWith("Z"));
   }
}
