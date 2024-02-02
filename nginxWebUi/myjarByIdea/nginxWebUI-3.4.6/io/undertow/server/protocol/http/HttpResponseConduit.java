package io.undertow.server.protocol.http;

import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import io.undertow.util.Protocols;
import io.undertow.util.StatusCodes;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

final class HttpResponseConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final ByteBufferPool pool;
   private final HttpServerConnection connection;
   private int state = 1;
   private long fiCookie = -1L;
   private String string;
   private HeaderValues headerValues;
   private int valueIdx;
   private int charIndex;
   private PooledByteBuffer pooledBuffer;
   private PooledByteBuffer pooledFileTransferBuffer;
   private HttpServerExchange exchange;
   private ByteBuffer[] writevBuffer;
   private boolean done = false;
   private static final int STATE_BODY = 0;
   private static final int STATE_START = 1;
   private static final int STATE_HDR_NAME = 2;
   private static final int STATE_HDR_D = 3;
   private static final int STATE_HDR_DS = 4;
   private static final int STATE_HDR_VAL = 5;
   private static final int STATE_HDR_EOL_CR = 6;
   private static final int STATE_HDR_EOL_LF = 7;
   private static final int STATE_HDR_FINAL_CR = 8;
   private static final int STATE_HDR_FINAL_LF = 9;
   private static final int STATE_BUF_FLUSH = 10;
   private static final int MASK_STATE = 15;
   private static final int FLAG_SHUTDOWN = 16;

   HttpResponseConduit(StreamSinkConduit next, ByteBufferPool pool, HttpServerConnection connection) {
      super(next);
      this.pool = pool;
      this.connection = connection;
   }

   HttpResponseConduit(StreamSinkConduit next, ByteBufferPool pool, HttpServerConnection connection, HttpServerExchange exchange) {
      super(next);
      this.pool = pool;
      this.connection = connection;
      this.exchange = exchange;
   }

   void reset(HttpServerExchange exchange) {
      this.exchange = exchange;
      this.state = 1;
      this.fiCookie = -1L;
      this.string = null;
      this.headerValues = null;
      this.valueIdx = 0;
      this.charIndex = 0;
   }

   private int processWrite(int state, Object userData, int pos, int length) throws IOException {
      if (!this.done && this.exchange != null) {
         try {
            assert state != 0;

            ByteBuffer buffer;
            if (state == 10) {
               buffer = this.pooledBuffer.getBuffer();

               do {
                  long res = 0L;
                  if (userData != null && length != 0) {
                     ByteBuffer[] data;
                     if (userData instanceof ByteBuffer) {
                        data = this.writevBuffer;
                        if (data == null) {
                           data = this.writevBuffer = new ByteBuffer[2];
                        }

                        data[0] = buffer;
                        data[1] = (ByteBuffer)userData;
                        res = ((StreamSinkConduit)this.next).write(data, 0, 2);
                     } else {
                        data = this.writevBuffer;
                        if (data == null || data.length < length + 1) {
                           data = this.writevBuffer = new ByteBuffer[length + 1];
                        }

                        data[0] = buffer;
                        System.arraycopy(userData, pos, data, 1, length);
                        res = ((StreamSinkConduit)this.next).write(data, 0, length + 1);
                     }
                  } else {
                     res = (long)((StreamSinkConduit)this.next).write(buffer);
                  }

                  if (res == 0L) {
                     return 10;
                  }
               } while(buffer.hasRemaining());

               this.bufferDone();
               return 0;
            } else if (state != 1) {
               return this.processStatefulWrite(state, userData, pos, length);
            } else {
               Connectors.flattenCookies(this.exchange);
               if (this.pooledBuffer == null) {
                  this.pooledBuffer = this.pool.allocate();
               }

               buffer = this.pooledBuffer.getBuffer();

               assert buffer.remaining() >= 50;

               Protocols.HTTP_1_1.appendTo(buffer);
               buffer.put((byte)32);
               int code = this.exchange.getStatusCode();

               assert 999 >= code && code >= 100;

               buffer.put((byte)(code / 100 + 48));
               buffer.put((byte)(code / 10 % 10 + 48));
               buffer.put((byte)(code % 10 + 48));
               buffer.put((byte)32);
               String string = this.exchange.getReasonPhrase();
               if (string == null) {
                  string = StatusCodes.getReason(code);
               }

               if (string.length() > buffer.remaining()) {
                  this.pooledBuffer.close();
                  this.pooledBuffer = null;
                  this.truncateWrites();
                  throw UndertowMessages.MESSAGES.reasonPhraseToLargeForBuffer(string);
               } else {
                  writeString(buffer, string);
                  buffer.put((byte)13).put((byte)10);
                  int remaining = buffer.remaining();
                  HeaderMap headers = this.exchange.getResponseHeaders();

                  for(long fiCookie = headers.fastIterateNonEmpty(); fiCookie != -1L; fiCookie = headers.fiNextNonEmpty(fiCookie)) {
                     HeaderValues headerValues = headers.fiCurrent(fiCookie);
                     HttpString header = headerValues.getHeaderName();
                     int headerSize = header.length();
                     int valueIdx = 0;

                     while(valueIdx < headerValues.size()) {
                        remaining -= headerSize + 2;
                        if (remaining < 0) {
                           this.fiCookie = fiCookie;
                           this.string = string;
                           this.headerValues = headerValues;
                           this.valueIdx = valueIdx;
                           this.charIndex = 0;
                           this.state = 2;
                           buffer.flip();
                           return this.processStatefulWrite(2, userData, pos, length);
                        }

                        header.appendTo(buffer);
                        buffer.put((byte)58).put((byte)32);
                        string = headerValues.get(valueIdx++);
                        remaining -= string.length() + 2;
                        if (remaining < 2) {
                           this.fiCookie = fiCookie;
                           this.string = string;
                           this.headerValues = headerValues;
                           this.valueIdx = valueIdx;
                           this.charIndex = 0;
                           this.state = 5;
                           buffer.flip();
                           return this.processStatefulWrite(5, userData, pos, length);
                        }

                        writeString(buffer, string);
                        buffer.put((byte)13).put((byte)10);
                     }
                  }

                  buffer.put((byte)13).put((byte)10);
                  buffer.flip();

                  do {
                     long res = 0L;
                     if (userData == null) {
                        res = (long)((StreamSinkConduit)this.next).write(buffer);
                     } else {
                        ByteBuffer[] data;
                        if (userData instanceof ByteBuffer) {
                           data = this.writevBuffer;
                           if (data == null) {
                              data = this.writevBuffer = new ByteBuffer[2];
                           }

                           data[0] = buffer;
                           data[1] = (ByteBuffer)userData;
                           res = ((StreamSinkConduit)this.next).write(data, 0, 2);
                        } else {
                           data = this.writevBuffer;
                           if (data == null || data.length < length + 1) {
                              data = this.writevBuffer = new ByteBuffer[length + 1];
                           }

                           data[0] = buffer;
                           System.arraycopy(userData, pos, data, 1, length);
                           res = ((StreamSinkConduit)this.next).write(data, 0, length + 1);
                        }
                     }

                     if (res == 0L) {
                        return 10;
                     }
                  } while(buffer.hasRemaining());

                  this.bufferDone();
                  return 0;
               }
            }
         } catch (RuntimeException | Error | IOException var16) {
            if (this.pooledBuffer != null) {
               this.pooledBuffer.close();
               this.pooledBuffer = null;
            }

            throw var16;
         }
      } else {
         throw new ClosedChannelException();
      }
   }

   private void bufferDone() {
      if (this.exchange != null) {
         HttpServerConnection connection = (HttpServerConnection)this.exchange.getConnection();
         if (connection.getExtraBytes() != null && connection.isOpen() && this.exchange.isRequestComplete()) {
            this.pooledBuffer.getBuffer().clear();
         } else {
            this.pooledBuffer.close();
            this.pooledBuffer = null;
            this.exchange = null;
         }

      }
   }

   public void freeContinueResponse() {
      if (this.pooledBuffer != null) {
         this.pooledBuffer.close();
         this.pooledBuffer = null;
      }

   }

   private static void writeString(ByteBuffer buffer, String string) {
      int length = string.length();

      for(int charIndex = 0; charIndex < length; ++charIndex) {
         char c = string.charAt(charIndex);
         byte b = (byte)c;
         if (b != 13 && b != 10) {
            buffer.put(b);
         } else {
            buffer.put((byte)32);
         }
      }

   }

   private int processStatefulWrite(int state, Object userData, int pos, int len) throws IOException {
      ByteBuffer buffer = this.pooledBuffer.getBuffer();
      long fiCookie = this.fiCookie;
      int valueIdx = this.valueIdx;
      int charIndex = this.charIndex;
      String string = this.string;
      HeaderValues headerValues = this.headerValues;
      int res;
      if (buffer.hasRemaining()) {
         do {
            res = ((StreamSinkConduit)this.next).write(buffer);
            if (res == 0) {
               return state;
            }
         } while(buffer.hasRemaining());
      }

      buffer.clear();
      HeaderMap headers = this.exchange.getResponseHeaders();

      label279: {
         long r;
         ByteBuffer[] b;
         label280: {
            label266:
            while(true) {
               label282: {
                  label263:
                  while(true) {
                     int length;
                     label295: {
                        switch (state) {
                           case 2:
                              HttpString headerName = headerValues.getHeaderName();
                              length = headerName.length();

                              label242:
                              while(true) {
                                 while(true) {
                                    if (charIndex >= length) {
                                       break label242;
                                    }

                                    if (buffer.hasRemaining()) {
                                       buffer.put(headerName.byteAt(charIndex++));
                                    } else {
                                       buffer.flip();

                                       do {
                                          res = ((StreamSinkConduit)this.next).write(buffer);
                                          if (res == 0) {
                                             this.string = string;
                                             this.headerValues = headerValues;
                                             this.charIndex = charIndex;
                                             this.fiCookie = fiCookie;
                                             this.valueIdx = valueIdx;
                                             return 2;
                                          }
                                       } while(buffer.hasRemaining());

                                       buffer.clear();
                                    }
                                 }
                              }
                           case 3:
                              if (!buffer.hasRemaining()) {
                                 buffer.flip();

                                 while(true) {
                                    res = ((StreamSinkConduit)this.next).write(buffer);
                                    if (res == 0) {
                                       this.string = string;
                                       this.headerValues = headerValues;
                                       this.charIndex = charIndex;
                                       this.fiCookie = fiCookie;
                                       this.valueIdx = valueIdx;
                                       return 3;
                                    }

                                    if (!buffer.hasRemaining()) {
                                       buffer.clear();
                                       break;
                                    }
                                 }
                              }

                              buffer.put((byte)58);
                           case 4:
                              break;
                           case 5:
                              break label295;
                           case 6:
                              break label263;
                           case 7:
                              break label282;
                           case 8:
                              break label266;
                           case 9:
                              break label280;
                           case 10:
                              break label279;
                           default:
                              throw new IllegalStateException();
                        }

                        if (!buffer.hasRemaining()) {
                           buffer.flip();

                           while(true) {
                              res = ((StreamSinkConduit)this.next).write(buffer);
                              if (res == 0) {
                                 this.string = string;
                                 this.headerValues = headerValues;
                                 this.charIndex = charIndex;
                                 this.fiCookie = fiCookie;
                                 this.valueIdx = valueIdx;
                                 return 4;
                              }

                              if (!buffer.hasRemaining()) {
                                 buffer.clear();
                                 break;
                              }
                           }
                        }

                        buffer.put((byte)32);
                        string = headerValues.get(valueIdx++);
                        charIndex = 0;
                     }

                     length = string.length();

                     while(true) {
                        while(charIndex < length) {
                           if (buffer.hasRemaining()) {
                              buffer.put((byte)string.charAt(charIndex++));
                           } else {
                              buffer.flip();

                              do {
                                 res = ((StreamSinkConduit)this.next).write(buffer);
                                 if (res == 0) {
                                    this.string = string;
                                    this.headerValues = headerValues;
                                    this.charIndex = charIndex;
                                    this.fiCookie = fiCookie;
                                    this.valueIdx = valueIdx;
                                    return 5;
                                 }
                              } while(buffer.hasRemaining());

                              buffer.clear();
                           }
                        }

                        charIndex = 0;
                        if (valueIdx != headerValues.size()) {
                           break label263;
                        }

                        if (!buffer.hasRemaining() && this.flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
                           return 6;
                        }

                        buffer.put((byte)13);
                        if (!buffer.hasRemaining() && this.flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
                           return 7;
                        }

                        buffer.put((byte)10);
                        if ((fiCookie = headers.fiNextNonEmpty(fiCookie)) == -1L) {
                           if (!buffer.hasRemaining() && this.flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
                              return 8;
                           }

                           buffer.put((byte)13);
                           if (!buffer.hasRemaining() && this.flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
                              return 9;
                           }

                           buffer.put((byte)10);
                           this.fiCookie = -1L;
                           this.valueIdx = 0;
                           this.string = null;
                           buffer.flip();
                           if (userData == null) {
                              do {
                                 res = ((StreamSinkConduit)this.next).write(buffer);
                                 if (res == 0) {
                                    return 10;
                                 }
                              } while(buffer.hasRemaining());
                           } else if (userData instanceof ByteBuffer) {
                              b = new ByteBuffer[]{buffer, (ByteBuffer)userData};

                              do {
                                 r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
                                 if (r == 0L && buffer.hasRemaining()) {
                                    return 10;
                                 }
                              } while(buffer.hasRemaining());
                           } else {
                              b = new ByteBuffer[1 + len];
                              b[0] = buffer;
                              System.arraycopy(userData, pos, b, 1, len);

                              do {
                                 r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
                                 if (r == 0L && buffer.hasRemaining()) {
                                    return 10;
                                 }
                              } while(buffer.hasRemaining());
                           }

                           this.bufferDone();
                           return 0;
                        }

                        headerValues = headers.fiCurrent(fiCookie);
                        valueIdx = 0;
                        state = 2;
                        break;
                     }
                  }

                  if (!buffer.hasRemaining() && this.flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
                     return 6;
                  }

                  buffer.put((byte)13);
               }

               if (!buffer.hasRemaining() && this.flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
                  return 7;
               }

               buffer.put((byte)10);
               if (valueIdx < headerValues.size()) {
                  state = 2;
               } else {
                  if ((fiCookie = headers.fiNextNonEmpty(fiCookie)) != -1L) {
                     headerValues = headers.fiCurrent(fiCookie);
                     valueIdx = 0;
                     state = 2;
                     continue;
                  }
                  break;
               }
            }

            if (!buffer.hasRemaining() && this.flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
               return 8;
            }

            buffer.put((byte)13);
         }

         if (!buffer.hasRemaining() && this.flushHeaderBuffer(buffer, string, headerValues, charIndex, fiCookie, valueIdx)) {
            return 9;
         }

         buffer.put((byte)10);
         this.fiCookie = -1L;
         this.valueIdx = 0;
         this.string = null;
         buffer.flip();
         if (userData == null) {
            do {
               res = ((StreamSinkConduit)this.next).write(buffer);
               if (res == 0) {
                  return 10;
               }
            } while(buffer.hasRemaining());
         } else if (userData instanceof ByteBuffer) {
            b = new ByteBuffer[]{buffer, (ByteBuffer)userData};

            do {
               r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
               if (r == 0L && buffer.hasRemaining()) {
                  return 10;
               }
            } while(buffer.hasRemaining());
         } else {
            b = new ByteBuffer[1 + len];
            b[0] = buffer;
            System.arraycopy(userData, pos, b, 1, len);

            do {
               r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
               if (r == 0L && buffer.hasRemaining()) {
                  return 10;
               }
            } while(buffer.hasRemaining());
         }
      }

      this.bufferDone();
      return 0;
   }

   private boolean flushHeaderBuffer(ByteBuffer buffer, String string, HeaderValues headerValues, int charIndex, long fiCookie, int valueIdx) throws IOException {
      buffer.flip();

      do {
         int res = ((StreamSinkConduit)this.next).write(buffer);
         if (res == 0) {
            this.string = string;
            this.headerValues = headerValues;
            this.charIndex = charIndex;
            this.fiCookie = fiCookie;
            this.valueIdx = valueIdx;
            return true;
         }
      } while(buffer.hasRemaining());

      buffer.clear();
      return false;
   }

   public int write(ByteBuffer src) throws IOException {
      try {
         int oldState = this.state;
         int state = oldState & 15;
         int alreadyWritten = 0;
         int originalRemaining = -1;

         try {
            if (state != 0) {
               originalRemaining = src.remaining();
               state = this.processWrite(state, src, -1, -1);
               if (state != 0) {
                  byte var12 = 0;
                  return var12;
               }

               alreadyWritten = originalRemaining - src.remaining();
               if (Bits.allAreSet(oldState, 16)) {
                  ((StreamSinkConduit)this.next).terminateWrites();
                  throw new ClosedChannelException();
               }
            }

            int var6;
            if (alreadyWritten != originalRemaining) {
               var6 = ((StreamSinkConduit)this.next).write(src) + alreadyWritten;
               return var6;
            } else {
               var6 = alreadyWritten;
               return var6;
            }
         } finally {
            this.state = oldState & -16 | state;
         }
      } catch (RuntimeException | Error | IOException var11) {
         IoUtils.safeClose((Closeable)this.connection);
         throw var11;
      }
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.write(srcs, 0, srcs.length);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (length == 0) {
         return 0L;
      } else {
         int oldVal = this.state;
         int state = oldVal & 15;

         long var10;
         try {
            long rem;
            if (state == 0) {
               rem = length == 1 ? (long)((StreamSinkConduit)this.next).write(srcs[offset]) : ((StreamSinkConduit)this.next).write(srcs, offset, length);
               return rem;
            }

            rem = Buffers.remaining(srcs, offset, length);
            state = this.processWrite(state, srcs, offset, length);
            long ret = rem - Buffers.remaining(srcs, offset, length);
            if (state == 0) {
               if (Bits.allAreSet(oldVal, 16)) {
                  ((StreamSinkConduit)this.next).terminateWrites();
                  throw new ClosedChannelException();
               }

               var10 = ret;
               return var10;
            }

            var10 = ret;
         } catch (RuntimeException | Error | IOException var15) {
            IoUtils.safeClose((Closeable)this.connection);
            throw var15;
         } finally {
            this.state = oldVal & -16 | state;
         }

         return var10;
      }
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      try {
         if (this.pooledFileTransferBuffer != null) {
            long var24;
            try {
               var24 = (long)this.write(this.pooledFileTransferBuffer.getBuffer());
            } catch (RuntimeException | Error | IOException var20) {
               if (this.pooledFileTransferBuffer != null) {
                  this.pooledFileTransferBuffer.close();
                  this.pooledFileTransferBuffer = null;
               }

               throw var20;
            } finally {
               if (this.pooledFileTransferBuffer != null && !this.pooledFileTransferBuffer.getBuffer().hasRemaining()) {
                  this.pooledFileTransferBuffer.close();
                  this.pooledFileTransferBuffer = null;
               }

            }

            return var24;
         } else if (this.state != 0) {
            PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
            ByteBuffer buffer = pooled.getBuffer();

            long var9;
            try {
               int res = src.read(buffer);
               buffer.flip();
               if (res <= 0) {
                  var9 = (long)res;
                  return var9;
               }

               var9 = (long)this.write(buffer);
            } finally {
               if (buffer.hasRemaining()) {
                  this.pooledFileTransferBuffer = pooled;
               } else {
                  pooled.close();
               }

            }

            return var9;
         } else {
            return ((StreamSinkConduit)this.next).transferFrom(src, position, count);
         }
      } catch (RuntimeException | Error | IOException var23) {
         IoUtils.safeClose((Closeable)this.connection);
         throw var23;
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      try {
         return this.state != 0 ? IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this)) : ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
      } catch (RuntimeException | Error | IOException var6) {
         IoUtils.safeClose((Closeable)this.connection);
         throw var6;
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      try {
         return Conduits.writeFinalBasic(this, src);
      } catch (RuntimeException | Error | IOException var3) {
         IoUtils.safeClose((Closeable)this.connection);
         throw var3;
      }
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      try {
         return Conduits.writeFinalBasic(this, srcs, offset, length);
      } catch (RuntimeException | Error | IOException var5) {
         IoUtils.safeClose((Closeable)this.connection);
         throw var5;
      }
   }

   public boolean flush() throws IOException {
      int oldVal = this.state;
      int state = oldVal & 15;

      try {
         boolean var3;
         if (state != 0) {
            state = this.processWrite(state, (Object)null, -1, -1);
            if (state != 0) {
               var3 = false;
               return var3;
            }

            if (Bits.allAreSet(oldVal, 16)) {
               ((StreamSinkConduit)this.next).terminateWrites();
            }
         }

         var3 = ((StreamSinkConduit)this.next).flush();
         return var3;
      } catch (RuntimeException | Error | IOException var7) {
         IoUtils.safeClose((Closeable)this.connection);
         throw var7;
      } finally {
         this.state = oldVal & -16 | state;
      }
   }

   public void terminateWrites() throws IOException {
      try {
         int oldVal = this.state;
         if (Bits.allAreClear(oldVal, 15)) {
            ((StreamSinkConduit)this.next).terminateWrites();
         } else {
            this.state = oldVal | 16;
         }
      } catch (RuntimeException | Error | IOException var2) {
         IoUtils.safeClose((Closeable)this.connection);
         throw var2;
      }
   }

   public void truncateWrites() throws IOException {
      try {
         ((StreamSinkConduit)this.next).truncateWrites();
      } catch (RuntimeException | Error | IOException var5) {
         IoUtils.safeClose((Closeable)this.connection);
         throw var5;
      } finally {
         if (this.pooledBuffer != null) {
            this.bufferDone();
         }

         if (this.pooledFileTransferBuffer != null) {
            this.pooledFileTransferBuffer.close();
            this.pooledFileTransferBuffer = null;
         }

      }

   }

   public XnioWorker getWorker() {
      return ((StreamSinkConduit)this.next).getWorker();
   }

   void freeBuffers() {
      this.done = true;
      if (this.pooledBuffer != null) {
         this.bufferDone();
      }

      if (this.pooledFileTransferBuffer != null) {
         this.pooledFileTransferBuffer.close();
         this.pooledFileTransferBuffer = null;
      }

   }
}
