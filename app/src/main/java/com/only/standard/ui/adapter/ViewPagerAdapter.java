package com.only.standard.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import static com.only.coreksdk.utils.LogUtils.*;
import java.util.List;


/**
 * Created by only on 16/6/15.
 * Email: onlybeyond99@gmail.com
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList=fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        LOGD("fragment","size"+fragmentList.size());
        return fragmentList.size();

    }

}
