package com.etv.adapter.recycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.etv.adapter.recycle.listener.OnItemChildClickListener;
import com.etv.adapter.recycle.listener.OnItemChildLongClickListener;
import com.etv.adapter.recycle.listener.OnItemClickListener;
import com.etv.adapter.recycle.listener.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class BaseQuickAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> mDatas;
    private MultiItemType<T> mMultiType;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemChildClickListener mOnItemChildClickListener;
    private OnItemChildLongClickListener mOnItemChildLongClickListener;

    /**
     * 用于保存需要设置点击事件的 item
     */
    private final LinkedHashSet<Integer> mChildClickViewIds = new LinkedHashSet<>();

    /**
     * 用于保存需要设置长按点击事件的 item
     */
    private final LinkedHashSet<Integer> mChildLongClickViewIds = new LinkedHashSet<>();

    public BaseQuickAdapter() {
        this(0);
    }

    public BaseQuickAdapter(@LayoutRes int layoutRes) {
        this(layoutRes, new ArrayList<>());
    }

    public BaseQuickAdapter(@LayoutRes int layoutRes, List<T> list) {
        mDatas = (list == null ? new ArrayList<>() : list);
        initItemType(layoutRes);
    }

    private void initItemType(int layoutRes) {
        mMultiType = new MultiItemType<T>() {
            @Override
            public int getItemType(T item, int position) {
                return 0;
            }
        };
        mMultiType.addItemType(0, layoutRes);
    }

    public void setMultiItemType(MultiItemType<T> itemType) {
        if (itemType != null) {
            this.mMultiType = itemType;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VH viewHolder = createVHolder(parent, viewType);
        bindViewClickListener(viewHolder, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        convert(holder, getItem(position));
    }

    @SuppressWarnings("unchecked")
    private VH createVHolder(ViewGroup group, int viewType) {
        int layoutId = mMultiType.getLayoutId(viewType);
        View view = LayoutInflater.from(group.getContext()).inflate(layoutId, group, false);
        return (VH) new BaseViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiType != null) {
            return mMultiType.getItemType(mDatas.get(position), position);
        }
        return 0;
    }

    public void setLayoutRes(@LayoutRes int layoutId) {
        mMultiType.addItemType(0, layoutId);
    }

    public void addChildClickViewId(@IdRes int viewId) {
        mChildClickViewIds.add(viewId);
    }

    /**
     * 设置需要点击事件的子view
     * @param viewIds IntArray
     */
    public void addChildClickViewIds(@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            mChildClickViewIds.add(viewId);
        }
    }

    public void addChildLongClickViewId(@IdRes int viewId) {
        mChildLongClickViewIds.add(viewId);
    }

    /**
     * 设置需要点击事件的子view
     * @param viewIds IntArray
     */
    public void addChildLongClickViewIds(@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            mChildLongClickViewIds.add(viewId);
        }
    }

    protected void bindViewClickListener(VH holder, int viewType) {
        final BaseQuickAdapter<T, VH> adapter = this;
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(adapter, v, getPosition(holder)));
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> mOnItemLongClickListener.onItemLongClick(adapter, v, getPosition(holder)));
        }
        if (mOnItemChildClickListener != null) {
            View view;
            for (int viewId : mChildClickViewIds) {
                view = holder.itemView.findViewById(viewId);
                if (view != null)
                    view.setOnClickListener(v -> mOnItemChildClickListener.onItemChildClick(adapter, v, getPosition(holder)));
            }
        }
        if (mOnItemChildLongClickListener != null) {
            View view;
            for (int viewId : mChildLongClickViewIds) {
                view = holder.itemView.findViewById(viewId);
                if (view != null)
                    view.setOnLongClickListener(v -> mOnItemChildLongClickListener.onItemChildLongClick(adapter, v, getPosition(holder)));
            }
        }
    }

    private int getPosition(VH holder) {
        return holder.getAdapterPosition();
    }

    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void remove(int position) {
        if (position < mDatas.size()) {
            mDatas.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void remove(T item) {
        int index = mDatas.indexOf(item);
        if (index > -1) {
            remove(index);
        }
    }

    public List<T> getData() {
        return mDatas;
    }

    public void addData(T data) {
        mDatas.add(data);
        notifyItemInserted(mDatas.size() -1);
    }

    public void addData(int index, T data) {
        if (index < mDatas.size()) {
            mDatas.add(index, data);
            notifyItemInserted(index);
        }
    }

    public void addData(Collection<T> data) {
        mDatas.addAll(data);
        notifyDataChanged();
    }

    public void setData(int index, T data) {
        if (index < mDatas.size()) {
            mDatas.set(index, data);
            notifyItemChanged(index);
        }
    }

    public void setData(Collection<T> data) {
        mDatas.clear();
        if (data != null) {
            mDatas.addAll(data);
        }
        notifyDataChanged();
    }

    public void setNewData(List<T> data) {
        mDatas = (data == null ? new ArrayList<>() : data);
        notifyDataChanged();
    }

    public void clear() {
        mDatas.clear();
        notifyDataChanged();
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void notifyDataChanged() {
        notifyDataSetChanged();
    }

    protected abstract void convert(VH holder, T item);

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        this.mOnItemChildClickListener = listener;
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener listener) {
        this.mOnItemChildLongClickListener = listener;
    }
}
