package cn.hutool.log.level;

public interface WarnLog {
  boolean isWarnEnabled();
  
  void warn(Throwable paramThrowable);
  
  void warn(String paramString, Object... paramVarArgs);
  
  void warn(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void warn(String paramString1, Throwable paramThrowable, String paramString2, Object... paramVarArgs);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\level\WarnLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */