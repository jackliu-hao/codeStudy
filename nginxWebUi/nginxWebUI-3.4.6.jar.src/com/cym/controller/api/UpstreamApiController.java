/*     */ package com.cym.controller.api;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.model.Upstream;
/*     */ import com.cym.model.UpstreamServer;
/*     */ import com.cym.service.UpstreamService;
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
/*     */ @Mapping("/api/upstream")
/*     */ @Controller
/*     */ public class UpstreamApiController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   UpstreamService upstreamService;
/*     */   
/*     */   @Mapping("getPage")
/*     */   public JsonResult<Page<Upstream>> getPage(Integer current, Integer limit, String keywords) {
/*  40 */     Page page = new Page();
/*  41 */     page.setCurr(current);
/*  42 */     page.setLimit(limit);
/*  43 */     page = this.upstreamService.search(page, keywords);
/*     */     
/*  45 */     return renderSuccess(page);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("insertOrUpdate")
/*     */   public JsonResult<?> insertOrUpdate(Upstream upstream) {
/*  56 */     if (StrUtil.isEmpty(upstream.getName())) {
/*  57 */       return renderError("name" + this.m.get("apiStr.notFill"));
/*     */     }
/*     */     
/*  60 */     if (StrUtil.isEmpty(upstream.getId())) {
/*  61 */       Long count = this.upstreamService.getCountByName(upstream.getName());
/*  62 */       if (count.longValue() > 0L) {
/*  63 */         return renderError(this.m.get("upstreamStr.sameName"));
/*     */       }
/*     */     } else {
/*  66 */       Long count = this.upstreamService.getCountByNameWithOutId(upstream.getName(), upstream.getId());
/*  67 */       if (count.longValue() > 0L) {
/*  68 */         return renderError(this.m.get("upstreamStr.sameName"));
/*     */       }
/*     */     } 
/*  71 */     if (StrUtil.isEmpty(upstream.getId())) {
/*  72 */       upstream.setSeq(SnowFlakeUtils.getId());
/*     */     }
/*  74 */     this.sqlHelper.insertOrUpdate(upstream);
/*  75 */     return renderSuccess(upstream);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("delete")
/*     */   public JsonResult<?> delete(String id) {
/*  85 */     this.upstreamService.deleteById(id);
/*     */     
/*  87 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getServerByUpstreamId")
/*     */   public JsonResult<List<UpstreamServer>> getServerByUpstreamId(String upstreamId) {
/*  97 */     List<UpstreamServer> list = this.upstreamService.getUpstreamServers(upstreamId);
/*     */     
/*  99 */     return renderSuccess(list);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("insertOrUpdateServer")
/*     */   public JsonResult insertOrUpdateServer(UpstreamServer upstreamServer) {
/* 109 */     if (StrUtil.isEmpty(upstreamServer.getUpstreamId())) {
/* 110 */       return renderError("upstreamId" + this.m.get("apiStr.notFill"));
/*     */     }
/* 112 */     if (null == upstreamServer.getPort()) {
/* 113 */       return renderError("port" + this.m.get("apiStr.notFill"));
/*     */     }
/* 115 */     if (StrUtil.isEmpty(upstreamServer.getServer())) {
/* 116 */       return renderError("server" + this.m.get("apiStr.notFill"));
/*     */     }
/*     */     
/* 119 */     this.sqlHelper.insertOrUpdate(upstreamServer);
/* 120 */     return renderSuccess(upstreamServer);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\api\UpstreamApiController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */