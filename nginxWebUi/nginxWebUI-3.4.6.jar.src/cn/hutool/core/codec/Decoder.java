package cn.hutool.core.codec;

public interface Decoder<T, R> {
  R decode(T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */