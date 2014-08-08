// fFLASH V4, (C) May 14, 2005, Copyright IBM Corporation - All Rights Reserved - Licensed Materials - Property of IBM
// $Id: SystemUtils.java,v 1.6 2012/02/22 21:57:22 localh Exp $

package com.ibm.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.security.*;
import java.text.*;

import javax.swing.*;

/** Class SystemUtils provides a set of useful methods for dealing with external processes.
     - launch(...): launch an external process
     - parseProcessOutput(): collects the stderr and stdout of a process and sends it to the stdout of the process in which it lives.<br>
     - parseProcessOutput(): invoke with: <pre> int rc = SystemUtils.parseProcessOutput(Runtime.getRuntime().exec(cmd), ...); </pre>
    In addition it provides a simple API for measuring and recording CPU usage. See individual methods for details. */

public final class SystemUtils {

  private static final Runtime      RUNTIME = Runtime.getRuntime();

  private static Properties  envVars = new Properties();
  
  private static       String cygstartPath = ""; // path to CygWin's cygutils' "cygstart" binary

  private static       boolean  debug;
  public  static       boolean getDebug () { return debug; }
  public  static       void    setDebug (boolean b) { debug = b; }

  private static       boolean  ignoreWarnings;
  public  static       boolean getIgnoreWarnings () { return ignoreWarnings; }
  public  static       void    setIgnoreWarnings (boolean b) { ignoreWarnings = b; }

  private SystemUtils () { /* empty private impl. prevents subclassing */ }

  public static boolean isNTServiceIsRunning (String serviceName) {
    ArrayList<String> serviceList = getRunningNTServices();
    if (serviceList == null) return false; // not NT kernel?
    for (String svc : serviceList) {
      if (serviceName.equals(svc.trim())) return true; // service running
    }
    return false;
  }

  public static int ensureNTServiceIsRunning (String serviceName, String service) throws Exception {
    ArrayList<String> serviceList = getRunningNTServices();
    if (serviceList == null) return -1; // not NT kernel?
    for (String svc : serviceList) {
      if (serviceName.equals(svc.trim())) return 0; // service already running
    }
    Process process = RUNTIME.exec(comSpec + "net start \"" + service + "\"");
    process.waitFor(); // wait for the process to complete
    int rc = process.exitValue(); // pick up its return code
    boolean success = (rc == 0);
    if (success) System.out.println("Successfully started service '" + serviceName + "'.");
    return (success ? 1 : -1);
  }

  private static ArrayList<String> getRunningNTServices () {
    return (isWinPlatform() && !isWin9X) ? executeSystemCommand("net start") : null; // what about WinME?
  }

  public static ArrayList<String> executeShellCommand (String cmd) {
    return (isCygWin() || isUnix()) ? executeSystemCommand("sh " + cmd) : null;
  }

  /** Execute the system command 'cmd' and fill an ArrayList with the results. */
  public static ArrayList<String> executeSystemCommand (String cmd) {
    if (debug) System.out.println("cmd: " + cmd);
    ArrayList<String> list = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(RUNTIME.exec(/*comSpec +*/ cmd).getInputStream()))) {
      for (String line = null; (line = br.readLine()) != null;) { if (debug) System.out.println(line); list.add(line); }
    } catch (IOException e) { e.printStackTrace(); }
    return list;
  }

  /** Display default email client to type and send an email. */
  public static void sendMail () throws IOException {
    if (isWinPlatform()) RUNTIME.exec("rundll32 url.dll,FileProtocolHandler mailto:hannes@2horns.com?subject=Houdy");
  }

  /** Aggressively free up everything currently possible. */
  public static void fullGC () { fullGC(true); }
  public static void fullGC (boolean verbose) {
    if (verbose) System.out.print(new Date().toString() + ' ' + String.valueOf((RUNTIME.totalMemory() - RUNTIME.freeMemory()) / 1024L) + "Kb used");
    long isFree = RUNTIME.freeMemory();
    long wasFree;
    do {
      wasFree = isFree;
      RUNTIME.runFinalization(); RUNTIME.gc();
      isFree = RUNTIME.freeMemory();
    } while (isFree > wasFree);
    if (verbose) System.out.println(" --> " + String.valueOf((RUNTIME.totalMemory() - RUNTIME.freeMemory()) / 1024L) + "Kb used");
  }

  public static String memoryToString () {
    DecimalFormat fmt = new DecimalFormat("0.0");
    return "Memory: " + fmt.format(RUNTIME.maxMemory()   / 1048576D) + "MByte maximum, "
                      + fmt.format(RUNTIME.totalMemory() / 1048576D) + "MByte total, "
                      + fmt.format(RUNTIME.totalMemory() / 1048576D) + "MByte free";
  }

  public static void quit (int exitCode) { // redirect to System.exit() as to reduce warnings by static code analysis tools
    System.exit(exitCode);
  }

  // === CPU usage related stuff ======================================================================================================================================

  /** Minimum time difference [in milliseconds] enforced for the inputs into getProcessCPUUsage(long[],long[]). The motivation for this restriction
      is the fact that <code>System.currentTimeMillis()</code> on some systems has a low resolution (e.g., 10ms on win32). The current value is 100 ms. */
  public static final int MIN_ELAPSED_TIME = 100;

  /** Creates a CPU usage data snapshot by associating CPU time used with system time. The resulting data can be fed into getProcessCPUUsage(long[],long[]).
      @return long[2]  [0] current system time stamp, [1] process CPU time */
  public static long[] makeCPUUsageSnapshot () {
    return new long[] { System.currentTimeMillis(), getProcessCPUTime() };
  }

  /** Computes CPU usage (fraction of 1.0) between <code>start[1]</code> and <code>end[1]</code> time points [1.0 corresponds to 100% utilization of all processors].
      @throws IllegalArgumentException if start and end time points are less than #MIN_ELAPSED_TIME ms apart.
      @throws IllegalArgumentException if either argument is null
      @param  start,end   long[2]: [0] system time stamp, [1] process CPU time (as returned by makeCPUUsageSnapshot()). */
  public static double getProcessCPUUsage (long[] start, long[] end) {
    if (start == null) throw new IllegalArgumentException("null input: start");
    if (end   == null) throw new IllegalArgumentException("null input: end");
  //if (end[0] < start[0] + MIN_ELAPSED_TIME) throw new IllegalArgumentException("end time must be at least " + MIN_ELAPSED_TIME + " ms later than start time");
    end[0] = Math.max(end[0], start[0] + MIN_ELAPSED_TIME);

    return ((double) (end[1] - start[1])) / (double) (end[0] - start[0]);
  }

  /** Returns the PID of the current process. The result is useful when you need to integrate a Java app with external tools. */
  public static native int getProcessID ();

  /** Returns CPU (kernel + user) time used by the current process [in milliseconds]. The returned value is adjusted for the number of processors in the system. */
  public static native long getProcessCPUTime ();

  /** Returns CPU usage (fraction of 1.0) so far by the current process. This is a total for all processors since the process creation time. */
  //public static native double getProcessCPUUsage ();

  // === OS related stuff ==========================================================================================================================================

  private static boolean isWin7, isVista, isWinXP, isWin2k, isWinNT, isWin9X, isCygWin;
  private static boolean isMac, isSolaris, isLinux, isUnix;

  private static final String SYSLIB = "syslib";

  private static String  comSpec = ""; // the Windows / DOS command line processor, e.g. C:\WINDOWS\system32\cmd.exe

  private static boolean startsWithIgnoreCase (String string, String prefix) {
	if (string == null || string.isEmpty() || prefix == null) return false;
	int l2 = prefix.length();
	return (string.length() >= l2 && string.regionMatches(true, 0, prefix, 0, l2));
  }

  
  /** Try to determine whether this application is running under Windows or some other platform by examining the "os.name" property. */
  static {
    String os = System.getProperty("os.name");
  //String version = System.getProperty("os.version"); // for Win7, reports "6.0" on JDK7, should be "6.1"; for Win8, reports "6.2" as of JDK7u17

    if      (SystemUtils.startsWithIgnoreCase(os, "windows 7"))     isWin7    = true; // reports "Windows Vista" on JDK7
    else if (SystemUtils.startsWithIgnoreCase(os, "windows 8"))     isWin7    = true; // reports "Windows 8" as of JDK7u17
    else if (SystemUtils.startsWithIgnoreCase(os, "windows vista")) isVista   = true;
    else if (SystemUtils.startsWithIgnoreCase(os, "windows xp"))    isWinXP   = true;
    else if (SystemUtils.startsWithIgnoreCase(os, "windows 2000"))  isWin2k   = true;
    else if (SystemUtils.startsWithIgnoreCase(os, "windows nt"))    isWinNT   = true;
    else if (SystemUtils.startsWithIgnoreCase(os, "windows"))       isWin9X   = true; // win95 or win98 (what about WinME?)
    else if (SystemUtils.startsWithIgnoreCase(os, "mac"))           isMac     = true;
    else if (SystemUtils.startsWithIgnoreCase(os, "so"))            isSolaris = true; // sunos or solaris
    else if (os.equalsIgnoreCase("linux"))                          isLinux   = true;
    else                                                            isUnix    = true; // assume UNIX, e.g. AIX, HP-UX, IRIX

    String osarch = System.getProperty("os.arch");
    String arch = (osarch != null && osarch.contains("64")) ? "_x64" /* eg. 'amd64' */ : "_x32";
    String syslib = SYSLIB + arch;
    
    try { // loading a native lib in a static initializer ensures that it is available before any method in this class is called:
      System.loadLibrary(syslib);
      System.out.println("Done loading '" + System.mapLibraryName(syslib) + "', PID=" + getProcessID());
    } catch (Error e) {
      System.err.println("Native library '" + System.mapLibraryName(syslib) + "' not found in 'java.library.path': " + System.getProperty("java.library.path"));
      throw e; // re-throw
    }

    if (isWinPlatform()) {
      System.setProperty("line.separator","\n"); // so we won't have to mess with DOS line endings ever again
      comSpec = getEnv("comSpec"); // use native method here since getEnvironmentVariable() needs to know comSpec
      comSpec = (comSpec != null) ? comSpec + " /c " : "";

      try (BufferedReader br = new BufferedReader(new InputStreamReader(RUNTIME.exec(comSpec + "ver").getInputStream()))) { // fix for Win7,8
        for (String line = null; (line = br.readLine()) != null;) {
          if (isVista && (line.contains("6.1"/*Win7*/) || line.contains("6.2"/*Win8*/))) { isVista = false; isWin7 = true; }
        }
      } catch (IOException e) { e.printStackTrace(); }

      String cygdir = getEnv("cygdir"); // this is set during CygWin install to "?:/cygwin/bin"
      isCygWin = (cygdir != null && !cygdir.equals("%cygdir%"));
      cygstartPath = cygdir + "/cygstart.exe"; // path to CygWin's cygutils' "cygstart" binary

      if (getDebug() && Desktop.isDesktopSupported()) {
        Desktop desktop = Desktop.getDesktop();
        for (Desktop.Action action : Desktop.Action.values()) System.out.println("Desktop action " + action + " supported?  " + desktop.isSupported(action));
      }
    }
  }

  public static boolean isWinPlatform () { return (isWin7 || isVista || isWinXP || isWin2k || isWinNT || isWin9X); }

  public static boolean isWin7  () { return isWin7;  } // true for Win7 & Win8
  public static boolean isVista () { return isVista; }
  public static boolean isWinXP () { return isWinXP; }
  public static boolean isWin2k () { return isWin2k; }
  public static boolean isWinNT () { return isWinNT; }
  public static boolean isWin9X () { return isWin9X; }

  public static boolean isMac () { return isMac; }

  public static boolean isSolaris () { return isSolaris; }

  public static boolean isLinux () { return isLinux; }

  public static boolean isUnix () { return isUnix || isSolaris || isLinux || isMac /*assume OSX*/; }

  public static boolean isCygWin () { return isCygWin; }

  public static boolean isCaseSensitive () { return isUnix(); }

  public static String getOSType () {
    if (isWinPlatform()) return "Windows";
    if (isLinux())       return "Linux";
    if (isUnix())        return "Unix";
    if (isMac())         return "Mac";
    return "unknown";
  }

  /** Return the name of the Null device on platforms which support it, or "junk" otherwise. */
  public static String getDevNull () {
    return (isWinPlatform() ? "NUL:" : (isUnix() ? "/dev/null" : "junk"));
  }

  public static boolean isJava7 () { return System.getProperty("java.version").startsWith("1.7"); }

  /** Determine whether the current platform has CygWin's cygutils being installed in their default location. */
  public static boolean hasCygUtils () {
    return (isCygWin && new File(cygstartPath).exists());
  }

  public static void showDesktop () { // Windows only
    try {
      if (SystemUtils.isWinPlatform()) RUNTIME.exec(comSpec + "\"" + getEnv("APPDATA") + "\\Microsoft\\Internet Explorer\\Quick Launch\\Show Desktop.scf\"");
    } catch (IOException e) { e.printStackTrace(); }
  }

  /** Determine whether the current user has Windows administrator privileges. */
  public static boolean isWinAdmin () {
    if (!isWinPlatform()) return false;
//    for (String group : new com.sun.security.auth.module.NTSystem().getGroupIDs()) {
//      if (group.equals("S-1-5-32-544")) return true; // "S-1-5-32-544" is a well-known security identifier in Windows. If the current user has it then he/she/it is an administrator.
//    }
//    return false;

    try {
      Class<?> ntsys = Class.forName("com.sun.security.auth.module.NTSystem");
      if (ntsys != null) {
        Object groupObj = SystemUtils.invoke(ntsys.newInstance(), ntsys.getDeclaredMethod("getGroupIDs"), (Object[]) null);
        if (groupObj instanceof String[]) {
          for (String group : (String[]) groupObj) {
            if (group.equals("S-1-5-32-544")) return true; // "S-1-5-32-544" is a well-known security identifier in Windows. If the current user has it then he/she/it is an administrator.
          }
        }
      }
    } catch (ReflectiveOperationException e) { System.err.println(e); }
    return false;
  }

  // === environment variable stuff ==================================================================================================================

  public static String getHomePath () {
    String home = System.getProperty("HOME");
    if (home == null) {
      try {
        String thisPath = SystemUtils.class.getResource("SystemUtils.class").getPath();
        String thisPkg  = SystemUtils.class.getPackage().getName();
        home = thisPath.substring(0, thisPath.indexOf(thisPkg.substring(0, thisPkg.indexOf('.'))));
      } catch (Exception e) { e.printStackTrace(); }
    }
    if (home == null) home = "";
    return home;
  }

  /** Print all system property keys and values. */
  public static void printSystemProperties () {
    Properties p = System.getProperties();
    for (Object key : p.keySet()) System.out.println(key + ": " + p.getProperty(key.toString()));
  }

  public static Properties getEnvironmentVariables () {
    synchronized (cygstartPath) {
      if (envVars != null) return envVars;
      envVars = new Properties();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(RUNTIME.exec(comSpec + "env").getInputStream()))) {
        for (String line = null; (line = br.readLine()) != null;) {
          //if (debug) System.out.println("getEnvironmentVariables(): line=" + line);
          int idx = line.indexOf('=');
          if (idx > 0) envVars.put(line.substring(0, idx), line.substring(idx + 1));
        }
      } catch (IOException e) { e.printStackTrace(); }
      return envVars;
    }
  }

  //public static String[] getEnvironment () { return getEnvironmentVariables().toArray(); }

  /** Returns the setting of an environment variable set inside the JVM. */
  public static String getEnvironmentVariable (String envVarName) {
    if (envVars == null) envVars = getEnvironmentVariables();
    return (String) envVars.get(envVarName);
  }

  /** Returns the setting of an environment variable using JNI. Use this method to obtain env settings known outside the JVM. */
  public static native String getEnv (String envVarName);

  /** Returns the name of the file pointed to by a Windows shortcut (.lnk) using JNI. */
  public static native String readLnk (String lnkName);

  /** Creates a Windows link to a specified target.
      e.g. SystemUtils.createLnk("X:\\jodel", "Y:\\Stuff\\Edit1.txt", "Y:\\Stuff\\", "jodel");
           creates lnk file 'X:/jodel.lnk' which is shortcut to 'Y:/Stuff/Edit1.txt'. */
  public static native int createLnk (String lnkName, String targetName, String targetPath, String desc);

  /** Moves a file to the Windows RecycleBin'. */
  public static native void sendToRecycleBin (String fileName);

  /** Returns the Windows volume label assigned to a FAT or NTFS drive letter using JNI. */
  public static native String getVolumeLabel (String drive);

  /** Sets the specified Windows volume's label. */
  public static native boolean setVolumeLabel (String drive, String label);

  public static final int DRIVE_UNKNOWN     = 0; /** The drive type cannot be determined. */
  public static final int DRIVE_NO_ROOT_DIR = 1; /** The root directory does not exist. */
  public static final int DRIVE_REMOVABLE   = 2; /** The disk can be removed from the drive. */
  public static final int DRIVE_FIXED       = 3; /** The disk cannot be removed from the drive. */
  public static final int DRIVE_REMOTE      = 4; /** The drive is a remote (network) drive. */
  public static final int DRIVE_CDROM       = 5; /** The drive is a CD-ROM drive. */
  public static final int DRIVE_RAMDISK     = 6; /** The drive is a RAM disk. */

  /** Retrieves the specified drive's type. */
  public static native int getDriveType (String drive);

  /** Set the NTFS file compression attribute on file named 'fileName'. */
  public static native void setNTFSCompression (String fileName);

  public static native boolean isNTFSCompressed (String pathName);

  /** Retrieves the Win32 window handle for the specified window title.
      @param <CODE>title</CODE> the title of the window whose Win32 window handle to retrieve
      @return an <CODE>int</CODE> representing the Win32 window handle for the specified window title */
  public static native int getHwnd (String title);

  /** Sets the specified window as the topmost window in the z-order.
      @param <CODE>hwnd</CODE> the window's Win32 handle
      @param <CODE>flag</CODE> a <CODE>boolean</CODE> parameter that specifies whether
      the window will be the topmost window in the z-order. <CODE><B>true</B></CODE> sets the window as
      the topmost window in the z-order, <CODE><B>false</B></CODE> sets the window behind all topmost windows.
      @see #getHwnd */
  public static native void setWindowAlwaysOnTop (int hwnd, boolean flag);

  /** Retrieves the current directory.
      @return a <CODE>String</CODE> representing the current directory */
  public static native String getCurrentDirectory ();

  // =============================================================================================================================================

  public static String ping (String address) {
    String reply = "Request timed out";
    try (BufferedReader br = new BufferedReader(new InputStreamReader(RUNTIME.exec("ping " + address).getInputStream()))) {
      for (String line = null; (line = br.readLine()) != null;) {
        if (line.trim().startsWith("Reply ")) { reply = line; break; }
      }
    } catch (IOException e) { e.printStackTrace(); }
    return reply;
  }

  /** This method runs the Runnable and measures how long it takes.
      @param r is the Runnable for the task that we want to measure
      @return the time it took to execute this task  */
  public static long time (Runnable r) {
    long time = -System.currentTimeMillis();
    r.run();
    time += System.currentTimeMillis();
    System.out.println("Took " + time + "ms");
    return time;
  }

  /** Invoke a method on an object using reflection. */
  public static Object invoke (final Object target, final Method method, final Object... args) {
    try {
      return AccessController.doPrivileged(new PrivilegedExceptionAction<Object> () {
        public Object run () throws IllegalAccessException, InvocationTargetException {
          if (!method.isAccessible()) method.setAccessible(true); // Say please - else invoke() will fail if method is protected
          return method.invoke(target, args);
        }
      });
    } catch (PrivilegedActionException e) { e.printStackTrace(); }
    return null;
  }

//  public static <T extends Object> T makeAccessible (final T obj) {
//    try {
//      AccessController.doPrivileged(new PrivilegedExceptionAction<Object> () {
//        public Object run () throws IllegalAccessException, InvocationTargetException {
//          if (obj instanceof Method) ((Method) obj).setAccessible(true);
//          if (obj instanceof Field ) ((Field ) obj).setAccessible(true);
//          return obj;
//        }
//      });
//    } catch (PrivilegedActionException e) { IError.printStackTrace(e); }
//    return obj;
//  }

  public static Field makeAccessible (Class<?> target, String fieldName) {
    try {
      final Field field = target.getDeclaredField(fieldName);
      return (Field) AccessController.doPrivileged(new PrivilegedExceptionAction<Object> () {
        public Object run () throws IllegalAccessException, InvocationTargetException {
          if (!field.isAccessible()) field.setAccessible(true);
          return field;
        }
      });
    } catch (NoSuchFieldException | PrivilegedActionException e) { System.out.print(""); } // keep quiet IError.printStackTrace(e); }
    return null;
  }

  public static Frame getSharedOwnerFrame () {
    try {
      Method getSharedOwnerFrame = SwingUtilities.class.getDeclaredMethod("getSharedOwnerFrame"); // package-protected
      Object frame = SystemUtils.invoke(null, getSharedOwnerFrame); // getSharedOwnerFrame() is static - invoke it on null object
      return (frame instanceof Frame) ? (Frame) frame : null;
    } catch (NoSuchMethodException | SecurityException e) { e.printStackTrace(); }
    return null;
  }

  // =============================================================================================================================================

  private static final String WINDOWS_DESKTOP = "Desktop";

  /** Return the current user desktop path. */
  public static String getWindowsCurrentUserDesktopPath () {
    return System.getenv("userprofile") + '/' + WINDOWS_DESKTOP;
  }

  /** Create an Internet shortcut on User's Desktop no icon specified, e.g. SystemUtils.createInternetShortcutOnDesktop("GOOGLE", "http://www.google.com");
      @param name    name of the shortcut
      @param target  URL */
  public static void createInternetShortcutOnDesktop (String name, String target) {
    createInternetShortcut(name, getWindowsCurrentUserDesktopPath() + '/' + name + ".URL", target, "");
  }

  /** Create an Internet shortcut on User's Desktop, icon specified.
      @param name    name of the shortcut
      @param target  URL
      @param icon    URL (ex. http://www.server.com/favicon.ico) */
  public static void createInternetShortcutOnDesktop (String name, String target, String icon) {
    createInternetShortcut(name, getWindowsCurrentUserDesktopPath() + '/' + name + ".URL", target, icon);
  }

  /** Create an Internet shortcut
      @param name    name of the shortcut
      @param where   location of the shortcut
      @param target  URL
      @param icon    URL (ex. http://www.server.com/favicon.ico) */
  public static void createInternetShortcut (String name, String where, String target, String icon) {
    try (FileWriter fw = new FileWriter(where)) {
      fw.write("[InternetShortcut]\n");
      fw.write("URL=" + target + '\n');
      if (!icon.isEmpty()) fw.write("IconFile=" + icon + '\n');
    } catch (IOException iox) { /**/ }
  }

  public static void setClipboardText (String text) {
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
  }

  // =============================================================================================================================================

  // open application associated to a file extension
  public static void open (File document) throws IOException {
    if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(document);
  }

  // print a file
  public static void print (File document) throws IOException {
    if (Desktop.isDesktopSupported()) Desktop.getDesktop().print(document);
  }

  // open URL in default browser
  public static void browse (URI document) throws IOException {
    if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(document);
  }

  // open default mail client (use the mailto: protocol as the URI), eg.: mailto:elvis@heaven.com?SUBJECT=Love me tender&BODY=love me sweet
  public static void mail (URI document) throws IOException {
    if (Desktop.isDesktopSupported()) Desktop.getDesktop().mail(document);
  }
} // end class SystemUtils
