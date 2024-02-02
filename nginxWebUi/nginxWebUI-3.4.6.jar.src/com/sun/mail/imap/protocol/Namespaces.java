/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ProtocolException;
/*     */ import com.sun.mail.iap.Response;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Namespaces
/*     */ {
/*     */   public Namespace[] personal;
/*     */   public Namespace[] otherUsers;
/*     */   public Namespace[] shared;
/*     */   
/*     */   public static class Namespace
/*     */   {
/*     */     public String prefix;
/*     */     public char delimiter;
/*     */     
/*     */     public Namespace(Response r) throws ProtocolException {
/*  77 */       if (r.readByte() != 40) {
/*  78 */         throw new ProtocolException("Missing '(' at start of Namespace");
/*     */       }
/*     */       
/*  81 */       this.prefix = BASE64MailboxDecoder.decode(r.readString());
/*  82 */       r.skipSpaces();
/*     */       
/*  84 */       if (r.peekByte() == 34) {
/*  85 */         r.readByte();
/*  86 */         this.delimiter = (char)r.readByte();
/*  87 */         if (this.delimiter == '\\')
/*  88 */           this.delimiter = (char)r.readByte(); 
/*  89 */         if (r.readByte() != 34) {
/*  90 */           throw new ProtocolException("Missing '\"' at end of QUOTED_CHAR");
/*     */         }
/*     */       } else {
/*  93 */         String s = r.readAtom();
/*  94 */         if (s == null)
/*  95 */           throw new ProtocolException("Expected NIL, got null"); 
/*  96 */         if (!s.equalsIgnoreCase("NIL"))
/*  97 */           throw new ProtocolException("Expected NIL, got " + s); 
/*  98 */         this.delimiter = Character.MIN_VALUE;
/*     */       } 
/*     */       
/* 101 */       if (r.peekByte() != 41) {
/*     */ 
/*     */ 
/*     */         
/* 105 */         r.skipSpaces();
/* 106 */         r.readString();
/* 107 */         r.skipSpaces();
/* 108 */         r.readStringList();
/*     */       } 
/* 110 */       if (r.readByte() != 41) {
/* 111 */         throw new ProtocolException("Missing ')' at end of Namespace");
/*     */       }
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Namespaces(Response r) throws ProtocolException {
/* 137 */     this.personal = getNamespaces(r);
/* 138 */     this.otherUsers = getNamespaces(r);
/* 139 */     this.shared = getNamespaces(r);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Namespace[] getNamespaces(Response r) throws ProtocolException {
/* 146 */     r.skipSpaces();
/*     */     
/* 148 */     if (r.peekByte() == 40) {
/* 149 */       Vector v = new Vector();
/* 150 */       r.readByte();
/*     */       while (true) {
/* 152 */         Namespace ns = new Namespace(r);
/* 153 */         v.addElement(ns);
/* 154 */         if (r.peekByte() == 41)
/* 155 */         { r.readByte();
/* 156 */           Namespace[] nsa = new Namespace[v.size()];
/* 157 */           v.copyInto((Object[])nsa);
/* 158 */           return nsa; } 
/*     */       } 
/* 160 */     }  String s = r.readAtom();
/* 161 */     if (s == null)
/* 162 */       throw new ProtocolException("Expected NIL, got null"); 
/* 163 */     if (!s.equalsIgnoreCase("NIL"))
/* 164 */       throw new ProtocolException("Expected NIL, got " + s); 
/* 165 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\Namespaces.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */