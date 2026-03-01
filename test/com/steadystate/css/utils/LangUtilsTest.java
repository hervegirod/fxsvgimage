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

package com.steadystate.css.utils;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import com.steadystate.css.util.LangUtils;

/**
 * @author rbri
 */
public class LangUtilsTest {
    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void join() throws Exception {
        Assert.assertNull(LangUtils.join(null, null));

        Assert.assertEquals("", LangUtils.join(new LinkedList<String>(), null));
        Assert.assertEquals("1", LangUtils.join(Arrays.asList("1"), null));
        Assert.assertEquals("12", LangUtils.join(Arrays.asList("1", "2"), null));

        Assert.assertEquals("", LangUtils.join(new LinkedList<String>(), ","));
        Assert.assertEquals("1", LangUtils.join(Arrays.asList("1"), ","));
        Assert.assertEquals("1,2", LangUtils.join(Arrays.asList("1", "2"), ","));

        Assert.assertEquals("", LangUtils.join(new LinkedList<String>(), ", "));
        Assert.assertEquals("1", LangUtils.join(Arrays.asList("1"), ", "));
        Assert.assertEquals("1, 2", LangUtils.join(Arrays.asList("1", "2"), ", "));
    }
}
