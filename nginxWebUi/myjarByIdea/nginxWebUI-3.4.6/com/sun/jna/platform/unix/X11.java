package com.sun.jna.platform.unix;

import com.sun.jna.Callback;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

public interface X11 extends Library {
   X11 INSTANCE = (X11)Native.load("X11", X11.class);
   int XK_0 = 48;
   int XK_9 = 57;
   int XK_A = 65;
   int XK_Z = 90;
   int XK_a = 97;
   int XK_z = 122;
   int XK_Shift_L = 65505;
   int XK_Shift_R = 65505;
   int XK_Control_L = 65507;
   int XK_Control_R = 65508;
   int XK_CapsLock = 65509;
   int XK_ShiftLock = 65510;
   int XK_Meta_L = 65511;
   int XK_Meta_R = 65512;
   int XK_Alt_L = 65513;
   int XK_Alt_R = 65514;
   int VisualNoMask = 0;
   int VisualIDMask = 1;
   int VisualScreenMask = 2;
   int VisualDepthMask = 4;
   int VisualClassMask = 8;
   int VisualRedMaskMask = 16;
   int VisualGreenMaskMask = 32;
   int VisualBlueMaskMask = 64;
   int VisualColormapSizeMask = 128;
   int VisualBitsPerRGBMask = 256;
   int VisualAllMask = 511;
   Atom XA_PRIMARY = new Atom(1L);
   Atom XA_SECONDARY = new Atom(2L);
   Atom XA_ARC = new Atom(3L);
   Atom XA_ATOM = new Atom(4L);
   Atom XA_BITMAP = new Atom(5L);
   Atom XA_CARDINAL = new Atom(6L);
   Atom XA_COLORMAP = new Atom(7L);
   Atom XA_CURSOR = new Atom(8L);
   Atom XA_CUT_BUFFER0 = new Atom(9L);
   Atom XA_CUT_BUFFER1 = new Atom(10L);
   Atom XA_CUT_BUFFER2 = new Atom(11L);
   Atom XA_CUT_BUFFER3 = new Atom(12L);
   Atom XA_CUT_BUFFER4 = new Atom(13L);
   Atom XA_CUT_BUFFER5 = new Atom(14L);
   Atom XA_CUT_BUFFER6 = new Atom(15L);
   Atom XA_CUT_BUFFER7 = new Atom(16L);
   Atom XA_DRAWABLE = new Atom(17L);
   Atom XA_FONT = new Atom(18L);
   Atom XA_INTEGER = new Atom(19L);
   Atom XA_PIXMAP = new Atom(20L);
   Atom XA_POINT = new Atom(21L);
   Atom XA_RECTANGLE = new Atom(22L);
   Atom XA_RESOURCE_MANAGER = new Atom(23L);
   Atom XA_RGB_COLOR_MAP = new Atom(24L);
   Atom XA_RGB_BEST_MAP = new Atom(25L);
   Atom XA_RGB_BLUE_MAP = new Atom(26L);
   Atom XA_RGB_DEFAULT_MAP = new Atom(27L);
   Atom XA_RGB_GRAY_MAP = new Atom(28L);
   Atom XA_RGB_GREEN_MAP = new Atom(29L);
   Atom XA_RGB_RED_MAP = new Atom(30L);
   Atom XA_STRING = new Atom(31L);
   Atom XA_VISUALID = new Atom(32L);
   Atom XA_WINDOW = new Atom(33L);
   Atom XA_WM_COMMAND = new Atom(34L);
   Atom XA_WM_HINTS = new Atom(35L);
   Atom XA_WM_CLIENT_MACHINE = new Atom(36L);
   Atom XA_WM_ICON_NAME = new Atom(37L);
   Atom XA_WM_ICON_SIZE = new Atom(38L);
   Atom XA_WM_NAME = new Atom(39L);
   Atom XA_WM_NORMAL_HINTS = new Atom(40L);
   Atom XA_WM_SIZE_HINTS = new Atom(41L);
   Atom XA_WM_ZOOM_HINTS = new Atom(42L);
   Atom XA_MIN_SPACE = new Atom(43L);
   Atom XA_NORM_SPACE = new Atom(44L);
   Atom XA_MAX_SPACE = new Atom(45L);
   Atom XA_END_SPACE = new Atom(46L);
   Atom XA_SUPERSCRIPT_X = new Atom(47L);
   Atom XA_SUPERSCRIPT_Y = new Atom(48L);
   Atom XA_SUBSCRIPT_X = new Atom(49L);
   Atom XA_SUBSCRIPT_Y = new Atom(50L);
   Atom XA_UNDERLINE_POSITION = new Atom(51L);
   Atom XA_UNDERLINE_THICKNESS = new Atom(52L);
   Atom XA_STRIKEOUT_ASCENT = new Atom(53L);
   Atom XA_STRIKEOUT_DESCENT = new Atom(54L);
   Atom XA_ITALIC_ANGLE = new Atom(55L);
   Atom XA_X_HEIGHT = new Atom(56L);
   Atom XA_QUAD_WIDTH = new Atom(57L);
   Atom XA_WEIGHT = new Atom(58L);
   Atom XA_POINT_SIZE = new Atom(59L);
   Atom XA_RESOLUTION = new Atom(60L);
   Atom XA_COPYRIGHT = new Atom(61L);
   Atom XA_NOTICE = new Atom(62L);
   Atom XA_FONT_NAME = new Atom(63L);
   Atom XA_FAMILY_NAME = new Atom(64L);
   Atom XA_FULL_NAME = new Atom(65L);
   Atom XA_CAP_HEIGHT = new Atom(66L);
   Atom XA_WM_CLASS = new Atom(67L);
   Atom XA_WM_TRANSIENT_FOR = new Atom(68L);
   Atom XA_LAST_PREDEFINED = XA_WM_TRANSIENT_FOR;
   int None = 0;
   int ParentRelative = 1;
   int CopyFromParent = 0;
   int PointerWindow = 0;
   int InputFocus = 1;
   int PointerRoot = 1;
   int AnyPropertyType = 0;
   int AnyKey = 0;
   int AnyButton = 0;
   int AllTemporary = 0;
   int CurrentTime = 0;
   int NoSymbol = 0;
   int NoEventMask = 0;
   int KeyPressMask = 1;
   int KeyReleaseMask = 2;
   int ButtonPressMask = 4;
   int ButtonReleaseMask = 8;
   int EnterWindowMask = 16;
   int LeaveWindowMask = 32;
   int PointerMotionMask = 64;
   int PointerMotionHintMask = 128;
   int Button1MotionMask = 256;
   int Button2MotionMask = 512;
   int Button3MotionMask = 1024;
   int Button4MotionMask = 2048;
   int Button5MotionMask = 4096;
   int ButtonMotionMask = 8192;
   int KeymapStateMask = 16384;
   int ExposureMask = 32768;
   int VisibilityChangeMask = 65536;
   int StructureNotifyMask = 131072;
   int ResizeRedirectMask = 262144;
   int SubstructureNotifyMask = 524288;
   int SubstructureRedirectMask = 1048576;
   int FocusChangeMask = 2097152;
   int PropertyChangeMask = 4194304;
   int ColormapChangeMask = 8388608;
   int OwnerGrabButtonMask = 16777216;
   int KeyPress = 2;
   int KeyRelease = 3;
   int ButtonPress = 4;
   int ButtonRelease = 5;
   int MotionNotify = 6;
   int EnterNotify = 7;
   int LeaveNotify = 8;
   int FocusIn = 9;
   int FocusOut = 10;
   int KeymapNotify = 11;
   int Expose = 12;
   int GraphicsExpose = 13;
   int NoExpose = 14;
   int VisibilityNotify = 15;
   int CreateNotify = 16;
   int DestroyNotify = 17;
   int UnmapNotify = 18;
   int MapNotify = 19;
   int MapRequest = 20;
   int ReparentNotify = 21;
   int ConfigureNotify = 22;
   int ConfigureRequest = 23;
   int GravityNotify = 24;
   int ResizeRequest = 25;
   int CirculateNotify = 26;
   int CirculateRequest = 27;
   int PropertyNotify = 28;
   int SelectionClear = 29;
   int SelectionRequest = 30;
   int SelectionNotify = 31;
   int ColormapNotify = 32;
   int ClientMessage = 33;
   int MappingNotify = 34;
   int LASTEvent = 35;
   int ShiftMask = 1;
   int LockMask = 2;
   int ControlMask = 4;
   int Mod1Mask = 8;
   int Mod2Mask = 16;
   int Mod3Mask = 32;
   int Mod4Mask = 64;
   int Mod5Mask = 128;
   int ShiftMapIndex = 0;
   int LockMapIndex = 1;
   int ControlMapIndex = 2;
   int Mod1MapIndex = 3;
   int Mod2MapIndex = 4;
   int Mod3MapIndex = 5;
   int Mod4MapIndex = 6;
   int Mod5MapIndex = 7;
   int Button1Mask = 256;
   int Button2Mask = 512;
   int Button3Mask = 1024;
   int Button4Mask = 2048;
   int Button5Mask = 4096;
   int AnyModifier = 32768;
   int Button1 = 1;
   int Button2 = 2;
   int Button3 = 3;
   int Button4 = 4;
   int Button5 = 5;
   int NotifyNormal = 0;
   int NotifyGrab = 1;
   int NotifyUngrab = 2;
   int NotifyWhileGrabbed = 3;
   int NotifyHint = 1;
   int NotifyAncestor = 0;
   int NotifyVirtual = 1;
   int NotifyInferior = 2;
   int NotifyNonlinear = 3;
   int NotifyNonlinearVirtual = 4;
   int NotifyPointer = 5;
   int NotifyPointerRoot = 6;
   int NotifyDetailNone = 7;
   int VisibilityUnobscured = 0;
   int VisibilityPartiallyObscured = 1;
   int VisibilityFullyObscured = 2;
   int PlaceOnTop = 0;
   int PlaceOnBottom = 1;
   int FamilyInternet = 0;
   int FamilyDECnet = 1;
   int FamilyChaos = 2;
   int FamilyInternet6 = 6;
   int FamilyServerInterpreted = 5;
   int PropertyNewValue = 0;
   int PropertyDelete = 1;
   int ColormapUninstalled = 0;
   int ColormapInstalled = 1;
   int GrabModeSync = 0;
   int GrabModeAsync = 1;
   int GrabSuccess = 0;
   int AlreadyGrabbed = 1;
   int GrabInvalidTime = 2;
   int GrabNotViewable = 3;
   int GrabFrozen = 4;
   int AsyncPointer = 0;
   int SyncPointer = 1;
   int ReplayPointer = 2;
   int AsyncKeyboard = 3;
   int SyncKeyboard = 4;
   int ReplayKeyboard = 5;
   int AsyncBoth = 6;
   int SyncBoth = 7;
   int RevertToNone = 0;
   int RevertToPointerRoot = 1;
   int RevertToParent = 2;
   int Success = 0;
   int BadRequest = 1;
   int BadValue = 2;
   int BadWindow = 3;
   int BadPixmap = 4;
   int BadAtom = 5;
   int BadCursor = 6;
   int BadFont = 7;
   int BadMatch = 8;
   int BadDrawable = 9;
   int BadAccess = 10;
   int BadAlloc = 11;
   int BadColor = 12;
   int BadGC = 13;
   int BadIDChoice = 14;
   int BadName = 15;
   int BadLength = 16;
   int BadImplementation = 17;
   int FirstExtensionError = 128;
   int LastExtensionError = 255;
   int InputOutput = 1;
   int InputOnly = 2;
   int CWBackPixmap = 1;
   int CWBackPixel = 2;
   int CWBorderPixmap = 4;
   int CWBorderPixel = 8;
   int CWBitGravity = 16;
   int CWWinGravity = 32;
   int CWBackingStore = 64;
   int CWBackingPlanes = 128;
   int CWBackingPixel = 256;
   int CWOverrideRedirect = 512;
   int CWSaveUnder = 1024;
   int CWEventMask = 2048;
   int CWDontPropagate = 4096;
   int CWColormap = 8192;
   int CWCursor = 16384;
   int CWX = 1;
   int CWY = 2;
   int CWWidth = 4;
   int CWHeight = 8;
   int CWBorderWidth = 16;
   int CWSibling = 32;
   int CWStackMode = 64;
   int ForgetGravity = 0;
   int NorthWestGravity = 1;
   int NorthGravity = 2;
   int NorthEastGravity = 3;
   int WestGravity = 4;
   int CenterGravity = 5;
   int EastGravity = 6;
   int SouthWestGravity = 7;
   int SouthGravity = 8;
   int SouthEastGravity = 9;
   int StaticGravity = 10;
   int UnmapGravity = 0;
   int NotUseful = 0;
   int WhenMapped = 1;
   int Always = 2;
   int IsUnmapped = 0;
   int IsUnviewable = 1;
   int IsViewable = 2;
   int SetModeInsert = 0;
   int SetModeDelete = 1;
   int DestroyAll = 0;
   int RetainPermanent = 1;
   int RetainTemporary = 2;
   int Above = 0;
   int Below = 1;
   int TopIf = 2;
   int BottomIf = 3;
   int Opposite = 4;
   int RaiseLowest = 0;
   int LowerHighest = 1;
   int PropModeReplace = 0;
   int PropModePrepend = 1;
   int PropModeAppend = 2;
   int GXclear = 0;
   int GXand = 1;
   int GXandReverse = 2;
   int GXcopy = 3;
   int GXandInverted = 4;
   int GXnoop = 5;
   int GXxor = 6;
   int GXor = 7;
   int GXnor = 8;
   int GXequiv = 9;
   int GXinvert = 10;
   int GXorReverse = 11;
   int GXcopyInverted = 12;
   int GXorInverted = 13;
   int GXnand = 14;
   int GXset = 15;
   int LineSolid = 0;
   int LineOnOffDash = 1;
   int LineDoubleDash = 2;
   int CapNotLast = 0;
   int CapButt = 1;
   int CapRound = 2;
   int CapProjecting = 3;
   int JoinMiter = 0;
   int JoinRound = 1;
   int JoinBevel = 2;
   int FillSolid = 0;
   int FillTiled = 1;
   int FillStippled = 2;
   int FillOpaqueStippled = 3;
   int EvenOddRule = 0;
   int WindingRule = 1;
   int ClipByChildren = 0;
   int IncludeInferiors = 1;
   int Unsorted = 0;
   int YSorted = 1;
   int YXSorted = 2;
   int YXBanded = 3;
   int CoordModeOrigin = 0;
   int CoordModePrevious = 1;
   int Complex = 0;
   int Nonconvex = 1;
   int Convex = 2;
   int ArcChord = 0;
   int ArcPieSlice = 1;
   int GCFunction = 1;
   int GCPlaneMask = 2;
   int GCForeground = 4;
   int GCBackground = 8;
   int GCLineWidth = 16;
   int GCLineStyle = 32;
   int GCCapStyle = 64;
   int GCJoinStyle = 128;
   int GCFillStyle = 256;
   int GCFillRule = 512;
   int GCTile = 1024;
   int GCStipple = 2048;
   int GCTileStipXOrigin = 4096;
   int GCTileStipYOrigin = 8192;
   int GCFont = 16384;
   int GCSubwindowMode = 32768;
   int GCGraphicsExposures = 65536;
   int GCClipXOrigin = 131072;
   int GCClipYOrigin = 262144;
   int GCClipMask = 524288;
   int GCDashOffset = 1048576;
   int GCDashList = 2097152;
   int GCArcMode = 4194304;
   int GCLastBit = 22;
   int FontLeftToRight = 0;
   int FontRightToLeft = 1;
   int FontChange = 255;
   int XYBitmap = 0;
   int XYPixmap = 1;
   int ZPixmap = 2;
   int AllocNone = 0;
   int AllocAll = 1;
   int DoRed = 1;
   int DoGreen = 2;
   int DoBlue = 4;
   int CursorShape = 0;
   int TileShape = 1;
   int StippleShape = 2;
   int AutoRepeatModeOff = 0;
   int AutoRepeatModeOn = 1;
   int AutoRepeatModeDefault = 2;
   int LedModeOff = 0;
   int LedModeOn = 1;
   int KBKeyClickPercent = 1;
   int KBBellPercent = 2;
   int KBBellPitch = 4;
   int KBBellDuration = 8;
   int KBLed = 16;
   int KBLedMode = 32;
   int KBKey = 64;
   int KBAutoRepeatMode = 128;
   int MappingSuccess = 0;
   int MappingBusy = 1;
   int MappingFailed = 2;
   int MappingModifier = 0;
   int MappingKeyboard = 1;
   int MappingPointer = 2;
   int DontPreferBlanking = 0;
   int PreferBlanking = 1;
   int DefaultBlanking = 2;
   int DisableScreenSaver = 0;
   int DisableScreenInterval = 0;
   int DontAllowExposures = 0;
   int AllowExposures = 1;
   int DefaultExposures = 2;
   int ScreenSaverReset = 0;
   int ScreenSaverActive = 1;
   int HostInsert = 0;
   int HostDelete = 1;
   int EnableAccess = 1;
   int DisableAccess = 0;
   int StaticGray = 0;
   int GrayScale = 1;
   int StaticColor = 2;
   int PseudoColor = 3;
   int TrueColor = 4;
   int DirectColor = 5;
   int LSBFirst = 0;
   int MSBFirst = 1;

   Display XOpenDisplay(String var1);

   int XGetErrorText(Display var1, int var2, byte[] var3, int var4);

   int XDefaultScreen(Display var1);

   Screen DefaultScreenOfDisplay(Display var1);

   Visual XDefaultVisual(Display var1, int var2);

   Colormap XDefaultColormap(Display var1, int var2);

   int XDisplayWidth(Display var1, int var2);

   int XDisplayHeight(Display var1, int var2);

   Window XDefaultRootWindow(Display var1);

   Window XRootWindow(Display var1, int var2);

   int XAllocNamedColor(Display var1, int var2, String var3, Pointer var4, Pointer var5);

   XSizeHints XAllocSizeHints();

   void XSetWMProperties(Display var1, Window var2, String var3, String var4, String[] var5, int var6, XSizeHints var7, Pointer var8, Pointer var9);

   int XSetWMProtocols(Display var1, Window var2, Atom[] var3, int var4);

   int XGetWMProtocols(Display var1, Window var2, PointerByReference var3, IntByReference var4);

   int XFree(Pointer var1);

   Window XCreateSimpleWindow(Display var1, Window var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   Pixmap XCreateBitmapFromData(Display var1, Window var2, Pointer var3, int var4, int var5);

   int XMapWindow(Display var1, Window var2);

   int XMapRaised(Display var1, Window var2);

   int XMapSubwindows(Display var1, Window var2);

   int XFlush(Display var1);

   int XSync(Display var1, boolean var2);

   int XEventsQueued(Display var1, int var2);

   int XPending(Display var1);

   int XUnmapWindow(Display var1, Window var2);

   int XDestroyWindow(Display var1, Window var2);

   int XCloseDisplay(Display var1);

   int XClearWindow(Display var1, Window var2);

   int XClearArea(Display var1, Window var2, int var3, int var4, int var5, int var6, int var7);

   Pixmap XCreatePixmap(Display var1, Drawable var2, int var3, int var4, int var5);

   int XFreePixmap(Display var1, Pixmap var2);

   GC XCreateGC(Display var1, Drawable var2, NativeLong var3, XGCValues var4);

   int XSetFillRule(Display var1, GC var2, int var3);

   int XFreeGC(Display var1, GC var2);

   int XDrawPoint(Display var1, Drawable var2, GC var3, int var4, int var5);

   int XDrawPoints(Display var1, Drawable var2, GC var3, XPoint[] var4, int var5, int var6);

   int XFillRectangle(Display var1, Drawable var2, GC var3, int var4, int var5, int var6, int var7);

   int XFillRectangles(Display var1, Drawable var2, GC var3, XRectangle[] var4, int var5);

   int XSetForeground(Display var1, GC var2, NativeLong var3);

   int XSetBackground(Display var1, GC var2, NativeLong var3);

   int XFillArc(Display var1, Drawable var2, GC var3, int var4, int var5, int var6, int var7, int var8, int var9);

   int XFillPolygon(Display var1, Drawable var2, GC var3, XPoint[] var4, int var5, int var6, int var7);

   int XQueryTree(Display var1, Window var2, WindowByReference var3, WindowByReference var4, PointerByReference var5, IntByReference var6);

   boolean XQueryPointer(Display var1, Window var2, WindowByReference var3, WindowByReference var4, IntByReference var5, IntByReference var6, IntByReference var7, IntByReference var8, IntByReference var9);

   int XGetWindowAttributes(Display var1, Window var2, XWindowAttributes var3);

   int XChangeWindowAttributes(Display var1, Window var2, NativeLong var3, XSetWindowAttributes var4);

   int XGetGeometry(Display var1, Drawable var2, WindowByReference var3, IntByReference var4, IntByReference var5, IntByReference var6, IntByReference var7, IntByReference var8, IntByReference var9);

   boolean XTranslateCoordinates(Display var1, Window var2, Window var3, int var4, int var5, IntByReference var6, IntByReference var7, WindowByReference var8);

   int XSelectInput(Display var1, Window var2, NativeLong var3);

   int XSendEvent(Display var1, Window var2, int var3, NativeLong var4, XEvent var5);

   int XNextEvent(Display var1, XEvent var2);

   int XPeekEvent(Display var1, XEvent var2);

   int XWindowEvent(Display var1, Window var2, NativeLong var3, XEvent var4);

   boolean XCheckWindowEvent(Display var1, Window var2, NativeLong var3, XEvent var4);

   int XMaskEvent(Display var1, NativeLong var2, XEvent var3);

   boolean XCheckMaskEvent(Display var1, NativeLong var2, XEvent var3);

   boolean XCheckTypedEvent(Display var1, int var2, XEvent var3);

   boolean XCheckTypedWindowEvent(Display var1, Window var2, int var3, XEvent var4);

   XWMHints XGetWMHints(Display var1, Window var2);

   int XGetWMName(Display var1, Window var2, XTextProperty var3);

   XVisualInfo XGetVisualInfo(Display var1, NativeLong var2, XVisualInfo var3, IntByReference var4);

   Colormap XCreateColormap(Display var1, Window var2, Visual var3, int var4);

   int XGetWindowProperty(Display var1, Window var2, Atom var3, NativeLong var4, NativeLong var5, boolean var6, Atom var7, AtomByReference var8, IntByReference var9, NativeLongByReference var10, NativeLongByReference var11, PointerByReference var12);

   int XChangeProperty(Display var1, Window var2, Atom var3, Atom var4, int var5, int var6, Pointer var7, int var8);

   int XDeleteProperty(Display var1, Window var2, Atom var3);

   Atom XInternAtom(Display var1, String var2, boolean var3);

   String XGetAtomName(Display var1, Atom var2);

   int XCopyArea(Display var1, Drawable var2, Drawable var3, GC var4, int var5, int var6, int var7, int var8, int var9, int var10);

   XImage XCreateImage(Display var1, Visual var2, int var3, int var4, int var5, Pointer var6, int var7, int var8, int var9, int var10);

   int XPutImage(Display var1, Drawable var2, GC var3, XImage var4, int var5, int var6, int var7, int var8, int var9, int var10);

   int XDestroyImage(XImage var1);

   XErrorHandler XSetErrorHandler(XErrorHandler var1);

   String XKeysymToString(KeySym var1);

   KeySym XStringToKeysym(String var1);

   byte XKeysymToKeycode(Display var1, KeySym var2);

   KeySym XKeycodeToKeysym(Display var1, byte var2, int var3);

   int XGrabKey(Display var1, int var2, int var3, Window var4, int var5, int var6, int var7);

   int XUngrabKey(Display var1, int var2, int var3, Window var4);

   int XGrabKeyboard(Display var1, Window var2, int var3, int var4, int var5, NativeLong var6);

   int XUngrabKeyboard(Display var1, NativeLong var2);

   int XFetchName(Display var1, Window var2, PointerByReference var3);

   int XChangeKeyboardMapping(Display var1, int var2, int var3, KeySym[] var4, int var5);

   KeySym XGetKeyboardMapping(Display var1, byte var2, int var3, IntByReference var4);

   int XDisplayKeycodes(Display var1, IntByReference var2, IntByReference var3);

   int XSetModifierMapping(Display var1, XModifierKeymapRef var2);

   XModifierKeymapRef XGetModifierMapping(Display var1);

   XModifierKeymapRef XNewModifiermap(int var1);

   XModifierKeymapRef XInsertModifiermapEntry(XModifierKeymapRef var1, byte var2, int var3);

   XModifierKeymapRef XDeleteModifiermapEntry(XModifierKeymapRef var1, byte var2, int var3);

   int XFreeModifiermap(XModifierKeymapRef var1);

   int XChangeKeyboardControl(Display var1, NativeLong var2, XKeyboardControlRef var3);

   int XGetKeyboardControl(Display var1, XKeyboardStateRef var2);

   int XAutoRepeatOn(Display var1);

   int XAutoRepeatOff(Display var1);

   int XBell(Display var1, int var2);

   int XQueryKeymap(Display var1, byte[] var2);

   @Structure.FieldOrder({"key_click_percent", "bell_percent", "bell_pitch", "bell_duration", "led_mask", "global_auto_repeat", "auto_repeats"})
   public static class XKeyboardStateRef extends Structure implements Structure.ByReference {
      public int key_click_percent;
      public int bell_percent;
      public int bell_pitch;
      public int bell_duration;
      public NativeLong led_mask;
      public int global_auto_repeat;
      public byte[] auto_repeats = new byte[32];

      public String toString() {
         return "XKeyboardStateByReference{key_click_percent=" + this.key_click_percent + ", bell_percent=" + this.bell_percent + ", bell_pitch=" + this.bell_pitch + ", bell_duration=" + this.bell_duration + ", led_mask=" + this.led_mask + ", global_auto_repeat=" + this.global_auto_repeat + ", auto_repeats=" + this.auto_repeats + '}';
      }
   }

   @Structure.FieldOrder({"key_click_percent", "bell_percent", "bell_pitch", "bell_duration", "led", "led_mode", "key", "auto_repeat_mode"})
   public static class XKeyboardControlRef extends Structure implements Structure.ByReference {
      public int key_click_percent;
      public int bell_percent;
      public int bell_pitch;
      public int bell_duration;
      public int led;
      public int led_mode;
      public int key;
      public int auto_repeat_mode;

      public String toString() {
         return "XKeyboardControlByReference{key_click_percent=" + this.key_click_percent + ", bell_percent=" + this.bell_percent + ", bell_pitch=" + this.bell_pitch + ", bell_duration=" + this.bell_duration + ", led=" + this.led + ", led_mode=" + this.led_mode + ", key=" + this.key + ", auto_repeat_mode=" + this.auto_repeat_mode + '}';
      }
   }

   @Structure.FieldOrder({"max_keypermod", "modifiermap"})
   public static class XModifierKeymapRef extends Structure implements Structure.ByReference {
      public int max_keypermod;
      public Pointer modifiermap;
   }

   public interface XErrorHandler extends Callback {
      int apply(Display var1, XErrorEvent var2);
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "key_vector"})
   public static class XKeymapEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public byte[] key_vector = new byte[32];
   }

   @Structure.FieldOrder({"type", "display", "serial", "error_code", "request_code", "minor_code", "resourceid"})
   public static class XErrorEvent extends Structure {
      public int type;
      public Display display;
      public NativeLong serial;
      public byte error_code;
      public byte request_code;
      public byte minor_code;
      public XID resourceid;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "request", "first_keycode", "count"})
   public static class XMappingEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public int request;
      public int first_keycode;
      public int count;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "colormap", "c_new", "state"})
   public static class XColormapEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public Colormap colormap;
      public int c_new;
      public int state;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "requestor", "selection", "target", "property", "time"})
   public static class XSelectionEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window requestor;
      public Atom selection;
      public Atom target;
      public Atom property;
      public NativeLong time;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "owner", "requestor", "selection", "target", "property", "time"})
   public static class XSelectionRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window owner;
      public Window requestor;
      public Atom selection;
      public Atom target;
      public Atom property;
      public NativeLong time;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "selection", "time"})
   public static class XSelectionClearEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public Atom selection;
      public NativeLong time;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "atom", "time", "state"})
   public static class XPropertyEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public Atom atom;
      public NativeLong time;
      public int state;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "parent", "window", "place"})
   public static class XCirculateRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window parent;
      public Window window;
      public int place;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "place"})
   public static class XCirculateEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window event;
      public Window window;
      public int place;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "parent", "window", "x", "y", "width", "height", "border_width", "above", "detail", "value_mask"})
   public static class XConfigureRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window parent;
      public Window window;
      public int x;
      public int y;
      public int width;
      public int height;
      public int border_width;
      public Window above;
      public int detail;
      public NativeLong value_mask;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "width", "height"})
   public static class XResizeRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public int width;
      public int height;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "x", "y"})
   public static class XGravityEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window event;
      public Window window;
      public int x;
      public int y;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "x", "y", "width", "height", "border_width", "above", "override_redirect"})
   public static class XConfigureEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window event;
      public Window window;
      public int x;
      public int y;
      public int width;
      public int height;
      public int border_width;
      public Window above;
      public int override_redirect;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "parent", "x", "y", "override_redirect"})
   public static class XReparentEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window event;
      public Window window;
      public Window parent;
      public int x;
      public int y;
      public int override_redirect;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "parent", "window"})
   public static class XMapRequestEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window parent;
      public Window window;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "override_redirect"})
   public static class XMapEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window event;
      public Window window;
      public int override_redirect;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "from_configure"})
   public static class XUnmapEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window event;
      public Window window;
      public int from_configure;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window"})
   public static class XDestroyWindowEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window event;
      public Window window;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "parent", "window", "x", "y", "width", "height", "border_width", "override_redirect"})
   public static class XCreateWindowEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window parent;
      public Window window;
      public int x;
      public int y;
      public int width;
      public int height;
      public int border_width;
      public int override_redirect;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "state"})
   public static class XVisibilityEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public int state;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "drawable", "major_code", "minor_code"})
   public static class XNoExposeEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Drawable drawable;
      public int major_code;
      public int minor_code;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "drawable", "x", "y", "width", "height", "count", "major_code", "minor_code"})
   public static class XGraphicsExposeEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Drawable drawable;
      public int x;
      public int y;
      public int width;
      public int height;
      public int count;
      public int major_code;
      public int minor_code;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "x", "y", "width", "height", "count"})
   public static class XExposeEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public int x;
      public int y;
      public int width;
      public int height;
      public int count;
   }

   public static class XFocusOutEvent extends XFocusChangeEvent {
   }

   public static class XFocusInEvent extends XFocusChangeEvent {
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "mode", "detail"})
   public static class XFocusChangeEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public int mode;
      public int detail;
   }

   public static class XLeaveWindowEvent extends XCrossingEvent {
   }

   public static class XEnterWindowEvent extends XCrossingEvent {
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "root", "subwindow", "time", "x", "y", "x_root", "y_root", "mode", "detail", "same_screen", "focus", "state"})
   public static class XCrossingEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public Window root;
      public Window subwindow;
      public NativeLong time;
      public int x;
      public int y;
      public int x_root;
      public int y_root;
      public int mode;
      public int detail;
      public int same_screen;
      public int focus;
      public int state;
   }

   public static class XPointerMovedEvent extends XMotionEvent {
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "root", "subwindow", "time", "x", "y", "x_root", "y_root", "state", "is_hint", "same_screen"})
   public static class XMotionEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public Window root;
      public Window subwindow;
      public NativeLong time;
      public int x;
      public int y;
      public int x_root;
      public int y_root;
      public int state;
      public byte is_hint;
      public int same_screen;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "message_type", "format", "data"})
   public static class XClientMessageEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public Atom message_type;
      public int format;
      public Data data;

      public static class Data extends Union {
         public byte[] b = new byte[20];
         public short[] s = new short[10];
         public NativeLong[] l = new NativeLong[5];
      }
   }

   public static class XButtonReleasedEvent extends XButtonEvent {
   }

   public static class XButtonPressedEvent extends XButtonEvent {
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "root", "subwindow", "time", "x", "y", "x_root", "y_root", "state", "button", "same_screen"})
   public static class XButtonEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public Window root;
      public Window subwindow;
      public NativeLong time;
      public int x;
      public int y;
      public int x_root;
      public int y_root;
      public int state;
      public int button;
      public int same_screen;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "root", "subwindow", "time", "x", "y", "x_root", "y_root", "state", "keycode", "same_screen"})
   public static class XKeyEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
      public Window root;
      public Window subwindow;
      public NativeLong time;
      public int x;
      public int y;
      public int x_root;
      public int y_root;
      public int state;
      public int keycode;
      public int same_screen;
   }

   @Structure.FieldOrder({"type", "serial", "send_event", "display", "window"})
   public static class XAnyEvent extends Structure {
      public int type;
      public NativeLong serial;
      public int send_event;
      public Display display;
      public Window window;
   }

   public static class XEvent extends Union {
      public int type;
      public XAnyEvent xany;
      public XKeyEvent xkey;
      public XButtonEvent xbutton;
      public XMotionEvent xmotion;
      public XCrossingEvent xcrossing;
      public XFocusChangeEvent xfocus;
      public XExposeEvent xexpose;
      public XGraphicsExposeEvent xgraphicsexpose;
      public XNoExposeEvent xnoexpose;
      public XVisibilityEvent xvisibility;
      public XCreateWindowEvent xcreatewindow;
      public XDestroyWindowEvent xdestroywindow;
      public XUnmapEvent xunmap;
      public XMapEvent xmap;
      public XMapRequestEvent xmaprequest;
      public XReparentEvent xreparent;
      public XConfigureEvent xconfigure;
      public XGravityEvent xgravity;
      public XResizeRequestEvent xresizerequest;
      public XConfigureRequestEvent xconfigurerequest;
      public XCirculateEvent xcirculate;
      public XCirculateRequestEvent xcirculaterequest;
      public XPropertyEvent xproperty;
      public XSelectionClearEvent xselectionclear;
      public XSelectionRequestEvent xselectionrequest;
      public XSelectionEvent xselection;
      public XColormapEvent xcolormap;
      public XClientMessageEvent xclient;
      public XMappingEvent xmapping;
      public XErrorEvent xerror;
      public XKeymapEvent xkeymap;
      public NativeLong[] pad = new NativeLong[24];
   }

   @Structure.FieldOrder({"function", "plane_mask", "foreground", "background", "line_width", "line_style", "cap_style", "join_style", "fill_style", "fill_rule", "arc_mode", "tile", "stipple", "ts_x_origin", "ts_y_origin", "font", "subwindow_mode", "graphics_exposures", "clip_x_origin", "clip_y_origin", "clip_mask", "dash_offset", "dashes"})
   public static class XGCValues extends Structure {
      public int function;
      public NativeLong plane_mask;
      public NativeLong foreground;
      public NativeLong background;
      public int line_width;
      public int line_style;
      public int cap_style;
      public int join_style;
      public int fill_style;
      public int fill_rule;
      public int arc_mode;
      public Pixmap tile;
      public Pixmap stipple;
      public int ts_x_origin;
      public int ts_y_origin;
      public Font font;
      public int subwindow_mode;
      public boolean graphics_exposures;
      public int clip_x_origin;
      public int clip_y_origin;
      public Pixmap clip_mask;
      public int dash_offset;
      public byte dashes;
   }

   @Structure.FieldOrder({"x", "y", "width", "height"})
   public static class XRectangle extends Structure {
      public short x;
      public short y;
      public short width;
      public short height;

      public XRectangle() {
         this((short)0, (short)0, (short)0, (short)0);
      }

      public XRectangle(short x, short y, short width, short height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }

   @Structure.FieldOrder({"x", "y"})
   public static class XPoint extends Structure {
      public short x;
      public short y;

      public XPoint() {
         this((short)0, (short)0);
      }

      public XPoint(short x, short y) {
         this.x = x;
         this.y = y;
      }
   }

   @Structure.FieldOrder({"visual", "visualid", "screen", "depth", "c_class", "red_mask", "green_mask", "blue_mask", "colormap_size", "bits_per_rgb"})
   public static class XVisualInfo extends Structure {
      public Visual visual;
      public VisualID visualid;
      public int screen;
      public int depth;
      public int c_class;
      public NativeLong red_mask;
      public NativeLong green_mask;
      public NativeLong blue_mask;
      public int colormap_size;
      public int bits_per_rgb;
   }

   @Structure.FieldOrder({"background_pixmap", "background_pixel", "border_pixmap", "border_pixel", "bit_gravity", "win_gravity", "backing_store", "backing_planes", "backing_pixel", "save_under", "event_mask", "do_not_propagate_mask", "override_redirect", "colormap", "cursor"})
   public static class XSetWindowAttributes extends Structure {
      public Pixmap background_pixmap;
      public NativeLong background_pixel;
      public Pixmap border_pixmap;
      public NativeLong border_pixel;
      public int bit_gravity;
      public int win_gravity;
      public int backing_store;
      public NativeLong backing_planes;
      public NativeLong backing_pixel;
      public boolean save_under;
      public NativeLong event_mask;
      public NativeLong do_not_propagate_mask;
      public boolean override_redirect;
      public Colormap colormap;
      public Cursor cursor;
   }

   @Structure.FieldOrder({"x", "y", "width", "height", "border_width", "depth", "visual", "root", "c_class", "bit_gravity", "win_gravity", "backing_store", "backing_planes", "backing_pixel", "save_under", "colormap", "map_installed", "map_state", "all_event_masks", "your_event_mask", "do_not_propagate_mask", "override_redirect", "screen"})
   public static class XWindowAttributes extends Structure {
      public int x;
      public int y;
      public int width;
      public int height;
      public int border_width;
      public int depth;
      public Visual visual;
      public Window root;
      public int c_class;
      public int bit_gravity;
      public int win_gravity;
      public int backing_store;
      public NativeLong backing_planes;
      public NativeLong backing_pixel;
      public boolean save_under;
      public Colormap colormap;
      public boolean map_installed;
      public int map_state;
      public NativeLong all_event_masks;
      public NativeLong your_event_mask;
      public NativeLong do_not_propagate_mask;
      public boolean override_redirect;
      public Screen screen;
   }

   @Structure.FieldOrder({"flags", "x", "y", "width", "height", "min_width", "min_height", "max_width", "max_height", "width_inc", "height_inc", "min_aspect", "max_aspect", "base_width", "base_height", "win_gravity"})
   public static class XSizeHints extends Structure {
      public NativeLong flags;
      public int x;
      public int y;
      public int width;
      public int height;
      public int min_width;
      public int min_height;
      public int max_width;
      public int max_height;
      public int width_inc;
      public int height_inc;
      public Aspect min_aspect;
      public Aspect max_aspect;
      public int base_width;
      public int base_height;
      public int win_gravity;

      @Structure.FieldOrder({"x", "y"})
      public static class Aspect extends Structure {
         public int x;
         public int y;
      }
   }

   @Structure.FieldOrder({"value", "encoding", "format", "nitems"})
   public static class XTextProperty extends Structure {
      public String value;
      public Atom encoding;
      public int format;
      public NativeLong nitems;
   }

   @Structure.FieldOrder({"flags", "input", "initial_state", "icon_pixmap", "icon_window", "icon_x", "icon_y", "icon_mask", "window_group"})
   public static class XWMHints extends Structure {
      public NativeLong flags;
      public boolean input;
      public int initial_state;
      public Pixmap icon_pixmap;
      public Window icon_window;
      public int icon_x;
      public int icon_y;
      public Pixmap icon_mask;
      public XID window_group;
   }

   @Structure.FieldOrder({"device_id", "num_classes", "classes"})
   public static class XDeviceByReference extends Structure implements Structure.ByReference {
      public XID device_id;
      public int num_classes;
      public XInputClassInfoByReference classes;
   }

   @Structure.FieldOrder({"input_class", "event_type_base"})
   public static class XInputClassInfoByReference extends Structure implements Structure.ByReference {
      public byte input_class;
      public byte event_type_base;
   }

   public interface XTest extends Library {
      XTest INSTANCE = (XTest)Native.load("Xtst", XTest.class);

      boolean XTestQueryExtension(Display var1, IntByReference var2, IntByReference var3, IntByReference var4, IntByReference var5);

      boolean XTestCompareCursorWithWindow(Display var1, Window var2, Cursor var3);

      boolean XTestCompareCurrentCursorWithWindow(Display var1, Window var2);

      int XTestFakeKeyEvent(Display var1, int var2, boolean var3, NativeLong var4);

      int XTestFakeButtonEvent(Display var1, int var2, boolean var3, NativeLong var4);

      int XTestFakeMotionEvent(Display var1, int var2, int var3, int var4, NativeLong var5);

      int XTestFakeRelativeMotionEvent(Display var1, int var2, int var3, NativeLong var4);

      int XTestFakeDeviceKeyEvent(Display var1, XDeviceByReference var2, int var3, boolean var4, IntByReference var5, int var6, NativeLong var7);

      int XTestFakeDeviceButtonEvent(Display var1, XDeviceByReference var2, int var3, boolean var4, IntByReference var5, int var6, NativeLong var7);

      int XTestFakeProximityEvent(Display var1, XDeviceByReference var2, boolean var3, IntByReference var4, int var5, NativeLong var6);

      int XTestFakeDeviceMotionEvent(Display var1, XDeviceByReference var2, boolean var3, int var4, IntByReference var5, int var6, NativeLong var7);

      int XTestGrabControl(Display var1, boolean var2);

      void XTestSetVisualIDOfVisual(Visual var1, VisualID var2);

      int XTestDiscard(Display var1);
   }

   public interface Xevie extends Library {
      Xevie INSTANCE = (Xevie)Native.load("Xevie", Xevie.class);
      int XEVIE_UNMODIFIED = 0;
      int XEVIE_MODIFIED = 1;

      boolean XevieQueryVersion(Display var1, IntByReference var2, IntByReference var3);

      int XevieStart(Display var1);

      int XevieEnd(Display var1);

      int XevieSendEvent(Display var1, XEvent var2, int var3);

      int XevieSelectInput(Display var1, NativeLong var2);
   }

   public interface Xrender extends Library {
      Xrender INSTANCE = (Xrender)Native.load("Xrender", Xrender.class);
      int PictTypeIndexed = 0;
      int PictTypeDirect = 1;

      XRenderPictFormat XRenderFindVisualFormat(Display var1, Visual var2);

      @Structure.FieldOrder({"id", "type", "depth", "direct", "colormap"})
      public static class XRenderPictFormat extends Structure {
         public PictFormat id;
         public int type;
         public int depth;
         public XRenderDirectFormat direct;
         public Colormap colormap;
      }

      public static class PictFormat extends XID {
         private static final long serialVersionUID = 1L;
         public static final PictFormat None = null;

         public PictFormat(long value) {
            super(value);
         }

         public PictFormat() {
            this(0L);
         }

         public Object fromNative(Object nativeValue, FromNativeContext context) {
            return this.isNone(nativeValue) ? None : new PictFormat(((Number)nativeValue).longValue());
         }
      }

      @Structure.FieldOrder({"red", "redMask", "green", "greenMask", "blue", "blueMask", "alpha", "alphaMask"})
      public static class XRenderDirectFormat extends Structure {
         public short red;
         public short redMask;
         public short green;
         public short greenMask;
         public short blue;
         public short blueMask;
         public short alpha;
         public short alphaMask;
      }
   }

   public interface Xext extends Library {
      Xext INSTANCE = (Xext)Native.load("Xext", Xext.class);
      int ShapeBounding = 0;
      int ShapeClip = 1;
      int ShapeInput = 2;
      int ShapeSet = 0;
      int ShapeUnion = 1;
      int ShapeIntersect = 2;
      int ShapeSubtract = 3;
      int ShapeInvert = 4;

      void XShapeCombineMask(Display var1, Window var2, int var3, int var4, int var5, Pixmap var6, int var7);
   }

   public static class XImage extends PointerType {
   }

   public static class GC extends PointerType {
   }

   public static class Screen extends PointerType {
   }

   public static class Visual extends PointerType {
      public VisualID getVisualID() {
         if (this.getPointer() != null) {
            return new VisualID(this.getPointer().getNativeLong((long)Native.POINTER_SIZE).longValue());
         } else {
            throw new IllegalStateException("Attempting to retrieve VisualID from a null Visual");
         }
      }

      public String toString() {
         return "Visual: VisualID=0x" + Long.toHexString(this.getVisualID().longValue());
      }
   }

   public static class Display extends PointerType {
   }

   public static class Pixmap extends Drawable {
      private static final long serialVersionUID = 1L;
      public static final Pixmap None = null;

      public Pixmap() {
      }

      public Pixmap(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new Pixmap(((Number)nativeValue).longValue());
      }
   }

   public static class WindowByReference extends ByReference {
      public WindowByReference() {
         super(X11.XID.SIZE);
      }

      public Window getValue() {
         NativeLong value = this.getPointer().getNativeLong(0L);
         return value.longValue() == 0L ? X11.Window.None : new Window(value.longValue());
      }
   }

   public static class Window extends Drawable {
      private static final long serialVersionUID = 1L;
      public static final Window None = null;

      public Window() {
      }

      public Window(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new Window(((Number)nativeValue).longValue());
      }
   }

   public static class Drawable extends XID {
      private static final long serialVersionUID = 1L;
      public static final Drawable None = null;

      public Drawable() {
      }

      public Drawable(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new Drawable(((Number)nativeValue).longValue());
      }
   }

   public static class KeySym extends XID {
      private static final long serialVersionUID = 1L;
      public static final KeySym None = null;

      public KeySym() {
      }

      public KeySym(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new KeySym(((Number)nativeValue).longValue());
      }
   }

   public static class Cursor extends XID {
      private static final long serialVersionUID = 1L;
      public static final Cursor None = null;

      public Cursor() {
      }

      public Cursor(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new Cursor(((Number)nativeValue).longValue());
      }
   }

   public static class Font extends XID {
      private static final long serialVersionUID = 1L;
      public static final Font None = null;

      public Font() {
      }

      public Font(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new Font(((Number)nativeValue).longValue());
      }
   }

   public static class Colormap extends XID {
      private static final long serialVersionUID = 1L;
      public static final Colormap None = null;

      public Colormap() {
      }

      public Colormap(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new Colormap(((Number)nativeValue).longValue());
      }
   }

   public static class AtomByReference extends ByReference {
      public AtomByReference() {
         super(X11.XID.SIZE);
      }

      public Atom getValue() {
         NativeLong value = this.getPointer().getNativeLong(0L);
         return (Atom)(new Atom()).fromNative(value, (FromNativeContext)null);
      }
   }

   public static class Atom extends XID {
      private static final long serialVersionUID = 1L;
      public static final Atom None = null;

      public Atom() {
      }

      public Atom(long id) {
         super(id);
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         long value = ((Number)nativeValue).longValue();
         if (value <= 2147483647L) {
            switch ((int)value) {
               case 0:
                  return None;
               case 1:
                  return X11.XA_PRIMARY;
               case 2:
                  return X11.XA_SECONDARY;
               case 3:
                  return X11.XA_ARC;
               case 4:
                  return X11.XA_ATOM;
               case 5:
                  return X11.XA_BITMAP;
               case 6:
                  return X11.XA_CARDINAL;
               case 7:
                  return X11.XA_COLORMAP;
               case 8:
                  return X11.XA_CURSOR;
               case 9:
                  return X11.XA_CUT_BUFFER0;
               case 10:
                  return X11.XA_CUT_BUFFER1;
               case 11:
                  return X11.XA_CUT_BUFFER2;
               case 12:
                  return X11.XA_CUT_BUFFER3;
               case 13:
                  return X11.XA_CUT_BUFFER4;
               case 14:
                  return X11.XA_CUT_BUFFER5;
               case 15:
                  return X11.XA_CUT_BUFFER6;
               case 16:
                  return X11.XA_CUT_BUFFER7;
               case 17:
                  return X11.XA_DRAWABLE;
               case 18:
                  return X11.XA_FONT;
               case 19:
                  return X11.XA_INTEGER;
               case 20:
                  return X11.XA_PIXMAP;
               case 21:
                  return X11.XA_POINT;
               case 22:
                  return X11.XA_RECTANGLE;
               case 23:
                  return X11.XA_RESOURCE_MANAGER;
               case 24:
                  return X11.XA_RGB_COLOR_MAP;
               case 25:
                  return X11.XA_RGB_BEST_MAP;
               case 26:
                  return X11.XA_RGB_BLUE_MAP;
               case 27:
                  return X11.XA_RGB_DEFAULT_MAP;
               case 28:
                  return X11.XA_RGB_GRAY_MAP;
               case 29:
                  return X11.XA_RGB_GREEN_MAP;
               case 30:
                  return X11.XA_RGB_RED_MAP;
               case 31:
                  return X11.XA_STRING;
               case 32:
                  return X11.XA_VISUALID;
               case 33:
                  return X11.XA_WINDOW;
               case 34:
                  return X11.XA_WM_COMMAND;
               case 35:
                  return X11.XA_WM_HINTS;
               case 36:
                  return X11.XA_WM_CLIENT_MACHINE;
               case 37:
                  return X11.XA_WM_ICON_NAME;
               case 38:
                  return X11.XA_WM_ICON_SIZE;
               case 39:
                  return X11.XA_WM_NAME;
               case 40:
                  return X11.XA_WM_NORMAL_HINTS;
               case 41:
                  return X11.XA_WM_SIZE_HINTS;
               case 42:
                  return X11.XA_WM_ZOOM_HINTS;
               case 43:
                  return X11.XA_MIN_SPACE;
               case 44:
                  return X11.XA_NORM_SPACE;
               case 45:
                  return X11.XA_MAX_SPACE;
               case 46:
                  return X11.XA_END_SPACE;
               case 47:
                  return X11.XA_SUPERSCRIPT_X;
               case 48:
                  return X11.XA_SUPERSCRIPT_Y;
               case 49:
                  return X11.XA_SUBSCRIPT_X;
               case 50:
                  return X11.XA_SUBSCRIPT_Y;
               case 51:
                  return X11.XA_UNDERLINE_POSITION;
               case 52:
                  return X11.XA_UNDERLINE_THICKNESS;
               case 53:
                  return X11.XA_STRIKEOUT_ASCENT;
               case 54:
                  return X11.XA_STRIKEOUT_DESCENT;
               case 55:
                  return X11.XA_ITALIC_ANGLE;
               case 56:
                  return X11.XA_X_HEIGHT;
               case 57:
                  return X11.XA_QUAD_WIDTH;
               case 58:
                  return X11.XA_WEIGHT;
               case 59:
                  return X11.XA_POINT_SIZE;
               case 60:
                  return X11.XA_RESOLUTION;
               case 61:
                  return X11.XA_COPYRIGHT;
               case 62:
                  return X11.XA_NOTICE;
               case 63:
                  return X11.XA_FONT_NAME;
               case 64:
                  return X11.XA_FAMILY_NAME;
               case 65:
                  return X11.XA_FULL_NAME;
               case 66:
                  return X11.XA_CAP_HEIGHT;
               case 67:
                  return X11.XA_WM_CLASS;
               case 68:
                  return X11.XA_WM_TRANSIENT_FOR;
            }
         }

         return new Atom(value);
      }
   }

   public static class XID extends NativeLong {
      private static final long serialVersionUID = 1L;
      public static final XID None = null;

      public XID() {
         this(0L);
      }

      public XID(long id) {
         super(id, true);
      }

      protected boolean isNone(Object o) {
         return o == null || o instanceof Number && ((Number)o).longValue() == 0L;
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new XID(((Number)nativeValue).longValue());
      }

      public String toString() {
         return "0x" + Long.toHexString(this.longValue());
      }
   }

   public static class VisualID extends NativeLong {
      private static final long serialVersionUID = 1L;
      public static final VisualID None = null;

      public VisualID() {
         this(0L);
      }

      public VisualID(long value) {
         super(value, true);
      }

      protected boolean isNone(Object o) {
         return o == null || o instanceof Number && ((Number)o).longValue() == 0L;
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         return this.isNone(nativeValue) ? None : new VisualID(((Number)nativeValue).longValue());
      }
   }
}
