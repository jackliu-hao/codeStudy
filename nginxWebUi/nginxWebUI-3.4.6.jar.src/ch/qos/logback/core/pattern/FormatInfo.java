/*     */ package ch.qos.logback.core.pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FormatInfo
/*     */ {
/*  23 */   private int min = Integer.MIN_VALUE;
/*  24 */   private int max = Integer.MAX_VALUE;
/*     */   
/*     */   private boolean leftPad = true;
/*     */   private boolean leftTruncate = true;
/*     */   
/*     */   public FormatInfo() {}
/*     */   
/*     */   public FormatInfo(int min, int max) {
/*  32 */     this.min = min;
/*  33 */     this.max = max;
/*     */   }
/*     */   
/*     */   public FormatInfo(int min, int max, boolean leftPad, boolean leftTruncate) {
/*  37 */     this.min = min;
/*  38 */     this.max = max;
/*  39 */     this.leftPad = leftPad;
/*  40 */     this.leftTruncate = leftTruncate;
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
/*     */   public static FormatInfo valueOf(String str) throws IllegalArgumentException {
/*  52 */     if (str == null) {
/*  53 */       throw new NullPointerException("Argument cannot be null");
/*     */     }
/*     */     
/*  56 */     FormatInfo fi = new FormatInfo();
/*     */     
/*  58 */     int indexOfDot = str.indexOf('.');
/*  59 */     String minPart = null;
/*  60 */     String maxPart = null;
/*  61 */     if (indexOfDot != -1) {
/*  62 */       minPart = str.substring(0, indexOfDot);
/*  63 */       if (indexOfDot + 1 == str.length()) {
/*  64 */         throw new IllegalArgumentException("Formatting string [" + str + "] should not end with '.'");
/*     */       }
/*  66 */       maxPart = str.substring(indexOfDot + 1);
/*     */     } else {
/*     */       
/*  69 */       minPart = str;
/*     */     } 
/*     */     
/*  72 */     if (minPart != null && minPart.length() > 0) {
/*  73 */       int min = Integer.parseInt(minPart);
/*  74 */       if (min >= 0) {
/*  75 */         fi.min = min;
/*     */       } else {
/*  77 */         fi.min = -min;
/*  78 */         fi.leftPad = false;
/*     */       } 
/*     */     } 
/*     */     
/*  82 */     if (maxPart != null && maxPart.length() > 0) {
/*  83 */       int max = Integer.parseInt(maxPart);
/*  84 */       if (max >= 0) {
/*  85 */         fi.max = max;
/*     */       } else {
/*  87 */         fi.max = -max;
/*  88 */         fi.leftTruncate = false;
/*     */       } 
/*     */     } 
/*     */     
/*  92 */     return fi;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeftPad() {
/*  97 */     return this.leftPad;
/*     */   }
/*     */   
/*     */   public void setLeftPad(boolean leftAlign) {
/* 101 */     this.leftPad = leftAlign;
/*     */   }
/*     */   
/*     */   public int getMax() {
/* 105 */     return this.max;
/*     */   }
/*     */   
/*     */   public void setMax(int max) {
/* 109 */     this.max = max;
/*     */   }
/*     */   
/*     */   public int getMin() {
/* 113 */     return this.min;
/*     */   }
/*     */   
/*     */   public void setMin(int min) {
/* 117 */     this.min = min;
/*     */   }
/*     */   
/*     */   public boolean isLeftTruncate() {
/* 121 */     return this.leftTruncate;
/*     */   }
/*     */   
/*     */   public void setLeftTruncate(boolean leftTruncate) {
/* 125 */     this.leftTruncate = leftTruncate;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 129 */     if (this == o) {
/* 130 */       return true;
/*     */     }
/* 132 */     if (!(o instanceof FormatInfo)) {
/* 133 */       return false;
/*     */     }
/* 135 */     FormatInfo r = (FormatInfo)o;
/*     */     
/* 137 */     return (this.min == r.min && this.max == r.max && this.leftPad == r.leftPad && this.leftTruncate == r.leftTruncate);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 142 */     int result = this.min;
/* 143 */     result = 31 * result + this.max;
/* 144 */     result = 31 * result + (this.leftPad ? 1 : 0);
/* 145 */     result = 31 * result + (this.leftTruncate ? 1 : 0);
/* 146 */     return result;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 150 */     return "FormatInfo(" + this.min + ", " + this.max + ", " + this.leftPad + ", " + this.leftTruncate + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\FormatInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */