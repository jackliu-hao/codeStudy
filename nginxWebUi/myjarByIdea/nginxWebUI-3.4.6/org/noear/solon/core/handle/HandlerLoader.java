package org.noear.solon.core.handle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.noear.solon.Utils;
import org.noear.solon.annotation.After;
import org.noear.solon.annotation.Before;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Options;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.ext.ConsumerEx;

public class HandlerLoader extends HandlerAide {
   protected BeanWrap bw;
   protected Render bRender;
   protected Mapping bMapping;
   protected String bPath;
   protected boolean bRemoting;
   protected boolean allowMapping;

   public HandlerLoader(BeanWrap wrap) {
      this.bMapping = (Mapping)wrap.clz().getAnnotation(Mapping.class);
      if (this.bMapping == null) {
         this.initDo(wrap, (String)null, wrap.remoting(), (Render)null, true);
      } else {
         String bPath = Utils.annoAlias(this.bMapping.value(), this.bMapping.path());
         this.initDo(wrap, bPath, wrap.remoting(), (Render)null, true);
      }

   }

   public HandlerLoader(BeanWrap wrap, String mapping) {
      this.initDo(wrap, mapping, wrap.remoting(), (Render)null, true);
   }

   public HandlerLoader(BeanWrap wrap, String mapping, boolean remoting) {
      this.initDo(wrap, mapping, remoting, (Render)null, true);
   }

   public HandlerLoader(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
      this.initDo(wrap, mapping, remoting, render, allowMapping);
   }

   private void initDo(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
      this.bw = wrap;
      this.bRender = render;
      this.allowMapping = allowMapping;
      if (mapping != null) {
         this.bPath = mapping;
      }

      this.bRemoting = remoting;
   }

   public String mapping() {
      return this.bPath;
   }

   public void load(HandlerSlots slots) {
      this.load(this.bRemoting, slots);
   }

   public void load(boolean all, HandlerSlots slots) {
      if (Handler.class.isAssignableFrom(this.bw.clz())) {
         this.loadHandlerDo(slots);
      } else {
         this.loadActionDo(slots, all || this.bRemoting);
      }

   }

   protected void loadHandlerDo(HandlerSlots slots) {
      if (this.bMapping == null) {
         throw new IllegalStateException(this.bw.clz().getName() + " No @Mapping!");
      } else {
         Handler handler = (Handler)this.bw.raw();
         Set<MethodType> v0 = MethodTypeUtil.findAndFill(new HashSet(), (t) -> {
            return this.bw.annotationGet(t) != null;
         });
         if (((Set)v0).size() == 0) {
            v0 = new HashSet(Arrays.asList(this.bMapping.method()));
         }

         slots.add((Mapping)this.bMapping, (Set)v0, handler);
      }
   }

   protected void loadActionDo(HandlerSlots slots, boolean all) {
      if (this.bPath == null) {
         this.bPath = "";
      }

      Set<MethodType> b_method = new HashSet();
      this.loadControllerAide(b_method);
      int m_index = false;
      Method[] var8 = this.bw.clz().getDeclaredMethods();
      int var9 = var8.length;

      label71:
      for(int var10 = 0; var10 < var9; ++var10) {
         Method method = var8[var10];
         Mapping m_map = (Mapping)method.getAnnotation(Mapping.class);
         int m_index = 0;
         Set<MethodType> m_method = new HashSet();
         MethodTypeUtil.findAndFill(m_method, (t) -> {
            return method.getAnnotation(t) != null;
         });
         String m_path;
         if (m_map != null) {
            m_path = Utils.annoAlias(m_map.value(), m_map.path());
            if (m_method.size() == 0) {
               m_method.addAll(Arrays.asList(m_map.method()));
            }

            m_index = m_map.index();
         } else {
            m_path = method.getName();
            if (m_method.size() == 0) {
               MethodTypeUtil.findAndFill(m_method, (t) -> {
                  return this.bw.clz().getAnnotation(t) != null;
               });
            }

            if (m_method.size() == 0) {
               if (this.bMapping == null) {
                  m_method.add(MethodType.HTTP);
               } else {
                  m_method.addAll(Arrays.asList(this.bMapping.method()));
               }
            }
         }

         if (m_map != null || all) {
            String newPath = PathUtil.mergePath(this.bPath, m_path);
            Action action = this.createAction(this.bw, method, m_map, newPath, this.bRemoting);
            this.loadActionAide(method, action, m_method);
            if (b_method.size() > 0 && !m_method.contains(MethodType.HTTP) && !m_method.contains(MethodType.ALL)) {
               m_method.addAll(b_method);
            }

            Iterator var14 = m_method.iterator();

            while(true) {
               while(true) {
                  if (!var14.hasNext()) {
                     continue label71;
                  }

                  MethodType m1 = (MethodType)var14.next();
                  if (m_map == null) {
                     slots.add((String)newPath, (MethodType)m1, action);
                  } else if (!m_map.after() && !m_map.before()) {
                     slots.add((String)newPath, (MethodType)m1, action);
                  } else if (m_map.after()) {
                     slots.after(newPath, m1, m_index, action);
                  } else {
                     slots.before(newPath, m1, m_index, action);
                  }
               }
            }
         }
      }

   }

   protected void loadControllerAide(Set<MethodType> methodSet) {
      Annotation[] var2 = this.bw.clz().getAnnotations();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation anno = var2[var4];
         if (anno instanceof Before) {
            addDo(((Before)anno).value(), (b) -> {
               this.before((Handler)Aop.getOrNew(b));
            });
         } else if (anno instanceof After) {
            addDo(((After)anno).value(), (f) -> {
               this.after((Handler)Aop.getOrNew(f));
            });
         } else {
            Annotation[] var6 = anno.annotationType().getAnnotations();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Annotation anno2 = var6[var8];
               if (anno2 instanceof Before) {
                  addDo(((Before)anno2).value(), (b) -> {
                     this.before((Handler)Aop.getOrNew(b));
                  });
               } else if (anno2 instanceof After) {
                  addDo(((After)anno2).value(), (f) -> {
                     this.after((Handler)Aop.getOrNew(f));
                  });
               } else if (anno2 instanceof Options) {
                  methodSet.add(MethodType.OPTIONS);
               }
            }
         }
      }

   }

   protected void loadActionAide(Method method, Action action, Set<MethodType> methodSet) {
      Annotation[] var4 = method.getAnnotations();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Annotation anno = var4[var6];
         if (anno instanceof Before) {
            addDo(((Before)anno).value(), (b) -> {
               action.before((Handler)Aop.getOrNew(b));
            });
         } else if (anno instanceof After) {
            addDo(((After)anno).value(), (f) -> {
               action.after((Handler)Aop.getOrNew(f));
            });
         } else {
            Annotation[] var8 = anno.annotationType().getAnnotations();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Annotation anno2 = var8[var10];
               if (anno2 instanceof Before) {
                  addDo(((Before)anno2).value(), (b) -> {
                     action.before((Handler)Aop.getOrNew(b));
                  });
               } else if (anno2 instanceof After) {
                  addDo(((After)anno2).value(), (f) -> {
                     action.after((Handler)Aop.getOrNew(f));
                  });
               } else if (anno2 instanceof Options && !methodSet.contains(MethodType.HTTP) && !methodSet.contains(MethodType.ALL)) {
                  methodSet.add(MethodType.OPTIONS);
               }
            }
         }
      }

   }

   protected Action createAction(BeanWrap bw, Method method, Mapping mp, String path, boolean remoting) {
      return this.allowMapping ? new Action(bw, this, method, mp, path, remoting, this.bRender) : new Action(bw, this, method, (Mapping)null, path, remoting, this.bRender);
   }

   private static <T> void addDo(T[] ary, ConsumerEx<T> fun) {
      if (ary != null) {
         Object[] var2 = ary;
         int var3 = ary.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            T t = var2[var4];

            try {
               fun.accept(t);
            } catch (RuntimeException var7) {
               throw var7;
            } catch (Throwable var8) {
               throw new RuntimeException(var8);
            }
         }
      }

   }
}
