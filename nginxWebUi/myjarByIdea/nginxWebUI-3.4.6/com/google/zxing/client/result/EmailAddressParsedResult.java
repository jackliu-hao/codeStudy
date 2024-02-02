package com.google.zxing.client.result;

public final class EmailAddressParsedResult extends ParsedResult {
   private final String[] tos;
   private final String[] ccs;
   private final String[] bccs;
   private final String subject;
   private final String body;

   EmailAddressParsedResult(String to) {
      this(new String[]{to}, (String[])null, (String[])null, (String)null, (String)null);
   }

   EmailAddressParsedResult(String[] tos, String[] ccs, String[] bccs, String subject, String body) {
      super(ParsedResultType.EMAIL_ADDRESS);
      this.tos = tos;
      this.ccs = ccs;
      this.bccs = bccs;
      this.subject = subject;
      this.body = body;
   }

   /** @deprecated */
   @Deprecated
   public String getEmailAddress() {
      return this.tos != null && this.tos.length != 0 ? this.tos[0] : null;
   }

   public String[] getTos() {
      return this.tos;
   }

   public String[] getCCs() {
      return this.ccs;
   }

   public String[] getBCCs() {
      return this.bccs;
   }

   public String getSubject() {
      return this.subject;
   }

   public String getBody() {
      return this.body;
   }

   /** @deprecated */
   @Deprecated
   public String getMailtoURI() {
      return "mailto:";
   }

   public String getDisplayResult() {
      StringBuilder result = new StringBuilder(30);
      maybeAppend(this.tos, result);
      maybeAppend(this.ccs, result);
      maybeAppend(this.bccs, result);
      maybeAppend(this.subject, result);
      maybeAppend(this.body, result);
      return result.toString();
   }
}
