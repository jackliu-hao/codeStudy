package io.undertow.server.handlers.resource;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ETag;
import io.undertow.util.MimeMappings;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public interface Resource {
  String getPath();
  
  Date getLastModified();
  
  String getLastModifiedString();
  
  ETag getETag();
  
  String getName();
  
  boolean isDirectory();
  
  List<Resource> list();
  
  String getContentType(MimeMappings paramMimeMappings);
  
  void serve(Sender paramSender, HttpServerExchange paramHttpServerExchange, IoCallback paramIoCallback);
  
  Long getContentLength();
  
  String getCacheKey();
  
  File getFile();
  
  Path getFilePath();
  
  File getResourceManagerRoot();
  
  Path getResourceManagerRootPath();
  
  URL getUrl();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\Resource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */