/*     */ package ch.qos.logback.core.joran.spi;
/*     */ 
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ElementSelector
/*     */   extends ElementPath
/*     */ {
/*     */   public ElementSelector() {}
/*     */   
/*     */   public ElementSelector(List<String> list) {
/*  35 */     super(list);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ElementSelector(String p) {
/*  45 */     super(p);
/*     */   }
/*     */   
/*     */   public boolean fullPathMatch(ElementPath path) {
/*  49 */     if (path.size() != size()) {
/*  50 */       return false;
/*     */     }
/*     */     
/*  53 */     int len = size();
/*  54 */     for (int i = 0; i < len; i++) {
/*  55 */       if (!equalityCheck(get(i), path.get(i))) {
/*  56 */         return false;
/*     */       }
/*     */     } 
/*     */     
/*  60 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTailMatchLength(ElementPath p) {
/*  69 */     if (p == null) {
/*  70 */       return 0;
/*     */     }
/*     */     
/*  73 */     int lSize = this.partList.size();
/*  74 */     int rSize = p.partList.size();
/*     */ 
/*     */     
/*  77 */     if (lSize == 0 || rSize == 0) {
/*  78 */       return 0;
/*     */     }
/*     */     
/*  81 */     int minLen = (lSize <= rSize) ? lSize : rSize;
/*  82 */     int match = 0;
/*     */ 
/*     */     
/*  85 */     for (int i = 1; i <= minLen; ) {
/*  86 */       String l = this.partList.get(lSize - i);
/*  87 */       String r = p.partList.get(rSize - i);
/*     */       
/*  89 */       if (equalityCheck(l, r)) {
/*  90 */         match++;
/*     */         
/*     */         i++;
/*     */       } 
/*     */     } 
/*  95 */     return match;
/*     */   }
/*     */   
/*     */   public boolean isContainedIn(ElementPath p) {
/*  99 */     if (p == null) {
/* 100 */       return false;
/*     */     }
/* 102 */     return p.toStableString().contains(toStableString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPrefixMatchLength(ElementPath p) {
/* 111 */     if (p == null) {
/* 112 */       return 0;
/*     */     }
/*     */     
/* 115 */     int lSize = this.partList.size();
/* 116 */     int rSize = p.partList.size();
/*     */ 
/*     */     
/* 119 */     if (lSize == 0 || rSize == 0) {
/* 120 */       return 0;
/*     */     }
/*     */     
/* 123 */     int minLen = (lSize <= rSize) ? lSize : rSize;
/* 124 */     int match = 0;
/*     */     
/* 126 */     for (int i = 0; i < minLen; ) {
/* 127 */       String l = this.partList.get(i);
/* 128 */       String r = p.partList.get(i);
/*     */       
/* 130 */       if (equalityCheck(l, r)) {
/* 131 */         match++;
/*     */         
/*     */         i++;
/*     */       } 
/*     */     } 
/*     */     
/* 137 */     return match;
/*     */   }
/*     */   
/*     */   private boolean equalityCheck(String x, String y) {
/* 141 */     return x.equalsIgnoreCase(y);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 146 */     if (o == null || !(o instanceof ElementSelector)) {
/* 147 */       return false;
/*     */     }
/*     */     
/* 150 */     ElementSelector r = (ElementSelector)o;
/*     */     
/* 152 */     if (r.size() != size()) {
/* 153 */       return false;
/*     */     }
/*     */     
/* 156 */     int len = size();
/*     */     
/* 158 */     for (int i = 0; i < len; i++) {
/* 159 */       if (!equalityCheck(get(i), r.get(i))) {
/* 160 */         return false;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 165 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 170 */     int hc = 0;
/* 171 */     int len = size();
/*     */     
/* 173 */     for (int i = 0; i < len; i++)
/*     */     {
/*     */       
/* 176 */       hc ^= get(i).toLowerCase().hashCode();
/*     */     }
/* 178 */     return hc;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\ElementSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */