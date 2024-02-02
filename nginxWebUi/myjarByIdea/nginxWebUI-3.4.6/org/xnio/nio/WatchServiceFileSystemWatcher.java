package org.xnio.nio;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.xnio.FileChangeCallback;
import org.xnio.FileChangeEvent;
import org.xnio.FileSystemWatcher;
import org.xnio.IoUtils;

class WatchServiceFileSystemWatcher implements FileSystemWatcher, Runnable {
   private static final AtomicInteger threadIdCounter = new AtomicInteger(0);
   public static final String THREAD_NAME = "xnio-file-watcher";
   private WatchService watchService;
   private final Map<File, PathData> files = Collections.synchronizedMap(new HashMap());
   private final Map<WatchKey, PathData> pathDataByKey = Collections.synchronizedMap(new IdentityHashMap());
   private volatile boolean stopped = false;
   private final Thread watchThread;

   WatchServiceFileSystemWatcher(String name, boolean daemon) {
      try {
         this.watchService = FileSystems.getDefault().newWatchService();
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }

      this.watchThread = new Thread(this, "xnio-file-watcher[" + name + "]-" + threadIdCounter);
      this.watchThread.setDaemon(daemon);
      this.watchThread.start();
   }

   public void run() {
      label225:
      while(true) {
         if (!this.stopped) {
            try {
               WatchKey key = this.watchService.take();
               if (key == null) {
                  continue;
               }

               try {
                  PathData pathData = (PathData)this.pathDataByKey.get(key);
                  if (pathData == null) {
                     continue;
                  }

                  List<FileChangeEvent> results = new ArrayList();
                  List<WatchEvent<?>> events = key.pollEvents();
                  Set<File> addedFiles = new HashSet();
                  Set<File> deletedFiles = new HashSet();
                  Iterator it = events.iterator();

                  while(true) {
                     File targetFile;
                     FileChangeEvent.Type type;
                     while(true) {
                        if (!it.hasNext()) {
                           key.pollEvents().clear();
                           it = results.iterator();

                           while(true) {
                              FileChangeEvent event;
                              do {
                                 label196:
                                 do {
                                    while(it.hasNext()) {
                                       event = (FileChangeEvent)it.next();
                                       if (event.getType() == FileChangeEvent.Type.MODIFIED) {
                                          continue label196;
                                       }

                                       if (event.getType() == FileChangeEvent.Type.ADDED) {
                                          if (deletedFiles.contains(event.getFile())) {
                                             it.remove();
                                          }
                                       } else if (event.getType() == FileChangeEvent.Type.REMOVED && addedFiles.contains(event.getFile())) {
                                          it.remove();
                                       }
                                    }

                                    if (results.isEmpty()) {
                                       continue label225;
                                    }

                                    Iterator var23 = pathData.callbacks.iterator();

                                    while(true) {
                                       if (!var23.hasNext()) {
                                          continue label225;
                                       }

                                       FileChangeCallback callback = (FileChangeCallback)var23.next();
                                       invokeCallback(callback, results);
                                    }
                                 } while(addedFiles.contains(event.getFile()) && deletedFiles.contains(event.getFile()));
                              } while(!addedFiles.contains(event.getFile()) && !deletedFiles.contains(event.getFile()));

                              it.remove();
                           }
                        }

                        WatchEvent<?> event = (WatchEvent)it.next();
                        Path eventPath = (Path)event.context();
                        targetFile = ((Path)key.watchable()).resolve(eventPath).toFile();
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                           type = FileChangeEvent.Type.ADDED;
                           addedFiles.add(targetFile);
                           if (targetFile.isDirectory()) {
                              try {
                                 this.addWatchedDirectory(pathData, targetFile);
                              } catch (IOException var18) {
                                 Log.log.debugf(var18, "Could not add watched directory %s", targetFile);
                              }
                           }
                           break;
                        }

                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                           type = FileChangeEvent.Type.MODIFIED;
                           break;
                        }

                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                           type = FileChangeEvent.Type.REMOVED;
                           deletedFiles.add(targetFile);
                           break;
                        }
                     }

                     results.add(new FileChangeEvent(targetFile, type));
                  }
               } finally {
                  if (!key.reset()) {
                     this.files.remove(key.watchable());
                  }

               }
            } catch (InterruptedException var20) {
               continue;
            } catch (ClosedWatchServiceException var21) {
            }
         }

         return;
      }
   }

   public synchronized void watchPath(File file, FileChangeCallback callback) {
      try {
         PathData data = (PathData)this.files.get(file);
         if (data == null) {
            Set<File> allDirectories = doScan(file).keySet();
            Path path = Paths.get(file.toURI());
            data = new PathData(path);
            Iterator var6 = allDirectories.iterator();

            while(var6.hasNext()) {
               File dir = (File)var6.next();
               this.addWatchedDirectory(data, dir);
            }

            this.files.put(file, data);
         }

         data.callbacks.add(callback);
      } catch (IOException var8) {
         throw new RuntimeException(var8);
      }
   }

   private void addWatchedDirectory(PathData data, File dir) throws IOException {
      Path path = Paths.get(dir.toURI());
      WatchKey key = path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
      this.pathDataByKey.put(key, data);
      data.keys.add(key);
   }

   public synchronized void unwatchPath(File file, FileChangeCallback callback) {
      PathData data = (PathData)this.files.get(file);
      if (data != null) {
         data.callbacks.remove(callback);
         if (data.callbacks.isEmpty()) {
            this.files.remove(file);
            Iterator var4 = data.keys.iterator();

            while(var4.hasNext()) {
               WatchKey key = (WatchKey)var4.next();
               key.cancel();
               this.pathDataByKey.remove(key);
            }
         }
      }

   }

   public void close() throws IOException {
      this.stopped = true;
      this.watchThread.interrupt();
      IoUtils.safeClose((Closeable)this.watchService);
   }

   private static Map<File, Long> doScan(File file) {
      Map<File, Long> results = new HashMap();
      Deque<File> toScan = new ArrayDeque();
      toScan.add(file);

      while(true) {
         File[] list;
         do {
            File next;
            do {
               if (toScan.isEmpty()) {
                  return results;
               }

               next = (File)toScan.pop();
            } while(!next.isDirectory());

            results.put(next, next.lastModified());
            list = next.listFiles();
         } while(list == null);

         File[] var5 = list;
         int var6 = list.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File f = var5[var7];
            toScan.push(new File(f.getAbsolutePath()));
         }
      }
   }

   private static void invokeCallback(FileChangeCallback callback, List<FileChangeEvent> results) {
      try {
         callback.handleChanges(results);
      } catch (Exception var3) {
         Log.log.failedToInvokeFileWatchCallback(var3);
      }

   }

   private class PathData {
      final Path path;
      final List<FileChangeCallback> callbacks;
      final List<WatchKey> keys;

      private PathData(Path path) {
         this.callbacks = new ArrayList();
         this.keys = new ArrayList();
         this.path = path;
      }

      // $FF: synthetic method
      PathData(Path x1, Object x2) {
         this(x1);
      }
   }
}
