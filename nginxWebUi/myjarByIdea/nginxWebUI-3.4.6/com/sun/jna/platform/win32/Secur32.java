package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Secur32 extends StdCallLibrary {
   Secur32 INSTANCE = (Secur32)Native.load("Secur32", Secur32.class, W32APIOptions.DEFAULT_OPTIONS);

   boolean GetUserNameEx(int var1, char[] var2, IntByReference var3);

   int AcquireCredentialsHandle(String var1, String var2, int var3, WinNT.LUID var4, Pointer var5, Pointer var6, Pointer var7, Sspi.CredHandle var8, Sspi.TimeStamp var9);

   int InitializeSecurityContext(Sspi.CredHandle var1, Sspi.CtxtHandle var2, String var3, int var4, int var5, int var6, Sspi.SecBufferDesc var7, int var8, Sspi.CtxtHandle var9, Sspi.SecBufferDesc var10, IntByReference var11, Sspi.TimeStamp var12);

   int DeleteSecurityContext(Sspi.CtxtHandle var1);

   int FreeCredentialsHandle(Sspi.CredHandle var1);

   int AcceptSecurityContext(Sspi.CredHandle var1, Sspi.CtxtHandle var2, Sspi.SecBufferDesc var3, int var4, int var5, Sspi.CtxtHandle var6, Sspi.SecBufferDesc var7, IntByReference var8, Sspi.TimeStamp var9);

   int CompleteAuthToken(Sspi.CtxtHandle var1, Sspi.SecBufferDesc var2);

   int EnumerateSecurityPackages(IntByReference var1, Sspi.PSecPkgInfo var2);

   int FreeContextBuffer(Pointer var1);

   int QuerySecurityContextToken(Sspi.CtxtHandle var1, WinNT.HANDLEByReference var2);

   int ImpersonateSecurityContext(Sspi.CtxtHandle var1);

   int RevertSecurityContext(Sspi.CtxtHandle var1);

   int QueryContextAttributes(Sspi.CtxtHandle var1, int var2, Structure var3);

   int QueryCredentialsAttributes(Sspi.CredHandle var1, int var2, Structure var3);

   int QuerySecurityPackageInfo(String var1, Sspi.PSecPkgInfo var2);

   int EncryptMessage(Sspi.CtxtHandle var1, int var2, Sspi.SecBufferDesc var3, int var4);

   int VerifySignature(Sspi.CtxtHandle var1, Sspi.SecBufferDesc var2, int var3, IntByReference var4);

   int MakeSignature(Sspi.CtxtHandle var1, int var2, Sspi.SecBufferDesc var3, int var4);

   int DecryptMessage(Sspi.CtxtHandle var1, Sspi.SecBufferDesc var2, int var3, IntByReference var4);

   public abstract static class EXTENDED_NAME_FORMAT {
      public static final int NameUnknown = 0;
      public static final int NameFullyQualifiedDN = 1;
      public static final int NameSamCompatible = 2;
      public static final int NameDisplay = 3;
      public static final int NameUniqueId = 6;
      public static final int NameCanonical = 7;
      public static final int NameUserPrincipal = 8;
      public static final int NameCanonicalEx = 9;
      public static final int NameServicePrincipal = 10;
      public static final int NameDnsDomain = 12;
   }
}
