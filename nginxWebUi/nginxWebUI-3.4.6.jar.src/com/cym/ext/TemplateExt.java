/*    */ package com.cym.ext;
/*    */ 
/*    */ import com.cym.model.Param;
/*    */ import com.cym.model.Template;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ public class TemplateExt
/*    */ {
/*    */   Template template;
/*    */   Integer count;
/*    */   List<Param> paramList;
/*    */   
/*    */   public List<Param> getParamList() {
/* 15 */     return this.paramList;
/*    */   }
/*    */   public void setParamList(List<Param> paramList) {
/* 18 */     this.paramList = paramList;
/*    */   }
/*    */   public Template getTemplate() {
/* 21 */     return this.template;
/*    */   }
/*    */   public void setTemplate(Template template) {
/* 24 */     this.template = template;
/*    */   }
/*    */   public Integer getCount() {
/* 27 */     return this.count;
/*    */   }
/*    */   public void setCount(Integer count) {
/* 30 */     this.count = count;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\ext\TemplateExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */