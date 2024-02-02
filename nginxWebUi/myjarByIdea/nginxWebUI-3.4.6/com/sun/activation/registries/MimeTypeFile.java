package com.sun.activation.registries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class MimeTypeFile {
   private String fname = null;
   private Hashtable type_hash = new Hashtable();

   public MimeTypeFile(String new_fname) throws IOException {
      File mime_file = null;
      FileReader fr = null;
      this.fname = new_fname;
      mime_file = new File(this.fname);
      fr = new FileReader(mime_file);

      try {
         this.parse(new BufferedReader(fr));
      } finally {
         try {
            fr.close();
         } catch (IOException var10) {
         }

      }

   }

   public MimeTypeFile(InputStream is) throws IOException {
      this.parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
   }

   public MimeTypeFile() {
   }

   public MimeTypeEntry getMimeTypeEntry(String file_ext) {
      return (MimeTypeEntry)this.type_hash.get(file_ext);
   }

   public String getMIMETypeString(String file_ext) {
      MimeTypeEntry entry = this.getMimeTypeEntry(file_ext);
      return entry != null ? entry.getMIMEType() : null;
   }

   public void appendToRegistry(String mime_types) {
      try {
         this.parse(new BufferedReader(new StringReader(mime_types)));
      } catch (IOException var3) {
      }

   }

   private void parse(BufferedReader buf_reader) throws IOException {
      String line = null;
      String prev = null;

      while(true) {
         while((line = buf_reader.readLine()) != null) {
            if (prev == null) {
               prev = line;
            } else {
               prev = prev + line;
            }

            int end = prev.length();
            if (prev.length() > 0 && prev.charAt(end - 1) == '\\') {
               prev = prev.substring(0, end - 1);
            } else {
               this.parseEntry(prev);
               prev = null;
            }
         }

         if (prev != null) {
            this.parseEntry(prev);
         }

         return;
      }
   }

   private void parseEntry(String line) {
      String mime_type = null;
      String file_ext = null;
      line = line.trim();
      if (line.length() != 0) {
         if (line.charAt(0) != '#') {
            String value;
            if (line.indexOf(61) > 0) {
               LineTokenizer lt = new LineTokenizer(line);

               while(true) {
                  while(lt.hasMoreTokens()) {
                     String name = lt.nextToken();
                     value = null;
                     if (lt.hasMoreTokens() && lt.nextToken().equals("=") && lt.hasMoreTokens()) {
                        value = lt.nextToken();
                     }

                     if (value == null) {
                        if (LogSupport.isLoggable()) {
                           LogSupport.log("Bad .mime.types entry: " + line);
                        }

                        return;
                     }

                     if (name.equals("type")) {
                        mime_type = value;
                     } else if (name.equals("exts")) {
                        StringTokenizer st = new StringTokenizer(value, ",");

                        while(st.hasMoreTokens()) {
                           file_ext = st.nextToken();
                           MimeTypeEntry entry = new MimeTypeEntry(mime_type, file_ext);
                           this.type_hash.put(file_ext, entry);
                           if (LogSupport.isLoggable()) {
                              LogSupport.log("Added: " + entry.toString());
                           }
                        }
                     }
                  }

                  return;
               }
            } else {
               StringTokenizer strtok = new StringTokenizer(line);
               int num_tok = strtok.countTokens();
               if (num_tok != 0) {
                  mime_type = strtok.nextToken();

                  while(strtok.hasMoreTokens()) {
                     value = null;
                     file_ext = strtok.nextToken();
                     MimeTypeEntry entry = new MimeTypeEntry(mime_type, file_ext);
                     this.type_hash.put(file_ext, entry);
                     if (LogSupport.isLoggable()) {
                        LogSupport.log("Added: " + entry.toString());
                     }
                  }

               }
            }
         }
      }
   }
}
