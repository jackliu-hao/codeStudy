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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class StringTerm
/*     */   extends SearchTerm
/*     */ {
/*     */   protected String pattern;
/*     */   protected boolean ignoreCase;
/*     */   private static final long serialVersionUID = 1274042129007696269L;
/*     */   
/*     */   protected StringTerm(String pattern) {
/*  69 */     this.pattern = pattern;
/*  70 */     this.ignoreCase = true;
/*     */   }
/*     */   
/*     */   protected StringTerm(String pattern, boolean ignoreCase) {
/*  74 */     this.pattern = pattern;
/*  75 */     this.ignoreCase = ignoreCase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPattern() {
/*  82 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getIgnoreCase() {
/*  89 */     return this.ignoreCase;
/*     */   }
/*     */   
/*     */   protected boolean match(String s) {
/*  93 */     int len = s.length() - this.pattern.length();
/*  94 */     for (int i = 0; i <= len; i++) {
/*  95 */       if (s.regionMatches(this.ignoreCase, i, this.pattern, 0, this.pattern.length()))
/*     */       {
/*  97 */         return true; } 
/*     */     } 
/*  99 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 106 */     if (!(obj instanceof StringTerm))
/* 107 */       return false; 
/* 108 */     StringTerm st = (StringTerm)obj;
/* 109 */     if (this.ignoreCase) {
/* 110 */       return (st.pattern.equalsIgnoreCase(this.pattern) && st.ignoreCase == this.ignoreCase);
/*     */     }
/*     */     
/* 113 */     return (st.pattern.equals(this.pattern) && st.ignoreCase == this.ignoreCase);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 121 */     return this.ignoreCase ? this.pattern.hashCode() : (this.pattern.hashCode() ^ 0xFFFFFFFF);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\StringTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */