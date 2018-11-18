package cn.idcby.jiajubang.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.CircleTransInfo;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.UserActive;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.CircleDetailActivity;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.SkipUtils;

/**
 * 圈子
 * listView
 * 2018-06-30 10:04:22
 * 注意：暂时在用户主页使用，头像点击不用跳转，如果别的地方调用，单独处理
 *
 * 2018-09-15 16:33:14
 * 添加转载样式
 */

public class AdapterCircleActiveLv extends BaseAdapter {
    private Activity context ;
    private List<UserActive> mNewsList ;
    private boolean isLatest = false ;
    private boolean isSelf = false ;
    private RecyclerViewClickListener mClickListener ;

    private Map<String,Integer[]> mItemWidMap = new HashMap<>() ;//保存某个图片的宽高，key imgUrl

    private int mMoreImageWidHei;
    private static final int VIEW_TYPE_ONE = 0 ;
    private static final int VIEW_TYPE_FOUR = 1 ;
    private static final int VIEW_TYPE_MORE = 2 ;
    private static final int VIEW_TYPE_TRANS = 3 ;

    public static final int CLICK_TYPE_FOCUS = 0 ;
    public static final int CLICK_TYPE_SUPPORT = 1 ;
    public static final int CLICK_TYPE_TRANS = 2 ;
    public static final int CLICK_TYPE_DELETE = 3 ;
    public static final int CLICK_TYPE_SHARE = 4 ;

    public AdapterCircleActiveLv(Activity context , List<UserActive> mNewsList , boolean isLatest
            , RecyclerViewClickListener mClickListener) {
        this.context = context;
        this.mNewsList = mNewsList;
        this.isLatest = isLatest;
        this.mClickListener = mClickListener;
        this.mMoreImageWidHei = (ResourceUtils.getScreenWidth(context)
                - ResourceUtils.dip2px(context , 15) * 2 - ResourceUtils.dip2px(context , 2) * 2) / 3 ;
    }

    @Override
    public int getCount() {
        return  null == mNewsList ? 0 : mNewsList.size() ;
    }

    @Override
    public int getViewTypeCount() {
        return 4 ;
    }

    @Override
    public int getItemViewType(int position) {
        int type = VIEW_TYPE_ONE ;
        UserActive info = mNewsList.get(position) ;
        if(info != null){
            if(info.isReprint()){
                type = VIEW_TYPE_TRANS ;
            }else{
                List<ImageThumb> thumbList = info.getAlbumsList() ;
                if(thumbList != null){
                    int size = thumbList.size() ;
                    if(size <= 1){
                        type = VIEW_TYPE_ONE ;
                    }else if(size == 4){
                        type = VIEW_TYPE_FOUR;
                    }else{
                        type = VIEW_TYPE_MORE;
                    }
                }
            }
        }

        return type ;
    }

    @Override
    public Object getItem(int position) {
        return mNewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final int realPosition = position ;
        int viewType = getItemViewType(position) ;

        if(null == view){
            if(VIEW_TYPE_TRANS == viewType) {
                view = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_circle_item_transport, parent, false);
                NewsListHeadLineHolderTrans hlHolder = new NewsListHeadLineHolderTrans(view);
                view.setTag(hlHolder);
            }else if(VIEW_TYPE_ONE == viewType) {
                view = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_circle_item_one, parent, false);
                NewsListHeadLineHolderOne hlHolder = new NewsListHeadLineHolderOne(view);
                view.setTag(hlHolder);
            }else if(VIEW_TYPE_FOUR == viewType){
                view = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_circle_item_four, parent , false) ;
                NewsListHeadLineHolderFour hlHolder = new NewsListHeadLineHolderFour(view ,mMoreImageWidHei) ;
                view.setTag(hlHolder);
            }else{
                view = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_circle_item_three, parent , false) ;
                NewsListHeadLineHolderMore hlHolder = new NewsListHeadLineHolderMore(view) ;
                view.setTag(hlHolder);
            }
        }

        Object viewHolder = view.getTag() ;

        if(viewHolder instanceof BaseLvHeadLineHolder){
            BaseLvHeadLineHolder parentHolder = (BaseLvHeadLineHolder) viewHolder;

            final UserActive info = mNewsList.get(position) ;
            if(info != null){
                String writerName = info.getCreateUserName() ;
                String writerPhoto = info.getCreateUserHeadIcon() ;
                final String content = info.getBodyContent() ;
                String data = info.getReleaseTime() ;
                String companyName = info.getCompanyName() ;
                String postName = info.getPostText() ;

                boolean isFocused = (1 == info.getIsFollow()) ;
                boolean isSupport = info.isSupport() ;
                if (isSupport) {
                    parentHolder.mSuppoertIv.setImageResource(R.mipmap.ic_support_checked);
                } else {
                    parentHolder.mSuppoertIv.setImageResource(R.mipmap.ic_support_nomal);
                }

                parentHolder.mAutherNameTv.setText(writerName);
                parentHolder.mTimeTv.setText(data);
                parentHolder.mCompanyNameTv.setText(companyName);
                parentHolder.mPostNameTv.setText(postName);

                if(isLatest || isSelf){
                    parentHolder.mAttentionTv.setVisibility(View.INVISIBLE);
                    if(isSelf){
                        parentHolder.mDeleteIv.setVisibility(View.VISIBLE);
                    }
                }else{
                    parentHolder.mAttentionTv.setVisibility(View.VISIBLE);

                    parentHolder.mAttentionTv.setText(isFocused ? "已关注" : "+关注");
                    parentHolder.mAttentionTv.setBackgroundDrawable(context.getResources().getDrawable(isFocused
                            ? R.drawable.round_focus_bg_focused
                            : R.drawable.round_focus_bg_nomal));
                    parentHolder.mAttentionTv.setTextColor(context.getResources().getColor(isFocused
                            ? R.color.color_grey_88
                            : R.color.color_theme));
                }

                String supportCount = info.getLikeNumber() + "";
                String commentCount = info.getCommentNumber() + "" ;
                String transCount = info.getTransformNumber() + "" ;

                parentHolder.mCommentCountTv.setText(commentCount) ;
                parentHolder.mSuppoertCountTv.setText(supportCount) ;
                parentHolder.mTransCountTv.setText(transCount) ;

                if(!"".equals(content)){
                    parentHolder.mContentTv.setText(content);

                    if(parentHolder.mContentTv.getVisibility() != View.VISIBLE){
                        parentHolder.mContentTv.setVisibility(View.VISIBLE) ;
                    }
                }else{
                    if(parentHolder.mContentTv.getVisibility() != View.GONE){
                        parentHolder.mContentTv.setVisibility(View.GONE) ;
                    }
                }

                GlideUtils.loaderUser(writerPhoto , parentHolder.mAutherIv) ;

                parentHolder.mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mClickListener != null){
                            mClickListener.onItemClickListener(CLICK_TYPE_DELETE ,realPosition) ;
                        }
                    }
                });
                parentHolder.mAttentionTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mClickListener != null){
                            mClickListener.onItemClickListener(CLICK_TYPE_FOCUS ,realPosition) ;
                        }
                    }
                });
                parentHolder.mCommentLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toDtIt = new Intent(context , CircleDetailActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID , info.getPostID()) ;
                        context.startActivity(toDtIt) ;
                    }
                });
                parentHolder.mShareLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mClickListener != null){
                            mClickListener.onItemClickListener(CLICK_TYPE_SHARE ,realPosition) ;
                        }
                    }
                });
                parentHolder.mSupportLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mClickListener != null){
                            mClickListener.onItemClickListener(CLICK_TYPE_SUPPORT ,realPosition) ;
                        }
                    }
                });
                parentHolder.mTransContentLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mClickListener != null){
                            mClickListener.onItemClickListener(CLICK_TYPE_TRANS ,realPosition) ;
                        }
                    }
                });

                parentHolder.mMainLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toDtIt = new Intent(context , CircleDetailActivity.class) ;
                        toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID , info.getPostID()) ;
                        context.startActivity(toDtIt) ;
                    }
                });
            }

            if(viewHolder instanceof NewsListHeadLineHolderTrans){
                final NewsListHeadLineHolderTrans hlHolder = (NewsListHeadLineHolderTrans) viewHolder;

                if(info != null){
                    final CircleTransInfo transInfo = info.getSourcePostInfo() ;
                    if(transInfo != null){
                        hlHolder.mTransDtLay.setVisibility(View.VISIBLE) ;
                        hlHolder.mTransDeletedLay.setVisibility(View.GONE) ;

                        String name = transInfo.getBodyContent() ;
                        String imgUrl = transInfo.getImgUrl() ;
                        String categoryName = info.getCategoryTitle() ;//从列表上拿，理论上转载的分类跟被转载的是一致的

                        hlHolder.mTransportTitleTv.setText(name);
                        hlHolder.mTransportTypeTv.setText(categoryName);
                        GlideUtils.loader(imgUrl ,hlHolder.mTransportIv) ;
                    }else{
                        hlHolder.mTransDtLay.setVisibility(View.GONE) ;
                        hlHolder.mTransDeletedLay.setVisibility(View.VISIBLE) ;
                    }

                    hlHolder.mTransDtLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //要不要点击跳转到转载的详细？
                            Intent toDtIt = new Intent(context , CircleDetailActivity.class) ;
                            toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID , transInfo.getPostID()) ;
                            context.startActivity(toDtIt) ;

                        }
                    });
                }
            }else if(viewHolder instanceof NewsListHeadLineHolderOne){
                final NewsListHeadLineHolderOne hlHolder = (NewsListHeadLineHolderOne) viewHolder;
                if(info != null){
                    final List<ImageThumb> thumbList = info.getAlbumsList() ;
                    if(thumbList != null && thumbList.size() > 0){
                        hlHolder.mImageIv.setVisibility(View.VISIBLE) ;
                        hlHolder.mImageIv.setImageDrawable(context.getResources().getDrawable(R.mipmap.default_icon)) ;
                        ViewGroup.LayoutParams params = hlHolder.mImageIv.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT ;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT ;
                        hlHolder.mImageIv.setLayoutParams(params);

                        final String thumb = thumbList.get(0).getThumbImgUrl() ;
                        hlHolder.mImageIv.setTag(R.id.glide_image_tag ,thumb) ;

                        int wid = 0 ;
                        int hei = 0 ;

                        if(mItemWidMap.containsKey(thumb)){
                            Integer[] widHeiIt = mItemWidMap.get(thumb) ;
                            wid = widHeiIt[0] ;
                            hei = widHeiIt[1] ;
                        }
                        if(wid > 0 && hei > 0){
                            hlHolder.mImageIv.getLayoutParams().width = wid ;
                            hlHolder.mImageIv.getLayoutParams().height = hei ;
                            GlideUtils.loader(thumb ,hlHolder.mImageIv) ;
                        }else{
                            Glide.with(MyApplication.getInstance()).asBitmap().load(thumb).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource
                                        , @Nullable Transition<? super Bitmap> transition) {
                                    int width = resource.getWidth();
                                    int height = resource.getHeight();

                                    //图片的 宽 / 高
                                    float scale = (float) width / (float) height;
                                    int screenWidth = ResourceUtils.getScreenWidth(MyApplication.getInstance()) ;
                                    int imageWid ;
                                    int imageHei ;

                                    if(scale > 1F){//横图
                                        imageWid = (int) (screenWidth / ImageWidthUtils.getSingleImageItemSelfRote(true));
                                    }else{//竖图
                                        imageWid = (int) (screenWidth / ImageWidthUtils.getSingleImageItemSelfRote(false));
                                    }
                                    imageHei = (int) (imageWid / scale);

                                    if(imageHei > (ResourceUtils.getDeviceHeight(MyApplication.getInstance()) / 2)){
                                        imageHei = ResourceUtils.getDeviceHeight(MyApplication.getInstance()) / 2 ;
                                    }

                                    Integer[] widHeiIt = new Integer[]{imageWid,imageHei} ;
                                    mItemWidMap.put(thumb ,widHeiIt) ;

                                    if(thumb.equals(hlHolder.mImageIv.getTag(R.id.glide_image_tag))){
                                        if(hlHolder.mImageIv.getScaleType() != ImageView.ScaleType.CENTER_CROP){
                                            hlHolder.mImageIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        }

                                        ViewGroup.LayoutParams params = hlHolder.mImageIv.getLayoutParams();
                                        params.width = imageWid ;
                                        params.height = imageHei;
                                        hlHolder.mImageIv.setLayoutParams(params);

                                        GlideUtils.loader(thumb ,hlHolder.mImageIv) ;
                                    }
                                }
                            }) ;
                        }

                        hlHolder.mImageIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SkipUtils.toImageShowActivity(context ,thumb,0);
                            }
                        });

                    }else{
                        hlHolder.mImageIv.setVisibility(View.GONE) ;
                    }
                }
            }else if(viewHolder instanceof NewsListHeadLineHolderFour){
                NewsListHeadLineHolderFour hlHolder = (NewsListHeadLineHolderFour) viewHolder;

                if(info != null){
                    final List<ImageThumb> imageList = info.getAlbumsList() ;
                    if(imageList != null && imageList.size() == 4){
                        String imgUrlOne = imageList.get(0).getThumbImgUrl() ;
                        String imgUrlTwo = imageList.get(1).getThumbImgUrl() ;
                        String imgUrlThree = imageList.get(2).getThumbImgUrl() ;
                        String imgUrlFour = imageList.get(3).getThumbImgUrl() ;

                        GlideUtils.loader(imgUrlOne ,hlHolder.mImageOneIv) ;
                        GlideUtils.loader(imgUrlTwo ,hlHolder.mImageTwoIv) ;
                        GlideUtils.loader(imgUrlThree ,hlHolder.mImageThreeIv) ;
                        GlideUtils.loader(imgUrlFour ,hlHolder.mImageFourIv) ;

                        hlHolder.mImageOneIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(context ,imageList ,0) ;
                            }
                        });
                        hlHolder.mImageTwoIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(context ,imageList ,1) ;
                            }
                        });
                        hlHolder.mImageThreeIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(context ,imageList ,2) ;
                            }
                        });
                        hlHolder.mImageFourIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(context ,imageList ,3) ;
                            }
                        });
                    }
                }
            }else{
                NewsListHeadLineHolderMore hlHolder = (NewsListHeadLineHolderMore) viewHolder;
                if(info != null){
                    List<ImageThumb> thumbList = info.getAlbumsList() ;

                    RecyclerView rv = hlHolder.mImageRv ;
                    GridLayoutManager gm = new GridLayoutManager(context , 3) ;
                    gm.setAutoMeasureEnabled(true);
                    rv.setLayoutManager(gm) ;
                    rv.setFocusable(false) ;
                    rv.setAdapter(new AdapterCircleThreeImage(context, thumbList));
                }
            }
        }

        return view ;
    }


    private static class NewsListHeadLineHolderTrans extends BaseLvHeadLineHolder {
        private View mTransDtLay ;
        private ImageView mTransportIv ;
        private TextView mTransportTitleTv ;
        private TextView mTransportTypeTv ;
        private View mTransDeletedLay ;

        public NewsListHeadLineHolderTrans(View itemView) {
            super(itemView);

            mTransDtLay = itemView.findViewById(R.id.adapter_circle_item_transport_dt_lay);
            mTransportIv = itemView.findViewById(R.id.adapter_circle_item_transport_dt_iv);
            mTransportTitleTv = itemView.findViewById(R.id.adapter_circle_item_transport_dt_title_tv);
            mTransportTypeTv = itemView.findViewById(R.id.adapter_circle_item_transport_dt_type_tv);
            mTransDeletedLay = itemView.findViewById(R.id.adapter_circle_item_transport_dt_deleted_lay);
        }
    }

    private static class NewsListHeadLineHolderMore extends BaseLvHeadLineHolder{
        private RecyclerView mImageRv ;

        public NewsListHeadLineHolderMore(View itemView) {
            super(itemView);

            mImageRv = itemView.findViewById(R.id.adapter_circle_item_img_rv);

            mImageRv.setFocusable(false) ;
            mImageRv.setNestedScrollingEnabled(false) ;
        }
    }

    private static class NewsListHeadLineHolderFour extends BaseLvHeadLineHolder{

        private ImageView mImageOneIv ;
        private ImageView mImageTwoIv ;
        private ImageView mImageThreeIv ;
        private ImageView mImageFourIv ;

        public NewsListHeadLineHolderFour(View itemView , int imgWidHei) {
            super(itemView);

            mImageOneIv = itemView.findViewById(R.id.adapter_circle_item_one_iv) ;
            mImageTwoIv = itemView.findViewById(R.id.adapter_circle_item_two_iv) ;
            mImageThreeIv = itemView.findViewById(R.id.adapter_circle_item_three_iv) ;
            mImageFourIv = itemView.findViewById(R.id.adapter_circle_item_four_iv) ;

            mImageOneIv.getLayoutParams().width = imgWidHei ;
            mImageOneIv.getLayoutParams().height = imgWidHei ;
            mImageTwoIv.getLayoutParams().width = imgWidHei ;
            mImageTwoIv.getLayoutParams().height = imgWidHei ;
            mImageThreeIv.getLayoutParams().width = imgWidHei ;
            mImageThreeIv.getLayoutParams().height = imgWidHei ;
            mImageFourIv.getLayoutParams().width = imgWidHei ;
            mImageFourIv.getLayoutParams().height = imgWidHei ;
        }
    }

    private static class NewsListHeadLineHolderOne extends BaseLvHeadLineHolder{
        private ImageView mImageIv;

        public NewsListHeadLineHolderOne(View itemView) {
            super(itemView);

            mImageIv =  itemView.findViewById(R.id.adapter_circle_item_one_single_iv);
        }
    }

    static class BaseLvHeadLineHolder{
        View mMainLay ;
        ImageView mDeleteIv ;
        ImageView mAutherIv ;
        TextView mAutherNameTv ;
        TextView mTimeTv ;
        TextView mCompanyNameTv ;
        TextView mPostNameTv ;
        TextView mAttentionTv ;
        TextView mContentTv ;
        LinearLayout mSupportLay ;
        TextView mSuppoertCountTv ;
        ImageView mSuppoertIv ;
        LinearLayout mCommentLay ;
        TextView mCommentCountTv ;
        TextView mTransCountTv ;
        View mTransContentLay ;//转载
        View mShareLay ;

        public BaseLvHeadLineHolder(View itemView) {
            mMainLay =  itemView.findViewById(R.id.adapter_circle_item_main_lay);
            mDeleteIv = itemView.findViewById(R.id.adapter_circle_item_delete_iv);
            mAutherIv = itemView.findViewById(R.id.adapter_circle_item_user_iv);
            mAutherNameTv = itemView.findViewById(R.id.adapter_circle_item_user_name_tv);
            mTimeTv = itemView.findViewById(R.id.adapter_circle_item_send_time_tv);
            mCompanyNameTv = itemView.findViewById(R.id.adapter_circle_item_send_company_tv);
            mPostNameTv = itemView.findViewById(R.id.adapter_circle_item_send_post_tv);
            mAttentionTv = itemView.findViewById(R.id.adapter_circle_item_focus_tv);
            mContentTv = itemView.findViewById(R.id.adapter_circle_item_content_tv);
            mSupportLay = itemView.findViewById(R.id.adapter_circle_item_support_lay);
            mSuppoertCountTv = itemView.findViewById(R.id.adapter_circle_support_count_tv);
            mSuppoertIv = itemView.findViewById(R.id.adapter_circle_support_iv);
            mCommentLay = itemView.findViewById(R.id.adapter_circle_item_comment_lay);
            mCommentCountTv = itemView.findViewById(R.id.adapter_circle_comment_count_tv);
            mTransContentLay= itemView.findViewById(R.id.adapter_circle_item_trans_lay);
            mTransCountTv = itemView.findViewById(R.id.adapter_circle_trans_count_tv);
            mShareLay = itemView.findViewById(R.id.adapter_circle_item_share_lay);
        }
    }

}
