/*     */ package org.h2.expression.condition;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.index.IndexCondition;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ public final class CompareLike extends Condition {
/*     */   private static final int MATCH = 0;
/*     */   private static final int ONE = 1;
/*     */   private static final int ANY = 2;
/*     */   private final CompareMode compareMode;
/*     */   private final String defaultEscape;
/*     */   private final LikeType likeType;
/*     */   private Expression left;
/*     */   private final boolean not;
/*     */   private final boolean whenOperand;
/*     */   private Expression right;
/*     */   private Expression escape;
/*     */   private boolean isInit;
/*     */   private char[] patternChars;
/*     */   private String patternString;
/*     */   private int[] patternTypes;
/*     */   private int patternLength;
/*     */   private Pattern patternRegexp;
/*     */   private boolean ignoreCase;
/*     */   private boolean fastCompare;
/*     */   private boolean invalidPattern;
/*     */   private boolean shortcutToStartsWith;
/*     */   private boolean shortcutToEndsWith;
/*     */   private boolean shortcutToContains;
/*     */   
/*     */   public enum LikeType {
/*  44 */     LIKE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  49 */     ILIKE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     REGEXP;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompareLike(Database paramDatabase, Expression paramExpression1, boolean paramBoolean1, boolean paramBoolean2, Expression paramExpression2, Expression paramExpression3, LikeType paramLikeType) {
/*  94 */     this(paramDatabase.getCompareMode(), (paramDatabase.getSettings()).defaultEscape, paramExpression1, paramBoolean1, paramBoolean2, paramExpression2, paramExpression3, paramLikeType);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompareLike(CompareMode paramCompareMode, String paramString, Expression paramExpression1, boolean paramBoolean1, boolean paramBoolean2, Expression paramExpression2, Expression paramExpression3, LikeType paramLikeType) {
/*  99 */     this.compareMode = paramCompareMode;
/* 100 */     this.defaultEscape = paramString;
/* 101 */     this.likeType = paramLikeType;
/* 102 */     this.left = paramExpression1;
/* 103 */     this.not = paramBoolean1;
/* 104 */     this.whenOperand = paramBoolean2;
/* 105 */     this.right = paramExpression2;
/* 106 */     this.escape = paramExpression3;
/*     */   }
/*     */   
/*     */   private static Character getEscapeChar(String paramString) {
/* 110 */     return (paramString == null || paramString.isEmpty()) ? null : Character.valueOf(paramString.charAt(0));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/* 115 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 120 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 125 */     if (this.not) {
/* 126 */       paramStringBuilder.append(" NOT");
/*     */     }
/* 128 */     switch (this.likeType) {
/*     */       case LIKE:
/*     */       case ILIKE:
/* 131 */         paramStringBuilder.append((this.likeType == LikeType.LIKE) ? " LIKE " : " ILIKE ");
/* 132 */         this.right.getSQL(paramStringBuilder, paramInt, 0);
/* 133 */         if (this.escape != null) {
/* 134 */           this.escape.getSQL(paramStringBuilder.append(" ESCAPE "), paramInt, 0);
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 144 */         return paramStringBuilder;case REGEXP: paramStringBuilder.append(" REGEXP "); this.right.getSQL(paramStringBuilder, paramInt, 0); return paramStringBuilder;
/*     */     } 
/*     */     throw DbException.getUnsupportedException(this.likeType.name());
/*     */   }
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 149 */     this.left = this.left.optimize(paramSessionLocal);
/* 150 */     this.right = this.right.optimize(paramSessionLocal);
/* 151 */     if (this.likeType == LikeType.ILIKE || this.left.getType().getValueType() == 4) {
/* 152 */       this.ignoreCase = true;
/*     */     }
/* 154 */     if (this.escape != null) {
/* 155 */       this.escape = this.escape.optimize(paramSessionLocal);
/*     */     }
/* 157 */     if (this.whenOperand) {
/* 158 */       return this;
/*     */     }
/* 160 */     if (this.left.isValueSet()) {
/* 161 */       Value value = this.left.getValue(paramSessionLocal);
/* 162 */       if (value == ValueNull.INSTANCE)
/*     */       {
/* 164 */         return (Expression)TypedValueExpression.UNKNOWN;
/*     */       }
/*     */     } 
/* 167 */     if (this.right.isValueSet() && (this.escape == null || this.escape.isValueSet())) {
/* 168 */       if (this.left.isValueSet()) {
/* 169 */         return (Expression)ValueExpression.getBoolean(getValue(paramSessionLocal));
/*     */       }
/* 171 */       Value value1 = this.right.getValue(paramSessionLocal);
/* 172 */       if (value1 == ValueNull.INSTANCE)
/*     */       {
/* 174 */         return (Expression)TypedValueExpression.UNKNOWN;
/*     */       }
/* 176 */       Value value2 = (this.escape == null) ? null : this.escape.getValue(paramSessionLocal);
/* 177 */       if (value2 == ValueNull.INSTANCE) {
/* 178 */         return (Expression)TypedValueExpression.UNKNOWN;
/*     */       }
/* 180 */       String str = value1.getString();
/* 181 */       initPattern(str, getEscapeChar(value2));
/* 182 */       if (this.invalidPattern) {
/* 183 */         return (Expression)TypedValueExpression.UNKNOWN;
/*     */       }
/* 185 */       if (this.likeType != LikeType.REGEXP && "%".equals(str))
/*     */       {
/* 187 */         return (new SearchedCase(new Expression[] { new NullPredicate(this.left, true, false), 
/* 188 */               (Expression)ValueExpression.getBoolean(!this.not), (Expression)TypedValueExpression.UNKNOWN })).optimize(paramSessionLocal);
/*     */       }
/* 190 */       if (isFullMatch()) {
/*     */         
/* 192 */         Value value = (Value)(this.ignoreCase ? ValueVarcharIgnoreCase.get(this.patternString) : ValueVarchar.get(this.patternString));
/* 193 */         ValueExpression valueExpression = ValueExpression.get(value);
/* 194 */         return (new Comparison(this.not ? 1 : 0, this.left, (Expression)valueExpression, false))
/* 195 */           .optimize(paramSessionLocal);
/*     */       } 
/* 197 */       this.isInit = true;
/*     */     } 
/* 199 */     return this;
/*     */   }
/*     */   private Character getEscapeChar(Value paramValue) {
/*     */     Character character;
/* 203 */     if (paramValue == null) {
/* 204 */       return getEscapeChar(this.defaultEscape);
/*     */     }
/* 206 */     String str = paramValue.getString();
/*     */     
/* 208 */     if (str == null)
/* 209 */     { character = getEscapeChar(this.defaultEscape); }
/* 210 */     else if (str.length() == 0)
/* 211 */     { character = null; }
/* 212 */     else { if (str.length() > 1) {
/* 213 */         throw DbException.get(22025, str);
/*     */       }
/* 215 */       character = Character.valueOf(str.charAt(0)); }
/*     */     
/* 217 */     return character;
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 222 */     if (this.not || this.whenOperand || this.likeType == LikeType.REGEXP || !(this.left instanceof ExpressionColumn)) {
/*     */       return;
/*     */     }
/* 225 */     ExpressionColumn expressionColumn = (ExpressionColumn)this.left;
/* 226 */     if (paramTableFilter != expressionColumn.getTableFilter() || !TypeInfo.haveSameOrdering(expressionColumn.getType(), this.ignoreCase ? TypeInfo.TYPE_VARCHAR_IGNORECASE : TypeInfo.TYPE_VARCHAR)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 235 */     if (!this.right.isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
/*     */       return;
/*     */     }
/* 238 */     if (this.escape != null && !this.escape.isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
/*     */       return;
/*     */     }
/* 241 */     String str1 = this.right.getValue(paramSessionLocal).getString();
/* 242 */     if (!this.isInit) {
/* 243 */       Value value = (this.escape == null) ? null : this.escape.getValue(paramSessionLocal);
/* 244 */       if (value == ValueNull.INSTANCE)
/*     */       {
/* 246 */         throw DbException.getInternalError();
/*     */       }
/* 248 */       initPattern(str1, getEscapeChar(value));
/*     */     } 
/* 250 */     if (this.invalidPattern) {
/*     */       return;
/*     */     }
/* 253 */     if (this.patternLength <= 0 || this.patternTypes[0] != 0) {
/*     */       return;
/*     */     }
/*     */     
/* 257 */     if (!DataType.isStringType(expressionColumn.getColumn().getType().getValueType())) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 263 */     byte b = 0;
/* 264 */     StringBuilder stringBuilder = new StringBuilder();
/* 265 */     while (b < this.patternLength && this.patternTypes[b] == 0) {
/* 266 */       stringBuilder.append(this.patternChars[b++]);
/*     */     }
/* 268 */     String str2 = stringBuilder.toString();
/* 269 */     if (b == this.patternLength) {
/* 270 */       paramTableFilter.addIndexCondition(IndexCondition.get(0, expressionColumn, 
/* 271 */             (Expression)ValueExpression.get(ValueVarchar.get(str2))));
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 276 */     else if (str2.length() > 0) {
/* 277 */       paramTableFilter.addIndexCondition(IndexCondition.get(5, expressionColumn, 
/*     */             
/* 279 */             (Expression)ValueExpression.get(ValueVarchar.get(str2))));
/* 280 */       char c = str2.charAt(str2.length() - 1);
/*     */ 
/*     */       
/* 283 */       for (byte b1 = 1; b1 < 'ߐ'; b1++) {
/* 284 */         String str = str2.substring(0, str2.length() - 1) + (char)(c + b1);
/* 285 */         if (this.compareMode.compareString(str2, str, this.ignoreCase) < 0) {
/* 286 */           paramTableFilter.addIndexCondition(IndexCondition.get(2, expressionColumn, 
/*     */                 
/* 288 */                 (Expression)ValueExpression.get(ValueVarchar.get(str))));
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 298 */     return getValue(paramSessionLocal, this.left.getValue(paramSessionLocal));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 303 */     if (!this.whenOperand) {
/* 304 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/* 306 */     return getValue(paramSessionLocal, paramValue).isTrue();
/*     */   }
/*     */   private Value getValue(SessionLocal paramSessionLocal, Value paramValue) {
/*     */     boolean bool;
/* 310 */     if (paramValue == ValueNull.INSTANCE) {
/* 311 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 313 */     if (!this.isInit) {
/* 314 */       Value value1 = this.right.getValue(paramSessionLocal);
/* 315 */       if (value1 == ValueNull.INSTANCE) {
/* 316 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/* 318 */       String str1 = value1.getString();
/* 319 */       Value value2 = (this.escape == null) ? null : this.escape.getValue(paramSessionLocal);
/* 320 */       if (value2 == ValueNull.INSTANCE) {
/* 321 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/* 323 */       initPattern(str1, getEscapeChar(value2));
/*     */     } 
/* 325 */     if (this.invalidPattern) {
/* 326 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 328 */     String str = paramValue.getString();
/*     */     
/* 330 */     if (this.likeType == LikeType.REGEXP) {
/* 331 */       bool = this.patternRegexp.matcher(str).find();
/* 332 */     } else if (this.shortcutToStartsWith) {
/* 333 */       bool = str.regionMatches(this.ignoreCase, 0, this.patternString, 0, this.patternLength - 1);
/* 334 */     } else if (this.shortcutToEndsWith) {
/* 335 */       bool = str.regionMatches(this.ignoreCase, str.length() - this.patternLength + 1, this.patternString, 1, this.patternLength - 1);
/*     */     }
/* 337 */     else if (this.shortcutToContains) {
/* 338 */       String str1 = this.patternString.substring(1, this.patternString.length() - 1);
/* 339 */       if (this.ignoreCase) {
/* 340 */         bool = containsIgnoreCase(str, str1);
/*     */       } else {
/* 342 */         bool = str.contains(str1);
/*     */       } 
/*     */     } else {
/* 345 */       bool = compareAt(str, 0, 0, str.length(), this.patternChars, this.patternTypes);
/*     */     } 
/* 347 */     return (Value)ValueBoolean.get(this.not ^ bool);
/*     */   }
/*     */   
/*     */   private static boolean containsIgnoreCase(String paramString1, String paramString2) {
/* 351 */     int i = paramString2.length();
/* 352 */     if (i == 0)
/*     */     {
/* 354 */       return true;
/*     */     }
/*     */     
/* 357 */     char c1 = Character.toLowerCase(paramString2.charAt(0));
/* 358 */     char c2 = Character.toUpperCase(paramString2.charAt(0));
/*     */     
/* 360 */     for (int j = paramString1.length() - i; j >= 0; j--) {
/*     */       
/* 362 */       char c = paramString1.charAt(j);
/* 363 */       if (c == c1 || c == c2)
/*     */       {
/*     */         
/* 366 */         if (paramString1.regionMatches(true, j, paramString2, 0, i)) {
/* 367 */           return true;
/*     */         }
/*     */       }
/*     */     } 
/* 371 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean compareAt(String paramString, int paramInt1, int paramInt2, int paramInt3, char[] paramArrayOfchar, int[] paramArrayOfint) {
/* 376 */     for (; paramInt1 < this.patternLength; paramInt1++) {
/* 377 */       switch (paramArrayOfint[paramInt1]) {
/*     */         case 0:
/* 379 */           if (paramInt2 >= paramInt3 || !compare(paramArrayOfchar, paramString, paramInt1, paramInt2++)) {
/* 380 */             return false;
/*     */           }
/*     */           break;
/*     */         case 1:
/* 384 */           if (paramInt2++ >= paramInt3) {
/* 385 */             return false;
/*     */           }
/*     */           break;
/*     */         case 2:
/* 389 */           if (++paramInt1 >= this.patternLength) {
/* 390 */             return true;
/*     */           }
/* 392 */           while (paramInt2 < paramInt3) {
/* 393 */             if (compare(paramArrayOfchar, paramString, paramInt1, paramInt2) && 
/* 394 */               compareAt(paramString, paramInt1, paramInt2, paramInt3, paramArrayOfchar, paramArrayOfint)) {
/* 395 */               return true;
/*     */             }
/* 397 */             paramInt2++;
/*     */           } 
/* 399 */           return false;
/*     */         default:
/* 401 */           throw DbException.getInternalError(Integer.toString(paramArrayOfint[paramInt1]));
/*     */       } 
/*     */     } 
/* 404 */     return (paramInt2 == paramInt3);
/*     */   }
/*     */   
/*     */   private boolean compare(char[] paramArrayOfchar, String paramString, int paramInt1, int paramInt2) {
/* 408 */     return (paramArrayOfchar[paramInt1] == paramString.charAt(paramInt2) || (!this.fastCompare && this.compareMode
/* 409 */       .equalsChars(this.patternString, paramInt1, paramString, paramInt2, this.ignoreCase)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWhenConditionOperand() {
/* 415 */     return this.whenOperand;
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
/*     */   public boolean test(String paramString1, String paramString2, char paramChar) {
/* 427 */     initPattern(paramString1, Character.valueOf(paramChar));
/* 428 */     return test(paramString2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean test(String paramString) {
/* 438 */     if (this.invalidPattern) {
/* 439 */       return false;
/*     */     }
/* 441 */     return compareAt(paramString, 0, 0, paramString.length(), this.patternChars, this.patternTypes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initPattern(String paramString, Character paramCharacter) {
/* 451 */     if (this.compareMode.getName().equals("OFF") && !this.ignoreCase) {
/* 452 */       this.fastCompare = true;
/*     */     }
/* 454 */     if (this.likeType == LikeType.REGEXP) {
/* 455 */       this.patternString = paramString;
/*     */       try {
/* 457 */         if (this.ignoreCase) {
/* 458 */           this.patternRegexp = Pattern.compile(paramString, 2);
/*     */         } else {
/* 460 */           this.patternRegexp = Pattern.compile(paramString);
/*     */         } 
/* 462 */       } catch (PatternSyntaxException patternSyntaxException) {
/* 463 */         throw DbException.get(22025, patternSyntaxException, new String[] { paramString });
/*     */       } 
/*     */       return;
/*     */     } 
/* 467 */     this.patternLength = 0;
/* 468 */     if (paramString == null) {
/* 469 */       this.patternTypes = null;
/* 470 */       this.patternChars = null;
/*     */       return;
/*     */     } 
/* 473 */     int i = paramString.length();
/* 474 */     this.patternChars = new char[i];
/* 475 */     this.patternTypes = new int[i];
/* 476 */     boolean bool = false; byte b;
/* 477 */     for (b = 0; b < i; b++) {
/* 478 */       boolean bool1; char c = paramString.charAt(b);
/*     */       
/* 480 */       if (paramCharacter != null && paramCharacter.charValue() == c) {
/* 481 */         if (b >= i - 1) {
/* 482 */           this.invalidPattern = true;
/*     */           return;
/*     */         } 
/* 485 */         c = paramString.charAt(++b);
/* 486 */         bool1 = false;
/* 487 */         bool = false;
/* 488 */       } else if (c == '%') {
/* 489 */         if (bool) {
/*     */           continue;
/*     */         }
/* 492 */         bool1 = true;
/* 493 */         bool = true;
/* 494 */       } else if (c == '_') {
/* 495 */         bool1 = true;
/*     */       } else {
/* 497 */         bool1 = false;
/* 498 */         bool = false;
/*     */       } 
/* 500 */       this.patternTypes[this.patternLength] = bool1;
/* 501 */       this.patternChars[this.patternLength++] = c; continue;
/*     */     } 
/* 503 */     for (b = 0; b < this.patternLength - 1; b++) {
/* 504 */       if (this.patternTypes[b] == 2 && this.patternTypes[b + 1] == 1) {
/* 505 */         this.patternTypes[b] = 1;
/* 506 */         this.patternTypes[b + 1] = 2;
/*     */       } 
/*     */     } 
/* 509 */     this.patternString = new String(this.patternChars, 0, this.patternLength);
/*     */ 
/*     */     
/* 512 */     this.shortcutToStartsWith = false;
/* 513 */     this.shortcutToEndsWith = false;
/* 514 */     this.shortcutToContains = false;
/*     */ 
/*     */     
/* 517 */     if (this.compareMode.getName().equals("OFF") && this.patternLength > 1) {
/* 518 */       b = 0;
/* 519 */       while (b < this.patternLength && this.patternTypes[b] == 0) {
/* 520 */         b++;
/*     */       }
/* 522 */       if (b == this.patternLength - 1 && this.patternTypes[this.patternLength - 1] == 2) {
/* 523 */         this.shortcutToStartsWith = true;
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 528 */     if (this.compareMode.getName().equals("OFF") && this.patternLength > 1 && 
/* 529 */       this.patternTypes[0] == 2) {
/* 530 */       b = 1;
/* 531 */       while (b < this.patternLength && this.patternTypes[b] == 0) {
/* 532 */         b++;
/*     */       }
/* 534 */       if (b == this.patternLength) {
/* 535 */         this.shortcutToEndsWith = true;
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 541 */     if (this.compareMode.getName().equals("OFF") && this.patternLength > 2 && 
/* 542 */       this.patternTypes[0] == 2) {
/* 543 */       b = 1;
/* 544 */       while (b < this.patternLength && this.patternTypes[b] == 0) {
/* 545 */         b++;
/*     */       }
/* 547 */       if (b == this.patternLength - 1 && this.patternTypes[this.patternLength - 1] == 2) {
/* 548 */         this.shortcutToContains = true;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isFullMatch() {
/* 555 */     if (this.patternTypes == null) {
/* 556 */       return false;
/*     */     }
/* 558 */     for (int i : this.patternTypes) {
/* 559 */       if (i != 0) {
/* 560 */         return false;
/*     */       }
/*     */     } 
/* 563 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 568 */     if (this.whenOperand) {
/* 569 */       return null;
/*     */     }
/* 571 */     return new CompareLike(this.compareMode, this.defaultEscape, this.left, !this.not, false, this.right, this.escape, this.likeType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 576 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 577 */     this.right.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 578 */     if (this.escape != null) {
/* 579 */       this.escape.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 585 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 586 */     this.right.setEvaluatable(paramTableFilter, paramBoolean);
/* 587 */     if (this.escape != null) {
/* 588 */       this.escape.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 594 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 595 */     this.right.updateAggregate(paramSessionLocal, paramInt);
/* 596 */     if (this.escape != null) {
/* 597 */       this.escape.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 603 */     return (this.left.isEverything(paramExpressionVisitor) && this.right.isEverything(paramExpressionVisitor) && (this.escape == null || this.escape
/* 604 */       .isEverything(paramExpressionVisitor)));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 609 */     return this.left.getCost() + this.right.getCost() + 3;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 614 */     return (this.escape == null) ? 2 : 3;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 619 */     switch (paramInt) {
/*     */       case 0:
/* 621 */         return this.left;
/*     */       case 1:
/* 623 */         return this.right;
/*     */       case 2:
/* 625 */         if (this.escape != null) {
/* 626 */           return this.escape;
/*     */         }
/*     */         break;
/*     */     } 
/* 630 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\CompareLike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */