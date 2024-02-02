/*     */ package javax.mail.search;
/*     */ 
/*     */ import java.util.Date;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class DateTerm
/*     */   extends ComparisonTerm
/*     */ {
/*     */   protected Date date;
/*     */   private static final long serialVersionUID = 4818873430063720043L;
/*     */   
/*     */   protected DateTerm(int comparison, Date date) {
/*  67 */     this.comparison = comparison;
/*  68 */     this.date = date;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getDate() {
/*  75 */     return new Date(this.date.getTime());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getComparison() {
/*  82 */     return this.comparison;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean match(Date d) {
/*  92 */     switch (this.comparison) {
/*     */       case 1:
/*  94 */         return (d.before(this.date) || d.equals(this.date));
/*     */       case 2:
/*  96 */         return d.before(this.date);
/*     */       case 3:
/*  98 */         return d.equals(this.date);
/*     */       case 4:
/* 100 */         return !d.equals(this.date);
/*     */       case 5:
/* 102 */         return d.after(this.date);
/*     */       case 6:
/* 104 */         return (d.after(this.date) || d.equals(this.date));
/*     */     } 
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 114 */     if (!(obj instanceof DateTerm))
/* 115 */       return false; 
/* 116 */     DateTerm dt = (DateTerm)obj;
/* 117 */     return (dt.date.equals(this.date) && super.equals(obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 124 */     return this.date.hashCode() + super.hashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\DateTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */