package peterjasko.places;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.place_list)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fabIcon;

    PlaceAdapter adapter;
    List<Place> places;
    List<Place> favorites;
    boolean favoritesShowing;

    ItemTouchHelper itemTouchHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getInstance().register(this);

        if(!haveNetworkConnection()) {
            showDialog();
        }

        places = new ArrayList<Place>();
        favorites = new ArrayList<Place>();
        favoritesShowing = false;

        GetLocationsTask getLocationsTask = new GetLocationsTask();
        getLocationsTask.getPlaces();

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PlaceAdapter(this,places);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = getSimpleCallback();
        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @OnClick(R.id.fab)
    public void changeListView() {
        if(!favoritesShowing) {
            if(favorites.size() == 0) {
                Toast.makeText(getApplicationContext(),"You don't have any favorites yet!",Toast.LENGTH_SHORT).show();
            } else {
                adapter = null;
                adapter = new PlaceAdapter(this, favorites);
                favoritesShowing = true;
                fabIcon.setImageResource(R.drawable.ic_arrow_back_white_24dp);
                Toast.makeText(getApplicationContext(), "Scroll down to view all favorites!", Toast.LENGTH_SHORT).show();
                itemTouchHelper.attachToRecyclerView(null);
            }

        } else {
            adapter = null;
            adapter = new PlaceAdapter(this,places);
            favoritesShowing = false;
            fabIcon.setImageResource(R.drawable.ic_star_white_24dp);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(places.size() == 0 && favorites.size() == 0) {
            places.clear();
            GetLocationsTask getLocationsTask = new GetLocationsTask();
            getLocationsTask.getPlaces();
        }
    }
    @Override
    protected void onDestroy() {
        EventBus.getInstance().unregister(this);
        super.onDestroy();
    }

    private boolean haveNetworkConnection() {
        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    connected = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    connected = true;
        }
        return connected;
    }

    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Connect to Wifi to Use Places")
                .setCancelable(false)
                .setPositiveButton("Wifi Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @NonNull
    private ItemTouchHelper.SimpleCallback getSimpleCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(swipeDir == ItemTouchHelper.LEFT) {
                    places.remove(viewHolder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"Disliked",Toast.LENGTH_SHORT).show();
                } else {
                    favorites.add(places.get(viewHolder.getAdapterPosition()));
                    places.remove(viewHolder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"Favorited",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    //Otto Subscriptions
    @Subscribe
    public void onGetLocationsResultEvent(GetLocationsResultEvent event) {
        places.addAll(event.getPlaces());
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onLaunchMapResultEvent(LaunchMapResult event) {
        Uri locationUri = event.getLocationUri();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
        startActivity(mapIntent);
    }
    @Subscribe
    public void onSharePlaceResultEvent(SharePlaceResult event) {
           startActivity(event.getShareIntent());
    }
}
