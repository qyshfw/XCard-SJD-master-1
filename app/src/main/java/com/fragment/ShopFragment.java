package com.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.com.x.AppModel.BannerModel;
import com.com.x.card.CZManagerVC;
import com.com.x.card.CardManageVC;
import com.com.x.huiyuan.HYManageVC;
import com.com.x.huodong.HDManageVC;
import com.com.x.user.SystemMsg;
import com.example.x.xcard.ApplicationClass;
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

public class ShopFragment extends Fragment implements Properties {
    private XGridView gview;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;

    List<BannerModel> bannerArr = new ArrayList<BannerModel>();

    private ConvenientBanner convenientBanner;//顶部广告栏控件
    private List<String> networkImages = new ArrayList<String>();

    // 图片封装为一个数组
    private int[] icon = {R.drawable.index_icon01,R.drawable.index_icon06,R.drawable.index_icon04,
            R.drawable.index_icon02,R.drawable.index_icon09,R.drawable.index_icon11, R.color.white};
    private String[] iconName = {"会员管理", "会员卡管理", "活动管理", "充值管理",
            "系统公告",  "更多..", ""};
    MainActivity mainActivity=new MainActivity();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载fragment_shop布局
        View view=inflater.inflate(R.layout.fragment_shop, container, false);
        //通过findViewById获取布局文件中定义的元素
        gview = (XGridView) view.findViewById(R.id.homt_gview);
        //取消设置滚动启用
        gview.setScrollEnable(false);
        //gview设置点击事件
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                //如果
                if (i < 3) {
                    if (!mainActivity.checkIsLogin()) {
                        return;
                    } else {
                        if (!mainActivity.CheckUserPower(i + 4 + "")) {
                            return;
                        }
                    }

                }

                switch (i) {
                    case 0:
                        mainActivity.pushVC(HYManageVC.class);
                        break;
                    case 1://卡类管理
                        mainActivity.pushVC(CardManageVC.class);

                    case 2:
                        mainActivity.pushVC(HDManageVC.class);
                        break;
                    case 3:
                        mainActivity.pushVC(CZManagerVC.class);
                        break;
                    case 4:
                        mainActivity.pushVC(SystemMsg.class);
                        break;
                    default:
                        break;
                }

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

        convenientBanner = (ConvenientBanner) view.findViewById(R.id.convenientBanner);
        convenientBanner.setPageIndicator(new int[]{R.drawable.banner_dot_default, R.drawable.banner_dot_selected})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);


        ViewGroup.LayoutParams layoutParams = convenientBanner.getLayoutParams();

        int w = ApplicationClass.SW - DensityUtil.dip2px(ApplicationClass.context,28);
        int h = (int)(w * 7 / 16.0);

        layoutParams.height = h;
        convenientBanner.setLayoutParams(layoutParams);

        getBanner();

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
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                System.out.println("~~~ Banner点击position: " + position);
            }
        });
        XNotificationCenter.getInstance().addObserver("ShowAccountLogout", new XNotificationCenter.OnNoticeListener() {
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


}
