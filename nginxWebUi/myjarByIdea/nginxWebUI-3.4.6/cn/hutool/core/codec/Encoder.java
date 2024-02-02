package cn.hutool.core.codec;

public interface Encoder<T, R> {
   R encode(T var1);
}
