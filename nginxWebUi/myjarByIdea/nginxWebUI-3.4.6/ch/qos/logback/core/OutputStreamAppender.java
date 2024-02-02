package ch.qos.logback.core;

import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.status.ErrorStatus;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

public class OutputStreamAppender<E> extends UnsynchronizedAppenderBase<E> {
   protected Encoder<E> encoder;
   protected final ReentrantLock lock = new ReentrantLock(false);
   private OutputStream outputStream;
   boolean immediateFlush = true;

   public OutputStream getOutputStream() {
      return this.outputStream;
   }

   public void start() {
      int errors = 0;
      if (this.encoder == null) {
         this.addStatus(new ErrorStatus("No encoder set for the appender named \"" + this.name + "\".", this));
         ++errors;
      }

      if (this.outputStream == null) {
         this.addStatus(new ErrorStatus("No output stream set for the appender named \"" + this.name + "\".", this));
         ++errors;
      }

      if (errors == 0) {
         super.start();
      }

   }

   public void setLayout(Layout<E> layout) {
      this.addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
      this.addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
      this.addWarn("See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details");
      LayoutWrappingEncoder<E> lwe = new LayoutWrappingEncoder();
      lwe.setLayout(layout);
      lwe.setContext(this.context);
      this.encoder = lwe;
   }

   protected void append(E eventObject) {
      if (this.isStarted()) {
         this.subAppend(eventObject);
      }
   }

   public void stop() {
      this.lock.lock();

      try {
         this.closeOutputStream();
         super.stop();
      } finally {
         this.lock.unlock();
      }

   }

   protected void closeOutputStream() {
      if (this.outputStream != null) {
         try {
            this.encoderClose();
            this.outputStream.close();
            this.outputStream = null;
         } catch (IOException var2) {
            this.addStatus(new ErrorStatus("Could not close output stream for OutputStreamAppender.", this, var2));
         }
      }

   }

   void encoderClose() {
      if (this.encoder != null && this.outputStream != null) {
         try {
            byte[] footer = this.encoder.footerBytes();
            this.writeBytes(footer);
         } catch (IOException var2) {
            this.started = false;
            this.addStatus(new ErrorStatus("Failed to write footer for appender named [" + this.name + "].", this, var2));
         }
      }

   }

   public void setOutputStream(OutputStream outputStream) {
      this.lock.lock();

      try {
         this.closeOutputStream();
         this.outputStream = outputStream;
         if (this.encoder != null) {
            this.encoderInit();
            return;
         }

         this.addWarn("Encoder has not been set. Cannot invoke its init method.");
      } finally {
         this.lock.unlock();
      }

   }

   void encoderInit() {
      if (this.encoder != null && this.outputStream != null) {
         try {
            byte[] header = this.encoder.headerBytes();
            this.writeBytes(header);
         } catch (IOException var2) {
            this.started = false;
            this.addStatus(new ErrorStatus("Failed to initialize encoder for appender named [" + this.name + "].", this, var2));
         }
      }

   }

   protected void writeOut(E event) throws IOException {
      byte[] byteArray = this.encoder.encode(event);
      this.writeBytes(byteArray);
   }

   private void writeBytes(byte[] byteArray) throws IOException {
      if (byteArray != null && byteArray.length != 0) {
         this.lock.lock();

         try {
            this.outputStream.write(byteArray);
            if (this.immediateFlush) {
               this.outputStream.flush();
            }
         } finally {
            this.lock.unlock();
         }

      }
   }

   protected void subAppend(E event) {
      if (this.isStarted()) {
         try {
            if (event instanceof DeferredProcessingAware) {
               ((DeferredProcessingAware)event).prepareForDeferredProcessing();
            }

            byte[] byteArray = this.encoder.encode(event);
            this.writeBytes(byteArray);
         } catch (IOException var3) {
            this.started = false;
            this.addStatus(new ErrorStatus("IO failure in appender", this, var3));
         }

      }
   }

   public Encoder<E> getEncoder() {
      return this.encoder;
   }

   public void setEncoder(Encoder<E> encoder) {
      this.encoder = encoder;
   }

   public boolean isImmediateFlush() {
      return this.immediateFlush;
   }

   public void setImmediateFlush(boolean immediateFlush) {
      this.immediateFlush = immediateFlush;
   }
}
