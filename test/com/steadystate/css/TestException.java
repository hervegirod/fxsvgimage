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

import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.CSSVersion;

/**
 * Attempts to perform some illegal operations to ensure the correct exceptions are thrown.
 *
 * @author David Schweinsberg
 * @author rbri
 */
public class TestException {

    @Test
    public void test() throws Exception {
        final CSSOMParser parser = new CSSOMParser(CSSVersion.CSS21);
        final ErrorHandler errorHandler = new ErrorHandler();
        parser.setErrorHandler(errorHandler);

        final Reader r = new StringReader("");
        final InputSource source = new InputSource(r);
        final CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);

        Assert.assertEquals(0, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(0, errorHandler.getWarningCount());

        stylesheet.insertRule("P { color: blue }", 0);
        stylesheet.insertRule("@import url(http://www.steadystate.com/primary.css);", 0);
        stylesheet.insertRule("@charset \"US-ASCII\";", 0);

        final CSSRuleList rules = stylesheet.getCssRules();
        Assert.assertEquals(3, rules.getLength());

        Assert.assertEquals("@charset \"US-ASCII\";", rules.item(0).getCssText());
        Assert.assertEquals("@import url(http://www.steadystate.com/primary.css);", rules.item(1).getCssText());
        Assert.assertEquals("P { color: blue }", rules.item(2).getCssText());

        stylesheet.deleteRule(1);

        Assert.assertEquals(2, rules.getLength());
        Assert.assertEquals("@charset \"US-ASCII\";", rules.item(0).getCssText());
        Assert.assertEquals("P { color: blue }", rules.item(1).getCssText());

        CSSRule rule = rules.item(1);
        rule.setCssText("H2 { smell: strong }");
        Assert.assertEquals("H2 { smell: strong }", rules.item(1).getCssText());

        final int n = stylesheet.insertRule("@media speech { H1 { voice: male } }", 1);
        Assert.assertEquals(1, n);

        Assert.assertEquals(3, rules.getLength());
        Assert.assertEquals("@charset \"US-ASCII\";", rules.item(0).getCssText());
        Assert.assertEquals("@media speech {H1 { voice: male } }", rules.item(1).getCssText());
        Assert.assertEquals("H2 { smell: strong }", rules.item(2).getCssText());

        rule = rules.item(1);
        ((CSSMediaRule) rule).insertRule("P { voice: female }", 1);
        Assert.assertEquals("speech", ((CSSMediaRule) rule).getMedia().getMediaText());

        // TODO
        ((CSSMediaRule) rule).getMedia().setMediaText("speech, signlanguage");
        Assert.assertEquals("speech, speech, signlanguage", ((CSSMediaRule) rule).getMedia().getMediaText());

        ((CSSMediaRule) rule).getMedia().deleteMedium("signlanguage");
        Assert.assertEquals("speech, speech", ((CSSMediaRule) rule).getMedia().getMediaText());

        ((CSSMediaRule) rule).getMedia().appendMedium("semaphore");
        Assert.assertEquals("speech, speech, semaphore", ((CSSMediaRule) rule).getMedia().getMediaText());
    }
}
