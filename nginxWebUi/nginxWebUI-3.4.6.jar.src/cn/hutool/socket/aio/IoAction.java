package cn.hutool.socket.aio;

public interface IoAction<T> {
  void accept(AioSession paramAioSession);
  
  void doAction(AioSession paramAioSession, T paramT);
  
  void failed(Throwable paramThrowable, AioSession paramAioSession);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\aio\IoAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */