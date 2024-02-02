/*     */ package org.apache.http.impl.auth;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.auth.ChallengeState;
/*     */ import org.apache.http.auth.MalformedChallengeException;
/*     */ import org.apache.http.message.BasicHeaderValueParser;
/*     */ import org.apache.http.message.ParserCursor;
/*     */ import org.apache.http.util.CharArrayBuffer;
/*     */ import org.apache.http.util.CharsetUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class RFC2617Scheme
/*     */   extends AuthSchemeBase
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2845454858205884623L;
/*     */   private final Map<String, String> params;
/*     */   private transient Charset credentialsCharset;
/*     */   
/*     */   @Deprecated
/*     */   public RFC2617Scheme(ChallengeState challengeState) {
/*  76 */     super(challengeState);
/*  77 */     this.params = new HashMap<String, String>();
/*  78 */     this.credentialsCharset = Consts.ASCII;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RFC2617Scheme(Charset credentialsCharset) {
/*  86 */     this.params = new HashMap<String, String>();
/*  87 */     this.credentialsCharset = (credentialsCharset != null) ? credentialsCharset : Consts.ASCII;
/*     */   }
/*     */   
/*     */   public RFC2617Scheme() {
/*  91 */     this(Consts.ASCII);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCredentialsCharset() {
/*  99 */     return (this.credentialsCharset != null) ? this.credentialsCharset : Consts.ASCII;
/*     */   }
/*     */   
/*     */   String getCredentialsCharset(HttpRequest request) {
/* 103 */     String charset = (String)request.getParams().getParameter("http.auth.credential-charset");
/* 104 */     if (charset == null) {
/* 105 */       charset = getCredentialsCharset().name();
/*     */     }
/* 107 */     return charset;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void parseChallenge(CharArrayBuffer buffer, int pos, int len) throws MalformedChallengeException {
/* 113 */     BasicHeaderValueParser basicHeaderValueParser = BasicHeaderValueParser.INSTANCE;
/* 114 */     ParserCursor cursor = new ParserCursor(pos, buffer.length());
/* 115 */     HeaderElement[] elements = basicHeaderValueParser.parseElements(buffer, cursor);
/* 116 */     this.params.clear();
/* 117 */     for (HeaderElement element : elements) {
/* 118 */       this.params.put(element.getName().toLowerCase(Locale.ROOT), element.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, String> getParameters() {
/* 128 */     return this.params;
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
/*     */   public String getParameter(String name) {
/* 140 */     if (name == null) {
/* 141 */       return null;
/*     */     }
/* 143 */     return this.params.get(name.toLowerCase(Locale.ROOT));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRealm() {
/* 153 */     return getParameter("realm");
/*     */   }
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/* 157 */     out.defaultWriteObject();
/* 158 */     out.writeUTF(this.credentialsCharset.name());
/* 159 */     out.writeObject(this.challengeState);
/*     */   }
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 164 */     in.defaultReadObject();
/* 165 */     this.credentialsCharset = CharsetUtils.get(in.readUTF());
/* 166 */     if (this.credentialsCharset == null) {
/* 167 */       this.credentialsCharset = Consts.ASCII;
/*     */     }
/* 169 */     this.challengeState = (ChallengeState)in.readObject();
/*     */   }
/*     */   
/*     */   private void readObjectNoData() throws ObjectStreamException {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\RFC2617Scheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */