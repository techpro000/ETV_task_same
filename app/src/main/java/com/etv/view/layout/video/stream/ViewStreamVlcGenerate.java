//package com.etv.view.layout.video.stream;
//
//import android.content.Context;
//import android.view.View;
//import android.widget.LinearLayout;
//
//import com.etv.task.entity.CpListEntity;
//import com.etv.task.entity.MediAddEntity;
//import com.etv.task.entity.SceneEntity;
//import com.etv.view.layout.Generator;
//import com.etv.view.layout.video.vlc.VLCSurfaceViewNew;
//import com.etv.view.layout.video.vlc.VlcPlayerStatuesListener;
//import com.ys.etv.R;
//
//import java.util.List;
//
//public class ViewStreamVlcGenerate extends Generator {
//
//    Context context;
//    View view;
//    String streamUrl;
//    CpListEntity cpListEntity;
//
//    public ViewStreamVlcGenerate(Context context, CpListEntity cpListEntity, int startX, int StartY, int width, int height, String streamUrl) {
//        super(context, startX, StartY, width, height);
//        this.context = context;
//        this.streamUrl = streamUrl;
//        this.cpListEntity = cpListEntity;
//        view = View.inflate(context, R.layout.view_stream_vlc, null);
//        initView();
//    }
//
//    VLCSurfaceViewNew surfaceView_vlc;
//    LinearLayout lin_wait_view;
//    View view_vlc_click;
//
//    private void initView() {
//        surfaceView_vlc = (VLCSurfaceViewNew) view.findViewById(R.id.surfaceView_vlc);
//        lin_wait_view = (LinearLayout) view.findViewById(R.id.lin_wait_view);
//        surfaceView_vlc.setPlayVlcSatues(new VlcPlayerStatuesListener() {
//
//            @Override
//            public void showProgressStatues(boolean isShow) {
//                lin_wait_view.setVisibility(isShow ? View.VISIBLE : View.GONE);
//            }
//        });
//
//        view_vlc_click = (View) view.findViewById(R.id.view_vlc_click);
//        view_vlc_click.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//    }
//
//    @Override
//    public int getVideoPlayCurrentDuartion() {
//        return 0;
//    }
//
//    @Override
//    public void timeChangeToUpdateView() {
//
//    }
//
//    @Override
//    public View getView() {
//        return view;
//    }
//
//    @Override
//    public void clearMemory() {
//        if (surfaceView_vlc != null) {
//            surfaceView_vlc.clearMemoryCache();
//        }
//    }
//
//    @Override
//    public void removeCacheView(String tag) {
//        if (surfaceView_vlc != null) {
//            surfaceView_vlc.clearMemoryCache();
//        }
//    }
//
//    @Override
//    public void updateView(Object object, boolean isShowBtn) {
//        surfaceView_vlc.startPlayUrl(streamUrl);
//    }
//
//    @Override
//    public void updateTextInfo(Object object) {
//
//    }
//
//    @Override
//    public void playComplet() {
//
//    }
//
//    @Override
//    public void pauseDisplayView() {
//
//    }
//
//    @Override
//    public void resumePlayView() {
//
//    }
//
//    @Override
//    public void playPositionScenProgram(int position, List<MediAddEntity> mediAddEntities, SceneEntity currentScentity) {
//
//    }
//}
