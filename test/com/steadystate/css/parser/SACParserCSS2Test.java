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

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.InputSource;
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
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.parser.selectors.ChildSelectorImpl;
import com.steadystate.css.parser.selectors.ConditionalSelectorImpl;
import com.steadystate.css.parser.selectors.LangConditionImpl;
import org.junit.BeforeClass;

/**
 * @author rbri
 */
public class SACParserCSS2Test extends AbstractSACParserTestUtils {
   
   @BeforeClass
   public static void setUpClass() {
      ParserConfiguration.getInstance().setVersion(CSSVersion.CSS2);
   }

   @Override
   protected SACParser sacParser() {
      return CSSOMParser.newParser();
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
    * @see <a href="http://www.w3.org/TR/CSS2/selector.html#lang">http://www.w3.org/TR/CSS2/selector.html#lang</a>
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
    * @see <a href="http://www.w3.org/TR/CSS2/selector.html#lang">http://www.w3.org/TR/CSS2/selector.html#lang</a>
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
      conditionAssert("[rel=val]", "rel", "val", true);
      Assert.assertNull(createSelectors("[rel=]")); // invalid rule
      conditionAssert("[rel~=val]", "rel", "val", true);
      Assert.assertNull(createSelectors("[rel~=]")); // invalid rule
      conditionAssert("[rel|=val]", "rel", "val", true);
      Assert.assertNull(createSelectors("[rel|=]")); // invalid rule
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
         + "Was expecting one of: \"lang(\", <FUNCTION>, <IDENT>.)");

      // pseudo element not at end
      checkErrorSelector("input:before:not(#test)",
         "Error in pseudo class or element. (Invalid token \"#test\". Was expecting one of: <S>, <IDENT>.)");
      checkErrorSelector("input:before[type='file']",
         "Error in attribute selector. (Invalid token \"type\". Was expecting: <S>.)");
      checkErrorSelector("input:before.styleClass", "Error in class selector. "
         + "(Invalid token \"\". Was expecting one of: .)");
      checkErrorSelector("input:before#hash", "Error in hash. (Invalid token \"\". Was expecting one of: .)");
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
    * @see <a href="http://www.w3.org/TR/CSS2/generate.html#counters">
    * http://www.w3.org/TR/CSS2/generate.html#counters</a>
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
    * @see <a href="http://www.w3.org/TR/CSS2/generate.html#counters">
    * http://www.w3.org/TR/CSS2/generate.html#counters</a>
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
      CSSConfig.setDebugMode(CSSConfig.SIGNAL_ERRORS);
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
      final CSSStyleSheet sheet = parse(css, 5, 0, 4);
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
}
