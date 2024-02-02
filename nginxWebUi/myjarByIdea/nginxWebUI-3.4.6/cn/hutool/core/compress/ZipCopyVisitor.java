package cn.hutool.core.compress;

import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ZipCopyVisitor extends SimpleFileVisitor<Path> {
   private final Path source;
   private final FileSystem fileSystem;
   private final CopyOption[] copyOptions;

   public ZipCopyVisitor(Path source, FileSystem fileSystem, CopyOption... copyOptions) {
      this.source = source;
      this.fileSystem = fileSystem;
      this.copyOptions = copyOptions;
   }

   public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
      Path targetDir = this.resolveTarget(dir);
      if (StrUtil.isNotEmpty(targetDir.toString())) {
         try {
            Files.copy(dir, targetDir, this.copyOptions);
         } catch (FileAlreadyExistsException var5) {
            if (!Files.isDirectory(targetDir, new LinkOption[0])) {
               throw var5;
            }
         }
      }

      return FileVisitResult.CONTINUE;
   }

   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      Files.copy(file, this.resolveTarget(file), this.copyOptions);
      return FileVisitResult.CONTINUE;
   }

   private Path resolveTarget(Path file) {
      return this.fileSystem.getPath(this.source.relativize(file).toString());
   }
}
