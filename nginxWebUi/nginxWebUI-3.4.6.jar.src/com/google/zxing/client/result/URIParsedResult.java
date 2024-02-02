/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class URIParsedResult
/*    */   extends ParsedResult
/*    */ {
/* 28 */   private static final Pattern USER_IN_HOST = Pattern.compile(":/*([^/@]+)@[^/]+");
/*    */   
/*    */   private final String uri;
/*    */   private final String title;
/*    */   
/*    */   public URIParsedResult(String uri, String title) {
/* 34 */     super(ParsedResultType.URI);
/* 35 */     this.uri = massageURI(uri);
/* 36 */     this.title = title;
/*    */   }
/*    */   
/*    */   public String getURI() {
/* 40 */     return this.uri;
/*    */   }
/*    */   
/*    */   public String getTitle() {
/* 44 */     return this.title;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isPossiblyMaliciousURI() {
/* 56 */     return USER_IN_HOST.matcher(this.uri).find();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDisplayResult() {
/* 61 */     StringBuilder result = new StringBuilder(30);
/* 62 */     maybeAppend(this.title, result);
/* 63 */     maybeAppend(this.uri, result);
/* 64 */     return result.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String massageURI(String uri) {
/*    */     int protocolEnd;
/* 74 */     if ((protocolEnd = (uri = uri.trim()).indexOf(':')) < 0 || isColonFollowedByPortNumber(uri, protocolEnd))
/*    */     {
/*    */       
/* 77 */       uri = "http://" + uri;
/*    */     }
/* 79 */     return uri;
/*    */   }
/*    */   
/*    */   private static boolean isColonFollowedByPortNumber(String uri, int protocolEnd) {
/* 83 */     int start = protocolEnd + 1;
/*    */     int nextSlash;
/* 85 */     if ((nextSlash = uri.indexOf('/', start)) < 0) {
/* 86 */       nextSlash = uri.length();
/*    */     }
/* 88 */     return ResultParser.isSubstringOfDigits(uri, start, nextSlash - start);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\URIParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */