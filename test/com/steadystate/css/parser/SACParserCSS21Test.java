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
package com.steadystate.css.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;

import com.steadystate.css.ErrorHandler;
import com.steadystate.css.dom.CSSMediaRuleImpl;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.MediaListImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.parser.selectors.ChildSelectorImpl;
import com.steadystate.css.parser.selectors.ConditionalSelectorImpl;
import com.steadystate.css.parser.selectors.LangConditionImpl;

/**
 * @author rbri
 */
public class SACParserCSS21Test extends AbstractSACParserTestUtils {

   @Override
   protected SACParser sacParser() {
      return new SACParserCSS21();
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void selectorList() throws Exception {
      selectorList("h1:first-line", 1);
      selectorList("h1", 1);
      selectorList("h1, h2", 2);
      selectorList("h1:first-line, h2", 2);
      selectorList("h1, h2, h3", 3);
      selectorList("h1, h2,\nh3", 3);
      selectorList("h1, h2, h3#id", 3);
      selectorList("h1.class, h2, h3", 3);
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void selector() throws Exception {
      selectorType("a#id.class:link", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a#id.class", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a#id:link", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a#id", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a.class:link", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a.class", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a:link", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a", Selector.SAC_ELEMENT_NODE_SELECTOR);
      selectorType("#id.class:link", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("#id.class", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("#id:link", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("#id", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType(".class:link", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType(".class", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType(":link", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a:visited", Selector.SAC_CONDITIONAL_SELECTOR);
      selectorType("a:active", Selector.SAC_CONDITIONAL_SELECTOR);

      selectorType("h1 a", Selector.SAC_DESCENDANT_SELECTOR, Selector.SAC_ELEMENT_NODE_SELECTOR,
         Selector.SAC_ELEMENT_NODE_SELECTOR);
      selectorType("h1  a", Selector.SAC_DESCENDANT_SELECTOR, Selector.SAC_ELEMENT_NODE_SELECTOR,
         Selector.SAC_ELEMENT_NODE_SELECTOR);
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void selectorPseudo() throws Exception {
      selectorType("h1:first-line", Selector.SAC_DESCENDANT_SELECTOR, Selector.SAC_ELEMENT_NODE_SELECTOR,
         Selector.SAC_PSEUDO_ELEMENT_SELECTOR);
      selectorType("a:first-letter", Selector.SAC_DESCENDANT_SELECTOR, Selector.SAC_ELEMENT_NODE_SELECTOR,
         Selector.SAC_PSEUDO_ELEMENT_SELECTOR);
      selectorType("a:before", Selector.SAC_DESCENDANT_SELECTOR, Selector.SAC_ELEMENT_NODE_SELECTOR,
         Selector.SAC_PSEUDO_ELEMENT_SELECTOR);
      selectorType("a:after", Selector.SAC_DESCENDANT_SELECTOR, Selector.SAC_ELEMENT_NODE_SELECTOR,
         Selector.SAC_PSEUDO_ELEMENT_SELECTOR);

      selectorType("h1:lang(en)", Selector.SAC_CONDITIONAL_SELECTOR, Selector.SAC_ELEMENT_NODE_SELECTOR,
         Selector.SAC_PSEUDO_ELEMENT_SELECTOR);
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/selector.html#lang">http://www.w3.org/TR/CSS21/selector.html#lang</a>
    * @throws Exception if any error occurs
    */
   @Test
   public void selectorLang() throws Exception {
      final String css = "html:lang(fr-ca) { }\n"
         + "html:lang(de) { }\n"
         + ":lang(fr) > Q { }\n"
         + ":lang(de) > Q { }";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(4, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("html:lang(fr-ca) { }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      ConditionalSelectorImpl selector = (ConditionalSelectorImpl) ((CSSStyleRuleImpl) rule).getSelectors().item(0);
      Assert.assertEquals(Condition.SAC_LANG_CONDITION, selector.getCondition().getConditionType());
      Assert.assertEquals("fr-ca", ((LangConditionImpl) selector.getCondition()).getLang());

      rule = rules.item(1);
      Assert.assertEquals("html:lang(de) { }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      selector = (ConditionalSelectorImpl) ((CSSStyleRuleImpl) rule).getSelectors().item(0);
      Assert.assertEquals(Condition.SAC_LANG_CONDITION, selector.getCondition().getConditionType());
      Assert.assertEquals("de", ((LangConditionImpl) selector.getCondition()).getLang());

      rule = rules.item(2);
      Assert.assertEquals(":lang(fr) > Q { }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      ChildSelectorImpl childSelector = (ChildSelectorImpl) ((CSSStyleRuleImpl) rule).getSelectors().item(0);
      selector = (ConditionalSelectorImpl) childSelector.getAncestorSelector();
      Assert.assertEquals(Condition.SAC_LANG_CONDITION, selector.getCondition().getConditionType());
      Assert.assertEquals("fr", ((LangConditionImpl) selector.getCondition()).getLang());

      rule = rules.item(3);
      Assert.assertEquals(":lang(de) > Q { }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      childSelector = (ChildSelectorImpl) ((CSSStyleRuleImpl) rule).getSelectors().item(0);
      selector = (ConditionalSelectorImpl) childSelector.getAncestorSelector();
      Assert.assertEquals(Condition.SAC_LANG_CONDITION, selector.getCondition().getConditionType());
      Assert.assertEquals("de", ((LangConditionImpl) selector.getCondition()).getLang());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/selector.html#lang">http://www.w3.org/TR/CSS21/selector.html#lang</a>
    * @throws Exception if any error occurs
    */
   @Test
   public void selectorLangInvalid() throws Exception {
      final String css = "html:lang() { background: red }\n"
         + "p { color:green; }";

      final CSSStyleSheet sheet = parse(css, 1, 0, 1);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());
      Assert.assertEquals("p { color: green }", rules.toString().trim());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void condition() throws Exception {
      conditionType("a#id.class:link", Condition.SAC_AND_CONDITION, Condition.SAC_AND_CONDITION,
         Condition.SAC_ID_CONDITION, Condition.SAC_CLASS_CONDITION, Condition.SAC_PSEUDO_CLASS_CONDITION);
      conditionType("a#id.class", Condition.SAC_AND_CONDITION, Condition.SAC_ID_CONDITION,
         Condition.SAC_CLASS_CONDITION);
      conditionType("a#id:link", Condition.SAC_AND_CONDITION, Condition.SAC_ID_CONDITION,
         Condition.SAC_PSEUDO_CLASS_CONDITION);
      conditionType("a#id", Condition.SAC_ID_CONDITION);
      conditionType("a.class:link", Condition.SAC_AND_CONDITION, Condition.SAC_CLASS_CONDITION,
         Condition.SAC_PSEUDO_CLASS_CONDITION);
      conditionType("a.class", Condition.SAC_CLASS_CONDITION);
      conditionType("a:link", Condition.SAC_PSEUDO_CLASS_CONDITION);
      conditionType("#id.class:link", Condition.SAC_AND_CONDITION, Condition.SAC_AND_CONDITION,
         Condition.SAC_ID_CONDITION, Condition.SAC_CLASS_CONDITION, Condition.SAC_PSEUDO_CLASS_CONDITION);
      conditionType("#id.class", Condition.SAC_AND_CONDITION, Condition.SAC_ID_CONDITION,
         Condition.SAC_CLASS_CONDITION);
      conditionType("#id:link", Condition.SAC_AND_CONDITION, Condition.SAC_ID_CONDITION,
         Condition.SAC_PSEUDO_CLASS_CONDITION);
      conditionType("#id", Condition.SAC_ID_CONDITION);
      conditionType(".class:link", Condition.SAC_AND_CONDITION, Condition.SAC_CLASS_CONDITION,
         Condition.SAC_PSEUDO_CLASS_CONDITION);
      conditionType(".class", Condition.SAC_CLASS_CONDITION);
      conditionType(":link", Condition.SAC_PSEUDO_CLASS_CONDITION);
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void classCondition() throws Exception {
      conditionAssert(".class", null, "class", true);
      conditionAssert("h1.class", null, "class", true);
      Assert.assertNull(createSelectors("."));
      Assert.assertNull(createSelectors("h1."));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void idCondition() throws Exception {
      conditionAssert("#id", null, "id", true);
      conditionAssert("h1#id", null, "id", true);
      Assert.assertNull(createSelectors("#"));
      Assert.assertNull(createSelectors("h1.#"));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void pseudoCondition() throws Exception {
      conditionAssert(":link", null, "link", true);
      conditionAssert("a:link", null, "link", true);
      conditionAssert("a:visited", null, "visited", true);
      conditionAssert(":visited", null, "visited", true);
      conditionAssert("a:active", null, "active", true);
      conditionAssert(":active", null, "active", true);
      Assert.assertNull(createSelectors(":"));
      Assert.assertNull(createSelectors("a:"));
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void attributeCondition() throws Exception {
      conditionAssert("[rel]", "rel", null, false);
      conditionAssert("[ rel ]", "rel", null, false);

      conditionAssert("[rel=val]", "rel", "val", true);
      conditionAssert("[rel =val ]", "rel", "val", true);
      Assert.assertNull(createSelectors("[rel=]")); // invalid rule

      conditionAssert("[rel~=val]", "rel", "val", true);
      conditionAssert("[ rel ~= val ]", "rel", "val", true);
      Assert.assertNull(createSelectors("[rel~=]")); // invalid rule

      conditionAssert("[rel|=val]", "rel", "val", true);
      conditionAssert("[ rel |= val]", "rel", "val", true);
      Assert.assertNull(createSelectors("[rel|=]")); // invalid rule
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dojoCSS() throws Exception {
      final InputStream is = getClass().getResourceAsStream("dojo.css");
      Assert.assertNotNull(is);

      final CSSStyleSheet sheet = parse(is);
      Assert.assertEquals(17, sheet.getCssRules().getLength());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void emptyCSS() throws Exception {
      final CSSStyleSheet sheet = parse("");

      Assert.assertEquals(0, sheet.getCssRules().getLength());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void whitespaceOnlyCSS() throws Exception {
      final CSSStyleSheet sheet = parse("  \t \r\n \n");

      Assert.assertEquals(0, sheet.getCssRules().getLength());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void charset() throws Exception {
      final String css = "@charset 'UTF-8';\n"
         + "h1 { color: blue }\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(2, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@charset \"UTF-8\";", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h1 { color: blue }", rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void charsetWhitespaceBefore() throws Exception {
      final String css = "/* comment */ \n @charset 'UTF-8';\n"
         + "h1 { color: blue }\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(2, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@charset \"UTF-8\";", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h1 { color: blue }", rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void charsetImportBefore() throws Exception {
      final String css = "@import 'subs.css';\n"
         + "@charset 'UTF-8';\n"
         + "h1 { color: blue }\n"
         + "h2 { color: red }\n";

      final CSSStyleSheet sheet = parse(css, 1, 0, 1);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@import url(subs.css);", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h1 { color: blue }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("h2 { color: red }", rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void charsetImportBeforeBrokenCharset() throws Exception {
      final String css = "@import 'subs.css';\n"
         + "@charset\n"
         + "h1 { color: blue }\n"
         + "h2 { color: red }\n";

      final CSSStyleSheet sheet = parse(css, 2, 0, 2);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(2, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@import url(subs.css);", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h2 { color: red }", rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void charsetImportBeforeSkipComment() throws Exception {
      final String css = "@import 'subs.css';\n"
         + "@charset 'UTF-8';\n"
         + "/* comment */\n"
         + "h1 { color: blue }\n"
         + "h2 { color: red }\n";

      final CSSStyleSheet sheet = parse(css, 1, 0, 1);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@import url(subs.css);", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h1 { color: blue }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("h2 { color: red }", rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void importRuleOnly() throws Exception {
      final String css = "@import 'subs.css';";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("@import url(subs.css);", rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void importRulesOnly() throws Exception {
      final String css = "@import 'subs.css'; @import 'subs1.css'; @import 'subs2.css';";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@import url(subs.css);", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("@import url(subs1.css);", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("@import url(subs2.css);", rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void mediaRulePrint() throws Exception {
      final String css = "@media print { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());
      final CSSRule cssRule = rules.item(0);
      Assert.assertEquals("@media print {h1 { color: red } }", cssRule.getCssText());

      final MediaListImpl mediaList = (MediaListImpl) ((CSSMediaRuleImpl) cssRule).getMedia();

      Assert.assertEquals(1, mediaList.getLength());
      Assert.assertEquals("print", mediaList.getMediaText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void mediaRulePrintAndScreen() throws Exception {
      final String css = "@media print,screen { h1 { color: red } }";
      final CSSStyleSheet sheet = parse(css);

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());
      final CSSRule cssRule = rules.item(0);
      Assert.assertEquals("@media print, screen {h1 { color: red } }", cssRule.getCssText());

      final MediaListImpl mediaList = (MediaListImpl) ((CSSMediaRuleImpl) cssRule).getMedia();

      Assert.assertEquals(2, mediaList.getLength());
      Assert.assertEquals("print, screen", mediaList.getMediaText());
   }

   /**
    * @see <a href="http://dev.w3.org/csswg/css3-fonts/#font-face-rule">
    * http://dev.w3.org/csswg/css3-fonts/#font-face-rule</a>
    * @throws Exception if the test fails
    */
   @Test
   public void atRuleFontFaceNotSupported() throws Exception {
      final String css = "@font-face { font-family: Gentium; src: url(http://example.com/fonts/Gentium.ttf); }\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals(CSSRule.UNKNOWN_RULE, rule.getType());
      Assert.assertEquals("@font-face { font-family: Gentium; src: http://example.com/fonts/Gentium.ttf; }",
         rule.getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#at-rules">
    * http://www.w3.org/TR/CSS21/syndata.html#at-rules</a>
    * @throws Exception if the test fails
    */
   @Test
   public void atRules1() throws Exception {
      final String css = "@import 'subs.css';\n"
         + "h1 { color: blue }\n"
         + "@import 'list.css';\n"
         + "h2 { color: red }\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "@import rule must occur before all other rules, except the @charset rule."
         + " (Invalid token \"@import\". Was expecting one of: <S>, \"<!--\", \"-->\".)";
      Assert.assertEquals(expected, errorHandler.getErrorMessage());
      Assert.assertEquals("3", errorHandler.getErrorLines());
      Assert.assertEquals("1", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@import url(subs.css);", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h1 { color: blue }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("h2 { color: red }", rule.getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#at-rules">
    * http://www.w3.org/TR/CSS21/syndata.html#at-rules</a>
    * @throws Exception if the test fails
    */
   @Test
   public void atRules2() throws Exception {
      final String css = "@import 'subs.css';\n"
         + "@media print {\n"
         + "  @import 'print-main.css';\n"
         + "  body { font-size: 10pt }\n"
         + "}\n"
         + "h1 {color: blue }\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "@import rule must occur before all other rules, except the @charset rule."
         + " (Invalid token \"@import\". Was expecting: <S>.)";
      Assert.assertEquals(expected, errorHandler.getErrorMessage());
      Assert.assertEquals("3", errorHandler.getErrorLines());
      Assert.assertEquals("3", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@import url(subs.css);", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("@media print {body { font-size: 10pt } }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("h1 { color: blue }", rule.getCssText());
   }

   /**
    * Test for {@literal @}import after a rule
    *
    * @throws Exception
    */
   @Test
   public void atRules2b() throws Exception {
      final String css = "@import 'subs.css';\n"
         + "@media print {\n"
         + "  body { font-size: 10pt }\n"
         + "  @import 'print-main.css';\n"
         + "}\n"
         + "h1 {color: blue }\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "@import rule must occur before all other rules, except the @charset rule."
         + " (Invalid token \"@import\". Was expecting: <S>.)";
      Assert.assertEquals(expected, errorHandler.getErrorMessage());
      Assert.assertEquals("4", errorHandler.getErrorLines());
      Assert.assertEquals("3", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@import url(subs.css);", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("@media print {body { font-size: 10pt } }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("h1 { color: blue }", rule.getCssText());
   }

   @Test
   public void hexColor() throws Exception {
      final String cssText = "color: #ccc; background: #1c1d00;";

      final CSSOMParser parser = parser();
      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final InputSource source = new InputSource(new StringReader(cssText));
      final CSSStyleDeclaration style = parser.parseStyleDeclaration(source);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      // Enumerate the properties and retrieve their values
      Assert.assertEquals(2, style.getLength());

      String name = style.item(0);
      name = style.item(0);
      Assert.assertEquals("color : rgb(204, 204, 204)", name + " : " + style.getPropertyValue(name));

      name = style.item(1);
      name = style.item(1);
      Assert.assertEquals("background : rgb(28, 29, 0)", name + " : " + style.getPropertyValue(name));
   }

   @Test
   public void rgb() throws Exception {
      final String cssText = "foreground: rgb( 10, 20, 30 )";

      final CSSOMParser parser = parser();
      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final InputSource source = new InputSource(new StringReader(cssText));
      final CSSStyleDeclaration style = parser.parseStyleDeclaration(source);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      // Enumerate the properties and retrieve their values
      Assert.assertEquals(1, style.getLength());

      String name = style.item(0);
      name = style.item(0);
      Assert.assertEquals("foreground : rgb(10, 20, 30)", name + " : " + style.getPropertyValue(name));
   }

   @Test
   public void rgbInsideFunction() throws Exception {
      final String cssText = "color: foo(#cd4);";

      final CSSOMParser parser = parser();
      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final InputSource source = new InputSource(new StringReader(cssText));
      final CSSStyleDeclaration style = parser.parseStyleDeclaration(source);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      // Enumerate the properties and retrieve their values
      Assert.assertEquals(1, style.getLength());

      String name = style.item(0);
      name = style.item(0);
      Assert.assertEquals("color: foo(rgb(204, 221, 68))", name + ": " + style.getPropertyValue(name));
   }

   @Test
   public void funct() throws Exception {
      final String cssText = "clip: foo(rect( 10px, 20em, 30px, max(40, blue(rgb(1,2,3))) ) )";

      final CSSOMParser parser = parser();
      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final InputSource source = new InputSource(new StringReader(cssText));
      final CSSStyleDeclaration style = parser.parseStyleDeclaration(source);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      // Enumerate the properties and retrieve their values
      Assert.assertEquals(1, style.getLength());

      String name = style.item(0);
      name = style.item(0);
      Assert.assertEquals("clip : foo(rect(10px, 20em, 30px, max(40, blue(rgb(1, 2, 3)))))",
         name + " : " + style.getPropertyValue(name));

      final CSSValueImpl value = (CSSValueImpl) style.getPropertyCSSValue(name);
      LexicalUnitImpl unit = (LexicalUnitImpl) value.getValue();
      Assert.assertEquals(LexicalUnit.SAC_FUNCTION, unit.getLexicalUnitType());
      Assert.assertEquals("foo", unit.getFunctionName());

      unit = (LexicalUnitImpl) unit.getParameters();
      Assert.assertEquals(LexicalUnit.SAC_RECT_FUNCTION, unit.getLexicalUnitType());
      Assert.assertEquals("rect", unit.getFunctionName());

      unit = (LexicalUnitImpl) unit.getParameters();
      Assert.assertEquals(LexicalUnit.SAC_PIXEL, unit.getLexicalUnitType());
      Assert.assertEquals(10f, unit.getFloatValue(), 0.00001);

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_OPERATOR_COMMA, unit.getLexicalUnitType());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_EM, unit.getLexicalUnitType());
      Assert.assertEquals(20f, unit.getFloatValue(), 0.00001);

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_OPERATOR_COMMA, unit.getLexicalUnitType());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_PIXEL, unit.getLexicalUnitType());
      Assert.assertEquals(30f, unit.getFloatValue(), 0.00001);

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_OPERATOR_COMMA, unit.getLexicalUnitType());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_FUNCTION, unit.getLexicalUnitType());
      Assert.assertEquals("max", unit.getFunctionName());

      unit = (LexicalUnitImpl) unit.getParameters();
      Assert.assertEquals(LexicalUnit.SAC_INTEGER, unit.getLexicalUnitType());
      Assert.assertEquals(40, unit.getIntegerValue());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_OPERATOR_COMMA, unit.getLexicalUnitType());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_FUNCTION, unit.getLexicalUnitType());
      Assert.assertEquals("blue", unit.getFunctionName());

      unit = (LexicalUnitImpl) unit.getParameters();
      Assert.assertEquals(LexicalUnit.SAC_RGBCOLOR, unit.getLexicalUnitType());
      Assert.assertEquals("rgb", unit.getFunctionName());

      unit = (LexicalUnitImpl) unit.getParameters();
      Assert.assertEquals(LexicalUnit.SAC_INTEGER, unit.getLexicalUnitType());
      Assert.assertEquals(1, unit.getIntegerValue());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_OPERATOR_COMMA, unit.getLexicalUnitType());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_INTEGER, unit.getLexicalUnitType());
      Assert.assertEquals(2, unit.getIntegerValue());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_OPERATOR_COMMA, unit.getLexicalUnitType());

      unit = (LexicalUnitImpl) unit.getNextLexicalUnit();
      Assert.assertEquals(LexicalUnit.SAC_INTEGER, unit.getLexicalUnitType());
      Assert.assertEquals(3, unit.getIntegerValue());

      Assert.assertNull(unit.getNextLexicalUnit());
   }

   @Test
   public void beforeAfter() throws Exception {
      final String cssText = "heading:before { content: attr(test) \"testData\" }";

      final CSSOMParser parser = parser();
      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final InputSource source = new InputSource(new StringReader(cssText));
      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) rules.item(0);
      Assert.assertEquals("heading:before { content: attr(test) \"testData\" }", rule.getCssText());

      final CSSStyleDeclaration style = rule.getStyle();

      Assert.assertEquals(1, style.getLength());

      String name = style.item(0);
      name = style.item(0);
      Assert.assertEquals("content : attr(test) \"testData\"", name + " : " + style.getPropertyValue(name));
   }

   @Test
   public void rect() throws Exception {
      final String cssText = "clip: rect( 10px, 20px, 30px, 40px )";

      final CSSOMParser parser = parser();
      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final InputSource source = new InputSource(new StringReader(cssText));
      final CSSStyleDeclaration style = parser.parseStyleDeclaration(source);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      // Enumerate the properties and retrieve their values
      Assert.assertEquals(1, style.getLength());

      String name = style.item(0);
      name = style.item(0);
      Assert.assertEquals("clip : rect(10px, 20px, 30px, 40px)", name + " : " + style.getPropertyValue(name));
   }

   @Test
   public void attr() throws Exception {
      final String cssText = "content: attr( data-foo )";

      final CSSOMParser parser = parser();
      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final InputSource source = new InputSource(new StringReader(cssText));
      final CSSStyleDeclaration style = parser.parseStyleDeclaration(source);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      // Enumerate the properties and retrieve their values
      Assert.assertEquals(1, style.getLength());

      String name = style.item(0);
      name = style.item(0);
      Assert.assertEquals("content : attr(data-foo)", name + " : " + style.getPropertyValue(name));
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/generate.html#counters">
    * http://www.w3.org/TR/CSS21/generate.html#counters</a>
    * @throws Exception if the test fails
    */
   @Test
   public void counter() throws Exception {
      final String css
         = "H1:before        { content: counter(chno, upper-latin) \". \" }\n"
         + "H2:before        { content: counter(section, upper-roman) \" - \" }\n"
         + "BLOCKQUOTE:after { content: \" [\" counter(bq, lower-greek) \"]\" }\n"
         + "DIV.note:before  { content: counter(notecntr, disc) \" \" }\n"
         + "P:before         { content: counter(p, none) }";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(5, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("H1:before { content: counter(chno, upper-latin) \". \" }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      CSSValueImpl value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("content");
      Assert.assertEquals("counter(chno, upper-latin)", ((CSSValueImpl) value.item(0)).getCounterValue().toString());

      rule = rules.item(1);
      Assert.assertEquals("H2:before { content: counter(section, upper-roman) \" - \" }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("content");
      Assert.assertEquals("counter(section, upper-roman)",
         ((CSSValueImpl) value.item(0)).getCounterValue().toString());

      rule = rules.item(2);
      Assert.assertEquals("BLOCKQUOTE:after { content: \" [\" counter(bq, lower-greek) \"]\" }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("content");
      Assert.assertEquals("counter(bq, lower-greek)", ((CSSValueImpl) value.item(1)).getCounterValue().toString());

      rule = rules.item(3);
      Assert.assertEquals("DIV.note:before { content: counter(notecntr, disc) \" \" }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("content");
      Assert.assertEquals("counter(notecntr, disc)", ((CSSValueImpl) value.item(0)).getCounterValue().toString());

      rule = rules.item(4);
      Assert.assertEquals("P:before { content: counter(p, none) }", rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("content");
      Assert.assertEquals("counter(p, none)", value.getCounterValue().toString());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/generate.html#counters">
    * http://www.w3.org/TR/CSS21/generate.html#counters</a>
    * @throws Exception if the test fails
    */
   @Test
   public void counters() throws Exception {
      final String css = "LI:before { content: counters(item, \".\") \" \"; counter-increment: item }";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("LI:before { content: counters(item, \".\") \" \"; counter-increment: item }",
         rule.getCssText());
      Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
      final CSSValueImpl value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("content");
      Assert.assertEquals("counters(item, \".\")", ((CSSValueImpl) value.item(0)).getCounterValue().toString());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#parsing-errors">
    * http://www.w3.org/TR/CSS21/syndata.html#parsing-errors</a>
    * @throws Exception if the test fails
    */
   @Test
   public void unknownProperty() throws Exception {
      final String css = "h1 { color: red; rotation: 70minutes }\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      // parser accepts this
      Assert.assertEquals("h1 { color: red; rotation: 70minutes }", rule.getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#parsing-errors">
    * http://www.w3.org/TR/CSS21/syndata.html#parsing-errors</a>
    * @throws Exception if the test fails
    */
   @Test
   public void illegalValues() throws Exception {
      final String css = "img { float: left } /* correct CSS 2.1 */\n"
         + "img { float: left here } /* 'here' is not a value of 'float' */\n"
         + "img { background: \"red\" } /* keywords cannot be quoted */\n"
         + "img { background: \'red\' } /* keywords cannot be quoted */\n"
         + "img { border-width: 3 } /* a unit must be specified for length values */\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(5, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("img { float: left }", rule.getCssText());

      // parser accepts this
      rule = rules.item(1);
      Assert.assertEquals("img { float: left here }", rule.getCssText());
      rule = rules.item(2);
      Assert.assertEquals("img { background: \"red\" }", rule.getCssText());
      rule = rules.item(3);
      Assert.assertEquals("img { background: \"red\" }", rule.getCssText());
      rule = rules.item(4);
      Assert.assertEquals("img { border-width: 3 }", rule.getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#parsing-errors">
    * http://www.w3.org/TR/CSS21/syndata.html#parsing-errors</a>
    * @throws Exception if the test fails
    */
   @Test
   public void malformedDeclaration() throws Exception {
      final String css = "p { color:green }\n"
         + "p { color:green; background } /* malformed declaration missing ':', value */\n"
         + "p { color:red;   background; visibility:hidden } /* same with expected recovery */\n"
         + "p { color:green; background: } /* malformed declaration missing value */\n"
         + "p { color:red;   background:; visibility:hidden } /* same with expected recovery */\n"
         + "p { color:green; background{;visibility:hidden} } /* unexpected tokens { } */\n"
         + "p { color:red;   background{;visibility:hidden}; display:block } /* same with recovery */\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(7, errorHandler.getErrorCount());
      final String expected = "Error in declaration. (Invalid token \"}\". Was expecting one of: <S>, \":\".)"
         + " Error in declaration. (Invalid token \";\". Was expecting one of: <S>, \":\".)"
         + " Error in expression. (Invalid token \"}\". Was expecting one of: <S>, <NUMBER>, \"inherit\", "
         + "<IDENT>, <STRING>, \"-\", <PLUS>, <HASH>, <EMS>, <EXS>, "
         + "<LENGTH_PX>, <LENGTH_CM>, <LENGTH_MM>, "
         + "<LENGTH_IN>, <LENGTH_PT>, <LENGTH_PC>, <ANGLE_DEG>, <ANGLE_RAD>, <ANGLE_GRAD>, <TIME_MS>, "
         + "<TIME_S>, <FREQ_HZ>, <FREQ_KHZ>, <PERCENTAGE>, <DIMENSION>, <URI>, <FUNCTION>, \"progid:\".)"
         + " Error in expression. (Invalid token \";\". Was expecting one of: <S>, <NUMBER>, \"inherit\", "
         + "<IDENT>, <STRING>, \"-\", <PLUS>, <HASH>, <EMS>, <EXS>, "
         + "<LENGTH_PX>, <LENGTH_CM>, <LENGTH_MM>, "
         + "<LENGTH_IN>, <LENGTH_PT>, <LENGTH_PC>, <ANGLE_DEG>, <ANGLE_RAD>, <ANGLE_GRAD>, <TIME_MS>, "
         + "<TIME_S>, <FREQ_HZ>, <FREQ_KHZ>, <PERCENTAGE>, <DIMENSION>, <URI>, <FUNCTION>, \"progid:\".)"
         + " Error in declaration. (Invalid token \"{\". Was expecting one of: <S>, \":\".)"
         + " Error in style rule. (Invalid token \" \". Was expecting one of: <EOF>, \"}\", \";\".)"
         + " Error in declaration. (Invalid token \"{\". Was expecting one of: <S>, \":\".)";
      Assert.assertEquals(expected, errorHandler.getErrorMessage());
      Assert.assertEquals("2 3 4 5 6 6 7", errorHandler.getErrorLines());
      Assert.assertEquals("29 28 30 29 28 48 28", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(1, errorHandler.getWarningCount());
      Assert.assertTrue(errorHandler.getWarningMessage(),
         errorHandler.getWarningMessage().startsWith("Ignoring the following declarations in this rule."));
      Assert.assertEquals("6", errorHandler.getWarningLines());
      Assert.assertEquals("48", errorHandler.getWarningColumns());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(7, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("p { color: green }", rule.getCssText());

      // parser accepts this
      rule = rules.item(1);
      Assert.assertEquals("p { color: green }", rule.getCssText());
      rule = rules.item(2);
      Assert.assertEquals("p { color: red; visibility: hidden }", rule.getCssText());
      rule = rules.item(3);
      Assert.assertEquals("p { color: green }", rule.getCssText());
      rule = rules.item(4);
      Assert.assertEquals("p { color: red; visibility: hidden }", rule.getCssText());
      rule = rules.item(5);
      Assert.assertEquals("p { color: green }", rule.getCssText());
      rule = rules.item(6);
      Assert.assertEquals("p { color: red; display: block }", rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void malformedDeclarationSkipOnlyDeclaration() throws Exception {
      final String css = "h1 { color: red&; width: 10px; }\n"
         + "h2 { color = red; width: 10px }\n"
         + "h3 { color: red& }\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(3, errorHandler.getErrorCount());
      Assert.assertEquals("1 2 3", errorHandler.getErrorLines());
      Assert.assertEquals("16 12 16", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();
      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("h1 { color: red; width: 10px }", rule.getCssText());
      rule = rules.item(1);
      Assert.assertEquals("h2 { width: 10px }", rule.getCssText());
      rule = rules.item(2);
      Assert.assertEquals("h3 { color: red }", rule.getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#parsing-errors">
    * http://www.w3.org/TR/CSS21/syndata.html#parsing-errors</a>
    * @throws Exception if the test fails
    */
   @Test
   public void malformedStatements() throws Exception {
      final String css = "p { color:green }\n"
         + "p @here {color:red} /* ruleset with unexpected at-keyword '@here' */\n"
         + "@foo @bar; /* at-rule with unexpected at-keyword '@bar' */\n"
         // TODO + "}} {{ - }} /* ruleset with unexpected right brace */\n"
         // TODO + ") ( {} ) p {color: red } /* ruleset with unexpected right parenthesis */\n"
         + "p { color:blue; }\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "Error in style rule. "
         + "(Invalid token \"@here\". Was expecting one of: <S>, <LBRACE>, <COMMA>.)";
      Assert.assertEquals(expected, errorHandler.getErrorMessage());
      Assert.assertEquals("2", errorHandler.getErrorLines());
      Assert.assertEquals("3", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(1, errorHandler.getWarningCount());
      Assert.assertEquals("Ignoring the following declarations in this rule.", errorHandler.getWarningMessage());
      Assert.assertEquals("2", errorHandler.getWarningLines());
      Assert.assertEquals("3", errorHandler.getWarningColumns());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("p { color: green }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("@foo @bar;", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("p { color: blue }", rule.getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#parsing-errors">
    * http://www.w3.org/TR/CSS21/syndata.html#parsing-errors</a>
    * @throws Exception if the test fails
    */
   @Test
   public void atRulesWithUnknownAtKeywords() throws Exception {
      final String css = "@three-dee {\n"
         + "  @background-lighting {\n"
         + "    azimuth: 30deg;\n"
         + "    elevation: 190deg;\n"
         + "  }\n"
         + "  h1 { color: red }\n"
         + "  }\n"
         + "  h1 { color: blue }\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(2, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("@three-dee {\n"
         + "  @background-lighting {\n"
         + "    azimuth: 30deg;\n"
         + "    elevation: 190deg;\n"
         + "  }\n"
         + "  h1 { color: red }\n"
         + "  }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h1 { color: blue }", rule.getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#parsing-errors">
    * http://www.w3.org/TR/CSS21/syndata.html#parsing-errors</a>
    * @throws Exception if the test fails
    */
   @Test
   public void unexpectedEndOfStyleSheet() throws Exception {
      final String css = "@media screen {\n"
         + "  p:before { content: Hello";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "Error in @media rule. (Invalid token \"<EOF>\". Was expecting one of: ";
      Assert.assertTrue(errorHandler.getErrorMessage(), errorHandler.getErrorMessage().startsWith(expected));
      Assert.assertEquals("2", errorHandler.getErrorLines());
      Assert.assertEquals("27", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(1, errorHandler.getWarningCount());
      Assert.assertEquals("Ignoring the whole rule.", errorHandler.getWarningMessage());
      Assert.assertEquals("2", errorHandler.getWarningLines());
      Assert.assertEquals("27", errorHandler.getWarningColumns());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("@media screen {p:before { content: Hello } }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void unexpectedEndOfMediaRule() throws Exception {
      final String css = "@media screen {\n"
         + "  p:before { content: Hello }";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "Error in @media rule. (Invalid token \"<EOF>\". Was expecting one of: ";
      Assert.assertTrue(errorHandler.getErrorMessage(), errorHandler.getErrorMessage().startsWith(expected));
      Assert.assertEquals("2", errorHandler.getErrorLines());
      Assert.assertEquals("29", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(1, errorHandler.getWarningCount());
      Assert.assertEquals("Ignoring the whole rule.", errorHandler.getWarningMessage());
      Assert.assertEquals("2", errorHandler.getWarningLines());
      Assert.assertEquals("29", errorHandler.getWarningColumns());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("@media screen {p:before { content: Hello } }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void unexpectedEndOfPageRule() throws Exception {
      final String css = "@page :pageStyle { size: 21.0cm 29.7cm;";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "Error in @page rule. "
         + "(Invalid token \"<EOF>\". Was expecting one of: <S>, <IDENT>, \"}\", \";\", \"*\".)";
      Assert.assertEquals(expected, errorHandler.getErrorMessage());
      Assert.assertEquals("1", errorHandler.getErrorLines());
      Assert.assertEquals("39", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(1, errorHandler.getWarningCount());
      Assert.assertEquals("Ignoring the whole rule.", errorHandler.getWarningMessage());
      Assert.assertEquals("1", errorHandler.getWarningLines());
      Assert.assertEquals("39", errorHandler.getWarningColumns());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("@page :pageStyle {size: 21cm 29.7cm}", rule.getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#parsing-errors">
    * http://www.w3.org/TR/CSS21/syndata.html#parsing-errors</a>
    *
    * @throws Exception in case of failure
    */
   @Test
   public void unexpectedEndOfString() throws Exception {
      final String css = "p {\n"
         + "  color: green;\n"
         + "  font-family: 'Courier New Times\n"
         + "  visibility: hidden;\n"
         + "  background: green;\n"
         + "}";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "Error in expression. "
         + "(Invalid token \"\\'\". Was expecting one of: <S>, <NUMBER>, \"inherit\", "
         + "<IDENT>, <STRING>, \"-\", <PLUS>, <HASH>, <EMS>, <EXS>, "
         + "<LENGTH_PX>, <LENGTH_CM>, <LENGTH_MM>, "
         + "<LENGTH_IN>, <LENGTH_PT>, <LENGTH_PC>, <ANGLE_DEG>, <ANGLE_RAD>, <ANGLE_GRAD>, <TIME_MS>, "
         + "<TIME_S>, <FREQ_HZ>, <FREQ_KHZ>, <PERCENTAGE>, <DIMENSION>, "
         + "<URI>, <FUNCTION>, \"progid:\".)";
      Assert.assertEquals(expected, errorHandler.getErrorMessage());
      Assert.assertEquals("3", errorHandler.getErrorLines());
      Assert.assertEquals("16", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("p { color: green; background: green }", rule.getCssText());
   }

   /**
    * @throws Exception in case of failure
    */
   @Test
   public void numbersOutsideDeclaration() throws Exception {
      final String css = ".red { color: red }\n"
         + ".invalid {} 0.75in\n"
         + ".blue { color: blue }\n"
         + ".green { color: green }\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "Error in rule. (Invalid token \"0.75\".";
      Assert.assertTrue("'" + errorHandler.getErrorMessage() + "' does not start with '" + expected + "'",
         errorHandler.getErrorMessage().startsWith(expected));
      Assert.assertEquals("2", errorHandler.getErrorLines());
      Assert.assertEquals("13", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(1, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());
      Assert.assertEquals(".red { color: red }", rules.item(0).getCssText());
      Assert.assertEquals(".invalid { }", rules.item(1).getCssText());
      Assert.assertEquals(".green { color: green }", rules.item(2).getCssText());
   }

   /**
    * @throws Exception in case of failure
    */
   @Test
   public void unbalancedRBraceAtEnd() throws Exception {
      final String css = ".red { color: red }\n}";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      final String expected = "Error in rule. (Invalid token \"}\".";
      Assert.assertTrue("'" + errorHandler.getErrorMessage() + "' does not start with '" + expected + "'",
         errorHandler.getErrorMessage().startsWith(expected));
      Assert.assertEquals("2", errorHandler.getErrorLines());
      Assert.assertEquals("1", errorHandler.getErrorColumns());

      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(1, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());
      Assert.assertEquals(".red { color: red }", rules.item(0).getCssText());
   }

   /**
    * @throws Exception in case of failure
    */
   @Test
   public void whitespaceAtEnd() throws Exception {
      final String css = ".red { color: red }\n   \t \r\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSStyleSheet sheet = parse(source, 0, 0, 0);

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());
      Assert.assertEquals(".red { color: red }", rules.item(0).getCssText());
   }

   /**
    * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#strings">
    * http://www.w3.org/TR/CSS21/syndata.html#strings</a>
    *
    * @throws Exception in case of failure
    */
   @Test
   public void strings() throws Exception {
      final String css = "h1 { background: url(\"this is a 'string'\") }\n"
         + "h2 { background: url(\"this is a \\\"string\\\"\") }\n"
         + "h4 { background: url('this is a \"string\"') }\n"
         + "h5 { background: url('this is a \\'string\\'') }"
         + "h6 { background: url('this is a \\\r\n string') }"
         + "h1:before { content: 'chapter\\A hoofdstuk\\00000a chapitre' }";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(6, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("h1 { background: url(this is a 'string') }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h2 { background: url(this is a \"string\") }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("h4 { background: url(this is a \"string\") }", rule.getCssText());

      rule = rules.item(3);
      Assert.assertEquals("h5 { background: url(this is a 'string') }", rule.getCssText());

      rule = rules.item(4);
      Assert.assertEquals("h6 { background: url(this is a  string) }", rule.getCssText());

      rule = rules.item(5);
      Assert.assertEquals("h1:before { content: \"chapter\\A hoofdstuk\\A  chapitre\" }", rule.getCssText());
   }

   /**
    * @throws Exception in case of failure
    */
   @Test
   public void emptyUrl() throws Exception {
      final String css = "h1 { background: url() }\n"
         + "h2 { background: url(\"\") }\n"
         + "h4 { background: url('') }\n"
         + "h5 { background: url( ) }";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(4, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("h1 { background: url() }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h2 { background: url() }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("h4 { background: url() }", rule.getCssText());

      rule = rules.item(3);
      Assert.assertEquals("h5 { background: url() }", rule.getCssText());
   }

   /**
    * Regression test for bug 1420893.
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void invalidCommaInDef() throws Exception {
      final String css = ".a, .b, { test: 1; }";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(1, errorHandler.getWarningCount());

      Assert.assertTrue(errorHandler.getErrorMessage(),
         errorHandler.getErrorMessage().startsWith("Error in simple selector."));
      Assert.assertEquals("Ignoring the whole rule.", errorHandler.getWarningMessage());
   }

   /**
    * Regression test for bug 1420893.
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void missingValue() throws Exception {
      final String css = ".a { test; }\n"
         + "p { color: green }";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      Assert.assertTrue(errorHandler.getErrorMessage(),
         errorHandler.getErrorMessage().startsWith("Error in declaration."));

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(2, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals(".a { }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("p { color: green }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void skipDeklarationsErrorBefore() throws Exception {
      final String css = ".a { test; color: green }";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      Assert.assertTrue(errorHandler.getErrorMessage(),
         errorHandler.getErrorMessage().startsWith("Error in declaration."));

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals(".a { color: green }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void skipDeklarationsErrorBetween() throws Exception {
      final String css = ".a { color: blue; test; background: green }";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(1, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      Assert.assertTrue(errorHandler.getErrorMessage(),
         errorHandler.getErrorMessage().startsWith("Error in declaration."));

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals(".a { color: blue; background: green }", rule.getCssText());
   }

   /**
    * see https://issues.jboss.org/browse/RF-11741
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void skipJBossIssue() throws Exception {
      final String css = ".shadow {\n"
         + " -webkit-box-shadow: 1px 4px 5px '#{richSkin.additionalBackgroundColor}';"
         + " -moz-box-shadow: 2px 4px 5px '#{richSkin.additionalBackgroundColor}';"
         + " box-shadow: 3px 4px 5px '#{richSkin.additionalBackgroundColor}';"
         + "}";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      final CSSStyleRuleImpl ruleImpl = (CSSStyleRuleImpl) rule;
      final CSSStyleDeclarationImpl declImpl = (CSSStyleDeclarationImpl) ruleImpl.getStyle();

      Property prop = declImpl.getPropertyDeclaration("-webkit-box-shadow");
      CSSValueImpl valueImpl = (CSSValueImpl) prop.getValue();
      Assert.assertEquals("1px 4px 5px \"#{richSkin.additionalBackgroundColor}\"", valueImpl.getCssText());

      prop = declImpl.getPropertyDeclaration("-moz-box-shadow");
      valueImpl = (CSSValueImpl) prop.getValue();
      Assert.assertEquals("2px 4px 5px \"#{richSkin.additionalBackgroundColor}\"", valueImpl.getCssText());

      prop = declImpl.getPropertyDeclaration("box-shadow");
      valueImpl = (CSSValueImpl) prop.getValue();
      Assert.assertEquals("3px 4px 5px \"#{richSkin.additionalBackgroundColor}\"", valueImpl.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionPercent() throws Exception {
      final CSSValueImpl value = dimension("2%");
      Assert.assertEquals(CSSPrimitiveValue.CSS_PERCENTAGE, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionPX() throws Exception {
      final CSSValueImpl value = dimension("3px");
      Assert.assertEquals(CSSPrimitiveValue.CSS_PX, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionCM() throws Exception {
      final CSSValueImpl value = dimension("5cm");
      Assert.assertEquals(CSSPrimitiveValue.CSS_CM, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionMM() throws Exception {
      final CSSValueImpl value = dimension("7mm");
      Assert.assertEquals(CSSPrimitiveValue.CSS_MM, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionIN() throws Exception {
      final CSSValueImpl value = dimension("11in");
      Assert.assertEquals(CSSPrimitiveValue.CSS_IN, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionPT() throws Exception {
      final CSSValueImpl value = dimension("13pt");
      Assert.assertEquals(CSSPrimitiveValue.CSS_PT, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionEMS() throws Exception {
      final CSSValueImpl value = dimension("17em");
      Assert.assertEquals(CSSPrimitiveValue.CSS_EMS, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionEXS() throws Exception {
      final CSSValueImpl value = dimension("19ex");
      Assert.assertEquals(CSSPrimitiveValue.CSS_EXS, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionDEG() throws Exception {
      final CSSValueImpl value = dimension("13deg");
      Assert.assertEquals(CSSPrimitiveValue.CSS_DEG, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionRAD() throws Exception {
      final CSSValueImpl value = dimension("99rad");
      Assert.assertEquals(CSSPrimitiveValue.CSS_RAD, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionGRAD() throws Exception {
      final CSSValueImpl value = dimension("31grad");
      Assert.assertEquals(CSSPrimitiveValue.CSS_GRAD, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionMS() throws Exception {
      final CSSValueImpl value = dimension("37ms");
      Assert.assertEquals(CSSPrimitiveValue.CSS_MS, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionS() throws Exception {
      final CSSValueImpl value = dimension("41s");
      Assert.assertEquals(CSSPrimitiveValue.CSS_S, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionHZ() throws Exception {
      final CSSValueImpl value = dimension("43Hz");
      Assert.assertEquals(CSSPrimitiveValue.CSS_HZ, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionKHZ() throws Exception {
      final CSSValueImpl value = dimension("47kHz");
      Assert.assertEquals(CSSPrimitiveValue.CSS_KHZ, value.getPrimitiveType());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void dimensionPC() throws Exception {
      dimension("5pc");
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void illegalDimension() throws Exception {
      final String css = ".a { top: 0\\9; }"
         + ".b { top: -01.234newDim; }";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(2, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals(".a { top: 0\\9 }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals(".b { top: -1.234newDim }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void gradientIE6Style() throws Exception {
      final String css = ".a {\n"
         + "filter: progid:DXImageTransform.Microsoft."
         + "gradient(GradientType=0, startColorstr=#6191bf, endColorstr=#cde6f9);\n"
         + "color: green;\n"
         + "}\n"
         + ".img {filter: progid:DXImageTransform.Microsoft.gradient(GradientType=0)}\n"
         + ".h1 {filter: progid:DXImageTransform.Microsoft.gradient(GradientType=1) \t }\n"
         + ".h1 {filter: progid: }\n";

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(4, rules.getLength());

      CSSRule rule = rules.item(0);
      CSSValueImpl value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("filter");
      Assert.assertEquals("progid:DXImageTransform.Microsoft.gradient("
         + "GradientType=0, startColorstr=#6191bf, endColorstr=#cde6f9)", value.getCssText());
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("color");
      Assert.assertEquals("green", value.getCssText());
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("color");
      Assert.assertEquals("green", value.getCssText());

      rule = rules.item(1);
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("filter");
      Assert.assertEquals("progid:DXImageTransform.Microsoft.gradient(GradientType=0)", value.getCssText());

      rule = rules.item(2);
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("filter");
      Assert.assertEquals("progid:DXImageTransform.Microsoft.gradient(GradientType=1)", value.getCssText());

      rule = rules.item(3);
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("filter");
      Assert.assertEquals("progid:", value.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void gradientIE8Style() throws Exception {
      final String css = ".a {\n"
         + "-ms-filter: \"progid:DXImageTransform.Microsoft."
         + "gradient(GradientType=0, startColorstr=#6191bf, endColorstr=#cde6f9)\";\n"
         + "color: green;"
         + "}";
      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);

      CSSValueImpl value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("-ms-filter");
      Assert.assertEquals("\"progid:DXImageTransform.Microsoft."
         + "gradient(GradientType=0, startColorstr=#6191bf, endColorstr=#cde6f9)\"", value.getCssText());

      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("color");
      Assert.assertEquals("green", value.getCssText());
   }

   /**
    * @throws Exception in case of failure
    */
   @Test
   public void fontContainingForwardSlash() throws Exception {
      final String css = "p { font:normal normal normal 14px/11 FontAwesome; }";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);

      final CSSValueImpl value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("font");
      Assert.assertEquals("normal normal normal 14px / 11 FontAwesome", value.getCssText());

      Assert.assertEquals(CSSValue.CSS_VALUE_LIST, value.getCssValueType());
      Assert.assertEquals(CSSPrimitiveValue.CSS_UNKNOWN, value.getPrimitiveType());

      Assert.assertEquals(7, value.getLength());
      Assert.assertEquals("normal", value.item(0).getCssText());
      Assert.assertEquals("normal", value.item(1).getCssText());
      Assert.assertEquals("normal", value.item(2).getCssText());
      Assert.assertEquals("14px", value.item(3).getCssText());
      Assert.assertEquals("/", value.item(4).getCssText());
      Assert.assertEquals("11", value.item(5).getCssText());
      Assert.assertEquals("FontAwesome", value.item(6).getCssText());
   }

   @Test
   public void transformRotate() throws Exception {
      final String css = ".flipped {\n"
         + "  transform: rotateY(180deg);\n"
         + "}";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);

      final CSSValueImpl value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("transform");
      Assert.assertEquals("rotateY(180deg)", value.getCssText());
   }

   @Test
   public void rgba() throws Exception {
      final String css = "p {\n"
         + "  background-color: rgba(0,0,0,0.2);\n"
         + "}";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);

      final CSSValueImpl value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().
         getPropertyCSSValue("background-color");
      Assert.assertEquals("rgba(0, 0, 0, 0.2)", value.getCssText());

      Assert.assertEquals(CSSPrimitiveValue.CSS_PRIMITIVE_VALUE, value.getCssValueType());
      Assert.assertEquals(CSSPrimitiveValue.CSS_STRING, value.getPrimitiveType());
      Assert.assertEquals("rgba(0, 0, 0, 0.2)", value.getStringValue());
   }

   @Test
   public void linearGradient() throws Exception {
      final String css = "h1 { background: linear-gradient(top, #fff, #f2f2f2); }\n"
         + "h2 { background: linear-gradient( 45deg, blue, red ); }\n"
         + "h3 { background: linear-gradient( to left top, #00f, red); }\n"
         + "h4 { background: linear-gradient( 0 deg, blue, green 40%, red ); }\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(4, rules.getLength());

      CSSRule rule = rules.item(0);
      CSSValueImpl value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("background");
      Assert.assertEquals("linear-gradient(top, rgb(255, 255, 255), rgb(242, 242, 242))", value.getCssText());

      rule = rules.item(1);
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("background");
      Assert.assertEquals("linear-gradient(45deg, blue, red)", value.getCssText());

      rule = rules.item(2);
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("background");
      Assert.assertEquals("linear-gradient(to left top, rgb(0, 0, 255), red)", value.getCssText());

      rule = rules.item(3);
      value = (CSSValueImpl) ((CSSStyleRule) rule).getStyle().getPropertyCSSValue("background");
      Assert.assertEquals("linear-gradient(0 deg, blue, green 40%, red)", value.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void selectorTrimWhitespace() throws Exception {
      final String cssText = "  \t\r\n  div > hi  \t\r\n  ";
      final SelectorList selectors = createSelectors(cssText);
      Assert.assertEquals(cssText.trim(), selectors.item(0).toString());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void twoPseudo() throws Exception {
      SelectorList selectors = createSelectors("input:lang(en):lang(de)");
      Assert.assertEquals("input:lang(en):lang(de)", selectors.item(0).toString());

      selectors = createSelectors("input:foo(test):foo(rest)");
      Assert.assertEquals("input:foo(test):foo(rest)", selectors.item(0).toString());

      selectors = createSelectors("input:foo(test):before");
      Assert.assertEquals("input:foo(test):before", selectors.item(0).toString());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void pseudoElementsErrors() throws Exception {
      CSSConfig.setDebugMode(CSSConfig.SIGNAL_ERRORS);
      // two pseudo elements
      checkErrorSelector("input:before:after", "Duplicate pseudo class \":after\" "
         + "or pseudo class \":after\" not at end.");
      checkErrorSelector("input:before:lang(de)",
         "Duplicate pseudo class \":lang(de)\" or pseudo class \":lang(de)\" not at end.");
      checkErrorSelector("input:before:foo(ab)",
         "Duplicate pseudo class \":foo(ab)\" or pseudo class \":foo(ab)\" not at end.");
      checkErrorSelector("input:before:",
         "Error in pseudo class or element. (Invalid token \"<EOF>\". "
         + "Was expecting one of: <IDENT>, <FUNCTION_LANG>, <FUNCTION>.)");

      // pseudo element not at end
      checkErrorSelector("input:before:not(#test)",
         "Error in pseudo class or element. "
         + "(Invalid token \"#test\". Was expecting one of: <S>, <IDENT>, \")\".)");
      checkErrorSelector("input:before[type='file']",
         "Error in attribute selector. (Invalid token \"type\". Was expecting: <S>.)");
      checkErrorSelector("input:before.styleClass", "Error in class selector. "
         + "(Invalid token \"\". Was expecting one of: .)");
      checkErrorSelector("input:before#hash", "Error in hash. (Invalid token \"\". Was expecting one of: .)");
   }

   /**
    * The CDO (<!--) and CDC (-->) symbols may appear in certain locations of a stylesheet. In other locations, they should cause parts of
    * the stylesheet to be ignored.
    *
    * @see <a href="http://www.hixie.ch/tests/evil/mixed/cdocdc.html">
    * http://www.hixie.ch/tests/evil/mixed/cdocdc.html</a>
    * @see <a href="https://test.csswg.org/suites/css2.1/20101027/html4/sgml-comments-002.htm">
    * https://test.csswg.org/suites/css2.1/20101027/html4/sgml-comments-002.htm</a>
    * @throws Exception if any error occurs
    */
   @Test
   public void cdoCdc() throws Exception {
      final String css = "\n"
         + "    OL { list-style-type: lower-alpha; }\n"
         + "\n"
         + "<!--\n"
         + "\n"
         + "    .a { color: green; background: white none; }\n"
         + "<!--.b { color: green; background: white none; } --> <!-- --> <!--\n"
         + "    .c { color: green; background: white none; }\n"
         + "\n"
         + "<!--\n"
         + ".d { color: green; background: white none; }\n"
         + "-->\n"
         + "\n"
         + "    .e { color: green; background: white none; }\n"
         + "\n"
         + "\n"
         + "    <!--    .f { color: green; background: white none; }-->\n"
         + "-->.g { color: green; background: white none; }<!--\n"
         + "    .h { color: green; background: white none; }\n"
         + "-->-->-->-->-->-->.i { color: green; background: white none; }-->-->-->-->\n"
         + "\n"
         + "<!-- .j { color: green; background: white none; } -->\n"
         + "\n"
         + "<!--\n"
         + "     .k { color: green; background: white none; }\n"
         + "-->\n"
         + "\n"
         + "    .xa <!-- { color: yellow; background: red none; }\n"
         + "\n"
         + "    .xb { color: yellow -->; background: red none <!--; }\n"
         + "\n"
         + "    .xc { <!-- color: yellow; --> background: red none; }\n"
         + "\n"
         + "    .xd { <!-- color: yellow; background: red none -->; }\n"
         + "\n"
         + " <! -- .xe { color: yellow; background: red none; }\n"
         + "\n"
         + "--> <!--       --> <!-- -- >\n"
         + "\n"
         + "  .xf { color: yellow; background: red none; }\n";
      final CSSStyleSheet sheet = parse(css, 6, 0, 6);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(15, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("OL { list-style-type: lower-alpha }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals(".a { color: green; background: white none }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals(".a { color: green; background: white none }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals(".b { color: green; background: white none }", rule.getCssText());

      rule = rules.item(3);
      Assert.assertEquals(".c { color: green; background: white none }", rule.getCssText());

      rule = rules.item(4);
      Assert.assertEquals(".d { color: green; background: white none }", rule.getCssText());

      rule = rules.item(5);
      Assert.assertEquals(".e { color: green; background: white none }", rule.getCssText());

      rule = rules.item(6);
      Assert.assertEquals(".f { color: green; background: white none }", rule.getCssText());

      rule = rules.item(7);
      Assert.assertEquals(".g { color: green; background: white none }", rule.getCssText());

      rule = rules.item(8);
      Assert.assertEquals(".h { color: green; background: white none }", rule.getCssText());

      rule = rules.item(9);
      Assert.assertEquals(".i { color: green; background: white none }", rule.getCssText());

      rule = rules.item(10);
      Assert.assertEquals(".j { color: green; background: white none }", rule.getCssText());

      rule = rules.item(11);
      Assert.assertEquals(".k { color: green; background: white none }", rule.getCssText());

      rule = rules.item(12);
//         Assert.assertEquals(".xb { }", rule.getCssText());

      rule = rules.item(13);
      Assert.assertEquals(".xc { }", rule.getCssText());

      rule = rules.item(14);
      Assert.assertEquals(".xd { }", rule.getCssText());
   }

   /**
    * Empty declaration
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void emptyDeclaration() throws Exception {
      final String css = "p {  }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("p { }", rule.getCssText());
   }

   /**
    * Empty declaration only semicolon
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void emptyDeclarationSemicolon() throws Exception {
      final String css = "p { ; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("p { }", rule.getCssText());
   }

   /**
    * Empty declaration only some semicolon
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void emptyDeclarationManySemicolon() throws Exception {
      final String css = "p { ; ; \t     ; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("p { }", rule.getCssText());
   }

   /**
    * Empty declaration only comment
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void emptyDeclarationComment() throws Exception {
      final String css = "p { /* background: white; */ }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("p { }", rule.getCssText());
   }

   /**
    * Comments
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void comment() throws Exception {
      final String css = "p { color: red; /* background: white; */ background: green; }";
      final CSSStyleSheet sheet = parse(css, 0, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("p { color: red; background: green }", rule.getCssText());
   }

   /**
    * Comments
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void commentNotClosed() throws Exception {
      CSSConfig.setDebugMode(CSSConfig.SIGNAL_ERRORS);
      final String css = "p { color: red; /* background: white; }"
         + "h1 { color: blue; }";
      final CSSStyleSheet sheet = parse(css, 1, 0, 0);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("p { color: red }", rule.getCssText());
   }

   /**
    * Handle the famous star hack as smart as possible.
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void starHackFirst() throws Exception {
      String css = "p { *color: red; background: white; }"
         + "h1 { color: blue; }";
      CSSStyleSheet sheet = parse(css, 1, 0, 0);

      CSSRuleList rules = sheet.getCssRules();
      Assert.assertEquals(2, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("p { background: white }", rule.getCssText());

      css = "p { color: red; *background: white }"
         + "h1 { color: blue; }";
      sheet = parse(css, 1, 0, 0);

      rules = sheet.getCssRules();
      Assert.assertEquals(2, rules.getLength());

      rule = rules.item(0);
      Assert.assertEquals("p { color: red }", rule.getCssText());
   }

   /**
    * Star Hack support
    *
    * @throws Exception if any error occurs
    */
   @Test
   public void starHackSupport() throws Exception {
      final String css = "p { color: blue; *color: red; background: white; }";
      final SACParser sacParser = sacParser();
      sacParser.setIeStarHackAccepted(true);

      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = new CSSOMParser(sacParser);
      final ErrorHandler errorHandler = new ErrorHandler();
      parser.setErrorHandler(errorHandler);

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);

      Assert.assertEquals(0, errorHandler.getErrorCount());
      Assert.assertEquals(0, errorHandler.getFatalErrorCount());
      Assert.assertEquals(0, errorHandler.getWarningCount());

      final CSSRuleList rules = sheet.getCssRules();
      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("p { color: blue; *color: red; background: white }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void unicode() throws Exception {
      unicode("@p\\41ge :pageStyle {}", "@page :pageStyle {}");
      unicode("@p\\041ge :pageStyle {}", "@page :pageStyle {}");
      unicode("@p\\0041ge :pageStyle {}", "@page :pageStyle {}");
      unicode("@p\\00041ge :pageStyle {}", "@page :pageStyle {}");
      unicode("@p\\000041ge :pageStyle {}", "@page :pageStyle {}");

      // \\0000041 - fails
      unicode("@p\\0000041ge :pageStyle {}", "@p\\0000041ge :pageStyle {}");

      // terminated by whitespace
      unicode("@\\0070 age :pageStyle {}", "@page :pageStyle {}");
      unicode("@\\0070\tage :pageStyle {}", "@page :pageStyle {}");
      unicode("@\\0070\r\nage :pageStyle {}", "@page :pageStyle {}");

      // terminated by lenght
      unicode("@\\000070age :pageStyle {}", "@page :pageStyle {}");

      // backslash ignored
      unicode("@\\page :pageStyle {}", "@page :pageStyle {}");
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void unicodeEscaping() throws Exception {
      unicode("@media paper\\7b { }", "@media paper{ {}");
      unicode(".class\\7b { color: blue }", ".class{ { color: blue }");
      unicode("@page :pseu\\64o { color: blue }", "@page :pseudo {color: blue}");
      unicode("h1:first-l\\69ne { color: blue }", "h1:first-line { color: blue }");
      unicode(".cls { color: blu\\65 }", ".cls { color: blue }");
   }

   private void unicode(final String css, final String expected) throws IOException {
      final InputSource source = new InputSource(new StringReader(css));
      final CSSOMParser parser = parser();

      final CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());
      final CSSRule rule = rules.item(0);
      Assert.assertEquals(expected, rule.getCssText());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void commaSeparatedTerms() throws Exception {
      final String css = "h1 { box-shadow: inset 0 0 1em gold, 0 0 1em red }\n"
         + "h2 { box-shadow: 0 0 0 1px #fff, 0 0 0 1px silver }\n"
         + "h3 { box-shadow: 0 0 0 1px #fff / 0 0 0 1px silver }\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(3, rules.getLength());

      CSSRule rule = rules.item(0);
      Assert.assertEquals("h1 { box-shadow: inset 0 0 1em gold, 0 0 1em red }", rule.getCssText());

      rule = rules.item(1);
      Assert.assertEquals("h2 { box-shadow: 0 0 0 1px rgb(255, 255, 255), 0 0 0 1px silver }", rule.getCssText());

      rule = rules.item(2);
      Assert.assertEquals("h3 { box-shadow: 0 0 0 1px rgb(255, 255, 255) / 0 0 0 1px silver }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void errorAtStart() throws Exception {
      final String css = "// comments \n"
         + "h1 { color: red; }"
         + "h2 { color: blue; }";
      final CSSStyleSheet sheet = parse(css, 1, 0, 1);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("h2 { color: blue }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void errorAtStart2() throws Exception {
      final String css = "& test\n"
         + "h1 { color: red; }"
         + "h2 { color: blue; }";
      final CSSStyleSheet sheet = parse(css, 1, 0, 1);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("h2 { color: blue }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void errorAtStartOnly() throws Exception {
      final String css = "// comments";
      final CSSStyleSheet sheet = parse(css, 1, 0, 1);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(0, rules.getLength());
   }

   /**
    * @throws Exception if the test fails
    */
   @Test
   public void pseudoElement() throws Exception {
      final String css = "h1:first-line { color: red }\n";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("h1:first-line { color: red }", rule.toString());
      Assert.assertEquals("h1:first-line { color: red }", rule.getCssText());
   }

   /**
    * @throws Exception if any error occurs
    */
   @Test
   public void unknownRule() throws Exception {
      final String css = "@keyframes load5 {0%, 100% {"
         + "box-shadow: 0em -2.6em 0em 0em #000000, 1.8em -1.8em 0 0em rgba(0, 0, 0, 0.2), "
         + "2.5em 0em 0 0em rgba(0, 0, 0, 0.2), 1.75em 1.75em 0 0em rgba(0, 0, 0, 0.2), "
         + "0em 2.5em 0 0em rgba(0, 0, 0, 0.2), -1.8em 1.8em 0 0em rgba(0, 0, 0, 0.2), "
         + "-2.6em 0em 0 0em rgba(0, 0, 0, 0.5), -1.8em -1.8em 0 0em rgba(0, 0, 0, 0.7);"
         + "}";

      final CSSStyleSheet sheet = parse(css);
      final CSSRuleList rules = sheet.getCssRules();

      Assert.assertEquals(1, rules.getLength());

      final CSSRule rule = rules.item(0);
      Assert.assertEquals("@keyframes load5 {0%, 100% {"
         + "box-shadow: 0em -2.6em 0em 0em #000000, 1.8em -1.8em 0 0em rgba(0, 0, 0, 0.2), "
         + "2.5em 0em 0 0em rgba(0, 0, 0, 0.2), 1.75em 1.75em 0 0em rgba(0, 0, 0, 0.2), "
         + "0em 2.5em 0 0em rgba(0, 0, 0, 0.2), -1.8em 1.8em 0 0em rgba(0, 0, 0, 0.2), "
         + "-2.6em 0em 0 0em rgba(0, 0, 0, 0.5), -1.8em -1.8em 0 0em rgba(0, 0, 0, 0.7);"
         + "}", rule.toString());
      Assert.assertEquals("@keyframes load5 {0%, 100% {"
         + "box-shadow: 0em -2.6em 0em 0em #000000, 1.8em -1.8em 0 0em rgba(0, 0, 0, 0.2), "
         + "2.5em 0em 0 0em rgba(0, 0, 0, 0.2), 1.75em 1.75em 0 0em rgba(0, 0, 0, 0.2), "
         + "0em 2.5em 0 0em rgba(0, 0, 0, 0.2), -1.8em 1.8em 0 0em rgba(0, 0, 0, 0.2), "
         + "-2.6em 0em 0 0em rgba(0, 0, 0, 0.5), -1.8em -1.8em 0 0em rgba(0, 0, 0, 0.7);"
         + "}", rule.getCssText());
   }

//
//    /**
//     * @throws Exception if any error occurs
//     */
//    @Test
//    public void fakeSingleLineCommentBetweenRules() throws Exception {
//        final String css =
//                "h1 { color: red; }"
//                + "// comment"
//                + "h2 { color: blue; }";
//        final CSSStyleSheet sheet = parse(css, 1, 0, 1);
//        final CSSRuleList rules = sheet.getCssRules();
//
//        Assert.assertEquals(1, rules.getLength());
//
//        final CSSRule rule = rules.item(0);
//        Assert.assertEquals("p { color: red; background: green }", rule.getCssText());
//    }
//
//    /**
//     * @throws Exception if any error occurs
//     */
//    @Test
//    public void fakeSingleLineCommentInsideRule() throws Exception {
//        final String css =
//                "h1 {\n"
//                + " color: red; \n"
//                + "  // comment \n"
//                + " background: blue;\n"
//                + " height: 20px;\n"
//                + "}";
//        final CSSStyleSheet sheet = parse(css, 1, 0, 1);
//        final CSSRuleList rules = sheet.getCssRules();
//
//        Assert.assertEquals(1, rules.getLength());
//
//        final CSSRule rule = rules.item(0);
//        Assert.assertEquals("p { color: red; background: green }", rule.getCssText());
//    }
//
//    /**
//     * @throws Exception if any error occurs
//     */
//    @Test
//    public void fakeSingleLineCommentBeforeFirstRule() throws Exception {
//        final String css =
//                "h1 {\n"
//                + "  // comment \n"
//                + " background: blue;\n"
//                + " height: 20px;\n"
//                + "}";
//        final CSSStyleSheet sheet = parse(css, 1, 0, 1);
//        final CSSRuleList rules = sheet.getCssRules();
//
//        Assert.assertEquals(1, rules.getLength());
//
//        final CSSRule rule = rules.item(0);
//        Assert.assertEquals("h1 { height: 20px }", rule.getCssText());
//    }
//
//    /**
//     * @throws Exception if any error occurs
//     */
//    @Test
//    public void fakeSingleLineCommentAsLastRule() throws Exception {
//        final String css =
//                "h1 {\n"
//                + " color: red; \n"
//                + " background: blue;\n"
//                + " height: 20px;\n"
//                + "  // comment \n"
//                + "}";
//        final CSSStyleSheet sheet = parse(css, 1, 0, 1);
//        final CSSRuleList rules = sheet.getCssRules();
//
//        Assert.assertEquals(1, rules.getLength());
//
//        final CSSRule rule = rules.item(0);
//        Assert.assertEquals("h1 { color: red; height: 20px }", rule.getCssText());
//    }
}
