package com.etv.view.layout.video.stream;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.view.layout.Generator;
import com.etv.view.layout.video.rtsp.RtspSurfaceView;
import com.ys.etv.R;

import java.util.List;

public class ViewStreamRtspGenerate extends Generator {

    Context context;
    View view;
    String streamUrl;
    CpListEntity cpListEntity;

    public ViewStreamRtspGenerate(Context context, CpListEntity cpListEntity, int startX, int StartY, int width, int height, String streamUrl) {
        super(context, startX, StartY, width, height);
        this.context = context;
        this.streamUrl = streamUrl;
        this.cpListEntity = cpListEntity;
        view = View.inflate(context, R.layout.view_stream_rtsp, null);
        initView();
    }

    RtspSurfaceView rtspSurfaceView;
    LinearLayout lin_wait_view;
    View view_rtsp_click;

    private void initView() {
        rtspSurfaceView = view.findViewById(R.id.surfaceView_rtsp);
        lin_wait_view = (LinearLayout) view.findViewById(R.id.lin_wait_view);

        view_rtsp_click = (View) view.findViewById(R.id.view_rtsp_click);
        view_rtsp_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public void timeChangeToUpdateView() {

    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void clearMemory() {
        if (rtspSurfaceView != null) {
            rtspSurfaceView.clearMemoryCache();
        }
    }

    @Override
    public void removeCacheView(String tag) {
        if (rtspSurfaceView != null) {
            rtspSurfaceView.clearMemoryCache();
        }
    }

    @Override
    public void updateView(Object object, boolean isShowBtn) {
        rtspSurfaceView.startPlayUrl(streamUrl);
    }

    @Override
    public void updateTextInfo(Object object) {

    }

    @Override
    public void playComplet() {

    }

    @Override
    public int getVideoPlayCurrentDuartion() {
        return 0;
    }

    @Override
    public void pauseDisplayView() {

    }

    @Override
    public void resumePlayView() {

    }




    @Override
    public void playPositionScenProgram(int position, List<MediAddEntity> mediAddEntities, SceneEntity currentScentity) {

    }
}
