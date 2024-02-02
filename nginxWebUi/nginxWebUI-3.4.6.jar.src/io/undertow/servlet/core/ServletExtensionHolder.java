/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.servlet.ServletExtension;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CopyOnWriteArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServletExtensionHolder
/*    */ {
/* 33 */   private static List<ServletExtension> extensions = new CopyOnWriteArrayList<>();
/*    */   
/*    */   public static List<ServletExtension> getServletExtensions() {
/* 36 */     return extensions;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ServletExtensionHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */