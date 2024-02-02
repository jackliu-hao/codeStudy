/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.AssertionFailedException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonParser
/*     */ {
/*     */   enum Whitespace
/*     */   {
/*  46 */     TAB('\t'), LF('\n'), CR('\r'), SPACE(' ');
/*     */     
/*     */     public final char CHAR;
/*     */     
/*     */     Whitespace(char character) {
/*  51 */       this.CHAR = character;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   enum StructuralToken
/*     */   {
/*  59 */     LSQBRACKET('['),
/*     */ 
/*     */ 
/*     */     
/*  63 */     RSQBRACKET(']'),
/*     */ 
/*     */ 
/*     */     
/*  67 */     LCRBRACKET('{'),
/*     */ 
/*     */ 
/*     */     
/*  71 */     RCRBRACKET('}'),
/*     */ 
/*     */ 
/*     */     
/*  75 */     COLON(':'),
/*     */ 
/*     */ 
/*     */     
/*  79 */     COMMA(',');
/*     */     
/*     */     public final char CHAR;
/*     */     
/*     */     StructuralToken(char character) {
/*  84 */       this.CHAR = character;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   enum EscapeChar
/*     */   {
/*  93 */     QUOTE('"', "\\\""),
/*     */ 
/*     */ 
/*     */     
/*  97 */     RSOLIDUS('\\', "\\\\"),
/*     */ 
/*     */ 
/*     */     
/* 101 */     SOLIDUS('/', "\\/"),
/*     */ 
/*     */ 
/*     */     
/* 105 */     BACKSPACE('\b', "\\b"),
/*     */ 
/*     */ 
/*     */     
/* 109 */     FF('\f', "\\f"),
/*     */ 
/*     */ 
/*     */     
/* 113 */     LF('\n', "\\n"),
/*     */ 
/*     */ 
/*     */     
/* 117 */     CR('\r', "\\r"),
/*     */ 
/*     */ 
/*     */     
/* 121 */     TAB('\t', "\\t");
/*     */     
/*     */     public final char CHAR;
/*     */     public final String ESCAPED;
/*     */     
/*     */     EscapeChar(char character, String escaped) {
/* 127 */       this.CHAR = character;
/* 128 */       this.ESCAPED = escaped;
/*     */     }
/*     */   }
/*     */   
/* 132 */   static Set<Character> whitespaceChars = new HashSet<>();
/* 133 */   static HashMap<Character, Character> unescapeChars = new HashMap<>();
/*     */   
/*     */   static {
/* 136 */     for (EscapeChar ec : EscapeChar.values()) {
/* 137 */       unescapeChars.put(Character.valueOf(ec.ESCAPED.charAt(1)), Character.valueOf(ec.CHAR));
/*     */     }
/* 139 */     for (Whitespace ws : Whitespace.values()) {
/* 140 */       whitespaceChars.add(Character.valueOf(ws.CHAR));
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isValidEndOfValue(char ch) {
/* 145 */     return (StructuralToken.COMMA.CHAR == ch || StructuralToken.RCRBRACKET.CHAR == ch || StructuralToken.RSQBRACKET.CHAR == ch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbDoc parseDoc(String jsonString) {
/*     */     try {
/* 157 */       return parseDoc(new StringReader(jsonString));
/* 158 */     } catch (IOException ex) {
/* 159 */       throw AssertionFailedException.shouldNotHappen(ex);
/*     */     } 
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
/*     */   public static DbDoc parseDoc(StringReader reader) throws IOException {
/* 175 */     DbDoc doc = new DbDocImpl();
/*     */ 
/*     */     
/* 178 */     int leftBrackets = 0;
/* 179 */     int rightBrackets = 0;
/*     */     
/*     */     int intch;
/* 182 */     while ((intch = reader.read()) != -1) {
/* 183 */       String key = null;
/* 184 */       char ch = (char)intch;
/* 185 */       if (ch == StructuralToken.LCRBRACKET.CHAR || ch == StructuralToken.COMMA.CHAR) {
/* 186 */         if (ch == StructuralToken.LCRBRACKET.CHAR) {
/* 187 */           leftBrackets++;
/*     */         }
/* 189 */         if ((key = nextKey(reader)) != null) {
/*     */           try {
/* 191 */             JsonValue val; if ((val = nextValue(reader)) != null) {
/* 192 */               doc.put(key, val); continue;
/*     */             } 
/* 194 */             reader.reset();
/*     */           }
/* 196 */           catch (WrongArgumentException ex) {
/* 197 */             throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.0", new String[] { key }), ex);
/*     */           }  continue;
/*     */         } 
/* 200 */         reader.reset(); continue;
/*     */       } 
/* 202 */       if (ch == StructuralToken.RCRBRACKET.CHAR) {
/* 203 */         rightBrackets++;
/*     */         break;
/*     */       } 
/* 206 */       if (!whitespaceChars.contains(Character.valueOf(ch))) {
/* 207 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 212 */     if (leftBrackets == 0)
/* 213 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.2")); 
/* 214 */     if (leftBrackets > rightBrackets) {
/* 215 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 216 */           Messages.getString("JsonParser.3", new Character[] { Character.valueOf(StructuralToken.RCRBRACKET.CHAR) }));
/*     */     }
/*     */     
/* 219 */     return doc;
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
/*     */   public static JsonArray parseArray(StringReader reader) throws IOException {
/* 234 */     JsonArray arr = new JsonArray();
/*     */     
/* 236 */     int openings = 0;
/*     */     
/*     */     int intch;
/* 239 */     while ((intch = reader.read()) != -1) {
/* 240 */       char ch = (char)intch;
/* 241 */       if (ch == StructuralToken.LSQBRACKET.CHAR || ch == StructuralToken.COMMA.CHAR) {
/* 242 */         if (ch == StructuralToken.LSQBRACKET.CHAR)
/* 243 */           openings++; 
/*     */         JsonValue val;
/* 245 */         if ((val = nextValue(reader)) != null) {
/* 246 */           arr.add(val); continue;
/*     */         } 
/* 248 */         reader.reset();
/*     */         continue;
/*     */       } 
/* 251 */       if (ch == StructuralToken.RSQBRACKET.CHAR) {
/* 252 */         openings--;
/*     */         break;
/*     */       } 
/* 255 */       if (!whitespaceChars.contains(Character.valueOf(ch))) {
/* 256 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 261 */     if (openings > 0) {
/* 262 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 263 */           Messages.getString("JsonParser.3", new Character[] { Character.valueOf(StructuralToken.RSQBRACKET.CHAR) }));
/*     */     }
/*     */     
/* 266 */     return arr;
/*     */   }
/*     */   
/*     */   private static String nextKey(StringReader reader) throws IOException {
/* 270 */     reader.mark(1);
/*     */     
/* 272 */     JsonString val = parseString(reader);
/* 273 */     if (val == null) {
/* 274 */       reader.reset();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 279 */     char ch = ' '; int intch;
/* 280 */     while ((intch = reader.read()) != -1) {
/* 281 */       ch = (char)intch;
/* 282 */       if (ch == StructuralToken.COLON.CHAR) {
/*     */         break;
/*     */       }
/* 285 */       if (ch == StructuralToken.RCRBRACKET.CHAR) {
/*     */         break;
/*     */       }
/* 288 */       if (!whitespaceChars.contains(Character.valueOf(ch))) {
/* 289 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
/*     */       }
/*     */     } 
/*     */     
/* 293 */     if (ch != StructuralToken.COLON.CHAR && val != null && val.getString().length() > 0) {
/* 294 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.4", new String[] { val.getString() }));
/*     */     }
/* 296 */     return (val != null) ? val.getString() : null;
/*     */   }
/*     */   
/*     */   private static JsonValue nextValue(StringReader reader) throws IOException {
/* 300 */     reader.mark(1);
/*     */     int intch;
/* 302 */     while ((intch = reader.read()) != -1) {
/* 303 */       char ch = (char)intch;
/* 304 */       if (ch == EscapeChar.QUOTE.CHAR) {
/*     */         
/* 306 */         reader.reset();
/* 307 */         return parseString(reader);
/*     */       } 
/* 309 */       if (ch == StructuralToken.LSQBRACKET.CHAR) {
/*     */         
/* 311 */         reader.reset();
/* 312 */         return parseArray(reader);
/*     */       } 
/* 314 */       if (ch == StructuralToken.LCRBRACKET.CHAR) {
/*     */         
/* 316 */         reader.reset();
/* 317 */         return parseDoc(reader);
/*     */       } 
/* 319 */       if (ch == '-' || (ch >= '0' && ch <= '9')) {
/*     */         
/* 321 */         reader.reset();
/* 322 */         return parseNumber(reader);
/*     */       } 
/* 324 */       if (ch == JsonLiteral.TRUE.value.charAt(0)) {
/*     */         
/* 326 */         reader.reset();
/* 327 */         return parseLiteral(reader);
/*     */       } 
/* 329 */       if (ch == JsonLiteral.FALSE.value.charAt(0)) {
/*     */         
/* 331 */         reader.reset();
/* 332 */         return parseLiteral(reader);
/*     */       } 
/* 334 */       if (ch == JsonLiteral.NULL.value.charAt(0)) {
/*     */         
/* 336 */         reader.reset();
/* 337 */         return parseLiteral(reader);
/*     */       } 
/* 339 */       if (ch == StructuralToken.RSQBRACKET.CHAR)
/*     */       {
/* 341 */         return null;
/*     */       }
/* 343 */       if (!whitespaceChars.contains(Character.valueOf(ch))) {
/* 344 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
/*     */       }
/* 346 */       reader.mark(1);
/*     */     } 
/*     */     
/* 349 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5"));
/*     */   }
/*     */   
/*     */   private static void appendChar(StringBuilder sb, char ch) {
/* 353 */     if (sb == null) {
/* 354 */       if (!whitespaceChars.contains(Character.valueOf(ch))) {
/* 355 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.6", new Character[] { Character.valueOf(ch) }));
/*     */       }
/*     */     } else {
/* 358 */       sb.append(ch);
/*     */     } 
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
/*     */   static JsonString parseString(StringReader reader) throws IOException {
/* 373 */     int quotes = 0;
/* 374 */     boolean escapeNextChar = false;
/*     */     
/* 376 */     StringBuilder sb = null;
/*     */     
/*     */     int intch;
/* 379 */     while ((intch = reader.read()) != -1) {
/* 380 */       char ch = (char)intch;
/* 381 */       if (escapeNextChar) {
/* 382 */         if (unescapeChars.containsKey(Character.valueOf(ch))) {
/* 383 */           appendChar(sb, ((Character)unescapeChars.get(Character.valueOf(ch))).charValue());
/*     */         } else {
/* 385 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.7", new Character[] { Character.valueOf(ch) }));
/*     */         } 
/* 387 */         escapeNextChar = false; continue;
/*     */       } 
/* 389 */       if (ch == EscapeChar.QUOTE.CHAR) {
/* 390 */         if (sb == null) {
/*     */           
/* 392 */           sb = new StringBuilder();
/* 393 */           quotes++;
/*     */           continue;
/*     */         } 
/* 396 */         quotes--;
/*     */         
/*     */         break;
/*     */       } 
/* 400 */       if (quotes == 0 && ch == StructuralToken.RCRBRACKET.CHAR) {
/*     */         break;
/*     */       }
/*     */       
/* 404 */       if (ch == EscapeChar.RSOLIDUS.CHAR) {
/* 405 */         escapeNextChar = true;
/*     */         continue;
/*     */       } 
/* 408 */       appendChar(sb, ch);
/*     */     } 
/*     */ 
/*     */     
/* 412 */     if (quotes > 0) {
/* 413 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.3", new Character[] { Character.valueOf(EscapeChar.QUOTE.CHAR) }));
/*     */     }
/*     */     
/* 416 */     return (sb == null) ? null : (new JsonString()).setValue(sb.toString());
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
/*     */   static JsonNumber parseNumber(StringReader reader) throws IOException {
/* 431 */     StringBuilder sb = null;
/* 432 */     char lastChar = ' ';
/* 433 */     boolean hasFractionalPart = false;
/* 434 */     boolean hasExponent = false;
/*     */     
/*     */     int intch;
/* 437 */     while ((intch = reader.read()) != -1) {
/* 438 */       char ch = (char)intch;
/*     */       
/* 440 */       if (sb == null) {
/*     */         
/* 442 */         if (ch == '-') {
/*     */           
/* 444 */           sb = new StringBuilder();
/* 445 */           sb.append(ch);
/* 446 */         } else if (ch >= '0' && ch <= '9') {
/*     */           
/* 448 */           sb = new StringBuilder();
/* 449 */           sb.append(ch);
/* 450 */         } else if (!whitespaceChars.contains(Character.valueOf(ch))) {
/*     */           
/* 452 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
/*     */         } 
/* 454 */       } else if (ch == '-') {
/*     */         
/* 456 */         if (lastChar == 'E' || lastChar == 'e') {
/* 457 */           sb.append(ch);
/*     */         } else {
/* 459 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 460 */               Messages.getString("JsonParser.8", new Object[] { Character.valueOf(ch), sb.toString() }));
/*     */         }
/*     */       
/* 463 */       } else if (ch >= '0' && ch <= '9') {
/* 464 */         sb.append(ch);
/*     */       }
/* 466 */       else if (ch == 'E' || ch == 'e') {
/*     */         
/* 468 */         if (lastChar >= '0' && lastChar <= '9') {
/* 469 */           hasExponent = true;
/* 470 */           sb.append(ch);
/*     */         } else {
/* 472 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 473 */               Messages.getString("JsonParser.8", new Object[] { Character.valueOf(ch), sb.toString() }));
/*     */         }
/*     */       
/* 476 */       } else if (ch == '.') {
/*     */         
/* 478 */         if (hasFractionalPart) {
/* 479 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 480 */               Messages.getString("JsonParser.10", new Object[] { Character.valueOf(ch), sb.toString() }));
/*     */         }
/* 482 */         if (hasExponent) {
/* 483 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.11"));
/*     */         }
/* 485 */         if (lastChar >= '0' && lastChar <= '9') {
/* 486 */           hasFractionalPart = true;
/* 487 */           sb.append(ch);
/*     */         } else {
/* 489 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 490 */               Messages.getString("JsonParser.8", new Object[] { Character.valueOf(ch), sb.toString() }));
/*     */         }
/*     */       
/* 493 */       } else if (ch == '+') {
/*     */         
/* 495 */         if (lastChar == 'E' || lastChar == 'e') {
/* 496 */           sb.append(ch);
/*     */         } else {
/* 498 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 499 */               Messages.getString("JsonParser.8", new Object[] { Character.valueOf(ch), sb.toString() }));
/*     */         } 
/*     */       } else {
/* 502 */         if (whitespaceChars.contains(Character.valueOf(ch)) || isValidEndOfValue(ch)) {
/*     */           
/* 504 */           reader.reset();
/*     */           
/*     */           break;
/*     */         } 
/*     */         
/* 509 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
/*     */       } 
/* 511 */       lastChar = ch;
/*     */       
/* 513 */       reader.mark(1);
/*     */     } 
/*     */     
/* 516 */     if (sb == null || sb.length() == 0) {
/* 517 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5"));
/*     */     }
/*     */     
/* 520 */     return (new JsonNumber()).setValue(sb.toString());
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
/*     */   static JsonLiteral parseLiteral(StringReader reader) throws IOException {
/* 534 */     StringBuilder sb = null;
/* 535 */     JsonLiteral res = null;
/* 536 */     int literalIndex = 0;
/*     */     
/*     */     int intch;
/* 539 */     while ((intch = reader.read()) != -1) {
/* 540 */       char ch = (char)intch;
/* 541 */       if (sb == null) {
/*     */         
/* 543 */         if (ch == JsonLiteral.TRUE.value.charAt(0)) {
/*     */           
/* 545 */           res = JsonLiteral.TRUE;
/* 546 */           sb = new StringBuilder();
/* 547 */           sb.append(ch);
/* 548 */           literalIndex++;
/* 549 */         } else if (ch == JsonLiteral.FALSE.value.charAt(0)) {
/*     */           
/* 551 */           res = JsonLiteral.FALSE;
/* 552 */           sb = new StringBuilder();
/* 553 */           sb.append(ch);
/* 554 */           literalIndex++;
/* 555 */         } else if (ch == JsonLiteral.NULL.value.charAt(0)) {
/*     */           
/* 557 */           res = JsonLiteral.NULL;
/* 558 */           sb = new StringBuilder();
/* 559 */           sb.append(ch);
/* 560 */           literalIndex++;
/* 561 */         } else if (!whitespaceChars.contains(Character.valueOf(ch))) {
/*     */           
/* 563 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
/*     */         } 
/* 565 */       } else if (literalIndex < res.value.length() && ch == res.value.charAt(literalIndex)) {
/* 566 */         sb.append(ch);
/* 567 */         literalIndex++;
/*     */       } else {
/* 569 */         if (whitespaceChars.contains(Character.valueOf(ch)) || isValidEndOfValue(ch)) {
/*     */           
/* 571 */           reader.reset();
/*     */           
/*     */           break;
/*     */         } 
/*     */         
/* 576 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[] { Character.valueOf(ch) }));
/*     */       } 
/*     */       
/* 579 */       reader.mark(1);
/*     */     } 
/*     */     
/* 582 */     if (sb == null) {
/* 583 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5"));
/*     */     }
/*     */     
/* 586 */     if (literalIndex == res.value.length()) {
/* 587 */       return res;
/*     */     }
/*     */     
/* 590 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.12", new String[] { sb.toString() }));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\JsonParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */