package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class ByteOutput {
  public abstract void write(byte paramByte) throws IOException;
  
  public abstract void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  public abstract void writeLazy(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  public abstract void write(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract void writeLazy(ByteBuffer paramByteBuffer) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ByteOutput.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */