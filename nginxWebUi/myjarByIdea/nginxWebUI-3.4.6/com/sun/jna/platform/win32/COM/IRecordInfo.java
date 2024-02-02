package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public interface IRecordInfo extends IUnknown {
   Guid.IID IID_IRecordInfo = new Guid.IID("{0000002F-0000-0000-C000-000000000046}");

   WinNT.HRESULT RecordInit(WinDef.PVOID var1);

   WinNT.HRESULT RecordClear(WinDef.PVOID var1);

   WinNT.HRESULT RecordCopy(WinDef.PVOID var1, WinDef.PVOID var2);

   WinNT.HRESULT GetGuid(Guid.GUID var1);

   WinNT.HRESULT GetName(WTypes.BSTR var1);

   WinNT.HRESULT GetSize(WinDef.ULONG var1);

   WinNT.HRESULT GetTypeInfo(ITypeInfo var1);

   WinNT.HRESULT GetField(WinDef.PVOID var1, WString var2, Variant.VARIANT var3);

   WinNT.HRESULT GetFieldNoCopy(WinDef.PVOID var1, WString var2, Variant.VARIANT var3, WinDef.PVOID var4);

   WinNT.HRESULT PutField(WinDef.ULONG var1, WinDef.PVOID var2, WString var3, Variant.VARIANT var4);

   WinNT.HRESULT PutFieldNoCopy(WinDef.ULONG var1, WinDef.PVOID var2, WString var3, Variant.VARIANT var4);

   WinNT.HRESULT GetFieldNames(WinDef.ULONG var1, WTypes.BSTR var2);

   WinDef.BOOL IsMatchingType(IRecordInfo var1);

   WinDef.PVOID RecordCreate();

   WinNT.HRESULT RecordCreateCopy(WinDef.PVOID var1, WinDef.PVOID var2);

   WinNT.HRESULT RecordDestroy(WinDef.PVOID var1);
}
