package cn.idcby.jiajubang.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.util.ArrayList;
import java.util.List;

import cn.idcby.commonlibrary.utils.AppManager;
import cn.idcby.commonlibrary.utils.LogUtils;
import cn.idcby.commonlibrary.utils.StatusBarUtil;
import cn.idcby.jiajubang.R;
import cn.idcby.jiajubang.adapter.AdapterChoosePoi;

/**
 * 地图选点
 */
public class ChooseMapLocationActivity extends AppCompatActivity implements View.OnClickListener {
	private MapView mMapView = null;
	private TextView mSubTv = null ;

    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();

    private static BDLocation lastLocation = null;
    private ProgressDialog progressDialog;
	private BaiduMap mBaiduMap;

    private GeoCoder mSearch ;
    private List<PoiInfo> mPoiList = new ArrayList<>() ;
    private AdapterChoosePoi mPoiAdapter ;


    public static void launch(Activity context ,Bundle bundle ,int requestCode){
	    Intent toCmIt = new Intent(context ,ChooseMapLocationActivity.class) ;
	    if(bundle != null){
            toCmIt.putExtra("locationInfo" ,bundle) ;
        }
	    context.startActivityForResult(toCmIt ,requestCode) ;
    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_choose_map_location);

        AppManager.getAppManager().addActivity(this);
        StatusBarUtil.resetStatusBarColor(this) ;

		mMapView = findViewById(R.id.bmapView);
		View mBackIv = findViewById(R.id.back);
		mSubTv = findViewById(R.id.acti_choose_map_location_sub_tv);
        mBackIv.setOnClickListener(this);
        mSubTv.setOnClickListener(this);

        ListView mLv = findViewById(R.id.acti_choose_map_location_lv) ;
        mPoiAdapter = new AdapterChoosePoi(this ,mPoiList) ;
        mLv.setAdapter(mPoiAdapter);
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PoiInfo info = mPoiList.get(i) ;

                Intent intent = new Intent() ;
                intent.putExtra("latitude", "" + info.location.latitude);
                intent.putExtra("longitude", "" + info.location.longitude);
                intent.putExtra("address", info.address + info.name);
                intent.putExtra("provinceName", info.province);//此处的province
                intent.putExtra("cityName", info.city);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mSearch = GeoCoder.newInstance();

		LocationMode mCurrentMode = LocationMode.NORMAL;
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		initMapView();

        mMapView = new MapView(this, new BaiduMapOptions());
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));

        Bundle bundle = getIntent().getBundleExtra("locationInfo") ;
        if(bundle != null && bundle.getString("latitude") != null
                && bundle.getString("longitude") != null){
            showMap(bundle.getString("latitude"),bundle.getString("longitude")) ;
        }else{
            showMapWithLocationClient();
        }
	}

    /**
     * 根据之前传递的经纬度，回到指定界面
     * @param latitude lat
     * @param longtitude longt
     */
    private void showMap(String latitude, String longtitude) {
		LatLng llA = new LatLng(Double.valueOf(latitude), Double.valueOf(longtitude));
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
		mBaiduMap.animateMapStatus(u);

		searchPoi(llA);
	}

    /**
     * 开始定位
     */
	private void showMapWithLocationClient() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				finish();
			}
		});

		progressDialog.show();

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// open gps
		// option.setCoorType("bd09ll"); 
		// Johnson change to use gcj02 coordination. chinese national standard
		// so need to conver to bd09 everytime when draw on baidu map
		option.setCoorType("gcj02");
		option.setScanSpan(30000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start() ;
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)
			mLocClient.stop();
		mMapView.onDestroy();
        mSearch.destroy();

		AppManager.getAppManager().finishActivity(this);

		super.onDestroy();
	}
	private void initMapView() {
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                LocationMode.NORMAL, true, BitmapDescriptorFactory .fromResource(R.drawable.ease_chat_location_normal),
                0xAAFFFF88, 0xAA00FF00));
        mBaiduMap.setMyLocationEnabled(true);
        mMapView.setLongClickable(true);

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                searchPoi(mapStatus.target) ;
            }
        });

        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    LogUtils.showLog("MapSearchPoi" ,"未找到结果");
                    return;
                }

                //获取反向地理编码结果

                //获取POI检索结果
                List<PoiInfo> poiInfos = reverseGeoCodeResult.getPoiList() ;
                if(poiInfos != null){
                    mPoiList.clear();
                    mPoiList.addAll(poiInfos) ;
                    mPoiAdapter.notifyDataSetChanged() ;
                }
            }
        });
	}

	private void searchPoi(LatLng latLng){
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(latLng));
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.acti_choose_map_location_sub_tv == vId){

        }else if(R.id.back == vId){
            finish() ;
        }
    }

    /**
	 * format new location to string and show on screen
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}

			mSubTv.setEnabled(true);

			if (progressDialog != null) {
				progressDialog.dismiss();
			}

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					return;
				}
			}

			lastLocation = location;

			LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			CoordinateConverter converter= new CoordinateConverter();
			converter.coord(llA);
			converter.from(CoordinateConverter.CoordType.COMMON);
			LatLng convertLatLng = converter.convert();

			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
			mBaiduMap.animateMapStatus(u);

            searchPoi(convertLatLng) ;
		}
	}
}
