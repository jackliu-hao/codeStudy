/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.Structure.FieldOrder;
/*     */ import com.sun.jna.Union;
/*     */ import com.sun.jna.win32.W32APITypeMapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Ntifs
/*     */   extends WinDef, BaseTSD
/*     */ {
/*     */   public static final int MAXIMUM_REPARSE_DATA_BUFFER_SIZE = 16384;
/*     */   public static final int REPARSE_BUFFER_HEADER_SIZE = 8;
/*     */   public static final int SYMLINK_FLAG_RELATIVE = 1;
/*     */   
/*     */   @FieldOrder({"SubstituteNameOffset", "SubstituteNameLength", "PrintNameOffset", "PrintNameLength", "Flags", "PathBuffer"})
/*     */   public static class SymbolicLinkReparseBuffer
/*     */     extends Structure
/*     */   {
/*     */     public static class ByReference
/*     */       extends SymbolicLinkReparseBuffer
/*     */       implements Structure.ByReference
/*     */     {
/*     */       public ByReference() {}
/*     */       
/*     */       public ByReference(Pointer memory) {
/*  59 */         super(memory);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  67 */     public short SubstituteNameOffset = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  73 */     public short SubstituteNameLength = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  79 */     public short PrintNameOffset = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  85 */     public short PrintNameLength = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     public int Flags = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 104 */     public char[] PathBuffer = new char[8192];
/*     */     
/*     */     public static int sizeOf() {
/* 107 */       return Native.getNativeSize(Ntifs.MountPointReparseBuffer.class, null);
/*     */     }
/*     */     
/*     */     public SymbolicLinkReparseBuffer() {
/* 111 */       super(W32APITypeMapper.UNICODE);
/*     */     }
/*     */     
/*     */     public SymbolicLinkReparseBuffer(Pointer memory) {
/* 115 */       super(memory, 0, W32APITypeMapper.UNICODE);
/* 116 */       read();
/*     */     }
/*     */ 
/*     */     
/*     */     public SymbolicLinkReparseBuffer(String substituteName, String printName, int Flags) {
/* 121 */       String bothNames = substituteName + printName;
/* 122 */       this.PathBuffer = bothNames.toCharArray();
/* 123 */       this.SubstituteNameOffset = 0;
/* 124 */       this.SubstituteNameLength = (short)(substituteName.length() * 2);
/* 125 */       this.PrintNameOffset = (short)(substituteName.length() * 2);
/* 126 */       this.PrintNameLength = (short)(printName.length() * 2);
/* 127 */       this.Flags = Flags;
/* 128 */       write();
/*     */     }
/*     */ 
/*     */     
/*     */     public SymbolicLinkReparseBuffer(short SubstituteNameOffset, short SubstituteNameLength, short PrintNameOffset, short PrintNameLength, int Flags, String PathBuffer) {
/* 133 */       this.SubstituteNameOffset = SubstituteNameOffset;
/* 134 */       this.SubstituteNameLength = SubstituteNameLength;
/* 135 */       this.PrintNameOffset = PrintNameOffset;
/* 136 */       this.PrintNameLength = PrintNameLength;
/* 137 */       this.Flags = Flags;
/* 138 */       this.PathBuffer = PathBuffer.toCharArray();
/* 139 */       write();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getPrintName() {
/* 146 */       return String.copyValueOf(this.PathBuffer, this.PrintNameOffset / 2, this.PrintNameLength / 2);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getSubstituteName() {
/* 153 */       return String.copyValueOf(this.PathBuffer, this.SubstituteNameOffset / 2, this.SubstituteNameLength / 2);
/*     */     }
/*     */   }
/*     */   
/*     */   @FieldOrder({"SubstituteNameOffset", "SubstituteNameLength", "PrintNameOffset", "PrintNameLength", "PathBuffer"})
/*     */   public static class MountPointReparseBuffer
/*     */     extends Structure {
/*     */     public static class ByReference
/*     */       extends MountPointReparseBuffer implements Structure.ByReference {
/*     */       public ByReference() {}
/*     */       
/*     */       public ByReference(Pointer memory) {
/* 165 */         super(memory);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 173 */     public short SubstituteNameOffset = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 179 */     public short SubstituteNameLength = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 185 */     public short PrintNameOffset = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 191 */     public short PrintNameLength = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 202 */     public char[] PathBuffer = new char[8192];
/*     */     
/*     */     public static int sizeOf() {
/* 205 */       return Native.getNativeSize(MountPointReparseBuffer.class, null);
/*     */     }
/*     */     
/*     */     public MountPointReparseBuffer() {
/* 209 */       super(W32APITypeMapper.UNICODE);
/*     */     }
/*     */     
/*     */     public MountPointReparseBuffer(Pointer memory) {
/* 213 */       super(memory, 0, W32APITypeMapper.UNICODE);
/* 214 */       read();
/*     */     }
/*     */ 
/*     */     
/*     */     public MountPointReparseBuffer(String substituteName, String printName) {
/* 219 */       String bothNames = substituteName + printName;
/* 220 */       this.PathBuffer = bothNames.toCharArray();
/* 221 */       this.SubstituteNameOffset = 0;
/* 222 */       this.SubstituteNameLength = (short)substituteName.length();
/* 223 */       this.PrintNameOffset = (short)(substituteName.length() * 2);
/* 224 */       this.PrintNameLength = (short)(printName.length() * 2);
/* 225 */       write();
/*     */     }
/*     */ 
/*     */     
/*     */     public MountPointReparseBuffer(short SubstituteNameOffset, short SubstituteNameLength, short PrintNameOffset, short PrintNameLength, String PathBuffer) {
/* 230 */       this.SubstituteNameOffset = SubstituteNameOffset;
/* 231 */       this.SubstituteNameLength = SubstituteNameLength;
/* 232 */       this.PrintNameOffset = PrintNameOffset;
/* 233 */       this.PrintNameLength = PrintNameLength;
/* 234 */       this.PathBuffer = PathBuffer.toCharArray();
/* 235 */       write();
/*     */     }
/*     */   }
/*     */   
/*     */   @FieldOrder({"DataBuffer"})
/*     */   public static class GenericReparseBuffer
/*     */     extends Structure {
/*     */     public static class ByReference
/*     */       extends GenericReparseBuffer implements Structure.ByReference {
/*     */       public ByReference() {}
/*     */       
/*     */       public ByReference(Pointer memory) {
/* 247 */         super(memory);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 255 */     public byte[] DataBuffer = new byte[16384];
/*     */     
/*     */     public static int sizeOf() {
/* 258 */       return Native.getNativeSize(GenericReparseBuffer.class, null);
/*     */     }
/*     */ 
/*     */     
/*     */     public GenericReparseBuffer() {}
/*     */ 
/*     */     
/*     */     public GenericReparseBuffer(Pointer memory) {
/* 266 */       super(memory);
/* 267 */       read();
/*     */     }
/*     */ 
/*     */     
/*     */     public GenericReparseBuffer(String DataBuffer) {
/* 272 */       this.DataBuffer = DataBuffer.getBytes();
/* 273 */       write();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @FieldOrder({"ReparseTag", "ReparseDataLength", "Reserved", "u"})
/*     */   public static class REPARSE_DATA_BUFFER
/*     */     extends Structure
/*     */   {
/*     */     public static class ByReference
/*     */       extends REPARSE_DATA_BUFFER
/*     */       implements Structure.ByReference
/*     */     {
/*     */       public ByReference() {}
/*     */       
/*     */       public ByReference(Pointer memory) {
/* 289 */         super(memory);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 296 */     public int ReparseTag = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 301 */     public short ReparseDataLength = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 309 */     public short Reserved = 0;
/*     */     
/*     */     public REPARSE_UNION u;
/*     */ 
/*     */     
/*     */     public static class REPARSE_UNION
/*     */       extends Union
/*     */     {
/*     */       public Ntifs.SymbolicLinkReparseBuffer symLinkReparseBuffer;
/*     */       public Ntifs.MountPointReparseBuffer mountPointReparseBuffer;
/*     */       
/*     */       public REPARSE_UNION(Pointer memory) {
/* 321 */         super(memory);
/*     */       }
/*     */       
/*     */       public Ntifs.GenericReparseBuffer genericReparseBuffer;
/*     */       
/*     */       public static class ByReference extends REPARSE_UNION implements Structure.ByReference {}
/*     */       
/*     */       public REPARSE_UNION() {}
/*     */     }
/*     */     
/*     */     public static int sizeOf() {
/* 332 */       return Native.getNativeSize(REPARSE_DATA_BUFFER.class, null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getSize() {
/* 339 */       return 8 + this.ReparseDataLength;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public REPARSE_DATA_BUFFER() {}
/*     */ 
/*     */     
/*     */     public REPARSE_DATA_BUFFER(int ReparseTag, short Reserved) {
/* 348 */       this.ReparseTag = ReparseTag;
/* 349 */       this.Reserved = Reserved;
/* 350 */       this.ReparseDataLength = 0;
/* 351 */       write();
/*     */     }
/*     */ 
/*     */     
/*     */     public REPARSE_DATA_BUFFER(int ReparseTag, short Reserved, Ntifs.SymbolicLinkReparseBuffer symLinkReparseBuffer) {
/* 356 */       this.ReparseTag = ReparseTag;
/* 357 */       this.Reserved = Reserved;
/* 358 */       this.ReparseDataLength = (short)symLinkReparseBuffer.size();
/* 359 */       this.u.setType(Ntifs.SymbolicLinkReparseBuffer.class);
/* 360 */       this.u.symLinkReparseBuffer = symLinkReparseBuffer;
/* 361 */       write();
/*     */     }
/*     */     
/*     */     public REPARSE_DATA_BUFFER(Pointer memory) {
/* 365 */       super(memory);
/* 366 */       read();
/*     */     }
/*     */ 
/*     */     
/*     */     public void read() {
/* 371 */       super.read();
/*     */       
/* 373 */       switch (this.ReparseTag) {
/*     */         default:
/* 375 */           this.u.setType(Ntifs.GenericReparseBuffer.class);
/*     */           break;
/*     */         case -1610612733:
/* 378 */           this.u.setType(Ntifs.MountPointReparseBuffer.class);
/*     */           break;
/*     */         case -1610612724:
/* 381 */           this.u.setType(Ntifs.SymbolicLinkReparseBuffer.class);
/*     */           break;
/*     */       } 
/* 384 */       this.u.read();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Ntifs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */