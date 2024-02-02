package cn.hutool.log.level;

public interface TraceLog {
  boolean isTraceEnabled();
  
  void trace(Throwable paramThrowable);
  
  void trace(String paramString, Object... paramVarArgs);
  
  void trace(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void trace(String paramString1, Throwable paramThrowable, String paramString2, Object... paramVarArgs);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\level\TraceLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */