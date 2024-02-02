/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.annotation.Note;
/*     */ import org.noear.solon.core.handle.ActionExecutor;
/*     */ import org.noear.solon.core.handle.ActionExecutorDefault;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.handle.SessionState;
/*     */ import org.noear.solon.core.handle.SessionStateEmpty;
/*     */ import org.noear.solon.core.handle.SessionStateFactory;
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
/*     */ 
/*     */ 
/*     */ public class Bridge
/*     */ {
/*     */   private static SessionStateFactory _sessionStateFactory = ctx -> new SessionStateEmpty();
/*     */   private static boolean sessionStateUpdated;
/*     */   
/*     */   public static SessionStateFactory sessionStateFactory() {
/*  52 */     return _sessionStateFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("设置Session状态管理器")
/*     */   public static void sessionStateFactorySet(SessionStateFactory ssf) {
/*  60 */     if (ssf != null) {
/*  61 */       _sessionStateFactory = ssf;
/*     */       
/*  63 */       if (!sessionStateUpdated) {
/*  64 */         sessionStateUpdated = true;
/*     */         
/*  66 */         Solon.app().before("**", MethodType.HTTP, c -> c.sessionState().sessionRefresh());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("获取Session状态管理器")
/*     */   public static SessionState sessionState(Context ctx) {
/*  78 */     return _sessionStateFactory.create(ctx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static LoadBalance.Factory _upstreamFactory = (g, s) -> null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("获取负载工厂")
/*     */   public static LoadBalance.Factory upstreamFactory() {
/*  92 */     return _upstreamFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("设置负载工厂")
/*     */   public static void upstreamFactorySet(LoadBalance.Factory uf) {
/* 100 */     if (uf != null) {
/* 101 */       _upstreamFactory = uf;
/*     */     }
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
/* 113 */   private static ActionExecutor _actionExecutorDef = (ActionExecutor)new ActionExecutorDefault();
/*     */ 
/*     */ 
/*     */   
/* 117 */   private static Map<Class<?>, ActionExecutor> _actionExecutors = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("获取默认的Action执行器")
/*     */   public static ActionExecutor actionExecutorDef() {
/* 124 */     return _actionExecutorDef;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("设置默认的Action执行器")
/*     */   public static void actionExecutorDefSet(ActionExecutor ae) {
/* 132 */     if (ae != null) {
/* 133 */       _actionExecutorDef = ae;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("获取所有Action执行器")
/*     */   public static Collection<ActionExecutor> actionExecutors() {
/* 142 */     return Collections.unmodifiableCollection(_actionExecutors.values());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Note("添加Action执行器")
/*     */   public static void actionExecutorAdd(ActionExecutor e) {
/* 150 */     if (e != null) {
/* 151 */       _actionExecutors.put(e.getClass(), e);
/*     */     }
/*     */   }
/*     */   
/*     */   @Note("移除Action执行器")
/*     */   public static void actionExecutorRemove(Class<?> clz) {
/* 157 */     _actionExecutors.remove(clz);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\Bridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */