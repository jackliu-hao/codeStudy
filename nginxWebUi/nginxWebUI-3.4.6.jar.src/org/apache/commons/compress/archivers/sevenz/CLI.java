/*     */ package org.apache.commons.compress.archivers.sevenz;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
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
/*     */ public class CLI
/*     */ {
/*     */   private enum Mode
/*     */   {
/*  27 */     LIST("Analysing")
/*     */     {
/*     */       public void takeAction(SevenZFile archive, SevenZArchiveEntry entry) {
/*  30 */         System.out.print(entry.getName());
/*  31 */         if (entry.isDirectory()) {
/*  32 */           System.out.print(" dir");
/*     */         } else {
/*  34 */           System.out.print(" " + entry.getCompressedSize() + "/" + entry
/*  35 */               .getSize());
/*     */         } 
/*  37 */         if (entry.getHasLastModifiedDate()) {
/*  38 */           System.out.print(" " + entry.getLastModifiedDate());
/*     */         } else {
/*  40 */           System.out.print(" no last modified date");
/*     */         } 
/*  42 */         if (!entry.isDirectory()) {
/*  43 */           System.out.println(" " + getContentMethods(entry));
/*     */         } else {
/*  45 */           System.out.println();
/*     */         } 
/*     */       }
/*     */       
/*     */       private String getContentMethods(SevenZArchiveEntry entry) {
/*  50 */         StringBuilder sb = new StringBuilder();
/*  51 */         boolean first = true;
/*  52 */         for (SevenZMethodConfiguration m : entry.getContentMethods()) {
/*  53 */           if (!first) {
/*  54 */             sb.append(", ");
/*     */           }
/*  56 */           first = false;
/*  57 */           sb.append(m.getMethod());
/*  58 */           if (m.getOptions() != null) {
/*  59 */             sb.append("(").append(m.getOptions()).append(")");
/*     */           }
/*     */         } 
/*  62 */         return sb.toString();
/*     */       } };
/*     */     
/*     */     private final String message;
/*     */     
/*     */     Mode(String message) {
/*  68 */       this.message = message;
/*     */     }
/*     */     public String getMessage() {
/*  71 */       return this.message;
/*     */     }
/*     */     
/*     */     public abstract void takeAction(SevenZFile param1SevenZFile, SevenZArchiveEntry param1SevenZArchiveEntry) throws IOException;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  78 */     if (args.length == 0) {
/*  79 */       usage();
/*     */       return;
/*     */     } 
/*  82 */     Mode mode = grabMode(args);
/*  83 */     System.out.println(mode.getMessage() + " " + args[0]);
/*  84 */     File f = new File(args[0]);
/*  85 */     if (!f.isFile()) {
/*  86 */       System.err.println(f + " doesn't exist or is a directory");
/*     */     }
/*  88 */     try (SevenZFile archive = new SevenZFile(f)) {
/*     */       SevenZArchiveEntry ae;
/*  90 */       while ((ae = archive.getNextEntry()) != null) {
/*  91 */         mode.takeAction(archive, ae);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void usage() {
/*  97 */     System.out.println("Parameters: archive-name [list]");
/*     */   }
/*     */   
/*     */   private static Mode grabMode(String[] args) {
/* 101 */     if (args.length < 2) {
/* 102 */       return Mode.LIST;
/*     */     }
/* 104 */     return Enum.<Mode>valueOf(Mode.class, args[1].toUpperCase());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\CLI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */