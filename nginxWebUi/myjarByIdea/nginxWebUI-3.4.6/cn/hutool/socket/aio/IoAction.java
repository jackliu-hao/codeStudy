package cn.hutool.socket.aio;

public interface IoAction<T> {
   void accept(AioSession var1);

   void doAction(AioSession var1, T var2);

   void failed(Throwable var1, AioSession var2);
}
