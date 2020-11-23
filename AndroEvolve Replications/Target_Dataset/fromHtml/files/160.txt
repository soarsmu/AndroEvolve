package com.example.surbhitrao.myapplication.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.surbhitrao.myapplication.R;
import com.example.surbhitrao.myapplication.model.DetailItem;
import com.example.surbhitrao.myapplication.model.GridItem;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DetailsActivity extends AppCompatActivity {
    private TextView titleTextView;
    private ImageView imageView;
    private TextView OverviewText;
    private TextView UserRating;
    private TextView ReleaseDate;
    private TextView Runtime;
    private TextView Genre;
    private TextView Lang;
    private TextView Budget;
    private TextView Revenue;





    String movieid;
    Toolbar tl;
    String forecastJsonStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);


        String title = getIntent().getStringExtra("title");
        actionBar.setTitle(title);
        String image = getIntent().getStringExtra("backd");
        Log.i("hghg",image);
        String overview = getIntent().getStringExtra("overview");
        String votes = getIntent().getStringExtra("votes");
        String r_dates = getIntent().getStringExtra("r_dates");
        movieid= getIntent().getStringExtra("id");

        titleTextView = (TextView) findViewById(R.id.title);
        OverviewText = (TextView) findViewById(R.id.overview);
        UserRating=(TextView) findViewById(R.id.votes);
        ReleaseDate=(TextView) findViewById(R.id.dates);
        imageView = (ImageView) findViewById(R.id.grid_item_image);
        Runtime=(TextView) findViewById(R.id.runtime);
        Genre=(TextView) findViewById(R.id.genre);
        Lang=(TextView) findViewById(R.id.lang);
        Budget=(TextView) findViewById(R.id.budg);
        Revenue=(TextView) findViewById(R.id.rev);

        Picasso.with(this).load(image).into(imageView);

        new GetDetails().execute();










        //new AsyncHttpTask().execute("http://api.themoviedb.org/3/movie/");


    }


    private class GetDetails extends AsyncTask<Void, Void, Void>{

        String description;
        String backdropPath;
        String runtime;
        String releaseDate;
        String rating;
        String genres;
        String language;
        String budget;
        String revenue;


        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/" + movieid + "?api_key=9610e24872b8b9b8aa0fe214baa00bb1&language=en-US");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                String response = builder.toString();

                JSONObject responseObject = new JSONObject(response);
                backdropPath = "http://image.tmdb.org/t/p/w342" + responseObject.getString("backdrop_path");
                description = responseObject.getString("overview");
                runtime = Integer.toString(responseObject.getInt("runtime")) + " minutes";
                //TODO Format date according to document
                releaseDate = responseObject.getString("release_date");
                rating = Double.toString(responseObject.getDouble("vote_average"));
                JSONArray genresArray = responseObject.getJSONArray("genres");
                genres = "";
                for(int i = 0 ; i < genresArray.length() ; i++){
                    JSONObject genre = genresArray.getJSONObject(i);
                    String currentGenre = genre.getString("name");
                    if (i == 0){
                        genres = currentGenre;
                    }
                    else{
                        genres = genres + ", " + currentGenre;
                    }
                }
                language = responseObject.getString("original_language");
                int budgetInt = responseObject.getInt("budget");
                budgetInt = (int) (budgetInt / Math.pow(10, 6));
                budget = "$" + Integer.toString(budgetInt) + " Million";
                int revenueInt = responseObject.getInt("revenue");
                revenueInt = (int) (revenueInt / Math.pow(10, 6));
                revenue = "$" + Integer.toString(revenueInt) + " Million";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.i("desz",description);
            Log.i("rus",runtime);

            Runtime.setText(runtime);
            Genre.setText(genres);
            Lang.setText(language);
            Budget.setText(budget);
            Revenue.setText(revenue);

            String title = getIntent().getStringExtra("title");
            //actionBar.setTitle(title);
            String image = getIntent().getStringExtra("backd");
            Log.i("hghg",image);
            String overview = getIntent().getStringExtra("overview");
            String votes = getIntent().getStringExtra("votes");
            String r_dates = getIntent().getStringExtra("r_dates");
            movieid= getIntent().getStringExtra("id");

            titleTextView.setText(Html.fromHtml(title));
            OverviewText.setText(Html.fromHtml(overview));
            UserRating.setText(Html.fromHtml(votes));
            ReleaseDate.setText(Html.fromHtml(r_dates));





        }
    }


}
