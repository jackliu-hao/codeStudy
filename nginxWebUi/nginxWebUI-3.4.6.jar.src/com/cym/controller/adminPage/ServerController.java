/*     */ package com.cym.controller.adminPage;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.http.HttpUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.ext.ServerExt;
/*     */ import com.cym.model.Cert;
/*     */ import com.cym.model.Http;
/*     */ import com.cym.model.Location;
/*     */ import com.cym.model.Password;
/*     */ import com.cym.model.Remote;
/*     */ import com.cym.model.Server;
/*     */ import com.cym.model.Stream;
/*     */ import com.cym.model.Upstream;
/*     */ import com.cym.model.Www;
/*     */ import com.cym.service.ConfService;
/*     */ import com.cym.service.ParamService;
/*     */ import com.cym.service.ServerService;
/*     */ import com.cym.service.SettingService;
/*     */ import com.cym.service.UpstreamService;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.sqlhelper.bean.Sort;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.SnowFlakeUtils;
/*     */ import com.cym.utils.TelnetUtils;
/*     */ import com.cym.utils.ToolUtils;
/*     */ import com.github.odiszapc.nginxparser.NgxBlock;
/*     */ import com.github.odiszapc.nginxparser.NgxConfig;
/*     */ import com.github.odiszapc.nginxparser.NgxDumper;
/*     */ import com.github.odiszapc.nginxparser.NgxEntry;
/*     */ import com.github.odiszapc.nginxparser.NgxParam;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.noear.solon.core.handle.UploadedFile;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Controller
/*     */ @Mapping("/adminPage/server")
/*     */ public class ServerController
/*     */   extends BaseController
/*     */ {
/*  54 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   @Inject
/*     */   ServerService serverService;
/*     */   @Inject
/*     */   UpstreamService upstreamService;
/*     */   @Inject
/*     */   ParamService paramService;
/*     */   @Inject
/*     */   SettingService settingService;
/*     */   @Inject
/*     */   ConfService confService;
/*     */   
/*     */   @Mapping("")
/*     */   public ModelAndView index(ModelAndView modelAndView, Page page, String keywords) {
/*  68 */     page = this.serverService.search(page, keywords);
/*     */     
/*  70 */     List<ServerExt> exts = new ArrayList<>();
/*  71 */     for (Server server : page.getRecords()) {
/*  72 */       ServerExt serverExt = new ServerExt();
/*  73 */       if (server.getEnable() == null) {
/*  74 */         server.setEnable(Boolean.valueOf(false));
/*     */       }
/*     */ 
/*     */       
/*  78 */       if (StrUtil.isNotEmpty(server.getDescr())) {
/*  79 */         server.setDescr(server.getDescr().replace("\n", "<br>").replace(" ", "&nbsp;"));
/*     */       }
/*     */       
/*  82 */       serverExt.setServer(server);
/*  83 */       if (server.getProxyType().intValue() == 0) {
/*  84 */         serverExt.setLocationStr(buildLocationStr(server.getId()));
/*     */       } else {
/*  86 */         Upstream upstream = (Upstream)this.sqlHelper.findById(server.getProxyUpstreamId(), Upstream.class);
/*  87 */         serverExt.setLocationStr(this.m.get("serverStr.server") + ": " + ((upstream != null) ? upstream.getName() : ""));
/*     */       } 
/*     */       
/*  90 */       exts.add(serverExt);
/*     */     } 
/*  92 */     page.setRecords(exts);
/*     */     
/*  94 */     modelAndView.put("page", page);
/*     */     
/*  96 */     List<Upstream> upstreamList = this.upstreamService.getListByProxyType(Integer.valueOf(0));
/*  97 */     modelAndView.put("upstreamList", upstreamList);
/*  98 */     modelAndView.put("upstreamSize", Integer.valueOf(upstreamList.size()));
/*     */     
/* 100 */     List<Upstream> upstreamTcpList = this.upstreamService.getListByProxyType(Integer.valueOf(1));
/* 101 */     modelAndView.put("upstreamTcpList", upstreamTcpList);
/* 102 */     modelAndView.put("upstreamTcpSize", Integer.valueOf(upstreamTcpList.size()));
/*     */     
/* 104 */     List<Cert> certs = this.sqlHelper.findAll(Cert.class);
/* 105 */     for (Cert cert : certs) {
/* 106 */       if (cert.getType().intValue() == 0 || cert.getType().intValue() == 2) {
/* 107 */         cert.setDomain(cert.getDomain() + "(" + cert.getEncryption() + ")");
/*     */       }
/*     */     } 
/* 110 */     modelAndView.put("certList", certs);
/* 111 */     modelAndView.put("wwwList", this.sqlHelper.findAll(Www.class));
/*     */     
/* 113 */     modelAndView.put("passwordList", this.sqlHelper.findAll(Password.class));
/*     */     
/* 115 */     modelAndView.put("keywords", keywords);
/* 116 */     modelAndView.view("/adminPage/server/index.html");
/* 117 */     return modelAndView;
/*     */   }
/*     */   
/*     */   private String buildLocationStr(String id) {
/* 121 */     List<String> str = new ArrayList<>();
/* 122 */     List<Location> locations = this.serverService.getLocationByServerId(id);
/*     */     
/* 124 */     for (Location location : locations) {
/* 125 */       String descr = this.m.get("commonStr.descr");
/* 126 */       if (StrUtil.isNotEmpty(location.getDescr())) {
/* 127 */         descr = location.getDescr();
/*     */       }
/*     */       
/* 130 */       if (location.getType().intValue() == 0) {
/* 131 */         str.add("<span class='path'>" + location.getPath() + "</span><a class='descrBtn' href='javascript:editLocationDescr(\"" + location
/* 132 */             .getId() + "\")'>" + descr + "</a><br><span class='value'>" + location
/*     */             
/* 134 */             .getValue() + "</span>"); continue;
/* 135 */       }  if (location.getType().intValue() == 1) {
/* 136 */         str.add("<span class='path'>" + location.getPath() + "</span><a class='descrBtn' href='javascript:editLocationDescr(\"" + location
/* 137 */             .getId() + "\")'>" + descr + "</a><br><span class='value'>" + location
/*     */ 
/*     */             
/* 140 */             .getRootPath() + "</span>"); continue;
/* 141 */       }  if (location.getType().intValue() == 2) {
/* 142 */         Upstream upstream = (Upstream)this.sqlHelper.findById(location.getUpstreamId(), Upstream.class);
/* 143 */         if (upstream != null)
/* 144 */           str.add("<span class='path'>" + location.getPath() + "</span><a class='descrBtn' href='javascript:editLocationDescr(\"" + location
/* 145 */               .getId() + "\")'>" + descr + "</a><br><span class='value'>http://" + upstream
/*     */               
/* 147 */               .getName() + ((location.getUpstreamPath() != null) ? location.getUpstreamPath() : "") + "</span>");  continue;
/*     */       } 
/* 149 */       if (location.getType().intValue() == 3) {
/* 150 */         str.add("<span class='path'>" + location.getPath() + "</span><a class='descrBtn' href='javascript:editLocationDescr(\"" + location
/* 151 */             .getId() + "\")'>" + descr + "</a>");
/*     */       }
/*     */     } 
/*     */     
/* 155 */     return StrUtil.join("<br>", str);
/*     */   }
/*     */   
/*     */   @Mapping("addOver")
/*     */   public JsonResult addOver(String serverJson, String serverParamJson, String locationJson) {
/* 160 */     Server server = (Server)JSONUtil.toBean(serverJson, Server.class);
/* 161 */     List<Location> locations = JSONUtil.toList(JSONUtil.parseArray(locationJson), Location.class);
/*     */     
/* 163 */     if (StrUtil.isEmpty(server.getId())) {
/* 164 */       server.setSeq(SnowFlakeUtils.getId());
/*     */     }
/*     */     
/* 167 */     if (server.getProxyType().intValue() == 0) {
/*     */       try {
/* 169 */         this.serverService.addOver(server, serverParamJson, locations);
/* 170 */       } catch (Exception e) {
/* 171 */         return renderError(e.getMessage());
/*     */       } 
/*     */     } else {
/* 174 */       this.serverService.addOverTcp(server, serverParamJson);
/*     */     } 
/*     */     
/* 177 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("setEnable")
/*     */   public JsonResult setEnable(Server server) {
/* 182 */     this.sqlHelper.updateById(server);
/* 183 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("detail")
/*     */   public JsonResult detail(String id) {
/* 188 */     Server server = (Server)this.sqlHelper.findById(id, Server.class);
/*     */     
/* 190 */     ServerExt serverExt = new ServerExt();
/* 191 */     serverExt.setServer(server);
/* 192 */     List<Location> list = this.serverService.getLocationByServerId(id);
/* 193 */     for (Location location : list) {
/* 194 */       String str = this.paramService.getJsonByTypeId(location.getId(), "location");
/* 195 */       location.setLocationParamJson((str != null) ? str : null);
/*     */     } 
/* 197 */     serverExt.setLocationList(list);
/* 198 */     String json = this.paramService.getJsonByTypeId(server.getId(), "server");
/* 199 */     serverExt.setParamJson((json != null) ? json : null);
/*     */     
/* 201 */     return renderSuccess(serverExt);
/*     */   }
/*     */   
/*     */   @Mapping("del")
/*     */   public JsonResult del(String id) {
/* 206 */     this.serverService.deleteById(id);
/*     */     
/* 208 */     return renderSuccess();
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
/*     */   @Mapping("importServer")
/*     */   public JsonResult importServer(String nginxPath) {
/* 222 */     if (StrUtil.isEmpty(nginxPath) || !FileUtil.exist(nginxPath)) {
/* 223 */       return renderError(this.m.get("serverStr.fileNotExist"));
/*     */     }
/*     */     
/*     */     try {
/* 227 */       this.serverService.importServer(nginxPath);
/* 228 */       return renderSuccess(this.m.get("serverStr.importSuccess"));
/* 229 */     } catch (Exception e) {
/* 230 */       this.logger.error(e.getMessage(), e);
/* 231 */       return renderError(this.m.get("serverStr.importFail"));
/*     */     } 
/*     */   }
/*     */   
/*     */   @Mapping("testPort")
/*     */   public JsonResult testPort() {
/* 237 */     List<Server> servers = this.sqlHelper.findAll(Server.class);
/*     */     
/* 239 */     List<String> ips = new ArrayList<>();
/* 240 */     for (Server server : servers) {
/* 241 */       String ip = "";
/* 242 */       String port = "";
/* 243 */       if (server.getListen().contains(":")) {
/* 244 */         String[] strArray = server.getListen().split(":");
/*     */         
/* 246 */         port = strArray[strArray.length - 1];
/* 247 */         ip = server.getListen().replace(":" + port, "");
/*     */       } else {
/* 249 */         ip = "127.0.0.1";
/* 250 */         port = server.getListen();
/*     */       } 
/*     */       
/* 253 */       if (TelnetUtils.isRunning(ip, Integer.parseInt(port)) && !ips.contains(server.getListen())) {
/* 254 */         ips.add(server.getListen());
/*     */       }
/*     */     } 
/*     */     
/* 258 */     if (ips.size() == 0) {
/* 259 */       return renderSuccess();
/*     */     }
/* 261 */     return renderError(this.m.get("serverStr.portUserdList") + ": " + StrUtil.join(" , ", ips));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("editDescr")
/*     */   public JsonResult editDescr(String id, String descr) {
/* 268 */     Server server = new Server();
/* 269 */     server.setId(id);
/* 270 */     server.setDescr(descr);
/* 271 */     this.sqlHelper.updateById(server);
/*     */     
/* 273 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("preview")
/*     */   public JsonResult preview(String id, String type) {
/* 278 */     NgxBlock ngxBlock = null;
/* 279 */     if (type.equals("server")) {
/* 280 */       Server server = (Server)this.sqlHelper.findById(id, Server.class);
/* 281 */       ngxBlock = this.confService.bulidBlockServer(server);
/* 282 */     } else if (type.equals("upstream")) {
/* 283 */       Upstream upstream = (Upstream)this.sqlHelper.findById(id, Upstream.class);
/* 284 */       ngxBlock = this.confService.buildBlockUpstream(upstream);
/* 285 */     } else if (type.equals("http")) {
/* 286 */       List<Http> httpList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Http.class);
/* 287 */       ngxBlock = new NgxBlock();
/* 288 */       ngxBlock.addValue("http");
/* 289 */       for (Http http : httpList) {
/* 290 */         NgxParam ngxParam = new NgxParam();
/* 291 */         ngxParam.addValue(http.getName().trim() + " " + http.getValue().trim());
/* 292 */         ngxBlock.addEntry((NgxEntry)ngxParam);
/*     */       } 
/* 294 */     } else if (type.equals("stream")) {
/* 295 */       List<Stream> streamList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Stream.class);
/* 296 */       ngxBlock = new NgxBlock();
/* 297 */       ngxBlock.addValue("stream");
/* 298 */       for (Stream stream : streamList) {
/* 299 */         NgxParam ngxParam = new NgxParam();
/* 300 */         ngxParam.addValue(stream.getName() + " " + stream.getValue());
/* 301 */         ngxBlock.addEntry((NgxEntry)ngxParam);
/*     */       } 
/*     */     } 
/* 304 */     NgxConfig ngxConfig = new NgxConfig();
/* 305 */     ngxConfig.addEntry((NgxEntry)ngxBlock);
/*     */     
/* 307 */     String conf = ToolUtils.handleConf((new NgxDumper(ngxConfig)).dump());
/*     */     
/* 309 */     return renderSuccess(conf);
/*     */   }
/*     */   
/*     */   @Mapping("setOrder")
/*     */   public JsonResult setOrder(String id, Integer count) {
/* 314 */     this.serverService.setSeq(id, count);
/* 315 */     return renderSuccess();
/*     */   }
/*     */   
/*     */   @Mapping("getDescr")
/*     */   public JsonResult getDescr(String id) {
/* 320 */     Server server = (Server)this.sqlHelper.findById(id, Server.class);
/* 321 */     return renderSuccess(server.getDescr());
/*     */   }
/*     */   
/*     */   @Mapping("getLocationDescr")
/*     */   public JsonResult getLocationDescr(String id) {
/* 326 */     Location location = (Location)this.sqlHelper.findById(id, Location.class);
/* 327 */     return renderSuccess(location.getDescr());
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("setLocationDescr")
/*     */   public JsonResult setLocationDescr(String id, String descr) {
/* 333 */     Location location = new Location();
/* 334 */     location.setId(id);
/* 335 */     location.setDescr(descr);
/* 336 */     this.sqlHelper.updateById(location);
/*     */     
/* 338 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */   
/*     */   @Mapping("upload")
/*     */   public JsonResult upload(Context context, UploadedFile file) {
/*     */     try {
/* 345 */       File temp = new File(FileUtil.getTmpDir() + "/" + file.name);
/* 346 */       file.transferTo(temp);
/*     */ 
/*     */       
/* 349 */       File dest = new File(this.homeConfig.home + "cert/" + file.name);
/* 350 */       while (FileUtil.exist(dest)) {
/* 351 */         dest = new File(dest.getPath() + "_1");
/*     */       }
/* 353 */       FileUtil.move(temp, dest, true);
/*     */       
/* 355 */       String localType = (String)context.session("localType");
/* 356 */       if ("remote".equals(localType)) {
/* 357 */         Remote remote = (Remote)context.session("remote");
/*     */         
/* 359 */         HashMap<String, Object> paramMap = new HashMap<>();
/* 360 */         paramMap.put("file", temp);
/*     */         
/* 362 */         String rs = HttpUtil.post(remote.getProtocol() + "://" + remote.getIp() + ":" + remote.getPort() + "/upload", paramMap);
/* 363 */         JsonResult jsonResult = (JsonResult)JSONUtil.toBean(rs, JsonResult.class);
/* 364 */         FileUtil.del(temp);
/* 365 */         return jsonResult;
/*     */       } 
/*     */       
/* 368 */       return renderSuccess(dest.getPath().replace("\\", "/"));
/* 369 */     } catch (IllegalStateException|java.io.IOException e) {
/* 370 */       this.logger.error(e.getMessage(), e);
/*     */ 
/*     */       
/* 373 */       return renderError();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\ServerController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */