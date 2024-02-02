package org.noear.solon.core.handle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Consumes;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.ext.DataThrowable;

public class Action extends HandlerAide implements Handler {
   private final BeanWrap bWrap;
   private final HandlerAide bAide;
   private Render bRender;
   private final boolean mIsMain;
   private final MethodWrap mWrap;
   private String mProduces;
   private String mConsumes;
   private final String mName;
   private final String mFullName;
   private final boolean mRemoting;
   private final Mapping mMapping;
   private boolean mMultipart;
   private PathAnalyzer pathAnalyzer;
   private List<String> pathKeys;

   public Action(BeanWrap bWrap, Method method) {
      this(bWrap, (HandlerAide)null, method, (Mapping)null, (String)null, false, (Render)null);
   }

   public Action(BeanWrap bWrap, HandlerAide bAide, Method method, Mapping mapping, String path, boolean remoting, Render render) {
      this.bWrap = bWrap;
      this.bAide = bAide;
      method.setAccessible(true);
      this.mWrap = bWrap.context().methodGet(method);
      this.mRemoting = remoting;
      this.mMapping = mapping;
      this.bRender = render;
      if (this.bRender == null && Render.class.isAssignableFrom(bWrap.clz())) {
         this.bRender = (Render)bWrap.raw();
      }

      if (mapping == null) {
         this.mName = method.getName();
         this.mIsMain = true;
      } else {
         Produces producesAnno = (Produces)method.getAnnotation(Produces.class);
         Consumes consumesAnno = (Consumes)method.getAnnotation(Consumes.class);
         if (producesAnno == null) {
            this.mProduces = mapping.produces();
         } else {
            this.mProduces = producesAnno.value();
         }

         if (consumesAnno == null) {
            this.mConsumes = mapping.consumes();
         } else {
            this.mConsumes = consumesAnno.value();
         }

         this.mMultipart = mapping.multipart();
         this.mName = Utils.annoAlias(mapping.value(), mapping.path());
         this.mIsMain = !mapping.after() && !mapping.before();
      }

      if (Utils.isEmpty(path)) {
         this.mFullName = this.mName;
      } else if (path.startsWith("/")) {
         this.mFullName = path.substring(1);
      } else {
         this.mFullName = path;
      }

      if (!this.mMultipart) {
         Class[] var12 = method.getParameterTypes();
         int var14 = var12.length;

         for(int var10 = 0; var10 < var14; ++var10) {
            Class<?> clz = var12[var10];
            if (UploadedFile.class.isAssignableFrom(clz)) {
               this.mMultipart = true;
            }
         }
      }

      if (path != null && path.contains("{")) {
         this.pathKeys = new ArrayList();
         Matcher pm = PathUtil.pathKeyExpr.matcher(path);

         while(pm.find()) {
            this.pathKeys.add(pm.group(1));
         }

         if (this.pathKeys.size() > 0) {
            this.pathAnalyzer = PathAnalyzer.get(path);
         }
      }

   }

   public String name() {
      return this.mName;
   }

   public String fullName() {
      return this.mFullName;
   }

   public Mapping mapping() {
      return this.mMapping;
   }

   public MethodWrap method() {
      return this.mWrap;
   }

   public BeanWrap controller() {
      return this.bWrap;
   }

   public String produces() {
      return this.mProduces;
   }

   public String consumes() {
      return this.mConsumes;
   }

   public void handle(Context x) throws Throwable {
      if (!Utils.isNotEmpty(this.mConsumes) || x.contentType() != null && x.contentType().contains(this.mConsumes)) {
         if (this.mMultipart) {
            x.autoMultipart(true);
         }

         this.invoke(x, (Object)null);
      } else {
         x.status(415);
      }
   }

   public void invoke(Context c, Object obj) throws Throwable {
      c.remotingSet(this.mRemoting);

      try {
         if (obj == null) {
            obj = this.bWrap.get();
         }

         if (this.mIsMain) {
            c.attrSet("controller", obj);
            c.attrSet("action", this);
         }

         this.invoke0(c, obj);
      } catch (Throwable var5) {
         c.setHandled(true);
         Throwable e = Utils.throwableUnwrap(var5);
         if (e instanceof DataThrowable) {
            DataThrowable ex = (DataThrowable)e;
            if (ex.data() == null) {
               this.renderDo(ex, c);
            } else {
               this.renderDo(ex.data(), c);
            }
         } else {
            c.errors = e;
            EventBus.push(e);
            if (c.result == null) {
               this.renderDo(e, c);
            } else {
               this.renderDo(c.result, c);
            }
         }
      }

   }

