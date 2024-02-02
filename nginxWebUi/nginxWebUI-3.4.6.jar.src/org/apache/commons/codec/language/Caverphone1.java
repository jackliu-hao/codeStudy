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
/*     */ public class Caverphone1
/*     */   extends AbstractCaverphone
/*     */ {
/*     */   private static final String SIX_1 = "111111";
/*     */   
/*     */   public String encode(String source) {
/*  45 */     String txt = source;
/*  46 */     if (txt == null || txt.length() == 0) {
/*  47 */       return "111111";
/*     */     }
/*     */ 
/*     */     
/*  51 */     txt = txt.toLowerCase(Locale.ENGLISH);
/*     */ 
/*     */     
/*  54 */     txt = txt.replaceAll("[^a-z]", "");
/*     */ 
/*     */ 
/*     */     
/*  58 */     txt = txt.replaceAll("^cough", "cou2f");
/*  59 */     txt = txt.replaceAll("^rough", "rou2f");
/*  60 */     txt = txt.replaceAll("^tough", "tou2f");
/*  61 */     txt = txt.replaceAll("^enough", "enou2f");
/*  62 */     txt = txt.replaceAll("^gn", "2n");
/*     */ 
/*     */     
/*  65 */     txt = txt.replaceAll("mb$", "m2");
/*     */ 
/*     */     
/*  68 */     txt = txt.replaceAll("cq", "2q");
/*  69 */     txt = txt.replaceAll("ci", "si");
/*  70 */     txt = txt.replaceAll("ce", "se");
/*  71 */     txt = txt.replaceAll("cy", "sy");
/*  72 */     txt = txt.replaceAll("tch", "2ch");
/*  73 */     txt = txt.replaceAll("c", "k");
/*  74 */     txt = txt.replaceAll("q", "k");
/*  75 */     txt = txt.replaceAll("x", "k");
/*  76 */     txt = txt.replaceAll("v", "f");
/*  77 */     txt = txt.replaceAll("dg", "2g");
/*  78 */     txt = txt.replaceAll("tio", "sio");
/*  79 */     txt = txt.replaceAll("tia", "sia");
/*  80 */     txt = txt.replaceAll("d", "t");
/*  81 */     txt = txt.replaceAll("ph", "fh");
/*  82 */     txt = txt.replaceAll("b", "p");
/*  83 */     txt = txt.replaceAll("sh", "s2");
/*  84 */     txt = txt.replaceAll("z", "s");
/*  85 */     txt = txt.replaceAll("^[aeiou]", "A");
/*     */     
/*  87 */     txt = txt.replaceAll("[aeiou]", "3");
/*  88 */     txt = txt.replaceAll("3gh3", "3kh3");
/*  89 */     txt = txt.replaceAll("gh", "22");
/*  90 */     txt = txt.replaceAll("g", "k");
/*  91 */     txt = txt.replaceAll("s+", "S");
/*  92 */     txt = txt.replaceAll("t+", "T");
/*  93 */     txt = txt.replaceAll("p+", "P");
/*  94 */     txt = txt.replaceAll("k+", "K");
/*  95 */     txt = txt.replaceAll("f+", "F");
/*  96 */     txt = txt.replaceAll("m+", "M");
/*  97 */     txt = txt.replaceAll("n+", "N");
/*  98 */     txt = txt.replaceAll("w3", "W3");
/*  99 */     txt = txt.replaceAll("wy", "Wy");
/* 100 */     txt = txt.replaceAll("wh3", "Wh3");
/* 101 */     txt = txt.replaceAll("why", "Why");
/* 102 */     txt = txt.replaceAll("w", "2");
/* 103 */     txt = txt.replaceAll("^h", "A");
/* 104 */     txt = txt.replaceAll("h", "2");
/* 105 */     txt = txt.replaceAll("r3", "R3");
/* 106 */     txt = txt.replaceAll("ry", "Ry");
/* 107 */     txt = txt.replaceAll("r", "2");
/* 108 */     txt = txt.replaceAll("l3", "L3");
/* 109 */     txt = txt.replaceAll("ly", "Ly");
/* 110 */     txt = txt.replaceAll("l", "2");
/* 111 */     txt = txt.replaceAll("j", "y");
/* 112 */     txt = txt.replaceAll("y3", "Y3");
/* 113 */     txt = txt.replaceAll("y", "2");
/*     */ 
/*     */     
/* 116 */     txt = txt.replaceAll("2", "");
/* 117 */     txt = txt.replaceAll("3", "");
/*     */ 
/*     */     
/* 120 */     txt = txt + "111111";
/*     */ 
/*     */     
/* 123 */     return txt.substring(0, "111111".length());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\Caverphone1.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */