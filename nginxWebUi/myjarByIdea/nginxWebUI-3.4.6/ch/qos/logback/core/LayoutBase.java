package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class LayoutBase<E> extends ContextAwareBase implements Layout<E> {
   protected boolean started;
   String fileHeader;
   String fileFooter;
   String presentationHeader;
   String presentationFooter;

   public void setContext(Context context) {
      this.context = context;
   }

   public Context getContext() {
      return this.context;
   }

   public void start() {
      this.started = true;
   }

   public void stop() {
      this.started = false;
   }

   public boolean isStarted() {
      return this.started;
   }

   public String getFileHeader() {
      return this.fileHeader;
   }

   public String getPresentationHeader() {
      return this.presentationHeader;
   }

   public String getPresentationFooter() {
      return this.presentationFooter;
   }

   public String getFileFooter() {
      return this.fileFooter;
   }

   public String getContentType() {
      return "text/plain";
   }

   public void setFileHeader(String header) {
      this.fileHeader = header;
   }

   public void setFileFooter(String footer) {
      this.fileFooter = footer;
   }

   public void setPresentationHeader(String header) {
      this.presentationHeader = header;
   }

   public void setPresentationFooter(String footer) {
      this.presentationFooter = footer;
   }
}
