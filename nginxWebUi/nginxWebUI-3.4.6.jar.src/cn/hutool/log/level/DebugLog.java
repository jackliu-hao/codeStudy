package cn.hutool.log.level;

public interface DebugLog {
  boolean isDebugEnabled();
  
  void debug(Throwable paramThrowable);
  
  void debug(String paramString, Object... paramVarArgs);
  
  void debug(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void debug(String paramString1, Throwable paramThrowable, String paramString2, Object... paramVarArgs);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\level\DebugLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */