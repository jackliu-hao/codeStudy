/*     */ package com.cym.service;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.model.Location;
/*     */ import com.cym.model.Param;
/*     */ import com.cym.model.Server;
/*     */ import com.cym.sqlhelper.bean.BaseModel;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.sqlhelper.bean.Sort;
/*     */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionOrWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*     */ import com.cym.sqlhelper.utils.SqlHelper;
/*     */ import com.cym.utils.SnowFlakeUtils;
/*     */ import com.github.odiszapc.nginxparser.NgxBlock;
/*     */ import com.github.odiszapc.nginxparser.NgxConfig;
/*     */ import com.github.odiszapc.nginxparser.NgxEntry;
/*     */ import com.github.odiszapc.nginxparser.NgxParam;
/*     */ import com.github.odiszapc.nginxparser.NgxToken;
/*     */ import java.io.IOException;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.aspect.annotation.Service;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ @Service
/*     */ public class ServerService {
/*  36 */   Logger logger = LoggerFactory.getLogger(getClass());
/*     */   
/*     */   @Inject
/*     */   SqlHelper sqlHelper;
/*     */   
/*     */   public Page search(Page page, String keywords) {
/*  42 */     ConditionOrWrapper conditionOrWrapper = new ConditionOrWrapper();
/*  43 */     if (StrUtil.isNotEmpty(keywords)) {
/*  44 */       conditionOrWrapper.like(Server::getDescr, keywords.trim())
/*  45 */         .like(Server::getServerName, keywords.trim())
/*  46 */         .like(Server::getListen, keywords.trim());
/*     */       
/*  48 */       List<String> serverIds = this.sqlHelper.findPropertiesByQuery((ConditionWrapper)(new ConditionOrWrapper())
/*  49 */           .like(Location::getDescr, keywords)
/*  50 */           .like(Location::getValue, keywords)
/*  51 */           .like(Location::getPath, keywords), Location.class, Location::getServerId);
/*     */       
/*  53 */       conditionOrWrapper.in(BaseModel::getId, serverIds);
/*     */     } 
/*     */     
/*  56 */     Sort sort = (new Sort()).add("seq", Sort.Direction.DESC);
/*     */     
/*  58 */     page = this.sqlHelper.findPage((ConditionWrapper)conditionOrWrapper, sort, page, Server.class);
/*     */     
/*  60 */     return page;
/*     */   }
/*     */   
/*     */   public void deleteById(String id) {
/*  64 */     this.sqlHelper.deleteById(id, Server.class);
/*  65 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("serverId", id), Location.class);
/*     */   }
/*     */   
/*     */   public List<Location> getLocationByServerId(String serverId) {
/*  69 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("serverId", serverId), Location.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addOver(Server server, String serverParamJson, List<Location> locations) throws Exception {
/*  74 */     if (server.getDef() != null && server.getDef().intValue() == 1) {
/*  75 */       clearDef();
/*     */     }
/*     */     
/*  78 */     this.sqlHelper.insertOrUpdate(server);
/*     */     
/*  80 */     List<Param> paramList = new ArrayList<>();
/*  81 */     if (StrUtil.isNotEmpty(serverParamJson) && JSONUtil.isTypeJSON(serverParamJson)) {
/*  82 */       paramList = JSONUtil.toList(JSONUtil.parseArray(serverParamJson), Param.class);
/*     */     }
/*  84 */     List<String> locationIds = this.sqlHelper.findIdsByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("serverId", server.getId()), Location.class);
/*  85 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionOrWrapper()).eq("serverId", server.getId()).in("locationId", locationIds), Param.class);
/*     */ 
/*     */     
/*  88 */     Collections.reverse(paramList);
/*  89 */     for (Param param : paramList) {
/*  90 */       param.setServerId(server.getId());
/*  91 */       this.sqlHelper.insert(param);
/*     */     } 
/*     */     
/*  94 */     List<Location> locationOlds = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("serverId", server.getId()), Location.class);
/*  95 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("serverId", server.getId()), Location.class);
/*     */     
/*  97 */     if (locations != null) {
/*     */       
/*  99 */       Collections.reverse(locations);
/*     */       
/* 101 */       for (Location location : locations) {
/* 102 */         location.setServerId(server.getId());
/* 103 */         location.setDescr(findLocationDescr(locationOlds, location));
/* 104 */         this.sqlHelper.insert(location);
/*     */         
/* 106 */         paramList = new ArrayList<>();
/* 107 */         if (StrUtil.isNotEmpty(location.getLocationParamJson()) && JSONUtil.isJson(location.getLocationParamJson())) {
/* 108 */           paramList = JSONUtil.toList(JSONUtil.parseArray(location.getLocationParamJson()), Param.class);
/*     */         }
/*     */ 
/*     */         
/* 112 */         Collections.reverse(paramList);
/* 113 */         for (Param param : paramList) {
/* 114 */           param.setLocationId(location.getId());
/* 115 */           this.sqlHelper.insert(param);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String findLocationDescr(List<Location> locationOlds, Location locationNew) {
/* 124 */     for (Location location : locationOlds) {
/* 125 */       if (location.getPath().equals(locationNew.getPath()) && location.getType() == locationNew.getType()) {
/* 126 */         return location.getDescr();
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 131 */     return null;
/*     */   }
/*     */   
/*     */   private void clearDef() {
/* 135 */     List<Server> servers = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("def", Integer.valueOf(1)), Server.class);
/* 136 */     for (Server server : servers) {
/* 137 */       server.setDef(Integer.valueOf(0));
/* 138 */       this.sqlHelper.updateById(server);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addOverTcp(Server server, String serverParamJson) {
/* 143 */     this.sqlHelper.insertOrUpdate(server);
/*     */     
/* 145 */     List<String> locationIds = this.sqlHelper.findIdsByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("serverId", server.getId()), Location.class);
/* 146 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionOrWrapper()).eq("serverId", server.getId()).in("locationId", locationIds), Param.class);
/* 147 */     List<Param> paramList = new ArrayList<>();
/* 148 */     if (StrUtil.isNotEmpty(serverParamJson) && JSONUtil.isTypeJSON(serverParamJson)) {
/* 149 */       paramList = JSONUtil.toList(JSONUtil.parseArray(serverParamJson), Param.class);
/*     */     }
/*     */     
/* 152 */     for (Param param : paramList) {
/* 153 */       param.setServerId(server.getId());
/* 154 */       this.sqlHelper.insert(param);
/*     */     } 
/*     */     
/* 157 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("serverId", server.getId()), Location.class);
/*     */   }
/*     */   
/*     */   public List<Server> getListByProxyType(String[] proxyType) {
/* 161 */     Sort sort = (new Sort()).add("seq", Sort.Direction.DESC);
/* 162 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).in("proxyType", (Object[])proxyType), sort, Server.class);
/*     */   }
/*     */   
/*     */   public void importServer(String nginxPath) throws Exception {
/* 166 */     String initNginxPath = initNginx(nginxPath);
/* 167 */     NgxConfig conf = null;
/*     */     try {
/* 169 */       conf = NgxConfig.read(initNginxPath);
/* 170 */     } catch (IOException e) {
/* 171 */       this.logger.error(e.getMessage(), e);
/* 172 */       throw new Exception("文件读取失败");
/*     */     } 
/*     */     
/* 175 */     List<NgxEntry> servers = conf.findAll(NgxConfig.BLOCK, new String[] { "server" });
/* 176 */     servers.addAll(conf.findAll(NgxConfig.BLOCK, new String[] { "http", "server" }));
/*     */ 
/*     */     
/* 179 */     Collections.reverse(servers);
/*     */     
/* 181 */     for (NgxEntry ngxEntry : servers) {
/* 182 */       NgxBlock serverNgx = (NgxBlock)ngxEntry;
/* 183 */       NgxParam serverName = serverNgx.findParam(new String[] { "server_name" });
/* 184 */       Server server = new Server();
/* 185 */       if (serverName == null) {
/* 186 */         server.setServerName("");
/*     */       } else {
/* 188 */         server.setServerName(serverName.getValue());
/*     */       } 
/*     */       
/* 191 */       server.setProxyType(Integer.valueOf(0));
/*     */ 
/*     */       
/* 194 */       List<NgxEntry> listens = serverNgx.findAll(NgxConfig.PARAM, new String[] { "listen" });
/* 195 */       for (NgxEntry item : listens) {
/* 196 */         NgxParam param = (NgxParam)item;
/*     */         
/* 198 */         if (server.getListen() == null) {
/* 199 */           server.setListen((String)param.getValues().toArray()[0]);
/*     */         }
/*     */         
/* 202 */         if (param.getTokens().stream().anyMatch(item2 -> "ssl".equals(item2.getToken()))) {
/* 203 */           server.setSsl(Integer.valueOf(1));
/* 204 */           NgxParam key = serverNgx.findParam(new String[] { "ssl_certificate_key" });
/* 205 */           NgxParam perm = serverNgx.findParam(new String[] { "ssl_certificate" });
/* 206 */           server.setKey((key == null) ? "" : key.getValue());
/* 207 */           server.setPem((perm == null) ? "" : perm.getValue());
/*     */         } 
/*     */         
/* 210 */         if (param.getTokens().stream().anyMatch(item2 -> "http2".equals(item2.getToken()))) {
/* 211 */           server.setHttp2(Integer.valueOf(1));
/*     */         }
/*     */       } 
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
/* 224 */       long rewriteCount = serverNgx.getEntries().stream().filter(item -> { if (item instanceof NgxBlock) { NgxBlock itemNgx = (NgxBlock)item; return itemNgx.getEntries().toString().contains("rewrite"); }  return false; }).count();
/*     */       
/* 226 */       if (rewriteCount > 0L) {
/* 227 */         server.setRewrite(Integer.valueOf(1));
/*     */       } else {
/* 229 */         server.setRewrite(Integer.valueOf(0));
/*     */       } 
/*     */ 
/*     */       
/* 233 */       List<Location> locations = new ArrayList<>();
/* 234 */       List<NgxEntry> locationBlocks = serverNgx.findAll(NgxBlock.class, new String[] { "location" });
/* 235 */       for (NgxEntry item : locationBlocks) {
/* 236 */         Location location = new Location();
/*     */         
/* 238 */         NgxParam proxyPassParam = ((NgxBlock)item).findParam(new String[] { "proxy_pass" });
/*     */         
/* 240 */         location.setPath(((NgxBlock)item).getValue());
/*     */         
/* 242 */         if (proxyPassParam != null) {
/* 243 */           location.setValue(proxyPassParam.getValue());
/* 244 */           location.setType(Integer.valueOf(0));
/*     */         } else {
/* 246 */           NgxParam rootParam = ((NgxBlock)item).findParam(new String[] { "root" });
/* 247 */           if (rootParam == null) {
/* 248 */             rootParam = ((NgxBlock)item).findParam(new String[] { "alias" });
/*     */           }
/* 250 */           if (rootParam == null) {
/*     */             continue;
/*     */           }
/*     */           
/* 254 */           location.setRootType(rootParam.getName());
/* 255 */           location.setRootPath(rootParam.getValue());
/*     */           
/* 257 */           NgxParam indexParam = ((NgxBlock)item).findParam(new String[] { "index" });
/* 258 */           if (indexParam != null) {
/* 259 */             location.setRootPage(indexParam.getValue());
/*     */           }
/*     */           
/* 262 */           location.setType(Integer.valueOf(1));
/*     */         } 
/* 264 */         location.setLocationParamJson(null);
/* 265 */         locations.add(location);
/*     */       } 
/*     */       
/* 268 */       server.setDef(Integer.valueOf(0));
/* 269 */       server.setSeq(SnowFlakeUtils.getId());
/* 270 */       addOver(server, "", locations);
/*     */     } 
/*     */ 
/*     */     
/* 274 */     FileUtil.del(initNginxPath);
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
/*     */   private String initNginx(String nginxPath) {
/* 286 */     List<String> lines = FileUtil.readLines(nginxPath, CharsetUtil.CHARSET_UTF_8);
/* 287 */     List<String> rs = new ArrayList<>();
/* 288 */     for (String str : lines) {
/* 289 */       if (str.trim().indexOf("#") == 0) {
/*     */         continue;
/*     */       }
/* 292 */       rs.add(str);
/*     */     } 
/*     */     
/* 295 */     String initNginxPath = FileUtil.getTmpDirPath() + UUID.randomUUID().toString();
/* 296 */     FileUtil.writeLines(rs, initNginxPath, CharsetUtil.CHARSET_UTF_8);
/* 297 */     return initNginxPath;
/*     */   }
/*     */   
/*     */   public void setSeq(String serverId, Integer seqAdd) {
/* 301 */     Server server = (Server)this.sqlHelper.findById(serverId, Server.class);
/*     */     
/* 303 */     List<Server> serverList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.DESC), Server.class);
/* 304 */     if (serverList.size() > 0) {
/* 305 */       Server tagert = null;
/* 306 */       if (seqAdd.intValue() < 0) {
/*     */         
/* 308 */         for (int i = 0; i < serverList.size(); i++) {
/* 309 */           if (((Server)serverList.get(i)).getSeq().longValue() < server.getSeq().longValue()) {
/* 310 */             tagert = serverList.get(i);
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } else {
/* 316 */         System.out.println(server.getSeq());
/* 317 */         for (int i = serverList.size() - 1; i >= 0; i--) {
/* 318 */           System.out.println(((Server)serverList.get(i)).getSeq());
/* 319 */           if (((Server)serverList.get(i)).getSeq().longValue() > server.getSeq().longValue()) {
/* 320 */             tagert = serverList.get(i);
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 326 */       if (tagert != null) {
/*     */         
/* 328 */         System.err.println("tagert:" + tagert.getServerName() + tagert.getListen());
/* 329 */         System.err.println("server:" + server.getServerName() + server.getListen());
/*     */ 
/*     */         
/* 332 */         Long seq = tagert.getSeq();
/* 333 */         tagert.setSeq(server.getSeq());
/* 334 */         server.setSeq(seq);
/*     */         
/* 336 */         this.sqlHelper.updateById(tagert);
/* 337 */         this.sqlHelper.updateById(server);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void moveLocation(String locationId, Integer seqAdd) {
/* 343 */     Location location = (Location)this.sqlHelper.findById(locationId, Location.class);
/*     */     
/* 345 */     List<Location> locationList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Location::getServerId, location.getServerId()), new Sort("id", Sort.Direction.DESC), Location.class);
/* 346 */     if (locationList.size() > 0) {
/* 347 */       Location tagert = null;
/* 348 */       if (seqAdd.intValue() > 0) {
/*     */         
/* 350 */         for (int i = 0; i < locationList.size(); i++) {
/* 351 */           if (Long.parseLong(((Location)locationList.get(i)).getId()) < Long.parseLong(location.getId())) {
/* 352 */             tagert = locationList.get(i);
/*     */           }
/*     */         } 
/*     */       } else {
/*     */         
/* 357 */         for (int i = locationList.size() - 1; i >= 0; i--) {
/* 358 */           if (Long.parseLong(((Location)locationList.get(i)).getId()) > Long.parseLong(location.getId())) {
/* 359 */             tagert = locationList.get(i);
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 364 */       if (tagert != null) {
/*     */         
/* 366 */         String id = tagert.getId();
/* 367 */         tagert.setId(location.getId());
/* 368 */         location.setId(id);
/*     */         
/* 370 */         this.sqlHelper.updateById(tagert);
/* 371 */         this.sqlHelper.updateById(location);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\ServerService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */