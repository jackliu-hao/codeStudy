package org.xnio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.xnio._private.Messages;

class PollingFileSystemWatcher implements FileSystemWatcher, Runnable {
   private static final AtomicInteger threadIdCounter = new AtomicInteger(0);
   public static final String THREAD_NAME = "xnio-polling-file-watcher";
   private final Map<File, PollHolder> files = Collections.synchronizedMap(new HashMap());
   private final Thread watchThread;
   private final int pollInterval;
   private volatile boolean stopped = false;

   PollingFileSystemWatcher(String name, int pollInterval, boolean daemon) {
      this.watchThread = new Thread(this, "xnio-polling-file-watcher[" + name + "]-" + threadIdCounter);
      this.watchThread.setDaemon(daemon);
      this.watchThread.start();
      this.pollInterval = pollInterval;
   }

   public void run() {
      while(!this.stopped) {
         try {
            this.doNotify();
            Thread.sleep((long)this.pollInterval);
         } catch (InterruptedException var2) {
         }
      }

   }

   private void doNotify() {
      Iterator var1 = this.files.entrySet().iterator();

      while(true) {
         Map.Entry entry;
         Map result;
         List currentDiff;
         do {
            if (!var1.hasNext()) {
               return;
            }

            entry = (Map.Entry)var1.next();
            result = doScan((File)entry.getKey());
            currentDiff = this.doDiff(result, ((PollHolder)entry.getValue()).currentFileState);
         } while(currentDiff.isEmpty());

         ((PollHolder)entry.getValue()).currentFileState = result;
         Iterator var5 = ((PollHolder)entry.getValue()).callbacks.iterator();

         while(var5.hasNext()) {
            FileChangeCallback callback = (FileChangeCallback)var5.next();
            invokeCallback(callback, currentDiff);
         }
      }
   }

   private List<FileChangeEvent> doDiff(Map<File, Long> newFileState, Map<File, Long> currentFileState) {
      List<FileChangeEvent> results = new ArrayList();
      Map<File, Long> currentCopy = new HashMap(currentFileState);
      Iterator var5 = newFileState.entrySet().iterator();

      Map.Entry newEntry;
      while(var5.hasNext()) {
         newEntry = (Map.Entry)var5.next();
         Long old = (Long)currentCopy.remove(newEntry.getKey());
         if (old == null) {
            results.add(new FileChangeEvent((File)newEntry.getKey(), FileChangeEvent.Type.ADDED));
         } else if (!old.equals(newEntry.getValue()) && !((File)newEntry.getKey()).isDirectory()) {
            results.add(new FileChangeEvent((File)newEntry.getKey(), FileChangeEvent.Type.MODIFIED));
         }
      }

      var5 = currentCopy.entrySet().iterator();

      while(var5.hasNext()) {
         newEntry = (Map.Entry)var5.next();
         results.add(new FileChangeEvent((File)newEntry.getKey(), FileChangeEvent.Type.REMOVED));
      }

      return results;
   }

   public synchronized void watchPath(File file, FileChangeCallback callback) {
      PollHolder holder = (PollHolder)this.files.get(file);
      if (holder == null) {
         this.files.put(file, holder = new PollHolder(doScan(file)));
      }

      holder.callbacks.add(callback);
   }

   public synchronized void unwatchPath(File file, FileChangeCallback callback) {
      PollHolder holder = (PollHolder)this.files.get(file);
      if (holder != null) {
         holder.callbacks.remove(callback);
         if (holder.callbacks.isEmpty()) {
            this.files.remove(file);
         }
      }

      this.files.remove(file);
   }

   public void close() throws IOException {
      this.stopped = true;
      this.watchThread.interrupt();
   }

   static Map<File, Long> doScan(File file) {
      Map<File, Long> results = new HashMap();
      Deque<File> toScan = new ArrayDeque();
      toScan.add(file);

      while(true) {
         File[] list;
         label24:
         do {
            while(!toScan.isEmpty()) {
               File next = (File)toScan.pop();
               if (next.isDirectory()) {
                  results.put(next, next.lastModified());
                  list = next.listFiles();
                  continue label24;
               }

               results.put(next, next.lastModified());
            }

            return results;
         } while(list == null);

         File[] var5 = list;
         int var6 = list.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File f = var5[var7];
            toScan.push(new File(f.getAbsolutePath()));
         }
      }
   }

   static void invokeCallback(FileChangeCallback callback, List<FileChangeEvent> results) {
      try {
         callback.handleChanges(results);
      } catch (Exception var3) {
         Messages.msg.failedToInvokeFileWatchCallback(var3);
      }

   }

   private class PollHolder {
      Map<File, Long> currentFileState;
      final List<FileChangeCallback> callbacks;

      private PollHolder(Map<File, Long> currentFileState) {
         this.callbacks = new ArrayList();
         this.currentFileState = currentFileState;
      }

      // $FF: synthetic method
      PollHolder(Map x1, Object x2) {
         this(x1);
      }
   }
}
