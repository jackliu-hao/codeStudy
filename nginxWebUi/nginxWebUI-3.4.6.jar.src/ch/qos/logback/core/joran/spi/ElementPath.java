/*     */ package ch.qos.logback.core.joran.spi;
/*     */ 
/*     */ import java.util.ArrayList;
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
/*     */ public class ElementPath
/*     */ {
/*  27 */   ArrayList<String> partList = new ArrayList<String>();
/*     */ 
/*     */   
/*     */   public ElementPath() {}
/*     */   
/*     */   public ElementPath(List<String> list) {
/*  33 */     this.partList.addAll(list);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ElementPath(String pathStr) {
/*  42 */     if (pathStr == null) {
/*     */       return;
/*     */     }
/*     */     
/*  46 */     String[] partArray = pathStr.split("/");
/*  47 */     if (partArray == null) {
/*     */       return;
/*     */     }
/*  50 */     for (String part : partArray) {
/*  51 */       if (part.length() > 0) {
/*  52 */         this.partList.add(part);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public ElementPath duplicate() {
/*  58 */     ElementPath p = new ElementPath();
/*  59 */     p.partList.addAll(this.partList);
/*  60 */     return p;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  66 */     if (o == null || !(o instanceof ElementPath)) {
/*  67 */       return false;
/*     */     }
/*     */     
/*  70 */     ElementPath r = (ElementPath)o;
/*     */     
/*  72 */     if (r.size() != size()) {
/*  73 */       return false;
/*     */     }
/*     */     
/*  76 */     int len = size();
/*     */     
/*  78 */     for (int i = 0; i < len; i++) {
/*  79 */       if (!equalityCheck(get(i), r.get(i))) {
/*  80 */         return false;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  85 */     return true;
/*     */   }
/*     */   
/*     */   private boolean equalityCheck(String x, String y) {
/*  89 */     return x.equalsIgnoreCase(y);
/*     */   }
/*     */   
/*     */   public List<String> getCopyOfPartList() {
/*  93 */     return new ArrayList<String>(this.partList);
/*     */   }
/*     */   
/*     */   public void push(String s) {
/*  97 */     this.partList.add(s);
/*     */   }
/*     */   
/*     */   public String get(int i) {
/* 101 */     return this.partList.get(i);
/*     */   }
/*     */   
/*     */   public void pop() {
/* 105 */     if (!this.partList.isEmpty()) {
/* 106 */       this.partList.remove(this.partList.size() - 1);
/*     */     }
/*     */   }
/*     */   
/*     */   public String peekLast() {
/* 111 */     if (!this.partList.isEmpty()) {
/* 112 */       int size = this.partList.size();
/* 113 */       return this.partList.get(size - 1);
/*     */     } 
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 120 */     return this.partList.size();
/*     */   }
/*     */   
/*     */   protected String toStableString() {
/* 124 */     StringBuilder result = new StringBuilder();
/* 125 */     for (String current : this.partList) {
/* 126 */       result.append("[").append(current).append("]");
/*     */     }
/* 128 */     return result.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 133 */     return toStableString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\ElementPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */