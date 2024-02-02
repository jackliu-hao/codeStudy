/*    */ package org.noear.solon.extend.impl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.StringReader;
/*    */ import java.net.URL;
/*    */ import java.util.Properties;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.config.yaml.PropertiesJson;
/*    */ import org.noear.solon.config.yaml.PropertiesYaml;
/*    */ import org.noear.solon.core.PropsLoader;
/*    */ import org.noear.solon.core.util.PrintUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PropsLoaderExt
/*    */   extends PropsLoader
/*    */ {
/*    */   public boolean isSupport(String suffix) {
/* 25 */     if (suffix == null) {
/* 26 */       return false;
/*    */     }
/*    */     
/* 29 */     return (suffix.endsWith(".properties") || suffix.endsWith(".yml"));
/*    */   }
/*    */ 
/*    */   
/*    */   public Properties load(URL url) throws IOException {
/* 34 */     if (url == null) {
/* 35 */       return null;
/*    */     }
/*    */     
/* 38 */     String fileName = url.toString();
/*    */     
/* 40 */     if (fileName.endsWith(".properties")) {
/* 41 */       PrintUtil.info(url);
/*    */       
/* 43 */       Properties tmp = new Properties();
/* 44 */       tmp.load(new InputStreamReader(url.openStream(), Solon.encoding()));
/* 45 */       return tmp;
/*    */     } 
/*    */     
/* 48 */     if (fileName.endsWith(".yml")) {
/* 49 */       PrintUtil.info(url);
/*    */       
/* 51 */       PropertiesYaml tmp = new PropertiesYaml();
/* 52 */       tmp.loadYml(new InputStreamReader(url.openStream(), Solon.encoding()));
/* 53 */       return (Properties)tmp;
/*    */     } 
/*    */     
/* 56 */     throw new RuntimeException("This profile is not supported: " + fileName);
/*    */   }
/*    */ 
/*    */   
/*    */   public Properties build(String text) throws IOException {
/* 61 */     text = text.trim();
/*    */     
/* 63 */     int idx1 = text.indexOf("=");
/* 64 */     int idx2 = text.indexOf(":");
/*    */ 
/*    */     
/* 67 */     if (text.startsWith("{") && text.endsWith("}")) {
/* 68 */       PropertiesJson tmp = new PropertiesJson();
/* 69 */       tmp.loadJson(text);
/* 70 */       return (Properties)tmp;
/*    */     } 
/*    */ 
/*    */     
/* 74 */     if (text.startsWith("[") && text.endsWith("]")) {
/* 75 */       PropertiesJson tmp = new PropertiesJson();
/* 76 */       tmp.loadJson(text);
/* 77 */       return (Properties)tmp;
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 83 */     if (idx1 > 0 && (idx1 < idx2 || idx2 < 0)) {
/* 84 */       Properties tmp = new Properties();
/* 85 */       tmp.load(new StringReader(text));
/* 86 */       return tmp;
/*    */     } 
/*    */ 
/*    */     
/* 90 */     if (idx2 > 0 && (idx2 < idx1 || idx1 < 0)) {
/* 91 */       PropertiesYaml tmp = new PropertiesYaml();
/* 92 */       tmp.loadYml(new StringReader(text));
/* 93 */       return (Properties)tmp;
/*    */     } 
/*    */     
/* 96 */     return new Properties();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\extend\impl\PropsLoaderExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */