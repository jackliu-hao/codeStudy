/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import org.apache.http.params.AbstractHttpParams;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class ClientParamsStack
/*     */   extends AbstractHttpParams
/*     */ {
/*     */   protected final HttpParams applicationParams;
/*     */   protected final HttpParams clientParams;
/*     */   protected final HttpParams requestParams;
/*     */   protected final HttpParams overrideParams;
/*     */   
/*     */   public ClientParamsStack(HttpParams aparams, HttpParams cparams, HttpParams rparams, HttpParams oparams) {
/*  99 */     this.applicationParams = aparams;
/* 100 */     this.clientParams = cparams;
/* 101 */     this.requestParams = rparams;
/* 102 */     this.overrideParams = oparams;
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
/*     */   public ClientParamsStack(ClientParamsStack stack) {
/* 114 */     this(stack.getApplicationParams(), stack.getClientParams(), stack.getRequestParams(), stack.getOverrideParams());
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
/*     */   public ClientParamsStack(ClientParamsStack stack, HttpParams aparams, HttpParams cparams, HttpParams rparams, HttpParams oparams) {
/* 137 */     this((aparams != null) ? aparams : stack.getApplicationParams(), (cparams != null) ? cparams : stack.getClientParams(), (rparams != null) ? rparams : stack.getRequestParams(), (oparams != null) ? oparams : stack.getOverrideParams());
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
/*     */   public final HttpParams getApplicationParams() {
/* 150 */     return this.applicationParams;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final HttpParams getClientParams() {
/* 159 */     return this.clientParams;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final HttpParams getRequestParams() {
/* 168 */     return this.requestParams;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final HttpParams getOverrideParams() {
/* 177 */     return this.overrideParams;
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
/*     */   public Object getParameter(String name) {
/* 192 */     Args.notNull(name, "Parameter name");
/*     */     
/* 194 */     Object result = null;
/*     */     
/* 196 */     if (this.overrideParams != null) {
/* 197 */       result = this.overrideParams.getParameter(name);
/*     */     }
/* 199 */     if (result == null && this.requestParams != null) {
/* 200 */       result = this.requestParams.getParameter(name);
/*     */     }
/* 202 */     if (result == null && this.clientParams != null) {
/* 203 */       result = this.clientParams.getParameter(name);
/*     */     }
/* 205 */     if (result == null && this.applicationParams != null) {
/* 206 */       result = this.applicationParams.getParameter(name);
/*     */     }
/* 208 */     return result;
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
/*     */   public HttpParams setParameter(String name, Object value) throws UnsupportedOperationException {
/* 228 */     throw new UnsupportedOperationException("Setting parameters in a stack is not supported.");
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
/*     */   public boolean removeParameter(String name) {
/* 247 */     throw new UnsupportedOperationException("Removing parameters in a stack is not supported.");
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
/*     */   public HttpParams copy() {
/* 268 */     return (HttpParams)this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\ClientParamsStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */