package com.udacity.sandwichclub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import java.io.StringReader;
import java.util.List;

import static java.lang.System.in;
import static java.lang.System.out;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView imageIv = findViewById(R.id.image_iv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);
        Picasso.with(this)
                .load(sandwich.getImage())
                .into(imageIv);

        setTitle(sandwich.getMainName());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Sandwich sandwich) {
        String mainName = sandwich.getMainName();
        String description = sandwich.getDescription();
        String placeOfOrigin = sandwich.getPlaceOfOrigin();
        List<String> ingredients =   sandwich.getIngredients();
        List<String> alsoKnownAs = sandwich.getAlsoKnownAs();

        TextView name_tv = findViewById(R.id.name_tv);
        TextView description_tv = findViewById(R.id.description_tv);
        TextView alsoKnownAs_tv = findViewById(R.id.alsoKnownAs_tv);
        TextView placeOfOrigin_tv  = findViewById(R.id.placeOfOrigin_tv);
        TextView Ingredients_tv  = findViewById(R.id.ingredients_tv);



        String  name_tv_Styled= "<br><font color='#b2b2b2'>"+mainName+"</font><br>";
        name_tv.append( Html.fromHtml( name_tv_Styled));


        String  description_tv_Styled= "<br><font color='#b2b2b2'>"+description+"</font><br>";
        description_tv.append( Html.fromHtml( description_tv_Styled));

        String  placeOfOrigin_tv_Styled= "<br><font color='#b2b2b2'>"+placeOfOrigin+"</font><br>";
        placeOfOrigin_tv.append( Html.fromHtml( placeOfOrigin_tv_Styled));

        String alsoKnownAsString = "";
        int listLength = alsoKnownAs.size();
        int i = 0;
        for (String item : alsoKnownAs ) {
            if (i != listLength-1){
            alsoKnownAsString += item +", ";
            i++;
            }
            else {
                alsoKnownAsString += item;
            }
        }

        String  alsoKnownAsStringStyled= "<br><font color='#b2b2b2'>"+alsoKnownAsString+"</font><br>";
        alsoKnownAs_tv.append( Html.fromHtml( alsoKnownAsStringStyled));

        String ingredientsString = "";
        int ingListLength = ingredients.size();
        int j=0;
        for (String ingred : ingredients ) {
            if (j != ingListLength-1) {
                ingredientsString += ingred + ", ";
                j++;
            }
            else
            {
                ingredientsString += ingred;
            }
        }

        String  Ingredients_tv_Styled= "<br><font color='#b2b2b2'>"+ingredientsString+"</font><br>";
        Ingredients_tv.append( Html.fromHtml( Ingredients_tv_Styled));

    }
}
