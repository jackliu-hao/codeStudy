/*     */ package cn.hutool.core.exceptions;
/*     */ 
/*     */ import cn.hutool.core.lang.func.Func;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.lang.func.Func1;
/*     */ import cn.hutool.core.lang.func.Supplier1;
/*     */ import cn.hutool.core.lang.func.VoidFunc;
/*     */ import cn.hutool.core.lang.func.VoidFunc0;
/*     */ import cn.hutool.core.lang.func.VoidFunc1;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.util.Objects;
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
/*     */ public class CheckedUtil
/*     */ {
/*     */   public static <P, R> FuncRt<P, R> uncheck(Func<P, R> expression) {
/*  57 */     return uncheck(expression, RuntimeException::new);
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
/*     */   public static <R> Func0Rt<R> uncheck(Func0<R> expression) {
/*  69 */     return uncheck(expression, RuntimeException::new);
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
/*     */   
/*     */   public static <P, R> Func1Rt<P, R> uncheck(Func1<P, R> expression) {
/*  82 */     return uncheck(expression, RuntimeException::new);
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
/*     */   
/*     */   public static <P> VoidFuncRt<P> uncheck(VoidFunc<P> expression) {
/*  95 */     return uncheck(expression, RuntimeException::new);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static VoidFunc0Rt uncheck(VoidFunc0 expression) {
/* 106 */     return uncheck(expression, RuntimeException::new);
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
/*     */   public static <P> VoidFunc1Rt<P> uncheck(VoidFunc1<P> expression) {
/* 118 */     return uncheck(expression, RuntimeException::new);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <P, R> FuncRt<P, R> uncheck(Func<P, R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
/* 133 */     Objects.requireNonNull(expression, "expression can not be null");
/* 134 */     return t -> {
/*     */         try {
/*     */           return expression.call(t);
/* 137 */         } catch (Exception e) {
/*     */           if (rteSupplier == null) {
/*     */             throw new RuntimeException(e);
/*     */           }
/*     */           throw (RuntimeException)rteSupplier.get(e);
/*     */         } 
/*     */       };
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
/*     */ 
/*     */   
/*     */   public static <R> Func0Rt<R> uncheck(Func0<R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
/* 157 */     Objects.requireNonNull(expression, "expression can not be null");
/* 158 */     return () -> {
/*     */         try {
/*     */           return expression.call();
/* 161 */         } catch (Exception e) {
/*     */           if (rteSupplier == null) {
/*     */             throw new RuntimeException(e);
/*     */           }
/*     */           throw (RuntimeException)rteSupplier.get(e);
/*     */         } 
/*     */       };
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static <P, R> Func1Rt<P, R> uncheck(Func1<P, R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
/* 182 */     Objects.requireNonNull(expression, "expression can not be null");
/* 183 */     return t -> {
/*     */         try {
/*     */           return expression.call(t);
/* 186 */         } catch (Exception e) {
/*     */           if (rteSupplier == null) {
/*     */             throw new RuntimeException(e);
/*     */           }
/*     */           throw (RuntimeException)rteSupplier.get(e);
/*     */         } 
/*     */       };
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
/*     */ 
/*     */   
/*     */   public static <P> VoidFuncRt<P> uncheck(VoidFunc<P> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
/* 206 */     Objects.requireNonNull(expression, "expression can not be null");
/* 207 */     return t -> {
/*     */         try {
/*     */           expression.call(t);
/* 210 */         } catch (Exception e) {
/*     */           if (rteSupplier == null) {
/*     */             throw new RuntimeException(e);
/*     */           }
/*     */           throw (RuntimeException)rteSupplier.get(e);
/*     */         } 
/*     */       };
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
/*     */ 
/*     */   
/*     */   public static VoidFunc0Rt uncheck(VoidFunc0 expression, RuntimeException rte) {
/* 230 */     Objects.requireNonNull(expression, "expression can not be null");
/* 231 */     return () -> {
/*     */         try {
/*     */           expression.call();
/* 234 */         } catch (Exception e) {
/*     */           if (rte == null) {
/*     */             throw new RuntimeException(e);
/*     */           }
/*     */           rte.initCause(e);
/*     */           throw rte;
/*     */         } 
/*     */       };
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
/*     */   
/*     */   public static VoidFunc0Rt uncheck(VoidFunc0 expression, Supplier1<RuntimeException, Exception> rteSupplier) {
/* 254 */     Objects.requireNonNull(expression, "expression can not be null");
/* 255 */     return () -> {
/*     */         try {
/*     */           expression.call();
/* 258 */         } catch (Exception e) {
/*     */           if (rteSupplier == null) {
/*     */             throw new RuntimeException(e);
/*     */           }
/*     */           throw (RuntimeException)rteSupplier.get(e);
/*     */         } 
/*     */       };
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
/*     */ 
/*     */   
/*     */   public static <P> VoidFunc1Rt<P> uncheck(VoidFunc1<P> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
/* 278 */     Objects.requireNonNull(expression, "expression can not be null");
/* 279 */     return t -> {
/*     */         try {
/*     */           expression.call(t);
/* 282 */         } catch (Exception e) {
/*     */           if (rteSupplier == null)
/*     */             throw new RuntimeException(e); 
/*     */           throw (RuntimeException)rteSupplier.get(e);
/*     */         } 
/*     */       };
/*     */   }
/*     */   
/*     */   public static interface VoidFunc1Rt<P> extends VoidFunc1<P> {
/*     */     void call(P param1P) throws RuntimeException;
/*     */   }
/*     */   
/*     */   public static interface VoidFunc0Rt extends VoidFunc0 {
/*     */     void call() throws RuntimeException;
/*     */   }
/*     */   
/*     */   public static interface VoidFuncRt<P> extends VoidFunc<P> {
/*     */     void call(P... param1VarArgs) throws RuntimeException;
/*     */   }
/*     */   
/*     */   public static interface Func1Rt<P, R> extends Func1<P, R> {
/*     */     R call(P param1P) throws RuntimeException;
/*     */   }
/*     */   
/*     */   public static interface Func0Rt<R> extends Func0<R> {
/*     */     R call() throws RuntimeException;
/*     */   }
/*     */   
/*     */   public static interface FuncRt<P, R> extends Func<P, R> {
/*     */     R call(P... param1VarArgs) throws RuntimeException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\exceptions\CheckedUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */