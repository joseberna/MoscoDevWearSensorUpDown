

package demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseberna on 24/11/15.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragments = null;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<Fragment>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }
}
