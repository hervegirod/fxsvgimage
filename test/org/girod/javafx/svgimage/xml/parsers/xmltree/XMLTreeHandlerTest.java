/*
Copyright (c) 2025 Hervé Girod
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
package org.girod.javafx.svgimage.xml.parsers.xmltree;

import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic node parsing tests.
 *
 * @since 1.3
 */
public class XMLTreeHandlerTest {

   public XMLTreeHandlerTest() {
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
    * Test of the XMLTreeHandler class.
    */
   @Test
   public void testParse() {
      System.out.println("XMLTreeHandlerTest : testParse");
      URL url = XMLTreeHandlerTest.class.getResource("circle.svg");
      XMLRoot root = XMLParserTestUtils.parse(url);
      assertNotNull("XMLRoot must not be null", root);
      String rootName = root.getName();
      assertEquals("Root name", "svg", rootName);
      assertTrue("Must have the width attribute", root.hasAttribute("width"));
      assertEquals("width attribute", "210mm", root.getAttributeValue("width"));

      List<XMLNode> children = root.getChildren();
      assertNotNull("children must not be null", children);
      assertEquals("Must have 2 children", 2, children.size());

      XMLNode node = children.get(0);
      assertEquals("node name", "defs", node.getName());
      assertTrue("Must have the id attribute", node.hasAttribute("id"));
      assertEquals("width attribute", "defs4684", node.getAttributeValue("id"));

      node = children.get(1);
      assertEquals("node name", "g", node.getName());
   }
}
