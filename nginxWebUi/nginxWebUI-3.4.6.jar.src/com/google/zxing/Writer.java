package com.google.zxing;

import com.google.zxing.common.BitMatrix;
import java.util.Map;

public interface Writer {
  BitMatrix encode(String paramString, BarcodeFormat paramBarcodeFormat, int paramInt1, int paramInt2) throws WriterException;
  
  BitMatrix encode(String paramString, BarcodeFormat paramBarcodeFormat, int paramInt1, int paramInt2, Map<EncodeHintType, ?> paramMap) throws WriterException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\Writer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */