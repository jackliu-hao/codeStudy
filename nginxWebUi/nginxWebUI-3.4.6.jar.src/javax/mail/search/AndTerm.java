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
/*     */ 
/*     */ public final class AndTerm
/*     */   extends SearchTerm
/*     */ {
/*     */   protected SearchTerm[] terms;
/*     */   private static final long serialVersionUID = -3583274505380989582L;
/*     */   
/*     */   public AndTerm(SearchTerm t1, SearchTerm t2) {
/*  71 */     this.terms = new SearchTerm[2];
/*  72 */     this.terms[0] = t1;
/*  73 */     this.terms[1] = t2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AndTerm(SearchTerm[] t) {
/*  82 */     this.terms = new SearchTerm[t.length];
/*  83 */     for (int i = 0; i < t.length; i++) {
/*  84 */       this.terms[i] = t[i];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SearchTerm[] getTerms() {
/*  91 */     return (SearchTerm[])this.terms.clone();
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
/*     */   public boolean match(Message msg) {
/* 105 */     for (int i = 0; i < this.terms.length; i++) {
/* 106 */       if (!this.terms[i].match(msg))
/* 107 */         return false; 
/* 108 */     }  return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 115 */     if (!(obj instanceof AndTerm))
/* 116 */       return false; 
/* 117 */     AndTerm at = (AndTerm)obj;
/* 118 */     if (at.terms.length != this.terms.length)
/* 119 */       return false; 
/* 120 */     for (int i = 0; i < this.terms.length; i++) {
/* 121 */       if (!this.terms[i].equals(at.terms[i]))
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\AndTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */