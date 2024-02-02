/*    */ package org.noear.solon.boot.undertow;
/*    */ import javax.servlet.annotation.WebFilter;
/*    */ import javax.servlet.annotation.WebListener;
/*    */ import javax.servlet.annotation.WebServlet;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.SolonApp;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.boot.ServerProps;
/*    */ import org.noear.solon.boot.prop.HttpSignalProps;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.core.Signal;
/*    */ import org.noear.solon.core.SignalSim;
/*    */ import org.noear.solon.core.util.PrintUtil;
/*    */ 
/*    */ public final class XPluginImp implements Plugin {
/*    */   public static Signal signal() {
/* 19 */     return _signal;
/*    */   }
/*    */   private static Signal _signal;
/* 22 */   private ServerLifecycle _server = null;
/*    */   public static String solon_boot_ver() {
/* 24 */     return "undertow 2.1/" + Solon.cfg().version();
/*    */   }
/*    */ 
/*    */   
/*    */   public void start(AopContext context) {
/* 29 */     if (!Solon.app().enableHttp()) {
/*    */       return;
/*    */     }
/*    */     
/* 33 */     context.beanBuilderAdd(WebFilter.class, (clz, bw, ano) -> {
/*    */         
/* 35 */         }); context.beanBuilderAdd(WebServlet.class, (clz, bw, ano) -> {
/*    */         
/* 37 */         }); context.beanBuilderAdd(WebListener.class, (clz, bw, ano) -> {
/*    */         
/*    */         });
/* 40 */     context.beanOnloaded(ctx -> {
/*    */           try {
/*    */             start0(Solon.app());
/* 43 */           } catch (RuntimeException e) {
/*    */             throw e;
/* 45 */           } catch (Throwable e) {
/*    */             throw new RuntimeException(e);
/*    */           } 
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   private void start0(SolonApp app) throws Throwable {
/* 53 */     ServerProps.init();
/*    */     
/* 55 */     HttpSignalProps props = new HttpSignalProps();
/* 56 */     String _host = props.getHost();
/* 57 */     int _port = props.getPort();
/* 58 */     String _name = props.getName();
/*    */     
/* 60 */     long time_start = System.currentTimeMillis();
/* 61 */     PrintUtil.info("Server:main: Undertow 2.2.17(undertow)");
/*    */     
/* 63 */     Class<?> jspClz = Utils.loadClass("io.undertow.jsp.JspServletBuilder");
/*    */     
/* 65 */     if (jspClz == null) {
/* 66 */       this._server = new UndertowServer();
/*    */     } else {
/* 68 */       this._server = new UndertowServerAddJsp();
/*    */     } 
/*    */     
/* 71 */     this._server.start(_host, _port);
/*    */     
/* 73 */     _signal = (Signal)new SignalSim(_name, _port, "http", SignalType.HTTP);
/*    */     
/* 75 */     app.signalAdd(_signal);
/*    */     
/* 77 */     long time_end = System.currentTimeMillis();
/*    */     
/* 79 */     String connectorInfo = "solon.connector:main: undertow: Started ServerConnector@{HTTP/1.1,[http/1.1]";
/* 80 */     if (app.enableWebSocket()) {
/* 81 */       System.out.println(connectorInfo + "[WebSocket]}{0.0.0.0:" + _port + "}");
/*    */     }
/*    */     
/* 84 */     System.out.println(connectorInfo + "}{http://localhost:" + _port + "}");
/*    */     
/* 86 */     PrintUtil.info("Server:main: undertow: Started @" + (time_end - time_start) + "ms");
/*    */   }
/*    */ 
/*    */   
/*    */   public void stop() throws Throwable {
/* 91 */     if (this._server != null) {
/* 92 */       this._server.stop();
/* 93 */       this._server = null;
/*    */       
/* 95 */       PrintUtil.info("Server:main: undertow: Has Stopped " + solon_boot_ver());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */