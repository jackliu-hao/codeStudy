package cn.hutool.core.swing.clipboard;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.io.Closeable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public enum ClipboardMonitor implements ClipboardOwner, Runnable, Closeable {
   INSTANCE;

   public static final int DEFAULT_TRY_COUNT = 10;
   public static final long DEFAULT_DELAY = 100L;
   private int tryCount;
   private long delay;
   private final Clipboard clipboard;
   private final Set<ClipboardListener> listenerSet;
   private boolean isRunning;

   private ClipboardMonitor() {
      this(10, 100L);
   }

   private ClipboardMonitor(int tryCount, long delay) {
      this(tryCount, delay, ClipboardUtil.getClipboard());
   }

   private ClipboardMonitor(int tryCount, long delay, Clipboard clipboard) {
      this.listenerSet = new LinkedHashSet();
      this.tryCount = tryCount;
      this.delay = delay;
      this.clipboard = clipboard;
   }

   public ClipboardMonitor setTryCount(int tryCount) {
      this.tryCount = tryCount;
      return this;
   }

   public ClipboardMonitor setDelay(long delay) {
      this.delay = delay;
      return this;
   }

   public ClipboardMonitor addListener(ClipboardListener listener) {
      this.listenerSet.add(listener);
      return this;
   }

   public ClipboardMonitor removeListener(ClipboardListener listener) {
      this.listenerSet.remove(listener);
      return this;
   }

   public ClipboardMonitor clearListener() {
      this.listenerSet.clear();
      return this;
   }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
      Transferable newContents;
      try {
         newContents = this.tryGetContent(clipboard);
      } catch (InterruptedException var9) {
         return;
      }

      Transferable transferable = null;
      Iterator var5 = this.listenerSet.iterator();

      while(var5.hasNext()) {
         ClipboardListener listener = (ClipboardListener)var5.next();

         try {
            transferable = listener.onChange(clipboard, (Transferable)ObjectUtil.defaultIfNull(transferable, (Object)newContents));
         } catch (Throwable var8) {
         }
      }

      if (this.isRunning) {
         clipboard.setContents((Transferable)ObjectUtil.defaultIfNull(transferable, (Object)ObjectUtil.defaultIfNull(newContents, (Object)contents)), this);
      }

   }

   public synchronized void run() {
      if (!this.isRunning) {
         Clipboard clipboard = this.clipboard;
         clipboard.setContents(clipboard.getContents((Object)null), this);
         this.isRunning = true;
      }

   }

   public void listen(boolean sync) {
      this.run();
      if (sync) {
         ThreadUtil.sync(this);
      }

   }

   public void close() {
      this.isRunning = false;
   }

   private Transferable tryGetContent(Clipboard clipboard) throws InterruptedException {
      Transferable newContents = null;

      for(int i = 0; i < this.tryCount; ++i) {
         if (this.delay > 0L && i > 0) {
            Thread.sleep(this.delay);
         }

         try {
            newContents = clipboard.getContents((Object)null);
         } catch (IllegalStateException var5) {
         }

         if (null != newContents) {
            return newContents;
         }
      }

      return null;
   }
}
