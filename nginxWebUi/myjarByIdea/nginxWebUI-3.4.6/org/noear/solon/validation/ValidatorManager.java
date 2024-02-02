package org.noear.solon.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.Aop;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.ext.DataThrowable;
import org.noear.solon.validation.annotation.Date;
import org.noear.solon.validation.annotation.DateValidator;
import org.noear.solon.validation.annotation.DecimalMax;
import org.noear.solon.validation.annotation.DecimalMaxValidator;
import org.noear.solon.validation.annotation.DecimalMin;
import org.noear.solon.validation.annotation.DecimalMinValidator;
import org.noear.solon.validation.annotation.Email;
import org.noear.solon.validation.annotation.EmailValidator;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.LengthValidator;
import org.noear.solon.validation.annotation.Logined;
import org.noear.solon.validation.annotation.LoginedChecker;
import org.noear.solon.validation.annotation.LoginedValidator;
import org.noear.solon.validation.annotation.Max;
import org.noear.solon.validation.annotation.MaxValidator;
import org.noear.solon.validation.annotation.Min;
import org.noear.solon.validation.annotation.MinValidator;
import org.noear.solon.validation.annotation.NoRepeatSubmit;
import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;
import org.noear.solon.validation.annotation.NoRepeatSubmitValidator;
import org.noear.solon.validation.annotation.NotBlacklist;
import org.noear.solon.validation.annotation.NotBlacklistChecker;
import org.noear.solon.validation.annotation.NotBlacklistValidator;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotBlankValidator;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.NotEmptyValidator;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.NotNullValidator;
import org.noear.solon.validation.annotation.NotZero;
import org.noear.solon.validation.annotation.NotZeroValidator;
import org.noear.solon.validation.annotation.Null;
import org.noear.solon.validation.annotation.NullValidator;
import org.noear.solon.validation.annotation.Numeric;
import org.noear.solon.validation.annotation.NumericValidator;
import org.noear.solon.validation.annotation.Pattern;
import org.noear.solon.validation.annotation.PatternValidator;
import org.noear.solon.validation.annotation.Size;
import org.noear.solon.validation.annotation.SizeValidator;
import org.noear.solon.validation.annotation.Validated;
import org.noear.solon.validation.annotation.ValidatedValidator;
import org.noear.solon.validation.annotation.Whitelist;
import org.noear.solon.validation.annotation.WhitelistChecker;
import org.noear.solon.validation.annotation.WhitelistValidator;

public class ValidatorManager {
   private static BeanValidator validator = new BeanValidatorDefault();
   private static final Map<Class<? extends Annotation>, Validator> validMap;
   private static ValidatorFailureHandler failureHandler;

   public static void setNoRepeatSubmitChecker(NoRepeatSubmitChecker checker) {
      NoRepeatSubmitValidator.instance.setChecker(checker);
   }

   public static void setLoginedChecker(LoginedChecker checker) {
      LoginedValidator.instance.setChecker(checker);
   }

   public static void setWhitelistChecker(WhitelistChecker checker) {
      WhitelistValidator.instance.setChecker(checker);
   }

   public static void setNotBlacklistChecker(NotBlacklistChecker checker) {
      NotBlacklistValidator.instance.setChecker(checker);
   }

   public static void setFailureHandler(ValidatorFailureHandler handler) {
      if (handler != null) {
         failureHandler = handler;
      }

   }

   private static void initialize() {
      register(Date.class, DateValidator.instance);
      register(DecimalMax.class, DecimalMaxValidator.instance);
      register(DecimalMin.class, DecimalMinValidator.instance);
      register(Email.class, EmailValidator.instance);
      register(Max.class, MaxValidator.instance);
      register(Min.class, MinValidator.instance);
      register(NoRepeatSubmit.class, NoRepeatSubmitValidator.instance);
      register(NotBlank.class, NotBlankValidator.instance);
      register(NotEmpty.class, NotEmptyValidator.instance);
      register(NotNull.class, NotNullValidator.instance);
      register(NotZero.class, NotZeroValidator.instance);
      register(Null.class, NullValidator.instance);
      register(Numeric.class, NumericValidator.instance);
      register(Pattern.class, PatternValidator.instance);
      register(Length.class, LengthValidator.instance);
      register(Size.class, SizeValidator.instance);
      register(Whitelist.class, WhitelistValidator.instance);
      register(Logined.class, LoginedValidator.instance);
      register(NotBlacklist.class, NotBlacklistValidator.instance);
      register(Validated.class, ValidatedValidator.instance);
   }

   @Note("清除所有验证器")
   public static void clear() {
      validMap.clear();
   }

   @Note("移除某个类型的验证器")
   public static <T extends Annotation> void remove(Class<T> type) {
      validMap.remove(type);
   }

   @Note("注册验证器")
   public static <T extends Annotation> void register(Class<T> type, Validator<T> validator) {
      validMap.put(type, validator);
   }

   @Note("移除某个类型的验证器")
   public static <T extends Annotation> Validator<T> get(Class<T> type) {
      return (Validator)validMap.get(type);
   }

   @Note("执行上下文的验证处理")
   public static void validateOfContext(Context ctx, Action action) throws Throwable {
      StringBuilder tmp = new StringBuilder();
      Annotation[] var3 = action.controller().annotations();
      int var4 = var3.length;

      int var5;
      Annotation anno;
      for(var5 = 0; var5 < var4; ++var5) {
         anno = var3[var5];
         if (validateOfContext0(ctx, anno, (String)null, tmp)) {
            return;
         }
      }

      var3 = action.method().getAnnotations();
      var4 = var3.length;

      for(var5 = 0; var5 < var4; ++var5) {
         anno = var3[var5];
         if (validateOfContext0(ctx, anno, (String)null, tmp)) {
            return;
         }
      }

   }

   private static boolean validateOfContext0(Context ctx, Annotation anno, String name, StringBuilder tmp) {
      if (ctx.getHandled()) {
         return true;
      } else {
         Validator valid = (Validator)validMap.get(anno.annotationType());
         if (valid != null) {
            tmp.setLength(0);
            Result rst = valid.validateOfContext(ctx, anno, name, tmp);
            if (rst.getCode() != Result.SUCCEED_CODE && failureDo(ctx, anno, rst, valid.message(anno))) {
               return true;
            }
         }

         return false;
      }
   }

   @Note("执行参数的验证处理")
   public static void validateOfInvocation(Invocation inv) throws Throwable {
      StringBuilder tmp = new StringBuilder();
      int i = 0;

      for(int len = inv.args().length; i < len; ++i) {
         ParamWrap pw = inv.method().getParamWraps()[i];
         Annotation[] var5 = pw.getParameter().getAnnotations();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Annotation anno = var5[var7];
            validateOfValue0(pw.getName(), anno, inv.args()[i], tmp);
         }
      }

   }

   private static void validateOfValue0(String label, Annotation anno, Object val, StringBuilder tmp) {
      Validator valid = (Validator)validMap.get(anno.annotationType());
      if (valid != null) {
         tmp.setLength(0);
         Result rst = valid.validateOfValue(anno, val, tmp);
         if (rst.getCode() == Result.FAILURE_CODE) {
            String message = null;
            if (Utils.isEmpty(rst.getDescription())) {
               rst.setDescription(label);
            }

            if (rst.getData() instanceof BeanValidateInfo) {
               BeanValidateInfo info = (BeanValidateInfo)rst.getData();
               anno = info.anno;
               message = info.message;
            } else {
               message = valid.message(anno);
            }

            if (failureDo(Context.current(), anno, rst, message)) {
               throw new DataThrowable();
            }

            throw new IllegalArgumentException(rst.getDescription());
         }
      }

   }

   @Note("执行实体的验证处理")
   public static Result validateOfEntity(Object obj, Class<?>[] groups) {
      try {
         if (obj instanceof Collection) {
            return validateOfEntityAry(obj, groups);
         } else {
            return obj instanceof Map ? validateOfEntityMap(obj, groups) : validateOfEntityOne(obj, groups);
         }
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   private static Result validateOfEntityAry(Object obj, Class<?>[] groups) {
      Iterator iterator = ((Collection)obj).iterator();

      while(iterator.hasNext()) {
         Object val2 = iterator.next();
         if (val2 != null) {
            Result rst = validateOfEntity(val2, groups);
            if (rst.getCode() != Result.SUCCEED_CODE) {
               return rst;
            }
         }
      }

      return Result.succeed();
   }

   private static Result validateOfEntityMap(Object obj, Class<?>[] groups) {
      Iterator iterator = ((Map)obj).values().iterator();

      while(iterator.hasNext()) {
         Object val2 = iterator.next();
         if (val2 != null) {
            Result rst = validateOfEntity(val2, groups);
            if (rst.getCode() != Result.SUCCEED_CODE) {
               return rst;
            }
         }
      }

      return Result.succeed();
   }

   private static Result validateOfEntityOne(Object obj, Class<?>[] groups) throws IllegalAccessException {
      if (obj == null) {
         return Result.succeed();
      } else {
         ClassWrap cw = ClassWrap.get(obj.getClass());
         StringBuilder tmp = new StringBuilder();
         Iterator var4 = cw.getFieldAllWraps().entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, FieldWrap> kv = (Map.Entry)var4.next();
            Field field = ((FieldWrap)kv.getValue()).field;
            Annotation[] var7 = ((FieldWrap)kv.getValue()).annoS;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Annotation anno = var7[var9];
               Validator valid = get(anno.annotationType());
               if (valid != null && inGroup(valid.groups(anno), groups)) {
                  tmp.setLength(0);
                  Result rst = valid.validateOfValue(anno, field.get(obj), tmp);
                  if (rst.getCode() != Result.SUCCEED_CODE) {
                     if (Utils.isEmpty(rst.getDescription())) {
                        rst.setDescription(cw.clz().getSimpleName() + "." + field.getName());
                     }

                     if (!(rst.getData() instanceof BeanValidateInfo)) {
                        rst.setData(new BeanValidateInfo(anno, valid.message(anno)));
                     }

                     return rst;
                  }
               }
            }
         }

         return Result.succeed();
      }
   }

   @Note("执行错误处理")
   public static boolean failureDo(Context ctx, Annotation ano, Result result, String message) {
      if (ctx == null) {
         return false;
      } else {
         try {
            return failureHandler.onFailure(ctx, ano, result, message);
         } catch (Throwable var5) {
            Throwable ex = Utils.throwableUnwrap(var5);
            if (ex instanceof RuntimeException) {
               throw (RuntimeException)ex;
            } else {
               throw new RuntimeException(ex);
            }
         }
      }
   }

   private static boolean inGroup(Class<?>[] annoGroups, Class<?>[] groups) {
      if (annoGroups != null && annoGroups.length != 0) {
         if (groups != null && groups.length != 0) {
            Class[] var2 = groups;
            int var3 = groups.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Class<?> g1 = var2[var4];
               Class[] var6 = annoGroups;
               int var7 = annoGroups.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  Class<?> g2 = var6[var8];
                  if (g1 == g2) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   static {
      Aop.getAsyn(BeanValidator.class, (bw) -> {
         validator = (BeanValidator)bw.get();
      });
      validMap = new HashMap();
      failureHandler = new ValidatorFailureHandlerDefault();
      initialize();
   }
}
