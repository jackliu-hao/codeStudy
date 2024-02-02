package io.undertow.server.handlers.resource;

import java.io.File;

public class FileResource extends PathResource {
   public FileResource(File file, FileResourceManager manager, String path) {
      super(file.toPath(), manager, path);
   }
}
