/*     */ package com.cym.service;
/*     */ 
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.config.HomeConfig;
/*     */ import com.cym.ext.AsycPack;
/*     */ import com.cym.ext.ConfExt;
/*     */ import com.cym.ext.ConfFile;
/*     */ import com.cym.model.Bak;
/*     */ import com.cym.model.BakSub;
/*     */ import com.cym.model.Basic;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.Http;
/*     */ import com.cym.model.Location;
/*     */ import com.cym.model.Param;
/*     */ import com.cym.model.Password;
/*     */ import com.cym.model.Server;
/*     */ import com.cym.model.Stream;
/*     */ import com.cym.model.Template;
/*     */ import com.cym.model.Upstream;
/*     */ import com.cym.model.UpstreamServer;
/*     */ import com.cym.sqlhelper.bean.Sort;
/*     */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*     */ import com.cym.sqlhelper.utils.SqlHelper;
/*     */ import com.cym.utils.ToolUtils;
/*     */ import com.github.odiszapc.nginxparser.NgxBlock;
/*     */ import com.github.odiszapc.nginxparser.NgxConfig;
/*     */ import com.github.odiszapc.nginxparser.NgxDumper;
/*     */ import com.github.odiszapc.nginxparser.NgxEntry;
/*     */ import com.github.odiszapc.nginxparser.NgxParam;
/*     */ import java.io.File;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.aspect.annotation.Service;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Service
/*     */ public class ConfService
/*     */ {
/*  49 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   @Inject
/*     */   UpstreamService upstreamService;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   ServerService serverService;
/*     */   @Inject
/*     */   LocationService locationService;
/*     */   @Inject
/*     */   ParamService paramService;
/*     */   @Inject
/*     */   SqlHelper sqlHelper;
/*     */   @Inject
/*     */   TemplateService templateService;
/*     */   @Inject
/*     */   OperateLogService operateLogService;
/*     */   @Inject
/*     */   HomeConfig homeConfig;
/*     */   
/*     */   public synchronized ConfExt buildConf(Boolean decompose, Boolean check) {
/*  70 */     ConfExt confExt = new ConfExt();
/*  71 */     confExt.setFileList(new ArrayList());
/*     */     
/*  73 */     String nginxPath = this.settingService.get("nginxPath");
/*  74 */     if (check.booleanValue()) {
/*  75 */       nginxPath = this.homeConfig.home + "temp/nginx.conf";
/*     */     }
/*     */     
/*     */     try {
/*  79 */       NgxConfig ngxConfig = new NgxConfig();
/*     */ 
/*     */       
/*  82 */       List<Basic> basicList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Basic.class);
/*  83 */       for (Basic basic : basicList) {
/*  84 */         NgxParam ngxParam = new NgxParam();
/*  85 */         ngxParam.addValue(basic.getName().trim() + " " + basic.getValue().trim());
/*  86 */         ngxConfig.addEntry((NgxEntry)ngxParam);
/*     */       } 
/*     */ 
/*     */       
/*  90 */       List<Http> httpList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Http.class);
/*  91 */       boolean hasHttp = false;
/*  92 */       NgxBlock ngxBlockHttp = new NgxBlock();
/*  93 */       ngxBlockHttp.addValue("http");
/*  94 */       for (Http http : httpList) {
/*  95 */         NgxParam ngxParam = new NgxParam();
/*  96 */         ngxParam.addValue(http.getName().trim() + " " + http.getValue().trim());
/*  97 */         ngxBlockHttp.addEntry((NgxEntry)ngxParam);
/*     */         
/*  99 */         hasHttp = true;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 104 */       List<Upstream> upstreams = this.upstreamService.getListByProxyType(Integer.valueOf(0));
/*     */       
/* 106 */       for (Upstream upstream : upstreams) {
/* 107 */         NgxBlock ngxBlockServer = new NgxBlock();
/* 108 */         ngxBlockServer.addValue("upstream " + upstream.getName().trim());
/*     */         
/* 110 */         if (StrUtil.isNotEmpty(upstream.getDescr())) {
/* 111 */           String[] descrs = upstream.getDescr().split("\n");
/* 112 */           for (String d : descrs) {
/* 113 */             NgxParam ngxParam = new NgxParam();
/* 114 */             ngxParam.addValue("# " + d);
/* 115 */             ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 120 */         if (StrUtil.isNotEmpty(upstream.getTactics())) {
/* 121 */           NgxParam ngxParam = new NgxParam();
/* 122 */           ngxParam.addValue(upstream.getTactics());
/* 123 */           ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */         } 
/*     */         
/* 126 */         List<UpstreamServer> upstreamServers = this.upstreamService.getUpstreamServers(upstream.getId());
/* 127 */         for (UpstreamServer upstreamServer : upstreamServers) {
/* 128 */           NgxParam ngxParam = new NgxParam();
/* 129 */           ngxParam.addValue("server " + buildNodeStr(upstreamServer));
/* 130 */           ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */         } 
/*     */ 
/*     */         
/* 134 */         List<Param> paramList = this.paramService.getListByTypeId(upstream.getId(), "upstream");
/* 135 */         for (Param param : paramList) {
/* 136 */           setSameParam(param, ngxBlockServer);
/*     */         }
/*     */         
/* 139 */         hasHttp = true;
/*     */         
/* 141 */         if (decompose.booleanValue()) {
/* 142 */           addConfFile(confExt, "upstreams." + upstream.getName() + ".conf", ngxBlockServer);
/*     */           
/* 144 */           NgxParam ngxParam = new NgxParam();
/* 145 */           ngxParam.addValue("include " + (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/upstreams." + upstream.getName().replace(" ", "_").replace("*", "-") + ".conf");
/* 146 */           ngxBlockHttp.addEntry((NgxEntry)ngxParam);
/*     */           continue;
/*     */         } 
/* 149 */         ngxBlockHttp.addEntry((NgxEntry)ngxBlockServer);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 155 */       List<Server> servers = this.serverService.getListByProxyType(new String[] { "0" });
/* 156 */       for (Server server : servers) {
/* 157 */         if (server.getEnable() == null || !server.getEnable().booleanValue()) {
/*     */           continue;
/*     */         }
/*     */         
/* 161 */         NgxBlock ngxBlockServer = bulidBlockServer(server);
/* 162 */         hasHttp = true;
/*     */ 
/*     */         
/* 165 */         if (decompose.booleanValue()) {
/* 166 */           String name = "all";
/*     */           
/* 168 */           if (StrUtil.isNotEmpty(server.getServerName())) {
/* 169 */             name = server.getServerName();
/*     */           }
/*     */           
/* 172 */           addConfFile(confExt, name + ".conf", ngxBlockServer);
/*     */           
/* 174 */           NgxParam ngxParam = new NgxParam();
/* 175 */           ngxParam.addValue("include " + (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/" + name.replace(" ", "_").replace("*", "-") + ".conf");
/*     */           
/* 177 */           if (noContain(ngxBlockHttp, ngxParam)) {
/* 178 */             ngxBlockHttp.addEntry((NgxEntry)ngxParam);
/*     */           }
/*     */           continue;
/*     */         } 
/* 182 */         ngxBlockHttp.addEntry((NgxEntry)ngxBlockServer);
/*     */       } 
/*     */ 
/*     */       
/* 186 */       if (hasHttp) {
/* 187 */         ngxConfig.addEntry((NgxEntry)ngxBlockHttp);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 192 */       List<Stream> streamList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Stream.class);
/* 193 */       boolean hasStream = false;
/* 194 */       NgxBlock ngxBlockStream = new NgxBlock();
/* 195 */       ngxBlockStream.addValue("stream");
/* 196 */       for (Stream stream : streamList) {
/* 197 */         NgxParam ngxParam = new NgxParam();
/* 198 */         ngxParam.addValue(stream.getName() + " " + stream.getValue());
/* 199 */         ngxBlockStream.addEntry((NgxEntry)ngxParam);
/*     */         
/* 201 */         hasStream = true;
/*     */       } 
/*     */ 
/*     */       
/* 205 */       upstreams = this.upstreamService.getListByProxyType(Integer.valueOf(1));
/* 206 */       for (Upstream upstream : upstreams) {
/* 207 */         NgxBlock ngxBlockServer = buildBlockUpstream(upstream);
/*     */         
/* 209 */         if (decompose.booleanValue()) {
/* 210 */           addConfFile(confExt, "upstreams." + upstream.getName() + ".conf", ngxBlockServer);
/*     */           
/* 212 */           NgxParam ngxParam = new NgxParam();
/* 213 */           ngxParam.addValue("include " + (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/upstreams." + upstream.getName().replace(" ", "_").replace("*", "-") + ".conf");
/* 214 */           ngxBlockStream.addEntry((NgxEntry)ngxParam);
/*     */         } else {
/* 216 */           ngxBlockStream.addEntry((NgxEntry)ngxBlockServer);
/*     */         } 
/*     */         
/* 219 */         hasStream = true;
/*     */       } 
/*     */ 
/*     */       
/* 223 */       servers = this.serverService.getListByProxyType(new String[] { "1", "2" });
/* 224 */       for (Server server : servers) {
/* 225 */         if (server.getEnable() == null || !server.getEnable().booleanValue()) {
/*     */           continue;
/*     */         }
/*     */         
/* 229 */         NgxBlock ngxBlockServer = bulidBlockServer(server);
/*     */         
/* 231 */         if (decompose.booleanValue()) {
/* 232 */           String type = "";
/* 233 */           if (server.getProxyType().intValue() == 0) {
/* 234 */             type = "http";
/* 235 */           } else if (server.getProxyType().intValue() == 1) {
/* 236 */             type = "tcp";
/* 237 */           } else if (server.getProxyType().intValue() == 2) {
/* 238 */             type = "udp";
/*     */           } 
/*     */           
/* 241 */           addConfFile(confExt, type + "." + server.getListen() + ".conf", ngxBlockServer);
/*     */           
/* 243 */           NgxParam ngxParam = new NgxParam();
/* 244 */           ngxParam.addValue("include " + (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/" + type + "." + server.getListen() + ".conf");
/* 245 */           ngxBlockStream.addEntry((NgxEntry)ngxParam);
/*     */         } else {
/* 247 */           ngxBlockStream.addEntry((NgxEntry)ngxBlockServer);
/*     */         } 
/*     */         
/* 250 */         hasStream = true;
/*     */       } 
/*     */       
/* 253 */       if (hasStream) {
/* 254 */         ngxConfig.addEntry((NgxEntry)ngxBlockStream);
/*     */       }
/*     */       
/* 257 */       String conf = ToolUtils.handleConf((new NgxDumper(ngxConfig)).dump());
/* 258 */       confExt.setConf(conf);
/*     */       
/* 260 */       return confExt;
/* 261 */     } catch (Exception e) {
/* 262 */       this.logger.error(e.getMessage(), e);
/*     */ 
/*     */       
/* 265 */       return null;
/*     */     } 
/*     */   }
/*     */   public NgxBlock buildBlockUpstream(Upstream upstream) {
/* 269 */     NgxParam ngxParam = null;
/*     */     
/* 271 */     NgxBlock ngxBlockServer = new NgxBlock();
/*     */     
/* 273 */     ngxBlockServer.addValue("upstream " + upstream.getName());
/*     */     
/* 275 */     if (StrUtil.isNotEmpty(upstream.getDescr())) {
/* 276 */       String[] descrs = upstream.getDescr().split("\n");
/* 277 */       for (String d : descrs) {
/* 278 */         ngxParam = new NgxParam();
/* 279 */         ngxParam.addValue("# " + d);
/* 280 */         ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 285 */     if (StrUtil.isNotEmpty(upstream.getTactics())) {
/* 286 */       ngxParam = new NgxParam();
/* 287 */       ngxParam.addValue(upstream.getTactics());
/* 288 */       ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */     } 
/*     */     
/* 291 */     List<UpstreamServer> upstreamServers = this.upstreamService.getUpstreamServers(upstream.getId());
/* 292 */     for (UpstreamServer upstreamServer : upstreamServers) {
/* 293 */       ngxParam = new NgxParam();
/* 294 */       ngxParam.addValue("server " + buildNodeStr(upstreamServer));
/* 295 */       ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */     } 
/*     */ 
/*     */     
/* 299 */     List<Param> paramList = this.paramService.getListByTypeId(upstream.getId(), "upstream");
/* 300 */     for (Param param : paramList) {
/* 301 */       setSameParam(param, ngxBlockServer);
/*     */     }
/*     */     
/* 304 */     return ngxBlockServer;
/*     */   }
/*     */   
/*     */   public NgxBlock bulidBlockServer(Server server) {
/* 308 */     NgxParam ngxParam = null;
/*     */     
/* 310 */     NgxBlock ngxBlockServer = new NgxBlock();
/* 311 */     if (server.getProxyType().intValue() == 0) {
/* 312 */       ngxBlockServer.addValue("server");
/*     */       
/* 314 */       if (StrUtil.isNotEmpty(server.getDescr())) {
/* 315 */         String[] descrs = server.getDescr().split("\n");
/* 316 */         for (String d : descrs) {
/* 317 */           ngxParam = new NgxParam();
/* 318 */           ngxParam.addValue("# " + d);
/* 319 */           ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 324 */       if (StrUtil.isNotEmpty(server.getServerName())) {
/* 325 */         ngxParam = new NgxParam();
/* 326 */         ngxParam.addValue("server_name " + server.getServerName());
/* 327 */         ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */       } 
/*     */ 
/*     */       
/* 331 */       ngxParam = new NgxParam();
/* 332 */       String value = "listen " + server.getListen();
/* 333 */       if (server.getDef().intValue() == 1) {
/* 334 */         value = value + " default";
/*     */       }
/* 336 */       if (server.getProxyProtocol().intValue() == 1) {
/* 337 */         value = value + " proxy_protocol";
/*     */       }
/*     */       
/* 340 */       if (server.getSsl() != null && server.getSsl().intValue() == 1) {
/* 341 */         value = value + " ssl";
/* 342 */         if (server.getHttp2() != null && server.getHttp2().intValue() == 1) {
/* 343 */           value = value + " http2";
/*     */         }
/*     */       } 
/* 346 */       ngxParam.addValue(value);
/* 347 */       ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */ 
/*     */       
/* 350 */       if (StrUtil.isNotEmpty(server.getPasswordId())) {
/* 351 */         Password password = (Password)this.sqlHelper.findById(server.getPasswordId(), Password.class);
/*     */         
/* 353 */         if (password != null) {
/* 354 */           ngxParam = new NgxParam();
/* 355 */           ngxParam.addValue("auth_basic \"" + password.getDescr() + "\"");
/* 356 */           ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */           
/* 358 */           ngxParam = new NgxParam();
/* 359 */           ngxParam.addValue("auth_basic_user_file " + password.getPath());
/* 360 */           ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 365 */       setServerSsl(server, ngxBlockServer);
/*     */ 
/*     */       
/* 368 */       String type = "server";
/* 369 */       if (server.getProxyType().intValue() != 0) {
/* 370 */         type = type + server.getProxyType();
/*     */       }
/* 372 */       List<Param> paramList = this.paramService.getListByTypeId(server.getId(), type);
/* 373 */       for (Param param : paramList) {
/* 374 */         setSameParam(param, ngxBlockServer);
/*     */       }
/*     */       
/* 377 */       List<Location> locationList = this.serverService.getLocationByServerId(server.getId());
/*     */ 
/*     */       
/* 380 */       for (Location location : locationList) {
/* 381 */         NgxBlock ngxBlockLocation = new NgxBlock();
/* 382 */         ngxBlockLocation.addValue("location");
/* 383 */         ngxBlockLocation.addValue(location.getPath());
/*     */         
/* 385 */         if (StrUtil.isNotEmpty(location.getDescr())) {
/* 386 */           String[] descrs = location.getDescr().split("\n");
/* 387 */           for (String d : descrs) {
/* 388 */             ngxParam = new NgxParam();
/* 389 */             ngxParam.addValue("# " + d);
/* 390 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */           } 
/*     */         } 
/*     */         
/* 394 */         if (location.getType().intValue() == 0 || location.getType().intValue() == 2) {
/*     */           
/* 396 */           if (location.getType().intValue() == 0) {
/* 397 */             ngxParam = new NgxParam();
/* 398 */             ngxParam.addValue("proxy_pass " + location.getValue());
/* 399 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/* 400 */           } else if (location.getType().intValue() == 2) {
/* 401 */             Upstream upstream = (Upstream)this.sqlHelper.findById(location.getUpstreamId(), Upstream.class);
/* 402 */             if (upstream != null) {
/* 403 */               ngxParam = new NgxParam();
/* 404 */               ngxParam.addValue("proxy_pass " + location.getUpstreamType() + "://" + upstream.getName() + ((location.getUpstreamPath() != null) ? location.getUpstreamPath() : ""));
/* 405 */               ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */             } 
/*     */           } 
/*     */           
/* 409 */           if (location.getHeader().intValue() == 1) {
/* 410 */             ngxParam = new NgxParam();
/* 411 */             ngxParam.addValue("proxy_set_header Host $host");
/* 412 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */             
/* 414 */             ngxParam = new NgxParam();
/* 415 */             ngxParam.addValue("proxy_set_header X-Real-IP $remote_addr");
/* 416 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */             
/* 418 */             ngxParam = new NgxParam();
/* 419 */             ngxParam.addValue("proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for");
/* 420 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */             
/* 422 */             ngxParam = new NgxParam();
/* 423 */             ngxParam.addValue("proxy_set_header X-Forwarded-Host $http_host");
/* 424 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */             
/* 426 */             ngxParam = new NgxParam();
/* 427 */             ngxParam.addValue("proxy_set_header X-Forwarded-Port $server_port");
/* 428 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */             
/* 430 */             ngxParam = new NgxParam();
/* 431 */             ngxParam.addValue("proxy_set_header X-Forwarded-Proto $scheme");
/* 432 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */           } 
/*     */           
/* 435 */           if (location.getWebsocket().intValue() == 1) {
/* 436 */             ngxParam = new NgxParam();
/* 437 */             ngxParam.addValue("proxy_http_version 1.1");
/* 438 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */             
/* 440 */             ngxParam = new NgxParam();
/* 441 */             ngxParam.addValue("proxy_set_header Upgrade $http_upgrade");
/* 442 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */             
/* 444 */             ngxParam = new NgxParam();
/* 445 */             ngxParam.addValue("proxy_set_header Connection \"upgrade\"");
/* 446 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */           } 
/*     */           
/* 449 */           if (server.getSsl().intValue() == 1 && server.getRewrite().intValue() == 1) {
/* 450 */             ngxParam = new NgxParam();
/* 451 */             ngxParam.addValue("proxy_redirect http:// https://");
/* 452 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */           }
/*     */         
/* 455 */         } else if (location.getType().intValue() == 1) {
/* 456 */           if (location.getRootType() != null && location.getRootType().equals("alias")) {
/* 457 */             ngxParam = new NgxParam();
/* 458 */             ngxParam.addValue("alias " + ToolUtils.handlePath(location.getRootPath()));
/* 459 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */           } else {
/* 461 */             ngxParam = new NgxParam();
/* 462 */             ngxParam.addValue("root " + ToolUtils.handlePath(location.getRootPath()));
/* 463 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */           } 
/*     */           
/* 466 */           if (StrUtil.isNotEmpty(location.getRootPage())) {
/* 467 */             ngxParam = new NgxParam();
/* 468 */             ngxParam.addValue("index " + location.getRootPage());
/* 469 */             ngxBlockLocation.addEntry((NgxEntry)ngxParam);
/*     */           }
/*     */         
/* 472 */         } else if (location.getType().intValue() == 3) {
/*     */         
/*     */         } 
/*     */ 
/*     */         
/* 477 */         paramList = this.paramService.getListByTypeId(location.getId(), "location");
/* 478 */         for (Param param : paramList) {
/* 479 */           setSameParam(param, ngxBlockLocation);
/*     */         }
/*     */         
/* 482 */         ngxBlockServer.addEntry((NgxEntry)ngxBlockLocation);
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 487 */       ngxBlockServer.addValue("server");
/*     */ 
/*     */       
/* 490 */       ngxParam = new NgxParam();
/* 491 */       String value = "listen " + server.getListen();
/* 492 */       if (server.getProxyProtocol().intValue() == 1) {
/* 493 */         value = value + " proxy_protocol";
/*     */       }
/* 495 */       if (server.getProxyType().intValue() == 2) {
/* 496 */         value = value + " udp reuseport";
/*     */       }
/* 498 */       if (server.getSsl() != null && server.getSsl().intValue() == 1) {
/* 499 */         value = value + " ssl";
/*     */       }
/*     */       
/* 502 */       ngxParam.addValue(value);
/* 503 */       ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */ 
/*     */       
/* 506 */       Upstream upstream = (Upstream)this.sqlHelper.findById(server.getProxyUpstreamId(), Upstream.class);
/* 507 */       if (upstream != null) {
/* 508 */         ngxParam = new NgxParam();
/* 509 */         ngxParam.addValue("proxy_pass " + upstream.getName());
/* 510 */         ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */       } 
/*     */ 
/*     */       
/* 514 */       setServerSsl(server, ngxBlockServer);
/*     */ 
/*     */       
/* 517 */       String type = "server";
/* 518 */       if (server.getProxyType().intValue() != 0) {
/* 519 */         type = type + server.getProxyType();
/*     */       }
/* 521 */       List<Param> paramList = this.paramService.getListByTypeId(server.getId(), type);
/* 522 */       for (Param param : paramList) {
/* 523 */         setSameParam(param, ngxBlockServer);
/*     */       }
/*     */     } 
/*     */     
/* 527 */     return ngxBlockServer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setServerSsl(Server server, NgxBlock ngxBlockServer) {
/* 537 */     NgxParam ngxParam = null;
/* 538 */     if (server.getSsl().intValue() == 1) {
/* 539 */       if (StrUtil.isNotEmpty(server.getPem()) && StrUtil.isNotEmpty(server.getKey())) {
/* 540 */         ngxParam = new NgxParam();
/* 541 */         ngxParam.addValue("ssl_certificate " + ToolUtils.handlePath(server.getPem()));
/* 542 */         ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */         
/* 544 */         ngxParam = new NgxParam();
/* 545 */         ngxParam.addValue("ssl_certificate_key " + ToolUtils.handlePath(server.getKey()));
/* 546 */         ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */         
/* 548 */         if (StrUtil.isNotEmpty(server.getProtocols())) {
/* 549 */           ngxParam = new NgxParam();
/* 550 */           ngxParam.addValue("ssl_protocols " + server.getProtocols());
/* 551 */           ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 557 */       if (server.getProxyType().intValue() == 0 && server.getRewrite().intValue() == 1) {
/* 558 */         if (StrUtil.isNotEmpty(server.getRewriteListen())) {
/* 559 */           ngxParam = new NgxParam();
/* 560 */           String reValue = "listen " + server.getRewriteListen();
/* 561 */           if (server.getDef().intValue() == 1) {
/* 562 */             reValue = reValue + " default";
/*     */           }
/* 564 */           if (server.getProxyProtocol().intValue() == 1) {
/* 565 */             reValue = reValue + " proxy_protocol";
/*     */           }
/* 567 */           ngxParam.addValue(reValue);
/* 568 */           ngxBlockServer.addEntry((NgxEntry)ngxParam);
/*     */         } 
/*     */         
/* 571 */         String port = "";
/* 572 */         if (server.getListen().contains(":")) {
/* 573 */           port = server.getListen().split(":")[1];
/*     */         } else {
/* 575 */           port = server.getListen();
/*     */         } 
/*     */         
/* 578 */         NgxBlock ngxBlock = new NgxBlock();
/* 579 */         ngxBlock.addValue("if ($scheme = http)");
/* 580 */         ngxParam = new NgxParam();
/*     */         
/* 582 */         ngxParam.addValue("return 301 https://$host:" + port + "$request_uri");
/* 583 */         ngxBlock.addEntry((NgxEntry)ngxParam);
/*     */         
/* 585 */         ngxBlockServer.addEntry((NgxEntry)ngxBlock);
/*     */       } 
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
/*     */   private boolean noContain(NgxBlock ngxBlockHttp, NgxParam ngxParam) {
/* 599 */     for (NgxEntry ngxEntry : ngxBlockHttp.getEntries()) {
/* 600 */       if (ngxEntry.toString().equals(ngxParam.toString())) {
/* 601 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 605 */     return true;
/*     */   }
/*     */   
/*     */   public String buildNodeStr(UpstreamServer upstreamServer) {
/* 609 */     String status = "";
/* 610 */     if (!"none".equals(upstreamServer.getStatus())) {
/* 611 */       status = upstreamServer.getStatus();
/*     */     }
/*     */     
/* 614 */     if (upstreamServer.getServer().contains(":")) {
/* 615 */       upstreamServer.setServer("[" + upstreamServer.getServer() + "]");
/*     */     }
/*     */     
/* 618 */     String conf = upstreamServer.getServer() + ":" + upstreamServer.getPort();
/* 619 */     if (upstreamServer.getWeight() != null) {
/* 620 */       conf = conf + " weight=" + upstreamServer.getWeight();
/*     */     }
/* 622 */     if (upstreamServer.getFailTimeout() != null) {
/* 623 */       conf = conf + " fail_timeout=" + upstreamServer.getFailTimeout() + "s";
/*     */     }
/* 625 */     if (upstreamServer.getMaxFails() != null) {
/* 626 */       conf = conf + " max_fails=" + upstreamServer.getMaxFails();
/*     */     }
/* 628 */     if (upstreamServer.getMaxConns() != null) {
/* 629 */       conf = conf + " max_conns=" + upstreamServer.getMaxConns();
/*     */     }
/* 631 */     conf = conf + " " + status;
/* 632 */     return conf;
/*     */   }
/*     */   
/*     */   private void setSameParam(Param param, NgxBlock ngxBlock) {
/* 636 */     if (StrUtil.isEmpty(param.getTemplateValue())) {
/* 637 */       NgxParam ngxParam = new NgxParam();
/* 638 */       if (StrUtil.isNotEmpty(param.getName().trim())) {
/* 639 */         param.setName(param.getName().trim() + " ");
/*     */       }
/*     */       
/* 642 */       ngxParam.addValue(param.getName() + param.getValue().trim());
/* 643 */       ngxBlock.addEntry((NgxEntry)ngxParam);
/*     */     } else {
/* 645 */       List<Param> params = this.templateService.getParamList(param.getTemplateValue());
/* 646 */       for (Param paramSub : params) {
/* 647 */         NgxParam ngxParam = new NgxParam();
/* 648 */         if (StrUtil.isNotEmpty(paramSub.getName().trim())) {
/* 649 */           paramSub.setName(paramSub.getName().trim() + " ");
/*     */         }
/*     */         
/* 652 */         ngxParam.addValue(paramSub.getName() + paramSub.getValue().trim());
/* 653 */         ngxBlock.addEntry((NgxEntry)ngxParam);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addConfFile(ConfExt confExt, String name, NgxBlock ngxBlockServer) {
/* 659 */     name = name.replace(" ", "_").replace("*", "-");
/*     */     
/* 661 */     boolean hasSameName = false;
/* 662 */     for (ConfFile confFile : confExt.getFileList()) {
/* 663 */       if (confFile.getName().equals(name)) {
/* 664 */         confFile.setConf(confFile.getConf() + "\n" + buildStr(ngxBlockServer));
/* 665 */         hasSameName = true;
/*     */       } 
/*     */     } 
/*     */     
/* 669 */     if (!hasSameName) {
/* 670 */       ConfFile confFile = new ConfFile();
/* 671 */       confFile.setName(name);
/* 672 */       confFile.setConf(buildStr(ngxBlockServer));
/* 673 */       confExt.getFileList().add(confFile);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private String buildStr(NgxBlock ngxBlockServer) {
/* 679 */     NgxConfig ngxConfig = new NgxConfig();
/* 680 */     ngxConfig.addEntry((NgxEntry)ngxBlockServer);
/*     */     
/* 682 */     return ToolUtils.handleConf((new NgxDumper(ngxConfig)).dump());
/*     */   }
/*     */ 
/*     */   
/*     */   public void replace(String nginxPath, String nginxContent, List<String> subContent, List<String> subName, Boolean isReplace, String adminName) {
/* 687 */     String beforeConf = null;
/* 688 */     if (isReplace.booleanValue())
/*     */     {
/* 690 */       beforeConf = FileUtil.readString(nginxPath, StandardCharsets.UTF_8);
/*     */     }
/*     */     
/* 693 */     String confd = (new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/";
/*     */     
/* 695 */     FileUtil.del(confd);
/* 696 */     FileUtil.mkdir(confd);
/*     */ 
/*     */     
/* 699 */     FileUtil.writeString(nginxContent, nginxPath.replace(" ", "_"), StandardCharsets.UTF_8);
/* 700 */     String decompose = this.settingService.get("decompose");
/*     */     
/* 702 */     if ("true".equals(decompose))
/*     */     {
/* 704 */       if (subContent != null) {
/* 705 */         for (int i = 0; i < subContent.size(); i++) {
/* 706 */           String tagert = ((new File(nginxPath)).getParent().replace("\\", "/") + "/conf.d/" + (String)subName.get(i)).replace(" ", "_");
/* 707 */           FileUtil.writeString(subContent.get(i), tagert, StandardCharsets.UTF_8);
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 713 */     if (isReplace.booleanValue()) {
/* 714 */       Bak bak = new Bak();
/* 715 */       bak.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
/* 716 */       bak.setContent(nginxContent);
/* 717 */       this.sqlHelper.insert(bak);
/*     */ 
/*     */       
/* 720 */       for (int i = 0; i < subContent.size(); i++) {
/* 721 */         BakSub bakSub = new BakSub();
/* 722 */         bakSub.setBakId(bak.getId());
/*     */         
/* 724 */         bakSub.setName(subName.get(i));
/* 725 */         bakSub.setContent(subContent.get(i));
/* 726 */         this.sqlHelper.insert(bakSub);
/*     */       } 
/*     */ 
/*     */       
/* 730 */       if (StrUtil.isNotEmpty(adminName)) {
/* 731 */         this.operateLogService.addLog(beforeConf, nginxContent, adminName);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public AsycPack getAsycPack(String[] asycData) {
/* 737 */     AsycPack asycPack = new AsycPack();
/* 738 */     if (hasStr(asycData, "basic") || hasStr(asycData, "all")) {
/* 739 */       asycPack.setBasicList(this.sqlHelper.findAll(Basic.class));
/*     */     }
/*     */     
/* 742 */     if (hasStr(asycData, "http") || hasStr(asycData, "all")) {
/* 743 */       asycPack.setHttpList(this.sqlHelper.findAll(Http.class));
/*     */     }
/*     */     
/* 746 */     if (hasStr(asycData, "server") || hasStr(asycData, "all")) {
/* 747 */       List<Server> serverList = this.sqlHelper.findAll(Server.class);
/* 748 */       for (Server server : serverList) {
/* 749 */         if (StrUtil.isNotEmpty(server.getPem()) && FileUtil.exist(server.getPem())) {
/* 750 */           server.setPemStr(FileUtil.readString(server.getPem(), StandardCharsets.UTF_8));
/*     */         }
/*     */         
/* 753 */         if (StrUtil.isNotEmpty(server.getKey()) && FileUtil.exist(server.getKey())) {
/* 754 */           server.setKeyStr(FileUtil.readString(server.getKey(), StandardCharsets.UTF_8));
/*     */         }
/*     */       } 
/* 757 */       asycPack.setServerList(serverList);
/* 758 */       asycPack.setLocationList(this.sqlHelper.findAll(Location.class));
/*     */     } 
/*     */     
/* 761 */     if (hasStr(asycData, "password") || hasStr(asycData, "all")) {
/* 762 */       List<Password> passwordList = this.sqlHelper.findAll(Password.class);
/* 763 */       for (Password password : passwordList) {
/* 764 */         if (StrUtil.isNotEmpty(password.getPath()) && FileUtil.exist(password.getPath())) {
/* 765 */           password.setPathStr(FileUtil.readString(password.getPath(), StandardCharsets.UTF_8));
/*     */         }
/*     */       } 
/*     */       
/* 769 */       asycPack.setPasswordList(passwordList);
/*     */     } 
/*     */     
/* 772 */     if (hasStr(asycData, "upstream") || hasStr(asycData, "all")) {
/* 773 */       asycPack.setUpstreamList(this.sqlHelper.findAll(Upstream.class));
/* 774 */       asycPack.setUpstreamServerList(this.sqlHelper.findAll(UpstreamServer.class));
/*     */     } 
/*     */     
/* 777 */     if (hasStr(asycData, "stream") || hasStr(asycData, "all")) {
/* 778 */       asycPack.setStreamList(this.sqlHelper.findAll(Stream.class));
/*     */     }
/*     */     
/* 781 */     if (hasStr(asycData, "param") || hasStr(asycData, "all")) {
/* 782 */       asycPack.setTemplateList(this.sqlHelper.findAll(Template.class));
/* 783 */       asycPack.setParamList(this.sqlHelper.findAll(Param.class));
/*     */     } 
/*     */     
/* 786 */     return asycPack;
/*     */   }
/*     */   
/*     */   private boolean hasStr(String[] asycData, String data) {
/* 790 */     for (String str : asycData) {
/* 791 */       if (str.equals(data)) {
/* 792 */         return true;
/*     */       }
/*     */     } 
/* 795 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAsycPack(AsycPack asycPack) {
/*     */     try {
/* 802 */       if (asycPack.getBasicList() != null) {
/* 803 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Basic.class);
/* 804 */         this.sqlHelper.insertAll(asycPack.getBasicList());
/*     */       } 
/*     */       
/* 807 */       if (asycPack.getHttpList() != null) {
/* 808 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Http.class);
/* 809 */         this.sqlHelper.insertAll(asycPack.getHttpList());
/*     */       } 
/*     */       
/* 812 */       if (asycPack.getServerList() != null) {
/* 813 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Server.class);
/* 814 */         this.sqlHelper.insertAll(asycPack.getServerList());
/* 815 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Location.class);
/* 816 */         this.sqlHelper.insertAll(asycPack.getLocationList());
/*     */         
/* 818 */         for (Server server : asycPack.getServerList()) {
/*     */           try {
/* 820 */             if (StrUtil.isNotEmpty(server.getPem()) && StrUtil.isNotEmpty(server.getPemStr())) {
/* 821 */               FileUtil.writeString(server.getPemStr(), server.getPem(), StandardCharsets.UTF_8);
/*     */             }
/* 823 */             if (StrUtil.isNotEmpty(server.getKey()) && StrUtil.isNotEmpty(server.getKeyStr())) {
/* 824 */               FileUtil.writeString(server.getKeyStr(), server.getKey(), StandardCharsets.UTF_8);
/*     */             }
/* 826 */           } catch (Exception e) {
/* 827 */             e.printStackTrace();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 832 */       if (asycPack.getUpstreamList() != null) {
/* 833 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Upstream.class);
/* 834 */         this.sqlHelper.insertAll(asycPack.getUpstreamList());
/* 835 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), UpstreamServer.class);
/* 836 */         this.sqlHelper.insertAll(asycPack.getUpstreamServerList());
/*     */       } 
/*     */       
/* 839 */       if (asycPack.getStreamList() != null) {
/* 840 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Stream.class);
/* 841 */         this.sqlHelper.insertAll(asycPack.getStreamList());
/*     */       } 
/*     */       
/* 844 */       if (asycPack.getTemplateList() != null) {
/* 845 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Template.class);
/* 846 */         this.sqlHelper.insertAll(asycPack.getTemplateList());
/* 847 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Param.class);
/* 848 */         this.sqlHelper.insertAll(asycPack.getParamList());
/*     */       } 
/*     */       
/* 851 */       if (asycPack.getPasswordList() != null) {
/* 852 */         this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Password.class);
/* 853 */         this.sqlHelper.insertAll(asycPack.getPasswordList());
/*     */         
/* 855 */         for (Password password : asycPack.getPasswordList()) {
/* 856 */           if (StrUtil.isNotEmpty(password.getPath()) && StrUtil.isNotEmpty(password.getPathStr())) {
/* 857 */             FileUtil.writeString(password.getPathStr(), password.getPath(), StandardCharsets.UTF_8);
/*     */           }
/*     */         }
/*     */       
/*     */       } 
/* 862 */     } catch (Exception e) {
/* 863 */       this.logger.error(e.getMessage(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Cert> getApplyCerts() {
/* 869 */     List<Cert> certs = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).ne(Cert::getType, Integer.valueOf(1)), Cert.class);
/* 870 */     return certs;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\ConfService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */