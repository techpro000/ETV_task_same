//package com.ys.model.dialog;
//
//import android.content.Context;
//import android.view.View;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.ys.model.R;
//import com.ys.model.adapter.BaseViewHolder;
//import com.ys.model.adapter.QuickAdapter;
//import com.ys.model.databinding.YsDialogListBinding;
//import com.ys.model.decoration.LineDivider;
//import com.ys.model.entity.ListItem;
//
//import java.util.List;
//
//public class ListItemDialog extends BaseDialog {
//
//    private final YsDialogListBinding mView;
//    private OnItemClickListener listener;
//    private QuickAdapter<ListItem> adapter;
//    private List<ListItem> dataList;
//
//    public ListItemDialog(Context context) {
//        super(context);
//        mView = getBindView();
//    }
//
//
//    @Override
//    protected int getLayoutRes() {
//        return R.layout.ys_dialog_list;
//    }
//
//    @Override
//    protected void initView() {
//        adapter = new QuickAdapter<ListItem>(R.layout.layout_list_item, dataList) {
//            @Override
//            protected void convert(BaseViewHolder holder, ListItem item) {
//                holder.setText(R.id.tv_name, item.name);
//            }
//        };
//        LineDivider divider = new LineDivider(LinearLayoutManager.HORIZONTAL);
//        divider.setDividerColor(getContext().getResources().getColor(R.color.divider_color));
//        mView.rvList.addItemDecoration(divider);
//        mView.rvList.setLayoutManager(new LinearLayoutManager(getContext()));
//        mView.rvList.setAdapter(adapter);
//        adapter.setOnItemClickListener((base, view, position) -> {
//            if (listener != null) {
//                listener.onItemClick(position, adapter.getItem(position));
//            }
//        });
//    }
//
//    public void setDataList(List<ListItem> dataList) {
//        this.dataList = dataList;
//        if (adapter != null) {
//            adapter.setData(dataList);
//        }
//    }
//
//    public void setTitle(String title) {
//        mView.tvTitle.setText(title);
//    }
//
//    public void showTitle(boolean show) {
//        mView.tvTitle.setVisibility(show ? View.VISIBLE : View.GONE);
//    }
//
//    public void setOnItemListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(int index, ListItem item);
//    }
//}
