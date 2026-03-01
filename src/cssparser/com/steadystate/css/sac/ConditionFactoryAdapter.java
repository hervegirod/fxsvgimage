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

import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;

import com.steadystate.css.parser.selectors.PrefixAttributeConditionImpl;
import com.steadystate.css.parser.selectors.SubstringAttributeConditionImpl;
import com.steadystate.css.parser.selectors.SuffixAttributeConditionImpl;

/**
 * Implementation of the ConditionFactoryExt interface that maps calls back to a
 * native sac ConditionFactory.
 */
public class ConditionFactoryAdapter implements ConditionFactoryExt {

    private final ConditionFactory conditionFactory_;

    public ConditionFactoryAdapter(final ConditionFactory selectorFactory) {
        conditionFactory_ = selectorFactory;
    }

    @Override
    public CombinatorCondition createAndCondition(final Condition first, final Condition second) throws CSSException {
        return conditionFactory_.createAndCondition(first, second);
    }

    @Override
    public CombinatorCondition createOrCondition(final Condition first, final Condition second) throws CSSException {
        return conditionFactory_.createOrCondition(first, second);
    }

    @Override
    public NegativeCondition createNegativeCondition(final Condition condition) throws CSSException {
        return conditionFactory_.createNegativeCondition(condition);
    }

    @Override
    public PositionalCondition createPositionalCondition(final int position, final boolean typeNode, final boolean type)
            throws CSSException {
        return conditionFactory_.createPositionalCondition(position, typeNode, type);
    }

    @Override
    public AttributeCondition createAttributeCondition(final String localName, final String namespaceURI,
            final boolean specified, final String value) throws CSSException {
        return conditionFactory_.createAttributeCondition(localName, namespaceURI, specified, value);
    }

    @Override
    public AttributeCondition createIdCondition(final String value) throws CSSException {
        return conditionFactory_.createIdCondition(value);
    }

    @Override
    public AttributeCondition createIdCondition(final String value, final Locator locator) throws CSSException {
        return conditionFactory_.createIdCondition(value);
    }

    @Override
    public LangCondition createLangCondition(final String lang) throws CSSException {
        return conditionFactory_.createLangCondition(lang);
    }

    @Override
    public LangCondition createLangCondition(final String lang, final Locator locator) throws CSSException {
        return conditionFactory_.createLangCondition(lang);
    }

    @Override
    public AttributeCondition createOneOfAttributeCondition(final String localName, final String namespaceURI,
            final boolean specified, final String value) throws CSSException {
        return conditionFactory_.createOneOfAttributeCondition(localName, namespaceURI, specified, value);
    }

    @Override
    public AttributeCondition createBeginHyphenAttributeCondition(final String localName, final String namespaceURI,
            final boolean specified, final String value) throws CSSException {
        return conditionFactory_.createBeginHyphenAttributeCondition(localName, namespaceURI, specified, value);
    }

    @Override
    public AttributeCondition createClassCondition(final String namespaceURI, final String value) throws CSSException {
        return conditionFactory_.createClassCondition(namespaceURI, value);
    }

    @Override
    public AttributeCondition createClassCondition(final String namespaceURI, final String value, final Locator locator)
            throws CSSException {
        return conditionFactory_.createClassCondition(namespaceURI, value);
    }

    @Override
    public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value)
            throws CSSException {
        return conditionFactory_.createPseudoClassCondition(namespaceURI, value);
    }

    @Override
    public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value,
            final Locator locator, final boolean doubleColon) throws CSSException {
        return conditionFactory_.createPseudoClassCondition(namespaceURI, value);
    }

    @Override
    public Condition createOnlyChildCondition() throws CSSException {
        return conditionFactory_.createOnlyChildCondition();
    }

    @Override
    public Condition createOnlyTypeCondition() throws CSSException {
        return conditionFactory_.createOnlyTypeCondition();
    }

    @Override
    public ContentCondition createContentCondition(final String data) throws CSSException {
        return conditionFactory_.createContentCondition(data);
    }

    @Override
    public AttributeCondition createPrefixAttributeCondition(final String localName, final String namespaceURI,
            final boolean specified, final String value) throws CSSException {
        return new PrefixAttributeConditionImpl(localName, value, specified);
    }

    @Override
    public AttributeCondition createSuffixAttributeCondition(final String localName, final String namespaceURI,
            final boolean specified, final String value) throws CSSException {
        return new SuffixAttributeConditionImpl(localName, value, specified);
    }

    @Override
    public AttributeCondition createSubstringAttributeCondition(final String localName, final String namespaceURI,
            final boolean specified, final String value) throws CSSException {
        return new SubstringAttributeConditionImpl(localName, value, specified);
    }
}
