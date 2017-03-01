package com.example.x.xcard;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.x.xcard.fragment.MemberFragment;
import com.example.x.xcard.fragment.MyFragment;
import com.example.x.xcard.fragment.NewsFragment;
import com.example.x.xcard.fragment.ShopFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {



    // 定义4个Fragment对象
    private ShopFragment shopFragment;
    private MemberFragment memberFragment;
    private NewsFragment newsFragment;
    private MyFragment myFragment;
    // 帧布局对象，用来存放Fragment对象
    private FrameLayout frameLayout;
    // 定义每个选项中的相关控件
    private RelativeLayout shopLayout;
    private RelativeLayout memberLayout;
    private RelativeLayout newsLayout;
    private RelativeLayout myLayout;
    private ImageView shopImage;
    private ImageView memberImage;
    private ImageView newsImage;
    private ImageView myImage;
    private TextView shopText;
    private TextView memberText;
    private TextView newsText;
    private TextView myText;
    // 定义几个颜色
    private int whirt = 0xFFF7F6F6;
    private int gray = 0xFF808080;
    private int blue = 0xff63B8FF;
    // 定义FragmentManager对象管理器
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //自定义宽高（高度一般不用调整，在xml调整好就可以了，这里我只调整了宽度）
        WindowManager.LayoutParams params = getWindow().getAttributes();
        //去掉虚拟按钮，全屏显示
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().setAttributes(params);
        //加载布局
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        initView(); // 初始化界面控件
        setChioceItem(0); // 初始化页面加载时显示第一个选项卡
        doHideBackBtn();



    }
    /**
     * 初始化页面
     */
    private void initView() {
// 初始化底部导航栏的控件
        shopImage = (ImageView) findViewById(R.id.shop_image);
        memberImage = (ImageView) findViewById(R.id.member_image);
        newsImage = (ImageView) findViewById(R.id.news_image);
        myImage = (ImageView) findViewById(R.id.my_image);
        shopText = (TextView) findViewById(R.id.shop_text);
        memberText = (TextView) findViewById(R.id.member_text);
        newsText = (TextView) findViewById(R.id.news_text);
        myText = (TextView) findViewById(R.id.my_text);
        shopLayout = (RelativeLayout) findViewById(R.id.shop_layout);
        memberLayout = (RelativeLayout) findViewById(R.id.member_layout);
        newsLayout = (RelativeLayout) findViewById(R.id.news_layout);
        myLayout = (RelativeLayout) findViewById(R.id.my_layout);
        shopLayout.setOnClickListener(MainActivity.this);
        memberLayout.setOnClickListener(MainActivity.this);
        newsLayout.setOnClickListener(MainActivity.this);
        myLayout.setOnClickListener(MainActivity.this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shop_layout:
                setChioceItem(0);
                break;
            case R.id.member_layout:
                setChioceItem(1);
                break;
            case R.id.news_layout:
                setChioceItem(2);
                break;
            case R.id.my_layout:
                setChioceItem(3);
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2, 3
     */
    private void setChioceItem(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
// firstImage.setImageResource(R.drawable.XXXX); 需要的话自行修改
                shopText.setTextColor(blue);
                shopLayout.setBackgroundColor(whirt);
                shopImage.setImageResource(R.drawable.menu_myshop_on);
// 如果shopFragment为空，则创建一个并添加到界面上
                if (shopFragment == null) {
                    shopFragment = new ShopFragment();
                    fragmentTransaction.add(R.id.content, shopFragment);
                } else {
// 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(shopFragment);
                }
                break;
            case 1:
                memberImage.setImageResource(R.drawable.menu_member_on);
                memberText.setTextColor(blue);
                memberLayout.setBackgroundColor(whirt);
                if (memberFragment == null) {
                    memberFragment = new MemberFragment();
                    fragmentTransaction.add(R.id.content, memberFragment);
                } else {
                    fragmentTransaction.show(memberFragment);
                }
                break;
            case 2:
                newsImage.setImageResource(R.drawable.menu_news_on);
                newsText.setTextColor(blue);
                newsLayout.setBackgroundColor(whirt);
                if (newsFragment == null) {
                    newsFragment = new NewsFragment();
                    fragmentTransaction.add(R.id.content, newsFragment);
                } else {
                    fragmentTransaction.show(newsFragment);
                }
                break;
            case 3:
                myImage.setImageResource(R.drawable.menu_my_on);
                myText.setTextColor(blue);
                myLayout.setBackgroundColor(whirt);
                if (myFragment == null) {
                    myFragment = new MyFragment();
                    fragmentTransaction.add(R.id.content, myFragment);
                } else {
                    fragmentTransaction.show(myFragment);
                }
                break;
        }
        fragmentTransaction.commit(); // 提交
    }
    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        shopImage.setImageResource(R.drawable.menu_myshop_off);
        shopText.setTextColor(gray);
        shopLayout.setBackgroundColor(whirt);

        memberImage.setImageResource(R.drawable.menu_member_off);
        memberText.setTextColor(gray);
        memberLayout.setBackgroundColor(whirt);

        newsImage.setImageResource(R.drawable.menu_news_off);
        newsText.setTextColor(gray);
        newsLayout.setBackgroundColor(whirt);

        myImage.setImageResource(R.drawable.menu_my_off);
        myText.setTextColor(gray);
        myLayout.setBackgroundColor(whirt);
    }
    /**
     * 隐藏Fragment
     *
     * @param fragmentTransaction
     */
    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if (shopFragment != null) {
            fragmentTransaction.hide(shopFragment);
        }
        if (memberFragment != null) {
            fragmentTransaction.hide(memberFragment);
        }
        if (newsFragment != null) {
            fragmentTransaction.hide(newsFragment);
        }
        if (myFragment != null) {
            fragmentTransaction.hide(myFragment);
        }
    }

    @Override
    protected void setupUi() {

    }

    @Override
    protected void setupData() {

    }
    public void onCreateCustomToolBar(Toolbar toolbar){
        super.onCreateCustomToolBar(toolbar);
    }

}