/*     */ package cn.hutool.http.server;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.lang.Console;
/*     */ import cn.hutool.core.thread.GlobalThreadPool;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.http.server.action.Action;
/*     */ import cn.hutool.http.server.action.RootAction;
/*     */ import cn.hutool.http.server.filter.HttpFilter;
/*     */ import cn.hutool.http.server.filter.SimpleFilter;
/*     */ import cn.hutool.http.server.handler.ActionHandler;
/*     */ import com.sun.net.httpserver.Filter;
/*     */ import com.sun.net.httpserver.HttpContext;
/*     */ import com.sun.net.httpserver.HttpExchange;
/*     */ import com.sun.net.httpserver.HttpHandler;
/*     */ import com.sun.net.httpserver.HttpServer;
/*     */ import com.sun.net.httpserver.HttpsConfigurator;
/*     */ import com.sun.net.httpserver.HttpsServer;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Executor;
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
/*     */ public class SimpleServer
/*     */ {
/*     */   private final HttpServer server;
/*     */   private final List<Filter> filters;
/*     */   
/*     */   public SimpleServer(int port) {
/*  44 */     this(new InetSocketAddress(port));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer(String hostname, int port) {
/*  54 */     this(new InetSocketAddress(hostname, port));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer(InetSocketAddress address) {
/*  63 */     this(address, (HttpsConfigurator)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer(InetSocketAddress address, HttpsConfigurator configurator) {
/*     */     try {
/*  74 */       if (null != configurator) {
/*  75 */         HttpsServer server = HttpsServer.create(address, 0);
/*  76 */         server.setHttpsConfigurator(configurator);
/*  77 */         this.server = server;
/*     */       } else {
/*  79 */         this.server = HttpServer.create(address, 0);
/*     */       } 
/*  81 */     } catch (IOException e) {
/*  82 */       throw new IORuntimeException(e);
/*     */     } 
/*  84 */     setExecutor(GlobalThreadPool.getExecutor());
/*  85 */     this.filters = new ArrayList<>();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer addFilter(Filter filter) {
/* 105 */     this.filters.add(filter);
/* 106 */     return this;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer addFilter(final HttpFilter filter) {
/* 126 */     return addFilter((Filter)new SimpleFilter()
/*     */         {
/*     */           public void doFilter(HttpExchange httpExchange, Filter.Chain chain) throws IOException {
/* 129 */             filter.doFilter(new HttpServerRequest(httpExchange), new HttpServerResponse(httpExchange), chain);
/*     */           }
/*     */         });
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
/*     */   public SimpleServer addHandler(String path, HttpHandler handler) {
/* 143 */     createContext(path, handler);
/* 144 */     return this;
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
/*     */   public HttpContext createContext(String path, HttpHandler handler) {
/* 157 */     path = StrUtil.addPrefixIfNot(path, "/");
/* 158 */     HttpContext context = this.server.createContext(path, handler);
/*     */     
/* 160 */     context.getFilters().addAll(this.filters);
/* 161 */     return context;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer setRoot(String root) {
/* 171 */     return setRoot(new File(root));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer setRoot(File root) {
/* 181 */     return addAction("/", (Action)new RootAction(root));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer addAction(String path, Action action) {
/* 192 */     return addHandler(path, (HttpHandler)new ActionHandler(action));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleServer setExecutor(Executor executor) {
/* 202 */     this.server.setExecutor(executor);
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServer getRawServer() {
/* 212 */     return this.server;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InetSocketAddress getAddress() {
/* 221 */     return this.server.getAddress();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 228 */     InetSocketAddress address = getAddress();
/* 229 */     Console.log("Hutool Simple Http Server listen on 【{}:{}】", new Object[] { address.getHostName(), Integer.valueOf(address.getPort()) });
/* 230 */     this.server.start();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\server\SimpleServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */