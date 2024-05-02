package com.vathanakmao.solarnoon.util;

import android.util.Log;

import java.net.InetAddress;

public class NetworkUtil {

    public static boolean isConnectedToInternet() {
        try {
            InetAddress address = InetAddress.getByAddress(new byte[]{8, 8, 8, 8}); // Google's public DNS server
            return address.isReachable(3000); // Try pinging for 1 second
        } catch (Exception e) {
            Log.e(NetworkUtil.class.getSimpleName(), Log.getStackTraceString(e));
            // handle exceptions like unreachable host or timeout
            return false;
        }
    }
}
