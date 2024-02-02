package cn.hutool.extra.tokenizer.engine;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerException;
import cn.hutool.log.StaticLog;
import java.lang.invoke.SerializedLambda;

public class TokenizerFactory {
   public static TokenizerEngine get() {
      return (TokenizerEngine)Singleton.get(TokenizerEngine.class.getName(), TokenizerFactory::create);
   }

   public static TokenizerEngine create() {
      TokenizerEngine engine = doCreate();
      StaticLog.debug("Use [{}] Tokenizer Engine As Default.", StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine"));
      return engine;
   }

   private static TokenizerEngine doCreate() {
      TokenizerEngine engine = (TokenizerEngine)ServiceLoaderUtil.loadFirstAvailable(TokenizerEngine.class);
      if (null != engine) {
         return engine;
      } else {
         throw new TokenizerException("No tokenizer found ! Please add some tokenizer jar to your project !");
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "create":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/tokenizer/engine/TokenizerFactory") && lambda.getImplMethodSignature().equals("()Lcn/hutool/extra/tokenizer/TokenizerEngine;")) {
               return TokenizerFactory::create;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
