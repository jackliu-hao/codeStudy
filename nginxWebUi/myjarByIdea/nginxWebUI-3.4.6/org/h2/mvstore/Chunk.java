package org.h2.mvstore;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Map;
import org.h2.util.StringUtils;

public final class Chunk {
   public static final int MAX_ID = 67108863;
   static final int MAX_HEADER_LENGTH = 1024;
   static final int FOOTER_LENGTH = 128;
   private static final String ATTR_CHUNK = "chunk";
   private static final String ATTR_BLOCK = "block";
   private static final String ATTR_LEN = "len";
   private static final String ATTR_MAP = "map";
   private static final String ATTR_MAX = "max";
   private static final String ATTR_NEXT = "next";
   private static final String ATTR_PAGES = "pages";
   private static final String ATTR_ROOT = "root";
   private static final String ATTR_TIME = "time";
   private static final String ATTR_VERSION = "version";
   private static final String ATTR_LIVE_MAX = "liveMax";
   private static final String ATTR_LIVE_PAGES = "livePages";
   private static final String ATTR_UNUSED = "unused";
   private static final String ATTR_UNUSED_AT_VERSION = "unusedAtVersion";
   private static final String ATTR_PIN_COUNT = "pinCount";
   private static final String ATTR_TOC = "toc";
   private static final String ATTR_OCCUPANCY = "occupancy";
   private static final String ATTR_FLETCHER = "fletcher";
   public final int id;
   public volatile long block;
   public int len;
   int pageCount;
   int pageCountLive;
   int tocPos;
   BitSet occupancy;
   public long maxLen;
   public long maxLenLive;
   int collectPriority;
   long layoutRootPos;
   public long version;
   public long time;
   public long unused;
   long unusedAtVersion;
   public int mapId;
   public long next;
   private int pinCount;

   private Chunk(String var1) {
      this(DataUtils.parseMap(var1), true);
   }

   Chunk(Map<String, String> var1) {
      this(var1, false);
   }

   private Chunk(Map<String, String> var1, boolean var2) {
      this(DataUtils.readHexInt(var1, "chunk", 0));
      this.block = DataUtils.readHexLong(var1, "block", 0L);
      this.version = DataUtils.readHexLong(var1, "version", (long)this.id);
      if (var2) {
         this.len = DataUtils.readHexInt(var1, "len", 0);
         this.pageCount = DataUtils.readHexInt(var1, "pages", 0);
         this.pageCountLive = DataUtils.readHexInt(var1, "livePages", this.pageCount);
         this.mapId = DataUtils.readHexInt(var1, "map", 0);
         this.maxLen = DataUtils.readHexLong(var1, "max", 0L);
         this.maxLenLive = DataUtils.readHexLong(var1, "liveMax", this.maxLen);
         this.layoutRootPos = DataUtils.readHexLong(var1, "root", 0L);
         this.time = DataUtils.readHexLong(var1, "time", 0L);
         this.unused = DataUtils.readHexLong(var1, "unused", 0L);
         this.unusedAtVersion = DataUtils.readHexLong(var1, "unusedAtVersion", 0L);
         this.next = DataUtils.readHexLong(var1, "next", 0L);
         this.pinCount = DataUtils.readHexInt(var1, "pinCount", 0);
         this.tocPos = DataUtils.readHexInt(var1, "toc", 0);
         byte[] var3 = DataUtils.parseHexBytes(var1, "occupancy");
         if (var3 == null) {
            this.occupancy = new BitSet();
         } else {
            this.occupancy = BitSet.valueOf(var3);
            if (this.pageCount - this.pageCountLive != this.occupancy.cardinality()) {
               throw DataUtils.newMVStoreException(6, "Inconsistent occupancy info {0} - {1} != {2} {3}", this.pageCount, this.pageCountLive, this.occupancy.cardinality(), this);
            }
         }
      }

   }

   Chunk(int var1) {
      this.id = var1;
      if (var1 <= 0) {
         throw DataUtils.newMVStoreException(6, "Invalid chunk id {0}", var1);
      }
   }

