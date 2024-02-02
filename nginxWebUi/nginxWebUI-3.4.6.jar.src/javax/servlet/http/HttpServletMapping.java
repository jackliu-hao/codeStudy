package javax.servlet.http;

public interface HttpServletMapping {
  String getMatchValue();
  
  String getPattern();
  
  String getServletName();
  
  MappingMatch getMappingMatch();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpServletMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */