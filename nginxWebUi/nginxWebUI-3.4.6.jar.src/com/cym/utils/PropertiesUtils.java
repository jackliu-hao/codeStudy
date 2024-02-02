/*    */ package com.cym.utils;
/*    */ 
/*    */ import cn.hutool.core.io.resource.ClassPathResource;
/*    */ import com.cym.NginxWebUI;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.Properties;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class PropertiesUtils
/*    */ {
/* 17 */   static Logger logger = LoggerFactory.getLogger(NginxWebUI.class);
/*    */   public Properties getPropertis(String name) {
/* 19 */     Properties properties = new Properties();
/*    */     
/*    */     try {
/* 22 */       ClassPathResource resource = new ClassPathResource(name);
/* 23 */       InputStream in = resource.getStream();
/*    */ 
/*    */       
/* 26 */       properties.load(in);
/* 27 */     } catch (IOException e) {
/* 28 */       logger.error(e.getMessage(), e);
/*    */     } 
/*    */ 
/*    */     
/* 32 */     return properties;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\PropertiesUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */