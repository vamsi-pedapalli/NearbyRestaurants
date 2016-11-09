package com.example.android.restaurantsnearby;

import android.content.Context;
import android.location.Location;
import android.media.Image;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.rating;
import static android.R.attr.resource;

/**
 * Created by vamsi on 05-11-2016.
 */

public class RestaurantAdapter extends ArrayAdapter<Restaurants> {

    public RestaurantAdapter(Context context, ArrayList<Restaurants> Restaurant) {
        super(context, 0, Restaurant);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.restaurants_list, parent, false);
        }

        Restaurants currentItem = getItem(position);

        TextView name = (TextView) listItemView.findViewById(R.id.Restaurant_Name);
        name.setText(currentItem.getName());

        TextView distance = (TextView) listItemView.findViewById(R.id.distance);
        double dist = ((calculateDistance(MainActivity.getUserLatitude(),MainActivity.getUserLongitude(),currentItem.getLatitude(),currentItem.getLongitude()))/1000);
        if(dist<=10){
        String formattedDistance = formatdouble(dist) + "km";
        distance.setText(formattedDistance);}
        else if(dist<=100 && dist > 10){
            distance.setText("10 km +");
        }
        else{
            distance.setText("Too Far");
        }

        ImageView loc = (ImageView) listItemView.findViewById(R.id.loca);
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });

        TextView rating = (TextView) listItemView.findViewById(R.id.Restaurant_rating);
        String formattedRating = formatdouble(currentItem.getRating());
        rating.setText(formattedRating);

        TextView address = (TextView) listItemView.findViewById(R.id.rAddress);
        address.setText(currentItem.getAddress());

        ImageView thumbImage = (ImageView) listItemView.findViewById(R.id.thumb);
        String Url = currentItem.getUrl();
        if (TextUtils.isEmpty(Url)) {
            Picasso.with(getContext()).load(R.drawable.sample_thumb).into(thumbImage);
        } else {
            Picasso.with(getContext()).load(Url).into(thumbImage);
        }
        return listItemView;
    }

    private String formatdouble(double rating) {
        DecimalFormat ratingFormat = new DecimalFormat("#.00");
        return ratingFormat.format(rating);
    }
    double calculateDistance(double uLat, double uLong, double rLat, double rLong) {
        Location locationA = new Location("user location");
        locationA.setLatitude(uLat);
        locationA.setLongitude(uLong);
        Location locationB = new Location("restaurant location");
        locationB.setLatitude(rLat);
        locationB.setLongitude(rLong);
        double distance = locationA.distanceTo(locationB);
        return distance;
    }

}
