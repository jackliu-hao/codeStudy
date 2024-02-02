/*     */ package org.apache.http.impl.auth;
/*     */ 
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.InvalidCredentialsException;
/*     */ import org.apache.http.auth.MalformedChallengeException;
/*     */ import org.apache.http.auth.NTCredentials;
/*     */ import org.apache.http.message.BufferedHeader;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NTLMScheme
/*     */   extends AuthSchemeBase
/*     */ {
/*     */   private final NTLMEngine engine;
/*     */   private State state;
/*     */   private String challenge;
/*     */   
/*     */   enum State
/*     */   {
/*  50 */     UNINITIATED,
/*  51 */     CHALLENGE_RECEIVED,
/*  52 */     MSG_TYPE1_GENERATED,
/*  53 */     MSG_TYPE2_RECEVIED,
/*  54 */     MSG_TYPE3_GENERATED,
/*  55 */     FAILED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NTLMScheme(NTLMEngine engine) {
/*  65 */     Args.notNull(engine, "NTLM engine");
/*  66 */     this.engine = engine;
/*  67 */     this.state = State.UNINITIATED;
/*  68 */     this.challenge = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NTLMScheme() {
/*  75 */     this(new NTLMEngineImpl());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemeName() {
/*  80 */     return "ntlm";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParameter(String name) {
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRealm() {
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConnectionBased() {
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void parseChallenge(CharArrayBuffer buffer, int beginIndex, int endIndex) throws MalformedChallengeException {
/* 104 */     this.challenge = buffer.substringTrimmed(beginIndex, endIndex);
/* 105 */     if (this.challenge.isEmpty()) {
/* 106 */       if (this.state == State.UNINITIATED) {
/* 107 */         this.state = State.CHALLENGE_RECEIVED;
/*     */       } else {
/* 109 */         this.state = State.FAILED;
/*     */       } 
/*     */     } else {
/* 112 */       if (this.state.compareTo(State.MSG_TYPE1_GENERATED) < 0) {
/* 113 */         this.state = State.FAILED;
/* 114 */         throw new MalformedChallengeException("Out of sequence NTLM response message");
/* 115 */       }  if (this.state == State.MSG_TYPE1_GENERATED) {
/* 116 */         this.state = State.MSG_TYPE2_RECEVIED;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
/* 125 */     NTCredentials ntcredentials = null;
/*     */     try {
/* 127 */       ntcredentials = (NTCredentials)credentials;
/* 128 */     } catch (ClassCastException e) {
/* 129 */       throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: " + credentials.getClass().getName());
/*     */     } 
/*     */ 
/*     */     
/* 133 */     String response = null;
/* 134 */     if (this.state == State.FAILED)
/* 135 */       throw new AuthenticationException("NTLM authentication failed"); 
/* 136 */     if (this.state == State.CHALLENGE_RECEIVED) {
/* 137 */       response = this.engine.generateType1Msg(ntcredentials.getDomain(), ntcredentials.getWorkstation());
/*     */ 
/*     */       
/* 140 */       this.state = State.MSG_TYPE1_GENERATED;
/* 141 */     } else if (this.state == State.MSG_TYPE2_RECEVIED) {
/* 142 */       response = this.engine.generateType3Msg(ntcredentials.getUserName(), ntcredentials.getPassword(), ntcredentials.getDomain(), ntcredentials.getWorkstation(), this.challenge);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 148 */       this.state = State.MSG_TYPE3_GENERATED;
/*     */     } else {
/* 150 */       throw new AuthenticationException("Unexpected state: " + this.state);
/*     */     } 
/* 152 */     CharArrayBuffer buffer = new CharArrayBuffer(32);
/* 153 */     if (isProxy()) {
/* 154 */       buffer.append("Proxy-Authorization");
/*     */     } else {
/* 156 */       buffer.append("Authorization");
/*     */     } 
/* 158 */     buffer.append(": NTLM ");
/* 159 */     buffer.append(response);
/* 160 */     return (Header)new BufferedHeader(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isComplete() {
/* 165 */     return (this.state == State.MSG_TYPE3_GENERATED || this.state == State.FAILED);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\NTLMScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */