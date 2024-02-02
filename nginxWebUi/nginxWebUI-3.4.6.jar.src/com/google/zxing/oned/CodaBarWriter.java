/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CodaBarWriter
/*     */   extends OneDimensionalCodeWriter
/*     */ {
/*  26 */   private static final char[] START_END_CHARS = new char[] { 'A', 'B', 'C', 'D' };
/*  27 */   private static final char[] ALT_START_END_CHARS = new char[] { 'T', 'N', '*', 'E' };
/*  28 */   private static final char[] CHARS_WHICH_ARE_TEN_LENGTH_EACH_AFTER_DECODED = new char[] { '/', ':', '+', '.' };
/*  29 */   private static final char DEFAULT_GUARD = START_END_CHARS[0];
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean[] encode(String contents) {
/*  34 */     if (contents.length() < 2) {
/*     */       
/*  36 */       contents = DEFAULT_GUARD + contents + DEFAULT_GUARD;
/*     */     } else {
/*     */       
/*  39 */       char firstChar = Character.toUpperCase(contents.charAt(0));
/*  40 */       char lastChar = Character.toUpperCase(contents.charAt(contents.length() - 1));
/*  41 */       boolean startsNormal = CodaBarReader.arrayContains(START_END_CHARS, firstChar);
/*  42 */       boolean endsNormal = CodaBarReader.arrayContains(START_END_CHARS, lastChar);
/*  43 */       boolean startsAlt = CodaBarReader.arrayContains(ALT_START_END_CHARS, firstChar);
/*  44 */       boolean endsAlt = CodaBarReader.arrayContains(ALT_START_END_CHARS, lastChar);
/*  45 */       if (startsNormal) {
/*  46 */         if (!endsNormal) {
/*  47 */           throw new IllegalArgumentException("Invalid start/end guards: " + contents);
/*     */         }
/*     */       }
/*  50 */       else if (startsAlt) {
/*  51 */         if (!endsAlt) {
/*  52 */           throw new IllegalArgumentException("Invalid start/end guards: " + contents);
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/*  57 */         if (endsNormal || endsAlt) {
/*  58 */           throw new IllegalArgumentException("Invalid start/end guards: " + contents);
/*     */         }
/*     */         
/*  61 */         contents = DEFAULT_GUARD + contents + DEFAULT_GUARD;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  66 */     int resultLength = 20;
/*  67 */     for (int i = 1; i < contents.length() - 1; i++) {
/*  68 */       if (Character.isDigit(contents.charAt(i)) || contents.charAt(i) == '-' || contents.charAt(i) == '$') {
/*  69 */         resultLength += 9;
/*  70 */       } else if (CodaBarReader.arrayContains(CHARS_WHICH_ARE_TEN_LENGTH_EACH_AFTER_DECODED, contents.charAt(i))) {
/*  71 */         resultLength += 10;
/*     */       } else {
/*  73 */         throw new IllegalArgumentException("Cannot encode : '" + contents.charAt(i) + '\'');
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  79 */     boolean[] result = new boolean[resultLength + contents.length() - 1];
/*  80 */     int position = 0;
/*  81 */     for (int index = 0; index < contents.length(); index++) {
/*  82 */       char c = Character.toUpperCase(contents.charAt(index));
/*  83 */       if (index == 0 || index == contents.length() - 1)
/*     */       {
/*  85 */         switch (c) {
/*     */           case 'T':
/*  87 */             c = 'A';
/*     */             break;
/*     */           case 'N':
/*  90 */             c = 'B';
/*     */             break;
/*     */           case '*':
/*  93 */             c = 'C';
/*     */             break;
/*     */           case 'E':
/*  96 */             c = 'D';
/*     */             break;
/*     */         } 
/*     */       }
/* 100 */       int code = 0;
/* 101 */       for (int j = 0; j < CodaBarReader.ALPHABET.length; j++) {
/*     */         
/* 103 */         if (c == CodaBarReader.ALPHABET[j]) {
/* 104 */           code = CodaBarReader.CHARACTER_ENCODINGS[j];
/*     */           break;
/*     */         } 
/*     */       } 
/* 108 */       boolean color = true;
/* 109 */       int counter = 0;
/* 110 */       int bit = 0;
/* 111 */       while (bit < 7) {
/* 112 */         result[position] = color;
/* 113 */         position++;
/* 114 */         if ((code >> 6 - bit & 0x1) == 0 || counter == 1) {
/* 115 */           color = !color;
/* 116 */           bit++;
/* 117 */           counter = 0; continue;
/*     */         } 
/* 119 */         counter++;
/*     */       } 
/*     */       
/* 122 */       if (index < contents.length() - 1) {
/* 123 */         result[position] = false;
/* 124 */         position++;
/*     */       } 
/*     */     } 
/* 127 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\CodaBarWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */