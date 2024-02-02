/*    */ package com.cym.service;
/*    */ 
/*    */ import com.cym.model.Basic;
/*    */ import com.cym.sqlhelper.bean.Sort;
/*    */ import com.cym.sqlhelper.utils.ConditionOrWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import java.lang.invoke.SerializedLambda;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ @Service
/*    */ public class BasicService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public List<Basic> findAll() {
/* 20 */     return this.sqlHelper.findAll((new Sort()).add("seq", Sort.Direction.ASC), Basic.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setSeq(String basicId, Integer seqAdd) {
/* 25 */     Basic basic = (Basic)this.sqlHelper.findById(basicId, Basic.class);
/*    */     
/* 27 */     List<Basic> basicList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Basic.class);
/* 28 */     if (basicList.size() > 0) {
/* 29 */       Basic tagert = null;
/* 30 */       if (seqAdd.intValue() < 0) {
/* 31 */         for (int i = 0; i < basicList.size(); i++) {
/* 32 */           if (((Basic)basicList.get(i)).getSeq().longValue() < basic.getSeq().longValue()) {
/* 33 */             tagert = basicList.get(i);
/*    */           }
/*    */         } 
/*    */       } else {
/* 37 */         for (int i = basicList.size() - 1; i >= 0; i--) {
/* 38 */           if (((Basic)basicList.get(i)).getSeq().longValue() > basic.getSeq().longValue()) {
/* 39 */             tagert = basicList.get(i);
/*    */           }
/*    */         } 
/*    */       } 
/*    */       
/* 44 */       if (tagert != null) {
/*    */         
/* 46 */         Long seq = tagert.getSeq();
/* 47 */         tagert.setSeq(basic.getSeq());
/* 48 */         basic.setSeq(seq);
/*    */         
/* 50 */         this.sqlHelper.updateById(tagert);
/* 51 */         this.sqlHelper.updateById(basic);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean contain(String content) {
/* 59 */     return (this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionOrWrapper()).like(Basic::getValue, content).like(Basic::getName, content), Basic.class).longValue() > 0L);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\BasicService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */