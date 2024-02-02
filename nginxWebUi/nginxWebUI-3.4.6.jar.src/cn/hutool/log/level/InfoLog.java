package cn.hutool.log.level;

public interface InfoLog {
  boolean isInfoEnabled();
  
  void info(Throwable paramThrowable);
  
  void info(String paramString, Object... paramVarArgs);
  
  void info(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void info(String paramString1, Throwable paramThrowable, String paramString2, Object... paramVarArgs);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\level\InfoLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */