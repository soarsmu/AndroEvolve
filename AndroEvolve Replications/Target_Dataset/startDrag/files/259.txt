package com.tfl.golf.activities;

import android.content.ClipData;
import android.content.ComponentName;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewStub;
import android.widget.Toast;

import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapCircleData;
import com.nhn.android.maps.overlay.NMapCircleStyle;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import com.tfl.golf.Constants;
import com.tfl.golf.R;
import com.tfl.golf.model.AppConfig;
import com.tfl.golf.model.FairwayData;
import com.tfl.golf.model.HoleData;
import com.tfl.golf.model.Position;
import com.tfl.golf.model.TraceLog;
import com.tfl.golf.task.InqueryElevationTask;
import com.tfl.golf.task.ProgressTask;
import com.tfl.golf.util.Tlog;
import com.tfl.golf.util.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * Daum맵 기반 메인화면
 *
 * @author dkchoi
 */
public class NaverMapActivity extends AbstractMapActivity implements NMapView.OnMapStateChangeListener, NMapView.OnMapViewTouchEventListener {

    private NMapView mMapView;

    private NGeoPoint mPinPoint;
    private NGeoPoint mTeeboxPoint;
    private NGeoPoint mAimPoint;

    private List<NGeoPoint> mNextAimPoints;

    private NMapPOIitem mAimMarker;
    private NMapPOIitem mPinMarker;
    private NMapPOIitem mTeeboxMarker;
    private NMapPOIitem mMarker;

    private NMapPOIdataOverlay mPOIdataOverlay;
    private NMapPOIdata mPOIdatas;


    private List<NMapPOIitem> mIpList;
    private List<NMapPOIitem> mRestaurantList;
    private List<NMapPOIitem> mCircleList;

    protected boolean isFinishingOnCreation;

    private NMapCircleStyle mCircleStyle;
    private NMapCircleData mMap_50;
    private NMapCircleData mMap_100;
    private NMapCircleData mMap_150;
    private NMapCircleData mMap_200;

    private NMapPathData mAimToPin;
    private NMapPathData mTeeboxToAim;

    private NMapPathData mOutbound;
    private NMapPathData mFairwayBound1;
    private NMapPathData mFairwayBound2;
    private NMapPathData mFairwayBound3;
    private NMapPathData mGreenBound;


    private NMapPathDataOverlay mPathdataOverlay;
    private NMapPathData mPathDatas;


    private boolean mIsAimSelected = false;
    private boolean mIsPinSelected = false;
    private boolean isMapInitialized = false;
    private boolean isAfterSerivceOnConnected = false;

    private NMapController mMapController;
    private NMapOverlayManager mOverlayManager;
    private NMapViewerResourceProvider mResourceProvider;

    private String currentAddress;
    private static final int SEARCH_INDEX = 10;
    private static final double AIMMAKER_OFFSET = 0.00010;
    private static final double PINMAKER_OFFSET = 0.00005;
    private boolean bFlagHoues;
    private SearchMapParser mSearchMapParser;
    private int searchedRestaurantIndex;
    private RestaurantData[] restaurantData = new RestaurantData[SEARCH_INDEX];


    private NaverMapFragment mMapFrag;
    View mBaseView;
    TextToSpeech mtts;

    private boolean btouchEvent;
    public int global_level;
    //private HistoryBallonAdapter mHistoryBallonAdapter;

    @Override
    public void firstMove() {

        if (!isMapInitialized) return;

        if (isGameMode()) {
            findLocationInGameMode();
        } else if (isTestMode()) {
            return;
        } else if (isSearchMode()) {
        } else {
            findCurrentLocation();
        }
    }

    @Override
    public void moveAndStop() {
        firstMove();
    }

    @Override
    public boolean isMapInitialize() {
        return isMapInitialized;
    }

    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
        // TODO Auto-generated method stub
        super.onServiceConnected(className, binder);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isMapInitialized) return;
                isAfterSerivceOnConnected = true;
            }
        });
        ProgressTask.dissmissTransactionMode();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (isFinishMode()) {
            return;
        }

        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            initComponent();

            init();

            registerControl();

            initMap(savedInstanceState);


            //음성을 위한 객체창조
            if (mtts != null) {
                mtts.shutdown();
                mtts = null;
            }
            mtts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {

                }
            });
            bFlag = true;

        } catch (Exception ex) {
            Tlog.e(ex);
        }


    }

    public boolean bStringSpeaker(TextToSpeech tts, String string) {
        if (tts == null || (string.length() == 0)) return false;

        int result = tts.setLanguage(Locale.KOREA);

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Language not supported", Toast.LENGTH_LONG).show();
            Log.e("TTS", "Language is not supported");
            return false;
        }

        tts.speak(string, TextToSpeech.QUEUE_FLUSH, null);
        return true;
    }


    /**
     * 다음 맵 초기화
     */
    @Override
    public void initMap(Bundle savedInstanceState) {

        bFlagHoues = false;
        ViewStub stub = (ViewStub) findViewById(R.id.maplayout);
        stub.setLayoutResource(R.layout.naver_layout);
        mBaseView = stub.inflate();

        mMapFrag = ((NaverMapFragment) getFragmentManager().findFragmentById(R.id.map));

        mMapView = (NMapView) mBaseView.findViewById(R.id.mapView);

        mMapFrag.getMapContext().setupMapView(mMapView);

        mMapView.setClientId("i9jZh4arahdGDgQ4UVWf");


        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        mMapView.setAutoRotateEnabled(true, false);

        // register listener for map state changes
        mMapView.setOnMapStateChangeListener(this);
        mMapView.setOnMapViewTouchEventListener(this);


        mMapController = mMapView.getMapController();
        initializeSearch();

        mBaseView.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                Log.d("===", mIsAimSelected + ":" + event.getAction());

                if (event.getAction() == DragEvent.ACTION_DRAG_ENDED || event.getAction() == DragEvent.ACTION_DROP) {
                    bFlag = false;
                    if (mIsAimSelected) {
                        mAimPoint = mMapView.getMapProjection().fromPixels((int) event.getX(), (int) event.getY());
                        if (mAimPoint != null) {
                            getAimIcon().setVisibility(View.INVISIBLE);
                            mIsAimSelected = false;
                            updateMapAndDashBoard(true);
                            refreshScreenOff();
                            /*음성을 듣게 한다.*/
                            mSpeakSnap = true;
                        }
                    } else if (mIsPinSelected) {
                        mPinPoint = mMapView.getMapProjection().fromPixels((int) event.getX(), (int) event.getY());
                        if (mPinPoint != null) {
                            getPinIcon().setVisibility(View.INVISIBLE);
                            mIsPinSelected = false;
                            getSelectedHole().getSelectedPinData().setPosition(new Position(mPinPoint.latitude, mPinPoint.longitude, 0.0));
                            updateMapAndDashBoard(true);
                            refreshScreenOff();
                        }
                    }
                } else if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                    if (mIsAimSelected) {
                        mAimPoint = mMapView.getMapProjection().fromPixels((int) event.getX(), (int) event.getY());
                    } else if (mIsPinSelected) {
                        getPinIcon().setX(event.getX() - getPinIcon().getWidth() / 2);
                        getPinIcon().setY(event.getY() - getPinIcon().getHeight() / 2);
                        getPinIcon().bringToFront();
                        mPinPoint = mMapView.getMapProjection().fromPixels((int) event.getX(), (int) event.getY());
                    }
                    updateMapAndDashBoard(false);
                }
                return true;
            }
        });

        mResourceProvider = new NMapViewerResourceProvider(this);
        // create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mResourceProvider);


        mPOIdataOverlay.showAllItems();

        //	mResourceProvider = new NMapViewerResourceProvider(this);
        //	mOverlayManager = new NMapOverlayManager(this, mMapView, mResourceProvider );

    }


    /**
     * 현재 위치 마커 표시
     */
    @Override
    public void markCurrent(Position position) {

        if (position == null) return;
        if (mMarker != null) {
            mMarker.setPoint(new NGeoPoint(position.longi, position.lati));
            mMarker.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 설정된 정보에 따라 맵에 정보 표시
     */
    @Override
    public void updateMapAndDashBoard(boolean drawMarker) {

        Position position = null;

        if (mAimPoint != null) {
            position = new Position(mAimPoint.getLatitude(), mAimPoint.getLongitude(), 0.0);
        }

        updateDashBoard(position);


        if (drawMarker) {
            if (mAimPoint != null) {

                if (mAimToPin != null) {
                    mAimToPin.initPathData();
                }
                if (mTeeboxToAim != null) {
                    mTeeboxToAim.initPathData();
                }

                //목표 위치를 Daum맵 위치 객체로 변환
                setAimPosition(position);

                //다음 목표 포인트를 Daum맵 위치 객체로 변환
                setAimMapPoint(position, getSelectedHole().getAdvicePositionList(position));

                //고도정보 가져오기
                InqueryElevationTask inqTask = new InqueryElevationTask();
                inqTask.execute(this);

                //목표지점 및 다음 목표지점 라인으로 표시

                mAimToPin = new NMapPathData(10);
                NMapPathLineStyle pathLineStyle = new NMapPathLineStyle(mMapView.getContext());
                pathLineStyle.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYLINE);
                pathLineStyle.setLineColor(Constants.COLOR_AIM_TO_PIN, 0xff);
                pathLineStyle.setLineWidth((float) 2.0);

                mAimToPin.setPathLineStyle(pathLineStyle);
                //mAimTOPin에 자료추가준비 **********************************
                mAimToPin.initPathData();


                if (mAimPoint != null)
                    mAimToPin.addPathPoint(mAimPoint.longitude, mAimPoint.latitude, NMapPathLineStyle.TYPE_SOLID);

                if (mNextAimPoints != null) {
                    if (mIpList != null) {
                        for (NMapPOIitem item : mIpList) {
                            item.setVisibility(NMapOverlayItem.INVISIBLE);
                            item = null;
                        }
                    } else {
                        mIpList = new ArrayList<NMapPOIitem>();
                    }

                    for (NGeoPoint mp : mNextAimPoints) {
                        if (mNextAimPoints.indexOf(mp) != (mNextAimPoints.size() - 1)) {
                            if (!mp.equals(mAimPoint) && !mp.equals(mPinPoint)) {
                                try {
                                    NMapPOIdata item = new NMapPOIdata(1, mResourceProvider);
                                    item.beginPOIdata(1);
                                    NMapPOIitem nMapPOIitem = item.addPOIitem(mp, "IP", getResources().getDrawable(R.drawable.pc10), 0);
                                    item.endPOIdata();
                                    nMapPOIitem.setAnchorRatio(0.5f, 0.5f);
                                    mPOIdataOverlay = mOverlayManager.createPOIdataOverlay(item, null);
                                    mIpList.add(nMapPOIitem);
                                } catch (Exception ex) {
                                    System.out.println(ex + "Here is red IP!");
                                }
                            }
                        }
                        mAimToPin.addPathPoint(mp.longitude, mp.latitude, 1);
                    }
                }
                mAimToPin.endPathData();//*************자료추가완료***************

                mPathdataOverlay = mOverlayManager.createPathDataOverlay(mAimToPin);

                if (mAimToPin.countOfPathPoints() > 1) {
                    mPathdataOverlay.addPathData(mAimToPin);
                    mPathdataOverlay.showAllPathData(0);
                }

                mTeeboxToAim = new NMapPathData(11);
                NMapPathLineStyle nMapPathLineStyle = new NMapPathLineStyle(mMapView.getContext());
                nMapPathLineStyle.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYLINE);
                nMapPathLineStyle.setLineColor(Constants.COLOR_TEEBOX_TO_AIM, 0xff);
                nMapPathLineStyle.setLineWidth((float) 2.0);
                mTeeboxToAim.setPathLineStyle(nMapPathLineStyle);

                if (mTeeboxPoint != null)
                    mTeeboxToAim.addPathPoint(mTeeboxPoint.longitude, mTeeboxPoint.latitude, 0);
                if (mAimPoint != null)
                    mTeeboxToAim.addPathPoint(mAimPoint.longitude, mAimPoint.latitude, 0);

                if (mTeeboxToAim.countOfPathPoints() > 1) {
                    mPathdataOverlay.addPathData(mTeeboxToAim);
                    mPathdataOverlay.showAllPathData(0);
                }

                try {
                    if (mPinMarker != null) {
                        mPOIdatas.removePOIitem(mPinMarker.getId());
                        mPinMarker = null;
                    }
                    if (mAimMarker != null) {
                        mPOIdatas.removePOIitem(mAimMarker.getId());
                        mAimMarker = null;
                    }
                    if (mTeeboxMarker != null) {
                        mPOIdatas.removePOIitem(mTeeboxMarker.getId());
                        mTeeboxMarker = null;
                    }

                    String strPin = "Pin = " + mPinPoint.getLatitude() + ":" + "\n" + mPinPoint.getLongitude();
                    String strTee = "Current = " + mTeeboxPoint.getLatitude() + ":" + "\n" + mTeeboxPoint.getLatitude();
                    //
                    Log.e("global_level_is ++++", global_level + "");
                    double dTemp = 0.0d;
                    if (global_level < 10) {
                        dTemp = getRatioLevel(10);
                    } else {
                        dTemp = getRatioLevel(global_level);
                    }

                    NGeoPoint geo_PinPoint = new NGeoPoint(mPinPoint.longitude, mPinPoint.latitude - PINMAKER_OFFSET);
                    NGeoPoint geo_AimPoint = new NGeoPoint(mAimPoint.longitude, mAimPoint.latitude - AIMMAKER_OFFSET);

                    mPOIdatas = new NMapPOIdata(3, mResourceProvider);
                    mPOIdatas.beginPOIdata(3);
                    mPinMarker = mPOIdatas.addPOIitem(geo_PinPoint, strPin, getResources().getDrawable(R.drawable.pin_flag), 0);
                    mAimMarker = mPOIdatas.addPOIitem(geo_AimPoint, "Target", getResources().getDrawable(R.drawable.aim), 0);
                    mTeeboxMarker = mPOIdatas.addPOIitem(mTeeboxPoint, strTee, getResources().getDrawable(R.drawable.teebox_24), 0);
                    mPOIdatas.endPOIdata();

                    mPinMarker.setAnchorRatio(0.5f, 0.5f);
                    mTeeboxMarker.setAnchorRatio(0.5f, 1.0f);
                    mAimMarker.setAnchorRatio(0.5f, 0.6f);
//					markPin();
//					markAim();
//					markTeebox();
                } catch (Exception ex) {
                    System.out.println(ex + "here i am errors!");
                }
                mPOIdataOverlay = mOverlayManager.createPOIdataOverlay(mPOIdatas, null);
                mPOIdataOverlay.showAllItems();
            }
            drawCircle();
        }

    }

    /* POI data State Change Listener*/
    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            Drawable drawable = item.getMarker();
        }

        @Override
        public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {

        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapInitHandler(NMapView mapView, NMapError arg1) {

        if (this.isSatelliteMode()) {
            mMapController.setMapViewMode(NMapView.VIEW_MODE_HYBRID);
        } else {
            mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);
        }

        boolean isGameMode = this.getIntent().getBooleanExtra(Constants.INTENT_EXTRA_PARAM_GAME_MODE, false);
        boolean isSearchMode = this.getIntent().getBooleanExtra(Constants.INTENT_EXTRA_PARAM_SEARCH_MODE, false);
        boolean isTestMode = this.getIntent().getBooleanExtra(Constants.INTENT_EXTRA_PARAM_TEST_MODE, false);
        String clubKey = this.getIntent().getStringExtra(Constants.INTENT_EXTRA_PARAM_CLUB_KEY);
        String holeKey = this.getIntent().getStringExtra(Constants.INTENT_EXTRA_PARAM_SELECTED_HOLE);

        if (isGameMode) {
            setMode(Constants.MODE_GAME);
        } else {
            if (isTestMode) {
                setMode(Constants.MODE_TEST);
                findLocationInTestMode();
                Toast.makeText(this, this.getString(R.string.test_tagging), Toast.LENGTH_LONG).show();
            } else if (isSearchMode) {
                setMode(Constants.MODE_SEARCH);
            } else {
                setMode(Constants.MODE_VIEW);
            }
            if (clubKey != null) {
                loadClubData(clubKey, holeKey);
            }
        }

        isMapInitialized = true;

        mMapFrag.setOnDragListener(new MapFrameLayout.OnDragListener() {


            @Override
            public void onDrag(MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (isPopuped()) return;

                        if (isHistoryMode()) return;

                        if (mAimPoint == null || mPinPoint == null) return;

                        Point aimPoint = mMapView.getMapProjection().toPixels(mAimPoint, null);
                        Point pinPoint = mMapView.getMapProjection().toPixels(mPinPoint, null);

                        if ((event.getX() > aimPoint.x - Constants.TOUCH_BOUNDARY_SIZE)
                                && (event.getX() < aimPoint.x + Constants.TOUCH_BOUNDARY_SIZE)
                                && (event.getY() > aimPoint.y - Constants.TOUCH_BOUNDARY_SIZE)
                                && (event.getY() < aimPoint.y + Constants.TOUCH_BOUNDARY_SIZE)
                                ) {

                            if (!canMoveAimOnMap()) return;

                            if (mAimMarker != null) mAimMarker.setVisibility(View.INVISIBLE);

                            ClipData data = ClipData.newPlainText("", "");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(getAimIcon());
                            getAimIcon().startDrag(data, shadowBuilder, getAimIcon(), 0);
                            mIsAimSelected = true;
                            return;
                        } else if ((event.getX() > pinPoint.x - Constants.TOUCH_BOUNDARY_SIZE)
                                && (event.getX() < pinPoint.x + Constants.TOUCH_BOUNDARY_SIZE)
                                && (event.getY() > pinPoint.y - Constants.TOUCH_BOUNDARY_SIZE)
                                && (event.getY() < pinPoint.y + Constants.TOUCH_BOUNDARY_SIZE)
                                ) {

                            if (!canMovePinOnMap()) return;

                            if (mPinMarker != null) mPinMarker.setVisibility(View.INVISIBLE);

                            getPinIcon().setX(event.getX());
                            getPinIcon().setY(event.getY());
                            getPinIcon().setVisibility(View.VISIBLE);
                            getPinIcon().bringToFront();
                            ClipData data = ClipData.newPlainText("", "");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(getPinIcon());
                            getPinIcon().startDrag(data, shadowBuilder, getPinIcon(), 0);
                            mIsPinSelected = true;
                            return;
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        getPinIcon().setVisibility(View.INVISIBLE);
                        getAimIcon().setVisibility(View.INVISIBLE);
                        break;

                }
                return;
            }


        });


    }

    /**
     * 그린 팝업에서 깃발 위치 이동시 맵에 반영하기 위한 함수
     */
    @Override
    public void movedPinFlag(Position position) {

        mPinPoint.longitude = position.longi;
        mPinPoint.latitude = position.lati;
        if (mPinPoint != null) {
            getPinIcon().setVisibility(View.INVISIBLE);
            mIsPinSelected = false;
            getSelectedHole().getSelectedPinData().setPosition(new Position(mPinPoint.latitude, mPinPoint.longitude, 0.0));
            if (!isHistoryMode()) {
                updateMapAndDashBoard(true);
            } else {
                updateMapAndDashBoard(false);
            }
            refreshScreenOff();
        }
    }


    /**
     * 더블 탭으로 화면 확대시 북향으로 돌아오므로 헤딩업된 방향으로 보여주기 위하여 맵을 회전시킴
     */
    public void ViewDoubleTapped(NMapView mapView, NGeoPoint mapPoint) {
        HoleData holeData = getSelectedHole();
        if (holeData == null) return;
        double rotaionDegree = holeData.getRotationDgree();
        mapView.setRotateAngle((float) rotaionDegree);
    }


    @Override
    public void rotaionMap(double radian) {
        if (mMapView == null) return;
        super.rotaionMap(radian);
    }

    public int get_level(float diameter) {
        int level = 1;
        if (diameter > 613) {
            level = 11;
        } else if (diameter > 534) {
            level = 11;
        } else if (diameter > 460) {
            level = 11;
        } else if (diameter > 384) {
            level = 11;
        } else if (diameter > 307) {
            level = 12;
        } else if (diameter > 269) {
            level = 12;
        } else if (diameter > 231) {
            level = 13;
        } else if (diameter > 192) {
            level = 13;
        } else if (diameter > 154) {
            level = 13;
        } else if (diameter > 134) {
            level = 13;
        } else if (diameter > 115) {
            level = 13;
        } else if (diameter > 96) {
            level = 14;
        } else if (diameter > 77) {
            level = 14;
        } else if (diameter > 59) {
            level = 14;
        } else if (diameter > 39) {
            level = 14;
        } else if (diameter > 20) {
            level = 14;
        } else if (diameter > 10) {
            level = 14;
        } else {
            level = 14;
        }

        return level;
    }

    public double getRatioLevel(int level) {
        Log.e("level============", level + "");
        double temp = 0;
        switch (level) {
            case 13:
                temp = PINMAKER_OFFSET + 0.00015;
                break;
            case 14:
                temp = PINMAKER_OFFSET;
                break;
            default:
                temp = PINMAKER_OFFSET + 0.0002;
        }
        return temp;
    }

    @Override
    public void moveAndZoomMap(Position p1, Position p2) {

        if (mMapView == null) return;

        double cLati = (p1.lati - p2.lati) / 2.0 + p2.lati;
        double cLongi = (p1.longi - p2.longi) / 2.0 + p2.longi;


        NGeoPoint cPoint = new NGeoPoint(cLongi, cLati);
        float diameter = p1.getDistance(p2);

        //CameraUpdate cu = null;

        if (isViewMode() || isHistoryMode()) {
            HoleData holeData = getSelectedHole();
            if (holeData != null) {
                Position pp[] = holeData.getRectangleBound();
                cPoint = new NGeoPoint(holeData.getCenter().longi, holeData.getCenter().lati);
                diameter = pp[0].getDistance(pp[1]);
            }
        }

        diameter *= 0.6;
        int level = get_level(diameter);
        Log.e("eeeeeee", level + "++++");
        mMapController.setMapCenter((mAimPoint == null ? cPoint : mAimPoint), level);
    }

    @Override
    public void moveAndZoomMap(Position[] pData) {
        moveAndZoomMap(pData[0], pData[1]);
    }

    @Override
    public void setPinMapPoint(Position poinPoint) {
        mPinPoint = new NGeoPoint(poinPoint.longi, poinPoint.lati);

    }

    @Override
    public void setTeeboxMapPoint(Position poinPoint) {
        mTeeboxPoint = new NGeoPoint(poinPoint.longi, poinPoint.lati);
    }

    @Override
    public void setAimMapPoint(List<Position> pList) {

        if (mNextAimPoints != null) mNextAimPoints.clear();


        if (pList == null || pList.size() == 0) {
            Position p = getSelectedHole().getSelectedPinData().getPosition();
            mAimPoint = new NGeoPoint(p.longi, p.lati);
            return;
        }

        mAimPoint = new NGeoPoint(pList.get(0).longi, pList.get(0).lati);

        if (pList.size() > 1) {
            mNextAimPoints = new ArrayList<NGeoPoint>();
            for (int i = 1; i < pList.size(); i++) {
                mNextAimPoints.add(new NGeoPoint(pList.get(i).longi, pList.get(i).lati));
            }
        }
    }

    public void setAimMapPoint(Position p, List<Position> pList) {

        if (mNextAimPoints != null) mNextAimPoints.clear();

        mAimPoint = new NGeoPoint(p.longi, p.lati);

        if (pList.size() > 0) {
            mNextAimPoints = new ArrayList<NGeoPoint>();
            for (int i = 0; i < pList.size(); i++) {
                mNextAimPoints.add(new NGeoPoint(pList.get(i).longi, pList.get(i).lati));
            }
        }
    }

    @Override
    public Object getPinMapPoint() {
        return mPinPoint;
    }

    @Override
    public Object getTeeboxMapPoint() {
        return mTeeboxPoint;
    }

    @Override
    public Object getAimMapPoint() {
        return mAimPoint;
    }


    /**
     * 깃발 위치 마킹
     */
    @Override
    public void markPin() {
        if (mPinMarker != null) {
            mPOIdatas.removePOIitem(mPinMarker.getId());
            mPinMarker = null;
        }
        String strPin = "Pin = " + mPinPoint.getLatitude() + ":" + "\n" + mPinPoint.getLongitude();
        NGeoPoint geo_PinPoint = new NGeoPoint(mPinPoint.longitude, mPinPoint.latitude - getDTemp());
        mPOIdatas = new NMapPOIdata(1, mResourceProvider);
        mPOIdatas.beginPOIdata(1);
        mPinMarker = mPOIdatas.addPOIitem(geo_PinPoint, strPin, getResources().getDrawable(R.drawable.pin_flag), 1);
        mPOIdatas.endPOIdata();
        mPinMarker.setAnchorRatio(0.5f, 0.5f);
        mPOIdataOverlay = mOverlayManager.createPOIdataOverlay(mPOIdatas, null);
        mPOIdataOverlay.showAllPOIdata(1);
        mPinMarker.setVisibility(View.VISIBLE);
    }

    /**
     * Teebox 마킹
     */
    @Override
    public void markTeebox() {
        if (mTeeboxMarker != null) {
            mPOIdatas.removePOIitem(mTeeboxMarker.getId());
            mTeeboxMarker = null;
        }
        String strTee = "Current = " + mTeeboxPoint.getLatitude() + ":" + "\n" + mTeeboxPoint.getLatitude();
        mPOIdatas = new NMapPOIdata(1, mResourceProvider);
        mPOIdatas.beginPOIdata(1);
        mTeeboxMarker = mPOIdatas.addPOIitem(mTeeboxPoint, strTee, getResources().getDrawable(R.drawable.teebox_24), 0);
        mPOIdatas.endPOIdata();
        mTeeboxMarker.setAnchorRatio(0.5f, 1.0f);
        mPOIdataOverlay = mOverlayManager.createPOIdataOverlay(mPOIdatas, null);
        mPOIdataOverlay.showAllPOIdata(0);
        mTeeboxMarker.setVisibility(View.VISIBLE);
    }

    /**
     * 목표 아이콘 마킹
     */
    @Override
    public void markAim() {
        if (mAimMarker != null) {
            mPOIdatas.removePOIitem(mAimMarker.getId());
            mAimMarker = null;
        }
        NGeoPoint geo_AimPoint = new NGeoPoint(mAimPoint.longitude, mAimPoint.latitude - getDTemp());
        mPOIdatas = new NMapPOIdata(1, mResourceProvider);
        mPOIdatas.beginPOIdata(1);
        mAimMarker = mPOIdatas.addPOIitem(geo_AimPoint, "Target", getResources().getDrawable(R.drawable.aim), 2);
        mPOIdatas.endPOIdata();
        mAimMarker.setAnchorRatio(0.5f, 0.6f);
        mPOIdataOverlay = mOverlayManager.createPOIdataOverlay(mPOIdatas, null);
        mPOIdataOverlay.showAllPOIdata(2);
        mAimMarker.setVisibility(View.VISIBLE);
    }

    private double getDTemp() {
        Log.e("global_level_is ++++", global_level + "");
        double dTemp = 0.0d;
        if (global_level < 10) {
            dTemp = getRatioLevel(10);
        } else {
            dTemp = getRatioLevel(global_level);
        }
        return dTemp;
    }

    /**
     * 50,100,150 및 200 m 안내선 표시
     */
    @Override
    public void drawCircle() {

        if (mMapView == null) return;

        mPathdataOverlay = mOverlayManager.createPathDataOverlay();

        if (mMap_50 != null) {
            mMap_50.setHidden(true);
            mMap_50 = null;
        }
        if (mMap_100 != null) {
            mMap_100.setHidden(true);
            mMap_100 = null;
        }
        if (mMap_150 != null) {
            mMap_150.setHidden(true);
            mMap_150 = null;
        }
        if (mMap_200 != null) {
            mMap_200.setHidden(true);
            mMap_200 = null;
        }

        //nMapCircle data configuration!
        mCircleStyle = new NMapCircleStyle(mMapView.getContext());
        mCircleStyle.setLineType(NMapPathLineStyle.TYPE_SOLID);
        mCircleStyle.setStrokeWidth((float) 1.5);


        if (getAppConfig().getDrawGreenCircle()) {
            mMap_50 = new NMapCircleData(1);
            mMap_50.initCircleData();
            mMap_50.addCirclePoint(mPinPoint.longitude, mPinPoint.latitude, 50);
            mCircleStyle.setFillColor(Constants.COLOR_50[this.isSatelliteMode() ? 0 : 1], Color.TRANSPARENT);
            mMap_50.setCircleStyle(mCircleStyle);
            mMap_100 = new NMapCircleData(1);
            mMap_100.initCircleData();
            mMap_100.addCirclePoint(mPinPoint.longitude, mPinPoint.latitude, 100);
            mCircleStyle.setFillColor(Constants.COLOR_100[this.isSatelliteMode() ? 0 : 1], 0x00);
            mMap_100.setCircleStyle(mCircleStyle);
            mMap_150 = new NMapCircleData(1);
            mMap_150.initCircleData();
            mMap_150.addCirclePoint(mPinPoint.longitude, mPinPoint.latitude, 150);
            mCircleStyle.setFillColor(Constants.COLOR_150[this.isSatelliteMode() ? 0 : 1], Color.TRANSPARENT);
            mMap_150.setCircleStyle(mCircleStyle);
            mPathdataOverlay.addCircleData(mMap_50);
            mPathdataOverlay.addCircleData(mMap_100);
            mPathdataOverlay.addCircleData(mMap_150);
            mPathdataOverlay.showAllPathData(0);
        }

        if (getAppConfig().getDrawTeeboxCircle()) {
            mMap_200 = new NMapCircleData(1);
            mMap_200.initCircleData();
            mMap_200.addCirclePoint(mTeeboxPoint.longitude, mTeeboxPoint.latitude, (int) AppConfig.getDistance());
            mCircleStyle.setFillColor(Constants.COLOR_200[this.isSatelliteMode() ? 0 : 1], Color.TRANSPARENT);
            mMap_200.setCircleStyle(mCircleStyle);
            mPathdataOverlay.addCircleData(mMap_200);
            mPathdataOverlay.showAllPathData(0);
        }
    }

    /**
     * 그린 레이아웃 표시
     * 맵 타일에 따라서 색깔 변경
     */
    @Override
    public void drawGreen() {
        try {

            if (mMapView == null) return;

            HoleData holeData = getSelectedHole();
            if (holeData == null) return;

            if (holeData.getSelectedGreenData() == null) return;

            if (mGreenBound != null) {
                mMapView.removeView((View) mGreenBound.getObject());
                mGreenBound = null;
            }
            if (!getAppConfig().getDrawGreenLayout()) return;

            mGreenBound = new NMapPathData(30);
            NMapPathLineStyle pathLineStyle = new NMapPathLineStyle(mMapView.getContext());
            pathLineStyle.setPataDataType(NMapPathLineStyle.TYPE_SOLID);
            pathLineStyle.setLineColor(Constants.COLOR_GREEN[this.isSatelliteMode() ? 0 : 1], 1);

            for (Position p : holeData.getSelectedGreenData().getBoundary()) {
                mGreenBound.addPathPoint((double) p.longi, (double) p.lati, 1);
            }

            mGreenBound.setPathLineStyle(pathLineStyle);

            mPathdataOverlay = mOverlayManager.createPathDataOverlay();
            mPathdataOverlay.addPathData(mGreenBound);
            mPathdataOverlay.showAllPathData(0);

        } catch (Exception ex) {
            Tlog.e(ex);
        }

    }

    /**
     * Fairway 레이아웃 표시(최대 3개까지만 표시)
     */
    @Override
    public void drawFairway() {
        try {

            if (mMapView == null) return;

            HoleData holeData = getSelectedHole();
            if (holeData == null) return;

            if (mFairwayBound1 != null) {
                mMapView.removeView((View) mFairwayBound1.getObject());
                mFairwayBound1 = null;
            }
            if (mFairwayBound2 != null) {
                mMapView.removeView((View) mFairwayBound2.getObject());
                mFairwayBound2 = null;
            }
            if (mFairwayBound3 != null) {
                mMapView.removeView((View) mFairwayBound3.getObject());
                mFairwayBound3 = null;
            }

            if (!getAppConfig().getDrawFairwayLayout()) return;
            NMapPathLineStyle pathLineStyle = new NMapPathLineStyle(mMapView.getContext());
            pathLineStyle.setLineColor(Constants.COLOR_FAIRWAY[this.isSatelliteMode() ? 0 : 1], 1);

            mFairwayBound1 = new NMapPathData(40);
            mFairwayBound1.setPathLineStyle(pathLineStyle);

            mFairwayBound2 = new NMapPathData(40);
            mFairwayBound2.setPathLineStyle(pathLineStyle);

            mFairwayBound3 = new NMapPathData(40);
            mFairwayBound3.setPathLineStyle(pathLineStyle);

            for (FairwayData fairway : holeData.getFairwayDatas()) {
                if (fairway.getBoundary().size() > 0) {
                    if (holeData.getFairwayDatas().indexOf(fairway) == 0) {
                        for (Position p : fairway.getBoundary()) {
                            mFairwayBound1.addPathPoint((double) p.longi, (double) p.lati, NMapPathLineStyle.TYPE_SOLID);
                        }
                        Position p = fairway.getBoundary().get(0);
                        mFairwayBound1.addPathPoint((double) p.longi, (double) p.lati, NMapPathLineStyle.TYPE_SOLID);
                    } else if (holeData.getFairwayDatas().indexOf(fairway) == 1) {
                        for (Position p : fairway.getBoundary()) {
                            mFairwayBound2.addPathPoint((double) p.longi, (double) p.lati, NMapPathLineStyle.TYPE_SOLID);
                        }
                        Position p = fairway.getBoundary().get(0);
                        mFairwayBound2.addPathPoint((double) p.longi, (double) p.lati, NMapPathLineStyle.TYPE_SOLID);
                    } else {
                        for (Position p : fairway.getBoundary()) {
                            mFairwayBound3.addPathPoint((double) p.longi, (double) p.lati, NMapPathLineStyle.TYPE_SOLID);
                        }
                        Position p = fairway.getBoundary().get(0);
                        mFairwayBound3.addPathPoint((double) p.longi, (double) p.lati, NMapPathLineStyle.TYPE_SOLID);
                    }
                }
            }

            mPathdataOverlay = mOverlayManager.createPathDataOverlay();
            mPathdataOverlay.addPathData(mFairwayBound1);
            mPathdataOverlay.addPathData(mFairwayBound2);
            mPathdataOverlay.addPathData(mFairwayBound3);
            mPathdataOverlay.showAllPathData(0);

        } catch (Exception ex) {
            Tlog.e(ex);
        }
    }

    /**
     * 홀 레이아웃 표시
     */
    @Override
    public void drawOutbound() {

        try {

            if (mMapView == null) return;

            HoleData holeData = getSelectedHole();
            if (holeData == null) return;

            if (mOutbound != null) {
                mMapView.removeView((View) mOutbound.getObject());
                mOutbound = null;
            }

            if (!getAppConfig().getDrawHoleLayout()) return;

            NMapPathLineStyle pathLineStyle = new NMapPathLineStyle(mMapView.getContext());
            pathLineStyle.setLineColor(Constants.COLOR_OUTBOUND[this.isSatelliteMode() ? 0 : 1], 0xff);
            pathLineStyle.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);
            pathLineStyle.setLineWidth((float) 2.0);

            int nCount = holeData.getBoundary().size();
            mOutbound = new NMapPathData(nCount);
            mOutbound.initPathData();
            for (Position p : holeData.getBoundary()) {
                mOutbound.addPathPoint(p.longi, (double) p.lati, NMapPathLineStyle.DATA_TYPE_POLYLINE);
            }
            mOutbound.endPathData();
            mOutbound.setPathLineStyle(pathLineStyle);
            NMapPathDataOverlay nMapPathDataOverlay = mOverlayManager.createPathDataOverlay(mOutbound);
            nMapPathDataOverlay.showAllPathData(0);

        } catch (Exception ex) {
            Tlog.e(ex);
        }
    }


    /**
     * 복기 모드시 해당 홀에 본이이 태깅한 위치를 표시
     */
    @Override
    public void drawHistory(List<TraceLog> logs) {
        try {
            if (mMapView == null) return;

            clearHistory();
            if (logs == null) return;

            NMapPathLineStyle pathLineStyle = new NMapPathLineStyle(mMapView.getContext());
            pathLineStyle.setLineColor(Constants.COLOR_TRACE[this.isSatelliteMode() ? 0 : 1], 1);

            NMapPathData line = new NMapPathData(130);
            line.setPathLineStyle(pathLineStyle);

            NMapPOIdata poIdata = new NMapPOIdata((int) logs.size(), mResourceProvider);
            // Markers for POI item
            NMapPOIitem marker;

            int idx = 0;
            for (int i = 0; i < logs.size(); i++) {
                TraceLog log = logs.get(i);


                if (idx > 0) {
                    double dist = new Position(logs.get(idx - 1).getLatitude(), logs.get(idx - 1).getLongitude(), 0.0).getDistance(log.getLatitude(), log.getLongitude());
                    if (dist < 1) continue;
                }
//fixme setData()메소드문제***	marker.setData(idx>0?logs.get(idx-1):null, log);
                line.addPathPoint(log.getLongitude(), log.getLatitude(), 1);
                int resId = Util.getResourceId(this, String.format("p_%02d", idx + 1));
                marker = poIdata.addPOIitem(log.getLongitude(), log.getLatitude(), "Default Marker", resId, 0);
                idx++;
//fixme				mMapView.addPOIItem(marker);

            }

//fixme			mMapView.addPolyline(line);
        } catch (Exception ex) {
            Tlog.e(ex);
        }
    }

    public void clearHazard() {

        if (mMapView == null) return;

		/*fixme		for(NMapPOIitem  pItem:mMapView.getPOIitems()){
            if(pItem.getTag() == 150){
				mMapView.removePOIItem(pItem);
			}
		}*/
    }

    public void drawHazard() {

    }

    @Override
    public void reDrawBoundary() {

        drawGreen();
        drawOutbound();
        drawFairway();
        drawCircle();
        drawHazard();
    }


    /**
     * 맵 타일이 변경되었을때 화면 다시 그리기
     */
    @Override
    public void mapTileChange() {

        if (mMapView == null) return;

        super.mapTileChange();

        if (isSatelliteMode()) {
            invalidateOptionsMenu();
            mMapController.setMapViewMode(NMapView.VIEW_MODE_SATELLITE);
        } else {
            invalidateOptionsMenu();
            mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);
        }

        if (getSelectedHole() != null) {
            reDrawBoundary();
        }
        try {
            updateMapAndDashBoard(true);
        } catch (Exception ex) {
            System.out.print("맵타입에로" + ex);
        }
    }


    /**
     * 맵에 표시된 맵 Component 삭제
     */
    @Override
    public void clearMap() {
        if (mOverlayManager != null) ;
        mOverlayManager.clearOverlays();

        mAimMarker = null;
        mPinMarker = null;
        mTeeboxMarker = null;

        mMap_50 = null;
        mMap_100 = null;
        mMap_150 = null;
        mMap_200 = null;

        mAimToPin = null;
        mTeeboxToAim = null;

        mOutbound = null;
        mFairwayBound1 = null;
        mFairwayBound2 = null;
        mFairwayBound3 = null;
        mGreenBound = null;
        mMarker = null;

    }

    /**
     * 맵에 표시된 맵 Marker Component만 삭제
     */
    @Override
    public void clearMapItem() {
        if (mAimMarker != null) mAimMarker.setVisibility(View.INVISIBLE);
        if (mPinMarker != null) mPinMarker.setVisibility(View.INVISIBLE);
        if (mTeeboxMarker != null) mTeeboxMarker.setVisibility(View.INVISIBLE);
        if (mMarker != null) mMarker.setVisibility(View.INVISIBLE);
    }

    /**
     * 복기 모드에서 사용된  Component 삭제
     */
    @Override
    public void clearHistory() {

        if (mAimMarker != null) mAimMarker.setVisibility(View.INVISIBLE);
        if (mAimToPin != null) mAimToPin.initPathData();
        if (mTeeboxToAim != null) mTeeboxToAim.initPathData();
        if (mTeeboxMarker != null) mTeeboxMarker.setVisibility(View.INVISIBLE);

//		for(MapPolyline pLine:mMapView.getPolylines()){
//			if(pLine.getTag() == 130){
//				mMapView.removePolyline(pLine);
//			}
//		}
//
//		for(MapPOIItem pItem:mMapView.getPOIItems()){
//			if(pItem.getTag() == 130){
//				mMapView.removePOIItem(pItem);
//			}
//		}
    }


    /**
     * 복기 모드시 태깅한 지점에 대한 상세 정보 어뎁터
     *
     * @author dkchoi
     */
//	public class HistoryBallonAdapter implements CalloutBalloonAdapter {
//		private final View mCalloutBalloon;
//		private TextView mTraceTime;
//		private TextView mTraceDist;
//		private TextView mTraceDate;
//
//
//		public HistoryBallonAdapter() {
//			mCalloutBalloon = getLayoutInflater().inflate(R.layout.view_ballon, null);
//			mTraceDate = ((TextView) mCalloutBalloon.findViewById(R.id.trace_date));
//			mTraceTime = ((TextView) mCalloutBalloon.findViewById(R.id.trace_time));
//			mTraceDist = ((TextView) mCalloutBalloon.findViewById(R.id.trace_dist));
//		}
//
//		@Override
//		public View getCalloutBalloon(MapPOIItem poiItem) {
//
//			refreshScreenOff();
//
//			if(poiItem instanceof TracePOIItem){
//				mTraceDist.setText(((TracePOIItem) poiItem).getDistance());
//				mTraceTime.setText(((TracePOIItem) poiItem).getTraceLog().getTime());
//				mTraceDate.setText(((TracePOIItem) poiItem).getTraceLog().getDate());
//			}else{
//				mTraceDate.setText(poiItem.getMapPoint().getMapPointGeoCoord().latitude+":"+poiItem.getMapPoint().getMapPointGeoCoord().longitude);
//			}
//			return mCalloutBalloon;
//		}
//
//		@Override
//		public View getPressedCalloutBalloon(final MapPOIItem poiItem) {
//			refreshScreenOff();
//
//			poiItem.setCustomPressedCalloutBalloon(mCalloutBalloon);
//			if(poiItem instanceof TracePOIItem){
//				mTraceDist.setText(((TracePOIItem) poiItem).getDistance());
//				mTraceTime.setText(((TracePOIItem) poiItem).getTraceLog().getTime());
//				mTraceDate.setText(((TracePOIItem) poiItem).getTraceLog().getDate());
//			}else{
//
//			}
//
//			return mCalloutBalloon;
//		}
//	}

    public class TracePOIItem extends NMapPOIitem {

        private TraceLog mLog;
        private String mDist;

        public TracePOIItem(NGeoPoint nGeoPoint, String s) {
            super(nGeoPoint, s);
        }


        public void setData(TraceLog prevLog, TraceLog log) {
            mLog = log;

            Position p = new Position(log.getLatitude(), log.getLongitude(), 0.0);

            if (prevLog != null) {
                double dist = p.getDistance(prevLog.getLatitude(), prevLog.getLongitude());
                String unit = "m";
                if (isYard()) {
                    dist = dist * Constants.YARD_UNIT;
                    unit = "y";
                }
                mDist = String.format("%5.2f %s", dist, unit);
            } else {
                mDist = "0.0";
            }
        }

        public String getDistance() {
            return mDist;
        }

        public TraceLog getTraceLog() {
            return mLog;
        }

    }

    @Override
    public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapCenterChangeFine(NMapView arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onZoomLevelChange(NMapView arg0, int arg1) {
        global_level = arg1;
        Log.e("wwwwww", "" + global_level);
    }

    @Override
    public void onLongPress(NMapView arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLongPressCanceled(NMapView arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(NMapView arg0, MotionEvent arg1, MotionEvent arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSingleTapUp(NMapView arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTouchDown(NMapView arg0, MotionEvent event) {
        // TODO Auto-generated method stub


    }

    @Override
    public void onTouchUp(NMapView arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub

    }

    private void initializeSearch() {
        mSearchMapParser = new SearchMapParser();
        for (int i = 0; i < SEARCH_INDEX; i++)
            restaurantData[i] = new RestaurantData();
    }

    private void searchRestaurant(NGeoPoint restcent, boolean bIshow) {

        currentAddress = mSearchMapParser.search_current_addr(restcent);
        searchedRestaurantIndex = mSearchMapParser.search(restaurantData, currentAddress + " " + "맛집", SEARCH_INDEX, 1, restcent);
        displayRestaurantItem(bIshow);

    }

    private void displayRestaurantItem(boolean bIsShow) {
        NMapPOIdataOverlay poiDataOverlay = null;
        int markerId = SearchMapPOIflagType.SPOT;
        if (mRestaurantList != null) {
            for (NMapPOIitem mp : mRestaurantList) {
                mp.setVisibility(NMapOverlayItem.INVISIBLE);
                mp = null;
            }
        } else if (mRestaurantList == null) {
            mRestaurantList = new ArrayList<>();
        }
        if (bIsShow) {
            // create overlay manager
            mOverlayManager = new NMapOverlayManager(this, mMapView, mResourceProvider);
            NMapPOIdata poiData = new NMapPOIdata(searchedRestaurantIndex, mResourceProvider);
            poiData.beginPOIdata(searchedRestaurantIndex);

            for (int i = 0; i < searchedRestaurantIndex; i++) {
                NMapPOIitem poIitem = poiData.addPOIitem(Double.parseDouble(restaurantData[i].sMapX), Double.parseDouble(restaurantData[i].sMapY),
                        restaurantData[i].sTitle + "\n" + restaurantData[i].sAddress + "\n" + restaurantData[i].sTel, markerId, 0);
                mRestaurantList.add(poIitem);
            }

            poiData.endPOIdata();
            poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        } else {
            NMapPOIdata poiData = new NMapPOIdata(searchedRestaurantIndex, mResourceProvider);
            poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        }
        poiDataOverlay.showAllItems();
    }

    /*맛집정보현시*/
    @Override
    public void RestaurantShowOnOff() {

        mIsRestaurant = !mIsRestaurant;
        mRestaurantBtn.setBackground(getBaseContext().getResources().getDrawable(!mIsRestaurant ? R.drawable.restaurant : R.drawable.restaurant_));

        if (mPinPoint != null) searchRestaurant(mPinPoint, mIsRestaurant);
    }

}


