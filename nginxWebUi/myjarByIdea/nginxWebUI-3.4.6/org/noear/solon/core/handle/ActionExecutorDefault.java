package org.noear.solon.core.handle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

public class ActionExecutorDefault implements ActionExecutor {
   public boolean matched(Context ctx, String ct) {
      return true;
   }

   public Object execute(Context ctx, Object obj, MethodWrap mWrap) throws Throwable {
      List<Object> args = this.buildArgs(ctx, mWrap.getParamWraps());
      return mWrap.invokeByAspect(obj, args.toArray());
   }

   protected List<Object> buildArgs(Context ctx, ParamWrap[] pSet) throws Exception {
      List<Object> args = new ArrayList(pSet.length);
      Object bodyObj = this.changeBody(ctx);
      int i = 0;

      for(int len = pSet.length; i < len; ++i) {
         ParamWrap p = pSet[i];
         Class<?> pt = p.getType();
         if (Context.class.isAssignableFrom(pt)) {
            args.add(ctx);
         } else if (ModelAndView.class.isAssignableFrom(pt)) {
            args.add(new ModelAndView());
         } else if (Locale.class.isAssignableFrom(pt)) {
            args.add(ctx.getLocale());
         } else if (UploadedFile.class == pt) {
            args.add(ctx.file(p.getName()));
         } else if (pt.getTypeName().equals("javax.servlet.http.HttpServletRequest")) {
            args.add(ctx.request());
         } else if (pt.getTypeName().equals("javax.servlet.http.HttpServletResponse")) {
            args.add(ctx.response());
         } else {
            Object tv = null;
            if (p.requireBody()) {
               if (String.class.equals(pt)) {
                  tv = ctx.bodyNew();
               } else if (InputStream.class.equals(pt)) {
                  tv = ctx.bodyAsStream();
               }
            }

            if (tv == null) {
               tv = this.changeValue(ctx, p, i, pt, bodyObj);
            }

            if (tv == null && pt.isPrimitive()) {
               if (pt == Short.TYPE) {
                  tv = Short.valueOf((short)0);
               } else if (pt == Integer.TYPE) {
                  tv = 0;
               } else if (pt == Long.TYPE) {
                  tv = 0L;
               } else if (pt == Double.TYPE) {
                  tv = 0.0;
               } else if (pt == Float.TYPE) {
                  tv = 0.0F;
               } else {
                  if (pt != Boolean.TYPE) {
                     throw new IllegalArgumentException("Please enter a valid parameter @" + p.getName());
                  }

                  tv = false;
               }
            }

            if (tv == null && p.required()) {
               ctx.status(400);
               throw new IllegalArgumentException("Required parameter @" + p.getName());
            }

            args.add(tv);
         }
      }

      return args;
   }

   protected Object changeBody(Context ctx) throws Exception {
      return null;
   }

   protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
      String pn = p.getName();
      String pv = null;
      Object tv = null;
      if (p.requireHeader()) {
         pv = ctx.header(pn);
      } else {
         pv = ctx.param(pn);
      }

      if (pv == null) {
         pv = p.defaultValue();
      }

      if (pv == null) {
         if (UploadedFile.class == pt) {
            tv = ctx.file(pn);
         } else if (pn.startsWith("$")) {
            tv = ctx.attr(pn);
         } else if (!pt.getName().startsWith("java.") && !pt.isArray() && !pt.isPrimitive()) {
            tv = this.changeEntityDo(ctx, pn, pt);
         } else {
            tv = null;
         }
      } else {
         tv = ConvertUtil.to(p.getParameter(), pt, pn, pv, ctx);
      }

      return tv;
   }

   private Object changeEntityDo(Context ctx, String name, Class<?> type) throws Exception {
      ClassWrap clzW = ClassWrap.get(type);
      Map<String, String> map = ctx.paramMap();
      return clzW.newBy(map::get, ctx);
   }
}
