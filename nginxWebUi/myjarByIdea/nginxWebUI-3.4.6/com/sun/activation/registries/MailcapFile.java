package com.sun.activation.registries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MailcapFile {
   private Map type_hash = new HashMap();
   private Map fallback_hash = new HashMap();
   private Map native_commands = new HashMap();
   private static boolean addReverse = false;

   public MailcapFile(String new_fname) throws IOException {
      if (LogSupport.isLoggable()) {
         LogSupport.log("new MailcapFile: file " + new_fname);
      }

      FileReader reader = null;

      try {
         reader = new FileReader(new_fname);
         this.parse(new BufferedReader(reader));
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (IOException var9) {
            }
         }

      }

   }

   public MailcapFile(InputStream is) throws IOException {
      if (LogSupport.isLoggable()) {
         LogSupport.log("new MailcapFile: InputStream");
      }

      this.parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
   }

   public MailcapFile() {
      if (LogSupport.isLoggable()) {
         LogSupport.log("new MailcapFile: default");
      }

   }

   public Map getMailcapList(String mime_type) {
      Map search_result = null;
      Map wildcard_result = null;
      search_result = (Map)this.type_hash.get(mime_type);
      int separator = mime_type.indexOf(47);
      String subtype = mime_type.substring(separator + 1);
      if (!subtype.equals("*")) {
         String type = mime_type.substring(0, separator + 1) + "*";
         wildcard_result = (Map)this.type_hash.get(type);
         if (wildcard_result != null) {
            if (search_result != null) {
               search_result = this.mergeResults(search_result, wildcard_result);
            } else {
               search_result = wildcard_result;
            }
         }
      }

      return search_result;
   }

   public Map getMailcapFallbackList(String mime_type) {
      Map search_result = null;
      Map wildcard_result = null;
      search_result = (Map)this.fallback_hash.get(mime_type);
      int separator = mime_type.indexOf(47);
      String subtype = mime_type.substring(separator + 1);
      if (!subtype.equals("*")) {
         String type = mime_type.substring(0, separator + 1) + "*";
         wildcard_result = (Map)this.fallback_hash.get(type);
         if (wildcard_result != null) {
            if (search_result != null) {
               search_result = this.mergeResults(search_result, wildcard_result);
            } else {
               search_result = wildcard_result;
            }
         }
      }

      return search_result;
   }

   public String[] getMimeTypes() {
      Set types = new HashSet(this.type_hash.keySet());
      types.addAll(this.fallback_hash.keySet());
      types.addAll(this.native_commands.keySet());
      String[] mts = new String[types.size()];
      mts = (String[])((String[])types.toArray(mts));
      return mts;
   }

   public String[] getNativeCommands(String mime_type) {
      String[] cmds = null;
      List v = (List)this.native_commands.get(mime_type.toLowerCase());
      if (v != null) {
         cmds = new String[v.size()];
         cmds = (String[])((String[])v.toArray(cmds));
      }

      return cmds;
   }

   private Map mergeResults(Map first, Map second) {
      Iterator verb_enum = second.keySet().iterator();
      Map clonedHash = new HashMap(first);

      while(verb_enum.hasNext()) {
         String verb = (String)verb_enum.next();
         List cmdVector = (List)clonedHash.get(verb);
         if (cmdVector == null) {
            clonedHash.put(verb, second.get(verb));
         } else {
            List oldV = (List)second.get(verb);
            List cmdVector = new ArrayList(cmdVector);
            cmdVector.addAll(oldV);
            clonedHash.put(verb, cmdVector);
         }
      }

      return clonedHash;
   }

   public void appendToMailcap(String mail_cap) {
      if (LogSupport.isLoggable()) {
         LogSupport.log("appendToMailcap: " + mail_cap);
      }

      try {
         this.parse(new StringReader(mail_cap));
      } catch (IOException var3) {
      }

   }

   private void parse(Reader reader) throws IOException {
      BufferedReader buf_reader = new BufferedReader(reader);
      String line = null;
      String continued = null;

      while((line = buf_reader.readLine()) != null) {
         line = line.trim();

         try {
            if (line.charAt(0) != '#') {
               if (line.charAt(line.length() - 1) == '\\') {
                  if (continued != null) {
                     continued = continued + line.substring(0, line.length() - 1);
                  } else {
                     continued = line.substring(0, line.length() - 1);
                  }
               } else if (continued != null) {
                  continued = continued + line;

                  try {
                     this.parseLine(continued);
                  } catch (MailcapParseException var7) {
                  }

                  continued = null;
               } else {
                  try {
                     this.parseLine(line);
                  } catch (MailcapParseException var6) {
                  }
               }
            }
         } catch (StringIndexOutOfBoundsException var8) {
         }
      }

   }

   protected void parseLine(String mailcapEntry) throws MailcapParseException, IOException {
      MailcapTokenizer tokenizer = new MailcapTokenizer(mailcapEntry);
      tokenizer.setIsAutoquoting(false);
      if (LogSupport.isLoggable()) {
         LogSupport.log("parse: " + mailcapEntry);
      }

      int currentToken = tokenizer.nextToken();
      if (currentToken != 2) {
         reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
      }

      String primaryType = tokenizer.getCurrentTokenValue().toLowerCase();
      String subType = "*";
      currentToken = tokenizer.nextToken();
      if (currentToken != 47 && currentToken != 59) {
         reportParseError(47, 59, currentToken, tokenizer.getCurrentTokenValue());
      }

      if (currentToken == 47) {
         currentToken = tokenizer.nextToken();
         if (currentToken != 2) {
            reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
         }

         subType = tokenizer.getCurrentTokenValue().toLowerCase();
         currentToken = tokenizer.nextToken();
      }

      String mimeType = primaryType + "/" + subType;
      if (LogSupport.isLoggable()) {
         LogSupport.log("  Type: " + mimeType);
      }

      Map commands = new LinkedHashMap();
      if (currentToken != 59) {
         reportParseError(59, currentToken, tokenizer.getCurrentTokenValue());
      }

      tokenizer.setIsAutoquoting(true);
      currentToken = tokenizer.nextToken();
      tokenizer.setIsAutoquoting(false);
      if (currentToken != 2 && currentToken != 59) {
         reportParseError(2, 59, currentToken, tokenizer.getCurrentTokenValue());
      }

      if (currentToken == 2) {
         List v = (List)this.native_commands.get(mimeType);
         if (v == null) {
            List v = new ArrayList();
            v.add(mailcapEntry);
            this.native_commands.put(mimeType, v);
         } else {
            v.add(mailcapEntry);
         }
      }

      if (currentToken != 59) {
         currentToken = tokenizer.nextToken();
      }

      if (currentToken == 59) {
         boolean isFallback = false;

         do {
            currentToken = tokenizer.nextToken();
            if (currentToken != 2) {
               reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
            }

            String paramName = tokenizer.getCurrentTokenValue().toLowerCase();
            currentToken = tokenizer.nextToken();
            if (currentToken != 61 && currentToken != 59 && currentToken != 5) {
               reportParseError(61, 59, 5, currentToken, tokenizer.getCurrentTokenValue());
            }

            if (currentToken == 61) {
               tokenizer.setIsAutoquoting(true);
               currentToken = tokenizer.nextToken();
               tokenizer.setIsAutoquoting(false);
               if (currentToken != 2) {
                  reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
               }

               String paramValue = tokenizer.getCurrentTokenValue();
               if (paramName.startsWith("x-java-")) {
                  String commandName = paramName.substring(7);
                  if (commandName.equals("fallback-entry") && paramValue.equalsIgnoreCase("true")) {
                     isFallback = true;
                  } else {
                     if (LogSupport.isLoggable()) {
                        LogSupport.log("    Command: " + commandName + ", Class: " + paramValue);
                     }

                     List classes = (List)commands.get(commandName);
                     if (classes == null) {
                        classes = new ArrayList();
                        commands.put(commandName, classes);
                     }

                     if (addReverse) {
                        ((List)classes).add(0, paramValue);
                     } else {
                        ((List)classes).add(paramValue);
                     }
                  }
               }

               currentToken = tokenizer.nextToken();
            }
         } while(currentToken == 59);

         Map masterHash = isFallback ? this.fallback_hash : this.type_hash;
         Map curcommands = (Map)masterHash.get(mimeType);
         if (curcommands == null) {
            masterHash.put(mimeType, commands);
         } else {
            if (LogSupport.isLoggable()) {
               LogSupport.log("Merging commands for type " + mimeType);
            }

            Iterator cn = curcommands.keySet().iterator();

            label123:
            while(true) {
               List ccv;
               List cv;
               do {
                  String cmdName;
                  if (!cn.hasNext()) {
                     cn = commands.keySet().iterator();

                     while(cn.hasNext()) {
                        cmdName = (String)cn.next();
                        if (!curcommands.containsKey(cmdName)) {
                           ccv = (List)commands.get(cmdName);
                           curcommands.put(cmdName, ccv);
                        }
                     }
                     break label123;
                  }

                  cmdName = (String)cn.next();
                  ccv = (List)curcommands.get(cmdName);
                  cv = (List)commands.get(cmdName);
               } while(cv == null);

               Iterator cvn = cv.iterator();

               while(cvn.hasNext()) {
                  String clazz = (String)cvn.next();
                  if (!ccv.contains(clazz)) {
                     if (addReverse) {
                        ccv.add(0, clazz);
                     } else {
                        ccv.add(clazz);
                     }
                  }
               }
            }
         }
      } else if (currentToken != 5) {
         reportParseError(5, 59, currentToken, tokenizer.getCurrentTokenValue());
      }

   }

   protected static void reportParseError(int expectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
      throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " token.");
   }

   protected static void reportParseError(int expectedToken, int otherExpectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
      throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " or a " + MailcapTokenizer.nameForToken(otherExpectedToken) + " token.");
   }

   protected static void reportParseError(int expectedToken, int otherExpectedToken, int anotherExpectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
      if (LogSupport.isLoggable()) {
         LogSupport.log("PARSE ERROR: Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
      }

      throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
   }

   static {
      try {
         addReverse = Boolean.getBoolean("javax.activation.addreverse");
      } catch (Throwable var1) {
      }

   }
}
