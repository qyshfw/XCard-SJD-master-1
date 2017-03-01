package com.example.x.xcard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.com.x.AppModel.BannerModel;
import com.com.x.card.CZMainVC;
import com.com.x.card.CZManagerVC;
import com.com.x.card.CardManageVC;
import com.com.x.card.MakeCardVC;
import com.com.x.card.XFManageVC;
import com.com.x.huiyuan.HYManageVC;
import com.com.x.huodong.HDManageVC;
import com.com.x.user.LoginVC;
import com.com.x.user.SystemMsg;
import com.com.x.yuangong.YGManageMainVC;
import com.example.x.xcard.ApplicationClass;
import com.example.x.xcard.BaseActivity;
import com.example.x.xcard.CountTime;
import com.example.x.xcard.MainActivity;
import com.example.x.xcard.NetworkImageHolderView;
import com.example.x.xcard.Properties;
import com.example.x.xcard.R;
import com.x.custom.DensityUtil;
import com.x.custom.XActivityindicator;
import com.x.custom.XGridView;
import com.x.custom.XNetUtil;
import com.x.custom.XNotificationCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.x.xcard.ApplicationClass.APPDataCache;
import static com.example.x.xcard.ApplicationClass.APPService;


public class ShopFragment extends Fragment implements Properties{


    private XGridView gview;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private ImageView banKa;
    private ImageView xiaoFei;
    private ImageView chongZhi;
    List<BannerModel> bannerArr = new ArrayList<BannerModel>();

    private ConvenientBanner convenientBanner;//顶部广告栏控件
    private List<String> networkImages = new ArrayList<String>();

    // 图片封装为一个数组
    private int[] icon = {R.drawable.index_icon01,R.drawable.index_icon06,R.drawable.index_icon04,
                R.drawable.index_icon02,R.drawable.index_icon09,R.drawable.index_icon11, R.color.white};
    private String[] iconName = {"员工管理", "会员卡管理", "活动管理", "充值管理",
            "系统公告",  "更多.."," "};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载fragment_shop布局
        View view=inflater.inflate(R.layout.fragment_shop, container, false);

        //通过findViewById获取布局文件中定义的元素
        gview = (XGridView) view.findViewById(R.id.homt_gview);
        banKa=(ImageView) view.findViewById(R.id.toBanKa);
        xiaoFei=(ImageView) view.findViewById(R.id.toXiaoFei);
        chongZhi=(ImageView) view.findViewById(R.id.toChongZhi);

        //取消设置滚动启用
        gview.setScrollEnable(false);
        /*
         *九宫格（功能布局）
         */
        //gview设置点击事件
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
//                //如果
//                if (i < 3) {
//                    if (!mainActivity.checkIsLogin()) {
//                        return;
//                    } else {
//                        if (!mainActivity.CheckUserPower(i + 4 + "")) {
//                            return;
//                        }
//                    }
//
//                }

                switch (i) {
                    case 0://员工管理
                        pushVC(YGManageMainVC.class);
                        break;
                    case 1://卡类管理
                        pushVC(CardManageVC.class);

                    case 2://活动
                        pushVC(HDManageVC.class);
                        break;
                    case 3://充值
                        pushVC(CZManagerVC.class);
                        break;
                    case 4://系统公告
                        pushVC(SystemMsg.class);
                        break;
                }

            }
        });
        banKa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "办卡");
                pushVC(MakeCardVC.class, bundle);
            }
        });
        xiaoFei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "消费");
                pushVC(XFManageVC.class, bundle);
            }
        });
        chongZhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", "充值");
                pushVC(CZManagerVC.class, bundle);
            }
        });
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(getActivity(), data_list, R.layout.home_item_cell, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);

        /*
         *轮播
         */
        //通过findViewById获取gridView自定义元素
        convenientBanner = (ConvenientBanner) view.findViewById(R.id.convenientBanner);
        //设置指示器（dot,defaule:白色；selected:蓝色）
        convenientBanner.setPageIndicator(new int[]{R.drawable.banner_dot_default, R.drawable.banner_dot_selected})
        //设置指示器的方向
        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

//        //重新 设置 控件的 布局，
//        ViewGroup.LayoutParams layoutParams = convenientBanner.getLayoutParams();
//
//        int w = ApplicationClass.SW - DensityUtil.dip2px(,28);
//        int h = (int)(w * 7 / 16.0);
//
//        layoutParams.height = h;
//        convenientBanner.setLayoutParams(layoutParams);

        getBanner();
        //处理滑动事件（轮播）
        convenientBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("!!! Banner选择position: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //轮播图片点击事件
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                System.out.println("~~~ Banner点击position: " + position);
            }
        });


        //观察者模式（待定）
        XNotificationCenter.getInstance().addObserver("ShowAccountLogout",new XNotificationCenter.OnNoticeListener(){
            @Override
            public void OnNotice(Object obj) {
                showAccountLogout();
            }
        });
        return view;
    }

    private void showAccountLogout()
    {
        AlertView Alert = new AlertView("提醒", "您的账户已在其他设备登录", null, null,
                new String[]{"确定"},
                ApplicationClass.context, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {

                }
            }
        });

        XActivityindicator.setAlert(Alert);

        Alert.show();

        APPDataCache.User.unRegistNotice();
        APPDataCache.User.reSet();
    }
    //通过ServicesAPI接口获取轮播图片信息
    private void getBanner() {

        XNetUtil.Handle(APPService.getBanner(), new XNetUtil.OnHttpResult<List<BannerModel>>() {
            @Override
            public void onError(Throwable e) {

                XNetUtil.APPPrintln(e);

            }

            @Override
            public void onSuccess(List<BannerModel> bannerModels) {

                bannerArr = bannerModels;
                for(BannerModel model:bannerModels)
                {
                    System.out.println(model.toString());
                    networkImages.add(model.getPicurl());
                }

                convenientBanner.setPages(new CBViewHolderCreator() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, networkImages);


            }

        });

    }
    //获取九宫格功能图片image和名称text
    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }
    //
    /**
     * 启动另外一个界面通过push动画
     *
     * @param activity
     */
    public void pushVC(Class activity) {

        if(!CountTime.isBeyoundTime("启动界面", 300)){
            return;
        }

        Intent intentActive = new Intent(getActivity(), activity);

        //用Bundle携带数据
        Bundle bundle=new Bundle();
        bundle.putBoolean("isPush", true);
        intentActive.putExtras(bundle);

        startActivity(intentActive);
//        overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
    /**
     * 启动另外一个界面通过push动画
     *
     * @param activity
     * @param bundle
     */
    public void pushVC(Class activity,Bundle bundle) {

        if(!CountTime.isBeyoundTime("启动界面", 300)){
            return;
        }

        Intent intentActive = new Intent(getActivity(), activity);
        //用Bundle携带数据
        bundle.putBoolean("isPush", true);
        intentActive.putExtras(bundle);

        startActivity(intentActive);
//        overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
    /**
     * 启动另外一个界面通过present动画
     *
     * @param activity
     */
    public void presentVC(Class activity) {

        if(!CountTime.isBeyoundTime("启动界面", 300)){
            return;
        }

        Intent intentActive = new Intent(getActivity(), activity);
        //用Bundle携带数据
        Bundle bundle=new Bundle();
        bundle.putBoolean("isPush", false);
        intentActive.putExtras(bundle);

        startActivity(intentActive);

//        overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
    }
    /**
     * 启动另外一个界面通过present动画
     *
     * @param activity
     * @param bundle
     */
    public void presentVC(Class activity,Bundle bundle) {

        if(!CountTime.isBeyoundTime("启动界面", 300)){
            return;
        }

        Intent intentActive = new Intent(getActivity(), activity);
        //用Bundle携带数据
        bundle.putBoolean("isPush", false);
        intentActive.putExtras(bundle);

        startActivity(intentActive);

//        overridePendingTransition(R.anim.push_up_in,R.anim.push_up_out);
    }



}
