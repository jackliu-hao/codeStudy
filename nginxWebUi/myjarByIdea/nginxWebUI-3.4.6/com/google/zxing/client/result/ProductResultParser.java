package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.oned.UPCEReader;

public final class ProductResultParser extends ResultParser {
   public ProductParsedResult parse(Result result) {
      BarcodeFormat format;
      if ((format = result.getBarcodeFormat()) != BarcodeFormat.UPC_A && format != BarcodeFormat.UPC_E && format != BarcodeFormat.EAN_8 && format != BarcodeFormat.EAN_13) {
         return null;
      } else {
         String rawText;
         String var10000 = rawText = getMassagedText(result);
         if (!isStringOfDigits(var10000, var10000.length())) {
            return null;
         } else {
            String normalizedProductID;
            if (format == BarcodeFormat.UPC_E && rawText.length() == 8) {
               normalizedProductID = UPCEReader.convertUPCEtoUPCA(rawText);
            } else {
               normalizedProductID = rawText;
            }

            return new ProductParsedResult(rawText, normalizedProductID);
         }
      }
   }
}
