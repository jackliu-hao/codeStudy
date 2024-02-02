package cn.hutool.core.io.watch.watchers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.Chain;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Iterator;
import java.util.List;

public class WatcherChain implements Watcher, Chain<Watcher, WatcherChain> {
   private final List<Watcher> chain;

   public static WatcherChain create(Watcher... watchers) {
      return new WatcherChain(watchers);
   }

   public WatcherChain(Watcher... watchers) {
      this.chain = CollUtil.newArrayList((Object[])watchers);
   }

   public void onCreate(WatchEvent<?> event, Path currentPath) {
      Iterator var3 = this.chain.iterator();

      while(var3.hasNext()) {
         Watcher watcher = (Watcher)var3.next();
         watcher.onCreate(event, currentPath);
      }

   }

   public void onModify(WatchEvent<?> event, Path currentPath) {
      Iterator var3 = this.chain.iterator();

      while(var3.hasNext()) {
         Watcher watcher = (Watcher)var3.next();
         watcher.onModify(event, currentPath);
      }

   }

   public void onDelete(WatchEvent<?> event, Path currentPath) {
      Iterator var3 = this.chain.iterator();

      while(var3.hasNext()) {
         Watcher watcher = (Watcher)var3.next();
         watcher.onDelete(event, currentPath);
      }

   }

   public void onOverflow(WatchEvent<?> event, Path currentPath) {
      Iterator var3 = this.chain.iterator();

      while(var3.hasNext()) {
         Watcher watcher = (Watcher)var3.next();
         watcher.onOverflow(event, currentPath);
      }

   }

   public Iterator<Watcher> iterator() {
      return this.chain.iterator();
   }

   public WatcherChain addChain(Watcher element) {
      this.chain.add(element);
      return this;
   }
}
