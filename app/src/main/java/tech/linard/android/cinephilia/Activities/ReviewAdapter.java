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
import tech.linard.android.cinephilia.R;

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        }
        Review currentReview = getItem(position);
        TextView content = (TextView) listItemView.findViewById(R.id.review_item_content_value);
        content.setText(currentReview.getAuthor());

        return listItemView;
    }
}
