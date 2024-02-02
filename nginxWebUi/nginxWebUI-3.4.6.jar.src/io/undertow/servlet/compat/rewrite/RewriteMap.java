package io.undertow.servlet.compat.rewrite;

public interface RewriteMap {
  String setParameters(String paramString);
  
  String lookup(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\RewriteMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */