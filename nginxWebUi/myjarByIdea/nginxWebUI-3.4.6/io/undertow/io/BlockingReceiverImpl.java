package io.undertow.io;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class BlockingReceiverImpl implements Receiver {
   private static final Receiver.ErrorCallback END_EXCHANGE = new Receiver.ErrorCallback() {
      public void error(HttpServerExchange exchange, IOException e) {
         if (!exchange.isResponseStarted()) {
            exchange.setStatusCode(500);
         }

         exchange.setPersistent(false);
         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
         exchange.endExchange();
      }
   };
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   private final HttpServerExchange exchange;
   private final InputStream inputStream;
   private int maxBufferSize = -1;
   private boolean done = false;

   public BlockingReceiverImpl(HttpServerExchange exchange, InputStream inputStream) {
      this.exchange = exchange;
      this.inputStream = inputStream;
   }

   public void setMaxBufferSize(int maxBufferSize) {
      this.maxBufferSize = maxBufferSize;
   }

   public void receiveFullString(Receiver.FullStringCallback callback, Receiver.ErrorCallback errorCallback) {
      this.receiveFullString(callback, errorCallback, StandardCharsets.ISO_8859_1);
   }

   public void receiveFullString(Receiver.FullStringCallback callback) {
      this.receiveFullString(callback, END_EXCHANGE, StandardCharsets.ISO_8859_1);
   }

   public void receivePartialString(Receiver.PartialStringCallback callback, Receiver.ErrorCallback errorCallback) {
      this.receivePartialString(callback, errorCallback, StandardCharsets.ISO_8859_1);
   }

   public void receivePartialString(Receiver.PartialStringCallback callback) {
      this.receivePartialString(callback, END_EXCHANGE, StandardCharsets.ISO_8859_1);
   }

   public void receiveFullString(Receiver.FullStringCallback callback, Receiver.ErrorCallback errorCallback, Charset charset) {
      if (this.done) {
         throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
      } else {
         Receiver.ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
         if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
         } else if (this.exchange.isRequestComplete()) {
            callback.handle(this.exchange, "");
         } else {
            String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
            long contentLength;
            ByteArrayOutputStream sb;
            if (contentLengthString != null) {
               contentLength = Long.parseLong(contentLengthString);
               if (contentLength > 2147483647L) {
                  error.error(this.exchange, new Receiver.RequestToLargeException());
                  return;
               }

               sb = new ByteArrayOutputStream((int)contentLength);
            } else {
               contentLength = -1L;
               sb = new ByteArrayOutputStream();
            }

            if (this.maxBufferSize > 0 && contentLength > (long)this.maxBufferSize) {
               error.error(this.exchange, new Receiver.RequestToLargeException());
            } else {
               try {
                  PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
                  Throwable var11 = null;

                  try {
                     int s;
                     while((s = this.inputStream.read(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), pooled.getBuffer().remaining())) > 0) {
                        sb.write(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), s);
                     }

                     callback.handle(this.exchange, sb.toString(charset.name()));
                  } catch (Throwable var21) {
                     var11 = var21;
                     throw var21;
                  } finally {
                     if (pooled != null) {
                        if (var11 != null) {
                           try {
                              pooled.close();
                           } catch (Throwable var20) {
                              var11.addSuppressed(var20);
                           }
                        } else {
                           pooled.close();
                        }
                     }

                  }
               } catch (IOException var23) {
                  error.error(this.exchange, var23);
               }

            }
         }
      }
   }

   public void receiveFullString(Receiver.FullStringCallback callback, Charset charset) {
      this.receiveFullString(callback, END_EXCHANGE, charset);
   }

   public void receivePartialString(Receiver.PartialStringCallback callback, Receiver.ErrorCallback errorCallback, Charset charset) {
      if (this.done) {
         throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
      } else {
         Receiver.ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
         if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
         } else if (this.exchange.isRequestComplete()) {
            callback.handle(this.exchange, "", true);
         } else {
            String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
            long contentLength;
            if (contentLengthString != null) {
               contentLength = Long.parseLong(contentLengthString);
               if (contentLength > 2147483647L) {
                  error.error(this.exchange, new Receiver.RequestToLargeException());
                  return;
               }
            } else {
               contentLength = -1L;
            }

            if (this.maxBufferSize > 0 && contentLength > (long)this.maxBufferSize) {
               error.error(this.exchange, new Receiver.RequestToLargeException());
            } else {
               CharsetDecoder decoder = charset.newDecoder();

               try {
                  PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
                  Throwable var11 = null;

                  try {
                     int s;
                     while((s = this.inputStream.read(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), pooled.getBuffer().remaining())) > 0) {
                        pooled.getBuffer().limit(s);
                        CharBuffer res = decoder.decode(pooled.getBuffer());
                        callback.handle(this.exchange, res.toString(), false);
                        pooled.getBuffer().clear();
                     }

                     callback.handle(this.exchange, "", true);
                  } catch (Throwable var21) {
                     var11 = var21;
                     throw var21;
                  } finally {
                     if (pooled != null) {
                        if (var11 != null) {
                           try {
                              pooled.close();
                           } catch (Throwable var20) {
                              var11.addSuppressed(var20);
                           }
                        } else {
                           pooled.close();
                        }
                     }

                  }
               } catch (IOException var23) {
                  error.error(this.exchange, var23);
               }

            }
         }
      }
   }

   public void receivePartialString(Receiver.PartialStringCallback callback, Charset charset) {
      this.receivePartialString(callback, END_EXCHANGE, charset);
   }

   public void receiveFullBytes(Receiver.FullBytesCallback callback, Receiver.ErrorCallback errorCallback) {
      if (this.done) {
         throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
      } else {
         Receiver.ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
         if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
         } else if (this.exchange.isRequestComplete()) {
            callback.handle(this.exchange, EMPTY_BYTE_ARRAY);
         } else {
            String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
            long contentLength;
            ByteArrayOutputStream sb;
            if (contentLengthString != null) {
               contentLength = Long.parseLong(contentLengthString);
               if (contentLength > 2147483647L) {
                  error.error(this.exchange, new Receiver.RequestToLargeException());
                  return;
               }

               sb = new ByteArrayOutputStream((int)contentLength);
            } else {
               contentLength = -1L;
               sb = new ByteArrayOutputStream();
            }

            if (this.maxBufferSize > 0 && contentLength > (long)this.maxBufferSize) {
               error.error(this.exchange, new Receiver.RequestToLargeException());
            } else {
               try {
                  PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
                  Throwable var10 = null;

                  try {
                     int s;
                     while((s = this.inputStream.read(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), pooled.getBuffer().remaining())) > 0) {
                        sb.write(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), s);
                     }

                     callback.handle(this.exchange, sb.toByteArray());
                  } catch (Throwable var20) {
                     var10 = var20;
                     throw var20;
                  } finally {
                     if (pooled != null) {
                        if (var10 != null) {
                           try {
                              pooled.close();
                           } catch (Throwable var19) {
                              var10.addSuppressed(var19);
                           }
                        } else {
                           pooled.close();
                        }
                     }

                  }
               } catch (IOException var22) {
                  error.error(this.exchange, var22);
               }

            }
         }
      }
   }

   public void receiveFullBytes(Receiver.FullBytesCallback callback) {
      this.receiveFullBytes(callback, END_EXCHANGE);
   }

   public void receivePartialBytes(Receiver.PartialBytesCallback callback, Receiver.ErrorCallback errorCallback) {
      if (this.done) {
         throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
      } else {
         Receiver.ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
         if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
         } else if (this.exchange.isRequestComplete()) {
            callback.handle(this.exchange, EMPTY_BYTE_ARRAY, true);
         } else {
            String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
            long contentLength;
            if (contentLengthString != null) {
               contentLength = Long.parseLong(contentLengthString);
               if (contentLength > 2147483647L) {
                  error.error(this.exchange, new Receiver.RequestToLargeException());
                  return;
               }
            } else {
               contentLength = -1L;
            }

            if (this.maxBufferSize > 0 && contentLength > (long)this.maxBufferSize) {
               error.error(this.exchange, new Receiver.RequestToLargeException());
            } else {
               try {
                  PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
                  Throwable var9 = null;

                  try {
                     int s;
                     while((s = this.inputStream.read(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), pooled.getBuffer().remaining())) > 0) {
                        byte[] newData = new byte[s];
                        System.arraycopy(pooled.getBuffer().array(), pooled.getBuffer().arrayOffset(), newData, 0, s);
                        callback.handle(this.exchange, newData, false);
                     }

                     callback.handle(this.exchange, EMPTY_BYTE_ARRAY, true);
                  } catch (Throwable var19) {
                     var9 = var19;
                     throw var19;
                  } finally {
                     if (pooled != null) {
                        if (var9 != null) {
                           try {
                              pooled.close();
                           } catch (Throwable var18) {
                              var9.addSuppressed(var18);
                           }
                        } else {
                           pooled.close();
                        }
                     }

                  }
               } catch (IOException var21) {
                  error.error(this.exchange, var21);
               }

            }
         }
      }
   }

   public void receivePartialBytes(Receiver.PartialBytesCallback callback) {
      this.receivePartialBytes(callback, END_EXCHANGE);
   }

   public void pause() {
   }

   public void resume() {
   }
}
