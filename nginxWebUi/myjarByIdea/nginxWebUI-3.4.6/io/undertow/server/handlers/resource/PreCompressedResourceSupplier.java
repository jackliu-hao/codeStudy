package io.undertow.server.handlers.resource;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.ETag;
import io.undertow.util.Headers;
import io.undertow.util.MimeMappings;
import io.undertow.util.QValueParser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PreCompressedResourceSupplier implements ResourceSupplier {
   private final ResourceManager resourceManager;
   private final Map<String, String> encodingMap = new CopyOnWriteMap();

   public PreCompressedResourceSupplier(ResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public Resource getResource(HttpServerExchange exchange, String path) throws IOException {
      if (exchange.getRequestHeaders().contains(Headers.RANGE)) {
         return this.resourceManager.getResource(path);
      } else {
         Resource resource = this.getEncodedResource(exchange, path);
         return resource == null ? this.resourceManager.getResource(path) : resource;
      }
   }

   private Resource getEncodedResource(HttpServerExchange exchange, String path) throws IOException {
      List<String> res = exchange.getRequestHeaders().get(Headers.ACCEPT_ENCODING);
      if (res != null && !res.isEmpty()) {
         List<List<QValueParser.QValueResult>> found = QValueParser.parse(res);
         Iterator var5 = found.iterator();

         while(var5.hasNext()) {
            List<QValueParser.QValueResult> result = (List)var5.next();
            Iterator var7 = result.iterator();

            while(var7.hasNext()) {
               final QValueParser.QValueResult value = (QValueParser.QValueResult)var7.next();
               final String extension = (String)this.encodingMap.get(value.getValue());
               if (extension != null) {
                  String newPath = path + extension;
                  final Resource resource = this.resourceManager.getResource(newPath);
                  if (resource != null && !resource.isDirectory()) {
                     return new Resource() {
                        public String getPath() {
                           return resource.getPath();
                        }

                        public Date getLastModified() {
                           return resource.getLastModified();
                        }

                        public String getLastModifiedString() {
                           return resource.getLastModifiedString();
                        }

                        public ETag getETag() {
                           return resource.getETag();
                        }

                        public String getName() {
                           return resource.getName();
                        }

                        public boolean isDirectory() {
                           return false;
                        }

                        public List<Resource> list() {
                           return resource.list();
                        }

                        public String getContentType(MimeMappings mimeMappings) {
                           String fileName = resource.getName();
                           String originalFileName = fileName.substring(0, fileName.length() - extension.length());
                           int index = originalFileName.lastIndexOf(46);
                           return index != -1 && index != originalFileName.length() - 1 ? mimeMappings.getMimeType(originalFileName.substring(index + 1)) : null;
                        }

                        public void serve(Sender sender, HttpServerExchange exchange, IoCallback completionCallback) {
                           exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, value.getValue());
                           resource.serve(sender, exchange, completionCallback);
                        }

                        public Long getContentLength() {
                           return resource.getContentLength();
                        }

                        public String getCacheKey() {
                           return resource.getCacheKey();
                        }

                        public File getFile() {
                           return resource.getFile();
                        }

                        public Path getFilePath() {
                           return resource.getFilePath();
                        }

                        public File getResourceManagerRoot() {
                           return resource.getResourceManagerRoot();
                        }

                        public Path getResourceManagerRootPath() {
                           return resource.getResourceManagerRootPath();
                        }

                        public URL getUrl() {
                           return resource.getUrl();
                        }
                     };
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public PreCompressedResourceSupplier addEncoding(String encoding, String extension) {
      this.encodingMap.put(encoding, extension);
      return this;
   }

   public PreCompressedResourceSupplier removeEncoding(String encoding) {
      this.encodingMap.remove(encoding);
      return this;
   }
}
