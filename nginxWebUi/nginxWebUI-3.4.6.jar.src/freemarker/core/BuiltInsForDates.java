/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.AdapterTemplateModel;
/*     */ import freemarker.template.SimpleDate;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.DateUtil;
/*     */ import freemarker.template.utility.UnrecognizedTimeZoneException;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BuiltInsForDates
/*     */ {
/*     */   static class dateType_if_unknownBI
/*     */     extends BuiltIn
/*     */   {
/*     */     private final int dateType;
/*     */     
/*     */     dateType_if_unknownBI(int dateType) {
/*  49 */       this.dateType = dateType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/*  55 */       TemplateModel model = this.target.eval(env);
/*  56 */       if (model instanceof TemplateDateModel) {
/*  57 */         TemplateDateModel tdm = (TemplateDateModel)model;
/*  58 */         int tdmDateType = tdm.getDateType();
/*  59 */         if (tdmDateType != 0) {
/*  60 */           return (TemplateModel)tdm;
/*     */         }
/*  62 */         return (TemplateModel)new SimpleDate(EvalUtil.modelToDate(tdm, this.target), this.dateType);
/*     */       } 
/*  64 */       throw BuiltInForDate.newNonDateException(env, model, this.target);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
/*  70 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class iso_BI
/*     */     extends AbstractISOBI
/*     */   {
/*     */     class Result
/*     */       implements TemplateMethodModelEx
/*     */     {
/*     */       private final Date date;
/*     */       private final int dateType;
/*     */       private final Environment env;
/*     */       
/*     */       Result(Date date, int dateType, Environment env) {
/*  86 */         this.date = date;
/*  87 */         this.dateType = dateType;
/*  88 */         this.env = env;
/*     */       }
/*     */       
/*     */       public Object exec(List<TemplateModel> args) throws TemplateModelException {
/*     */         TimeZone tzArg;
/*  93 */         BuiltInsForDates.iso_BI.this.checkMethodArgCount(args, 1);
/*     */         
/*  95 */         TemplateModel tzArgTM = args.get(0);
/*     */         
/*     */         Object adaptedObj;
/*  98 */         if (tzArgTM instanceof AdapterTemplateModel && 
/*     */ 
/*     */           
/* 101 */           adaptedObj = ((AdapterTemplateModel)tzArgTM).getAdaptedObject(TimeZone.class) instanceof TimeZone) {
/*     */           
/* 103 */           tzArg = (TimeZone)adaptedObj;
/* 104 */         } else if (tzArgTM instanceof TemplateScalarModel) {
/* 105 */           String tzName = EvalUtil.modelToString((TemplateScalarModel)tzArgTM, null, null);
/*     */           try {
/* 107 */             tzArg = DateUtil.getTimeZone(tzName);
/* 108 */           } catch (UnrecognizedTimeZoneException e) {
/* 109 */             throw new _TemplateModelException(new Object[] { "The time zone string specified for ?", this.this$0.key, "(...) is not recognized as a valid time zone name: ", new _DelayedJQuote(tzName) });
/*     */           }
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 115 */           throw _MessageUtil.newMethodArgUnexpectedTypeException("?" + BuiltInsForDates.iso_BI.this.key, 0, "string or java.util.TimeZone", tzArgTM);
/*     */         } 
/*     */ 
/*     */         
/* 119 */         return new SimpleScalar(DateUtil.dateToISO8601String(this.date, (this.dateType != 1), (this.dateType != 2), BuiltInsForDates.iso_BI.this
/*     */ 
/*     */ 
/*     */               
/* 123 */               .shouldShowOffset(this.date, this.dateType, this.env), BuiltInsForDates.iso_BI.this.accuracy, tzArg, this.env
/*     */ 
/*     */               
/* 126 */               .getISOBuiltInCalendarFactory()));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     iso_BI(Boolean showOffset, int accuracy) {
/* 132 */       super(showOffset, accuracy);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
/* 139 */       checkDateTypeNotUnknown(dateType);
/* 140 */       return (TemplateModel)new Result(date, dateType, env);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class iso_utc_or_local_BI
/*     */     extends AbstractISOBI
/*     */   {
/*     */     private final boolean useUTC;
/*     */ 
/*     */ 
/*     */     
/*     */     iso_utc_or_local_BI(Boolean showOffset, int accuracy, boolean useUTC) {
/* 154 */       super(showOffset, accuracy);
/* 155 */       this.useUTC = useUTC;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected TemplateModel calculateResult(Date date, int dateType, Environment env) throws TemplateException {
/* 162 */       checkDateTypeNotUnknown(dateType);
/* 163 */       return (TemplateModel)new SimpleScalar(DateUtil.dateToISO8601String(date, (dateType != 1), (dateType != 2), 
/*     */ 
/*     */ 
/*     */             
/* 167 */             shouldShowOffset(date, dateType, env), this.accuracy, this.useUTC ? DateUtil.UTC : (
/*     */ 
/*     */ 
/*     */             
/* 171 */             env.shouldUseSQLDTTZ(date.getClass()) ? env
/* 172 */             .getSQLDateAndTimeTimeZone() : env
/* 173 */             .getTimeZone()), env
/* 174 */             .getISOBuiltInCalendarFactory()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static abstract class AbstractISOBI
/*     */     extends BuiltInForDate
/*     */   {
/*     */     protected final Boolean showOffset;
/*     */     
/*     */     protected final int accuracy;
/*     */     
/*     */     protected AbstractISOBI(Boolean showOffset, int accuracy) {
/* 187 */       this.showOffset = showOffset;
/* 188 */       this.accuracy = accuracy;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void checkDateTypeNotUnknown(int dateType) throws TemplateException {
/* 193 */       if (dateType == 0) {
/* 194 */         throw new _MiscTemplateException((new _ErrorDescriptionBuilder(new Object[] { "The value of the following has unknown date type, but ?", this.key, " needs a value where it's known if it's a date (no time part), time, or date-time value:"
/*     */ 
/*     */               
/* 197 */               })).blame(this.target).tip("Use ?date, ?time, or ?datetime to tell FreeMarker the exact type."));
/*     */       }
/*     */     }
/*     */     
/*     */     protected boolean shouldShowOffset(Date date, int dateType, Environment env) {
/* 202 */       if (dateType == 2)
/* 203 */         return false; 
/* 204 */       if (this.showOffset != null) {
/* 205 */         return this.showOffset.booleanValue();
/*     */       }
/*     */       
/* 208 */       return (!(date instanceof java.sql.Time) || 
/* 209 */         _TemplateAPI.getTemplateLanguageVersionAsInt(this) < _TemplateAPI.VERSION_INT_2_3_21);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForDates.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */