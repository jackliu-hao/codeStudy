/*     */ package com.cym.controller.api;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.model.Location;
/*     */ import com.cym.model.Server;
/*     */ import com.cym.service.ParamService;
/*     */ import com.cym.service.ServerService;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.utils.BaseController;
/*     */ import com.cym.utils.JsonResult;
/*     */ import com.cym.utils.SnowFlakeUtils;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Controller;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
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
/*     */ @Mapping("/api/server")
/*     */ @Controller
/*     */ public class ServerApiController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   ServerService serverService;
/*     */   @Inject
/*     */   ParamService paramService;
/*     */   
/*     */   @Mapping("getPage")
/*     */   public JsonResult<Page<Server>> getPage(Integer current, Integer limit, String keywords) {
/*  43 */     Page page = new Page();
/*  44 */     page.setCurr(current);
/*  45 */     page.setLimit(limit);
/*  46 */     page = this.serverService.search(page, keywords);
/*     */     
/*  48 */     return renderSuccess(page);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("insertOrUpdate")
/*     */   public JsonResult<?> insertOrUpdate(Server server) {
/*  59 */     if (StrUtil.isEmpty(server.getListen())) {
/*  60 */       return renderError("listen" + this.m.get("apiStr.notFill"));
/*     */     }
/*     */     
/*  63 */     if (StrUtil.isEmpty(server.getId())) {
/*  64 */       server.setSeq(SnowFlakeUtils.getId());
/*     */     }
/*  66 */     this.sqlHelper.insertOrUpdate(server);
/*  67 */     return renderSuccess(server);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("delete")
/*     */   public JsonResult<?> delete(String id) {
/*  78 */     this.serverService.deleteById(id);
/*     */     
/*  80 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getLocationByServerId")
/*     */   public JsonResult<List<Location>> getLocationByServerId(String serverId) {
/*  91 */     List<Location> locationList = this.serverService.getLocationByServerId(serverId);
/*  92 */     for (Location location : locationList) {
/*  93 */       String json = this.paramService.getJsonByTypeId(location.getId(), "location");
/*  94 */       location.setLocationParamJson((json != null) ? json : null);
/*     */     } 
/*  96 */     return renderSuccess(locationList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("insertOrUpdateLocation")
/*     */   public JsonResult<?> insertOrUpdateLocation(Location location) {
/* 107 */     if (StrUtil.isEmpty(location.getServerId())) {
/* 108 */       return renderError("serverId" + this.m.get("apiStr.notFill"));
/*     */     }
/* 110 */     if (StrUtil.isEmpty(location.getPath())) {
/* 111 */       return renderError("path" + this.m.get("apiStr.notFill"));
/*     */     }
/* 113 */     this.sqlHelper.insertOrUpdate(location);
/* 114 */     return renderSuccess(location);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("deleteLocation")
/*     */   public JsonResult<?> deleteLocation(String id) {
/* 125 */     this.sqlHelper.deleteById(id, Location.class);
/*     */     
/* 127 */     return renderSuccess();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\api\ServerApiController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */