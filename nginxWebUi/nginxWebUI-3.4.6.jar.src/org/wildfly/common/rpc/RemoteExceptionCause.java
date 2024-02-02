/*     */ package org.wildfly.common.rpc;
/*     */ 
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.function.Function;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RemoteExceptionCause
/*     */   extends Throwable
/*     */ {
/*     */   private static final long serialVersionUID = 7849011228540958997L;
/*     */   
/*  50 */   private static final ClassValue<Function<Throwable, Map<String, String>>> fieldGetterValue = new ClassValue<Function<Throwable, Map<String, String>>>() {
/*     */       protected Function<Throwable, Map<String, String>> computeValue(Class<?> type) {
/*  52 */         Field[] finalFields, fields = type.getFields();
/*  53 */         int length = fields.length;
/*     */         int i, j;
/*  55 */         for (i = 0, j = 0; i < length; i++) {
/*  56 */           if ((fields[i].getModifiers() & 0x9) == 1) {
/*  57 */             fields[j++] = fields[i];
/*     */           }
/*     */         } 
/*  60 */         int finalLength = j;
/*     */         
/*  62 */         if (j < i) {
/*  63 */           finalFields = Arrays.<Field>copyOf(fields, j);
/*     */         } else {
/*  65 */           finalFields = fields;
/*     */         } 
/*  67 */         if (finalLength == 0)
/*  68 */           return t -> Collections.emptyMap(); 
/*  69 */         if (finalLength == 1) {
/*  70 */           Field field = finalFields[0];
/*  71 */           return t -> {
/*     */               try {
/*     */                 return Collections.singletonMap(field.getName(), String.valueOf(field.get(t)));
/*  74 */               } catch (IllegalAccessException e) {
/*     */                 throw new IllegalStateException(e);
/*     */               } 
/*     */             };
/*     */         } 
/*     */         
/*  80 */         return t -> {
/*     */             Map<String, String> map = new TreeMap<>();
/*     */             for (Field field : finalFields) {
/*     */               try {
/*     */                 map.put(field.getName(), String.valueOf(field.get(t)));
/*  85 */               } catch (IllegalAccessException e) {
/*     */                 throw new IllegalStateException(e);
/*     */               } 
/*     */             } 
/*     */             return Collections.unmodifiableMap(map);
/*     */           };
/*     */       }
/*     */     };
/*     */   private final String exceptionClassName; private final Map<String, String> fields; private transient String toString; private static final int ST_NULL = 0;
/*  94 */   private static final StackTraceElement[] EMPTY_STACK = new StackTraceElement[0];
/*     */   
/*     */   private static final int ST_NEW_STRING = 1;
/*     */   private static final int ST_NEW_STACK_ELEMENT_V8 = 2;
/*     */   private static final int ST_NEW_STACK_ELEMENT_V9 = 3;
/*     */   
/*     */   RemoteExceptionCause(String msg, RemoteExceptionCause cause, String exceptionClassName, Map<String, String> fields, boolean cloneFields) {
/* 101 */     super(msg);
/* 102 */     if (cause != null) {
/* 103 */       initCause(cause);
/*     */     }
/* 105 */     Assert.checkNotNullParam("exceptionClassName", exceptionClassName);
/* 106 */     this.exceptionClassName = exceptionClassName;
/* 107 */     if (cloneFields)
/* 108 */     { Iterator<Map.Entry<String, String>> iterator = fields.entrySet().iterator();
/* 109 */       if (!iterator.hasNext()) {
/* 110 */         this.fields = Collections.emptyMap();
/*     */       } else {
/* 112 */         Map.Entry<String, String> e1 = iterator.next();
/* 113 */         String name1 = e1.getKey();
/* 114 */         String value1 = e1.getValue();
/* 115 */         if (name1 == null || value1 == null) {
/* 116 */           throw CommonMessages.msg.cannotContainNullFieldNameOrValue();
/*     */         }
/* 118 */         if (!iterator.hasNext()) {
/* 119 */           this.fields = Collections.singletonMap(name1, value1);
/*     */         } else {
/* 121 */           Map<String, String> map = new TreeMap<>();
/* 122 */           map.put(name1, value1);
/*     */           while (true) {
/* 124 */             Map.Entry<String, String> next = iterator.next();
/* 125 */             map.put(next.getKey(), next.getValue());
/* 126 */             if (!iterator.hasNext())
/* 127 */             { this.fields = Collections.unmodifiableMap(map); return; } 
/*     */           } 
/*     */         } 
/*     */       }  }
/* 131 */     else { this.fields = fields; }
/*     */   
/*     */   }
/*     */   private static final int ST_NEW_EXCEPTION_CAUSE = 4;
/*     */   private static final int ST_INT8 = 5;
/*     */   private static final int ST_INT16 = 6;
/*     */   private static final int ST_INT32 = 7;
/*     */   private static final int ST_INT_MINI = 32;
/*     */   private static final int ST_BACKREF_FAR = 64;
/*     */   private static final int ST_BACKREF_NEAR = 128;
/*     */   
/*     */   public RemoteExceptionCause(String msg, String exceptionClassName) {
/* 143 */     this(msg, (RemoteExceptionCause)null, exceptionClassName, Collections.emptyMap(), false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RemoteExceptionCause(String msg, RemoteExceptionCause cause, String exceptionClassName) {
/* 154 */     this(msg, cause, exceptionClassName, Collections.emptyMap(), false);
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
/*     */   public RemoteExceptionCause(String msg, String exceptionClassName, Map<String, String> fields) {
/* 166 */     this(msg, (RemoteExceptionCause)null, exceptionClassName, fields, true);
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
/*     */   public RemoteExceptionCause(String msg, RemoteExceptionCause cause, String exceptionClassName, Map<String, String> fields) {
/* 178 */     this(msg, cause, exceptionClassName, fields, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RemoteExceptionCause of(Throwable t) {
/* 189 */     return of(t, new IdentityHashMap<>());
/*     */   }
/*     */   
/*     */   private static RemoteExceptionCause of(Throwable t, IdentityHashMap<Throwable, RemoteExceptionCause> seen) {
/* 193 */     if (t == null) return null; 
/* 194 */     if (t instanceof RemoteExceptionCause) {
/* 195 */       return (RemoteExceptionCause)t;
/*     */     }
/* 197 */     RemoteExceptionCause existing = seen.get(t);
/* 198 */     if (existing != null) {
/* 199 */       return existing;
/*     */     }
/* 201 */     RemoteExceptionCause e = new RemoteExceptionCause(t.getMessage(), t.getClass().getName(), ((Function<Throwable, Map<String, String>>)fieldGetterValue.get(t.getClass())).apply(t));
/* 202 */     e.setStackTrace(t.getStackTrace());
/* 203 */     seen.put(t, e);
/* 204 */     Throwable cause = t.getCause();
/* 205 */     if (cause != null) e.initCause(of(cause, seen)); 
/* 206 */     for (Throwable throwable : t.getSuppressed()) {
/* 207 */       e.addSuppressed(of(throwable, seen));
/*     */     }
/* 209 */     return e;
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
/*     */   public Throwable toPlainThrowable() {
/* 221 */     Throwable throwable = new Throwable(toString(), getCause());
/* 222 */     throwable.setStackTrace(getStackTrace());
/* 223 */     for (Throwable s : getSuppressed()) {
/* 224 */       throwable.addSuppressed(s);
/*     */     }
/* 226 */     return throwable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getExceptionClassName() {
/* 235 */     return this.exceptionClassName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getFieldNames() {
/* 244 */     return this.fields.keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFieldValue(String fieldName) {
/* 254 */     Assert.checkNotNullParam("fieldName", fieldName);
/* 255 */     return this.fields.get(fieldName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 266 */     String toString = this.toString;
/* 267 */     if (toString == null) {
/* 268 */       String message = getMessage();
/* 269 */       StringBuilder b = new StringBuilder();
/* 270 */       b.append((message == null) ? CommonMessages.msg.remoteException(this.exceptionClassName) : CommonMessages.msg.remoteException(this.exceptionClassName, message));
/* 271 */       Iterator<Map.Entry<String, String>> iterator = this.fields.entrySet().iterator();
/* 272 */       if (iterator.hasNext()) {
/* 273 */         b.append("\n\tPublic fields:");
/*     */         do {
/* 275 */           Map.Entry<String, String> entry = iterator.next();
/* 276 */           b.append('\n').append('\t').append('\t').append(entry.getKey()).append('=').append(entry.getValue());
/* 277 */         } while (iterator.hasNext());
/*     */       } 
/* 279 */       return this.toString = b.toString();
/*     */     } 
/* 281 */     return toString;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeToStream(DataOutput output) throws IOException {
/* 312 */     Assert.checkNotNullParam("output", output);
/* 313 */     writeToStream(output, new IdentityIntMap(), new HashMap<>(), 0);
/*     */   }
/*     */   
/*     */   private static int readPackedInt(DataInput is) throws IOException {
/* 317 */     int b = is.readUnsignedByte();
/* 318 */     if ((b & 0xE0) == 32)
/*     */     {
/* 320 */       return b << 27 >> 27; } 
/* 321 */     if (b == 5)
/* 322 */       return is.readByte(); 
/* 323 */     if (b == 6)
/* 324 */       return is.readShort(); 
/* 325 */     if (b == 7) {
/* 326 */       return is.readInt();
/*     */     }
/* 328 */     throw CommonMessages.msg.corruptedStream();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void writePackedInt(DataOutput os, int val) throws IOException {
/* 333 */     if (-16 <= val && val < 16) {
/* 334 */       os.write(0x20 | val & 0x1F);
/* 335 */     } else if (-128 <= val && val < 128) {
/* 336 */       os.write(5);
/* 337 */       os.write(val);
/* 338 */     } else if (-32768 <= val && val < 32768) {
/* 339 */       os.write(6);
/* 340 */       os.writeShort(val);
/*     */     } else {
/* 342 */       os.write(7);
/* 343 */       os.writeInt(val);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private int writeToStream(DataOutput output, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
/* 349 */     seen.put(this, cnt++);
/*     */     
/* 351 */     output.writeByte(4);
/*     */     
/* 353 */     cnt = writeString(output, this.exceptionClassName, seen, stringCache, cnt);
/*     */     
/* 355 */     cnt = writeString(output, getMessage(), seen, stringCache, cnt);
/*     */     
/* 357 */     cnt = writeStackTrace(output, getStackTrace(), seen, stringCache, cnt);
/*     */     
/* 359 */     cnt = writeFields(output, this.fields, seen, stringCache, cnt);
/*     */     
/* 361 */     cnt = writeThrowable(output, getCause(), seen, stringCache, cnt);
/*     */     
/* 363 */     Throwable[] suppressed = getSuppressed();
/* 364 */     writePackedInt(output, suppressed.length);
/* 365 */     for (Throwable t : suppressed) {
/* 366 */       cnt = writeThrowable(output, t, seen, stringCache, cnt);
/*     */     }
/* 368 */     return cnt;
/*     */   }
/*     */   
/*     */   private int writeFields(DataOutput output, Map<String, String> fields, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
/* 372 */     writePackedInt(output, fields.size());
/* 373 */     for (Map.Entry<String, String> entry : fields.entrySet()) {
/* 374 */       cnt = writeString(output, entry.getKey(), seen, stringCache, cnt);
/* 375 */       cnt = writeString(output, entry.getValue(), seen, stringCache, cnt);
/*     */     } 
/* 377 */     return cnt;
/*     */   }
/*     */ 
/*     */   
/*     */   private int writeStackTrace(DataOutput output, StackTraceElement[] stackTrace, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
/* 382 */     int length = stackTrace.length;
/* 383 */     writePackedInt(output, length);
/* 384 */     for (StackTraceElement element : stackTrace) {
/* 385 */       cnt = writeStackElement(output, element, seen, stringCache, cnt);
/*     */     }
/* 387 */     return cnt;
/*     */   }
/*     */   
/*     */   private int writeStackElement(DataOutput output, StackTraceElement element, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
/* 391 */     int idx = seen.get(element, -1);
/* 392 */     int distance = cnt - idx;
/* 393 */     if (idx == -1 || distance > 16383) {
/* 394 */       output.write(2);
/* 395 */       cnt = writeString(output, element.getClassName(), seen, stringCache, cnt);
/* 396 */       cnt = writeString(output, element.getMethodName(), seen, stringCache, cnt);
/* 397 */       cnt = writeString(output, element.getFileName(), seen, stringCache, cnt);
/* 398 */       writePackedInt(output, element.getLineNumber());
/* 399 */       seen.put(element, cnt++);
/* 400 */       return cnt;
/*     */     } 
/* 402 */     if (distance < 127) {
/* 403 */       output.writeByte(0x80 | distance);
/*     */     } else {
/* 405 */       assert distance <= 16383;
/* 406 */       output.writeByte(0x40 | distance >> 8);
/* 407 */       output.writeByte(distance);
/*     */     } 
/* 409 */     return cnt;
/*     */   }
/*     */ 
/*     */   
/*     */   private int writeThrowable(DataOutput output, Throwable throwable, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
/* 414 */     if (throwable == null) {
/* 415 */       output.write(0);
/* 416 */       return cnt;
/*     */     } 
/* 418 */     int idx = seen.get(throwable, -1);
/* 419 */     int distance = cnt - idx;
/* 420 */     if (idx == -1 || distance >= 16384) {
/*     */       RemoteExceptionCause nested;
/* 422 */       if (throwable instanceof RemoteExceptionCause) {
/* 423 */         nested = (RemoteExceptionCause)throwable;
/*     */       } else {
/* 425 */         seen.put(throwable, cnt);
/* 426 */         nested = of(throwable);
/*     */       } 
/* 428 */       return nested.writeToStream(output, seen, stringCache, cnt);
/*     */     } 
/* 430 */     if (distance < 127) {
/* 431 */       output.writeByte(0x80 | distance);
/*     */     } else {
/* 433 */       assert distance <= 16383;
/* 434 */       output.writeByte(0x40 | distance >> 8);
/* 435 */       output.writeByte(distance);
/*     */     } 
/* 437 */     return cnt;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int writeString(DataOutput output, String string, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
/* 443 */     if (string == null) {
/* 444 */       output.write(0);
/* 445 */       return cnt;
/*     */     } 
/*     */     
/* 448 */     string = stringCache.computeIfAbsent(string, (Function)Function.identity());
/* 449 */     int idx = seen.get(string, -1);
/* 450 */     int distance = cnt - idx;
/* 451 */     if (idx == -1 || distance > 16383) {
/* 452 */       seen.put(string, cnt);
/* 453 */       output.write(1);
/* 454 */       output.writeUTF(string);
/* 455 */       return cnt + 1;
/*     */     } 
/* 457 */     if (distance < 127) {
/* 458 */       output.writeByte(0x80 | distance);
/*     */     } else {
/* 460 */       assert distance <= 16383;
/* 461 */       output.writeByte(0x40 | distance >> 8);
/* 462 */       output.writeByte(distance);
/*     */     } 
/* 464 */     return cnt;
/*     */   }
/*     */ 
/*     */   
/*     */   public static RemoteExceptionCause readFromStream(DataInput input) throws IOException {
/* 469 */     return readObject(input, RemoteExceptionCause.class, new ArrayList(), false);
/*     */   }
/*     */   
/*     */   private static <T> T readObject(DataInput input, Class<T> expect, ArrayList<Object> cache, boolean allowNull) throws IOException {
/* 473 */     int b = input.readUnsignedByte();
/* 474 */     if (b == 0) {
/* 475 */       if (!allowNull) {
/* 476 */         throw CommonMessages.msg.corruptedStream();
/*     */       }
/* 478 */       return null;
/* 479 */     }  if (b == 1) {
/* 480 */       if (expect != String.class) {
/* 481 */         throw CommonMessages.msg.corruptedStream();
/*     */       }
/* 483 */       String str = input.readUTF();
/* 484 */       cache.add(str);
/* 485 */       return expect.cast(str);
/* 486 */     }  if (b == 4) {
/* 487 */       StackTraceElement[] stackTrace; Map<String, String> fields; if (expect != RemoteExceptionCause.class) {
/* 488 */         throw CommonMessages.msg.corruptedStream();
/*     */       }
/* 490 */       int idx = cache.size();
/* 491 */       cache.add(null);
/* 492 */       String exClassName = readObject(input, String.class, cache, false);
/* 493 */       String exMessage = readObject(input, String.class, cache, true);
/* 494 */       int length = readPackedInt(input);
/*     */       
/* 496 */       if (length == 0) {
/* 497 */         stackTrace = EMPTY_STACK;
/*     */       } else {
/* 499 */         stackTrace = new StackTraceElement[length];
/* 500 */         for (int j = 0; j < length; j++) {
/* 501 */           stackTrace[j] = readObject(input, StackTraceElement.class, cache, false);
/*     */         }
/*     */       } 
/*     */       
/* 505 */       length = readPackedInt(input);
/* 506 */       if (length == 0) {
/* 507 */         fields = Collections.emptyMap();
/* 508 */       } else if (length == 1) {
/* 509 */         fields = Collections.singletonMap(readObject(input, String.class, cache, false), readObject(input, String.class, cache, false));
/*     */       } else {
/* 511 */         fields = new HashMap<>(length);
/* 512 */         for (int j = 0; j < length; j++) {
/* 513 */           fields.put(readObject(input, String.class, cache, false), readObject(input, String.class, cache, false));
/*     */         }
/*     */       } 
/* 516 */       RemoteExceptionCause result = new RemoteExceptionCause(exMessage, null, exClassName, fields, false);
/* 517 */       cache.set(idx, result);
/* 518 */       RemoteExceptionCause causedBy = readObject(input, RemoteExceptionCause.class, cache, true);
/* 519 */       result.initCause(causedBy);
/* 520 */       length = readPackedInt(input);
/* 521 */       result.setStackTrace(stackTrace);
/* 522 */       for (int i = 0; i < length; i++)
/*     */       {
/*     */         
/* 525 */         result.addSuppressed(readObject(input, (Class)RemoteExceptionCause.class, cache, false));
/*     */       }
/* 527 */       return expect.cast(result);
/* 528 */     }  if (b == 2) {
/* 529 */       if (expect != StackTraceElement.class) {
/* 530 */         throw CommonMessages.msg.corruptedStream();
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 538 */       StackTraceElement element = new StackTraceElement(readObject(input, String.class, cache, false), readObject(input, String.class, cache, false), readObject(input, String.class, cache, true), readPackedInt(input));
/*     */       
/* 540 */       cache.add(element);
/* 541 */       return expect.cast(element);
/* 542 */     }  if (b == 3) {
/* 543 */       if (expect != StackTraceElement.class) {
/* 544 */         throw CommonMessages.msg.corruptedStream();
/*     */       }
/*     */       
/* 547 */       readObject(input, String.class, cache, true);
/* 548 */       readObject(input, String.class, cache, true);
/* 549 */       readObject(input, String.class, cache, true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 556 */       StackTraceElement element = new StackTraceElement(readObject(input, String.class, cache, false), readObject(input, String.class, cache, false), readObject(input, String.class, cache, true), readPackedInt(input));
/*     */       
/* 558 */       cache.add(element);
/* 559 */       return expect.cast(element);
/* 560 */     }  if ((b & 0x80) != 0) {
/* 561 */       int idx = b & 0x7F;
/* 562 */       if (idx > cache.size()) {
/* 563 */         throw CommonMessages.msg.corruptedStream();
/*     */       }
/* 565 */       Object obj = cache.get(cache.size() - idx);
/* 566 */       if (expect.isInstance(obj)) {
/* 567 */         return expect.cast(obj);
/*     */       }
/* 569 */       throw CommonMessages.msg.corruptedStream();
/*     */     } 
/* 571 */     if ((b & 0x40) != 0) {
/* 572 */       int b2 = input.readUnsignedByte();
/* 573 */       int idx = (b & 0x3F) << 8 | b2;
/* 574 */       if (idx > cache.size()) {
/* 575 */         throw CommonMessages.msg.corruptedStream();
/*     */       }
/* 577 */       Object obj = cache.get(cache.size() - idx);
/* 578 */       if (expect.isInstance(obj)) {
/* 579 */         return expect.cast(obj);
/*     */       }
/* 581 */       throw CommonMessages.msg.corruptedStream();
/*     */     } 
/*     */     
/* 584 */     throw CommonMessages.msg.corruptedStream();
/*     */   }
/*     */ 
/*     */   
/* 588 */   private static final String[] NO_STRINGS = new String[0];
/* 589 */   private static final RemoteExceptionCause[] NO_REMOTE_EXCEPTION_CAUSES = new RemoteExceptionCause[0]; Object writeReplace() {
/*     */     RemoteExceptionCause[] suppressed;
/*     */     String[] fieldArray;
/* 592 */     Throwable[] origSuppressed = getSuppressed();
/* 593 */     int length = origSuppressed.length;
/*     */     
/* 595 */     if (length == 0) {
/* 596 */       suppressed = NO_REMOTE_EXCEPTION_CAUSES;
/*     */     } else {
/* 598 */       suppressed = new RemoteExceptionCause[length];
/* 599 */       for (int i = 0; i < length; i++) {
/* 600 */         suppressed[i] = of(origSuppressed[i]);
/*     */       }
/*     */     } 
/*     */     
/* 604 */     int size = this.fields.size();
/* 605 */     if (size == 0) {
/* 606 */       fieldArray = NO_STRINGS;
/*     */     } else {
/* 608 */       fieldArray = new String[size << 1];
/* 609 */       int i = 0;
/* 610 */       for (Map.Entry<String, String> entry : this.fields.entrySet()) {
/* 611 */         fieldArray[i++] = entry.getKey();
/* 612 */         fieldArray[i++] = entry.getValue();
/*     */       } 
/*     */     } 
/* 615 */     return new Serialized(getMessage(), this.exceptionClassName, of(getCause()), suppressed, getStackTrace(), fieldArray);
/*     */   }
/*     */   
/*     */   public RemoteExceptionCause getCause() {
/* 619 */     return (RemoteExceptionCause)super.getCause();
/*     */   }
/*     */ 
/*     */   
/*     */   static final class Serialized
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = -2201431870774913071L;
/*     */     final String m;
/*     */     final String cn;
/*     */     final RemoteExceptionCause c;
/*     */     final RemoteExceptionCause[] s;
/*     */     final StackTraceElement[] st;
/*     */     final String[] f;
/*     */     
/*     */     Serialized(String m, String cn, RemoteExceptionCause c, RemoteExceptionCause[] s, StackTraceElement[] st, String[] f) {
/* 635 */       this.m = m;
/* 636 */       this.cn = cn;
/* 637 */       this.c = c;
/* 638 */       this.s = s;
/* 639 */       this.st = st;
/* 640 */       this.f = f;
/*     */     }
/*     */     
/*     */     Object readResolve() {
/*     */       Map<String, String> fields;
/* 645 */       if (this.f == null) {
/* 646 */         fields = Collections.emptyMap();
/*     */       } else {
/* 648 */         int fl = this.f.length;
/* 649 */         if ((fl & 0x1) != 0)
/* 650 */           throw CommonMessages.msg.invalidOddFields(); 
/* 651 */         if (fl == 0) {
/* 652 */           fields = Collections.emptyMap();
/* 653 */         } else if (fl == 2) {
/* 654 */           fields = Collections.singletonMap(this.f[0], this.f[1]);
/*     */         } else {
/* 656 */           TreeMap<String, String> map = new TreeMap<>();
/* 657 */           for (int i = 0; i < fl; i += 2) {
/* 658 */             map.put(this.f[i], this.f[i + 1]);
/*     */           }
/* 660 */           fields = Collections.unmodifiableMap(map);
/*     */         } 
/*     */       } 
/* 663 */       RemoteExceptionCause ex = new RemoteExceptionCause(this.m, this.c, this.cn, fields, false);
/* 664 */       ex.setStackTrace(this.st);
/* 665 */       RemoteExceptionCause[] suppressed = this.s;
/* 666 */       if (suppressed != null) for (RemoteExceptionCause c : suppressed) {
/* 667 */           ex.addSuppressed(c);
/*     */         } 
/* 669 */       return ex;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\rpc\RemoteExceptionCause.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */