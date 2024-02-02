package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;

public interface Guid {
   IID IID_NULL = new IID();

   public static class IID extends GUID {
      public IID() {
      }

      public IID(Pointer memory) {
         super(memory);
      }

      public IID(String iid) {
         super(iid);
      }

      public IID(byte[] data) {
         super(data);
      }

      public IID(GUID guid) {
         this(guid.toGuidString());
      }
   }

   public static class REFIID extends PointerType {
      public REFIID() {
      }

      public REFIID(Pointer memory) {
         super(memory);
      }

      public REFIID(IID guid) {
         super(guid.getPointer());
      }

      public void setValue(IID value) {
         this.setPointer(value.getPointer());
      }

      public IID getValue() {
         return new IID(this.getPointer());
      }

      public boolean equals(Object o) {
         if (o == null) {
            return false;
         } else if (this == o) {
            return true;
         } else if (this.getClass() != o.getClass()) {
            return false;
         } else {
            REFIID other = (REFIID)o;
            return this.getValue().equals(other.getValue());
         }
      }

      public int hashCode() {
         return this.getValue().hashCode();
      }
   }

   public static class CLSID extends GUID {
      public CLSID() {
      }

      public CLSID(String guid) {
         super(guid);
      }

      public CLSID(GUID guid) {
         super(guid);
      }

      public static class ByReference extends GUID {
         public ByReference() {
         }

         public ByReference(GUID guid) {
            super(guid);
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"Data1", "Data2", "Data3", "Data4"})
   public static class GUID extends Structure {
      public int Data1;
      public short Data2;
      public short Data3;
      public byte[] Data4;

      public GUID() {
         this.Data4 = new byte[8];
      }

      public GUID(GUID guid) {
         this.Data4 = new byte[8];
         this.Data1 = guid.Data1;
         this.Data2 = guid.Data2;
         this.Data3 = guid.Data3;
         this.Data4 = guid.Data4;
         this.writeFieldsToMemory();
      }

      public GUID(String guid) {
         this(fromString(guid));
      }

      public GUID(byte[] data) {
         this(fromBinary(data));
      }

      public GUID(Pointer memory) {
         super(memory);
         this.Data4 = new byte[8];
         this.read();
      }

      public boolean equals(Object o) {
         if (o == null) {
            return false;
         } else if (this == o) {
            return true;
         } else if (this.getClass() != o.getClass()) {
            return false;
         } else {
            GUID other = (GUID)o;
            return this.Data1 == other.Data1 && this.Data2 == other.Data2 && this.Data3 == other.Data3 && Arrays.equals(this.Data4, other.Data4);
         }
      }

      public int hashCode() {
         return this.Data1 + this.Data2 & '\uffff' + this.Data3 & '\uffff' + Arrays.hashCode(this.Data4);
      }

      public static GUID fromBinary(byte[] data) {
         if (data.length != 16) {
            throw new IllegalArgumentException("Invalid data length: " + data.length);
         } else {
            GUID newGuid = new GUID();
            long data1Temp = (long)(data[0] & 255);
            data1Temp <<= 8;
            data1Temp |= (long)(data[1] & 255);
            data1Temp <<= 8;
            data1Temp |= (long)(data[2] & 255);
            data1Temp <<= 8;
            data1Temp |= (long)(data[3] & 255);
            newGuid.Data1 = (int)data1Temp;
            int data2Temp = data[4] & 255;
            data2Temp <<= 8;
            data2Temp |= data[5] & 255;
            newGuid.Data2 = (short)data2Temp;
            int data3Temp = data[6] & 255;
            data3Temp <<= 8;
            data3Temp |= data[7] & 255;
            newGuid.Data3 = (short)data3Temp;
            newGuid.Data4[0] = data[8];
            newGuid.Data4[1] = data[9];
            newGuid.Data4[2] = data[10];
            newGuid.Data4[3] = data[11];
            newGuid.Data4[4] = data[12];
            newGuid.Data4[5] = data[13];
            newGuid.Data4[6] = data[14];
            newGuid.Data4[7] = data[15];
            newGuid.writeFieldsToMemory();
            return newGuid;
         }
      }

