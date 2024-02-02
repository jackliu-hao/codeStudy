/*     */ package org.noear.solon.core.handle;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.annotation.After;
/*     */ import org.noear.solon.annotation.Before;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.Aop;
/*     */ import org.noear.solon.core.BeanWrap;
/*     */ import org.noear.solon.core.util.PathUtil;
/*     */ import org.noear.solon.ext.ConsumerEx;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HandlerLoader
/*     */   extends HandlerAide
/*     */ {
/*     */   protected BeanWrap bw;
/*     */   protected Render bRender;
/*     */   protected Mapping bMapping;
/*     */   protected String bPath;
/*     */   protected boolean bRemoting;
/*     */   protected boolean allowMapping;
/*     */   
/*     */   public HandlerLoader(BeanWrap wrap) {
/*  30 */     this.bMapping = (Mapping)wrap.clz().getAnnotation(Mapping.class);
/*     */     
/*  32 */     if (this.bMapping == null) {
/*  33 */       initDo(wrap, (String)null, wrap.remoting(), (Render)null, true);
/*     */     } else {
/*  35 */       String bPath = Utils.annoAlias(this.bMapping.value(), this.bMapping.path());
/*  36 */       initDo(wrap, bPath, wrap.remoting(), (Render)null, true);
/*     */     } 
/*     */   }
/*     */   
/*     */   public HandlerLoader(BeanWrap wrap, String mapping) {
/*  41 */     initDo(wrap, mapping, wrap.remoting(), (Render)null, true);
/*     */   }
/*     */   
/*     */   public HandlerLoader(BeanWrap wrap, String mapping, boolean remoting) {
/*  45 */     initDo(wrap, mapping, remoting, (Render)null, true);
/*     */   }
/*     */   
/*     */   public HandlerLoader(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
/*  49 */     initDo(wrap, mapping, remoting, render, allowMapping);
/*     */   }
/*     */   
/*     */   private void initDo(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
/*  53 */     this.bw = wrap;
/*  54 */     this.bRender = render;
/*  55 */     this.allowMapping = allowMapping;
/*     */     
/*  57 */     if (mapping != null) {
/*  58 */       this.bPath = mapping;
/*     */     }
/*     */     
/*  61 */     this.bRemoting = remoting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String mapping() {
/*  68 */     return this.bPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load(HandlerSlots slots) {
/*  77 */     load(this.bRemoting, slots);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void load(boolean all, HandlerSlots slots) {
/*  87 */     if (Handler.class.isAssignableFrom(this.bw.clz())) {
/*  88 */       loadHandlerDo(slots);
/*     */     } else {
/*  90 */       loadActionDo(slots, (all || this.bRemoting));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void loadHandlerDo(HandlerSlots slots) {
/*  98 */     if (this.bMapping == null) {
/*  99 */       throw new IllegalStateException(this.bw.clz().getName() + " No @Mapping!");
/*     */     }
/*     */     
/* 102 */     Handler handler = (Handler)this.bw.raw();
/* 103 */     Set<MethodType> v0 = MethodTypeUtil.findAndFill(new HashSet<>(), t -> (this.bw.annotationGet(t) != null));
/* 104 */     if (v0.size() == 0) {
/* 105 */       v0 = new HashSet<>(Arrays.asList(this.bMapping.method()));
/*     */     }
/*     */     
/* 108 */     slots.add(this.bMapping, v0, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void loadActionDo(HandlerSlots slots, boolean all) {
/* 118 */     if (this.bPath == null) {
/* 119 */       this.bPath = "";
/*     */     }
/*     */     
/* 122 */     Set<MethodType> b_method = new HashSet<>();
/*     */     
/* 124 */     loadControllerAide(b_method);
/*     */ 
/*     */ 
/*     */     
/* 128 */     int m_index = 0;
/*     */ 
/*     */     
/* 131 */     for (Method method : this.bw.clz().getDeclaredMethods()) {
/* 132 */       String m_path; Mapping m_map = method.<Mapping>getAnnotation(Mapping.class);
/* 133 */       m_index = 0;
/* 134 */       Set<MethodType> m_method = new HashSet<>();
/*     */ 
/*     */       
/* 137 */       MethodTypeUtil.findAndFill(m_method, t -> (method.getAnnotation(t) != null));
/*     */ 
/*     */       
/* 140 */       if (m_map != null) {
/* 141 */         m_path = Utils.annoAlias(m_map.value(), m_map.path());
/*     */         
/* 143 */         if (m_method.size() == 0)
/*     */         {
/* 145 */           m_method.addAll(Arrays.asList(m_map.method()));
/*     */         }
/* 147 */         m_index = m_map.index();
/*     */       } else {
/* 149 */         m_path = method.getName();
/*     */         
/* 151 */         if (m_method.size() == 0)
/*     */         {
/* 153 */           MethodTypeUtil.findAndFill(m_method, t -> (this.bw.clz().getAnnotation(t) != null));
/*     */         }
/*     */         
/* 156 */         if (m_method.size() == 0)
/*     */         {
/* 158 */           if (this.bMapping == null) {
/* 159 */             m_method.add(MethodType.HTTP);
/*     */           } else {
/* 161 */             m_method.addAll(Arrays.asList(this.bMapping.method()));
/*     */           } 
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 167 */       if (m_map != null || all) {
/* 168 */         String newPath = PathUtil.mergePath(this.bPath, m_path);
/*     */         
/* 170 */         Action action = createAction(this.bw, method, m_map, newPath, this.bRemoting);
/*     */ 
/*     */         
/* 173 */         loadActionAide(method, action, m_method);
/* 174 */         if (b_method.size() > 0 && 
/* 175 */           !m_method.contains(MethodType.HTTP) && 
/* 176 */           !m_method.contains(MethodType.ALL))
/*     */         {
/* 178 */           m_method.addAll(b_method);
/*     */         }
/*     */         
/* 181 */         for (MethodType m1 : m_method) {
/* 182 */           if (m_map == null) {
/* 183 */             slots.add(newPath, m1, action); continue;
/*     */           } 
/* 185 */           if (m_map.after() || m_map.before()) {
/* 186 */             if (m_map.after()) {
/* 187 */               slots.after(newPath, m1, m_index, action); continue;
/*     */             } 
/* 189 */             slots.before(newPath, m1, m_index, action);
/*     */             continue;
/*     */           } 
/* 192 */           slots.add(newPath, m1, action);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void loadControllerAide(Set<MethodType> methodSet) {
/* 202 */     for (Annotation anno : this.bw.clz().getAnnotations()) {
/* 203 */       if (anno instanceof Before) {
/* 204 */         addDo((Class<?>[][])((Before)anno).value(), b -> before((Handler)Aop.getOrNew(b)));
/* 205 */       } else if (anno instanceof After) {
/* 206 */         addDo((Class<?>[][])((After)anno).value(), f -> after((Handler)Aop.getOrNew(f)));
/*     */       } else {
/* 208 */         for (Annotation anno2 : anno.annotationType().getAnnotations()) {
/* 209 */           if (anno2 instanceof Before) {
/* 210 */             addDo((Class<?>[][])((Before)anno2).value(), b -> before((Handler)Aop.getOrNew(b)));
/* 211 */           } else if (anno2 instanceof After) {
/* 212 */             addDo((Class<?>[][])((After)anno2).value(), f -> after((Handler)Aop.getOrNew(f)));
/* 213 */           } else if (anno2 instanceof org.noear.solon.annotation.Options) {
/*     */             
/* 215 */             methodSet.add(MethodType.OPTIONS);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void loadActionAide(Method method, Action action, Set<MethodType> methodSet) {
/* 223 */     for (Annotation anno : method.getAnnotations()) {
/* 224 */       if (anno instanceof Before) {
/* 225 */         addDo((Class<?>[][])((Before)anno).value(), b -> action.before((Handler)Aop.getOrNew(b)));
/* 226 */       } else if (anno instanceof After) {
/* 227 */         addDo((Class<?>[][])((After)anno).value(), f -> action.after((Handler)Aop.getOrNew(f)));
/*     */       } else {
/* 229 */         for (Annotation anno2 : anno.annotationType().getAnnotations()) {
/* 230 */           if (anno2 instanceof Before) {
/* 231 */             addDo((Class<?>[][])((Before)anno2).value(), b -> action.before((Handler)Aop.getOrNew(b)));
/* 232 */           } else if (anno2 instanceof After) {
/* 233 */             addDo((Class<?>[][])((After)anno2).value(), f -> action.after((Handler)Aop.getOrNew(f)));
/* 234 */           } else if (anno2 instanceof org.noear.solon.annotation.Options) {
/*     */             
/* 236 */             if (!methodSet.contains(MethodType.HTTP) && 
/* 237 */               !methodSet.contains(MethodType.ALL)) {
/* 238 */               methodSet.add(MethodType.OPTIONS);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Action createAction(BeanWrap bw, Method method, Mapping mp, String path, boolean remoting) {
/* 250 */     if (this.allowMapping) {
/* 251 */       return new Action(bw, this, method, mp, path, remoting, this.bRender);
/*     */     }
/* 253 */     return new Action(bw, this, method, null, path, remoting, this.bRender);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> void addDo(T[] ary, ConsumerEx<T> fun) {
/* 261 */     if (ary != null)
/* 262 */       for (T t : ary) {
/*     */         try {
/* 264 */           fun.accept(t);
/* 265 */         } catch (RuntimeException ex) {
/* 266 */           throw ex;
/* 267 */         } catch (Throwable ex) {
/* 268 */           throw new RuntimeException(ex);
/*     */         } 
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\HandlerLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */