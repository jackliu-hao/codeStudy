/*     */ package javax.mail.search;
/*     */ 
/*     */ import javax.mail.Message;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class OrTerm
/*     */   extends SearchTerm
/*     */ {
/*     */   protected SearchTerm[] terms;
/*     */   private static final long serialVersionUID = 5380534067523646936L;
/*     */   
/*     */   public OrTerm(SearchTerm t1, SearchTerm t2) {
/*  70 */     this.terms = new SearchTerm[2];
/*  71 */     this.terms[0] = t1;
/*  72 */     this.terms[1] = t2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OrTerm(SearchTerm[] t) {
/*  81 */     this.terms = new SearchTerm[t.length];
/*  82 */     for (int i = 0; i < t.length; i++) {
/*  83 */       this.terms[i] = t[i];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SearchTerm[] getTerms() {
/*  90 */     return (SearchTerm[])this.terms.clone();
/*     */   }
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
/*     */   public boolean match(Message msg) {
/* 105 */     for (int i = 0; i < this.terms.length; i++) {
/* 106 */       if (this.terms[i].match(msg))
/* 107 */         return true; 
/* 108 */     }  return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 115 */     if (!(obj instanceof OrTerm))
/* 116 */       return false; 
/* 117 */     OrTerm ot = (OrTerm)obj;
/* 118 */     if (ot.terms.length != this.terms.length)
/* 119 */       return false; 
/* 120 */     for (int i = 0; i < this.terms.length; i++) {
/* 121 */       if (!this.terms[i].equals(ot.terms[i]))
/* 122 */         return false; 
/* 123 */     }  return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 130 */     int hash = 0;
/* 131 */     for (int i = 0; i < this.terms.length; i++)
/* 132 */       hash += this.terms[i].hashCode(); 
/* 133 */     return hash;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\OrTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */