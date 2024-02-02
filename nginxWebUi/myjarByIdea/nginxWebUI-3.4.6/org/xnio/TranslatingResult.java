package org.xnio;

import java.io.IOException;

public abstract class TranslatingResult<T, O> implements Result<T> {
   private final Result<O> output;

   protected TranslatingResult(Result<O> output) {
      this.output = output;
   }

   public boolean setException(IOException exception) {
      return this.output.setException(exception);
   }

   public boolean setCancelled() {
      return this.output.setCancelled();
   }

   public boolean setResult(T result) {
      try {
         return this.output.setResult(this.translate(result));
      } catch (IOException var3) {
         return this.output.setException(var3);
      }
   }

   protected abstract O translate(T var1) throws IOException;
}
