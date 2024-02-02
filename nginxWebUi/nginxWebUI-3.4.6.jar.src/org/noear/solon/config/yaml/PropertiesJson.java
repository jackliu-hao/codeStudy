/*    */ package org.noear.solon.config.yaml;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Properties;
/*    */ import org.noear.snack.ONode;
/*    */ 
/*    */ public class PropertiesJson
/*    */   extends Properties {
/*    */   public synchronized void loadJson(String text) throws IOException {
/* 10 */     ONode.loadStr(text).bindTo(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\config\yaml\PropertiesJson.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */