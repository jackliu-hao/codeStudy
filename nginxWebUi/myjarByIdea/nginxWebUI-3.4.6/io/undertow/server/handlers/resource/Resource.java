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

   String getContentType(MimeMappings var1);

   void serve(Sender var1, HttpServerExchange var2, IoCallback var3);

   Long getContentLength();

   String getCacheKey();

   File getFile();

   Path getFilePath();

   File getResourceManagerRoot();

   Path getResourceManagerRootPath();

   URL getUrl();
}
