package com.google.zxing;

import java.util.Map;

public interface Reader {
  Result decode(BinaryBitmap paramBinaryBitmap) throws NotFoundException, ChecksumException, FormatException;
  
  Result decode(BinaryBitmap paramBinaryBitmap, Map<DecodeHintType, ?> paramMap) throws NotFoundException, ChecksumException, FormatException;
  
  void reset();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\Reader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */