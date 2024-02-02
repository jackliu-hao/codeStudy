/*    */ package com.cym.service;
/*    */ 
/*    */ import com.cym.model.Param;
/*    */ import com.cym.model.Stream;
/*    */ import com.cym.sqlhelper.bean.Sort;
/*    */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*    */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*    */ import com.cym.sqlhelper.utils.SqlHelper;
/*    */ import com.cym.utils.SnowFlakeUtils;
/*    */ import java.lang.invoke.SerializedLambda;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class StreamService
/*    */ {
/*    */   @Inject
/*    */   SqlHelper sqlHelper;
/*    */   
/*    */   public void setSeq(String streamId, Integer seqAdd) {
/* 23 */     Stream http = (Stream)this.sqlHelper.findById(streamId, Stream.class);
/*    */     
/* 25 */     List<Stream> httpList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Stream.class);
/* 26 */     if (httpList.size() > 0) {
/* 27 */       Stream tagert = null;
/* 28 */       if (seqAdd.intValue() < 0) {
/* 29 */         for (int i = 0; i < httpList.size(); i++) {
/* 30 */           if (((Stream)httpList.get(i)).getSeq().longValue() < http.getSeq().longValue()) {
/* 31 */             tagert = httpList.get(i);
/*    */           }
/*    */         } 
/*    */       } else {
/* 35 */         for (int i = httpList.size() - 1; i >= 0; i--) {
/* 36 */           if (((Stream)httpList.get(i)).getSeq().longValue() > http.getSeq().longValue()) {
/* 37 */             tagert = httpList.get(i);
/*    */           }
/*    */         } 
/*    */       } 
/*    */       
/* 42 */       if (tagert != null) {
/*    */         
/* 44 */         Long seq = tagert.getSeq();
/* 45 */         tagert.setSeq(http.getSeq());
/* 46 */         http.setSeq(seq);
/*    */         
/* 48 */         this.sqlHelper.updateById(tagert);
/* 49 */         this.sqlHelper.updateById(http);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public List<Stream> findAll() {
/* 57 */     return this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Stream.class);
/*    */   }
/*    */   
/*    */   public void addTemplate(String templateId) {
/* 61 */     List<Param> parmList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Param::getTemplateId, templateId), Param.class);
/*    */     
/* 63 */     for (Param param : parmList) {
/* 64 */       Stream stream = new Stream();
/* 65 */       stream.setName(param.getName());
/* 66 */       stream.setValue(param.getValue());
/* 67 */       stream.setSeq(SnowFlakeUtils.getId());
/*    */       
/* 69 */       this.sqlHelper.insert(stream);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void setAll(List<Stream> streams) {
/* 75 */     for (Stream stream : streams) {
/* 76 */       Stream streamOrg = (Stream)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", stream.getName()), Stream.class);
/*    */       
/* 78 */       if (streamOrg != null) {
/* 79 */         this.sqlHelper.deleteById(streamOrg.getId(), Stream.class);
/*    */       }
/*    */       
/* 82 */       this.sqlHelper.insert(stream);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\StreamService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */