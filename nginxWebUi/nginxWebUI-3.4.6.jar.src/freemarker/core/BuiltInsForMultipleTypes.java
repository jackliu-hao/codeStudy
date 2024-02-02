/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.ext.beans.BeanModel;
/*     */ import freemarker.ext.beans._BeansAPI;
/*     */ import freemarker.template.SimpleDate;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModelEx;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateModelWithAPISupport;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.NumberUtil;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BuiltInsForMultipleTypes
/*     */ {
/*     */   static class cBI
/*     */     extends AbstractCBI
/*     */     implements ICIChainMember
/*     */   {
/*     */     static class BIBeforeICI2d3d21
/*     */       extends BuiltInsForMultipleTypes.AbstractCBI
/*     */     {
/*     */       protected TemplateModel formatNumber(Environment env, TemplateModel model) throws TemplateModelException {
/*  64 */         Number num = EvalUtil.modelToNumber((TemplateNumberModel)model, this.target);
/*  65 */         if (num instanceof Integer || num instanceof Long)
/*     */         {
/*  67 */           return (TemplateModel)new SimpleScalar(num.toString());
/*     */         }
/*  69 */         return (TemplateModel)new SimpleScalar(env.getCNumberFormat().format(num));
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  75 */     private final BIBeforeICI2d3d21 prevICIObj = new BIBeforeICI2d3d21();
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/*  79 */       TemplateModel model = this.target.eval(env);
/*  80 */       if (model instanceof TemplateNumberModel)
/*  81 */         return formatNumber(env, model); 
/*  82 */       if (model instanceof TemplateBooleanModel) {
/*  83 */         return (TemplateModel)new SimpleScalar(((TemplateBooleanModel)model).getAsBoolean() ? "true" : "false");
/*     */       }
/*     */       
/*  86 */       throw new UnexpectedTypeException(this.target, model, "number or boolean", new Class[] { TemplateNumberModel.class, TemplateBooleanModel.class }, env);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected TemplateModel formatNumber(Environment env, TemplateModel model) throws TemplateModelException {
/*  95 */       Number num = EvalUtil.modelToNumber((TemplateNumberModel)model, this.target);
/*  96 */       if (num instanceof Integer || num instanceof Long)
/*     */       {
/*  98 */         return (TemplateModel)new SimpleScalar(num.toString()); } 
/*  99 */       if (num instanceof Double) {
/* 100 */         double n = num.doubleValue();
/* 101 */         if (n == Double.POSITIVE_INFINITY) {
/* 102 */           return (TemplateModel)new SimpleScalar("INF");
/*     */         }
/* 104 */         if (n == Double.NEGATIVE_INFINITY) {
/* 105 */           return (TemplateModel)new SimpleScalar("-INF");
/*     */         }
/* 107 */         if (Double.isNaN(n)) {
/* 108 */           return (TemplateModel)new SimpleScalar("NaN");
/*     */         }
/*     */       }
/* 111 */       else if (num instanceof Float) {
/* 112 */         float n = num.floatValue();
/* 113 */         if (n == Float.POSITIVE_INFINITY) {
/* 114 */           return (TemplateModel)new SimpleScalar("INF");
/*     */         }
/* 116 */         if (n == Float.NEGATIVE_INFINITY) {
/* 117 */           return (TemplateModel)new SimpleScalar("-INF");
/*     */         }
/* 119 */         if (Float.isNaN(n)) {
/* 120 */           return (TemplateModel)new SimpleScalar("NaN");
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 125 */       return (TemplateModel)new SimpleScalar(env.getCNumberFormat().format(num));
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMinimumICIVersion() {
/* 130 */       return _TemplateAPI.VERSION_INT_2_3_21;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getPreviousICIChainMember() {
/* 135 */       return this.prevICIObj;
/*     */     }
/*     */   }
/*     */   
/*     */   static class dateBI
/*     */     extends BuiltIn
/*     */   {
/*     */     private final int dateType;
/*     */     
/*     */     private class DateParser
/*     */       implements TemplateDateModel, TemplateMethodModel, TemplateHashModel
/*     */     {
/*     */       private final String text;
/*     */       private final Environment env;
/*     */       private final TemplateDateFormat defaultFormat;
/*     */       private TemplateDateModel cachedValue;
/*     */       
/*     */       DateParser(String text, Environment env) throws TemplateException {
/* 153 */         this.text = text;
/* 154 */         this.env = env;
/* 155 */         this.defaultFormat = env.getTemplateDateFormat(BuiltInsForMultipleTypes.dateBI.this.dateType, Date.class, BuiltInsForMultipleTypes.dateBI.this.target, false);
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List<String> args) throws TemplateModelException {
/* 160 */         BuiltInsForMultipleTypes.dateBI.this.checkMethodArgCount(args, 0, 1);
/* 161 */         return (args.size() == 0) ? getAsDateModel() : get(args.get(0));
/*     */       }
/*     */ 
/*     */       
/*     */       public TemplateModel get(String pattern) throws TemplateModelException {
/*     */         TemplateDateFormat format;
/*     */         try {
/* 168 */           format = this.env.getTemplateDateFormat(pattern, BuiltInsForMultipleTypes.dateBI.this.dateType, Date.class, BuiltInsForMultipleTypes.dateBI.this.target, BuiltInsForMultipleTypes.dateBI.this, true);
/* 169 */         } catch (TemplateException e) {
/*     */           
/* 171 */           throw _CoreAPI.ensureIsTemplateModelException("Failed to get format", e);
/*     */         } 
/* 173 */         return (TemplateModel)toTemplateDateModel(parse(format));
/*     */       }
/*     */       
/*     */       private TemplateDateModel toTemplateDateModel(Object date) throws _TemplateModelException {
/* 177 */         if (date instanceof Date) {
/* 178 */           return (TemplateDateModel)new SimpleDate((Date)date, BuiltInsForMultipleTypes.dateBI.this.dateType);
/*     */         }
/* 180 */         TemplateDateModel tm = (TemplateDateModel)date;
/* 181 */         if (tm.getDateType() != BuiltInsForMultipleTypes.dateBI.this.dateType) {
/* 182 */           throw new _TemplateModelException("The result of the parsing was of the wrong date type.");
/*     */         }
/* 184 */         return tm;
/*     */       }
/*     */ 
/*     */       
/*     */       private TemplateDateModel getAsDateModel() throws TemplateModelException {
/* 189 */         if (this.cachedValue == null) {
/* 190 */           this.cachedValue = toTemplateDateModel(parse(this.defaultFormat));
/*     */         }
/* 192 */         return this.cachedValue;
/*     */       }
/*     */ 
/*     */       
/*     */       public Date getAsDate() throws TemplateModelException {
/* 197 */         return getAsDateModel().getAsDate();
/*     */       }
/*     */ 
/*     */       
/*     */       public int getDateType() {
/* 202 */         return BuiltInsForMultipleTypes.dateBI.this.dateType;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isEmpty() {
/* 207 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       private Object parse(TemplateDateFormat df) throws TemplateModelException {
/*     */         try {
/* 213 */           return df.parse(this.text, BuiltInsForMultipleTypes.dateBI.this.dateType);
/* 214 */         } catch (TemplateValueFormatException e) {
/* 215 */           throw new _TemplateModelException(e, new Object[] { "The string doesn't match the expected date/time/date-time format. The string to parse was: ", new _DelayedJQuote(this.text), ". ", "The expected format was: ", new _DelayedJQuote(df
/*     */ 
/*     */                   
/* 218 */                   .getDescription()), ".", 
/* 219 */                 (e.getMessage() != null) ? "\nThe nested reason given follows:\n" : "", 
/* 220 */                 (e.getMessage() != null) ? e.getMessage() : "" });
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     dateBI(int dateType) {
/* 229 */       this.dateType = dateType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 235 */       TemplateModel model = this.target.eval(env);
/* 236 */       if (model instanceof TemplateDateModel) {
/* 237 */         TemplateDateModel dmodel = (TemplateDateModel)model;
/* 238 */         int dtype = dmodel.getDateType();
/*     */         
/* 240 */         if (this.dateType == dtype) {
/* 241 */           return model;
/*     */         }
/*     */         
/* 244 */         if (dtype == 0 || dtype == 3) {
/* 245 */           return (TemplateModel)new SimpleDate(dmodel.getAsDate(), this.dateType);
/*     */         }
/* 247 */         throw new _MiscTemplateException(this, new Object[] { "Cannot convert ", TemplateDateModel.TYPE_NAMES
/* 248 */               .get(dtype), " to ", TemplateDateModel.TYPE_NAMES
/* 249 */               .get(this.dateType) });
/*     */       } 
/*     */ 
/*     */       
/* 253 */       String s = this.target.evalAndCoerceToPlainText(env);
/* 254 */       return (TemplateModel)new DateParser(s, env);
/*     */     }
/*     */   }
/*     */   
/*     */   static class apiBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 262 */       if (!env.isAPIBuiltinEnabled()) {
/* 263 */         throw new _MiscTemplateException(this, new Object[] { "Can't use ?api, because the \"", "api_builtin_enabled", "\" configuration setting is false. Think twice before you set it to true though. Especially, it shouldn't abused for modifying Map-s and Collection-s." });
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 268 */       TemplateModel tm = this.target.eval(env);
/* 269 */       if (!(tm instanceof TemplateModelWithAPISupport)) {
/* 270 */         this.target.assertNonNull(tm, env);
/* 271 */         throw new APINotSupportedTemplateException(env, this.target, tm);
/*     */       } 
/* 273 */       return ((TemplateModelWithAPISupport)tm).getAPI();
/*     */     }
/*     */   }
/*     */   
/*     */   static class has_apiBI
/*     */     extends BuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 280 */       TemplateModel tm = this.target.eval(env);
/* 281 */       this.target.assertNonNull(tm, env);
/* 282 */       return (tm instanceof TemplateModelWithAPISupport) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_booleanBI
/*     */     extends BuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 289 */       TemplateModel tm = this.target.eval(env);
/* 290 */       this.target.assertNonNull(tm, env);
/* 291 */       return (tm instanceof TemplateBooleanModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_collectionBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 299 */       TemplateModel tm = this.target.eval(env);
/* 300 */       this.target.assertNonNull(tm, env);
/* 301 */       return (tm instanceof freemarker.template.TemplateCollectionModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_collection_exBI
/*     */     extends BuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 308 */       TemplateModel tm = this.target.eval(env);
/* 309 */       this.target.assertNonNull(tm, env);
/* 310 */       return (tm instanceof TemplateCollectionModelEx) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_dateLikeBI
/*     */     extends BuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 317 */       TemplateModel tm = this.target.eval(env);
/* 318 */       this.target.assertNonNull(tm, env);
/* 319 */       return (tm instanceof TemplateDateModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_dateOfTypeBI
/*     */     extends BuiltIn
/*     */   {
/*     */     private final int dateType;
/*     */     
/*     */     is_dateOfTypeBI(int dateType) {
/* 329 */       this.dateType = dateType;
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 334 */       TemplateModel tm = this.target.eval(env);
/* 335 */       this.target.assertNonNull(tm, env);
/* 336 */       return (tm instanceof TemplateDateModel && ((TemplateDateModel)tm).getDateType() == this.dateType) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_directiveBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 344 */       TemplateModel tm = this.target.eval(env);
/* 345 */       this.target.assertNonNull(tm, env);
/*     */       
/* 347 */       return (tm instanceof freemarker.template.TemplateTransformModel || tm instanceof Macro || tm instanceof freemarker.template.TemplateDirectiveModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_enumerableBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 355 */       TemplateModel tm = this.target.eval(env);
/* 356 */       this.target.assertNonNull(tm, env);
/* 357 */       return ((tm instanceof TemplateSequenceModel || tm instanceof freemarker.template.TemplateCollectionModel) && (
/* 358 */         _TemplateAPI.getTemplateLanguageVersionAsInt(this) < _TemplateAPI.VERSION_INT_2_3_21 || (!(tm instanceof freemarker.ext.beans.SimpleMethodModel) && !(tm instanceof freemarker.ext.beans.OverloadedMethodsModel)))) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class is_hash_exBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 368 */       TemplateModel tm = this.target.eval(env);
/* 369 */       this.target.assertNonNull(tm, env);
/* 370 */       return (tm instanceof TemplateHashModelEx) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_hashBI
/*     */     extends BuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 377 */       TemplateModel tm = this.target.eval(env);
/* 378 */       this.target.assertNonNull(tm, env);
/* 379 */       return (tm instanceof TemplateHashModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_indexableBI
/*     */     extends BuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 386 */       TemplateModel tm = this.target.eval(env);
/* 387 */       this.target.assertNonNull(tm, env);
/* 388 */       return (tm instanceof TemplateSequenceModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_macroBI
/*     */     extends BuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 395 */       TemplateModel tm = this.target.eval(env);
/* 396 */       this.target.assertNonNull(tm, env);
/*     */       
/* 398 */       return (tm instanceof Macro) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_markup_outputBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 406 */       TemplateModel tm = this.target.eval(env);
/* 407 */       this.target.assertNonNull(tm, env);
/* 408 */       return (tm instanceof TemplateMarkupOutputModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_methodBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 416 */       TemplateModel tm = this.target.eval(env);
/* 417 */       this.target.assertNonNull(tm, env);
/* 418 */       return (tm instanceof TemplateMethodModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_nodeBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 426 */       TemplateModel tm = this.target.eval(env);
/* 427 */       this.target.assertNonNull(tm, env);
/* 428 */       return (tm instanceof freemarker.template.TemplateNodeModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_numberBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 436 */       TemplateModel tm = this.target.eval(env);
/* 437 */       this.target.assertNonNull(tm, env);
/* 438 */       return (tm instanceof TemplateNumberModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_sequenceBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 446 */       TemplateModel tm = this.target.eval(env);
/* 447 */       this.target.assertNonNull(tm, env);
/* 448 */       return (tm instanceof TemplateSequenceModel && ((!(tm instanceof freemarker.ext.beans.OverloadedMethodsModel) && !(tm instanceof freemarker.ext.beans.SimpleMethodModel)) || 
/*     */ 
/*     */         
/* 451 */         !env.isIcI2324OrLater())) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class is_stringBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 460 */       TemplateModel tm = this.target.eval(env);
/* 461 */       this.target.assertNonNull(tm, env);
/* 462 */       return (tm instanceof TemplateScalarModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class is_transformBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 470 */       TemplateModel tm = this.target.eval(env);
/* 471 */       this.target.assertNonNull(tm, env);
/* 472 */       return (tm instanceof freemarker.template.TemplateTransformModel) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */   }
/*     */   
/*     */   static class namespaceBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 480 */       TemplateModel tm = this.target.eval(env);
/* 481 */       if (!(tm instanceof Macro)) {
/* 482 */         throw new UnexpectedTypeException(this.target, tm, "macro or function", new Class[] { Macro.class }, env);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 487 */       return (TemplateModel)env.getMacroNamespace((Macro)tm);
/*     */     }
/*     */   }
/*     */   
/*     */   static class sizeBI
/*     */     extends BuiltIn {
/*     */     private int countingLimit;
/*     */     
/*     */     protected void setTarget(Expression target) {
/* 496 */       super.setTarget(target);
/* 497 */       target.enableLazilyGeneratedResult();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/*     */       int size;
/* 504 */       TemplateModel model = this.target.eval(env);
/*     */ 
/*     */       
/* 507 */       if (this.countingLimit == 1 && model instanceof TemplateCollectionModelEx) {
/* 508 */         size = ((TemplateCollectionModelEx)model).isEmpty() ? 0 : 1;
/* 509 */       } else if (model instanceof TemplateSequenceModel) {
/* 510 */         size = ((TemplateSequenceModel)model).size();
/* 511 */       } else if (model instanceof TemplateCollectionModelEx) {
/* 512 */         size = ((TemplateCollectionModelEx)model).size();
/* 513 */       } else if (model instanceof TemplateHashModelEx) {
/* 514 */         size = ((TemplateHashModelEx)model).size();
/* 515 */       } else if (model instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)model)
/* 516 */         .isSequence()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 523 */         TemplateModelIterator iterator = ((LazilyGeneratedCollectionModel)model).iterator();
/* 524 */         int counter = 0;
/*     */         
/* 526 */         counter++;
/* 527 */         while (iterator.hasNext() && counter != this.countingLimit)
/*     */         {
/*     */           
/* 530 */           iterator.next();
/*     */         }
/* 532 */         size = counter;
/*     */       } else {
/* 534 */         throw new UnexpectedTypeException(this.target, model, "extended-hash or sequence or extended collection", new Class[] { TemplateHashModelEx.class, TemplateSequenceModel.class, TemplateCollectionModelEx.class }, env);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 544 */       return (TemplateModel)new SimpleNumber(size);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void setCountingLimit(int cmpOperator, NumberLiteral rightOperand) {
/*     */       int cmpInt;
/*     */       try {
/* 555 */         cmpInt = NumberUtil.toIntExact(rightOperand.getAsNumber());
/* 556 */       } catch (ArithmeticException e) {
/*     */         return;
/*     */       } 
/*     */ 
/*     */       
/* 561 */       switch (cmpOperator) { case 1:
/* 562 */           this.countingLimit = cmpInt + 1; return;
/* 563 */         case 2: this.countingLimit = cmpInt + 1; return;
/* 564 */         case 3: this.countingLimit = cmpInt; return;
/* 565 */         case 4: this.countingLimit = cmpInt + 1; return;
/* 566 */         case 5: this.countingLimit = cmpInt + 1; return;
/* 567 */         case 6: this.countingLimit = cmpInt; return; }
/* 568 */        throw new BugException("Unsupported comparator operator code: " + cmpOperator);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class stringBI
/*     */     extends BuiltIn
/*     */   {
/*     */     private class BooleanFormatter
/*     */       implements TemplateScalarModel, TemplateMethodModel
/*     */     {
/*     */       private final TemplateBooleanModel bool;
/*     */       private final Environment env;
/*     */       
/*     */       BooleanFormatter(TemplateBooleanModel bool, Environment env) {
/* 583 */         this.bool = bool;
/* 584 */         this.env = env;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List<String> args) throws TemplateModelException {
/* 589 */         BuiltInsForMultipleTypes.stringBI.this.checkMethodArgCount(args, 2);
/* 590 */         return new SimpleScalar(args.get(this.bool.getAsBoolean() ? 0 : 1));
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public String getAsString() throws TemplateModelException {
/* 596 */         if (this.bool instanceof TemplateScalarModel) {
/* 597 */           return ((TemplateScalarModel)this.bool).getAsString();
/*     */         }
/*     */         try {
/* 600 */           return this.env.formatBoolean(this.bool.getAsBoolean(), true);
/* 601 */         } catch (TemplateException e) {
/* 602 */           throw new TemplateModelException(e);
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private class DateFormatter
/*     */       implements TemplateScalarModel, TemplateHashModel, TemplateMethodModel
/*     */     {
/*     */       private final TemplateDateModel dateModel;
/*     */       
/*     */       private final Environment env;
/*     */       
/*     */       private final TemplateDateFormat defaultFormat;
/*     */       
/*     */       private String cachedValue;
/*     */       
/*     */       DateFormatter(TemplateDateModel dateModel, Environment env) throws TemplateException {
/* 620 */         this.dateModel = dateModel;
/* 621 */         this.env = env;
/*     */         
/* 623 */         int dateType = dateModel.getDateType();
/* 624 */         this
/*     */           
/* 626 */           .defaultFormat = (dateType == 0) ? null : env.getTemplateDateFormat(dateType, 
/* 627 */             (Class)EvalUtil.modelToDate(dateModel, BuiltInsForMultipleTypes.stringBI.this.target).getClass(), BuiltInsForMultipleTypes.stringBI.this.target, true);
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List<String> args) throws TemplateModelException {
/* 632 */         BuiltInsForMultipleTypes.stringBI.this.checkMethodArgCount(args, 1);
/* 633 */         return formatWith(args.get(0));
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public TemplateModel get(String key) throws TemplateModelException {
/* 639 */         return formatWith(key);
/*     */       }
/*     */ 
/*     */       
/*     */       private TemplateModel formatWith(String key) throws TemplateModelException {
/*     */         try {
/* 645 */           return (TemplateModel)new SimpleScalar(this.env.formatDateToPlainText(this.dateModel, key, BuiltInsForMultipleTypes.stringBI.this.target, BuiltInsForMultipleTypes.stringBI.this, true));
/* 646 */         } catch (TemplateException e) {
/*     */           
/* 648 */           throw _CoreAPI.ensureIsTemplateModelException("Failed to format value", e);
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public String getAsString() throws TemplateModelException {
/* 655 */         if (this.cachedValue == null) {
/* 656 */           if (this.defaultFormat == null) {
/* 657 */             if (this.dateModel.getDateType() == 0) {
/* 658 */               throw _MessageUtil.newCantFormatUnknownTypeDateException(BuiltInsForMultipleTypes.stringBI.this.target, null);
/*     */             }
/* 660 */             throw new BugException();
/*     */           } 
/*     */           
/*     */           try {
/* 664 */             this.cachedValue = EvalUtil.assertFormatResultNotNull(this.defaultFormat.formatToPlainText(this.dateModel));
/* 665 */           } catch (TemplateValueFormatException e) {
/*     */             try {
/* 667 */               throw _MessageUtil.newCantFormatDateException(this.defaultFormat, BuiltInsForMultipleTypes.stringBI.this.target, e, true);
/* 668 */             } catch (TemplateException e2) {
/*     */               
/* 670 */               throw _CoreAPI.ensureIsTemplateModelException("Failed to format date/time/datetime", e2);
/*     */             } 
/*     */           } 
/*     */         } 
/* 674 */         return this.cachedValue;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isEmpty() {
/* 679 */         return false;
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private class NumberFormatter
/*     */       implements TemplateScalarModel, TemplateHashModel, TemplateMethodModel
/*     */     {
/*     */       private final TemplateNumberModel numberModel;
/*     */       
/*     */       private final Number number;
/*     */       private final Environment env;
/*     */       private final TemplateNumberFormat defaultFormat;
/*     */       private String cachedValue;
/*     */       
/*     */       NumberFormatter(TemplateNumberModel numberModel, Environment env) throws TemplateException {
/* 695 */         this.env = env;
/*     */ 
/*     */         
/* 698 */         this.numberModel = numberModel;
/* 699 */         this.number = EvalUtil.modelToNumber(numberModel, BuiltInsForMultipleTypes.stringBI.this.target);
/*     */         try {
/* 701 */           this.defaultFormat = env.getTemplateNumberFormat(BuiltInsForMultipleTypes.stringBI.this, true);
/* 702 */         } catch (TemplateException e) {
/*     */           
/* 704 */           throw _CoreAPI.ensureIsTemplateModelException("Failed to get default number format", e);
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List<String> args) throws TemplateModelException {
/* 710 */         BuiltInsForMultipleTypes.stringBI.this.checkMethodArgCount(args, 1);
/* 711 */         return get(args.get(0));
/*     */       }
/*     */       
/*     */       public TemplateModel get(String key) throws TemplateModelException {
/*     */         TemplateNumberFormat format;
/*     */         String result;
/*     */         try {
/* 718 */           format = this.env.getTemplateNumberFormat(key, BuiltInsForMultipleTypes.stringBI.this, true);
/* 719 */         } catch (TemplateException e) {
/*     */           
/* 721 */           throw _CoreAPI.ensureIsTemplateModelException("Failed to get number format", e);
/*     */         } 
/*     */ 
/*     */         
/*     */         try {
/* 726 */           if (format instanceof BackwardCompatibleTemplateNumberFormat) {
/* 727 */             result = this.env.formatNumberToPlainText(this.number, (BackwardCompatibleTemplateNumberFormat)format, BuiltInsForMultipleTypes.stringBI.this.target);
/*     */           } else {
/* 729 */             result = this.env.formatNumberToPlainText(this.numberModel, format, BuiltInsForMultipleTypes.stringBI.this.target, true);
/*     */           } 
/* 731 */         } catch (TemplateException e) {
/*     */           
/* 733 */           throw _CoreAPI.ensureIsTemplateModelException("Failed to format number", e);
/*     */         } 
/*     */         
/* 736 */         return (TemplateModel)new SimpleScalar(result);
/*     */       }
/*     */ 
/*     */       
/*     */       public String getAsString() throws TemplateModelException {
/* 741 */         if (this.cachedValue == null) {
/*     */           try {
/* 743 */             if (this.defaultFormat instanceof BackwardCompatibleTemplateNumberFormat) {
/* 744 */               this.cachedValue = this.env.formatNumberToPlainText(this.number, (BackwardCompatibleTemplateNumberFormat)this.defaultFormat, BuiltInsForMultipleTypes.stringBI.this.target);
/*     */             } else {
/*     */               
/* 747 */               this.cachedValue = this.env.formatNumberToPlainText(this.numberModel, this.defaultFormat, BuiltInsForMultipleTypes.stringBI.this.target, true);
/*     */             } 
/* 749 */           } catch (TemplateException e) {
/*     */             
/* 751 */             throw _CoreAPI.ensureIsTemplateModelException("Failed to format number", e);
/*     */           } 
/*     */         }
/* 754 */         return this.cachedValue;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isEmpty() {
/* 759 */         return false;
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 765 */       TemplateModel model = this.target.eval(env);
/* 766 */       if (model instanceof TemplateNumberModel)
/* 767 */         return (TemplateModel)new NumberFormatter((TemplateNumberModel)model, env); 
/* 768 */       if (model instanceof TemplateDateModel) {
/* 769 */         TemplateDateModel dm = (TemplateDateModel)model;
/* 770 */         return (TemplateModel)new DateFormatter(dm, env);
/* 771 */       }  if (model instanceof SimpleScalar)
/* 772 */         return model; 
/* 773 */       if (model instanceof TemplateBooleanModel)
/* 774 */         return (TemplateModel)new BooleanFormatter((TemplateBooleanModel)model, env); 
/* 775 */       if (model instanceof TemplateScalarModel)
/* 776 */         return (TemplateModel)new SimpleScalar(((TemplateScalarModel)model).getAsString()); 
/* 777 */       if (env.isClassicCompatible() && model instanceof BeanModel) {
/* 778 */         return (TemplateModel)new SimpleScalar(_BeansAPI.getAsClassicCompatibleString((BeanModel)model));
/*     */       }
/* 780 */       throw new UnexpectedTypeException(this.target, model, "number, date, boolean or string", new Class[] { TemplateNumberModel.class, TemplateDateModel.class, TemplateBooleanModel.class, TemplateScalarModel.class }, env);
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
/*     */   static abstract class AbstractCBI
/*     */     extends BuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 799 */       TemplateModel model = this.target.eval(env);
/* 800 */       if (model instanceof TemplateNumberModel)
/* 801 */         return formatNumber(env, model); 
/* 802 */       if (model instanceof TemplateBooleanModel) {
/* 803 */         return (TemplateModel)new SimpleScalar(((TemplateBooleanModel)model).getAsBoolean() ? "true" : "false");
/*     */       }
/*     */       
/* 806 */       throw new UnexpectedTypeException(this.target, model, "number or boolean", new Class[] { TemplateNumberModel.class, TemplateBooleanModel.class }, env);
/*     */     }
/*     */     
/*     */     protected abstract TemplateModel formatNumber(Environment param1Environment, TemplateModel param1TemplateModel) throws TemplateModelException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForMultipleTypes.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */