package com.sh.browser.activities;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.sh.browser.R;
import com.sh.browser.UCAppliCation;
import com.sh.browser.fragment.CommonFragment;
import com.sh.browser.utils.CommonUtils;
import com.sh.browser.views.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

public class CollectionAndHistoryActivity extends BaseActivity {
    private TabPageIndicator indicator;
    private ViewPager viewPager;
    private List<Fragment>fragments = new ArrayList<>();

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_collection_and_history;
    }

    @Override
    protected void initViews() {
        super.initViews();
        indicator = findViewById(R.id.indicator);
        viewPager = findViewById(R.id.viewPager);
        fragments.add(0, CommonFragment.newInstance("0"));//收藏
        fragments.add(1, CommonFragment.newInstance("1"));//历史
        BasePagerAdapter adapter = new BasePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        setTabPagerIndicator();
    }

    private void setTabPagerIndicator() {
        indicator.setIndicatorMode(TabPageIndicator.IndicatorMode.MODE_WEIGHT_NOEXPAND_SAME);// 设置模式，一定要先设置模式
        indicator.setDividerColor(Color.parseColor("#00bbcf"));// 设置分割线的颜色
        indicator.setDividerPadding(CommonUtils.dip2px(UCAppliCation.getContext(), 10));
        indicator.setIndicatorColor(Color.parseColor("#FFFFFF"));// 设置底部导航线的颜色
        indicator.setTextColorSelected(Color.parseColor("#FFFFFF"));// 设置tab标题选中的颜色
        indicator.setTextColor(Color.parseColor("#d2d2d2"));// 设置tab标题未被选中的颜色
        indicator.setTextSize(CommonUtils.sp2px(UCAppliCation.getContext(), 20));// 设置字体大小
    }

    class BasePagerAdapter extends FragmentPagerAdapter {
        String[] titles;

        public BasePagerAdapter(FragmentManager fm) {
            super(fm);
            this.titles = CommonUtils.getStringArray(R.array.no_expand_titles);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
