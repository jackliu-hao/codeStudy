/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.core._DelayedOrdinal;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class EmptyMemberAndArguments
/*    */   extends MaybeEmptyMemberAndArguments
/*    */ {
/* 30 */   static final EmptyMemberAndArguments WRONG_NUMBER_OF_ARGUMENTS = new EmptyMemberAndArguments("No compatible overloaded variation was found; wrong number of arguments.", true, null);
/*    */   
/*    */   private final Object errorDescription;
/*    */   
/*    */   private final boolean numberOfArgumentsWrong;
/*    */   
/*    */   private final Object[] unwrappedArguments;
/*    */ 
/*    */   
/*    */   private EmptyMemberAndArguments(Object errorDescription, boolean numberOfArgumentsWrong, Object[] unwrappedArguments) {
/* 40 */     this.errorDescription = errorDescription;
/* 41 */     this.numberOfArgumentsWrong = numberOfArgumentsWrong;
/* 42 */     this.unwrappedArguments = unwrappedArguments;
/*    */   }
/*    */   
/*    */   static EmptyMemberAndArguments noCompatibleOverload(int unwrappableIndex) {
/* 46 */     return new EmptyMemberAndArguments(new Object[] { "No compatible overloaded variation was found; can't convert (unwrap) the ", new _DelayedOrdinal(
/*    */             
/* 48 */             Integer.valueOf(unwrappableIndex)), " argument to the desired Java type." }, false, null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   static EmptyMemberAndArguments noCompatibleOverload(Object[] unwrappedArgs) {
/* 54 */     return new EmptyMemberAndArguments("No compatible overloaded variation was found; declared parameter types and argument value types mismatch.", false, unwrappedArgs);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static EmptyMemberAndArguments ambiguous(Object[] unwrappedArgs) {
/* 61 */     return new EmptyMemberAndArguments("Multiple compatible overloaded variations were found with the same priority.", false, unwrappedArgs);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static MaybeEmptyMemberAndArguments from(EmptyCallableMemberDescriptor emtpyMemberDesc, Object[] unwrappedArgs) {
/* 69 */     if (emtpyMemberDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD)
/* 70 */       return noCompatibleOverload(unwrappedArgs); 
/* 71 */     if (emtpyMemberDesc == EmptyCallableMemberDescriptor.AMBIGUOUS_METHOD) {
/* 72 */       return ambiguous(unwrappedArgs);
/*    */     }
/* 74 */     throw new IllegalArgumentException("Unrecognized constant: " + emtpyMemberDesc);
/*    */   }
/*    */ 
/*    */   
/*    */   Object getErrorDescription() {
/* 79 */     return this.errorDescription;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Object[] getUnwrappedArguments() {
/* 86 */     return this.unwrappedArguments;
/*    */   }
/*    */   
/*    */   public boolean isNumberOfArgumentsWrong() {
/* 90 */     return this.numberOfArgumentsWrong;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\EmptyMemberAndArguments.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */