package cn.hutool.extra.expression.engine;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.expression.ExpressionEngine;
import cn.hutool.extra.expression.ExpressionException;
import cn.hutool.log.StaticLog;
import java.lang.invoke.SerializedLambda;

public class ExpressionFactory {
   public static ExpressionEngine get() {
      return (ExpressionEngine)Singleton.get(ExpressionEngine.class.getName(), ExpressionFactory::create);
   }

   public static ExpressionEngine create() {
      ExpressionEngine engine = doCreate();
      StaticLog.debug("Use [{}] Engine As Default.", StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine"));
      return engine;
   }

   private static ExpressionEngine doCreate() {
      ExpressionEngine engine = (ExpressionEngine)ServiceLoaderUtil.loadFirstAvailable(ExpressionEngine.class);
      if (null != engine) {
         return engine;
      } else {
         throw new ExpressionException("No expression jar found ! Please add one of it to your project !");
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "create":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/expression/engine/ExpressionFactory") && lambda.getImplMethodSignature().equals("()Lcn/hutool/extra/expression/ExpressionEngine;")) {
               return ExpressionFactory::create;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
