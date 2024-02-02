/*    */ package com.cym.service;
/*    */ 
/*    */ import com.cym.model.Bak;
/*    */ import com.cym.model.BakSub;
/*    */ import com.cym.sqlhelper.bean.Page;
/*    */ import com.cym.sqlhelper.bean.Sort;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import java.lang.invoke.SerializedLambda;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ @Service
/*    */ public class BakService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public Page<Bak> getList(Page page) {
/* 22 */     return this.sqlHelper.findPage((ConditionWrapper)new ConditionAndWrapper(), new Sort(Bak::getTime, Sort.Direction.DESC), page, Bak.class);
/*    */   }
/*    */   
/*    */   public List<BakSub> getSubList(String id) {
/* 26 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(BakSub::getBakId, id), BakSub.class);
/*    */   }
/*    */   
/*    */   public void del(String id) {
/* 30 */     this.sqlHelper.deleteById(id, Bak.class);
/* 31 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(BakSub::getBakId, id), BakSub.class);
/*    */   }
/*    */   
/*    */   public void delAll() {
/* 35 */     this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), Bak.class);
/* 36 */     this.sqlHelper.deleteByQuery((ConditionWrapper)new ConditionAndWrapper(), BakSub.class);
/*    */   }
/*    */   
/*    */   public Bak getPre(String id) {
/* 40 */     Bak bak = (Bak)this.sqlHelper.findById(id, Bak.class);
/* 41 */     Bak pre = (Bak)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).lt(Bak::getTime, bak.getTime()), new Sort(Bak::getTime, Sort.Direction.DESC), Bak.class);
/*    */     
/* 43 */     return pre;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\BakService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */