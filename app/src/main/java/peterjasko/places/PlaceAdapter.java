package peterjasko.places;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    private List<Place> places;
    private Context context;

    public PlaceAdapter(Context c, List<Place> places) {
        this.places = places;
        this.context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_list_item, parent
                , false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Place place = places.get(position);

        holder.title.setText(place.getTitle());
        holder.location.setText(place.getLocation());

        final String imageUrl = "https://travelpoker-production.s3.amazonaws.com/uploads/card/image/"
                + place.getId() + "/" + place.getImage();
        Picasso.with(context).load(imageUrl).resize(600, 600).centerCrop()
                .placeholder(R.drawable.placeholder).into(holder.image);

        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://maps.google.com/maps?q="+ place.getLatitude() + "," +
                        place.getLongitude()+"("+ Uri.encode(place.getTitle())+")&iwloc=A&hl=es");
                EventBus.getInstance().post(new LaunchMapResult(uri));
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String sendText = "Hey! " + place.getTitle()+ ". Check out the picture at "
                        + imageUrl;
                sharingIntent.putExtra(Intent.EXTRA_TEXT,sendText);
                EventBus.getInstance().post(new SharePlaceResult(sharingIntent));
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.location) TextView location;
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.map_icon) ImageButton mapButton;
        @BindView(R.id.share_icon) ImageButton shareButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

