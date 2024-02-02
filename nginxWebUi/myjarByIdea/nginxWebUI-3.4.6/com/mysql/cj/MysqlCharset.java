package com.mysql.cj;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class MysqlCharset {
   public final String charsetName;
   public final int mblen;
   public final int priority;
   public final List<String> javaEncodingsUc;
   public final ServerVersion minimumVersion;

   public MysqlCharset(String charsetName, int mblen, int priority, String[] javaEncodings) {
      this(charsetName, mblen, priority, javaEncodings, new ServerVersion(0, 0, 0));
   }

   private void addEncodingMapping(String encoding) {
      String encodingUc = encoding.toUpperCase(Locale.ENGLISH);
      if (!this.javaEncodingsUc.contains(encodingUc)) {
         this.javaEncodingsUc.add(encodingUc);
      }

   }

   public MysqlCharset(String charsetName, int mblen, int priority, String[] javaEncodings, ServerVersion minimumVersion) {
      this.javaEncodingsUc = new ArrayList();
      this.charsetName = charsetName;
      this.mblen = mblen;
      this.priority = priority;

      for(int i = 0; i < javaEncodings.length; ++i) {
         String encoding = javaEncodings[i];

         try {
            Charset cs = Charset.forName(encoding);
            this.addEncodingMapping(cs.name());
            cs.aliases().forEach(this::addEncodingMapping);
         } catch (Exception var9) {
            if (mblen == 1) {
               this.addEncodingMapping(encoding);
            }
         }
      }

      if (this.javaEncodingsUc.size() == 0) {
         this.addEncodingMapping(mblen > 1 ? "UTF-8" : "Cp1252");
      }

      this.minimumVersion = minimumVersion;
   }

   public String toString() {
      StringBuilder asString = new StringBuilder();
      asString.append("[");
      asString.append("charsetName=");
      asString.append(this.charsetName);
      asString.append(",mblen=");
      asString.append(this.mblen);
      asString.append("]");
      return asString.toString();
   }

   boolean isOkayForVersion(ServerVersion version) {
      return version.meetsMinimum(this.minimumVersion);
   }

   String getMatchingJavaEncoding(String javaEncoding) {
      return javaEncoding != null && this.javaEncodingsUc.contains(javaEncoding.toUpperCase(Locale.ENGLISH)) ? javaEncoding : (String)this.javaEncodingsUc.get(0);
   }
}
