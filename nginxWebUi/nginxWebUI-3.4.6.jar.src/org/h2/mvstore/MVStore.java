/*      */ package org.h2.mvstore;
/*      */ 
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Deque;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.Map;
/*      */ import java.util.PriorityQueue;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ArrayBlockingQueue;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.concurrent.LinkedBlockingQueue;
/*      */ import java.util.concurrent.PriorityBlockingQueue;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import java.util.function.Predicate;
/*      */ import java.util.function.Supplier;
/*      */ import org.h2.compress.CompressDeflate;
/*      */ import org.h2.compress.CompressLZF;
/*      */ import org.h2.compress.Compressor;
/*      */ import org.h2.mvstore.cache.CacheLongKeyLIRS;
/*      */ import org.h2.mvstore.type.DataType;
/*      */ import org.h2.mvstore.type.StringDataType;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.Utils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MVStore
/*      */   implements AutoCloseable
/*      */ {
/*      */   private static final String HDR_H = "H";
/*      */   private static final String HDR_BLOCK_SIZE = "blockSize";
/*      */   private static final String HDR_FORMAT = "format";
/*      */   private static final String HDR_CREATED = "created";
/*      */   private static final String HDR_FORMAT_READ = "formatRead";
/*      */   private static final String HDR_CHUNK = "chunk";
/*      */   private static final String HDR_BLOCK = "block";
/*      */   private static final String HDR_VERSION = "version";
/*      */   private static final String HDR_CLEAN = "clean";
/*      */   private static final String HDR_FLETCHER = "fletcher";
/*      */   public static final String META_ID_KEY = "meta.id";
/*      */   static final int BLOCK_SIZE = 4096;
/*      */   private static final int FORMAT_WRITE_MIN = 2;
/*      */   private static final int FORMAT_WRITE_MAX = 2;
/*      */   private static final int FORMAT_READ_MIN = 2;
/*      */   private static final int FORMAT_READ_MAX = 2;
/*      */   private static final int STATE_OPEN = 0;
/*      */   private static final int STATE_STOPPING = 1;
/*      */   private static final int STATE_CLOSING = 2;
/*      */   private static final int STATE_CLOSED = 3;
/*      */   private static final int PIPE_LENGTH = 1;
/*  203 */   private final ReentrantLock storeLock = new ReentrantLock(true);
/*  204 */   private final ReentrantLock serializationLock = new ReentrantLock(true);
/*  205 */   private final ReentrantLock saveChunkLock = new ReentrantLock(true);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  210 */   private final AtomicReference<BackgroundWriterThread> backgroundWriterThread = new AtomicReference<>();
/*      */ 
/*      */ 
/*      */   
/*      */   private ThreadPoolExecutor serializationExecutor;
/*      */ 
/*      */ 
/*      */   
/*      */   private ThreadPoolExecutor bufferSaveExecutor;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile boolean reuseSpace = true;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile int state;
/*      */ 
/*      */ 
/*      */   
/*      */   private final FileStore fileStore;
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean fileStoreIsProvided;
/*      */ 
/*      */ 
/*      */   
/*      */   private final int pageSplitSize;
/*      */ 
/*      */ 
/*      */   
/*      */   private final int keysPerPage;
/*      */ 
/*      */ 
/*      */   
/*      */   private final CacheLongKeyLIRS<Page<?, ?>> cache;
/*      */ 
/*      */ 
/*      */   
/*      */   private final CacheLongKeyLIRS<long[]> chunksToC;
/*      */ 
/*      */   
/*      */   private volatile Chunk lastChunk;
/*      */ 
/*      */   
/*  256 */   private final ConcurrentHashMap<Integer, Chunk> chunks = new ConcurrentHashMap<>();
/*      */   
/*  258 */   private final Queue<RemovedPageInfo> removedPages = new PriorityBlockingQueue<>();
/*      */   
/*  260 */   private final Deque<Chunk> deadChunks = new ArrayDeque<>();
/*      */   
/*  262 */   private long updateCounter = 0L;
/*  263 */   private long updateAttemptCounter = 0L;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final MVMap<String, String> layout;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final MVMap<String, String> meta;
/*      */ 
/*      */ 
/*      */   
/*  277 */   private final ConcurrentHashMap<Integer, MVMap<?, ?>> maps = new ConcurrentHashMap<>();
/*      */   
/*  279 */   private final HashMap<String, Object> storeHeader = new HashMap<>();
/*      */   
/*  281 */   private final Queue<WriteBuffer> writeBufferPool = new ArrayBlockingQueue<>(2);
/*      */   
/*  283 */   private final AtomicInteger lastMapId = new AtomicInteger();
/*      */   
/*      */   private int lastChunkId;
/*      */   
/*  287 */   private int versionsToKeep = 5;
/*      */ 
/*      */   
/*      */   private final int compressionLevel;
/*      */ 
/*      */   
/*      */   private Compressor compressorFast;
/*      */ 
/*      */   
/*      */   private Compressor compressorHigh;
/*      */ 
/*      */   
/*      */   private final boolean recoveryMode;
/*      */ 
/*      */   
/*      */   public final Thread.UncaughtExceptionHandler backgroundExceptionHandler;
/*      */ 
/*      */   
/*      */   private volatile long currentVersion;
/*      */ 
/*      */   
/*  308 */   private final AtomicLong oldestVersionToKeep = new AtomicLong();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  314 */   private final Deque<TxCounter> versions = new LinkedList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  319 */   private volatile TxCounter currentTxCounter = new TxCounter(this.currentVersion);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int unsavedMemory;
/*      */ 
/*      */ 
/*      */   
/*      */   private final int autoCommitMemory;
/*      */ 
/*      */ 
/*      */   
/*      */   private volatile boolean saveNeeded;
/*      */ 
/*      */ 
/*      */   
/*      */   private long creationTime;
/*      */ 
/*      */ 
/*      */   
/*      */   private int retentionTime;
/*      */ 
/*      */ 
/*      */   
/*      */   private long lastCommitTime;
/*      */ 
/*      */ 
/*      */   
/*  348 */   private volatile long currentStoreVersion = -1L;
/*      */ 
/*      */   
/*      */   private volatile boolean metaChanged;
/*      */ 
/*      */   
/*      */   private int autoCommitDelay;
/*      */ 
/*      */   
/*      */   private final int autoCompactFillRate;
/*      */ 
/*      */   
/*      */   private long autoCompactLastFileOpCount;
/*      */ 
/*      */   
/*      */   private volatile MVStoreException panicException;
/*      */ 
/*      */   
/*      */   private long lastTimeAbsolute;
/*      */ 
/*      */   
/*      */   private long leafCount;
/*      */ 
/*      */   
/*      */   private long nonLeafCount;
/*      */ 
/*      */ 
/*      */   
/*      */   MVStore(Map<String, Object> paramMap) {
/*  377 */     this.recoveryMode = paramMap.containsKey("recoveryMode");
/*  378 */     this.compressionLevel = DataUtils.getConfigParam(paramMap, "compress", 0);
/*  379 */     String str = (String)paramMap.get("fileName");
/*  380 */     FileStore fileStore = (FileStore)paramMap.get("fileStore");
/*  381 */     if (fileStore == null) {
/*  382 */       this.fileStoreIsProvided = false;
/*  383 */       if (str != null) {
/*  384 */         fileStore = new FileStore();
/*      */       }
/*      */     } else {
/*  387 */       if (str != null) {
/*  388 */         throw new IllegalArgumentException("fileName && fileStore");
/*      */       }
/*  390 */       this.fileStoreIsProvided = true;
/*      */     } 
/*  392 */     this.fileStore = fileStore;
/*      */     
/*  394 */     int i = 48;
/*  395 */     CacheLongKeyLIRS.Config config1 = null;
/*  396 */     CacheLongKeyLIRS.Config config2 = null;
/*  397 */     if (this.fileStore != null) {
/*  398 */       int j = DataUtils.getConfigParam(paramMap, "cacheSize", 16);
/*  399 */       if (j > 0) {
/*  400 */         config1 = new CacheLongKeyLIRS.Config();
/*  401 */         config1.maxMemory = j * 1024L * 1024L;
/*  402 */         Object object = paramMap.get("cacheConcurrency");
/*  403 */         if (object != null) {
/*  404 */           config1.segmentCount = ((Integer)object).intValue();
/*      */         }
/*      */       } 
/*  407 */       config2 = new CacheLongKeyLIRS.Config();
/*  408 */       config2.maxMemory = 1048576L;
/*  409 */       i = 16384;
/*      */     } 
/*  411 */     if (config1 != null) {
/*  412 */       this.cache = new CacheLongKeyLIRS(config1);
/*      */     } else {
/*  414 */       this.cache = null;
/*      */     } 
/*  416 */     this.chunksToC = (config2 == null) ? null : new CacheLongKeyLIRS(config2);
/*      */     
/*  418 */     i = DataUtils.getConfigParam(paramMap, "pageSplitSize", i);
/*      */     
/*  420 */     if (this.cache != null && i > this.cache.getMaxItemSize()) {
/*  421 */       i = (int)this.cache.getMaxItemSize();
/*      */     }
/*  423 */     this.pageSplitSize = i;
/*  424 */     this.keysPerPage = DataUtils.getConfigParam(paramMap, "keysPerPage", 48);
/*  425 */     this
/*  426 */       .backgroundExceptionHandler = (Thread.UncaughtExceptionHandler)paramMap.get("backgroundExceptionHandler");
/*  427 */     this.layout = new MVMap<>(this, 0, (DataType<String>)StringDataType.INSTANCE, (DataType<String>)StringDataType.INSTANCE);
/*  428 */     if (this.fileStore != null) {
/*  429 */       this.retentionTime = this.fileStore.getDefaultRetentionTime();
/*      */       
/*  431 */       int j = Math.max(1, Math.min(19, Utils.scaleForAvailableMemory(64))) * 1024;
/*  432 */       j = DataUtils.getConfigParam(paramMap, "autoCommitBufferSize", j);
/*  433 */       this.autoCommitMemory = j * 1024;
/*  434 */       this.autoCompactFillRate = DataUtils.getConfigParam(paramMap, "autoCompactFillRate", 90);
/*  435 */       char[] arrayOfChar = (char[])paramMap.get("encryptionKey");
/*      */ 
/*      */       
/*  438 */       this.storeLock.lock();
/*      */       try {
/*  440 */         this.saveChunkLock.lock();
/*      */         try {
/*  442 */           if (!this.fileStoreIsProvided) {
/*  443 */             boolean bool = paramMap.containsKey("readOnly");
/*  444 */             this.fileStore.open(str, bool, arrayOfChar);
/*      */           } 
/*  446 */           if (this.fileStore.size() == 0L) {
/*  447 */             this.creationTime = getTimeAbsolute();
/*  448 */             this.storeHeader.put("H", Integer.valueOf(2));
/*  449 */             this.storeHeader.put("blockSize", Integer.valueOf(4096));
/*  450 */             this.storeHeader.put("format", Integer.valueOf(2));
/*  451 */             this.storeHeader.put("created", Long.valueOf(this.creationTime));
/*  452 */             setLastChunk(null);
/*  453 */             writeStoreHeader();
/*      */           } else {
/*  455 */             readStoreHeader();
/*      */           } 
/*      */         } finally {
/*  458 */           this.saveChunkLock.unlock();
/*      */         } 
/*  460 */       } catch (MVStoreException mVStoreException) {
/*  461 */         panic(mVStoreException);
/*      */       } finally {
/*  463 */         if (arrayOfChar != null) {
/*  464 */           Arrays.fill(arrayOfChar, false);
/*      */         }
/*  466 */         unlockAndCheckPanicCondition();
/*      */       } 
/*  468 */       this.lastCommitTime = getTimeSinceCreation();
/*      */       
/*  470 */       this.meta = openMetaMap();
/*  471 */       scrubLayoutMap();
/*  472 */       scrubMetaMap();
/*      */ 
/*      */ 
/*      */       
/*  476 */       int k = DataUtils.getConfigParam(paramMap, "autoCommitDelay", 1000);
/*  477 */       setAutoCommitDelay(k);
/*      */     } else {
/*  479 */       this.autoCommitMemory = 0;
/*  480 */       this.autoCompactFillRate = 0;
/*  481 */       this.meta = openMetaMap();
/*      */     } 
/*  483 */     onVersionChange(this.currentVersion);
/*      */   }
/*      */   private MVMap<String, String> openMetaMap() {
/*      */     int i;
/*  487 */     String str = this.layout.get("meta.id");
/*      */     
/*  489 */     if (str == null) {
/*  490 */       i = this.lastMapId.incrementAndGet();
/*  491 */       this.layout.put("meta.id", Integer.toHexString(i));
/*      */     } else {
/*  493 */       i = DataUtils.parseHexInt(str);
/*      */     } 
/*  495 */     MVMap<Object, Object> mVMap = new MVMap<>(this, i, (DataType<?>)StringDataType.INSTANCE, (DataType<?>)StringDataType.INSTANCE);
/*  496 */     mVMap.setRootPos(getRootPos(mVMap.getId()), this.currentVersion - 1L);
/*  497 */     return (MVMap)mVMap;
/*      */   }
/*      */   
/*      */   private void scrubLayoutMap() {
/*  501 */     HashSet<String> hashSet = new HashSet();
/*      */ 
/*      */     
/*  504 */     for (String str : new String[] { "name.", "map." }) {
/*  505 */       for (Iterator<String> iterator = this.layout.keyIterator(str); iterator.hasNext(); ) {
/*  506 */         String str1 = iterator.next();
/*  507 */         if (!str1.startsWith(str)) {
/*      */           break;
/*      */         }
/*  510 */         this.meta.putIfAbsent(str1, this.layout.get(str1));
/*  511 */         markMetaChanged();
/*  512 */         hashSet.add(str1);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  517 */     for (null = this.layout.keyIterator("root."); null.hasNext(); ) {
/*  518 */       String str1 = null.next();
/*  519 */       if (!str1.startsWith("root.")) {
/*      */         break;
/*      */       }
/*  522 */       String str2 = str1.substring(str1.lastIndexOf('.') + 1);
/*  523 */       if (!this.meta.containsKey("map." + str2) && DataUtils.parseHexInt(str2) != this.meta.getId()) {
/*  524 */         hashSet.add(str1);
/*      */       }
/*      */     } 
/*      */     
/*  528 */     for (String str : hashSet) {
/*  529 */       this.layout.remove(str);
/*      */     }
/*      */   }
/*      */   
/*      */   private void scrubMetaMap() {
/*  534 */     HashSet<String> hashSet = new HashSet();
/*      */ 
/*      */ 
/*      */     
/*  538 */     for (null = this.meta.keyIterator("name."); null.hasNext(); ) {
/*  539 */       String str1 = null.next();
/*  540 */       if (!str1.startsWith("name.")) {
/*      */         break;
/*      */       }
/*  543 */       String str2 = str1.substring("name.".length());
/*  544 */       int i = DataUtils.parseHexInt(this.meta.get(str1));
/*  545 */       String str3 = getMapName(i);
/*  546 */       if (!str2.equals(str3)) {
/*  547 */         hashSet.add(str1);
/*      */       }
/*      */     } 
/*      */     
/*  551 */     for (String str : hashSet) {
/*  552 */       this.meta.remove(str);
/*  553 */       markMetaChanged();
/*      */     } 
/*      */     
/*  556 */     for (Iterator<String> iterator = this.meta.keyIterator("map."); iterator.hasNext(); ) {
/*  557 */       String str1 = iterator.next();
/*  558 */       if (!str1.startsWith("map.")) {
/*      */         break;
/*      */       }
/*  561 */       String str2 = DataUtils.getMapName(this.meta.get(str1));
/*  562 */       String str3 = str1.substring("map.".length());
/*      */       
/*  564 */       int i = DataUtils.parseHexInt(str3);
/*  565 */       if (i > this.lastMapId.get()) {
/*  566 */         this.lastMapId.set(i);
/*      */       }
/*      */       
/*  569 */       if (!str3.equals(this.meta.get("name." + str2))) {
/*  570 */         this.meta.put("name." + str2, str3);
/*  571 */         markMetaChanged();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void unlockAndCheckPanicCondition() {
/*  577 */     this.storeLock.unlock();
/*  578 */     if (getPanicException() != null) {
/*  579 */       closeImmediately();
/*      */     }
/*      */   }
/*      */   
/*      */   private void panic(MVStoreException paramMVStoreException) {
/*  584 */     if (isOpen()) {
/*  585 */       handleException(paramMVStoreException);
/*  586 */       this.panicException = paramMVStoreException;
/*      */     } 
/*  588 */     throw paramMVStoreException;
/*      */   }
/*      */   
/*      */   public MVStoreException getPanicException() {
/*  592 */     return this.panicException;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static MVStore open(String paramString) {
/*  603 */     HashMap<Object, Object> hashMap = new HashMap<>();
/*  604 */     hashMap.put("fileName", paramString);
/*  605 */     return new MVStore((Map)hashMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <K, V> MVMap<K, V> openMap(String paramString) {
/*  619 */     return openMap(paramString, new MVMap.Builder<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <M extends MVMap<K, V>, K, V> M openMap(String paramString, MVMap.MapBuilder<M, K, V> paramMapBuilder) {
/*      */     MVMap mVMap1;
/*  635 */     int i = getMapId(paramString);
/*  636 */     if (i >= 0) {
/*      */       
/*  638 */       MVMap<?, ?> mVMap = getMap(i);
/*  639 */       if (mVMap == null) {
/*  640 */         mVMap = openMap(i, paramMapBuilder);
/*      */       }
/*  642 */       assert paramMapBuilder.getKeyType() == null || mVMap.getKeyType().getClass().equals(paramMapBuilder.getKeyType().getClass());
/*  643 */       assert paramMapBuilder.getValueType() == null || mVMap
/*  644 */         .getValueType().getClass().equals(paramMapBuilder.getValueType().getClass());
/*  645 */       return (M)mVMap;
/*      */     } 
/*  647 */     HashMap<Object, Object> hashMap = new HashMap<>();
/*  648 */     i = this.lastMapId.incrementAndGet();
/*  649 */     assert getMap(i) == null;
/*  650 */     hashMap.put("id", Integer.valueOf(i));
/*  651 */     hashMap.put("createVersion", Long.valueOf(this.currentVersion));
/*  652 */     M m = paramMapBuilder.create(this, (Map)hashMap);
/*  653 */     String str1 = Integer.toHexString(i);
/*  654 */     this.meta.put(MVMap.getMapKey(i), m.asString(paramString));
/*  655 */     String str2 = this.meta.putIfAbsent("name." + paramString, str1);
/*  656 */     if (str2 != null) {
/*      */       
/*  658 */       this.meta.remove(MVMap.getMapKey(i));
/*  659 */       return openMap(paramString, paramMapBuilder);
/*      */     } 
/*  661 */     long l = this.currentVersion - 1L;
/*  662 */     m.setRootPos(0L, l);
/*  663 */     markMetaChanged();
/*      */     
/*  665 */     MVMap mVMap2 = this.maps.putIfAbsent(Integer.valueOf(i), (MVMap<?, ?>)m);
/*  666 */     if (mVMap2 != null) {
/*  667 */       mVMap1 = mVMap2;
/*      */     }
/*  669 */     return (M)mVMap1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <M extends MVMap<K, V>, K, V> M openMap(int paramInt, MVMap.MapBuilder<M, K, V> paramMapBuilder) {
/*      */     MVMap<?, ?> mVMap;
/*  686 */     while ((mVMap = getMap(paramInt)) == null) {
/*  687 */       String str = this.meta.get(MVMap.getMapKey(paramInt));
/*  688 */       DataUtils.checkArgument((str != null), "Missing map with id {0}", new Object[] { Integer.valueOf(paramInt) });
/*  689 */       HashMap<String, String> hashMap = new HashMap<>(DataUtils.parseMap(str));
/*  690 */       hashMap.put("id", Integer.valueOf(paramInt));
/*  691 */       mVMap = (MVMap<?, ?>)paramMapBuilder.create(this, (Map)hashMap);
/*  692 */       long l1 = getRootPos(paramInt);
/*  693 */       long l2 = this.currentVersion - 1L;
/*  694 */       mVMap.setRootPos(l1, l2);
/*  695 */       if (this.maps.putIfAbsent(Integer.valueOf(paramInt), mVMap) == null) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */     
/*  700 */     return (M)mVMap;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <K, V> MVMap<K, V> getMap(int paramInt) {
/*  712 */     checkOpen();
/*      */     
/*  714 */     return (MVMap)this.maps.get(Integer.valueOf(paramInt));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<String> getMapNames() {
/*  724 */     HashSet<String> hashSet = new HashSet();
/*  725 */     checkOpen();
/*  726 */     for (Iterator<String> iterator = this.meta.keyIterator("name."); iterator.hasNext(); ) {
/*  727 */       String str1 = iterator.next();
/*  728 */       if (!str1.startsWith("name.")) {
/*      */         break;
/*      */       }
/*  731 */       String str2 = str1.substring("name.".length());
/*  732 */       hashSet.add(str2);
/*      */     } 
/*  734 */     return hashSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MVMap<String, String> getLayoutMap() {
/*  752 */     checkOpen();
/*  753 */     return this.layout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MVMap<String, String> getMetaMap() {
/*  772 */     checkOpen();
/*  773 */     return this.meta;
/*      */   }
/*      */   
/*      */   private MVMap<String, String> getLayoutMap(long paramLong) {
/*  777 */     Chunk chunk = getChunkForVersion(paramLong);
/*  778 */     DataUtils.checkArgument((chunk != null), "Unknown version {0}", new Object[] { Long.valueOf(paramLong) });
/*  779 */     long l = chunk.block;
/*  780 */     chunk = readChunkHeader(l);
/*  781 */     return this.layout.openReadOnly(chunk.layoutRootPos, paramLong);
/*      */   }
/*      */ 
/*      */   
/*      */   private Chunk getChunkForVersion(long paramLong) {
/*  786 */     Chunk chunk = null;
/*  787 */     for (Chunk chunk1 : this.chunks.values()) {
/*  788 */       if (chunk1.version <= paramLong && (
/*  789 */         chunk == null || chunk1.id > chunk.id)) {
/*  790 */         chunk = chunk1;
/*      */       }
/*      */     } 
/*      */     
/*  794 */     return chunk;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasMap(String paramString) {
/*  804 */     return this.meta.containsKey("name." + paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasData(String paramString) {
/*  814 */     return (hasMap(paramString) && getRootPos(getMapId(paramString)) != 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void markMetaChanged() {
/*  820 */     this.metaChanged = true;
/*      */   }
/*      */   
/*      */   private void readStoreHeader() {
/*  824 */     Chunk chunk = null;
/*  825 */     boolean bool1 = true;
/*  826 */     boolean bool2 = false;
/*      */ 
/*      */     
/*  829 */     ByteBuffer byteBuffer = this.fileStore.readFully(0L, 8192);
/*  830 */     byte[] arrayOfByte = new byte[4096]; int i;
/*  831 */     for (i = 0; i <= 4096; i += 4096) {
/*  832 */       byteBuffer.get(arrayOfByte);
/*      */       
/*      */       try {
/*  835 */         HashMap<String, String> hashMap1 = DataUtils.parseChecksummedMap(arrayOfByte);
/*  836 */         if (hashMap1 == null) {
/*  837 */           bool1 = false;
/*      */         } else {
/*      */           
/*  840 */           long l = DataUtils.readHexLong(hashMap1, "version", 0L);
/*      */ 
/*      */           
/*  843 */           bool1 = (bool1 && (chunk == null || l == chunk.version)) ? true : false;
/*  844 */           if (chunk == null || l > chunk.version) {
/*  845 */             bool2 = true;
/*  846 */             this.storeHeader.putAll(hashMap1);
/*  847 */             this.creationTime = DataUtils.readHexLong(hashMap1, "created", 0L);
/*  848 */             int k = DataUtils.readHexInt(hashMap1, "chunk", 0);
/*  849 */             long l5 = DataUtils.readHexLong(hashMap1, "block", 2L);
/*  850 */             Chunk chunk1 = readChunkHeaderAndFooter(l5, k);
/*  851 */             if (chunk1 != null)
/*  852 */               chunk = chunk1; 
/*      */           } 
/*      */         } 
/*  855 */       } catch (Exception exception) {
/*  856 */         bool1 = false;
/*      */       } 
/*      */     } 
/*      */     
/*  860 */     if (!bool2) {
/*  861 */       throw DataUtils.newMVStoreException(6, "Store header is corrupt: {0}", new Object[] { this.fileStore });
/*      */     }
/*      */ 
/*      */     
/*  865 */     i = DataUtils.readHexInt(this.storeHeader, "blockSize", 4096);
/*  866 */     if (i != 4096)
/*  867 */       throw DataUtils.newMVStoreException(5, "Block size {0} is currently not supported", new Object[] {
/*      */ 
/*      */             
/*  870 */             Integer.valueOf(i)
/*      */           }); 
/*  872 */     long l1 = DataUtils.readHexLong(this.storeHeader, "format", 1L);
/*  873 */     if (!this.fileStore.isReadOnly()) {
/*  874 */       if (l1 > 2L) {
/*  875 */         throw getUnsupportedWriteFormatException(l1, 2, "The write format {0} is larger than the supported format {1}");
/*      */       }
/*  877 */       if (l1 < 2L) {
/*  878 */         throw getUnsupportedWriteFormatException(l1, 2, "The write format {0} is smaller than the supported format {1}");
/*      */       }
/*      */     } 
/*      */     
/*  882 */     l1 = DataUtils.readHexLong(this.storeHeader, "formatRead", l1);
/*  883 */     if (l1 > 2L)
/*  884 */       throw DataUtils.newMVStoreException(5, "The read format {0} is larger than the supported format {1}", new Object[] {
/*      */ 
/*      */             
/*  887 */             Long.valueOf(l1), Integer.valueOf(2) }); 
/*  888 */     if (l1 < 2L) {
/*  889 */       throw DataUtils.newMVStoreException(5, "The read format {0} is smaller than the supported format {1}", new Object[] {
/*      */ 
/*      */             
/*  892 */             Long.valueOf(l1), Integer.valueOf(2)
/*      */           });
/*      */     }
/*  895 */     bool1 = (bool1 && chunk != null && !this.recoveryMode) ? true : false;
/*  896 */     if (bool1) {
/*  897 */       bool1 = (DataUtils.readHexInt(this.storeHeader, "clean", 0) != 0) ? true : false;
/*      */     }
/*  899 */     this.chunks.clear();
/*  900 */     long l2 = System.currentTimeMillis();
/*      */ 
/*      */     
/*  903 */     int j = 1970 + (int)(l2 / 31557600000L);
/*  904 */     if (j < 2014) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  909 */       this.creationTime = l2 - this.fileStore.getDefaultRetentionTime();
/*  910 */     } else if (l2 < this.creationTime) {
/*      */ 
/*      */       
/*  913 */       this.creationTime = l2;
/*  914 */       this.storeHeader.put("created", Long.valueOf(this.creationTime));
/*      */     } 
/*      */     
/*  917 */     long l3 = this.fileStore.size();
/*  918 */     long l4 = l3 / 4096L;
/*      */     
/*  920 */     Comparator<?> comparator = (paramChunk1, paramChunk2) -> {
/*      */         int i = Long.compare(paramChunk2.version, paramChunk1.version);
/*      */         
/*      */         if (i == 0) {
/*      */           i = Long.compare(paramChunk1.block, paramChunk2.block);
/*      */         }
/*      */         
/*      */         return i;
/*      */       };
/*      */     
/*  930 */     HashMap<Object, Object> hashMap = new HashMap<>();
/*  931 */     if (!bool1) {
/*  932 */       Chunk chunk1 = discoverChunk(l4);
/*  933 */       if (chunk1 != null) {
/*  934 */         l4 = chunk1.block;
/*  935 */         if (chunk == null || chunk1.version > chunk.version) {
/*  936 */           chunk = chunk1;
/*      */         }
/*      */       } 
/*      */       
/*  940 */       if (chunk != null)
/*      */       {
/*      */         while (true) {
/*      */           
/*  944 */           hashMap.put(Long.valueOf(chunk.block), chunk);
/*  945 */           if (chunk.next == 0L || chunk.next >= l4) {
/*      */             break;
/*      */           }
/*      */           
/*  949 */           Chunk chunk2 = readChunkHeaderAndFooter(chunk.next, chunk.id + 1);
/*  950 */           if (chunk2 == null || chunk2.version <= chunk.version) {
/*      */             break;
/*      */           }
/*      */           
/*  954 */           bool1 = false;
/*  955 */           chunk = chunk2;
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/*  960 */     if (bool1) {
/*      */       
/*  962 */       PriorityQueue<Chunk> priorityQueue = new PriorityQueue(20, Collections.reverseOrder(comparator));
/*      */       try {
/*  964 */         setLastChunk(chunk);
/*      */ 
/*      */         
/*  967 */         Cursor<String, String> cursor = this.layout.cursor("chunk.");
/*  968 */         while (cursor.hasNext() && ((String)cursor.next()).startsWith("chunk.")) {
/*  969 */           Chunk chunk2 = Chunk.fromString(cursor.getValue());
/*  970 */           assert chunk2.version <= this.currentVersion;
/*      */ 
/*      */           
/*  973 */           this.chunks.putIfAbsent(Integer.valueOf(chunk2.id), chunk2);
/*  974 */           priorityQueue.offer(chunk2);
/*  975 */           if (priorityQueue.size() == 20) {
/*  976 */             priorityQueue.poll();
/*      */           }
/*      */         } 
/*      */         Chunk chunk1;
/*  980 */         while (bool1 && (chunk1 = priorityQueue.poll()) != null) {
/*  981 */           Chunk chunk2 = readChunkHeaderAndFooter(chunk1.block, chunk1.id);
/*  982 */           bool1 = (chunk2 != null) ? true : false;
/*  983 */           if (bool1) {
/*  984 */             hashMap.put(Long.valueOf(chunk2.block), chunk2);
/*      */           }
/*      */         } 
/*  987 */       } catch (MVStoreException mVStoreException) {
/*  988 */         bool1 = false;
/*      */       } 
/*      */     } 
/*      */     
/*  992 */     if (!bool1) {
/*  993 */       boolean bool = false;
/*  994 */       if (!this.recoveryMode) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1001 */         Chunk[] arrayOfChunk = (Chunk[])hashMap.values().toArray((Object[])new Chunk[0]);
/* 1002 */         Arrays.sort((Object[])arrayOfChunk, comparator);
/* 1003 */         HashMap<Object, Object> hashMap1 = new HashMap<>();
/* 1004 */         for (Chunk chunk1 : arrayOfChunk) {
/* 1005 */           hashMap1.put(Integer.valueOf(chunk1.id), chunk1);
/*      */         }
/* 1007 */         bool = findLastChunkWithCompleteValidChunkSet(arrayOfChunk, (Map)hashMap, (Map)hashMap1, false);
/*      */       } 
/*      */ 
/*      */       
/* 1011 */       if (!bool) {
/*      */ 
/*      */         
/* 1014 */         long l = l4;
/*      */         Chunk chunk1;
/* 1016 */         while ((chunk1 = discoverChunk(l)) != null) {
/* 1017 */           l = chunk1.block;
/* 1018 */           hashMap.put(Long.valueOf(l), chunk1);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1023 */         Chunk[] arrayOfChunk = (Chunk[])hashMap.values().toArray((Object[])new Chunk[0]);
/* 1024 */         Arrays.sort((Object[])arrayOfChunk, comparator);
/* 1025 */         HashMap<Object, Object> hashMap1 = new HashMap<>();
/* 1026 */         for (Chunk chunk2 : arrayOfChunk) {
/* 1027 */           hashMap1.put(Integer.valueOf(chunk2.id), chunk2);
/*      */         }
/* 1029 */         if (!findLastChunkWithCompleteValidChunkSet(arrayOfChunk, (Map)hashMap, (Map)hashMap1, true) && this.lastChunk != null)
/*      */         {
/* 1031 */           throw DataUtils.newMVStoreException(6, "File is corrupted - unable to recover a valid set of chunks", new Object[0]);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1039 */     this.fileStore.clear();
/*      */     
/* 1041 */     for (Chunk chunk1 : this.chunks.values()) {
/* 1042 */       if (chunk1.isSaved()) {
/* 1043 */         long l = chunk1.block * 4096L;
/* 1044 */         int k = chunk1.len * 4096;
/* 1045 */         this.fileStore.markUsed(l, k);
/*      */       } 
/* 1047 */       if (!chunk1.isLive()) {
/* 1048 */         this.deadChunks.offer(chunk1);
/*      */       }
/*      */     } 
/* 1051 */     assert validateFileLength("on open");
/*      */   }
/*      */   
/*      */   private MVStoreException getUnsupportedWriteFormatException(long paramLong, int paramInt, String paramString) {
/* 1055 */     paramLong = DataUtils.readHexLong(this.storeHeader, "formatRead", paramLong);
/* 1056 */     if (paramLong >= 2L && paramLong <= 2L) {
/* 1057 */       paramString = paramString + ", and the file was not opened in read-only mode";
/*      */     }
/* 1059 */     return DataUtils.newMVStoreException(5, paramString, new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean findLastChunkWithCompleteValidChunkSet(Chunk[] paramArrayOfChunk, Map<Long, Chunk> paramMap, Map<Integer, Chunk> paramMap1, boolean paramBoolean) {
/* 1069 */     for (Chunk chunk : paramArrayOfChunk) {
/* 1070 */       boolean bool = true;
/*      */       try {
/* 1072 */         setLastChunk(chunk);
/*      */ 
/*      */         
/* 1075 */         Cursor<String, String> cursor = this.layout.cursor("chunk.");
/* 1076 */         while (cursor.hasNext() && ((String)cursor.next()).startsWith("chunk.")) {
/* 1077 */           Chunk chunk1 = Chunk.fromString(cursor.getValue());
/* 1078 */           assert chunk1.version <= this.currentVersion;
/*      */ 
/*      */           
/* 1081 */           Chunk chunk2 = this.chunks.putIfAbsent(Integer.valueOf(chunk1.id), chunk1);
/* 1082 */           if (chunk2 != null) {
/* 1083 */             chunk1 = chunk2;
/*      */           }
/* 1085 */           assert this.chunks.get(Integer.valueOf(chunk1.id)) == chunk1;
/* 1086 */           if ((chunk2 = paramMap.get(Long.valueOf(chunk1.block))) == null || chunk2.id != chunk1.id) {
/* 1087 */             if ((chunk2 = paramMap1.get(Integer.valueOf(chunk1.id))) != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1095 */               chunk1.block = chunk2.block;
/* 1096 */             } else if (chunk1.isLive() && (paramBoolean || readChunkHeaderAndFooter(chunk1.block, chunk1.id) == null)) {
/*      */ 
/*      */               
/* 1099 */               bool = false;
/*      */               break;
/*      */             } 
/*      */           }
/* 1103 */           if (!chunk1.isLive()) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1108 */             chunk1.block = Long.MAX_VALUE;
/* 1109 */             chunk1.len = Integer.MAX_VALUE;
/* 1110 */             if (chunk1.unused == 0L) {
/* 1111 */               chunk1.unused = this.creationTime;
/*      */             }
/* 1113 */             if (chunk1.unusedAtVersion == 0L) {
/* 1114 */               chunk1.unusedAtVersion = -1L;
/*      */             }
/*      */           } 
/*      */         } 
/* 1118 */       } catch (Exception exception) {
/* 1119 */         bool = false;
/*      */       } 
/* 1121 */       if (bool) {
/* 1122 */         return true;
/*      */       }
/*      */     } 
/* 1125 */     return false;
/*      */   }
/*      */   
/*      */   private void setLastChunk(Chunk paramChunk) {
/* 1129 */     this.chunks.clear();
/* 1130 */     this.lastChunk = paramChunk;
/* 1131 */     this.lastChunkId = 0;
/* 1132 */     this.currentVersion = lastChunkVersion();
/* 1133 */     long l = 0L;
/* 1134 */     int i = 0;
/* 1135 */     if (paramChunk != null) {
/* 1136 */       this.lastChunkId = paramChunk.id;
/* 1137 */       this.currentVersion = paramChunk.version;
/* 1138 */       l = paramChunk.layoutRootPos;
/* 1139 */       i = paramChunk.mapId;
/* 1140 */       this.chunks.put(Integer.valueOf(paramChunk.id), paramChunk);
/*      */     } 
/* 1142 */     this.lastMapId.set(i);
/* 1143 */     this.layout.setRootPos(l, this.currentVersion - 1L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Chunk discoverChunk(long paramLong) {
/* 1154 */     long l = Long.MAX_VALUE;
/* 1155 */     Chunk chunk = null;
/*      */     while (true) {
/* 1157 */       if (paramLong == l) {
/* 1158 */         return chunk;
/*      */       }
/* 1160 */       if (paramLong == 2L) {
/* 1161 */         return null;
/*      */       }
/* 1163 */       Chunk chunk1 = readChunkFooter(paramLong);
/* 1164 */       if (chunk1 != null) {
/*      */ 
/*      */         
/* 1167 */         l = Long.MAX_VALUE;
/* 1168 */         chunk1 = readChunkHeaderOptionally(chunk1.block, chunk1.id);
/* 1169 */         if (chunk1 != null) {
/*      */ 
/*      */           
/* 1172 */           chunk = chunk1;
/* 1173 */           l = chunk1.block;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1180 */       if (--paramLong > l && readChunkHeaderOptionally(paramLong) != null) {
/* 1181 */         l = Long.MAX_VALUE;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Chunk readChunkHeaderAndFooter(long paramLong, int paramInt) {
/* 1196 */     Chunk chunk = readChunkHeaderOptionally(paramLong, paramInt);
/* 1197 */     if (chunk != null) {
/* 1198 */       Chunk chunk1 = readChunkFooter(paramLong + chunk.len);
/* 1199 */       if (chunk1 == null || chunk1.id != paramInt || chunk1.block != chunk.block) {
/* 1200 */         return null;
/*      */       }
/*      */     } 
/* 1203 */     return chunk;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Chunk readChunkFooter(long paramLong) {
/*      */     try {
/* 1216 */       long l = paramLong * 4096L - 128L;
/* 1217 */       if (l < 0L) {
/* 1218 */         return null;
/*      */       }
/* 1220 */       ByteBuffer byteBuffer = this.fileStore.readFully(l, 128);
/* 1221 */       byte[] arrayOfByte = new byte[128];
/* 1222 */       byteBuffer.get(arrayOfByte);
/* 1223 */       HashMap<String, String> hashMap = DataUtils.parseChecksummedMap(arrayOfByte);
/* 1224 */       if (hashMap != null) {
/* 1225 */         return new Chunk(hashMap);
/*      */       }
/* 1227 */     } catch (Exception exception) {}
/*      */ 
/*      */     
/* 1230 */     return null;
/*      */   }
/*      */   
/*      */   private void writeStoreHeader() {
/* 1234 */     Chunk chunk = this.lastChunk;
/* 1235 */     if (chunk != null) {
/* 1236 */       this.storeHeader.put("block", Long.valueOf(chunk.block));
/* 1237 */       this.storeHeader.put("chunk", Integer.valueOf(chunk.id));
/* 1238 */       this.storeHeader.put("version", Long.valueOf(chunk.version));
/*      */     } 
/* 1240 */     StringBuilder stringBuilder = new StringBuilder(112);
/* 1241 */     DataUtils.appendMap(stringBuilder, this.storeHeader);
/* 1242 */     byte[] arrayOfByte = stringBuilder.toString().getBytes(StandardCharsets.ISO_8859_1);
/* 1243 */     int i = DataUtils.getFletcher32(arrayOfByte, 0, arrayOfByte.length);
/* 1244 */     DataUtils.appendMap(stringBuilder, "fletcher", i);
/* 1245 */     stringBuilder.append('\n');
/* 1246 */     arrayOfByte = stringBuilder.toString().getBytes(StandardCharsets.ISO_8859_1);
/* 1247 */     ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
/* 1248 */     byteBuffer.put(arrayOfByte);
/* 1249 */     byteBuffer.position(4096);
/* 1250 */     byteBuffer.put(arrayOfByte);
/* 1251 */     byteBuffer.rewind();
/* 1252 */     write(0L, byteBuffer);
/*      */   }
/*      */   
/*      */   private void write(long paramLong, ByteBuffer paramByteBuffer) {
/*      */     try {
/* 1257 */       this.fileStore.writeFully(paramLong, paramByteBuffer);
/* 1258 */     } catch (MVStoreException mVStoreException) {
/* 1259 */       panic(mVStoreException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() {
/* 1268 */     closeStore(true, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close(int paramInt) {
/* 1279 */     closeStore(true, paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void closeImmediately() {
/*      */     try {
/* 1289 */       closeStore(false, 0);
/* 1290 */     } catch (Throwable throwable) {
/* 1291 */       handleException(throwable);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void closeStore(boolean paramBoolean, int paramInt) {
/* 1299 */     while (!isClosed()) {
/* 1300 */       stopBackgroundThread(paramBoolean);
/* 1301 */       this.storeLock.lock();
/*      */       try {
/* 1303 */         if (this.state == 0) {
/* 1304 */           this.state = 1;
/*      */           try {
/*      */             try {
/* 1307 */               if (paramBoolean && this.fileStore != null && !this.fileStore.isReadOnly()) {
/* 1308 */                 for (MVMap<?, ?> mVMap : this.maps.values()) {
/* 1309 */                   if (mVMap.isClosed()) {
/* 1310 */                     deregisterMapRoot(mVMap.getId());
/*      */                   }
/*      */                 } 
/* 1313 */                 setRetentionTime(0);
/* 1314 */                 commit();
/* 1315 */                 if (paramInt > 0) {
/* 1316 */                   compactFile(paramInt);
/* 1317 */                 } else if (paramInt < 0) {
/* 1318 */                   doMaintenance(this.autoCompactFillRate);
/*      */                 } 
/*      */                 
/* 1321 */                 this.saveChunkLock.lock();
/*      */                 try {
/* 1323 */                   shrinkFileIfPossible(0);
/* 1324 */                   this.storeHeader.put("clean", Integer.valueOf(1));
/* 1325 */                   writeStoreHeader();
/* 1326 */                   sync();
/* 1327 */                   assert validateFileLength("on close");
/*      */                 } finally {
/* 1329 */                   this.saveChunkLock.unlock();
/*      */                 } 
/*      */               } 
/*      */               
/* 1333 */               this.state = 2;
/*      */ 
/*      */ 
/*      */               
/* 1337 */               clearCaches();
/* 1338 */               for (MVMap mVMap : new ArrayList(this.maps.values())) {
/* 1339 */                 mVMap.close();
/*      */               }
/* 1341 */               this.chunks.clear();
/* 1342 */               this.maps.clear();
/*      */             } finally {
/* 1344 */               if (this.fileStore != null && !this.fileStoreIsProvided) {
/* 1345 */                 this.fileStore.close();
/*      */               }
/*      */             } 
/*      */           } finally {
/* 1349 */             this.state = 3;
/*      */           } 
/*      */         } 
/*      */       } finally {
/* 1353 */         this.storeLock.unlock();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void shutdownExecutor(ThreadPoolExecutor paramThreadPoolExecutor) {
/* 1359 */     if (paramThreadPoolExecutor != null) {
/* 1360 */       paramThreadPoolExecutor.shutdown();
/*      */       try {
/* 1362 */         if (paramThreadPoolExecutor.awaitTermination(1000L, TimeUnit.MILLISECONDS)) {
/*      */           return;
/*      */         }
/* 1365 */       } catch (InterruptedException interruptedException) {}
/* 1366 */       paramThreadPoolExecutor.shutdownNow();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Chunk getChunk(long paramLong) {
/* 1377 */     int i = DataUtils.getPageChunkId(paramLong);
/* 1378 */     Chunk chunk = this.chunks.get(Integer.valueOf(i));
/* 1379 */     if (chunk == null) {
/* 1380 */       checkOpen();
/* 1381 */       String str = this.layout.get(Chunk.getMetaKey(i));
/* 1382 */       if (str == null)
/* 1383 */         throw DataUtils.newMVStoreException(9, "Chunk {0} not found", new Object[] {
/*      */               
/* 1385 */               Integer.valueOf(i)
/*      */             }); 
/* 1387 */       chunk = Chunk.fromString(str);
/* 1388 */       if (!chunk.isSaved())
/* 1389 */         throw DataUtils.newMVStoreException(6, "Chunk {0} is invalid", new Object[] {
/*      */               
/* 1391 */               Integer.valueOf(i)
/*      */             }); 
/* 1393 */       this.chunks.put(Integer.valueOf(chunk.id), chunk);
/*      */     } 
/* 1395 */     return chunk;
/*      */   }
/*      */   
/*      */   private void setWriteVersion(long paramLong) {
/* 1399 */     for (Iterator<MVMap> iterator = this.maps.values().iterator(); iterator.hasNext(); ) {
/* 1400 */       MVMap<String, String> mVMap = iterator.next();
/* 1401 */       assert mVMap != this.layout && mVMap != this.meta;
/* 1402 */       if (mVMap.setWriteVersion(paramLong) == null) {
/* 1403 */         iterator.remove();
/*      */       }
/*      */     } 
/* 1406 */     this.meta.setWriteVersion(paramLong);
/* 1407 */     this.layout.setWriteVersion(paramLong);
/* 1408 */     onVersionChange(paramLong);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long tryCommit() {
/* 1420 */     return tryCommit(paramMVStore -> true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long tryCommit(Predicate<MVStore> paramPredicate) {
/* 1427 */     if ((!this.storeLock.isHeldByCurrentThread() || this.currentStoreVersion < 0L) && this.storeLock
/* 1428 */       .tryLock()) {
/*      */       try {
/* 1430 */         if (paramPredicate.test(this)) {
/* 1431 */           store(false);
/*      */         }
/*      */       } finally {
/* 1434 */         unlockAndCheckPanicCondition();
/*      */       } 
/*      */     }
/* 1437 */     return this.currentVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long commit() {
/* 1457 */     return commit(paramMVStore -> true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long commit(Predicate<MVStore> paramPredicate) {
/* 1464 */     if (!this.storeLock.isHeldByCurrentThread() || this.currentStoreVersion < 0L) {
/* 1465 */       this.storeLock.lock();
/*      */       try {
/* 1467 */         if (paramPredicate.test(this)) {
/* 1468 */           store(true);
/*      */         }
/*      */       } finally {
/* 1471 */         unlockAndCheckPanicCondition();
/*      */       } 
/*      */     } 
/* 1474 */     return this.currentVersion;
/*      */   }
/*      */   
/*      */   private void store(boolean paramBoolean) {
/* 1478 */     assert this.storeLock.isHeldByCurrentThread();
/* 1479 */     assert !this.saveChunkLock.isHeldByCurrentThread();
/* 1480 */     if (isOpenOrStopping() && 
/* 1481 */       hasUnsavedChanges()) {
/* 1482 */       dropUnusedChunks();
/*      */       try {
/* 1484 */         this.currentStoreVersion = this.currentVersion;
/* 1485 */         if (this.fileStore == null) {
/*      */           
/* 1487 */           this.currentVersion++;
/* 1488 */           setWriteVersion(this.currentVersion);
/* 1489 */           this.metaChanged = false;
/*      */         } else {
/* 1491 */           if (this.fileStore.isReadOnly()) {
/* 1492 */             throw DataUtils.newMVStoreException(2, "This store is read-only", new Object[0]);
/*      */           }
/*      */           
/* 1495 */           storeNow(paramBoolean, 0L, () -> Long.valueOf(this.reuseSpace ? 0L : getAfterLastBlock()));
/*      */         }
/*      */       
/*      */       } finally {
/*      */         
/* 1500 */         this.currentStoreVersion = -1L;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void storeNow(boolean paramBoolean, long paramLong, Supplier<Long> paramSupplier) {
/*      */     try {
/* 1508 */       this.lastCommitTime = getTimeSinceCreation();
/* 1509 */       int i = this.unsavedMemory;
/*      */ 
/*      */       
/* 1512 */       long l = ++this.currentVersion;
/* 1513 */       ArrayList<Page<?, ?>> arrayList = collectChangedMapRoots(l);
/*      */       
/* 1515 */       if (!$assertionsDisabled && !this.storeLock.isHeldByCurrentThread()) throw new AssertionError(); 
/* 1516 */       submitOrRun(this.serializationExecutor, () -> serializeAndStore(paramBoolean, paramLong1, paramSupplier, paramArrayList, this.lastCommitTime, paramLong2), paramBoolean);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1523 */       this.saveNeeded = false;
/* 1524 */       this.unsavedMemory = Math.max(0, this.unsavedMemory - i);
/* 1525 */     } catch (MVStoreException mVStoreException) {
/* 1526 */       panic(mVStoreException);
/* 1527 */     } catch (Throwable throwable) {
/* 1528 */       panic(DataUtils.newMVStoreException(3, "{0}", new Object[] { throwable.toString(), throwable }));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void submitOrRun(ThreadPoolExecutor paramThreadPoolExecutor, Runnable paramRunnable, boolean paramBoolean) throws ExecutionException {
/* 1535 */     if (paramThreadPoolExecutor != null) {
/*      */       try {
/* 1537 */         Future<?> future = paramThreadPoolExecutor.submit(paramRunnable);
/* 1538 */         if (paramBoolean || paramThreadPoolExecutor.getQueue().size() > 1) {
/*      */           try {
/* 1540 */             future.get();
/* 1541 */           } catch (InterruptedException interruptedException) {}
/*      */         }
/*      */         return;
/* 1544 */       } catch (RejectedExecutionException rejectedExecutionException) {
/* 1545 */         assert paramThreadPoolExecutor.isShutdown();
/* 1546 */         shutdownExecutor(paramThreadPoolExecutor);
/*      */       } 
/*      */     }
/* 1549 */     paramRunnable.run();
/*      */   }
/*      */   
/*      */   private ArrayList<Page<?, ?>> collectChangedMapRoots(long paramLong) {
/* 1553 */     long l = paramLong - 2L;
/* 1554 */     ArrayList<Page> arrayList = new ArrayList();
/* 1555 */     for (Iterator<MVMap> iterator = this.maps.values().iterator(); iterator.hasNext(); ) {
/* 1556 */       MVMap mVMap = iterator.next();
/* 1557 */       RootReference rootReference1 = mVMap.setWriteVersion(paramLong);
/* 1558 */       if (rootReference1 == null) {
/* 1559 */         iterator.remove(); continue;
/* 1560 */       }  if (mVMap.getCreateVersion() < paramLong && 
/* 1561 */         !mVMap.isVolatile() && mVMap
/* 1562 */         .hasChangesSince(l)) {
/* 1563 */         assert rootReference1.version <= paramLong : rootReference1.version + " > " + paramLong;
/* 1564 */         Page page = rootReference1.root;
/* 1565 */         if (!page.isSaved() || page
/*      */ 
/*      */ 
/*      */           
/* 1569 */           .isLeaf()) {
/* 1570 */           arrayList.add(page);
/*      */         }
/*      */       } 
/*      */     } 
/* 1574 */     RootReference<String, String> rootReference = this.meta.setWriteVersion(paramLong);
/* 1575 */     if (this.meta.hasChangesSince(l) || this.metaChanged) {
/* 1576 */       assert rootReference != null && rootReference.version <= paramLong : (rootReference == null) ? "null" : (rootReference.version + " > " + paramLong);
/*      */       
/* 1578 */       Page<String, String> page = rootReference.root;
/* 1579 */       if (!page.isSaved() || page
/*      */ 
/*      */ 
/*      */         
/* 1583 */         .isLeaf()) {
/* 1584 */         arrayList.add(page);
/*      */       }
/*      */     } 
/* 1587 */     return (ArrayList)arrayList;
/*      */   }
/*      */ 
/*      */   
/*      */   private void serializeAndStore(boolean paramBoolean, long paramLong1, Supplier<Long> paramSupplier, ArrayList<Page<?, ?>> paramArrayList, long paramLong2, long paramLong3) {
/* 1592 */     this.serializationLock.lock();
/*      */     try {
/* 1594 */       Chunk chunk = createChunk(paramLong2, paramLong3);
/* 1595 */       this.chunks.put(Integer.valueOf(chunk.id), chunk);
/* 1596 */       WriteBuffer writeBuffer = getWriteBuffer();
/* 1597 */       serializeToBuffer(writeBuffer, paramArrayList, chunk, paramLong1, paramSupplier);
/*      */       
/* 1599 */       submitOrRun(this.bufferSaveExecutor, () -> storeBuffer(paramChunk, paramWriteBuffer, paramArrayList), paramBoolean);
/*      */     }
/* 1601 */     catch (MVStoreException mVStoreException) {
/* 1602 */       panic(mVStoreException);
/* 1603 */     } catch (Throwable throwable) {
/* 1604 */       panic(DataUtils.newMVStoreException(3, "{0}", new Object[] { throwable.toString(), throwable }));
/*      */     } finally {
/* 1606 */       this.serializationLock.unlock();
/*      */     } 
/*      */   }
/*      */   
/*      */   private Chunk createChunk(long paramLong1, long paramLong2) {
/* 1611 */     int j, i = this.lastChunkId;
/* 1612 */     if (i != 0) {
/* 1613 */       i &= 0x3FFFFFF;
/* 1614 */       Chunk chunk1 = this.chunks.get(Integer.valueOf(i));
/* 1615 */       assert chunk1 != null;
/* 1616 */       assert chunk1.isSaved();
/* 1617 */       assert chunk1.version + 1L == paramLong2 : chunk1.version + " " + paramLong2;
/*      */ 
/*      */ 
/*      */       
/* 1621 */       this.layout.put(Chunk.getMetaKey(i), chunk1.asString());
/*      */       
/* 1623 */       paramLong1 = Math.max(chunk1.time, paramLong1);
/*      */     } 
/*      */     
/*      */     while (true) {
/* 1627 */       j = ++this.lastChunkId & 0x3FFFFFF;
/* 1628 */       Chunk chunk1 = this.chunks.get(Integer.valueOf(j));
/* 1629 */       if (chunk1 == null) {
/*      */         break;
/*      */       }
/* 1632 */       if (!chunk1.isSaved()) {
/* 1633 */         MVStoreException mVStoreException = DataUtils.newMVStoreException(3, "Last block {0} not stored, possibly due to out-of-memory", new Object[] { chunk1 });
/*      */ 
/*      */         
/* 1636 */         panic(mVStoreException);
/*      */       } 
/*      */     } 
/* 1639 */     Chunk chunk = new Chunk(j);
/* 1640 */     chunk.pageCount = 0;
/* 1641 */     chunk.pageCountLive = 0;
/* 1642 */     chunk.maxLen = 0L;
/* 1643 */     chunk.maxLenLive = 0L;
/* 1644 */     chunk.layoutRootPos = Long.MAX_VALUE;
/* 1645 */     chunk.block = Long.MAX_VALUE;
/* 1646 */     chunk.len = Integer.MAX_VALUE;
/* 1647 */     chunk.time = paramLong1;
/* 1648 */     chunk.version = paramLong2;
/* 1649 */     chunk.next = Long.MAX_VALUE;
/* 1650 */     chunk.occupancy = new BitSet();
/* 1651 */     return chunk;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void serializeToBuffer(WriteBuffer paramWriteBuffer, ArrayList<Page<?, ?>> paramArrayList, Chunk paramChunk, long paramLong, Supplier<Long> paramSupplier) {
/* 1657 */     paramChunk.writeChunkHeader(paramWriteBuffer, 0);
/* 1658 */     int i = paramWriteBuffer.position() + 44;
/* 1659 */     paramWriteBuffer.position(i);
/*      */     
/* 1661 */     long l = paramChunk.version;
/* 1662 */     ArrayList<Long> arrayList = new ArrayList();
/* 1663 */     for (Page<?, ?> page1 : paramArrayList) {
/* 1664 */       String str = MVMap.getMapRootKey(page1.getMapId());
/* 1665 */       if (page1.getTotalCount() == 0L) {
/* 1666 */         this.layout.remove(str); continue;
/*      */       } 
/* 1668 */       page1.writeUnsavedRecursive(paramChunk, paramWriteBuffer, arrayList);
/* 1669 */       long l1 = page1.getPos();
/* 1670 */       this.layout.put(str, Long.toHexString(l1));
/*      */     } 
/*      */ 
/*      */     
/* 1674 */     acceptChunkOccupancyChanges(paramChunk.time, l);
/*      */     
/* 1676 */     RootReference<String, String> rootReference = this.layout.setWriteVersion(l);
/* 1677 */     assert rootReference != null;
/* 1678 */     assert rootReference.version == l : rootReference.version + " != " + l;
/* 1679 */     this.metaChanged = false;
/*      */     
/* 1681 */     acceptChunkOccupancyChanges(paramChunk.time, l);
/*      */     
/* 1683 */     onVersionChange(l);
/*      */     
/* 1685 */     Page<String, String> page = rootReference.root;
/* 1686 */     page.writeUnsavedRecursive(paramChunk, paramWriteBuffer, arrayList);
/* 1687 */     paramChunk.layoutRootPos = page.getPos();
/* 1688 */     paramArrayList.add(page);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1693 */     paramChunk.mapId = this.lastMapId.get();
/*      */     
/* 1695 */     paramChunk.tocPos = paramWriteBuffer.position();
/* 1696 */     long[] arrayOfLong = new long[arrayList.size()];
/* 1697 */     byte b = 0;
/* 1698 */     for (Iterator<Long> iterator = arrayList.iterator(); iterator.hasNext(); ) { long l1 = ((Long)iterator.next()).longValue();
/* 1699 */       arrayOfLong[b++] = l1;
/* 1700 */       paramWriteBuffer.putLong(l1);
/* 1701 */       if (DataUtils.isLeafPosition(l1)) {
/* 1702 */         this.leafCount++; continue;
/*      */       } 
/* 1704 */       this.nonLeafCount++; }
/*      */ 
/*      */     
/* 1707 */     this.chunksToC.put(paramChunk.id, arrayOfLong);
/* 1708 */     int j = paramWriteBuffer.position();
/*      */ 
/*      */     
/* 1711 */     int k = MathUtils.roundUpInt(j + 128, 4096);
/*      */     
/* 1713 */     paramWriteBuffer.limit(k);
/*      */     
/* 1715 */     this.saveChunkLock.lock();
/*      */     try {
/* 1717 */       Long long_ = paramSupplier.get();
/* 1718 */       long l1 = this.fileStore.allocate(paramWriteBuffer.limit(), paramLong, long_.longValue());
/* 1719 */       paramChunk.len = paramWriteBuffer.limit() / 4096;
/* 1720 */       paramChunk.block = l1 / 4096L;
/* 1721 */       assert validateFileLength(paramChunk.asString());
/*      */       
/* 1723 */       if (paramLong > 0L || long_.longValue() == paramLong) {
/* 1724 */         paramChunk.next = this.fileStore.predictAllocation(paramChunk.len, 0L, 0L);
/*      */       } else {
/*      */         
/* 1727 */         paramChunk.next = 0L;
/*      */       } 
/* 1729 */       assert paramChunk.pageCountLive == paramChunk.pageCount : paramChunk;
/* 1730 */       assert paramChunk.occupancy.cardinality() == 0 : paramChunk;
/*      */       
/* 1732 */       paramWriteBuffer.position(0);
/* 1733 */       assert paramChunk.pageCountLive == paramChunk.pageCount : paramChunk;
/* 1734 */       assert paramChunk.occupancy.cardinality() == 0 : paramChunk;
/* 1735 */       paramChunk.writeChunkHeader(paramWriteBuffer, i);
/*      */       
/* 1737 */       paramWriteBuffer.position(paramWriteBuffer.limit() - 128);
/* 1738 */       paramWriteBuffer.put(paramChunk.getFooterBytes());
/*      */     } finally {
/* 1740 */       this.saveChunkLock.unlock();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void storeBuffer(Chunk paramChunk, WriteBuffer paramWriteBuffer, ArrayList<Page<?, ?>> paramArrayList) {
/* 1745 */     this.saveChunkLock.lock();
/*      */     try {
/* 1747 */       paramWriteBuffer.position(0);
/* 1748 */       long l = paramChunk.block * 4096L;
/* 1749 */       write(l, paramWriteBuffer.getBuffer());
/* 1750 */       releaseWriteBuffer(paramWriteBuffer);
/*      */ 
/*      */       
/* 1753 */       boolean bool = (l + paramWriteBuffer.limit() >= this.fileStore.size()) ? true : false;
/* 1754 */       boolean bool1 = isWriteStoreHeader(paramChunk, bool);
/* 1755 */       this.lastChunk = paramChunk;
/* 1756 */       if (bool1) {
/* 1757 */         writeStoreHeader();
/*      */       }
/* 1759 */       if (!bool)
/*      */       {
/* 1761 */         shrinkFileIfPossible(1);
/*      */       }
/* 1763 */     } catch (MVStoreException mVStoreException) {
/* 1764 */       panic(mVStoreException);
/* 1765 */     } catch (Throwable throwable) {
/* 1766 */       panic(DataUtils.newMVStoreException(3, "{0}", new Object[] { throwable.toString(), throwable }));
/*      */     } finally {
/* 1768 */       this.saveChunkLock.unlock();
/*      */     } 
/*      */     
/* 1771 */     for (Page<?, ?> page : paramArrayList) {
/* 1772 */       page.releaseSavedPages();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isWriteStoreHeader(Chunk paramChunk, boolean paramBoolean) {
/* 1778 */     boolean bool = false;
/* 1779 */     if (!paramBoolean) {
/* 1780 */       Chunk chunk = this.lastChunk;
/* 1781 */       if (chunk == null) {
/* 1782 */         bool = true;
/* 1783 */       } else if (chunk.next != paramChunk.block) {
/*      */         
/* 1785 */         bool = true;
/*      */       } else {
/* 1787 */         long l = DataUtils.readHexLong(this.storeHeader, "version", 0L);
/* 1788 */         if (chunk.version - l > 20L) {
/*      */           
/* 1790 */           bool = true;
/*      */         } else {
/* 1792 */           int i = DataUtils.readHexInt(this.storeHeader, "chunk", 0);
/* 1793 */           for (; !bool && i <= chunk.id; i++)
/*      */           {
/*      */             
/* 1796 */             bool = !this.chunks.containsKey(Integer.valueOf(i)) ? true : false;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1802 */     if (this.storeHeader.remove("clean") != null) {
/* 1803 */       bool = true;
/*      */     }
/* 1805 */     return bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private WriteBuffer getWriteBuffer() {
/* 1815 */     WriteBuffer writeBuffer = this.writeBufferPool.poll();
/* 1816 */     if (writeBuffer != null) {
/* 1817 */       writeBuffer.clear();
/*      */     } else {
/* 1819 */       writeBuffer = new WriteBuffer();
/*      */     } 
/* 1821 */     return writeBuffer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void releaseWriteBuffer(WriteBuffer paramWriteBuffer) {
/* 1831 */     if (paramWriteBuffer.capacity() <= 4194304) {
/* 1832 */       this.writeBufferPool.offer(paramWriteBuffer);
/*      */     }
/*      */   }
/*      */   
/*      */   private static boolean canOverwriteChunk(Chunk paramChunk, long paramLong) {
/* 1837 */     return (!paramChunk.isLive() && paramChunk.unusedAtVersion < paramLong);
/*      */   }
/*      */   
/*      */   private boolean isSeasonedChunk(Chunk paramChunk, long paramLong) {
/* 1841 */     return (this.retentionTime < 0 || paramChunk.time + this.retentionTime <= paramLong);
/*      */   }
/*      */   
/*      */   private long getTimeSinceCreation() {
/* 1845 */     return Math.max(0L, getTimeAbsolute() - this.creationTime);
/*      */   }
/*      */   
/*      */   private long getTimeAbsolute() {
/* 1849 */     long l = System.currentTimeMillis();
/* 1850 */     if (this.lastTimeAbsolute != 0L && l < this.lastTimeAbsolute) {
/*      */ 
/*      */ 
/*      */       
/* 1854 */       l = this.lastTimeAbsolute;
/*      */     } else {
/* 1856 */       this.lastTimeAbsolute = l;
/*      */     } 
/* 1858 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void acceptChunkOccupancyChanges(long paramLong1, long paramLong2) {
/* 1868 */     assert this.serializationLock.isHeldByCurrentThread();
/* 1869 */     if (this.lastChunk != null) {
/* 1870 */       HashSet<Chunk> hashSet = new HashSet();
/*      */       while (true) {
/*      */         RemovedPageInfo removedPageInfo;
/* 1873 */         if ((removedPageInfo = this.removedPages.peek()) != null && removedPageInfo.version < paramLong2) {
/* 1874 */           removedPageInfo = this.removedPages.poll();
/* 1875 */           assert removedPageInfo != null;
/* 1876 */           assert removedPageInfo.version < paramLong2 : removedPageInfo + " < " + paramLong2;
/* 1877 */           int i = removedPageInfo.getPageChunkId();
/* 1878 */           Chunk chunk = this.chunks.get(Integer.valueOf(i));
/* 1879 */           assert !isOpen() || chunk != null : i;
/* 1880 */           if (chunk != null) {
/* 1881 */             hashSet.add(chunk);
/* 1882 */             if (chunk.accountForRemovedPage(removedPageInfo.getPageNo(), removedPageInfo.getPageLength(), removedPageInfo
/* 1883 */                 .isPinned(), paramLong1, removedPageInfo.version))
/* 1884 */               this.deadChunks.offer(chunk); 
/*      */           } 
/*      */           continue;
/*      */         } 
/* 1888 */         if (hashSet.isEmpty()) {
/*      */           return;
/*      */         }
/* 1891 */         for (Chunk chunk : hashSet) {
/* 1892 */           int i = chunk.id;
/* 1893 */           this.layout.put(Chunk.getMetaKey(i), chunk.asString());
/*      */         } 
/* 1895 */         hashSet.clear();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void shrinkFileIfPossible(int paramInt) {
/* 1907 */     assert this.saveChunkLock.isHeldByCurrentThread();
/* 1908 */     if (this.fileStore.isReadOnly()) {
/*      */       return;
/*      */     }
/* 1911 */     long l1 = getFileLengthInUse();
/* 1912 */     long l2 = this.fileStore.size();
/* 1913 */     if (l1 >= l2) {
/*      */       return;
/*      */     }
/* 1916 */     if (paramInt > 0 && l2 - l1 < 4096L) {
/*      */       return;
/*      */     }
/* 1919 */     int i = (int)(100L - l1 * 100L / l2);
/* 1920 */     if (i < paramInt) {
/*      */       return;
/*      */     }
/* 1923 */     if (isOpenOrStopping()) {
/* 1924 */       sync();
/*      */     }
/* 1926 */     this.fileStore.truncate(l1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long getFileLengthInUse() {
/* 1935 */     assert this.saveChunkLock.isHeldByCurrentThread();
/* 1936 */     long l = this.fileStore.getFileLengthInUse();
/* 1937 */     assert l == measureFileLengthInUse() : l + " != " + measureFileLengthInUse();
/* 1938 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long getAfterLastBlock() {
/* 1948 */     assert this.saveChunkLock.isHeldByCurrentThread();
/* 1949 */     return this.fileStore.getAfterLastBlock();
/*      */   }
/*      */   
/*      */   private long measureFileLengthInUse() {
/* 1953 */     assert this.saveChunkLock.isHeldByCurrentThread();
/* 1954 */     long l = 2L;
/* 1955 */     for (Chunk chunk : this.chunks.values()) {
/* 1956 */       if (chunk.isSaved()) {
/* 1957 */         l = Math.max(l, chunk.block + chunk.len);
/*      */       }
/*      */     } 
/* 1960 */     return l * 4096L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasUnsavedChanges() {
/* 1969 */     if (this.metaChanged) {
/* 1970 */       return true;
/*      */     }
/* 1972 */     long l = this.currentVersion - 1L;
/* 1973 */     for (MVMap<?, ?> mVMap : this.maps.values()) {
/* 1974 */       if (!mVMap.isClosed() && 
/* 1975 */         mVMap.hasChangesSince(l)) {
/* 1976 */         return true;
/*      */       }
/*      */     } 
/*      */     
/* 1980 */     return (this.layout.hasChangesSince(l) && l > -1L);
/*      */   }
/*      */   
/*      */   private Chunk readChunkHeader(long paramLong) {
/* 1984 */     long l = paramLong * 4096L;
/* 1985 */     ByteBuffer byteBuffer = this.fileStore.readFully(l, 1024);
/* 1986 */     return Chunk.readChunkHeader(byteBuffer, l);
/*      */   }
/*      */   
/*      */   private Chunk readChunkHeaderOptionally(long paramLong) {
/*      */     try {
/* 1991 */       Chunk chunk = readChunkHeader(paramLong);
/* 1992 */       return (chunk.block != paramLong) ? null : chunk;
/* 1993 */     } catch (Exception exception) {
/* 1994 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   private Chunk readChunkHeaderOptionally(long paramLong, int paramInt) {
/* 1999 */     Chunk chunk = readChunkHeaderOptionally(paramLong);
/* 2000 */     return (chunk == null || chunk.id != paramInt) ? null : chunk;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void compactMoveChunks() {
/* 2007 */     compactMoveChunks(100, Long.MAX_VALUE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean compactMoveChunks(int paramInt, long paramLong) {
/* 2022 */     boolean bool = false;
/* 2023 */     this.storeLock.lock();
/*      */     try {
/* 2025 */       checkOpen();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2030 */       submitOrRun(this.serializationExecutor, () -> {  }true);
/* 2031 */       this.serializationLock.lock();
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 2036 */         submitOrRun(this.bufferSaveExecutor, () -> {  }true);
/* 2037 */         this.saveChunkLock.lock();
/*      */         try {
/* 2039 */           if (this.lastChunk != null && this.reuseSpace && getFillRate() <= paramInt) {
/* 2040 */             bool = compactMoveChunks(paramLong);
/*      */           }
/*      */         } finally {
/* 2043 */           this.saveChunkLock.unlock();
/*      */         } 
/*      */       } finally {
/* 2046 */         this.serializationLock.unlock();
/*      */       } 
/* 2048 */     } catch (MVStoreException mVStoreException) {
/* 2049 */       panic(mVStoreException);
/* 2050 */     } catch (Throwable throwable) {
/* 2051 */       panic(DataUtils.newMVStoreException(3, "{0}", new Object[] { throwable
/* 2052 */               .toString(), throwable }));
/*      */     } finally {
/* 2054 */       unlockAndCheckPanicCondition();
/*      */     } 
/* 2056 */     return bool;
/*      */   }
/*      */   
/*      */   private boolean compactMoveChunks(long paramLong) {
/* 2060 */     assert this.storeLock.isHeldByCurrentThread();
/* 2061 */     dropUnusedChunks();
/* 2062 */     long l = this.fileStore.getFirstFree() / 4096L;
/* 2063 */     Iterable<Chunk> iterable = findChunksToMove(l, paramLong);
/* 2064 */     if (iterable == null) {
/* 2065 */       return false;
/*      */     }
/* 2067 */     compactMoveChunks(iterable);
/* 2068 */     return true;
/*      */   }
/*      */   
/*      */   private Iterable<Chunk> findChunksToMove(long paramLong1, long paramLong2) {
/* 2072 */     long l = paramLong2 / 4096L;
/* 2073 */     ArrayList<Chunk> arrayList = null;
/* 2074 */     if (l > 0L) {
/* 2075 */       PriorityQueue<Chunk> priorityQueue = new PriorityQueue(this.chunks.size() / 2 + 1, (paramChunk1, paramChunk2) -> {
/*      */             int i = Integer.compare(paramChunk2.collectPriority, paramChunk1.collectPriority);
/*      */ 
/*      */ 
/*      */             
/*      */             return (i != 0) ? i : Long.signum(paramChunk2.block - paramChunk1.block);
/*      */           });
/*      */ 
/*      */ 
/*      */       
/* 2085 */       long l1 = 0L;
/* 2086 */       for (Chunk chunk : this.chunks.values()) {
/* 2087 */         if (chunk.isSaved() && chunk.block > paramLong1) {
/* 2088 */           chunk.collectPriority = getMovePriority(chunk);
/* 2089 */           priorityQueue.offer(chunk);
/* 2090 */           l1 += chunk.len;
/* 2091 */           while (l1 > l) {
/* 2092 */             Chunk chunk1 = priorityQueue.poll();
/* 2093 */             if (chunk1 == null) {
/*      */               break;
/*      */             }
/* 2096 */             l1 -= chunk1.len;
/*      */           } 
/*      */         } 
/*      */       } 
/* 2100 */       if (!priorityQueue.isEmpty()) {
/* 2101 */         ArrayList<Chunk> arrayList1 = new ArrayList<>(priorityQueue);
/* 2102 */         arrayList1.sort(Chunk.PositionComparator.INSTANCE);
/* 2103 */         arrayList = arrayList1;
/*      */       } 
/*      */     } 
/* 2106 */     return arrayList;
/*      */   }
/*      */   
/*      */   private int getMovePriority(Chunk paramChunk) {
/* 2110 */     return this.fileStore.getMovePriority((int)paramChunk.block);
/*      */   }
/*      */   
/*      */   private void compactMoveChunks(Iterable<Chunk> paramIterable) {
/* 2114 */     assert this.storeLock.isHeldByCurrentThread();
/* 2115 */     assert this.serializationLock.isHeldByCurrentThread();
/* 2116 */     assert this.saveChunkLock.isHeldByCurrentThread();
/* 2117 */     if (paramIterable != null) {
/*      */ 
/*      */ 
/*      */       
/* 2121 */       writeStoreHeader();
/* 2122 */       sync();
/*      */       
/* 2124 */       Iterator<Chunk> iterator = paramIterable.iterator();
/* 2125 */       assert iterator.hasNext();
/* 2126 */       long l1 = ((Chunk)iterator.next()).block;
/* 2127 */       long l2 = getAfterLastBlock();
/*      */ 
/*      */ 
/*      */       
/* 2131 */       for (Chunk chunk1 : paramIterable) {
/* 2132 */         moveChunk(chunk1, l1, l2);
/*      */       }
/*      */       
/* 2135 */       store(l1, l2);
/* 2136 */       sync();
/*      */       
/* 2138 */       Chunk chunk = this.lastChunk;
/* 2139 */       assert chunk != null;
/* 2140 */       long l3 = getAfterLastBlock();
/*      */       
/* 2142 */       boolean bool1 = (chunk.block < l1) ? true : false;
/* 2143 */       boolean bool2 = !bool1 ? true : false;
/*      */ 
/*      */ 
/*      */       
/* 2147 */       for (Chunk chunk1 : paramIterable) {
/* 2148 */         if (chunk1.block >= l2 && 
/* 2149 */           moveChunk(chunk1, l2, l3)) {
/* 2150 */           assert chunk1.block < l2;
/* 2151 */           bool2 = true;
/*      */         } 
/*      */       } 
/* 2154 */       assert l3 >= getAfterLastBlock();
/*      */       
/* 2156 */       if (bool2) {
/* 2157 */         boolean bool = moveChunkInside(chunk, l2);
/*      */ 
/*      */         
/* 2160 */         store(l2, l3);
/* 2161 */         sync();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2167 */         long l = (bool || bool1) ? l3 : chunk.block;
/*      */         
/* 2169 */         bool = (!bool && moveChunkInside(chunk, l));
/* 2170 */         if (moveChunkInside(this.lastChunk, l) || bool) {
/* 2171 */           store(l, -1L);
/*      */         }
/*      */       } 
/*      */       
/* 2175 */       shrinkFileIfPossible(0);
/* 2176 */       sync();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void store(long paramLong1, long paramLong2) {
/* 2181 */     this.saveChunkLock.unlock();
/*      */     try {
/* 2183 */       this.serializationLock.unlock();
/*      */       try {
/* 2185 */         storeNow(true, paramLong1, () -> Long.valueOf(paramLong));
/*      */       } finally {
/* 2187 */         this.serializationLock.lock();
/*      */       } 
/*      */     } finally {
/* 2190 */       this.saveChunkLock.lock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean moveChunkInside(Chunk paramChunk, long paramLong) {
/* 2197 */     boolean bool = (paramChunk.block >= paramLong && this.fileStore.predictAllocation(paramChunk.len, paramLong, -1L) < paramLong && moveChunk(paramChunk, paramLong, -1L)) ? true : false;
/* 2198 */     assert !bool || paramChunk.block + paramChunk.len <= paramLong;
/* 2199 */     return bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean moveChunk(Chunk paramChunk, long paramLong1, long paramLong2) {
/*      */     long l2;
/* 2216 */     if (!this.chunks.containsKey(Integer.valueOf(paramChunk.id))) {
/* 2217 */       return false;
/*      */     }
/* 2219 */     long l1 = paramChunk.block * 4096L;
/* 2220 */     int i = paramChunk.len * 4096;
/*      */     
/* 2222 */     WriteBuffer writeBuffer = getWriteBuffer();
/*      */     try {
/* 2224 */       writeBuffer.limit(i);
/* 2225 */       ByteBuffer byteBuffer = this.fileStore.readFully(l1, i);
/* 2226 */       Chunk chunk = Chunk.readChunkHeader(byteBuffer, l1);
/* 2227 */       int j = byteBuffer.position();
/* 2228 */       writeBuffer.position(j);
/* 2229 */       writeBuffer.put(byteBuffer);
/* 2230 */       long l = this.fileStore.allocate(i, paramLong1, paramLong2);
/* 2231 */       l2 = l / 4096L;
/*      */ 
/*      */       
/* 2234 */       assert paramLong2 > 0L || l2 <= paramChunk.block : l2 + " " + paramChunk;
/* 2235 */       writeBuffer.position(0);
/*      */ 
/*      */ 
/*      */       
/* 2239 */       chunk.block = l2;
/* 2240 */       chunk.next = 0L;
/* 2241 */       chunk.writeChunkHeader(writeBuffer, j);
/* 2242 */       writeBuffer.position(i - 128);
/* 2243 */       writeBuffer.put(chunk.getFooterBytes());
/* 2244 */       writeBuffer.position(0);
/* 2245 */       write(l, writeBuffer.getBuffer());
/*      */     } finally {
/* 2247 */       releaseWriteBuffer(writeBuffer);
/*      */     } 
/* 2249 */     this.fileStore.free(l1, i);
/* 2250 */     paramChunk.block = l2;
/* 2251 */     paramChunk.next = 0L;
/* 2252 */     this.layout.put(Chunk.getMetaKey(paramChunk.id), paramChunk.asString());
/* 2253 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sync() {
/* 2261 */     checkOpen();
/* 2262 */     FileStore fileStore = this.fileStore;
/* 2263 */     if (fileStore != null) {
/* 2264 */       fileStore.sync();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void compactFile(int paramInt) {
/* 2277 */     setRetentionTime(0);
/* 2278 */     long l = System.nanoTime() + paramInt * 1000000L;
/* 2279 */     while (compact(95, 16777216)) {
/* 2280 */       sync();
/* 2281 */       compactMoveChunks(95, 16777216L);
/* 2282 */       if (System.nanoTime() - l > 0L) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean compact(int paramInt1, int paramInt2) {
/* 2307 */     if (this.reuseSpace && this.lastChunk != null) {
/* 2308 */       checkOpen();
/* 2309 */       if (paramInt1 > 0 && getChunksFillRate() < paramInt1) {
/*      */         
/*      */         try {
/*      */ 
/*      */ 
/*      */           
/* 2315 */           if (this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
/*      */             try {
/* 2317 */               return rewriteChunks(paramInt2, 100);
/*      */             } finally {
/* 2319 */               this.storeLock.unlock();
/*      */             } 
/*      */           }
/* 2322 */         } catch (InterruptedException interruptedException) {
/* 2323 */           throw new RuntimeException(interruptedException);
/*      */         } 
/*      */       }
/*      */     } 
/* 2327 */     return false;
/*      */   }
/*      */   
/*      */   private boolean rewriteChunks(int paramInt1, int paramInt2) {
/* 2331 */     this.serializationLock.lock();
/*      */     try {
/* 2333 */       TxCounter txCounter = registerVersionUsage();
/*      */       try {
/* 2335 */         acceptChunkOccupancyChanges(getTimeSinceCreation(), this.currentVersion);
/* 2336 */         Iterable<Chunk> iterable = findOldChunks(paramInt1, paramInt2);
/* 2337 */         if (iterable != null) {
/* 2338 */           HashSet<Integer> hashSet = createIdSet(iterable);
/* 2339 */           return (!hashSet.isEmpty() && compactRewrite(hashSet) > 0);
/*      */         } 
/*      */       } finally {
/* 2342 */         deregisterVersionUsage(txCounter);
/*      */       } 
/* 2344 */       return false;
/*      */     } finally {
/* 2346 */       this.serializationLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getChunksFillRate() {
/* 2359 */     return getChunksFillRate(true);
/*      */   }
/*      */   
/*      */   public int getRewritableChunksFillRate() {
/* 2363 */     return getChunksFillRate(false);
/*      */   }
/*      */   
/*      */   private int getChunksFillRate(boolean paramBoolean) {
/* 2367 */     long l1 = 1L;
/* 2368 */     long l2 = 1L;
/* 2369 */     long l3 = getTimeSinceCreation();
/* 2370 */     for (Chunk chunk : this.chunks.values()) {
/* 2371 */       if (paramBoolean || isRewritable(chunk, l3)) {
/* 2372 */         assert chunk.maxLen >= 0L;
/* 2373 */         l1 += chunk.maxLen;
/* 2374 */         l2 += chunk.maxLenLive;
/*      */       } 
/*      */     } 
/*      */     
/* 2378 */     return (int)(100L * l2 / l1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getChunkCount() {
/* 2388 */     return this.chunks.size();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPageCount() {
/* 2397 */     int i = 0;
/* 2398 */     for (Chunk chunk : this.chunks.values()) {
/* 2399 */       i += chunk.pageCount;
/*      */     }
/* 2401 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLivePageCount() {
/* 2410 */     int i = 0;
/* 2411 */     for (Chunk chunk : this.chunks.values()) {
/* 2412 */       i += chunk.pageCountLive;
/*      */     }
/* 2414 */     return i;
/*      */   }
/*      */   
/*      */   private int getProjectedFillRate(int paramInt) {
/* 2418 */     this.saveChunkLock.lock();
/*      */     try {
/* 2420 */       int i = 0;
/* 2421 */       long l1 = 1L;
/* 2422 */       long l2 = 1L;
/* 2423 */       long l3 = getTimeSinceCreation();
/* 2424 */       for (Chunk chunk : this.chunks.values()) {
/* 2425 */         assert chunk.maxLen >= 0L;
/* 2426 */         if (isRewritable(chunk, l3) && chunk.getFillRate() <= paramInt) {
/* 2427 */           assert chunk.maxLen >= chunk.maxLenLive;
/* 2428 */           i += chunk.len;
/* 2429 */           l1 += chunk.maxLen;
/* 2430 */           l2 += chunk.maxLenLive;
/*      */         } 
/*      */       } 
/* 2433 */       int j = (int)(i * l2 / l1);
/* 2434 */       int k = this.fileStore.getProjectedFillRate(i - j);
/* 2435 */       return k;
/*      */     } finally {
/* 2437 */       this.saveChunkLock.unlock();
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getFillRate() {
/* 2442 */     this.saveChunkLock.lock();
/*      */     try {
/* 2444 */       return this.fileStore.getFillRate();
/*      */     } finally {
/* 2446 */       this.saveChunkLock.unlock();
/*      */     } 
/*      */   }
/*      */   
/*      */   private Iterable<Chunk> findOldChunks(int paramInt1, int paramInt2) {
/* 2451 */     assert this.lastChunk != null;
/* 2452 */     long l1 = getTimeSinceCreation();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2458 */     PriorityQueue<Chunk> priorityQueue = new PriorityQueue(this.chunks.size() / 4 + 1, (paramChunk1, paramChunk2) -> {
/*      */           int i = Integer.compare(paramChunk2.collectPriority, paramChunk1.collectPriority);
/*      */           
/*      */           if (i == 0) {
/*      */             i = Long.compare(paramChunk2.maxLenLive, paramChunk1.maxLenLive);
/*      */           }
/*      */           
/*      */           return i;
/*      */         });
/* 2467 */     long l2 = 0L;
/* 2468 */     long l3 = this.lastChunk.version + 1L;
/* 2469 */     for (Chunk chunk : this.chunks.values()) {
/*      */ 
/*      */ 
/*      */       
/* 2473 */       int i = chunk.getFillRate();
/* 2474 */       if (isRewritable(chunk, l1) && i <= paramInt2) {
/* 2475 */         long l = Math.max(1L, l3 - chunk.version);
/* 2476 */         chunk.collectPriority = (int)((i * 1000) / l);
/* 2477 */         l2 += chunk.maxLenLive;
/* 2478 */         priorityQueue.offer(chunk);
/* 2479 */         while (l2 > paramInt1) {
/* 2480 */           Chunk chunk1 = priorityQueue.poll();
/* 2481 */           if (chunk1 == null) {
/*      */             break;
/*      */           }
/* 2484 */           l2 -= chunk1.maxLenLive;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 2489 */     return priorityQueue.isEmpty() ? null : priorityQueue;
/*      */   }
/*      */   
/*      */   private boolean isRewritable(Chunk paramChunk, long paramLong) {
/* 2493 */     return (paramChunk.isRewritable() && isSeasonedChunk(paramChunk, paramLong));
/*      */   }
/*      */   
/*      */   private int compactRewrite(Set<Integer> paramSet) {
/* 2497 */     assert this.storeLock.isHeldByCurrentThread();
/* 2498 */     assert this.currentStoreVersion < 0L;
/* 2499 */     acceptChunkOccupancyChanges(getTimeSinceCreation(), this.currentVersion);
/* 2500 */     int i = rewriteChunks(paramSet, false);
/* 2501 */     acceptChunkOccupancyChanges(getTimeSinceCreation(), this.currentVersion);
/* 2502 */     i += rewriteChunks(paramSet, true);
/* 2503 */     return i;
/*      */   }
/*      */   
/*      */   private int rewriteChunks(Set<Integer> paramSet, boolean paramBoolean) {
/* 2507 */     byte b = 0;
/* 2508 */     for (Iterator<Integer> iterator = paramSet.iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
/* 2509 */       Chunk chunk = this.chunks.get(Integer.valueOf(i));
/* 2510 */       long[] arrayOfLong = getToC(chunk);
/* 2511 */       if (arrayOfLong != null) {
/* 2512 */         for (int j = 0; (j = chunk.occupancy.nextClearBit(j)) < chunk.pageCount; j++) {
/* 2513 */           long l = arrayOfLong[j];
/* 2514 */           int k = DataUtils.getPageMapId(l);
/* 2515 */           MVMap<String, String> mVMap = (MVMap<String, String>)((k == this.layout.getId()) ? this.layout : ((k == this.meta.getId()) ? this.meta : getMap(k)));
/* 2516 */           if (mVMap != null && !mVMap.isClosed()) {
/* 2517 */             assert !mVMap.isSingleWriter();
/* 2518 */             if (paramBoolean || DataUtils.isLeafPosition(l)) {
/* 2519 */               long l1 = DataUtils.getPagePos(i, l);
/* 2520 */               this.serializationLock.unlock();
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2536 */     return b;
/*      */   }
/*      */   
/*      */   private static HashSet<Integer> createIdSet(Iterable<Chunk> paramIterable) {
/* 2540 */     HashSet<Integer> hashSet = new HashSet();
/* 2541 */     for (Chunk chunk : paramIterable) {
/* 2542 */       hashSet.add(Integer.valueOf(chunk.id));
/*      */     }
/* 2544 */     return hashSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   <K, V> Page<K, V> readPage(MVMap<K, V> paramMVMap, long paramLong) {
/*      */     try {
/* 2559 */       if (!DataUtils.isPageSaved(paramLong)) {
/* 2560 */         throw DataUtils.newMVStoreException(6, "Position 0", new Object[0]);
/*      */       }
/*      */       
/* 2563 */       Page<?, ?> page = readPageFromCache(paramLong);
/* 2564 */       if (page == null) {
/* 2565 */         Chunk chunk = getChunk(paramLong);
/* 2566 */         int i = DataUtils.getPageOffset(paramLong);
/*      */         try {
/* 2568 */           ByteBuffer byteBuffer = chunk.readBufferForPage(this.fileStore, i, paramLong);
/* 2569 */           page = Page.read(byteBuffer, paramLong, paramMVMap);
/* 2570 */         } catch (MVStoreException mVStoreException) {
/* 2571 */           throw mVStoreException;
/* 2572 */         } catch (Exception exception) {
/* 2573 */           throw DataUtils.newMVStoreException(6, "Unable to read the page at position {0}, chunk {1}, offset {2}", new Object[] {
/*      */                 
/* 2575 */                 Long.valueOf(paramLong), Integer.valueOf(chunk.id), Integer.valueOf(i), exception });
/*      */         } 
/* 2577 */         cachePage(page);
/*      */       } 
/* 2579 */       return (Page)page;
/* 2580 */     } catch (MVStoreException mVStoreException) {
/* 2581 */       if (this.recoveryMode) {
/* 2582 */         return paramMVMap.createEmptyLeaf();
/*      */       }
/* 2584 */       throw mVStoreException;
/*      */     } 
/*      */   }
/*      */   
/*      */   private long[] getToC(Chunk paramChunk) {
/* 2589 */     if (paramChunk.tocPos == 0)
/*      */     {
/* 2591 */       return null;
/*      */     }
/* 2593 */     long[] arrayOfLong = (long[])this.chunksToC.get(paramChunk.id);
/* 2594 */     if (arrayOfLong == null) {
/* 2595 */       arrayOfLong = paramChunk.readToC(this.fileStore);
/* 2596 */       this.chunksToC.put(paramChunk.id, arrayOfLong, arrayOfLong.length * 8);
/*      */     } 
/* 2598 */     assert arrayOfLong.length == paramChunk.pageCount : arrayOfLong.length + " != " + paramChunk.pageCount;
/* 2599 */     return arrayOfLong;
/*      */   }
/*      */ 
/*      */   
/*      */   private <K, V> Page<K, V> readPageFromCache(long paramLong) {
/* 2604 */     return (this.cache == null) ? null : (Page<K, V>)this.cache.get(paramLong);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void accountForRemovedPage(long paramLong1, long paramLong2, boolean paramBoolean, int paramInt) {
/* 2615 */     assert DataUtils.isPageSaved(paramLong1);
/* 2616 */     if (paramInt < 0) {
/* 2617 */       paramInt = calculatePageNo(paramLong1);
/*      */     }
/* 2619 */     RemovedPageInfo removedPageInfo = new RemovedPageInfo(paramLong1, paramBoolean, paramLong2, paramInt);
/* 2620 */     this.removedPages.add(removedPageInfo);
/*      */   }
/*      */   
/*      */   private int calculatePageNo(long paramLong) {
/* 2624 */     int i = -1;
/* 2625 */     Chunk chunk = getChunk(paramLong);
/* 2626 */     long[] arrayOfLong = getToC(chunk);
/* 2627 */     if (arrayOfLong != null) {
/* 2628 */       int j = DataUtils.getPageOffset(paramLong);
/* 2629 */       int k = 0;
/* 2630 */       int m = arrayOfLong.length - 1;
/* 2631 */       while (k <= m) {
/* 2632 */         int n = k + m >>> 1;
/* 2633 */         long l = DataUtils.getPageOffset(arrayOfLong[n]);
/* 2634 */         if (l < j) {
/* 2635 */           k = n + 1; continue;
/* 2636 */         }  if (l > j) {
/* 2637 */           m = n - 1; continue;
/*      */         } 
/* 2639 */         i = n;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 2644 */     return i;
/*      */   }
/*      */   
/*      */   Compressor getCompressorFast() {
/* 2648 */     if (this.compressorFast == null) {
/* 2649 */       this.compressorFast = (Compressor)new CompressLZF();
/*      */     }
/* 2651 */     return this.compressorFast;
/*      */   }
/*      */   
/*      */   Compressor getCompressorHigh() {
/* 2655 */     if (this.compressorHigh == null) {
/* 2656 */       this.compressorHigh = (Compressor)new CompressDeflate();
/*      */     }
/* 2658 */     return this.compressorHigh;
/*      */   }
/*      */   
/*      */   int getCompressionLevel() {
/* 2662 */     return this.compressionLevel;
/*      */   }
/*      */   
/*      */   public int getPageSplitSize() {
/* 2666 */     return this.pageSplitSize;
/*      */   }
/*      */   
/*      */   public int getKeysPerPage() {
/* 2670 */     return this.keysPerPage;
/*      */   }
/*      */   
/*      */   public long getMaxPageSize() {
/* 2674 */     return (this.cache == null) ? Long.MAX_VALUE : (this.cache.getMaxItemSize() >> 4L);
/*      */   }
/*      */   
/*      */   public boolean getReuseSpace() {
/* 2678 */     return this.reuseSpace;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setReuseSpace(boolean paramBoolean) {
/* 2695 */     this.reuseSpace = paramBoolean;
/*      */   }
/*      */   
/*      */   public int getRetentionTime() {
/* 2699 */     return this.retentionTime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRetentionTime(int paramInt) {
/* 2724 */     this.retentionTime = paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setVersionsToKeep(int paramInt) {
/* 2734 */     this.versionsToKeep = paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getVersionsToKeep() {
/* 2743 */     return this.versionsToKeep;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   long getOldestVersionToKeep() {
/* 2758 */     long l = this.oldestVersionToKeep.get();
/* 2759 */     l = Math.max(l - this.versionsToKeep, -1L);
/* 2760 */     if (this.fileStore != null) {
/* 2761 */       long l1 = lastChunkVersion() - 1L;
/* 2762 */       if (l1 != -1L && l1 < l) {
/* 2763 */         l = l1;
/*      */       }
/*      */     } 
/* 2766 */     return l;
/*      */   }
/*      */   
/*      */   private void setOldestVersionToKeep(long paramLong) {
/*      */     boolean bool;
/*      */     do {
/* 2772 */       long l = this.oldestVersionToKeep.get();
/*      */ 
/*      */       
/* 2775 */       bool = (paramLong <= l || this.oldestVersionToKeep.compareAndSet(l, paramLong)) ? true : false;
/* 2776 */     } while (!bool);
/*      */   }
/*      */   
/*      */   private long lastChunkVersion() {
/* 2780 */     Chunk chunk = this.lastChunk;
/* 2781 */     return (chunk == null) ? 0L : chunk.version;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isKnownVersion(long paramLong) {
/* 2793 */     if (paramLong > this.currentVersion || paramLong < 0L) {
/* 2794 */       return false;
/*      */     }
/* 2796 */     if (paramLong == this.currentVersion || this.chunks.isEmpty())
/*      */     {
/* 2798 */       return true;
/*      */     }
/*      */     
/* 2801 */     Chunk chunk = getChunkForVersion(paramLong);
/* 2802 */     if (chunk == null) {
/* 2803 */       return false;
/*      */     }
/*      */ 
/*      */     
/* 2807 */     MVMap<String, String> mVMap = getLayoutMap(paramLong);
/*      */     try {
/* 2809 */       for (Iterator<String> iterator = mVMap.keyIterator("chunk."); iterator.hasNext(); ) {
/* 2810 */         String str = iterator.next();
/* 2811 */         if (!str.startsWith("chunk.")) {
/*      */           break;
/*      */         }
/* 2814 */         if (!this.layout.containsKey(str)) {
/* 2815 */           String str1 = mVMap.get(str);
/* 2816 */           Chunk chunk1 = Chunk.fromString(str1);
/* 2817 */           Chunk chunk2 = readChunkHeaderAndFooter(chunk1.block, chunk1.id);
/* 2818 */           if (chunk2 == null) {
/* 2819 */             return false;
/*      */           }
/*      */         } 
/*      */       } 
/* 2823 */     } catch (MVStoreException mVStoreException) {
/*      */       
/* 2825 */       return false;
/*      */     } 
/* 2827 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void registerUnsavedMemory(int paramInt) {
/* 2841 */     this.unsavedMemory += paramInt;
/* 2842 */     int i = this.unsavedMemory;
/* 2843 */     if (i > this.autoCommitMemory && this.autoCommitMemory > 0) {
/* 2844 */       this.saveNeeded = true;
/*      */     }
/*      */   }
/*      */   
/*      */   boolean isSaveNeeded() {
/* 2849 */     return this.saveNeeded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void beforeWrite(MVMap<?, ?> paramMVMap) {
/* 2858 */     if (this.saveNeeded && this.fileStore != null && isOpenOrStopping() && (this.storeLock
/*      */ 
/*      */ 
/*      */       
/* 2862 */       .isHeldByCurrentThread() || !paramMVMap.getRoot().isLockedByCurrentThread()) && paramMVMap != this.layout) {
/*      */ 
/*      */ 
/*      */       
/* 2866 */       this.saveNeeded = false;
/*      */       
/* 2868 */       if (this.autoCommitMemory > 0 && needStore())
/*      */       {
/*      */ 
/*      */         
/* 2872 */         if (requireStore() && !paramMVMap.isSingleWriter()) {
/* 2873 */           commit(MVStore::requireStore);
/*      */         } else {
/* 2875 */           tryCommit(MVStore::needStore);
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean requireStore() {
/* 2882 */     return (3 * this.unsavedMemory > 4 * this.autoCommitMemory);
/*      */   }
/*      */   
/*      */   private boolean needStore() {
/* 2886 */     return (this.unsavedMemory > this.autoCommitMemory);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getStoreVersion() {
/* 2897 */     checkOpen();
/* 2898 */     String str = this.meta.get("setting.storeVersion");
/* 2899 */     return (str == null) ? 0 : DataUtils.parseHexInt(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setStoreVersion(int paramInt) {
/* 2908 */     this.storeLock.lock();
/*      */     try {
/* 2910 */       checkOpen();
/* 2911 */       markMetaChanged();
/* 2912 */       this.meta.put("setting.storeVersion", Integer.toHexString(paramInt));
/*      */     } finally {
/* 2914 */       this.storeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollback() {
/* 2923 */     rollbackTo(this.currentVersion);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void rollbackTo(long paramLong) {
/* 2935 */     this.storeLock.lock();
/*      */     try {
/* 2937 */       checkOpen();
/* 2938 */       this.currentVersion = paramLong;
/* 2939 */       if (paramLong == 0L) {
/*      */         
/* 2941 */         this.layout.setInitialRoot(this.layout.createEmptyLeaf(), -1L);
/* 2942 */         this.meta.setInitialRoot(this.meta.createEmptyLeaf(), -1L);
/* 2943 */         this.layout.put("meta.id", Integer.toHexString(this.meta.getId()));
/* 2944 */         this.deadChunks.clear();
/* 2945 */         this.removedPages.clear();
/* 2946 */         this.chunks.clear();
/* 2947 */         clearCaches();
/* 2948 */         if (this.fileStore != null) {
/* 2949 */           this.saveChunkLock.lock();
/*      */           try {
/* 2951 */             this.fileStore.clear();
/*      */           } finally {
/* 2953 */             this.saveChunkLock.unlock();
/*      */           } 
/*      */         } 
/* 2956 */         this.lastChunk = null;
/* 2957 */         this.versions.clear();
/* 2958 */         setWriteVersion(paramLong);
/* 2959 */         this.metaChanged = false;
/* 2960 */         for (MVMap<?, ?> mVMap : this.maps.values()) {
/* 2961 */           mVMap.close();
/*      */         }
/*      */         return;
/*      */       } 
/* 2965 */       DataUtils.checkArgument(
/* 2966 */           isKnownVersion(paramLong), "Unknown version {0}", new Object[] {
/* 2967 */             Long.valueOf(paramLong)
/*      */           });
/*      */       TxCounter txCounter;
/* 2970 */       while ((txCounter = this.versions.peekLast()) != null && txCounter.version >= paramLong) {
/* 2971 */         this.versions.removeLast();
/*      */       }
/* 2973 */       this.currentTxCounter = new TxCounter(paramLong);
/*      */       
/* 2975 */       if (!this.layout.rollbackRoot(paramLong)) {
/* 2976 */         MVMap<String, String> mVMap = getLayoutMap(paramLong);
/* 2977 */         this.layout.setInitialRoot(mVMap.getRootPage(), paramLong);
/*      */       } 
/* 2979 */       if (!this.meta.rollbackRoot(paramLong)) {
/* 2980 */         this.meta.setRootPos(getRootPos(this.meta.getId()), paramLong - 1L);
/*      */       }
/* 2982 */       this.metaChanged = false;
/*      */       
/* 2984 */       for (MVMap mVMap : new ArrayList(this.maps.values())) {
/* 2985 */         int i = mVMap.getId();
/* 2986 */         if (mVMap.getCreateVersion() >= paramLong) {
/* 2987 */           mVMap.close();
/* 2988 */           this.maps.remove(Integer.valueOf(i)); continue;
/*      */         } 
/* 2990 */         if (!mVMap.rollbackRoot(paramLong)) {
/* 2991 */           mVMap.setRootPos(getRootPos(i), paramLong - 1L);
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 2996 */       this.deadChunks.clear();
/* 2997 */       this.removedPages.clear();
/* 2998 */       clearCaches();
/*      */       
/* 3000 */       this.serializationLock.lock();
/*      */       try {
/* 3002 */         Chunk chunk = getChunkForVersion(paramLong);
/* 3003 */         if (chunk != null) {
/* 3004 */           this.saveChunkLock.lock();
/*      */           try {
/* 3006 */             setLastChunk(chunk);
/* 3007 */             this.storeHeader.put("clean", Integer.valueOf(1));
/* 3008 */             writeStoreHeader();
/* 3009 */             readStoreHeader();
/*      */           } finally {
/* 3011 */             this.saveChunkLock.unlock();
/*      */           } 
/*      */         } 
/*      */       } finally {
/* 3015 */         this.serializationLock.unlock();
/*      */       } 
/* 3017 */       onVersionChange(this.currentVersion);
/* 3018 */       assert !hasUnsavedChanges();
/*      */     } finally {
/* 3020 */       unlockAndCheckPanicCondition();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void clearCaches() {
/* 3025 */     if (this.cache != null) {
/* 3026 */       this.cache.clear();
/*      */     }
/* 3028 */     if (this.chunksToC != null) {
/* 3029 */       this.chunksToC.clear();
/*      */     }
/*      */   }
/*      */   
/*      */   private long getRootPos(int paramInt) {
/* 3034 */     String str = this.layout.get(MVMap.getMapRootKey(paramInt));
/* 3035 */     return (str == null) ? 0L : DataUtils.parseHexLong(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getCurrentVersion() {
/* 3045 */     return this.currentVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FileStore getFileStore() {
/* 3054 */     return this.fileStore;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, Object> getStoreHeader() {
/* 3065 */     return this.storeHeader;
/*      */   }
/*      */   
/*      */   private void checkOpen() {
/* 3069 */     if (!isOpenOrStopping()) {
/* 3070 */       throw DataUtils.newMVStoreException(4, "This store is closed", new Object[] { this.panicException });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void renameMap(MVMap<?, ?> paramMVMap, String paramString) {
/* 3082 */     checkOpen();
/* 3083 */     DataUtils.checkArgument((paramMVMap != this.layout && paramMVMap != this.meta), "Renaming the meta map is not allowed", new Object[0]);
/*      */     
/* 3085 */     int i = paramMVMap.getId();
/* 3086 */     String str = getMapName(i);
/* 3087 */     if (str != null && !str.equals(paramString)) {
/* 3088 */       String str1 = Integer.toHexString(i);
/*      */       
/* 3090 */       String str2 = this.meta.putIfAbsent("name." + paramString, str1);
/*      */       
/* 3092 */       DataUtils.checkArgument((str2 == null || str2
/* 3093 */           .equals(str1)), "A map named {0} already exists", new Object[] { paramString });
/*      */ 
/*      */       
/* 3096 */       this.meta.put(MVMap.getMapKey(i), paramMVMap.asString(paramString));
/*      */       
/* 3098 */       this.meta.remove("name." + str);
/* 3099 */       markMetaChanged();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeMap(MVMap<?, ?> paramMVMap) {
/* 3109 */     this.storeLock.lock();
/*      */     try {
/* 3111 */       checkOpen();
/* 3112 */       DataUtils.checkArgument((this.layout != this.meta && paramMVMap != this.meta), "Removing the meta map is not allowed", new Object[0]);
/*      */       
/* 3114 */       RootReference<?, ?> rootReference = paramMVMap.clearIt();
/* 3115 */       paramMVMap.close();
/*      */       
/* 3117 */       this.updateCounter += rootReference.updateCounter;
/* 3118 */       this.updateAttemptCounter += rootReference.updateAttemptCounter;
/*      */       
/* 3120 */       int i = paramMVMap.getId();
/* 3121 */       String str = getMapName(i);
/* 3122 */       if (this.meta.remove(MVMap.getMapKey(i)) != null) {
/* 3123 */         markMetaChanged();
/*      */       }
/* 3125 */       if (this.meta.remove("name." + str) != null) {
/* 3126 */         markMetaChanged();
/*      */       }
/*      */     } finally {
/* 3129 */       this.storeLock.unlock();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void deregisterMapRoot(int paramInt) {
/* 3140 */     if (this.layout.remove(MVMap.getMapRootKey(paramInt)) != null) {
/* 3141 */       markMetaChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeMap(String paramString) {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_1
/*      */     //   2: invokespecial getMapId : (Ljava/lang/String;)I
/*      */     //   5: istore_2
/*      */     //   6: iload_2
/*      */     //   7: ifle -> 34
/*      */     //   10: aload_0
/*      */     //   11: iload_2
/*      */     //   12: invokevirtual getMap : (I)Lorg/h2/mvstore/MVMap;
/*      */     //   15: astore_3
/*      */     //   16: aload_3
/*      */     //   17: ifnonnull -> 29
/*      */     //   20: aload_0
/*      */     //   21: aload_1
/*      */     //   22: invokestatic getGenericMapBuilder : ()Lorg/h2/mvstore/MVMap$Builder;
/*      */     //   25: invokevirtual openMap : (Ljava/lang/String;Lorg/h2/mvstore/MVMap$MapBuilder;)Lorg/h2/mvstore/MVMap;
/*      */     //   28: astore_3
/*      */     //   29: aload_0
/*      */     //   30: aload_3
/*      */     //   31: invokevirtual removeMap : (Lorg/h2/mvstore/MVMap;)V
/*      */     //   34: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #3151	-> 0
/*      */     //   #3152	-> 6
/*      */     //   #3153	-> 10
/*      */     //   #3154	-> 16
/*      */     //   #3155	-> 20
/*      */     //   #3157	-> 29
/*      */     //   #3159	-> 34
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getMapName(int paramInt) {
/* 3168 */     String str = this.meta.get(MVMap.getMapKey(paramInt));
/* 3169 */     return (str == null) ? null : DataUtils.getMapName(str);
/*      */   }
/*      */   
/*      */   private int getMapId(String paramString) {
/* 3173 */     String str = this.meta.get("name." + paramString);
/* 3174 */     return (str == null) ? -1 : DataUtils.parseHexInt(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void writeInBackground() {
/*      */     
/* 3183 */     try { if (!isOpenOrStopping() || isReadOnly()) {
/*      */         return;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3190 */       long l = getTimeSinceCreation();
/* 3191 */       if (l > this.lastCommitTime + this.autoCommitDelay) {
/* 3192 */         tryCommit();
/* 3193 */         if (this.autoCompactFillRate < 0) {
/* 3194 */           compact(-getTargetFillRate(), this.autoCommitMemory);
/*      */         }
/*      */       } 
/* 3197 */       int i = getFillRate();
/* 3198 */       if (this.fileStore.isFragmented() && i < this.autoCompactFillRate) {
/* 3199 */         if (this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
/*      */           try {
/* 3201 */             int j = this.autoCommitMemory;
/* 3202 */             if (isIdle()) {
/* 3203 */               j *= 4;
/*      */             }
/* 3205 */             compactMoveChunks(101, j);
/*      */           } finally {
/* 3207 */             unlockAndCheckPanicCondition();
/*      */           } 
/*      */         }
/* 3210 */       } else if (i >= this.autoCompactFillRate && this.lastChunk != null) {
/* 3211 */         int j = getRewritableChunksFillRate();
/* 3212 */         j = isIdle() ? (100 - (100 - j) / 2) : j;
/* 3213 */         if (j < getTargetFillRate() && 
/* 3214 */           this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
/*      */           try {
/* 3216 */             int k = this.autoCommitMemory * i / Math.max(j, 1);
/* 3217 */             if (!isIdle()) {
/* 3218 */               k /= 4;
/*      */             }
/* 3220 */             if (rewriteChunks(k, j)) {
/* 3221 */               dropUnusedChunks();
/*      */             }
/*      */           } finally {
/* 3224 */             this.storeLock.unlock();
/*      */           } 
/*      */         }
/*      */       } 
/*      */       
/* 3229 */       this.autoCompactLastFileOpCount = this.fileStore.getWriteCount() + this.fileStore.getReadCount(); }
/* 3230 */     catch (InterruptedException interruptedException) {  }
/* 3231 */     catch (Throwable throwable)
/* 3232 */     { handleException(throwable);
/* 3233 */       if (this.backgroundExceptionHandler == null) {
/* 3234 */         throw throwable;
/*      */       } }
/*      */   
/*      */   }
/*      */   
/*      */   private void doMaintenance(int paramInt) {
/* 3240 */     if (this.autoCompactFillRate > 0 && this.lastChunk != null && this.reuseSpace) {
/*      */       try {
/* 3242 */         int i = -1;
/* 3243 */         for (byte b = 0; b < 5; b++) {
/* 3244 */           int j = getFillRate();
/* 3245 */           int k = j;
/* 3246 */           if (j > paramInt) {
/* 3247 */             k = getProjectedFillRate(100);
/* 3248 */             if (k > paramInt || k <= i) {
/*      */               break;
/*      */             }
/*      */           } 
/* 3252 */           i = k;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 3257 */           if (!this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS))
/*      */           {
/*      */             break;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           }
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/* 3274 */       catch (InterruptedException interruptedException) {
/* 3275 */         throw new RuntimeException(interruptedException);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private int getTargetFillRate() {
/* 3281 */     int i = this.autoCompactFillRate;
/*      */     
/* 3283 */     if (!isIdle()) {
/* 3284 */       i /= 2;
/*      */     }
/* 3286 */     return i;
/*      */   }
/*      */   
/*      */   private boolean isIdle() {
/* 3290 */     return (this.autoCompactLastFileOpCount == this.fileStore.getWriteCount() + this.fileStore.getReadCount());
/*      */   }
/*      */   
/*      */   private void handleException(Throwable paramThrowable) {
/* 3294 */     if (this.backgroundExceptionHandler != null) {
/*      */       try {
/* 3296 */         this.backgroundExceptionHandler.uncaughtException(Thread.currentThread(), paramThrowable);
/* 3297 */       } catch (Throwable throwable) {
/* 3298 */         if (paramThrowable != throwable) {
/* 3299 */           paramThrowable.addSuppressed(throwable);
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCacheSize(int paramInt) {
/* 3311 */     long l = paramInt * 1024L * 1024L;
/* 3312 */     if (this.cache != null) {
/* 3313 */       this.cache.setMaxMemory(l);
/* 3314 */       this.cache.clear();
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isOpen() {
/* 3319 */     return (this.state == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() {
/* 3327 */     if (isOpen()) {
/* 3328 */       return false;
/*      */     }
/* 3330 */     this.storeLock.lock();
/*      */     try {
/* 3332 */       assert this.state == 3;
/* 3333 */       return true;
/*      */     } finally {
/* 3335 */       this.storeLock.unlock();
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isOpenOrStopping() {
/* 3340 */     return (this.state <= 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void stopBackgroundThread(boolean paramBoolean) {
/*      */     BackgroundWriterThread backgroundWriterThread;
/* 3348 */     while ((backgroundWriterThread = this.backgroundWriterThread.get()) != null) {
/* 3349 */       if (this.backgroundWriterThread.compareAndSet(backgroundWriterThread, null)) {
/*      */         
/* 3351 */         if (backgroundWriterThread != Thread.currentThread()) {
/* 3352 */           synchronized (backgroundWriterThread.sync) {
/* 3353 */             backgroundWriterThread.sync.notifyAll();
/*      */           } 
/*      */           
/* 3356 */           if (paramBoolean) {
/*      */             try {
/* 3358 */               backgroundWriterThread.join();
/* 3359 */             } catch (Exception exception) {}
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/* 3364 */         shutdownExecutor(this.serializationExecutor);
/* 3365 */         this.serializationExecutor = null;
/* 3366 */         shutdownExecutor(this.bufferSaveExecutor);
/* 3367 */         this.bufferSaveExecutor = null;
/*      */         break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoCommitDelay(int paramInt) {
/* 3385 */     if (this.autoCommitDelay == paramInt) {
/*      */       return;
/*      */     }
/* 3388 */     this.autoCommitDelay = paramInt;
/* 3389 */     if (this.fileStore == null || this.fileStore.isReadOnly()) {
/*      */       return;
/*      */     }
/* 3392 */     stopBackgroundThread(true);
/*      */     
/* 3394 */     if (paramInt > 0 && isOpen()) {
/* 3395 */       int i = Math.max(1, paramInt / 10);
/*      */ 
/*      */       
/* 3398 */       BackgroundWriterThread backgroundWriterThread = new BackgroundWriterThread(this, i, this.fileStore.toString());
/* 3399 */       if (this.backgroundWriterThread.compareAndSet(null, backgroundWriterThread)) {
/* 3400 */         backgroundWriterThread.start();
/* 3401 */         this.serializationExecutor = createSingleThreadExecutor("H2-serialization");
/* 3402 */         this.bufferSaveExecutor = createSingleThreadExecutor("H2-save");
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static ThreadPoolExecutor createSingleThreadExecutor(String paramString) {
/* 3408 */     return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), paramRunnable -> {
/*      */           Thread thread = new Thread(paramRunnable, paramString);
/*      */           thread.setDaemon(true);
/*      */           return thread;
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBackgroundThread() {
/* 3418 */     return (Thread.currentThread() == this.backgroundWriterThread.get());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getAutoCommitDelay() {
/* 3427 */     return this.autoCommitDelay;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getAutoCommitMemory() {
/* 3437 */     return this.autoCommitMemory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getUnsavedMemory() {
/* 3449 */     return this.unsavedMemory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void cachePage(Page<?, ?> paramPage) {
/* 3457 */     if (this.cache != null) {
/* 3458 */       this.cache.put(paramPage.getPos(), paramPage, paramPage.getMemory());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCacheSizeUsed() {
/* 3470 */     if (this.cache == null) {
/* 3471 */       return 0;
/*      */     }
/* 3473 */     return (int)(this.cache.getUsedMemory() >> 20L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCacheSize() {
/* 3484 */     if (this.cache == null) {
/* 3485 */       return 0;
/*      */     }
/* 3487 */     return (int)(this.cache.getMaxMemory() >> 20L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CacheLongKeyLIRS<Page<?, ?>> getCache() {
/* 3496 */     return this.cache;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/* 3505 */     return (this.fileStore != null && this.fileStore.isReadOnly());
/*      */   }
/*      */   
/*      */   public int getCacheHitRatio() {
/* 3509 */     return getCacheHitRatio(this.cache);
/*      */   }
/*      */   
/*      */   public int getTocCacheHitRatio() {
/* 3513 */     return getCacheHitRatio(this.chunksToC);
/*      */   }
/*      */   
/*      */   private static int getCacheHitRatio(CacheLongKeyLIRS<?> paramCacheLongKeyLIRS) {
/* 3517 */     if (paramCacheLongKeyLIRS == null) {
/* 3518 */       return 0;
/*      */     }
/* 3520 */     long l = paramCacheLongKeyLIRS.getHits();
/* 3521 */     return (int)(100L * l / (l + paramCacheLongKeyLIRS.getMisses() + 1L));
/*      */   }
/*      */   
/*      */   public int getLeafRatio() {
/* 3525 */     return (int)(this.leafCount * 100L / Math.max(1L, this.leafCount + this.nonLeafCount));
/*      */   }
/*      */   
/*      */   public double getUpdateFailureRatio() {
/* 3529 */     long l1 = this.updateCounter;
/* 3530 */     long l2 = this.updateAttemptCounter;
/* 3531 */     RootReference<String, String> rootReference = this.layout.getRoot();
/* 3532 */     l1 += rootReference.updateCounter;
/* 3533 */     l2 += rootReference.updateAttemptCounter;
/* 3534 */     rootReference = this.meta.getRoot();
/* 3535 */     l1 += rootReference.updateCounter;
/* 3536 */     l2 += rootReference.updateAttemptCounter;
/* 3537 */     for (MVMap<?, ?> mVMap : this.maps.values()) {
/* 3538 */       RootReference rootReference1 = mVMap.getRoot();
/* 3539 */       l1 += rootReference1.updateCounter;
/* 3540 */       l2 += rootReference1.updateAttemptCounter;
/*      */     } 
/* 3542 */     return (l2 == 0L) ? 0.0D : (1.0D - l1 / l2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TxCounter registerVersionUsage() {
/*      */     while (true) {
/* 3555 */       TxCounter txCounter = this.currentTxCounter;
/* 3556 */       if (txCounter.incrementAndGet() > 0) {
/* 3557 */         return txCounter;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3566 */       assert txCounter != this.currentTxCounter : txCounter;
/* 3567 */       txCounter.decrementAndGet();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void deregisterVersionUsage(TxCounter paramTxCounter) {
/* 3580 */     if (paramTxCounter != null && 
/* 3581 */       paramTxCounter.decrementAndGet() <= 0) {
/* 3582 */       if (this.storeLock.isHeldByCurrentThread()) {
/* 3583 */         dropUnusedVersions();
/* 3584 */       } else if (this.storeLock.tryLock()) {
/*      */         try {
/* 3586 */           dropUnusedVersions();
/*      */         } finally {
/* 3588 */           this.storeLock.unlock();
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void onVersionChange(long paramLong) {
/* 3596 */     TxCounter txCounter = this.currentTxCounter;
/* 3597 */     assert txCounter.get() >= 0;
/* 3598 */     this.versions.add(txCounter);
/* 3599 */     this.currentTxCounter = new TxCounter(paramLong);
/* 3600 */     txCounter.decrementAndGet();
/* 3601 */     dropUnusedVersions();
/*      */   }
/*      */   
/*      */   private void dropUnusedVersions() {
/*      */     TxCounter txCounter;
/* 3606 */     while ((txCounter = this.versions.peek()) != null && txCounter
/* 3607 */       .get() < 0) {
/* 3608 */       this.versions.poll();
/*      */     }
/* 3610 */     setOldestVersionToKeep(((txCounter != null) ? txCounter : this.currentTxCounter).version);
/*      */   }
/*      */   
/*      */   private int dropUnusedChunks() {
/* 3614 */     assert this.storeLock.isHeldByCurrentThread();
/* 3615 */     byte b = 0;
/* 3616 */     if (!this.deadChunks.isEmpty()) {
/* 3617 */       long l1 = getOldestVersionToKeep();
/* 3618 */       long l2 = getTimeSinceCreation();
/* 3619 */       this.saveChunkLock.lock();
/*      */       try {
/*      */         Chunk chunk;
/* 3622 */         while ((chunk = this.deadChunks.poll()) != null && ((
/* 3623 */           isSeasonedChunk(chunk, l2) && canOverwriteChunk(chunk, l1)) || 
/*      */ 
/*      */           
/* 3626 */           !this.deadChunks.offerFirst(chunk))) {
/*      */           
/* 3628 */           if (this.chunks.remove(Integer.valueOf(chunk.id)) != null) {
/*      */             
/* 3630 */             long[] arrayOfLong = (long[])this.chunksToC.remove(chunk.id);
/* 3631 */             if (arrayOfLong != null && this.cache != null) {
/* 3632 */               for (long l3 : arrayOfLong) {
/* 3633 */                 long l4 = DataUtils.getPagePos(chunk.id, l3);
/* 3634 */                 this.cache.remove(l4);
/*      */               } 
/*      */             }
/*      */             
/* 3638 */             if (this.layout.remove(Chunk.getMetaKey(chunk.id)) != null) {
/* 3639 */               markMetaChanged();
/*      */             }
/* 3641 */             if (chunk.isSaved()) {
/* 3642 */               freeChunkSpace(chunk);
/*      */             }
/* 3644 */             b++;
/*      */           } 
/*      */         } 
/*      */       } finally {
/* 3648 */         this.saveChunkLock.unlock();
/*      */       } 
/*      */     } 
/* 3651 */     return b;
/*      */   }
/*      */   
/*      */   private void freeChunkSpace(Chunk paramChunk) {
/* 3655 */     long l = paramChunk.block * 4096L;
/* 3656 */     int i = paramChunk.len * 4096;
/* 3657 */     freeFileSpace(l, i);
/*      */   }
/*      */   
/*      */   private void freeFileSpace(long paramLong, int paramInt) {
/* 3661 */     this.fileStore.free(paramLong, paramInt);
/* 3662 */     assert validateFileLength(paramLong + ":" + paramInt);
/*      */   }
/*      */   
/*      */   private boolean validateFileLength(String paramString) {
/* 3666 */     assert this.saveChunkLock.isHeldByCurrentThread();
/* 3667 */     assert this.fileStore.getFileLengthInUse() == measureFileLengthInUse() : this.fileStore
/* 3668 */       .getFileLengthInUse() + " != " + measureFileLengthInUse() + " " + paramString;
/* 3669 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class TxCounter
/*      */   {
/*      */     public final long version;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile int counter;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3690 */     private static final AtomicIntegerFieldUpdater<TxCounter> counterUpdater = AtomicIntegerFieldUpdater.newUpdater(TxCounter.class, "counter");
/*      */ 
/*      */     
/*      */     TxCounter(long param1Long) {
/* 3694 */       this.version = param1Long;
/*      */     }
/*      */     
/*      */     int get() {
/* 3698 */       return this.counter;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int incrementAndGet() {
/* 3707 */       return counterUpdater.incrementAndGet(this);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int decrementAndGet() {
/* 3716 */       return counterUpdater.decrementAndGet(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 3721 */       return "v=" + this.version + " / cnt=" + this.counter;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class BackgroundWriterThread
/*      */     extends Thread
/*      */   {
/* 3731 */     public final Object sync = new Object();
/*      */     private final MVStore store;
/*      */     private final int sleep;
/*      */     
/*      */     BackgroundWriterThread(MVStore param1MVStore, int param1Int, String param1String) {
/* 3736 */       super("MVStore background writer " + param1String);
/* 3737 */       this.store = param1MVStore;
/* 3738 */       this.sleep = param1Int;
/* 3739 */       setDaemon(true);
/*      */     }
/*      */ 
/*      */     
/*      */     public void run() {
/* 3744 */       while (this.store.isBackgroundThread()) {
/* 3745 */         synchronized (this.sync) {
/*      */           try {
/* 3747 */             this.sync.wait(this.sleep);
/* 3748 */           } catch (InterruptedException interruptedException) {}
/*      */         } 
/*      */         
/* 3751 */         if (!this.store.isBackgroundThread()) {
/*      */           break;
/*      */         }
/* 3754 */         this.store.writeInBackground();
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static class RemovedPageInfo implements Comparable<RemovedPageInfo> {
/*      */     final long version;
/*      */     final long removedPageInfo;
/*      */     
/*      */     RemovedPageInfo(long param1Long1, boolean param1Boolean, long param1Long2, int param1Int) {
/* 3764 */       this.removedPageInfo = createRemovedPageInfo(param1Long1, param1Boolean, param1Int);
/* 3765 */       this.version = param1Long2;
/*      */     }
/*      */ 
/*      */     
/*      */     public int compareTo(RemovedPageInfo param1RemovedPageInfo) {
/* 3770 */       return Long.compare(this.version, param1RemovedPageInfo.version);
/*      */     }
/*      */     
/*      */     int getPageChunkId() {
/* 3774 */       return DataUtils.getPageChunkId(this.removedPageInfo);
/*      */     }
/*      */     
/*      */     int getPageNo() {
/* 3778 */       return DataUtils.getPageOffset(this.removedPageInfo);
/*      */     }
/*      */     
/*      */     int getPageLength() {
/* 3782 */       return DataUtils.getPageMaxLength(this.removedPageInfo);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     boolean isPinned() {
/* 3790 */       return ((this.removedPageInfo & 0x1L) == 1L);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static long createRemovedPageInfo(long param1Long, boolean param1Boolean, int param1Int) {
/* 3803 */       long l = param1Long & 0xFFFFFFC00000003EL | (param1Int << 6) & 0xFFFFFFFFL;
/* 3804 */       if (param1Boolean) {
/* 3805 */         l |= 0x1L;
/*      */       }
/* 3807 */       return l;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 3812 */       return "RemovedPageInfo{version=" + this.version + ", chunk=" + 
/*      */         
/* 3814 */         getPageChunkId() + ", pageNo=" + 
/* 3815 */         getPageNo() + ", len=" + 
/* 3816 */         getPageLength() + (
/* 3817 */         isPinned() ? ", pinned" : "") + '}';
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Builder
/*      */   {
/*      */     private final HashMap<String, Object> config;
/*      */ 
/*      */ 
/*      */     
/*      */     private Builder(HashMap<String, Object> param1HashMap) {
/* 3830 */       this.config = param1HashMap;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder() {
/* 3837 */       this.config = new HashMap<>();
/*      */     }
/*      */     
/*      */     private Builder set(String param1String, Object param1Object) {
/* 3841 */       this.config.put(param1String, param1Object);
/* 3842 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder autoCommitDisabled() {
/* 3857 */       return set("autoCommitDelay", Integer.valueOf(0));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder autoCommitBufferSize(int param1Int) {
/* 3874 */       return set("autoCommitBufferSize", Integer.valueOf(param1Int));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder autoCompactFillRate(int param1Int) {
/* 3892 */       return set("autoCompactFillRate", Integer.valueOf(param1Int));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder fileName(String param1String) {
/* 3903 */       return set("fileName", param1String);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder encryptionKey(char[] param1ArrayOfchar) {
/* 3918 */       return set("encryptionKey", param1ArrayOfchar);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder readOnly() {
/* 3934 */       return set("readOnly", Integer.valueOf(1));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder keysPerPage(int param1Int) {
/* 3944 */       return set("keysPerPage", Integer.valueOf(param1Int));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder recoveryMode() {
/* 3953 */       return set("recoveryMode", Integer.valueOf(1));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder cacheSize(int param1Int) {
/* 3963 */       return set("cacheSize", Integer.valueOf(param1Int));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder cacheConcurrency(int param1Int) {
/* 3974 */       return set("cacheConcurrency", Integer.valueOf(param1Int));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder compress() {
/* 3989 */       return set("compress", Integer.valueOf(1));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder compressHigh() {
/* 4004 */       return set("compress", Integer.valueOf(2));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder pageSplitSize(int param1Int) {
/* 4018 */       return set("pageSplitSize", Integer.valueOf(param1Int));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder backgroundExceptionHandler(Thread.UncaughtExceptionHandler param1UncaughtExceptionHandler) {
/* 4030 */       return set("backgroundExceptionHandler", param1UncaughtExceptionHandler);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder fileStore(FileStore param1FileStore) {
/* 4047 */       return set("fileStore", param1FileStore);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public MVStore open() {
/* 4056 */       return new MVStore(this.config);
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/* 4061 */       return DataUtils.appendMap(new StringBuilder(), this.config).toString();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static Builder fromString(String param1String) {
/* 4073 */       return new Builder((HashMap)DataUtils.parseMap(param1String));
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\MVStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */