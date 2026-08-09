/*
Copyright (c) 2022, 2026 Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/fxsvgimage
 */
package org.girod.javafx.svgimage.fromjfx;

import org.girod.javafx.svgimage.fromjfx.utils.CSSProperty;
import java.util.Map;

/**
 * The converter interface.
 *
 * @version 1.8
 * @param <O> the type of Objects returned by the converter
 */
public interface NodeConverter<O extends Object> {
   /**
    * Convert the Node to an xml element.
    *
    * @param isDisabled true if the node is disabled
    * @return the conversion result
    */
   public O convert(boolean isDisabled);

   /**
    * Return the CSS properties Map of the Node. These properties include those which are set by default (null
    * StyleOrigin).
    *
    * @return the CSS properties Map
    */
   public Map<String, CSSProperty> getCSSProperties();

   /**
    * Return all the CSS properties Map of the Node.
    *
    * @return the CSS properties Map
    */
   public Map<String, Object> getAllProperties();

   /**
    * Return the CSS properties Map of the Node. These propeties only include those set by the CSS user file or inline
    * for the widget.
    *
    * @return the CSS properties Map
    */
   public Map<String, Object> getProperties();
}
