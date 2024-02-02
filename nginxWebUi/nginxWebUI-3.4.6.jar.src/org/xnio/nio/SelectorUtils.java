/*    */ package org.xnio.nio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.ClosedChannelException;
/*    */ import java.nio.channels.SelectableChannel;
/*    */ import java.nio.channels.SelectionKey;
/*    */ import java.nio.channels.Selector;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.xnio.Xnio;
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
/*    */ final class SelectorUtils
/*    */ {
/*    */   public static void await(NioXnio nioXnio, SelectableChannel channel, int op) throws IOException {
/*    */     SelectionKey selectionKey;
/* 38 */     if (NioXnio.IS_HP_UX) {
/*    */       
/* 40 */       await(nioXnio, channel, op, 1L, TimeUnit.SECONDS);
/*    */       return;
/*    */     } 
/* 43 */     Xnio.checkBlockingAllowed();
/* 44 */     Selector selector = nioXnio.getSelector();
/*    */     
/*    */     try {
/* 47 */       selectionKey = channel.register(selector, op);
/* 48 */     } catch (ClosedChannelException e) {
/*    */       return;
/*    */     } 
/* 51 */     selector.select();
/* 52 */     selector.selectedKeys().clear();
/* 53 */     if (Thread.currentThread().isInterrupted()) {
/* 54 */       throw Log.log.interruptedIO();
/*    */     }
/* 56 */     selectionKey.cancel();
/* 57 */     selector.selectNow();
/*    */   }
/*    */   public static void await(NioXnio nioXnio, SelectableChannel channel, int op, long time, TimeUnit unit) throws IOException {
/*    */     SelectionKey selectionKey;
/* 61 */     if (time <= 0L) {
/* 62 */       await(nioXnio, channel, op);
/*    */       return;
/*    */     } 
/* 65 */     Xnio.checkBlockingAllowed();
/* 66 */     Selector selector = nioXnio.getSelector();
/*    */     
/*    */     try {
/* 69 */       selectionKey = channel.register(selector, op);
/* 70 */     } catch (ClosedChannelException e) {
/*    */       return;
/*    */     } 
/* 73 */     long timeoutInMillis = unit.toMillis(time);
/* 74 */     selector.select((timeoutInMillis == 0L) ? 1L : timeoutInMillis);
/* 75 */     selector.selectedKeys().clear();
/* 76 */     if (Thread.currentThread().isInterrupted()) {
/* 77 */       throw Log.log.interruptedIO();
/*    */     }
/* 79 */     selectionKey.cancel();
/* 80 */     selector.selectNow();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\SelectorUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */