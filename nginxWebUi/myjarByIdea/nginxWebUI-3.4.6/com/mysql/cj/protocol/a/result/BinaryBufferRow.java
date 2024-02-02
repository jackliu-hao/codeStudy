package com.mysql.cj.protocol.a.result;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ValueDecoder;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.NativeUtils;
import com.mysql.cj.result.Row;
import com.mysql.cj.result.ValueFactory;

public class BinaryBufferRow extends AbstractBufferRow {
   private int preNullBitmaskHomePosition = 0;
   private boolean[] isNull;

   public BinaryBufferRow(NativePacketPayload buf, ColumnDefinition cd, ExceptionInterceptor exceptionInterceptor, ValueDecoder valueDecoder) {
      super(exceptionInterceptor);
      this.rowFromServer = buf;
      this.homePosition = this.rowFromServer.getPosition();
      this.preNullBitmaskHomePosition = this.homePosition;
      this.valueDecoder = valueDecoder;
      if (cd.getFields() != null) {
         this.setMetadata(cd);
      }

   }

   public boolean isBinaryEncoded() {
      return true;
   }

   protected int findAndSeekToOffset(int index) {
      if (index == 0) {
         this.lastRequestedIndex = 0;
         this.lastRequestedPos = this.homePosition;
         this.rowFromServer.setPosition(this.homePosition);
         return 0;
      } else if (index == this.lastRequestedIndex) {
         this.rowFromServer.setPosition(this.lastRequestedPos);
         return this.lastRequestedPos;
      } else {
         int startingIndex = 0;
         if (index > this.lastRequestedIndex) {
            if (this.lastRequestedIndex >= 0) {
               startingIndex = this.lastRequestedIndex;
            } else {
               startingIndex = 0;
               this.lastRequestedPos = this.homePosition;
            }

            this.rowFromServer.setPosition(this.lastRequestedPos);
         } else {
            this.rowFromServer.setPosition(this.homePosition);
         }

         for(int i = startingIndex; i < index; ++i) {
            if (!this.isNull[i]) {
               int type = this.metadata.getFields()[i].getMysqlTypeId();
               if (type != 6) {
                  int length = NativeUtils.getBinaryEncodedLength(this.metadata.getFields()[i].getMysqlTypeId());
                  if (length == 0) {
                     this.rowFromServer.skipBytes(NativeConstants.StringSelfDataType.STRING_LENENC);
                  } else {
                     if (length == -1) {
                        throw ExceptionFactory.createException(Messages.getString("MysqlIO.97", new Object[]{type, i + 1, this.metadata.getFields().length}), this.exceptionInterceptor);
                     }

                     int curPosition = this.rowFromServer.getPosition();
                     this.rowFromServer.setPosition(curPosition + length);
                  }
               }
            }
         }

         this.lastRequestedIndex = index;
         this.lastRequestedPos = this.rowFromServer.getPosition();
         return this.lastRequestedPos;
      }
   }

   public byte[] getBytes(int index) {
      this.findAndSeekToOffset(index);
      if (this.getNull(index)) {
         return null;
      } else {
         int type = this.metadata.getFields()[index].getMysqlTypeId();
         switch (type) {
            case 1:
               return this.rowFromServer.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, 1);
            case 6:
               return null;
            default:
               int length = NativeUtils.getBinaryEncodedLength(type);
               if (length == 0) {
                  return this.rowFromServer.readBytes(NativeConstants.StringSelfDataType.STRING_LENENC);
               } else if (length == -1) {
                  throw ExceptionFactory.createException(Messages.getString("MysqlIO.97", new Object[]{type, index + 1, this.metadata.getFields().length}), this.exceptionInterceptor);
               } else {
                  return this.rowFromServer.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, length);
               }
         }
      }
   }

   public boolean getNull(int columnIndex) {
      this.wasNull = this.isNull[columnIndex];
      return this.wasNull;
   }

   public Row setMetadata(ColumnDefinition f) {
      super.setMetadata(f);
      this.setupIsNullBitmask();
      return this;
   }

   private void setupIsNullBitmask() {
      if (this.isNull == null) {
         this.rowFromServer.setPosition(this.preNullBitmaskHomePosition);
         int len = this.metadata.getFields().length;
         int nullCount = (len + 9) / 8;
         byte[] nullBitMask = this.rowFromServer.readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, nullCount);
         this.homePosition = this.rowFromServer.getPosition();
         this.isNull = new boolean[len];
         int nullMaskPos = 0;
         int bit = 4;

         for(int i = 0; i < len; ++i) {
            this.isNull[i] = (nullBitMask[nullMaskPos] & bit) != 0;
            if (((bit <<= 1) & 255) == 0) {
               bit = 1;
               ++nullMaskPos;
            }
         }

      }
   }

   public <T> T getValue(int columnIndex, ValueFactory<T> vf) {
      this.findAndSeekToOffset(columnIndex);
      int type = this.metadata.getFields()[columnIndex].getMysqlTypeId();
      int length = NativeUtils.getBinaryEncodedLength(type);
      if (!this.getNull(columnIndex)) {
         if (length == 0) {
            length = (int)this.rowFromServer.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
         } else if (length == -1) {
            throw ExceptionFactory.createException(Messages.getString("MysqlIO.97", new Object[]{type, columnIndex + 1, this.metadata.getFields().length}), this.exceptionInterceptor);
         }
      }

      return this.getValueFromBytes(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), length, vf);
   }

   public void setBytes(int columnIndex, byte[] value) {
      byte[] backup = null;
      int backupLength = false;
      if (columnIndex + 1 < this.metadata.getFields().length) {
         this.findAndSeekToOffset(columnIndex + 1);
         int backupLength = this.rowFromServer.getPayloadLength() - this.rowFromServer.getPosition();
         backup = new byte[backupLength];
         System.arraycopy(this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), backup, 0, backupLength);
      }

      this.findAndSeekToOffset(columnIndex);
      this.rowFromServer.setPayloadLength(this.rowFromServer.getPosition());
      if (value == null) {
         this.metadata.getFields()[columnIndex].setMysqlTypeId(6);
      } else {
         int type = this.metadata.getFields()[columnIndex].getMysqlTypeId();
         int length = NativeUtils.getBinaryEncodedLength(type);
         if (length == 0) {
            this.rowFromServer.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, value);
         } else {
            if (length == -1) {
               throw ExceptionFactory.createException(Messages.getString("MysqlIO.97", new Object[]{type, columnIndex + 1, this.metadata.getFields().length}), this.exceptionInterceptor);
            }

            if (length != value.length) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Value length doesn't match the expected one for type " + type, this.exceptionInterceptor);
            }

            this.rowFromServer.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, value);
         }
      }

      if (backup != null) {
         this.rowFromServer.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, backup);
      }

   }
}
