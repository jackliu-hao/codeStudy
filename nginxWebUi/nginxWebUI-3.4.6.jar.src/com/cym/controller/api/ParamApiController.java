/*    */ package com.cym.controller.api;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.model.Param;
/*    */ import com.cym.service.ParamService;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Mapping("/api/param")
/*    */ @Controller
/*    */ public class ParamApiController
/*    */   extends BaseController
/*    */ {
/*    */   @Inject
/*    */   ParamService paramService;
/*    */   
/*    */   @Mapping("getList")
/*    */   public JsonResult<List<Param>> getList(String serverId, String locationId, String upstreamId) {
/* 39 */     if (StrUtil.isEmpty(serverId) && StrUtil.isEmpty(locationId) && StrUtil.isEmpty(upstreamId)) {
/* 40 */       return renderError(this.m.get("apiStr.paramError"));
/*    */     }
/*    */     
/* 43 */     List<Param> list = this.paramService.getList(serverId, locationId, upstreamId);
/* 44 */     return renderSuccess(list);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Mapping("insertOrUpdate")
/*    */   public JsonResult<?> insertOrUpdate(Param param) throws IOException {
/* 55 */     Integer count = Integer.valueOf(0);
/* 56 */     if (StrUtil.isNotEmpty(param.getLocationId())) {
/* 57 */       Integer integer1 = count, integer2 = count = Integer.valueOf(count.intValue() + 1);
/*    */     }
/* 59 */     if (StrUtil.isNotEmpty(param.getServerId())) {
/* 60 */       Integer integer1 = count, integer2 = count = Integer.valueOf(count.intValue() + 1);
/*    */     }
/* 62 */     if (StrUtil.isNotEmpty(param.getUpstreamId())) {
/* 63 */       Integer integer1 = count, integer2 = count = Integer.valueOf(count.intValue() + 1);
/*    */     }
/*    */     
/* 66 */     if (count.intValue() != 1) {
/* 67 */       return renderError(this.m.get("apiStr.paramError"));
/*    */     }
/*    */     
/* 70 */     this.sqlHelper.insertOrUpdate(param);
/*    */     
/* 72 */     return renderSuccess(param);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Mapping("del")
/*    */   public JsonResult<?> del(String id) {
/* 83 */     this.sqlHelper.deleteById(id, Param.class);
/*    */     
/* 85 */     return renderSuccess();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\api\ParamApiController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */