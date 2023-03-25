package com.etv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etv.adapter.FileAdapter;
import com.etv.config.AppInfo;
import com.etv.util.FileUtil;
import com.etv.util.sdcard.FileManagerView;
import com.etv.util.sdcard.FileergodicUtil;
import com.ys.model.dialog.MyToastView;
import com.ys.etv.R;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.entity.FileEntity;
import com.ys.model.listener.OridinryDialogClick;

import java.util.ArrayList;
import java.util.List;


/**
 * 文件管理器
 */
public class FileListActivity extends BaseActivity implements AdapterView.OnItemClickListener, FileManagerView,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private GridView lv_file;
    List<FileEntity> list = new ArrayList<FileEntity>();
    FileAdapter adapter;
    public static final String PATH_SEARCH = "PATH_SEARCH";
    OridinryDialog oridinryDialog;
    String path_search = AppInfo.FILE_RECEIVER_PATH();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        initView();
    }

    LinearLayout lin_exit;
    TextView tv_exit;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        try {
            Intent intent = getIntent();
            if (intent != null) {
                path_search = intent.getStringExtra(PATH_SEARCH);
            }
        } catch (Exception e) {
            path_search = AppInfo.FILE_RECEIVER_PATH();
        }

        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        lin_exit.setOnClickListener(this);
        tv_exit.setOnClickListener(this);

        lv_file = (GridView) findViewById(R.id.lv_file);
        adapter = new FileAdapter(FileListActivity.this, list);
        lv_file.setAdapter(adapter);
        lv_file.setOnItemClickListener(this);
        lv_file.setOnItemLongClickListener(this);
        lv_file.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_exit:
            case R.id.tv_exit:
                finish();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getFileList();
    }

    private void getFileList() {
        list.clear();
        adapter.setItems(list);
        if (path_search == null || path_search.length() < 5) {
            return;
        }
        list = FileergodicUtil.getFileList(path_search);
        if (list == null) {
            return;
        }
        adapter.setItems(list);
    }

    FileEntity currentFileEntity = null;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentFileEntity = list.get(position);
        Log.e("path", "============url===" + currentFileEntity.getFilePath());
        int fileStyle = currentFileEntity.getFileStyle();
        if (fileStyle == FileEntity.FILE_STYLE_DIR) {  //文件夹
            String fileDirPath = currentFileEntity.getFilePath();
            Intent intent = new Intent(FileListActivity.this, FileListActivity.class);
            intent.putExtra(FileListActivity.PATH_SEARCH, fileDirPath);
            startActivity(intent);
        } else if (fileStyle == FileEntity.FILE_STYLE_FILE) {  //文件
            MyToastView.getInstance().Toast(FileListActivity.this, "暂不支持预览");
        }
    }

    /***
     * 文件解压成功，刷新界面
     */
    @Override
    public void zipFileSuccess() {
        getFileList();
    }

    /***
     * listView长按事件
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        currentFileEntity = list.get(position);
        delFileChooice(currentFileEntity);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentFileEntity = list.get(position);
    }

    /***
     * 删除选中的文件
     * @param entity
     */
    private void delFileChooice(FileEntity entity) {
        if (oridinryDialog == null) {
            oridinryDialog = new OridinryDialog(FileListActivity.this);
        }
        final String filePath = entity.getFilePath();
        String fileName = entity.getFileName();
        oridinryDialog.show("是否删除 <" + fileName + "> 文件 ？", "删除", "在想想");
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                boolean isDelFile = FileUtil.deleteDirOrFilePath(filePath, "删除选中的文件");
                if (isDelFile) {
                    MyToastView.getInstance().Toast(FileListActivity.this, "文件删除成功");
                    getFileList();
                }
            }

            @Override
            public void noSure() {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
