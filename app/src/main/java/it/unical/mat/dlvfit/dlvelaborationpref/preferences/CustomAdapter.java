package it.unical.mat.dlvfit.dlvelaborationpref.preferences;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import it.unical.mat.dlvfit.R;

public class CustomAdapter extends ArrayAdapter<ListItem> {

    private final List<ListItem> list;
    private final Activity context;

    public CustomAdapter(Activity context, List<ListItem> list) {
        super(context, R.layout.rowcustom, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected RatingBar ratingBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.rowcustom, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.activity_name);
            viewHolder.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            viewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ListItem element = (ListItem) viewHolder.ratingBar.getTag();
                    element.setRate((int)rating);
                }
            });
            view.setTag(viewHolder);
            viewHolder.ratingBar.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).ratingBar.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getActivityName());
        holder.ratingBar.setRating(list.get(position).getRate());
        return view;
    }
}