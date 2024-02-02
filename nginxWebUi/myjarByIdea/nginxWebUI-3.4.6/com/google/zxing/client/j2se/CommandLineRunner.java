package com.google.zxing.client.j2se;

import com.beust.jcommander.JCommander;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class CommandLineRunner {
   private CommandLineRunner() {
   }

   public static void main(String[] args) throws Exception {
      DecoderConfig config = new DecoderConfig();
      JCommander jCommander = new JCommander(config, args);
      jCommander.setProgramName(CommandLineRunner.class.getSimpleName());
      if (config.help) {
         jCommander.usage();
      } else {
         List<URI> inputs = new ArrayList(config.inputPaths.size());

         URI uri;
         for(Iterator var4 = config.inputPaths.iterator(); var4.hasNext(); ((List)inputs).add(uri)) {
            String inputPath = (String)var4.next();

            try {
               uri = new URI(inputPath);
            } catch (URISyntaxException var12) {
               if (!Files.exists(Paths.get(inputPath), new LinkOption[0])) {
                  throw var12;
               }

               uri = new URI("file", inputPath, (String)null);
            }
         }

         do {
            inputs = retainValid(expand((List)inputs), config.recursive);
         } while(config.recursive && isExpandable((List)inputs));

         int numInputs = ((List)inputs).size();
         if (numInputs == 0) {
            jCommander.usage();
         } else {
            Queue<URI> syncInputs = new ConcurrentLinkedQueue((Collection)inputs);
            int numThreads = Math.min(numInputs, Runtime.getRuntime().availableProcessors());
            int successful = 0;
            if (numThreads > 1) {
               ExecutorService executor = Executors.newFixedThreadPool(numThreads);
               Collection<Future<Integer>> futures = new ArrayList(numThreads);

               for(int x = 0; x < numThreads; ++x) {
                  futures.add(executor.submit(new DecodeWorker(config, syncInputs)));
               }

               executor.shutdown();

               Future future;
               for(Iterator var16 = futures.iterator(); var16.hasNext(); successful += (Integer)future.get()) {
                  future = (Future)var16.next();
               }
            } else {
               successful += (new DecodeWorker(config, syncInputs)).call();
            }

            if (!config.brief && numInputs > 1) {
               System.out.println("\nDecoded " + successful + " files out of " + numInputs + " successfully (" + successful * 100 / numInputs + "%)\n");
            }

         }
      }
   }

   private static List<URI> expand(List<URI> inputs) throws IOException {
      List<URI> expanded = new ArrayList();
      Iterator var2 = inputs.iterator();

      while(true) {
         while(true) {
            URI input;
            while(var2.hasNext()) {
               input = (URI)var2.next();
               if (isFileOrDir(input)) {
                  Path inputPath = Paths.get(input);
                  if (Files.isDirectory(inputPath, new LinkOption[0])) {
                     DirectoryStream<Path> childPaths = Files.newDirectoryStream(inputPath);
                     Throwable var6 = null;

                     try {
                        Iterator var7 = childPaths.iterator();

                        while(var7.hasNext()) {
                           Path childPath = (Path)var7.next();
                           expanded.add(childPath.toUri());
                        }
                     } catch (Throwable var16) {
                        var6 = var16;
                        throw var16;
                     } finally {
                        if (childPaths != null) {
                           if (var6 != null) {
                              try {
                                 childPaths.close();
                              } catch (Throwable var15) {
                                 var6.addSuppressed(var15);
                              }
                           } else {
                              childPaths.close();
                           }
                        }

                     }
                  } else {
                     expanded.add(input);
                  }
               } else {
                  expanded.add(input);
               }
            }

            for(int i = 0; i < expanded.size(); ++i) {
               input = (URI)expanded.get(i);
               if (input.getScheme() == null) {
                  expanded.set(i, Paths.get(input.getRawPath()).toUri());
               }
            }

            return expanded;
         }
      }
   }

   private static List<URI> retainValid(List<URI> inputs, boolean recursive) {
      List<URI> retained = new ArrayList();
      Iterator var3 = inputs.iterator();

      while(var3.hasNext()) {
         URI input = (URI)var3.next();
         boolean retain;
         if (!isFileOrDir(input)) {
            retain = true;
         } else {
            Path inputPath = Paths.get(input);
            retain = !inputPath.getFileName().toString().startsWith(".") && (recursive || !Files.isDirectory(inputPath, new LinkOption[0]));
         }

         if (retain) {
            retained.add(input);
         }
      }

      return retained;
   }

   private static boolean isExpandable(List<URI> inputs) {
      Iterator var1 = inputs.iterator();

      URI input;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         input = (URI)var1.next();
      } while(!isFileOrDir(input) || !Files.isDirectory(Paths.get(input), new LinkOption[0]));

      return true;
   }

   private static boolean isFileOrDir(URI uri) {
      return "file".equals(uri.getScheme());
   }
}
