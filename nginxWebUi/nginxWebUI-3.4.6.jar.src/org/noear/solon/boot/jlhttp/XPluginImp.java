/*     */ package org.noear.solon.boot.jlhttp;
/*     */ 
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.SolonApp;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.boot.ServerProps;
/*     */ import org.noear.solon.boot.prop.HttpSignalProps;
/*     */ import org.noear.solon.boot.ssl.SslContextFactory;
/*     */ import org.noear.solon.core.AopContext;
/*     */ import org.noear.solon.core.Plugin;
/*     */ import org.noear.solon.core.Signal;
/*     */ import org.noear.solon.core.SignalSim;
/*     */ import org.noear.solon.core.SignalType;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.util.PrintUtil;
/*     */ import org.noear.solon.ext.NamedThreadFactory;
/*     */ 
/*     */ public final class XPluginImp implements Plugin {
/*     */   private static Signal _signal;
/*     */   
/*     */   public static Signal signal() {
/*  24 */     return _signal;
/*     */   }
/*     */   
/*  27 */   private HTTPServer _server = null;
/*     */   
/*     */   public static String solon_boot_ver() {
/*  30 */     return "jlhttp 2.6/" + Solon.cfg().version();
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(AopContext context) {
/*  35 */     if (!Solon.app().enableHttp()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  40 */     if (Utils.loadClass("org.noear.solon.boot.jetty.XPluginImp") != null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  45 */     if (Utils.loadClass("org.noear.solon.boot.undertow.XPluginImp") != null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  50 */     if (Utils.loadClass("org.noear.solon.boot.smarthttp.XPluginImp") != null) {
/*     */       return;
/*     */     }
/*     */     
/*  54 */     context.beanOnloaded(ctx -> {
/*     */           try {
/*     */             start0(Solon.app());
/*  57 */           } catch (RuntimeException e) {
/*     */             throw e;
/*  59 */           } catch (Throwable e) {
/*     */             throw new RuntimeException(e);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private void start0(SolonApp app) throws Throwable {
/*  67 */     ServerProps.init();
/*     */     
/*  69 */     this._server = new HTTPServer();
/*     */     
/*  71 */     HttpSignalProps props = new HttpSignalProps();
/*  72 */     String _host = props.getHost();
/*  73 */     int _port = props.getPort();
/*  74 */     String _name = props.getName();
/*     */     
/*  76 */     long time_start = System.currentTimeMillis();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     if (ServerProps.request_maxHeaderSize > 0) {
/*  83 */       HTTPServer.MAX_HEADER_SIZE = ServerProps.request_maxHeaderSize;
/*     */     }
/*     */     
/*  86 */     if (ServerProps.request_maxBodySize > 0) {
/*  87 */       HTTPServer.MAX_BODY_SIZE = ServerProps.request_maxBodySize;
/*     */     }
/*     */     
/*  90 */     JlHttpContextHandler _handler = new JlHttpContextHandler();
/*     */ 
/*     */     
/*  93 */     if (System.getProperty("javax.net.ssl.keyStore") != null)
/*     */     {
/*  95 */       this._server.setServerSocketFactory(SslContextFactory.create().getServerSocketFactory());
/*     */     }
/*     */     
/*  98 */     HTTPServer.VirtualHost host = this._server.getVirtualHost(null);
/*     */ 
/*     */     
/* 101 */     host.setDirectoryIndex(null);
/*     */     
/* 103 */     host.addContext("/", _handler, new String[] { MethodType.HEAD.name, MethodType.GET.name, MethodType.POST.name, MethodType.PUT.name, MethodType.DELETE.name, MethodType.PATCH.name, MethodType.OPTIONS.name });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 112 */     PrintUtil.info("Server:main: JlHttpServer 2.6(jlhttp)");
/*     */ 
/*     */     
/* 115 */     this._server.setExecutor(Executors.newCachedThreadPool((ThreadFactory)new NamedThreadFactory("jlhttp-")));
/* 116 */     this._server.setPort(_port);
/* 117 */     if (Utils.isNotEmpty(_host)) {
/* 118 */       this._server.setHost(_host);
/*     */     }
/* 120 */     this._server.start();
/*     */     
/* 122 */     _signal = (Signal)new SignalSim(_name, _port, "http", SignalType.HTTP);
/*     */     
/* 124 */     app.signalAdd(_signal);
/*     */     
/* 126 */     long time_end = System.currentTimeMillis();
/*     */     
/* 128 */     PrintUtil.info("Connector:main: jlhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{http://localhost:" + _port + "}");
/* 129 */     PrintUtil.info("Server:main: jlhttp: Started @" + (time_end - time_start) + "ms");
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() throws Throwable {
/* 134 */     if (this._server != null) {
/* 135 */       this._server.stop();
/* 136 */       this._server = null;
/*     */       
/* 138 */       PrintUtil.info("Server:main: jlhttp: Has Stopped " + solon_boot_ver());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\jlhttp\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */