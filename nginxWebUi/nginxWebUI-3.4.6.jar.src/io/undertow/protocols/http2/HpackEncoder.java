/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ public class HpackEncoder
/*     */ {
/*     */   private static final Set<HttpString> SKIP;
/*     */   
/*     */   static {
/*  52 */     Set<HttpString> set = new HashSet<>();
/*  53 */     set.add(Headers.CONNECTION);
/*  54 */     set.add(Headers.TRANSFER_ENCODING);
/*  55 */     set.add(Headers.KEEP_ALIVE);
/*  56 */     set.add(Headers.UPGRADE);
/*  57 */     SKIP = Collections.unmodifiableSet(set);
/*     */   }
/*     */   
/*  60 */   public static final HpackHeaderFunction DEFAULT_HEADER_FUNCTION = new HpackHeaderFunction()
/*     */     {
/*     */       
/*     */       public boolean shouldUseIndexing(HttpString headerName, String value)
/*     */       {
/*  65 */         return (!headerName.equals(Headers.CONTENT_LENGTH) && !headerName.equals(Headers.DATE));
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean shouldUseHuffman(HttpString header, String value) {
/*  70 */         return (value.length() > 10);
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean shouldUseHuffman(HttpString header) {
/*  75 */         return (header.length() > 10);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */   
/*  81 */   private long headersIterator = -1L;
/*     */   
/*     */   private boolean firstPass = true;
/*     */   
/*     */   private HeaderMap currentHeaders;
/*     */   
/*     */   private int entryPositionCounter;
/*  88 */   private int newMaxHeaderSize = -1;
/*  89 */   private int minNewMaxHeaderSize = -1;
/*     */   
/*     */   private static final Map<HttpString, TableEntry[]> ENCODING_STATIC_TABLE;
/*     */   
/*  93 */   private final Deque<TableEntry> evictionQueue = new ArrayDeque<>();
/*  94 */   private final Map<HttpString, List<TableEntry>> dynamicTable = new HashMap<>();
/*     */   
/*     */   private byte[] overflowData;
/*     */   private int overflowPos;
/*     */   private int overflowLength;
/*     */   
/*     */   static {
/* 101 */     Map<HttpString, TableEntry[]> map = (Map)new HashMap<>();
/* 102 */     for (int i = 1; i < Hpack.STATIC_TABLE.length; i++) {
/* 103 */       Hpack.HeaderField m = Hpack.STATIC_TABLE[i];
/* 104 */       TableEntry[] existing = map.get(m.name);
/* 105 */       if (existing == null) {
/* 106 */         map.put(m.name, new TableEntry[] { new TableEntry(m.name, m.value, i) });
/*     */       } else {
/* 108 */         TableEntry[] newEntry = new TableEntry[existing.length + 1];
/* 109 */         System.arraycopy(existing, 0, newEntry, 0, existing.length);
/* 110 */         newEntry[existing.length] = new TableEntry(m.name, m.value, i);
/* 111 */         map.put(m.name, newEntry);
/*     */       } 
/*     */     } 
/* 114 */     ENCODING_STATIC_TABLE = Collections.unmodifiableMap((Map)map);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int maxTableSize;
/*     */ 
/*     */   
/*     */   private int currentTableSize;
/*     */ 
/*     */   
/*     */   private final HpackHeaderFunction hpackHeaderFunction;
/*     */ 
/*     */ 
/*     */   
/*     */   public HpackEncoder(int maxTableSize, HpackHeaderFunction headerFunction) {
/* 130 */     this.maxTableSize = maxTableSize;
/* 131 */     this.hpackHeaderFunction = headerFunction;
/*     */   }
/*     */   
/*     */   public HpackEncoder(int maxTableSize) {
/* 135 */     this(maxTableSize, DEFAULT_HEADER_FUNCTION);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public State encode(HeaderMap headers, ByteBuffer target) {
/* 145 */     if (this.overflowData != null) {
/* 146 */       for (int i = this.overflowPos; i < this.overflowLength; i++) {
/* 147 */         if (!target.hasRemaining()) {
/* 148 */           this.overflowPos = i;
/* 149 */           return State.OVERFLOW;
/*     */         } 
/* 151 */         target.put(this.overflowData[i]);
/*     */       } 
/* 153 */       this.overflowData = null;
/*     */     } 
/*     */     
/* 156 */     long it = this.headersIterator;
/* 157 */     if (this.headersIterator == -1L) {
/* 158 */       handleTableSizeChange(target);
/*     */       
/* 160 */       it = headers.fastIterate();
/* 161 */       this.currentHeaders = headers;
/*     */     } else {
/* 163 */       if (headers != this.currentHeaders) {
/* 164 */         throw new IllegalStateException();
/*     */       }
/* 166 */       it = headers.fiNext(it);
/*     */     } 
/* 168 */     while (it != -1L) {
/* 169 */       HeaderValues values = headers.fiCurrent(it);
/* 170 */       boolean skip = false;
/* 171 */       if (this.firstPass) {
/* 172 */         if (values.getHeaderName().byteAt(0) != 58) {
/* 173 */           skip = true;
/*     */         }
/*     */       }
/* 176 */       else if (values.getHeaderName().byteAt(0) == 58) {
/* 177 */         skip = true;
/*     */       } 
/*     */       
/* 180 */       if (SKIP.contains(values.getHeaderName()))
/*     */       {
/* 182 */         skip = true;
/*     */       }
/* 184 */       if (!skip) {
/* 185 */         for (int i = 0; i < values.size(); i++) {
/*     */           
/* 187 */           HttpString headerName = values.getHeaderName();
/* 188 */           int required = 11 + headerName.length();
/*     */           
/* 190 */           String val = values.get(i);
/* 191 */           for (int v = 0; v < val.length(); v++) {
/* 192 */             char c = val.charAt(v);
/* 193 */             if (c == '\r' || c == '\n') {
/* 194 */               val = val.replace('\r', ' ').replace('\n', ' ');
/*     */               break;
/*     */             } 
/*     */           } 
/* 198 */           TableEntry tableEntry = findInTable(headerName, val);
/*     */           
/* 200 */           required += 1 + val.length();
/* 201 */           boolean overflowing = false;
/*     */           
/* 203 */           ByteBuffer current = target;
/* 204 */           if (current.remaining() < required) {
/* 205 */             overflowing = true;
/* 206 */             current = ByteBuffer.wrap(this.overflowData = new byte[required]);
/* 207 */             this.overflowPos = 0;
/*     */           } 
/* 209 */           boolean canIndex = (this.hpackHeaderFunction.shouldUseIndexing(headerName, val) && headerName.length() + val.length() + 32 < this.maxTableSize);
/* 210 */           if (tableEntry == null && canIndex) {
/*     */             
/* 212 */             current.put((byte)64);
/* 213 */             writeHuffmanEncodableName(current, headerName);
/* 214 */             writeHuffmanEncodableValue(current, headerName, val);
/* 215 */             addToDynamicTable(headerName, val);
/* 216 */           } else if (tableEntry == null) {
/*     */             
/* 218 */             current.put((byte)16);
/* 219 */             writeHuffmanEncodableName(current, headerName);
/* 220 */             writeHuffmanEncodableValue(current, headerName, val);
/*     */           
/*     */           }
/* 223 */           else if (val.equals(tableEntry.value)) {
/*     */             
/* 225 */             current.put(-128);
/* 226 */             Hpack.encodeInteger(current, tableEntry.getPosition(), 7);
/*     */           }
/* 228 */           else if (canIndex) {
/*     */             
/* 230 */             current.put((byte)64);
/* 231 */             Hpack.encodeInteger(current, tableEntry.getPosition(), 6);
/* 232 */             writeHuffmanEncodableValue(current, headerName, val);
/* 233 */             addToDynamicTable(headerName, val);
/*     */           } else {
/*     */             
/* 236 */             current.put((byte)16);
/* 237 */             Hpack.encodeInteger(current, tableEntry.getPosition(), 4);
/* 238 */             writeHuffmanEncodableValue(current, headerName, val);
/*     */           } 
/*     */ 
/*     */           
/* 242 */           if (overflowing) {
/* 243 */             this.headersIterator = it;
/* 244 */             this.overflowLength = current.position();
/* 245 */             return State.OVERFLOW;
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 250 */       it = headers.fiNext(it);
/* 251 */       if (it == -1L && this.firstPass) {
/* 252 */         this.firstPass = false;
/* 253 */         it = headers.fastIterate();
/*     */       } 
/*     */     } 
/* 256 */     this.headersIterator = -1L;
/* 257 */     this.firstPass = true;
/* 258 */     return State.COMPLETE;
/*     */   }
/*     */   
/*     */   private void writeHuffmanEncodableName(ByteBuffer target, HttpString headerName) {
/* 262 */     if (this.hpackHeaderFunction.shouldUseHuffman(headerName) && 
/* 263 */       HPackHuffman.encode(target, headerName.toString(), true)) {
/*     */       return;
/*     */     }
/*     */     
/* 267 */     target.put((byte)0);
/* 268 */     Hpack.encodeInteger(target, headerName.length(), 7);
/* 269 */     for (int j = 0; j < headerName.length(); j++) {
/* 270 */       target.put(Hpack.toLower(headerName.byteAt(j)));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeHuffmanEncodableValue(ByteBuffer target, HttpString headerName, String val) {
/* 276 */     if (this.hpackHeaderFunction.shouldUseHuffman(headerName, val)) {
/* 277 */       if (!HPackHuffman.encode(target, val, false)) {
/* 278 */         writeValueString(target, val);
/*     */       }
/*     */     } else {
/* 281 */       writeValueString(target, val);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeValueString(ByteBuffer target, String val) {
/* 286 */     target.put((byte)0);
/* 287 */     Hpack.encodeInteger(target, val.length(), 7);
/* 288 */     for (int j = 0; j < val.length(); j++) {
/* 289 */       target.put((byte)val.charAt(j));
/*     */     }
/*     */   }
/*     */   
/*     */   private void addToDynamicTable(HttpString headerName, String val) {
/* 294 */     int pos = this.entryPositionCounter++;
/* 295 */     DynamicTableEntry d = new DynamicTableEntry(headerName, val, -pos);
/* 296 */     List<TableEntry> existing = this.dynamicTable.get(headerName);
/* 297 */     if (existing == null) {
/* 298 */       this.dynamicTable.put(headerName, existing = new ArrayList<>(1));
/*     */     }
/* 300 */     existing.add(d);
/* 301 */     this.evictionQueue.add(d);
/* 302 */     this.currentTableSize += d.size;
/* 303 */     runEvictionIfRequired();
/* 304 */     if (this.entryPositionCounter == Integer.MAX_VALUE)
/*     */     {
/* 306 */       preventPositionRollover();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void preventPositionRollover() {
/* 315 */     for (Map.Entry<HttpString, List<TableEntry>> entry : this.dynamicTable.entrySet()) {
/* 316 */       for (TableEntry t : entry.getValue()) {
/* 317 */         t.position = t.getPosition();
/*     */       }
/*     */     } 
/* 320 */     this.entryPositionCounter = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   private void runEvictionIfRequired() {
/* 325 */     while (this.currentTableSize > this.maxTableSize) {
/* 326 */       TableEntry next = this.evictionQueue.poll();
/* 327 */       if (next == null) {
/*     */         return;
/*     */       }
/* 330 */       this.currentTableSize -= next.size;
/* 331 */       List<TableEntry> list = this.dynamicTable.get(next.name);
/* 332 */       list.remove(next);
/* 333 */       if (list.isEmpty()) {
/* 334 */         this.dynamicTable.remove(next.name);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private TableEntry findInTable(HttpString headerName, String value) {
/* 340 */     TableEntry[] staticTable = ENCODING_STATIC_TABLE.get(headerName);
/* 341 */     if (staticTable != null) {
/* 342 */       for (TableEntry st : staticTable) {
/* 343 */         if (st.value != null && st.value.equals(value)) {
/* 344 */           return st;
/*     */         }
/*     */       } 
/*     */     }
/* 348 */     List<TableEntry> dynamic = this.dynamicTable.get(headerName);
/* 349 */     if (dynamic != null) {
/* 350 */       for (int i = 0; i < dynamic.size(); i++) {
/* 351 */         TableEntry st = dynamic.get(i);
/* 352 */         if (st.value.equals(value)) {
/* 353 */           return st;
/*     */         }
/*     */       } 
/*     */     }
/* 357 */     if (staticTable != null) {
/* 358 */       return staticTable[0];
/*     */     }
/* 360 */     return null;
/*     */   }
/*     */   
/*     */   public void setMaxTableSize(int newSize) {
/* 364 */     this.newMaxHeaderSize = newSize;
/* 365 */     if (this.minNewMaxHeaderSize == -1) {
/* 366 */       this.minNewMaxHeaderSize = newSize;
/*     */     } else {
/* 368 */       this.minNewMaxHeaderSize = Math.min(newSize, this.minNewMaxHeaderSize);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleTableSizeChange(ByteBuffer target) {
/* 373 */     if (this.newMaxHeaderSize == -1) {
/*     */       return;
/*     */     }
/* 376 */     if (this.minNewMaxHeaderSize != this.newMaxHeaderSize) {
/* 377 */       target.put((byte)32);
/* 378 */       Hpack.encodeInteger(target, this.minNewMaxHeaderSize, 5);
/*     */     } 
/* 380 */     target.put((byte)32);
/* 381 */     Hpack.encodeInteger(target, this.newMaxHeaderSize, 5);
/* 382 */     this.maxTableSize = this.newMaxHeaderSize;
/* 383 */     runEvictionIfRequired();
/* 384 */     this.newMaxHeaderSize = -1;
/* 385 */     this.minNewMaxHeaderSize = -1;
/*     */   }
/*     */   
/*     */   public enum State {
/* 389 */     COMPLETE,
/* 390 */     OVERFLOW;
/*     */   }
/*     */   
/*     */   static class TableEntry {
/*     */     final HttpString name;
/*     */     final String value;
/*     */     final int size;
/*     */     int position;
/*     */     
/*     */     TableEntry(HttpString name, String value, int position) {
/* 400 */       this.name = name;
/* 401 */       this.value = value;
/* 402 */       this.position = position;
/* 403 */       if (value != null) {
/* 404 */         this.size = 32 + name.length() + value.length();
/*     */       } else {
/* 406 */         this.size = -1;
/*     */       } 
/*     */     }
/*     */     
/*     */     public int getPosition() {
/* 411 */       return this.position;
/*     */     }
/*     */   }
/*     */   
/*     */   class DynamicTableEntry
/*     */     extends TableEntry {
/*     */     DynamicTableEntry(HttpString name, String value, int position) {
/* 418 */       super(name, value, position);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getPosition() {
/* 423 */       return super.getPosition() + HpackEncoder.this.entryPositionCounter + Hpack.STATIC_TABLE_LENGTH;
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface HpackHeaderFunction {
/*     */     boolean shouldUseIndexing(HttpString param1HttpString, String param1String);
/*     */     
/*     */     boolean shouldUseHuffman(HttpString param1HttpString, String param1String);
/*     */     
/*     */     boolean shouldUseHuffman(HttpString param1HttpString);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\HpackEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */