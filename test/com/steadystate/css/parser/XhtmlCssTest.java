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
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;

import com.steadystate.css.ErrorHandler;

/**
 * @author rbri
 */
public class XhtmlCssTest {

    private static final String CSS_CODE = "<!--/*--><![CDATA[/*><!--*/ \n"
        + "body { color: #000000; background-color: #FFFFFF; }\n"
        + "a:link { color: #0000CC; }\n"
        + "p, address {margin-left: 3em;}\n"
        + "span {font-size: smaller;}\n"
        + "/*]]>*/-->";

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void xhtmlCssCSS1() throws Exception {
        xhtmlCss(new SACParserCSS1());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void xhtmlCssCSS2() throws Exception {
        xhtmlCss(new SACParserCSS2());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void xhtmlCssCSS21() throws Exception {
        xhtmlCss(new SACParserCSS21());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void xhtmlCssCSS3() throws Exception {
        xhtmlCss(new SACParserCSS3());
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void xhtmlCssCSSmobileOKBasic1() throws Exception {
        xhtmlCss(new SACParserCSSmobileOKBasic1());
    }

    private void xhtmlCss(final Parser sacParser) throws Exception {
        final ErrorHandler errorHandler = new ErrorHandler();
        sacParser.setErrorHandler(errorHandler);

        final InputSource source = new InputSource(new StringReader(CSS_CODE));

        sacParser.parseStyleSheet(source);

        Assert.assertEquals(0, errorHandler.getFatalErrorCount());
        Assert.assertEquals(0, errorHandler.getErrorCount());
        Assert.assertEquals(0, errorHandler.getWarningCount());
    }
}
