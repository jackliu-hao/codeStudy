/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class HpackDecoder
/*     */ {
/*     */   private static final int DEFAULT_RING_BUFFER_SIZE = 10;
/*     */   private HeaderEmitter headerEmitter;
/*     */   private Hpack.HeaderField[] headerTable;
/*  52 */   private int firstSlotPosition = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   private int filledTableSlots = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   private int currentMemorySize = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   private int specifiedMemorySize;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int maxAllowedMemorySize;
/*     */ 
/*     */   
/*     */   private boolean first = true;
/*     */ 
/*     */   
/*  76 */   private final StringBuilder stringBuilder = new StringBuilder();
/*     */   
/*     */   public HpackDecoder(int maxAllowedMemorySize) {
/*  79 */     this.specifiedMemorySize = Math.min(4096, maxAllowedMemorySize);
/*  80 */     this.maxAllowedMemorySize = maxAllowedMemorySize;
/*  81 */     this.headerTable = new Hpack.HeaderField[10];
/*     */   }
/*     */   
/*     */   public HpackDecoder() {
/*  85 */     this(4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void decode(ByteBuffer buffer, boolean moreData) throws HpackException {
/*  96 */     while (buffer.hasRemaining()) {
/*  97 */       int originalPos = buffer.position();
/*  98 */       byte b = buffer.get();
/*  99 */       if ((b & 0x80) != 0) {
/* 100 */         this.first = false;
/*     */         
/* 102 */         buffer.position(buffer.position() - 1);
/* 103 */         int index = Hpack.decodeInteger(buffer, 7);
/* 104 */         if (index == -1) {
/* 105 */           if (!moreData) {
/* 106 */             throw UndertowMessages.MESSAGES.hpackFailed();
/*     */           }
/* 108 */           buffer.position(originalPos); return;
/*     */         } 
/* 110 */         if (index == 0) {
/* 111 */           throw UndertowMessages.MESSAGES.zeroNotValidHeaderTableIndex();
/*     */         }
/* 113 */         handleIndex(index); continue;
/* 114 */       }  if ((b & 0x40) != 0) {
/* 115 */         this.first = false;
/*     */         
/* 117 */         HttpString headerName = readHeaderName(buffer, 6);
/* 118 */         if (headerName == null) {
/* 119 */           if (!moreData) {
/* 120 */             throw UndertowMessages.MESSAGES.hpackFailed();
/*     */           }
/* 122 */           buffer.position(originalPos);
/*     */           return;
/*     */         } 
/* 125 */         String headerValue = readHpackString(buffer);
/* 126 */         if (headerValue == null) {
/* 127 */           if (!moreData) {
/* 128 */             throw UndertowMessages.MESSAGES.hpackFailed();
/*     */           }
/* 130 */           buffer.position(originalPos);
/*     */           return;
/*     */         } 
/* 133 */         this.headerEmitter.emitHeader(headerName, headerValue, false);
/* 134 */         addEntryToHeaderTable(new Hpack.HeaderField(headerName, headerValue)); continue;
/* 135 */       }  if ((b & 0xF0) == 0) {
/* 136 */         this.first = false;
/*     */         
/* 138 */         HttpString headerName = readHeaderName(buffer, 4);
/* 139 */         if (headerName == null) {
/* 140 */           if (!moreData) {
/* 141 */             throw UndertowMessages.MESSAGES.hpackFailed();
/*     */           }
/* 143 */           buffer.position(originalPos);
/*     */           return;
/*     */         } 
/* 146 */         String headerValue = readHpackString(buffer);
/* 147 */         if (headerValue == null) {
/* 148 */           if (!moreData) {
/* 149 */             throw UndertowMessages.MESSAGES.hpackFailed();
/*     */           }
/* 151 */           buffer.position(originalPos);
/*     */           return;
/*     */         } 
/* 154 */         this.headerEmitter.emitHeader(headerName, headerValue, false); continue;
/* 155 */       }  if ((b & 0xF0) == 16) {
/* 156 */         this.first = false;
/*     */         
/* 158 */         HttpString headerName = readHeaderName(buffer, 4);
/* 159 */         if (headerName == null) {
/* 160 */           buffer.position(originalPos);
/*     */           return;
/*     */         } 
/* 163 */         String headerValue = readHpackString(buffer);
/* 164 */         if (headerValue == null) {
/* 165 */           if (!moreData) {
/* 166 */             throw UndertowMessages.MESSAGES.hpackFailed();
/*     */           }
/* 168 */           buffer.position(originalPos);
/*     */           return;
/*     */         } 
/* 171 */         this.headerEmitter.emitHeader(headerName, headerValue, true); continue;
/* 172 */       }  if ((b & 0xE0) == 32) {
/* 173 */         if (!this.first) {
/* 174 */           throw new HpackException();
/*     */         }
/*     */         
/* 177 */         if (!handleMaxMemorySizeChange(buffer, originalPos))
/*     */           return; 
/*     */         continue;
/*     */       } 
/* 181 */       throw UndertowMessages.MESSAGES.invalidHpackEncoding(b);
/*     */     } 
/*     */     
/* 184 */     if (!moreData) {
/* 185 */       this.first = true;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean handleMaxMemorySizeChange(ByteBuffer buffer, int originalPos) throws HpackException {
/* 190 */     buffer.position(buffer.position() - 1);
/* 191 */     int size = Hpack.decodeInteger(buffer, 5);
/* 192 */     if (size == -1) {
/* 193 */       buffer.position(originalPos);
/* 194 */       return false;
/*     */     } 
/* 196 */     if (size > this.maxAllowedMemorySize) {
/* 197 */       throw new HpackException(1);
/*     */     }
/* 199 */     this.specifiedMemorySize = size;
/* 200 */     if (this.currentMemorySize > this.specifiedMemorySize) {
/* 201 */       int newTableSlots = this.filledTableSlots;
/* 202 */       int tableLength = this.headerTable.length;
/* 203 */       int newSize = this.currentMemorySize;
/* 204 */       while (newSize > this.specifiedMemorySize) {
/* 205 */         int clearIndex = this.firstSlotPosition;
/* 206 */         this.firstSlotPosition++;
/* 207 */         if (this.firstSlotPosition == tableLength) {
/* 208 */           this.firstSlotPosition = 0;
/*     */         }
/* 210 */         Hpack.HeaderField oldData = this.headerTable[clearIndex];
/* 211 */         this.headerTable[clearIndex] = null;
/* 212 */         newSize -= oldData.size;
/* 213 */         newTableSlots--;
/*     */       } 
/* 215 */       this.filledTableSlots = newTableSlots;
/* 216 */       this.currentMemorySize = newSize;
/*     */     } 
/* 218 */     return true;
/*     */   }
/*     */   
/*     */   private HttpString readHeaderName(ByteBuffer buffer, int prefixLength) throws HpackException {
/* 222 */     buffer.position(buffer.position() - 1);
/* 223 */     int index = Hpack.decodeInteger(buffer, prefixLength);
/* 224 */     if (index == -1)
/* 225 */       return null; 
/* 226 */     if (index != 0) {
/* 227 */       return handleIndexedHeaderName(index);
/*     */     }
/* 229 */     String string = readHpackString(buffer);
/* 230 */     if (string == null)
/* 231 */       return null; 
/* 232 */     if (string.isEmpty())
/*     */     {
/* 234 */       throw new HpackException();
/*     */     }
/* 236 */     return new HttpString(string);
/*     */   }
/*     */ 
/*     */   
/*     */   private String readHpackString(ByteBuffer buffer) throws HpackException {
/* 241 */     if (!buffer.hasRemaining()) {
/* 242 */       return null;
/*     */     }
/* 244 */     byte data = buffer.get(buffer.position());
/*     */     
/* 246 */     int length = Hpack.decodeInteger(buffer, 7);
/* 247 */     if (buffer.remaining() < length || length == -1) {
/* 248 */       return null;
/*     */     }
/* 250 */     boolean huffman = ((data & 0x80) != 0);
/* 251 */     if (huffman) {
/* 252 */       return readHuffmanString(length, buffer);
/*     */     }
/* 254 */     for (int i = 0; i < length; i++) {
/* 255 */       this.stringBuilder.append((char)buffer.get());
/*     */     }
/* 257 */     String ret = this.stringBuilder.toString();
/* 258 */     this.stringBuilder.setLength(0);
/* 259 */     if (ret.isEmpty())
/*     */     {
/* 261 */       return "";
/*     */     }
/* 263 */     return ret;
/*     */   }
/*     */   
/*     */   private String readHuffmanString(int length, ByteBuffer buffer) throws HpackException {
/* 267 */     HPackHuffman.decode(buffer, length, this.stringBuilder);
/* 268 */     String ret = this.stringBuilder.toString();
/* 269 */     if (ret.isEmpty()) {
/* 270 */       return "";
/*     */     }
/* 272 */     this.stringBuilder.setLength(0);
/* 273 */     return ret;
/*     */   }
/*     */   
/*     */   private HttpString handleIndexedHeaderName(int index) throws HpackException {
/* 277 */     if (index <= Hpack.STATIC_TABLE_LENGTH) {
/* 278 */       return (Hpack.STATIC_TABLE[index]).name;
/*     */     }
/* 280 */     if (index > Hpack.STATIC_TABLE_LENGTH + this.filledTableSlots) {
/* 281 */       throw new HpackException();
/*     */     }
/* 283 */     int adjustedIndex = getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
/* 284 */     Hpack.HeaderField res = this.headerTable[adjustedIndex];
/* 285 */     if (res == null) {
/* 286 */       throw new HpackException();
/*     */     }
/* 288 */     return res.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void handleIndex(int index) throws HpackException {
/* 299 */     if (index <= Hpack.STATIC_TABLE_LENGTH) {
/* 300 */       addStaticTableEntry(index);
/*     */     } else {
/* 302 */       int adjustedIndex = getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
/* 303 */       Hpack.HeaderField headerField = this.headerTable[adjustedIndex];
/* 304 */       this.headerEmitter.emitHeader(headerField.name, headerField.value, false);
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
/*     */   
/*     */   int getRealIndex(int index) throws HpackException {
/* 321 */     int newIndex = (this.firstSlotPosition + this.filledTableSlots - index) % this.headerTable.length;
/* 322 */     if (newIndex < 0) {
/* 323 */       throw UndertowMessages.MESSAGES.invalidHpackIndex(index);
/*     */     }
/* 325 */     return newIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addStaticTableEntry(int index) throws HpackException {
/* 330 */     Hpack.HeaderField entry = Hpack.STATIC_TABLE[index];
/* 331 */     this.headerEmitter.emitHeader(entry.name, (entry.value == null) ? "" : entry.value, false);
/*     */   }
/*     */   
/*     */   private void addEntryToHeaderTable(Hpack.HeaderField entry) {
/* 335 */     if (entry.size > this.specifiedMemorySize) {
/*     */       
/* 337 */       while (this.filledTableSlots > 0) {
/* 338 */         this.headerTable[this.firstSlotPosition] = null;
/* 339 */         this.firstSlotPosition++;
/* 340 */         if (this.firstSlotPosition == this.headerTable.length) {
/* 341 */           this.firstSlotPosition = 0;
/*     */         }
/* 343 */         this.filledTableSlots--;
/*     */       } 
/* 345 */       this.currentMemorySize = 0;
/*     */       return;
/*     */     } 
/* 348 */     resizeIfRequired();
/* 349 */     int newTableSlots = this.filledTableSlots + 1;
/* 350 */     int tableLength = this.headerTable.length;
/* 351 */     int index = (this.firstSlotPosition + this.filledTableSlots) % tableLength;
/* 352 */     this.headerTable[index] = entry;
/* 353 */     int newSize = this.currentMemorySize + entry.size;
/* 354 */     while (newSize > this.specifiedMemorySize) {
/* 355 */       int clearIndex = this.firstSlotPosition;
/* 356 */       this.firstSlotPosition++;
/* 357 */       if (this.firstSlotPosition == tableLength) {
/* 358 */         this.firstSlotPosition = 0;
/*     */       }
/* 360 */       Hpack.HeaderField oldData = this.headerTable[clearIndex];
/* 361 */       this.headerTable[clearIndex] = null;
/* 362 */       newSize -= oldData.size;
/* 363 */       newTableSlots--;
/*     */     } 
/* 365 */     this.filledTableSlots = newTableSlots;
/* 366 */     this.currentMemorySize = newSize;
/*     */   }
/*     */   
/*     */   private void resizeIfRequired() {
/* 370 */     if (this.filledTableSlots == this.headerTable.length) {
/* 371 */       Hpack.HeaderField[] newArray = new Hpack.HeaderField[this.headerTable.length + 10];
/* 372 */       for (int i = 0; i < this.headerTable.length; i++) {
/* 373 */         newArray[i] = this.headerTable[(this.firstSlotPosition + i) % this.headerTable.length];
/*     */       }
/* 375 */       this.firstSlotPosition = 0;
/* 376 */       this.headerTable = newArray;
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
/*     */   public HeaderEmitter getHeaderEmitter() {
/* 388 */     return this.headerEmitter;
/*     */   }
/*     */   
/*     */   public void setHeaderEmitter(HeaderEmitter headerEmitter) {
/* 392 */     this.headerEmitter = headerEmitter;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getFirstSlotPosition() {
/* 398 */     return this.firstSlotPosition;
/*     */   }
/*     */   
/*     */   Hpack.HeaderField[] getHeaderTable() {
/* 402 */     return this.headerTable;
/*     */   }
/*     */   
/*     */   int getFilledTableSlots() {
/* 406 */     return this.filledTableSlots;
/*     */   }
/*     */   
/*     */   int getCurrentMemorySize() {
/* 410 */     return this.currentMemorySize;
/*     */   }
/*     */   
/*     */   int getSpecifiedMemorySize() {
/* 414 */     return this.specifiedMemorySize;
/*     */   }
/*     */   
/*     */   public static interface HeaderEmitter {
/*     */     void emitHeader(HttpString param1HttpString, String param1String, boolean param1Boolean) throws HpackException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\HpackDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */