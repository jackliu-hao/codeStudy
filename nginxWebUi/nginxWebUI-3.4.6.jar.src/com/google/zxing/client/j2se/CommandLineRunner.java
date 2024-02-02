/*     */ package com.google.zxing.client.j2se;
/*     */ 
/*     */ import com.beust.jcommander.JCommander;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CommandLineRunner
/*     */ {
/*     */   public static void main(String[] args) throws Exception {
/*  51 */     DecoderConfig config = new DecoderConfig();
/*  52 */     JCommander jCommander = new JCommander(config, args);
/*  53 */     jCommander.setProgramName(CommandLineRunner.class.getSimpleName());
/*  54 */     if (config.help) {
/*  55 */       jCommander.usage();
/*     */       
/*     */       return;
/*     */     } 
/*  59 */     List<URI> inputs = new ArrayList<>(config.inputPaths.size());
/*  60 */     for (String inputPath : config.inputPaths) {
/*     */       URI uri;
/*     */       try {
/*  63 */         uri = new URI(inputPath);
/*  64 */       } catch (URISyntaxException use) {
/*     */         
/*  66 */         if (!Files.exists(Paths.get(inputPath, new String[0]), new java.nio.file.LinkOption[0])) {
/*  67 */           throw use;
/*     */         }
/*  69 */         uri = new URI("file", inputPath, null);
/*     */       } 
/*  71 */       inputs.add(uri);
/*     */     } 
/*     */     
/*     */     do {
/*  75 */       inputs = retainValid(expand(inputs), config.recursive);
/*  76 */     } while (config.recursive && isExpandable(inputs));
/*     */     
/*  78 */     int numInputs = inputs.size();
/*  79 */     if (numInputs == 0) {
/*  80 */       jCommander.usage();
/*     */       
/*     */       return;
/*     */     } 
/*  84 */     Queue<URI> syncInputs = new ConcurrentLinkedQueue<>(inputs);
/*  85 */     int numThreads = Math.min(numInputs, Runtime.getRuntime().availableProcessors());
/*  86 */     int successful = 0;
/*  87 */     if (numThreads > 1) {
/*  88 */       ExecutorService executor = Executors.newFixedThreadPool(numThreads);
/*  89 */       Collection<Future<Integer>> futures = new ArrayList<>(numThreads);
/*  90 */       for (int x = 0; x < numThreads; x++) {
/*  91 */         futures.add(executor.submit(new DecodeWorker(config, syncInputs)));
/*     */       }
/*  93 */       executor.shutdown();
/*  94 */       for (Future<Integer> future : futures) {
/*  95 */         successful += ((Integer)future.get()).intValue();
/*     */       }
/*     */     } else {
/*  98 */       successful += (new DecodeWorker(config, syncInputs)).call().intValue();
/*     */     } 
/*     */     
/* 101 */     if (!config.brief && numInputs > 1) {
/* 102 */       System.out.println("\nDecoded " + successful + " files out of " + numInputs + " successfully (" + (successful * 100 / numInputs) + "%)\n");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<URI> expand(List<URI> inputs) throws IOException {
/* 108 */     List<URI> expanded = new ArrayList<>();
/* 109 */     for (URI input : inputs) {
/* 110 */       if (isFileOrDir(input)) {
/* 111 */         Path inputPath = Paths.get(input);
/* 112 */         if (Files.isDirectory(inputPath, new java.nio.file.LinkOption[0])) {
/* 113 */           try (DirectoryStream<Path> childPaths = Files.newDirectoryStream(inputPath)) {
/* 114 */             for (Path childPath : childPaths)
/* 115 */               expanded.add(childPath.toUri()); 
/*     */           } 
/*     */           continue;
/*     */         } 
/* 119 */         expanded.add(input);
/*     */         continue;
/*     */       } 
/* 122 */       expanded.add(input);
/*     */     } 
/*     */     
/* 125 */     for (int i = 0; i < expanded.size(); i++) {
/* 126 */       URI input = expanded.get(i);
/* 127 */       if (input.getScheme() == null) {
/* 128 */         expanded.set(i, Paths.get(input.getRawPath(), new String[0]).toUri());
/*     */       }
/*     */     } 
/* 131 */     return expanded;
/*     */   }
/*     */   
/*     */   private static List<URI> retainValid(List<URI> inputs, boolean recursive) {
/* 135 */     List<URI> retained = new ArrayList<>();
/* 136 */     for (URI input : inputs) {
/*     */       boolean retain;
/* 138 */       if (isFileOrDir(input)) {
/* 139 */         Path inputPath = Paths.get(input);
/*     */ 
/*     */         
/* 142 */         retain = (!inputPath.getFileName().toString().startsWith(".") && (recursive || !Files.isDirectory(inputPath, new java.nio.file.LinkOption[0])));
/*     */       } else {
/* 144 */         retain = true;
/*     */       } 
/* 146 */       if (retain) {
/* 147 */         retained.add(input);
/*     */       }
/*     */     } 
/* 150 */     return retained;
/*     */   }
/*     */   
/*     */   private static boolean isExpandable(List<URI> inputs) {
/* 154 */     for (URI input : inputs) {
/* 155 */       if (isFileOrDir(input) && Files.isDirectory(Paths.get(input), new java.nio.file.LinkOption[0])) {
/* 156 */         return true;
/*     */       }
/*     */     } 
/* 159 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean isFileOrDir(URI uri) {
/* 163 */     return "file".equals(uri.getScheme());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\CommandLineRunner.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */