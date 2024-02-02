/*    */ package com.cym.controller.adminPage;
/*    */ 
/*    */ import com.cym.utils.BaseController;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ import org.noear.solon.core.handle.ModelAndView;
/*    */ 
/*    */ 
/*    */ @Mapping("/adminPage/about")
/*    */ @Controller
/*    */ public class AboutController
/*    */   extends BaseController
/*    */ {
/*    */   @Mapping("")
/*    */   public ModelAndView index(ModelAndView modelAndView) {
/* 16 */     modelAndView.view("/adminPage/about/index.html");
/* 17 */     return modelAndView;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\AboutController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */