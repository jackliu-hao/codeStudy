package cn.hutool.socket.protocol;

public interface Protocol<T> extends MsgEncoder<T>, MsgDecoder<T> {
}
