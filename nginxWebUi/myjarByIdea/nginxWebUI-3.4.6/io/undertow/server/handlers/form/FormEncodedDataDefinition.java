package io.undertow.server.handlers.form;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.URLUtils;
import io.undertow.util.UrlDecodeException;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;

public class FormEncodedDataDefinition implements FormParserFactory.ParserDefinition<FormEncodedDataDefinition> {
   public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
   private static boolean parseExceptionLogAsDebug = false;
   private String defaultEncoding = "ISO-8859-1";
   private boolean forceCreation = false;

   public FormDataParser create(HttpServerExchange exchange) {
      String mimeType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
      if (this.forceCreation || mimeType != null && mimeType.startsWith("application/x-www-form-urlencoded")) {
         String charset = this.defaultEncoding;
         String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
         if (contentType != null) {
            String cs = Headers.extractQuotedValueFromHeader(contentType, "charset");
            if (cs != null) {
               charset = cs;
            }
         }

         UndertowLogger.REQUEST_LOGGER.tracef("Created form encoded parser for %s", exchange);
         return new FormEncodedDataParser(charset, exchange);
      } else {
         return null;
      }
   }

   public String getDefaultEncoding() {
      return this.defaultEncoding;
   }

   public boolean isForceCreation() {
      return this.forceCreation;
   }

   public FormEncodedDataDefinition setForceCreation(boolean forceCreation) {
      this.forceCreation = forceCreation;
      return this;
   }

   public FormEncodedDataDefinition setDefaultEncoding(String defaultEncoding) {
      this.defaultEncoding = defaultEncoding;
      return this;
   }

   private static final class FormEncodedDataParser implements ChannelListener<StreamSourceChannel>, FormDataParser {
      private final HttpServerExchange exchange;
      private final FormData data;
      private final StringBuilder builder;
      private String name;
      private String charset;
      private HttpHandler handler;
      private int state;

      private FormEncodedDataParser(String charset, HttpServerExchange exchange) {
         this.builder = new StringBuilder();
         this.name = null;
         this.state = 0;
         this.exchange = exchange;
         this.charset = charset;
         this.data = new FormData(exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_PARAMETERS, 1000));
      }

      public void handleEvent(StreamSourceChannel channel) {
         try {
            this.doParse(channel);
            if (this.state == 4) {
               this.exchange.dispatch(SameThreadExecutor.INSTANCE, this.handler);
            }
         } catch (IOException var3) {
            IoUtils.safeClose((Closeable)channel);
            UndertowLogger.REQUEST_IO_LOGGER.ioExceptionReadingFromChannel(var3);
            this.exchange.endExchange();
         }

      }

      private void doParse(StreamSourceChannel channel) throws IOException {
         int c = false;
         PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();

         try {
            ByteBuffer buffer = pooled.getBuffer();

            int c;
            label192:
            do {
               buffer.clear();
               c = channel.read(buffer);
               if (c > 0) {
                  buffer.flip();

                  while(true) {
                     while(true) {
                        if (!buffer.hasRemaining()) {
                           continue label192;
                        }

                        byte n = buffer.get();
                        switch (this.state) {
                           case 0:
                              if (n == 61) {
                                 this.name = this.builder.toString();
                                 this.builder.setLength(0);
                                 this.state = 2;
                              } else if (n == 38) {
                                 this.addPair(this.builder.toString(), "");
                                 this.builder.setLength(0);
                                 this.state = 0;
                              } else {
                                 if (n != 37 && n != 43 && n >= 0) {
                                    this.builder.append((char)n);
                                    continue;
                                 }

                                 this.state = 1;
                                 this.builder.append((char)(n & 255));
                              }
                              break;
                           case 1:
                              if (n == 61) {
                                 this.name = this.decodeParameterName(this.builder.toString(), this.charset, true, new StringBuilder());
                                 this.builder.setLength(0);
                                 this.state = 2;
                              } else if (n == 38) {
                                 this.addPair(this.decodeParameterName(this.builder.toString(), this.charset, true, new StringBuilder()), "");
                                 this.builder.setLength(0);
                                 this.state = 0;
                              } else {
                                 this.builder.append((char)(n & 255));
                              }
                              break;
                           case 2:
                              if (n == 38) {
                                 this.addPair(this.name, this.builder.toString());
                                 this.builder.setLength(0);
                                 this.state = 0;
                              } else {
                                 if (n != 37 && n != 43 && n >= 0) {
                                    this.builder.append((char)n);
                                    continue;
                                 }

                                 this.state = 3;
                                 this.builder.append((char)(n & 255));
                              }
                              break;
                           case 3:
                              if (n == 38) {
                                 this.addPair(this.name, this.decodeParameterValue(this.name, this.builder.toString(), this.charset, true, new StringBuilder()));
                                 this.builder.setLength(0);
                                 this.state = 0;
                              } else {
                                 this.builder.append((char)(n & 255));
                              }
                        }
                     }
                  }
               }
            } while(c > 0);

            if (c == -1) {
               if (this.state == 2) {
                  this.addPair(this.name, this.builder.toString());
               } else if (this.state == 3) {
                  this.addPair(this.name, this.decodeParameterValue(this.name, this.builder.toString(), this.charset, true, new StringBuilder()));
               } else if (this.builder.length() > 0) {
                  if (this.state == 1) {
                     this.addPair(this.decodeParameterName(this.builder.toString(), this.charset, true, new StringBuilder()), "");
                  } else {
                     this.addPair(this.builder.toString(), "");
                  }
               }

               this.state = 4;
               this.exchange.putAttachment(FORM_DATA, this.data);
            }
         } finally {
            pooled.close();
         }

      }

      private void addPair(String name, String value) {
         if (name != null && value != null) {
            this.data.add(name, value);
         }

      }

      private String decodeParameterValue(String name, String value, String charset, boolean decodeSlash, StringBuilder stringBuilder) {
         String decodedValue = null;

         try {
            decodedValue = URLUtils.decode(value, charset, decodeSlash, stringBuilder);
         } catch (UrlDecodeException var8) {
            if (!FormEncodedDataDefinition.parseExceptionLogAsDebug) {
               UndertowLogger.REQUEST_LOGGER.errorf(UndertowMessages.MESSAGES.failedToDecodeParameterValue(name, value, var8), new Object[0]);
               FormEncodedDataDefinition.parseExceptionLogAsDebug = true;
            } else {
               UndertowLogger.REQUEST_LOGGER.debugf(UndertowMessages.MESSAGES.failedToDecodeParameterValue(name, value, var8), new Object[0]);
            }
         }

         return decodedValue;
      }

      private String decodeParameterName(String name, String charset, boolean decodeSlash, StringBuilder stringBuilder) {
         String decodedName = null;

         try {
            decodedName = URLUtils.decode(name, charset, decodeSlash, stringBuilder);
         } catch (UrlDecodeException var7) {
            if (!FormEncodedDataDefinition.parseExceptionLogAsDebug) {
               UndertowLogger.REQUEST_LOGGER.errorf(UndertowMessages.MESSAGES.failedToDecodeParameterName(name, var7), new Object[0]);
               FormEncodedDataDefinition.parseExceptionLogAsDebug = true;
            } else {
               UndertowLogger.REQUEST_LOGGER.debugf(UndertowMessages.MESSAGES.failedToDecodeParameterName(name, var7), new Object[0]);
            }
         }

         return decodedName;
      }

      public void parse(HttpHandler handler) throws Exception {
         if (this.exchange.getAttachment(FORM_DATA) != null) {
            handler.handleRequest(this.exchange);
         } else {
            this.handler = handler;
            StreamSourceChannel channel = this.exchange.getRequestChannel();
            if (channel == null) {
               throw new IOException(UndertowMessages.MESSAGES.requestChannelAlreadyProvided());
            } else {
               this.doParse(channel);
               if (this.state != 4) {
                  channel.getReadSetter().set(this);
                  channel.resumeReads();
               } else {
                  this.exchange.dispatch(SameThreadExecutor.INSTANCE, handler);
               }

            }
         }
      }

      public FormData parseBlocking() throws IOException {
         FormData existing = (FormData)this.exchange.getAttachment(FORM_DATA);
         if (existing != null) {
            return existing;
         } else {
            StreamSourceChannel channel = this.exchange.getRequestChannel();
            if (channel == null) {
               throw new IOException(UndertowMessages.MESSAGES.requestChannelAlreadyProvided());
            } else {
               while(this.state != 4) {
                  this.doParse(channel);
                  if (this.state != 4) {
                     channel.awaitReadable();
                  }
               }

               return this.data;
            }
         }
      }

      public void close() throws IOException {
      }

      public void setCharacterEncoding(String encoding) {
         this.charset = encoding;
      }

      // $FF: synthetic method
      FormEncodedDataParser(String x0, HttpServerExchange x1, Object x2) {
         this(x0, x1);
      }
   }
}
