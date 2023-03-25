//package com.etv.util.system;
//
//import androidx.lifecycle.MutableLiveData;
//
//import org.videolan.libvlc.RendererItem;
//
//public class RendererLiveData extends MutableLiveData<RendererItem> {
//
//    @Override
//    public void setValue(RendererItem value) {
//        if (getValue() != null) {
//            getValue().release();
//        }
//        if (value != null) {
//            value.retain();
//        }
//        super.setValue(value);
//    }
//}
