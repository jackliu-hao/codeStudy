package io.undertow.servlet.api;

public class MimeMapping {
   private final String extension;
   private final String mimeType;

   public MimeMapping(String extension, String mimeType) {
      this.extension = extension;
      this.mimeType = mimeType;
   }

   public String getExtension() {
      return this.extension;
   }

   public String getMimeType() {
      return this.mimeType;
   }
}
