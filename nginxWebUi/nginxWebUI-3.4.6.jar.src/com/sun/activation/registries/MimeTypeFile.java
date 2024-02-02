/*     */ package com.sun.activation.registries;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.StringReader;
/*     */ import java.util.Hashtable;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class MimeTypeFile
/*     */ {
/*  34 */   private String fname = null;
/*  35 */   private Hashtable type_hash = new Hashtable();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MimeTypeFile(String new_fname) throws IOException {
/*  43 */     File mime_file = null;
/*  44 */     FileReader fr = null;
/*     */     
/*  46 */     this.fname = new_fname;
/*     */     
/*  48 */     mime_file = new File(this.fname);
/*     */     
/*  50 */     fr = new FileReader(mime_file);
/*     */     
/*     */     try {
/*  53 */       parse(new BufferedReader(fr));
/*     */     } finally {
/*     */       try {
/*  56 */         fr.close();
/*  57 */       } catch (IOException e) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MimeTypeFile(InputStream is) throws IOException {
/*  64 */     parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MimeTypeFile() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MimeTypeEntry getMimeTypeEntry(String file_ext) {
/*  77 */     return (MimeTypeEntry)this.type_hash.get(file_ext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMIMETypeString(String file_ext) {
/*  84 */     MimeTypeEntry entry = getMimeTypeEntry(file_ext);
/*     */     
/*  86 */     if (entry != null) {
/*  87 */       return entry.getMIMEType();
/*     */     }
/*  89 */     return null;
/*     */   }
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
/*     */   public void appendToRegistry(String mime_types) {
/*     */     try {
/* 109 */       parse(new BufferedReader(new StringReader(mime_types)));
/* 110 */     } catch (IOException ex) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parse(BufferedReader buf_reader) throws IOException {
/* 119 */     String line = null, prev = null;
/*     */     
/* 121 */     while ((line = buf_reader.readLine()) != null) {
/* 122 */       if (prev == null) {
/* 123 */         prev = line;
/*     */       } else {
/* 125 */         prev = prev + line;
/* 126 */       }  int end = prev.length();
/* 127 */       if (prev.length() > 0 && prev.charAt(end - 1) == '\\') {
/* 128 */         prev = prev.substring(0, end - 1);
/*     */         continue;
/*     */       } 
/* 131 */       parseEntry(prev);
/* 132 */       prev = null;
/*     */     } 
/* 134 */     if (prev != null) {
/* 135 */       parseEntry(prev);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseEntry(String line) {
/* 142 */     String mime_type = null;
/* 143 */     String file_ext = null;
/* 144 */     line = line.trim();
/*     */     
/* 146 */     if (line.length() == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 150 */     if (line.charAt(0) == '#') {
/*     */       return;
/*     */     }
/*     */     
/* 154 */     if (line.indexOf('=') > 0) {
/*     */       
/* 156 */       LineTokenizer lt = new LineTokenizer(line);
/* 157 */       while (lt.hasMoreTokens()) {
/* 158 */         String name = lt.nextToken();
/* 159 */         String value = null;
/* 160 */         if (lt.hasMoreTokens() && lt.nextToken().equals("=") && lt.hasMoreTokens())
/*     */         {
/* 162 */           value = lt.nextToken(); } 
/* 163 */         if (value == null) {
/* 164 */           if (LogSupport.isLoggable())
/* 165 */             LogSupport.log("Bad .mime.types entry: " + line); 
/*     */           return;
/*     */         } 
/* 168 */         if (name.equals("type")) {
/* 169 */           mime_type = value; continue;
/* 170 */         }  if (name.equals("exts")) {
/* 171 */           StringTokenizer st = new StringTokenizer(value, ",");
/* 172 */           while (st.hasMoreTokens()) {
/* 173 */             file_ext = st.nextToken();
/* 174 */             MimeTypeEntry entry = new MimeTypeEntry(mime_type, file_ext);
/*     */             
/* 176 */             this.type_hash.put(file_ext, entry);
/* 177 */             if (LogSupport.isLoggable()) {
/* 178 */               LogSupport.log("Added: " + entry.toString());
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 185 */       StringTokenizer strtok = new StringTokenizer(line);
/* 186 */       int num_tok = strtok.countTokens();
/*     */       
/* 188 */       if (num_tok == 0) {
/*     */         return;
/*     */       }
/* 191 */       mime_type = strtok.nextToken();
/*     */       
/* 193 */       while (strtok.hasMoreTokens()) {
/* 194 */         MimeTypeEntry entry = null;
/*     */         
/* 196 */         file_ext = strtok.nextToken();
/* 197 */         entry = new MimeTypeEntry(mime_type, file_ext);
/* 198 */         this.type_hash.put(file_ext, entry);
/* 199 */         if (LogSupport.isLoggable())
/* 200 */           LogSupport.log("Added: " + entry.toString()); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\registries\MimeTypeFile.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */