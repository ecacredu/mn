package com.mynagarsevak.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.mynagarsevak.fragments.BlankFragment;
import com.mynagarsevak.fragments.MessagesFragment;
import com.mynagarsevak.fragments.MyIssueFragment;
import com.mynagarsevak.fragments.PublicIssueFragment;

/**
 * Created by sd on 18-04-2017.
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {

  SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

  public MyPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int pos) {
    switch (pos) {

      case 0:
        return MyIssueFragment.newInstance("FirstFragment");
      case 1:
        return PublicIssueFragment.newInstance("PublicIssueFragment");
      case 2:
        return MessagesFragment.newInstance("MessagesFragment");
      default:
        return BlankFragment.newInstance("BlankFragment");
    }
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    Fragment fragment = (Fragment) super.instantiateItem(container, position);
    registeredFragments.put(position, fragment);
    return fragment;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    registeredFragments.remove(position);
    super.destroyItem(container, position, object);
  }

  public Fragment getRegisteredFragment(int position) {
    return registeredFragments.get(position);
  }

  @Override
  public int getCount() {
    return 3;
  }

}
