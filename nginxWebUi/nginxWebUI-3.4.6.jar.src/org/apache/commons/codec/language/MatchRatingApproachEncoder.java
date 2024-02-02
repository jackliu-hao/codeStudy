/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MatchRatingApproachEncoder
/*     */   implements StringEncoder
/*     */ {
/*     */   private static final String SPACE = " ";
/*     */   private static final String EMPTY = "";
/*     */   private static final int ONE = 1;
/*     */   private static final int TWO = 2;
/*     */   private static final int THREE = 3;
/*     */   private static final int FOUR = 4;
/*     */   private static final int FIVE = 5;
/*     */   private static final int SIX = 6;
/*     */   private static final int SEVEN = 7;
/*     */   private static final int ELEVEN = 11;
/*     */   private static final int TWELVE = 12;
/*     */   private static final String PLAIN_ASCII = "AaEeIiOoUuAaEeIiOoUuYyAaEeIiOoUuYyAaOoNnAaEeIiOoUuYyAaCcOoUu";
/*     */   private static final String UNICODE = "ÀàÈèÌìÒòÙùÁáÉéÍíÓóÚúÝýÂâÊêÎîÔôÛûŶŷÃãÕõÑñÄäËëÏïÖöÜüŸÿÅåÇçŐőŰű";
/*  66 */   private static final String[] DOUBLE_CONSONANT = new String[] { "BB", "CC", "DD", "FF", "GG", "HH", "JJ", "KK", "LL", "MM", "NN", "PP", "QQ", "RR", "SS", "TT", "VV", "WW", "XX", "YY", "ZZ" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String cleanName(String name) {
/*  84 */     String upperName = name.toUpperCase(Locale.ENGLISH);
/*     */     
/*  86 */     String[] charsToTrim = { "\\-", "[&]", "\\'", "\\.", "[\\,]" };
/*  87 */     for (String str : charsToTrim) {
/*  88 */       upperName = upperName.replaceAll(str, "");
/*     */     }
/*     */     
/*  91 */     upperName = removeAccents(upperName);
/*  92 */     upperName = upperName.replaceAll("\\s+", "");
/*     */     
/*  94 */     return upperName;
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
/*     */ 
/*     */   
/*     */   public final Object encode(Object pObject) throws EncoderException {
/* 110 */     if (!(pObject instanceof String)) {
/* 111 */       throw new EncoderException("Parameter supplied to Match Rating Approach encoder is not of type java.lang.String");
/*     */     }
/*     */     
/* 114 */     return encode((String)pObject);
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
/*     */   public final String encode(String name) {
/* 127 */     if (name == null || "".equalsIgnoreCase(name) || " ".equalsIgnoreCase(name) || name.length() == 1) {
/* 128 */       return "";
/*     */     }
/*     */ 
/*     */     
/* 132 */     name = cleanName(name);
/*     */ 
/*     */ 
/*     */     
/* 136 */     name = removeVowels(name);
/*     */ 
/*     */     
/* 139 */     name = removeDoubleConsonants(name);
/*     */ 
/*     */     
/* 142 */     name = getFirst3Last3(name);
/*     */     
/* 144 */     return name;
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
/*     */ 
/*     */   
/*     */   String getFirst3Last3(String name) {
/* 160 */     int nameLength = name.length();
/*     */     
/* 162 */     if (nameLength > 6) {
/* 163 */       String firstThree = name.substring(0, 3);
/* 164 */       String lastThree = name.substring(nameLength - 3, nameLength);
/* 165 */       return firstThree + lastThree;
/*     */     } 
/* 167 */     return name;
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
/*     */ 
/*     */ 
/*     */   
/*     */   int getMinRating(int sumLength) {
/* 184 */     int minRating = 0;
/*     */     
/* 186 */     if (sumLength <= 4) {
/* 187 */       minRating = 5;
/* 188 */     } else if (sumLength <= 7) {
/* 189 */       minRating = 4;
/* 190 */     } else if (sumLength <= 11) {
/* 191 */       minRating = 3;
/* 192 */     } else if (sumLength == 12) {
/* 193 */       minRating = 2;
/*     */     } else {
/* 195 */       minRating = 1;
/*     */     } 
/*     */     
/* 198 */     return minRating;
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
/*     */   
/*     */   public boolean isEncodeEquals(String name1, String name2) {
/* 213 */     if (name1 == null || "".equalsIgnoreCase(name1) || " ".equalsIgnoreCase(name1))
/* 214 */       return false; 
/* 215 */     if (name2 == null || "".equalsIgnoreCase(name2) || " ".equalsIgnoreCase(name2))
/* 216 */       return false; 
/* 217 */     if (name1.length() == 1 || name2.length() == 1)
/* 218 */       return false; 
/* 219 */     if (name1.equalsIgnoreCase(name2)) {
/* 220 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 224 */     name1 = cleanName(name1);
/* 225 */     name2 = cleanName(name2);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 230 */     name1 = removeVowels(name1);
/* 231 */     name2 = removeVowels(name2);
/*     */ 
/*     */     
/* 234 */     name1 = removeDoubleConsonants(name1);
/* 235 */     name2 = removeDoubleConsonants(name2);
/*     */ 
/*     */     
/* 238 */     name1 = getFirst3Last3(name1);
/* 239 */     name2 = getFirst3Last3(name2);
/*     */ 
/*     */ 
/*     */     
/* 243 */     if (Math.abs(name1.length() - name2.length()) >= 3) {
/* 244 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 249 */     int sumLength = Math.abs(name1.length() + name2.length());
/* 250 */     int minRating = 0;
/* 251 */     minRating = getMinRating(sumLength);
/*     */ 
/*     */ 
/*     */     
/* 255 */     int count = leftToRightThenRightToLeftProcessing(name1, name2);
/*     */ 
/*     */ 
/*     */     
/* 259 */     return (count >= minRating);
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
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isVowel(String letter) {
/* 276 */     return (letter.equalsIgnoreCase("E") || letter.equalsIgnoreCase("A") || letter.equalsIgnoreCase("O") || letter
/* 277 */       .equalsIgnoreCase("I") || letter.equalsIgnoreCase("U"));
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
/*     */ 
/*     */ 
/*     */   
/*     */   int leftToRightThenRightToLeftProcessing(String name1, String name2) {
/* 294 */     char[] name1Char = name1.toCharArray();
/* 295 */     char[] name2Char = name2.toCharArray();
/*     */     
/* 297 */     int name1Size = name1.length() - 1;
/* 298 */     int name2Size = name2.length() - 1;
/*     */     
/* 300 */     String name1LtRStart = "";
/* 301 */     String name1LtREnd = "";
/*     */     
/* 303 */     String name2RtLStart = "";
/* 304 */     String name2RtLEnd = "";
/*     */     
/* 306 */     for (int i = 0; i < name1Char.length && 
/* 307 */       i <= name2Size; i++) {
/*     */ 
/*     */ 
/*     */       
/* 311 */       name1LtRStart = name1.substring(i, i + 1);
/* 312 */       name1LtREnd = name1.substring(name1Size - i, name1Size - i + 1);
/*     */       
/* 314 */       name2RtLStart = name2.substring(i, i + 1);
/* 315 */       name2RtLEnd = name2.substring(name2Size - i, name2Size - i + 1);
/*     */ 
/*     */       
/* 318 */       if (name1LtRStart.equals(name2RtLStart)) {
/* 319 */         name1Char[i] = ' ';
/* 320 */         name2Char[i] = ' ';
/*     */       } 
/*     */ 
/*     */       
/* 324 */       if (name1LtREnd.equals(name2RtLEnd)) {
/* 325 */         name1Char[name1Size - i] = ' ';
/* 326 */         name2Char[name2Size - i] = ' ';
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 331 */     String strA = (new String(name1Char)).replaceAll("\\s+", "");
/* 332 */     String strB = (new String(name2Char)).replaceAll("\\s+", "");
/*     */ 
/*     */     
/* 335 */     if (strA.length() > strB.length()) {
/* 336 */       return Math.abs(6 - strA.length());
/*     */     }
/* 338 */     return Math.abs(6 - strB.length());
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
/*     */   String removeAccents(String accentedWord) {
/* 350 */     if (accentedWord == null) {
/* 351 */       return null;
/*     */     }
/*     */     
/* 354 */     StringBuilder sb = new StringBuilder();
/* 355 */     int n = accentedWord.length();
/*     */     
/* 357 */     for (int i = 0; i < n; i++) {
/* 358 */       char c = accentedWord.charAt(i);
/* 359 */       int pos = "ÀàÈèÌìÒòÙùÁáÉéÍíÓóÚúÝýÂâÊêÎîÔôÛûŶŷÃãÕõÑñÄäËëÏïÖöÜüŸÿÅåÇçŐőŰű".indexOf(c);
/* 360 */       if (pos > -1) {
/* 361 */         sb.append("AaEeIiOoUuAaEeIiOoUuYyAaEeIiOoUuYyAaOoNnAaEeIiOoUuYyAaCcOoUu".charAt(pos));
/*     */       } else {
/* 363 */         sb.append(c);
/*     */       } 
/*     */     } 
/*     */     
/* 367 */     return sb.toString();
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
/*     */ 
/*     */   
/*     */   String removeDoubleConsonants(String name) {
/* 383 */     String replacedName = name.toUpperCase(Locale.ENGLISH);
/* 384 */     for (String dc : DOUBLE_CONSONANT) {
/* 385 */       if (replacedName.contains(dc)) {
/* 386 */         String singleLetter = dc.substring(0, 1);
/* 387 */         replacedName = replacedName.replace(dc, singleLetter);
/*     */       } 
/*     */     } 
/* 390 */     return replacedName;
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
/*     */ 
/*     */ 
/*     */   
/*     */   String removeVowels(String name) {
/* 407 */     String firstLetter = name.substring(0, 1);
/*     */     
/* 409 */     name = name.replaceAll("A", "");
/* 410 */     name = name.replaceAll("E", "");
/* 411 */     name = name.replaceAll("I", "");
/* 412 */     name = name.replaceAll("O", "");
/* 413 */     name = name.replaceAll("U", "");
/*     */     
/* 415 */     name = name.replaceAll("\\s{2,}\\b", " ");
/*     */ 
/*     */     
/* 418 */     if (isVowel(firstLetter)) {
/* 419 */       return firstLetter + name;
/*     */     }
/* 421 */     return name;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\MatchRatingApproachEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */