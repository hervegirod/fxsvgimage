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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rbri
 */
public class ParserUtilsTest {

    @Test
    public void trimBy() {
        Assert.assertEquals("test", ParserUtils.trimBy(new StringBuilder("test"), 0, 0));

        Assert.assertEquals("est", ParserUtils.trimBy(new StringBuilder("test"), 1, 0));
        Assert.assertEquals("st", ParserUtils.trimBy(new StringBuilder("test"), 2, 0));

        Assert.assertEquals("tes", ParserUtils.trimBy(new StringBuilder("test"), 0, 1));
        Assert.assertEquals("te", ParserUtils.trimBy(new StringBuilder("test"), 0, 2));

        Assert.assertEquals("e", ParserUtils.trimBy(new StringBuilder("test"), 1, 2));
    }

    @Test
    public void trimUrl() {
        Assert.assertEquals("test", ParserUtils.trimUrl(new StringBuilder("url(test)")));
        Assert.assertEquals("", ParserUtils.trimUrl(new StringBuilder("url()")));

        Assert.assertEquals("test", ParserUtils.trimUrl(new StringBuilder("url('test')")));
        Assert.assertEquals("test", ParserUtils.trimUrl(new StringBuilder("url(\"test\")")));

        Assert.assertEquals("test", ParserUtils.trimUrl(new StringBuilder("url(   test \t )")));
    }
}