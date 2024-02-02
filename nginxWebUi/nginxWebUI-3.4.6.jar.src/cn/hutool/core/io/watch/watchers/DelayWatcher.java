/*    */ package cn.hutool.core.io.watch.watchers;
/*    */ 
/*    */ import cn.hutool.core.collection.ConcurrentHashSet;
/*    */ import cn.hutool.core.io.watch.Watcher;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.thread.ThreadUtil;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.nio.file.WatchEvent;
/*    */ import java.util.Set;
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
/*    */ public class DelayWatcher
/*    */   implements Watcher
/*    */ {
/* 27 */   private final Set<Path> eventSet = (Set<Path>)new ConcurrentHashSet();
/*    */ 
/*    */ 
/*    */   
/*    */   private final Watcher watcher;
/*    */ 
/*    */ 
/*    */   
/*    */   private final long delay;
/*    */ 
/*    */ 
/*    */   
/*    */   public DelayWatcher(Watcher watcher, long delay) {
/* 40 */     Assert.notNull(watcher);
/* 41 */     if (watcher instanceof DelayWatcher) {
/* 42 */       throw new IllegalArgumentException("Watcher must not be a DelayWatcher");
/*    */     }
/* 44 */     this.watcher = watcher;
/* 45 */     this.delay = delay;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onModify(WatchEvent<?> event, Path currentPath) {
/* 51 */     if (this.delay < 1L) {
/* 52 */       this.watcher.onModify(event, currentPath);
/*    */     } else {
/* 54 */       onDelayModify(event, currentPath);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onCreate(WatchEvent<?> event, Path currentPath) {
/* 60 */     this.watcher.onCreate(event, currentPath);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDelete(WatchEvent<?> event, Path currentPath) {
/* 65 */     this.watcher.onDelete(event, currentPath);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onOverflow(WatchEvent<?> event, Path currentPath) {
/* 70 */     this.watcher.onOverflow(event, currentPath);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void onDelayModify(WatchEvent<?> event, Path currentPath) {
/* 80 */     Path eventPath = Paths.get(currentPath.toString(), new String[] { event.context().toString() });
/* 81 */     if (this.eventSet.contains(eventPath)) {
/*    */       return;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 87 */     this.eventSet.add(eventPath);
/* 88 */     startHandleModifyThread(event, currentPath);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void startHandleModifyThread(WatchEvent<?> event, Path currentPath) {
/* 98 */     ThreadUtil.execute(() -> {
/*    */           ThreadUtil.sleep(this.delay);
/*    */           this.eventSet.remove(Paths.get(currentPath.toString(), new String[] { event.context().toString() }));
/*    */           this.watcher.onModify(event, currentPath);
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\watchers\DelayWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */