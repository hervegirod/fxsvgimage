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
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.Locator;

/**
 * Extension of the ConditionFactory interface.
 * This was added to support the locator parameter to
 * inform about the code position.
 */
public interface ConditionFactoryExt extends ConditionFactory {

    AttributeCondition createClassCondition(String namespaceURI, String value, Locator locator) throws CSSException;

    AttributeCondition createIdCondition(String value, Locator locator) throws CSSException;

    AttributeCondition createPseudoClassCondition(String namespaceURI, String value,
            Locator locator, boolean doubleColon)
            throws CSSException;

    LangCondition createLangCondition(String lang, Locator locator) throws CSSException;

    AttributeCondition createPrefixAttributeCondition(String localName, String namespaceURI, boolean specified,
            String value) throws CSSException;

    AttributeCondition createSuffixAttributeCondition(String localName, String namespaceURI, boolean specified,
            String value) throws CSSException;

    AttributeCondition createSubstringAttributeCondition(String localName, String namespaceURI, boolean specified,
            String value) throws CSSException;
}
