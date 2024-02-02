/*    */ package com.cym.controller.adminPage;
/*    */ 
/*    */ import com.cym.model.OperateLog;
/*    */ import com.cym.service.OperateLogService;
/*    */ import com.cym.sqlhelper.bean.Page;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ import org.noear.solon.core.handle.ModelAndView;
/*    */ 
/*    */ @Controller
/*    */ @Mapping("/adminPage/operateLog")
/*    */ public class OperateLogController
/*    */   extends BaseController {
/*    */   @Inject
/*    */   OperateLogService operateLogService;
/*    */   
/*    */   @Mapping("")
/*    */   public ModelAndView index(ModelAndView modelAndView, Page page) {
/* 22 */     page = this.operateLogService.search(page);
/*    */     
/* 24 */     modelAndView.put("page", page);
/*    */     
/* 26 */     modelAndView.view("/adminPage/operatelog/index.html");
/* 27 */     return modelAndView;
/*    */   }
/*    */ 
/*    */   
/*    */   @Mapping("detail")
/*    */   public JsonResult detail(String id) {
/* 33 */     return renderSuccess(this.sqlHelper.findById(id, OperateLog.class));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\OperateLogController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */