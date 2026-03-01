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

package com.steadystate.css;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.CSSVersion;
import com.steadystate.css.parser.SACParserCSS2;

/**
 * Tests the CSS DOM implementation by loading a stylesheet and performing a few operations upon it.
 *
 * @author David Schweinsberg
 * @author rbri
 */
public class Dom2Test {

    @Test
    public void test() throws Exception {
        final InputStream is = Dom2Test.class.getResourceAsStream("test.css");
        Assert.assertNotNull(is);

        final Reader r = new InputStreamReader(is);
        final InputSource source = new InputSource(r);

        // set CSS2 parser because this test contains test for features
        // no longer supported by CSS21new SACParserCSS2(
        final CSSOMParser parser = new CSSOMParser(CSSVersion.CSS2);
        final ErrorHandler errorHandler = new ErrorHandler();
        parser.setErrorHandler(errorHandler);
        final CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);

        Assert.assertEquals(3, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(3, errorHandler.getWarningCount());

        final CSSRuleList rules = stylesheet.getCssRules();
        Assert.assertEquals(78, rules.getLength());

        CSSRule rule = rules.item(9);
        Assert.assertEquals("H1, H2 { color: green; background-color: blue }", rule.getCssText());

        // Do some modifications and output the results

        // Style Rules
        rule = rules.item(9);
        rule.setCssText("apple { color: green }");
        Assert.assertEquals("apple { color: green }", rule.getCssText());

        ((CSSStyleRule) rule).setSelectorText("banana");
        Assert.assertEquals("banana { color: green }", rule.getCssText());

        ((CSSStyleRule) rule).setSelectorText("orange tangerine, grapefruit");
        Assert.assertEquals("orange tangerine, grapefruit { color: green }", rule.getCssText());

        ((CSSStyleRule) rule).getStyle().setCssText(
            "color: red green brown; smell: sweet, sour; taste: sweet/tart");
        Assert.assertEquals(
                "orange tangerine, grapefruit { color: red green brown; smell: sweet, sour; taste: sweet / tart }",
                rule.getCssText());

        rule = rules.item(0);
        Assert.assertEquals("@import url(foo.css);", rule.getCssText());

        // Import rules
        stylesheet.insertRule("@import \"thing.css\";", 0);
        Assert.assertEquals(79, rules.getLength());
        rule = rules.item(0);
        Assert.assertEquals("@import url(thing.css);", rule.getCssText());

        ((CSSImportRule) rule).setCssText("@import \"thing-hack.css\";");
        rule = rules.item(0);
        Assert.assertEquals("@import url(thing-hack.css);", rule.getCssText());

        // Font-face rules
        stylesheet.insertRule("@font-face { src: \"#foo\" }", 10);
        Assert.assertEquals(80, rules.getLength());
        rule = rules.item(10);
        Assert.assertEquals("@font-face {src: \"#foo\"}", rule.getCssText());

        ((CSSFontFaceRule) rules.item(10)).setCssText("@font-face { src: \"#bar\" }");
        Assert.assertEquals("@font-face {src: \"#bar\"}", rule.getCssText());
    }
}
