package com.mysql.cj.protocol;

import com.mysql.cj.result.Field;
import com.mysql.cj.result.ValueFactory;

public interface ValueDecoder {
   <T> T decodeDate(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeTime(byte[] var1, int var2, int var3, int var4, ValueFactory<T> var5);

   <T> T decodeTimestamp(byte[] var1, int var2, int var3, int var4, ValueFactory<T> var5);

   <T> T decodeDatetime(byte[] var1, int var2, int var3, int var4, ValueFactory<T> var5);

   <T> T decodeInt1(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeUInt1(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeInt2(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeUInt2(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeInt4(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeUInt4(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeInt8(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeUInt8(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeFloat(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeDouble(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeDecimal(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeByteArray(byte[] var1, int var2, int var3, Field var4, ValueFactory<T> var5);

   <T> T decodeBit(byte[] var1, int var2, int var3, ValueFactory<T> var4);

   <T> T decodeSet(byte[] var1, int var2, int var3, Field var4, ValueFactory<T> var5);

   <T> T decodeYear(byte[] var1, int var2, int var3, ValueFactory<T> var4);
}
