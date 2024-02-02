package org.h2.mvstore;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.h2.compress.CompressDeflate;
import org.h2.compress.CompressLZF;
import org.h2.compress.Compressor;
import org.h2.mvstore.cache.CacheLongKeyLIRS;
import org.h2.mvstore.type.StringDataType;
import org.h2.util.MathUtils;
import org.h2.util.Utils;

public class MVStore implements AutoCloseable {
   private static final String HDR_H = "H";
   private static final String HDR_BLOCK_SIZE = "blockSize";
   private static final String HDR_FORMAT = "format";
   private static final String HDR_CREATED = "created";
   private static final String HDR_FORMAT_READ = "formatRead";
   private static final String HDR_CHUNK = "chunk";
   private static final String HDR_BLOCK = "block";
   private static final String HDR_VERSION = "version";
   private static final String HDR_CLEAN = "clean";
   private static final String HDR_FLETCHER = "fletcher";
   public static final String META_ID_KEY = "meta.id";
   static final int BLOCK_SIZE = 4096;
   private static final int FORMAT_WRITE_MIN = 2;
   private static final int FORMAT_WRITE_MAX = 2;
   private static final int FORMAT_READ_MIN = 2;
   private static final int FORMAT_READ_MAX = 2;
   private static final int STATE_OPEN = 0;
   private static final int STATE_STOPPING = 1;
   private static final int STATE_CLOSING = 2;
   private static final int STATE_CLOSED = 3;
   private static final int PIPE_LENGTH = 1;
   private final ReentrantLock storeLock = new ReentrantLock(true);
   private final ReentrantLock serializationLock = new ReentrantLock(true);
   private final ReentrantLock saveChunkLock = new ReentrantLock(true);
   private final AtomicReference<BackgroundWriterThread> backgroundWriterThread = new AtomicReference();
   private ThreadPoolExecutor serializationExecutor;
   private ThreadPoolExecutor bufferSaveExecutor;
   private volatile boolean reuseSpace = true;
   private volatile int state;
   private final FileStore fileStore;
   private final boolean fileStoreIsProvided;
   private final int pageSplitSize;
   private final int keysPerPage;
   private final CacheLongKeyLIRS<Page<?, ?>> cache;
   private final CacheLongKeyLIRS<long[]> chunksToC;
   private volatile Chunk lastChunk;
   private final ConcurrentHashMap<Integer, Chunk> chunks = new ConcurrentHashMap();
   private final Queue<RemovedPageInfo> removedPages = new PriorityBlockingQueue();
   private final Deque<Chunk> deadChunks = new ArrayDeque();
   private long updateCounter = 0L;
   private long updateAttemptCounter = 0L;
   private final MVMap<String, String> layout;
   private final MVMap<String, String> meta;
   private final ConcurrentHashMap<Integer, MVMap<?, ?>> maps = new ConcurrentHashMap();
   private final HashMap<String, Object> storeHeader = new HashMap();
   private final Queue<WriteBuffer> writeBufferPool = new ArrayBlockingQueue(2);
   private final AtomicInteger lastMapId = new AtomicInteger();
   private int lastChunkId;
   private int versionsToKeep = 5;
   private final int compressionLevel;
   private Compressor compressorFast;
   private Compressor compressorHigh;
   private final boolean recoveryMode;
   public final Thread.UncaughtExceptionHandler backgroundExceptionHandler;
   private volatile long currentVersion;
   private final AtomicLong oldestVersionToKeep = new AtomicLong();
   private final Deque<TxCounter> versions = new LinkedList();
   private volatile TxCounter currentTxCounter;
   private int unsavedMemory;
   private final int autoCommitMemory;
   private volatile boolean saveNeeded;
   private long creationTime;
   private int retentionTime;
   private long lastCommitTime;
   private volatile long currentStoreVersion;
   private volatile boolean metaChanged;
   private int autoCommitDelay;
   private final int autoCompactFillRate;
   private long autoCompactLastFileOpCount;
   private volatile MVStoreException panicException;
   private long lastTimeAbsolute;
   private long leafCount;
   private long nonLeafCount;

   MVStore(Map<String, Object> var1) {
      this.currentTxCounter = new TxCounter(this.currentVersion);
      this.currentStoreVersion = -1L;
      this.recoveryMode = var1.containsKey("recoveryMode");
      this.compressionLevel = DataUtils.getConfigParam(var1, "compress", 0);
      String var2 = (String)var1.get("fileName");
      FileStore var3 = (FileStore)var1.get("fileStore");
      if (var3 == null) {
         this.fileStoreIsProvided = false;
         if (var2 != null) {
            var3 = new FileStore();
         }
      } else {
         if (var2 != null) {
            throw new IllegalArgumentException("fileName && fileStore");
         }

         this.fileStoreIsProvided = true;
      }

      this.fileStore = var3;
      int var4 = 48;
      CacheLongKeyLIRS.Config var5 = null;
      CacheLongKeyLIRS.Config var6 = null;
      int var7;
      if (this.fileStore != null) {
         var7 = DataUtils.getConfigParam(var1, "cacheSize", 16);
         if (var7 > 0) {
            var5 = new CacheLongKeyLIRS.Config();
            var5.maxMemory = (long)var7 * 1024L * 1024L;
            Object var8 = var1.get("cacheConcurrency");
            if (var8 != null) {
               var5.segmentCount = (Integer)var8;
            }
         }

         var6 = new CacheLongKeyLIRS.Config();
         var6.maxMemory = 1048576L;
         var4 = 16384;
      }

      if (var5 != null) {
         this.cache = new CacheLongKeyLIRS(var5);
      } else {
         this.cache = null;
      }

      this.chunksToC = var6 == null ? null : new CacheLongKeyLIRS(var6);
      var4 = DataUtils.getConfigParam(var1, "pageSplitSize", var4);
      if (this.cache != null && (long)var4 > this.cache.getMaxItemSize()) {
         var4 = (int)this.cache.getMaxItemSize();
      }

      this.pageSplitSize = var4;
      this.keysPerPage = DataUtils.getConfigParam(var1, "keysPerPage", 48);
      this.backgroundExceptionHandler = (Thread.UncaughtExceptionHandler)var1.get("backgroundExceptionHandler");
      this.layout = new MVMap(this, 0, StringDataType.INSTANCE, StringDataType.INSTANCE);
      if (this.fileStore != null) {
         this.retentionTime = this.fileStore.getDefaultRetentionTime();
         var7 = Math.max(1, Math.min(19, Utils.scaleForAvailableMemory(64))) * 1024;
         var7 = DataUtils.getConfigParam(var1, "autoCommitBufferSize", var7);
         this.autoCommitMemory = var7 * 1024;
         this.autoCompactFillRate = DataUtils.getConfigParam(var1, "autoCompactFillRate", 90);
         char[] var21 = (char[])((char[])var1.get("encryptionKey"));
         this.storeLock.lock();

         try {
            this.saveChunkLock.lock();

            try {
               if (!this.fileStoreIsProvided) {
                  boolean var9 = var1.containsKey("readOnly");
                  this.fileStore.open(var2, var9, var21);
               }

               if (this.fileStore.size() == 0L) {
                  this.creationTime = this.getTimeAbsolute();
                  this.storeHeader.put("H", 2);
                  this.storeHeader.put("blockSize", 4096);
                  this.storeHeader.put("format", 2);
                  this.storeHeader.put("created", this.creationTime);
                  this.setLastChunk((Chunk)null);
                  this.writeStoreHeader();
               } else {
                  this.readStoreHeader();
               }
            } finally {
               this.saveChunkLock.unlock();
            }
         } catch (MVStoreException var19) {
            this.panic(var19);
         } finally {
            if (var21 != null) {
               Arrays.fill(var21, '\u0000');
            }

            this.unlockAndCheckPanicCondition();
         }

         this.lastCommitTime = this.getTimeSinceCreation();
         this.meta = this.openMetaMap();
         this.scrubLayoutMap();
         this.scrubMetaMap();
         int var22 = DataUtils.getConfigParam(var1, "autoCommitDelay", 1000);
         this.setAutoCommitDelay(var22);
      } else {
         this.autoCommitMemory = 0;
         this.autoCompactFillRate = 0;
         this.meta = this.openMetaMap();
      }

      this.onVersionChange(this.currentVersion);
   }

   private MVMap<String, String> openMetaMap() {
      String var1 = (String)this.layout.get("meta.id");
      int var2;
      if (var1 == null) {
         var2 = this.lastMapId.incrementAndGet();
         this.layout.put("meta.id", Integer.toHexString(var2));
      } else {
         var2 = DataUtils.parseHexInt(var1);
      }

      MVMap var3 = new MVMap(this, var2, StringDataType.INSTANCE, StringDataType.INSTANCE);
      var3.setRootPos(this.getRootPos(var3.getId()), this.currentVersion - 1L);
      return var3;
   }

   private void scrubLayoutMap() {
      HashSet var1 = new HashSet();
      String[] var2 = new String[]{"name.", "map."};
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         Iterator var6 = this.layout.keyIterator(var5);

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            if (!var7.startsWith(var5)) {
               break;
            }

            this.meta.putIfAbsent(var7, this.layout.get(var7));
            this.markMetaChanged();
            var1.add(var7);
         }
      }

      Iterator var8 = this.layout.keyIterator("root.");

      String var9;
      while(var8.hasNext()) {
         var9 = (String)var8.next();
         if (!var9.startsWith("root.")) {
            break;
         }

         String var10 = var9.substring(var9.lastIndexOf(46) + 1);
         if (!this.meta.containsKey("map." + var10) && DataUtils.parseHexInt(var10) != this.meta.getId()) {
            var1.add(var9);
         }
      }

      var8 = var1.iterator();

      while(var8.hasNext()) {
         var9 = (String)var8.next();
         this.layout.remove(var9);
      }

   }

   private void scrubMetaMap() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.meta.keyIterator("name.");

      String var3;
      String var4;
      while(var2.hasNext()) {
         var3 = (String)var2.next();
         if (!var3.startsWith("name.")) {
            break;
         }

         var4 = var3.substring("name.".length());
         int var5 = DataUtils.parseHexInt((String)this.meta.get(var3));
         String var6 = this.getMapName(var5);
         if (!var4.equals(var6)) {
            var1.add(var3);
         }
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         var3 = (String)var2.next();
         this.meta.remove(var3);
         this.markMetaChanged();
      }

      var2 = this.meta.keyIterator("map.");

      while(var2.hasNext()) {
         var3 = (String)var2.next();
         if (!var3.startsWith("map.")) {
            break;
         }

         var4 = DataUtils.getMapName((String)this.meta.get(var3));
         String var7 = var3.substring("map.".length());
         int var8 = DataUtils.parseHexInt(var7);
         if (var8 > this.lastMapId.get()) {
            this.lastMapId.set(var8);
         }

         if (!var7.equals(this.meta.get("name." + var4))) {
            this.meta.put("name." + var4, var7);
            this.markMetaChanged();
         }
      }

   }

   private void unlockAndCheckPanicCondition() {
      this.storeLock.unlock();
      if (this.getPanicException() != null) {
         this.closeImmediately();
      }

   }

   private void panic(MVStoreException var1) {
      if (this.isOpen()) {
         this.handleException(var1);
         this.panicException = var1;
      }

      throw var1;
   }

   public MVStoreException getPanicException() {
      return this.panicException;
   }

   public static MVStore open(String var0) {
      HashMap var1 = new HashMap();
      var1.put("fileName", var0);
      return new MVStore(var1);
   }

   public <K, V> MVMap<K, V> openMap(String var1) {
      return this.openMap(var1, new MVMap.Builder());
   }

   public <M extends MVMap<K, V>, K, V> M openMap(String var1, MVMap.MapBuilder<M, K, V> var2) {
      int var3 = this.getMapId(var1);
      if (var3 >= 0) {
         MVMap var11 = this.getMap(var3);
         if (var11 == null) {
            var11 = this.openMap(var3, var2);
         }

         assert var2.getKeyType() == null || var11.getKeyType().getClass().equals(var2.getKeyType().getClass());

         assert var2.getValueType() == null || var11.getValueType().getClass().equals(var2.getValueType().getClass());

         return var11;
      } else {
         HashMap var4 = new HashMap();
         var3 = this.lastMapId.incrementAndGet();

         assert this.getMap(var3) == null;

         var4.put("id", var3);
         var4.put("createVersion", this.currentVersion);
         MVMap var5 = var2.create(this, var4);
         String var6 = Integer.toHexString(var3);
         this.meta.put(MVMap.getMapKey(var3), var5.asString(var1));
         String var7 = (String)this.meta.putIfAbsent("name." + var1, var6);
         if (var7 != null) {
            this.meta.remove(MVMap.getMapKey(var3));
            return this.openMap(var1, var2);
         } else {
            long var8 = this.currentVersion - 1L;
            var5.setRootPos(0L, var8);
            this.markMetaChanged();
            MVMap var10 = (MVMap)this.maps.putIfAbsent(var3, var5);
            if (var10 != null) {
               var5 = var10;
            }

            return var5;
         }
      }
   }

   public <M extends MVMap<K, V>, K, V> M openMap(int var1, MVMap.MapBuilder<M, K, V> var2) {
      while(true) {
         MVMap var3;
         if ((var3 = this.getMap(var1)) == null) {
            String var4 = (String)this.meta.get(MVMap.getMapKey(var1));
            DataUtils.checkArgument(var4 != null, "Missing map with id {0}", var1);
            HashMap var5 = new HashMap(DataUtils.parseMap(var4));
            var5.put("id", var1);
            var3 = var2.create(this, var5);
            long var6 = this.getRootPos(var1);
            long var8 = this.currentVersion - 1L;
            var3.setRootPos(var6, var8);
            if (this.maps.putIfAbsent(var1, var3) != null) {
               continue;
            }
         }

         return var3;
      }
   }

   public <K, V> MVMap<K, V> getMap(int var1) {
      this.checkOpen();
      MVMap var2 = (MVMap)this.maps.get(var1);
      return var2;
   }

   public Set<String> getMapNames() {
      HashSet var1 = new HashSet();
      this.checkOpen();
      Iterator var2 = this.meta.keyIterator("name.");

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (!var3.startsWith("name.")) {
            break;
         }

         String var4 = var3.substring("name.".length());
         var1.add(var4);
      }

      return var1;
   }

   public MVMap<String, String> getLayoutMap() {
      this.checkOpen();
      return this.layout;
   }

   public MVMap<String, String> getMetaMap() {
      this.checkOpen();
      return this.meta;
   }

   private MVMap<String, String> getLayoutMap(long var1) {
      Chunk var3 = this.getChunkForVersion(var1);
      DataUtils.checkArgument(var3 != null, "Unknown version {0}", var1);
      long var4 = var3.block;
      var3 = this.readChunkHeader(var4);
      MVMap var6 = this.layout.openReadOnly(var3.layoutRootPos, var1);
      return var6;
   }

   private Chunk getChunkForVersion(long var1) {
      Chunk var3 = null;
      Iterator var4 = this.chunks.values().iterator();

      while(true) {
         Chunk var5;
         do {
            do {
               if (!var4.hasNext()) {
                  return var3;
               }

               var5 = (Chunk)var4.next();
            } while(var5.version > var1);
         } while(var3 != null && var5.id <= var3.id);

         var3 = var5;
      }
   }

   public boolean hasMap(String var1) {
      return this.meta.containsKey("name." + var1);
   }

   public boolean hasData(String var1) {
      return this.hasMap(var1) && this.getRootPos(this.getMapId(var1)) != 0L;
   }

   private void markMetaChanged() {
      this.metaChanged = true;
   }

   private void readStoreHeader() {
      Chunk var1 = null;
      boolean var2 = true;
      boolean var3 = false;
      ByteBuffer var4 = this.fileStore.readFully(0L, 8192);
      byte[] var5 = new byte[4096];

      int var6;
      for(var6 = 0; var6 <= 4096; var6 += 4096) {
         var4.get(var5);

         try {
            HashMap var7 = DataUtils.parseChecksummedMap(var5);
            if (var7 == null) {
               var2 = false;
            } else {
               long var8 = DataUtils.readHexLong(var7, "version", 0L);
               var2 = var2 && (var1 == null || var8 == var1.version);
               if (var1 == null || var8 > var1.version) {
                  var3 = true;
                  this.storeHeader.putAll(var7);
                  this.creationTime = DataUtils.readHexLong(var7, "created", 0L);
                  int var10 = DataUtils.readHexInt(var7, "chunk", 0);
                  long var11 = DataUtils.readHexLong(var7, "block", 2L);
                  Chunk var13 = this.readChunkHeaderAndFooter(var11, var10);
                  if (var13 != null) {
                     var1 = var13;
                  }
               }
            }
         } catch (Exception var29) {
            var2 = false;
         }
      }

      if (!var3) {
         throw DataUtils.newMVStoreException(6, "Store header is corrupt: {0}", this.fileStore);
      } else {
         var6 = DataUtils.readHexInt(this.storeHeader, "blockSize", 4096);
         if (var6 != 4096) {
            throw DataUtils.newMVStoreException(5, "Block size {0} is currently not supported", var6);
         } else {
            long var30 = DataUtils.readHexLong(this.storeHeader, "format", 1L);
            if (!this.fileStore.isReadOnly()) {
               if (var30 > 2L) {
                  throw this.getUnsupportedWriteFormatException(var30, 2, "The write format {0} is larger than the supported format {1}");
               }

               if (var30 < 2L) {
                  throw this.getUnsupportedWriteFormatException(var30, 2, "The write format {0} is smaller than the supported format {1}");
               }
            }

            var30 = DataUtils.readHexLong(this.storeHeader, "formatRead", var30);
            if (var30 > 2L) {
               throw DataUtils.newMVStoreException(5, "The read format {0} is larger than the supported format {1}", var30, 2);
            } else if (var30 < 2L) {
               throw DataUtils.newMVStoreException(5, "The read format {0} is smaller than the supported format {1}", var30, 2);
            } else {
               var2 = var2 && var1 != null && !this.recoveryMode;
               if (var2) {
                  var2 = DataUtils.readHexInt(this.storeHeader, "clean", 0) != 0;
               }

               this.chunks.clear();
               long var9 = System.currentTimeMillis();
               int var31 = 1970 + (int)(var9 / 31557600000L);
               if (var31 < 2014) {
                  this.creationTime = var9 - (long)this.fileStore.getDefaultRetentionTime();
               } else if (var9 < this.creationTime) {
                  this.creationTime = var9;
                  this.storeHeader.put("created", this.creationTime);
               }

               long var12 = this.fileStore.size();
               long var14 = var12 / 4096L;
               Comparator var16 = (var0, var1x) -> {
                  int var2 = Long.compare(var1x.version, var0.version);
                  if (var2 == 0) {
                     var2 = Long.compare(var0.block, var1x.block);
                  }

                  return var2;
               };
               HashMap var17 = new HashMap();
               Chunk var19;
               if (!var2) {
                  Chunk var18 = this.discoverChunk(var14);
                  if (var18 != null) {
                     var14 = var18.block;
                     if (var1 == null || var18.version > var1.version) {
                        var1 = var18;
                     }
                  }

                  if (var1 != null) {
                     while(true) {
                        var17.put(var1.block, var1);
                        if (var1.next == 0L || var1.next >= var14) {
                           break;
                        }

                        var19 = this.readChunkHeaderAndFooter(var1.next, var1.id + 1);
                        if (var19 == null || var19.version <= var1.version) {
                           break;
                        }

                        var2 = false;
                        var1 = var19;
                     }
                  }
               }

               Chunk var21;
               if (var2) {
                  PriorityQueue var32 = new PriorityQueue(20, Collections.reverseOrder(var16));

                  try {
                     this.setLastChunk(var1);
                     Cursor var34 = this.layout.cursor("chunk.");

                     Chunk var20;
                     while(var34.hasNext() && ((String)var34.next()).startsWith("chunk.")) {
                        var20 = Chunk.fromString((String)var34.getValue());

                        assert var20.version <= this.currentVersion;

                        this.chunks.putIfAbsent(var20.id, var20);
                        var32.offer(var20);
                        if (var32.size() == 20) {
                           var32.poll();
                        }
                     }

                     while(var2 && (var20 = (Chunk)var32.poll()) != null) {
                        var21 = this.readChunkHeaderAndFooter(var20.block, var20.id);
                        var2 = var21 != null;
                        if (var2) {
                           var17.put(var21.block, var21);
                        }
                     }
                  } catch (MVStoreException var28) {
                     var2 = false;
                  }
               }

               int var22;
               if (!var2) {
                  boolean var33 = false;
                  if (!this.recoveryMode) {
                     Chunk[] var36 = (Chunk[])var17.values().toArray(new Chunk[0]);
                     Arrays.sort(var36, var16);
                     HashMap var38 = new HashMap();
                     Chunk[] var40 = var36;
                     var22 = var36.length;

                     for(int var23 = 0; var23 < var22; ++var23) {
                        Chunk var24 = var40[var23];
                        var38.put(var24.id, var24);
                     }

                     var33 = this.findLastChunkWithCompleteValidChunkSet(var36, var17, var38, false);
                  }

                  if (!var33) {
                     long var37 = var14;

                     while((var21 = this.discoverChunk(var37)) != null) {
                        var37 = var21.block;
                        var17.put(var37, var21);
                     }

                     Chunk[] var41 = (Chunk[])var17.values().toArray(new Chunk[0]);
                     Arrays.sort(var41, var16);
                     HashMap var42 = new HashMap();
                     Chunk[] var43 = var41;
                     int var25 = var41.length;

                     for(int var26 = 0; var26 < var25; ++var26) {
                        Chunk var27 = var43[var26];
                        var42.put(var27.id, var27);
                     }

                     if (!this.findLastChunkWithCompleteValidChunkSet(var41, var17, var42, true) && this.lastChunk != null) {
                        throw DataUtils.newMVStoreException(6, "File is corrupted - unable to recover a valid set of chunks");
                     }
                  }
               }

               this.fileStore.clear();
               Iterator var35 = this.chunks.values().iterator();

               while(var35.hasNext()) {
                  var19 = (Chunk)var35.next();
                  if (var19.isSaved()) {
                     long var39 = var19.block * 4096L;
                     var22 = var19.len * 4096;
                     this.fileStore.markUsed(var39, var22);
                  }

                  if (!var19.isLive()) {
                     this.deadChunks.offer(var19);
                  }
               }

               assert this.validateFileLength("on open");

            }
         }
      }
   }

   private MVStoreException getUnsupportedWriteFormatException(long var1, int var3, String var4) {
      var1 = DataUtils.readHexLong(this.storeHeader, "formatRead", var1);
      if (var1 >= 2L && var1 <= 2L) {
         var4 = var4 + ", and the file was not opened in read-only mode";
      }

      return DataUtils.newMVStoreException(5, var4, var1, var3);
   }

   private boolean findLastChunkWithCompleteValidChunkSet(Chunk[] var1, Map<Long, Chunk> var2, Map<Integer, Chunk> var3, boolean var4) {
      Chunk[] var5 = var1;
      int var6 = var1.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Chunk var8 = var5[var7];
         boolean var9 = true;

         try {
            this.setLastChunk(var8);
            Cursor var10 = this.layout.cursor("chunk.");

            while(var10.hasNext() && ((String)var10.next()).startsWith("chunk.")) {
               Chunk var11 = Chunk.fromString((String)var10.getValue());

               assert var11.version <= this.currentVersion;

               Chunk var12 = (Chunk)this.chunks.putIfAbsent(var11.id, var11);
               if (var12 != null) {
                  var11 = var12;
               }

               assert this.chunks.get(var11.id) == var11;

               if ((var12 = (Chunk)var2.get(var11.block)) == null || var12.id != var11.id) {
                  if ((var12 = (Chunk)var3.get(var11.id)) != null) {
                     var11.block = var12.block;
                  } else if (var11.isLive() && (var4 || this.readChunkHeaderAndFooter(var11.block, var11.id) == null)) {
                     var9 = false;
                     break;
                  }
               }

               if (!var11.isLive()) {
                  var11.block = Long.MAX_VALUE;
                  var11.len = Integer.MAX_VALUE;
                  if (var11.unused == 0L) {
                     var11.unused = this.creationTime;
                  }

                  if (var11.unusedAtVersion == 0L) {
                     var11.unusedAtVersion = -1L;
                  }
               }
            }
         } catch (Exception var13) {
            var9 = false;
         }

         if (var9) {
            return true;
         }
      }

      return false;
   }

   private void setLastChunk(Chunk var1) {
      this.chunks.clear();
      this.lastChunk = var1;
      this.lastChunkId = 0;
      this.currentVersion = this.lastChunkVersion();
      long var2 = 0L;
      int var4 = 0;
      if (var1 != null) {
         this.lastChunkId = var1.id;
         this.currentVersion = var1.version;
         var2 = var1.layoutRootPos;
         var4 = var1.mapId;
         this.chunks.put(var1.id, var1);
      }

      this.lastMapId.set(var4);
      this.layout.setRootPos(var2, this.currentVersion - 1L);
   }

   private Chunk discoverChunk(long var1) {
      long var3 = Long.MAX_VALUE;
      Chunk var5 = null;

      while(var1 != var3) {
         if (var1 == 2L) {
            return null;
         }

         Chunk var6 = this.readChunkFooter(var1);
         if (var6 != null) {
            var3 = Long.MAX_VALUE;
            var6 = this.readChunkHeaderOptionally(var6.block, var6.id);
            if (var6 != null) {
               var5 = var6;
               var3 = var6.block;
            }
         }

         if (--var1 > var3 && this.readChunkHeaderOptionally(var1) != null) {
            var3 = Long.MAX_VALUE;
         }
      }

      return var5;
   }

   private Chunk readChunkHeaderAndFooter(long var1, int var3) {
      Chunk var4 = this.readChunkHeaderOptionally(var1, var3);
      if (var4 != null) {
         Chunk var5 = this.readChunkFooter(var1 + (long)var4.len);
         if (var5 == null || var5.id != var3 || var5.block != var4.block) {
            return null;
         }
      }

      return var4;
   }

   private Chunk readChunkFooter(long var1) {
      try {
         long var3 = var1 * 4096L - 128L;
         if (var3 < 0L) {
            return null;
         }

         ByteBuffer var5 = this.fileStore.readFully(var3, 128);
         byte[] var6 = new byte[128];
         var5.get(var6);
         HashMap var7 = DataUtils.parseChecksummedMap(var6);
         if (var7 != null) {
            return new Chunk(var7);
         }
      } catch (Exception var8) {
      }

      return null;
   }

   private void writeStoreHeader() {
      Chunk var1 = this.lastChunk;
      if (var1 != null) {
         this.storeHeader.put("block", var1.block);
         this.storeHeader.put("chunk", var1.id);
         this.storeHeader.put("version", var1.version);
      }

      StringBuilder var2 = new StringBuilder(112);
      DataUtils.appendMap(var2, this.storeHeader);
      byte[] var3 = var2.toString().getBytes(StandardCharsets.ISO_8859_1);
      int var4 = DataUtils.getFletcher32(var3, 0, var3.length);
      DataUtils.appendMap(var2, "fletcher", var4);
      var2.append('\n');
      var3 = var2.toString().getBytes(StandardCharsets.ISO_8859_1);
      ByteBuffer var5 = ByteBuffer.allocate(8192);
      var5.put(var3);
      var5.position(4096);
      var5.put(var3);
      var5.rewind();
      this.write(0L, var5);
   }

   private void write(long var1, ByteBuffer var3) {
      try {
         this.fileStore.writeFully(var1, var3);
      } catch (MVStoreException var5) {
         this.panic(var5);
      }

   }

   public void close() {
      this.closeStore(true, 0);
   }

   public void close(int var1) {
      this.closeStore(true, var1);
   }

   public void closeImmediately() {
      try {
         this.closeStore(false, 0);
      } catch (Throwable var2) {
         this.handleException(var2);
      }

   }

   private void closeStore(boolean var1, int var2) {
      while(!this.isClosed()) {
         this.stopBackgroundThread(var1);
         this.storeLock.lock();

         try {
            if (this.state == 0) {
               this.state = 1;

               try {
                  try {
                     Iterator var3;
                     MVMap var4;
                     if (var1 && this.fileStore != null && !this.fileStore.isReadOnly()) {
                        var3 = this.maps.values().iterator();

                        while(var3.hasNext()) {
                           var4 = (MVMap)var3.next();
                           if (var4.isClosed()) {
                              this.deregisterMapRoot(var4.getId());
                           }
                        }

                        this.setRetentionTime(0);
                        this.commit();
                        if (var2 > 0) {
                           this.compactFile(var2);
                        } else if (var2 < 0) {
                           this.doMaintenance(this.autoCompactFillRate);
                        }

                        this.saveChunkLock.lock();

                        try {
                           this.shrinkFileIfPossible(0);
                           this.storeHeader.put("clean", 1);
                           this.writeStoreHeader();
                           this.sync();

                           assert this.validateFileLength("on close");
                        } finally {
                           this.saveChunkLock.unlock();
                        }
                     }

                     this.state = 2;
                     this.clearCaches();
                     var3 = (new ArrayList(this.maps.values())).iterator();

                     while(var3.hasNext()) {
                        var4 = (MVMap)var3.next();
                        var4.close();
                     }

                     this.chunks.clear();
                     this.maps.clear();
                  } finally {
                     if (this.fileStore != null && !this.fileStoreIsProvided) {
                        this.fileStore.close();
                     }

                  }
               } finally {
                  this.state = 3;
               }
            }
         } finally {
            this.storeLock.unlock();
         }
      }

   }

   private static void shutdownExecutor(ThreadPoolExecutor var0) {
      if (var0 != null) {
         var0.shutdown();

         try {
            if (var0.awaitTermination(1000L, TimeUnit.MILLISECONDS)) {
               return;
            }
         } catch (InterruptedException var2) {
         }

         var0.shutdownNow();
      }

   }

   private Chunk getChunk(long var1) {
      int var3 = DataUtils.getPageChunkId(var1);
      Chunk var4 = (Chunk)this.chunks.get(var3);
      if (var4 == null) {
         this.checkOpen();
         String var5 = (String)this.layout.get(Chunk.getMetaKey(var3));
         if (var5 == null) {
            throw DataUtils.newMVStoreException(9, "Chunk {0} not found", var3);
         }

         var4 = Chunk.fromString(var5);
         if (!var4.isSaved()) {
            throw DataUtils.newMVStoreException(6, "Chunk {0} is invalid", var3);
         }

         this.chunks.put(var4.id, var4);
      }

      return var4;
   }

   private void setWriteVersion(long var1) {
      Iterator var3 = this.maps.values().iterator();

      while(var3.hasNext()) {
         MVMap var4 = (MVMap)var3.next();

         assert var4 != this.layout && var4 != this.meta;

         if (var4.setWriteVersion(var1) == null) {
            var3.remove();
         }
      }

      this.meta.setWriteVersion(var1);
      this.layout.setWriteVersion(var1);
      this.onVersionChange(var1);
   }

   public long tryCommit() {
      return this.tryCommit((var0) -> {
         return true;
      });
   }

   private long tryCommit(Predicate<MVStore> var1) {
      if ((!this.storeLock.isHeldByCurrentThread() || this.currentStoreVersion < 0L) && this.storeLock.tryLock()) {
         try {
            if (var1.test(this)) {
               this.store(false);
            }
         } finally {
            this.unlockAndCheckPanicCondition();
         }
      }

      return this.currentVersion;
   }

   public long commit() {
      return this.commit((var0) -> {
         return true;
      });
   }

   private long commit(Predicate<MVStore> var1) {
      if (!this.storeLock.isHeldByCurrentThread() || this.currentStoreVersion < 0L) {
         this.storeLock.lock();

         try {
            if (var1.test(this)) {
               this.store(true);
            }
         } finally {
            this.unlockAndCheckPanicCondition();
         }
      }

      return this.currentVersion;
   }

   private void store(boolean var1) {
      assert this.storeLock.isHeldByCurrentThread();

      assert !this.saveChunkLock.isHeldByCurrentThread();

      if (this.isOpenOrStopping() && this.hasUnsavedChanges()) {
         this.dropUnusedChunks();

         try {
            this.currentStoreVersion = this.currentVersion;
            if (this.fileStore == null) {
               ++this.currentVersion;
               this.setWriteVersion(this.currentVersion);
               this.metaChanged = false;
            } else {
               if (this.fileStore.isReadOnly()) {
                  throw DataUtils.newMVStoreException(2, "This store is read-only");
               }

               this.storeNow(var1, 0L, () -> {
                  return this.reuseSpace ? 0L : this.getAfterLastBlock();
               });
            }
         } finally {
            this.currentStoreVersion = -1L;
         }
      }

   }

   private void storeNow(boolean var1, long var2, Supplier<Long> var4) {
      try {
         this.lastCommitTime = this.getTimeSinceCreation();
         int var5 = this.unsavedMemory;
         long var6 = ++this.currentVersion;
         ArrayList var8 = this.collectChangedMapRoots(var6);

         assert this.storeLock.isHeldByCurrentThread();

         submitOrRun(this.serializationExecutor, () -> {
            this.serializeAndStore(var1, var2, var4, var8, this.lastCommitTime, var6);
         }, var1);
         this.saveNeeded = false;
         this.unsavedMemory = Math.max(0, this.unsavedMemory - var5);
      } catch (MVStoreException var9) {
         this.panic(var9);
      } catch (Throwable var10) {
         this.panic(DataUtils.newMVStoreException(3, "{0}", var10.toString(), var10));
      }

   }

   private static void submitOrRun(ThreadPoolExecutor var0, Runnable var1, boolean var2) throws ExecutionException {
      if (var0 != null) {
         try {
            Future var3 = var0.submit(var1);
            if (var2 || var0.getQueue().size() > 1) {
               try {
                  var3.get();
               } catch (InterruptedException var5) {
               }
            }

            return;
         } catch (RejectedExecutionException var6) {
            assert var0.isShutdown();

            shutdownExecutor(var0);
         }
      }

      var1.run();
   }

   private ArrayList<Page<?, ?>> collectChangedMapRoots(long var1) {
      long var3 = var1 - 2L;
      ArrayList var5 = new ArrayList();
      Iterator var6 = this.maps.values().iterator();

      while(true) {
         while(var6.hasNext()) {
            MVMap var7 = (MVMap)var6.next();
            RootReference var8 = var7.setWriteVersion(var1);
            if (var8 == null) {
               var6.remove();
            } else if (var7.getCreateVersion() < var1 && !var7.isVolatile() && var7.hasChangesSince(var3)) {
               assert var8.version <= var1 : var8.version + " > " + var1;

               Page var9 = var8.root;
               if (!var9.isSaved() || var9.isLeaf()) {
                  var5.add(var9);
               }
            }
         }

         RootReference var10 = this.meta.setWriteVersion(var1);
         if (this.meta.hasChangesSince(var3) || this.metaChanged) {
            assert var10 != null && var10.version <= var1 : var10 == null ? "null" : var10.version + " > " + var1;

            Page var11 = var10.root;
            if (!var11.isSaved() || var11.isLeaf()) {
               var5.add(var11);
            }
         }

         return var5;
      }
   }

   private void serializeAndStore(boolean var1, long var2, Supplier<Long> var4, ArrayList<Page<?, ?>> var5, long var6, long var8) {
      this.serializationLock.lock();

      try {
         Chunk var10 = this.createChunk(var6, var8);
         this.chunks.put(var10.id, var10);
         WriteBuffer var11 = this.getWriteBuffer();
         this.serializeToBuffer(var11, var5, var10, var2, var4);
         submitOrRun(this.bufferSaveExecutor, () -> {
            this.storeBuffer(var10, var11, var5);
         }, var1);
      } catch (MVStoreException var16) {
         this.panic(var16);
      } catch (Throwable var17) {
         this.panic(DataUtils.newMVStoreException(3, "{0}", var17.toString(), var17));
      } finally {
         this.serializationLock.unlock();
      }

   }

   private Chunk createChunk(long var1, long var3) {
      int var5 = this.lastChunkId;
      if (var5 != 0) {
         var5 &= 67108863;
         Chunk var6 = (Chunk)this.chunks.get(var5);

         assert var6 != null;

         assert var6.isSaved();

         assert var6.version + 1L == var3 : var6.version + " " + var3;

         this.layout.put(Chunk.getMetaKey(var5), var6.asString());
         var1 = Math.max(var6.time, var1);
      }

      while(true) {
         int var9 = ++this.lastChunkId & 67108863;
         Chunk var7 = (Chunk)this.chunks.get(var9);
         if (var7 == null) {
            var7 = new Chunk(var9);
            var7.pageCount = 0;
            var7.pageCountLive = 0;
            var7.maxLen = 0L;
            var7.maxLenLive = 0L;
            var7.layoutRootPos = Long.MAX_VALUE;
            var7.block = Long.MAX_VALUE;
            var7.len = Integer.MAX_VALUE;
            var7.time = var1;
            var7.version = var3;
            var7.next = Long.MAX_VALUE;
            var7.occupancy = new BitSet();
            return var7;
         }

         if (!var7.isSaved()) {
            MVStoreException var8 = DataUtils.newMVStoreException(3, "Last block {0} not stored, possibly due to out-of-memory", var7);
            this.panic(var8);
         }
      }
   }

   private void serializeToBuffer(WriteBuffer var1, ArrayList<Page<?, ?>> var2, Chunk var3, long var4, Supplier<Long> var6) {
      var3.writeChunkHeader(var1, 0);
      int var7 = var1.position() + 44;
      var1.position(var7);
      long var8 = var3.version;
      ArrayList var10 = new ArrayList();
      Iterator var11 = var2.iterator();

      Page var12;
      while(var11.hasNext()) {
         var12 = (Page)var11.next();
         String var13 = MVMap.getMapRootKey(var12.getMapId());
         if (var12.getTotalCount() == 0L) {
            this.layout.remove(var13);
         } else {
            var12.writeUnsavedRecursive(var3, var1, var10);
            long var14 = var12.getPos();
            this.layout.put(var13, Long.toHexString(var14));
         }
      }

      this.acceptChunkOccupancyChanges(var3.time, var8);
      RootReference var23 = this.layout.setWriteVersion(var8);

      assert var23 != null;

      assert var23.version == var8 : var23.version + " != " + var8;

      this.metaChanged = false;
      this.acceptChunkOccupancyChanges(var3.time, var8);
      this.onVersionChange(var8);
      var12 = var23.root;
      var12.writeUnsavedRecursive(var3, var1, var10);
      var3.layoutRootPos = var12.getPos();
      var2.add(var12);
      var3.mapId = this.lastMapId.get();
      var3.tocPos = var1.position();
      long[] var24 = new long[var10.size()];
      int var25 = 0;
      Iterator var15 = var10.iterator();

      while(var15.hasNext()) {
         long var16 = (Long)var15.next();
         var24[var25++] = var16;
         var1.putLong(var16);
         if (DataUtils.isLeafPosition(var16)) {
            ++this.leafCount;
         } else {
            ++this.nonLeafCount;
         }
      }

      this.chunksToC.put((long)var3.id, var24);
      int var26 = var1.position();
      int var27 = MathUtils.roundUpInt(var26 + 128, 4096);
      var1.limit(var27);
      this.saveChunkLock.lock();

      try {
         Long var17 = (Long)var6.get();
         long var18 = this.fileStore.allocate(var1.limit(), var4, var17);
         var3.len = var1.limit() / 4096;
         var3.block = var18 / 4096L;

         assert this.validateFileLength(var3.asString());

         if (var4 <= 0L && var17 != var4) {
            var3.next = 0L;
         } else {
            var3.next = this.fileStore.predictAllocation(var3.len, 0L, 0L);
         }

         assert var3.pageCountLive == var3.pageCount : var3;

         assert var3.occupancy.cardinality() == 0 : var3;

         var1.position(0);

         assert var3.pageCountLive == var3.pageCount : var3;

         assert var3.occupancy.cardinality() == 0 : var3;

         var3.writeChunkHeader(var1, var7);
         var1.position(var1.limit() - 128);
         var1.put(var3.getFooterBytes());
      } finally {
         this.saveChunkLock.unlock();
      }

   }

   private void storeBuffer(Chunk var1, WriteBuffer var2, ArrayList<Page<?, ?>> var3) {
      this.saveChunkLock.lock();

      try {
         var2.position(0);
         long var4 = var1.block * 4096L;
         this.write(var4, var2.getBuffer());
         this.releaseWriteBuffer(var2);
         boolean var6 = var4 + (long)var2.limit() >= this.fileStore.size();
         boolean var7 = this.isWriteStoreHeader(var1, var6);
         this.lastChunk = var1;
         if (var7) {
            this.writeStoreHeader();
         }

         if (!var6) {
            this.shrinkFileIfPossible(1);
         }
      } catch (MVStoreException var12) {
         this.panic(var12);
      } catch (Throwable var13) {
         this.panic(DataUtils.newMVStoreException(3, "{0}", var13.toString(), var13));
      } finally {
         this.saveChunkLock.unlock();
      }

      Iterator var15 = var3.iterator();

      while(var15.hasNext()) {
         Page var5 = (Page)var15.next();
         var5.releaseSavedPages();
      }

   }

   private boolean isWriteStoreHeader(Chunk var1, boolean var2) {
      boolean var3 = false;
      if (!var2) {
         Chunk var4 = this.lastChunk;
         if (var4 == null) {
            var3 = true;
         } else if (var4.next != var1.block) {
            var3 = true;
         } else {
            long var5 = DataUtils.readHexLong(this.storeHeader, "version", 0L);
            if (var4.version - var5 > 20L) {
               var3 = true;
            } else {
               for(int var7 = DataUtils.readHexInt(this.storeHeader, "chunk", 0); !var3 && var7 <= var4.id; ++var7) {
                  var3 = !this.chunks.containsKey(var7);
               }
            }
         }
      }

      if (this.storeHeader.remove("clean") != null) {
         var3 = true;
      }

      return var3;
   }

   private WriteBuffer getWriteBuffer() {
      WriteBuffer var1 = (WriteBuffer)this.writeBufferPool.poll();
      if (var1 != null) {
         var1.clear();
      } else {
         var1 = new WriteBuffer();
      }

      return var1;
   }

   private void releaseWriteBuffer(WriteBuffer var1) {
      if (var1.capacity() <= 4194304) {
         this.writeBufferPool.offer(var1);
      }

   }

   private static boolean canOverwriteChunk(Chunk var0, long var1) {
      return !var0.isLive() && var0.unusedAtVersion < var1;
   }

   private boolean isSeasonedChunk(Chunk var1, long var2) {
      return this.retentionTime < 0 || var1.time + (long)this.retentionTime <= var2;
   }

   private long getTimeSinceCreation() {
      return Math.max(0L, this.getTimeAbsolute() - this.creationTime);
   }

   private long getTimeAbsolute() {
      long var1 = System.currentTimeMillis();
      if (this.lastTimeAbsolute != 0L && var1 < this.lastTimeAbsolute) {
         var1 = this.lastTimeAbsolute;
      } else {
         this.lastTimeAbsolute = var1;
      }

      return var1;
   }

   private void acceptChunkOccupancyChanges(long var1, long var3) {
      assert this.serializationLock.isHeldByCurrentThread();

      if (this.lastChunk != null) {
         HashSet var5 = new HashSet();

         while(true) {
            RemovedPageInfo var6;
            Chunk var8;
            while((var6 = (RemovedPageInfo)this.removedPages.peek()) == null || var6.version >= var3) {
               if (var5.isEmpty()) {
                  return;
               }

               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  var8 = (Chunk)var7.next();
                  int var9 = var8.id;
                  this.layout.put(Chunk.getMetaKey(var9), var8.asString());
               }

               var5.clear();
            }

            var6 = (RemovedPageInfo)this.removedPages.poll();

            assert var6 != null;

            assert var6.version < var3 : var6 + " < " + var3;

            int var10 = var6.getPageChunkId();
            var8 = (Chunk)this.chunks.get(var10);

            assert !this.isOpen() || var8 != null : var10;

            if (var8 != null) {
               var5.add(var8);
               if (var8.accountForRemovedPage(var6.getPageNo(), var6.getPageLength(), var6.isPinned(), var1, var6.version)) {
                  this.deadChunks.offer(var8);
               }
            }
         }
      }
   }

   private void shrinkFileIfPossible(int var1) {
      assert this.saveChunkLock.isHeldByCurrentThread();

      if (!this.fileStore.isReadOnly()) {
         long var2 = this.getFileLengthInUse();
         long var4 = this.fileStore.size();
         if (var2 < var4) {
            if (var1 <= 0 || var4 - var2 >= 4096L) {
               int var6 = (int)(100L - var2 * 100L / var4);
               if (var6 >= var1) {
                  if (this.isOpenOrStopping()) {
                     this.sync();
                  }

                  this.fileStore.truncate(var2);
               }
            }
         }
      }
   }

   private long getFileLengthInUse() {
      assert this.saveChunkLock.isHeldByCurrentThread();

      long var1 = this.fileStore.getFileLengthInUse();

      assert var1 == this.measureFileLengthInUse() : var1 + " != " + this.measureFileLengthInUse();

      return var1;
   }

   private long getAfterLastBlock() {
      assert this.saveChunkLock.isHeldByCurrentThread();

      return this.fileStore.getAfterLastBlock();
   }

   private long measureFileLengthInUse() {
      assert this.saveChunkLock.isHeldByCurrentThread();

      long var1 = 2L;
      Iterator var3 = this.chunks.values().iterator();

      while(var3.hasNext()) {
         Chunk var4 = (Chunk)var3.next();
         if (var4.isSaved()) {
            var1 = Math.max(var1, var4.block + (long)var4.len);
         }
      }

      return var1 * 4096L;
   }

   public boolean hasUnsavedChanges() {
      if (this.metaChanged) {
         return true;
      } else {
         long var1 = this.currentVersion - 1L;
         Iterator var3 = this.maps.values().iterator();

         while(var3.hasNext()) {
            MVMap var4 = (MVMap)var3.next();
            if (!var4.isClosed() && var4.hasChangesSince(var1)) {
               return true;
            }
         }

         return this.layout.hasChangesSince(var1) && var1 > -1L;
      }
   }

   private Chunk readChunkHeader(long var1) {
      long var3 = var1 * 4096L;
      ByteBuffer var5 = this.fileStore.readFully(var3, 1024);
      return Chunk.readChunkHeader(var5, var3);
   }

   private Chunk readChunkHeaderOptionally(long var1) {
      try {
         Chunk var3 = this.readChunkHeader(var1);
         return var3.block != var1 ? null : var3;
      } catch (Exception var4) {
         return null;
      }
   }

   private Chunk readChunkHeaderOptionally(long var1, int var3) {
      Chunk var4 = this.readChunkHeaderOptionally(var1);
      return var4 != null && var4.id == var3 ? var4 : null;
   }

   public void compactMoveChunks() {
      this.compactMoveChunks(100, Long.MAX_VALUE);
   }

   boolean compactMoveChunks(int var1, long var2) {
      boolean var4 = false;
      this.storeLock.lock();

      try {
         this.checkOpen();
         submitOrRun(this.serializationExecutor, () -> {
         }, true);
         this.serializationLock.lock();

         try {
            submitOrRun(this.bufferSaveExecutor, () -> {
            }, true);
            this.saveChunkLock.lock();

            try {
               if (this.lastChunk != null && this.reuseSpace && this.getFillRate() <= var1) {
                  var4 = this.compactMoveChunks(var2);
               }
            } finally {
               this.saveChunkLock.unlock();
            }
         } finally {
            this.serializationLock.unlock();
         }
      } catch (MVStoreException var25) {
         this.panic(var25);
      } catch (Throwable var26) {
         this.panic(DataUtils.newMVStoreException(3, "{0}", var26.toString(), var26));
      } finally {
         this.unlockAndCheckPanicCondition();
      }

      return var4;
   }

   private boolean compactMoveChunks(long var1) {
      assert this.storeLock.isHeldByCurrentThread();

      this.dropUnusedChunks();
      long var3 = this.fileStore.getFirstFree() / 4096L;
      Iterable var5 = this.findChunksToMove(var3, var1);
      if (var5 == null) {
         return false;
      } else {
         this.compactMoveChunks(var5);
         return true;
      }
   }

   private Iterable<Chunk> findChunksToMove(long var1, long var3) {
      long var5 = var3 / 4096L;
      ArrayList var7 = null;
      if (var5 > 0L) {
         PriorityQueue var8 = new PriorityQueue(this.chunks.size() / 2 + 1, (var0, var1x) -> {
            int var2 = Integer.compare(var1x.collectPriority, var0.collectPriority);
            return var2 != 0 ? var2 : Long.signum(var1x.block - var0.block);
         });
         long var9 = 0L;
         Iterator var11 = this.chunks.values().iterator();

         while(true) {
            Chunk var12;
            do {
               do {
                  if (!var11.hasNext()) {
                     if (!var8.isEmpty()) {
                        ArrayList var14 = new ArrayList(var8);
                        var14.sort(Chunk.PositionComparator.INSTANCE);
                        var7 = var14;
                     }

                     return var7;
                  }

                  var12 = (Chunk)var11.next();
               } while(!var12.isSaved());
            } while(var12.block <= var1);

            var12.collectPriority = this.getMovePriority(var12);
            var8.offer(var12);

            Chunk var13;
            for(var9 += (long)var12.len; var9 > var5; var9 -= (long)var13.len) {
               var13 = (Chunk)var8.poll();
               if (var13 == null) {
                  break;
               }
            }
         }
      } else {
         return var7;
      }
   }

   private int getMovePriority(Chunk var1) {
      return this.fileStore.getMovePriority((int)var1.block);
   }

   private void compactMoveChunks(Iterable<Chunk> var1) {
      assert this.storeLock.isHeldByCurrentThread();

      assert this.serializationLock.isHeldByCurrentThread();

      assert this.saveChunkLock.isHeldByCurrentThread();

      if (var1 != null) {
         this.writeStoreHeader();
         this.sync();
         Iterator var2 = var1.iterator();

         assert var2.hasNext();

         long var3 = ((Chunk)var2.next()).block;
         long var5 = this.getAfterLastBlock();
         Iterator var7 = var1.iterator();

         while(var7.hasNext()) {
            Chunk var8 = (Chunk)var7.next();
            this.moveChunk(var8, var3, var5);
         }

         this.store(var3, var5);
         this.sync();
         Chunk var15 = this.lastChunk;

         assert var15 != null;

         long var16 = this.getAfterLastBlock();
         boolean var10 = var15.block < var3;
         boolean var11 = !var10;
         Iterator var12 = var1.iterator();

         while(var12.hasNext()) {
            Chunk var13 = (Chunk)var12.next();
            if (var13.block >= var5 && this.moveChunk(var13, var5, var16)) {
               assert var13.block < var5;

               var11 = true;
            }
         }

         assert var16 >= this.getAfterLastBlock();

         if (var11) {
            boolean var17 = this.moveChunkInside(var15, var5);
            this.store(var5, var16);
            this.sync();
            long var18 = !var17 && !var10 ? var15.block : var16;
            var17 = !var17 && this.moveChunkInside(var15, var18);
            if (this.moveChunkInside(this.lastChunk, var18) || var17) {
               this.store(var18, -1L);
            }
         }

         this.shrinkFileIfPossible(0);
         this.sync();
      }

   }

   private void store(long var1, long var3) {
      this.saveChunkLock.unlock();

      try {
         this.serializationLock.unlock();

         try {
            this.storeNow(true, var1, () -> {
               return var3;
            });
         } finally {
            this.serializationLock.lock();
         }
      } finally {
         this.saveChunkLock.lock();
      }

   }

   private boolean moveChunkInside(Chunk var1, long var2) {
      boolean var4 = var1.block >= var2 && this.fileStore.predictAllocation(var1.len, var2, -1L) < var2 && this.moveChunk(var1, var2, -1L);

      assert !var4 || var1.block + (long)var1.len <= var2;

      return var4;
   }

   private boolean moveChunk(Chunk var1, long var2, long var4) {
      if (!this.chunks.containsKey(var1.id)) {
         return false;
      } else {
         long var6 = var1.block * 4096L;
         int var8 = var1.len * 4096;
         WriteBuffer var11 = this.getWriteBuffer();

         long var9;
         try {
            var11.limit(var8);
            ByteBuffer var12 = this.fileStore.readFully(var6, var8);
            Chunk var13 = Chunk.readChunkHeader(var12, var6);
            int var14 = var12.position();
            var11.position(var14);
            var11.put(var12);
            long var15 = this.fileStore.allocate(var8, var2, var4);
            var9 = var15 / 4096L;

            assert var4 > 0L || var9 <= var1.block : var9 + " " + var1;

            var11.position(0);
            var13.block = var9;
            var13.next = 0L;
            var13.writeChunkHeader(var11, var14);
            var11.position(var8 - 128);
            var11.put(var13.getFooterBytes());
            var11.position(0);
            this.write(var15, var11.getBuffer());
         } finally {
            this.releaseWriteBuffer(var11);
         }

         this.fileStore.free(var6, var8);
         var1.block = var9;
         var1.next = 0L;
         this.layout.put(Chunk.getMetaKey(var1.id), var1.asString());
         return true;
      }
   }

   public void sync() {
      this.checkOpen();
      FileStore var1 = this.fileStore;
      if (var1 != null) {
         var1.sync();
      }

   }

   public void compactFile(int var1) {
      this.setRetentionTime(0);
      long var2 = System.nanoTime() + (long)var1 * 1000000L;

      while(this.compact(95, 16777216)) {
         this.sync();
         this.compactMoveChunks(95, 16777216L);
         if (System.nanoTime() - var2 > 0L) {
            break;
         }
      }

   }

   public boolean compact(int var1, int var2) {
      if (this.reuseSpace && this.lastChunk != null) {
         this.checkOpen();
         if (var1 > 0 && this.getChunksFillRate() < var1) {
            try {
               if (this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
                  boolean var3;
                  try {
                     var3 = this.rewriteChunks(var2, 100);
                  } finally {
                     this.storeLock.unlock();
                  }

                  return var3;
               }
            } catch (InterruptedException var8) {
               throw new RuntimeException(var8);
            }
         }
      }

      return false;
   }

   private boolean rewriteChunks(int var1, int var2) {
      this.serializationLock.lock();

      try {
         TxCounter var3 = this.registerVersionUsage();

         try {
            this.acceptChunkOccupancyChanges(this.getTimeSinceCreation(), this.currentVersion);
            Iterable var4 = this.findOldChunks(var1, var2);
            if (var4 != null) {
               HashSet var5 = createIdSet(var4);
               boolean var6 = !var5.isEmpty() && this.compactRewrite(var5) > 0;
               return var6;
            }
         } finally {
            this.deregisterVersionUsage(var3);
         }

         boolean var15 = false;
         return var15;
      } finally {
         this.serializationLock.unlock();
      }
   }

   public int getChunksFillRate() {
      return this.getChunksFillRate(true);
   }

   public int getRewritableChunksFillRate() {
      return this.getChunksFillRate(false);
   }

   private int getChunksFillRate(boolean var1) {
      long var2 = 1L;
      long var4 = 1L;
      long var6 = this.getTimeSinceCreation();
      Iterator var8 = this.chunks.values().iterator();

      while(true) {
         Chunk var9;
         do {
            if (!var8.hasNext()) {
               int var10 = (int)(100L * var4 / var2);
               return var10;
            }

            var9 = (Chunk)var8.next();
         } while(!var1 && !this.isRewritable(var9, var6));

         assert var9.maxLen >= 0L;

         var2 += var9.maxLen;
         var4 += var9.maxLenLive;
      }
   }

   public int getChunkCount() {
      return this.chunks.size();
   }

   public int getPageCount() {
      int var1 = 0;

      Chunk var3;
      for(Iterator var2 = this.chunks.values().iterator(); var2.hasNext(); var1 += var3.pageCount) {
         var3 = (Chunk)var2.next();
      }

      return var1;
   }

   public int getLivePageCount() {
      int var1 = 0;

      Chunk var3;
      for(Iterator var2 = this.chunks.values().iterator(); var2.hasNext(); var1 += var3.pageCountLive) {
         var3 = (Chunk)var2.next();
      }

      return var1;
   }

   private int getProjectedFillRate(int var1) {
      this.saveChunkLock.lock();

      try {
         int var2 = 0;
         long var3 = 1L;
         long var5 = 1L;
         long var7 = this.getTimeSinceCreation();
         Iterator var9 = this.chunks.values().iterator();

         while(var9.hasNext()) {
            Chunk var10 = (Chunk)var9.next();

            assert var10.maxLen >= 0L;

            if (this.isRewritable(var10, var7) && var10.getFillRate() <= var1) {
               assert var10.maxLen >= var10.maxLenLive;

               var2 += var10.len;
               var3 += var10.maxLen;
               var5 += var10.maxLenLive;
            }
         }

         int var16 = (int)((long)var2 * var5 / var3);
         int var15 = this.fileStore.getProjectedFillRate(var2 - var16);
         int var11 = var15;
         return var11;
      } finally {
         this.saveChunkLock.unlock();
      }
   }

   public int getFillRate() {
      this.saveChunkLock.lock();

      int var1;
      try {
         var1 = this.fileStore.getFillRate();
      } finally {
         this.saveChunkLock.unlock();
      }

      return var1;
   }

   private Iterable<Chunk> findOldChunks(int var1, int var2) {
      assert this.lastChunk != null;

      long var3 = this.getTimeSinceCreation();
      PriorityQueue var5 = new PriorityQueue(this.chunks.size() / 4 + 1, (var0, var1x) -> {
         int var2 = Integer.compare(var1x.collectPriority, var0.collectPriority);
         if (var2 == 0) {
            var2 = Long.compare(var1x.maxLenLive, var0.maxLenLive);
         }

         return var2;
      });
      long var6 = 0L;
      long var8 = this.lastChunk.version + 1L;
      Iterator var10 = this.chunks.values().iterator();

      while(true) {
         Chunk var11;
         int var12;
         do {
            do {
               if (!var10.hasNext()) {
                  return var5.isEmpty() ? null : var5;
               }

               var11 = (Chunk)var10.next();
               var12 = var11.getFillRate();
            } while(!this.isRewritable(var11, var3));
         } while(var12 > var2);

         long var13 = Math.max(1L, var8 - var11.version);
         var11.collectPriority = (int)((long)(var12 * 1000) / var13);
         var6 += var11.maxLenLive;
         var5.offer(var11);

         while(var6 > (long)var1) {
            Chunk var15 = (Chunk)var5.poll();
            if (var15 == null) {
               break;
            }

            var6 -= var15.maxLenLive;
         }
      }
   }

   private boolean isRewritable(Chunk var1, long var2) {
      return var1.isRewritable() && this.isSeasonedChunk(var1, var2);
   }

   private int compactRewrite(Set<Integer> var1) {
      assert this.storeLock.isHeldByCurrentThread();

      assert this.currentStoreVersion < 0L;

      this.acceptChunkOccupancyChanges(this.getTimeSinceCreation(), this.currentVersion);
      int var2 = this.rewriteChunks(var1, false);
      this.acceptChunkOccupancyChanges(this.getTimeSinceCreation(), this.currentVersion);
      var2 += this.rewriteChunks(var1, true);
      return var2;
   }

   private int rewriteChunks(Set<Integer> var1, boolean var2) {
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(true) {
         int var5;
         Chunk var6;
         long[] var7;
         do {
            if (!var4.hasNext()) {
               return var3;
            }

            var5 = (Integer)var4.next();
            var6 = (Chunk)this.chunks.get(var5);
            var7 = this.getToC(var6);
         } while(var7 == null);

         for(int var8 = 0; (var8 = var6.occupancy.nextClearBit(var8)) < var6.pageCount; ++var8) {
            long var9 = var7[var8];
            int var11 = DataUtils.getPageMapId(var9);
            MVMap var12 = var11 == this.layout.getId() ? this.layout : (var11 == this.meta.getId() ? this.meta : this.getMap(var11));
            if (var12 != null && !var12.isClosed()) {
               assert !var12.isSingleWriter();

               if (var2 || DataUtils.isLeafPosition(var9)) {
                  long var13 = DataUtils.getPagePos(var5, var9);
                  this.serializationLock.unlock();

                  try {
                     if (var12.rewritePage(var13)) {
                        ++var3;
                        if (var12 == this.meta) {
                           this.markMetaChanged();
                        }
                     }
                  } finally {
                     this.serializationLock.lock();
                  }
               }
            }
         }
      }
   }

   private static HashSet<Integer> createIdSet(Iterable<Chunk> var0) {
      HashSet var1 = new HashSet();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Chunk var3 = (Chunk)var2.next();
         var1.add(var3.id);
      }

      return var1;
   }

   <K, V> Page<K, V> readPage(MVMap<K, V> var1, long var2) {
      try {
         if (!DataUtils.isPageSaved(var2)) {
            throw DataUtils.newMVStoreException(6, "Position 0");
         } else {
            Page var4 = this.readPageFromCache(var2);
            if (var4 == null) {
               Chunk var5 = this.getChunk(var2);
               int var6 = DataUtils.getPageOffset(var2);

               try {
                  ByteBuffer var7 = var5.readBufferForPage(this.fileStore, var6, var2);
                  var4 = Page.read(var7, var2, var1);
               } catch (MVStoreException var8) {
                  throw var8;
               } catch (Exception var9) {
                  throw DataUtils.newMVStoreException(6, "Unable to read the page at position {0}, chunk {1}, offset {2}", var2, var5.id, var6, var9);
               }

               this.cachePage(var4);
            }

            return var4;
         }
      } catch (MVStoreException var10) {
         if (this.recoveryMode) {
            return var1.createEmptyLeaf();
         } else {
            throw var10;
         }
      }
   }

   private long[] getToC(Chunk var1) {
      if (var1.tocPos == 0) {
         return null;
      } else {
         long[] var2 = (long[])this.chunksToC.get((long)var1.id);
         if (var2 == null) {
            var2 = var1.readToC(this.fileStore);
            this.chunksToC.put((long)var1.id, var2, var2.length * 8);
         }

         assert var2.length == var1.pageCount : var2.length + " != " + var1.pageCount;

         return var2;
      }
   }

   private <K, V> Page<K, V> readPageFromCache(long var1) {
      return this.cache == null ? null : (Page)this.cache.get(var1);
   }

   void accountForRemovedPage(long var1, long var3, boolean var5, int var6) {
      assert DataUtils.isPageSaved(var1);

      if (var6 < 0) {
         var6 = this.calculatePageNo(var1);
      }

      RemovedPageInfo var7 = new RemovedPageInfo(var1, var5, var3, var6);
      this.removedPages.add(var7);
   }

   private int calculatePageNo(long var1) {
      int var3 = -1;
      Chunk var4 = this.getChunk(var1);
      long[] var5 = this.getToC(var4);
      if (var5 != null) {
         int var6 = DataUtils.getPageOffset(var1);
         int var7 = 0;
         int var8 = var5.length - 1;

         while(var7 <= var8) {
            int var9 = var7 + var8 >>> 1;
            long var10 = (long)DataUtils.getPageOffset(var5[var9]);
            if (var10 < (long)var6) {
               var7 = var9 + 1;
            } else {
               if (var10 <= (long)var6) {
                  var3 = var9;
                  break;
               }

               var8 = var9 - 1;
            }
         }
      }

      return var3;
   }

   Compressor getCompressorFast() {
      if (this.compressorFast == null) {
         this.compressorFast = new CompressLZF();
      }

      return this.compressorFast;
   }

   Compressor getCompressorHigh() {
      if (this.compressorHigh == null) {
         this.compressorHigh = new CompressDeflate();
      }

      return this.compressorHigh;
   }

   int getCompressionLevel() {
      return this.compressionLevel;
   }

   public int getPageSplitSize() {
      return this.pageSplitSize;
   }

   public int getKeysPerPage() {
      return this.keysPerPage;
   }

   public long getMaxPageSize() {
      return this.cache == null ? Long.MAX_VALUE : this.cache.getMaxItemSize() >> 4;
   }

   public boolean getReuseSpace() {
      return this.reuseSpace;
   }

   public void setReuseSpace(boolean var1) {
      this.reuseSpace = var1;
   }

   public int getRetentionTime() {
      return this.retentionTime;
   }

   public void setRetentionTime(int var1) {
      this.retentionTime = var1;
   }

   public void setVersionsToKeep(int var1) {
      this.versionsToKeep = var1;
   }

   public long getVersionsToKeep() {
      return (long)this.versionsToKeep;
   }

   long getOldestVersionToKeep() {
      long var1 = this.oldestVersionToKeep.get();
      var1 = Math.max(var1 - (long)this.versionsToKeep, -1L);
      if (this.fileStore != null) {
         long var3 = this.lastChunkVersion() - 1L;
         if (var3 != -1L && var3 < var1) {
            var1 = var3;
         }
      }

      return var1;
   }

   private void setOldestVersionToKeep(long var1) {
      boolean var3;
      do {
         long var4 = this.oldestVersionToKeep.get();
         var3 = var1 <= var4 || this.oldestVersionToKeep.compareAndSet(var4, var1);
      } while(!var3);

   }

   private long lastChunkVersion() {
      Chunk var1 = this.lastChunk;
      return var1 == null ? 0L : var1.version;
   }

   private boolean isKnownVersion(long var1) {
      if (var1 <= this.currentVersion && var1 >= 0L) {
         if (var1 != this.currentVersion && !this.chunks.isEmpty()) {
            Chunk var3 = this.getChunkForVersion(var1);
            if (var3 == null) {
               return false;
            } else {
               MVMap var4 = this.getLayoutMap(var1);

               try {
                  Iterator var5 = var4.keyIterator("chunk.");

                  while(var5.hasNext()) {
                     String var6 = (String)var5.next();
                     if (!var6.startsWith("chunk.")) {
                        break;
                     }

                     if (!this.layout.containsKey(var6)) {
                        String var7 = (String)var4.get(var6);
                        Chunk var8 = Chunk.fromString(var7);
                        Chunk var9 = this.readChunkHeaderAndFooter(var8.block, var8.id);
                        if (var9 == null) {
                           return false;
                        }
                     }
                  }

                  return true;
               } catch (MVStoreException var10) {
                  return false;
               }
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public void registerUnsavedMemory(int var1) {
      this.unsavedMemory += var1;
      int var2 = this.unsavedMemory;
      if (var2 > this.autoCommitMemory && this.autoCommitMemory > 0) {
         this.saveNeeded = true;
      }

   }

   boolean isSaveNeeded() {
      return this.saveNeeded;
   }

   void beforeWrite(MVMap<?, ?> var1) {
      if (this.saveNeeded && this.fileStore != null && this.isOpenOrStopping() && (this.storeLock.isHeldByCurrentThread() || !var1.getRoot().isLockedByCurrentThread()) && var1 != this.layout) {
         this.saveNeeded = false;
         if (this.autoCommitMemory > 0 && this.needStore()) {
            if (this.requireStore() && !var1.isSingleWriter()) {
               this.commit(MVStore::requireStore);
            } else {
               this.tryCommit(MVStore::needStore);
            }
         }
      }

   }

   private boolean requireStore() {
      return 3 * this.unsavedMemory > 4 * this.autoCommitMemory;
   }

   private boolean needStore() {
      return this.unsavedMemory > this.autoCommitMemory;
   }

   public int getStoreVersion() {
      this.checkOpen();
      String var1 = (String)this.meta.get("setting.storeVersion");
      return var1 == null ? 0 : DataUtils.parseHexInt(var1);
   }

   public void setStoreVersion(int var1) {
      this.storeLock.lock();

      try {
         this.checkOpen();
         this.markMetaChanged();
         this.meta.put("setting.storeVersion", Integer.toHexString(var1));
      } finally {
         this.storeLock.unlock();
      }

   }

   public void rollback() {
      this.rollbackTo(this.currentVersion);
   }

   public void rollbackTo(long var1) {
      this.storeLock.lock();

      try {
         this.checkOpen();
         this.currentVersion = var1;
         MVMap var4;
         if (var1 == 0L) {
            this.layout.setInitialRoot(this.layout.createEmptyLeaf(), -1L);
            this.meta.setInitialRoot(this.meta.createEmptyLeaf(), -1L);
            this.layout.put("meta.id", Integer.toHexString(this.meta.getId()));
            this.deadChunks.clear();
            this.removedPages.clear();
            this.chunks.clear();
            this.clearCaches();
            if (this.fileStore != null) {
               this.saveChunkLock.lock();

               try {
                  this.fileStore.clear();
               } finally {
                  this.saveChunkLock.unlock();
               }
            }

            this.lastChunk = null;
            this.versions.clear();
            this.setWriteVersion(var1);
            this.metaChanged = false;
            Iterator var30 = this.maps.values().iterator();

            while(var30.hasNext()) {
               var4 = (MVMap)var30.next();
               var4.close();
            }

            return;
         }

         DataUtils.checkArgument(this.isKnownVersion(var1), "Unknown version {0}", var1);

         TxCounter var3;
         while((var3 = (TxCounter)this.versions.peekLast()) != null && var3.version >= var1) {
            this.versions.removeLast();
         }

         this.currentTxCounter = new TxCounter(var1);
         if (!this.layout.rollbackRoot(var1)) {
            var4 = this.getLayoutMap(var1);
            this.layout.setInitialRoot(var4.getRootPage(), var1);
         }

         if (!this.meta.rollbackRoot(var1)) {
            this.meta.setRootPos(this.getRootPos(this.meta.getId()), var1 - 1L);
         }

         this.metaChanged = false;
         Iterator var31 = (new ArrayList(this.maps.values())).iterator();

         while(var31.hasNext()) {
            MVMap var5 = (MVMap)var31.next();
            int var6 = var5.getId();
            if (var5.getCreateVersion() >= var1) {
               var5.close();
               this.maps.remove(var6);
            } else if (!var5.rollbackRoot(var1)) {
               var5.setRootPos(this.getRootPos(var6), var1 - 1L);
            }
         }

         this.deadChunks.clear();
         this.removedPages.clear();
         this.clearCaches();
         this.serializationLock.lock();

         try {
            Chunk var32 = this.getChunkForVersion(var1);
            if (var32 != null) {
               this.saveChunkLock.lock();

               try {
                  this.setLastChunk(var32);
                  this.storeHeader.put("clean", 1);
                  this.writeStoreHeader();
                  this.readStoreHeader();
               } finally {
                  this.saveChunkLock.unlock();
               }
            }
         } finally {
            this.serializationLock.unlock();
         }

         this.onVersionChange(this.currentVersion);

         assert !this.hasUnsavedChanges();
      } finally {
         this.unlockAndCheckPanicCondition();
      }

   }

   private void clearCaches() {
      if (this.cache != null) {
         this.cache.clear();
      }

      if (this.chunksToC != null) {
         this.chunksToC.clear();
      }

   }

   private long getRootPos(int var1) {
      String var2 = (String)this.layout.get(MVMap.getMapRootKey(var1));
      return var2 == null ? 0L : DataUtils.parseHexLong(var2);
   }

   public long getCurrentVersion() {
      return this.currentVersion;
   }

   public FileStore getFileStore() {
      return this.fileStore;
   }

   public Map<String, Object> getStoreHeader() {
      return this.storeHeader;
   }

   private void checkOpen() {
      if (!this.isOpenOrStopping()) {
         throw DataUtils.newMVStoreException(4, "This store is closed", this.panicException);
      }
   }

   public void renameMap(MVMap<?, ?> var1, String var2) {
      this.checkOpen();
      DataUtils.checkArgument(var1 != this.layout && var1 != this.meta, "Renaming the meta map is not allowed");
      int var3 = var1.getId();
      String var4 = this.getMapName(var3);
      if (var4 != null && !var4.equals(var2)) {
         String var5 = Integer.toHexString(var3);
         String var6 = (String)this.meta.putIfAbsent("name." + var2, var5);
         DataUtils.checkArgument(var6 == null || var6.equals(var5), "A map named {0} already exists", var2);
         this.meta.put(MVMap.getMapKey(var3), var1.asString(var2));
         this.meta.remove("name." + var4);
         this.markMetaChanged();
      }

   }

   public void removeMap(MVMap<?, ?> var1) {
      this.storeLock.lock();

      try {
         this.checkOpen();
         DataUtils.checkArgument(this.layout != this.meta && var1 != this.meta, "Removing the meta map is not allowed");
         RootReference var2 = var1.clearIt();
         var1.close();
         this.updateCounter += var2.updateCounter;
         this.updateAttemptCounter += var2.updateAttemptCounter;
         int var3 = var1.getId();
         String var4 = this.getMapName(var3);
         if (this.meta.remove(MVMap.getMapKey(var3)) != null) {
            this.markMetaChanged();
         }

         if (this.meta.remove("name." + var4) != null) {
            this.markMetaChanged();
         }
      } finally {
         this.storeLock.unlock();
      }

   }

   void deregisterMapRoot(int var1) {
      if (this.layout.remove(MVMap.getMapRootKey(var1)) != null) {
         this.markMetaChanged();
      }

   }

   public void removeMap(String var1) {
      int var2 = this.getMapId(var1);
      if (var2 > 0) {
         MVMap var3 = this.getMap(var2);
         if (var3 == null) {
            var3 = this.openMap(var1, MVStoreTool.getGenericMapBuilder());
         }

         this.removeMap(var3);
      }

   }

   public String getMapName(int var1) {
      String var2 = (String)this.meta.get(MVMap.getMapKey(var1));
      return var2 == null ? null : DataUtils.getMapName(var2);
   }

   private int getMapId(String var1) {
      String var2 = (String)this.meta.get("name." + var1);
      return var2 == null ? -1 : DataUtils.parseHexInt(var2);
   }

   void writeInBackground() {
      try {
         if (!this.isOpenOrStopping() || this.isReadOnly()) {
            return;
         }

         long var1 = this.getTimeSinceCreation();
         if (var1 > this.lastCommitTime + (long)this.autoCommitDelay) {
            this.tryCommit();
            if (this.autoCompactFillRate < 0) {
               this.compact(-this.getTargetFillRate(), this.autoCommitMemory);
            }
         }

         int var3 = this.getFillRate();
         int var4;
         if (this.fileStore.isFragmented() && var3 < this.autoCompactFillRate) {
            if (this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
               try {
                  var4 = this.autoCommitMemory;
                  if (this.isIdle()) {
                     var4 *= 4;
                  }

                  this.compactMoveChunks(101, (long)var4);
               } finally {
                  this.unlockAndCheckPanicCondition();
               }
            }
         } else if (var3 >= this.autoCompactFillRate && this.lastChunk != null) {
            var4 = this.getRewritableChunksFillRate();
            var4 = this.isIdle() ? 100 - (100 - var4) / 2 : var4;
            if (var4 < this.getTargetFillRate() && this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
               try {
                  int var5 = this.autoCommitMemory * var3 / Math.max(var4, 1);
                  if (!this.isIdle()) {
                     var5 /= 4;
                  }

                  if (this.rewriteChunks(var5, var4)) {
                     this.dropUnusedChunks();
                  }
               } finally {
                  this.storeLock.unlock();
               }
            }
         }

         this.autoCompactLastFileOpCount = this.fileStore.getWriteCount() + this.fileStore.getReadCount();
      } catch (InterruptedException var17) {
      } catch (Throwable var18) {
         this.handleException(var18);
         if (this.backgroundExceptionHandler == null) {
            throw var18;
         }
      }

   }

   private void doMaintenance(int var1) {
      if (this.autoCompactFillRate > 0 && this.lastChunk != null && this.reuseSpace) {
         try {
            int var2 = -1;

            for(int var3 = 0; var3 < 5; ++var3) {
               int var4 = this.getFillRate();
               int var5 = var4;
               if (var4 > var1) {
                  var5 = this.getProjectedFillRate(100);
                  if (var5 > var1 || var5 <= var2) {
                     break;
                  }
               }

               var2 = var5;
               if (!this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
                  break;
               }

               try {
                  int var6 = this.autoCommitMemory * var1 / Math.max(var5, 1);
                  if (var5 < var4 && (!this.rewriteChunks(var6, var1) || this.dropUnusedChunks() == 0) && var3 > 0 || !this.compactMoveChunks(101, (long)var6)) {
                     break;
                  }
               } finally {
                  this.unlockAndCheckPanicCondition();
               }
            }
         } catch (InterruptedException var11) {
            throw new RuntimeException(var11);
         }
      }

   }

   private int getTargetFillRate() {
      int var1 = this.autoCompactFillRate;
      if (!this.isIdle()) {
         var1 /= 2;
      }

      return var1;
   }

   private boolean isIdle() {
      return this.autoCompactLastFileOpCount == this.fileStore.getWriteCount() + this.fileStore.getReadCount();
   }

   private void handleException(Throwable var1) {
      if (this.backgroundExceptionHandler != null) {
         try {
            this.backgroundExceptionHandler.uncaughtException(Thread.currentThread(), var1);
         } catch (Throwable var3) {
            if (var1 != var3) {
               var1.addSuppressed(var3);
            }
         }
      }

   }

   public void setCacheSize(int var1) {
      long var2 = (long)var1 * 1024L * 1024L;
      if (this.cache != null) {
         this.cache.setMaxMemory(var2);
         this.cache.clear();
      }

   }

   private boolean isOpen() {
      return this.state == 0;
   }

   public boolean isClosed() {
      if (this.isOpen()) {
         return false;
      } else {
         this.storeLock.lock();

         boolean var1;
         try {
            assert this.state == 3;

            var1 = true;
         } finally {
            this.storeLock.unlock();
         }

         return var1;
      }
   }

   private boolean isOpenOrStopping() {
      return this.state <= 1;
   }

   private void stopBackgroundThread(boolean var1) {
      while(true) {
         BackgroundWriterThread var2;
         if ((var2 = (BackgroundWriterThread)this.backgroundWriterThread.get()) != null) {
            if (!this.backgroundWriterThread.compareAndSet(var2, (Object)null)) {
               continue;
            }

            if (var2 != Thread.currentThread()) {
               synchronized(var2.sync) {
                  var2.sync.notifyAll();
               }

               if (var1) {
                  try {
                     var2.join();
                  } catch (Exception var5) {
                  }
               }
            }

            shutdownExecutor(this.serializationExecutor);
            this.serializationExecutor = null;
            shutdownExecutor(this.bufferSaveExecutor);
            this.bufferSaveExecutor = null;
         }

         return;
      }
   }

   public void setAutoCommitDelay(int var1) {
      if (this.autoCommitDelay != var1) {
         this.autoCommitDelay = var1;
         if (this.fileStore != null && !this.fileStore.isReadOnly()) {
            this.stopBackgroundThread(true);
            if (var1 > 0 && this.isOpen()) {
               int var2 = Math.max(1, var1 / 10);
               BackgroundWriterThread var3 = new BackgroundWriterThread(this, var2, this.fileStore.toString());
               if (this.backgroundWriterThread.compareAndSet((Object)null, var3)) {
                  var3.start();
                  this.serializationExecutor = createSingleThreadExecutor("H2-serialization");
                  this.bufferSaveExecutor = createSingleThreadExecutor("H2-save");
               }
            }

         }
      }
   }

   private static ThreadPoolExecutor createSingleThreadExecutor(String var0) {
      return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), (var1) -> {
         Thread var2 = new Thread(var1, var0);
         var2.setDaemon(true);
         return var2;
      });
   }

   public boolean isBackgroundThread() {
      return Thread.currentThread() == this.backgroundWriterThread.get();
   }

   public int getAutoCommitDelay() {
      return this.autoCommitDelay;
   }

   public int getAutoCommitMemory() {
      return this.autoCommitMemory;
   }

   public int getUnsavedMemory() {
      return this.unsavedMemory;
   }

   void cachePage(Page<?, ?> var1) {
      if (this.cache != null) {
         this.cache.put(var1.getPos(), var1, var1.getMemory());
      }

   }

   public int getCacheSizeUsed() {
      return this.cache == null ? 0 : (int)(this.cache.getUsedMemory() >> 20);
   }

   public int getCacheSize() {
      return this.cache == null ? 0 : (int)(this.cache.getMaxMemory() >> 20);
   }

   public CacheLongKeyLIRS<Page<?, ?>> getCache() {
      return this.cache;
   }

   public boolean isReadOnly() {
      return this.fileStore != null && this.fileStore.isReadOnly();
   }

   public int getCacheHitRatio() {
      return getCacheHitRatio(this.cache);
   }

   public int getTocCacheHitRatio() {
      return getCacheHitRatio(this.chunksToC);
   }

   private static int getCacheHitRatio(CacheLongKeyLIRS<?> var0) {
      if (var0 == null) {
         return 0;
      } else {
         long var1 = var0.getHits();
         return (int)(100L * var1 / (var1 + var0.getMisses() + 1L));
      }
   }

   public int getLeafRatio() {
      return (int)(this.leafCount * 100L / Math.max(1L, this.leafCount + this.nonLeafCount));
   }

   public double getUpdateFailureRatio() {
      long var1 = this.updateCounter;
      long var3 = this.updateAttemptCounter;
      RootReference var5 = this.layout.getRoot();
      var1 += var5.updateCounter;
      var3 += var5.updateAttemptCounter;
      var5 = this.meta.getRoot();
      var1 += var5.updateCounter;
      var3 += var5.updateAttemptCounter;

      RootReference var8;
      for(Iterator var6 = this.maps.values().iterator(); var6.hasNext(); var3 += var8.updateAttemptCounter) {
         MVMap var7 = (MVMap)var6.next();
         var8 = var7.getRoot();
         var1 += var8.updateCounter;
      }

      return var3 == 0L ? 0.0 : 1.0 - (double)var1 / (double)var3;
   }

   public TxCounter registerVersionUsage() {
      while(true) {
         TxCounter var1 = this.currentTxCounter;
         if (var1.incrementAndGet() > 0) {
            return var1;
         }

         assert var1 != this.currentTxCounter : var1;

         var1.decrementAndGet();
      }
   }

   public void deregisterVersionUsage(TxCounter var1) {
      if (var1 != null && var1.decrementAndGet() <= 0) {
         if (this.storeLock.isHeldByCurrentThread()) {
            this.dropUnusedVersions();
         } else if (this.storeLock.tryLock()) {
            try {
               this.dropUnusedVersions();
            } finally {
               this.storeLock.unlock();
            }
         }
      }

   }

   private void onVersionChange(long var1) {
      TxCounter var3 = this.currentTxCounter;

      assert var3.get() >= 0;

      this.versions.add(var3);
      this.currentTxCounter = new TxCounter(var1);
      var3.decrementAndGet();
      this.dropUnusedVersions();
   }

   private void dropUnusedVersions() {
      TxCounter var1;
      while((var1 = (TxCounter)this.versions.peek()) != null && var1.get() < 0) {
         this.versions.poll();
      }

      this.setOldestVersionToKeep((var1 != null ? var1 : this.currentTxCounter).version);
   }

   private int dropUnusedChunks() {
      assert this.storeLock.isHeldByCurrentThread();

      int var1 = 0;
      if (!this.deadChunks.isEmpty()) {
         long var2 = this.getOldestVersionToKeep();
         long var4 = this.getTimeSinceCreation();
         this.saveChunkLock.lock();

         try {
            while(true) {
               Chunk var6;
               do {
                  if ((var6 = (Chunk)this.deadChunks.poll()) == null || (!this.isSeasonedChunk(var6, var4) || !canOverwriteChunk(var6, var2)) && this.deadChunks.offerFirst(var6)) {
                     return var1;
                  }
               } while(this.chunks.remove(var6.id) == null);

               long[] var7 = (long[])this.chunksToC.remove((long)var6.id);
               if (var7 != null && this.cache != null) {
                  long[] var8 = var7;
                  int var9 = var7.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     long var11 = var8[var10];
                     long var13 = DataUtils.getPagePos(var6.id, var11);
                     this.cache.remove(var13);
                  }
               }

               if (this.layout.remove(Chunk.getMetaKey(var6.id)) != null) {
                  this.markMetaChanged();
               }

               if (var6.isSaved()) {
                  this.freeChunkSpace(var6);
               }

               ++var1;
            }
         } finally {
            this.saveChunkLock.unlock();
         }
      } else {
         return var1;
      }
   }

   private void freeChunkSpace(Chunk var1) {
      long var2 = var1.block * 4096L;
      int var4 = var1.len * 4096;
      this.freeFileSpace(var2, var4);
   }

   private void freeFileSpace(long var1, int var3) {
      this.fileStore.free(var1, var3);

      assert this.validateFileLength(var1 + ":" + var3);

   }

   private boolean validateFileLength(String var1) {
      assert this.saveChunkLock.isHeldByCurrentThread();

      assert this.fileStore.getFileLengthInUse() == this.measureFileLengthInUse() : this.fileStore.getFileLengthInUse() + " != " + this.measureFileLengthInUse() + " " + var1;

      return true;
   }

   public static final class Builder {
      private final HashMap<String, Object> config;

      private Builder(HashMap<String, Object> var1) {
         this.config = var1;
      }

      public Builder() {
         this.config = new HashMap();
      }

      private Builder set(String var1, Object var2) {
         this.config.put(var1, var2);
         return this;
      }

      public Builder autoCommitDisabled() {
         return this.set("autoCommitDelay", 0);
      }

      public Builder autoCommitBufferSize(int var1) {
         return this.set("autoCommitBufferSize", var1);
      }

      public Builder autoCompactFillRate(int var1) {
         return this.set("autoCompactFillRate", var1);
      }

      public Builder fileName(String var1) {
         return this.set("fileName", var1);
      }

      public Builder encryptionKey(char[] var1) {
         return this.set("encryptionKey", var1);
      }

      public Builder readOnly() {
         return this.set("readOnly", 1);
      }

      public Builder keysPerPage(int var1) {
         return this.set("keysPerPage", var1);
      }

      public Builder recoveryMode() {
         return this.set("recoveryMode", 1);
      }

      public Builder cacheSize(int var1) {
         return this.set("cacheSize", var1);
      }

      public Builder cacheConcurrency(int var1) {
         return this.set("cacheConcurrency", var1);
      }

      public Builder compress() {
         return this.set("compress", 1);
      }

      public Builder compressHigh() {
         return this.set("compress", 2);
      }

      public Builder pageSplitSize(int var1) {
         return this.set("pageSplitSize", var1);
      }

      public Builder backgroundExceptionHandler(Thread.UncaughtExceptionHandler var1) {
         return this.set("backgroundExceptionHandler", var1);
      }

      public Builder fileStore(FileStore var1) {
         return this.set("fileStore", var1);
      }

      public MVStore open() {
         return new MVStore(this.config);
      }

      public String toString() {
         return DataUtils.appendMap(new StringBuilder(), this.config).toString();
      }

      public static Builder fromString(String var0) {
         return new Builder(DataUtils.parseMap(var0));
      }
   }

   private static class RemovedPageInfo implements Comparable<RemovedPageInfo> {
      final long version;
      final long removedPageInfo;

      RemovedPageInfo(long var1, boolean var3, long var4, int var6) {
         this.removedPageInfo = createRemovedPageInfo(var1, var3, var6);
         this.version = var4;
      }

      public int compareTo(RemovedPageInfo var1) {
         return Long.compare(this.version, var1.version);
      }

      int getPageChunkId() {
         return DataUtils.getPageChunkId(this.removedPageInfo);
      }

      int getPageNo() {
         return DataUtils.getPageOffset(this.removedPageInfo);
      }

      int getPageLength() {
         return DataUtils.getPageMaxLength(this.removedPageInfo);
      }

      boolean isPinned() {
         return (this.removedPageInfo & 1L) == 1L;
      }

      private static long createRemovedPageInfo(long var0, boolean var2, int var3) {
         long var4 = var0 & -274877906882L | (long)(var3 << 6) & 4294967295L;
         if (var2) {
            var4 |= 1L;
         }

         return var4;
      }

      public String toString() {
         return "RemovedPageInfo{version=" + this.version + ", chunk=" + this.getPageChunkId() + ", pageNo=" + this.getPageNo() + ", len=" + this.getPageLength() + (this.isPinned() ? ", pinned" : "") + '}';
      }
   }

   private static class BackgroundWriterThread extends Thread {
      public final Object sync = new Object();
      private final MVStore store;
      private final int sleep;

      BackgroundWriterThread(MVStore var1, int var2, String var3) {
         super("MVStore background writer " + var3);
         this.store = var1;
         this.sleep = var2;
         this.setDaemon(true);
      }

      public void run() {
         while(true) {
            if (this.store.isBackgroundThread()) {
               synchronized(this.sync) {
                  try {
                     this.sync.wait((long)this.sleep);
                  } catch (InterruptedException var4) {
                  }
               }

               if (this.store.isBackgroundThread()) {
                  this.store.writeInBackground();
                  continue;
               }
            }

            return;
         }
      }
   }

   public static final class TxCounter {
      public final long version;
      private volatile int counter;
      private static final AtomicIntegerFieldUpdater<TxCounter> counterUpdater = AtomicIntegerFieldUpdater.newUpdater(TxCounter.class, "counter");

      TxCounter(long var1) {
         this.version = var1;
      }

      int get() {
         return this.counter;
      }

      int incrementAndGet() {
         return counterUpdater.incrementAndGet(this);
      }

      int decrementAndGet() {
         return counterUpdater.decrementAndGet(this);
      }

      public String toString() {
         return "v=" + this.version + " / cnt=" + this.counter;
      }
   }
}
