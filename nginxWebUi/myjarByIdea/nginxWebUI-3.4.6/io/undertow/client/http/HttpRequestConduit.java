package io.undertow.client.http;

import io.undertow.client.ClientRequest;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.TruncatedResponseException;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import org.jboss.logging.Logger;
import org.xnio.Bits;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

final class HttpRequestConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private static final Logger log = Logger.getLogger("io.undertow.client.request");
   private final ByteBufferPool pool;
   private int state = 2;
   private Iterator<HttpString> nameIterator;
   private String string;
   private HttpString headerName;
   private Iterator<String> valueIterator;
   private int charIndex;
   private PooledByteBuffer pooledBuffer;
   private final ClientRequest request;
   private static final int STATE_BODY = 0;
   private static final int STATE_URL = 1;
   private static final int STATE_START = 2;
   private static final int STATE_HDR_NAME = 3;
   private static final int STATE_HDR_D = 4;
   private static final int STATE_HDR_DS = 5;
   private static final int STATE_HDR_VAL = 6;
   private static final int STATE_HDR_EOL_CR = 7;
   private static final int STATE_HDR_EOL_LF = 8;
   private static final int STATE_HDR_FINAL_CR = 9;
   private static final int STATE_HDR_FINAL_LF = 10;
   private static final int STATE_BUF_FLUSH = 11;
   private static final int MASK_STATE = 15;
   private static final int FLAG_SHUTDOWN = 16;

   HttpRequestConduit(StreamSinkConduit next, ByteBufferPool pool, ClientRequest request) {
      super(next);
      this.pool = pool;
      this.request = request;
   }

   private int processWrite(int state, ByteBuffer userData) throws IOException {
      if (state == 2) {
         this.pooledBuffer = this.pool.allocate();
      }

      ClientRequest request = this.request;
      ByteBuffer buffer = this.pooledBuffer.getBuffer();
      int res;
      if (state != 2 && buffer.hasRemaining()) {
         log.trace("Flushing remaining buffer");

         do {
            res = ((StreamSinkConduit)this.next).write(buffer);
            if (res == 0) {
               return state;
            }
         } while(buffer.hasRemaining());
      }

      buffer.clear();

      label412:
      while(true) {
         ByteBuffer[] b;
         long r;
         label462: {
            label407:
            while(true) {
               label464: {
                  label404:
                  while(true) {
                     int length;
                     label466: {
                        label467: {
                           label400:
                           while(true) {
                              int i;
                              switch (state) {
                                 case 0:
                                    return state;
                                 case 1:
                                    for(i = this.charIndex; i < this.string.length(); ++i) {
                                       if (!buffer.hasRemaining()) {
                                          buffer.flip();

                                          while(true) {
                                             res = ((StreamSinkConduit)this.next).write(buffer);
                                             if (res == 0) {
                                                log.trace("Continuation");
                                                this.charIndex = i;
                                                this.state = 1;
                                                return 1;
                                             }

                                             if (!buffer.hasRemaining()) {
                                                buffer.clear();
                                                break;
                                             }
                                          }
                                       }

                                       buffer.put((byte)this.string.charAt(i));
                                    }

                                    HeaderMap headers = request.getRequestHeaders();
                                    this.nameIterator = headers.getHeaderNames().iterator();
                                    state = 3;
                                    if (!this.nameIterator.hasNext()) {
                                       log.trace("No request headers");
                                       buffer.put((byte)13).put((byte)10);
                                       buffer.flip();

                                       do {
                                          if (!buffer.hasRemaining()) {
                                             this.pooledBuffer.close();
                                             this.pooledBuffer = null;
                                             log.trace("Body");
                                             return 0;
                                          }

                                          res = ((StreamSinkConduit)this.next).write(buffer);
                                       } while(res != 0);

                                       log.trace("Continuation");
                                       return 11;
                                    }

                                    this.headerName = (HttpString)this.nameIterator.next();
                                    this.charIndex = 0;
                                    break;
                                 case 2:
                                    log.trace("Starting request");
                                    i = request.getMethod().length() + request.getPath().length() + request.getProtocol().length() + 4;
                                    if (i > buffer.remaining()) {
                                       StringBuilder sb = new StringBuilder(i);
                                       sb.append(request.getMethod().toString());
                                       sb.append(" ");
                                       sb.append(request.getPath());
                                       sb.append(" ");
                                       sb.append(request.getProtocol());
                                       sb.append("\r\n");
                                       this.string = sb.toString();
                                       this.charIndex = 0;
                                       state = 1;
                                       break;
                                    } else {
                                       assert buffer.remaining() >= 50;

                                       request.getMethod().appendTo(buffer);
                                       buffer.put((byte)32);
                                       this.string = request.getPath();
                                       length = this.string.length();

                                       for(this.charIndex = 0; this.charIndex < length; ++this.charIndex) {
                                          buffer.put((byte)this.string.charAt(this.charIndex));
                                       }

                                       buffer.put((byte)32);
                                       request.getProtocol().appendTo(buffer);
                                       buffer.put((byte)13).put((byte)10);
                                       HeaderMap headers = request.getRequestHeaders();
                                       this.nameIterator = headers.getHeaderNames().iterator();
                                       if (!this.nameIterator.hasNext()) {
                                          log.trace("No request headers");
                                          buffer.put((byte)13).put((byte)10);
                                          buffer.flip();

                                          do {
                                             if (!buffer.hasRemaining()) {
                                                this.pooledBuffer.close();
                                                this.pooledBuffer = null;
                                                log.trace("Body");
                                                return 0;
                                             }

                                             res = ((StreamSinkConduit)this.next).write(buffer);
                                          } while(res != 0);

                                          log.trace("Continuation");
                                          return 11;
                                       }

                                       this.headerName = (HttpString)this.nameIterator.next();
                                       this.charIndex = 0;
                                    }
                                 case 3:
                                    log.tracef("Processing header '%s'", (Object)this.headerName);
                                    length = this.headerName.length();

                                    while(true) {
                                       while(true) {
                                          if (this.charIndex >= length) {
                                             break label400;
                                          }

                                          if (buffer.hasRemaining()) {
                                             buffer.put(this.headerName.byteAt(this.charIndex++));
                                          } else {
                                             log.trace("Buffer flush");
                                             buffer.flip();

                                             do {
                                                res = ((StreamSinkConduit)this.next).write(buffer);
                                                if (res == 0) {
                                                   log.trace("Continuation");
                                                   return 3;
                                                }
                                             } while(buffer.hasRemaining());

                                             buffer.clear();
                                          }
                                       }
                                    }
                                 case 4:
                                    break label400;
                                 case 5:
                                    break label467;
                                 case 6:
                                    break label466;
                                 case 7:
                                    break label404;
                                 case 8:
                                    break label464;
                                 case 9:
                                    break label407;
                                 case 10:
                                    break label462;
                                 case 11:
                                    break label412;
                                 default:
                                    throw new IllegalStateException();
                              }
                           }

                           if (!buffer.hasRemaining()) {
                              buffer.flip();

                              while(true) {
                                 res = ((StreamSinkConduit)this.next).write(buffer);
                                 if (res == 0) {
                                    log.trace("Continuation");
                                    return 4;
                                 }

                                 if (!buffer.hasRemaining()) {
                                    buffer.clear();
                                    break;
                                 }
                              }
                           }

                           buffer.put((byte)58);
                        }

                        if (!buffer.hasRemaining()) {
                           buffer.flip();

                           while(true) {
                              res = ((StreamSinkConduit)this.next).write(buffer);
                              if (res == 0) {
                                 log.trace("Continuation");
                                 return 5;
                              }

                              if (!buffer.hasRemaining()) {
                                 buffer.clear();
                                 break;
                              }
                           }
                        }

                        buffer.put((byte)32);
                        if (this.valueIterator == null) {
                           this.valueIterator = request.getRequestHeaders().get(this.headerName).iterator();
                        }

                        assert this.valueIterator.hasNext();

                        this.string = (String)this.valueIterator.next();
                        this.charIndex = 0;
                     }

                     log.tracef("Processing header value '%s'", (Object)this.string);
                     length = this.string.length();

                     while(true) {
                        while(this.charIndex < length) {
                           if (buffer.hasRemaining()) {
                              buffer.put((byte)this.string.charAt(this.charIndex++));
                           } else {
                              buffer.flip();

                              do {
                                 res = ((StreamSinkConduit)this.next).write(buffer);
                                 if (res == 0) {
                                    log.trace("Continuation");
                                    return 6;
                                 }
                              } while(buffer.hasRemaining());

                              buffer.clear();
                           }
                        }

                        this.charIndex = 0;
                        if (this.valueIterator.hasNext()) {
                           break label404;
                        }

                        if (!buffer.hasRemaining()) {
                           buffer.flip();

                           do {
                              res = ((StreamSinkConduit)this.next).write(buffer);
                              if (res == 0) {
                                 log.trace("Continuation");
                                 return 7;
                              }
                           } while(buffer.hasRemaining());

                           buffer.clear();
                        }

                        buffer.put((byte)13);
                        if (!buffer.hasRemaining()) {
                           buffer.flip();

                           do {
                              res = ((StreamSinkConduit)this.next).write(buffer);
                              if (res == 0) {
                                 log.trace("Continuation");
                                 return 8;
                              }
                           } while(buffer.hasRemaining());

                           buffer.clear();
                        }

                        buffer.put((byte)10);
                        if (!this.nameIterator.hasNext()) {
                           if (!buffer.hasRemaining()) {
                              buffer.flip();

                              do {
                                 res = ((StreamSinkConduit)this.next).write(buffer);
                                 if (res == 0) {
                                    log.trace("Continuation");
                                    return 9;
                                 }
                              } while(buffer.hasRemaining());

                              buffer.clear();
                           }

                           buffer.put((byte)13);
                           if (!buffer.hasRemaining()) {
                              buffer.flip();

                              do {
                                 res = ((StreamSinkConduit)this.next).write(buffer);
                                 if (res == 0) {
                                    log.trace("Continuation");
                                    return 10;
                                 }
                              } while(buffer.hasRemaining());

                              buffer.clear();
                           }

                           buffer.put((byte)10);
                           this.nameIterator = null;
                           this.valueIterator = null;
                           this.string = null;
                           buffer.flip();
                           if (userData == null) {
                              do {
                                 res = ((StreamSinkConduit)this.next).write(buffer);
                                 if (res == 0) {
                                    log.trace("Continuation");
                                    return 11;
                                 }
                              } while(buffer.hasRemaining());
                           } else {
                              b = new ByteBuffer[]{buffer, userData};

                              do {
                                 r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
                                 if (r == 0L && buffer.hasRemaining()) {
                                    log.trace("Continuation");
                                    return 11;
                                 }
                              } while(buffer.hasRemaining());
                           }

                           this.pooledBuffer.close();
                           this.pooledBuffer = null;
                           log.trace("Body");
                           return 0;
                        }

                        this.headerName = (HttpString)this.nameIterator.next();
                        this.valueIterator = null;
                        state = 3;
                        break;
                     }
                  }

                  if (!buffer.hasRemaining()) {
                     buffer.flip();

                     while(true) {
                        res = ((StreamSinkConduit)this.next).write(buffer);
                        if (res == 0) {
                           log.trace("Continuation");
                           return 7;
                        }

                        if (!buffer.hasRemaining()) {
                           buffer.clear();
                           break;
                        }
                     }
                  }

                  buffer.put((byte)13);
               }

               if (!buffer.hasRemaining()) {
                  buffer.flip();

                  while(true) {
                     res = ((StreamSinkConduit)this.next).write(buffer);
                     if (res == 0) {
                        log.trace("Continuation");
                        return 8;
                     }

                     if (!buffer.hasRemaining()) {
                        buffer.clear();
                        break;
                     }
                  }
               }

               buffer.put((byte)10);
               if (this.valueIterator != null && this.valueIterator.hasNext()) {
                  state = 3;
               } else {
                  if (!this.nameIterator.hasNext()) {
                     break;
                  }

                  this.headerName = (HttpString)this.nameIterator.next();
                  this.valueIterator = null;
                  state = 3;
               }
            }

            if (!buffer.hasRemaining()) {
               buffer.flip();

               while(true) {
                  res = ((StreamSinkConduit)this.next).write(buffer);
                  if (res == 0) {
                     log.trace("Continuation");
                     return 9;
                  }

                  if (!buffer.hasRemaining()) {
                     buffer.clear();
                     break;
                  }
               }
            }

            buffer.put((byte)13);
         }

         if (!buffer.hasRemaining()) {
            buffer.flip();

            while(true) {
               res = ((StreamSinkConduit)this.next).write(buffer);
               if (res == 0) {
                  log.trace("Continuation");
                  return 10;
               }

               if (!buffer.hasRemaining()) {
                  buffer.clear();
                  break;
               }
            }
         }

         buffer.put((byte)10);
         this.nameIterator = null;
         this.valueIterator = null;
         this.string = null;
         buffer.flip();
         if (userData == null) {
            while(true) {
               res = ((StreamSinkConduit)this.next).write(buffer);
               if (res == 0) {
                  log.trace("Continuation");
                  return 11;
               }

               if (!buffer.hasRemaining()) {
                  break label412;
               }
            }
         } else {
            b = new ByteBuffer[]{buffer, userData};

            while(true) {
               r = ((StreamSinkConduit)this.next).write(b, 0, b.length);
               if (r == 0L && buffer.hasRemaining()) {
                  log.trace("Continuation");
                  return 11;
               }

               if (!buffer.hasRemaining()) {
                  break label412;
               }
            }
         }
      }

      this.pooledBuffer.close();
      this.pooledBuffer = null;
      return 0;
   }

   public int write(ByteBuffer src) throws IOException {
      log.trace("write");
      int oldState = this.state;
      int state = oldState & 15;
      int alreadyWritten = 0;
      int originalRemaining = -1;

      int var6;
      try {
         if (state != 0) {
            originalRemaining = src.remaining();
            state = this.processWrite(state, src);
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

         if (alreadyWritten == originalRemaining) {
            var6 = alreadyWritten;
            return var6;
         }

         var6 = ((StreamSinkConduit)this.next).write(src) + alreadyWritten;
      } catch (RuntimeException | Error | IOException var10) {
         this.state |= 16;
         if (this.pooledBuffer != null) {
            this.pooledBuffer.close();
            this.pooledBuffer = null;
         }

         throw var10;
      } finally {
         this.state = oldState & -16 | state;
      }

      return var6;
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.write(srcs, 0, srcs.length);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      log.trace("write");
      if (length == 0) {
         return 0L;
      } else {
         int oldVal = this.state;
         int state = oldVal & 15;

         try {
            long var6;
            if (state != 0) {
               state = this.processWrite(state, (ByteBuffer)null);
               if (state != 0) {
                  var6 = 0L;
                  return var6;
               }

               if (Bits.allAreSet(oldVal, 16)) {
                  ((StreamSinkConduit)this.next).terminateWrites();
                  throw new ClosedChannelException();
               }
            }

            var6 = length == 1 ? (long)((StreamSinkConduit)this.next).write(srcs[offset]) : ((StreamSinkConduit)this.next).write(srcs, offset, length);
            return var6;
         } catch (RuntimeException | Error | IOException var11) {
            this.state |= 16;
            if (this.pooledBuffer != null) {
               this.pooledBuffer.close();
               this.pooledBuffer = null;
            }

            throw var11;
         } finally {
            this.state = oldVal & -16 | state;
         }
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      log.trace("transfer");
      if (count == 0L) {
         return 0L;
      } else {
         int oldVal = this.state;
         int state = oldVal & 15;

         try {
            long var8;
            if (state != 0) {
               state = this.processWrite(state, (ByteBuffer)null);
               if (state != 0) {
                  var8 = 0L;
                  return var8;
               }

               if (Bits.allAreSet(oldVal, 16)) {
                  ((StreamSinkConduit)this.next).terminateWrites();
                  throw new ClosedChannelException();
               }
            }

            var8 = ((StreamSinkConduit)this.next).transferFrom(src, position, count);
            return var8;
         } catch (RuntimeException | Error | IOException var13) {
            this.state |= 16;
            if (this.pooledBuffer != null) {
               this.pooledBuffer.close();
               this.pooledBuffer = null;
            }

            throw var13;
         } finally {
            this.state = oldVal & -16 | state;
         }
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      log.trace("transfer");
      if (count == 0L) {
         throughBuffer.clear().limit(0);
         return 0L;
      } else {
         int oldVal = this.state;
         int state = oldVal & 15;

         long var7;
         try {
            if (state != 0) {
               state = this.processWrite(state, (ByteBuffer)null);
               if (state != 0) {
                  var7 = 0L;
                  return var7;
               }

               if (Bits.allAreSet(oldVal, 16)) {
                  ((StreamSinkConduit)this.next).terminateWrites();
                  throw new ClosedChannelException();
               }
            }

            var7 = ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
         } catch (RuntimeException | Error | IOException var12) {
            this.state |= 16;
            if (this.pooledBuffer != null) {
               this.pooledBuffer.close();
               this.pooledBuffer = null;
            }

            throw var12;
         } finally {
            this.state = oldVal & -16 | state;
         }

         return var7;
      }
   }

   public boolean flush() throws IOException {
      log.trace("flush");
      int oldVal = this.state;
      int state = oldVal & 15;

      try {
         boolean var3;
         if (state != 0) {
            state = this.processWrite(state, (ByteBuffer)null);
            if (state != 0) {
               log.trace("Flush false because headers aren't written yet");
               var3 = false;
               return var3;
            }

            if (Bits.allAreSet(oldVal, 16)) {
               ((StreamSinkConduit)this.next).terminateWrites();
            }
         }

         log.trace("Delegating flush");
         var3 = ((StreamSinkConduit)this.next).flush();
         return var3;
      } catch (RuntimeException | Error | IOException var7) {
         this.state |= 16;
         if (this.pooledBuffer != null) {
            this.pooledBuffer.close();
            this.pooledBuffer = null;
         }

         throw var7;
      } finally {
         this.state = oldVal & -16 | state;
      }
   }

   public void terminateWrites() throws IOException {
      log.trace("shutdown");
      int oldVal = this.state;
      if (Bits.allAreClear(oldVal, 15)) {
         ((StreamSinkConduit)this.next).terminateWrites();
      } else {
         this.state = oldVal | 16;
      }
   }

   public void truncateWrites() throws IOException {
      log.trace("close");
      int oldVal = this.state;
      if (Bits.allAreClear(oldVal, 15)) {
         try {
            ((StreamSinkConduit)this.next).truncateWrites();
         } finally {
            if (this.pooledBuffer != null) {
               this.pooledBuffer.close();
               this.pooledBuffer = null;
            }

         }

      } else {
         this.state = oldVal & -16 | 16;
         throw new TruncatedResponseException();
      }
   }

   public XnioWorker getWorker() {
      return ((StreamSinkConduit)this.next).getWorker();
   }

   public void freeBuffers() {
      if (this.pooledBuffer != null) {
         this.pooledBuffer.close();
         this.pooledBuffer = null;
         this.state = this.state & -16 | 16;
      }

   }
}
