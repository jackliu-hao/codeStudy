package org.noear.solon.serialization.snack3;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

public class SnackJsonActionExecutor extends ActionExecutorDefault {
   private static final String label = "/json";

   public boolean matched(Context ctx, String ct) {
      return ct != null && ct.contains("/json");
   }

   protected Object changeBody(Context ctx) throws Exception {
      String json = ctx.bodyNew();
      return Utils.isNotEmpty(json) ? ONode.loadStr(json) : null;
   }

   protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
      if (ctx.paramMap().containsKey(p.getName())) {
         return super.changeValue(ctx, p, pi, pt, bodyObj);
      } else if (bodyObj == null) {
         return super.changeValue(ctx, p, pi, pt, bodyObj);
      } else {
         ONode tmp = (ONode)bodyObj;
         ParameterizedType gp;
         if (tmp.isObject()) {
            if (tmp.contains(p.getName())) {
               gp = p.getGenericType();
               return gp != null ? tmp.get(p.getName()).toObject(gp) : tmp.get(p.getName()).toObject(pt);
            } else if (ctx.paramMap().containsKey(p.getName())) {
               return super.changeValue(ctx, p, pi, pt, bodyObj);
            } else if (!pt.isPrimitive() && !pt.getTypeName().startsWith("java.lang.")) {
               if (List.class.isAssignableFrom(p.getType())) {
                  return null;
               } else if (p.getType().isArray()) {
                  return null;
               } else {
                  gp = p.getGenericType();
                  return gp != null ? tmp.toObject(gp) : tmp.toObject(pt);
               }
            } else {
               return super.changeValue(ctx, p, pi, pt, bodyObj);
            }
         } else if (tmp.isArray()) {
            if (!Collection.class.isAssignableFrom(pt)) {
               return null;
            } else {
               gp = p.getGenericType();
               return gp != null ? tmp.toObject(gp) : tmp.toObject(p.getType());
            }
         } else {
            return tmp.val().getRaw();
         }
      }
   }
}
