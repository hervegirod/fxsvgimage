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
import org.w3c.css.sac.SiblingSelector;

import com.steadystate.css.format.CSSFormat;

/**
 * Testcases for {@link DirectAdjacentSelectorImpl}.
 * @author rbri
 */
public class DirectAdjacentSelectorImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withoutParentSimple() throws Exception {
        final DirectAdjacentSelectorImpl s = new DirectAdjacentSelectorImpl(SiblingSelector.ANY_NODE, null, null);
        Assert.assertEquals(Selector.SAC_DIRECT_ADJACENT_SELECTOR, s.getSelectorType());
        Assert.assertEquals(SiblingSelector.ANY_NODE, s.getNodeType());
        Assert.assertNull(s.getSelector());
        Assert.assertNull(s.getSiblingSelector());

        Assert.assertEquals(" + ", s.toString());

        Assert.assertEquals(" + ", s.getCssText(null));
        Assert.assertEquals(" + ", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void selectorOnly() throws Exception {
        final ElementSelectorImpl selector = new ElementSelectorImpl("p");
        final DirectAdjacentSelectorImpl s = new DirectAdjacentSelectorImpl(selector.getSelectorType(), selector, null);
        Assert.assertEquals(Selector.SAC_DIRECT_ADJACENT_SELECTOR, s.getSelectorType());
        Assert.assertEquals(selector.getSelectorType(), s.getNodeType());
        Assert.assertEquals(selector, s.getSelector());
        Assert.assertNull(s.getSiblingSelector());

        Assert.assertEquals("p + ", s.toString());

        Assert.assertEquals("p + ", s.getCssText(null));
        Assert.assertEquals("p + ", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void simpleOnly() throws Exception {
        final ElementSelectorImpl simple = new ElementSelectorImpl("c");
        final DirectAdjacentSelectorImpl s = new DirectAdjacentSelectorImpl(SiblingSelector.ANY_NODE, null, simple);
        Assert.assertEquals(Selector.SAC_DIRECT_ADJACENT_SELECTOR, s.getSelectorType());
        Assert.assertEquals(SiblingSelector.ANY_NODE, s.getNodeType());
        Assert.assertNull(s.getSelector());
        Assert.assertEquals(simple, s.getSiblingSelector());

        Assert.assertEquals(" + c", s.toString());

        Assert.assertEquals(" + c", s.getCssText(null));
        Assert.assertEquals(" + c", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void both() throws Exception {
        final ElementSelectorImpl selector = new ElementSelectorImpl("p");
        final ElementSelectorImpl simple = new ElementSelectorImpl("c");
        final DirectAdjacentSelectorImpl s =
                    new DirectAdjacentSelectorImpl(selector.getSelectorType(), selector, simple);
        Assert.assertEquals(Selector.SAC_DIRECT_ADJACENT_SELECTOR, s.getSelectorType());
        Assert.assertEquals(selector.getSelectorType(), s.getNodeType());
        Assert.assertEquals(selector, s.getSelector());
        Assert.assertEquals(simple, s.getSiblingSelector());

        Assert.assertEquals("p + c", s.toString());

        Assert.assertEquals("p + c", s.getCssText(null));
        Assert.assertEquals("p + c", s.getCssText(new CSSFormat()));
    }
}
