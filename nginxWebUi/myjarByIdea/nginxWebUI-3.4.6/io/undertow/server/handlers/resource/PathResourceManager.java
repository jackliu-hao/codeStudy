package io.undertow.server.handlers.resource;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.util.ETag;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.jboss.logging.Logger;
import org.xnio.FileChangeCallback;
import org.xnio.FileChangeEvent;
import org.xnio.FileSystemWatcher;
import org.xnio.OptionMap;
import org.xnio.Xnio;

public class PathResourceManager implements ResourceManager {
   private static final Logger log = Logger.getLogger(PathResourceManager.class.getName());
   private static final boolean DEFAULT_CHANGE_LISTENERS_ALLOWED = !Boolean.getBoolean("io.undertow.disable-file-system-watcher");
   private static final long DEFAULT_TRANSFER_MIN_SIZE = 1024L;
   private static final ETagFunction NULL_ETAG_FUNCTION = new ETagFunction() {
      public ETag generate(Path path) {
         return null;
      }
   };
   private final List<ResourceChangeListener> listeners;
   private FileSystemWatcher fileSystemWatcher;
   protected volatile String base;
   protected volatile FileSystem fileSystem;
   private final long transferMinSize;
   private final boolean caseSensitive;
   private final boolean followLinks;
   private final TreeSet<String> safePaths;
   private final ETagFunction eTagFunction;
   private final boolean allowResourceChangeListeners;

   public PathResourceManager(Path base) {
      this(base, 1024L, true, false, (String[])null);
   }

   public PathResourceManager(Path base, long transferMinSize) {
      this(base, transferMinSize, true, false, (String[])null);
   }

   public PathResourceManager(Path base, long transferMinSize, boolean caseSensitive) {
      this(base, transferMinSize, caseSensitive, false, (String[])null);
   }

   public PathResourceManager(Path base, long transferMinSize, boolean followLinks, String... safePaths) {
      this(base, transferMinSize, true, followLinks, safePaths);
   }

   protected PathResourceManager(long transferMinSize, boolean caseSensitive, boolean followLinks, String... safePaths) {
      this(transferMinSize, caseSensitive, followLinks, DEFAULT_CHANGE_LISTENERS_ALLOWED, safePaths);
   }

   protected PathResourceManager(long transferMinSize, boolean caseSensitive, boolean followLinks, boolean allowResourceChangeListeners, String... safePaths) {
      this.listeners = new ArrayList();
      this.safePaths = new TreeSet();
      this.fileSystem = FileSystems.getDefault();
      this.caseSensitive = caseSensitive;
      this.followLinks = followLinks;
      this.transferMinSize = transferMinSize;
      this.allowResourceChangeListeners = allowResourceChangeListeners;
      if (this.followLinks) {
         if (safePaths == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("safePaths");
         }

         String[] var7 = safePaths;
         int var8 = safePaths.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String safePath = var7[var9];
            if (safePath == null) {
               throw UndertowMessages.MESSAGES.argumentCannotBeNull("safePaths");
            }
         }

         this.safePaths.addAll(Arrays.asList(safePaths));
      }

