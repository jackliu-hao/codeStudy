package org.h2.api;

public interface JavaObjectSerializer {
   byte[] serialize(Object var1) throws Exception;

   Object deserialize(byte[] var1) throws Exception;
}
