/*
Copyright (c) 2026, Hervé Girod
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
package org.girod.javafx.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.girod.javafx.svgimage.SVGImage;

/**
 * Used to show an image for comparing it. This is necessary to show an image to be able to have the CSS effects applied on them.
 *
 * @since 1.7.1
 */
public class PaneImageUtil {
   private Stage stage = null;

   public PaneImageUtil() {
   }

   public Stage createStage(SVGImage image) {
      new JFXPanel();
      FutureTask<Stage> future = new FutureTask<>(new Callable<Stage>() {
         @Override
         public Stage call() throws Exception {
            Stage stage = createStageImpl(image);
            return stage;
         }
      });
      Platform.runLater(future);
      try {
         stage = future.get();
         return stage;
      } catch (InterruptedException | ExecutionException ex) {
         return null;
      }
   }

   public void hideStage() {
      if (stage != null) {
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               stage.hide();
            }
         });
      }
   }

   private Stage createStageImpl(SVGImage image) {
      Scene scene = new Scene(image, 100, 100);
      Stage _stage = new Stage();
      _stage.setScene(scene);
      _stage.show();
      return _stage;
   }
}
