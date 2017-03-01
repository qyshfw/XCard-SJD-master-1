package com.com.x.card;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.com.x.AppModel.CardTypeModel;
import com.com.x.AppModel.UserModel;
import com.com.x.huiyuan.HYUserInfoVC;
import com.example.x.xcard.ApplicationClass;
import com.example.x.xcard.BaseActivity;
import com.example.x.xcard.R;
import com.x.custom.XNetUtil;
import com.x.custom.XNotificationCenter;

import java.util.ArrayList;
import java.util.List;

import static com.example.x.xcard.ApplicationClass.APPService;

/**
 * Created by X on 16/9/5.
 */
public class CZMainVC extends BaseActivity {

    private LinearLayout no;
    private LinearLayout has;
    private ListView list;
    private String title = "";

    private EditText editText;
    private TextView tel;
    private TextView name;
    private TextView btn;

    private CardManageAdapter adapter = new CardManageAdapter();
    private List<CardTypeModel> dataArr = new ArrayList<>();
    String sid = ApplicationClass.APPDataCache.User.getShopid();

    private UserModel user;

    private int row;

    @Override
    protected void setupUi() {
        setContentView(R.layout.card_cz_main);

        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && bundle.containsKey("title"))
        {
            title = bundle.getString("title");
        }

        setPageTitle(title);

        editText = (EditText)findViewById(R.id.card_cz_main_edit);
        tel = (TextView)findViewById(R.id.card_cz_main_tel);
        name = (TextView)findViewById(R.id.card_cz_main_name);
        btn = (TextView)findViewById(R.id.card_cz_main_btn);

        no = (LinearLayout)findViewById(R.id.card_cz_main_no);
        has = (LinearLayout)findViewById(R.id.card_cz_main_has);

        no.setVisibility(View.GONE);

        list = (ListView)findViewById(R.id.card_cz_main_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                row = i;
                adapter.notifyDataSetChanged();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                checkUser();
            }
        });

        XNotificationCenter.getInstance().addObserver("CZMainChanged", new XNotificationCenter.OnNoticeListener() {
            @Override
            public void OnNotice(Object obj) {
                getData();
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XNotificationCenter.getInstance().removeObserver("CZMainChanged");
    }

    private void checkUser()
    {
        String txt = editText.getText().toString().trim();
        if(txt.length() == 11)
        {
            XNetUtil.Handle(APPService.shopdGetUserInfoM(txt, sid), new XNetUtil.OnHttpResult<List<UserModel>>() {

                @Override
                public void onError(Throwable e) {

                    XNetUtil.APPPrintln(e);
                }

                @Override
                public void onSuccess(List<UserModel> userModels) {

                    if(userModels.size() > 0)
                    {
                        has.setVisibility(View.VISIBLE);
                        no.setVisibility(View.GONE);
                        user = userModels.get(0);
                        name.setText(user.getTruename());
                        tel.setText(user.getMobile());
                        getData();
                    }
                    else
                    {
                        has.setVisibility(View.GONE);
                        no.setVisibility(View.VISIBLE);
                        btn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.CardBtnGray));
                        btn.setClickable(false);
                        dataArr.clear();
                        adapter.notifyDataSetChanged();
                    }

                }
            });
        }
        else
        {
            user = null;
            name.setText("");
            tel.setText("请输入手机号");
            btn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.CardBtnGray));
            btn.setClickable(false);
            has.setVisibility(View.VISIBLE);
            no.setVisibility(View.GONE);
            dataArr.clear();
            adapter.notifyDataSetChanged();
        }

    }


    private void getData() {


        XNetUtil.Handle(APPService.hykGetShopCardY(sid, user.getUid()), new XNetUtil.OnHttpResult<List<CardTypeModel>>() {

            @Override
            public void onError(Throwable e) {
                XNetUtil.APPPrintln(e);
            }

            @Override
            public void onSuccess(List<CardTypeModel> cardTypeModels) {
                if(cardTypeModels.size()>0)
                {

                    if(title.equals("充值"))
                    {
                        dataArr.clear();
                        for(CardTypeModel m : cardTypeModels)
                        {
                            if(m.getType().equals("计次卡") || m.getType().equals("充值卡"))
                            {
                                btn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.APPOrange));
                                btn.setClickable(true);
                                dataArr.add(m);
                            }
                        }
                    }
                    else
                    {
                        dataArr = cardTypeModels;
                        btn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.APPOrange));
                        btn.setClickable(true);
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    @Override
    protected void setupData() {

    }

    public void toNext(View v)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user",user);
        bundle.putSerializable("card",dataArr.get(row));

        switch (title)
        {
            case "消费":
                pushVC(XFInfoVC.class,bundle);
                break;
            case "充值":
                pushVC(CZInfoVC.class,bundle);
                break;
            default:
                break;
        }


    }




    /**
     * 定义ListView适配器MainListViewAdapter
     */
    class CardManageAdapter extends BaseAdapter {

        /**
         * 返回item的个数
         */
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dataArr.size();
        }

        /**
         * 返回item的内容
         */
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return dataArr.get(position);
        }

        /**
         * 返回item的id
         */
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        /**
         * 返回item的视图
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CZMainVC.ListItemView listItemView;

            // 初始化item view
            if (convertView == null) {
                // 通过LayoutInflater将xml中定义的视图实例化到一个View中
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.card_manage_cell, null);

                // 实例化一个封装类ListItemView，并实例化它的两个域
                listItemView = new CZMainVC.ListItemView();
                listItemView.img = (ImageView) convertView
                        .findViewById(R.id.card_manage_cell_img);
                listItemView.name = (TextView) convertView
                        .findViewById(R.id.card_manage_cell_name);
                listItemView.info = (TextView) convertView
                        .findViewById(R.id.card_manage_cell_info);
                listItemView.checkBox = (RadioButton) convertView
                        .findViewById(R.id.card_manage_cell_check);

                // 将ListItemView对象传递给convertView
                convertView.setTag(listItemView);
            } else {
                // 从converView中获取ListItemView对象
                listItemView = (CZMainVC.ListItemView) convertView.getTag();
            }

            // 获取到mList中指定索引位置的资源
            //int img = (int) dataArr.get(position).
            String name = (String) dataArr.get(position).getType();
            String info = (String) dataArr.get(position).getInfo();
            String color = (String) dataArr.get(position).getColor();


            // 将资源传递给ListItemView的两个域对象
            //listItemView.header.setImageResource(img);
            //listItemView.imageView.setImageDrawable(img);
            listItemView.name.setText(name);
            listItemView.info.setText(info);
            listItemView.checkBox.setEnabled(true);
            listItemView.checkBox.setClickable(false);
            listItemView.checkBox.setChecked(position == row);
            listItemView.img.setBackgroundColor(Color.parseColor("#"+color));

            // 返回convertView对象
            return convertView;
        }

    }

    /**
     * 封装两个视图组件的类
     */
    class ListItemView {
        ImageView img;
        TextView name;
        TextView info;
        RadioButton checkBox;
    }

}