   protected void invoke0(Context c, Object obj) throws Throwable {
      boolean var11 = false;

      Iterator var14;
      Handler h;
      label325: {
         try {
            var11 = true;
            if (this.mIsMain) {
               if (this.bAide != null) {
                  var14 = this.bAide.befores.iterator();

                  while(var14.hasNext()) {
                     h = (Handler)var14.next();
                     h.handle(c);
                  }
               }

               var14 = this.befores.iterator();

               while(var14.hasNext()) {
                  h = (Handler)var14.next();
                  h.handle(c);
               }
            }

            if (this.mIsMain && c.getHandled()) {
               var11 = false;
            } else {
               if (this.pathAnalyzer != null) {
                  Matcher pm = this.pathAnalyzer.matcher(c.pathNew());
                  if (pm.find()) {
                     int i = 0;

                     for(int len = this.pathKeys.size(); i < len; ++i) {
                        c.paramSet((String)this.pathKeys.get(i), pm.group(i + 1));
                     }
                  }
               }

               Object tmp = this.callDo(c, obj, this.mWrap);
               if (this.mIsMain) {
                  c.result = tmp;
                  if (!Utils.isEmpty(this.mProduces)) {
                     c.contentType(this.mProduces);
                  }

                  this.renderDo(tmp, c);
                  var11 = false;
               } else {
                  var11 = false;
               }
            }
            break label325;
         } catch (Throwable var12) {
            Throwable e = Utils.throwableUnwrap(var12);
            if (!(e instanceof DataThrowable)) {
               c.errors = e;
               throw e;
            }

            DataThrowable ex = (DataThrowable)e;
            if (ex.data() == null) {
               this.renderDo(ex, c);
               var11 = false;
            } else {
               this.renderDo(ex.data(), c);
               var11 = false;
            }
         } finally {
            if (var11) {
               if (this.mIsMain) {
                  Iterator var7;
                  Handler h;
                  if (this.bAide != null) {
                     var7 = this.bAide.afters.iterator();

                     while(var7.hasNext()) {
                        h = (Handler)var7.next();
                        h.handle(c);
                     }
                  }

                  var7 = this.afters.iterator();

                  while(var7.hasNext()) {
                     h = (Handler)var7.next();
                     h.handle(c);
                  }
               }

            }
         }

         if (this.mIsMain) {
            if (this.bAide != null) {
               var14 = this.bAide.afters.iterator();

               while(var14.hasNext()) {
                  h = (Handler)var14.next();
                  h.handle(c);
               }
            }

            var14 = this.afters.iterator();

            while(var14.hasNext()) {
               h = (Handler)var14.next();
               h.handle(c);
            }
         }

         return;
      }

      if (this.mIsMain) {
         if (this.bAide != null) {
            var14 = this.bAide.afters.iterator();

            while(var14.hasNext()) {
               h = (Handler)var14.next();
               h.handle(c);
            }
         }

         var14 = this.afters.iterator();

         while(var14.hasNext()) {
            h = (Handler)var14.next();
            h.handle(c);
         }
      }

   }

   protected Object callDo(Context ctx, Object obj, MethodWrap mWrap) throws Throwable {
      String ct = ctx.contentType();
      if (mWrap.getParamWraps().length > 0) {
         Iterator var5 = Bridge.actionExecutors().iterator();

         while(var5.hasNext()) {
            ActionExecutor me = (ActionExecutor)var5.next();
            if (me.matched(ctx, ct)) {
               return me.execute(ctx, obj, mWrap);
            }
         }
      }

      return Bridge.actionExecutorDef().execute(ctx, obj, mWrap);
   }

   protected void renderDo(Object obj, Context c) throws Throwable {
      if (!c.getRendered()) {
         c.result = obj;
         if (this.bRender == null) {
            if (obj instanceof DataThrowable) {
               return;
            }

            if (obj instanceof Throwable) {
               if (!c.remoting()) {
                  c.setHandled(false);
                  throw (Throwable)obj;
               }

               c.render(obj);
            } else {
               c.render(obj);
            }
         } else {
            this.bRender.render(obj, c);
         }

      }
   }
}