   static Chunk readChunkHeader(ByteBuffer var0, long var1) {
      int var3 = var0.position();
      byte[] var4 = new byte[Math.min(var0.remaining(), 1024)];
      var0.get(var4);

      try {
         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (var4[var5] == 10) {
               var0.position(var3 + var5 + 1);
               String var6 = (new String(var4, 0, var5, StandardCharsets.ISO_8859_1)).trim();
               return fromString(var6);
            }
         }
      } catch (Exception var7) {
         throw DataUtils.newMVStoreException(6, "File corrupt reading chunk at position {0}", var1, var7);
      }

      throw DataUtils.newMVStoreException(6, "File corrupt reading chunk at position {0}", var1);
   }

   void writeChunkHeader(WriteBuffer var1, int var2) {
      long var3 = (long)(var1.position() + var2 - 1);
      var1.put(this.asString().getBytes(StandardCharsets.ISO_8859_1));

      while((long)var1.position() < var3) {
         var1.put((byte)32);
      }

      if (var2 != 0 && (long)var1.position() > var3) {
         throw DataUtils.newMVStoreException(3, "Chunk metadata too long");
      } else {
         var1.put((byte)10);
      }
   }

   static String getMetaKey(int var0) {
      return "chunk." + Integer.toHexString(var0);
   }

   public static Chunk fromString(String var0) {
      return new Chunk(var0);
   }

   int getFillRate() {
      assert this.maxLenLive <= this.maxLen : this.maxLenLive + " > " + this.maxLen;

      if (this.maxLenLive <= 0L) {
         return 0;
      } else {
         return this.maxLenLive == this.maxLen ? 100 : 1 + (int)(98L * this.maxLenLive / this.maxLen);
      }
   }

   public int hashCode() {
      return this.id;
   }

   public boolean equals(Object var1) {
      return var1 instanceof Chunk && ((Chunk)var1).id == this.id;
   }

   public String asString() {
      StringBuilder var1 = new StringBuilder(240);
      DataUtils.appendMap(var1, "chunk", this.id);
      DataUtils.appendMap(var1, "block", this.block);
      DataUtils.appendMap(var1, "len", this.len);
      if (this.maxLen != this.maxLenLive) {
         DataUtils.appendMap(var1, "liveMax", this.maxLenLive);
      }

      if (this.pageCount != this.pageCountLive) {
         DataUtils.appendMap(var1, "livePages", this.pageCountLive);
      }

      DataUtils.appendMap(var1, "map", this.mapId);
      DataUtils.appendMap(var1, "max", this.maxLen);
      if (this.next != 0L) {
         DataUtils.appendMap(var1, "next", this.next);
      }

      DataUtils.appendMap(var1, "pages", this.pageCount);
      DataUtils.appendMap(var1, "root", this.layoutRootPos);
      DataUtils.appendMap(var1, "time", this.time);
      if (this.unused != 0L) {
         DataUtils.appendMap(var1, "unused", this.unused);
      }

      if (this.unusedAtVersion != 0L) {
         DataUtils.appendMap(var1, "unusedAtVersion", this.unusedAtVersion);
      }

      DataUtils.appendMap(var1, "version", this.version);
      if (this.pinCount > 0) {
         DataUtils.appendMap(var1, "pinCount", this.pinCount);
      }

      if (this.tocPos > 0) {
         DataUtils.appendMap(var1, "toc", this.tocPos);
      }

      if (!this.occupancy.isEmpty()) {
         DataUtils.appendMap(var1, "occupancy", StringUtils.convertBytesToHex(this.occupancy.toByteArray()));
      }

      return var1.toString();
   }

   byte[] getFooterBytes() {
      StringBuilder var1 = new StringBuilder(128);
      DataUtils.appendMap(var1, "chunk", this.id);
      DataUtils.appendMap(var1, "block", this.block);
      DataUtils.appendMap(var1, "version", this.version);
      byte[] var2 = var1.toString().getBytes(StandardCharsets.ISO_8859_1);
      int var3 = DataUtils.getFletcher32(var2, 0, var2.length);
      DataUtils.appendMap(var1, "fletcher", var3);

      while(var1.length() < 127) {
         var1.append(' ');
      }

      var1.append('\n');
      return var1.toString().getBytes(StandardCharsets.ISO_8859_1);
   }

   boolean isSaved() {
      return this.block != Long.MAX_VALUE;
   }

   boolean isLive() {
      return this.pageCountLive > 0;
   }

   boolean isRewritable() {
      return this.isSaved() && this.isLive() && this.pageCountLive < this.pageCount && this.isEvacuatable();
   }

   private boolean isEvacuatable() {
      return this.pinCount == 0;
   }

   ByteBuffer readBufferForPage(FileStore var1, int var2, long var3) {
      assert this.isSaved() : this;

      while(true) {
         long var5 = this.block;

         try {
            long var7 = var5 * 4096L;
            long var9 = var7 + (long)this.len * 4096L;
            var7 += (long)var2;
            if (var7 < 0L) {
               throw DataUtils.newMVStoreException(6, "Negative position {0}; p={1}, c={2}", var7, var3, this.toString());
            }

            int var11 = DataUtils.getPageMaxLength(var3);
            if (var11 == 2097152) {
               var11 = var1.readFully(var7, 128).getInt();
               var11 += 4;
            }

            var11 = (int)Math.min(var9 - var7, (long)var11);
            if (var11 < 0) {
               throw DataUtils.newMVStoreException(6, "Illegal page length {0} reading at {1}; max pos {2} ", var11, var7, var9);
            }

            ByteBuffer var12 = var1.readFully(var7, var11);
            if (var5 == this.block) {
               return var12;
            }
         } catch (MVStoreException var13) {
            if (var5 == this.block) {
               throw var13;
            }
         }
      }
   }

   long[] readToC(FileStore var1) {
      assert this.isSaved() : this;

      assert this.tocPos > 0;

      while(true) {
         long var2 = this.block;

         try {
            long var4 = var2 * 4096L + (long)this.tocPos;
            int var6 = this.pageCount * 8;
            long[] var7 = new long[this.pageCount];
            var1.readFully(var4, var6).asLongBuffer().get(var7);
            if (var2 == this.block) {
               return var7;
            }
         } catch (MVStoreException var8) {
            if (var2 == this.block) {
               throw var8;
            }
         }
      }
   }

   void accountForWrittenPage(int var1, boolean var2) {
      this.maxLen += (long)var1;
      ++this.pageCount;
      this.maxLenLive += (long)var1;
      ++this.pageCountLive;
      if (var2) {
         ++this.pinCount;
      }

      assert this.pageCount - this.pageCountLive == this.occupancy.cardinality() : this.pageCount + " - " + this.pageCountLive + " <> " + this.occupancy.cardinality() + " : " + this.occupancy;

   }

   boolean accountForRemovedPage(int var1, int var2, boolean var3, long var4, long var6) {
      assert this.isSaved() : this;

      if (this.tocPos > 0) {
         assert var1 >= 0 && var1 < this.pageCount : var1 + " // " + this.pageCount;

         assert !this.occupancy.get(var1) : var1 + " " + this + " " + this.occupancy;

         assert this.pageCount - this.pageCountLive == this.occupancy.cardinality() : this.pageCount + " - " + this.pageCountLive + " <> " + this.occupancy.cardinality() + " : " + this.occupancy;

         this.occupancy.set(var1);
      }

      this.maxLenLive -= (long)var2;
      --this.pageCountLive;
      if (var3) {
         --this.pinCount;
      }

      if (this.unusedAtVersion < var6) {
         this.unusedAtVersion = var6;
      }

      assert this.pinCount >= 0 : this;

      assert this.pageCountLive >= 0 : this;

      assert this.pinCount <= this.pageCountLive : this;

      assert this.maxLenLive >= 0L : this;

      assert this.pageCountLive == 0 == (this.maxLenLive == 0L) : this;

      if (!this.isLive()) {
         this.unused = var4;
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      return this.asString();
   }

   public static final class PositionComparator implements Comparator<Chunk> {
      public static final Comparator<Chunk> INSTANCE = new PositionComparator();

      private PositionComparator() {
      }

      public int compare(Chunk var1, Chunk var2) {
         return Long.compare(var1.block, var2.block);
      }
   }
}
