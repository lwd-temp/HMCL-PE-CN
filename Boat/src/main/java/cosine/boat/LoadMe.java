package cosine.boat;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.*;

public class LoadMe {

    @SuppressLint("SdCardPath")
    public static String BOAT_LIB_DIR;

    public static native int chdir(String str);
    public static native int jliLaunch(String[] strArr);
    public static native void redirectStdio(String file);
    public static native void setenv(String str, String str2);
	public static native void setupJLI();
	public static native int dlopen(String name);
    public static native void setLibraryPath(String path);
	public static native void patchLinker();
	public static native void setGLName(String name);

    static {
        System.loadLibrary("boat");
    }

    public static int launchMinecraft(Context context,String javaPath, String home, boolean highVersion, Vector<String> args, String renderer) {

        BOAT_LIB_DIR = context.getDir("runtime",0).getAbsolutePath() + "/boat";
        switch (renderer){
            case "libGL112.so.1":
            case "libGL115.so.1":
                setGLName("libGL.so.1");
                break;
            case "libgl4es_114.so":
                setGLName("libgl04es.so");
                break;
            case "libvgpu.so":
                setGLName("libvgpu.so");
                break;
        }
		patchLinker();

        try {

			setenv("HOME", home);
			setenv("JAVA_HOME" , javaPath);
			setenv("LIBGL_MIPMAP","3");
			setenv("LIBGL_NORMALIZE","1");
			setenv("LIBGL_GL","21");

            // openjdk
            dlopen(javaPath + "/lib/aarch64/libpng16.so.16");
            dlopen(javaPath + "/lib/aarch64/libpng16.so");
            dlopen(javaPath + "/lib/aarch64/libfreetype.so");
			dlopen(javaPath + "/lib/aarch64/jli/libjli.so");
			dlopen(javaPath + "/lib/aarch64/server/libjvm.so");
			dlopen(javaPath + "/lib/aarch64/libverify.so");
			dlopen(javaPath + "/lib/aarch64/libjava.so");
			dlopen(javaPath + "/lib/aarch64/libnet.so");
			dlopen(javaPath + "/lib/aarch64/libnio.so");
			dlopen(javaPath + "/lib/aarch64/libawt.so");
			dlopen(javaPath + "/lib/aarch64/libawt_headless.so");
            dlopen(javaPath + "/lib/aarch64/libinstrument.so");
            dlopen(javaPath + "/lib/aarch64/libfontmanager.so");
            dlopen(BOAT_LIB_DIR + "/libopenal.so.1");
            dlopen(BOAT_LIB_DIR + "/renderer/" + renderer);

            String libraryPath;

            if (!highVersion) {
                libraryPath = javaPath + "/lib/aarch64/jli:" + javaPath + "/lib/aarch64:" + BOAT_LIB_DIR + "/lwjgl-2:" + BOAT_LIB_DIR + "/renderer";
                dlopen(BOAT_LIB_DIR + "/lwjgl-2/liblwjgl.so");
            }
            else {
                libraryPath = javaPath + "/lib/aarch64/jli:" + javaPath + "/lib/aarch64:" + BOAT_LIB_DIR + "/lwjgl-3:" + BOAT_LIB_DIR + "/renderer";
                dlopen(BOAT_LIB_DIR + "/libglfw.so");
                dlopen(BOAT_LIB_DIR + "/lwjgl-3/liblwjgl.so");
                dlopen(BOAT_LIB_DIR + "/lwjgl-3/liblwjgl_stb.so");
                dlopen(BOAT_LIB_DIR + "/lwjgl-3/liblwjgl_tinyfd.so");
                dlopen(BOAT_LIB_DIR + "/lwjgl-3/liblwjgl_opengl.so");
            }

            setLibraryPath(libraryPath);
			setupJLI();

            redirectStdio(home + "/boat_latest_log.txt");
            chdir(home);

			String finalArgs[] = new String[args.size()];
			for (int i = 0; i < args.size(); i++) {
                if (!args.get(i).equals(" ")) {
                    finalArgs[i] = args.get(i);
                    System.out.println("Minecraft Args:" + finalArgs[i]);
                }
			}
            System.out.println("OpenJDK exited with code : " + jliLaunch(finalArgs));
        }
        catch (Exception e) {
            e.printStackTrace();
			return 1;
        }
		return 0;
    }

    public static int launchJVM (String javaPath,Vector<String> args,String home) {

        patchLinker();

        try {
            setenv("HOME", home);
            setenv("JAVA_HOME" , javaPath);

            dlopen(javaPath + "/lib/aarch64/libfreetype.so");
            dlopen(javaPath + "/lib/aarch64/jli/libjli.so");
            dlopen(javaPath + "/lib/aarch64/server/libjvm.so");
            dlopen(javaPath + "/lib/aarch64/libverify.so");
            dlopen(javaPath + "/lib/aarch64/libjava.so");
            dlopen(javaPath + "/lib/aarch64/libnet.so");
            dlopen(javaPath + "/lib/aarch64/libnio.so");
            dlopen(javaPath + "/lib/aarch64/libawt.so");
            dlopen(javaPath + "/lib/aarch64/libawt_headless.so");
            dlopen(javaPath + "/lib/aarch64/libfontmanager.so");

            String libraryPath = javaPath + "/lib/aarch64/jli:" + javaPath + "/lib/aarch64";

            setLibraryPath(libraryPath);
            setupJLI();

            redirectStdio(home + "/boat_api_installer_log.txt");
            chdir(home);

            String finalArgs[] = new String[args.size()];
            for (int i = 0; i < args.size(); i++) {
                if (!args.get(i).equals(" ")) {
                    finalArgs[i] = args.get(i);
                    System.out.println("JVM Args:" + finalArgs[i]);
                }
            }
            System.out.println("OpenJDK exited with code : " + jliLaunch(finalArgs));
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

}





