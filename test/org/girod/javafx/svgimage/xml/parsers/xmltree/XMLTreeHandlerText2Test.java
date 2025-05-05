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
 * Node parsing tests for texts.
 *
 * @since 1.3
 */
public class XMLTreeHandlerText2Test {

   public XMLTreeHandlerText2Test() {
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
      System.out.println("XMLTreeHandlerText2Test : testParse1");
      URL url = XMLTreeHandlerText2Test.class.getResource("tspan.svg");
      XMLRoot root = XMLParserTestUtils.parse(url);
      assertNotNull("XMLRoot must not be null", root);
      String rootName = root.getName();
      assertEquals("Root name", "svg", rootName);
      assertTrue("Must have the width attribute", root.hasAttribute("width"));
      assertEquals("width attribute", "200", root.getAttributeValue("width"));

      List<XMLNode> children = root.getChildren();
      assertNotNull("children must not be null", children);
      assertEquals("Must have 1 child", 1, children.size());  
      
      XMLNode textNode = children.get(0);
      assertEquals("node name", "text", textNode.getName());
      String cdata = textNode.getCDATA();
      assertNotNull("Node cdata must not be null", cdata);
      assertEquals("node cdata", "I Love ", cdata);  
      
      List<ElementNode> allChildren = textNode.getAllChildren();
      assertNotNull("All children must not be null", allChildren);
      assertEquals("Must have 3 children", 3, allChildren.size());  
      
      ElementNode theNode = allChildren.get(0);
      assertTrue("Must be an XMLTextNode", theNode instanceof XMLTextNode);
      assertEquals("Text content", "I Love ", theNode.getText());  
      
      theNode = allChildren.get(1);
      assertTrue("Must be an XMLNode", theNode instanceof XMLNode);
      XMLNode xmlNode = (XMLNode)theNode;
      assertEquals("node name", "tspan", xmlNode.getName());
      
      theNode = allChildren.get(2);
      assertTrue("Must be an XMLTextNode", theNode instanceof XMLTextNode);
      assertEquals("Text content", "!", theNode.getText()); 
   }
}
