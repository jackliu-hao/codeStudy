/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
/*     */ import com.sun.mail.iap.Response;
/*     */ import java.util.Vector;
/*     */ import javax.mail.internet.AddressException;
/*     */ import javax.mail.internet.InternetAddress;
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
/*     */ class IMAPAddress
/*     */   extends InternetAddress
/*     */ {
/*     */   private boolean group = false;
/*     */   private InternetAddress[] grouplist;
/*     */   private String groupname;
/*     */   private static final long serialVersionUID = -3835822029483122232L;
/*     */   
/*     */   IMAPAddress(Response r) throws ParsingException {
/* 151 */     r.skipSpaces();
/*     */     
/* 153 */     if (r.readByte() != 40) {
/* 154 */       throw new ParsingException("ADDRESS parse error");
/*     */     }
/* 156 */     this.encodedPersonal = r.readString();
/*     */     
/* 158 */     r.readString();
/* 159 */     String mb = r.readString();
/* 160 */     String host = r.readString();
/*     */ 
/*     */     
/* 163 */     r.skipSpaces();
/* 164 */     if (r.readByte() != 41) {
/* 165 */       throw new ParsingException("ADDRESS parse error");
/*     */     }
/* 167 */     if (host == null) {
/*     */       
/* 169 */       this.group = true;
/* 170 */       this.groupname = mb;
/* 171 */       if (this.groupname == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 176 */       StringBuffer sb = new StringBuffer();
/* 177 */       sb.append(this.groupname).append(':');
/* 178 */       Vector v = new Vector();
/* 179 */       while (r.peekByte() != 41) {
/* 180 */         IMAPAddress a = new IMAPAddress(r);
/* 181 */         if (a.isEndOfGroup())
/*     */           break; 
/* 183 */         if (v.size() != 0)
/* 184 */           sb.append(','); 
/* 185 */         sb.append(a.toString());
/* 186 */         v.addElement(a);
/*     */       } 
/* 188 */       sb.append(';');
/* 189 */       this.address = sb.toString();
/* 190 */       this.grouplist = (InternetAddress[])new IMAPAddress[v.size()];
/* 191 */       v.copyInto((Object[])this.grouplist);
/*     */     }
/* 193 */     else if (mb == null || mb.length() == 0) {
/* 194 */       this.address = host;
/* 195 */     } else if (host.length() == 0) {
/* 196 */       this.address = mb;
/*     */     } else {
/* 198 */       this.address = mb + "@" + host;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isEndOfGroup() {
/* 204 */     return (this.group && this.groupname == null);
/*     */   }
/*     */   
/*     */   public boolean isGroup() {
/* 208 */     return this.group;
/*     */   }
/*     */   
/*     */   public InternetAddress[] getGroup(boolean strict) throws AddressException {
/* 212 */     if (this.grouplist == null)
/* 213 */       return null; 
/* 214 */     return (InternetAddress[])this.grouplist.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\IMAPAddress.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */