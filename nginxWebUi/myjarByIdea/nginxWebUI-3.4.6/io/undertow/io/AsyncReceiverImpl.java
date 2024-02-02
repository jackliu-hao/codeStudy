package io.undertow.io;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.Connectors;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import org.xnio.ChannelListener;
import org.xnio.channels.StreamSourceChannel;

public class AsyncReceiverImpl implements Receiver {
   private static final Receiver.ErrorCallback END_EXCHANGE = new Receiver.ErrorCallback() {
      public void error(HttpServerExchange exchange, IOException e) {
         e.printStackTrace();
         exchange.setStatusCode(500);
         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
         exchange.endExchange();
      }
   };
   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
   private final HttpServerExchange exchange;
   private final StreamSourceChannel channel;
   private int maxBufferSize = -1;
   private boolean paused = false;
   private boolean done = false;

   public AsyncReceiverImpl(HttpServerExchange exchange) {
      this.exchange = exchange;
      this.channel = exchange.getRequestChannel();
      if (this.channel == null) {
         throw UndertowMessages.MESSAGES.requestChannelAlreadyProvided();
      }
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

   public void receiveFullString(final Receiver.FullStringCallback callback, Receiver.ErrorCallback errorCallback, final Charset charset) {
      if (this.done) {
         throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
      } else {
         final Receiver.ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
         if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
         } else if (this.exchange.isRequestComplete()) {
            callback.handle(this.exchange, "");
         } else {
            String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
            long contentLength;
            final ByteArrayOutputStream sb;
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
               PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
               ByteBuffer buffer = pooled.getBuffer();

               try {
                  while(true) {
                     try {
                        buffer.clear();
                        int res = this.channel.read(buffer);
                        if (res == -1) {
                           this.done = true;
                           callback.handle(this.exchange, sb.toString(charset.name()));
                           return;
                        }

                        if (res == 0) {
                           this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
                              public void handleEvent(StreamSourceChannel channel) {
                                 if (!AsyncReceiverImpl.this.done) {
                                    PooledByteBuffer pooled = AsyncReceiverImpl.this.exchange.getConnection().getByteBufferPool().allocate();
                                    ByteBuffer buffer = pooled.getBuffer();

                                    try {
                                       while(true) {
                                          try {
                                             buffer.clear();
                                             int res = channel.read(buffer);
                                             if (res == -1) {
                                                AsyncReceiverImpl.this.done = true;
                                                Connectors.executeRootHandler(new HttpHandler() {
                                                   public void handleRequest(HttpServerExchange exchange) throws Exception {
                                                      callback.handle(exchange, sb.toString(charset.name()));
                                                   }
                                                }, AsyncReceiverImpl.this.exchange);
                                                return;
                                             }

                                             if (res == 0) {
                                                return;
                                             }

                                             buffer.flip();

                                             while(buffer.hasRemaining()) {
                                                sb.write(buffer.get());
                                             }

                                             if (AsyncReceiverImpl.this.maxBufferSize > 0 && sb.size() > AsyncReceiverImpl.this.maxBufferSize) {
                                                Connectors.executeRootHandler(new HttpHandler() {
                                                   public void handleRequest(HttpServerExchange exchange) throws Exception {
                                                      error.error(exchange, new Receiver.RequestToLargeException());
                                                   }
                                                }, AsyncReceiverImpl.this.exchange);
                                                return;
                                             }
                                          } catch (final IOException var9) {
                                             Connectors.executeRootHandler(new HttpHandler() {
                                                public void handleRequest(HttpServerExchange exchange) throws Exception {
                                                   error.error(exchange, var9);
                                                }
                                             }, AsyncReceiverImpl.this.exchange);
                                             return;
                                          }
                                       }
                                    } finally {
                                       pooled.close();
                                    }
                                 }
                              }
                           });
                           this.channel.resumeReads();
                           return;
                        }

                        buffer.flip();

                        while(buffer.hasRemaining()) {
                           sb.write(buffer.get());
                        }

                        if (this.maxBufferSize > 0 && sb.size() > this.maxBufferSize) {
                           error.error(this.exchange, new Receiver.RequestToLargeException());
                           return;
                        }
                     } catch (IOException var16) {
                        error.error(this.exchange, var16);
                        return;
                     }
                  }
               } finally {
                  pooled.close();
               }
            }
         }
      }
   }

   public void receiveFullString(Receiver.FullStringCallback callback, Charset charset) {
      this.receiveFullString(callback, END_EXCHANGE, charset);
   }

   public void receivePartialString(final Receiver.PartialStringCallback callback, Receiver.ErrorCallback errorCallback, Charset charset) {
      if (this.done) {
         throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
      } else {
         final Receiver.ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
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
               final CharsetDecoder decoder = charset.newDecoder();
               PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
               ByteBuffer buffer = pooled.getBuffer();
               this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
                  public void handleEvent(final StreamSourceChannel channel) {
                     if (!AsyncReceiverImpl.this.done && !AsyncReceiverImpl.this.paused) {
                        PooledByteBuffer pooled = AsyncReceiverImpl.this.exchange.getConnection().getByteBufferPool().allocate();
                        ByteBuffer buffer = pooled.getBuffer();

                        while(true) {
                           try {
                              if (AsyncReceiverImpl.this.paused) {
                                 return;
                              }

                              try {
                                 buffer.clear();
                                 int res = channel.read(buffer);
                                 if (res == -1) {
                                    AsyncReceiverImpl.this.done = true;
                                    Connectors.executeRootHandler(new HttpHandler() {
                                       public void handleRequest(HttpServerExchange exchange) throws Exception {
                                          callback.handle(exchange, "", true);
                                       }
                                    }, AsyncReceiverImpl.this.exchange);
                                    return;
                                 }

                                 if (res == 0) {
                                    return;
                                 }

                                 buffer.flip();
                                 final CharBuffer cb = decoder.decode(buffer);
                                 Connectors.executeRootHandler(new HttpHandler() {
                                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                                       callback.handle(exchange, cb.toString(), false);
                                       if (!AsyncReceiverImpl.this.paused) {
                                          channel.resumeReads();
                                       } else {
                                          System.out.println("paused");
                                       }

                                    }
                                 }, AsyncReceiverImpl.this.exchange);
                                 continue;
                              } catch (final IOException var9) {
                                 Connectors.executeRootHandler(new HttpHandler() {
                                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                                       error.error(exchange, var9);
                                    }
                                 }, AsyncReceiverImpl.this.exchange);
                              }
                           } finally {
                              pooled.close();
                           }

                           return;
                        }
                     }
                  }
               });

               try {
                  while(true) {
                     try {
                        buffer.clear();
                        int res = this.channel.read(buffer);
                        if (res == -1) {
                           this.done = true;
                           callback.handle(this.exchange, "", true);
                           return;
                        }

                        if (res == 0) {
                           this.channel.resumeReads();
                           return;
                        }

                        buffer.flip();
                        CharBuffer cb = decoder.decode(buffer);
                        callback.handle(this.exchange, cb.toString(), false);
                        if (this.paused) {
                           return;
                        }
                     } catch (IOException var16) {
                        error.error(this.exchange, var16);
                        return;
                     }
                  }
               } finally {
                  pooled.close();
               }
            }
         }
      }
   }

   public void receivePartialString(Receiver.PartialStringCallback callback, Charset charset) {
      this.receivePartialString(callback, END_EXCHANGE, charset);
   }

   public void receiveFullBytes(final Receiver.FullBytesCallback callback, Receiver.ErrorCallback errorCallback) {
      if (this.done) {
         throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
      } else {
         final Receiver.ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
         if (callback == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
         } else if (this.exchange.isRequestComplete()) {
            callback.handle(this.exchange, EMPTY_BYTE_ARRAY);
         } else {
            String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
            long contentLength;
            final ByteArrayOutputStream sb;
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
               PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
               ByteBuffer buffer = pooled.getBuffer();

               try {
                  while(true) {
                     try {
                        buffer.clear();
                        int res = this.channel.read(buffer);
                        if (res == -1) {
                           this.done = true;
                           callback.handle(this.exchange, sb.toByteArray());
                           return;
                        }

                        if (res == 0) {
                           this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
                              public void handleEvent(StreamSourceChannel channel) {
                                 if (!AsyncReceiverImpl.this.done) {
                                    PooledByteBuffer pooled = AsyncReceiverImpl.this.exchange.getConnection().getByteBufferPool().allocate();
                                    ByteBuffer buffer = pooled.getBuffer();

                                    try {
                                       while(true) {
                                          try {
                                             buffer.clear();
                                             int res = channel.read(buffer);
                                             if (res == -1) {
                                                AsyncReceiverImpl.this.done = true;
                                                Connectors.executeRootHandler(new HttpHandler() {
                                                   public void handleRequest(HttpServerExchange exchange) throws Exception {
                                                      callback.handle(exchange, sb.toByteArray());
                                                   }
                                                }, AsyncReceiverImpl.this.exchange);
                                                return;
                                             }

                                             if (res == 0) {
                                                return;
                                             }

                                             buffer.flip();

                                             while(buffer.hasRemaining()) {
                                                sb.write(buffer.get());
                                             }

                                             if (AsyncReceiverImpl.this.maxBufferSize > 0 && sb.size() > AsyncReceiverImpl.this.maxBufferSize) {
                                                Connectors.executeRootHandler(new HttpHandler() {
                                                   public void handleRequest(HttpServerExchange exchange) throws Exception {
                                                      error.error(exchange, new Receiver.RequestToLargeException());
                                                   }
                                                }, AsyncReceiverImpl.this.exchange);
                                                return;
                                             }
                                          } catch (final Exception var9) {
                                             Connectors.executeRootHandler(new HttpHandler() {
                                                public void handleRequest(HttpServerExchange exchange) throws Exception {
                                                   error.error(exchange, new IOException(var9));
                                                }
                                             }, AsyncReceiverImpl.this.exchange);
                                             return;
                                          }
                                       }
                                    } finally {
                                       pooled.close();
                                    }
                                 }
                              }
                           });
                           this.channel.resumeReads();
                           return;
                        }

                        buffer.flip();

                        while(buffer.hasRemaining()) {
                           sb.write(buffer.get());
                        }

                        if (this.maxBufferSize > 0 && sb.size() > this.maxBufferSize) {
                           error.error(this.exchange, new Receiver.RequestToLargeException());
                           return;
                        }
                     } catch (IOException var15) {
                        error.error(this.exchange, var15);
                        return;
                     }
                  }
               } finally {
                  pooled.close();
               }
            }
         }
      }
   }

   public void receiveFullBytes(Receiver.FullBytesCallback callback) {
      this.receiveFullBytes(callback, END_EXCHANGE);
   }

   public void receivePartialBytes(final Receiver.PartialBytesCallback callback, Receiver.ErrorCallback errorCallback) {
      if (this.done) {
         throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
      } else {
         final Receiver.ErrorCallback error = errorCallback == null ? END_EXCHANGE : errorCallback;
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
               PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
               ByteBuffer buffer = pooled.getBuffer();
               this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
                  public void handleEvent(final StreamSourceChannel channel) {
                     if (!AsyncReceiverImpl.this.done && !AsyncReceiverImpl.this.paused) {
                        PooledByteBuffer pooled = AsyncReceiverImpl.this.exchange.getConnection().getByteBufferPool().allocate();
                        ByteBuffer buffer = pooled.getBuffer();

                        try {
                           while(!AsyncReceiverImpl.this.paused) {
                              try {
                                 buffer.clear();
                                 int res = channel.read(buffer);
                                 if (res == -1) {
                                    AsyncReceiverImpl.this.done = true;
                                    Connectors.executeRootHandler(new HttpHandler() {
                                       public void handleRequest(HttpServerExchange exchange) throws Exception {
                                          callback.handle(exchange, AsyncReceiverImpl.EMPTY_BYTE_ARRAY, true);
                                       }
                                    }, AsyncReceiverImpl.this.exchange);
                                    return;
                                 }

                                 if (res == 0) {
                                    return;
                                 }

                                 buffer.flip();
                                 final byte[] data = new byte[buffer.remaining()];
                                 buffer.get(data);
                                 Connectors.executeRootHandler(new HttpHandler() {
                                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                                       callback.handle(exchange, data, false);
                                       if (!AsyncReceiverImpl.this.paused) {
                                          channel.resumeReads();
                                       }

                                    }
                                 }, AsyncReceiverImpl.this.exchange);
                              } catch (final IOException var9) {
                                 Connectors.executeRootHandler(new HttpHandler() {
                                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                                       error.error(exchange, var9);
                                    }
                                 }, AsyncReceiverImpl.this.exchange);
                                 return;
                              }
                           }
                        } finally {
                           pooled.close();
                        }

                     }
                  }
               });

               try {
                  while(true) {
                     try {
                        buffer.clear();
                        int res = this.channel.read(buffer);
                        if (res == -1) {
                           this.done = true;
                           callback.handle(this.exchange, EMPTY_BYTE_ARRAY, true);
                           return;
                        }

                        if (res == 0) {
                           this.channel.resumeReads();
                           return;
                        }

                        buffer.flip();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        callback.handle(this.exchange, data, false);
                        if (this.paused) {
                           return;
                        }
                     } catch (IOException var14) {
                        error.error(this.exchange, var14);
                        return;
                     }
                  }
               } finally {
                  pooled.close();
               }
            }
         }
      }
   }

   public void receivePartialBytes(Receiver.PartialBytesCallback callback) {
      this.receivePartialBytes(callback, END_EXCHANGE);
   }

   public void pause() {
      this.paused = true;
      this.channel.suspendReads();
   }

   public void resume() {
      this.paused = false;
      this.channel.wakeupReads();
   }
}
