/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.text.ParseException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
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
/*     */ class ExtendedDecimalFormatParser
/*     */ {
/*     */   private static final String PARAM_ROUNDING_MODE = "roundingMode";
/*     */   private static final String PARAM_MULTIPIER = "multipier";
/*     */   private static final String PARAM_MULTIPLIER = "multiplier";
/*     */   private static final String PARAM_DECIMAL_SEPARATOR = "decimalSeparator";
/*     */   private static final String PARAM_MONETARY_DECIMAL_SEPARATOR = "monetaryDecimalSeparator";
/*     */   private static final String PARAM_GROUP_SEPARATOR = "groupingSeparator";
/*     */   private static final String PARAM_EXPONENT_SEPARATOR = "exponentSeparator";
/*     */   private static final String PARAM_MINUS_SIGN = "minusSign";
/*     */   private static final String PARAM_INFINITY = "infinity";
/*     */   private static final String PARAM_NAN = "nan";
/*     */   private static final String PARAM_PERCENT = "percent";
/*     */   private static final String PARAM_PER_MILL = "perMill";
/*     */   private static final String PARAM_ZERO_DIGIT = "zeroDigit";
/*     */   private static final String PARAM_CURRENCY_CODE = "currencyCode";
/*     */   private static final String PARAM_CURRENCY_SYMBOL = "currencySymbol";
/*     */   private static final String PARAM_VALUE_RND_UP = "up";
/*     */   private static final String PARAM_VALUE_RND_DOWN = "down";
/*     */   private static final String PARAM_VALUE_RND_CEILING = "ceiling";
/*     */   private static final String PARAM_VALUE_RND_FLOOR = "floor";
/*     */   private static final String PARAM_VALUE_RND_HALF_DOWN = "halfDown";
/*     */   private static final String PARAM_VALUE_RND_HALF_EVEN = "halfEven";
/*     */   private static final String PARAM_VALUE_RND_HALF_UP = "halfUp";
/*     */   private static final String PARAM_VALUE_RND_UNNECESSARY = "unnecessary";
/*     */   private static final HashMap<String, ? extends ParameterHandler> PARAM_HANDLERS;
/*     */   private static final String SNIP_MARK = "[...]";
/*     */   private static final int MAX_QUOTATION_LENGTH = 10;
/*     */   private final String src;
/*     */   
/*     */   static {
/*  62 */     HashMap<String, ParameterHandler> m = new HashMap<>();
/*  63 */     m.put("roundingMode", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/*     */             RoundingMode parsedValue;
/*  68 */             if (value.equals("up")) {
/*  69 */               parsedValue = RoundingMode.UP;
/*  70 */             } else if (value.equals("down")) {
/*  71 */               parsedValue = RoundingMode.DOWN;
/*  72 */             } else if (value.equals("ceiling")) {
/*  73 */               parsedValue = RoundingMode.CEILING;
/*  74 */             } else if (value.equals("floor")) {
/*  75 */               parsedValue = RoundingMode.FLOOR;
/*  76 */             } else if (value.equals("halfDown")) {
/*  77 */               parsedValue = RoundingMode.HALF_DOWN;
/*  78 */             } else if (value.equals("halfEven")) {
/*  79 */               parsedValue = RoundingMode.HALF_EVEN;
/*  80 */             } else if (value.equals("halfUp")) {
/*  81 */               parsedValue = RoundingMode.HALF_UP;
/*  82 */             } else if (value.equals("unnecessary")) {
/*  83 */               parsedValue = RoundingMode.UNNECESSARY;
/*     */             } else {
/*  85 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Should be one of: up, down, ceiling, floor, halfDown, halfEven, unnecessary");
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  91 */             parser.roundingMode = parsedValue;
/*     */           }
/*     */         });
/*  94 */     ParameterHandler multiplierParamHandler = new ParameterHandler()
/*     */       {
/*     */         public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */         {
/*     */           try {
/*  99 */             parser.multiplier = Integer.valueOf(value);
/* 100 */           } catch (NumberFormatException e) {
/* 101 */             throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Malformed integer.");
/*     */           } 
/*     */         }
/*     */       };
/* 105 */     m.put("multiplier", multiplierParamHandler);
/* 106 */     m.put("multipier", multiplierParamHandler);
/* 107 */     m.put("decimalSeparator", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 111 */             if (value.length() != 1) {
/* 112 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Must contain exactly 1 character.");
/*     */             }
/* 114 */             parser.symbols.setDecimalSeparator(value.charAt(0));
/*     */           }
/*     */         });
/* 117 */     m.put("monetaryDecimalSeparator", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 121 */             if (value.length() != 1) {
/* 122 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Must contain exactly 1 character.");
/*     */             }
/* 124 */             parser.symbols.setMonetaryDecimalSeparator(value.charAt(0));
/*     */           }
/*     */         });
/* 127 */     m.put("groupingSeparator", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 131 */             if (value.length() != 1) {
/* 132 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Must contain exactly 1 character.");
/*     */             }
/* 134 */             parser.symbols.setGroupingSeparator(value.charAt(0));
/*     */           }
/*     */         });
/* 137 */     m.put("exponentSeparator", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 141 */             parser.symbols.setExponentSeparator(value);
/*     */           }
/*     */         });
/* 144 */     m.put("minusSign", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 148 */             if (value.length() != 1) {
/* 149 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Must contain exactly 1 character.");
/*     */             }
/* 151 */             parser.symbols.setMinusSign(value.charAt(0));
/*     */           }
/*     */         });
/* 154 */     m.put("infinity", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 158 */             parser.symbols.setInfinity(value);
/*     */           }
/*     */         });
/* 161 */     m.put("nan", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 165 */             parser.symbols.setNaN(value);
/*     */           }
/*     */         });
/* 168 */     m.put("percent", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 172 */             if (value.length() != 1) {
/* 173 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Must contain exactly 1 character.");
/*     */             }
/* 175 */             parser.symbols.setPercent(value.charAt(0));
/*     */           }
/*     */         });
/* 178 */     m.put("perMill", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 182 */             if (value.length() != 1) {
/* 183 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Must contain exactly 1 character.");
/*     */             }
/* 185 */             parser.symbols.setPerMill(value.charAt(0));
/*     */           }
/*     */         });
/* 188 */     m.put("zeroDigit", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/* 192 */             if (value.length() != 1) {
/* 193 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Must contain exactly 1 character.");
/*     */             }
/* 195 */             parser.symbols.setZeroDigit(value.charAt(0));
/*     */           }
/*     */         });
/* 198 */     m.put("currencyCode", new ParameterHandler()
/*     */         {
/*     */           public void handle(ExtendedDecimalFormatParser parser, String value) throws ExtendedDecimalFormatParser.InvalidParameterValueException
/*     */           {
/*     */             Currency currency;
/*     */             try {
/* 204 */               currency = Currency.getInstance(value);
/* 205 */             } catch (IllegalArgumentException e) {
/* 206 */               throw new ExtendedDecimalFormatParser.InvalidParameterValueException("Not a known ISO 4217 code.");
/*     */             } 
/* 208 */             parser.symbols.setCurrency(currency);
/*     */           }
/*     */         });
/* 211 */     PARAM_HANDLERS = m;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 218 */   private int pos = 0;
/*     */   
/*     */   private final DecimalFormatSymbols symbols;
/*     */   private RoundingMode roundingMode;
/*     */   private Integer multiplier;
/*     */   
/*     */   static DecimalFormat parse(String formatString, Locale locale) throws ParseException {
/* 225 */     return (new ExtendedDecimalFormatParser(formatString, locale)).parse();
/*     */   }
/*     */   private DecimalFormat parse() throws ParseException {
/*     */     DecimalFormat decimalFormat;
/* 229 */     String stdPattern = fetchStandardPattern();
/* 230 */     skipWS();
/* 231 */     parseFormatStringExtension();
/*     */ 
/*     */     
/*     */     try {
/* 235 */       decimalFormat = new DecimalFormat(stdPattern, this.symbols);
/* 236 */     } catch (IllegalArgumentException e) {
/* 237 */       ParseException pe = new ParseException(e.getMessage(), 0);
/* 238 */       if (e.getCause() != null) {
/*     */         try {
/* 240 */           e.initCause(e.getCause());
/* 241 */         } catch (Exception exception) {}
/*     */       }
/*     */ 
/*     */       
/* 245 */       throw pe;
/*     */     } 
/*     */     
/* 248 */     if (this.roundingMode != null) {
/* 249 */       decimalFormat.setRoundingMode(this.roundingMode);
/*     */     }
/*     */     
/* 252 */     if (this.multiplier != null) {
/* 253 */       decimalFormat.setMultiplier(this.multiplier.intValue());
/*     */     }
/*     */     
/* 256 */     return decimalFormat;
/*     */   }
/*     */   
/*     */   private void parseFormatStringExtension() throws ParseException {
/* 260 */     int ln = this.src.length();
/*     */     
/* 262 */     if (this.pos == ln) {
/*     */       return;
/*     */     }
/*     */     
/* 266 */     String currencySymbol = null;
/*     */     while (true) {
/* 268 */       int namePos = this.pos;
/* 269 */       String name = fetchName();
/* 270 */       if (name == null) {
/* 271 */         throw newExpectedSgParseException("name");
/*     */       }
/*     */       
/* 274 */       skipWS();
/*     */       
/* 276 */       if (!fetchChar('=')) {
/* 277 */         throw newExpectedSgParseException("\"=\"");
/*     */       }
/*     */       
/* 280 */       skipWS();
/*     */       
/* 282 */       int valuePos = this.pos;
/* 283 */       String value = fetchValue();
/* 284 */       if (value == null) {
/* 285 */         throw newExpectedSgParseException("value");
/*     */       }
/* 287 */       int paramEndPos = this.pos;
/*     */       
/* 289 */       ParameterHandler handler = PARAM_HANDLERS.get(name);
/* 290 */       if (handler == null) {
/* 291 */         if (name.equals("currencySymbol")) {
/* 292 */           currencySymbol = value;
/*     */         } else {
/* 294 */           throw newUnknownParameterException(name, namePos);
/*     */         } 
/*     */       } else {
/*     */         try {
/* 298 */           handler.handle(this, value);
/* 299 */         } catch (InvalidParameterValueException e) {
/* 300 */           throw newInvalidParameterValueException(name, value, valuePos, e);
/*     */         } 
/*     */       } 
/*     */       
/* 304 */       skipWS();
/*     */ 
/*     */       
/* 307 */       if (fetchChar(',')) {
/* 308 */         skipWS(); continue;
/*     */       } 
/* 310 */       if (this.pos == ln) {
/*     */         break;
/*     */       }
/* 313 */       if (this.pos == paramEndPos) {
/* 314 */         throw newExpectedSgParseException("parameter separator whitespace or comma");
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 320 */     if (currencySymbol != null) {
/* 321 */       this.symbols.setCurrencySymbol(currencySymbol);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private ParseException newInvalidParameterValueException(String name, String value, int valuePos, InvalidParameterValueException e) {
/* 327 */     return new ParseException(
/* 328 */         StringUtil.jQuote(value) + " is an invalid value for the \"" + name + "\" parameter: " + e
/* 329 */         .message, valuePos);
/*     */   }
/*     */ 
/*     */   
/*     */   private ParseException newUnknownParameterException(String name, int namePos) throws ParseException {
/* 334 */     StringBuilder sb = new StringBuilder(128);
/* 335 */     sb.append("Unsupported parameter name, ").append(StringUtil.jQuote(name));
/* 336 */     sb.append(". The supported names are: ");
/* 337 */     Set<String> legalNames = PARAM_HANDLERS.keySet();
/* 338 */     String[] legalNameArr = legalNames.<String>toArray(new String[legalNames.size()]);
/* 339 */     Arrays.sort((Object[])legalNameArr);
/* 340 */     for (int i = 0; i < legalNameArr.length; i++) {
/* 341 */       if (i != 0) {
/* 342 */         sb.append(", ");
/*     */       }
/* 344 */       sb.append(legalNameArr[i]);
/*     */     } 
/* 346 */     return new ParseException(sb.toString(), namePos);
/*     */   }
/*     */   
/*     */   private void skipWS() {
/* 350 */     int ln = this.src.length();
/* 351 */     while (this.pos < ln && isWS(this.src.charAt(this.pos))) {
/* 352 */       this.pos++;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean fetchChar(char fetchedChar) {
/* 357 */     if (this.pos < this.src.length() && this.src.charAt(this.pos) == fetchedChar) {
/* 358 */       this.pos++;
/* 359 */       return true;
/*     */     } 
/* 361 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isWS(char c) {
/* 366 */     return (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == ' ');
/*     */   }
/*     */   
/*     */   private String fetchName() throws ParseException {
/* 370 */     int ln = this.src.length();
/* 371 */     int startPos = this.pos;
/* 372 */     boolean firstChar = true;
/* 373 */     while (this.pos < ln) {
/* 374 */       char c = this.src.charAt(this.pos);
/* 375 */       if (firstChar) {
/* 376 */         if (!Character.isJavaIdentifierStart(c)) {
/*     */           break;
/*     */         }
/* 379 */         firstChar = false;
/* 380 */       } else if (!Character.isJavaIdentifierPart(c)) {
/*     */         break;
/*     */       } 
/* 383 */       this.pos++;
/*     */     } 
/* 385 */     return !firstChar ? this.src.substring(startPos, this.pos) : null;
/*     */   }
/*     */   
/*     */   private String fetchValue() throws ParseException {
/* 389 */     int ln = this.src.length();
/* 390 */     int startPos = this.pos;
/* 391 */     char openedQuot = Character.MIN_VALUE;
/* 392 */     boolean needsUnescaping = false;
/* 393 */     while (this.pos < ln) {
/* 394 */       char c = this.src.charAt(this.pos);
/* 395 */       if (c == '\'' || c == '"') {
/* 396 */         if (openedQuot == '\000') {
/* 397 */           if (startPos != this.pos) {
/* 398 */             throw new ParseException("The " + c + " character can only be used for quoting values, but it was in the middle of an non-quoted value.", this.pos);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 403 */           openedQuot = c;
/* 404 */         } else if (c == openedQuot) {
/* 405 */           if (this.pos + 1 < ln && this.src.charAt(this.pos + 1) == openedQuot) {
/* 406 */             this.pos++;
/* 407 */             needsUnescaping = true;
/*     */           } else {
/* 409 */             String str = this.src.substring(startPos + 1, this.pos);
/* 410 */             this.pos++;
/* 411 */             return needsUnescaping ? unescape(str, openedQuot) : str;
/*     */           }
/*     */         
/*     */         } 
/* 415 */       } else if (openedQuot == '\000' && !Character.isJavaIdentifierPart(c)) {
/*     */         break;
/*     */       } 
/*     */       
/* 419 */       this.pos++;
/*     */     } 
/* 421 */     if (openedQuot != '\000') {
/* 422 */       throw new ParseException("The " + openedQuot + " quotation wasn't closed when the end of the source was reached.", this.pos);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 427 */     return (startPos == this.pos) ? null : this.src.substring(startPos, this.pos);
/*     */   }
/*     */   
/*     */   private String unescape(String s, char openedQuot) {
/* 431 */     return (openedQuot == '\'') ? StringUtil.replace(s, "''", "'") : StringUtil.replace(s, "\"\"", "\"");
/*     */   }
/*     */   private String fetchStandardPattern() {
/*     */     String stdFormatStr;
/* 435 */     int pos = this.pos;
/* 436 */     int ln = this.src.length();
/* 437 */     int semicolonCnt = 0;
/* 438 */     boolean quotedMode = false;
/* 439 */     while (pos < ln) {
/* 440 */       char c = this.src.charAt(pos);
/* 441 */       if (c == ';' && !quotedMode) {
/* 442 */         semicolonCnt++;
/* 443 */         if (semicolonCnt == 2) {
/*     */           break;
/*     */         }
/* 446 */       } else if (c == '\'') {
/* 447 */         if (quotedMode) {
/* 448 */           if (pos + 1 < ln && this.src.charAt(pos + 1) == '\'') {
/*     */             
/* 450 */             pos++;
/*     */           } else {
/* 452 */             quotedMode = false;
/*     */           } 
/*     */         } else {
/* 455 */           quotedMode = true;
/*     */         } 
/*     */       } 
/* 458 */       pos++;
/*     */     } 
/*     */ 
/*     */     
/* 462 */     if (semicolonCnt < 2) {
/*     */       
/* 464 */       stdFormatStr = this.src;
/*     */     } else {
/* 466 */       int stdEndPos = pos;
/* 467 */       if (this.src.charAt(pos - 1) == ';')
/*     */       {
/* 469 */         stdEndPos--;
/*     */       }
/* 471 */       stdFormatStr = this.src.substring(0, stdEndPos);
/*     */     } 
/*     */     
/* 474 */     if (pos < ln) {
/* 475 */       pos++;
/*     */     }
/* 477 */     this.pos = pos;
/*     */     
/* 479 */     return stdFormatStr;
/*     */   }
/*     */   
/*     */   private ExtendedDecimalFormatParser(String formatString, Locale locale) {
/* 483 */     this.src = formatString;
/* 484 */     this.symbols = DecimalFormatSymbols.getInstance(locale);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ParseException newExpectedSgParseException(String expectedThing) {
/*     */     String quotation;
/* 491 */     int i = this.src.length() - 1;
/* 492 */     while (i >= 0 && Character.isWhitespace(this.src.charAt(i))) {
/* 493 */       i--;
/*     */     }
/* 495 */     int ln = i + 1;
/*     */     
/* 497 */     if (this.pos < ln) {
/* 498 */       int qEndPos = this.pos + 10;
/* 499 */       if (qEndPos >= ln) {
/* 500 */         quotation = this.src.substring(this.pos, ln);
/*     */       } else {
/* 502 */         quotation = this.src.substring(this.pos, qEndPos - "[...]".length()) + "[...]";
/*     */       } 
/*     */     } else {
/* 505 */       quotation = null;
/*     */     } 
/*     */     
/* 508 */     return new ParseException("Expected a(n) " + expectedThing + " at position " + this.pos + " (0-based), but " + ((quotation == null) ? "reached the end of the input." : ("found: " + quotation)), this.pos);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static interface ParameterHandler
/*     */   {
/*     */     void handle(ExtendedDecimalFormatParser param1ExtendedDecimalFormatParser, String param1String) throws ExtendedDecimalFormatParser.InvalidParameterValueException;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class InvalidParameterValueException
/*     */     extends Exception
/*     */   {
/*     */     private final String message;
/*     */ 
/*     */     
/*     */     public InvalidParameterValueException(String message) {
/* 526 */       this.message = message;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ExtendedDecimalFormatParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */