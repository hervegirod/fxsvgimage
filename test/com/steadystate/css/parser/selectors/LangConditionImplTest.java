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

package com.steadystate.css.parser.selectors;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.Condition;

import com.steadystate.css.format.CSSFormat;

/**
 * Testcases for {@link LangConditionImpl}.
 */
public class LangConditionImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withoutValue() throws Exception {
        final LangConditionImpl c = new LangConditionImpl(null);
        Assert.assertEquals(Condition.SAC_LANG_CONDITION, c.getConditionType());
        Assert.assertNull(c.getLang());

        Assert.assertEquals(":lang()", c.toString());

        Assert.assertEquals(":lang()", c.getCssText(null));
        Assert.assertEquals(":lang()", c.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void emptyValue() throws Exception {
        final LangConditionImpl c = new LangConditionImpl("");
        Assert.assertEquals(Condition.SAC_LANG_CONDITION, c.getConditionType());
        Assert.assertEquals("", c.getLang());

        Assert.assertEquals(":lang()", c.toString());

        Assert.assertEquals(":lang()", c.getCssText(null));
        Assert.assertEquals(":lang()", c.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withValue() throws Exception {
        final LangConditionImpl c = new LangConditionImpl("value");
        Assert.assertEquals(Condition.SAC_LANG_CONDITION, c.getConditionType());
        Assert.assertEquals("value", c.getLang());

        Assert.assertEquals(":lang(value)", c.toString());

        Assert.assertEquals(":lang(value)", c.getCssText(null));
        Assert.assertEquals(":lang(value)", c.getCssText(new CSSFormat()));
    }
}
