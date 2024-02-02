package cn.hutool.core.io.watch;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;

public class WatchUtil {
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

   public static WatchMonitor createAll(URL url, Watcher watcher) {
      return createAll((URL)url, 0, watcher);
   }

   public static WatchMonitor createAll(URL url, int maxDepth, Watcher watcher) {
      return createAll(URLUtil.toURI(url), maxDepth, watcher);
   }

   public static WatchMonitor createAll(URI uri, Watcher watcher) {
      return createAll((URI)uri, 0, watcher);
   }

   public static WatchMonitor createAll(URI uri, int maxDepth, Watcher watcher) {
      return createAll(Paths.get(uri), maxDepth, watcher);
   }

   public static WatchMonitor createAll(File file, Watcher watcher) {
      return createAll((File)file, 0, watcher);
   }

   public static WatchMonitor createAll(File file, int maxDepth, Watcher watcher) {
      return createAll((Path)file.toPath(), 0, watcher);
   }

   public static WatchMonitor createAll(String path, Watcher watcher) {
      return createAll((String)path, 0, watcher);
   }

   public static WatchMonitor createAll(String path, int maxDepth, Watcher watcher) {
      return createAll(Paths.get(path), maxDepth, watcher);
   }

   public static WatchMonitor createAll(Path path, Watcher watcher) {
      return createAll((Path)path, 0, watcher);
   }

   public static WatchMonitor createAll(Path path, int maxDepth, Watcher watcher) {
      WatchMonitor watchMonitor = create(path, maxDepth, WatchMonitor.EVENTS_ALL);
      watchMonitor.setWatcher(watcher);
      return watchMonitor;
   }

   public static WatchMonitor createModify(URL url, Watcher watcher) {
      return createModify((URL)url, 0, watcher);
   }

   public static WatchMonitor createModify(URL url, int maxDepth, Watcher watcher) {
      return createModify(URLUtil.toURI(url), maxDepth, watcher);
   }

   public static WatchMonitor createModify(URI uri, Watcher watcher) {
      return createModify((URI)uri, 0, watcher);
   }

   public static WatchMonitor createModify(URI uri, int maxDepth, Watcher watcher) {
      return createModify(Paths.get(uri), maxDepth, watcher);
   }

   public static WatchMonitor createModify(File file, Watcher watcher) {
      return createModify((File)file, 0, watcher);
   }

   public static WatchMonitor createModify(File file, int maxDepth, Watcher watcher) {
      return createModify((Path)file.toPath(), 0, watcher);
   }

   public static WatchMonitor createModify(String path, Watcher watcher) {
      return createModify((String)path, 0, watcher);
   }

   public static WatchMonitor createModify(String path, int maxDepth, Watcher watcher) {
      return createModify(Paths.get(path), maxDepth, watcher);
   }

   public static WatchMonitor createModify(Path path, Watcher watcher) {
      return createModify((Path)path, 0, watcher);
   }

   public static WatchMonitor createModify(Path path, int maxDepth, Watcher watcher) {
      WatchMonitor watchMonitor = create(path, maxDepth, WatchMonitor.ENTRY_MODIFY);
      watchMonitor.setWatcher(watcher);
      return watchMonitor;
   }

   public static WatchKey register(Watchable watchable, WatchService watcher, WatchEvent.Kind<?>... events) {
      try {
         return watchable.register(watcher, events);
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }
}
