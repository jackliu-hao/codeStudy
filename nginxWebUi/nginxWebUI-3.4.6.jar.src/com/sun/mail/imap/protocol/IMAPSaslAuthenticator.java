/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.Argument;
/*     */ import com.sun.mail.iap.ProtocolException;
/*     */ import com.sun.mail.iap.Response;
/*     */ import com.sun.mail.util.ASCIIUtility;
/*     */ import com.sun.mail.util.BASE64DecoderStream;
/*     */ import com.sun.mail.util.BASE64EncoderStream;
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import com.sun.mail.util.PropUtil;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import java.util.logging.Level;
/*     */ import javax.security.auth.callback.Callback;
/*     */ import javax.security.auth.callback.CallbackHandler;
/*     */ import javax.security.auth.callback.NameCallback;
/*     */ import javax.security.auth.callback.PasswordCallback;
/*     */ import javax.security.sasl.RealmCallback;
/*     */ import javax.security.sasl.RealmChoiceCallback;
/*     */ import javax.security.sasl.Sasl;
/*     */ import javax.security.sasl.SaslClient;
/*     */ import javax.security.sasl.SaslException;
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
/*     */ public class IMAPSaslAuthenticator
/*     */   implements SaslAuthenticator
/*     */ {
/*     */   private IMAPProtocol pr;
/*     */   private String name;
/*     */   private Properties props;
/*     */   private MailLogger logger;
/*     */   private String host;
/*     */   
/*     */   public IMAPSaslAuthenticator(IMAPProtocol pr, String name, Properties props, MailLogger logger, String host) {
/*  69 */     this.pr = pr;
/*  70 */     this.name = name;
/*  71 */     this.props = props;
/*  72 */     this.logger = logger;
/*  73 */     this.host = host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean authenticate(String[] mechs, final String realm, String authzid, final String u, final String p) throws ProtocolException {
/*  80 */     synchronized (this.pr) {
/*  81 */       SaslClient sc; Vector v = new Vector();
/*  82 */       String tag = null;
/*  83 */       Response r = null;
/*  84 */       boolean done = false;
/*  85 */       if (this.logger.isLoggable(Level.FINE)) {
/*  86 */         this.logger.fine("SASL Mechanisms:");
/*  87 */         for (int i = 0; i < mechs.length; i++)
/*  88 */           this.logger.fine(" " + mechs[i]); 
/*  89 */         this.logger.fine("");
/*     */       } 
/*     */ 
/*     */       
/*  93 */       CallbackHandler cbh = new CallbackHandler() { private final String val$u; private final String val$p;
/*     */           public void handle(Callback[] callbacks) {
/*  95 */             if (IMAPSaslAuthenticator.this.logger.isLoggable(Level.FINE))
/*  96 */               IMAPSaslAuthenticator.this.logger.fine("SASL callback length: " + callbacks.length); 
/*  97 */             for (int i = 0; i < callbacks.length; i++) {
/*  98 */               if (IMAPSaslAuthenticator.this.logger.isLoggable(Level.FINE))
/*  99 */                 IMAPSaslAuthenticator.this.logger.fine("SASL callback " + i + ": " + callbacks[i]); 
/* 100 */               if (callbacks[i] instanceof NameCallback) {
/* 101 */                 NameCallback ncb = (NameCallback)callbacks[i];
/* 102 */                 ncb.setName(u);
/* 103 */               } else if (callbacks[i] instanceof PasswordCallback) {
/* 104 */                 PasswordCallback pcb = (PasswordCallback)callbacks[i];
/* 105 */                 pcb.setPassword(p.toCharArray());
/* 106 */               } else if (callbacks[i] instanceof RealmCallback) {
/* 107 */                 RealmCallback rcb = (RealmCallback)callbacks[i];
/* 108 */                 rcb.setText((realm != null) ? realm : rcb.getDefaultText());
/*     */               }
/* 110 */               else if (callbacks[i] instanceof RealmChoiceCallback) {
/* 111 */                 RealmChoiceCallback rcb = (RealmChoiceCallback)callbacks[i];
/*     */                 
/* 113 */                 if (realm == null) {
/* 114 */                   rcb.setSelectedIndex(rcb.getDefaultChoice());
/*     */                 } else {
/*     */                   
/* 117 */                   String[] choices = rcb.getChoices();
/* 118 */                   for (int k = 0; k < choices.length; k++) {
/* 119 */                     if (choices[k].equals(realm)) {
/* 120 */                       rcb.setSelectedIndex(k);
/*     */                       break;
/*     */                     } 
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */           }
/*     */           private final String val$realm; private final IMAPSaslAuthenticator this$0; }
/*     */         ;
/*     */       try {
/* 131 */         sc = Sasl.createSaslClient(mechs, authzid, this.name, this.host, this.props, cbh);
/*     */       }
/* 133 */       catch (SaslException sex) {
/* 134 */         this.logger.log(Level.FINE, "Failed to create SASL client", sex);
/* 135 */         return false;
/*     */       } 
/* 137 */       if (sc == null) {
/* 138 */         this.logger.fine("No SASL support");
/* 139 */         return false;
/*     */       } 
/* 141 */       if (this.logger.isLoggable(Level.FINE)) {
/* 142 */         this.logger.fine("SASL client " + sc.getMechanismName());
/*     */       }
/*     */       try {
/* 145 */         tag = this.pr.writeCommand("AUTHENTICATE " + sc.getMechanismName(), null);
/*     */       }
/* 147 */       catch (Exception ex) {
/* 148 */         this.logger.log(Level.FINE, "SASL AUTHENTICATE Exception", ex);
/* 149 */         return false;
/*     */       } 
/*     */       
/* 152 */       OutputStream os = this.pr.getIMAPOutputStream();
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
/* 167 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 168 */       byte[] CRLF = { 13, 10 };
/*     */ 
/*     */       
/* 171 */       boolean isXGWTRUSTEDAPP = (sc.getMechanismName().equals("XGWTRUSTEDAPP") && PropUtil.getBooleanProperty(this.props, "mail." + this.name + ".sasl.xgwtrustedapphack.enable", true));
/*     */ 
/*     */ 
/*     */       
/* 175 */       while (!done) {
/*     */         try {
/* 177 */           r = this.pr.readResponse();
/* 178 */           if (r.isContinuation()) {
/* 179 */             byte[] ba = null;
/* 180 */             if (!sc.isComplete()) {
/* 181 */               ba = r.readByteArray().getNewBytes();
/* 182 */               if (ba.length > 0)
/* 183 */                 ba = BASE64DecoderStream.decode(ba); 
/* 184 */               if (this.logger.isLoggable(Level.FINE)) {
/* 185 */                 this.logger.fine("SASL challenge: " + ASCIIUtility.toString(ba, 0, ba.length) + " :");
/*     */               }
/* 187 */               ba = sc.evaluateChallenge(ba);
/*     */             } 
/* 189 */             if (ba == null) {
/* 190 */               this.logger.fine("SASL no response");
/* 191 */               os.write(CRLF);
/* 192 */               os.flush();
/* 193 */               bos.reset(); continue;
/*     */             } 
/* 195 */             if (this.logger.isLoggable(Level.FINE)) {
/* 196 */               this.logger.fine("SASL response: " + ASCIIUtility.toString(ba, 0, ba.length) + " :");
/*     */             }
/* 198 */             ba = BASE64EncoderStream.encode(ba);
/* 199 */             if (isXGWTRUSTEDAPP)
/* 200 */               bos.write(ASCIIUtility.getBytes("XGWTRUSTEDAPP ")); 
/* 201 */             bos.write(ba);
/*     */             
/* 203 */             bos.write(CRLF);
/* 204 */             os.write(bos.toByteArray());
/* 205 */             os.flush();
/* 206 */             bos.reset(); continue;
/*     */           } 
/* 208 */           if (r.isTagged() && r.getTag().equals(tag)) {
/*     */             
/* 210 */             done = true; continue;
/* 211 */           }  if (r.isBYE()) {
/* 212 */             done = true; continue;
/*     */           } 
/* 214 */           v.addElement(r);
/* 215 */         } catch (Exception ioex) {
/* 216 */           this.logger.log(Level.FINE, "SASL Exception", ioex);
/*     */           
/* 218 */           r = Response.byeResponse(ioex);
/* 219 */           done = true;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 224 */       if (sc.isComplete()) {
/* 225 */         String qop = (String)sc.getNegotiatedProperty("javax.security.sasl.qop");
/* 226 */         if (qop != null && (qop.equalsIgnoreCase("auth-int") || qop.equalsIgnoreCase("auth-conf"))) {
/*     */ 
/*     */           
/* 229 */           this.logger.fine("SASL Mechanism requires integrity or confidentiality");
/*     */           
/* 231 */           return false;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 241 */       Response[] responses = new Response[v.size()];
/* 242 */       v.copyInto((Object[])responses);
/* 243 */       this.pr.notifyResponseHandlers(responses);
/*     */ 
/*     */       
/* 246 */       this.pr.handleResult(r);
/* 247 */       this.pr.setCapabilities(r);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 254 */       if (isXGWTRUSTEDAPP) {
/* 255 */         Argument args = new Argument();
/* 256 */         args.writeString((authzid != null) ? authzid : u);
/*     */         
/* 258 */         responses = this.pr.command("LOGIN", args);
/*     */ 
/*     */         
/* 261 */         this.pr.notifyResponseHandlers(responses);
/*     */ 
/*     */         
/* 264 */         this.pr.handleResult(responses[responses.length - 1]);
/*     */         
/* 266 */         this.pr.setCapabilities(responses[responses.length - 1]);
/*     */       } 
/* 268 */       return true;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\IMAPSaslAuthenticator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */