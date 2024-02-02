/*     */ package org.wildfly.common.context;
/*     */ 
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.BiPredicate;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.IntFunction;
/*     */ import java.util.function.LongFunction;
/*     */ import java.util.function.Predicate;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ import org.wildfly.common.annotation.NotNull;
/*     */ import org.wildfly.common.function.ExceptionBiConsumer;
/*     */ import org.wildfly.common.function.ExceptionBiFunction;
/*     */ import org.wildfly.common.function.ExceptionBiPredicate;
/*     */ import org.wildfly.common.function.ExceptionConsumer;
/*     */ import org.wildfly.common.function.ExceptionFunction;
/*     */ import org.wildfly.common.function.ExceptionIntFunction;
/*     */ import org.wildfly.common.function.ExceptionLongFunction;
/*     */ import org.wildfly.common.function.ExceptionPredicate;
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
/*     */ public interface Contextual<C extends Contextual<C>>
/*     */ {
/*     */   @NotNull
/*     */   ContextManager<C> getInstanceContextManager();
/*     */   
/*     */   default void run(Runnable runnable) {
/*  69 */     Assert.checkNotNullParam("runnable", runnable);
/*  70 */     ContextManager<C> contextManager = getInstanceContextManager();
/*  71 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/*  73 */       runnable.run();
/*     */     } finally {
/*  75 */       contextManager.restoreCurrent(old);
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
/*     */   default <R> R runAction(PrivilegedAction<R> action) {
/*  87 */     ContextManager<C> contextManager = getInstanceContextManager();
/*  88 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/*  90 */       return action.run();
/*     */     } finally {
/*  92 */       contextManager.restoreCurrent(old);
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
/*     */   default <R> R runExceptionAction(PrivilegedExceptionAction<R> action) throws PrivilegedActionException {
/* 105 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 106 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 108 */       return action.run();
/* 109 */     } catch (Exception e) {
/* 110 */       throw CommonMessages.msg.privilegedActionFailed(e);
/*     */     } finally {
/* 112 */       contextManager.restoreCurrent(old);
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
/*     */   default <V> V runCallable(Callable<V> callable) throws Exception {
/* 124 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 125 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 127 */       return callable.call();
/*     */     } finally {
/* 129 */       contextManager.restoreCurrent(old);
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
/*     */   
/*     */   default <T, U> void runBiConsumer(BiConsumer<T, U> consumer, T param1, U param2) {
/* 143 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 144 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 146 */       consumer.accept(param1, param2);
/*     */     } finally {
/* 148 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */ 
/*     */   
/*     */   default <T, U, E extends Exception> void runExBiConsumer(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) throws E {
/* 164 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 165 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 167 */       consumer.accept(param1, param2);
/*     */     } finally {
/* 169 */       contextManager.restoreCurrent(old);
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
/*     */   default <T> void runConsumer(Consumer<T> consumer, T param) {
/* 181 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 182 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 184 */       consumer.accept(param);
/*     */     } finally {
/* 186 */       contextManager.restoreCurrent(old);
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
/*     */   
/*     */   default <T, E extends Exception> void runExConsumer(ExceptionConsumer<T, E> consumer, T param) throws E {
/* 200 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 201 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 203 */       consumer.accept(param);
/*     */     } finally {
/* 205 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */ 
/*     */   
/*     */   default <T, U, R> R runBiFunction(BiFunction<T, U, R> function, T param1, U param2) {
/* 221 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 222 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 224 */       return function.apply(param1, param2);
/*     */     } finally {
/* 226 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default <T, U, R, E extends Exception> R runExBiFunction(ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
/* 244 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 245 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 247 */       return (R)function.apply(param1, param2);
/*     */     } finally {
/* 249 */       contextManager.restoreCurrent(old);
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
/*     */   
/*     */   default <T, R> R runFunction(Function<T, R> function, T param) {
/* 263 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 264 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 266 */       return function.apply(param);
/*     */     } finally {
/* 268 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */ 
/*     */   
/*     */   default <T, R, E extends Exception> R runExFunction(ExceptionFunction<T, R, E> function, T param) throws E {
/* 284 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 285 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 287 */       return (R)function.apply(param);
/*     */     } finally {
/* 289 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */   
/*     */   default <T, U> boolean runBiPredicate(BiPredicate<T, U> predicate, T param1, U param2) {
/* 304 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 305 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 307 */       return predicate.test(param1, param2);
/*     */     } finally {
/* 309 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default <T, U, E extends Exception> boolean runExBiPredicate(ExceptionBiPredicate<T, U, E> predicate, T param1, U param2) throws E {
/* 326 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 327 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 329 */       return predicate.test(param1, param2);
/*     */     } finally {
/* 331 */       contextManager.restoreCurrent(old);
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
/*     */   default <T> boolean runPredicate(Predicate<T> predicate, T param) {
/* 344 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 345 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 347 */       return predicate.test(param);
/*     */     } finally {
/* 349 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */   
/*     */   default <T, E extends Exception> boolean runExPredicate(ExceptionPredicate<T, E> predicate, T param) throws E {
/* 364 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 365 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 367 */       return predicate.test(param);
/*     */     } finally {
/* 369 */       contextManager.restoreCurrent(old);
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
/*     */   default <T> T runIntFunction(IntFunction<T> function, int value) {
/* 382 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 383 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 385 */       return function.apply(value);
/*     */     } finally {
/* 387 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */   
/*     */   default <T, E extends Exception> T runExIntFunction(ExceptionIntFunction<T, E> function, int value) throws E {
/* 402 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 403 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 405 */       return (T)function.apply(value);
/*     */     } finally {
/* 407 */       contextManager.restoreCurrent(old);
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
/*     */   default <T> T runLongFunction(LongFunction<T> function, long value) {
/* 420 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 421 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 423 */       return function.apply(value);
/*     */     } finally {
/* 425 */       contextManager.restoreCurrent(old);
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
/*     */ 
/*     */   
/*     */   default <T, E extends Exception> T runExLongFunction(ExceptionLongFunction<T, E> function, long value) throws E {
/* 440 */     ContextManager<C> contextManager = getInstanceContextManager();
/* 441 */     C old = contextManager.getAndSetCurrent(this);
/*     */     try {
/* 443 */       return (T)function.apply(value);
/*     */     } finally {
/* 445 */       contextManager.restoreCurrent(old);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\context\Contextual.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */