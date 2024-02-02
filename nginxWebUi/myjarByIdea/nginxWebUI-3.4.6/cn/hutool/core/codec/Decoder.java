package cn.hutool.core.codec;

public interface Decoder<T, R> {
   R decode(T var1);
}
