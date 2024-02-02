package io.undertow.servlet.spec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Locale;
import javax.servlet.DispatcherType;

public class ServletPrintWriter {
   private static final char[] EMPTY_CHAR = new char[0];
   private final ServletOutputStreamImpl outputStream;
   private final String charset;
   private CharsetEncoder charsetEncoder;
   private boolean error = false;
   private boolean closed = false;
   private char[] underflow;

   public ServletPrintWriter(ServletOutputStreamImpl outputStream, String charset) throws UnsupportedEncodingException {
      this.charset = charset;
      this.outputStream = outputStream;
      if (!charset.equalsIgnoreCase("utf-8") && !charset.equalsIgnoreCase("iso-8859-1")) {
         this.createEncoder();
      }

   }

   private void createEncoder() {
      this.charsetEncoder = Charset.forName(this.charset).newEncoder();
      this.charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
      this.charsetEncoder.onMalformedInput(CodingErrorAction.REPLACE);
   }

   public void flush() {
      try {
         this.outputStream.flush();
      } catch (IOException var2) {
         this.error = true;
      }

   }

   public void close() {
      if (this.outputStream.getServletRequestContext().getOriginalRequest().getDispatcherType() != DispatcherType.INCLUDE) {
         if (!this.closed) {
            this.closed = true;

            try {
               boolean done = false;
               CharBuffer buffer;
               if (this.underflow == null) {
                  buffer = CharBuffer.wrap(EMPTY_CHAR);
               } else {
                  buffer = CharBuffer.wrap(this.underflow);
                  this.underflow = null;
               }

               if (this.charsetEncoder != null) {
                  do {
                     ByteBuffer out = this.outputStream.underlyingBuffer();
                     if (out == null) {
                        this.error = true;
                        return;
                     }

                     CoderResult result = this.charsetEncoder.encode(buffer, out, true);
                     if (result.isOverflow()) {
                        this.outputStream.flushInternal();
                        if (out.remaining() == 0) {
                           this.outputStream.close();
                           this.error = true;
                           return;
                        }
                     } else {
                        done = true;
                     }
                  } while(!done);
               }

               this.outputStream.close();
            } catch (IOException var5) {
               this.error = true;
            }

         }
      }
   }

   public boolean checkError() {
      this.flush();
      return this.error;
   }

   public void write(CharBuffer input) {
      ByteBuffer buffer = this.outputStream.underlyingBuffer();
      if (buffer == null) {
         this.error = true;
      } else {
         try {
            if (!buffer.hasRemaining()) {
               this.outputStream.flushInternal();
               if (!buffer.hasRemaining()) {
                  this.error = true;
                  return;
               }
            }

            if (this.charsetEncoder == null) {
               this.createEncoder();
            }

            CharBuffer cb;
            if (this.underflow == null) {
               cb = input;
            } else {
               char[] newArray = new char[this.underflow.length + input.remaining()];
               System.arraycopy(this.underflow, 0, newArray, 0, this.underflow.length);
               input.get(newArray, this.underflow.length, input.remaining());
               cb = CharBuffer.wrap(newArray);
               this.underflow = null;
            }

            for(int last = -1; cb.hasRemaining(); last = cb.remaining()) {
               int remaining = buffer.remaining();
               CoderResult result = this.charsetEncoder.encode(cb, buffer, false);
               this.outputStream.updateWritten((long)(remaining - buffer.remaining()));
               if (result.isOverflow() || !buffer.hasRemaining()) {
                  this.outputStream.flushInternal();
                  if (!buffer.hasRemaining()) {
                     this.error = true;
                     return;
                  }
               }

               if (result.isUnderflow()) {
                  this.underflow = new char[cb.remaining()];
                  cb.get(this.underflow);
                  return;
               }

               if (result.isError()) {
                  this.error = true;
                  return;
               }

               if (result.isUnmappable()) {
                  this.error = true;
                  return;
               }

               if (last == cb.remaining()) {
                  this.underflow = new char[cb.remaining()];
                  cb.get(this.underflow);
                  return;
               }
            }
         } catch (IOException var7) {
            this.error = true;
         }

      }
   }

   public void write(int c) {
      this.write(Character.toString((char)c));
   }

