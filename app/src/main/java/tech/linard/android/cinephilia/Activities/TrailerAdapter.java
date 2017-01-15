package tech.linard.android.cinephilia.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tech.linard.android.cinephilia.Model.Review;
import tech.linard.android.cinephilia.Model.Trailer;
import tech.linard.android.cinephilia.R;

/**
 * Created by lucas on 15/01/17.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer>  {

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers) {
        super(context, 0, trailers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, parent, false);
        }
        Trailer currTrailer= getItem(position);
        TextView content = (TextView) listItemView.findViewById(R.id.trailer_item_name);

        content.setText(currTrailer.getName());

        return listItemView;
    }
}
