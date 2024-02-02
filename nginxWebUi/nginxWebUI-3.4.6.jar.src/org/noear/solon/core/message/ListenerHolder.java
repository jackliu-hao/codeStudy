/*    */ package org.noear.solon.core.message;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.regex.Matcher;
/*    */ import org.noear.solon.core.util.PathAnalyzer;
/*    */ import org.noear.solon.core.util.PathUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ListenerHolder
/*    */   implements Listener
/*    */ {
/*    */   private Listener listener;
/*    */   private PathAnalyzer pathAnalyzer;
/*    */   private List<String> pathKeys;
/*    */   
/*    */   public ListenerHolder(String path, Listener listener) {
/* 26 */     this.listener = listener;
/*    */     
/* 28 */     if (path != null && path.indexOf("{") >= 0) {
/* 29 */       path = PathUtil.mergePath(null, path);
/*    */       
/* 31 */       this.pathKeys = new ArrayList<>();
/* 32 */       Matcher pm = PathUtil.pathKeyExpr.matcher(path);
/* 33 */       while (pm.find()) {
/* 34 */         this.pathKeys.add(pm.group(1));
/*    */       }
/*    */       
/* 37 */       if (this.pathKeys.size() > 0) {
/* 38 */         this.pathAnalyzer = PathAnalyzer.get(path);
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onOpen(Session s) {
/* 46 */     if (this.pathAnalyzer != null) {
/* 47 */       Matcher pm = this.pathAnalyzer.matcher(s.pathNew());
/* 48 */       if (pm.find()) {
/* 49 */         for (int i = 0, len = this.pathKeys.size(); i < len; i++) {
/* 50 */           s.paramSet(this.pathKeys.get(i), pm.group(i + 1));
/*    */         }
/*    */       }
/*    */     } 
/*    */     
/* 55 */     this.listener.onOpen(s);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onMessage(Session s, Message m) throws IOException {
/* 60 */     this.listener.onMessage(s, m);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onClose(Session s) {
/* 65 */     this.listener.onClose(s);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onError(Session s, Throwable e) {
/* 70 */     this.listener.onError(s, e);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\message\ListenerHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */