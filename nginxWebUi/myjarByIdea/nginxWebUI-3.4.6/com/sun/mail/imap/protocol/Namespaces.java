package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import java.util.Vector;

public class Namespaces {
   public Namespace[] personal;
   public Namespace[] otherUsers;
   public Namespace[] shared;

   public Namespaces(Response r) throws ProtocolException {
      this.personal = this.getNamespaces(r);
      this.otherUsers = this.getNamespaces(r);
      this.shared = this.getNamespaces(r);
   }

   private Namespace[] getNamespaces(Response r) throws ProtocolException {
      r.skipSpaces();
      if (r.peekByte() != 40) {
         String s = r.readAtom();
         if (s == null) {
            throw new ProtocolException("Expected NIL, got null");
         } else if (!s.equalsIgnoreCase("NIL")) {
            throw new ProtocolException("Expected NIL, got " + s);
         } else {
            return null;
         }
      } else {
         Vector v = new Vector();
         r.readByte();

         do {
            Namespace ns = new Namespace(r);
            v.addElement(ns);
         } while(r.peekByte() != 41);

         r.readByte();
         Namespace[] nsa = new Namespace[v.size()];
         v.copyInto(nsa);
         return nsa;
      }
   }

   public static class Namespace {
      public String prefix;
      public char delimiter;

      public Namespace(Response r) throws ProtocolException {
         if (r.readByte() != 40) {
            throw new ProtocolException("Missing '(' at start of Namespace");
         } else {
            this.prefix = BASE64MailboxDecoder.decode(r.readString());
            r.skipSpaces();
            if (r.peekByte() == 34) {
               r.readByte();
               this.delimiter = (char)r.readByte();
               if (this.delimiter == '\\') {
                  this.delimiter = (char)r.readByte();
               }

               if (r.readByte() != 34) {
                  throw new ProtocolException("Missing '\"' at end of QUOTED_CHAR");
               }
            } else {
               String s = r.readAtom();
               if (s == null) {
                  throw new ProtocolException("Expected NIL, got null");
               }

               if (!s.equalsIgnoreCase("NIL")) {
                  throw new ProtocolException("Expected NIL, got " + s);
               }

               this.delimiter = 0;
            }

            if (r.peekByte() != 41) {
               r.skipSpaces();
               r.readString();
               r.skipSpaces();
               r.readStringList();
            }

            if (r.readByte() != 41) {
               throw new ProtocolException("Missing ')' at end of Namespace");
            }
         }
      }
   }
}
