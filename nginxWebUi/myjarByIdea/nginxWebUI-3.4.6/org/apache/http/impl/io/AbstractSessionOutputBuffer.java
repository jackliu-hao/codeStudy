package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.http.Consts;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
public abstract class AbstractSessionOutputBuffer implements SessionOutputBuffer, BufferInfo {
   private static final byte[] CRLF = new byte[]{13, 10};
   private OutputStream outStream;
   private ByteArrayBuffer buffer;
   private Charset charset;
   private boolean ascii;
   private int minChunkLimit;
   private HttpTransportMetricsImpl metrics;
   private CodingErrorAction onMalformedCharAction;
   private CodingErrorAction onUnmappableCharAction;
   private CharsetEncoder encoder;
   private ByteBuffer bbuf;

   protected AbstractSessionOutputBuffer(OutputStream outStream, int bufferSize, Charset charset, int minChunkLimit, CodingErrorAction malformedCharAction, CodingErrorAction unmappableCharAction) {
      Args.notNull(outStream, "Input stream");
      Args.notNegative(bufferSize, "Buffer size");
      this.outStream = outStream;
      this.buffer = new ByteArrayBuffer(bufferSize);
      this.charset = charset != null ? charset : Consts.ASCII;
      this.ascii = this.charset.equals(Consts.ASCII);
      this.encoder = null;
      this.minChunkLimit = minChunkLimit >= 0 ? minChunkLimit : 512;
      this.metrics = this.createTransportMetrics();
      this.onMalformedCharAction = malformedCharAction != null ? malformedCharAction : CodingErrorAction.REPORT;
      this.onUnmappableCharAction = unmappableCharAction != null ? unmappableCharAction : CodingErrorAction.REPORT;
   }

   public AbstractSessionOutputBuffer() {
   }

   protected void init(OutputStream outStream, int bufferSize, HttpParams params) {
      Args.notNull(outStream, "Input stream");
      Args.notNegative(bufferSize, "Buffer size");
      Args.notNull(params, "HTTP parameters");
      this.outStream = outStream;
      this.buffer = new ByteArrayBuffer(bufferSize);
      String charset = (String)params.getParameter("http.protocol.element-charset");
      this.charset = charset != null ? Charset.forName(charset) : Consts.ASCII;
      this.ascii = this.charset.equals(Consts.ASCII);
      this.encoder = null;
      this.minChunkLimit = params.getIntParameter("http.connection.min-chunk-limit", 512);
      this.metrics = this.createTransportMetrics();
      CodingErrorAction a1 = (CodingErrorAction)params.getParameter("http.malformed.input.action");
      this.onMalformedCharAction = a1 != null ? a1 : CodingErrorAction.REPORT;
      CodingErrorAction a2 = (CodingErrorAction)params.getParameter("http.unmappable.input.action");
      this.onUnmappableCharAction = a2 != null ? a2 : CodingErrorAction.REPORT;
   }

   protected HttpTransportMetricsImpl createTransportMetrics() {
      return new HttpTransportMetricsImpl();
   }

   public int capacity() {
      return this.buffer.capacity();
   }

   public int length() {
      return this.buffer.length();
   }

   public int available() {
      return this.capacity() - this.length();
   }

   protected void flushBuffer() throws IOException {
      int len = this.buffer.length();
      if (len > 0) {
         this.outStream.write(this.buffer.buffer(), 0, len);
         this.buffer.clear();
         this.metrics.incrementBytesTransferred((long)len);
      }

   }

   public void flush() throws IOException {
      this.flushBuffer();
      this.outStream.flush();
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (b != null) {
         if (len <= this.minChunkLimit && len <= this.buffer.capacity()) {
            int freecapacity = this.buffer.capacity() - this.buffer.length();
            if (len > freecapacity) {
               this.flushBuffer();
            }

            this.buffer.append(b, off, len);
         } else {
            this.flushBuffer();
            this.outStream.write(b, off, len);
            this.metrics.incrementBytesTransferred((long)len);
         }

      }
   }

   public void write(byte[] b) throws IOException {
      if (b != null) {
         this.write(b, 0, b.length);
      }
   }

   public void write(int b) throws IOException {
      if (this.buffer.isFull()) {
         this.flushBuffer();
      }

      this.buffer.append(b);
   }

   public void writeLine(String s) throws IOException {
      if (s != null) {
         if (s.length() > 0) {
            if (this.ascii) {
               for(int i = 0; i < s.length(); ++i) {
                  this.write(s.charAt(i));
               }
            } else {
               CharBuffer cbuf = CharBuffer.wrap(s);
               this.writeEncoded(cbuf);
            }
         }

         this.write(CRLF);
      }
   }

   public void writeLine(CharArrayBuffer charbuffer) throws IOException {
      if (charbuffer != null) {
         if (this.ascii) {
            int off = 0;

            int chunk;
            for(int remaining = charbuffer.length(); remaining > 0; remaining -= chunk) {
               chunk = this.buffer.capacity() - this.buffer.length();
               chunk = Math.min(chunk, remaining);
               if (chunk > 0) {
                  this.buffer.append(charbuffer, off, chunk);
               }

               if (this.buffer.isFull()) {
                  this.flushBuffer();
               }

               off += chunk;
            }
         } else {
            CharBuffer cbuf = CharBuffer.wrap(charbuffer.buffer(), 0, charbuffer.length());
            this.writeEncoded(cbuf);
         }

         this.write(CRLF);
      }
   }

   private void writeEncoded(CharBuffer cbuf) throws IOException {
      if (cbuf.hasRemaining()) {
         if (this.encoder == null) {
            this.encoder = this.charset.newEncoder();
            this.encoder.onMalformedInput(this.onMalformedCharAction);
            this.encoder.onUnmappableCharacter(this.onUnmappableCharAction);
         }

         if (this.bbuf == null) {
            this.bbuf = ByteBuffer.allocate(1024);
         }

         this.encoder.reset();

         CoderResult result;
         while(cbuf.hasRemaining()) {
            result = this.encoder.encode(cbuf, this.bbuf, true);
            this.handleEncodingResult(result);
         }

         result = this.encoder.flush(this.bbuf);
         this.handleEncodingResult(result);
         this.bbuf.clear();
      }
   }

   private void handleEncodingResult(CoderResult result) throws IOException {
      if (result.isError()) {
         result.throwException();
      }

      this.bbuf.flip();

      while(this.bbuf.hasRemaining()) {
         this.write(this.bbuf.get());
      }

      this.bbuf.compact();
   }

   public HttpTransportMetrics getMetrics() {
      return this.metrics;
   }
}
