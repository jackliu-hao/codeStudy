package org.apache.commons.compress.archivers.zip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipException;

public class ExtraFieldUtils {
   private static final int WORD = 4;
   private static final Map<ZipShort, Class<?>> implementations = new ConcurrentHashMap();
   static final ZipExtraField[] EMPTY_ZIP_EXTRA_FIELD_ARRAY;

   public static void register(Class<?> c) {
      try {
         ZipExtraField ze = (ZipExtraField)c.newInstance();
         implementations.put(ze.getHeaderId(), c);
      } catch (ClassCastException var2) {
         throw new RuntimeException(c + " doesn't implement ZipExtraField");
      } catch (InstantiationException var3) {
         throw new RuntimeException(c + " is not a concrete class");
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(c + "'s no-arg constructor is not public");
      }
   }

   public static ZipExtraField createExtraField(ZipShort headerId) throws InstantiationException, IllegalAccessException {
      ZipExtraField field = createExtraFieldNoDefault(headerId);
      if (field != null) {
         return field;
      } else {
         UnrecognizedExtraField u = new UnrecognizedExtraField();
         u.setHeaderId(headerId);
         return u;
      }
   }

   public static ZipExtraField createExtraFieldNoDefault(ZipShort headerId) throws InstantiationException, IllegalAccessException {
      Class<?> c = (Class)implementations.get(headerId);
      return c != null ? (ZipExtraField)c.newInstance() : null;
   }

   public static ZipExtraField[] parse(byte[] data) throws ZipException {
      return parse(data, true, ExtraFieldUtils.UnparseableExtraField.THROW);
   }

   public static ZipExtraField[] parse(byte[] data, boolean local) throws ZipException {
      return parse(data, local, ExtraFieldUtils.UnparseableExtraField.THROW);
   }

