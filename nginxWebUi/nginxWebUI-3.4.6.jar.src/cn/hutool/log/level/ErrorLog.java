package cn.hutool.log.level;

public interface ErrorLog {
  boolean isErrorEnabled();
  
  void error(Throwable paramThrowable);
  
  void error(String paramString, Object... paramVarArgs);
  
  void error(Throwable paramThrowable, String paramString, Object... paramVarArgs);
  
  void error(String paramString1, Throwable paramThrowable, String paramString2, Object... paramVarArgs);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\level\ErrorLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */