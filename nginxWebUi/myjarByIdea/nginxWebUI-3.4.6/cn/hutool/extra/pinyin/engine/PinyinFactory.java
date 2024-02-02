package cn.hutool.extra.pinyin.engine;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinEngine;
import cn.hutool.extra.pinyin.PinyinException;
import cn.hutool.log.StaticLog;
import java.lang.invoke.SerializedLambda;

public class PinyinFactory {
   public static PinyinEngine get() {
      return (PinyinEngine)Singleton.get(PinyinEngine.class.getName(), PinyinFactory::create);
   }

   public static PinyinEngine create() {
      PinyinEngine engine = doCreate();
      StaticLog.debug("Use [{}] Engine As Default.", StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine"));
      return engine;
   }

   private static PinyinEngine doCreate() {
      PinyinEngine engine = (PinyinEngine)ServiceLoaderUtil.loadFirstAvailable(PinyinEngine.class);
      if (null != engine) {
         return engine;
      } else {
         throw new PinyinException("No pinyin jar found ! Please add one of it to your project !");
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "create":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/pinyin/engine/PinyinFactory") && lambda.getImplMethodSignature().equals("()Lcn/hutool/extra/pinyin/PinyinEngine;")) {
               return PinyinFactory::create;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
