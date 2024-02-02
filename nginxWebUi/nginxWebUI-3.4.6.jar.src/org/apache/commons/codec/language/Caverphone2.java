/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Caverphone2
/*     */   extends AbstractCaverphone
/*     */ {
/*     */   private static final String TEN_1 = "1111111111";
/*     */   
/*     */   public String encode(String source) {
/*  45 */     String txt = source;
/*  46 */     if (txt == null || txt.length() == 0) {
/*  47 */       return "1111111111";
/*     */     }
/*     */ 
/*     */     
/*  51 */     txt = txt.toLowerCase(Locale.ENGLISH);
/*     */ 
/*     */     
/*  54 */     txt = txt.replaceAll("[^a-z]", "");
/*     */ 
/*     */     
/*  57 */     txt = txt.replaceAll("e$", "");
/*     */ 
/*     */     
/*  60 */     txt = txt.replaceAll("^cough", "cou2f");
/*  61 */     txt = txt.replaceAll("^rough", "rou2f");
/*  62 */     txt = txt.replaceAll("^tough", "tou2f");
/*  63 */     txt = txt.replaceAll("^enough", "enou2f");
/*  64 */     txt = txt.replaceAll("^trough", "trou2f");
/*     */     
/*  66 */     txt = txt.replaceAll("^gn", "2n");
/*     */ 
/*     */     
/*  69 */     txt = txt.replaceAll("mb$", "m2");
/*     */ 
/*     */     
/*  72 */     txt = txt.replaceAll("cq", "2q");
/*  73 */     txt = txt.replaceAll("ci", "si");
/*  74 */     txt = txt.replaceAll("ce", "se");
/*  75 */     txt = txt.replaceAll("cy", "sy");
/*  76 */     txt = txt.replaceAll("tch", "2ch");
/*  77 */     txt = txt.replaceAll("c", "k");
/*  78 */     txt = txt.replaceAll("q", "k");
/*  79 */     txt = txt.replaceAll("x", "k");
/*  80 */     txt = txt.replaceAll("v", "f");
/*  81 */     txt = txt.replaceAll("dg", "2g");
/*  82 */     txt = txt.replaceAll("tio", "sio");
/*  83 */     txt = txt.replaceAll("tia", "sia");
/*  84 */     txt = txt.replaceAll("d", "t");
/*  85 */     txt = txt.replaceAll("ph", "fh");
/*  86 */     txt = txt.replaceAll("b", "p");
/*  87 */     txt = txt.replaceAll("sh", "s2");
/*  88 */     txt = txt.replaceAll("z", "s");
/*  89 */     txt = txt.replaceAll("^[aeiou]", "A");
/*  90 */     txt = txt.replaceAll("[aeiou]", "3");
/*  91 */     txt = txt.replaceAll("j", "y");
/*  92 */     txt = txt.replaceAll("^y3", "Y3");
/*  93 */     txt = txt.replaceAll("^y", "A");
/*  94 */     txt = txt.replaceAll("y", "3");
/*  95 */     txt = txt.replaceAll("3gh3", "3kh3");
/*  96 */     txt = txt.replaceAll("gh", "22");
/*  97 */     txt = txt.replaceAll("g", "k");
/*  98 */     txt = txt.replaceAll("s+", "S");
/*  99 */     txt = txt.replaceAll("t+", "T");
/* 100 */     txt = txt.replaceAll("p+", "P");
/* 101 */     txt = txt.replaceAll("k+", "K");
/* 102 */     txt = txt.replaceAll("f+", "F");
/* 103 */     txt = txt.replaceAll("m+", "M");
/* 104 */     txt = txt.replaceAll("n+", "N");
/* 105 */     txt = txt.replaceAll("w3", "W3");
/* 106 */     txt = txt.replaceAll("wh3", "Wh3");
/* 107 */     txt = txt.replaceAll("w$", "3");
/* 108 */     txt = txt.replaceAll("w", "2");
/* 109 */     txt = txt.replaceAll("^h", "A");
/* 110 */     txt = txt.replaceAll("h", "2");
/* 111 */     txt = txt.replaceAll("r3", "R3");
/* 112 */     txt = txt.replaceAll("r$", "3");
/* 113 */     txt = txt.replaceAll("r", "2");
/* 114 */     txt = txt.replaceAll("l3", "L3");
/* 115 */     txt = txt.replaceAll("l$", "3");
/* 116 */     txt = txt.replaceAll("l", "2");
/*     */ 
/*     */     
/* 119 */     txt = txt.replaceAll("2", "");
/* 120 */     txt = txt.replaceAll("3$", "A");
/* 121 */     txt = txt.replaceAll("3", "");
/*     */ 
/*     */     
/* 124 */     txt = txt + "1111111111";
/*     */ 
/*     */     
/* 127 */     return txt.substring(0, "1111111111".length());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\Caverphone2.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */