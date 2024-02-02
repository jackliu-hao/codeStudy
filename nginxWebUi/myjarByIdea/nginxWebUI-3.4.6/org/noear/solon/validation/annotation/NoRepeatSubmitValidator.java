package org.noear.solon.validation.annotation;

import java.io.IOException;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class NoRepeatSubmitValidator implements Validator<NoRepeatSubmit> {
   public static final NoRepeatSubmitValidator instance = new NoRepeatSubmitValidator();
   private NoRepeatSubmitChecker checker;

   public void setChecker(NoRepeatSubmitChecker checker) {
      if (checker != null) {
         this.checker = checker;
      }

   }

   public String message(NoRepeatSubmit anno) {
      return anno.message();
   }

   public Class<?>[] groups(NoRepeatSubmit anno) {
      return anno.groups();
   }

   public Result validateOfContext(Context ctx, NoRepeatSubmit anno, String name, StringBuilder tmp) {
      if (this.checker == null) {
         throw new IllegalArgumentException("Missing NoRepeatSubmitChecker Setting");
      } else {
         tmp.append(ctx.pathNew()).append("#");
         HttpPart[] var5 = anno.value();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            HttpPart part = var5[var7];
            switch (part) {
               case body:
                  try {
                     tmp.append("body:");
                     tmp.append(ctx.bodyNew()).append(";");
                     break;
                  } catch (IOException var10) {
                     throw new RuntimeException(var10);
                  }
               case headers:
                  tmp.append("headers:");
                  ctx.headerMap().forEach((k, v) -> {
                     tmp.append(k).append("=").append(v).append(";");
                  });
                  break;
               default:
                  tmp.append("params:");
                  ctx.paramMap().forEach((k, v) -> {
                     tmp.append(k).append("=").append(v).append(";");
                  });
            }
         }

         if (this.checker.check(anno, ctx, Utils.md5(tmp.toString()), anno.seconds())) {
            return Result.succeed();
         } else {
            return Result.failure();
         }
      }
   }
}
