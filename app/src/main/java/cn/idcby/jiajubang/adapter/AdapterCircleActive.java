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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.idcby.commonlibrary.utils.ResourceUtils;
import cn.idcby.jiajubang.Bean.CircleTransInfo;
import cn.idcby.jiajubang.Bean.CommentCircleList;
import cn.idcby.jiajubang.Bean.ImageThumb;
import cn.idcby.jiajubang.Bean.UserActive;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.activity.CircleDetailActivity;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.interf.RecyclerViewClickListener;
import cn.idcby.jiajubang.utils.GlideUtils;
import cn.idcby.jiajubang.utils.ImageWidthUtils;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtil;
import cn.idcby.jiajubang.utils.StringUtils;

/**
 * 圈子
 * <p>
 * 2018-03-26 09:17:43
 * 圈子界面的点赞暂时不做点击操作，只展示
 * <p>
 * 2018-05-08 15:19:47
 * 图集展示仿微博
 * <p>
 * 2018-06-01 15:10:59
 * 去掉性别、年龄 ；添加 公司名、职位
 * <p>
 * 2018-07-05 16:54:26
 * 仿即刻app，把关注的外框去掉了
 * <p>
 * 2018-09-14 16:39:15
 * 添加转载相关功能：列表展示 和 数量展示
 */

public class AdapterCircleActive extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private List<UserActive> mNewsList;
    private boolean isLatest = false;
    private boolean isSelf = false;
    private RecyclerViewClickListener mClickListener;

    private Map<String, Integer[]> mItemWidMap = new HashMap<>();//保存某个图片的宽高，key imgUrl

    private int mMoreImageWidHei;
    private static final int VIEW_TYPE_ONE = 0;
    private static final int VIEW_TYPE_FOUR = 1;
    private static final int VIEW_TYPE_MORE = 2;
    private static final int VIEW_TYPE_TRANS = 3;

    public static final int CLICK_TYPE_FOCUS = 0;
    public static final int CLICK_TYPE_SUPPORT = 1;
    public static final int CLICK_TYPE_TRANS = 2;
    public static final int CLICK_TYPE_DELETE = 3;
    public static final int CLICK_TYPE_SHARE = 4;

    public AdapterCircleActive(Activity context, List<UserActive> mNewsList, boolean isLatest
            , RecyclerViewClickListener mClickListener) {
        this.context = context;
        this.mNewsList = mNewsList;
        this.isLatest = isLatest;
        this.mClickListener = mClickListener;
        this.mMoreImageWidHei = (ResourceUtils.getScreenWidth(context)
                - ResourceUtils.dip2px(context, 15) * 2 - ResourceUtils.dip2px(context, 2) * 2) / 3;
    }

    public AdapterCircleActive(Activity context, List<UserActive> mNewsList, boolean isLatest, boolean isSelf
            , RecyclerViewClickListener mClickListener) {
        this.context = context;
        this.mNewsList = mNewsList;
        this.isLatest = isLatest;
        this.isSelf = isSelf;
        this.mClickListener = mClickListener;
        this.mMoreImageWidHei = (ResourceUtils.getScreenWidth(context)
                - ResourceUtils.dip2px(context, 15) * 2 - ResourceUtils.dip2px(context, 2) * 2) / 3;
    }

    @Override
    public int getItemCount() {
        return null == mNewsList ? 0 : mNewsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = VIEW_TYPE_ONE;
        UserActive info = mNewsList.get(position);
        if (info != null) {
            if (info.isReprint()) {
                type = VIEW_TYPE_TRANS;
            } else {
                List<ImageThumb> thumbList = info.getAlbumsList();
                if (thumbList != null) {
                    int size = thumbList.size();
                    if (size <= 1) {
                        type = VIEW_TYPE_ONE;
                    } else if (size == 4) {
                        type = VIEW_TYPE_FOUR;
                    } else {
                        type = VIEW_TYPE_MORE;
                    }
                }
            }
        }

        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (VIEW_TYPE_TRANS == viewType) {
            return new NewsListHeadLineHolderTrans(LayoutInflater.from(context)
                    .inflate(R.layout.adapter_circle_item_transport, parent, false));
        }

        if (VIEW_TYPE_ONE == viewType) {
            return new NewsListHeadLineHolderOne(LayoutInflater.from(context)
                    .inflate(R.layout.adapter_circle_item_one, parent, false));
        }

        if (VIEW_TYPE_FOUR == viewType) {
            return new NewsListHeadLineHolderFour(LayoutInflater.from(context)
                    .inflate(R.layout.adapter_circle_item_four, parent, false), mMoreImageWidHei);
        }

        return new NewsListHeadLineHolderMore(LayoutInflater.from(context)
                .inflate(R.layout.adapter_circle_item_three, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final int realPosition = position;

        if (holder instanceof BaseCircleListHolder) {
            BaseCircleListHolder parentHolder = (BaseCircleListHolder) holder;

            final UserActive info = mNewsList.get(position);
            if (info != null) {
                String writerName = info.getCreateUserName();
                String writerPhoto = info.getCreateUserHeadIcon();
                final String content = info.getBodyContent();
                String data = info.getReleaseTime();
                String companyName = info.getCompanyName();
                String postName = info.getPostText();

                boolean isFocused = (1 == info.getIsFollow());
                boolean isSupport = info.isSupport();
                if (isSupport) {
                    parentHolder.mSuppoertIv.setImageResource(R.mipmap.ic_support_checked);
                } else {
                    parentHolder.mSuppoertIv.setImageResource(R.mipmap.ic_support_nomal);
                }

                boolean isV = info.isIndustryV();
                parentHolder.mAutherVIv.setVisibility(isV ? View.VISIBLE : View.INVISIBLE);

                parentHolder.mAutherNameTv.setText(writerName);
                parentHolder.mTimeTv.setText(data);
                parentHolder.mCompanyNameTv.setText(companyName);
                parentHolder.mPostNameTv.setText(postName);
                parentHolder.tvDistance.setText(StringUtils.getDistance(info.getDistance()));

                if (isLatest || isSelf) {
                    parentHolder.mAttentionTv.setVisibility(View.INVISIBLE);
                    if (isSelf) {
                        parentHolder.mDeleteIv.setVisibility(View.VISIBLE);
                    }
                } else {
                    parentHolder.mAttentionTv.setVisibility(View.VISIBLE);

                    parentHolder.mAttentionTv.setText(isFocused ? "已关注" : "关注");
                    parentHolder.mAttentionTv.setTextColor(context.getResources().getColor(isFocused
                            ? R.color.color_grey_88
                            : R.color.color_theme));
                }

                String supportCount = info.getLikeNumber() + "";
                String commentCount = info.getCommentNumber() + "";
                String transCount = info.getTransformNumber() + "";

                parentHolder.mCommentCountTv.setText(commentCount);
                parentHolder.mSuppoertCountTv.setText(supportCount);
                parentHolder.mTransCountTv.setText(transCount);

//                if (!"".equals(content)) {
//                    parentHolder.mContentTv.setText(content);
//
//                    if (parentHolder.mContentTv.getVisibility() != View.VISIBLE) {
//                        parentHolder.mContentTv.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    if (parentHolder.mContentTv.getVisibility() != View.GONE) {
//                        parentHolder.mContentTv.setVisibility(View.GONE);
//                    }
//                }
                ExpandableTextView etvContent=holder.itemView.findViewById(R.id.expand_text_view);
                if (!"".equals(content)) {
                    etvContent.setText(content);

                    if (etvContent.getVisibility() != View.VISIBLE) {
                       etvContent.setVisibility(View.VISIBLE);
                    }
                    etvContent.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
                        @Override
                        public void onExpandStateChanged(TextView textView, boolean isExpanded) {

                        }
                    });
                } else {
                    if (etvContent.getVisibility() != View.GONE) {
                        etvContent.setVisibility(View.GONE);
                    }
                }
                GlideUtils.loaderUser(writerPhoto, parentHolder.mAutherIv);

                List<CommentCircleList> commentList = info.getCommentList();
                if (commentList.size() > 0) {
                    parentHolder.mCommentContentLay.setVisibility(View.VISIBLE);

                    CommentCircleList commentInfo = commentList.get(0);
                    if (commentInfo != null) {
                        String user = commentInfo.getCreateUserName();
                        String commentContent = commentInfo.getCommentContent();

                        parentHolder.mCommentUserTv.setVisibility(View.GONE);
                        parentHolder.mCommentContentTv.setText(user + " : " + commentContent);
                    } else {
                        parentHolder.mCommentContentLay.setVisibility(View.GONE);
                    }
                } else {
                    parentHolder.mCommentContentLay.setVisibility(View.GONE);
                }

                parentHolder.mDeleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.onItemClickListener(CLICK_TYPE_DELETE, realPosition);
                        }
                    }
                });
                parentHolder.mAttentionTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.onItemClickListener(CLICK_TYPE_FOCUS, realPosition);
                        }
                    }
                });
                parentHolder.mShareLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.onItemClickListener(CLICK_TYPE_SHARE, realPosition);
                        }
                    }
                });
                parentHolder.mSupportLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.onItemClickListener(CLICK_TYPE_SUPPORT, realPosition);
                        }
                    }
                });
                parentHolder.mTransContentLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.onItemClickListener(CLICK_TYPE_TRANS, realPosition);
                        }
                    }
                });

                parentHolder.mCommentLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toDtIt = new Intent(context, CircleDetailActivity.class);
                        toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID, info.getPostID());
                        context.startActivity(toDtIt);
                    }
                });
                parentHolder.mAutherIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SkipUtils.toOtherUserInfoActivity(context, info.getCreateUserId());
                    }
                });
                parentHolder.mAutherNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SkipUtils.toOtherUserInfoActivity(context, info.getCreateUserId());
                    }
                });

                parentHolder.mMainLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toDtIt = new Intent(context, CircleDetailActivity.class);
                        toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID, info.getPostID());
                        context.startActivity(toDtIt);
                    }
                });
            }

            if (holder instanceof NewsListHeadLineHolderTrans) {
                final NewsListHeadLineHolderTrans hlHolder = (NewsListHeadLineHolderTrans) holder;

                if (info != null) {
                    final CircleTransInfo transInfo = info.getSourcePostInfo();
                    if (transInfo != null) {
                        hlHolder.mTransDtLay.setVisibility(View.VISIBLE);
                        hlHolder.mTransDeletedLay.setVisibility(View.GONE);

                        String name = transInfo.getBodyContent();
                        String imgUrl = transInfo.getImgUrl();
                        String categoryName = info.getCategoryTitle();//从列表上拿，理论上转载的分类跟被转载的是一致的

                        hlHolder.mTransportTitleTv.setText(name);
                        hlHolder.mTransportTypeTv.setText(categoryName);
                        GlideUtils.loader(imgUrl, hlHolder.mTransportIv);
                    } else {
                        hlHolder.mTransDtLay.setVisibility(View.GONE);
                        hlHolder.mTransDeletedLay.setVisibility(View.VISIBLE);
                    }

                    hlHolder.mTransDtLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //要不要点击跳转到转载的详细？
                            Intent toDtIt = new Intent(context, CircleDetailActivity.class);
                            toDtIt.putExtra(SkipUtils.INTENT_ARTICLE_ID, transInfo.getPostID());
                            context.startActivity(toDtIt);

                        }
                    });
                }
            } else if (holder instanceof NewsListHeadLineHolderOne) {
                final NewsListHeadLineHolderOne hlHolder = (NewsListHeadLineHolderOne) holder;

                if (info != null) {
                    final List<ImageThumb> thumbList = info.getAlbumsList();
                    if (thumbList != null && thumbList.size() > 0) {
                        hlHolder.mImageIv.setVisibility(View.VISIBLE);
                        hlHolder.mImageIv.setImageDrawable(context.getResources().getDrawable(R.mipmap.default_icon));
                        ViewGroup.LayoutParams params = hlHolder.mImageIv.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        hlHolder.mImageIv.setLayoutParams(params);

                        final String thumb = thumbList.get(0).getThumbImgUrl();
                        hlHolder.mImageIv.setTag(R.id.glide_image_tag, thumb);

                        int wid = 0;
                        int hei = 0;

                        if (mItemWidMap.containsKey(thumb)) {
                            Integer[] widHeiIt = mItemWidMap.get(thumb);
                            wid = widHeiIt[0];
                            hei = widHeiIt[1];
                        }
                        if (wid > 0 && hei > 0) {
                            hlHolder.mImageIv.getLayoutParams().width = wid;
                            hlHolder.mImageIv.getLayoutParams().height = hei;
                            GlideUtils.loader(thumb, hlHolder.mImageIv);
                        } else {
                            Glide.with(MyApplication.getInstance()).asBitmap().load(thumb).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource
                                        , @Nullable Transition<? super Bitmap> transition) {
                                    int width = resource.getWidth();
                                    int height = resource.getHeight();

                                    //图片的 宽 / 高
                                    float scale = (float) width / (float) height;
                                    int screenWidth = ResourceUtils.getScreenWidth(MyApplication.getInstance());
                                    int imageWid;
                                    int imageHei;

                                    if (scale > 1F) {//横图
                                        imageWid = (int) (screenWidth / ImageWidthUtils.getSingleImageItemSelfRote(true));
                                    } else {//竖图
                                        imageWid = (int) (screenWidth / ImageWidthUtils.getSingleImageItemSelfRote(false));
                                    }
                                    imageHei = (int) (imageWid / scale);

                                    if (imageHei > (ResourceUtils.getDeviceHeight(MyApplication.getInstance()) / 2)) {
                                        imageHei = ResourceUtils.getDeviceHeight(MyApplication.getInstance()) / 2;
                                    }

                                    Integer[] widHeiIt = new Integer[]{imageWid, imageHei};
                                    mItemWidMap.put(thumb, widHeiIt);

                                    if (thumb.equals(hlHolder.mImageIv.getTag(R.id.glide_image_tag))) {
                                        if (hlHolder.mImageIv.getScaleType() != ImageView.ScaleType.CENTER_CROP) {
                                            hlHolder.mImageIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        }

                                        ViewGroup.LayoutParams params = hlHolder.mImageIv.getLayoutParams();
                                        params.width = imageWid;
                                        params.height = imageHei;
                                        hlHolder.mImageIv.setLayoutParams(params);

                                        GlideUtils.loader(thumb, hlHolder.mImageIv);
                                    }
                                }
                            });
                        }

                        hlHolder.mImageIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SkipUtils.toImageShowActivity(context, thumb, 0);
                            }
                        });

                    } else {
                        hlHolder.mImageIv.setVisibility(View.GONE);
                    }
                }
            } else if (holder instanceof NewsListHeadLineHolderFour) {
                NewsListHeadLineHolderFour hlHolder = (NewsListHeadLineHolderFour) holder;
                if (info != null) {
                    final List<ImageThumb> imageList = info.getAlbumsList();
                    if (imageList != null && imageList.size() == 4) {
                        String imgUrlOne = imageList.get(0).getThumbImgUrl();
                        String imgUrlTwo = imageList.get(1).getThumbImgUrl();
                        String imgUrlThree = imageList.get(2).getThumbImgUrl();
                        String imgUrlFour = imageList.get(3).getThumbImgUrl();

                        GlideUtils.loader(imgUrlOne, hlHolder.mImageOneIv);
                        GlideUtils.loader(imgUrlTwo, hlHolder.mImageTwoIv);
                        GlideUtils.loader(imgUrlThree, hlHolder.mImageThreeIv);
                        GlideUtils.loader(imgUrlFour, hlHolder.mImageFourIv);

                        hlHolder.mImageOneIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(context, imageList, 0);
                            }
                        });
                        hlHolder.mImageTwoIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(context, imageList, 1);
                            }
                        });
                        hlHolder.mImageThreeIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(context, imageList, 2);
                            }
                        });
                        hlHolder.mImageFourIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SkipUtils.toImageShowActivityWithThumb(context, imageList, 3);
                            }
                        });
                    }
                }
            } else if (holder instanceof NewsListHeadLineHolderMore) {
                NewsListHeadLineHolderMore hlHolder = (NewsListHeadLineHolderMore) holder;

                if (info != null) {
                    List<ImageThumb> thumbList = info.getAlbumsList();

                    RecyclerView rv = hlHolder.mImageRv;

                    GridLayoutManager gm = new GridLayoutManager(context, 3);
                    gm.setAutoMeasureEnabled(true);
                    rv.setLayoutManager(gm);
                    rv.setFocusable(false);
                    rv.setAdapter(new AdapterCircleThreeImage(context, thumbList));
                }
            }
        }
    }

    private static class NewsListHeadLineHolderTrans extends BaseCircleListHolder {
        private View mTransDtLay;
        private ImageView mTransportIv;
        private TextView mTransportTitleTv;
        private TextView mTransportTypeTv;
        private View mTransDeletedLay;

        public NewsListHeadLineHolderTrans(View itemView) {
            super(itemView);

            mTransDtLay = itemView.findViewById(R.id.adapter_circle_item_transport_dt_lay);
            mTransportIv = itemView.findViewById(R.id.adapter_circle_item_transport_dt_iv);
            mTransportTitleTv = itemView.findViewById(R.id.adapter_circle_item_transport_dt_title_tv);
            mTransportTypeTv = itemView.findViewById(R.id.adapter_circle_item_transport_dt_type_tv);
            mTransDeletedLay = itemView.findViewById(R.id.adapter_circle_item_transport_dt_deleted_lay);
        }
    }

    private static class NewsListHeadLineHolderMore extends BaseCircleListHolder {
        private RecyclerView mImageRv;

        public NewsListHeadLineHolderMore(View itemView) {
            super(itemView);

            mImageRv = itemView.findViewById(R.id.adapter_circle_item_img_rv);

            mImageRv.setFocusable(false);
            mImageRv.setNestedScrollingEnabled(false);
        }
    }

    private static class NewsListHeadLineHolderFour extends BaseCircleListHolder {
        private ImageView mImageOneIv;
        private ImageView mImageTwoIv;
        private ImageView mImageThreeIv;
        private ImageView mImageFourIv;

        public NewsListHeadLineHolderFour(View itemView, int imgWidHei) {
            super(itemView);

            mImageOneIv = itemView.findViewById(R.id.adapter_circle_item_one_iv);
            mImageTwoIv = itemView.findViewById(R.id.adapter_circle_item_two_iv);
            mImageThreeIv = itemView.findViewById(R.id.adapter_circle_item_three_iv);
            mImageFourIv = itemView.findViewById(R.id.adapter_circle_item_four_iv);

            mImageOneIv.getLayoutParams().width = imgWidHei;
            mImageOneIv.getLayoutParams().height = imgWidHei;
            mImageTwoIv.getLayoutParams().width = imgWidHei;
            mImageTwoIv.getLayoutParams().height = imgWidHei;
            mImageThreeIv.getLayoutParams().width = imgWidHei;
            mImageThreeIv.getLayoutParams().height = imgWidHei;
            mImageFourIv.getLayoutParams().width = imgWidHei;
            mImageFourIv.getLayoutParams().height = imgWidHei;
        }
    }

    private static class NewsListHeadLineHolderOne extends BaseCircleListHolder {
        private ImageView mImageIv;

        public NewsListHeadLineHolderOne(View itemView) {
            super(itemView);

            mImageIv = itemView.findViewById(R.id.adapter_circle_item_one_single_iv);
        }
    }

    private static class BaseCircleListHolder extends RecyclerView.ViewHolder {
        View mMainLay;
        ImageView mDeleteIv;
        ImageView mAutherIv;
        TextView mAutherNameTv;
        View mAutherVIv;
        TextView mTimeTv;
        TextView mCompanyNameTv;
        TextView mPostNameTv;
        TextView mAttentionTv;
        TextView mContentTv;
        LinearLayout mSupportLay;
        ImageView mSuppoertIv;
        TextView mSuppoertCountTv;
        LinearLayout mCommentLay;
        TextView mCommentCountTv;
        View mCommentContentLay;
        TextView mCommentUserTv; //实现评价人名字加粗效果，为跟IOS一致，暂时屏蔽了，如果需求改了，再做相关判断
        View mTransContentLay;//转载
        TextView mTransCountTv; //转载数量
        TextView mCommentContentTv;
        TextView tvDistance;
        View mShareLay;
        ExpandableTextView etvContent;

        BaseCircleListHolder(View itemView) {
            super(itemView);

            mMainLay = itemView.findViewById(R.id.adapter_circle_item_main_lay);
            mDeleteIv = itemView.findViewById(R.id.adapter_circle_item_delete_iv);
            mAutherIv = itemView.findViewById(R.id.adapter_circle_item_user_iv);
            mAutherVIv = itemView.findViewById(R.id.adapter_circle_item_user_v_iv);
            mAutherNameTv = itemView.findViewById(R.id.adapter_circle_item_user_name_tv);
            mTimeTv = itemView.findViewById(R.id.adapter_circle_item_send_time_tv);
            mCompanyNameTv = itemView.findViewById(R.id.adapter_circle_item_send_company_tv);
            mPostNameTv = itemView.findViewById(R.id.adapter_circle_item_send_post_tv);
            mAttentionTv = itemView.findViewById(R.id.adapter_circle_item_focus_tv);
            mContentTv = itemView.findViewById(R.id.adapter_circle_item_content_tv);
//            etvContent=itemView.findViewById(R.id.expand_text_view);
            mSupportLay = itemView.findViewById(R.id.adapter_circle_item_support_lay);
            mSuppoertIv = itemView.findViewById(R.id.adapter_circle_support_iv);
            mSuppoertCountTv = itemView.findViewById(R.id.adapter_circle_support_count_tv);
            mCommentLay = itemView.findViewById(R.id.adapter_circle_item_comment_lay);
            mCommentCountTv = itemView.findViewById(R.id.adapter_circle_comment_count_tv);
            mCommentContentLay = itemView.findViewById(R.id.adapter_circle_item_bot_comment_content_lay);
            mCommentUserTv = itemView.findViewById(R.id.adapter_circle_item_bot_comment_name_tv);
            mCommentContentTv = itemView.findViewById(R.id.adapter_circle_item_bot_comment_content_tv);
            mTransContentLay = itemView.findViewById(R.id.adapter_circle_item_trans_lay);
            mTransCountTv = itemView.findViewById(R.id.adapter_circle_trans_count_tv);
            mShareLay = itemView.findViewById(R.id.adapter_circle_item_share_lay);
            tvDistance=itemView.findViewById(R.id.adapter_circle_item_distance);
        }
    }
}