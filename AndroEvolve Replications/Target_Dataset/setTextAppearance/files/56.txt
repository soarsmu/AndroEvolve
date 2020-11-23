package com.example.maxwell.desafioeficiente;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {


    List<Cars> cars = new ArrayList<Cars>();
    Cars car1;
    Cars car2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SQLiteDatabase database = DbCreate();
        DropTable(database);
        TableCreate(database);
        InsertCar(database);
        cars = GetAllCars(database);

        TabHost tabhost = (TabHost) findViewById(R.id.tabHost);
        final TableLayout t2 = (TableLayout) findViewById(R.id.TableLayout2);

        tabhost.setup();

        TabHost.TabSpec tabspec = tabhost.newTabSpec("carros");
        tabspec.setContent(R.id.Carros);
        tabspec.setIndicator("Carros");
        tabhost.addTab(tabspec);

        tabspec = tabhost.newTabSpec("comparar");
        tabspec.setContent(R.id.Comparar);
        tabspec.setIndicator("Comparar");
        tabhost.addTab(tabspec);


        List<String> spinnerArray =  new ArrayList<String>();

        for (int i = 0; i < cars.size(); i++) {
            spinnerArray.add(cars.get(i).getModel().toString());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sItems = (Spinner) findViewById(R.id.spinner);
        final Spinner sItems2 = (Spinner) findViewById(R.id.spinner2);
        sItems.setAdapter(adapter);
        sItems2.setAdapter(adapter);

        TableLayout tl = (TableLayout) findViewById(R.id.TableLayout);


        final Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    t2.removeAllViews();


              car1 = GetCarByModel( database, sItems.getSelectedItem().toString());
              car2 = GetCarByModel( database, sItems2.getSelectedItem().toString());
              preencheTable(t2);

            }
        });

        TableRow tr;
        TextView tv;


       for (int i = 0; i < cars.size(); i++) {
           tr = new TableRow(this);
           tv = new TextView(this);

           tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
           TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);

           tv.setText(cars.get(i).getModel().toString());
           tv.setLayoutParams(paramsExample);
           tv.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);


           tr.addView(tv);
           tr.setClickable(true);


           tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


       }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void preencheTable(TableLayout t2){



        TableRow tr = new TableRow(this);
        TableRow tr2 = new TableRow(this);
        TableRow tr3 = new TableRow(this);
        TableRow tr4 = new TableRow(this);
        TableRow tr5 = new TableRow(this);
        TableRow tr6 = new TableRow(this);
        TableRow tr7 = new TableRow(this);
        TableRow tr8 = new TableRow(this);

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);


        TextView tv1 = new TextView(this);
        TextView tv2 = new TextView(this);
        TextView tv3 = new TextView(this);

        tv1.setLayoutParams(paramsExample);
        tv1.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv2.setLayoutParams(paramsExample);
        tv2.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv3.setLayoutParams(paramsExample);
        tv3.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv1.setText("MODELO");
        tv2.setText(car1.getModel().toString());
        tv3.setText(car2.getModel().toString());

        tr.addView(tv1);
        tr.addView(tv2);
        tr.addView(tv3);

        t2.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));



        TextView tv4 = new TextView(this);
        TextView tv5 = new TextView(this);
        TextView tv6 = new TextView(this);


        tv4.setLayoutParams(paramsExample);
        tv4.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv5.setLayoutParams(paramsExample);
        tv5.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv6.setLayoutParams(paramsExample);
        tv6.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv4.setText("PRECO");
        tv5.setText("R$ " + car1.getPrice().toString());
        tv6.setText("R$ " + car2.getPrice().toString());

        tr2.addView(tv4);
        tr2.addView(tv5);
        tr2.addView(tv6);
        t2.addView(tr2, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        TextView tv7 = new TextView(this);
        TextView tv8 = new TextView(this);
        TextView tv9 = new TextView(this);

        tv7.setLayoutParams(paramsExample);
        tv7.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv8.setLayoutParams(paramsExample);
        tv8.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv9.setLayoutParams(paramsExample);
        tv9.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv7.setText("CAVALOS");
        tv8.setText(car1.getHp().toString());
        tv9.setText(car2.getHp().toString());

        tr3.addView(tv7);
        tr3.addView(tv8);
        tr3.addView(tv9);
        t2.addView(tr3, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));



        TextView tv10 = new TextView(this);
        TextView tv11 = new TextView(this);
        TextView tv12 = new TextView(this);

        tv10.setLayoutParams(paramsExample);
        tv10.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv11.setLayoutParams(paramsExample);
        tv11.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv12.setLayoutParams(paramsExample);
        tv12.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv10.setText("CONSUMO GAS");
        tv11.setText(car1.getConsumptionGas().toString());
        tv12.setText(car2.getConsumptionGas().toString());

        tr4.addView(tv10);
        tr4.addView(tv11);
        tr4.addView(tv12);
        t2.addView(tr4, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv13 = new TextView(this);
        TextView tv14 = new TextView(this);
        TextView tv15 = new TextView(this);

        tv13.setLayoutParams(paramsExample);
        tv13.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv14.setLayoutParams(paramsExample);
        tv14.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv15.setLayoutParams(paramsExample);
        tv15.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv13.setText("CONSUMO ETAN");
        tv14.setText(car1.getConsumptionEth().toString());
        tv15.setText(car2.getConsumptionEth().toString());

        tr5.addView(tv13);
        tr5.addView(tv14);
        tr5.addView(tv15);
        t2.addView(tr5, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv16 = new TextView(this);
        TextView tv17 = new TextView(this);
        TextView tv18 = new TextView(this);

        tv16.setLayoutParams(paramsExample);
        tv16.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv17.setLayoutParams(paramsExample);
        tv17.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv18.setLayoutParams(paramsExample);
        tv18.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv16.setText("REVISAO");
        tv17.setText("R$ " + car1.getRevisionAVG().toString());
        tv18.setText("R$ " + car2.getRevisionAVG().toString());

        tr6.addView(tv16);
        tr6.addView(tv17);
        tr6.addView(tv18);
        t2.addView(tr6, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv19 = new TextView(this);
        TextView tv20 = new TextView(this);
        TextView tv21 = new TextView(this);

        tv19.setLayoutParams(paramsExample);
        tv19.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv20.setLayoutParams(paramsExample);
        tv20.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv21.setLayoutParams(paramsExample);
        tv21.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv19.setText("SEGURO");
        tv20.setText("R$ " + car1.getRevisionAVG().toString());
        tv21.setText("R$ " + car2.getRevisionAVG().toString());

        tr7.addView(tv19);
        tr7.addView(tv20);
        tr7.addView(tv21);
        t2.addView(tr7, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv22 = new TextView(this);
        TextView tv23 = new TextView(this);
        TextView tv24 = new TextView(this);

        tv22.setLayoutParams(paramsExample);
        tv22.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv23.setLayoutParams(paramsExample);
        tv23.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv24.setLayoutParams(paramsExample);
        tv24.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Medium);

        tv22.setText("PONTOS");
        tv23.setText(car1.getPoints().toString());
        tv24.setText(car2.getPoints().toString());

        tr8.addView(tv22);
        tr8.addView(tv23);
        tr8.addView(tv24);
        t2.addView(tr8, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private SQLiteDatabase DbCreate(){
        return openOrCreateDatabase("desafioEficiente.db", MODE_PRIVATE, null);
    }

    private void TableCreate(SQLiteDatabase database){
        database.execSQL("create table if not exists car (model text, hp text, price text, consumptiongas text, consumptioneht text, revisionavg text, " +
                "insuranceavg text, points text)");
    }

    private void InsertCar(SQLiteDatabase database){
        database.execSQL("insert into car values ('VW Gol 1.0 2015', '68', '29500', '15', '13', '365', '2700', '4')");
        database.execSQL("insert into car values ('Fiat Palio 1.0 2015', '65', '28500', '14', '12', '360', '2500', '3')");
        database.execSQL("insert into car values ('FordKa 1.0 2015', '60', '27000', '17', '15', '300', '1700', '5')");
        database.execSQL("insert into car values ('Chevrolet Vectra 2.0 2015', '80', '40500', '10', '12', '500', '3500', '6')");
        database.execSQL("insert into car values ('Chevrolet Onix 1.6 2015', '70', '35500', '15', '12', '365', '2700', '4')");
        database.execSQL("insert into car values ('VW Fusca 1.0 1990', '50', '10000', '14', '12', '360', '2500', '3')");

    }

    private void DropTable(SQLiteDatabase database){
        database.execSQL("drop table if exists car");
    }

    private Cars GetCarByModel(SQLiteDatabase database, String modelo){
        Cursor cursor = database.rawQuery("select * from car where model = " + "'" + modelo+ "'", null);
        Cars car = new Cars();
        if (cursor.moveToFirst()) {
            car = new Cars(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
        }
        return car;
    }


     private List<Cars> GetAllCars (SQLiteDatabase database){
         Cursor cursor = database.rawQuery("select * from car", null);
         List<Cars> cars = new ArrayList<Cars>();

         if (cursor != null) {

             // move cursor to first row

             if (cursor.moveToFirst()) {

                 do {

                     // Get version from Cursor

                     //String bookName = cursor.getString(cursor.getColumnIndex("bookTitle"));

                     cars.add(new Cars(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));


                     // add the bookName into the bookTitles ArrayList



                     // move to next row

                 } while (cursor.moveToNext());

             }

         }
         return cars;
     }


}
