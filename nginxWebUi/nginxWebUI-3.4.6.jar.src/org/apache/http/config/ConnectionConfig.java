/*     */ package org.apache.http.config;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class ConnectionConfig
/*     */   implements Cloneable
/*     */ {
/*  46 */   public static final ConnectionConfig DEFAULT = (new Builder()).build();
/*     */ 
/*     */   
/*     */   private final int bufferSize;
/*     */   
/*     */   private final int fragmentSizeHint;
/*     */   
/*     */   private final Charset charset;
/*     */   
/*     */   private final CodingErrorAction malformedInputAction;
/*     */   
/*     */   private final CodingErrorAction unmappableInputAction;
/*     */   
/*     */   private final MessageConstraints messageConstraints;
/*     */ 
/*     */   
/*     */   ConnectionConfig(int bufferSize, int fragmentSizeHint, Charset charset, CodingErrorAction malformedInputAction, CodingErrorAction unmappableInputAction, MessageConstraints messageConstraints) {
/*  63 */     this.bufferSize = bufferSize;
/*  64 */     this.fragmentSizeHint = fragmentSizeHint;
/*  65 */     this.charset = charset;
/*  66 */     this.malformedInputAction = malformedInputAction;
/*  67 */     this.unmappableInputAction = unmappableInputAction;
/*  68 */     this.messageConstraints = messageConstraints;
/*     */   }
/*     */   
/*     */   public int getBufferSize() {
/*  72 */     return this.bufferSize;
/*     */   }
/*     */   
/*     */   public int getFragmentSizeHint() {
/*  76 */     return this.fragmentSizeHint;
/*     */   }
/*     */   
/*     */   public Charset getCharset() {
/*  80 */     return this.charset;
/*     */   }
/*     */   
/*     */   public CodingErrorAction getMalformedInputAction() {
/*  84 */     return this.malformedInputAction;
/*     */   }
/*     */   
/*     */   public CodingErrorAction getUnmappableInputAction() {
/*  88 */     return this.unmappableInputAction;
/*     */   }
/*     */   
/*     */   public MessageConstraints getMessageConstraints() {
/*  92 */     return this.messageConstraints;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ConnectionConfig clone() throws CloneNotSupportedException {
/*  97 */     return (ConnectionConfig)super.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 102 */     StringBuilder builder = new StringBuilder();
/* 103 */     builder.append("[bufferSize=").append(this.bufferSize).append(", fragmentSizeHint=").append(this.fragmentSizeHint).append(", charset=").append(this.charset).append(", malformedInputAction=").append(this.malformedInputAction).append(", unmappableInputAction=").append(this.unmappableInputAction).append(", messageConstraints=").append(this.messageConstraints).append("]");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     return builder.toString();
/*     */   }
/*     */   
/*     */   public static Builder custom() {
/* 114 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static Builder copy(ConnectionConfig config) {
/* 118 */     Args.notNull(config, "Connection config");
/* 119 */     return (new Builder()).setBufferSize(config.getBufferSize()).setCharset(config.getCharset()).setFragmentSizeHint(config.getFragmentSizeHint()).setMalformedInputAction(config.getMalformedInputAction()).setUnmappableInputAction(config.getUnmappableInputAction()).setMessageConstraints(config.getMessageConstraints());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     private int bufferSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 138 */     private int fragmentSizeHint = -1; private Charset charset;
/*     */     private CodingErrorAction malformedInputAction;
/*     */     
/*     */     public Builder setBufferSize(int bufferSize) {
/* 142 */       this.bufferSize = bufferSize;
/* 143 */       return this;
/*     */     }
/*     */     private CodingErrorAction unmappableInputAction; private MessageConstraints messageConstraints;
/*     */     public Builder setFragmentSizeHint(int fragmentSizeHint) {
/* 147 */       this.fragmentSizeHint = fragmentSizeHint;
/* 148 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCharset(Charset charset) {
/* 152 */       this.charset = charset;
/* 153 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setMalformedInputAction(CodingErrorAction malformedInputAction) {
/* 157 */       this.malformedInputAction = malformedInputAction;
/* 158 */       if (malformedInputAction != null && this.charset == null) {
/* 159 */         this.charset = Consts.ASCII;
/*     */       }
/* 161 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setUnmappableInputAction(CodingErrorAction unmappableInputAction) {
/* 165 */       this.unmappableInputAction = unmappableInputAction;
/* 166 */       if (unmappableInputAction != null && this.charset == null) {
/* 167 */         this.charset = Consts.ASCII;
/*     */       }
/* 169 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setMessageConstraints(MessageConstraints messageConstraints) {
/* 173 */       this.messageConstraints = messageConstraints;
/* 174 */       return this;
/*     */     }
/*     */     
/*     */     public ConnectionConfig build() {
/* 178 */       Charset cs = this.charset;
/* 179 */       if (cs == null && (this.malformedInputAction != null || this.unmappableInputAction != null)) {
/* 180 */         cs = Consts.ASCII;
/*     */       }
/* 182 */       int bufSize = (this.bufferSize > 0) ? this.bufferSize : 8192;
/* 183 */       int fragmentHintSize = (this.fragmentSizeHint >= 0) ? this.fragmentSizeHint : bufSize;
/* 184 */       return new ConnectionConfig(bufSize, fragmentHintSize, cs, this.malformedInputAction, this.unmappableInputAction, this.messageConstraints);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\config\ConnectionConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */