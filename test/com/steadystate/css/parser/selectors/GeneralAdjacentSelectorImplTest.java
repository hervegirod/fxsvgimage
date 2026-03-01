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
 * Test cases for {@link GeneralAdjacentSelectorImpl}.
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
public class GeneralAdjacentSelectorImplTest {

    @Test
    public void withoutParentDescendant() {
        final GeneralAdjacentSelectorImpl selector =
                new GeneralAdjacentSelectorImpl(Selector.SAC_ANY_NODE_SELECTOR, null, null);
        Assert.assertNull(selector.getSelector());
        Assert.assertNull(selector.getSiblingSelector());

        Assert.assertEquals(" ~ ", selector.toString());

        Assert.assertEquals(" ~ ", selector.getCssText(null));
        Assert.assertEquals(" ~ ", selector.getCssText(new CSSFormat()));
    }

    @Test
    public void withoutParent() {
        final ElementSelectorImpl descendant = new ElementSelectorImpl("a");
        final GeneralAdjacentSelectorImpl selector =
                new GeneralAdjacentSelectorImpl(Selector.SAC_ANY_NODE_SELECTOR, null, descendant);
        Assert.assertNull(selector.getSelector());
        Assert.assertEquals(descendant, selector.getSiblingSelector());

        Assert.assertEquals(" ~ a", selector.toString());

        Assert.assertEquals(" ~ a", selector.getCssText(null));
        Assert.assertEquals(" ~ a", selector.getCssText(new CSSFormat()));
    }

    @Test
    public void withoutDescendant() {
        final ElementSelectorImpl parent = new ElementSelectorImpl("p");
        final GeneralAdjacentSelectorImpl selector =
                new GeneralAdjacentSelectorImpl(Selector.SAC_ANY_NODE_SELECTOR, parent, null);
        Assert.assertEquals(parent, selector.getSelector());
        Assert.assertNull(null, selector.getSiblingSelector());

        Assert.assertEquals("p ~ ", selector.toString());

        Assert.assertEquals("p ~ ", selector.getCssText(null));
        Assert.assertEquals("p ~ ", selector.getCssText(new CSSFormat()));
    }

    @Test
    public void both() {
        final ElementSelectorImpl parent = new ElementSelectorImpl("p");
        final ElementSelectorImpl descendant = new ElementSelectorImpl("a");
        final GeneralAdjacentSelectorImpl selector =
                new GeneralAdjacentSelectorImpl(Selector.SAC_ANY_NODE_SELECTOR, parent, descendant);
        Assert.assertEquals(parent, selector.getSelector());
        Assert.assertEquals(descendant, selector.getSiblingSelector());

        Assert.assertEquals("p ~ a", selector.toString());

        Assert.assertEquals("p ~ a", selector.getCssText(null));
        Assert.assertEquals("p ~ a", selector.getCssText(new CSSFormat()));
    }
}
