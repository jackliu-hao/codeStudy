package org.h2.api;

public interface JavaObjectSerializer {
  byte[] serialize(Object paramObject) throws Exception;
  
  Object deserialize(byte[] paramArrayOfbyte) throws Exception;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\JavaObjectSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */