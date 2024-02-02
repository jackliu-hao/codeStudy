package com.mysql.cj.protocol.x;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.util.Util;
import java.io.InputStream;
import java.io.OutputStream;

public class CompressorStreamsFactory {
   private CompressionAlgorithm compressionAlgorithm;
   private InputStream compressorInputStreamInstance = null;
   private ContinuousInputStream underlyingInputStream = null;
   private OutputStream compressorOutputStreamInstance = null;
   private ReusableOutputStream underlyingOutputStream = null;

   public CompressorStreamsFactory(CompressionAlgorithm algorithm) {
      this.compressionAlgorithm = algorithm;
   }

   public CompressionMode getCompressionMode() {
      return this.compressionAlgorithm.getCompressionMode();
   }

   public boolean areCompressedStreamsContinuous() {
      return this.getCompressionMode() == CompressionMode.STREAM;
   }

   public InputStream getInputStreamInstance(InputStream in) {
      InputStream underlyingIn = in;
      if (this.areCompressedStreamsContinuous()) {
         if (this.compressorInputStreamInstance != null) {
            this.underlyingInputStream.addInputStream(in);
            return this.compressorInputStreamInstance;
         }

         this.underlyingInputStream = new ContinuousInputStream(in);
         underlyingIn = this.underlyingInputStream;
      }

      InputStream compressionIn = (InputStream)Util.getInstance(this.compressionAlgorithm.getInputStreamClass().getName(), new Class[]{InputStream.class}, new Object[]{underlyingIn}, (ExceptionInterceptor)null, Messages.getString("Protocol.Compression.IoFactory.0", new Object[]{this.compressionAlgorithm.getInputStreamClass().getName(), this.compressionAlgorithm}));
      if (this.areCompressedStreamsContinuous()) {
         this.compressorInputStreamInstance = compressionIn;
      }

      return compressionIn;
   }

   public OutputStream getOutputStreamInstance(OutputStream out) {
      OutputStream underlyingOut = out;
      if (this.areCompressedStreamsContinuous()) {
         if (this.compressorOutputStreamInstance != null) {
            this.underlyingOutputStream.setOutputStream(out);
            return this.compressorOutputStreamInstance;
         }

         this.underlyingOutputStream = new ReusableOutputStream(out);
         underlyingOut = this.underlyingOutputStream;
      }

      OutputStream compressionOut = (OutputStream)Util.getInstance(this.compressionAlgorithm.getOutputStreamClass().getName(), new Class[]{OutputStream.class}, new Object[]{underlyingOut}, (ExceptionInterceptor)null, Messages.getString("Protocol.Compression.IoFactory.1", new Object[]{this.compressionAlgorithm.getOutputStreamClass().getName(), this.compressionAlgorithm}));
      if (this.areCompressedStreamsContinuous()) {
         compressionOut = new ContinuousOutputStream((OutputStream)compressionOut);
         this.compressorOutputStreamInstance = (OutputStream)compressionOut;
      }

      return (OutputStream)compressionOut;
   }
}