      this.eTagFunction = NULL_ETAG_FUNCTION;
   }

   public PathResourceManager(Path base, long transferMinSize, boolean caseSensitive, boolean followLinks, String... safePaths) {
      this(base, transferMinSize, caseSensitive, followLinks, DEFAULT_CHANGE_LISTENERS_ALLOWED, safePaths);
   }

   public PathResourceManager(Path base, long transferMinSize, boolean caseSensitive, boolean followLinks, boolean allowResourceChangeListeners, String... safePaths) {
      this(builder().setBase(base).setTransferMinSize(transferMinSize).setCaseSensitive(caseSensitive).setFollowLinks(followLinks).setAllowResourceChangeListeners(allowResourceChangeListeners).setSafePaths(safePaths));
   }

   private PathResourceManager(Builder builder) {
      this.listeners = new ArrayList();
      this.safePaths = new TreeSet();
      this.allowResourceChangeListeners = builder.allowResourceChangeListeners;
      if (builder.base == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("base");
      } else {
         this.fileSystem = builder.base.getFileSystem();
         String basePath = null;

         try {
            if (builder.followLinks) {
               basePath = builder.base.normalize().toRealPath().toString();
            } else {
               basePath = builder.base.normalize().toRealPath(LinkOption.NOFOLLOW_LINKS).toString();
            }
         } catch (IOException var7) {
            throw UndertowMessages.MESSAGES.failedToInitializePathManager(builder.base.toString(), var7);
         }

         if (!basePath.endsWith(this.fileSystem.getSeparator())) {
            basePath = basePath + this.fileSystem.getSeparator();
         }

         this.base = basePath;
         this.transferMinSize = builder.transferMinSize;
         this.caseSensitive = builder.caseSensitive;
         this.followLinks = builder.followLinks;
         if (this.followLinks) {
            if (builder.safePaths == null) {
               throw UndertowMessages.MESSAGES.argumentCannotBeNull("safePaths");
            }

            String[] var3 = builder.safePaths;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String safePath = var3[var5];
               if (safePath == null) {
                  throw UndertowMessages.MESSAGES.argumentCannotBeNull("safePaths");
               }
            }

            this.safePaths.addAll(Arrays.asList(builder.safePaths));
         }

         this.eTagFunction = builder.eTagFunction;
      }
   }

   public Path getBasePath() {
      return this.fileSystem.getPath(this.base);
   }

   public PathResourceManager setBase(Path base) {
      if (base == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("base");
      } else {
         this.fileSystem = base.getFileSystem();
         String basePath = base.toAbsolutePath().toString();
         if (!basePath.endsWith(this.fileSystem.getSeparator())) {
            basePath = basePath + this.fileSystem.getSeparator();
         }

         this.base = basePath;
         return this;
      }
   }

   public PathResourceManager setBase(File base) {
      if (base == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("base");
      } else {
         this.fileSystem = FileSystems.getDefault();
         String basePath = base.getAbsolutePath();
         if (!basePath.endsWith(this.fileSystem.getSeparator())) {
            basePath = basePath + this.fileSystem.getSeparator();
         }

         this.base = basePath;
         return this;
      }
   }

   public Resource getResource(String p) {
      if (p == null) {
         return null;
      } else {
         String path;
         if (p.startsWith("/")) {
            path = p.substring(1);
         } else {
            path = p;
         }

         try {
            Path file = this.fileSystem.getPath(this.base, path);
            String normalizedFile = file.normalize().toString();
            if (!normalizedFile.startsWith(this.base)) {
               if (normalizedFile.length() != this.base.length() - 1) {
                  log.tracef((String)"Failed to get path resource %s from path resource manager with base %s, as file was outside the base directory", (Object)p, (Object)this.base);
                  return null;
               }

               if (!this.base.startsWith(normalizedFile)) {
                  log.tracef((String)"Failed to get path resource %s from path resource manager with base %s, as file was outside the base directory", (Object)p, (Object)this.base);
                  return null;
               }
            }

            if (Files.exists(file, new LinkOption[0])) {
               if (path.endsWith("/") && !Files.isDirectory(file, new LinkOption[0])) {
                  log.tracef((String)"Failed to get path resource %s from path resource manager with base %s, as path ended with a / but was not a directory", (Object)p, (Object)this.base);
                  return null;
               } else {
                  boolean followAll = this.followLinks && this.safePaths.isEmpty();
                  SymlinkResult symlinkBase = this.getSymlinkBase(this.base, file);
                  if (!followAll && symlinkBase != null && symlinkBase.requiresCheck) {
                     if (this.followLinks && this.isSymlinkSafe(file)) {
                        return this.getFileResource(file, path, symlinkBase.path, normalizedFile);
                     } else {
                        log.tracef((String)"Failed to get path resource %s from path resource manager with base %s, as it was not a safe symlink path", (Object)p, (Object)this.base);
                        return null;
                     }
                  } else {
                     return this.getFileResource(file, path, symlinkBase == null ? null : symlinkBase.path, normalizedFile);
                  }
               }
            } else {
               log.tracef((String)"Failed to get path resource %s from path resource manager with base %s, as the path did not exist", (Object)p, (Object)this.base);
               return null;
            }
         } catch (IOException var7) {
            UndertowLogger.REQUEST_LOGGER.debugf(var7, "Invalid path %s", p);
            return null;
         } catch (SecurityException var8) {
            UndertowLogger.REQUEST_LOGGER.errorf(var8, "Missing JSM permissions for path %s", p);
            throw var8;
         } catch (Exception var9) {
            UndertowLogger.REQUEST_LOGGER.debugf(var9, "Other issue for path %s", p);
            return null;
         }
      }
   }

   public boolean isResourceChangeListenerSupported() {
      return this.allowResourceChangeListeners;
   }

   public synchronized void registerResourceChangeListener(ResourceChangeListener listener) {
      if (this.allowResourceChangeListeners) {
         if (!this.fileSystem.equals(FileSystems.getDefault())) {
            throw new IllegalStateException("Resource change listeners not supported when using a non-default file system");
         } else {
            this.listeners.add(listener);
            if (this.fileSystemWatcher == null) {
               this.fileSystemWatcher = Xnio.getInstance().createFileSystemWatcher("Watcher for " + this.base, OptionMap.EMPTY);
               this.fileSystemWatcher.watchPath(new File(this.base), new FileChangeCallback() {
                  public void handleChanges(Collection<FileChangeEvent> changes) {
                     synchronized(PathResourceManager.this) {
                        List<ResourceChangeEvent> events = new ArrayList();
                        Iterator var4 = changes.iterator();

                        while(var4.hasNext()) {
                           FileChangeEvent change = (FileChangeEvent)var4.next();
                           if (change.getFile().getAbsolutePath().startsWith(PathResourceManager.this.base)) {
                              String path = change.getFile().getAbsolutePath().substring(PathResourceManager.this.base.length());
                              if (File.separatorChar == '\\' && path.contains(File.separator)) {
                                 path = path.replace(File.separatorChar, '/');
                              }

                              events.add(new ResourceChangeEvent(path, ResourceChangeEvent.Type.valueOf(change.getType().name())));
                           }
                        }

                        var4 = PathResourceManager.this.listeners.iterator();

                        while(var4.hasNext()) {
                           ResourceChangeListener listener = (ResourceChangeListener)var4.next();
                           listener.handleChanges(events);
                        }

                     }
                  }
               });
            }

         }
      }
   }

   public synchronized void removeResourceChangeListener(ResourceChangeListener listener) {
      if (this.allowResourceChangeListeners) {
         this.listeners.remove(listener);
      }
   }

   public long getTransferMinSize() {
      return this.transferMinSize;
   }

   public synchronized void close() throws IOException {
      if (this.fileSystemWatcher != null) {
         this.fileSystemWatcher.close();
      }

   }

   private SymlinkResult getSymlinkBase(String base, Path file) {
      int nameCount = file.getNameCount();
      Path root = this.fileSystem.getPath(base);
      int rootCount = root.getNameCount();
      Path f = file;

      for(int i = nameCount - 1; i >= 0; --i) {
         if (SecurityActions.isSymbolicLink(f)) {
            return new SymlinkResult(i + 1 > rootCount, f);
         }

         f = f.getParent();
      }

      return null;
   }

   private boolean isFileSameCase(Path file, String normalizeFile) throws IOException {
      String canonicalName = file.toRealPath().toString();
      return canonicalName.equals(normalizeFile);
   }

   private boolean isSymlinkSafe(Path file) throws IOException {
      String canonicalPath = file.toRealPath().toString();
      Iterator var3 = this.safePaths.iterator();

      while(var3.hasNext()) {
         String safePath = (String)var3.next();
         if (safePath.length() > 0) {
            if (safePath.startsWith(this.fileSystem.getSeparator())) {
               if (safePath.length() > 0 && canonicalPath.length() >= safePath.length() && canonicalPath.startsWith(safePath)) {
                  return true;
               }
            } else {
               String absSafePath = this.base + this.fileSystem.getSeparator() + safePath;
               Path absSafePathFile = this.fileSystem.getPath(absSafePath);
               String canonicalSafePath = absSafePathFile.toRealPath().toString();
               if (canonicalSafePath.length() > 0 && canonicalPath.length() >= canonicalSafePath.length() && canonicalPath.startsWith(canonicalSafePath)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   protected PathResource getFileResource(Path file, String path, Path symlinkBase, String normalizedFile) throws IOException {
      if (this.caseSensitive) {
         if (symlinkBase != null) {
            String relative = symlinkBase.relativize(file.normalize()).toString();
            String fileResolved = file.toRealPath().toString();
            String symlinkBaseResolved = symlinkBase.toRealPath().toString();
            if (!fileResolved.startsWith(symlinkBaseResolved)) {
               log.tracef((String)"Rejected path resource %s from path resource manager with base %s, as the case did not match actual case of %s", (Object)path, this.base, normalizedFile);
               return null;
            } else {
               String compare = fileResolved.substring(symlinkBaseResolved.length());
               if (compare.startsWith(this.fileSystem.getSeparator())) {
                  compare = compare.substring(this.fileSystem.getSeparator().length());
               }

               if (relative.startsWith(this.fileSystem.getSeparator())) {
                  relative = relative.substring(this.fileSystem.getSeparator().length());
               }

               if (relative.equals(compare)) {
                  log.tracef((String)"Found path resource %s from path resource manager with base %s", (Object)path, (Object)this.base);
                  return new PathResource(file, this, path, this.eTagFunction.generate(file));
               } else {
                  log.tracef((String)"Rejected path resource %s from path resource manager with base %s, as the case did not match actual case of %s", (Object)path, this.base, normalizedFile);
                  return null;
               }
            }
         } else if (this.isFileSameCase(file, normalizedFile)) {
            log.tracef((String)"Found path resource %s from path resource manager with base %s", (Object)path, (Object)this.base);
            return new PathResource(file, this, path, this.eTagFunction.generate(file));
         } else {
            log.tracef((String)"Rejected path resource %s from path resource manager with base %s, as the case did not match actual case of %s", (Object)path, this.base, normalizedFile);
            return null;
         }
      } else {
         log.tracef((String)"Found path resource %s from path resource manager with base %s", (Object)path, (Object)this.base);
         return new PathResource(file, this, path, this.eTagFunction.generate(file));
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   // $FF: synthetic method
   PathResourceManager(Builder x0, Object x1) {
      this(x0);
   }

   public static final class Builder {
      private Path base;
      private long transferMinSize;
      private boolean caseSensitive;
      private boolean followLinks;
      private boolean allowResourceChangeListeners;
      private ETagFunction eTagFunction;
      private String[] safePaths;

      private Builder() {
         this.transferMinSize = 1024L;
         this.caseSensitive = true;
         this.followLinks = false;
         this.allowResourceChangeListeners = PathResourceManager.DEFAULT_CHANGE_LISTENERS_ALLOWED;
         this.eTagFunction = PathResourceManager.NULL_ETAG_FUNCTION;
      }

      public Builder setBase(Path base) {
         this.base = base;
         return this;
      }

      public Builder setTransferMinSize(long transferMinSize) {
         this.transferMinSize = transferMinSize;
         return this;
      }

      public Builder setCaseSensitive(boolean caseSensitive) {
         this.caseSensitive = caseSensitive;
         return this;
      }

      public Builder setFollowLinks(boolean followLinks) {
         this.followLinks = followLinks;
         return this;
      }

      public Builder setAllowResourceChangeListeners(boolean allowResourceChangeListeners) {
         this.allowResourceChangeListeners = allowResourceChangeListeners;
         return this;
      }

      public Builder setETagFunction(ETagFunction eTagFunction) {
         this.eTagFunction = eTagFunction;
         return this;
      }

      public Builder setSafePaths(String[] safePaths) {
         this.safePaths = safePaths;
         return this;
      }

      public ResourceManager build() {
         return new PathResourceManager(this);
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }
   }

   public interface ETagFunction {
      ETag generate(Path var1);
   }

   private static class SymlinkResult {
      public final boolean requiresCheck;
      public final Path path;

      private SymlinkResult(boolean requiresCheck, Path path) {
         this.requiresCheck = requiresCheck;
         this.path = path;
      }

      // $FF: synthetic method
      SymlinkResult(boolean x0, Path x1, Object x2) {
         this(x0, x1);
      }
   }
}
