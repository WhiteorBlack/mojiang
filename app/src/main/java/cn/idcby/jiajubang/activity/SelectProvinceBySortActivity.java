package cn.idcby.jiajubang.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import cn.idcby.commonlibrary.base.BaseActivity;
import cn.idcby.commonlibrary.utils.ToastUtils;
import cn.idcby.jiajubang.Bean.Address;
import cn.idcby.jiajubang.Bean.SortItem;
import cn.idcby.jiajubang.Bean.SortProvinceBean;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.application.MyApplication;
import cn.idcby.jiajubang.service.LocationService;
import cn.idcby.jiajubang.utils.NetUtils;
import cn.idcby.jiajubang.utils.ParaUtils;
import cn.idcby.jiajubang.utils.RequestListCallBack;
import cn.idcby.jiajubang.utils.SkipUtils;
import cn.idcby.jiajubang.utils.StringUtils;
import cn.idcby.jiajubang.utils.Urls;
import cn.idcby.jiajubang.view.MyLetterListView;
import de.halfbit.pinnedsection.PinnedSectionListView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 选择省份
 *
 * 2018-05-31 17:31:54
 * 记不清为什么加了  全国 选项 ，为与ios一致，去掉全国选项
 */
public class SelectProvinceBySortActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
	private PinnedSectionListView personList;
	private TextView overlay; // 对话框首字母textview
	private MyLetterListView letterListView; // A-Z listview

    private ListAdapter mAdapter ;

	//当前定位城市
	private TextView mLocationTv ;

	private HashMap<String, Integer> alphaIndexer = new HashMap<>();// 存放存在的汉语拼音首字母和与之对应的列表位置
	private Handler handler;
	private OverlayThread overlayThread; // 显示首字母对话框

	private String currentCity; // 用于保存定位到的城市
	private LocationService mLocationService;

	private static final int REQUEST_CODE_CHOOSE_CITY = 1000 ;

	@Override
	public int getLayoutID() {
		return R.layout.activity_select_province_by_sort;
	}

	@Override
	public void initView() {
		handler = new Handler();
		overlayThread = new OverlayThread();

		personList = findViewById(R.id.acti_select_sort_city_lv);
		letterListView = findViewById(R.id.MyLetterListView01);

		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		personList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SortItem items = mAdapter.getItem(position - personList.getHeaderViewsCount()) ;
                if(items != null && items.type == SortItem.ITEM){
                    SelectProvinceBySortCityActivity.launch(mActivity,"" + items.getCityBean().getCityId()
                            ,items.getCityBean().getCityName() ,REQUEST_CODE_CHOOSE_CITY);
                }
			}
		});

		initOverlay();
		initHeaderView() ;
	}

	/**
	 * 初始化header
	 */
	private void initHeaderView(){
		View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_choose_sort_city_lay,null) ;
		mLocationTv = headerView.findViewById(R.id.header_choose_sort_city_location_tv) ;
		View allTv = headerView.findViewById(R.id.header_choose_sort_city_all_tv) ;
//		View refreshIv = headerView.findViewById(R.id.header_choose_sort_city_refresh_iv) ;
//		refreshIv.setOnClickListener(this);
		mLocationTv.setOnClickListener(this);
        headerView.setOnClickListener(this);
        allTv.setOnClickListener(this);

		personList.addHeaderView(headerView) ;

		startLocations() ;
	}


	@Override
	public void initData() {
		getAllCity();
	}

	@Override
	public void initListener() {

	}

	@Override
	public void dealOhterClick(View view) {
		int vId = view.getId() ;

//		if(R.id.header_choose_sort_city_refresh_iv == vId){
//			startLocations() ;
//		}else
        if(R.id.header_choose_sort_city_location_tv == vId){
            if(!"".equals(StringUtils.convertNull(currentCity))){
                Intent intent = new Intent();
                intent.putExtra(SkipUtils.LOCATION_TYPE, SkipUtils.LOCATION_TYPE_CITY);
                intent.putExtra(SkipUtils.LOCATION_CONTENT_NAME, currentCity);
                intent.putExtra(SkipUtils.LOCATION_CONTENT_ID, "");

                setResult(RESULT_OK ,intent) ;
            }
            finish() ;
		}else if(R.id.header_choose_sort_city_all_tv == vId){//全国
            Intent intent = new Intent();
            intent.putExtra(SkipUtils.LOCATION_TYPE, SkipUtils.LOCATION_TYPE_ALL);
            intent.putExtra(SkipUtils.LOCATION_CONTENT_NAME, "全国");
            intent.putExtra(SkipUtils.LOCATION_CONTENT_ID, "");

            setResult(RESULT_OK ,intent) ;
            finish() ;
		}
	}



	public class ListAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter, SectionIndexer {
		private Context context;
		private LayoutInflater inflater;
		private List<SortProvinceBean> list;
		private List<SortItem> itemData;
		private SortItem[] adapterSections;//头标记数组


		public ListAdapter(Context context, List<SortProvinceBean> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			this.context = context;
			initSection();
		}

		private void initSection(){
			int dataSize = list.size() ;

			itemData = new ArrayList<>();
			adapterSections = new SortItem[dataSize];
			//数据准备
			int sectionPosition = 0, listPosition = 0;
			for (int i = 0; i < dataSize; i++) {
				SortProvinceBean cityBean = list.get(i) ;

				//添加头信息，将头标记和数据传入
				SortItem section = new SortItem(SortItem.SECTION, cityBean);
				section.sectionPosition = sectionPosition;
				section.listPosition = listPosition++;

				alphaIndexer.put(cityBean.getCityName() ,section.listPosition) ;

				//头标记组中城市id的标记相对应放入该城市的Item实例
				adapterSections[section.sectionPosition] = section;
				itemData.add(section);
				//当前城市的下级城市
				int childSize = cityBean.getSubordinateList().size() ;
				for (int j = 0; j < childSize; j++) {
					//下级城市为普通item，所以传入Item.ITEM
					SortItem item = new SortItem(SortItem.ITEM, cityBean.getSubordinateList().get(j));
					item.sectionPosition = sectionPosition;
					item.listPosition = listPosition++;
					itemData.add(item);
				}

				sectionPosition++;
			}
		}

		@Override
		public int getCount() {
			return itemData.size();
		}

		@Override
		public SortItem getItem(int position) {
			return itemData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SortItem item = itemData.get(position);// 从集合中获取当前行的数据

			if(item.type == SortItem.SECTION){
				SectionViewHolder holder;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.list_item_for_select_city_section, null);
					holder = new SectionViewHolder();
					holder.alpha = (TextView) convertView
							.findViewById(R.id.alpha);
					convertView.setTag(holder);
				} else {
					holder = (SectionViewHolder) convertView.getTag();
				}

				holder.alpha.setText(item.getCityBean().getCityName());
			}else{
				ViewHolder holder;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.list_item_for_select_city, null);
					holder = new ViewHolder();
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.name.setText(item.getCityBean().getCityName());
			}

			return convertView;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		//返回每一个视图的类型
		@Override
		public int getItemViewType(int position) {
			return getItem(position).type;
		}


		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == SortItem.SECTION ;
		}

		@Override
		public SortItem[] getSections() {
			return adapterSections;
		}

		@Override
		public int getPositionForSection(int sectionIndex) {
			//以免抛出异常
			if (sectionIndex >= adapterSections.length) {
				sectionIndex = adapterSections.length - 1;
			}
			//返回当前头集合的头id
			return adapterSections[sectionIndex].listPosition;
		}

		@Override
		public int getSectionForPosition(int position) {
			if (position >= getCount()) {
				position = getCount() - 1;
			}
			//返回当前item中保存的头id
			return getItem(position).sectionPosition;
		}

		private class ViewHolder {
			TextView name; // 城市名字
		}
		private class SectionViewHolder {
			TextView alpha; // 首字母标题
		}
	}


	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay_for_select_city, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}


	private class LetterListViewListener implements
			MyLetterListView.OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				personList.setSelection(position + personList.getHeaderViewsCount());
				overlay.setText(s);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1000);
			}
		}
	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	// 获得汉语拼音首字母
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		}else {
			return "#";
		}
	}

	/**
	 * 全部城市
	 **/
	private void getAllCity() {
		Map<String, String> paramMap = ParaUtils.getPara(mContext) ;

		NetUtils.getDataFromServerByPost(mContext, Urls.GET_PROVINCE, paramMap
				, new RequestListCallBack<Address>("getAllCity" ,mContext ,Address.class) {
					@Override
					public void onSuccessResult(List<Address> bean) {
						int size = bean.size() ;
						String[] saa = letterListView.getTipsB() ;
						int length = saa.length ;
						List<SortProvinceBean> data = new ArrayList<>() ;
						//处理城市列表，添加部分头
						for(int x = 0 ; x < length ; x ++){
							String curA = saa[x] ;

							List<SortProvinceBean> beijingList = new ArrayList<>();
							for (int i = 0; i < size; i++) {
								Address address = bean.get(i) ;
								if(getAlpha(address.getPinyi()).startsWith(curA)){
									SortProvinceBean cityBean = new SortProvinceBean();
									cityBean.setCityId(Integer.valueOf(address.AreaId));
									cityBean.setCityName(address.AreaName);
									beijingList.add(cityBean);
								}
							}

							SortProvinceBean cityBean = new SortProvinceBean();
							cityBean.setCityId(0);
							cityBean.setCityName(saa[x]);
							cityBean.setSubordinateList(beijingList);
							data.add(cityBean);
						}

						mAdapter = new ListAdapter(mContext , data) ;
						personList.setAdapter(mAdapter) ;
					}
					@Override
					public void onErrorResult(String str) {
					}
					@Override
					public void onFail(Exception e) {
					}
				});
	}


	/**
	 * 开始定位
	 */
	private void startLocations() {
		mLocationTv.setText("正在定位");

		if(!EasyPermissions.hasPermissions(mContext
				, Manifest.permission.READ_PHONE_STATE
				, Manifest.permission.ACCESS_COARSE_LOCATION
				, Manifest.permission.ACCESS_FINE_LOCATION
				, Manifest.permission.READ_EXTERNAL_STORAGE
				,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

			EasyPermissions.requestPermissions(mActivity
					,"应用需要定位权限来获取当前位置，拒绝会导致部分功能异常",500
					, Manifest.permission.READ_PHONE_STATE
					, Manifest.permission.ACCESS_COARSE_LOCATION
					, Manifest.permission.ACCESS_FINE_LOCATION
					,Manifest.permission.WRITE_EXTERNAL_STORAGE);
			return ;
		}

		// -----------location config ------------
		mLocationService = ((MyApplication) getApplication()).locationService;
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		mLocationService.registerListener(mListener);
		//注册监听
		mLocationService.setLocationOption(mLocationService.getDefaultLocationClientOption());
		mLocationService.start() ;
	}

	/*****
	 *
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDLocationListener mListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				String mCurSheng = location.getProvince() ;
				final String mCurShi = location.getCity() ;

				if(mCurShi != null && mCurSheng != null){
					currentCity = mCurShi ;

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mLocationTv.setText(mCurShi);
						}
					});

					mLocationService.unregisterListener(mListener); //注销掉监听
					mLocationService.stop(); //停止定位服务

					MyApplication.updateCurLocation(location) ;
				}
			}
		}
	};


	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
		if(500 == requestCode){
			startLocations() ;
		}
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {
		ToastUtils.showToast(mContext ,"拒绝了相关权限，会导致部分功能失败");
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults ,this);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE_CHOOSE_CITY == requestCode){
            if(RESULT_OK == resultCode && data != null){
                setResult(RESULT_OK ,data);
                finish() ;
            }
        }

    }

    @Override
	protected void onDestroy() {
		super.onDestroy();

		if(handler != null){
			handler.removeCallbacksAndMessages(null) ;
		}


		if(mLocationService != null){
			mLocationService.unregisterListener(mListener); //注销掉监听
			mLocationService.stop(); //停止定位服务
		}

	}
}