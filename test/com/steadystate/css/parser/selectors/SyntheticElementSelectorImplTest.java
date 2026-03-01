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
 * Testcases for {@link SyntheticElementSelectorImpl}.
 */
public class SyntheticElementSelectorImplTest {

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void ctor() throws Exception {
        final SyntheticElementSelectorImpl s = new SyntheticElementSelectorImpl();
        Assert.assertNull(s.getNamespaceURI());
        Assert.assertNull(s.getLocalName());
        Assert.assertEquals(Selector.SAC_ELEMENT_NODE_SELECTOR, s.getSelectorType());

        Assert.assertEquals("", s.toString());

        Assert.assertEquals("", s.getCssText(null));
        Assert.assertEquals("", s.getCssText(new CSSFormat()));
    }

    /**
     * @throws Exception if any error occurs
     */
    @Test
    public void withValue() throws Exception {
        final SyntheticElementSelectorImpl s = new SyntheticElementSelectorImpl();
        try {
            s.setLocalName("test");
            Assert.fail("RuntimeException expected");
        }
        catch (final RuntimeException e) {
            Assert.assertEquals("Method setLocalName is not supported for SyntheticElementSelectorImpl.",
                                    e.getMessage());
        }
    }
}
