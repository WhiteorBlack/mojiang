package cn.idcby.commonlibrary.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * D:recyclerView 添加header、footer
 */
public class HeaderFooterAdapter<T extends RecyclerView.Adapter>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final T mBase;

    private static final int HEADER_VIEW_TYPE = -1000;
    private static final int FOOTER_VIEW_TYPE = -2000;

    private final List<View> mHeaders = new ArrayList<>();
    private final List<View> mFooters = new ArrayList<>();

    private boolean isRecyclableHeader = true;//默认可以回收

    public boolean isRecyclableHeader() {
        return isRecyclableHeader;
    }

    public void setRecyclableHeader(boolean recyclableHeader) {
        isRecyclableHeader = recyclableHeader;
    }

    public HeaderFooterAdapter(T base) {
        super();
        mBase = base;
    }

    public HeaderFooterAdapter(T base, boolean isRecyclableHeader) {
        super();
        this.isRecyclableHeader = isRecyclableHeader;
        mBase = base;
    }

    public static View inflaterView(Context context, int id, RecyclerView recyclerView) {
        return LayoutInflater.from(context)
                .inflate(id, recyclerView, false);
    }

    public T getWrappedAdapter() {
        return mBase;
    }


    public void addHeader(@NonNull View view) {
        if (view == null) {
            throw new IllegalArgumentException("You can't have a null header!");
        }
        mHeaders.add(view);
        notifyDataSetChanged();
    }


    public void addFooter(@NonNull View view) {
        if (view == null) {
            throw new IllegalArgumentException("You can't have a null footer!");
        }
        mFooters.add(view);
        notifyDataSetChanged();
    }

    public void setHeaderVisibility(boolean shouldShow) {
        for (View header : mHeaders) {
            header.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
    }


    public void setFooterVisibility(boolean shouldShow) {
        for (View footer : mFooters) {
            footer.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
    }


    public int getHeaderCount() {
        return mHeaders.size();
    }


    public int getFooterCount() {
        return mFooters.size();
    }


    public View getHeader(int i) {
        return i < mHeaders.size() ? mHeaders.get(i) : null;
    }


    public View getFooter(int i) {
        return i < mFooters.size() ? mFooters.get(i) : null;
    }

    private boolean isHeader(int viewType) {
        return viewType >= HEADER_VIEW_TYPE && viewType < (HEADER_VIEW_TYPE + mHeaders.size());
    }

    public boolean isHeader(View view) {
        return mHeaders.contains(view);
    }

    public boolean isFooter(View view){
        return mFooters.contains(view);
    }

    public void removeHeader(View view) {
        if (view == null) {
            throw new IllegalArgumentException("You can't have a null header!");
        }
        if (mHeaders != null && mHeaders.size() > 0) {
            if (mHeaders.contains(view)) {
                mHeaders.remove(view);
                notifyDataSetChanged();
            }
        }
    }

    public void removeFooter(View view) {
        if (view == null) {
            throw new IllegalArgumentException("You can't have a null header!");
        }
        if (mFooters != null && mFooters.size() > 0) {
            if (mFooters.contains(view)) {
                mFooters.remove(view);
                notifyDataSetChanged();
            }
        }
    }


    private boolean isFooter(int viewType) {
        return viewType >= FOOTER_VIEW_TYPE && viewType < (FOOTER_VIEW_TYPE + mFooters.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (isHeader(viewType)) {
            int whichHeader = Math.abs(viewType - HEADER_VIEW_TYPE);
            View headerView = mHeaders.get(whichHeader);
            RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(headerView) {
            };
            if (!isRecyclableHeader) {
                viewHolder.setIsRecyclable(false);
            }
            return viewHolder;
        } else if (isFooter(viewType)) {
            int whichFooter = Math.abs(viewType - FOOTER_VIEW_TYPE);
            View footerView = mFooters.get(whichFooter);
            return new RecyclerView.ViewHolder(footerView) {
            };

        } else {
            return mBase.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position < mHeaders.size()) {

        } else if (position < mHeaders.size() + mBase.getItemCount()) {

            mBase.onBindViewHolder(viewHolder, position - mHeaders.size());

        } else {

        }
    }

    @Override
    public int getItemCount() {
        return mHeaders.size() + mBase.getItemCount() + mFooters.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaders.size()) {
            return HEADER_VIEW_TYPE + position;

        } else if (position < (mHeaders.size() + mBase.getItemCount())) {
            return mBase.getItemViewType(position - mHeaders.size());

        } else {
            return FOOTER_VIEW_TYPE + position - mHeaders.size() - mBase.getItemCount();
        }
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mBase.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        mBase.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mBase.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager)
        {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                @Override
                public int getSpanSize(int position){
                    if (position < mHeaders.size()) {//header
                        return gridLayoutManager.getSpanCount();
                    } else if (position < mHeaders.size() + mBase.getItemCount()) {
                        if (spanSizeLookup != null)
                            return spanSizeLookup.getSpanSize(position);

                        return 1;
                    } else {//footer
                        return gridLayoutManager.getSpanCount();
                    }
                }
            });

            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }

    public void notifyDataChanged(RecyclerView recyclerView){
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager)
        {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                @Override
                public int getSpanSize(int position){
                    if (position < mHeaders.size()) {//header
                        return gridLayoutManager.getSpanCount();
                    } else if (position < mHeaders.size() + mBase.getItemCount()) {
                        if (spanSizeLookup != null)
                            return spanSizeLookup.getSpanSize(position);

                        return 1;
                    } else {//footer
                        return gridLayoutManager.getSpanCount();
                    }
                }
            });

            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }

        notifyDataSetChanged() ;
    }


}