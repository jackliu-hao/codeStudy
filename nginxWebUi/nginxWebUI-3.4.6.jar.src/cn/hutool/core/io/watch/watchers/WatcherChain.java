/*    */ package cn.hutool.core.io.watch.watchers;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.io.watch.Watcher;
/*    */ import cn.hutool.core.lang.Chain;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.WatchEvent;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
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
/*    */ public class WatcherChain
/*    */   implements Watcher, Chain<Watcher, WatcherChain>
/*    */ {
/*    */   private final List<Watcher> chain;
/*    */   
/*    */   public static WatcherChain create(Watcher... watchers) {
/* 30 */     return new WatcherChain(watchers);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WatcherChain(Watcher... watchers) {
/* 38 */     this.chain = CollUtil.newArrayList((Object[])watchers);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onCreate(WatchEvent<?> event, Path currentPath) {
/* 43 */     for (Watcher watcher : this.chain) {
/* 44 */       watcher.onCreate(event, currentPath);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onModify(WatchEvent<?> event, Path currentPath) {
/* 50 */     for (Watcher watcher : this.chain) {
/* 51 */       watcher.onModify(event, currentPath);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDelete(WatchEvent<?> event, Path currentPath) {
/* 57 */     for (Watcher watcher : this.chain) {
/* 58 */       watcher.onDelete(event, currentPath);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onOverflow(WatchEvent<?> event, Path currentPath) {
/* 64 */     for (Watcher watcher : this.chain) {
/* 65 */       watcher.onOverflow(event, currentPath);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Iterator<Watcher> iterator() {
/* 72 */     return this.chain.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public WatcherChain addChain(Watcher element) {
/* 77 */     this.chain.add(element);
/* 78 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\watchers\WatcherChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */