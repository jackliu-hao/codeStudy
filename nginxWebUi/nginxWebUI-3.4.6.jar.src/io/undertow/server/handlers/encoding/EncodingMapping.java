/*    */ package io.undertow.server.handlers.encoding;
/*    */ 
/*    */ import io.undertow.predicate.Predicate;
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
/*    */ final class EncodingMapping
/*    */   implements Comparable<EncodingMapping>
/*    */ {
/*    */   private final String name;
/*    */   private final ContentEncodingProvider encoding;
/*    */   private final int priority;
/*    */   private final Predicate allowed;
/*    */   
/*    */   EncodingMapping(String name, ContentEncodingProvider encoding, int priority, Predicate allowed) {
/* 34 */     this.name = name;
/* 35 */     this.encoding = encoding;
/* 36 */     this.priority = priority;
/* 37 */     this.allowed = allowed;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 41 */     return this.name;
/*    */   }
/*    */   
/*    */   public ContentEncodingProvider getEncoding() {
/* 45 */     return this.encoding;
/*    */   }
/*    */   
/*    */   public int getPriority() {
/* 49 */     return this.priority;
/*    */   }
/*    */   
/*    */   public Predicate getAllowed() {
/* 53 */     return this.allowed;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 58 */     if (this == o) return true; 
/* 59 */     if (!(o instanceof EncodingMapping)) return false;
/*    */     
/* 61 */     EncodingMapping that = (EncodingMapping)o;
/* 62 */     return (compareTo(that) == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 67 */     return getPriority();
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(EncodingMapping o) {
/* 72 */     return this.priority - o.priority;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\EncodingMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */