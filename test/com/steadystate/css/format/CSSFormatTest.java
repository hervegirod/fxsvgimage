/*
 * Copyright (C) 1999-2020 David Schweinsberg.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.steadystate.css.format;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.parser.AbstractSACParserTestUtils;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.CSSVersion;
import com.steadystate.css.parser.ParserConfiguration;
import com.steadystate.css.parser.SACParser;
import org.junit.BeforeClass;

/**
 * @author rbri
 */
public class CSSFormatTest extends AbstractSACParserTestUtils {
   private static final String NEW_LINE = System.getProperty("line.separator");

   @BeforeClass
   public static void setUpClass() {
      ParserConfiguration.getInstance().setVersion(CSSVersion.CSS3);
   }

   @Override
   protected SACParser sacParser() {
      return CSSOMParser.newParser();
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void empty() throws Exception {
      final String css = "p {}";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("p { }", rule.getCssText());
      Assert.assertEquals("p { }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("p { }", rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("p { }", rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));

      Assert.assertEquals("p { }", rule.getCssText(new CSSFormat().setPropertiesInSeparateLines(4)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void simple() throws Exception {
      final String css = "p { background: green; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("p { background: green }", rule.getCssText());
      Assert.assertEquals("p { background: green }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("p { background: green }", rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("p { background: green }", rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));

      Assert.assertEquals("p {" + NEW_LINE + "background: green" + NEW_LINE + "}",
              rule.getCssText(new CSSFormat().setPropertiesInSeparateLines(0)));
      Assert.assertEquals("p {" + NEW_LINE + "    background: green" + NEW_LINE + "}",
              rule.getCssText(new CSSFormat().setPropertiesInSeparateLines(4)));
      Assert.assertEquals("p { background: green }",
              rule.getCssText(new CSSFormat().setPropertiesInSeparateLines(-1)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void many() throws Exception {
      final String css = "p { background: green; color: red }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("p { background: green; color: red }", rule.getCssText());
      Assert.assertEquals("p { background: green; color: red }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("p { background: green; color: red }", rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("p { background: green; color: red }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("p { background: green; color: red }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));

      Assert.assertEquals("p {" + NEW_LINE + "    background: green;" + NEW_LINE + "    color: red" + NEW_LINE + "}",
              rule.getCssText(new CSSFormat().setPropertiesInSeparateLines(4)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void allSelector() throws Exception {
      final String css = "* { display: none; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("* { display: none }", rule.getCssText());
      Assert.assertEquals("* { display: none }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("* { display: none }", rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("* { display: none }", rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("* { display: none }", rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));

      Assert.assertEquals("* {" + NEW_LINE + "    display: none" + NEW_LINE + "}",
              rule.getCssText(new CSSFormat().setPropertiesInSeparateLines(4)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void classSelector() throws Exception {
      final String css = ".info { display: none; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals(".info { display: none }", rule.getCssText());
      Assert.assertEquals(".info { display: none }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals(".info { display: none }", rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals(".info { display: none }", rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals(".info { display: none }", rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));

      Assert.assertEquals(".info {" + NEW_LINE + "    display: none" + NEW_LINE + "}",
              rule.getCssText(new CSSFormat().setPropertiesInSeparateLines(4)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void pseudoSelector() throws Exception {
      final String css = "*:hover{color:#f00;}";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("*:hover { color: rgb(255, 0, 0) }", rule.getCssText());
      Assert.assertEquals("*:hover { color: rgb(255, 0, 0) }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("*:hover { color: #ff0000 }", rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("*:hover { color: rgb(255, 0, 0) }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("*:hover { color: rgb(255, 0, 0) }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));

      Assert.assertEquals("*:hover {" + NEW_LINE + "    color: rgb(255, 0, 0)" + NEW_LINE + "}",
              rule.getCssText(new CSSFormat().setPropertiesInSeparateLines(4)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void substringSelector() throws Exception {
      final String css = ".li [test*=\"\"] { color:#f00; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals(".li [test*=\"\"] { color: rgb(255, 0, 0) }", rule.getCssText());
      Assert.assertEquals(".li [test*=\"\"] { color: rgb(255, 0, 0) }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals(".li [test*=\"\"] { color: #ff0000 }",
              rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals(".li [test*=\"\"] { color: rgb(255, 0, 0) }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals(".li [test*=''] { color: rgb(255, 0, 0) }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void syntheticSelector() throws Exception {
      String css = "*:hover{color:#f00;}";
      CSSStyleSheet sheet = parse(css, 0, 0, 0);
      CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("*:hover { color: rgb(255, 0, 0) }", rule.getCssText());
      Assert.assertEquals("*:hover { color: rgb(255, 0, 0) }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("*:hover { color: #ff0000 }", rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("*:hover { color: rgb(255, 0, 0) }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("*:hover { color: rgb(255, 0, 0) }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));

      css = ":hover{color:#f00;}";
      sheet = parse(css, 0, 0, 0);
      rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals(":hover { color: rgb(255, 0, 0) }", rule.getCssText());
      Assert.assertEquals(":hover { color: rgb(255, 0, 0) }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals(":hover { color: #ff0000 }", rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals(":hover { color: rgb(255, 0, 0) }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals(":hover { color: rgb(255, 0, 0) }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void newlineInsideString() throws Exception {
      String css = "div:after { content: 'abc \\A def'; }";
      CSSStyleSheet sheet = parse(css, 0, 0, 0);
      CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("div:after { content: \"abc \\A def\" }", rule.getCssText());
      Assert.assertEquals("div:after { content: \"abc \\A def\" }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("div:after { content: \"abc \\A def\" }",
              rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("div:after { content: \"abc \\A def\" }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("div:after { content: 'abc \\A def' }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));

      // CSS escaping is fun
      css = "div:after { content: 'abc \\d xyz'; }";
      sheet = parse(css, 0, 0, 0);
      rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("div:after { content: \"abc \\D xyz\" }", rule.getCssText());
      Assert.assertEquals("div:after { content: \"abc \\D xyz\" }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("div:after { content: \"abc \\D xyz\" }",
              rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("div:after { content: \"abc \\d xyz\" }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("div:after { content: 'abc \\D xyz' }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));

      // many
      css = "div:after { content: 'abc \\d \\a \\d \\a \\a xyz'; }";
      sheet = parse(css, 0, 0, 0);
      rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("div:after { content: \"abc \\D \\A \\D \\A \\A xyz\" }", rule.getCssText());
      Assert.assertEquals("div:after { content: \"abc \\D \\A \\D \\A \\A xyz\" }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("div:after { content: \"abc \\D \\A \\D \\A \\A xyz\" }",
              rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("div:after { content: \"abc \\d \\a \\d \\a \\a xyz\" }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("div:after { content: 'abc \\D \\A \\D \\A \\A xyz' }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void whitespaceInsideString() throws Exception {
      final String css = "div:after { content: 'abc \\A   def'; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("div:after { content: \"abc \\A   def\" }", rule.getCssText());
      Assert.assertEquals("div:after { content: \"abc \\A   def\" }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("div:after { content: \"abc \\A   def\" }",
              rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("div:after { content: \"abc \\A   def\" }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("div:after { content: 'abc \\A   def' }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void escaped() throws Exception {
      final String css = "div:after { content: 'css\\200Bparser'; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("div:after { content: \"css\u200Bparser\" }", rule.getCssText());
      Assert.assertEquals("div:after { content: \"css\u200Bparser\" }", rule.getCssText(new CSSFormat()));
      Assert.assertEquals("div:after { content: \"css\u200Bparser\" }",
              rule.getCssText(new CSSFormat().setRgbAsHex(true)));
      Assert.assertEquals("div:after { content: \"css\\200Bparser\" }",
              rule.getCssText(new CSSFormat().setUseSourceStringValues(true)));
      Assert.assertEquals("div:after { content: 'css\u200Bparser' }",
              rule.getCssText(new CSSFormat().setUseSingleQuotes(true)));
   }
}
