/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.util.regex.Matcher;
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
/*    */ final class ThreadNameInfo
/*    */ {
/*    */   private final long globalThreadSequenceNum;
/*    */   private final long perFactoryThreadSequenceNum;
/*    */   private final long factorySequenceNum;
/*    */   
/*    */   ThreadNameInfo(long globalThreadSequenceNum, long perFactoryThreadSequenceNum, long factorySequenceNum) {
/* 33 */     this.globalThreadSequenceNum = globalThreadSequenceNum;
/* 34 */     this.perFactoryThreadSequenceNum = perFactoryThreadSequenceNum;
/* 35 */     this.factorySequenceNum = factorySequenceNum;
/*    */   }
/*    */   
/*    */   public long getGlobalThreadSequenceNum() {
/* 39 */     return this.globalThreadSequenceNum;
/*    */   }
/*    */   
/*    */   public long getPerFactoryThreadSequenceNum() {
/* 43 */     return this.perFactoryThreadSequenceNum;
/*    */   }
/*    */   
/*    */   public long getFactorySequenceNum() {
/* 47 */     return this.factorySequenceNum;
/*    */   }
/*    */   
/* 50 */   private static final Pattern searchPattern = Pattern.compile("([^%]+)|%.");
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
/*    */   public String format(Thread thread, String formatString) {
/* 69 */     StringBuilder builder = new StringBuilder(formatString.length() * 5);
/* 70 */     ThreadGroup group = thread.getThreadGroup();
/* 71 */     Matcher matcher = searchPattern.matcher(formatString);
/* 72 */     while (matcher.find()) {
/* 73 */       if (matcher.group(1) != null) {
/* 74 */         builder.append(matcher.group()); continue;
/*    */       } 
/* 76 */       switch (matcher.group().charAt(1)) { case '%':
/* 77 */           builder.append('%');
/* 78 */         case 't': builder.append(this.perFactoryThreadSequenceNum);
/* 79 */         case 'g': builder.append(this.globalThreadSequenceNum);
/* 80 */         case 'f': builder.append(this.factorySequenceNum);
/* 81 */         case 'p': if (group != null) appendGroupPath(group, builder); 
/* 82 */         case 'i': builder.append(thread.getId());
/* 83 */         case 'G': if (group != null) builder.append(group.getName());
/*    */          }
/*    */     
/*    */     } 
/* 87 */     return builder.toString();
/*    */   }
/*    */   
/*    */   private static void appendGroupPath(ThreadGroup group, StringBuilder builder) {
/* 91 */     ThreadGroup parent = group.getParent();
/* 92 */     if (parent != null) {
/* 93 */       appendGroupPath(parent, builder);
/* 94 */       builder.append(':');
/*    */     } 
/* 96 */     builder.append(group.getName());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\ThreadNameInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */