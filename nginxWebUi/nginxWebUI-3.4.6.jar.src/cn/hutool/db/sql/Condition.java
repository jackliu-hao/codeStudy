/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.clone.CloneSupport;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.text.StrSplitter;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Condition
/*     */   extends CloneSupport<Condition>
/*     */ {
/*     */   private static final String OPERATOR_LIKE = "LIKE";
/*     */   private static final String OPERATOR_IN = "IN";
/*     */   private static final String OPERATOR_IS = "IS";
/*     */   private static final String OPERATOR_IS_NOT = "IS NOT";
/*     */   private static final String OPERATOR_BETWEEN = "BETWEEN";
/*     */   
/*     */   public enum LikeType
/*     */   {
/*  31 */     StartWith,
/*     */ 
/*     */ 
/*     */     
/*  35 */     EndWith,
/*     */ 
/*     */ 
/*     */     
/*  39 */     Contains;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   private static final List<String> OPERATORS = Arrays.asList(new String[] { "<>", "<=", "<", ">=", ">", "=", "!=", "IN" });
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String VALUE_NULL = "NULL";
/*     */ 
/*     */ 
/*     */   
/*     */   private String field;
/*     */ 
/*     */ 
/*     */   
/*     */   private String operator;
/*     */ 
/*     */ 
/*     */   
/*     */   private Object value;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isPlaceHolder = true;
/*     */ 
/*     */ 
/*     */   
/*     */   private Object secondValue;
/*     */ 
/*     */ 
/*     */   
/*  75 */   private LogicalOperator linkOperator = LogicalOperator.AND;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Condition parse(String field, Object expression) {
/*  85 */     return new Condition(field, expression);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition(boolean isPlaceHolder) {
/* 102 */     this.isPlaceHolder = isPlaceHolder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition(String field, Object value) {
/* 112 */     this(field, "=", value);
/* 113 */     parseValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition(String field, String operator, Object value) {
/* 124 */     this.field = field;
/* 125 */     this.operator = operator;
/* 126 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition(String field, String value, LikeType likeType) {
/* 137 */     this.field = field;
/* 138 */     this.operator = "LIKE";
/* 139 */     this.value = SqlUtil.buildLikeValue(value, likeType, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getField() {
/* 149 */     return this.field;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setField(String field) {
/* 158 */     this.field = field;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOperator() {
/* 168 */     return this.operator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOperator(String operator) {
/* 178 */     this.operator = operator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue() {
/* 187 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(Object value) {
/* 196 */     setValue(value, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(Object value, boolean isParse) {
/* 206 */     this.value = value;
/* 207 */     if (isParse) {
/* 208 */       parseValue();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPlaceHolder() {
/* 218 */     return this.isPlaceHolder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPlaceHolder(boolean isPlaceHolder) {
/* 227 */     this.isPlaceHolder = isPlaceHolder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOperatorBetween() {
/* 237 */     return "BETWEEN".equalsIgnoreCase(this.operator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOperatorIn() {
/* 247 */     return "IN".equalsIgnoreCase(this.operator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOperatorIs() {
/* 257 */     return "IS".equalsIgnoreCase(this.operator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOperatorLike() {
/* 267 */     return "LIKE".equalsIgnoreCase(this.operator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition checkValueNull() {
/* 276 */     if (null == this.value) {
/* 277 */       this.operator = "IS";
/* 278 */       this.value = "NULL";
/*     */     } 
/* 280 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getSecondValue() {
/* 289 */     return this.secondValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSecondValue(Object secondValue) {
/* 298 */     this.secondValue = secondValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogicalOperator getLinkOperator() {
/* 308 */     return this.linkOperator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLinkOperator(LogicalOperator linkOperator) {
/* 318 */     this.linkOperator = linkOperator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 325 */     return toString(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(List<Object> paramValues) {
/* 335 */     StringBuilder conditionStrBuilder = StrUtil.builder();
/*     */     
/* 337 */     checkValueNull();
/*     */ 
/*     */     
/* 340 */     conditionStrBuilder.append(this.field).append(" ").append(this.operator);
/*     */     
/* 342 */     if (isOperatorBetween()) {
/* 343 */       buildValuePartForBETWEEN(conditionStrBuilder, paramValues);
/* 344 */     } else if (isOperatorIn()) {
/*     */       
/* 346 */       buildValuePartForIN(conditionStrBuilder, paramValues);
/*     */     }
/* 348 */     else if (isPlaceHolder() && false == isOperatorIs()) {
/*     */       
/* 350 */       conditionStrBuilder.append(" ?");
/* 351 */       if (null != paramValues) {
/* 352 */         paramValues.add(this.value);
/*     */       }
/*     */     } else {
/*     */       
/* 356 */       String valueStr = String.valueOf(this.value);
/* 357 */       conditionStrBuilder.append(" ").append(isOperatorLike() ? 
/* 358 */           StrUtil.wrap(valueStr, "'") : valueStr);
/*     */     } 
/*     */ 
/*     */     
/* 362 */     return conditionStrBuilder.toString();
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
/*     */   private void buildValuePartForBETWEEN(StringBuilder conditionStrBuilder, List<Object> paramValues) {
/* 376 */     if (isPlaceHolder()) {
/*     */       
/* 378 */       conditionStrBuilder.append(" ?");
/* 379 */       if (null != paramValues) {
/* 380 */         paramValues.add(this.value);
/*     */       }
/*     */     } else {
/*     */       
/* 384 */       conditionStrBuilder.append(' ').append(this.value);
/*     */     } 
/*     */ 
/*     */     
/* 388 */     conditionStrBuilder.append(" ").append(LogicalOperator.AND);
/* 389 */     if (isPlaceHolder()) {
/*     */       
/* 391 */       conditionStrBuilder.append(" ?");
/* 392 */       if (null != paramValues) {
/* 393 */         paramValues.add(this.secondValue);
/*     */       }
/*     */     } else {
/*     */       
/* 397 */       conditionStrBuilder.append(' ').append(this.secondValue);
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
/*     */   private void buildValuePartForIN(StringBuilder conditionStrBuilder, List<Object> paramValues) {
/* 409 */     conditionStrBuilder.append(" (");
/* 410 */     Object value = this.value;
/* 411 */     if (isPlaceHolder()) {
/*     */       Collection<?> valuesForIn;
/*     */       
/* 414 */       if (value instanceof Collection) {
/*     */         
/* 416 */         valuesForIn = (Collection)value;
/* 417 */       } else if (value instanceof CharSequence) {
/* 418 */         valuesForIn = StrUtil.split((CharSequence)value, ',');
/*     */       } else {
/* 420 */         valuesForIn = Arrays.asList((Object[])Convert.convert(Object[].class, value));
/*     */       } 
/* 422 */       conditionStrBuilder.append(StrUtil.repeatAndJoin("?", valuesForIn.size(), ","));
/* 423 */       if (null != paramValues) {
/* 424 */         paramValues.addAll(valuesForIn);
/*     */       }
/*     */     } else {
/* 427 */       conditionStrBuilder.append(StrUtil.join(",", new Object[] { value }));
/*     */     } 
/* 429 */     conditionStrBuilder.append(')');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseValue() {
/* 440 */     if (null == this.value) {
/* 441 */       this.operator = "IS";
/* 442 */       this.value = "NULL";
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 447 */     if (this.value instanceof Collection || ArrayUtil.isArray(this.value)) {
/* 448 */       this.operator = "IN";
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 453 */     if (false == this.value instanceof String) {
/*     */       return;
/*     */     }
/*     */     
/* 457 */     String valueStr = (String)this.value;
/* 458 */     if (StrUtil.isBlank(valueStr)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 463 */     valueStr = StrUtil.trim(valueStr);
/*     */ 
/*     */     
/* 466 */     if (StrUtil.endWithIgnoreCase(valueStr, "null")) {
/* 467 */       if (StrUtil.equalsIgnoreCase("= null", valueStr) || StrUtil.equalsIgnoreCase("is null", valueStr)) {
/*     */         
/* 469 */         this.operator = "IS";
/* 470 */         this.value = "NULL";
/* 471 */         this.isPlaceHolder = false; return;
/*     */       } 
/* 473 */       if (StrUtil.equalsIgnoreCase("!= null", valueStr) || StrUtil.equalsIgnoreCase("is not null", valueStr)) {
/*     */         
/* 475 */         this.operator = "IS NOT";
/* 476 */         this.value = "NULL";
/* 477 */         this.isPlaceHolder = false;
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 482 */     List<String> strs = StrUtil.split(valueStr, ' ', 2);
/* 483 */     if (strs.size() < 2) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 488 */     String firstPart = ((String)strs.get(0)).trim().toUpperCase();
/* 489 */     if (OPERATORS.contains(firstPart)) {
/* 490 */       this.operator = firstPart;
/*     */       
/* 492 */       String valuePart = strs.get(1);
/* 493 */       this.value = isOperatorIn() ? valuePart : tryToNumber(valuePart);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 498 */     if ("LIKE".equals(firstPart)) {
/* 499 */       this.operator = "LIKE";
/* 500 */       this.value = unwrapQuote(strs.get(1));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 505 */     if ("BETWEEN".equals(firstPart)) {
/* 506 */       List<String> betweenValueStrs = StrSplitter.splitTrimIgnoreCase(strs.get(1), LogicalOperator.AND.toString(), 2, true);
/* 507 */       if (betweenValueStrs.size() < 2) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 512 */       this.operator = "BETWEEN";
/* 513 */       this.value = unwrapQuote(betweenValueStrs.get(0));
/* 514 */       this.secondValue = unwrapQuote(betweenValueStrs.get(1));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String unwrapQuote(String value) {
/* 525 */     if (null == value) {
/* 526 */       return null;
/*     */     }
/* 528 */     value = value.trim();
/*     */     
/* 530 */     int from = 0;
/* 531 */     int to = value.length();
/* 532 */     char startChar = value.charAt(0);
/* 533 */     char endChar = value.charAt(value.length() - 1);
/* 534 */     if (startChar == endChar && (
/* 535 */       '\'' == startChar || '"' == startChar)) {
/* 536 */       from = 1;
/* 537 */       to--;
/*     */     } 
/*     */ 
/*     */     
/* 541 */     if (from == 0)
/*     */     {
/* 543 */       return value;
/*     */     }
/* 545 */     return value.substring(from, to);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Object tryToNumber(String value) {
/* 555 */     value = StrUtil.trim(value);
/* 556 */     if (false == NumberUtil.isNumber(value)) {
/* 557 */       return value;
/*     */     }
/*     */     try {
/* 560 */       return NumberUtil.parseNumber(value);
/* 561 */     } catch (Exception ignore) {
/* 562 */       return value;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\Condition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */