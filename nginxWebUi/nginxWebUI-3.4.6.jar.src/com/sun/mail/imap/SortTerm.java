/*     */ package com.sun.mail.imap;
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
/*     */ public final class SortTerm
/*     */ {
/*  57 */   public static final SortTerm ARRIVAL = new SortTerm("ARRIVAL");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   public static final SortTerm CC = new SortTerm("CC");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   public static final SortTerm DATE = new SortTerm("DATE");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   public static final SortTerm FROM = new SortTerm("FROM");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   public static final SortTerm REVERSE = new SortTerm("REVERSE");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   public static final SortTerm SIZE = new SortTerm("SIZE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public static final SortTerm SUBJECT = new SortTerm("SUBJECT");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   public static final SortTerm TO = new SortTerm("TO");
/*     */   private String term;
/*     */   
/*     */   private SortTerm(String term) {
/*  98 */     this.term = term;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 102 */     return this.term;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\SortTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */