package com.com.x.card;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.com.x.AppModel.ValueSumModel;
import com.example.x.xcard.ApplicationClass;
import com.example.x.xcard.BaseActivity;
import com.example.x.xcard.R;
import com.x.custom.DensityUtil;
import com.x.custom.XNetUtil;
import com.x.custom.XNotificationCenter;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

import static com.example.x.xcard.ApplicationClass.APPService;

/**
 * Created by X on 16/9/6.
 */
public class CZManagerVC extends BaseActivity  {

    private TextView dtatle;
    private TextView dnum;
    private TextView num7;
    private TextView num30;

    private TextView jidu;
    private TextView year;

    private TextView numall;

    String sid = ApplicationClass.APPDataCache.User.getShopid();

    @Override
    protected void setupUi() {
        setContentView(R.layout.cz_manage);
        setPageTitle("充值管理");

        dtatle=(TextView)findViewById(R.id.cz_manage_tatle);
        dnum=(TextView)findViewById(R.id.cz_manage_daynum);
        num7=(TextView)findViewById(R.id.cz_manage_7num);
        num30=(TextView)findViewById(R.id.cz_manage_30num);
        numall=(TextView)findViewById(R.id.cz_manage_allnum);

        jidu=(TextView)findViewById(R.id.cz_manage_numjidu);
        year=(TextView)findViewById(R.id.cz_manage_numyear);


        getData();

        XNotificationCenter.getInstance().addObserver("MondetailDelSuccess", new XNotificationCenter.OnNoticeListener() {

            @Override
            public void OnNotice(Object obj) {
                getData();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XNotificationCenter.getInstance().removeObserver("MondetailDelSuccess");

    }

    private void getData()
    {
        XNetUtil.Handle(APPService.shoptGetValueSum(sid), new XNetUtil.OnHttpResult<ValueSumModel>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess(ValueSumModel valueSumModel) {

                if(valueSumModel != null)
                {
                    dtatle.setText("￥"+valueSumModel.getDay());
                    dnum.setText("今日充值次数: "+valueSumModel.getDaycnum()+"次");
                    num7.setText("￥"+valueSumModel.getWeek());
                    num30.setText("￥"+valueSumModel.getMonth());
                    numall.setText("￥"+valueSumModel.getAll());
                    jidu.setText("￥"+valueSumModel.getJidu());
                    year.setText("￥"+valueSumModel.getYear());
                }

            }
        });
    }

    @Override
    protected void setupData() {

    }

    public void toCZDetail(View v)
    {
        pushVC(CZDetailVC.class);
    }

}
