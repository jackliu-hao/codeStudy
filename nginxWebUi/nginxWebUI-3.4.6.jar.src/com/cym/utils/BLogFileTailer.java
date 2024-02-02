/*    */ package com.cym.utils;
/*    */ 
/*    */ import cn.hutool.core.io.LineHandler;
/*    */ import cn.hutool.core.io.file.Tailer;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.File;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Vector;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.jodah.expiringmap.ExpirationListener;
/*    */ import net.jodah.expiringmap.ExpirationPolicy;
/*    */ import net.jodah.expiringmap.ExpiringMap;
/*    */ import org.noear.solon.annotation.Component;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class BLogFileTailer
/*    */ {
/* 22 */   public Map<String, Tailer> tailerMap = (Map<String, Tailer>)ExpiringMap.builder()
/* 23 */     .expiration(20L, TimeUnit.SECONDS)
/* 24 */     .expirationPolicy(ExpirationPolicy.ACCESSED)
/* 25 */     .expirationListener(new ExpirationListener<String, Tailer>()
/*    */       {
/*    */         public void expired(String guid, Tailer tailer) {
/* 28 */           tailer.stop();
/* 29 */           tailer = null;
/*    */         }
/* 31 */       }).build();
/*    */ 
/*    */   
/* 34 */   public Map<String, Vector<String>> lineMap = (Map<String, Vector<String>>)ExpiringMap.builder()
/* 35 */     .expiration(20L, TimeUnit.SECONDS)
/* 36 */     .expirationPolicy(ExpirationPolicy.ACCESSED)
/* 37 */     .build();
/*    */ 
/*    */   
/*    */   public String run(final String guid, String path) {
/* 41 */     if (this.tailerMap.get(guid) == null) {
/* 42 */       Tailer tailer = new Tailer(new File(path), new LineHandler()
/*    */           {
/*    */             public void handle(String line)
/*    */             {
/* 46 */               if (BLogFileTailer.this.lineMap.get(guid) == null) {
/* 47 */                 BLogFileTailer.this.lineMap.put(guid, new Vector<>());
/*    */               }
/*    */               
/* 50 */               ((Vector<String>)BLogFileTailer.this.lineMap.get(guid)).add("<div>" + line + "</div>");
/*    */             }
/*    */           }50);
/* 53 */       tailer.start(true);
/*    */       
/* 55 */       this.tailerMap.put(guid, tailer);
/*    */     } 
/*    */     
/* 58 */     List<String> list = this.lineMap.get(guid);
/* 59 */     if (list != null && list.size() > 0) {
/*    */ 
/*    */       
/* 62 */       while (list.size() > 500) {
/* 63 */         list.remove(0);
/*    */       }
/*    */       
/* 66 */       return StrUtil.join("", list);
/*    */     } 
/* 68 */     return "";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\BLogFileTailer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */