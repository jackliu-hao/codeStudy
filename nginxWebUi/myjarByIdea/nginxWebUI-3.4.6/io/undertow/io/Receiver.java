package io.undertow.io;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.nio.charset.Charset;

public interface Receiver {
   void setMaxBufferSize(int var1);

   void receiveFullString(FullStringCallback var1, ErrorCallback var2);

   void receiveFullString(FullStringCallback var1);

   void receivePartialString(PartialStringCallback var1, ErrorCallback var2);

   void receivePartialString(PartialStringCallback var1);

   void receiveFullString(FullStringCallback var1, ErrorCallback var2, Charset var3);

   void receiveFullString(FullStringCallback var1, Charset var2);

   void receivePartialString(PartialStringCallback var1, ErrorCallback var2, Charset var3);

   void receivePartialString(PartialStringCallback var1, Charset var2);

   void receiveFullBytes(FullBytesCallback var1, ErrorCallback var2);

   void receiveFullBytes(FullBytesCallback var1);

   void receivePartialBytes(PartialBytesCallback var1, ErrorCallback var2);

   void receivePartialBytes(PartialBytesCallback var1);

   void pause();

   void resume();

   public static class RequestToLargeException extends IOException {
   }

   public interface PartialBytesCallback {
      void handle(HttpServerExchange var1, byte[] var2, boolean var3);
   }

   public interface PartialStringCallback {
      void handle(HttpServerExchange var1, String var2, boolean var3);
   }

   public interface FullBytesCallback {
      void handle(HttpServerExchange var1, byte[] var2);
   }

   public interface FullStringCallback {
      void handle(HttpServerExchange var1, String var2);
   }

   public interface ErrorCallback {
      void error(HttpServerExchange var1, IOException var2);
   }
}
