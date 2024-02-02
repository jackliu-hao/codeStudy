package com.sun.activation.registries;

public class MimeTypeEntry {
   private String type;
   private String extension;

   public MimeTypeEntry(String mime_type, String file_ext) {
      this.type = mime_type;
      this.extension = file_ext;
   }

   public String getMIMEType() {
      return this.type;
   }

   public String getFileExtension() {
      return this.extension;
   }

   public String toString() {
      return "MIMETypeEntry: " + this.type + ", " + this.extension;
   }
}
