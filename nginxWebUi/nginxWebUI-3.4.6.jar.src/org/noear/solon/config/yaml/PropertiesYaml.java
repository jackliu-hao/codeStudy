/*    */ package org.noear.solon.config.yaml;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.Reader;
/*    */ import java.util.Map;
/*    */ import java.util.Properties;
/*    */ import org.yaml.snakeyaml.Yaml;
/*    */ 
/*    */ public class PropertiesYaml
/*    */   extends Properties
/*    */ {
/*    */   public synchronized void loadYml(InputStream inputStream) {
/* 14 */     Yaml yaml = new Yaml();
/*    */     
/* 16 */     Object tmp = yaml.load(inputStream);
/*    */     
/* 18 */     String prefix = "";
/* 19 */     load0(prefix, tmp);
/*    */   }
/*    */   
/*    */   public synchronized void loadYml(Reader reader) throws IOException {
/* 23 */     Yaml yaml = new Yaml();
/*    */     
/* 25 */     Object tmp = yaml.load(reader);
/*    */     
/* 27 */     String prefix = "";
/* 28 */     load0(prefix, tmp);
/*    */   }
/*    */   
/*    */   private void load0(String prefix, Object tmp) {
/* 32 */     if (tmp instanceof Map) {
/* 33 */       ((Map)tmp).forEach((k, v) -> {
/*    */             String prefix2 = prefix + "." + k;
/*    */             
/*    */             load0(prefix2, v);
/*    */           });
/*    */       return;
/*    */     } 
/* 40 */     if (tmp instanceof java.util.List) {
/*    */ 
/*    */       
/* 43 */       int index = 0;
/* 44 */       for (Object v : tmp) {
/* 45 */         String prefix2 = prefix + "[" + index + "]";
/* 46 */         load0(prefix2, v);
/* 47 */         index++;
/*    */       } 
/*    */       
/*    */       return;
/*    */     } 
/* 52 */     if (tmp == null) {
/* 53 */       put0(prefix, "");
/*    */     } else {
/* 55 */       put0(prefix, String.valueOf(tmp));
/*    */     } 
/*    */   }
/*    */   
/*    */   private void put0(String key, Object val) {
/* 60 */     if (key.startsWith(".")) {
/* 61 */       put(key.substring(1), val);
/*    */     } else {
/* 63 */       put(key, val);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\config\yaml\PropertiesYaml.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */