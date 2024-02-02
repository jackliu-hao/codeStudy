package cn.hutool.extra.template.engine;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateException;
import cn.hutool.log.StaticLog;
import java.lang.invoke.SerializedLambda;

public class TemplateFactory {
   public static TemplateEngine get() {
      return (TemplateEngine)Singleton.get(TemplateEngine.class.getName(), TemplateFactory::create);
   }

   public static TemplateEngine create() {
      return create(new TemplateConfig());
   }

   public static TemplateEngine create(TemplateConfig config) {
      TemplateEngine engine = doCreate(config);
      StaticLog.debug("Use [{}] Engine As Default.", StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine"));
      return engine;
   }

   private static TemplateEngine doCreate(TemplateConfig config) {
      Class<? extends TemplateEngine> customEngineClass = config.getCustomEngine();
      TemplateEngine engine;
      if (null != customEngineClass) {
         engine = (TemplateEngine)ReflectUtil.newInstance(customEngineClass);
      } else {
         engine = (TemplateEngine)ServiceLoaderUtil.loadFirstAvailable(TemplateEngine.class);
      }

      if (null != engine) {
         return engine.init(config);
      } else {
         throw new TemplateException("No template found ! Please add one of template jar to your project !");
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "create":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/template/engine/TemplateFactory") && lambda.getImplMethodSignature().equals("()Lcn/hutool/extra/template/TemplateEngine;")) {
               return TemplateFactory::create;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
