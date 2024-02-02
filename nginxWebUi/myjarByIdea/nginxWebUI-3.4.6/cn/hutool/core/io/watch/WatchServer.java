package cn.hutool.core.io.watch;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ArrayUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.AccessDeniedException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WatchServer extends Thread implements Closeable, Serializable {
   private static final long serialVersionUID = 1L;
   private WatchService watchService;
   protected WatchEvent.Kind<?>[] events;
   private WatchEvent.Modifier[] modifiers;
   protected boolean isClosed;
   private final Map<WatchKey, Path> watchKeyPathMap = new HashMap();

   public void init() throws WatchException {
      try {
         this.watchService = FileSystems.getDefault().newWatchService();
      } catch (IOException var2) {
         throw new WatchException(var2);
      }

      this.isClosed = false;
   }

   public void setModifiers(WatchEvent.Modifier[] modifiers) {
      this.modifiers = modifiers;
   }

   public void registerPath(Path path, int maxDepth) {
      WatchEvent.Kind<?>[] kinds = (WatchEvent.Kind[])ArrayUtil.defaultIfEmpty(this.events, WatchKind.ALL);

      try {
         WatchKey key;
         if (ArrayUtil.isEmpty((Object[])this.modifiers)) {
            key = path.register(this.watchService, kinds);
         } else {
            key = path.register(this.watchService, kinds, this.modifiers);
         }

         this.watchKeyPathMap.put(key, path);
         if (maxDepth > 1) {
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), maxDepth, new SimpleFileVisitor<Path>() {
               public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                  WatchServer.this.registerPath(dir, 0);
                  return super.postVisitDirectory(dir, exc);
               }
            });
         }
      } catch (IOException var5) {
         if (!(var5 instanceof AccessDeniedException)) {
            throw new WatchException(var5);
         }
      }

   }

   public void watch(WatchAction action, Filter<WatchEvent<?>> watchFilter) {
      WatchKey wk;
      try {
         wk = this.watchService.take();
      } catch (ClosedWatchServiceException | InterruptedException var7) {
         this.close();
         return;
      }

      Path currentPath = (Path)this.watchKeyPathMap.get(wk);
      Iterator var5 = wk.pollEvents().iterator();

      while(true) {
         WatchEvent event;
         do {
            if (!var5.hasNext()) {
               wk.reset();
               return;
            }

            event = (WatchEvent)var5.next();
         } while(null != watchFilter && !watchFilter.accept(event));

         action.doAction(event, currentPath);
      }
   }

   public void watch(Watcher watcher, Filter<WatchEvent<?>> watchFilter) {
      this.watch((event, currentPath) -> {
         WatchEvent.Kind<?> kind = event.kind();
         if (kind == WatchKind.CREATE.getValue()) {
            watcher.onCreate(event, currentPath);
         } else if (kind == WatchKind.MODIFY.getValue()) {
            watcher.onModify(event, currentPath);
         } else if (kind == WatchKind.DELETE.getValue()) {
            watcher.onDelete(event, currentPath);
         } else if (kind == WatchKind.OVERFLOW.getValue()) {
            watcher.onOverflow(event, currentPath);
         }

      }, watchFilter);
   }

   public void close() {
      this.isClosed = true;
      IoUtil.close(this.watchService);
   }
}
