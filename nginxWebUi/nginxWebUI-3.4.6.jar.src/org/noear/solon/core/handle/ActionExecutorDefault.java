/*     */ package org.noear.solon.core.handle;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import org.noear.solon.core.NvMap;
/*     */ import org.noear.solon.core.util.ConvertUtil;
/*     */ import org.noear.solon.core.wrap.ClassWrap;
/*     */ import org.noear.solon.core.wrap.MethodWrap;
/*     */ import org.noear.solon.core.wrap.ParamWrap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ActionExecutorDefault
/*     */   implements ActionExecutor
/*     */ {
/*     */   public boolean matched(Context ctx, String ct) {
/*  30 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object execute(Context ctx, Object obj, MethodWrap mWrap) throws Throwable {
/*  42 */     List<Object> args = buildArgs(ctx, mWrap.getParamWraps());
/*  43 */     return mWrap.invokeByAspect(obj, args.toArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<Object> buildArgs(Context ctx, ParamWrap[] pSet) throws Exception {
/*  51 */     List<Object> args = new ArrayList(pSet.length);
/*     */     
/*  53 */     Object bodyObj = changeBody(ctx);
/*     */ 
/*     */ 
/*     */     
/*  57 */     for (int i = 0, len = pSet.length; i < len; i++) {
/*  58 */       ParamWrap p = pSet[i];
/*  59 */       Class<?> pt = p.getType();
/*     */       
/*  61 */       if (Context.class.isAssignableFrom(pt)) {
/*     */ 
/*     */         
/*  64 */         args.add(ctx);
/*  65 */       } else if (ModelAndView.class.isAssignableFrom(pt)) {
/*     */ 
/*     */         
/*  68 */         args.add(new ModelAndView());
/*  69 */       } else if (Locale.class.isAssignableFrom(pt)) {
/*     */ 
/*     */         
/*  72 */         args.add(ctx.getLocale());
/*  73 */       } else if (UploadedFile.class == pt) {
/*     */ 
/*     */         
/*  76 */         args.add(ctx.file(p.getName()));
/*  77 */       } else if (pt.getTypeName().equals("javax.servlet.http.HttpServletRequest")) {
/*  78 */         args.add(ctx.request());
/*  79 */       } else if (pt.getTypeName().equals("javax.servlet.http.HttpServletResponse")) {
/*  80 */         args.add(ctx.response());
/*     */       } else {
/*  82 */         Object tv = null;
/*     */ 
/*     */         
/*  85 */         if (p.requireBody())
/*     */         {
/*  87 */           if (String.class.equals(pt)) {
/*  88 */             tv = ctx.bodyNew();
/*  89 */           } else if (InputStream.class.equals(pt)) {
/*  90 */             tv = ctx.bodyAsStream();
/*     */           } 
/*     */         }
/*     */         
/*  94 */         if (tv == null)
/*     */         {
/*  96 */           tv = changeValue(ctx, p, i, pt, bodyObj);
/*     */         }
/*     */         
/*  99 */         if (tv == null)
/*     */         {
/*     */ 
/*     */           
/* 103 */           if (pt.isPrimitive())
/*     */           {
/*     */             
/* 106 */             if (pt == short.class) {
/* 107 */               tv = Short.valueOf((short)0);
/* 108 */             } else if (pt == int.class) {
/* 109 */               tv = Integer.valueOf(0);
/* 110 */             } else if (pt == long.class) {
/* 111 */               tv = Long.valueOf(0L);
/* 112 */             } else if (pt == double.class) {
/* 113 */               tv = Double.valueOf(0.0D);
/* 114 */             } else if (pt == float.class) {
/* 115 */               tv = Float.valueOf(0.0F);
/* 116 */             } else if (pt == boolean.class) {
/* 117 */               tv = Boolean.valueOf(false);
/*     */             
/*     */             }
/*     */             else {
/*     */               
/* 122 */               throw new IllegalArgumentException("Please enter a valid parameter @" + p.getName());
/*     */             } 
/*     */           }
/*     */         }
/*     */         
/* 127 */         if (tv == null && 
/* 128 */           p.required()) {
/* 129 */           ctx.status(400);
/* 130 */           throw new IllegalArgumentException("Required parameter @" + p.getName());
/*     */         } 
/*     */ 
/*     */         
/* 134 */         args.add(tv);
/*     */       } 
/*     */     } 
/*     */     
/* 138 */     return args;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object changeBody(Context ctx) throws Exception {
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
/* 152 */     String pn = p.getName();
/* 153 */     String pv = null;
/* 154 */     Object tv = null;
/*     */     
/* 156 */     if (p.requireHeader()) {
/* 157 */       pv = ctx.header(pn);
/*     */     } else {
/* 159 */       pv = ctx.param(pn);
/*     */     } 
/*     */     
/* 162 */     if (pv == null) {
/* 163 */       pv = p.defaultValue();
/*     */     }
/*     */     
/* 166 */     if (pv == null) {
/*     */ 
/*     */ 
/*     */       
/* 170 */       if (UploadedFile.class == pt) {
/*     */         
/* 172 */         tv = ctx.file(pn);
/*     */       
/*     */       }
/* 175 */       else if (pn.startsWith("$")) {
/* 176 */         tv = ctx.attr(pn);
/*     */       }
/* 178 */       else if (pt.getName().startsWith("java.") || pt.isArray() || pt.isPrimitive()) {
/*     */ 
/*     */         
/* 181 */         tv = null;
/*     */       } else {
/*     */         
/* 184 */         tv = changeEntityDo(ctx, pn, pt);
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 190 */       tv = ConvertUtil.to(p.getParameter(), pt, pn, pv, ctx);
/*     */     } 
/*     */     
/* 193 */     return tv;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object changeEntityDo(Context ctx, String name, Class<?> type) throws Exception {
/* 200 */     ClassWrap clzW = ClassWrap.get(type);
/* 201 */     NvMap nvMap = ctx.paramMap();
/*     */     
/* 203 */     return clzW.newBy(nvMap::get, ctx);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\ActionExecutorDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */