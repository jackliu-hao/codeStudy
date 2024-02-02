package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import java.util.Map;

public interface MultipleBarcodeReader {
  Result[] decodeMultiple(BinaryBitmap paramBinaryBitmap) throws NotFoundException;
  
  Result[] decodeMultiple(BinaryBitmap paramBinaryBitmap, Map<DecodeHintType, ?> paramMap) throws NotFoundException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\multi\MultipleBarcodeReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */