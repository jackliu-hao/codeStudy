/*     */ package com.cym.controller.api;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.model.Basic;
/*     */ import com.cym.model.Http;
/*     */ import com.cym.model.Stream;
/*     */ import com.cym.service.BasicService;
/*     */ import com.cym.service.HttpService;
/*     */ import com.cym.service.StreamService;
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
/*     */ @Mapping("/api/basic")
/*     */ @Controller
/*     */ public class BasicApiController
/*     */   extends BaseController
/*     */ {
/*     */   @Inject
/*     */   HttpService httpService;
/*     */   @Inject
/*     */   BasicService basicService;
/*     */   @Inject
/*     */   StreamService streamService;
/*     */   
/*     */   @Mapping("getHttp")
/*     */   public JsonResult<List<Http>> getHttp() {
/*  43 */     return renderSuccess(this.httpService.findAll());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("insertOrUpdateHttp")
/*     */   public JsonResult<Http> insertOrUpdateHttp(Http http) {
/*  54 */     if (StrUtil.isEmpty(http.getName()) || StrUtil.isEmpty(http.getValue())) {
/*  55 */       return renderError(this.m.get("apiStr.noContent"));
/*     */     }
/*     */     
/*  58 */     if (StrUtil.isEmpty(http.getId())) {
/*  59 */       http.setSeq(SnowFlakeUtils.getId());
/*     */     }
/*  61 */     this.sqlHelper.insertOrUpdate(http);
/*  62 */     return renderSuccess(http);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("delHttp")
/*     */   public JsonResult delHttp(String id) {
/*  73 */     this.sqlHelper.deleteById(id, Http.class);
/*  74 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getBasic")
/*     */   public JsonResult<List<Basic>> getBasic() {
/*  83 */     return renderSuccess(this.basicService.findAll());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("insertOrUpdateBasic")
/*     */   public JsonResult<Basic> insertOrUpdateBasic(Basic basic) {
/*  94 */     if (StrUtil.isEmpty(basic.getName()) || StrUtil.isEmpty(basic.getValue())) {
/*  95 */       return renderError(this.m.get("apiStr.noContent"));
/*     */     }
/*     */     
/*  98 */     if (StrUtil.isEmpty(basic.getId())) {
/*  99 */       basic.setSeq(SnowFlakeUtils.getId());
/*     */     }
/* 101 */     this.sqlHelper.insertOrUpdate(basic);
/* 102 */     return renderSuccess(basic);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("delBasic")
/*     */   public JsonResult delBasic(String id) {
/* 113 */     this.sqlHelper.deleteById(id, Basic.class);
/* 114 */     return renderSuccess();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("getStream")
/*     */   public JsonResult<List<Stream>> getStream() {
/* 123 */     return renderSuccess(this.streamService.findAll());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("insertOrUpdateStream")
/*     */   public JsonResult<Stream> insertOrUpdateStream(Stream stream) {
/* 134 */     if (StrUtil.isEmpty(stream.getName()) || StrUtil.isEmpty(stream.getValue())) {
/* 135 */       return renderError(this.m.get("apiStr.noContent"));
/*     */     }
/* 137 */     if (StrUtil.isEmpty(stream.getId())) {
/* 138 */       stream.setSeq(SnowFlakeUtils.getId());
/*     */     }
/* 140 */     this.sqlHelper.insertOrUpdate(stream);
/* 141 */     return renderSuccess(stream);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Mapping("delStream")
/*     */   public JsonResult delStream(String id) {
/* 152 */     this.sqlHelper.deleteById(id, Stream.class);
/* 153 */     return renderSuccess();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\api\BasicApiController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */