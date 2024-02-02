package cn.hutool.core.io.watch;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

public class WatchMonitor extends WatchServer {
   private static final long serialVersionUID = 1L;
   public static final WatchEvent.Kind<?> OVERFLOW;
   public static final WatchEvent.Kind<?> ENTRY_MODIFY;
   public static final WatchEvent.Kind<?> ENTRY_CREATE;
   public static final WatchEvent.Kind<?> ENTRY_DELETE;
   public static final WatchEvent.Kind<?>[] EVENTS_ALL;
   private Path path;
   private int maxDepth;
   private Path filePath;
   private Watcher watcher;

   public static WatchMonitor create(URL url, WatchEvent.Kind<?>... events) {
      return create((URL)url, 0, events);
   }

   public static WatchMonitor create(URL url, int maxDepth, WatchEvent.Kind<?>... events) {
      return create(URLUtil.toURI(url), maxDepth, events);
   }

   public static WatchMonitor create(URI uri, WatchEvent.Kind<?>... events) {
      return create((URI)uri, 0, events);
   }

   public static WatchMonitor create(URI uri, int maxDepth, WatchEvent.Kind<?>... events) {
      return create(Paths.get(uri), maxDepth, events);
   }

   public static WatchMonitor create(File file, WatchEvent.Kind<?>... events) {
      return create((File)file, 0, events);
   }

   public static WatchMonitor create(File file, int maxDepth, WatchEvent.Kind<?>... events) {
      return create(file.toPath(), maxDepth, events);
   }

   public static WatchMonitor create(String path, WatchEvent.Kind<?>... events) {
      return create((String)path, 0, events);
   }

   public static WatchMonitor create(String path, int maxDepth, WatchEvent.Kind<?>... events) {
      return create(Paths.get(path), maxDepth, events);
   }

   public static WatchMonitor create(Path path, WatchEvent.Kind<?>... events) {
      return create((Path)path, 0, events);
   }

   public static WatchMonitor create(Path path, int maxDepth, WatchEvent.Kind<?>... events) {
      return new WatchMonitor(path, maxDepth, events);
   }

   public static WatchMonitor createAll(URI uri, Watcher watcher) {
      return createAll(Paths.get(uri), watcher);
   }

   public static WatchMonitor createAll(URL url, Watcher watcher) {
      try {
         return createAll(Paths.get(url.toURI()), watcher);
      } catch (URISyntaxException var3) {
         throw new WatchException(var3);
      }
   }

   public static WatchMonitor createAll(File file, Watcher watcher) {
      return createAll(file.toPath(), watcher);
   }

   public static WatchMonitor createAll(String path, Watcher watcher) {
      return createAll(Paths.get(path), watcher);
   }

   public static WatchMonitor createAll(Path path, Watcher watcher) {
      WatchMonitor watchMonitor = create(path, EVENTS_ALL);
      watchMonitor.setWatcher(watcher);
      return watchMonitor;
   }

   public WatchMonitor(File file, WatchEvent.Kind<?>... events) {
      this(file.toPath(), events);
   }

   public WatchMonitor(String path, WatchEvent.Kind<?>... events) {
      this(Paths.get(path), events);
   }

   public WatchMonitor(Path path, WatchEvent.Kind<?>... events) {
      this(path, 0, events);
   }

   public WatchMonitor(Path path, int maxDepth, WatchEvent.Kind<?>... events) {
      this.path = path;
      this.maxDepth = maxDepth;
      this.events = events;
      this.init();
   }

   public void init() throws WatchException {
      if (!Files.exists(this.path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
         Path lastPathEle = FileUtil.getLastPathEle(this.path);
         if (null != lastPathEle) {
            String lastPathEleStr = lastPathEle.toString();
            if (StrUtil.contains(lastPathEleStr, '.') && !StrUtil.endWithIgnoreCase(lastPathEleStr, ".d")) {
               this.filePath = this.path;
               this.path = this.filePath.getParent();
            }
         }

         try {
            Files.createDirectories(this.path);
         } catch (IOException var3) {
            throw new IORuntimeException(var3);
         }
      } else if (Files.isRegularFile(this.path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
         this.filePath = this.path;
         this.path = this.filePath.getParent();
      }

      super.init();
   }

   public WatchMonitor setWatcher(Watcher watcher) {
      this.watcher = watcher;
      return this;
   }

   public void run() {
      this.watch();
   }

   public void watch() {
      this.watch(this.watcher);
   }

   public void watch(Watcher watcher) throws WatchException {
      if (this.isClosed) {
         throw new WatchException("Watch Monitor is closed !");
      } else {
         this.registerPath();

         while(!this.isClosed) {
            this.doTakeAndWatch(watcher);
         }

      }
   }

   public WatchMonitor setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
      return this;
   }

   private void doTakeAndWatch(Watcher watcher) {
      super.watch(watcher, (watchEvent) -> {
         return null == this.filePath || this.filePath.endsWith(watchEvent.context().toString());
      });
   }

   private void registerPath() {
      this.registerPath(this.path, null != this.filePath ? 0 : this.maxDepth);
   }

   static {
      OVERFLOW = WatchKind.OVERFLOW.getValue();
      ENTRY_MODIFY = WatchKind.MODIFY.getValue();
      ENTRY_CREATE = WatchKind.CREATE.getValue();
      ENTRY_DELETE = WatchKind.DELETE.getValue();
      EVENTS_ALL = WatchKind.ALL;
   }
}
