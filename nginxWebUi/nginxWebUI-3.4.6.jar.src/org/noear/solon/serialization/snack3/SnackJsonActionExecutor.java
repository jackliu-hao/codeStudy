/*     */ package org.noear.solon.serialization.snack3;
/*     */ 
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.noear.snack.ONode;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.handle.ActionExecutorDefault;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.wrap.ParamWrap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SnackJsonActionExecutor
/*     */   extends ActionExecutorDefault
/*     */ {
/*     */   private static final String label = "/json";
/*     */   
/*     */   public boolean matched(Context ctx, String ct) {
/*  24 */     if (ct != null && ct.contains("/json")) {
/*  25 */       return true;
/*     */     }
/*  27 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object changeBody(Context ctx) throws Exception {
/*  33 */     String json = ctx.bodyNew();
/*     */     
/*  35 */     if (Utils.isNotEmpty(json)) {
/*  36 */       return ONode.loadStr(json);
/*     */     }
/*  38 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
/*  44 */     if (ctx.paramMap().containsKey(p.getName())) {
/*  45 */       return super.changeValue(ctx, p, pi, pt, bodyObj);
/*     */     }
/*     */     
/*  48 */     if (bodyObj == null) {
/*  49 */       return super.changeValue(ctx, p, pi, pt, bodyObj);
/*     */     }
/*     */     
/*  52 */     ONode tmp = (ONode)bodyObj;
/*     */     
/*  54 */     if (tmp.isObject()) {
/*  55 */       if (tmp.contains(p.getName())) {
/*     */         
/*  57 */         ParameterizedType parameterizedType = p.getGenericType();
/*  58 */         if (parameterizedType != null) {
/*  59 */           return tmp.get(p.getName()).toObject(parameterizedType);
/*     */         }
/*     */         
/*  62 */         return tmp.get(p.getName()).toObject(pt);
/*  63 */       }  if (ctx.paramMap().containsKey(p.getName()))
/*     */       {
/*     */         
/*  66 */         return super.changeValue(ctx, p, pi, pt, bodyObj);
/*     */       }
/*  68 */       if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
/*  69 */         return super.changeValue(ctx, p, pi, pt, bodyObj);
/*     */       }
/*  71 */       if (List.class.isAssignableFrom(p.getType())) {
/*  72 */         return null;
/*     */       }
/*     */       
/*  75 */       if (p.getType().isArray()) {
/*  76 */         return null;
/*     */       }
/*     */ 
/*     */       
/*  80 */       ParameterizedType gp = p.getGenericType();
/*  81 */       if (gp != null) {
/*  82 */         return tmp.toObject(gp);
/*     */       }
/*     */       
/*  85 */       return tmp.toObject(pt);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  90 */     if (tmp.isArray()) {
/*     */       
/*  92 */       if (!Collection.class.isAssignableFrom(pt)) {
/*  93 */         return null;
/*     */       }
/*     */ 
/*     */       
/*  97 */       ParameterizedType gp = p.getGenericType();
/*  98 */       if (gp != null)
/*     */       {
/* 100 */         return tmp.toObject(gp);
/*     */       }
/*     */ 
/*     */       
/* 104 */       return tmp.toObject(p.getType());
/*     */     } 
/*     */     
/* 107 */     return tmp.val().getRaw();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\serialization\snack3\SnackJsonActionExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */