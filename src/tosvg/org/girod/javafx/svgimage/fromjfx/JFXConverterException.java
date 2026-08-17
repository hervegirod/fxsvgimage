/*
Copyright (c) 2026 Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/jfxconverter/
 */
package org.girod.javafx.svgimage.fromjfx;

/**
 * The exceptions which are thrown by the {@link org.girod.javafx.svgimage.fromjfx.JFXConverter}s.
 *
 * @since 1.8
 */
public class JFXConverterException extends Exception {
   /**
    * Constructs a JFXConverterException.
    */
   public JFXConverterException() {
      super();
   }

   /**
    * Constructs a JFXConverterException with the specified detail message.
    *
    * @param message the message
    */
   public JFXConverterException(String message) {
      super(message);
   }

   /**
    * Constructs a JFXConverterException with the specified cause an a detailed message.
    *
    * @param message the message
    * @param cause The cause
    * @since 1.8
    */
   public JFXConverterException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Constructs a JFXConverterException with the specified cause.
    *
    * @param cause The cause
    * @since 1.8
    */
   public JFXConverterException(Throwable cause) {
      super(cause);
   }
}