   public void write(char[] buf, int off, int len) {
      if (this.charsetEncoder == null) {
         try {
            ByteBuffer buffer = this.outputStream.underlyingBuffer();
            if (buffer == null) {
               this.error = true;
            } else {
               int remaining = buffer.remaining();
               boolean ok = true;
               int end = off + len;
               int i = off;
               int flushPos = off + remaining;

               while(ok && i < end) {
                  for(int realEnd = Math.min(end, flushPos); i < realEnd; ++i) {
                     char c = buf[i];
                     if (c > 127) {
                        ok = false;
                        break;
                     }

                     buffer.put((byte)c);
                  }

                  if (i == flushPos) {
                     this.outputStream.flushInternal();
                     flushPos = i + buffer.remaining();
                  }
               }

               this.outputStream.updateWritten((long)(remaining - buffer.remaining()));
               if (!ok) {
                  CharBuffer cb = CharBuffer.wrap(buf, i, len - (i - off));
                  this.write(cb);
               }
            }
         } catch (IOException var12) {
            this.error = false;
         }
      } else {
         CharBuffer cb = CharBuffer.wrap(buf, off, len);
         this.write(cb);
      }
   }

   public void write(char[] buf) {
      this.write((char[])buf, 0, buf.length);
   }

   public void write(String s, int off, int len) {
      if (this.charsetEncoder == null) {
         try {
            ByteBuffer buffer = this.outputStream.underlyingBuffer();
            if (buffer == null) {
               this.error = true;
            } else {
               int remaining = buffer.remaining();
               boolean ok = true;
               int end = off + len;
               int i = off;

               for(int fpos = off + remaining; i < end; ++i) {
                  if (i == fpos) {
                     this.outputStream.flushInternal();
                     fpos = i + buffer.remaining();
                  }

                  char c = s.charAt(i);
                  if (c > 127) {
                     ok = false;
                     break;
                  }

                  buffer.put((byte)c);
               }

               this.outputStream.updateWritten((long)(remaining - buffer.remaining()));
               if (!ok) {
                  CharBuffer cb = CharBuffer.wrap(s.toCharArray(), i, len - (i - off));
                  this.write(cb);
               }
            }
         } catch (IOException var11) {
            this.error = false;
         }
      } else {
         CharBuffer cb = CharBuffer.wrap(s, off, off + len);
         this.write(cb);
      }
   }

   public void write(String s) {
      this.write((String)s, 0, s.length());
   }

   public void print(boolean b) {
      this.write(Boolean.toString(b));
   }

   public void print(char c) {
      this.write(Character.toString(c));
   }

   public void print(int i) {
      this.write(Integer.toString(i));
   }

   public void print(long l) {
      this.write(Long.toString(l));
   }

   public void print(float f) {
      this.write(Float.toString(f));
   }

   public void print(double d) {
      this.write(Double.toString(d));
   }

   public void print(char[] s) {
      this.write(CharBuffer.wrap(s));
   }

   public void print(String s) {
      this.write(s == null ? "null" : s);
   }

   public void print(Object obj) {
      this.write(obj == null ? "null" : obj.toString());
   }

   public void println() {
      this.print("\r\n");
   }

   public void println(boolean b) {
      this.print(b);
      this.println();
   }

   public void println(char c) {
      this.print(c);
      this.println();
   }

   public void println(int i) {
      this.print(i);
      this.println();
   }

   public void println(long l) {
      this.print(l);
      this.println();
   }

   public void println(float f) {
      this.print(f);
      this.println();
   }

   public void println(double d) {
      this.print(d);
      this.println();
   }

   public void println(char[] s) {
      this.print(s);
      this.println();
   }

   public void println(String s) {
      this.print(s);
      this.println();
   }

   public void println(Object obj) {
      this.print(obj);
      this.println();
   }

   public void printf(String format, Object... args) {
      this.print(String.format(format, args));
   }

   public void printf(Locale l, String format, Object... args) {
      this.print(String.format(l, format, args));
   }

   public void format(String format, Object... args) {
      this.printf(format, args);
   }

   public void format(Locale l, String format, Object... args) {
      this.printf(l, format, args);
   }

   public void append(CharSequence csq) {
      if (csq == null) {
         this.write("null");
      } else {
         this.write(csq.toString());
      }

   }

   public void append(CharSequence csq, int start, int end) {
      CharSequence cs = csq == null ? "null" : csq;
      this.write(((CharSequence)cs).subSequence(start, end).toString());
   }

   public void append(char c) {
      this.write(c);
   }
}
