/*     */ package com.google.zxing.oned.rss.expanded.decoders;
/*     */ 
/*     */ import com.google.zxing.NotFoundException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class FieldParser
/*     */ {
/*  37 */   private static final Object VARIABLE_LENGTH = new Object();
/*     */   
/*  39 */   private static final Object[][] TWO_DIGIT_DATA_LENGTH = new Object[][] { { "00", 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  44 */         Integer.valueOf(18) }, { "01", 
/*  45 */         Integer.valueOf(14) }, { "02", 
/*  46 */         Integer.valueOf(14) }, { "10", VARIABLE_LENGTH, 
/*     */         
/*  48 */         Integer.valueOf(20) }, { "11", 
/*  49 */         Integer.valueOf(6) }, { "12", 
/*  50 */         Integer.valueOf(6) }, { "13", 
/*  51 */         Integer.valueOf(6) }, { "15", 
/*  52 */         Integer.valueOf(6) }, { "17", 
/*  53 */         Integer.valueOf(6) }, { "20", 
/*     */         
/*  55 */         Integer.valueOf(2) }, { "21", VARIABLE_LENGTH, 
/*  56 */         Integer.valueOf(20) }, { "22", VARIABLE_LENGTH, 
/*  57 */         Integer.valueOf(29) }, { "30", VARIABLE_LENGTH, 
/*     */         
/*  59 */         Integer.valueOf(8) }, { "37", VARIABLE_LENGTH, 
/*  60 */         Integer.valueOf(8) }, { "90", VARIABLE_LENGTH, 
/*     */ 
/*     */         
/*  63 */         Integer.valueOf(30) }, { "91", VARIABLE_LENGTH, 
/*  64 */         Integer.valueOf(30) }, { "92", VARIABLE_LENGTH, 
/*  65 */         Integer.valueOf(30) }, { "93", VARIABLE_LENGTH, 
/*  66 */         Integer.valueOf(30) }, { "94", VARIABLE_LENGTH, 
/*  67 */         Integer.valueOf(30) }, { "95", VARIABLE_LENGTH, 
/*  68 */         Integer.valueOf(30) }, { "96", VARIABLE_LENGTH, 
/*  69 */         Integer.valueOf(30) }, { "97", VARIABLE_LENGTH, 
/*  70 */         Integer.valueOf(30) }, { "98", VARIABLE_LENGTH, 
/*  71 */         Integer.valueOf(30) }, { "99", VARIABLE_LENGTH, 
/*  72 */         Integer.valueOf(30) } };
/*     */ 
/*     */   
/*  75 */   private static final Object[][] THREE_DIGIT_DATA_LENGTH = new Object[][] { { "240", VARIABLE_LENGTH, 
/*     */ 
/*     */         
/*  78 */         Integer.valueOf(30) }, { "241", VARIABLE_LENGTH, 
/*  79 */         Integer.valueOf(30) }, { "242", VARIABLE_LENGTH, 
/*  80 */         Integer.valueOf(6) }, { "250", VARIABLE_LENGTH, 
/*  81 */         Integer.valueOf(30) }, { "251", VARIABLE_LENGTH, 
/*  82 */         Integer.valueOf(30) }, { "253", VARIABLE_LENGTH, 
/*  83 */         Integer.valueOf(17) }, { "254", VARIABLE_LENGTH, 
/*  84 */         Integer.valueOf(20) }, { "400", VARIABLE_LENGTH, 
/*     */         
/*  86 */         Integer.valueOf(30) }, { "401", VARIABLE_LENGTH, 
/*  87 */         Integer.valueOf(30) }, { "402", 
/*  88 */         Integer.valueOf(17) }, { "403", VARIABLE_LENGTH, 
/*  89 */         Integer.valueOf(30) }, { "410", 
/*  90 */         Integer.valueOf(13) }, { "411", 
/*  91 */         Integer.valueOf(13) }, { "412", 
/*  92 */         Integer.valueOf(13) }, { "413", 
/*  93 */         Integer.valueOf(13) }, { "414", 
/*  94 */         Integer.valueOf(13) }, { "420", VARIABLE_LENGTH, 
/*  95 */         Integer.valueOf(20) }, { "421", VARIABLE_LENGTH, 
/*  96 */         Integer.valueOf(15) }, { "422", 
/*  97 */         Integer.valueOf(3) }, { "423", VARIABLE_LENGTH, 
/*  98 */         Integer.valueOf(15) }, { "424", 
/*  99 */         Integer.valueOf(3) }, { "425", 
/* 100 */         Integer.valueOf(3) }, { "426", 
/* 101 */         Integer.valueOf(3) } };
/*     */ 
/*     */   
/* 104 */   private static final Object[][] THREE_DIGIT_PLUS_DIGIT_DATA_LENGTH = new Object[][] { { "310", 
/*     */ 
/*     */         
/* 107 */         Integer.valueOf(6) }, { "311", 
/* 108 */         Integer.valueOf(6) }, { "312", 
/* 109 */         Integer.valueOf(6) }, { "313", 
/* 110 */         Integer.valueOf(6) }, { "314", 
/* 111 */         Integer.valueOf(6) }, { "315", 
/* 112 */         Integer.valueOf(6) }, { "316", 
/* 113 */         Integer.valueOf(6) }, { "320", 
/* 114 */         Integer.valueOf(6) }, { "321", 
/* 115 */         Integer.valueOf(6) }, { "322", 
/* 116 */         Integer.valueOf(6) }, { "323", 
/* 117 */         Integer.valueOf(6) }, { "324", 
/* 118 */         Integer.valueOf(6) }, { "325", 
/* 119 */         Integer.valueOf(6) }, { "326", 
/* 120 */         Integer.valueOf(6) }, { "327", 
/* 121 */         Integer.valueOf(6) }, { "328", 
/* 122 */         Integer.valueOf(6) }, { "329", 
/* 123 */         Integer.valueOf(6) }, { "330", 
/* 124 */         Integer.valueOf(6) }, { "331", 
/* 125 */         Integer.valueOf(6) }, { "332", 
/* 126 */         Integer.valueOf(6) }, { "333", 
/* 127 */         Integer.valueOf(6) }, { "334", 
/* 128 */         Integer.valueOf(6) }, { "335", 
/* 129 */         Integer.valueOf(6) }, { "336", 
/* 130 */         Integer.valueOf(6) }, { "340", 
/* 131 */         Integer.valueOf(6) }, { "341", 
/* 132 */         Integer.valueOf(6) }, { "342", 
/* 133 */         Integer.valueOf(6) }, { "343", 
/* 134 */         Integer.valueOf(6) }, { "344", 
/* 135 */         Integer.valueOf(6) }, { "345", 
/* 136 */         Integer.valueOf(6) }, { "346", 
/* 137 */         Integer.valueOf(6) }, { "347", 
/* 138 */         Integer.valueOf(6) }, { "348", 
/* 139 */         Integer.valueOf(6) }, { "349", 
/* 140 */         Integer.valueOf(6) }, { "350", 
/* 141 */         Integer.valueOf(6) }, { "351", 
/* 142 */         Integer.valueOf(6) }, { "352", 
/* 143 */         Integer.valueOf(6) }, { "353", 
/* 144 */         Integer.valueOf(6) }, { "354", 
/* 145 */         Integer.valueOf(6) }, { "355", 
/* 146 */         Integer.valueOf(6) }, { "356", 
/* 147 */         Integer.valueOf(6) }, { "357", 
/* 148 */         Integer.valueOf(6) }, { "360", 
/* 149 */         Integer.valueOf(6) }, { "361", 
/* 150 */         Integer.valueOf(6) }, { "362", 
/* 151 */         Integer.valueOf(6) }, { "363", 
/* 152 */         Integer.valueOf(6) }, { "364", 
/* 153 */         Integer.valueOf(6) }, { "365", 
/* 154 */         Integer.valueOf(6) }, { "366", 
/* 155 */         Integer.valueOf(6) }, { "367", 
/* 156 */         Integer.valueOf(6) }, { "368", 
/* 157 */         Integer.valueOf(6) }, { "369", 
/* 158 */         Integer.valueOf(6) }, { "390", VARIABLE_LENGTH, 
/* 159 */         Integer.valueOf(15) }, { "391", VARIABLE_LENGTH, 
/* 160 */         Integer.valueOf(18) }, { "392", VARIABLE_LENGTH, 
/* 161 */         Integer.valueOf(15) }, { "393", VARIABLE_LENGTH, 
/* 162 */         Integer.valueOf(18) }, { "703", VARIABLE_LENGTH, 
/* 163 */         Integer.valueOf(30) } };
/*     */ 
/*     */   
/* 166 */   private static final Object[][] FOUR_DIGIT_DATA_LENGTH = new Object[][] { { "7001", 
/*     */ 
/*     */         
/* 169 */         Integer.valueOf(13) }, { "7002", VARIABLE_LENGTH, 
/* 170 */         Integer.valueOf(30) }, { "7003", 
/* 171 */         Integer.valueOf(10) }, { "8001", 
/*     */         
/* 173 */         Integer.valueOf(14) }, { "8002", VARIABLE_LENGTH, 
/* 174 */         Integer.valueOf(20) }, { "8003", VARIABLE_LENGTH, 
/* 175 */         Integer.valueOf(30) }, { "8004", VARIABLE_LENGTH, 
/* 176 */         Integer.valueOf(30) }, { "8005", 
/* 177 */         Integer.valueOf(6) }, { "8006", 
/* 178 */         Integer.valueOf(18) }, { "8007", VARIABLE_LENGTH, 
/* 179 */         Integer.valueOf(30) }, { "8008", VARIABLE_LENGTH, 
/* 180 */         Integer.valueOf(12) }, { "8018", 
/* 181 */         Integer.valueOf(18) }, { "8020", VARIABLE_LENGTH, 
/* 182 */         Integer.valueOf(25) }, { "8100", 
/* 183 */         Integer.valueOf(6) }, { "8101", 
/* 184 */         Integer.valueOf(10) }, { "8102", 
/* 185 */         Integer.valueOf(2) }, { "8110", VARIABLE_LENGTH, 
/* 186 */         Integer.valueOf(70) }, { "8200", VARIABLE_LENGTH, 
/* 187 */         Integer.valueOf(70) } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String parseFieldsInGeneralPurpose(String rawInformation) throws NotFoundException {
/* 194 */     if (rawInformation.isEmpty()) {
/* 195 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 200 */     if (rawInformation.length() < 2) {
/* 201 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 204 */     String firstTwoDigits = rawInformation.substring(0, 2); Object[][] arrayOfObject1;
/*     */     int j;
/* 206 */     for (int i = (arrayOfObject1 = TWO_DIGIT_DATA_LENGTH).length; j < i; j++) {
/* 207 */       Object[] dataLength; if ((dataLength = arrayOfObject1[j])[0].equals(firstTwoDigits)) {
/* 208 */         if (dataLength[1] == VARIABLE_LENGTH) {
/* 209 */           return processVariableAI(2, ((Integer)dataLength[2]).intValue(), rawInformation);
/*     */         }
/* 211 */         return processFixedAI(2, ((Integer)dataLength[1]).intValue(), rawInformation);
/*     */       } 
/*     */     } 
/*     */     
/* 215 */     if (rawInformation.length() < 3) {
/* 216 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 219 */     String firstThreeDigits = rawInformation.substring(0, 3); Object[][] arrayOfObject2;
/*     */     int k;
/* 221 */     for (j = (arrayOfObject2 = THREE_DIGIT_DATA_LENGTH).length, k = 0; k < j; k++) {
/* 222 */       Object[] dataLength; if ((dataLength = arrayOfObject2[k])[0].equals(firstThreeDigits)) {
/* 223 */         if (dataLength[1] == VARIABLE_LENGTH) {
/* 224 */           return processVariableAI(3, ((Integer)dataLength[2]).intValue(), rawInformation);
/*     */         }
/* 226 */         return processFixedAI(3, ((Integer)dataLength[1]).intValue(), rawInformation);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 231 */     for (j = (arrayOfObject2 = THREE_DIGIT_PLUS_DIGIT_DATA_LENGTH).length, k = 0; k < j; k++) {
/* 232 */       Object[] dataLength; if ((dataLength = arrayOfObject2[k])[0].equals(firstThreeDigits)) {
/* 233 */         if (dataLength[1] == VARIABLE_LENGTH) {
/* 234 */           return processVariableAI(4, ((Integer)dataLength[2]).intValue(), rawInformation);
/*     */         }
/* 236 */         return processFixedAI(4, ((Integer)dataLength[1]).intValue(), rawInformation);
/*     */       } 
/*     */     } 
/*     */     
/* 240 */     if (rawInformation.length() < 4) {
/* 241 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 244 */     String firstFourDigits = rawInformation.substring(0, 4); Object[][] arrayOfObject3;
/*     */     byte b;
/* 246 */     for (k = (arrayOfObject3 = FOUR_DIGIT_DATA_LENGTH).length, b = 0; b < k; b++) {
/* 247 */       Object[] dataLength; if ((dataLength = arrayOfObject3[b])[0].equals(firstFourDigits)) {
/* 248 */         if (dataLength[1] == VARIABLE_LENGTH) {
/* 249 */           return processVariableAI(4, ((Integer)dataLength[2]).intValue(), rawInformation);
/*     */         }
/* 251 */         return processFixedAI(4, ((Integer)dataLength[1]).intValue(), rawInformation);
/*     */       } 
/*     */     } 
/*     */     
/* 255 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   private static String processFixedAI(int aiSize, int fieldSize, String rawInformation) throws NotFoundException {
/* 259 */     if (rawInformation.length() < aiSize) {
/* 260 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 263 */     String ai = rawInformation.substring(0, aiSize);
/*     */     
/* 265 */     if (rawInformation.length() < aiSize + fieldSize) {
/* 266 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 269 */     String field = rawInformation.substring(aiSize, aiSize + fieldSize);
/* 270 */     String remaining = rawInformation.substring(aiSize + fieldSize);
/* 271 */     String result = "(" + ai + ')' + field;
/*     */     String parsedAI;
/* 273 */     return ((parsedAI = parseFieldsInGeneralPurpose(remaining)) == null) ? result : (result + parsedAI);
/*     */   }
/*     */   
/*     */   private static String processVariableAI(int aiSize, int variableFieldSize, String rawInformation) throws NotFoundException {
/*     */     int maxSize;
/* 278 */     String ai = rawInformation.substring(0, aiSize);
/*     */     
/* 280 */     if (rawInformation.length() < aiSize + variableFieldSize) {
/* 281 */       maxSize = rawInformation.length();
/*     */     } else {
/* 283 */       maxSize = aiSize + variableFieldSize;
/*     */     } 
/* 285 */     String field = rawInformation.substring(aiSize, maxSize);
/* 286 */     String remaining = rawInformation.substring(maxSize);
/* 287 */     String result = "(" + ai + ')' + field;
/*     */     String parsedAI;
/* 289 */     return ((parsedAI = parseFieldsInGeneralPurpose(remaining)) == null) ? result : (result + parsedAI);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\FieldParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */