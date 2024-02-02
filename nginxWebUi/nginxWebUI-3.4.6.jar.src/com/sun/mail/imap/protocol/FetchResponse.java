/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
/*     */ import com.sun.mail.iap.Protocol;
/*     */ import com.sun.mail.iap.ProtocolException;
/*     */ import com.sun.mail.iap.Response;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
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
/*     */ public class FetchResponse
/*     */   extends IMAPResponse
/*     */ {
/*     */   private Item[] items;
/*     */   private Map extensionItems;
/*     */   private final FetchItem[] fitems;
/*     */   
/*     */   public FetchResponse(Protocol p) throws IOException, ProtocolException {
/*  73 */     super(p);
/*  74 */     this.fitems = null;
/*  75 */     parse();
/*     */   }
/*     */ 
/*     */   
/*     */   public FetchResponse(IMAPResponse r) throws IOException, ProtocolException {
/*  80 */     this(r, (FetchItem[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FetchResponse(IMAPResponse r, FetchItem[] fitems) throws IOException, ProtocolException {
/*  90 */     super(r);
/*  91 */     this.fitems = fitems;
/*  92 */     parse();
/*     */   }
/*     */   
/*     */   public int getItemCount() {
/*  96 */     return this.items.length;
/*     */   }
/*     */   
/*     */   public Item getItem(int index) {
/* 100 */     return this.items[index];
/*     */   }
/*     */   
/*     */   public Item getItem(Class c) {
/* 104 */     for (int i = 0; i < this.items.length; i++) {
/* 105 */       if (c.isInstance(this.items[i])) {
/* 106 */         return this.items[i];
/*     */       }
/*     */     } 
/* 109 */     return null;
/*     */   }
/*     */   
/*     */   public static Item getItem(Response[] r, int msgno, Class c) {
/* 113 */     if (r == null) {
/* 114 */       return null;
/*     */     }
/* 116 */     for (int i = 0; i < r.length; i++) {
/*     */       
/* 118 */       if (r[i] != null && r[i] instanceof FetchResponse && ((FetchResponse)r[i]).getNumber() == msgno) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 123 */         FetchResponse f = (FetchResponse)r[i];
/* 124 */         for (int j = 0; j < f.items.length; j++) {
/* 125 */           if (c.isInstance(f.items[j]))
/* 126 */             return f.items[j]; 
/*     */         } 
/*     */       } 
/*     */     } 
/* 130 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map getExtensionItems() {
/* 141 */     if (this.extensionItems == null)
/* 142 */       this.extensionItems = new HashMap(); 
/* 143 */     return this.extensionItems;
/*     */   }
/*     */   
/* 146 */   private static final char[] HEADER = new char[] { '.', 'H', 'E', 'A', 'D', 'E', 'R' };
/* 147 */   private static final char[] TEXT = new char[] { '.', 'T', 'E', 'X', 'T' };
/*     */   
/*     */   private void parse() throws ParsingException {
/* 150 */     skipSpaces();
/* 151 */     if (this.buffer[this.index] != 40) {
/* 152 */       throw new ParsingException("error in FETCH parsing, missing '(' at index " + this.index);
/*     */     }
/*     */     
/* 155 */     Vector v = new Vector();
/* 156 */     Item i = null;
/*     */     do {
/* 158 */       this.index++;
/*     */       
/* 160 */       if (this.index >= this.size) {
/* 161 */         throw new ParsingException("error in FETCH parsing, ran off end of buffer, size " + this.size);
/*     */       }
/*     */       
/* 164 */       i = parseItem();
/* 165 */       if (i != null) {
/* 166 */         v.addElement(i);
/* 167 */       } else if (!parseExtensionItem()) {
/* 168 */         throw new ParsingException("error in FETCH parsing, unrecognized item at index " + this.index);
/*     */       } 
/* 170 */     } while (this.buffer[this.index] != 41);
/*     */     
/* 172 */     this.index++;
/* 173 */     this.items = new Item[v.size()];
/* 174 */     v.copyInto((Object[])this.items);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Item parseItem() throws ParsingException {
/* 183 */     switch (this.buffer[this.index]) { case 69:
/*     */       case 101:
/* 185 */         if (match(ENVELOPE.name))
/* 186 */           return new ENVELOPE(this);  break;
/*     */       case 70:
/*     */       case 102:
/* 189 */         if (match(FLAGS.name))
/* 190 */           return new FLAGS(this);  break;
/*     */       case 73:
/*     */       case 105:
/* 193 */         if (match(INTERNALDATE.name))
/* 194 */           return new INTERNALDATE(this);  break;
/*     */       case 66:
/*     */       case 98:
/* 197 */         if (match(BODYSTRUCTURE.name))
/* 198 */           return new BODYSTRUCTURE(this); 
/* 199 */         if (match(BODY.name)) {
/* 200 */           if (this.buffer[this.index] == 91) {
/* 201 */             return new BODY(this);
/*     */           }
/* 203 */           return new BODYSTRUCTURE(this);
/*     */         }  break;
/*     */       case 82:
/*     */       case 114:
/* 207 */         if (match(RFC822SIZE.name))
/* 208 */           return new RFC822SIZE(this); 
/* 209 */         if (match(RFC822DATA.name)) {
/* 210 */           if (!match(HEADER))
/*     */           {
/* 212 */             if (match(TEXT));
/*     */           }
/* 214 */           return new RFC822DATA(this);
/*     */         }  break;
/*     */       case 85:
/*     */       case 117:
/* 218 */         if (match(UID.name)) {
/* 219 */           return new UID(this);
/*     */         }
/*     */         break; }
/*     */ 
/*     */     
/* 224 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean parseExtensionItem() throws ParsingException {
/* 231 */     if (this.fitems == null)
/* 232 */       return false; 
/* 233 */     for (int i = 0; i < this.fitems.length; i++) {
/* 234 */       if (match(this.fitems[i].getName())) {
/* 235 */         getExtensionItems().put(this.fitems[i].getName(), this.fitems[i].parseItem(this));
/*     */         
/* 237 */         return true;
/*     */       } 
/*     */     } 
/* 240 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean match(char[] itemName) {
/* 251 */     int len = itemName.length;
/* 252 */     for (int i = 0, j = this.index; i < len;) {
/*     */ 
/*     */       
/* 255 */       if (Character.toUpperCase((char)this.buffer[j++]) != itemName[i++])
/* 256 */         return false; 
/* 257 */     }  this.index += len;
/* 258 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean match(String itemName) {
/* 269 */     int len = itemName.length();
/* 270 */     for (int i = 0, j = this.index; i < len;) {
/*     */ 
/*     */       
/* 273 */       if (Character.toUpperCase((char)this.buffer[j++]) != itemName.charAt(i++))
/*     */       {
/* 275 */         return false; } 
/* 276 */     }  this.index += len;
/* 277 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\FetchResponse.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */