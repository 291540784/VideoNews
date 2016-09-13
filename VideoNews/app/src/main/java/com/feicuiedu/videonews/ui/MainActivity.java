package com.feicuiedu.videonews.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.ui.likes.LikesFragment;
import com.feicuiedu.videonews.ui.local.LocalVideoFragment;
import com.feicuiedu.videonews.ui.news.NewsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private Unbinder unbinder;
    @BindView(R.id.viewPager)ViewPager viewPager;
    @BindView(R.id.btnLikes)Button btnLikes;
    @BindView(R.id.btnLocal)Button btnLocal;
    @BindView(R.id.btnNews)Button btnNews;

    private final FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new NewsFragment();
                case 1:
                    return new LocalVideoFragment();
                case 2:
                    return new LikesFragment();
                default:
                    // TODO: 2016/9/9 0009
                    throw new RuntimeException("未知");
            }
        }

        @Override public int getCount() {
            return 3;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        unbinder = ButterKnife.bind(this);
        viewPager.setAdapter(adapter);
        // viewpager的监听 - Button的切换
        viewPager.addOnPageChangeListener(this);
        // 首次进入默认选中在线新闻
        btnNews.setSelected(true);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.btnNews,R.id.btnLikes,R.id.btnLocal})
    public void chooseFragment(Button button){
        switch (button.getId()){
            case R.id.btnNews:
                viewPager.setCurrentItem(0,false);
                break;
            case R.id.btnLocal:
                viewPager.setCurrentItem(1,false);
                break;
            case R.id.btnLikes:
                viewPager.setCurrentItem(2,false);
                break;
            default:
                // TODO: 2016/9/9 0009
                throw new RuntimeException("未知");
        }
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageSelected(int position) {
        btnNews.setSelected(position == 0);
        btnLocal.setSelected(position == 1);
        btnLikes.setSelected(position == 2);
    }

    @Override public void onPageScrollStateChanged(int state) {

    }
}
