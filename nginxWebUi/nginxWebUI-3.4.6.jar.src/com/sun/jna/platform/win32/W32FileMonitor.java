/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.platform.FileMonitor;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class W32FileMonitor
/*     */   extends FileMonitor
/*     */ {
/*  44 */   private static final Logger LOG = Logger.getLogger(W32FileMonitor.class.getName());
/*     */   private static final int BUFFER_SIZE = 4096;
/*     */   private Thread watcher;
/*     */   private WinNT.HANDLE port;
/*     */   
/*     */   private class FileInfo
/*     */   {
/*     */     public final File file;
/*     */     public final WinNT.HANDLE handle;
/*  53 */     public final WinNT.FILE_NOTIFY_INFORMATION info = new WinNT.FILE_NOTIFY_INFORMATION(4096); public final int notifyMask; public final boolean recursive;
/*  54 */     public final IntByReference infoLength = new IntByReference();
/*  55 */     public final WinBase.OVERLAPPED overlapped = new WinBase.OVERLAPPED();
/*     */     public FileInfo(File f, WinNT.HANDLE h, int mask, boolean recurse) {
/*  57 */       this.file = f;
/*  58 */       this.handle = h;
/*  59 */       this.notifyMask = mask;
/*  60 */       this.recursive = recurse;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*  65 */   private final Map<File, FileInfo> fileMap = new HashMap<File, FileInfo>();
/*  66 */   private final Map<WinNT.HANDLE, FileInfo> handleMap = new HashMap<WinNT.HANDLE, FileInfo>(); private boolean disposing = false;
/*     */   private static int watcherThreadID;
/*     */   
/*     */   private void handleChanges(FileInfo finfo) throws IOException {
/*  70 */     Kernel32 klib = Kernel32.INSTANCE;
/*  71 */     WinNT.FILE_NOTIFY_INFORMATION fni = finfo.info;
/*     */     
/*  73 */     fni.read();
/*     */     do {
/*  75 */       FileMonitor.FileEvent event = null;
/*  76 */       File file = new File(finfo.file, fni.getFilename());
/*  77 */       switch (fni.Action) {
/*     */         case 0:
/*     */           break;
/*     */         case 3:
/*  81 */           event = new FileMonitor.FileEvent(this, file, 4);
/*     */           break;
/*     */         case 1:
/*  84 */           event = new FileMonitor.FileEvent(this, file, 1);
/*     */           break;
/*     */         case 2:
/*  87 */           event = new FileMonitor.FileEvent(this, file, 2);
/*     */           break;
/*     */         case 4:
/*  90 */           event = new FileMonitor.FileEvent(this, file, 16);
/*     */           break;
/*     */         case 5:
/*  93 */           event = new FileMonitor.FileEvent(this, file, 32);
/*     */           break;
/*     */         
/*     */         default:
/*  97 */           LOG.log(Level.WARNING, "Unrecognized file action ''{0}''", Integer.valueOf(fni.Action));
/*     */           break;
/*     */       } 
/* 100 */       if (event != null) {
/* 101 */         notify(event);
/*     */       }
/*     */       
/* 104 */       fni = fni.next();
/* 105 */     } while (fni != null);
/*     */ 
/*     */     
/* 108 */     if (!finfo.file.exists()) {
/* 109 */       unwatch(finfo.file);
/*     */       
/*     */       return;
/*     */     } 
/* 113 */     if (!klib.ReadDirectoryChangesW(finfo.handle, finfo.info, finfo.info
/* 114 */         .size(), finfo.recursive, finfo.notifyMask, finfo.infoLength, finfo.overlapped, null))
/*     */     {
/* 116 */       if (!this.disposing) {
/* 117 */         int err = klib.GetLastError();
/* 118 */         throw new IOException("ReadDirectoryChangesW failed on " + finfo.file + ": '" + 
/*     */             
/* 120 */             Kernel32Util.formatMessageFromLastErrorCode(err) + "' (" + err + ")");
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private FileInfo waitForChange() {
/* 127 */     IntByReference rcount = new IntByReference();
/* 128 */     BaseTSD.ULONG_PTRByReference rkey = new BaseTSD.ULONG_PTRByReference();
/* 129 */     PointerByReference roverlap = new PointerByReference();
/* 130 */     if (!Kernel32.INSTANCE.GetQueuedCompletionStatus(this.port, rcount, rkey, roverlap, -1)) {
/* 131 */       return null;
/*     */     }
/* 133 */     synchronized (this) {
/* 134 */       return this.handleMap.get(new WinNT.HANDLE(rkey.getValue().toPointer()));
/*     */     } 
/*     */   }
/*     */   
/*     */   private int convertMask(int mask) {
/* 139 */     int result = 0;
/* 140 */     if ((mask & 0x1) != 0) {
/* 141 */       result |= 0x40;
/*     */     }
/* 143 */     if ((mask & 0x2) != 0) {
/* 144 */       result |= 0x3;
/*     */     }
/* 146 */     if ((mask & 0x4) != 0) {
/* 147 */       result |= 0x10;
/*     */     }
/* 149 */     if ((mask & 0x30) != 0) {
/* 150 */       result |= 0x3;
/*     */     }
/* 152 */     if ((mask & 0x40) != 0) {
/* 153 */       result |= 0x8;
/*     */     }
/* 155 */     if ((mask & 0x8) != 0) {
/* 156 */       result |= 0x20;
/*     */     }
/* 158 */     if ((mask & 0x80) != 0) {
/* 159 */       result |= 0x4;
/*     */     }
/* 161 */     if ((mask & 0x100) != 0) {
/* 162 */       result |= 0x100;
/*     */     }
/* 164 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized void watch(File file, int eventMask, boolean recursive) throws IOException {
/* 171 */     File dir = file;
/* 172 */     if (!dir.isDirectory()) {
/* 173 */       recursive = false;
/* 174 */       dir = file.getParentFile();
/*     */     } 
/* 176 */     while (dir != null && !dir.exists()) {
/* 177 */       recursive = true;
/* 178 */       dir = dir.getParentFile();
/*     */     } 
/* 180 */     if (dir == null) {
/* 181 */       throw new FileNotFoundException("No ancestor found for " + file);
/*     */     }
/* 183 */     Kernel32 klib = Kernel32.INSTANCE;
/* 184 */     int mask = 7;
/*     */     
/* 186 */     int flags = 1107296256;
/*     */     
/* 188 */     WinNT.HANDLE handle = klib.CreateFile(file.getAbsolutePath(), 1, mask, null, 3, flags, null);
/*     */ 
/*     */ 
/*     */     
/* 192 */     if (WinBase.INVALID_HANDLE_VALUE.equals(handle)) {
/* 193 */       throw new IOException("Unable to open " + file + " (" + klib
/* 194 */           .GetLastError() + ")");
/*     */     }
/* 196 */     int notifyMask = convertMask(eventMask);
/* 197 */     FileInfo finfo = new FileInfo(file, handle, notifyMask, recursive);
/* 198 */     this.fileMap.put(file, finfo);
/* 199 */     this.handleMap.put(handle, finfo);
/*     */     
/* 201 */     this.port = klib.CreateIoCompletionPort(handle, this.port, handle.getPointer(), 0);
/* 202 */     if (WinBase.INVALID_HANDLE_VALUE.equals(this.port)) {
/* 203 */       throw new IOException("Unable to create/use I/O Completion port for " + file + " (" + klib
/*     */           
/* 205 */           .GetLastError() + ")");
/*     */     }
/*     */ 
/*     */     
/* 209 */     if (!klib.ReadDirectoryChangesW(handle, finfo.info, finfo.info.size(), recursive, notifyMask, finfo.infoLength, finfo.overlapped, null)) {
/*     */ 
/*     */       
/* 212 */       int err = klib.GetLastError();
/* 213 */       throw new IOException("ReadDirectoryChangesW failed on " + finfo.file + ", handle " + handle + ": '" + 
/*     */           
/* 215 */           Kernel32Util.formatMessageFromLastErrorCode(err) + "' (" + err + ")");
/*     */     } 
/*     */     
/* 218 */     if (this.watcher == null) {
/* 219 */       this.watcher = new Thread("W32 File Monitor-" + watcherThreadID++)
/*     */         {
/*     */           public void run()
/*     */           {
/*     */             while (true) {
/* 224 */               W32FileMonitor.FileInfo finfo = W32FileMonitor.this.waitForChange();
/* 225 */               if (finfo == null) {
/* 226 */                 synchronized (W32FileMonitor.this) {
/* 227 */                   if (W32FileMonitor.this.fileMap.isEmpty()) {
/* 228 */                     W32FileMonitor.this.watcher = null;
/*     */                     
/*     */                     break;
/*     */                   } 
/*     */                 } 
/*     */                 continue;
/*     */               } 
/*     */               try {
/* 236 */                 W32FileMonitor.this.handleChanges(finfo);
/* 237 */               } catch (IOException e) {
/*     */                 
/* 239 */                 e.printStackTrace();
/*     */               } 
/*     */             } 
/*     */           }
/*     */         };
/* 244 */       this.watcher.setDaemon(true);
/* 245 */       this.watcher.start();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected synchronized void unwatch(File file) {
/* 251 */     FileInfo finfo = this.fileMap.remove(file);
/* 252 */     if (finfo != null) {
/* 253 */       this.handleMap.remove(finfo.handle);
/* 254 */       Kernel32 klib = Kernel32.INSTANCE;
/*     */       
/* 256 */       klib.CloseHandle(finfo.handle);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void dispose() {
/* 262 */     this.disposing = true;
/*     */ 
/*     */     
/* 265 */     int i = 0;
/* 266 */     for (Object[] keys = this.fileMap.keySet().toArray(); !this.fileMap.isEmpty();) {
/* 267 */       unwatch((File)keys[i++]);
/*     */     }
/*     */     
/* 270 */     Kernel32 klib = Kernel32.INSTANCE;
/* 271 */     klib.PostQueuedCompletionStatus(this.port, 0, null, null);
/* 272 */     klib.CloseHandle(this.port);
/* 273 */     this.port = null;
/* 274 */     this.watcher = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\W32FileMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */