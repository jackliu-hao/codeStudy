package io.undertow.protocols.http2;

import io.undertow.UndertowMessages;
import io.undertow.util.HttpString;
import java.nio.ByteBuffer;

public class HpackDecoder {
   private static final int DEFAULT_RING_BUFFER_SIZE = 10;
   private HeaderEmitter headerEmitter;
   private Hpack.HeaderField[] headerTable;
   private int firstSlotPosition;
   private int filledTableSlots;
   private int currentMemorySize;
   private int specifiedMemorySize;
   private final int maxAllowedMemorySize;
   private boolean first;
   private final StringBuilder stringBuilder;

   public HpackDecoder(int maxAllowedMemorySize) {
      this.firstSlotPosition = 0;
      this.filledTableSlots = 0;
      this.currentMemorySize = 0;
      this.first = true;
      this.stringBuilder = new StringBuilder();
      this.specifiedMemorySize = Math.min(4096, maxAllowedMemorySize);
      this.maxAllowedMemorySize = maxAllowedMemorySize;
      this.headerTable = new Hpack.HeaderField[10];
   }

   public HpackDecoder() {
      this(4096);
   }

   public void decode(ByteBuffer buffer, boolean moreData) throws HpackException {
      while(true) {
         if (buffer.hasRemaining()) {
            int originalPos = buffer.position();
            byte b = buffer.get();
            if ((b & 128) != 0) {
               this.first = false;
               buffer.position(buffer.position() - 1);
               int index = Hpack.decodeInteger(buffer, 7);
               if (index == -1) {
                  if (!moreData) {
                     throw UndertowMessages.MESSAGES.hpackFailed();
                  }

                  buffer.position(originalPos);
                  return;
               }

               if (index == 0) {
                  throw UndertowMessages.MESSAGES.zeroNotValidHeaderTableIndex();
               }

               this.handleIndex(index);
               continue;
            }

            HttpString headerName;
            String headerValue;
            if ((b & 64) != 0) {
               this.first = false;
               headerName = this.readHeaderName(buffer, 6);
               if (headerName == null) {
                  if (!moreData) {
                     throw UndertowMessages.MESSAGES.hpackFailed();
                  }

                  buffer.position(originalPos);
                  return;
               }

               headerValue = this.readHpackString(buffer);
               if (headerValue == null) {
                  if (!moreData) {
                     throw UndertowMessages.MESSAGES.hpackFailed();
                  }

                  buffer.position(originalPos);
                  return;
               }

               this.headerEmitter.emitHeader(headerName, headerValue, false);
               this.addEntryToHeaderTable(new Hpack.HeaderField(headerName, headerValue));
               continue;
            }

            if ((b & 240) == 0) {
               this.first = false;
               headerName = this.readHeaderName(buffer, 4);
               if (headerName == null) {
                  if (!moreData) {
                     throw UndertowMessages.MESSAGES.hpackFailed();
                  }

                  buffer.position(originalPos);
                  return;
               }

               headerValue = this.readHpackString(buffer);
               if (headerValue == null) {
                  if (!moreData) {
                     throw UndertowMessages.MESSAGES.hpackFailed();
                  }

                  buffer.position(originalPos);
                  return;
               }

               this.headerEmitter.emitHeader(headerName, headerValue, false);
               continue;
            }

            if ((b & 240) == 16) {
               this.first = false;
               headerName = this.readHeaderName(buffer, 4);
               if (headerName == null) {
                  buffer.position(originalPos);
                  return;
               }

               headerValue = this.readHpackString(buffer);
               if (headerValue == null) {
                  if (!moreData) {
                     throw UndertowMessages.MESSAGES.hpackFailed();
                  }

                  buffer.position(originalPos);
                  return;
               }

               this.headerEmitter.emitHeader(headerName, headerValue, true);
               continue;
            }

            if ((b & 224) == 32) {
               if (!this.first) {
                  throw new HpackException();
               }

               if (this.handleMaxMemorySizeChange(buffer, originalPos)) {
                  continue;
               }

               return;
            }

            throw UndertowMessages.MESSAGES.invalidHpackEncoding(b);
         }

         if (!moreData) {
            this.first = true;
         }

         return;
      }
   }

   private boolean handleMaxMemorySizeChange(ByteBuffer buffer, int originalPos) throws HpackException {
      buffer.position(buffer.position() - 1);
      int size = Hpack.decodeInteger(buffer, 5);
      if (size == -1) {
         buffer.position(originalPos);
         return false;
      } else if (size > this.maxAllowedMemorySize) {
         throw new HpackException(1);
      } else {
         this.specifiedMemorySize = size;
         if (this.currentMemorySize > this.specifiedMemorySize) {
            int newTableSlots = this.filledTableSlots;
            int tableLength = this.headerTable.length;

            int newSize;
            for(newSize = this.currentMemorySize; newSize > this.specifiedMemorySize; --newTableSlots) {
               int clearIndex = this.firstSlotPosition++;
               if (this.firstSlotPosition == tableLength) {
                  this.firstSlotPosition = 0;
               }

               Hpack.HeaderField oldData = this.headerTable[clearIndex];
               this.headerTable[clearIndex] = null;
               newSize -= oldData.size;
            }

            this.filledTableSlots = newTableSlots;
            this.currentMemorySize = newSize;
         }

         return true;
      }
   }

   private HttpString readHeaderName(ByteBuffer buffer, int prefixLength) throws HpackException {
      buffer.position(buffer.position() - 1);
      int index = Hpack.decodeInteger(buffer, prefixLength);
      if (index == -1) {
         return null;
      } else if (index != 0) {
         return this.handleIndexedHeaderName(index);
      } else {
         String string = this.readHpackString(buffer);
         if (string == null) {
            return null;
         } else if (string.isEmpty()) {
            throw new HpackException();
         } else {
            return new HttpString(string);
         }
      }
   }

   private String readHpackString(ByteBuffer buffer) throws HpackException {
      if (!buffer.hasRemaining()) {
         return null;
      } else {
         byte data = buffer.get(buffer.position());
         int length = Hpack.decodeInteger(buffer, 7);
         if (buffer.remaining() >= length && length != -1) {
            boolean huffman = (data & 128) != 0;
            if (huffman) {
               return this.readHuffmanString(length, buffer);
            } else {
               for(int i = 0; i < length; ++i) {
                  this.stringBuilder.append((char)buffer.get());
               }

               String ret = this.stringBuilder.toString();
               this.stringBuilder.setLength(0);
               return ret.isEmpty() ? "" : ret;
            }
         } else {
            return null;
         }
      }
   }

   private String readHuffmanString(int length, ByteBuffer buffer) throws HpackException {
      HPackHuffman.decode(buffer, length, this.stringBuilder);
      String ret = this.stringBuilder.toString();
      if (ret.isEmpty()) {
         return "";
      } else {
         this.stringBuilder.setLength(0);
         return ret;
      }
   }

   private HttpString handleIndexedHeaderName(int index) throws HpackException {
      if (index <= Hpack.STATIC_TABLE_LENGTH) {
         return Hpack.STATIC_TABLE[index].name;
      } else if (index > Hpack.STATIC_TABLE_LENGTH + this.filledTableSlots) {
         throw new HpackException();
      } else {
         int adjustedIndex = this.getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
         Hpack.HeaderField res = this.headerTable[adjustedIndex];
         if (res == null) {
            throw new HpackException();
         } else {
            return res.name;
         }
      }
   }

   private void handleIndex(int index) throws HpackException {
      if (index <= Hpack.STATIC_TABLE_LENGTH) {
         this.addStaticTableEntry(index);
      } else {
         int adjustedIndex = this.getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
         Hpack.HeaderField headerField = this.headerTable[adjustedIndex];
         this.headerEmitter.emitHeader(headerField.name, headerField.value, false);
      }

   }

   int getRealIndex(int index) throws HpackException {
      int newIndex = (this.firstSlotPosition + (this.filledTableSlots - index)) % this.headerTable.length;
      if (newIndex < 0) {
         throw UndertowMessages.MESSAGES.invalidHpackIndex(index);
      } else {
         return newIndex;
      }
   }

   private void addStaticTableEntry(int index) throws HpackException {
      Hpack.HeaderField entry = Hpack.STATIC_TABLE[index];
      this.headerEmitter.emitHeader(entry.name, entry.value == null ? "" : entry.value, false);
   }

   private void addEntryToHeaderTable(Hpack.HeaderField entry) {
      if (entry.size > this.specifiedMemorySize) {
         for(; this.filledTableSlots > 0; --this.filledTableSlots) {
            this.headerTable[this.firstSlotPosition] = null;
            ++this.firstSlotPosition;
            if (this.firstSlotPosition == this.headerTable.length) {
               this.firstSlotPosition = 0;
            }
         }

         this.currentMemorySize = 0;
      } else {
         this.resizeIfRequired();
         int newTableSlots = this.filledTableSlots + 1;
         int tableLength = this.headerTable.length;
         int index = (this.firstSlotPosition + this.filledTableSlots) % tableLength;
         this.headerTable[index] = entry;

         int newSize;
         for(newSize = this.currentMemorySize + entry.size; newSize > this.specifiedMemorySize; --newTableSlots) {
            int clearIndex = this.firstSlotPosition++;
            if (this.firstSlotPosition == tableLength) {
               this.firstSlotPosition = 0;
            }

            Hpack.HeaderField oldData = this.headerTable[clearIndex];
            this.headerTable[clearIndex] = null;
            newSize -= oldData.size;
         }

         this.filledTableSlots = newTableSlots;
         this.currentMemorySize = newSize;
      }
   }

   private void resizeIfRequired() {
      if (this.filledTableSlots == this.headerTable.length) {
         Hpack.HeaderField[] newArray = new Hpack.HeaderField[this.headerTable.length + 10];

         for(int i = 0; i < this.headerTable.length; ++i) {
            newArray[i] = this.headerTable[(this.firstSlotPosition + i) % this.headerTable.length];
         }

         this.firstSlotPosition = 0;
         this.headerTable = newArray;
      }

   }

   public HeaderEmitter getHeaderEmitter() {
      return this.headerEmitter;
   }

   public void setHeaderEmitter(HeaderEmitter headerEmitter) {
      this.headerEmitter = headerEmitter;
   }

   int getFirstSlotPosition() {
      return this.firstSlotPosition;
   }

   Hpack.HeaderField[] getHeaderTable() {
      return this.headerTable;
   }

   int getFilledTableSlots() {
      return this.filledTableSlots;
   }

   int getCurrentMemorySize() {
      return this.currentMemorySize;
   }

   int getSpecifiedMemorySize() {
      return this.specifiedMemorySize;
   }

   public interface HeaderEmitter {
      void emitHeader(HttpString var1, String var2, boolean var3) throws HpackException;
   }
}
