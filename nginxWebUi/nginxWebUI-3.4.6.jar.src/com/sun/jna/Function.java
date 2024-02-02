/*     */ package com.sun.jna;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Function
/*     */   extends Pointer
/*     */ {
/*     */   public static final int MAX_NARGS = 256;
/*     */   public static final int C_CONVENTION = 0;
/*     */   public static final int ALT_CONVENTION = 63;
/*     */   private static final int MASK_CC = 63;
/*     */   public static final int THROW_LAST_ERROR = 64;
/*     */   public static final int USE_VARARGS = 384;
/*  83 */   static final Integer INTEGER_TRUE = Integer.valueOf(-1);
/*  84 */   static final Integer INTEGER_FALSE = Integer.valueOf(0);
/*     */ 
/*     */   
/*     */   private NativeLibrary library;
/*     */ 
/*     */   
/*     */   private final String functionName;
/*     */   
/*     */   final String encoding;
/*     */   
/*     */   final int callFlags;
/*     */   
/*     */   final Map<String, ?> options;
/*     */   
/*     */   static final String OPTION_INVOKING_METHOD = "invoking-method";
/*     */ 
/*     */   
/*     */   public static Function getFunction(String libraryName, String functionName) {
/* 102 */     return NativeLibrary.getInstance(libraryName).getFunction(functionName);
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
/*     */   public static Function getFunction(String libraryName, String functionName, int callFlags) {
/* 123 */     return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags, null);
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
/*     */   public static Function getFunction(String libraryName, String functionName, int callFlags, String encoding) {
/* 147 */     return NativeLibrary.getInstance(libraryName).getFunction(functionName, callFlags, encoding);
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
/*     */   public static Function getFunction(Pointer p) {
/* 162 */     return getFunction(p, 0, (String)null);
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
/*     */   public static Function getFunction(Pointer p, int callFlags) {
/* 180 */     return getFunction(p, callFlags, (String)null);
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
/*     */   public static Function getFunction(Pointer p, int callFlags, String encoding) {
/* 201 */     return new Function(p, callFlags, encoding);
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
/* 216 */   private static final VarArgsChecker IS_VARARGS = VarArgsChecker.create();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Function(NativeLibrary library, String functionName, int callFlags, String encoding) {
/* 238 */     checkCallingConvention(callFlags & 0x3F);
/* 239 */     if (functionName == null) {
/* 240 */       throw new NullPointerException("Function name must not be null");
/*     */     }
/* 242 */     this.library = library;
/* 243 */     this.functionName = functionName;
/* 244 */     this.callFlags = callFlags;
/* 245 */     this.options = library.options;
/* 246 */     this.encoding = (encoding != null) ? encoding : Native.getDefaultStringEncoding();
/*     */     try {
/* 248 */       this.peer = library.getSymbolAddress(functionName);
/* 249 */     } catch (UnsatisfiedLinkError e) {
/* 250 */       throw new UnsatisfiedLinkError("Error looking up function '" + functionName + "': " + e
/*     */           
/* 252 */           .getMessage());
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
/*     */   Function(Pointer functionAddress, int callFlags, String encoding) {
/* 272 */     checkCallingConvention(callFlags & 0x3F);
/* 273 */     if (functionAddress == null || functionAddress.peer == 0L)
/*     */     {
/* 275 */       throw new NullPointerException("Function address may not be null");
/*     */     }
/* 277 */     this.functionName = functionAddress.toString();
/* 278 */     this.callFlags = callFlags;
/* 279 */     this.peer = functionAddress.peer;
/* 280 */     this.options = Collections.EMPTY_MAP;
/* 281 */     this
/* 282 */       .encoding = (encoding != null) ? encoding : Native.getDefaultStringEncoding();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkCallingConvention(int convention) throws IllegalArgumentException {
/* 288 */     if ((convention & 0x3F) != convention) {
/* 289 */       throw new IllegalArgumentException("Unrecognized calling convention: " + convention);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 295 */     return this.functionName;
/*     */   }
/*     */   
/*     */   public int getCallingConvention() {
/* 299 */     return this.callFlags & 0x3F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invoke(Class<?> returnType, Object[] inArgs) {
/* 306 */     return invoke(returnType, inArgs, this.options);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invoke(Class<?> returnType, Object[] inArgs, Map<String, ?> options) {
/* 313 */     Method invokingMethod = (Method)options.get("invoking-method");
/* 314 */     Class<?>[] paramTypes = (invokingMethod != null) ? invokingMethod.getParameterTypes() : null;
/* 315 */     return invoke(invokingMethod, paramTypes, returnType, inArgs, options);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object invoke(Method invokingMethod, Class<?>[] paramTypes, Class<?> returnType, Object[] inArgs, Map<String, ?> options) {
/* 326 */     Object[] args = new Object[0];
/* 327 */     if (inArgs != null) {
/* 328 */       if (inArgs.length > 256) {
/* 329 */         throw new UnsupportedOperationException("Maximum argument count is 256");
/*     */       }
/* 331 */       args = new Object[inArgs.length];
/* 332 */       System.arraycopy(inArgs, 0, args, 0, args.length);
/*     */     } 
/*     */     
/* 335 */     TypeMapper mapper = (TypeMapper)options.get("type-mapper");
/* 336 */     boolean allowObjects = Boolean.TRUE.equals(options.get("allow-objects"));
/* 337 */     boolean isVarArgs = (args.length > 0 && invokingMethod != null) ? isVarArgs(invokingMethod) : false;
/* 338 */     int fixedArgs = (args.length > 0 && invokingMethod != null) ? fixedArgs(invokingMethod) : 0;
/* 339 */     for (int i = 0; i < args.length; i++) {
/*     */ 
/*     */       
/* 342 */       Class<?> paramType = (invokingMethod != null) ? ((isVarArgs && i >= paramTypes.length - 1) ? paramTypes[paramTypes.length - 1].getComponentType() : paramTypes[i]) : null;
/*     */ 
/*     */       
/* 345 */       args[i] = convertArgument(args, i, invokingMethod, mapper, allowObjects, paramType);
/*     */     } 
/*     */     
/* 348 */     Class<?> nativeReturnType = returnType;
/* 349 */     FromNativeConverter resultConverter = null;
/* 350 */     if (NativeMapped.class.isAssignableFrom(returnType)) {
/* 351 */       NativeMappedConverter tc = NativeMappedConverter.getInstance(returnType);
/* 352 */       resultConverter = tc;
/* 353 */       nativeReturnType = tc.nativeType();
/* 354 */     } else if (mapper != null) {
/* 355 */       resultConverter = mapper.getFromNativeConverter(returnType);
/* 356 */       if (resultConverter != null) {
/* 357 */         nativeReturnType = resultConverter.nativeType();
/*     */       }
/*     */     } 
/*     */     
/* 361 */     Object result = invoke(args, nativeReturnType, allowObjects, fixedArgs);
/*     */     
/* 363 */     if (resultConverter != null) {
/*     */       FromNativeContext context;
/* 365 */       if (invokingMethod != null) {
/* 366 */         context = new MethodResultContext(returnType, this, inArgs, invokingMethod);
/*     */       } else {
/* 368 */         context = new FunctionResultContext(returnType, this, inArgs);
/*     */       } 
/* 370 */       result = resultConverter.fromNative(result, context);
/*     */     } 
/*     */ 
/*     */     
/* 374 */     if (inArgs != null) {
/* 375 */       for (int j = 0; j < inArgs.length; j++) {
/* 376 */         Object inArg = inArgs[j];
/* 377 */         if (inArg != null)
/*     */         {
/* 379 */           if (inArg instanceof Structure) {
/* 380 */             if (!(inArg instanceof Structure.ByValue)) {
/* 381 */               ((Structure)inArg).autoRead();
/*     */             }
/* 383 */           } else if (args[j] instanceof PostCallRead) {
/* 384 */             ((PostCallRead)args[j]).read();
/* 385 */             if (args[j] instanceof PointerArray) {
/* 386 */               PointerArray array = (PointerArray)args[j];
/* 387 */               if (Structure.ByReference[].class.isAssignableFrom(inArg.getClass())) {
/* 388 */                 Class<? extends Structure> type = (Class)inArg.getClass().getComponentType();
/* 389 */                 Structure[] ss = (Structure[])inArg;
/* 390 */                 for (int si = 0; si < ss.length; si++) {
/* 391 */                   Pointer p = array.getPointer((Native.POINTER_SIZE * si));
/* 392 */                   ss[si] = Structure.updateStructureByReference((Class)type, ss[si], p);
/*     */                 } 
/*     */               } 
/*     */             } 
/* 396 */           } else if (Structure[].class.isAssignableFrom(inArg.getClass())) {
/* 397 */             Structure.autoRead((Structure[])inArg);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/* 402 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   Object invoke(Object[] args, Class<?> returnType, boolean allowObjects) {
/* 407 */     return invoke(args, returnType, allowObjects, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   Object invoke(Object[] args, Class<?> returnType, boolean allowObjects, int fixedArgs) {
/* 412 */     Object result = null;
/* 413 */     int callFlags = this.callFlags | (fixedArgs & 0x3) << 7;
/* 414 */     if (returnType == null || returnType == void.class || returnType == Void.class) {
/* 415 */       Native.invokeVoid(this, this.peer, callFlags, args);
/* 416 */       result = null;
/* 417 */     } else if (returnType == boolean.class || returnType == Boolean.class) {
/* 418 */       result = valueOf((Native.invokeInt(this, this.peer, callFlags, args) != 0));
/* 419 */     } else if (returnType == byte.class || returnType == Byte.class) {
/* 420 */       result = Byte.valueOf((byte)Native.invokeInt(this, this.peer, callFlags, args));
/* 421 */     } else if (returnType == short.class || returnType == Short.class) {
/* 422 */       result = Short.valueOf((short)Native.invokeInt(this, this.peer, callFlags, args));
/* 423 */     } else if (returnType == char.class || returnType == Character.class) {
/* 424 */       result = Character.valueOf((char)Native.invokeInt(this, this.peer, callFlags, args));
/* 425 */     } else if (returnType == int.class || returnType == Integer.class) {
/* 426 */       result = Integer.valueOf(Native.invokeInt(this, this.peer, callFlags, args));
/* 427 */     } else if (returnType == long.class || returnType == Long.class) {
/* 428 */       result = Long.valueOf(Native.invokeLong(this, this.peer, callFlags, args));
/* 429 */     } else if (returnType == float.class || returnType == Float.class) {
/* 430 */       result = Float.valueOf(Native.invokeFloat(this, this.peer, callFlags, args));
/* 431 */     } else if (returnType == double.class || returnType == Double.class) {
/* 432 */       result = Double.valueOf(Native.invokeDouble(this, this.peer, callFlags, args));
/* 433 */     } else if (returnType == String.class) {
/* 434 */       result = invokeString(callFlags, args, false);
/* 435 */     } else if (returnType == WString.class) {
/* 436 */       String s = invokeString(callFlags, args, true);
/* 437 */       if (s != null)
/* 438 */         result = new WString(s); 
/*     */     } else {
/* 440 */       if (Pointer.class.isAssignableFrom(returnType))
/* 441 */         return invokePointer(callFlags, args); 
/* 442 */       if (Structure.class.isAssignableFrom(returnType)) {
/* 443 */         if (Structure.ByValue.class.isAssignableFrom(returnType)) {
/*     */           
/* 445 */           Structure s = Native.invokeStructure(this, this.peer, callFlags, args, 
/* 446 */               (Structure)Structure.newInstance(returnType));
/* 447 */           s.autoRead();
/* 448 */           result = s;
/*     */         } else {
/* 450 */           result = invokePointer(callFlags, args);
/* 451 */           if (result != null) {
/* 452 */             Structure s = (Structure)Structure.newInstance(returnType, (Pointer)result);
/* 453 */             s.conditionalAutoRead();
/* 454 */             result = s;
/*     */           } 
/*     */         } 
/* 457 */       } else if (Callback.class.isAssignableFrom(returnType)) {
/* 458 */         result = invokePointer(callFlags, args);
/* 459 */         if (result != null) {
/* 460 */           result = CallbackReference.getCallback(returnType, (Pointer)result);
/*     */         }
/* 462 */       } else if (returnType == String[].class) {
/* 463 */         Pointer p = invokePointer(callFlags, args);
/* 464 */         if (p != null) {
/* 465 */           result = p.getStringArray(0L, this.encoding);
/*     */         }
/* 467 */       } else if (returnType == WString[].class) {
/* 468 */         Pointer p = invokePointer(callFlags, args);
/* 469 */         if (p != null) {
/* 470 */           String[] arr = p.getWideStringArray(0L);
/* 471 */           WString[] warr = new WString[arr.length];
/* 472 */           for (int i = 0; i < arr.length; i++) {
/* 473 */             warr[i] = new WString(arr[i]);
/*     */           }
/* 475 */           result = warr;
/*     */         } 
/* 477 */       } else if (returnType == Pointer[].class) {
/* 478 */         Pointer p = invokePointer(callFlags, args);
/* 479 */         if (p != null) {
/* 480 */           result = p.getPointerArray(0L);
/*     */         }
/* 482 */       } else if (allowObjects) {
/* 483 */         result = Native.invokeObject(this, this.peer, callFlags, args);
/* 484 */         if (result != null && 
/* 485 */           !returnType.isAssignableFrom(result.getClass())) {
/* 486 */           throw new ClassCastException("Return type " + returnType + " does not match result " + result
/*     */               
/* 488 */               .getClass());
/*     */         }
/*     */       } else {
/* 491 */         throw new IllegalArgumentException("Unsupported return type " + returnType + " in function " + getName());
/*     */       } 
/* 493 */     }  return result;
/*     */   }
/*     */   
/*     */   private Pointer invokePointer(int callFlags, Object[] args) {
/* 497 */     long ptr = Native.invokePointer(this, this.peer, callFlags, args);
/* 498 */     return (ptr == 0L) ? null : new Pointer(ptr);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Object convertArgument(Object[] args, int index, Method invokingMethod, TypeMapper mapper, boolean allowObjects, Class<?> expectedType) {
/* 504 */     Object arg = args[index];
/* 505 */     if (arg != null) {
/* 506 */       Class<?> type = arg.getClass();
/* 507 */       ToNativeConverter converter = null;
/* 508 */       if (NativeMapped.class.isAssignableFrom(type)) {
/* 509 */         converter = NativeMappedConverter.getInstance(type);
/* 510 */       } else if (mapper != null) {
/* 511 */         converter = mapper.getToNativeConverter(type);
/*     */       } 
/* 513 */       if (converter != null) {
/*     */         ToNativeContext context;
/* 515 */         if (invokingMethod != null) {
/* 516 */           context = new MethodParameterContext(this, args, index, invokingMethod);
/*     */         } else {
/*     */           
/* 519 */           context = new FunctionParameterContext(this, args, index);
/*     */         } 
/* 521 */         arg = converter.toNative(arg, context);
/*     */       } 
/*     */     } 
/* 524 */     if (arg == null || isPrimitiveArray(arg.getClass())) {
/* 525 */       return arg;
/*     */     }
/*     */     
/* 528 */     Class<?> argClass = arg.getClass();
/*     */     
/* 530 */     if (arg instanceof Structure) {
/* 531 */       Structure struct = (Structure)arg;
/* 532 */       struct.autoWrite();
/* 533 */       if (struct instanceof Structure.ByValue) {
/*     */         
/* 535 */         Class<?> ptype = struct.getClass();
/* 536 */         if (invokingMethod != null) {
/* 537 */           Class<?>[] ptypes = invokingMethod.getParameterTypes();
/* 538 */           if (IS_VARARGS.isVarArgs(invokingMethod)) {
/* 539 */             if (index < ptypes.length - 1) {
/* 540 */               ptype = ptypes[index];
/*     */             } else {
/* 542 */               Class<?> etype = ptypes[ptypes.length - 1].getComponentType();
/* 543 */               if (etype != Object.class) {
/* 544 */                 ptype = etype;
/*     */               }
/*     */             } 
/*     */           } else {
/* 548 */             ptype = ptypes[index];
/*     */           } 
/*     */         } 
/* 551 */         if (Structure.ByValue.class.isAssignableFrom(ptype)) {
/* 552 */           return struct;
/*     */         }
/*     */       } 
/* 555 */       return struct.getPointer();
/* 556 */     }  if (arg instanceof Callback)
/*     */     {
/* 558 */       return CallbackReference.getFunctionPointer((Callback)arg); } 
/* 559 */     if (arg instanceof String)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 564 */       return (new NativeString((String)arg, false)).getPointer(); } 
/* 565 */     if (arg instanceof WString)
/*     */     {
/* 567 */       return (new NativeString(arg.toString(), true)).getPointer(); } 
/* 568 */     if (arg instanceof Boolean)
/*     */     {
/*     */       
/* 571 */       return Boolean.TRUE.equals(arg) ? INTEGER_TRUE : INTEGER_FALSE; } 
/* 572 */     if (String[].class == argClass)
/* 573 */       return new StringArray((String[])arg, this.encoding); 
/* 574 */     if (WString[].class == argClass)
/* 575 */       return new StringArray((WString[])arg); 
/* 576 */     if (Pointer[].class == argClass)
/* 577 */       return new PointerArray((Pointer[])arg); 
/* 578 */     if (NativeMapped[].class.isAssignableFrom(argClass))
/* 579 */       return new NativeMappedArray((NativeMapped[])arg); 
/* 580 */     if (Structure[].class.isAssignableFrom(argClass)) {
/*     */ 
/*     */       
/* 583 */       Structure[] ss = (Structure[])arg;
/* 584 */       Class<?> type = argClass.getComponentType();
/* 585 */       boolean byRef = Structure.ByReference.class.isAssignableFrom(type);
/* 586 */       if (expectedType != null && 
/* 587 */         !Structure.ByReference[].class.isAssignableFrom(expectedType)) {
/* 588 */         if (byRef) {
/* 589 */           throw new IllegalArgumentException("Function " + getName() + " declared Structure[] at parameter " + index + " but array of " + type + " was passed");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 594 */         for (int i = 0; i < ss.length; i++) {
/* 595 */           if (ss[i] instanceof Structure.ByReference) {
/* 596 */             throw new IllegalArgumentException("Function " + getName() + " declared Structure[] at parameter " + index + " but element " + i + " is of Structure.ByReference type");
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 604 */       if (byRef) {
/* 605 */         Structure.autoWrite(ss);
/* 606 */         Pointer[] pointers = new Pointer[ss.length + 1];
/* 607 */         for (int i = 0; i < ss.length; i++) {
/* 608 */           pointers[i] = (ss[i] != null) ? ss[i].getPointer() : null;
/*     */         }
/* 610 */         return new PointerArray(pointers);
/* 611 */       }  if (ss.length == 0)
/* 612 */         throw new IllegalArgumentException("Structure array must have non-zero length"); 
/* 613 */       if (ss[0] == null) {
/* 614 */         Structure.newInstance(type).toArray(ss);
/* 615 */         return ss[0].getPointer();
/*     */       } 
/* 617 */       Structure.autoWrite(ss);
/* 618 */       return ss[0].getPointer();
/*     */     } 
/* 620 */     if (argClass.isArray())
/* 621 */       throw new IllegalArgumentException("Unsupported array argument type: " + argClass
/* 622 */           .getComponentType()); 
/* 623 */     if (allowObjects)
/* 624 */       return arg; 
/* 625 */     if (!Native.isSupportedNativeType(arg.getClass())) {
/* 626 */       throw new IllegalArgumentException("Unsupported argument type " + arg
/* 627 */           .getClass().getName() + " at parameter " + index + " of function " + 
/*     */           
/* 629 */           getName());
/*     */     }
/* 631 */     return arg;
/*     */   }
/*     */   
/*     */   private boolean isPrimitiveArray(Class<?> argClass) {
/* 635 */     return (argClass.isArray() && argClass
/* 636 */       .getComponentType().isPrimitive());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void invoke(Object[] args) {
/* 645 */     invoke(Void.class, args);
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
/*     */   private String invokeString(int callFlags, Object[] args, boolean wide) {
/* 660 */     Pointer ptr = invokePointer(callFlags, args);
/* 661 */     String s = null;
/* 662 */     if (ptr != null) {
/* 663 */       if (wide) {
/* 664 */         s = ptr.getWideString(0L);
/*     */       } else {
/*     */         
/* 667 */         s = ptr.getString(0L, this.encoding);
/*     */       } 
/*     */     }
/* 670 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 676 */     if (this.library != null) {
/* 677 */       return "native function " + this.functionName + "(" + this.library.getName() + ")@0x" + 
/* 678 */         Long.toHexString(this.peer);
/*     */     }
/* 680 */     return "native function@0x" + Long.toHexString(this.peer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invokeObject(Object[] args) {
/* 687 */     return invoke(Object.class, args);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Pointer invokePointer(Object[] args) {
/* 694 */     return (Pointer)invoke(Pointer.class, args);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String invokeString(Object[] args, boolean wide) {
/* 705 */     Object o = invoke(wide ? WString.class : String.class, args);
/* 706 */     return (o != null) ? o.toString() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int invokeInt(Object[] args) {
/* 713 */     return ((Integer)invoke(Integer.class, args)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long invokeLong(Object[] args) {
/* 719 */     return ((Long)invoke(Long.class, args)).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public float invokeFloat(Object[] args) {
/* 725 */     return ((Float)invoke(Float.class, args)).floatValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double invokeDouble(Object[] args) {
/* 731 */     return ((Double)invoke(Double.class, args)).doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void invokeVoid(Object[] args) {
/* 737 */     invoke(Void.class, args);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 745 */     if (o == this) return true; 
/* 746 */     if (o == null) return false; 
/* 747 */     if (o.getClass() == getClass()) {
/* 748 */       Function other = (Function)o;
/* 749 */       return (other.callFlags == this.callFlags && other.options
/* 750 */         .equals(this.options) && other.peer == this.peer);
/*     */     } 
/*     */     
/* 753 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 761 */     return this.callFlags + this.options.hashCode() + super.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Object[] concatenateVarArgs(Object[] inArgs) {
/* 771 */     if (inArgs != null && inArgs.length > 0) {
/* 772 */       Object lastArg = inArgs[inArgs.length - 1];
/* 773 */       Class<?> argType = (lastArg != null) ? lastArg.getClass() : null;
/* 774 */       if (argType != null && argType.isArray()) {
/* 775 */         Object[] varArgs = (Object[])lastArg;
/*     */         
/* 777 */         for (int i = 0; i < varArgs.length; i++) {
/* 778 */           if (varArgs[i] instanceof Float) {
/* 779 */             varArgs[i] = Double.valueOf(((Float)varArgs[i]).floatValue());
/*     */           }
/*     */         } 
/* 782 */         Object[] fullArgs = new Object[inArgs.length + varArgs.length];
/* 783 */         System.arraycopy(inArgs, 0, fullArgs, 0, inArgs.length - 1);
/* 784 */         System.arraycopy(varArgs, 0, fullArgs, inArgs.length - 1, varArgs.length);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 790 */         fullArgs[fullArgs.length - 1] = null;
/* 791 */         inArgs = fullArgs;
/*     */       } 
/*     */     } 
/* 794 */     return inArgs;
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean isVarArgs(Method m) {
/* 799 */     return IS_VARARGS.isVarArgs(m);
/*     */   }
/*     */ 
/*     */   
/*     */   static int fixedArgs(Method m) {
/* 804 */     return IS_VARARGS.fixedArgs(m);
/*     */   }
/*     */   public static interface PostCallRead {
/*     */     void read(); }
/*     */   
/*     */   private static class NativeMappedArray extends Memory implements PostCallRead { public NativeMappedArray(NativeMapped[] arg) {
/* 810 */       super(Native.getNativeSize(arg.getClass(), arg));
/* 811 */       this.original = arg;
/* 812 */       setValue(0L, this.original, this.original.getClass());
/*     */     }
/*     */     private final NativeMapped[] original;
/*     */     public void read() {
/* 816 */       getValue(0L, this.original.getClass(), this.original);
/*     */     } }
/*     */   
/*     */   private static class PointerArray extends Memory implements PostCallRead {
/*     */     private final Pointer[] original;
/*     */     
/*     */     public PointerArray(Pointer[] arg) {
/* 823 */       super((Native.POINTER_SIZE * (arg.length + 1)));
/* 824 */       this.original = arg;
/* 825 */       for (int i = 0; i < arg.length; i++) {
/* 826 */         setPointer((i * Native.POINTER_SIZE), arg[i]);
/*     */       }
/* 828 */       setPointer((Native.POINTER_SIZE * arg.length), (Pointer)null);
/*     */     }
/*     */     
/*     */     public void read() {
/* 832 */       read(0L, this.original, 0, this.original.length);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static Boolean valueOf(boolean b) {
/* 838 */     return b ? Boolean.TRUE : Boolean.FALSE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\Function.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */