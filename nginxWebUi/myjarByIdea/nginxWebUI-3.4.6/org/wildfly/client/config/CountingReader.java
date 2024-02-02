package org.wildfly.client.config;

import java.io.IOException;
import java.io.Reader;

final class CountingReader extends Reader {
   private int lineNumber = 1;
   private int columnNumber = 1;
   private int characterOffset = 0;
   private final Reader reader;

   CountingReader(Reader reader) {
      this.reader = reader;
   }

   public int read() throws IOException {
      int ch = this.reader.read();
      if (ch == -1) {
         return -1;
      } else {
         this.processChar(ch);
         return ch;
      }
   }

   private void processChar(int ch) {
      switch (ch) {
         case 10:
            ++this.characterOffset;
            ++this.lineNumber;
            this.columnNumber = 1;
            break;
         default:
            if (!Character.isLowSurrogate((char)ch)) {
               ++this.characterOffset;
               ++this.columnNumber;
            }
      }

   }

   public int read(char[] cbuf) throws IOException {
      int cnt = this.reader.read(cbuf);
      if (cnt > 0) {
         for(int i = 0; i < cnt; ++i) {
            this.processChar(cbuf[i]);
         }
      }

      return cnt;
   }

   public int read(char[] cbuf, int off, int len) throws IOException {
      int cnt = this.reader.read(cbuf, off, len);
      if (cnt > 0) {
         for(int i = 0; i < cnt; ++i) {
            this.processChar(cbuf[i + off]);
         }
      }

      return cnt;
   }

   public void close() throws IOException {
      this.reader.close();
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }

   public int getCharacterOffset() {
      return this.characterOffset;
   }
}
