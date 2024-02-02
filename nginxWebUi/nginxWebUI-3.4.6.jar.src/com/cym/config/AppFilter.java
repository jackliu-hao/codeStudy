/*     */ package com.cym.config;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.http.HttpUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.model.Admin;
/*     */ import com.cym.model.Message;
/*     */ import com.cym.model.Remote;
/*     */ import com.cym.service.AdminService;
/*     */ import com.cym.service.CreditService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.MessageUtils;
/*     */ import com.cym.utils.PropertiesUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.noear.solon.annotation.Component;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.Filter;
/*     */ import org.noear.solon.core.handle.FilterChain;
/*     */ import org.noear.solon.core.handle.UploadedFile;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class AppFilter
/*     */   implements Filter
/*     */ {
/*  50 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject
/*     */   AdminService adminService;
/*     */   
/*     */   @Inject
/*     */   MessageUtils m;
/*     */   
/*     */   @Inject
/*     */   CreditService creditService;
/*     */   
/*     */   @Inject("${solon.app.name}")
/*     */   String projectName;
/*     */   @Inject
/*     */   VersionConfig versionConfig;
/*     */   @Inject
/*     */   PropertiesUtils propertiesUtils;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   
/*     */   public void doFilter(Context ctx, FilterChain chain) throws Throwable {
/*  71 */     if (!ctx.path().contains("/lib/") && 
/*  72 */       !ctx.path().contains("/js/") && 
/*  73 */       !ctx.path().contains("/doc/") && 
/*  74 */       !ctx.path().contains("/img/") && 
/*  75 */       !ctx.path().contains("/css/")) {
/*  76 */       frontInterceptor(ctx);
/*     */     }
/*     */ 
/*     */     
/*  80 */     if (ctx.path().contains("/adminPage/") && 
/*  81 */       !ctx.path().contains("/lib/") && 
/*  82 */       !ctx.path().contains("/doc/") && 
/*  83 */       !ctx.path().contains("/js/") && 
/*  84 */       !ctx.path().contains("/img/") && 
/*  85 */       !ctx.path().contains("/css/") && 
/*  86 */       !adminInterceptor(ctx)) {
/*     */       
/*  88 */       ctx.setHandled(true);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  94 */     if (ctx.path().contains("/api/") && 
/*  95 */       !ctx.path().contains("/lib/") && 
/*  96 */       !ctx.path().contains("/doc/") && 
/*  97 */       !ctx.path().contains("/js/") && 
/*  98 */       !ctx.path().contains("/img/") && 
/*  99 */       !ctx.path().contains("/css/") && 
/* 100 */       !apiInterceptor(ctx)) {
/*     */       
/* 102 */       ctx.setHandled(true);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 107 */     chain.doFilter(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean apiInterceptor(Context ctx) {
/* 112 */     String token = ctx.header("token");
/* 113 */     Admin admin = this.adminService.getByToken(token);
/*     */     
/* 115 */     if (admin != null && admin.getApi().booleanValue()) {
/* 116 */       return true;
/*     */     }
/*     */     
/* 119 */     JsonResult result = new JsonResult();
/* 120 */     result.setSuccess(false);
/* 121 */     result.setStatus("401");
/* 122 */     result.setMsg(this.m.get("apiStr.wrongToken"));
/*     */     
/* 124 */     ctx.output(JSONUtil.toJsonPrettyStr(result));
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean adminInterceptor(Context ctx) {
/* 131 */     String ctxStr = getCtxStr(ctx);
/*     */     
/* 133 */     if (ctx.path().contains("adminPage/login")) {
/* 134 */       return true;
/*     */     }
/*     */     
/* 137 */     String creditKey = ctx.param("creditKey");
/* 138 */     Boolean isCredit = Boolean.valueOf(this.creditService.check(creditKey));
/*     */     
/* 140 */     Boolean isLogin = (Boolean)ctx.session("isLogin");
/* 141 */     if ((isLogin == null || !isLogin.booleanValue()) && !isCredit.booleanValue()) {
/* 142 */       ctx.redirect("/adminPage/login");
/* 143 */       return false;
/*     */     } 
/*     */     
/* 146 */     String localType = (String)ctx.session("localType");
/* 147 */     if (localType != null && localType
/* 148 */       .equals("remote") && 
/* 149 */       !ctx.path().contains("adminPage/remote") && 
/* 150 */       !ctx.path().contains("adminPage/admin") && 
/* 151 */       !ctx.path().contains("adminPage/about")) {
/*     */ 
/*     */       
/* 154 */       Remote remote = (Remote)ctx.session("remote");
/* 155 */       String url = buildUrl(ctxStr, ctx, remote);
/*     */       
/*     */       try {
/* 158 */         String rs = null;
/* 159 */         if (url.contains("main/upload")) {
/*     */           
/* 161 */           Map<String, Object> map = new HashMap<>();
/* 162 */           map.put("creditKey", remote.getCreditKey());
/*     */           
/* 164 */           UploadedFile uploadedFile = ctx.file("file");
/*     */           
/* 166 */           File temp = new File(FileUtil.getTmpDir() + "/" + uploadedFile.name);
/* 167 */           uploadedFile.transferTo(temp);
/* 168 */           map.put("file", temp);
/*     */           
/* 170 */           rs = HttpUtil.post(url, map);
/*     */         }
/*     */         else {
/*     */           
/* 174 */           Admin admin = (new BaseController()).getAdmin();
/* 175 */           String body = buldBody(ctx.paramsMap(), remote, admin);
/* 176 */           rs = HttpUtil.post(url, body);
/*     */         } 
/*     */         
/* 179 */         ctx.charset("utf-8");
/* 180 */         ctx.contentType("text/html;charset=utf-8");
/*     */         
/* 182 */         if (JSONUtil.isTypeJSON(rs)) {
/* 183 */           String date = DateUtil.format(new Date(), "yyyy-MM-dd_HH-mm-ss");
/* 184 */           ctx.header("Content-Type", "application/octet-stream");
/* 185 */           ctx.header("content-disposition", "attachment;filename=" + URLEncoder.encode(date + ".json", "UTF-8"));
/*     */           
/* 187 */           byte[] buffer = new byte[1024];
/* 188 */           BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rs.getBytes(Charset.forName("UTF-8"))));
/* 189 */           OutputStream os = ctx.outputStream();
/* 190 */           int i = bis.read(buffer);
/* 191 */           while (i != -1) {
/* 192 */             os.write(buffer, 0, i);
/* 193 */             i = bis.read(buffer);
/*     */           } 
/*     */         } else {
/* 196 */           ctx.output(rs);
/*     */         }
/*     */       
/* 199 */       } catch (Exception e) {
/* 200 */         this.logger.error(e.getMessage(), e);
/* 201 */         ctx.redirect("/adminPage/login/noServer");
/*     */       } 
/*     */       
/* 204 */       return false;
/*     */     } 
/*     */     
/* 207 */     return true;
/*     */   }
/*     */   
/*     */   private void frontInterceptor(Context ctx) {
/* 211 */     String ctxStr = getCtxStr(ctx);
/* 212 */     if (StrUtil.isNotEmpty(ctx.param("ctx"))) {
/* 213 */       ctxStr = Base64.decodeStr(ctx.param("ctx"));
/*     */     }
/*     */     
/* 216 */     ctx.attrSet("ctx", ctxStr);
/*     */     
/* 218 */     ctx.attrSet("jsrandom", this.versionConfig.currentVersion);
/* 219 */     ctx.attrSet("currentVersion", this.versionConfig.currentVersion);
/* 220 */     ctx.attrSet("projectName", this.projectName);
/*     */     
/* 222 */     ctx.attrSet("showAdmin", ctx.param("showAdmin"));
/* 223 */     ctx.attrSet("admin", ctx.session("admin"));
/*     */ 
/*     */     
/* 226 */     if (this.versionConfig.newVersion != null) {
/* 227 */       ctx.attrSet("newVersion", this.versionConfig.newVersion);
/*     */       
/* 229 */       if (Integer.parseInt(this.versionConfig.currentVersion.replace(".", "").replace("v", "")) < Integer.parseInt(this.versionConfig.newVersion.getVersion().replace(".", "").replace("v", ""))) {
/* 230 */         ctx.attrSet("hasNewVersion", Integer.valueOf(1));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 235 */     Properties properties = null;
/* 236 */     String l = ctx.param("l");
/* 237 */     if ((StrUtil.isNotEmpty(l) && l.equals("en_US")) || (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US"))) {
/* 238 */       this.settingService.set("lang", "en_US");
/* 239 */       properties = this.m.getPropertiesEN();
/*     */     } else {
/* 241 */       this.settingService.set("lang", "");
/* 242 */       properties = this.m.getProperties();
/*     */     } 
/*     */ 
/*     */     
/* 246 */     Set<String> messageHeaders = new HashSet<>();
/* 247 */     List<Message> messages = new ArrayList<>();
/* 248 */     for (String key : properties.stringPropertyNames()) {
/* 249 */       Message message = new Message();
/* 250 */       message.setKey(key);
/* 251 */       message.setValue(properties.getProperty(key));
/* 252 */       messages.add(message);
/*     */       
/* 254 */       messageHeaders.add(key.split("\\.")[0]);
/*     */     } 
/*     */     
/* 257 */     ctx.attrSet("messageHeaders", messageHeaders);
/* 258 */     ctx.attrSet("messages", messages);
/*     */ 
/*     */     
/* 261 */     for (String key : messageHeaders) {
/* 262 */       Map<String, String> map = new HashMap<>();
/* 263 */       for (Message message : messages) {
/* 264 */         if (message.getKey().split("\\.")[0].equals(key)) {
/* 265 */           map.put(message.getKey().split("\\.")[1], message.getValue());
/*     */         }
/*     */       } 
/*     */       
/* 269 */       ctx.attrSet(key, map);
/*     */     } 
/*     */     
/* 272 */     if (this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US")) {
/* 273 */       ctx.attrSet("langType", "切换到中文");
/*     */     } else {
/* 275 */       ctx.attrSet("langType", "Switch to English");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private String buldBody(Map<String, List<String>> parameterMap, Remote remote, Admin admin) throws UnsupportedEncodingException {
/* 281 */     List<String> body = new ArrayList<>();
/* 282 */     body.add("creditKey=" + remote.getCreditKey());
/* 283 */     if (admin != null) {
/* 284 */       body.add("adminName=" + admin.getName());
/*     */     }
/*     */     
/* 287 */     for (Iterator<Map.Entry> itr = parameterMap.entrySet().iterator(); itr.hasNext(); ) {
/* 288 */       Map.Entry me = itr.next();
/*     */       
/* 290 */       for (String value : me.getValue()) {
/* 291 */         body.add((new StringBuilder()).append(me.getKey()).append("=").append(URLEncoder.encode(value, "UTF-8")).toString());
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 296 */     return StrUtil.join("&", body);
/*     */   }
/*     */   
/*     */   private String buildUrl(String ctxStr, Context ctx, Remote remote) {
/* 300 */     String url = ctx.url().replace(ctxStr, "//" + remote.getIp() + ":" + remote.getPort() + "/");
/*     */     
/* 302 */     if (url.startsWith("http")) {
/* 303 */       url = url.replace("http:", "").replace("https:", "");
/*     */     }
/*     */     
/* 306 */     url = remote.getProtocol() + ":" + url;
/*     */     
/* 308 */     Admin admin = (Admin)ctx.session("admin");
/* 309 */     String showAdmin = "false";
/* 310 */     if (admin != null && admin.getType().intValue() == 0) {
/* 311 */       showAdmin = "true";
/*     */     }
/* 313 */     return url + "?jsrandom=" + System.currentTimeMillis() + "&protocol=" + remote
/* 314 */       .getProtocol() + "&showAdmin=" + showAdmin + "&ctx=" + 
/*     */       
/* 316 */       Base64.encode(ctxStr);
/*     */   }
/*     */   
/*     */   public String getCtxStr(Context context) {
/* 320 */     String httpHost = context.header("X-Forwarded-Host");
/* 321 */     String realPort = context.header("X-Forwarded-Port");
/* 322 */     String host = context.header("Host");
/*     */     
/* 324 */     String ctx = "//";
/* 325 */     if (StrUtil.isNotEmpty(httpHost)) {
/* 326 */       ctx = ctx + httpHost;
/* 327 */     } else if (StrUtil.isNotEmpty(host)) {
/* 328 */       ctx = ctx + host;
/* 329 */       if (!host.contains(":") && StrUtil.isNotEmpty(realPort)) {
/* 330 */         ctx = ctx + ":" + realPort;
/*     */       }
/*     */     } else {
/* 333 */       host = context.url().split("/")[2];
/* 334 */       ctx = ctx + host;
/* 335 */       if (!host.contains(":") && StrUtil.isNotEmpty(realPort)) {
/* 336 */         ctx = ctx + ":" + realPort;
/*     */       }
/*     */     } 
/* 339 */     return ctx;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\config\AppFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */