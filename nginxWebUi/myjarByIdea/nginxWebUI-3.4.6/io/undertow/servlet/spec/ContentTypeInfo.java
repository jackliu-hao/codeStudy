package io.undertow.servlet.spec;

class ContentTypeInfo {
   private final String header;
   private final String charset;
   private final String contentType;

   ContentTypeInfo(String header, String charset, String contentType) {
      this.header = header;
      this.charset = charset;
      this.contentType = contentType;
   }

   public String getHeader() {
      return this.header;
   }

   public String getCharset() {
      return this.charset;
   }

   public String getContentType() {
      return this.contentType;
   }
}
