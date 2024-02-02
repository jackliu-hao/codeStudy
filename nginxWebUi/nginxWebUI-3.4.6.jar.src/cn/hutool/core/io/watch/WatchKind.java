/*    */ package cn.hutool.core.io.watch;
/*    */ 
/*    */ import java.nio.file.StandardWatchEventKinds;
/*    */ import java.nio.file.WatchEvent;
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
/*    */ public enum WatchKind
/*    */ {
/* 24 */   OVERFLOW(StandardWatchEventKinds.OVERFLOW),
/*    */ 
/*    */ 
/*    */   
/* 28 */   MODIFY(StandardWatchEventKinds.ENTRY_MODIFY),
/*    */ 
/*    */ 
/*    */   
/* 32 */   CREATE(StandardWatchEventKinds.ENTRY_CREATE),
/*    */ 
/*    */ 
/*    */   
/* 36 */   DELETE(StandardWatchEventKinds.ENTRY_DELETE);
/*    */ 
/*    */   
/*    */   public static final WatchEvent.Kind<?>[] ALL;
/*    */   
/*    */   private final WatchEvent.Kind<?> value;
/*    */ 
/*    */   
/*    */   static {
/* 45 */     ALL = (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { OVERFLOW.getValue(), MODIFY.getValue(), CREATE.getValue(), DELETE.getValue() };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   WatchKind(WatchEvent.Kind<?> value) {
/* 56 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WatchEvent.Kind<?> getValue() {
/* 65 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\WatchKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */