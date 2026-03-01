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
import org.w3c.css.sac.Selector;

import com.steadystate.css.format.CSSFormat;

/**
 * Testcases for {@link ConditionalSelectorImpl}.
 * @author rbri
 */
public class ConditionalSelectorImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withoutSelectorCondition() throws Exception {
        final ConditionalSelectorImpl s = new ConditionalSelectorImpl(null, null);
        Assert.assertEquals(Selector.SAC_CONDITIONAL_SELECTOR, s.getSelectorType());
        Assert.assertNull(s.getSimpleSelector());
        Assert.assertNull(s.getCondition());

        Assert.assertEquals("", s.toString());

        Assert.assertEquals("", s.getCssText(null));
        Assert.assertEquals("", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void selectorOnly() throws Exception {
        final ElementSelectorImpl selector = new ElementSelectorImpl("p");
        final ConditionalSelectorImpl s = new ConditionalSelectorImpl(selector, null);
        Assert.assertEquals(Selector.SAC_CONDITIONAL_SELECTOR, s.getSelectorType());
        Assert.assertEquals(selector, s.getSimpleSelector());
        Assert.assertNull(s.getCondition());

        Assert.assertEquals("p", s.toString());

        Assert.assertEquals("p", s.getCssText(null));
        Assert.assertEquals("p", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void conditionOnly() throws Exception {
        final IdConditionImpl condition = new IdConditionImpl("id");
        final ConditionalSelectorImpl s = new ConditionalSelectorImpl(null, condition);
        Assert.assertEquals(Selector.SAC_CONDITIONAL_SELECTOR, s.getSelectorType());
        Assert.assertNull(s.getSimpleSelector());
        Assert.assertEquals(condition, s.getCondition());

        Assert.assertEquals("#id", s.toString());

        Assert.assertEquals("#id", s.getCssText(null));
        Assert.assertEquals("#id", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void both() throws Exception {
        final ElementSelectorImpl selector = new ElementSelectorImpl("p");
        final IdConditionImpl condition = new IdConditionImpl("id");
        final ConditionalSelectorImpl s = new ConditionalSelectorImpl(selector, condition);
        Assert.assertEquals(Selector.SAC_CONDITIONAL_SELECTOR, s.getSelectorType());
        Assert.assertEquals(selector, s.getSimpleSelector());
        Assert.assertEquals(condition, s.getCondition());

        Assert.assertEquals("p#id", s.toString());

        Assert.assertEquals("p#id", s.getCssText(null));
        Assert.assertEquals("p#id", s.getCssText(new CSSFormat()));
    }
}
