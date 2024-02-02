package io.undertow.client;

import java.io.IOException;

public interface ClientCallback<T> {
   void completed(T var1);

   void failed(IOException var1);
}
