package com.uv.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uv.stormy.R;
import com.uv.stormy.weather.Day;

import org.w3c.dom.Text;

/** Adapter to map each piece of data of the Day class to a field in a layout (daily_list_item.xml
 */
public class DayAdapter extends BaseAdapter{

    // adapter needs to know these fields
    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }


    /* Abstract methods inherited from an Adapter interface*/
    // How many items are in the data set represented by this Adapter.
    @Override
    public int getCount() {
        return mDays.length;
    }

    // Get the data item associated with the specified position in the data set.
    @Override
    public Object getItem(int i) {
        return mDays[i];
    }

    // Get the row id associated with the specified position in the list.
    @Override
    public long getItemId(int i) {
        return 0; // we aren't going to use this. Tag items fore easy reference
    }

    // Get a View that displays the data at the specified position in the data set.
    // This method is called for each item in the list and then each time we scroll item into the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*  position - position of the item in an adapter
            convertView - The old view to reuse, if possible. If it is null ,then it's called first time
            thus it must be instantiated. Else it is available to reuse
            parent - The parent that this view will eventually be attached to (ListView) */

        ViewHolder holder;

        if(convertView == null){
            // brand new
            // inflater takes xml layouts and turns them to views in code for use
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null); // null for ViewGroup. not needed here

            holder = new ViewHolder();
            holder.iconImageView = (ImageView)convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView)convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel=(TextView)convertView.findViewById(R.id.dayNameLabel);

            // Sets the tag associated with this view. We will use it for reusing this view
            convertView.setTag(holder);
        }else{
            // we have this view setup
            holder = (ViewHolder)convertView.getTag();
        }

        /* REUSING */
        // construct day object from passed position
        Day day = mDays[position];

        // update views to match Day's parameters
        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getTemperatureMax()+"");
        if(position ==0){
            holder.dayLabel.setText("сегодня");
        }else
            holder.dayLabel.setText(day.getDayOfTheWeek());

        // вернуть converView, который будет вставлен в ListView на позиции position
        return convertView;
    }

    // ViewHolder allows us to reuse the same references objects in the view
    // By reusing we save some performance and power
    /* A ViewHolder object stores each of the component views inside the tag field of the Layout,
    so you can immediately access them without the need to look them up repeatedly. */
    private static class ViewHolder {
        // note, we do not use "m" prefix here
        ImageView iconImageView; // public by default
        TextView temperatureLabel;
        TextView dayLabel;
    }

}
