package cn.hutool.core.io.file.visitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DelVisitor extends SimpleFileVisitor<Path> {
   public static DelVisitor INSTANCE = new DelVisitor();

   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      Files.delete(file);
      return FileVisitResult.CONTINUE;
   }

   public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
      if (e == null) {
         Files.delete(dir);
         return FileVisitResult.CONTINUE;
      } else {
         throw e;
      }
   }
}
