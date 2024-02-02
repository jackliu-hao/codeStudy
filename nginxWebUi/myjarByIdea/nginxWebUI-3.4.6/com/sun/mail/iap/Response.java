package com.sun.mail.iap;

import com.sun.mail.util.ASCIIUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

public class Response {
   protected int index;
   protected int pindex;
   protected int size;
   protected byte[] buffer = null;
   protected int type = 0;
   protected String tag = null;
   private static final int increment = 100;
   public static final int TAG_MASK = 3;
   public static final int CONTINUATION = 1;
   public static final int TAGGED = 2;
   public static final int UNTAGGED = 3;
   public static final int TYPE_MASK = 28;
   public static final int OK = 4;
   public static final int NO = 8;
   public static final int BAD = 12;
   public static final int BYE = 16;
   public static final int SYNTHETIC = 32;

   public Response(String s) {
      this.buffer = ASCIIUtility.getBytes(s);
      this.size = this.buffer.length;
      this.parse();
   }

   public Response(Protocol p) throws IOException, ProtocolException {
      ByteArray ba = p.getResponseBuffer();
      ByteArray response = p.getInputStream().readResponse(ba);
      this.buffer = response.getBytes();
      this.size = response.getCount() - 2;
      this.parse();
   }

   public Response(Response r) {
      this.index = r.index;
      this.size = r.size;
      this.buffer = r.buffer;
      this.type = r.type;
      this.tag = r.tag;
   }

   public static Response byeResponse(Exception ex) {
      String err = "* BYE JavaMail Exception: " + ex.toString();
      err = err.replace('\r', ' ').replace('\n', ' ');
      Response r = new Response(err);
      r.type |= 32;
      return r;
   }

   private void parse() {
      this.index = 0;
      if (this.size != 0) {
         if (this.buffer[this.index] == 43) {
            this.type |= 1;
            ++this.index;
         } else {
            if (this.buffer[this.index] == 42) {
               this.type |= 3;
               ++this.index;
            } else {
               this.type |= 2;
               this.tag = this.readAtom();
               if (this.tag == null) {
                  this.tag = "";
               }
            }

            int mark = this.index;
            String s = this.readAtom();
            if (s == null) {
               s = "";
            }

            if (s.equalsIgnoreCase("OK")) {
               this.type |= 4;
            } else if (s.equalsIgnoreCase("NO")) {
               this.type |= 8;
            } else if (s.equalsIgnoreCase("BAD")) {
               this.type |= 12;
            } else if (s.equalsIgnoreCase("BYE")) {
               this.type |= 16;
            } else {
               this.index = mark;
            }

            this.pindex = this.index;
         }
      }
   }

   public void skipSpaces() {
      while(this.index < this.size && this.buffer[this.index] == 32) {
         ++this.index;
      }

   }

   public void skipToken() {
      while(this.index < this.size && this.buffer[this.index] != 32) {
         ++this.index;
      }

   }

   public void skip(int count) {
      this.index += count;
   }

   public byte peekByte() {
      return this.index < this.size ? this.buffer[this.index] : 0;
   }

   public byte readByte() {
      return this.index < this.size ? this.buffer[this.index++] : 0;
   }

   public String readAtom() {
      return this.readAtom('\u0000');
   }

   public String readAtom(char delim) {
      this.skipSpaces();
      if (this.index >= this.size) {
         return null;
      } else {
         byte b;
         int start;
         for(start = this.index; this.index < this.size && (b = this.buffer[this.index]) > 32 && b != 40 && b != 41 && b != 37 && b != 42 && b != 34 && b != 92 && b != 127 && (delim == 0 || b != delim); ++this.index) {
         }

         return ASCIIUtility.toString(this.buffer, start, this.index);
      }
   }

   public String readString(char delim) {
      this.skipSpaces();
      if (this.index >= this.size) {
         return null;
      } else {
         int start;
         for(start = this.index; this.index < this.size && this.buffer[this.index] != delim; ++this.index) {
         }

         return ASCIIUtility.toString(this.buffer, start, this.index);
      }
   }

   public String[] readStringList() {
      return this.readStringList(false);
   }

   public String[] readAtomStringList() {
      return this.readStringList(true);
   }

   private String[] readStringList(boolean atom) {
      this.skipSpaces();
      if (this.buffer[this.index] != 40) {
         return null;
      } else {
         ++this.index;
         Vector v = new Vector();

         do {
            v.addElement(atom ? this.readAtomString() : this.readString());
         } while(this.buffer[this.index++] != 41);

         int size = v.size();
         if (size > 0) {
            String[] s = new String[size];
            v.copyInto(s);
            return s;
         } else {
            return null;
         }
      }
   }

   public int readNumber() {
      this.skipSpaces();

      int start;
      for(start = this.index; this.index < this.size && Character.isDigit((char)this.buffer[this.index]); ++this.index) {
      }

      if (this.index > start) {
         try {
            return ASCIIUtility.parseInt(this.buffer, start, this.index);
         } catch (NumberFormatException var3) {
         }
      }

      return -1;
   }

   public long readLong() {
      this.skipSpaces();

      int start;
      for(start = this.index; this.index < this.size && Character.isDigit((char)this.buffer[this.index]); ++this.index) {
      }

      if (this.index > start) {
         try {
            return ASCIIUtility.parseLong(this.buffer, start, this.index);
         } catch (NumberFormatException var3) {
         }
      }

      return -1L;
   }

   public String readString() {
      return (String)this.parseString(false, true);
   }

   public ByteArrayInputStream readBytes() {
      ByteArray ba = this.readByteArray();
      return ba != null ? ba.toByteArrayInputStream() : null;
   }

   public ByteArray readByteArray() {
      if (this.isContinuation()) {
         this.skipSpaces();
         return new ByteArray(this.buffer, this.index, this.size - this.index);
      } else {
         return (ByteArray)this.parseString(false, false);
      }
   }

   public String readAtomString() {
      return (String)this.parseString(true, true);
   }

   private Object parseString(boolean parseAtoms, boolean returnString) {
      this.skipSpaces();
      byte b = this.buffer[this.index];
      int start;
      int count;
      if (b == 34) {
         ++this.index;
         start = this.index;

         for(count = this.index; this.index < this.size && (b = this.buffer[this.index]) != 34; ++this.index) {
            if (b == 92) {
               ++this.index;
            }

            if (this.index != count) {
               this.buffer[count] = this.buffer[this.index];
            }

            ++count;
         }

         if (this.index >= this.size) {
            return null;
         } else {
            ++this.index;
            return returnString ? ASCIIUtility.toString(this.buffer, start, count) : new ByteArray(this.buffer, start, count - start);
         }
      } else if (b != 123) {
         if (parseAtoms) {
            start = this.index;
            String s = this.readAtom();
            return returnString ? s : new ByteArray(this.buffer, start, this.index);
         } else if (b != 78 && b != 110) {
            return null;
         } else {
            this.index += 3;
            return null;
         }
      } else {
         for(start = ++this.index; this.buffer[this.index] != 125; ++this.index) {
         }

         int count = false;

         try {
            count = ASCIIUtility.parseInt(this.buffer, start, this.index);
         } catch (NumberFormatException var7) {
            return null;
         }

         start = this.index + 3;
         this.index = start + count;
         return returnString ? ASCIIUtility.toString(this.buffer, start, start + count) : new ByteArray(this.buffer, start, count);
      }
   }

   public int getType() {
      return this.type;
   }

   public boolean isContinuation() {
      return (this.type & 3) == 1;
   }

   public boolean isTagged() {
      return (this.type & 3) == 2;
   }

   public boolean isUnTagged() {
      return (this.type & 3) == 3;
   }

   public boolean isOK() {
      return (this.type & 28) == 4;
   }

   public boolean isNO() {
      return (this.type & 28) == 8;
   }

   public boolean isBAD() {
      return (this.type & 28) == 12;
   }

   public boolean isBYE() {
      return (this.type & 28) == 16;
   }

   public boolean isSynthetic() {
      return (this.type & 32) == 32;
   }

   public String getTag() {
      return this.tag;
   }

   public String getRest() {
      this.skipSpaces();
      return ASCIIUtility.toString(this.buffer, this.index, this.size);
   }

   public void reset() {
      this.index = this.pindex;
   }

   public String toString() {
      return ASCIIUtility.toString(this.buffer, 0, this.size);
   }
}
