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

package com.steadystate.css.sac;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * Implementation of the SelectorFactoryExt interface that maps calls back to a
 * native sac SelectorFactory.
 */
public class SelectorFactoryAdapter implements SelectorFactoryExt {

    private final SelectorFactory selectorFactory_;

    public SelectorFactoryAdapter(final SelectorFactory selectorFactory) {
        selectorFactory_ = selectorFactory;
    }

    @Override
    public ConditionalSelector createConditionalSelector(final SimpleSelector selector, final Condition condition)
            throws CSSException {
        return selectorFactory_.createConditionalSelector(selector, condition);
    }

    @Override
    public SimpleSelector createAnyNodeSelector() throws CSSException {
        return selectorFactory_.createAnyNodeSelector();
    }

    @Override
    public SimpleSelector createRootNodeSelector() throws CSSException {
        return selectorFactory_.createRootNodeSelector();
    }

    @Override
    public NegativeSelector createNegativeSelector(final SimpleSelector selector)
            throws CSSException {
        return selectorFactory_.createNegativeSelector(selector);
    }

    @Override
    public ElementSelector createElementSelector(final String namespaceURI, final String tagName)
                throws CSSException {
        return selectorFactory_.createElementSelector(namespaceURI, tagName);
    }

    @Override
    public CharacterDataSelector createTextNodeSelector(final String data) throws CSSException {
        return selectorFactory_.createTextNodeSelector(data);
    }

    @Override
    public CharacterDataSelector createCDataSectionSelector(final String data) throws CSSException {
        return selectorFactory_.createCDataSectionSelector(data);
    }

    @Override
    public ProcessingInstructionSelector createProcessingInstructionSelector(final String target, final String data)
            throws CSSException {
        return selectorFactory_.createProcessingInstructionSelector(target, data);
    }

    @Override
    public CharacterDataSelector createCommentSelector(final String data) throws CSSException {
        return selectorFactory_.createCommentSelector(data);
    }

    @Override
    public ElementSelector createPseudoElementSelector(final String namespaceURI, final String pseudoName)
            throws CSSException {
        return selectorFactory_.createPseudoElementSelector(namespaceURI, pseudoName);
    }

    @Override
    public DescendantSelector createDescendantSelector(final Selector parent, final SimpleSelector descendant)
            throws CSSException {
        return selectorFactory_.createDescendantSelector(parent, descendant);
    }

    @Override
    public DescendantSelector createChildSelector(final Selector parent, final SimpleSelector child)
            throws CSSException {
        return selectorFactory_.createChildSelector(parent, child);
    }

    @Override
    public SiblingSelector createDirectAdjacentSelector(final short nodeType, final Selector child,
            final SimpleSelector directAdjacent) throws CSSException {
        return selectorFactory_.createDirectAdjacentSelector(nodeType, child, directAdjacent);
    }

    @Override
    public ElementSelector createElementSelector(final String namespaceURI, final String tagName,
            final Locator locator) throws CSSException {
        return selectorFactory_.createElementSelector(namespaceURI, tagName);
    }

    @Override
    public ElementSelector createPseudoElementSelector(final String namespaceURI, final String pseudoName,
            final Locator locator, final boolean doubleColon) throws CSSException {
        return selectorFactory_.createPseudoElementSelector(namespaceURI, pseudoName);
    }

    @Override
    public ElementSelector createSyntheticElementSelector() throws CSSException {
        return null;
    }

    @Override
    public SiblingSelector createGeneralAdjacentSelector(
            final short nodeType,
            final Selector child,
            final SimpleSelector directAdjacent) throws CSSException {
        return null;
    }
}
