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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.ErrorHandler;

/**
 * Testcases.
 */
public class ImportantTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void css1() throws Exception {
        css(new SACParserCSS1());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void css2() throws Exception {
        css(new SACParserCSS2());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void css21() throws Exception {
        css(new SACParserCSS21());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void css3() throws Exception {
        css(new SACParserCSS3());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void cssCSSmobileOKBasic1() throws Exception {
        css(new SACParserCSSmobileOKBasic1());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void css1Error() throws Exception {
        final ErrorHandler errorHandler = parserError(new SACParserCSS1());

        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(1, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getWarningCount());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void css2Error() throws Exception {
        final ErrorHandler errorHandler = parserError(new SACParserCSS2());

        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(1, errorHandler.getErrorCount());
        Assert.assertEquals(1, errorHandler.getWarningCount());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void css21Error() throws Exception {
        final ErrorHandler errorHandler = parserError(new SACParserCSS21());

        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(0, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getWarningCount());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void css3Error() throws Exception {
        final ErrorHandler errorHandler = parserError(new SACParserCSS3());

        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(0, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getWarningCount());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void cssCSSmobileOKBasic1Error() throws Exception {
        final ErrorHandler errorHandler = parserError(new SACParserCSSmobileOKBasic1());

        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(1, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getWarningCount());
    }

    private void css(final Parser sacParser) throws Exception {
        final ErrorHandler errorHandler = new ErrorHandler();
        sacParser.setErrorHandler(errorHandler);

        final InputStream is = getClass().getResourceAsStream("important.css");
        Assert.assertNotNull(is);
        final Reader r = new InputStreamReader(is);
        final InputSource source = new InputSource(r);

        final CSSOMParser parser = new CSSOMParser(sacParser);
        final CSSStyleSheet ss = parser.parseStyleSheet(source, null, null);

        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(0, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getWarningCount());

        final CSSRuleList rules = ss.getCssRules();
        Assert.assertEquals(5, rules.getLength());

        CSSRule rule = rules.item(0);
        Assert.assertEquals(".sel1 { padding: 0 !important }", rule.getCssText());
        Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());

        rule = rules.item(1);
        Assert.assertEquals(".sel2 { font-weight: normal !important }", rule.getCssText());
        Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());

        rule = rules.item(2);
        Assert.assertEquals(".sel3 { font-weight: normal !important }", rule.getCssText());
        Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());

        rule = rules.item(3);
        Assert.assertEquals(".sel4 { font-weight: normal !important }", rule.getCssText());
        Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());

        rule = rules.item(4);
        Assert.assertEquals(".important { font-weight: bold }", rule.getCssText());
        Assert.assertEquals(CSSRule.STYLE_RULE, rule.getType());
    }

    private ErrorHandler parserError(final Parser sacParser) throws Exception {
        final ErrorHandler errorHandler = new ErrorHandler();
        sacParser.setErrorHandler(errorHandler);

        final InputSource source = new InputSource(
                new StringReader(".foo { font-weight: normal !/* comment */important; }"));

        sacParser.parseStyleSheet(source);
        return errorHandler;
    }
}
