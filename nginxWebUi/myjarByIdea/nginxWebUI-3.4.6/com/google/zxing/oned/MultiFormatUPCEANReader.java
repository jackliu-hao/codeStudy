package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public final class MultiFormatUPCEANReader extends OneDReader {
   private final UPCEANReader[] readers;

   public MultiFormatUPCEANReader(Map<DecodeHintType, ?> hints) {
      Collection<BarcodeFormat> possibleFormats = hints == null ? null : (Collection)hints.get(DecodeHintType.POSSIBLE_FORMATS);
      Collection<UPCEANReader> readers = new ArrayList();
      if (possibleFormats != null) {
         if (possibleFormats.contains(BarcodeFormat.EAN_13)) {
            readers.add(new EAN13Reader());
         } else if (possibleFormats.contains(BarcodeFormat.UPC_A)) {
            readers.add(new UPCAReader());
         }

         if (possibleFormats.contains(BarcodeFormat.EAN_8)) {
            readers.add(new EAN8Reader());
         }

         if (possibleFormats.contains(BarcodeFormat.UPC_E)) {
            readers.add(new UPCEReader());
         }
      }

      if (readers.isEmpty()) {
         readers.add(new EAN13Reader());
         readers.add(new EAN8Reader());
         readers.add(new UPCEReader());
      }

      this.readers = (UPCEANReader[])readers.toArray(new UPCEANReader[readers.size()]);
   }

   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException {
      int[] startGuardPattern = UPCEANReader.findStartGuardPattern(row);
      UPCEANReader[] var5;
      int var6 = (var5 = this.readers).length;
      int var7 = 0;

      while(true) {
         if (var7 < var6) {
            UPCEANReader reader = var5[var7];

            Result result;
            try {
               result = reader.decodeRow(rowNumber, row, startGuardPattern, hints);
            } catch (ReaderException var14) {
               ++var7;
               continue;
            }

            boolean ean13MayBeUPCA = result.getBarcodeFormat() == BarcodeFormat.EAN_13 && result.getText().charAt(0) == '0';
            Collection var10000 = hints == null ? null : (Collection)hints.get(DecodeHintType.POSSIBLE_FORMATS);
            Collection<BarcodeFormat> possibleFormats = var10000;
            boolean canReturnUPCA = var10000 == null || possibleFormats.contains(BarcodeFormat.UPC_A);
            if (ean13MayBeUPCA && canReturnUPCA) {
               Result resultUPCA;
               (resultUPCA = new Result(result.getText().substring(1), result.getRawBytes(), result.getResultPoints(), BarcodeFormat.UPC_A)).putAllMetadata(result.getResultMetadata());
               return resultUPCA;
            }

            return result;
         }

         throw NotFoundException.getNotFoundInstance();
      }
   }

   public void reset() {
      UPCEANReader[] var1;
      int var2 = (var1 = this.readers).length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1[var3].reset();
      }

   }
}
