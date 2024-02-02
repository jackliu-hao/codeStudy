package com.sun.mail.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UUDecoderStream extends FilterInputStream {
   private String name;
   private int mode;
   private byte[] buffer = new byte[45];
   private int bufsize = 0;
   private int index = 0;
   private boolean gotPrefix = false;
   private boolean gotEnd = false;
   private LineInputStream lin;
   private boolean ignoreErrors;
   private boolean ignoreMissingBeginEnd;
   private String readAhead;

   public UUDecoderStream(InputStream in) {
      super(in);
      this.lin = new LineInputStream(in);
      this.ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoreerrors", false);
      this.ignoreMissingBeginEnd = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoremissingbeginend", false);
   }

   public UUDecoderStream(InputStream in, boolean ignoreErrors, boolean ignoreMissingBeginEnd) {
      super(in);
      this.lin = new LineInputStream(in);
      this.ignoreErrors = ignoreErrors;
      this.ignoreMissingBeginEnd = ignoreMissingBeginEnd;
   }

   public int read() throws IOException {
      if (this.index >= this.bufsize) {
         this.readPrefix();
         if (!this.decode()) {
            return -1;
         }

         this.index = 0;
      }

      return this.buffer[this.index++] & 255;
   }

   public int read(byte[] buf, int off, int len) throws IOException {
      int i;
      for(i = 0; i < len; ++i) {
         int c;
         if ((c = this.read()) == -1) {
            if (i == 0) {
               i = -1;
            }
            break;
         }

         buf[off + i] = (byte)c;
      }

      return i;
   }

   public boolean markSupported() {
      return false;
   }

   public int available() throws IOException {
      return this.in.available() * 3 / 4 + (this.bufsize - this.index);
   }

   public String getName() throws IOException {
      this.readPrefix();
      return this.name;
   }

   public int getMode() throws IOException {
      this.readPrefix();
      return this.mode;
   }

   private void readPrefix() throws IOException {
      if (!this.gotPrefix) {
         this.mode = 438;
         this.name = "encoder.buf";

         while(true) {
            String line = this.lin.readLine();
            if (line == null) {
               if (!this.ignoreMissingBeginEnd) {
                  throw new DecodingException("UUDecoder: Missing begin");
               }

               this.gotPrefix = true;
               this.gotEnd = true;
               break;
            }

            if (line.regionMatches(false, 0, "begin", 0, 5)) {
               try {
                  this.mode = Integer.parseInt(line.substring(6, 9));
               } catch (NumberFormatException var4) {
                  if (!this.ignoreErrors) {
                     throw new DecodingException("UUDecoder: Error in mode: " + var4.toString());
                  }
               }

               if (line.length() > 10) {
                  this.name = line.substring(10);
               } else if (!this.ignoreErrors) {
                  throw new DecodingException("UUDecoder: Missing name: " + line);
               }

               this.gotPrefix = true;
               break;
            }

            if (this.ignoreMissingBeginEnd && line.length() != 0) {
               int count = line.charAt(0);
               count = count - 32 & 63;
               int need = (count * 8 + 5) / 6;
               if (need == 0 || line.length() >= need + 1) {
                  this.readAhead = line;
                  this.gotPrefix = true;
                  break;
               }
            }
         }

      }
   }

   private boolean decode() throws IOException {
      if (this.gotEnd) {
         return false;
      } else {
         this.bufsize = 0;
         int count = false;

         while(true) {
            String line;
            if (this.readAhead != null) {
               line = this.readAhead;
               this.readAhead = null;
            } else {
               line = this.lin.readLine();
            }

            if (line == null) {
               if (!this.ignoreMissingBeginEnd) {
                  throw new DecodingException("UUDecoder: Missing end at EOF");
               }

               this.gotEnd = true;
               return false;
            }

            if (line.equals("end")) {
               this.gotEnd = true;
               return false;
            }

            if (line.length() != 0) {
               int count = line.charAt(0);
               if (count < 32) {
                  if (!this.ignoreErrors) {
                     throw new DecodingException("UUDecoder: Buffer format error");
                  }
               } else {
                  count = count - 32 & 63;
                  if (count == 0) {
                     line = this.lin.readLine();
                     if ((line == null || !line.equals("end")) && !this.ignoreMissingBeginEnd) {
                        throw new DecodingException("UUDecoder: Missing End after count 0 line");
                     }

                     this.gotEnd = true;
                     return false;
                  }

                  int i = (count * 8 + 5) / 6;
                  if (line.length() >= i + 1) {
                     i = 1;

                     while(this.bufsize < count) {
                        byte a = (byte)(line.charAt(i++) - 32 & 63);
                        byte b = (byte)(line.charAt(i++) - 32 & 63);
                        this.buffer[this.bufsize++] = (byte)(a << 2 & 252 | b >>> 4 & 3);
                        if (this.bufsize < count) {
                           a = b;
                           b = (byte)(line.charAt(i++) - 32 & 63);
                           this.buffer[this.bufsize++] = (byte)(a << 4 & 240 | b >>> 2 & 15);
                        }

                        if (this.bufsize < count) {
                           a = b;
                           b = (byte)(line.charAt(i++) - 32 & 63);
                           this.buffer[this.bufsize++] = (byte)(a << 6 & 192 | b & 63);
                        }
                     }

                     return true;
                  }

                  if (!this.ignoreErrors) {
                     throw new DecodingException("UUDecoder: Short buffer error");
                  }
               }
            }
         }
      }
   }
}