      public static GUID fromString(String guid) {
         int y = 0;
         char[] _cnewguid = new char[32];
         char[] _cguid = guid.toCharArray();
         byte[] bdata = new byte[16];
         GUID newGuid = new GUID();
         if (guid.length() > 38) {
            throw new IllegalArgumentException("Invalid guid length: " + guid.length());
         } else {
            int i;
            for(i = 0; i < _cguid.length; ++i) {
               if (_cguid[i] != '{' && _cguid[i] != '-' && _cguid[i] != '}') {
                  _cnewguid[y++] = _cguid[i];
               }
            }

            for(i = 0; i < 32; i += 2) {
               bdata[i / 2] = (byte)((Character.digit(_cnewguid[i], 16) << 4) + Character.digit(_cnewguid[i + 1], 16) & 255);
            }

            if (bdata.length != 16) {
               throw new IllegalArgumentException("Invalid data length: " + bdata.length);
            } else {
               long data1Temp = (long)(bdata[0] & 255);
               data1Temp <<= 8;
               data1Temp |= (long)(bdata[1] & 255);
               data1Temp <<= 8;
               data1Temp |= (long)(bdata[2] & 255);
               data1Temp <<= 8;
               data1Temp |= (long)(bdata[3] & 255);
               newGuid.Data1 = (int)data1Temp;
               int data2Temp = bdata[4] & 255;
               data2Temp <<= 8;
               data2Temp |= bdata[5] & 255;
               newGuid.Data2 = (short)data2Temp;
               int data3Temp = bdata[6] & 255;
               data3Temp <<= 8;
               data3Temp |= bdata[7] & 255;
               newGuid.Data3 = (short)data3Temp;
               newGuid.Data4[0] = bdata[8];
               newGuid.Data4[1] = bdata[9];
               newGuid.Data4[2] = bdata[10];
               newGuid.Data4[3] = bdata[11];
               newGuid.Data4[4] = bdata[12];
               newGuid.Data4[5] = bdata[13];
               newGuid.Data4[6] = bdata[14];
               newGuid.Data4[7] = bdata[15];
               newGuid.writeFieldsToMemory();
               return newGuid;
            }
         }
      }

      public static GUID newGuid() {
         SecureRandom ng = new SecureRandom();
         byte[] randomBytes = new byte[16];
         ng.nextBytes(randomBytes);
         randomBytes[6] = (byte)(randomBytes[6] & 15);
         randomBytes[6] = (byte)(randomBytes[6] | 64);
         randomBytes[8] = (byte)(randomBytes[8] & 63);
         randomBytes[8] = (byte)(randomBytes[8] | 128);
         return new GUID(randomBytes);
      }

      public byte[] toByteArray() {
         byte[] guid = new byte[16];
         byte[] bytes1 = new byte[]{(byte)(this.Data1 >> 24), (byte)(this.Data1 >> 16), (byte)(this.Data1 >> 8), (byte)(this.Data1 >> 0)};
         byte[] bytes2 = new byte[]{(byte)(this.Data2 >> 24), (byte)(this.Data2 >> 16), (byte)(this.Data2 >> 8), (byte)(this.Data2 >> 0)};
         byte[] bytes3 = new byte[]{(byte)(this.Data3 >> 24), (byte)(this.Data3 >> 16), (byte)(this.Data3 >> 8), (byte)(this.Data3 >> 0)};
         System.arraycopy(bytes1, 0, guid, 0, 4);
         System.arraycopy(bytes2, 2, guid, 4, 2);
         System.arraycopy(bytes3, 2, guid, 6, 2);
         System.arraycopy(this.Data4, 0, guid, 8, 8);
         return guid;
      }

      public String toGuidString() {
         String HEXES = "0123456789ABCDEF";
         byte[] bGuid = this.toByteArray();
         StringBuilder hexStr = new StringBuilder(2 * bGuid.length);
         hexStr.append("{");

         for(int i = 0; i < bGuid.length; ++i) {
            char ch1 = "0123456789ABCDEF".charAt((bGuid[i] & 240) >> 4);
            char ch2 = "0123456789ABCDEF".charAt(bGuid[i] & 15);
            hexStr.append(ch1).append(ch2);
            if (i == 3 || i == 5 || i == 7 || i == 9) {
               hexStr.append("-");
            }
         }

         hexStr.append("}");
         return hexStr.toString();
      }

      protected void writeFieldsToMemory() {
         Iterator var1 = this.getFieldOrder().iterator();

         while(var1.hasNext()) {
            String name = (String)var1.next();
            this.writeField(name);
         }

      }

      public static class ByReference extends GUID implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(GUID guid) {
            super(guid.getPointer());
            this.Data1 = guid.Data1;
            this.Data2 = guid.Data2;
            this.Data3 = guid.Data3;
            this.Data4 = guid.Data4;
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }

      public static class ByValue extends GUID implements Structure.ByValue {
         public ByValue() {
         }

         public ByValue(GUID guid) {
            super(guid.getPointer());
            this.Data1 = guid.Data1;
            this.Data2 = guid.Data2;
            this.Data3 = guid.Data3;
            this.Data4 = guid.Data4;
         }

         public ByValue(Pointer memory) {
            super(memory);
         }
      }
   }
}
