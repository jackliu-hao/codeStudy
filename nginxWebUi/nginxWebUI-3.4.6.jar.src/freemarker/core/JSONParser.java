/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleHash;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.Constants;
/*     */ import freemarker.template.utility.NumberUtil;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
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
/*     */ class JSONParser
/*     */ {
/*     */   private static final String UNCLOSED_OBJECT_MESSAGE = "This {...} was still unclosed when the end of the file was reached. (Look for a missing \"}\")";
/*     */   private static final String UNCLOSED_ARRAY_MESSAGE = "This [...] was still unclosed when the end of the file was reached. (Look for a missing \"]\")";
/*  70 */   private static final BigDecimal MIN_INT_AS_BIGDECIMAL = BigDecimal.valueOf(-2147483648L);
/*  71 */   private static final BigDecimal MAX_INT_AS_BIGDECIMAL = BigDecimal.valueOf(2147483647L);
/*  72 */   private static final BigDecimal MIN_LONG_AS_BIGDECIMAL = BigDecimal.valueOf(Long.MIN_VALUE);
/*  73 */   private static final BigDecimal MAX_LONG_AS_BIGDECIMAL = BigDecimal.valueOf(Long.MAX_VALUE);
/*     */   
/*     */   private final String src;
/*     */   
/*     */   private final int ln;
/*     */   private int p;
/*     */   
/*     */   public static TemplateModel parse(String src) throws JSONParseException {
/*  81 */     return (new JSONParser(src)).parse();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JSONParser(String src) {
/*  88 */     this.src = src;
/*  89 */     this.ln = src.length();
/*     */   }
/*     */   
/*     */   private TemplateModel parse() throws JSONParseException {
/*  93 */     skipWS();
/*  94 */     TemplateModel result = consumeValue("Empty JSON (contains no value)", this.p);
/*     */     
/*  96 */     skipWS();
/*  97 */     if (this.p != this.ln) {
/*  98 */       throw newParseException("End-of-file was expected but found further non-whitespace characters.");
/*     */     }
/*     */     
/* 101 */     return result;
/*     */   }
/*     */   
/*     */   private TemplateModel consumeValue(String eofErrorMessage, int eofBlamePosition) throws JSONParseException {
/* 105 */     if (this.p == this.ln) {
/* 106 */       throw newParseException((eofErrorMessage == null) ? "A value was expected here, but end-of-file was reached." : eofErrorMessage, (eofBlamePosition == -1) ? this.p : eofBlamePosition);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     TemplateScalarModel templateScalarModel = tryConsumeString();
/* 115 */     if (templateScalarModel != null) return (TemplateModel)templateScalarModel;
/*     */     
/* 117 */     TemplateNumberModel templateNumberModel = tryConsumeNumber();
/* 118 */     if (templateNumberModel != null) return (TemplateModel)templateNumberModel;
/*     */     
/* 120 */     TemplateHashModelEx2 templateHashModelEx2 = tryConsumeObject();
/* 121 */     if (templateHashModelEx2 != null) return (TemplateModel)templateHashModelEx2;
/*     */     
/* 123 */     TemplateSequenceModel templateSequenceModel = tryConsumeArray();
/* 124 */     if (templateSequenceModel != null) return (TemplateModel)templateSequenceModel;
/*     */     
/* 126 */     TemplateModel templateModel = tryConsumeTrueFalseNull();
/* 127 */     if (templateModel != null) return (templateModel != TemplateNullModel.INSTANCE) ? templateModel : null;
/*     */ 
/*     */     
/* 130 */     if (this.p < this.ln && this.src.charAt(this.p) == '\'') {
/* 131 */       throw newParseException("Unexpected apostrophe-quote character. JSON strings must be quoted with quotation mark.");
/*     */     }
/*     */ 
/*     */     
/* 135 */     throw newParseException("Expected either the beginning of a (negative) number or the beginning of one of these: {...}, [...], \"...\", true, false, null. Found character " + 
/*     */         
/* 137 */         StringUtil.jQuote(Character.valueOf(this.src.charAt(this.p))) + " instead.");
/*     */   }
/*     */ 
/*     */   
/*     */   private TemplateModel tryConsumeTrueFalseNull() throws JSONParseException {
/* 142 */     int startP = this.p;
/* 143 */     if (this.p < this.ln && isIdentifierStart(this.src.charAt(this.p))) {
/* 144 */       this.p++;
/* 145 */       while (this.p < this.ln && isIdentifierPart(this.src.charAt(this.p))) {
/* 146 */         this.p++;
/*     */       }
/*     */     } 
/*     */     
/* 150 */     if (startP == this.p) return null;
/*     */     
/* 152 */     String keyword = this.src.substring(startP, this.p);
/* 153 */     if (keyword.equals("true"))
/* 154 */       return (TemplateModel)TemplateBooleanModel.TRUE; 
/* 155 */     if (keyword.equals("false"))
/* 156 */       return (TemplateModel)TemplateBooleanModel.FALSE; 
/* 157 */     if (keyword.equals("null")) {
/* 158 */       return TemplateNullModel.INSTANCE;
/*     */     }
/*     */     
/* 161 */     throw newParseException("Invalid JSON keyword: " + 
/* 162 */         StringUtil.jQuote(keyword) + ". Should be one of: true, false, null. If it meant to be a string then it must be quoted.", startP);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private TemplateNumberModel tryConsumeNumber() throws JSONParseException {
/* 168 */     if (this.p >= this.ln) {
/* 169 */       return null;
/*     */     }
/* 171 */     char c = this.src.charAt(this.p);
/* 172 */     boolean negative = (c == '-');
/* 173 */     if (!negative && !isDigit(c) && c != '.') {
/* 174 */       return null;
/*     */     }
/*     */     
/* 177 */     int startP = this.p;
/*     */     
/* 179 */     if (negative) {
/* 180 */       if (this.p + 1 >= this.ln) {
/* 181 */         throw newParseException("Expected a digit after \"-\", but reached end-of-file.");
/*     */       }
/* 183 */       char lookAheadC = this.src.charAt(this.p + 1);
/* 184 */       if (!isDigit(lookAheadC) && lookAheadC != '.') {
/* 185 */         return null;
/*     */       }
/* 187 */       this.p++;
/*     */     } 
/*     */     
/* 190 */     long longSum = 0L;
/* 191 */     boolean firstDigit = true;
/*     */     do {
/* 193 */       c = this.src.charAt(this.p);
/*     */       
/* 195 */       if (!isDigit(c)) {
/* 196 */         if (c == '.' && firstDigit) {
/* 197 */           throw newParseException("JSON doesn't allow numbers starting with \".\".");
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/* 202 */       int digit = c - 48;
/* 203 */       if (longSum == 0L) {
/* 204 */         if (!firstDigit) {
/* 205 */           throw newParseException("JSON doesn't allow superfluous leading 0-s.", this.p - 1);
/*     */         }
/*     */         
/* 208 */         longSum = !negative ? digit : -digit;
/* 209 */         this.p++;
/*     */       } else {
/* 211 */         long prevLongSum = longSum;
/* 212 */         longSum = longSum * 10L + (!negative ? digit : -digit);
/* 213 */         if ((!negative && prevLongSum > longSum) || (negative && prevLongSum < longSum)) {
/*     */           break;
/*     */         }
/*     */         
/* 217 */         this.p++;
/*     */       } 
/* 219 */       firstDigit = false;
/* 220 */     } while (this.p < this.ln);
/*     */     
/* 222 */     if (this.p < this.ln && isBigDecimalFittingTailCharacter(c)) {
/* 223 */       BigDecimal bd; char lastC = c;
/* 224 */       this.p++;
/*     */       
/* 226 */       while (this.p < this.ln) {
/* 227 */         c = this.src.charAt(this.p);
/* 228 */         if (isBigDecimalFittingTailCharacter(c)) {
/* 229 */           this.p++;
/* 230 */         } else if ((c == '+' || c == '-') && isE(lastC)) {
/* 231 */           this.p++;
/*     */         } else {
/*     */           break;
/*     */         } 
/* 235 */         lastC = c;
/*     */       } 
/*     */       
/* 238 */       String numStr = this.src.substring(startP, this.p);
/*     */       
/*     */       try {
/* 241 */         bd = new BigDecimal(numStr);
/* 242 */       } catch (NumberFormatException e) {
/* 243 */         throw new JSONParseException("Malformed number: " + numStr, this.src, startP, e);
/*     */       } 
/*     */       
/* 246 */       if (bd.compareTo(MIN_INT_AS_BIGDECIMAL) >= 0 && bd.compareTo(MAX_INT_AS_BIGDECIMAL) <= 0) {
/* 247 */         if (NumberUtil.isIntegerBigDecimal(bd)) {
/* 248 */           return (TemplateNumberModel)new SimpleNumber(bd.intValue());
/*     */         }
/* 250 */       } else if (bd.compareTo(MIN_LONG_AS_BIGDECIMAL) >= 0 && bd.compareTo(MAX_LONG_AS_BIGDECIMAL) <= 0 && 
/* 251 */         NumberUtil.isIntegerBigDecimal(bd)) {
/* 252 */         return (TemplateNumberModel)new SimpleNumber(bd.longValue());
/*     */       } 
/*     */       
/* 255 */       return (TemplateNumberModel)new SimpleNumber(bd);
/*     */     } 
/* 257 */     return (TemplateNumberModel)new SimpleNumber((longSum <= 2147483647L && longSum >= -2147483648L) ? 
/*     */         
/* 259 */         Integer.valueOf((int)longSum) : 
/* 260 */         Long.valueOf(longSum));
/*     */   }
/*     */ 
/*     */   
/*     */   private TemplateScalarModel tryConsumeString() throws JSONParseException {
/* 265 */     int startP = this.p;
/* 266 */     if (!tryConsumeChar('"')) return null;
/*     */     
/* 268 */     StringBuilder sb = new StringBuilder();
/* 269 */     char c = Character.MIN_VALUE;
/* 270 */     while (this.p < this.ln) {
/* 271 */       c = this.src.charAt(this.p);
/*     */       
/* 273 */       if (c == '"') {
/* 274 */         this.p++;
/* 275 */         return (TemplateScalarModel)new SimpleScalar(sb.toString());
/* 276 */       }  if (c == '\\') {
/* 277 */         this.p++;
/* 278 */         sb.append(consumeAfterBackslash()); continue;
/* 279 */       }  if (c <= '\037') {
/* 280 */         throw newParseException("JSON doesn't allow unescaped control characters in string literals, but found character with code (decimal): " + c);
/*     */       }
/*     */       
/* 283 */       this.p++;
/* 284 */       sb.append(c);
/*     */     } 
/*     */ 
/*     */     
/* 288 */     throw newParseException("String literal was still unclosed when the end of the file was reached. (Look for missing or accidentally escaped closing quotation mark.)", startP);
/*     */   }
/*     */ 
/*     */   
/*     */   private TemplateSequenceModel tryConsumeArray() throws JSONParseException {
/* 293 */     int startP = this.p;
/* 294 */     if (!tryConsumeChar('[')) return null;
/*     */     
/* 296 */     skipWS();
/* 297 */     if (tryConsumeChar(']')) return Constants.EMPTY_SEQUENCE;
/*     */     
/* 299 */     boolean afterComma = false;
/* 300 */     SimpleSequence elements = new SimpleSequence((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*     */     while (true) {
/* 302 */       skipWS();
/* 303 */       elements.add(consumeValue(afterComma ? null : "This [...] was still unclosed when the end of the file was reached. (Look for a missing \"]\")", afterComma ? -1 : startP));
/*     */       
/* 305 */       skipWS();
/* 306 */       afterComma = true;
/* 307 */       if (consumeChar(',', ']', "This [...] was still unclosed when the end of the file was reached. (Look for a missing \"]\")", startP) != ',')
/* 308 */         return (TemplateSequenceModel)elements; 
/*     */     } 
/*     */   }
/*     */   private TemplateHashModelEx2 tryConsumeObject() throws JSONParseException {
/* 312 */     int startP = this.p;
/* 313 */     if (!tryConsumeChar('{')) return null;
/*     */     
/* 315 */     skipWS();
/* 316 */     if (tryConsumeChar('}')) return Constants.EMPTY_HASH_EX2;
/*     */     
/* 318 */     boolean afterComma = false;
/* 319 */     Map<String, Object> map = new LinkedHashMap<>();
/*     */     while (true) {
/* 321 */       skipWS();
/* 322 */       int keyStartP = this.p;
/* 323 */       Object key = consumeValue(afterComma ? null : "This {...} was still unclosed when the end of the file was reached. (Look for a missing \"}\")", afterComma ? -1 : startP);
/* 324 */       if (!(key instanceof TemplateScalarModel)) {
/* 325 */         throw newParseException("Wrong key type. JSON only allows string keys inside {...}.", keyStartP);
/*     */       }
/* 327 */       String strKey = null;
/*     */       try {
/* 329 */         strKey = ((TemplateScalarModel)key).getAsString();
/* 330 */       } catch (TemplateModelException e) {
/* 331 */         throw new BugException(e);
/*     */       } 
/*     */       
/* 334 */       skipWS();
/* 335 */       consumeChar(':');
/*     */       
/* 337 */       skipWS();
/* 338 */       map.put(strKey, consumeValue(null, -1));
/*     */       
/* 340 */       skipWS();
/* 341 */       afterComma = true;
/* 342 */       if (consumeChar(',', '}', "This {...} was still unclosed when the end of the file was reached. (Look for a missing \"}\")", startP) != ',')
/* 343 */         return (TemplateHashModelEx2)new SimpleHash(map, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER, 0); 
/*     */     } 
/*     */   }
/*     */   private boolean isE(char c) {
/* 347 */     return (c == 'e' || c == 'E');
/*     */   }
/*     */   
/*     */   private boolean isBigDecimalFittingTailCharacter(char c) {
/* 351 */     return (c == '.' || isE(c) || isDigit(c));
/*     */   }
/*     */   
/*     */   private char consumeAfterBackslash() throws JSONParseException {
/* 355 */     if (this.p == this.ln) {
/* 356 */       throw newParseException("Reached the end of the file, but the escape is unclosed.");
/*     */     }
/*     */     
/* 359 */     char c = this.src.charAt(this.p);
/* 360 */     switch (c) {
/*     */       case '"':
/*     */       case '/':
/*     */       case '\\':
/* 364 */         this.p++;
/* 365 */         return c;
/*     */       case 'b':
/* 367 */         this.p++;
/* 368 */         return '\b';
/*     */       case 'f':
/* 370 */         this.p++;
/* 371 */         return '\f';
/*     */       case 'n':
/* 373 */         this.p++;
/* 374 */         return '\n';
/*     */       case 'r':
/* 376 */         this.p++;
/* 377 */         return '\r';
/*     */       case 't':
/* 379 */         this.p++;
/* 380 */         return '\t';
/*     */       case 'u':
/* 382 */         this.p++;
/* 383 */         return consumeAfterBackslashU();
/*     */     } 
/* 385 */     throw newParseException("Unsupported escape: \\" + c);
/*     */   }
/*     */   
/*     */   private char consumeAfterBackslashU() throws JSONParseException {
/* 389 */     if (this.p + 3 >= this.ln) {
/* 390 */       throw newParseException("\\u must be followed by exactly 4 hexadecimal digits");
/*     */     }
/* 392 */     String hex = this.src.substring(this.p, this.p + 4);
/*     */     try {
/* 394 */       char r = (char)Integer.parseInt(hex, 16);
/* 395 */       this.p += 4;
/* 396 */       return r;
/* 397 */     } catch (NumberFormatException e) {
/* 398 */       throw newParseException("\\u must be followed by exactly 4 hexadecimal digits, but was followed by " + 
/* 399 */           StringUtil.jQuote(hex) + ".");
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean tryConsumeChar(char c) {
/* 404 */     if (this.p < this.ln && this.src.charAt(this.p) == c) {
/* 405 */       this.p++;
/* 406 */       return true;
/*     */     } 
/* 408 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void consumeChar(char expected) throws JSONParseException {
/* 413 */     consumeChar(expected, false, null, -1);
/*     */   }
/*     */   
/*     */   private char consumeChar(char expected1, char expected2, String eofErrorHint, int eofErrorP) throws JSONParseException {
/* 417 */     if (this.p >= this.ln) {
/* 418 */       throw newParseException((eofErrorHint == null) ? ("Expected " + 
/* 419 */           StringUtil.jQuote(Character.valueOf(expected1)) + ((expected2 != '\000') ? (" or " + 
/* 420 */           StringUtil.jQuote(Character.valueOf(expected2))) : "") + " character, but reached end-of-file. ") : eofErrorHint, (eofErrorP == -1) ? this.p : eofErrorP);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 425 */     char c = this.src.charAt(this.p);
/* 426 */     if (c == expected1 || (expected2 != '\000' && c == expected2)) {
/* 427 */       this.p++;
/* 428 */       return c;
/*     */     } 
/* 430 */     throw newParseException("Expected " + StringUtil.jQuote(Character.valueOf(expected1)) + ((expected2 != '\000') ? (" or " + 
/* 431 */         StringUtil.jQuote(Character.valueOf(expected2))) : "") + " character, but found " + 
/* 432 */         StringUtil.jQuote(Character.valueOf(c)) + " instead.");
/*     */   }
/*     */   
/*     */   private void skipWS() throws JSONParseException {
/*     */     while (true) {
/* 437 */       if (this.p < this.ln && isWS(this.src.charAt(this.p))) {
/* 438 */         this.p++; continue;
/*     */       } 
/* 440 */       if (!skipComment())
/*     */         break; 
/*     */     } 
/*     */   } private boolean skipComment() throws JSONParseException {
/* 444 */     if (this.p + 1 < this.ln && 
/* 445 */       this.src.charAt(this.p) == '/') {
/* 446 */       char c2 = this.src.charAt(this.p + 1);
/* 447 */       if (c2 == '/') {
/* 448 */         int eolP = this.p + 2;
/* 449 */         while (eolP < this.ln && !isLineBreak(this.src.charAt(eolP))) {
/* 450 */           eolP++;
/*     */         }
/* 452 */         this.p = eolP;
/* 453 */         return true;
/* 454 */       }  if (c2 == '*') {
/* 455 */         int closerP = this.p + 3;
/* 456 */         while (closerP < this.ln && (this.src.charAt(closerP - 1) != '*' || this.src.charAt(closerP) != '/')) {
/* 457 */           closerP++;
/*     */         }
/* 459 */         if (closerP >= this.ln) {
/* 460 */           throw newParseException("Unclosed comment");
/*     */         }
/* 462 */         this.p = closerP + 1;
/* 463 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 467 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isWS(char c) {
/* 474 */     return (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == ' ' || c == '﻿');
/*     */   }
/*     */   
/*     */   private static boolean isLineBreak(char c) {
/* 478 */     return (c == '\r' || c == '\n');
/*     */   }
/*     */   
/*     */   private static boolean isIdentifierStart(char c) {
/* 482 */     return (Character.isLetter(c) || c == '_' || c == '$');
/*     */   }
/*     */   
/*     */   private static boolean isDigit(char c) {
/* 486 */     return (c >= '0' && c <= '9');
/*     */   }
/*     */   
/*     */   private static boolean isIdentifierPart(char c) {
/* 490 */     return (isIdentifierStart(c) || isDigit(c));
/*     */   }
/*     */   
/*     */   private JSONParseException newParseException(String message) {
/* 494 */     return newParseException(message, this.p);
/*     */   }
/*     */   
/*     */   private JSONParseException newParseException(String message, int p) {
/* 498 */     return new JSONParseException(message, this.src, p);
/*     */   }
/*     */   
/*     */   static class JSONParseException extends Exception {
/*     */     public JSONParseException(String message, String src, int position) {
/* 503 */       super(JSONParser.createSourceCodeErrorMessage(message, src, position));
/*     */     }
/*     */ 
/*     */     
/*     */     public JSONParseException(String message, String src, int position, Throwable cause) {
/* 508 */       super(JSONParser.createSourceCodeErrorMessage(message, src, position), cause);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 513 */   private static int MAX_QUOTATION_LENGTH = 50;
/*     */   
/*     */   private static String createSourceCodeErrorMessage(String message, String srcCode, int position) {
/* 516 */     int ln = srcCode.length();
/* 517 */     if (position < 0) {
/* 518 */       position = 0;
/*     */     }
/* 520 */     if (position >= ln) {
/* 521 */       return message + "\nError location: At the end of text.";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 527 */     int rowBegin = 0;
/*     */     
/* 529 */     int row = 1;
/* 530 */     char lastChar = Character.MIN_VALUE; int i;
/* 531 */     for (i = 0; i <= position; i++) {
/* 532 */       char c = srcCode.charAt(i);
/* 533 */       if (lastChar == '\n') {
/* 534 */         rowBegin = i;
/* 535 */         row++;
/* 536 */       } else if (lastChar == '\r' && c != '\n') {
/* 537 */         rowBegin = i;
/* 538 */         row++;
/*     */       } 
/* 540 */       lastChar = c;
/*     */     } 
/* 542 */     for (i = position; i < ln; i++) {
/* 543 */       char c = srcCode.charAt(i);
/* 544 */       if (c == '\n' || c == '\r') {
/* 545 */         if (c == '\n' && i > 0 && srcCode.charAt(i - 1) == '\r') {
/* 546 */           i--;
/*     */         }
/*     */         break;
/*     */       } 
/*     */     } 
/* 551 */     int rowEnd = i - 1;
/* 552 */     if (position > rowEnd + 1) {
/* 553 */       position = rowEnd + 1;
/*     */     }
/* 555 */     int col = position - rowBegin + 1;
/* 556 */     if (rowBegin > rowEnd) {
/* 557 */       return message + "\nError location: line " + row + ", column " + col + ":\n(Can't show the line because it is empty.)";
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 562 */     String s1 = srcCode.substring(rowBegin, position);
/* 563 */     String s2 = srcCode.substring(position, rowEnd + 1);
/* 564 */     s1 = expandTabs(s1, 8);
/* 565 */     int ln1 = s1.length();
/* 566 */     s2 = expandTabs(s2, 8, ln1);
/* 567 */     int ln2 = s2.length();
/* 568 */     if (ln1 + ln2 > MAX_QUOTATION_LENGTH) {
/* 569 */       int newLn2 = ln2 - ln1 + ln2 - MAX_QUOTATION_LENGTH;
/* 570 */       if (newLn2 < 6) {
/* 571 */         newLn2 = 6;
/*     */       }
/* 573 */       if (newLn2 < ln2) {
/* 574 */         s2 = s2.substring(0, newLn2 - 3) + "...";
/* 575 */         ln2 = newLn2;
/*     */       } 
/* 577 */       if (ln1 + ln2 > MAX_QUOTATION_LENGTH) {
/* 578 */         s1 = "..." + s1.substring(ln1 + ln2 - MAX_QUOTATION_LENGTH + 3);
/*     */       }
/*     */     } 
/* 581 */     StringBuilder res = new StringBuilder(message.length() + 80);
/* 582 */     res.append(message);
/* 583 */     res.append("\nError location: line ").append(row).append(", column ").append(col).append(":\n");
/* 584 */     res.append(s1).append(s2).append("\n");
/* 585 */     int x = s1.length();
/* 586 */     while (x != 0) {
/* 587 */       res.append(' ');
/* 588 */       x--;
/*     */     } 
/* 590 */     res.append('^');
/*     */     
/* 592 */     return res.toString();
/*     */   }
/*     */   
/*     */   private static String expandTabs(String s, int tabWidth) {
/* 596 */     return expandTabs(s, tabWidth, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String expandTabs(String s, int tabWidth, int startCol) {
/* 603 */     int e = s.indexOf('\t');
/* 604 */     if (e == -1) {
/* 605 */       return s;
/*     */     }
/* 607 */     int b = 0;
/* 608 */     StringBuilder buf = new StringBuilder(s.length() + Math.max(16, tabWidth * 2));
/*     */     while (true) {
/* 610 */       buf.append(s, b, e);
/* 611 */       int col = buf.length() + startCol;
/* 612 */       for (int i = tabWidth * (1 + col / tabWidth) - col; i > 0; i--) {
/* 613 */         buf.append(' ');
/*     */       }
/* 615 */       b = e + 1;
/* 616 */       e = s.indexOf('\t', b);
/* 617 */       if (e == -1) {
/* 618 */         buf.append(s, b, s.length());
/* 619 */         return buf.toString();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\JSONParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */