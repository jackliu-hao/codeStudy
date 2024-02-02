package io.undertow.protocols.http2;

import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HpackEncoder {
   private static final Set<HttpString> SKIP;
   public static final HpackHeaderFunction DEFAULT_HEADER_FUNCTION;
   private long headersIterator;
   private boolean firstPass;
   private HeaderMap currentHeaders;
   private int entryPositionCounter;
   private int newMaxHeaderSize;
   private int minNewMaxHeaderSize;
   private static final Map<HttpString, TableEntry[]> ENCODING_STATIC_TABLE;
   private final Deque<TableEntry> evictionQueue;
   private final Map<HttpString, List<TableEntry>> dynamicTable;
   private byte[] overflowData;
   private int overflowPos;
   private int overflowLength;
   private int maxTableSize;
   private int currentTableSize;
   private final HpackHeaderFunction hpackHeaderFunction;

   public HpackEncoder(int maxTableSize, HpackHeaderFunction headerFunction) {
      this.headersIterator = -1L;
      this.firstPass = true;
      this.newMaxHeaderSize = -1;
      this.minNewMaxHeaderSize = -1;
      this.evictionQueue = new ArrayDeque();
      this.dynamicTable = new HashMap();
      this.maxTableSize = maxTableSize;
      this.hpackHeaderFunction = headerFunction;
   }

   public HpackEncoder(int maxTableSize) {
      this(maxTableSize, DEFAULT_HEADER_FUNCTION);
   }

   public State encode(HeaderMap headers, ByteBuffer target) {
      if (this.overflowData != null) {
         for(int i = this.overflowPos; i < this.overflowLength; ++i) {
            if (!target.hasRemaining()) {
               this.overflowPos = i;
               return HpackEncoder.State.OVERFLOW;
            }

            target.put(this.overflowData[i]);
         }

         this.overflowData = null;
      }

      long it = this.headersIterator;
      if (this.headersIterator == -1L) {
         this.handleTableSizeChange(target);
         it = headers.fastIterate();
         this.currentHeaders = headers;
      } else {
         if (headers != this.currentHeaders) {
            throw new IllegalStateException();
         }

         it = headers.fiNext(it);
      }

      while(it != -1L) {
         HeaderValues values = headers.fiCurrent(it);
         boolean skip = false;
         if (this.firstPass) {
            if (values.getHeaderName().byteAt(0) != 58) {
               skip = true;
            }
         } else if (values.getHeaderName().byteAt(0) == 58) {
            skip = true;
         }

         if (SKIP.contains(values.getHeaderName())) {
            skip = true;
         }

         if (!skip) {
            for(int i = 0; i < values.size(); ++i) {
               HttpString headerName = values.getHeaderName();
               int required = 11 + headerName.length();
               String val = values.get(i);

               for(int v = 0; v < val.length(); ++v) {
                  char c = val.charAt(v);
                  if (c == '\r' || c == '\n') {
                     val = val.replace('\r', ' ').replace('\n', ' ');
                     break;
                  }
               }

               TableEntry tableEntry = this.findInTable(headerName, val);
               required += 1 + val.length();
               boolean overflowing = false;
               ByteBuffer current = target;
               if (target.remaining() < required) {
                  overflowing = true;
                  current = ByteBuffer.wrap(this.overflowData = new byte[required]);
                  this.overflowPos = 0;
               }

               boolean canIndex = this.hpackHeaderFunction.shouldUseIndexing(headerName, val) && headerName.length() + val.length() + 32 < this.maxTableSize;
               if (tableEntry == null && canIndex) {
                  current.put((byte)64);
                  this.writeHuffmanEncodableName(current, headerName);
                  this.writeHuffmanEncodableValue(current, headerName, val);
                  this.addToDynamicTable(headerName, val);
               } else if (tableEntry == null) {
                  current.put((byte)16);
                  this.writeHuffmanEncodableName(current, headerName);
                  this.writeHuffmanEncodableValue(current, headerName, val);
               } else if (val.equals(tableEntry.value)) {
                  current.put((byte)-128);
                  Hpack.encodeInteger(current, tableEntry.getPosition(), 7);
               } else if (canIndex) {
                  current.put((byte)64);
                  Hpack.encodeInteger(current, tableEntry.getPosition(), 6);
                  this.writeHuffmanEncodableValue(current, headerName, val);
                  this.addToDynamicTable(headerName, val);
               } else {
                  current.put((byte)16);
                  Hpack.encodeInteger(current, tableEntry.getPosition(), 4);
                  this.writeHuffmanEncodableValue(current, headerName, val);
               }

               if (overflowing) {
                  this.headersIterator = it;
                  this.overflowLength = current.position();
                  return HpackEncoder.State.OVERFLOW;
               }
            }
         }

         it = headers.fiNext(it);
         if (it == -1L && this.firstPass) {
            this.firstPass = false;
            it = headers.fastIterate();
         }
      }

      this.headersIterator = -1L;
      this.firstPass = true;
      return HpackEncoder.State.COMPLETE;
   }

   private void writeHuffmanEncodableName(ByteBuffer target, HttpString headerName) {
      if (!this.hpackHeaderFunction.shouldUseHuffman(headerName) || !HPackHuffman.encode(target, headerName.toString(), true)) {
         target.put((byte)0);
         Hpack.encodeInteger(target, headerName.length(), 7);

         for(int j = 0; j < headerName.length(); ++j) {
            target.put(Hpack.toLower(headerName.byteAt(j)));
         }

      }
   }

   private void writeHuffmanEncodableValue(ByteBuffer target, HttpString headerName, String val) {
      if (this.hpackHeaderFunction.shouldUseHuffman(headerName, val)) {
         if (!HPackHuffman.encode(target, val, false)) {
            this.writeValueString(target, val);
         }
      } else {
         this.writeValueString(target, val);
      }

   }

   private void writeValueString(ByteBuffer target, String val) {
      target.put((byte)0);
      Hpack.encodeInteger(target, val.length(), 7);

      for(int j = 0; j < val.length(); ++j) {
         target.put((byte)val.charAt(j));
      }

   }

   private void addToDynamicTable(HttpString headerName, String val) {
      int pos = this.entryPositionCounter++;
      DynamicTableEntry d = new DynamicTableEntry(headerName, val, -pos);
      List<TableEntry> existing = (List)this.dynamicTable.get(headerName);
      if (existing == null) {
         this.dynamicTable.put(headerName, existing = new ArrayList(1));
      }

      ((List)existing).add(d);
      this.evictionQueue.add(d);
      this.currentTableSize += d.size;
      this.runEvictionIfRequired();
      if (this.entryPositionCounter == Integer.MAX_VALUE) {
         this.preventPositionRollover();
      }

   }

   private void preventPositionRollover() {
      Iterator var1 = this.dynamicTable.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<HttpString, List<TableEntry>> entry = (Map.Entry)var1.next();

         TableEntry t;
         for(Iterator var3 = ((List)entry.getValue()).iterator(); var3.hasNext(); t.position = t.getPosition()) {
            t = (TableEntry)var3.next();
         }
      }

      this.entryPositionCounter = 0;
   }

   private void runEvictionIfRequired() {
      while(this.currentTableSize > this.maxTableSize) {
         TableEntry next = (TableEntry)this.evictionQueue.poll();
         if (next == null) {
            return;
         }

         this.currentTableSize -= next.size;
         List<TableEntry> list = (List)this.dynamicTable.get(next.name);
         list.remove(next);
         if (list.isEmpty()) {
            this.dynamicTable.remove(next.name);
         }
      }

   }

   private TableEntry findInTable(HttpString headerName, String value) {
      TableEntry[] staticTable = (TableEntry[])ENCODING_STATIC_TABLE.get(headerName);
      int i;
      if (staticTable != null) {
         TableEntry[] var4 = staticTable;
         i = staticTable.length;

         for(int var6 = 0; var6 < i; ++var6) {
            TableEntry st = var4[var6];
            if (st.value != null && st.value.equals(value)) {
               return st;
            }
         }
      }

      List<TableEntry> dynamic = (List)this.dynamicTable.get(headerName);
      if (dynamic != null) {
         for(i = 0; i < dynamic.size(); ++i) {
            TableEntry st = (TableEntry)dynamic.get(i);
            if (st.value.equals(value)) {
               return st;
            }
         }
      }

      return staticTable != null ? staticTable[0] : null;
   }

   public void setMaxTableSize(int newSize) {
      this.newMaxHeaderSize = newSize;
      if (this.minNewMaxHeaderSize == -1) {
         this.minNewMaxHeaderSize = newSize;
      } else {
         this.minNewMaxHeaderSize = Math.min(newSize, this.minNewMaxHeaderSize);
      }

   }

   private void handleTableSizeChange(ByteBuffer target) {
      if (this.newMaxHeaderSize != -1) {
         if (this.minNewMaxHeaderSize != this.newMaxHeaderSize) {
            target.put((byte)32);
            Hpack.encodeInteger(target, this.minNewMaxHeaderSize, 5);
         }

         target.put((byte)32);
         Hpack.encodeInteger(target, this.newMaxHeaderSize, 5);
         this.maxTableSize = this.newMaxHeaderSize;
         this.runEvictionIfRequired();
         this.newMaxHeaderSize = -1;
         this.minNewMaxHeaderSize = -1;
      }
   }

   static {
      Set<HttpString> set = new HashSet();
      set.add(Headers.CONNECTION);
      set.add(Headers.TRANSFER_ENCODING);
      set.add(Headers.KEEP_ALIVE);
      set.add(Headers.UPGRADE);
      SKIP = Collections.unmodifiableSet(set);
      DEFAULT_HEADER_FUNCTION = new HpackHeaderFunction() {
         public boolean shouldUseIndexing(HttpString headerName, String value) {
            return !headerName.equals(Headers.CONTENT_LENGTH) && !headerName.equals(Headers.DATE);
         }

         public boolean shouldUseHuffman(HttpString header, String value) {
            return value.length() > 10;
         }

         public boolean shouldUseHuffman(HttpString header) {
            return header.length() > 10;
         }
      };
      Map<HttpString, TableEntry[]> map = new HashMap();

      for(int i = 1; i < Hpack.STATIC_TABLE.length; ++i) {
         Hpack.HeaderField m = Hpack.STATIC_TABLE[i];
         TableEntry[] existing = (TableEntry[])map.get(m.name);
         if (existing == null) {
            map.put(m.name, new TableEntry[]{new TableEntry(m.name, m.value, i)});
         } else {
            TableEntry[] newEntry = new TableEntry[existing.length + 1];
            System.arraycopy(existing, 0, newEntry, 0, existing.length);
            newEntry[existing.length] = new TableEntry(m.name, m.value, i);
            map.put(m.name, newEntry);
         }
      }

      ENCODING_STATIC_TABLE = Collections.unmodifiableMap(map);
   }

   public interface HpackHeaderFunction {
      boolean shouldUseIndexing(HttpString var1, String var2);

      boolean shouldUseHuffman(HttpString var1, String var2);

      boolean shouldUseHuffman(HttpString var1);
   }

   class DynamicTableEntry extends TableEntry {
      DynamicTableEntry(HttpString name, String value, int position) {
         super(name, value, position);
      }

      public int getPosition() {
         return super.getPosition() + HpackEncoder.this.entryPositionCounter + Hpack.STATIC_TABLE_LENGTH;
      }
   }

   static class TableEntry {
      final HttpString name;
      final String value;
      final int size;
      int position;

      TableEntry(HttpString name, String value, int position) {
         this.name = name;
         this.value = value;
         this.position = position;
         if (value != null) {
            this.size = 32 + name.length() + value.length();
         } else {
            this.size = -1;
         }

      }

      public int getPosition() {
         return this.position;
      }
   }

   public static enum State {
      COMPLETE,
      OVERFLOW;
   }
}
