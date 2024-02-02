/*    */ package org.noear.solon.core.handle;
/*    */ import java.util.Set;
/*    */ import java.util.function.Predicate;
/*    */ import org.noear.solon.annotation.Head;
/*    */ import org.noear.solon.annotation.Http;
/*    */ import org.noear.solon.annotation.Options;
/*    */ import org.noear.solon.annotation.Patch;
/*    */ import org.noear.solon.annotation.Put;
/*    */ import org.noear.solon.annotation.Socket;
/*    */ import org.noear.solon.annotation.WebSocket;
/*    */ 
/*    */ public class MethodTypeUtil {
/*    */   public static Set<MethodType> findAndFill(Set<MethodType> list, Predicate<Class<?>> checker) {
/* 14 */     if (checker.test(Get.class)) {
/* 15 */       list.add(MethodType.GET);
/*    */     }
/*    */     
/* 18 */     if (checker.test(Post.class)) {
/* 19 */       list.add(MethodType.POST);
/*    */     }
/*    */     
/* 22 */     if (checker.test(Put.class)) {
/* 23 */       list.add(MethodType.PUT);
/*    */     }
/*    */     
/* 26 */     if (checker.test(Patch.class)) {
/* 27 */       list.add(MethodType.PATCH);
/*    */     }
/*    */     
/* 30 */     if (checker.test(Delete.class)) {
/* 31 */       list.add(MethodType.DELETE);
/*    */     }
/*    */     
/* 34 */     if (checker.test(Head.class)) {
/* 35 */       list.add(MethodType.HEAD);
/*    */     }
/*    */     
/* 38 */     if (checker.test(Options.class)) {
/* 39 */       list.add(MethodType.OPTIONS);
/*    */     }
/*    */     
/* 42 */     if (checker.test(Http.class)) {
/* 43 */       list.add(MethodType.HTTP);
/*    */     }
/*    */     
/* 46 */     if (checker.test(Socket.class)) {
/* 47 */       list.add(MethodType.SOCKET);
/*    */     }
/*    */     
/* 50 */     if (checker.test(WebSocket.class)) {
/* 51 */       list.add(MethodType.WEBSOCKET);
/*    */     }
/*    */     
/* 54 */     return list;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\MethodTypeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */