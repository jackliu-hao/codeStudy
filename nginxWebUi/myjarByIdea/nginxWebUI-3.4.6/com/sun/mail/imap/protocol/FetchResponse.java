package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FetchResponse extends IMAPResponse {
   private Item[] items;
   private Map extensionItems;
   private final FetchItem[] fitems;
   private static final char[] HEADER = new char[]{'.', 'H', 'E', 'A', 'D', 'E', 'R'};
   private static final char[] TEXT = new char[]{'.', 'T', 'E', 'X', 'T'};

   public FetchResponse(Protocol p) throws IOException, ProtocolException {
      super(p);
      this.fitems = null;
      this.parse();
   }

   public FetchResponse(IMAPResponse r) throws IOException, ProtocolException {
      this(r, (FetchItem[])null);
   }

   public FetchResponse(IMAPResponse r, FetchItem[] fitems) throws IOException, ProtocolException {
      super(r);
      this.fitems = fitems;
      this.parse();
   }

   public int getItemCount() {
      return this.items.length;
   }

   public Item getItem(int index) {
      return this.items[index];
   }

   public Item getItem(Class c) {
      for(int i = 0; i < this.items.length; ++i) {
         if (c.isInstance(this.items[i])) {
            return this.items[i];
         }
      }

      return null;
   }

   public static Item getItem(Response[] r, int msgno, Class c) {
      if (r == null) {
         return null;
      } else {
         for(int i = 0; i < r.length; ++i) {
            if (r[i] != null && r[i] instanceof FetchResponse && ((FetchResponse)r[i]).getNumber() == msgno) {
               FetchResponse f = (FetchResponse)r[i];

               for(int j = 0; j < f.items.length; ++j) {
                  if (c.isInstance(f.items[j])) {
                     return f.items[j];
                  }
               }
            }
         }

         return null;
      }
   }

   public Map getExtensionItems() {
      if (this.extensionItems == null) {
         this.extensionItems = new HashMap();
      }

      return this.extensionItems;
   }

   private void parse() throws ParsingException {
      this.skipSpaces();
      if (this.buffer[this.index] != 40) {
         throw new ParsingException("error in FETCH parsing, missing '(' at index " + this.index);
      } else {
         Vector v = new Vector();
         Item i = null;

         do {
            ++this.index;
            if (this.index >= this.size) {
               throw new ParsingException("error in FETCH parsing, ran off end of buffer, size " + this.size);
            }

            i = this.parseItem();
            if (i != null) {
               v.addElement(i);
            } else if (!this.parseExtensionItem()) {
               throw new ParsingException("error in FETCH parsing, unrecognized item at index " + this.index);
            }
         } while(this.buffer[this.index] != 41);

         ++this.index;
         this.items = new Item[v.size()];
         v.copyInto(this.items);
      }
   }

   private Item parseItem() throws ParsingException {
      switch (this.buffer[this.index]) {
         case 66:
         case 98:
            if (this.match(BODYSTRUCTURE.name)) {
               return new BODYSTRUCTURE(this);
            }

            if (this.match(BODY.name)) {
               if (this.buffer[this.index] == 91) {
                  return new BODY(this);
               }

               return new BODYSTRUCTURE(this);
            }
            break;
         case 69:
         case 101:
            if (this.match(ENVELOPE.name)) {
               return new ENVELOPE(this);
            }
            break;
         case 70:
         case 102:
            if (this.match(FLAGS.name)) {
               return new FLAGS(this);
            }
            break;
         case 73:
         case 105:
            if (this.match(INTERNALDATE.name)) {
               return new INTERNALDATE(this);
            }
            break;
         case 82:
         case 114:
            if (this.match(RFC822SIZE.name)) {
               return new RFC822SIZE(this);
            }

            if (this.match(RFC822DATA.name)) {
               if (!this.match(HEADER) && this.match(TEXT)) {
               }

               return new RFC822DATA(this);
            }
            break;
         case 85:
         case 117:
            if (this.match(UID.name)) {
               return new UID(this);
            }
      }

      return null;
   }

   private boolean parseExtensionItem() throws ParsingException {
      if (this.fitems == null) {
         return false;
      } else {
         for(int i = 0; i < this.fitems.length; ++i) {
            if (this.match(this.fitems[i].getName())) {
               this.getExtensionItems().put(this.fitems[i].getName(), this.fitems[i].parseItem(this));
               return true;
            }
         }

         return false;
      }
   }

   private boolean match(char[] itemName) {
      int len = itemName.length;
      int i = 0;
      int j = this.index;

      do {
         if (i >= len) {
            this.index += len;
            return true;
         }
      } while(Character.toUpperCase((char)this.buffer[j++]) == itemName[i++]);

      return false;
   }

   private boolean match(String itemName) {
      int len = itemName.length();
      int i = 0;
      int j = this.index;

      do {
         if (i >= len) {
            this.index += len;
            return true;
         }
      } while(Character.toUpperCase((char)this.buffer[j++]) == itemName.charAt(i++));

      return false;
   }
}
