/*     */ package io.undertow.server.handlers.resource;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.ETag;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.FileSystem;
/*     */ import java.nio.file.FileSystems;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.TreeSet;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.FileChangeCallback;
/*     */ import org.xnio.FileChangeEvent;
/*     */ import org.xnio.FileSystemWatcher;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Xnio;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathResourceManager
/*     */   implements ResourceManager
/*     */ {
/*  32 */   private static final Logger log = Logger.getLogger(PathResourceManager.class.getName());
/*     */   
/*  34 */   private static final boolean DEFAULT_CHANGE_LISTENERS_ALLOWED = !Boolean.getBoolean("io.undertow.disable-file-system-watcher"); private static final long DEFAULT_TRANSFER_MIN_SIZE = 1024L;
/*     */   
/*  36 */   private static final ETagFunction NULL_ETAG_FUNCTION = new ETagFunction()
/*     */     {
/*     */       public ETag generate(Path path) {
/*  39 */         return null;
/*     */       }
/*     */     };
/*     */   
/*  43 */   private final List<ResourceChangeListener> listeners = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private FileSystemWatcher fileSystemWatcher;
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile String base;
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile FileSystem fileSystem;
/*     */ 
/*     */ 
/*     */   
/*     */   private final long transferMinSize;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean caseSensitive;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean followLinks;
/*     */ 
/*     */ 
/*     */   
/*  71 */   private final TreeSet<String> safePaths = new TreeSet<>();
/*     */   
/*     */   private final ETagFunction eTagFunction;
/*     */   
/*     */   private final boolean allowResourceChangeListeners;
/*     */   
/*     */   public PathResourceManager(Path base) {
/*  78 */     this(base, 1024L, true, false, (String[])null);
/*     */   }
/*     */   
/*     */   public PathResourceManager(Path base, long transferMinSize) {
/*  82 */     this(base, transferMinSize, true, false, (String[])null);
/*     */   }
/*     */   
/*     */   public PathResourceManager(Path base, long transferMinSize, boolean caseSensitive) {
/*  86 */     this(base, transferMinSize, caseSensitive, false, (String[])null);
/*     */   }
/*     */   
/*     */   public PathResourceManager(Path base, long transferMinSize, boolean followLinks, String... safePaths) {
/*  90 */     this(base, transferMinSize, true, followLinks, safePaths);
/*     */   }
/*     */   
/*     */   protected PathResourceManager(long transferMinSize, boolean caseSensitive, boolean followLinks, String... safePaths) {
/*  94 */     this(transferMinSize, caseSensitive, followLinks, DEFAULT_CHANGE_LISTENERS_ALLOWED, safePaths);
/*     */   }
/*     */   
/*     */   protected PathResourceManager(long transferMinSize, boolean caseSensitive, boolean followLinks, boolean allowResourceChangeListeners, String... safePaths) {
/*  98 */     this.fileSystem = FileSystems.getDefault();
/*  99 */     this.caseSensitive = caseSensitive;
/* 100 */     this.followLinks = followLinks;
/* 101 */     this.transferMinSize = transferMinSize;
/* 102 */     this.allowResourceChangeListeners = allowResourceChangeListeners;
/* 103 */     if (this.followLinks) {
/* 104 */       if (safePaths == null) {
/* 105 */         throw UndertowMessages.MESSAGES.argumentCannotBeNull("safePaths");
/*     */       }
/* 107 */       for (String safePath : safePaths) {
/* 108 */         if (safePath == null) {
/* 109 */           throw UndertowMessages.MESSAGES.argumentCannotBeNull("safePaths");
/*     */         }
/*     */       } 
/* 112 */       this.safePaths.addAll(Arrays.asList(safePaths));
/*     */     } 
/* 114 */     this.eTagFunction = NULL_ETAG_FUNCTION;
/*     */   }
/*     */   
/*     */   public PathResourceManager(Path base, long transferMinSize, boolean caseSensitive, boolean followLinks, String... safePaths) {
/* 118 */     this(base, transferMinSize, caseSensitive, followLinks, DEFAULT_CHANGE_LISTENERS_ALLOWED, safePaths);
/*     */   }
/*     */   
/*     */   public PathResourceManager(Path base, long transferMinSize, boolean caseSensitive, boolean followLinks, boolean allowResourceChangeListeners, String... safePaths) {
/* 122 */     this(builder()
/* 123 */         .setBase(base)
/* 124 */         .setTransferMinSize(transferMinSize)
/* 125 */         .setCaseSensitive(caseSensitive)
/* 126 */         .setFollowLinks(followLinks)
/* 127 */         .setAllowResourceChangeListeners(allowResourceChangeListeners)
/* 128 */         .setSafePaths(safePaths));
/*     */   }
/*     */   
/*     */   private PathResourceManager(Builder builder) {
/* 132 */     this.allowResourceChangeListeners = builder.allowResourceChangeListeners;
/* 133 */     if (builder.base == null) {
/* 134 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("base");
/*     */     }
/* 136 */     this.fileSystem = builder.base.getFileSystem();
/* 137 */     String basePath = null;
/*     */     try {
/* 139 */       if (builder.followLinks) {
/* 140 */         basePath = builder.base.normalize().toRealPath(new LinkOption[0]).toString();
/*     */       } else {
/* 142 */         basePath = builder.base.normalize().toRealPath(new LinkOption[] { LinkOption.NOFOLLOW_LINKS }).toString();
/*     */       } 
/* 144 */     } catch (IOException e) {
/* 145 */       throw UndertowMessages.MESSAGES.failedToInitializePathManager(builder.base.toString(), e);
/*     */     } 
/* 147 */     if (!basePath.endsWith(this.fileSystem.getSeparator())) {
/* 148 */       basePath = basePath + this.fileSystem.getSeparator();
/*     */     }
/* 150 */     this.base = basePath;
/* 151 */     this.transferMinSize = builder.transferMinSize;
/* 152 */     this.caseSensitive = builder.caseSensitive;
/* 153 */     this.followLinks = builder.followLinks;
/* 154 */     if (this.followLinks) {
/* 155 */       if (builder.safePaths == null) {
/* 156 */         throw UndertowMessages.MESSAGES.argumentCannotBeNull("safePaths");
/*     */       }
/* 158 */       for (String safePath : builder.safePaths) {
/* 159 */         if (safePath == null) {
/* 160 */           throw UndertowMessages.MESSAGES.argumentCannotBeNull("safePaths");
/*     */         }
/*     */       } 
/* 163 */       this.safePaths.addAll(Arrays.asList(builder.safePaths));
/*     */     } 
/* 165 */     this.eTagFunction = builder.eTagFunction;
/*     */   }
/*     */   
/*     */   public Path getBasePath() {
/* 169 */     return this.fileSystem.getPath(this.base, new String[0]);
/*     */   }
/*     */   
/*     */   public PathResourceManager setBase(Path base) {
/* 173 */     if (base == null) {
/* 174 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("base");
/*     */     }
/* 176 */     this.fileSystem = base.getFileSystem();
/* 177 */     String basePath = base.toAbsolutePath().toString();
/* 178 */     if (!basePath.endsWith(this.fileSystem.getSeparator())) {
/* 179 */       basePath = basePath + this.fileSystem.getSeparator();
/*     */     }
/* 181 */     this.base = basePath;
/* 182 */     return this;
/*     */   }
/*     */   
/*     */   public PathResourceManager setBase(File base) {
/* 186 */     if (base == null) {
/* 187 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("base");
/*     */     }
/* 189 */     this.fileSystem = FileSystems.getDefault();
/* 190 */     String basePath = base.getAbsolutePath();
/* 191 */     if (!basePath.endsWith(this.fileSystem.getSeparator())) {
/* 192 */       basePath = basePath + this.fileSystem.getSeparator();
/*     */     }
/* 194 */     this.base = basePath;
/* 195 */     return this;
/*     */   }
/*     */   public Resource getResource(String p) {
/*     */     String path;
/* 199 */     if (p == null) {
/* 200 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 204 */     if (p.startsWith("/")) {
/* 205 */       path = p.substring(1);
/*     */     } else {
/* 207 */       path = p;
/*     */     } 
/*     */     try {
/* 210 */       Path file = this.fileSystem.getPath(this.base, new String[] { path });
/* 211 */       String normalizedFile = file.normalize().toString();
/* 212 */       if (!normalizedFile.startsWith(this.base)) {
/* 213 */         if (normalizedFile.length() == this.base.length() - 1) {
/*     */           
/* 215 */           if (!this.base.startsWith(normalizedFile)) {
/* 216 */             log.tracef("Failed to get path resource %s from path resource manager with base %s, as file was outside the base directory", p, this.base);
/* 217 */             return null;
/*     */           } 
/*     */         } else {
/* 220 */           log.tracef("Failed to get path resource %s from path resource manager with base %s, as file was outside the base directory", p, this.base);
/* 221 */           return null;
/*     */         } 
/*     */       }
/* 224 */       if (Files.exists(file, new LinkOption[0])) {
/* 225 */         if (path.endsWith("/") && !Files.isDirectory(file, new LinkOption[0])) {
/*     */           
/* 227 */           log.tracef("Failed to get path resource %s from path resource manager with base %s, as path ended with a / but was not a directory", p, this.base);
/* 228 */           return null;
/*     */         } 
/* 230 */         boolean followAll = (this.followLinks && this.safePaths.isEmpty());
/* 231 */         SymlinkResult symlinkBase = getSymlinkBase(this.base, file);
/* 232 */         if (!followAll && symlinkBase != null && symlinkBase.requiresCheck) {
/* 233 */           if (this.followLinks && isSymlinkSafe(file)) {
/* 234 */             return getFileResource(file, path, symlinkBase.path, normalizedFile);
/*     */           }
/* 236 */           log.tracef("Failed to get path resource %s from path resource manager with base %s, as it was not a safe symlink path", p, this.base);
/* 237 */           return null;
/*     */         } 
/*     */         
/* 240 */         return getFileResource(file, path, (symlinkBase == null) ? null : symlinkBase.path, normalizedFile);
/*     */       } 
/*     */       
/* 243 */       log.tracef("Failed to get path resource %s from path resource manager with base %s, as the path did not exist", p, this.base);
/* 244 */       return null;
/*     */     }
/* 246 */     catch (IOException e) {
/* 247 */       UndertowLogger.REQUEST_LOGGER.debugf(e, "Invalid path %s", p);
/* 248 */       return null;
/* 249 */     } catch (SecurityException e) {
/* 250 */       UndertowLogger.REQUEST_LOGGER.errorf(e, "Missing JSM permissions for path %s", p);
/* 251 */       throw e;
/* 252 */     } catch (Exception e) {
/* 253 */       UndertowLogger.REQUEST_LOGGER.debugf(e, "Other issue for path %s", p);
/* 254 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isResourceChangeListenerSupported() {
/* 260 */     return this.allowResourceChangeListeners;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void registerResourceChangeListener(ResourceChangeListener listener) {
/* 265 */     if (!this.allowResourceChangeListeners) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 270 */     if (!this.fileSystem.equals(FileSystems.getDefault())) {
/* 271 */       throw new IllegalStateException("Resource change listeners not supported when using a non-default file system");
/*     */     }
/* 273 */     this.listeners.add(listener);
/* 274 */     if (this.fileSystemWatcher == null) {
/* 275 */       this.fileSystemWatcher = Xnio.getInstance().createFileSystemWatcher("Watcher for " + this.base, OptionMap.EMPTY);
/* 276 */       this.fileSystemWatcher.watchPath(new File(this.base), new FileChangeCallback()
/*     */           {
/*     */             public void handleChanges(Collection<FileChangeEvent> changes) {
/* 279 */               synchronized (PathResourceManager.this) {
/* 280 */                 List<ResourceChangeEvent> events = new ArrayList<>();
/* 281 */                 for (FileChangeEvent change : changes) {
/* 282 */                   if (change.getFile().getAbsolutePath().startsWith(PathResourceManager.this.base)) {
/* 283 */                     String path = change.getFile().getAbsolutePath().substring(PathResourceManager.this.base.length());
/* 284 */                     if (File.separatorChar == '\\' && path.contains(File.separator)) {
/* 285 */                       path = path.replace(File.separatorChar, '/');
/*     */                     }
/* 287 */                     events.add(new ResourceChangeEvent(path, ResourceChangeEvent.Type.valueOf(change.getType().name())));
/*     */                   } 
/*     */                 } 
/* 290 */                 for (ResourceChangeListener listener : PathResourceManager.this.listeners) {
/* 291 */                   listener.handleChanges(events);
/*     */                 }
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void removeResourceChangeListener(ResourceChangeListener listener) {
/* 302 */     if (!this.allowResourceChangeListeners) {
/*     */       return;
/*     */     }
/* 305 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */   public long getTransferMinSize() {
/* 309 */     return this.transferMinSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void close() throws IOException {
/* 314 */     if (this.fileSystemWatcher != null) {
/* 315 */       this.fileSystemWatcher.close();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SymlinkResult getSymlinkBase(String base, Path file) {
/* 323 */     int nameCount = file.getNameCount();
/* 324 */     Path root = this.fileSystem.getPath(base, new String[0]);
/* 325 */     int rootCount = root.getNameCount();
/* 326 */     Path f = file;
/* 327 */     for (int i = nameCount - 1; i >= 0; i--) {
/* 328 */       if (SecurityActions.isSymbolicLink(f).booleanValue()) {
/* 329 */         return new SymlinkResult((i + 1 > rootCount), f);
/*     */       }
/* 331 */       f = f.getParent();
/*     */     } 
/*     */     
/* 334 */     return null;
/*     */   }
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
/*     */   private boolean isFileSameCase(Path file, String normalizeFile) throws IOException {
/* 348 */     String canonicalName = file.toRealPath(new LinkOption[0]).toString();
/* 349 */     return canonicalName.equals(normalizeFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isSymlinkSafe(Path file) throws IOException {
/* 357 */     String canonicalPath = file.toRealPath(new LinkOption[0]).toString();
/* 358 */     for (String safePath : this.safePaths) {
/* 359 */       if (safePath.length() > 0) {
/* 360 */         if (safePath.startsWith(this.fileSystem.getSeparator())) {
/*     */ 
/*     */ 
/*     */           
/* 364 */           if (safePath.length() > 0 && canonicalPath
/* 365 */             .length() >= safePath.length() && canonicalPath
/* 366 */             .startsWith(safePath)) {
/* 367 */             return true;
/*     */           }
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 373 */         String absSafePath = this.base + this.fileSystem.getSeparator() + safePath;
/* 374 */         Path absSafePathFile = this.fileSystem.getPath(absSafePath, new String[0]);
/* 375 */         String canonicalSafePath = absSafePathFile.toRealPath(new LinkOption[0]).toString();
/* 376 */         if (canonicalSafePath.length() > 0 && canonicalPath
/* 377 */           .length() >= canonicalSafePath.length() && canonicalPath
/* 378 */           .startsWith(canonicalSafePath)) {
/* 379 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 385 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PathResource getFileResource(Path file, String path, Path symlinkBase, String normalizedFile) throws IOException {
/* 392 */     if (this.caseSensitive) {
/* 393 */       if (symlinkBase != null) {
/* 394 */         String relative = symlinkBase.relativize(file.normalize()).toString();
/* 395 */         String fileResolved = file.toRealPath(new LinkOption[0]).toString();
/* 396 */         String symlinkBaseResolved = symlinkBase.toRealPath(new LinkOption[0]).toString();
/* 397 */         if (!fileResolved.startsWith(symlinkBaseResolved)) {
/* 398 */           log.tracef("Rejected path resource %s from path resource manager with base %s, as the case did not match actual case of %s", path, this.base, normalizedFile);
/* 399 */           return null;
/*     */         } 
/* 401 */         String compare = fileResolved.substring(symlinkBaseResolved.length());
/* 402 */         if (compare.startsWith(this.fileSystem.getSeparator())) {
/* 403 */           compare = compare.substring(this.fileSystem.getSeparator().length());
/*     */         }
/* 405 */         if (relative.startsWith(this.fileSystem.getSeparator())) {
/* 406 */           relative = relative.substring(this.fileSystem.getSeparator().length());
/*     */         }
/* 408 */         if (relative.equals(compare)) {
/* 409 */           log.tracef("Found path resource %s from path resource manager with base %s", path, this.base);
/* 410 */           return new PathResource(file, this, path, this.eTagFunction.generate(file));
/*     */         } 
/* 412 */         log.tracef("Rejected path resource %s from path resource manager with base %s, as the case did not match actual case of %s", path, this.base, normalizedFile);
/* 413 */         return null;
/* 414 */       }  if (isFileSameCase(file, normalizedFile)) {
/* 415 */         log.tracef("Found path resource %s from path resource manager with base %s", path, this.base);
/* 416 */         return new PathResource(file, this, path, this.eTagFunction.generate(file));
/*     */       } 
/* 418 */       log.tracef("Rejected path resource %s from path resource manager with base %s, as the case did not match actual case of %s", path, this.base, normalizedFile);
/* 419 */       return null;
/*     */     } 
/*     */     
/* 422 */     log.tracef("Found path resource %s from path resource manager with base %s", path, this.base);
/* 423 */     return new PathResource(file, this, path, this.eTagFunction.generate(file));
/*     */   }
/*     */   
/*     */   private static class SymlinkResult
/*     */   {
/*     */     public final boolean requiresCheck;
/*     */     public final Path path;
/*     */     
/*     */     private SymlinkResult(boolean requiresCheck, Path path) {
/* 432 */       this.requiresCheck = requiresCheck;
/* 433 */       this.path = path;
/*     */     }
/*     */   }
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
/*     */   public static Builder builder() {
/* 449 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static interface ETagFunction {
/*     */     ETag generate(Path param1Path); }
/*     */   
/* 455 */   public static final class Builder { private long transferMinSize = 1024L; private Path base;
/*     */     private boolean caseSensitive = true;
/*     */     private boolean followLinks = false;
/* 458 */     private boolean allowResourceChangeListeners = PathResourceManager.DEFAULT_CHANGE_LISTENERS_ALLOWED;
/* 459 */     private PathResourceManager.ETagFunction eTagFunction = PathResourceManager.NULL_ETAG_FUNCTION;
/*     */ 
/*     */     
/*     */     private String[] safePaths;
/*     */ 
/*     */     
/*     */     public Builder setBase(Path base) {
/* 466 */       this.base = base;
/* 467 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setTransferMinSize(long transferMinSize) {
/* 471 */       this.transferMinSize = transferMinSize;
/* 472 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCaseSensitive(boolean caseSensitive) {
/* 476 */       this.caseSensitive = caseSensitive;
/* 477 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setFollowLinks(boolean followLinks) {
/* 481 */       this.followLinks = followLinks;
/* 482 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setAllowResourceChangeListeners(boolean allowResourceChangeListeners) {
/* 486 */       this.allowResourceChangeListeners = allowResourceChangeListeners;
/* 487 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setETagFunction(PathResourceManager.ETagFunction eTagFunction) {
/* 491 */       this.eTagFunction = eTagFunction;
/* 492 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setSafePaths(String[] safePaths) {
/* 496 */       this.safePaths = safePaths;
/* 497 */       return this;
/*     */     }
/*     */     
/*     */     public ResourceManager build() {
/* 501 */       return new PathResourceManager(this);
/*     */     }
/*     */     
/*     */     private Builder() {} }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\PathResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */