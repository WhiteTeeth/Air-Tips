package org.baiya.practice.pm25;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.baiya.practice.pm25.bean.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LocationAdapter extends BaseAdapter {

    private List<City> mCityArray;
    private List<City> mShowCityArray;
    private LocationSelectedActivity mActivity;

    public LocationAdapter(LocationSelectedActivity activity) {
        mActivity = activity;
        mCityArray = new ArrayList<City>();
        String[] locationArray = activity.getResources().getStringArray(R.array.location);
        for (String city : locationArray) {
            String[] list = city.split("=");
            City location = new City();
            location.chRCN = list[0];
            location.chRTW = list[1];
            location.en = list[2];
            mCityArray.add(location);
        }
        Collections.sort(mCityArray, new Comparator<City>() {
            @Override
            public int compare(City lhs, City rhs) {
                return lhs.en.compareTo(rhs.en);
            }
        });
        mShowCityArray = new ArrayList<City>(mCityArray);
    }

    public void changeSearch(String search) {
        mShowCityArray.clear();
        Iterator<City> iterator = mCityArray.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                notifyDataSetChanged();
                return;
            }
            City city = iterator.next();
            if ((!TextUtils.isEmpty(search)) && (!city.chRCN.contains(search))) {
                if (!city.en.contains(search.toLowerCase()) && (!city.chRTW.contains(search))) {
                    continue;
                }
            }
            mShowCityArray.add(city);
        }
    }

    @Override
    public int getCount() {
        return mShowCityArray.size();
    }

    @Override
    public City getItem(int position) {
        return mShowCityArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mActivity, R.layout.location_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        City city = mShowCityArray.get(position);
        Character character = Character.valueOf(city.en.charAt(0));
        if (position == 0) {
            holder.mPlaceCategory.setText(character.toString());
            holder.mPlaceBtn.setText(city.getLocaleStr(mActivity));
        } else {
            Character lastCharacter = Character.valueOf(
                    mShowCityArray.get(position - 1).en.charAt(0));
            holder.mPlaceBtn.setText(city.getLocaleStr(mActivity));
            if (character.charValue() == lastCharacter.charValue()) {
                holder.mPlaceCategory.setVisibility(View.GONE);
                holder.mDivider.setVisibility(View.GONE);
            } else {
                holder.mPlaceCategory.setVisibility(View.VISIBLE);
                holder.mDivider.setVisibility(View.VISIBLE);
                holder.mPlaceCategory.setText(character.toString());
            }
        }
        return convertView;
    }


    class ViewHolder {
        TextView mPlaceBtn;
        TextView mPlaceCategory;
        View mDivider;

        ViewHolder(View convertView) {
            mPlaceBtn = (TextView) convertView.findViewById(R.id.place_btn);
            mPlaceCategory = (TextView) convertView.findViewById(R.id.place_category);
            mDivider = convertView.findViewById(R.id.category_divider);
        }
    }
}
