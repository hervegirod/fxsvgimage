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
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

/**
 * Implementation of the DocumentHandlerExt interface that maps calls back to a
 * native sac DocumentHandler.
 */
public class DocumentHandlerAdapter implements DocumentHandlerExt, ErrorHandler {

    private final DocumentHandler documentHanlder_;

    public DocumentHandlerAdapter(final DocumentHandler documentHanlder) {
        documentHanlder_ = documentHanlder;
    }

    @Override
    public void startDocument(final InputSource source) throws CSSException {
        documentHanlder_.startDocument(source);
    }

    @Override
    public void endDocument(final InputSource source) throws CSSException {
        documentHanlder_.endDocument(source);
    }

    @Override
    public void comment(final String text) throws CSSException {
        documentHanlder_.comment(text);
    }

    @Override
    public void ignorableAtRule(final String atRule) throws CSSException {
        documentHanlder_.ignorableAtRule(atRule);
    }

    @Override
    public void ignorableAtRule(final String atRule, final Locator locator) throws CSSException {
        documentHanlder_.ignorableAtRule(atRule);
    }

    @Override
    public void namespaceDeclaration(final String prefix, final String uri) throws CSSException {
        documentHanlder_.namespaceDeclaration(prefix, uri);
    }

    @Override
    public void importStyle(final String uri, final SACMediaList media,
            final String defaultNamespaceURI) throws CSSException {
        documentHanlder_.importStyle(uri, media, defaultNamespaceURI);
    }

    @Override
    public void importStyle(final String uri, final SACMediaList media,
            final String defaultNamespaceURI, final Locator locator) throws CSSException {
        documentHanlder_.importStyle(uri, media, defaultNamespaceURI);
    }

    @Override
    public void startMedia(final SACMediaList media) throws CSSException {
        documentHanlder_.startMedia(media);
    }

    @Override
    public void startMedia(final SACMediaList media, final Locator locator) throws CSSException {
        documentHanlder_.startMedia(media);
    }

    @Override
    public void endMedia(final SACMediaList media) throws CSSException {
        documentHanlder_.endMedia(media);
    }

    @Override
    public void startPage(final String name, final String pseudoPage) throws CSSException {
        documentHanlder_.startPage(name, pseudoPage);
    }

    @Override
    public void startPage(final String name, final String pseudoPage, final Locator locator) throws CSSException {
        documentHanlder_.startPage(name, pseudoPage);
    }

    @Override
    public void endPage(final String name, final String pseudoPage) throws CSSException {
        documentHanlder_.endPage(name, pseudoPage);
    }

    @Override
    public void startFontFace() throws CSSException {
        documentHanlder_.startFontFace();
    }

    @Override
    public void startFontFace(final Locator locator) throws CSSException {
        documentHanlder_.startFontFace();
    }

    @Override
    public void endFontFace() throws CSSException {
        documentHanlder_.endFontFace();
    }

    @Override
    public void startSelector(final SelectorList selectors) throws CSSException {
        documentHanlder_.startSelector(selectors);
    }

    @Override
    public void startSelector(final SelectorList selectors, final Locator locator) throws CSSException {
        documentHanlder_.startSelector(selectors);
    }

    @Override
    public void endSelector(final SelectorList selectors) throws CSSException {
        documentHanlder_.endSelector(selectors);
    }

    @Override
    public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
        documentHanlder_.property(name, value, important);
    }

    @Override
    public void property(final String name, final LexicalUnit value, final boolean important, final Locator locator) {
        documentHanlder_.property(name, value, important);
    }

    @Override
    public void charset(final String characterEncoding, final Locator locator) throws CSSException {
        // empty default impl
    }

    @Override
    public void warning(final CSSParseException exception) throws CSSException {
        final StringBuilder sb = new StringBuilder();
        sb.append(exception.getURI())
            .append(" [")
            .append(exception.getLineNumber())
            .append(":")
            .append(exception.getColumnNumber())
            .append("] ")
            .append(exception.getMessage());
        System.err.println(sb.toString());
    }

    @Override
    public void error(final CSSParseException exception) throws CSSException {
        final StringBuilder sb = new StringBuilder();
        sb.append(exception.getURI())
            .append(" [")
            .append(exception.getLineNumber())
            .append(":")
            .append(exception.getColumnNumber())
            .append("] ")
            .append(exception.getMessage());
        System.err.println(sb.toString());
    }

    @Override
    public void fatalError(final CSSParseException exception) throws CSSException {
        final StringBuilder sb = new StringBuilder();
        sb.append(exception.getURI())
            .append(" [")
            .append(exception.getLineNumber())
            .append(":")
            .append(exception.getColumnNumber())
            .append("] ")
            .append(exception.getMessage());
        System.err.println(sb.toString());
    }
}
