package cn.hutool.core.io;

public interface StreamProgress {
  void start();
  
  void progress(long paramLong1, long paramLong2);
  
  void finish();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\StreamProgress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */