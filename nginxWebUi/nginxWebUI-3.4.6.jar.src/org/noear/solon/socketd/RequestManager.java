/*    */ package org.noear.solon.socketd;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import org.noear.solon.core.message.Message;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RequestManager
/*    */ {
/* 17 */   public static int REQUEST_AND_RESPONSE_TIMEOUT_SECONDS = 30;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 22 */   private static Map<String, CompletableFuture<Message>> requests = new ConcurrentHashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void register(Message message, CompletableFuture<Message> future) {
/* 31 */     requests.putIfAbsent(message.key(), future);
/*    */   }
/*    */   
/*    */   public static CompletableFuture<Message> get(String key) {
/* 35 */     return requests.get(key);
/*    */   }
/*    */   
/*    */   public static void remove(String key) {
/* 39 */     requests.remove(key);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\RequestManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */