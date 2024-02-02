/*     */ package org.noear.solon.validation;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.annotation.Note;
/*     */ import org.noear.solon.core.BeanWrap;
/*     */ import org.noear.solon.core.aspect.Invocation;
/*     */ import org.noear.solon.core.handle.Action;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.Result;
/*     */ import org.noear.solon.core.wrap.ClassWrap;
/*     */ import org.noear.solon.core.wrap.FieldWrap;
/*     */ import org.noear.solon.core.wrap.ParamWrap;
/*     */ import org.noear.solon.validation.annotation.Date;
/*     */ import org.noear.solon.validation.annotation.DecimalMax;
/*     */ import org.noear.solon.validation.annotation.DecimalMin;
/*     */ import org.noear.solon.validation.annotation.Length;
/*     */ import org.noear.solon.validation.annotation.LoginedValidator;
/*     */ import org.noear.solon.validation.annotation.Max;
/*     */ import org.noear.solon.validation.annotation.Min;
/*     */ import org.noear.solon.validation.annotation.NoRepeatSubmit;
/*     */ import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;
/*     */ import org.noear.solon.validation.annotation.NotBlacklist;
/*     */ import org.noear.solon.validation.annotation.NotBlacklistChecker;
/*     */ import org.noear.solon.validation.annotation.NotBlank;
/*     */ import org.noear.solon.validation.annotation.NotNull;
/*     */ import org.noear.solon.validation.annotation.NotZero;
/*     */ import org.noear.solon.validation.annotation.Pattern;
/*     */ import org.noear.solon.validation.annotation.Whitelist;
/*     */ import org.noear.solon.validation.annotation.WhitelistChecker;
/*     */ 
/*     */ public class ValidatorManager {
/*  35 */   private static BeanValidator validator = new BeanValidatorDefault();
/*     */   static {
/*  37 */     Aop.getAsyn(BeanValidator.class, bw -> validator = (BeanValidator)bw.get());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setNoRepeatSubmitChecker(NoRepeatSubmitChecker checker) {
/*  46 */     NoRepeatSubmitValidator.instance.setChecker(checker);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setLoginedChecker(LoginedChecker checker) {
/*  53 */     LoginedValidator.instance.setChecker(checker);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setWhitelistChecker(WhitelistChecker checker) {
/*  60 */     WhitelistValidator.instance.setChecker(checker);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setNotBlacklistChecker(NotBlacklistChecker checker) {
/*  67 */     NotBlacklistValidator.instance.setChecker(checker);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setFailureHandler(ValidatorFailureHandler handler) {
/*  74 */     if (handler != null) {
/*  75 */       failureHandler = handler;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*  80 */   private static final Map<Class<? extends Annotation>, Validator> validMap = new HashMap<>();
/*  81 */   private static ValidatorFailureHandler failureHandler = new ValidatorFailureHandlerDefault();
/*     */   
/*     */   static {
/*  84 */     initialize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void initialize() {
/*  91 */     register(Date.class, (Validator<Date>)DateValidator.instance);
/*     */     
/*  93 */     register(DecimalMax.class, (Validator<DecimalMax>)DecimalMaxValidator.instance);
/*  94 */     register(DecimalMin.class, (Validator<DecimalMin>)DecimalMinValidator.instance);
/*     */     
/*  96 */     register(Email.class, (Validator<Email>)EmailValidator.instance);
/*     */     
/*  98 */     register(Max.class, (Validator<Max>)MaxValidator.instance);
/*  99 */     register(Min.class, (Validator<Min>)MinValidator.instance);
/*     */     
/* 101 */     register(NoRepeatSubmit.class, (Validator<NoRepeatSubmit>)NoRepeatSubmitValidator.instance);
/*     */     
/* 103 */     register(NotBlank.class, (Validator<NotBlank>)NotBlankValidator.instance);
/* 104 */     register(NotEmpty.class, (Validator<NotEmpty>)NotEmptyValidator.instance);
/* 105 */     register(NotNull.class, (Validator<NotNull>)NotNullValidator.instance);
/* 106 */     register(NotZero.class, (Validator<NotZero>)NotZeroValidator.instance);
/*     */     
/* 108 */     register(Null.class, (Validator<Null>)NullValidator.instance);
/* 109 */     register(Numeric.class, (Validator<Numeric>)NumericValidator.instance);
/*     */     
/* 111 */     register(Pattern.class, (Validator<Pattern>)PatternValidator.instance);
/* 112 */     register(Length.class, (Validator<Length>)LengthValidator.instance);
/* 113 */     register(Size.class, (Validator<Size>)SizeValidator.instance);
/*     */     
/* 115 */     register(Whitelist.class, (Validator<Whitelist>)WhitelistValidator.instance);
/* 116 */     register(Logined.class, (Validator<Logined>)LoginedValidator.instance);
/*     */     
/* 118 */     register(NotBlacklist.class, (Validator<NotBlacklist>)NotBlacklistValidator.instance);
/*     */     
/* 120 */     register(Validated.class, (Validator<Validated>)ValidatedValidator.instance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("清除所有验证器")
/*     */   public static void clear() {
/* 128 */     validMap.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("移除某个类型的验证器")
/*     */   public static <T extends Annotation> void remove(Class<T> type) {
/* 136 */     validMap.remove(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("注册验证器")
/*     */   public static <T extends Annotation> void register(Class<T> type, Validator<T> validator) {
/* 144 */     validMap.put(type, validator);
/*     */   }
/*     */   
/*     */   @Note("移除某个类型的验证器")
/*     */   public static <T extends Annotation> Validator<T> get(Class<T> type) {
/* 149 */     return validMap.get(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("执行上下文的验证处理")
/*     */   public static void validateOfContext(Context ctx, Action action) throws Throwable {
/* 157 */     StringBuilder tmp = new StringBuilder();
/*     */     
/* 159 */     for (Annotation anno : action.controller().annotations()) {
/* 160 */       if (validateOfContext0(ctx, anno, null, tmp)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 165 */     for (Annotation anno : action.method().getAnnotations()) {
/* 166 */       if (validateOfContext0(ctx, anno, null, tmp)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean validateOfContext0(Context ctx, Annotation anno, String name, StringBuilder tmp) {
/* 173 */     if (ctx.getHandled()) {
/* 174 */       return true;
/*     */     }
/*     */     
/* 177 */     Validator<Annotation> valid = validMap.get(anno.annotationType());
/*     */     
/* 179 */     if (valid != null) {
/* 180 */       tmp.setLength(0);
/* 181 */       Result rst = valid.validateOfContext(ctx, anno, name, tmp);
/*     */       
/* 183 */       if (rst.getCode() != Result.SUCCEED_CODE && 
/* 184 */         failureDo(ctx, anno, rst, valid.message(anno))) {
/* 185 */         return true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 190 */     return false;
/*     */   }
/*     */   
/*     */   @Note("执行参数的验证处理")
/*     */   public static void validateOfInvocation(Invocation inv) throws Throwable {
/* 195 */     StringBuilder tmp = new StringBuilder();
/*     */     
/* 197 */     for (int i = 0, len = (inv.args()).length; i < len; i++) {
/* 198 */       ParamWrap pw = inv.method().getParamWraps()[i];
/*     */       
/* 200 */       for (Annotation anno : pw.getParameter().getAnnotations())
/*     */       {
/* 202 */         validateOfValue0(pw.getName(), anno, inv.args()[i], tmp);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void validateOfValue0(String label, Annotation anno, Object val, StringBuilder tmp) {
/* 208 */     Validator<Annotation> valid = validMap.get(anno.annotationType());
/*     */     
/* 210 */     if (valid != null) {
/* 211 */       tmp.setLength(0);
/* 212 */       Result rst = valid.validateOfValue(anno, val, tmp);
/*     */       
/* 214 */       if (rst.getCode() == Result.FAILURE_CODE) {
/* 215 */         String message = null;
/*     */         
/* 217 */         if (Utils.isEmpty(rst.getDescription())) {
/* 218 */           rst.setDescription(label);
/*     */         }
/*     */         
/* 221 */         if (rst.getData() instanceof BeanValidateInfo) {
/* 222 */           BeanValidateInfo info = (BeanValidateInfo)rst.getData();
/* 223 */           anno = info.anno;
/* 224 */           message = info.message;
/*     */         } else {
/* 226 */           message = valid.message(anno);
/*     */         } 
/*     */         
/* 229 */         if (failureDo(Context.current(), anno, rst, message)) {
/* 230 */           throw new DataThrowable();
/*     */         }
/* 232 */         throw new IllegalArgumentException(rst.getDescription());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("执行实体的验证处理")
/*     */   public static Result validateOfEntity(Object obj, Class<?>[] groups) {
/*     */     try {
/* 245 */       if (obj instanceof Collection)
/* 246 */         return validateOfEntityAry(obj, groups); 
/* 247 */       if (obj instanceof Map) {
/* 248 */         return validateOfEntityMap(obj, groups);
/*     */       }
/* 250 */       return validateOfEntityOne(obj, groups);
/*     */     }
/* 252 */     catch (IllegalAccessException e) {
/* 253 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Result validateOfEntityAry(Object obj, Class<?>[] groups) {
/* 258 */     Iterator iterator = ((Collection)obj).iterator();
/* 259 */     while (iterator.hasNext()) {
/* 260 */       Object val2 = iterator.next();
/*     */       
/* 262 */       if (val2 != null) {
/* 263 */         Result rst = validateOfEntity(val2, groups);
/*     */         
/* 265 */         if (rst.getCode() != Result.SUCCEED_CODE) {
/* 266 */           return rst;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 271 */     return Result.succeed();
/*     */   }
/*     */   
/*     */   private static Result validateOfEntityMap(Object obj, Class<?>[] groups) {
/* 275 */     Iterator iterator = ((Map)obj).values().iterator();
/* 276 */     while (iterator.hasNext()) {
/* 277 */       Object val2 = iterator.next();
/*     */       
/* 279 */       if (val2 != null) {
/* 280 */         Result rst = validateOfEntity(val2, groups);
/*     */         
/* 282 */         if (rst.getCode() != Result.SUCCEED_CODE) {
/* 283 */           return rst;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 288 */     return Result.succeed();
/*     */   }
/*     */   
/*     */   private static Result validateOfEntityOne(Object obj, Class<?>[] groups) throws IllegalAccessException {
/* 292 */     if (obj == null)
/*     */     {
/* 294 */       return Result.succeed();
/*     */     }
/*     */     
/* 297 */     ClassWrap cw = ClassWrap.get(obj.getClass());
/* 298 */     StringBuilder tmp = new StringBuilder();
/*     */ 
/*     */     
/* 301 */     for (Map.Entry<String, FieldWrap> kv : (Iterable<Map.Entry<String, FieldWrap>>)cw.getFieldAllWraps().entrySet()) {
/* 302 */       Field field = ((FieldWrap)kv.getValue()).field;
/*     */       
/* 304 */       for (Annotation anno : ((FieldWrap)kv.getValue()).annoS) {
/* 305 */         Validator<? extends Annotation> valid = get(anno.annotationType());
/*     */         
/* 307 */         if (valid != null)
/*     */         {
/* 309 */           if (inGroup(valid.groups(anno), groups)) {
/*     */ 
/*     */ 
/*     */             
/* 313 */             tmp.setLength(0);
/* 314 */             Result rst = valid.validateOfValue(anno, field.get(obj), tmp);
/*     */             
/* 316 */             if (rst.getCode() != Result.SUCCEED_CODE) {
/* 317 */               if (Utils.isEmpty(rst.getDescription())) {
/* 318 */                 rst.setDescription(cw.clz().getSimpleName() + "." + field.getName());
/*     */               }
/*     */               
/* 321 */               if (!(rst.getData() instanceof BeanValidateInfo)) {
/* 322 */                 rst.setData(new BeanValidateInfo(anno, valid.message(anno)));
/*     */               }
/*     */               
/* 325 */               return rst;
/*     */             } 
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/* 331 */     return Result.succeed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("执行错误处理")
/*     */   public static boolean failureDo(Context ctx, Annotation ano, Result result, String message) {
/* 340 */     if (ctx == null) {
/* 341 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 345 */       return failureHandler.onFailure(ctx, ano, result, message);
/* 346 */     } catch (Throwable ex) {
/* 347 */       ex = Utils.throwableUnwrap(ex);
/* 348 */       if (ex instanceof RuntimeException) {
/* 349 */         throw (RuntimeException)ex;
/*     */       }
/* 351 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean inGroup(Class<?>[] annoGroups, Class<?>[] groups) {
/* 357 */     if (annoGroups == null || annoGroups.length == 0) {
/* 358 */       return true;
/*     */     }
/* 360 */     if (groups == null || groups.length == 0) {
/* 361 */       return false;
/*     */     }
/* 363 */     for (Class<?> g1 : groups) {
/* 364 */       for (Class<?> g2 : annoGroups) {
/* 365 */         if (g1 == g2) {
/* 366 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 371 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\validation\ValidatorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */