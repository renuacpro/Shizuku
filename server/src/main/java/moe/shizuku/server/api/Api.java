package moe.shizuku.server.api;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.IProcessObserver;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.IUserManager;
import android.os.RemoteException;
import android.os.ServiceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import moe.shizuku.server.utils.BinderSingleton;

public class Api {

    public static final BinderSingleton<IActivityManager> ACTIVITY_MANAGER_SINGLETON = new BinderSingleton<IActivityManager>() {

        @Override
        protected IActivityManager create() {
            //noinspection deprecation
            return ActivityManagerNative.getDefault();
        }
    };

    public static final BinderSingleton<IPackageManager> PACKAGE_MANAGER_SINGLETON = new BinderSingleton<IPackageManager>() {

        @Override
        protected IPackageManager create() {
            return IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        }
    };

    public static final BinderSingleton<IUserManager> USER_MANAGER_SINGLETON = new BinderSingleton<IUserManager>() {

        @Override
        protected IUserManager create() {
            return IUserManager.Stub.asInterface(ServiceManager.getService("user"));
        }
    };

    private static Method method_IActivityManager_getContentProviderExternal;

    static {
        try {
            method_IActivityManager_getContentProviderExternal =
                    IActivityManager.class.getDeclaredMethod("getContentProviderExternal", String.class, int.class, IBinder.class);
            method_IActivityManager_getContentProviderExternal.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static int checkPermission(String permission, int pid, int uid) throws RemoteException {
        IActivityManager am = ACTIVITY_MANAGER_SINGLETON.get();
        if (am == null) {
            throw new RemoteException("can't get IActivityManager");
        }
        return am.checkPermission(permission, pid, uid);
    }

    public static PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
        IPackageManager pm = PACKAGE_MANAGER_SINGLETON.get();
        if (pm == null) {
            throw new RemoteException("can't get IPackageManager");
        }
        return pm.getPackageInfo(packageName, flags, userId);
    }

    public static ApplicationInfo getApplicationInfo(String packageName, int flags, int userId) throws RemoteException {
        IPackageManager pm = PACKAGE_MANAGER_SINGLETON.get();
        if (pm == null) {
            throw new RemoteException("can't get IPackageManager");
        }
        return pm.getApplicationInfo(packageName, flags, userId);
    }

    public static int checkPermission(String permName, int uid) throws RemoteException {
        IPackageManager pm = PACKAGE_MANAGER_SINGLETON.get();
        if (pm == null) {
            throw new RemoteException("can't get IPackageManager");
        }
        return pm.checkUidPermission(permName, uid);
    }

    public static void registerProcessObserver(IProcessObserver processObserver) throws RemoteException {
        IActivityManager am = ACTIVITY_MANAGER_SINGLETON.get();
        if (am == null) {
            throw new RemoteException("can't get IActivityManager");
        }
        am.registerProcessObserver(processObserver);
    }

    public static String[] getPackagesForUid(int uid) throws RemoteException {
        IPackageManager pm = PACKAGE_MANAGER_SINGLETON.get();
        if (pm == null) {
            throw new RemoteException("can't get IPackageManager");
        }
        return pm.getPackagesForUid(uid);
    }

    public static Object getContentProviderExternal(String name, int userId, IBinder token) throws RemoteException {
        IActivityManager am = ACTIVITY_MANAGER_SINGLETON.get();
        if (am == null) {
            throw new RemoteException("can't get IActivityManager");
        }

        try {
            return method_IActivityManager_getContentProviderExternal.invoke(am, name, userId, token);
        } catch (IllegalAccessException e) {
            // should never happened
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
    }

    public static void removeContentProviderExternal(String name, IBinder token) throws RemoteException {
        IActivityManager am = ACTIVITY_MANAGER_SINGLETON.get();
        if (am == null) {
            throw new RemoteException("can't get IActivityManager");
        }
        am.removeContentProviderExternal(name, token);
    }

}