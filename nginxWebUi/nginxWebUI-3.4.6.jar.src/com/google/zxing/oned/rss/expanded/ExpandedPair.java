/*     */ package com.google.zxing.oned.rss.expanded;
/*     */ 
/*     */ import com.google.zxing.oned.rss.DataCharacter;
/*     */ import com.google.zxing.oned.rss.FinderPattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ExpandedPair
/*     */ {
/*     */   private final boolean mayBeLast;
/*     */   private final DataCharacter leftChar;
/*     */   private final DataCharacter rightChar;
/*     */   private final FinderPattern finderPattern;
/*     */   
/*     */   ExpandedPair(DataCharacter leftChar, DataCharacter rightChar, FinderPattern finderPattern, boolean mayBeLast) {
/*  46 */     this.leftChar = leftChar;
/*  47 */     this.rightChar = rightChar;
/*  48 */     this.finderPattern = finderPattern;
/*  49 */     this.mayBeLast = mayBeLast;
/*     */   }
/*     */   
/*     */   boolean mayBeLast() {
/*  53 */     return this.mayBeLast;
/*     */   }
/*     */   
/*     */   DataCharacter getLeftChar() {
/*  57 */     return this.leftChar;
/*     */   }
/*     */   
/*     */   DataCharacter getRightChar() {
/*  61 */     return this.rightChar;
/*     */   }
/*     */   
/*     */   FinderPattern getFinderPattern() {
/*  65 */     return this.finderPattern;
/*     */   }
/*     */   
/*     */   public boolean mustBeLast() {
/*  69 */     return (this.rightChar == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  74 */     return "[ " + this.leftChar + " , " + this.rightChar + " : " + ((this.finderPattern == null) ? "null" : 
/*     */       
/*  76 */       (String)Integer.valueOf(this.finderPattern.getValue())) + " ]";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  81 */     if (!(o instanceof ExpandedPair)) {
/*  82 */       return false;
/*     */     }
/*  84 */     ExpandedPair that = (ExpandedPair)o;
/*     */     
/*  86 */     if (equalsOrNull(this.leftChar, that.leftChar) && 
/*  87 */       equalsOrNull(this.rightChar, that.rightChar) && 
/*  88 */       equalsOrNull(this.finderPattern, that.finderPattern)) return true; 
/*     */     return false;
/*     */   }
/*     */   private static boolean equalsOrNull(Object o1, Object o2) {
/*  92 */     return (o1 == null) ? ((o2 == null)) : o1.equals(o2);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  97 */     return hashNotNull(this.leftChar) ^ hashNotNull(this.rightChar) ^ hashNotNull(this.finderPattern);
/*     */   }
/*     */   
/*     */   private static int hashNotNull(Object o) {
/* 101 */     return (o == null) ? 0 : o.hashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\ExpandedPair.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */