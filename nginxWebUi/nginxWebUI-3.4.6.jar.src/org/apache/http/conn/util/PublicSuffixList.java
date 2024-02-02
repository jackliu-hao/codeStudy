/*    */ package org.apache.http.conn.util;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.util.Args;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public final class PublicSuffixList
/*    */ {
/*    */   private final DomainType type;
/*    */   private final List<String> rules;
/*    */   private final List<String> exceptions;
/*    */   
/*    */   public PublicSuffixList(DomainType type, List<String> rules, List<String> exceptions) {
/* 56 */     this.type = (DomainType)Args.notNull(type, "Domain type");
/* 57 */     this.rules = Collections.unmodifiableList((List<? extends String>)Args.notNull(rules, "Domain suffix rules"));
/* 58 */     this.exceptions = Collections.unmodifiableList((exceptions != null) ? exceptions : Collections.<String>emptyList());
/*    */   }
/*    */   
/*    */   public PublicSuffixList(List<String> rules, List<String> exceptions) {
/* 62 */     this(DomainType.UNKNOWN, rules, exceptions);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DomainType getType() {
/* 69 */     return this.type;
/*    */   }
/*    */   
/*    */   public List<String> getRules() {
/* 73 */     return this.rules;
/*    */   }
/*    */   
/*    */   public List<String> getExceptions() {
/* 77 */     return this.exceptions;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\con\\util\PublicSuffixList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */