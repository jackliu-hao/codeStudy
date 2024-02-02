package ch.qos.logback.core.encoder;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.spi.ContextAware;
import java.nio.charset.Charset;

public class LayoutWrappingEncoder<E> extends EncoderBase<E> {
   protected Layout<E> layout;
   private Charset charset;
   ContextAware parent;
   Boolean immediateFlush = null;

   public Layout<E> getLayout() {
      return this.layout;
   }

   public void setLayout(Layout<E> layout) {
      this.layout = layout;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public void setCharset(Charset charset) {
      this.charset = charset;
   }

   public void setImmediateFlush(boolean immediateFlush) {
      this.addWarn("As of version 1.2.0 \"immediateFlush\" property should be set within the enclosing Appender.");
      this.addWarn("Please move \"immediateFlush\" property into the enclosing appender.");
      this.immediateFlush = immediateFlush;
   }

   public byte[] headerBytes() {
      if (this.layout == null) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();
         this.appendIfNotNull(sb, this.layout.getFileHeader());
         this.appendIfNotNull(sb, this.layout.getPresentationHeader());
         if (sb.length() > 0) {
            sb.append(CoreConstants.LINE_SEPARATOR);
         }

         return this.convertToBytes(sb.toString());
      }
   }

   public byte[] footerBytes() {
      if (this.layout == null) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();
         this.appendIfNotNull(sb, this.layout.getPresentationFooter());
         this.appendIfNotNull(sb, this.layout.getFileFooter());
         return this.convertToBytes(sb.toString());
      }
   }

   private byte[] convertToBytes(String s) {
      return this.charset == null ? s.getBytes() : s.getBytes(this.charset);
   }

   public byte[] encode(E event) {
      String txt = this.layout.doLayout(event);
      return this.convertToBytes(txt);
   }

   public boolean isStarted() {
      return false;
   }

   public void start() {
      if (this.immediateFlush != null) {
         if (this.parent instanceof OutputStreamAppender) {
            this.addWarn("Setting the \"immediateFlush\" property of the enclosing appender to " + this.immediateFlush);
            OutputStreamAppender<E> parentOutputStreamAppender = (OutputStreamAppender)this.parent;
            parentOutputStreamAppender.setImmediateFlush(this.immediateFlush);
         } else {
            this.addError("Could not set the \"immediateFlush\" property of the enclosing appender.");
         }
      }

      this.started = true;
   }

   public void stop() {
      this.started = false;
   }

   private void appendIfNotNull(StringBuilder sb, String s) {
      if (s != null) {
         sb.append(s);
      }

   }

   public void setParent(ContextAware parent) {
      this.parent = parent;
   }
}
