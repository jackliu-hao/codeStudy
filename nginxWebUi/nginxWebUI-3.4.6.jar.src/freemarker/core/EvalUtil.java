/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.ext.beans.BeanModel;
/*     */ import freemarker.ext.beans._BeansAPI;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class EvalUtil
/*     */ {
/*     */   static final int CMP_OP_EQUALS = 1;
/*     */   static final int CMP_OP_NOT_EQUALS = 2;
/*     */   static final int CMP_OP_LESS_THAN = 3;
/*     */   static final int CMP_OP_GREATER_THAN = 4;
/*     */   static final int CMP_OP_LESS_THAN_EQUALS = 5;
/*     */   static final int CMP_OP_GREATER_THAN_EQUALS = 6;
/*     */   private static final String VALUE_OF_THE_COMPARISON_IS_UNKNOWN_DATE_LIKE = "value of the comparison is a date-like value where it's not known if it's a date (no time part), time, or date-time, and thus can't be used in a comparison.";
/*     */   
/*     */   static String modelToString(TemplateScalarModel model, Expression expr, Environment env) throws TemplateModelException {
/*  59 */     String value = model.getAsString();
/*  60 */     if (value == null) {
/*  61 */       if (env == null) env = Environment.getCurrentEnvironment(); 
/*  62 */       if (env != null && env.isClassicCompatible()) {
/*  63 */         return "";
/*     */       }
/*  65 */       throw newModelHasStoredNullException(String.class, model, expr);
/*     */     } 
/*     */     
/*  68 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Number modelToNumber(TemplateNumberModel model, Expression expr) throws TemplateModelException {
/*  76 */     Number value = model.getAsNumber();
/*  77 */     if (value == null) throw newModelHasStoredNullException(Number.class, model, expr); 
/*  78 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Date modelToDate(TemplateDateModel model, Expression expr) throws TemplateModelException {
/*  86 */     Date value = model.getAsDate();
/*  87 */     if (value == null) throw newModelHasStoredNullException(Date.class, model, expr); 
/*  88 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static TemplateModelException newModelHasStoredNullException(Class expected, TemplateModel model, Expression expr) {
/*  94 */     return new _TemplateModelException(expr, 
/*  95 */         _TemplateModelException.modelHasStoredNullDescription(expected, model));
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
/*     */   static boolean compare(Expression leftExp, int operator, String operatorString, Expression rightExp, Expression defaultBlamed, Environment env) throws TemplateException {
/* 113 */     TemplateModel ltm = leftExp.eval(env);
/* 114 */     TemplateModel rtm = rightExp.eval(env);
/* 115 */     return compare(ltm, leftExp, operator, operatorString, rtm, rightExp, defaultBlamed, false, false, false, false, env);
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
/*     */   static boolean compare(TemplateModel leftValue, int operator, TemplateModel rightValue, Environment env) throws TemplateException {
/* 137 */     return compare(leftValue, null, operator, null, rightValue, null, null, false, false, false, false, env);
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
/*     */   static boolean compareLenient(TemplateModel leftValue, int operator, TemplateModel rightValue, Environment env) throws TemplateException {
/* 154 */     return compare(leftValue, null, operator, null, rightValue, null, null, false, true, false, false, env);
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
/*     */   static boolean compare(TemplateModel leftValue, Expression leftExp, int operator, String operatorString, TemplateModel rightValue, Expression rightExp, Expression defaultBlamed, boolean quoteOperandsInErrors, boolean typeMismatchMeansNotEqual, boolean leftNullReturnsFalse, boolean rightNullReturnsFalse, Environment env) throws TemplateException {
/*     */     int cmpResult;
/* 190 */     if (leftValue == null) {
/* 191 */       if (env != null && env.isClassicCompatible()) {
/* 192 */         leftValue = TemplateScalarModel.EMPTY_STRING;
/*     */       } else {
/* 194 */         if (leftNullReturnsFalse) {
/* 195 */           return false;
/*     */         }
/* 197 */         if (leftExp != null) {
/* 198 */           throw InvalidReferenceException.getInstance(leftExp, env);
/*     */         }
/* 200 */         throw new _MiscTemplateException(defaultBlamed, env, "The left operand of the comparison was undefined or null.");
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 207 */     if (rightValue == null) {
/* 208 */       if (env != null && env.isClassicCompatible()) {
/* 209 */         rightValue = TemplateScalarModel.EMPTY_STRING;
/*     */       } else {
/* 211 */         if (rightNullReturnsFalse) {
/* 212 */           return false;
/*     */         }
/* 214 */         if (rightExp != null) {
/* 215 */           throw InvalidReferenceException.getInstance(rightExp, env);
/*     */         }
/* 217 */         throw new _MiscTemplateException(defaultBlamed, env, "The right operand of the comparison was undefined or null.");
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 225 */     if (leftValue instanceof TemplateNumberModel && rightValue instanceof TemplateNumberModel) {
/* 226 */       Number leftNum = modelToNumber((TemplateNumberModel)leftValue, leftExp);
/* 227 */       Number rightNum = modelToNumber((TemplateNumberModel)rightValue, rightExp);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 232 */       ArithmeticEngine ae = (env != null) ? env.getArithmeticEngine() : ((leftExp != null) ? leftExp.getTemplate().getArithmeticEngine() : ArithmeticEngine.BIGDECIMAL_ENGINE);
/*     */       
/*     */       try {
/* 235 */         cmpResult = ae.compareNumbers(leftNum, rightNum);
/* 236 */       } catch (RuntimeException e) {
/* 237 */         throw new _MiscTemplateException(defaultBlamed, e, env, new Object[] { "Unexpected error while comparing two numbers: ", e });
/*     */       }
/*     */     
/* 240 */     } else if (leftValue instanceof TemplateDateModel && rightValue instanceof TemplateDateModel) {
/* 241 */       TemplateDateModel leftDateModel = (TemplateDateModel)leftValue;
/* 242 */       TemplateDateModel rightDateModel = (TemplateDateModel)rightValue;
/*     */       
/* 244 */       int leftDateType = leftDateModel.getDateType();
/* 245 */       int rightDateType = rightDateModel.getDateType();
/*     */       
/* 247 */       if (leftDateType == 0 || rightDateType == 0) {
/*     */         String sideName;
/*     */         Expression sideExp;
/* 250 */         if (leftDateType == 0) {
/* 251 */           sideName = "left";
/* 252 */           sideExp = leftExp;
/*     */         } else {
/* 254 */           sideName = "right";
/* 255 */           sideExp = rightExp;
/*     */         } 
/*     */         
/* 258 */         throw new _MiscTemplateException((sideExp != null) ? sideExp : defaultBlamed, env, new Object[] { "The ", sideName, " ", "value of the comparison is a date-like value where it's not known if it's a date (no time part), time, or date-time, and thus can't be used in a comparison." });
/*     */       } 
/*     */ 
/*     */       
/* 262 */       if (leftDateType != rightDateType)
/*     */       {
/* 264 */         throw new _MiscTemplateException(defaultBlamed, env, new Object[] { "Can't compare dates of different types. Left date type is ", TemplateDateModel.TYPE_NAMES
/*     */               
/* 266 */               .get(leftDateType), ", right date type is ", TemplateDateModel.TYPE_NAMES
/* 267 */               .get(rightDateType), "." });
/*     */       }
/*     */       
/* 270 */       Date leftDate = modelToDate(leftDateModel, leftExp);
/* 271 */       Date rightDate = modelToDate(rightDateModel, rightExp);
/* 272 */       cmpResult = leftDate.compareTo(rightDate);
/* 273 */     } else if (leftValue instanceof TemplateScalarModel && rightValue instanceof TemplateScalarModel) {
/* 274 */       if (operator != 1 && operator != 2) {
/* 275 */         throw new _MiscTemplateException(defaultBlamed, env, new Object[] { "Can't use operator \"", 
/* 276 */               cmpOpToString(operator, operatorString), "\" on string values." });
/*     */       }
/* 278 */       String leftString = modelToString((TemplateScalarModel)leftValue, leftExp, env);
/* 279 */       String rightString = modelToString((TemplateScalarModel)rightValue, rightExp, env);
/*     */       
/* 281 */       cmpResult = env.getCollator().compare(leftString, rightString);
/* 282 */     } else if (leftValue instanceof TemplateBooleanModel && rightValue instanceof TemplateBooleanModel) {
/* 283 */       if (operator != 1 && operator != 2) {
/* 284 */         throw new _MiscTemplateException(defaultBlamed, env, new Object[] { "Can't use operator \"", 
/* 285 */               cmpOpToString(operator, operatorString), "\" on boolean values." });
/*     */       }
/* 287 */       boolean leftBool = ((TemplateBooleanModel)leftValue).getAsBoolean();
/* 288 */       boolean rightBool = ((TemplateBooleanModel)rightValue).getAsBoolean();
/* 289 */       cmpResult = (leftBool ? 1 : 0) - (rightBool ? 1 : 0);
/* 290 */     } else if (env.isClassicCompatible()) {
/* 291 */       String leftSting = leftExp.evalAndCoerceToPlainText(env);
/* 292 */       String rightString = rightExp.evalAndCoerceToPlainText(env);
/* 293 */       cmpResult = env.getCollator().compare(leftSting, rightString);
/*     */     } else {
/* 295 */       if (typeMismatchMeansNotEqual) {
/* 296 */         if (operator == 1)
/* 297 */           return false; 
/* 298 */         if (operator == 2) {
/* 299 */           return true;
/*     */         }
/*     */       } 
/*     */       
/* 303 */       (new Object[12])[0] = "Can't compare values of these types. "; (new Object[12])[1] = "Allowed comparisons are between two numbers, two strings, two dates, or two booleans.\n"; (new Object[12])[2] = "Left hand operand "; (new Object[3])[0] = "("; (new Object[3])[1] = new _DelayedGetCanonicalForm(leftExp); (new Object[3])[2] = ") value "; (new Object[12])[3] = (quoteOperandsInErrors && leftExp != null) ? new Object[3] : ""; (new Object[12])[4] = "is "; (new Object[12])[5] = new _DelayedAOrAn(new _DelayedFTLTypeDescription(leftValue)); (new Object[12])[6] = ".\n"; (new Object[12])[7] = "Right hand operand "; (new Object[3])[0] = "("; (new Object[3])[1] = new _DelayedGetCanonicalForm(rightExp); (new Object[3])[2] = ") value "; throw new _MiscTemplateException(defaultBlamed, env, new Object[] { null, null, null, null, null, null, null, null, (quoteOperandsInErrors && rightExp != null) ? new Object[3] : "", "is ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(rightValue)), "." });
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 319 */     switch (operator) { case 1:
/* 320 */         return (cmpResult == 0);
/* 321 */       case 2: return (cmpResult != 0);
/* 322 */       case 3: return (cmpResult < 0);
/* 323 */       case 4: return (cmpResult > 0);
/* 324 */       case 5: return (cmpResult <= 0);
/* 325 */       case 6: return (cmpResult >= 0); }
/* 326 */      throw new BugException("Unsupported comparator operator code: " + operator);
/*     */   }
/*     */ 
/*     */   
/*     */   private static String cmpOpToString(int operator, String operatorString) {
/* 331 */     if (operatorString != null) {
/* 332 */       return operatorString;
/*     */     }
/* 334 */     switch (operator) { case 1:
/* 335 */         return "equals";
/* 336 */       case 2: return "not-equals";
/* 337 */       case 3: return "less-than";
/* 338 */       case 4: return "greater-than";
/* 339 */       case 5: return "less-than-equals";
/* 340 */       case 6: return "greater-than-equals"; }
/* 341 */      return "???";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static int mirrorCmpOperator(int operator) {
/* 347 */     switch (operator) { case 1:
/* 348 */         return 1;
/* 349 */       case 2: return 2;
/* 350 */       case 3: return 4;
/* 351 */       case 4: return 3;
/* 352 */       case 5: return 6;
/* 353 */       case 6: return 5; }
/* 354 */      throw new BugException("Unsupported comparator operator code: " + operator);
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
/*     */   static Object coerceModelToStringOrMarkup(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
/* 370 */     return coerceModelToStringOrMarkup(tm, exp, false, seqTip, env);
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
/*     */   static Object coerceModelToStringOrMarkup(TemplateModel tm, Expression exp, boolean returnNullOnNonCoercableType, String seqTip, Environment env) throws TemplateException {
/* 382 */     if (tm instanceof TemplateNumberModel) {
/* 383 */       TemplateNumberModel tnm = (TemplateNumberModel)tm;
/* 384 */       TemplateNumberFormat format = env.getTemplateNumberFormat(exp, false);
/*     */       try {
/* 386 */         return assertFormatResultNotNull(format.format(tnm));
/* 387 */       } catch (TemplateValueFormatException e) {
/* 388 */         throw _MessageUtil.newCantFormatNumberException(format, exp, e, false);
/*     */       } 
/* 390 */     }  if (tm instanceof TemplateDateModel) {
/* 391 */       TemplateDateModel tdm = (TemplateDateModel)tm;
/* 392 */       TemplateDateFormat format = env.getTemplateDateFormat(tdm, exp, false);
/*     */       try {
/* 394 */         return assertFormatResultNotNull(format.format(tdm));
/* 395 */       } catch (TemplateValueFormatException e) {
/* 396 */         throw _MessageUtil.newCantFormatDateException(format, exp, e, false);
/*     */       } 
/* 398 */     }  if (tm instanceof TemplateMarkupOutputModel) {
/* 399 */       return tm;
/*     */     }
/* 401 */     return coerceModelToTextualCommon(tm, exp, seqTip, true, returnNullOnNonCoercableType, env);
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
/*     */   static String coerceModelToStringOrUnsupportedMarkup(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
/* 417 */     if (tm instanceof TemplateNumberModel) {
/* 418 */       TemplateNumberModel tnm = (TemplateNumberModel)tm;
/* 419 */       TemplateNumberFormat format = env.getTemplateNumberFormat(exp, false);
/*     */       try {
/* 421 */         return ensureFormatResultString(format.format(tnm), exp, env);
/* 422 */       } catch (TemplateValueFormatException e) {
/* 423 */         throw _MessageUtil.newCantFormatNumberException(format, exp, e, false);
/*     */       } 
/* 425 */     }  if (tm instanceof TemplateDateModel) {
/* 426 */       TemplateDateModel tdm = (TemplateDateModel)tm;
/* 427 */       TemplateDateFormat format = env.getTemplateDateFormat(tdm, exp, false);
/*     */       try {
/* 429 */         return ensureFormatResultString(format.format(tdm), exp, env);
/* 430 */       } catch (TemplateValueFormatException e) {
/* 431 */         throw _MessageUtil.newCantFormatDateException(format, exp, e, false);
/*     */       } 
/*     */     } 
/* 434 */     return coerceModelToTextualCommon(tm, exp, seqTip, false, false, env);
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
/*     */   static String coerceModelToPlainText(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
/* 450 */     if (tm instanceof TemplateNumberModel)
/* 451 */       return assertFormatResultNotNull(env.formatNumberToPlainText((TemplateNumberModel)tm, exp, false)); 
/* 452 */     if (tm instanceof TemplateDateModel) {
/* 453 */       return assertFormatResultNotNull(env.formatDateToPlainText((TemplateDateModel)tm, exp, false));
/*     */     }
/* 455 */     return coerceModelToTextualCommon(tm, exp, seqTip, false, false, env);
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
/*     */   private static String coerceModelToTextualCommon(TemplateModel tm, Expression exp, String seqHint, boolean supportsTOM, boolean returnNullOnNonCoercableType, Environment env) throws TemplateModelException, InvalidReferenceException, TemplateException, NonStringOrTemplateOutputException, NonStringException {
/* 474 */     if (tm instanceof TemplateScalarModel)
/* 475 */       return modelToString((TemplateScalarModel)tm, exp, env); 
/* 476 */     if (tm == null) {
/* 477 */       if (env.isClassicCompatible()) {
/* 478 */         return "";
/*     */       }
/* 480 */       if (exp != null) {
/* 481 */         throw InvalidReferenceException.getInstance(exp, env);
/*     */       }
/* 483 */       throw new InvalidReferenceException("Null/missing value (no more informatoin avilable)", env);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 488 */     if (tm instanceof TemplateBooleanModel) {
/*     */ 
/*     */ 
/*     */       
/* 492 */       boolean booleanValue = ((TemplateBooleanModel)tm).getAsBoolean();
/* 493 */       int compatMode = env.getClassicCompatibleAsInt();
/* 494 */       if (compatMode == 0) {
/* 495 */         return env.formatBoolean(booleanValue, false);
/*     */       }
/* 497 */       if (compatMode == 1)
/* 498 */         return booleanValue ? "true" : ""; 
/* 499 */       if (compatMode == 2) {
/* 500 */         if (tm instanceof BeanModel)
/*     */         {
/* 502 */           return _BeansAPI.getAsClassicCompatibleString((BeanModel)tm);
/*     */         }
/* 504 */         return booleanValue ? "true" : "";
/*     */       } 
/*     */       
/* 507 */       throw new BugException("Unsupported classic_compatible variation: " + compatMode);
/*     */     } 
/*     */ 
/*     */     
/* 511 */     if (env.isClassicCompatible() && tm instanceof BeanModel) {
/* 512 */       return _BeansAPI.getAsClassicCompatibleString((BeanModel)tm);
/*     */     }
/* 514 */     if (returnNullOnNonCoercableType) {
/* 515 */       return null;
/*     */     }
/* 517 */     if (seqHint != null && (tm instanceof freemarker.template.TemplateSequenceModel || tm instanceof freemarker.template.TemplateCollectionModel)) {
/* 518 */       if (supportsTOM) {
/* 519 */         throw new NonStringOrTemplateOutputException(exp, tm, seqHint, env);
/*     */       }
/* 521 */       throw new NonStringException(exp, tm, seqHint, env);
/*     */     } 
/*     */     
/* 524 */     if (supportsTOM) {
/* 525 */       throw new NonStringOrTemplateOutputException(exp, tm, env);
/*     */     }
/* 527 */     throw new NonStringException(exp, tm, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String ensureFormatResultString(Object formatResult, Expression exp, Environment env) throws NonStringException {
/* 535 */     if (formatResult instanceof String) {
/* 536 */       return (String)formatResult;
/*     */     }
/*     */     
/* 539 */     assertFormatResultNotNull(formatResult);
/*     */     
/* 541 */     TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)formatResult;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 546 */     _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[] { "Value was formatted to convert it to string, but the result was markup of ouput format ", new _DelayedJQuote(mo.getOutputFormat()), "." })).tip("Use value?string to force formatting to plain text.").blame(exp);
/* 547 */     throw new NonStringException(null, desc);
/*     */   }
/*     */   
/*     */   static String assertFormatResultNotNull(String r) {
/* 551 */     if (r != null) {
/* 552 */       return r;
/*     */     }
/* 554 */     throw new NullPointerException("TemplateValueFormatter result can't be null");
/*     */   }
/*     */   
/*     */   static Object assertFormatResultNotNull(Object r) {
/* 558 */     if (r != null) {
/* 559 */       return r;
/*     */     }
/* 561 */     throw new NullPointerException("TemplateValueFormatter result can't be null");
/*     */   }
/*     */ 
/*     */   
/*     */   static TemplateMarkupOutputModel concatMarkupOutputs(TemplateObject parent, TemplateMarkupOutputModel leftMO, TemplateMarkupOutputModel rightMO) throws TemplateException {
/* 566 */     MarkupOutputFormat<TemplateMarkupOutputModel> leftOF = leftMO.getOutputFormat();
/* 567 */     MarkupOutputFormat<TemplateMarkupOutputModel> rightOF = rightMO.getOutputFormat();
/* 568 */     if (rightOF != leftOF) {
/*     */       String rightPT;
/*     */       
/* 571 */       if ((rightPT = rightOF.getSourcePlainText(rightMO)) != null)
/* 572 */         return leftOF.concat(leftMO, leftOF.fromPlainTextByEscaping(rightPT));  String leftPT;
/* 573 */       if ((leftPT = leftOF.getSourcePlainText(leftMO)) != null) {
/* 574 */         return rightOF.concat(rightOF.fromPlainTextByEscaping(leftPT), rightMO);
/*     */       }
/* 576 */       Object[] message = { "Concatenation left hand operand is in ", new _DelayedToString(leftOF), " format, while the right hand operand is in ", new _DelayedToString(rightOF), ". Conversion to common format wasn't possible." };
/*     */ 
/*     */       
/* 579 */       if (parent instanceof Expression) {
/* 580 */         throw new _MiscTemplateException((Expression)parent, message);
/*     */       }
/* 582 */       throw new _MiscTemplateException(message);
/*     */     } 
/*     */ 
/*     */     
/* 586 */     return leftOF.concat(leftMO, rightMO);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static ArithmeticEngine getArithmeticEngine(Environment env, TemplateObject tObj) {
/* 594 */     return (env != null) ? env
/* 595 */       .getArithmeticEngine() : tObj
/* 596 */       .getTemplate().getParserConfiguration().getArithmeticEngine();
/*     */   }
/*     */   
/*     */   static boolean shouldWrapUncheckedException(Throwable e, Environment env) {
/* 600 */     if (FlowControlException.class.isInstance(e)) {
/* 601 */       return false;
/*     */     }
/* 603 */     if (env.getWrapUncheckedExceptions())
/* 604 */       return true; 
/* 605 */     if (env.getConfiguration().getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_27) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 610 */       Class<? extends Throwable> c = (Class)e.getClass();
/* 611 */       return (c == NullPointerException.class || c == ClassCastException.class || c == IndexOutOfBoundsException.class || c == InvocationTargetException.class);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 616 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\EvalUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */