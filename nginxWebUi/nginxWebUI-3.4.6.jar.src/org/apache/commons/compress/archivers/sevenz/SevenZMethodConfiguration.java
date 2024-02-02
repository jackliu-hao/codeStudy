/*    */ package org.apache.commons.compress.archivers.sevenz;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ public class SevenZMethodConfiguration
/*    */ {
/*    */   private final SevenZMethod method;
/*    */   private final Object options;
/*    */   
/*    */   public SevenZMethodConfiguration(SevenZMethod method) {
/* 50 */     this(method, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SevenZMethodConfiguration(SevenZMethod method, Object options) {
/* 60 */     this.method = method;
/* 61 */     this.options = options;
/* 62 */     if (options != null && !Coders.findByMethod(method).canAcceptOptions(options)) {
/* 63 */       throw new IllegalArgumentException("The " + method + " method doesn't support options of type " + options
/* 64 */           .getClass());
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SevenZMethod getMethod() {
/* 73 */     return this.method;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getOptions() {
/* 81 */     return this.options;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 86 */     return (this.method == null) ? 0 : this.method.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 91 */     if (this == obj) {
/* 92 */       return true;
/*    */     }
/* 94 */     if (obj == null || getClass() != obj.getClass()) {
/* 95 */       return false;
/*    */     }
/* 97 */     SevenZMethodConfiguration other = (SevenZMethodConfiguration)obj;
/* 98 */     return (Objects.equals(this.method, other.method) && 
/* 99 */       Objects.equals(this.options, other.options));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\SevenZMethodConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */