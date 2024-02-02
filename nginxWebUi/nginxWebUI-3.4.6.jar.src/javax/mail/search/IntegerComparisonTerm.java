/*     */ package javax.mail.search;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class IntegerComparisonTerm
/*     */   extends ComparisonTerm
/*     */ {
/*     */   protected int number;
/*     */   private static final long serialVersionUID = -6963571240154302484L;
/*     */   
/*     */   protected IntegerComparisonTerm(int comparison, int number) {
/*  60 */     this.comparison = comparison;
/*  61 */     this.number = number;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumber() {
/*  68 */     return this.number;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getComparison() {
/*  75 */     return this.comparison;
/*     */   }
/*     */   
/*     */   protected boolean match(int i) {
/*  79 */     switch (this.comparison) {
/*     */       case 1:
/*  81 */         return (i <= this.number);
/*     */       case 2:
/*  83 */         return (i < this.number);
/*     */       case 3:
/*  85 */         return (i == this.number);
/*     */       case 4:
/*  87 */         return (i != this.number);
/*     */       case 5:
/*  89 */         return (i > this.number);
/*     */       case 6:
/*  91 */         return (i >= this.number);
/*     */     } 
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 101 */     if (!(obj instanceof IntegerComparisonTerm))
/* 102 */       return false; 
/* 103 */     IntegerComparisonTerm ict = (IntegerComparisonTerm)obj;
/* 104 */     return (ict.number == this.number && super.equals(obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 111 */     return this.number + super.hashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\IntegerComparisonTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */