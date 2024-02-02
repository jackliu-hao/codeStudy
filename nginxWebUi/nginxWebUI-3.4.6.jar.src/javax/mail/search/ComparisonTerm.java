/*    */ package javax.mail.search;
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
/*    */ public abstract class ComparisonTerm
/*    */   extends SearchTerm
/*    */ {
/*    */   public static final int LE = 1;
/*    */   public static final int LT = 2;
/*    */   public static final int EQ = 3;
/*    */   public static final int NE = 4;
/*    */   public static final int GT = 5;
/*    */   public static final int GE = 6;
/*    */   protected int comparison;
/*    */   private static final long serialVersionUID = 1456646953666474308L;
/*    */   
/*    */   public boolean equals(Object obj) {
/* 71 */     if (!(obj instanceof ComparisonTerm))
/* 72 */       return false; 
/* 73 */     ComparisonTerm ct = (ComparisonTerm)obj;
/* 74 */     return (ct.comparison == this.comparison);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 81 */     return this.comparison;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\ComparisonTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */