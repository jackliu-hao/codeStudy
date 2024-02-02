package javax.servlet;

public interface AsyncContext {
  public static final String ASYNC_REQUEST_URI = "javax.servlet.async.request_uri";
  
  public static final String ASYNC_CONTEXT_PATH = "javax.servlet.async.context_path";
  
  public static final String ASYNC_MAPPING = "javax.servlet.async.mapping";
  
  public static final String ASYNC_PATH_INFO = "javax.servlet.async.path_info";
  
  public static final String ASYNC_SERVLET_PATH = "javax.servlet.async.servlet_path";
  
  public static final String ASYNC_QUERY_STRING = "javax.servlet.async.query_string";
  
  ServletRequest getRequest();
  
  ServletResponse getResponse();
  
  boolean hasOriginalRequestAndResponse();
  
  void dispatch();
  
  void dispatch(String paramString);
  
  void dispatch(ServletContext paramServletContext, String paramString);
  
  void complete();
  
  void start(Runnable paramRunnable);
  
  void addListener(AsyncListener paramAsyncListener);
  
  void addListener(AsyncListener paramAsyncListener, ServletRequest paramServletRequest, ServletResponse paramServletResponse);
  
  <T extends AsyncListener> T createListener(Class<T> paramClass) throws ServletException;
  
  void setTimeout(long paramLong);
  
  long getTimeout();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\AsyncContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */