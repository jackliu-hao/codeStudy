/*    */ package com.cym.controller.adminPage;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.model.Stream;
/*    */ import com.cym.service.StreamService;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import com.cym.utils.SnowFlakeUtils;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ import org.noear.solon.core.handle.ModelAndView;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Controller
/*    */ @Mapping("/adminPage/stream")
/*    */ public class StreamController
/*    */   extends BaseController
/*    */ {
/*    */   @Inject
/*    */   StreamService streamService;
/*    */   
/*    */   @Mapping("")
/*    */   public ModelAndView index(ModelAndView modelAndView) {
/* 29 */     List<Stream> streamList = this.streamService.findAll();
/*    */     
/* 31 */     modelAndView.put("streamList", streamList);
/* 32 */     modelAndView.view("/adminPage/stream/index.html");
/* 33 */     return modelAndView;
/*    */   }
/*    */   
/*    */   @Mapping("addOver")
/*    */   public JsonResult addOver(Stream stream) {
/* 38 */     if (StrUtil.isEmpty(stream.getId())) {
/* 39 */       stream.setSeq(SnowFlakeUtils.getId());
/*    */     }
/* 41 */     this.sqlHelper.insertOrUpdate(stream);
/*    */     
/* 43 */     return renderSuccess();
/*    */   }
/*    */ 
/*    */   
/*    */   @Mapping("addTemplate")
/*    */   public JsonResult addTemplate(String templateId) {
/* 49 */     this.streamService.addTemplate(templateId);
/*    */     
/* 51 */     return renderSuccess();
/*    */   }
/*    */   
/*    */   @Mapping("detail")
/*    */   public JsonResult detail(String id) {
/* 56 */     return renderSuccess(this.sqlHelper.findById(id, Stream.class));
/*    */   }
/*    */   
/*    */   @Mapping("del")
/*    */   public JsonResult del(String id) {
/* 61 */     this.sqlHelper.deleteById(id, Stream.class);
/*    */     
/* 63 */     return renderSuccess();
/*    */   }
/*    */   
/*    */   @Mapping("setOrder")
/*    */   public JsonResult setOrder(String id, Integer count) {
/* 68 */     this.streamService.setSeq(id, count);
/*    */     
/* 70 */     return renderSuccess();
/*    */   }
/*    */   
/*    */   @Mapping("addGiudeOver")
/*    */   public JsonResult addGiudeOver(Boolean logStatus) {
/* 75 */     List<Stream> streams = new ArrayList<>();
/* 76 */     if (logStatus.booleanValue()) {
/*    */       
/* 78 */       Stream stream = new Stream();
/* 79 */       stream.setName("log_format basic");
/* 80 */       stream.setValue("'$remote_addr [$time_local] $protocol $status $bytes_sent $bytes_received $session_time \"$upstream_addr\" \"$upstream_bytes_sent\" \"$upstream_bytes_received\" \"$upstream_connect_time\"'");
/* 81 */       streams.add(stream);
/*    */       
/* 83 */       stream = new Stream();
/* 84 */       stream.setName("access_log");
/* 85 */       stream.setValue(this.homeConfig.home + "log/stream_access.log basic");
/* 86 */       streams.add(stream);
/*    */       
/* 88 */       stream = new Stream();
/* 89 */       stream.setName("open_log_file_cache");
/* 90 */       stream.setValue("off");
/* 91 */       streams.add(stream);
/*    */     } 
/*    */     
/* 94 */     this.streamService.setAll(streams);
/*    */     
/* 96 */     return renderSuccess();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\StreamController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */