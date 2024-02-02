package org.xnio;

import java.io.IOException;

public interface Result<T> {
   boolean setResult(T var1);

   boolean setException(IOException var1);

   boolean setCancelled();
}
