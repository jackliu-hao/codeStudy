/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QValueParser
/*     */ {
/*     */   public static List<List<QValueResult>> parse(List<String> headers) {
/*  48 */     List<QValueResult> found = new ArrayList<>();
/*  49 */     QValueResult current = null;
/*  50 */     for (String header : headers) {
/*  51 */       int l = header.length();
/*     */ 
/*     */       
/*  54 */       int stringStart = 0;
/*  55 */       for (int i = 0; i < l; i++) {
/*  56 */         char c = header.charAt(i);
/*  57 */         switch (c) {
/*     */           case ',':
/*  59 */             if (current != null && i - stringStart > 2 && header
/*  60 */               .charAt(stringStart) == 'q' && header
/*  61 */               .charAt(stringStart + 1) == '=') {
/*     */               
/*  63 */               current.qvalue = header.substring(stringStart + 2, i);
/*  64 */               current = null;
/*  65 */             } else if (stringStart != i) {
/*  66 */               current = handleNewEncoding(found, header, stringStart, i);
/*     */             } 
/*  68 */             stringStart = i + 1;
/*     */             break;
/*     */           
/*     */           case ';':
/*  72 */             if (stringStart != i) {
/*  73 */               current = handleNewEncoding(found, header, stringStart, i);
/*  74 */               stringStart = i + 1;
/*     */             } 
/*     */             break;
/*     */           
/*     */           case ' ':
/*  79 */             if (stringStart != i) {
/*  80 */               if (current != null && i - stringStart > 2 && header
/*  81 */                 .charAt(stringStart) == 'q' && header
/*  82 */                 .charAt(stringStart + 1) == '=') {
/*     */                 
/*  84 */                 current.qvalue = header.substring(stringStart + 2, i);
/*     */               } else {
/*  86 */                 current = handleNewEncoding(found, header, stringStart, i);
/*     */               } 
/*     */             }
/*  89 */             stringStart = i + 1;
/*     */             break;
/*     */         } 
/*     */       
/*     */       } 
/*  94 */       if (stringStart != l) {
/*  95 */         if (current != null && l - stringStart > 2 && header
/*  96 */           .charAt(stringStart) == 'q' && header
/*  97 */           .charAt(stringStart + 1) == '=') {
/*     */           
/*  99 */           current.qvalue = header.substring(stringStart + 2, l); continue;
/*     */         } 
/* 101 */         current = handleNewEncoding(found, header, stringStart, l);
/*     */       } 
/*     */     } 
/*     */     
/* 105 */     Collections.sort(found, Collections.reverseOrder());
/* 106 */     String currentQValue = null;
/* 107 */     List<List<QValueResult>> values = new ArrayList<>();
/* 108 */     List<QValueResult> currentSet = null;
/*     */     
/* 110 */     for (QValueResult val : found) {
/* 111 */       if (!val.qvalue.equals(currentQValue)) {
/* 112 */         currentQValue = val.qvalue;
/* 113 */         currentSet = new ArrayList<>();
/* 114 */         values.add(currentSet);
/*     */       } 
/* 116 */       currentSet.add(val);
/*     */     } 
/* 118 */     return values;
/*     */   }
/*     */   
/*     */   private static QValueResult handleNewEncoding(List<QValueResult> found, String header, int stringStart, int i) {
/* 122 */     QValueResult current = new QValueResult();
/* 123 */     current.value = header.substring(stringStart, i);
/* 124 */     found.add(current);
/* 125 */     return current;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class QValueResult
/*     */     implements Comparable<QValueResult>
/*     */   {
/*     */     private String value;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 141 */     private String qvalue = "1";
/*     */     
/*     */     public String getValue() {
/* 144 */       return this.value;
/*     */     }
/*     */     
/*     */     public String getQvalue() {
/* 148 */       return this.qvalue;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 153 */       if (this == o) return true; 
/* 154 */       if (!(o instanceof QValueResult)) return false;
/*     */       
/* 156 */       QValueResult that = (QValueResult)o;
/*     */       
/* 158 */       if ((getValue() != null) ? !getValue().equals(that.getValue()) : (that.getValue() != null)) return false; 
/* 159 */       return (getQvalue() != null) ? getQvalue().equals(that.getQvalue()) : ((that.getQvalue() == null));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 164 */       int result = (getValue() != null) ? getValue().hashCode() : 0;
/* 165 */       result = 31 * result + ((getQvalue() != null) ? getQvalue().hashCode() : 0);
/* 166 */       return result;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int compareTo(QValueResult other) {
/* 174 */       String t = this.qvalue;
/* 175 */       String o = other.qvalue;
/* 176 */       if (t == null && o == null)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 181 */         return 0;
/*     */       }
/*     */       
/* 184 */       if (o == null)
/* 185 */         return 1; 
/* 186 */       if (t == null) {
/* 187 */         return -1;
/*     */       }
/*     */       
/* 190 */       int tl = t.length();
/* 191 */       int ol = o.length();
/*     */       
/* 193 */       for (int i = 0; i < 5; i++) {
/* 194 */         if (tl == i || ol == i) {
/* 195 */           return ol - tl;
/*     */         }
/* 197 */         if (i != 1) {
/* 198 */           int tc = t.charAt(i);
/* 199 */           int oc = o.charAt(i);
/*     */           
/* 201 */           int res = tc - oc;
/* 202 */           if (res != 0)
/* 203 */             return res; 
/*     */         } 
/*     */       } 
/* 206 */       return 0;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isQValueZero() {
/* 212 */       if (this.qvalue != null) {
/* 213 */         int length = Math.min(5, this.qvalue.length());
/*     */ 
/*     */         
/* 216 */         boolean zero = true;
/* 217 */         for (int j = 0; j < length; j++) {
/* 218 */           if (j != 1 && 
/* 219 */             this.qvalue.charAt(j) != '0') {
/* 220 */             zero = false;
/*     */             break;
/*     */           } 
/*     */         } 
/* 224 */         return zero;
/*     */       } 
/* 226 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\QValueParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */