//package com.etv.view.layout.video.vlc;
//
//import android.content.Context;
//
//import org.videolan.libvlc.LibVLC;
//
//import java.util.ArrayList;
//
///**
// * Created by Brooks on 2015-12-31.
// * LibVLCUtil LibVLC 单例
// */
//public class LibVLCUtil {
//
//    private static LibVLC libVLC = null;
//
//    public synchronized static LibVLC getLibVLC(Context context, ArrayList<String> options) throws IllegalStateException {
//        if (libVLC == null) {
//            if (options == null) {
//                libVLC = new LibVLC(context);
//            } else {
//                libVLC = new LibVLC(context, options);
//            }
//        }
//        return libVLC;
//    }
//
//
////    private static LibVLC libVLC = null;
////
////    public synchronized static LibVLC getLibVLC(ArrayList<String> options) {
////        try {
////            if (libVLC == null) {
////                if (options == null) {
////                    libVLC = new LibVLC();
////                } else {
////                    libVLC = new LibVLC(options);
////                }
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return libVLC;
////    }
//}