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

package com.steadystate.css;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;

/**
 * @author RBRi
 */
public class ErrorHandler implements org.w3c.css.sac.ErrorHandler {

    private int errorCount_;
    private StringBuilder errorMsg_ = new StringBuilder();
    private StringBuilder errorLines_ = new StringBuilder();
    private StringBuilder errorColumns_ = new StringBuilder();

    private int fatalErrorCount_;
    private StringBuilder fatalErrorMsg_ = new StringBuilder();
    private StringBuilder fatalErrorLines_ = new StringBuilder();
    private StringBuilder fatalErrorColumns_ = new StringBuilder();

    private int warningCount_;
    private StringBuilder warningMsg_ = new StringBuilder();
    private StringBuilder warningLines_ = new StringBuilder();
    private StringBuilder warningColumns_ = new StringBuilder();

    @Override
    public void error(final CSSParseException e) throws CSSException {
        errorCount_++;
        errorMsg_.append(e.getMessage()).append(" ");
        errorLines_.append(e.getLineNumber()).append(" ");
        errorColumns_.append(e.getColumnNumber()).append(" ");
    }

    @Override
    public void fatalError(final CSSParseException e) throws CSSException {
        fatalErrorCount_++;
        fatalErrorMsg_.append(e.getMessage()).append(" ");
        fatalErrorLines_.append(e.getLineNumber()).append(" ");
        fatalErrorColumns_.append(e.getColumnNumber()).append(" ");
    }

    @Override
    public void warning(final CSSParseException e) throws CSSException {
        warningCount_++;
        warningMsg_.append(e.getMessage()).append(" ");
        warningLines_.append(e.getLineNumber()).append(" ");
        warningColumns_.append(e.getColumnNumber()).append(" ");
    }

    public int getErrorCount() {
        return errorCount_;
    }

    public String getErrorMessage() {
        return errorMsg_.toString().trim();
    }

    public String getErrorLines() {
        return errorLines_.toString().trim();
    }

    public String getErrorColumns() {
        return errorColumns_.toString().trim();
    }

    public int getFatalErrorCount() {
        return fatalErrorCount_;
    }

    public String getFatalErrorMessage() {
        return fatalErrorMsg_.toString().trim();
    }

    public String getFatalErrorLines() {
        return fatalErrorLines_.toString().trim();
    }

    public String getFatalErrorColumns() {
        return fatalErrorColumns_.toString().trim();
    }

    public int getWarningCount() {
        return warningCount_;
    }

    public String getWarningMessage() {
        return warningMsg_.toString().trim();
    }

    public String getWarningLines() {
        return warningLines_.toString().trim();
    }

    public String getWarningColumns() {
        return warningColumns_.toString().trim();
    }
}