   public static ZipExtraField[] parse(byte[] data, boolean local, final UnparseableExtraField onUnparseableData) throws ZipException {
      return parse(data, local, new ExtraFieldParsingBehavior() {
         public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException {
            return onUnparseableData.onUnparseableExtraField(data, off, len, local, claimedLength);
         }

         public ZipExtraField createExtraField(ZipShort headerId) throws ZipException, InstantiationException, IllegalAccessException {
            return ExtraFieldUtils.createExtraField(headerId);
         }

         public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) throws ZipException {
            return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
         }
      });
   }

   public static ZipExtraField[] parse(byte[] data, boolean local, ExtraFieldParsingBehavior parsingBehavior) throws ZipException {
      List<ZipExtraField> v = new ArrayList();
      int start = 0;
      int dataLength = data.length;

      while(start <= dataLength - 4) {
         ZipShort headerId = new ZipShort(data, start);
         int length = (new ZipShort(data, start + 2)).getValue();
         ZipExtraField field;
         if (start + 4 + length > dataLength) {
            field = parsingBehavior.onUnparseableExtraField(data, start, dataLength - start, local, length);
            if (field != null) {
               v.add(field);
            }
            break;
         }

         try {
            field = (ZipExtraField)Objects.requireNonNull(parsingBehavior.createExtraField(headerId), "createExtraField must not return null");
            v.add(Objects.requireNonNull(parsingBehavior.fill(field, data, start + 4, length, local), "fill must not return null"));
            start += length + 4;
         } catch (IllegalAccessException | InstantiationException var9) {
            throw (ZipException)(new ZipException(var9.getMessage())).initCause(var9);
         }
      }

      return (ZipExtraField[])v.toArray(EMPTY_ZIP_EXTRA_FIELD_ARRAY);
   }

   public static byte[] mergeLocalFileDataData(ZipExtraField[] data) {
      int dataLength = data.length;
      boolean lastIsUnparseableHolder = dataLength > 0 && data[dataLength - 1] instanceof UnparseableExtraFieldData;
      int regularExtraFieldCount = lastIsUnparseableHolder ? dataLength - 1 : dataLength;
      int sum = 4 * regularExtraFieldCount;
      ZipExtraField[] var5 = data;
      int start = data.length;

      int i;
      for(i = 0; i < start; ++i) {
         ZipExtraField element = var5[i];
         sum += element.getLocalFileDataLength().getValue();
      }

      byte[] result = new byte[sum];
      start = 0;

      for(i = 0; i < regularExtraFieldCount; ++i) {
         System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
         System.arraycopy(data[i].getLocalFileDataLength().getBytes(), 0, result, start + 2, 2);
         start += 4;
         byte[] local = data[i].getLocalFileDataData();
         if (local != null) {
            System.arraycopy(local, 0, result, start, local.length);
            start += local.length;
         }
      }

      if (lastIsUnparseableHolder) {
         byte[] local = data[dataLength - 1].getLocalFileDataData();
         if (local != null) {
            System.arraycopy(local, 0, result, start, local.length);
         }
      }

      return result;
   }

   public static byte[] mergeCentralDirectoryData(ZipExtraField[] data) {
      int dataLength = data.length;
      boolean lastIsUnparseableHolder = dataLength > 0 && data[dataLength - 1] instanceof UnparseableExtraFieldData;
      int regularExtraFieldCount = lastIsUnparseableHolder ? dataLength - 1 : dataLength;
      int sum = 4 * regularExtraFieldCount;
      ZipExtraField[] var5 = data;
      int start = data.length;

      int i;
      for(i = 0; i < start; ++i) {
         ZipExtraField element = var5[i];
         sum += element.getCentralDirectoryLength().getValue();
      }

      byte[] result = new byte[sum];
      start = 0;

      for(i = 0; i < regularExtraFieldCount; ++i) {
         System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
         System.arraycopy(data[i].getCentralDirectoryLength().getBytes(), 0, result, start + 2, 2);
         start += 4;
         byte[] central = data[i].getCentralDirectoryData();
         if (central != null) {
            System.arraycopy(central, 0, result, start, central.length);
            start += central.length;
         }
      }

      if (lastIsUnparseableHolder) {
         byte[] central = data[dataLength - 1].getCentralDirectoryData();
         if (central != null) {
            System.arraycopy(central, 0, result, start, central.length);
         }
      }

      return result;
   }

   public static ZipExtraField fillExtraField(ZipExtraField ze, byte[] data, int off, int len, boolean local) throws ZipException {
      try {
         if (local) {
            ze.parseFromLocalFileData(data, off, len);
         } else {
            ze.parseFromCentralDirectoryData(data, off, len);
         }

         return ze;
      } catch (ArrayIndexOutOfBoundsException var6) {
         throw (ZipException)(new ZipException("Failed to parse corrupt ZIP extra field of type " + Integer.toHexString(ze.getHeaderId().getValue()))).initCause(var6);
      }
   }

   static {
      register(AsiExtraField.class);
      register(X5455_ExtendedTimestamp.class);
      register(X7875_NewUnix.class);
      register(JarMarker.class);
      register(UnicodePathExtraField.class);
      register(UnicodeCommentExtraField.class);
      register(Zip64ExtendedInformationExtraField.class);
      register(X000A_NTFS.class);
      register(X0014_X509Certificates.class);
      register(X0015_CertificateIdForFile.class);
      register(X0016_CertificateIdForCentralDirectory.class);
      register(X0017_StrongEncryptionHeader.class);
      register(X0019_EncryptionRecipientCertificateList.class);
      register(ResourceAlignmentExtraField.class);
      EMPTY_ZIP_EXTRA_FIELD_ARRAY = new ZipExtraField[0];
   }

   public static final class UnparseableExtraField implements UnparseableExtraFieldBehavior {
      public static final int THROW_KEY = 0;
      public static final int SKIP_KEY = 1;
      public static final int READ_KEY = 2;
      public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
      public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
      public static final UnparseableExtraField READ = new UnparseableExtraField(2);
      private final int key;

      private UnparseableExtraField(int k) {
         this.key = k;
      }

      public int getKey() {
         return this.key;
      }

      public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException {
         switch (this.key) {
            case 0:
               throw new ZipException("Bad extra field starting at " + off + ".  Block length of " + claimedLength + " bytes exceeds remaining data of " + (len - 4) + " bytes.");
            case 1:
               return null;
            case 2:
               UnparseableExtraFieldData field = new UnparseableExtraFieldData();
               if (local) {
                  field.parseFromLocalFileData(data, off, len);
               } else {
                  field.parseFromCentralDirectoryData(data, off, len);
               }

               return field;
            default:
               throw new ZipException("Unknown UnparseableExtraField key: " + this.key);
         }
      }
   }
}
