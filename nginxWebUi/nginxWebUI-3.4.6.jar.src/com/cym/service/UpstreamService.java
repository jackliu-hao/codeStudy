/*     */ package com.cym.service;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.model.Param;
/*     */ import com.cym.model.Upstream;
/*     */ import com.cym.model.UpstreamServer;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.sqlhelper.bean.Sort;
/*     */ import com.cym.sqlhelper.bean.Update;
/*     */ import com.cym.sqlhelper.utils.ConditionAndWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionOrWrapper;
/*     */ import com.cym.sqlhelper.utils.ConditionWrapper;
/*     */ import com.cym.sqlhelper.utils.SqlHelper;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.aspect.annotation.Service;
/*     */ 
/*     */ 
/*     */ 
/*     */ @Service
/*     */ public class UpstreamService
/*     */ {
/*     */   @Inject
/*     */   SqlHelper sqlHelper;
/*     */   
/*     */   public Page search(Page page, String word) {
/*  30 */     ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
/*     */     
/*  32 */     if (StrUtil.isNotEmpty(word)) {
/*  33 */       conditionAndWrapper.and((ConditionWrapper)(new ConditionOrWrapper()).like("name", word));
/*     */     }
/*     */     
/*  36 */     page = this.sqlHelper.findPage((ConditionWrapper)conditionAndWrapper, new Sort("seq", Sort.Direction.DESC), page, Upstream.class);
/*     */     
/*  38 */     return page;
/*     */   }
/*     */ 
/*     */   
/*     */   public void deleteById(String id) {
/*  43 */     this.sqlHelper.deleteById(id, Upstream.class);
/*  44 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("upstreamId", id), UpstreamServer.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addOver(Upstream upstream, List<UpstreamServer> upstreamServers, String upstreamParamJson) {
/*  49 */     if (upstream.getProxyType().intValue() == 1 || upstream.getTactics() == null) {
/*  50 */       upstream.setTactics("");
/*     */     }
/*     */     
/*  53 */     this.sqlHelper.insertOrUpdate(upstream);
/*     */     
/*  55 */     List<Param> paramList = new ArrayList<>();
/*  56 */     if (StrUtil.isNotEmpty(upstreamParamJson) && JSONUtil.isTypeJSON(upstreamParamJson)) {
/*  57 */       paramList = JSONUtil.toList(JSONUtil.parseArray(upstreamParamJson), Param.class);
/*     */     }
/*  59 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("upstreamId", upstream.getId()), Param.class);
/*     */     
/*  61 */     Collections.reverse(paramList);
/*  62 */     for (Param param : paramList) {
/*  63 */       param.setUpstreamId(upstream.getId());
/*  64 */       this.sqlHelper.insert(param);
/*     */     } 
/*     */     
/*  67 */     this.sqlHelper.deleteByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("upstreamId", upstream.getId()), UpstreamServer.class);
/*  68 */     if (upstreamServers != null) {
/*     */       
/*  70 */       Collections.reverse(upstreamServers);
/*     */       
/*  72 */       for (UpstreamServer upstreamServer : upstreamServers) {
/*  73 */         upstreamServer.setUpstreamId(upstream.getId());
/*  74 */         this.sqlHelper.insert(upstreamServer);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public List<UpstreamServer> getUpstreamServers(String id) {
/*  81 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("upstreamId", id), UpstreamServer.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Upstream> getListByProxyType(Integer proxyType) {
/*  92 */     Sort sort = (new Sort()).add("seq", Sort.Direction.DESC);
/*  93 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("proxyType", proxyType), sort, Upstream.class);
/*     */   }
/*     */   
/*     */   public Long getCountByName(String name) {
/*  97 */     return this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", name), Upstream.class);
/*     */   }
/*     */   
/*     */   public Long getCountByNameWithOutId(String name, String id) {
/* 101 */     return this.sqlHelper.findCountByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("name", name).ne("id", id), Upstream.class);
/*     */   }
/*     */   
/*     */   public List<UpstreamServer> getServerListByMonitor(int monitor) {
/* 105 */     List<String> upstreamIds = this.sqlHelper.findIdsByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq("monitor", Integer.valueOf(monitor)), Upstream.class);
/*     */     
/* 107 */     return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).in("upstreamId", upstreamIds), UpstreamServer.class);
/*     */   }
/*     */   
/*     */   public List<UpstreamServer> getAllServer() {
/* 111 */     return this.sqlHelper.findAll(UpstreamServer.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetMonitorStatus() {
/* 116 */     this.sqlHelper.updateMulti((ConditionWrapper)new ConditionAndWrapper(), (new Update()).set("monitorStatus", Integer.valueOf(-1)), UpstreamServer.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSeq(String upstreamId, Integer seqAdd) {
/* 121 */     Upstream upstream = (Upstream)this.sqlHelper.findById(upstreamId, Upstream.class);
/*     */     
/* 123 */     List<Upstream> upstreamList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.DESC), Upstream.class);
/* 124 */     if (upstreamList.size() > 0) {
/* 125 */       Upstream tagert = null;
/* 126 */       if (seqAdd.intValue() < 0) {
/*     */         
/* 128 */         for (int i = 0; i < upstreamList.size(); i++) {
/* 129 */           if (((Upstream)upstreamList.get(i)).getSeq().longValue() < upstream.getSeq().longValue()) {
/* 130 */             tagert = upstreamList.get(i);
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } else {
/* 136 */         for (int i = upstreamList.size() - 1; i >= 0; i--) {
/* 137 */           if (((Upstream)upstreamList.get(i)).getSeq().longValue() > upstream.getSeq().longValue()) {
/* 138 */             tagert = upstreamList.get(i);
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 144 */       if (tagert != null) {
/*     */         
/* 146 */         Long seq = tagert.getSeq();
/* 147 */         tagert.setSeq(upstream.getSeq());
/* 148 */         upstream.setSeq(seq);
/*     */         
/* 150 */         this.sqlHelper.updateById(tagert);
/* 151 */         this.sqlHelper.updateById(upstream);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\UpstreamService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */