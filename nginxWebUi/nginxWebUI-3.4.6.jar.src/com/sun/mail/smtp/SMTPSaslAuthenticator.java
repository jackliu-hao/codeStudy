/*     */ package com.sun.mail.smtp;
/*     */ 
/*     */ import com.sun.mail.util.ASCIIUtility;
/*     */ import com.sun.mail.util.BASE64DecoderStream;
/*     */ import com.sun.mail.util.BASE64EncoderStream;
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Level;
/*     */ import javax.mail.MessagingException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SMTPSaslAuthenticator
/*     */   implements SaslAuthenticator
/*     */ {
/*     */   private SMTPTransport pr;
/*     */   private String name;
/*     */   private Properties props;
/*     */   private MailLogger logger;
/*     */   private String host;
/*     */   
/*     */   public SMTPSaslAuthenticator(SMTPTransport pr, String name, Properties props, MailLogger logger, String host) {
/*  68 */     this.pr = pr;
/*  69 */     this.name = name;
/*  70 */     this.props = props;
/*  71 */     this.logger = logger;
/*  72 */     this.host = host;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean authenticate(String[] mechs, final String realm, String authzid, final String u, final String p) throws MessagingException {
/*     */     SaslClient sc;
/*     */     int resp;
/*  79 */     boolean done = false;
/*  80 */     if (this.logger.isLoggable(Level.FINE)) {
/*  81 */       this.logger.fine("SASL Mechanisms:");
/*  82 */       for (int i = 0; i < mechs.length; i++)
/*  83 */         this.logger.fine(" " + mechs[i]); 
/*  84 */       this.logger.fine("");
/*     */     } 
/*     */ 
/*     */     
/*  88 */     CallbackHandler cbh = new CallbackHandler() { private final String val$u; private final String val$p;
/*     */         public void handle(Callback[] callbacks) {
/*  90 */           if (SMTPSaslAuthenticator.this.logger.isLoggable(Level.FINE))
/*  91 */             SMTPSaslAuthenticator.this.logger.fine("SASL callback length: " + callbacks.length); 
/*  92 */           for (int i = 0; i < callbacks.length; i++) {
/*  93 */             if (SMTPSaslAuthenticator.this.logger.isLoggable(Level.FINE))
/*  94 */               SMTPSaslAuthenticator.this.logger.fine("SASL callback " + i + ": " + callbacks[i]); 
/*  95 */             if (callbacks[i] instanceof NameCallback) {
/*  96 */               NameCallback ncb = (NameCallback)callbacks[i];
/*  97 */               ncb.setName(u);
/*  98 */             } else if (callbacks[i] instanceof PasswordCallback) {
/*  99 */               PasswordCallback pcb = (PasswordCallback)callbacks[i];
/* 100 */               pcb.setPassword(p.toCharArray());
/* 101 */             } else if (callbacks[i] instanceof RealmCallback) {
/* 102 */               RealmCallback rcb = (RealmCallback)callbacks[i];
/* 103 */               rcb.setText((realm != null) ? realm : rcb.getDefaultText());
/*     */             }
/* 105 */             else if (callbacks[i] instanceof RealmChoiceCallback) {
/* 106 */               RealmChoiceCallback rcb = (RealmChoiceCallback)callbacks[i];
/*     */               
/* 108 */               if (realm == null) {
/* 109 */                 rcb.setSelectedIndex(rcb.getDefaultChoice());
/*     */               } else {
/*     */                 
/* 112 */                 String[] choices = rcb.getChoices();
/* 113 */                 for (int k = 0; k < choices.length; k++) {
/* 114 */                   if (choices[k].equals(realm)) {
/* 115 */                     rcb.setSelectedIndex(k);
/*     */                     break;
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         }
/*     */         private final String val$realm; private final SMTPSaslAuthenticator this$0; }
/*     */       ;
/*     */     try {
/* 126 */       sc = Sasl.createSaslClient(mechs, authzid, this.name, this.host, this.props, cbh);
/*     */     }
/* 128 */     catch (SaslException sex) {
/* 129 */       this.logger.log(Level.FINE, "Failed to create SASL client: ", sex);
/* 130 */       return false;
/*     */     } 
/* 132 */     if (sc == null) {
/* 133 */       this.logger.fine("No SASL support");
/* 134 */       return false;
/*     */     } 
/* 136 */     if (this.logger.isLoggable(Level.FINE)) {
/* 137 */       this.logger.fine("SASL client " + sc.getMechanismName());
/*     */     }
/*     */     
/*     */     try {
/* 141 */       String mech = sc.getMechanismName();
/* 142 */       String ir = null;
/* 143 */       if (sc.hasInitialResponse()) {
/* 144 */         byte[] ba = sc.evaluateChallenge(new byte[0]);
/* 145 */         ba = BASE64EncoderStream.encode(ba);
/* 146 */         ir = ASCIIUtility.toString(ba, 0, ba.length);
/*     */       } 
/* 148 */       if (ir != null) {
/* 149 */         resp = this.pr.simpleCommand("AUTH " + mech + " " + ir);
/*     */       } else {
/* 151 */         resp = this.pr.simpleCommand("AUTH " + mech);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 157 */       if (resp == 530) {
/* 158 */         this.pr.startTLS();
/* 159 */         if (ir != null) {
/* 160 */           resp = this.pr.simpleCommand("AUTH " + mech + " " + ir);
/*     */         } else {
/* 162 */           resp = this.pr.simpleCommand("AUTH " + mech);
/*     */         } 
/*     */       } 
/* 165 */       if (resp == 235) {
/* 166 */         return true;
/*     */       }
/* 168 */       if (resp != 334)
/* 169 */         return false; 
/* 170 */     } catch (Exception ex) {
/* 171 */       this.logger.log(Level.FINE, "SASL AUTHENTICATE Exception", ex);
/* 172 */       return false;
/*     */     } 
/*     */     
/* 175 */     while (!done) {
/*     */       try {
/* 177 */         if (resp == 334) {
/* 178 */           byte[] ba = null;
/* 179 */           if (!sc.isComplete()) {
/* 180 */             ba = ASCIIUtility.getBytes(responseText(this.pr));
/* 181 */             if (ba.length > 0)
/* 182 */               ba = BASE64DecoderStream.decode(ba); 
/* 183 */             if (this.logger.isLoggable(Level.FINE)) {
/* 184 */               this.logger.fine("SASL challenge: " + ASCIIUtility.toString(ba, 0, ba.length) + " :");
/*     */             }
/* 186 */             ba = sc.evaluateChallenge(ba);
/*     */           } 
/* 188 */           if (ba == null) {
/* 189 */             this.logger.fine("SASL: no response");
/* 190 */             resp = this.pr.simpleCommand("*"); continue;
/*     */           } 
/* 192 */           if (this.logger.isLoggable(Level.FINE)) {
/* 193 */             this.logger.fine("SASL response: " + ASCIIUtility.toString(ba, 0, ba.length) + " :");
/*     */           }
/* 195 */           ba = BASE64EncoderStream.encode(ba);
/* 196 */           resp = this.pr.simpleCommand(ba);
/*     */           continue;
/*     */         } 
/* 199 */         done = true;
/* 200 */       } catch (Exception ioex) {
/* 201 */         this.logger.log(Level.FINE, "SASL Exception", ioex);
/* 202 */         done = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 207 */     if (sc.isComplete()) {
/* 208 */       String qop = (String)sc.getNegotiatedProperty("javax.security.sasl.qop");
/* 209 */       if (qop != null && (qop.equalsIgnoreCase("auth-int") || qop.equalsIgnoreCase("auth-conf"))) {
/*     */ 
/*     */         
/* 212 */         this.logger.fine("SASL Mechanism requires integrity or confidentiality");
/*     */         
/* 214 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 218 */     return true;
/*     */   }
/*     */   
/*     */   private static final String responseText(SMTPTransport pr) {
/* 222 */     String resp = pr.getLastServerResponse().trim();
/* 223 */     if (resp.length() > 4) {
/* 224 */       return resp.substring(4);
/*     */     }
/* 226 */     return "";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SMTPSaslAuthenticator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */