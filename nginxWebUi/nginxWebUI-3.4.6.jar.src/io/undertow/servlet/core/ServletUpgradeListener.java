/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.InstanceHandle;
/*     */ import io.undertow.servlet.api.ThreadSetupHandler;
/*     */ import io.undertow.servlet.spec.WebConnectionImpl;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.servlet.http.HttpUpgradeHandler;
/*     */ import javax.servlet.http.WebConnection;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.StreamConnection;
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
/*     */ public class ServletUpgradeListener<T extends HttpUpgradeHandler>
/*     */   implements HttpUpgradeListener
/*     */ {
/*     */   private final HttpServerExchange exchange;
/*     */   private final ThreadSetupHandler.Action<Void, StreamConnection> initAction;
/*     */   private final ThreadSetupHandler.Action<Void, Object> destroyAction;
/*     */   
/*     */   public ServletUpgradeListener(final InstanceHandle<T> instance, Deployment deployment, HttpServerExchange exchange) {
/*  46 */     this.exchange = exchange;
/*  47 */     this.initAction = deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, StreamConnection>()
/*     */         {
/*     */           public Void call(HttpServerExchange exchange, StreamConnection context)
/*     */           {
/*  51 */             ServletUpgradeListener.DelayedExecutor executor = new ServletUpgradeListener.DelayedExecutor((Executor)exchange.getIoThread());
/*     */             
/*     */             try {
/*  54 */               ((HttpUpgradeHandler)instance.getInstance()).init((WebConnection)new WebConnectionImpl(context, ServletUpgradeListener.this.exchange.getConnection().getByteBufferPool(), executor));
/*     */             } finally {
/*  56 */               executor.openGate();
/*     */             } 
/*  58 */             return null;
/*     */           }
/*     */         });
/*  61 */     this.destroyAction = new ThreadSetupHandler.Action<Void, Object>()
/*     */       {
/*     */         public Void call(HttpServerExchange exchange, Object context) throws Exception {
/*     */           try {
/*  65 */             ((HttpUpgradeHandler)instance.getInstance()).destroy();
/*     */           } finally {
/*  67 */             instance.release();
/*     */           } 
/*  69 */           return null;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleUpgrade(final StreamConnection channel, final HttpServerExchange exchange) {
/*  77 */     channel.getCloseSetter().set(new ChannelListener<StreamConnection>()
/*     */         {
/*     */           public void handleEvent(StreamConnection channel) {
/*     */             try {
/*  81 */               ServletUpgradeListener.this.destroyAction.call(null, null);
/*  82 */             } catch (Exception e) {
/*  83 */               throw new RuntimeException(e);
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/*  88 */     this.exchange.getConnection().getWorker().execute(new Runnable()
/*     */         {
/*     */           public void run() {
/*     */             try {
/*  92 */               ServletUpgradeListener.this.initAction.call(exchange, channel);
/*  93 */             } catch (Exception e) {
/*  94 */               throw new RuntimeException(e);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class DelayedExecutor
/*     */     implements Executor
/*     */   {
/*     */     private final Executor delegate;
/*     */     
/*     */     private volatile boolean queue = true;
/* 107 */     private final List<Runnable> tasks = new ArrayList<>();
/*     */     
/*     */     private DelayedExecutor(Executor delegate) {
/* 110 */       this.delegate = delegate;
/*     */     }
/*     */ 
/*     */     
/*     */     public void execute(Runnable command) {
/* 115 */       if (!this.queue) {
/* 116 */         this.delegate.execute(command);
/*     */       } else {
/* 118 */         synchronized (this) {
/* 119 */           if (!this.queue) {
/* 120 */             this.delegate.execute(command);
/*     */           } else {
/* 122 */             this.tasks.add(command);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     synchronized void openGate() {
/* 129 */       this.queue = false;
/* 130 */       for (Runnable task : this.tasks)
/* 131 */         this.delegate.execute(task); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ServletUpgradeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */