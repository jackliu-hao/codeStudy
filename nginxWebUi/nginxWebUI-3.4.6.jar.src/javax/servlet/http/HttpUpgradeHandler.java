package javax.servlet.http;

public interface HttpUpgradeHandler {
  void init(WebConnection paramWebConnection);
  
  void destroy();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpUpgradeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */