package org.h2.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import org.h2.message.DbException;

public class ScriptReader implements Closeable {
   private final Reader reader;
   private char[] buffer;
   private int bufferPos;
   private int bufferStart = -1;
   private int bufferEnd;
   private boolean endOfFile;
   private boolean insideRemark;
   private boolean blockRemark;
   private boolean skipRemarks;
   private int remarkStart;

   public ScriptReader(Reader var1) {
      this.reader = var1;
      this.buffer = new char[8192];
   }

   public void close() {
      try {
         this.reader.close();
      } catch (IOException var2) {
         throw DbException.convertIOException(var2, (String)null);
      }
   }

   public String readStatement() {
      if (this.endOfFile) {
         return null;
      } else {
         try {
            return this.readStatementLoop();
         } catch (IOException var2) {
            throw DbException.convertIOException(var2, (String)null);
         }
      }
   }

   private String readStatementLoop() throws IOException {
      this.bufferStart = this.bufferPos;
      int var1 = this.read();

      while(true) {
         if (var1 < 0) {
            this.endOfFile = true;
            if (this.bufferPos - 1 == this.bufferStart) {
               return null;
            }
            break;
         }

         if (var1 == 59) {
            break;
         }

         switch (var1) {
            case 34:
               do {
                  var1 = this.read();
               } while(var1 >= 0 && var1 != 34);

               var1 = this.read();
               break;
            case 35:
            case 37:
            case 38:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 46:
            default:
               var1 = this.read();
               break;
            case 36:
               var1 = this.read();
               if (var1 != 36 || this.bufferPos - this.bufferStart >= 3 && this.buffer[this.bufferPos - 3] > ' ') {
                  break;
               }

               while(true) {
                  var1 = this.read();
                  if (var1 < 0) {
                     break;
                  }

                  if (var1 == 36) {
                     var1 = this.read();
                     if (var1 < 0 || var1 == 36) {
                        break;
                     }
                  }
               }

               var1 = this.read();
               break;
            case 39:
               do {
                  var1 = this.read();
               } while(var1 >= 0 && var1 != 39);

               var1 = this.read();
               break;
            case 45:
               var1 = this.read();
               if (var1 != 45) {
                  break;
               }

               this.startRemark(false);

               while(true) {
                  var1 = this.read();
                  if (var1 < 0) {
                     this.clearRemark();
                     break;
                  }

                  if (var1 == 13 || var1 == 10) {
                     this.endRemark();
                     break;
                  }
               }

               var1 = this.read();
               break;
            case 47:
               var1 = this.read();
               if (var1 == 42) {
                  this.startRemark(true);
                  int var2 = 1;

                  while(true) {
                     var1 = this.read();
                     if (var1 < 0) {
                        break;
                     }

                     if (var1 == 42) {
                        var1 = this.read();
                        if (var1 < 0) {
                           this.clearRemark();
                           break;
                        }

                        if (var1 == 47) {
                           --var2;
                           if (var2 == 0) {
                              this.endRemark();
                              break;
                           }
                        }
                     } else if (var1 == 47) {
                        var1 = this.read();
                        if (var1 < 0) {
                           this.clearRemark();
                           break;
                        }

                        if (var1 == 42) {
                           ++var2;
                        }
                     }
                  }

                  var1 = this.read();
               } else if (var1 == 47) {
                  this.startRemark(false);

                  while(true) {
                     var1 = this.read();
                     if (var1 < 0) {
                        this.clearRemark();
                        break;
                     }

                     if (var1 == 13 || var1 == 10) {
                        this.endRemark();
                        break;
                     }
                  }

                  var1 = this.read();
               }
         }
      }

      return new String(this.buffer, this.bufferStart, this.bufferPos - 1 - this.bufferStart);
   }

   private void startRemark(boolean var1) {
      this.blockRemark = var1;
      this.remarkStart = this.bufferPos - 2;
      this.insideRemark = true;
   }

   private void endRemark() {
      this.clearRemark();
      this.insideRemark = false;
   }

   private void clearRemark() {
      if (this.skipRemarks) {
         Arrays.fill(this.buffer, this.remarkStart, this.bufferPos, ' ');
      }

   }

   private int read() throws IOException {
      return this.bufferPos >= this.bufferEnd ? this.readBuffer() : this.buffer[this.bufferPos++];
   }

   private int readBuffer() throws IOException {
      if (this.endOfFile) {
         return -1;
      } else {
         int var1 = this.bufferPos - this.bufferStart;
         if (var1 > 0) {
            char[] var2 = this.buffer;
            if (var1 + 4096 > var2.length) {
               if (var2.length >= 1073741823) {
                  throw new IOException("Error in parsing script, statement size exceeds 1G, first 80 characters of statement looks like: " + new String(this.buffer, this.bufferStart, 80));
               }

               this.buffer = new char[var2.length * 2];
            }

            System.arraycopy(var2, this.bufferStart, this.buffer, 0, var1);
         }

         this.remarkStart -= this.bufferStart;
         this.bufferStart = 0;
         this.bufferPos = var1;
         int var3 = this.reader.read(this.buffer, var1, 4096);
         if (var3 == -1) {
            this.bufferEnd = -1024;
            this.endOfFile = true;
            ++this.bufferPos;
            return -1;
         } else {
            this.bufferEnd = var1 + var3;
            return this.buffer[this.bufferPos++];
         }
      }
   }

   public boolean isInsideRemark() {
      return this.insideRemark;
   }

   public boolean isBlockRemark() {
      return this.blockRemark;
   }

   public void setSkipRemarks(boolean var1) {
      this.skipRemarks = var1;
   }
}
