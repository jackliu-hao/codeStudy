/*     */ package com.cym.service;
/*     */ 
/*     */ import com.cym.model.Http;
/*     */ import com.cym.model.Param;
/*     */ import com.cym.sqlhelper.bean.Sort;
/*     */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*     */ import com.cym.sqlhelper.utils.SqlHelper;
/*     */ import com.cym.utils.SnowFlakeUtils;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.aspect.annotation.Service;
/*     */ 
/*     */ @Service
/*     */ public class HttpService
/*     */ {
/*     */   @Inject
/*     */   SqlHelper sqlHelper;
/*     */   
/*     */   public List<Http> findAll() {
/*  22 */     return this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Http.class);
/*     */   }
/*     */   
/*     */   public void setAll(List<Http> https) {
/*  26 */     Http logFormat = null;
/*  27 */     Http accessLog = null;
/*  28 */     for (Http http : https) {
/*  29 */       if (http.getName().equals("log_format")) {
/*  30 */         logFormat = http;
/*     */       }
/*  32 */       if (http.getName().equals("access_log")) {
/*  33 */         accessLog = http;
/*     */       }
/*     */     } 
/*  36 */     if (logFormat != null) {
/*  37 */       https.remove(logFormat);
/*  38 */       https.add(logFormat);
/*     */     } 
/*  40 */     if (accessLog != null) {
/*  41 */       https.remove(accessLog);
/*  42 */       https.add(accessLog);
/*     */     } 
/*     */     
/*  45 */     for (Http http : https) {
/*  46 */       Http httpOrg = (Http)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", http.getName()), Http.class);
/*     */       
/*  48 */       if (httpOrg != null) {
/*  49 */         http.setId(httpOrg.getId());
/*     */       }
/*     */       
/*  52 */       http.setSeq(SnowFlakeUtils.getId());
/*  53 */       http.setValue(http.getValue() + http.getUnit());
/*     */       
/*  55 */       this.sqlHelper.insertOrUpdate(http);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSeq(String httpId, Integer seqAdd) {
/*  62 */     Http http = (Http)this.sqlHelper.findById(httpId, Http.class);
/*     */     
/*  64 */     List<Http> httpList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.ASC), Http.class);
/*  65 */     if (httpList.size() > 0) {
/*  66 */       Http tagert = null;
/*  67 */       if (seqAdd.intValue() < 0) {
/*     */         
/*  69 */         for (int i = 0; i < httpList.size(); i++) {
/*  70 */           if (((Http)httpList.get(i)).getSeq().longValue() < http.getSeq().longValue()) {
/*  71 */             tagert = httpList.get(i);
/*     */           }
/*     */         } 
/*     */       } else {
/*     */         
/*  76 */         for (int i = httpList.size() - 1; i >= 0; i--) {
/*  77 */           if (((Http)httpList.get(i)).getSeq().longValue() > http.getSeq().longValue()) {
/*  78 */             tagert = httpList.get(i);
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/*  83 */       if (tagert != null) {
/*     */         
/*  85 */         Long seq = tagert.getSeq();
/*  86 */         tagert.setSeq(http.getSeq());
/*  87 */         http.setSeq(seq);
/*     */         
/*  89 */         this.sqlHelper.updateById(tagert);
/*  90 */         this.sqlHelper.updateById(http);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Http getName(String name) {
/*  98 */     Http http = (Http)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", name), Http.class);
/*     */     
/* 100 */     return http;
/*     */   }
/*     */   
/*     */   public void addTemplate(String templateId) {
/* 104 */     List<Param> parmList = this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq(Param::getTemplateId, templateId), Param.class);
/*     */     
/* 106 */     for (Param param : parmList) {
/* 107 */       Http http = new Http();
/* 108 */       http.setName(param.getName());
/* 109 */       http.setValue(param.getValue());
/* 110 */       http.setSeq(SnowFlakeUtils.getId());
/*     */       
/* 112 */       this.sqlHelper.insert(http);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\HttpService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */