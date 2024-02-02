/*    */ package com.cym.controller.adminPage;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.model.Basic;
/*    */ import com.cym.service.BasicService;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import com.cym.utils.SnowFlakeUtils;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ import org.noear.solon.core.handle.ModelAndView;
/*    */ 
/*    */ 
/*    */ @Controller
/*    */ @Mapping("/adminPage/basic")
/*    */ public class BasicController
/*    */   extends BaseController
/*    */ {
/*    */   @Inject
/*    */   BasicService basicService;
/*    */   
/*    */   @Mapping("")
/*    */   public ModelAndView index(ModelAndView modelAndView) {
/* 26 */     List<Basic> basicList = this.basicService.findAll();
/*    */     
/* 28 */     modelAndView.put("basicList", basicList);
/* 29 */     modelAndView.view("/adminPage/basic/index.html");
/* 30 */     return modelAndView;
/*    */   }
/*    */   
/*    */   @Mapping("addOver")
/*    */   public JsonResult addOver(Basic basic) {
/* 35 */     if (StrUtil.isEmpty(basic.getId())) {
/* 36 */       basic.setSeq(SnowFlakeUtils.getId());
/*    */     }
/* 38 */     this.sqlHelper.insertOrUpdate(basic);
/*    */     
/* 40 */     return renderSuccess();
/*    */   }
/*    */   
/*    */   @Mapping("setOrder")
/*    */   public JsonResult setOrder(String id, Integer count) {
/* 45 */     this.basicService.setSeq(id, count);
/*    */     
/* 47 */     return renderSuccess();
/*    */   }
/*    */   
/*    */   @Mapping("detail")
/*    */   public JsonResult detail(String id) {
/* 52 */     return renderSuccess(this.sqlHelper.findById(id, Basic.class));
/*    */   }
/*    */   
/*    */   @Mapping("del")
/*    */   public JsonResult del(String id) {
/* 57 */     this.sqlHelper.deleteById(id, Basic.class);
/*    */     
/* 59 */     return renderSuccess();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\BasicController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */