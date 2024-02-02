package org.noear.solon.core.handle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.RoutingTable;
import org.noear.solon.core.route.RoutingTableDefault;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.ext.DataThrowable;

public abstract class Gateway extends HandlerAide implements Handler, Render {
   private Handler mainDef;
   private final RoutingTable<Handler> mainRouting;
   private final String mapping;
   private Mapping mappingAnno;
   private List<FilterEntity> filterList;

   public Gateway() {
      this(new RoutingTableDefault());
   }

   public Gateway(RoutingTable<Handler> routingTable) {
      this.filterList = new ArrayList();
      this.mainRouting = routingTable;
      this.mappingAnno = (Mapping)this.getClass().getAnnotation(Mapping.class);
      if (this.mappingAnno == null) {
         throw new IllegalStateException("No Mapping!");
      } else {
         this.mapping = Utils.annoAlias(this.mappingAnno.value(), this.mappingAnno.path());
         this.mainDef = (c) -> {
            c.status(404);
         };
         this.filterList.add(new FilterEntity(Integer.MAX_VALUE, this::doFilter));
         this.register();
      }
   }

   @Note("注册相关接口与拦截器")
   protected abstract void register();

   @Note("允许 Action Mapping 申明")
   protected boolean allowActionMapping() {
      return true;
   }

   @Note("充许提前准备控制器")
   protected boolean allowReadyController() {
      return true;
   }

   @Note("充许路径合并")
   protected boolean allowPathMerging() {
      return true;
   }

   public void render(Object obj, Context c) throws Throwable {
      if (!c.getRendered()) {
         if (!(obj instanceof DataThrowable)) {
            c.result = obj;
            c.render(obj);
         }
      }
   }

   public void filter(Filter filter) {
      this.filter(0, filter);
   }

   public void filter(int index, Filter filter) {
      this.filterList.add(new FilterEntity(index, filter));
      this.filterList.sort(Comparator.comparingInt((f) -> {
         return f.index;
      }));
   }

   public void handle(Context c) throws Throwable {
      try {
         (new FilterChainNode(this.filterList)).doFilter(c);
      } catch (Throwable var4) {
         c.setHandled(true);
         Throwable e = Utils.throwableUnwrap(var4);
         if (e instanceof DataThrowable) {
            DataThrowable ex = (DataThrowable)e;
            if (ex.data() == null) {
               this.render(ex, c);
            } else {
               this.render(ex.data(), c);
            }
         } else {
            c.errors = e;
            EventBus.push(e);
            if (c.result == null) {
               this.render(e, c);
            } else {
               this.render(c.result, c);
            }
         }
      }

   }

   protected void doFilter(Context c, FilterChain chain) throws Throwable {
      Handler m = this.find(c);
      Object obj = null;
      if (m != null) {
         Boolean is_action = m instanceof Action;
         if (is_action) {
            if (this.allowReadyController()) {
               obj = ((Action)m).controller().get();
               c.attrSet("controller", obj);
            }

            c.attrSet("action", m);
         }

         this.handle0(c, m, obj, is_action);
      }

   }

   private void handle0(Context c, Handler m, Object obj, Boolean is_action) throws Throwable {
      boolean var12 = false;

      Iterator var15;
      Handler h;
      label148: {
         try {
            var12 = true;
            var15 = this.befores.iterator();

            while(var15.hasNext()) {
               h = (Handler)var15.next();
               h.handle(c);
            }

            if (!c.getHandled()) {
               if (is_action) {
                  ((Action)m).invoke(c, obj);
                  var12 = false;
               } else {
                  m.handle(c);
                  var12 = false;
               }
            } else {
               this.render(c.result, c);
               var12 = false;
            }
            break label148;
         } catch (Throwable var13) {
            Throwable e = Utils.throwableUnwrap(var13);
            if (!(e instanceof DataThrowable)) {
               c.errors = e;
               throw e;
            }

            DataThrowable ex = (DataThrowable)e;
            if (ex.data() == null) {
               this.render(ex, c);
               var12 = false;
            } else {
               this.render(ex.data(), c);
               var12 = false;
            }
         } finally {
            if (var12) {
               Iterator var8 = this.afters.iterator();

               while(var8.hasNext()) {
                  Handler h = (Handler)var8.next();
                  h.handle(c);
               }

            }
         }

         var15 = this.afters.iterator();

         while(var15.hasNext()) {
            h = (Handler)var15.next();
            h.handle(c);
         }

         return;
      }

      var15 = this.afters.iterator();

      while(var15.hasNext()) {
         h = (Handler)var15.next();
         h.handle(c);
      }

   }

   @Note("添加前置拦截器")
   public <T extends Handler> void before(Class<T> interceptorClz) {
      super.before((Handler)Aop.getOrNew(interceptorClz));
   }

   @Note("添加后置拦截器")
   public <T extends Handler> void after(Class<T> interceptorClz) {
      super.after((Handler)Aop.getOrNew(interceptorClz));
   }

   @Note("添加接口")
   public void addBeans(Predicate<BeanWrap> where) {
      this.addBeans(where, false);
   }

   @Note("添加接口")
   public void addBeans(Predicate<BeanWrap> where, boolean remoting) {
      Aop.context().beanOnloaded((ctx) -> {
         ctx.beanForeach((bw) -> {
            if (where.test(bw)) {
               if (remoting) {
                  this.add(bw, remoting);
               } else {
                  this.add(bw);
               }
            }

         });
      });
   }

   @Note("添加接口")
   public void add(Class<?> beanClz) {
      if (beanClz != null) {
         BeanWrap bw = Aop.wrapAndPut(beanClz);
         this.add(bw, bw.remoting());
      }

   }

   @Note("添加接口")
   public void add(String path, Class<?> beanClz) {
      if (beanClz != null) {
         BeanWrap bw = Aop.wrapAndPut(beanClz);
         this.add(path, bw, bw.remoting());
      }

   }

   @Note("添加接口")
   public void add(Class<?> beanClz, boolean remoting) {
      if (beanClz != null) {
         this.add(Aop.wrapAndPut(beanClz), remoting);
      }

   }

   @Note("添加接口")
   public void add(String path, Class<?> beanClz, boolean remoting) {
      if (beanClz != null) {
         this.add(path, Aop.wrapAndPut(beanClz), remoting);
      }

   }

   @Note("添加接口")
   public void add(BeanWrap beanWp) {
      this.add(beanWp, beanWp.remoting());
   }

   @Note("添加接口")
   public void add(String path, BeanWrap beanWp) {
      this.add(path, beanWp, beanWp.remoting());
   }

   @Note("添加接口")
   public void add(BeanWrap beanWp, boolean remoting) {
      this.add((String)null, (BeanWrap)beanWp, remoting);
   }

   @Note("添加接口")
   public void add(String path, BeanWrap beanWp, boolean remoting) {
      if (beanWp != null) {
         Mapping bMapping = (Mapping)beanWp.clz().getAnnotation(Mapping.class);
         String bPath = null;
         if (bMapping != null) {
            bPath = Utils.annoAlias(bMapping.value(), bMapping.path());
         }

         HandlerLoader uw = new HandlerLoader(beanWp, bPath, remoting, this, this.allowActionMapping());
         uw.load((expr, method, handler) -> {
            if (path == null) {
               this.addDo(expr, method, handler);
            } else {
               this.addDo(PathUtil.mergePath(path, expr), method, handler);
            }

         });
      }
   }

   @Note("添加缺少处理")
   public void add(Handler handler) {
      this.addDo("", MethodType.ALL, handler);
   }

   @Note("添加二级路径处理")
   public void add(String path, Handler handler) {
      this.addDo(path, MethodType.ALL, handler);
   }

   @Note("添加二级路径处理")
   public void add(String path, MethodType method, Handler handler) {
      this.addDo(path, method, handler);
   }

   protected void addDo(String path, MethodType method, Handler handler) {
      if (!Utils.isEmpty(path) && !"/".equals(path)) {
         if (this.allowPathMerging()) {
            String path2 = PathUtil.mergePath(this.mapping, path);
            this.mainRouting.add(new RoutingDefault(path2, method, handler));
         } else {
            this.mainRouting.add(new RoutingDefault(path, method, handler));
         }

      } else {
         this.mainDef = handler;
      }
   }

   protected Handler getDo(Context c, String path) {
      if (path == null) {
         return null;
      } else {
         MethodType method = MethodType.valueOf(c.method());
         return (Handler)this.mainRouting.matchOne(path, method);
      }
   }

   protected Handler find(Context c) throws Throwable {
      return this.findDo(c, c.pathNew());
   }

   protected Handler findDo(Context c, String path) throws Throwable {
      Handler h = this.getDo(c, path);
      if (h == null) {
         this.mainDef.handle(c);
         c.setHandled(true);
         return this.mainDef;
      } else {
         if (h instanceof Action) {
            c.attrSet("handler_name", ((Action)h).fullName());
         }

         return h;
      }
   }
}
