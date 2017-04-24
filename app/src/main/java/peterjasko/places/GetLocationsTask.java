package peterjasko.places;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetLocationsTask {
    public static final String url = "https://gist.githubusercontent.com/shreyansb/678d35d7efaa4cbfb81d/raw/7e04c3d88f6c06d7a794ae570f39a96107b18457/";
    private Retrofit retrofit;
    private GetPlacesInterface getPlaces;

    public GetLocationsTask() {
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        getPlaces = retrofit.create(GetPlacesInterface.class);
    }

    public void getPlaces() {
        Call<List<Place>> call =  getPlaces.getPlace();
        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                EventBus.getInstance().post(new GetLocationsResultEvent(response.body()));
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                Log.v("Failure","Failure to load images");
            }
        });
    }
}

