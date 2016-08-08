package com.uv.stormy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uv.stormy.R;
import com.uv.stormy.weather.Hour;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Used by RecyclerView
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder>{

    private Hour[] mHours;
    // Where this adapter is being used
    private Context mContext;

    public HourAdapter(Context context,Hour[] hours){
        mContext=context;
        mHours = hours;
    }

    // Called when new Views are needed
    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("HourAdapter","onCreateViewHolder("+parent +" ,"+viewType+")");
        // This is the spot where new Views are created when they are needed
        // not reused! (like where we check for ViewGroup being a null in DayAdapter)

        // 1. Inflate(create) View from XML
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_list_item,parent, false);

        HourViewHolder viewHolder = new HourViewHolder(view);

        return viewHolder;
    }

    /** Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the RecyclerView.ViewHolder.itemView
     * to reflect the item at the given position.
     *
     * Вызывается сразу после каждого onCreateViewHolder(), а затем каждый раз при REUSE
     */
    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        // bind appropriate hour data to our HourViewHolder
        // чтобы показать, что метод вызывается когда надо сделать reuse. отследи по адрессу ячейки памяти
        holder.bindHour(mHours[position]);
        Log.d("HourAdapter","onBindViewHolder("+holder +" ,"+position+")");
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }


    // Our own ViewHolder implementation
    public class HourViewHolder extends RecyclerView.ViewHolder
            implements  View.OnClickListener{

        @BindView(R.id.timeLabel)public TextView mTimeLabel;
        @BindView(R.id.summaryLabel)public TextView mSummaryLabel;
        @BindView(R.id.temperatureLabel)public TextView mTemperatureLabel;
        @BindView(R.id.iconImageView)public ImageView mIconImageView;

        public HourViewHolder(View itemView) {
            super(itemView);

//            mTimeLabel=(TextView)itemView.findViewById(R.id.timeLabel);
//            mSummaryLabel=(TextView)itemView.findViewById(R.id.summaryLabel);
//            mTemperatureLabel=(TextView)itemView.findViewById(R.id.temperatureLabel);
//            mIconImageView=(ImageView)itemView.findViewById(R.id.iconImageView);
            // target - Target class for view binding.
            // source - View root on which IDs will be looked up.
            ButterKnife.bind(this, itemView);
            //set a listener for the root view in the ViewHolder
            itemView.setOnClickListener(this);
        }

        // Bind hour fields to according views
        public void bindHour(Hour hour){
            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mTemperatureLabel.setText(hour.getTemperature()+"");
            mIconImageView.setImageResource(hour.getIconId());
        }

        // For debugging purposes
        // hashcode allows to track when ViewHolder is being reused (it doesn't change)
        @Override
        public String toString() {
            return "HourViewHolder{" +
                    "\nhashcode=" + this.hashCode() +
                    "\nmTimeLabel=" + mTimeLabel.getText() +
                    ",\nmSummaryLabel=" + mSummaryLabel.getText() +
                    ",\nmTemperatureLabel=" + mTemperatureLabel.getText() +
                    ",\nmIconImageView=" + mIconImageView.getId() +
                    "\n}";
        }

        /* Instead of providing a listener on the RecyclerView itself,
        the responsibility is handed off to whatever wants to implement
        the interface for a click listener. */
        @Override
        public void onClick(View view) {
            String time=mTimeLabel.getText().toString();
            String temperature=mTemperatureLabel.getText().toString();
            String summary=mSummaryLabel.getText().toString();
            String message= String.format("At %s it will be %s and %s",time, temperature, summary);
            // we need to pass the context where this adapter is being used
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }




}
