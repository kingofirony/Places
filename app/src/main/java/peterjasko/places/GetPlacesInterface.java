package peterjasko.places;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetPlacesInterface {
    @GET("gistfile1.json")
    Call<List<Place>> getPlace();
}
