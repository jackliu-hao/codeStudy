/*    */ package ch.qos.logback.classic.spi;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class StackTraceElementProxy
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -2374374378980555982L;
/*    */   final StackTraceElement ste;
/*    */   private transient String steAsString;
/*    */   private ClassPackagingData cpd;
/*    */   
/*    */   public StackTraceElementProxy(StackTraceElement ste) {
/* 29 */     if (ste == null) {
/* 30 */       throw new IllegalArgumentException("ste cannot be null");
/*    */     }
/* 32 */     this.ste = ste;
/*    */   }
/*    */   
/*    */   public String getSTEAsString() {
/* 36 */     if (this.steAsString == null) {
/* 37 */       this.steAsString = "at " + this.ste.toString();
/*    */     }
/* 39 */     return this.steAsString;
/*    */   }
/*    */   
/*    */   public StackTraceElement getStackTraceElement() {
/* 43 */     return this.ste;
/*    */   }
/*    */   
/*    */   public void setClassPackagingData(ClassPackagingData cpd) {
/* 47 */     if (this.cpd != null) {
/* 48 */       throw new IllegalStateException("Packaging data has been already set");
/*    */     }
/* 50 */     this.cpd = cpd;
/*    */   }
/*    */   
/*    */   public ClassPackagingData getClassPackagingData() {
/* 54 */     return this.cpd;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 59 */     return this.ste.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 64 */     if (this == obj)
/* 65 */       return true; 
/* 66 */     if (obj == null)
/* 67 */       return false; 
/* 68 */     if (getClass() != obj.getClass())
/* 69 */       return false; 
/* 70 */     StackTraceElementProxy other = (StackTraceElementProxy)obj;
/*    */     
/* 72 */     if (!this.ste.equals(other.ste)) {
/* 73 */       return false;
/*    */     }
/* 75 */     if (this.cpd == null) {
/* 76 */       if (other.cpd != null) {
/* 77 */         return false;
/*    */       }
/* 79 */     } else if (!this.cpd.equals(other.cpd)) {
/* 80 */       return false;
/*    */     } 
/* 82 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 87 */     return getSTEAsString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\StackTraceElementProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */