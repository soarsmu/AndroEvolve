package edu.psm.projekt;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

/**
 * Klasa odpowiedzialna za pobranie danych do tworzenia wielu wydarzeń
 */
public class MultiAdds extends Activity {

    /**
     * Referencja do klasy głównej aktywności
     * @see MainActivity
     * oraz referencje do zmiennych nazwy wydarzenia, opisu, godziny i minuty oraz referencje typu int do godziny oraz minuty
     */
    private MainActivity mainActivity;
    public static String name;
    public static String description;
    public static String hour;
    public static String minute;
    public static int Godzin;
    public static int Minuta;

    /**
     * Metoda onCreate podpinająca layout oraz poszczególne kontrolki z niego
     * @param saveInstanceState
     */

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_layout2);

        final EditText NameEvent = findViewById(R.id.NameEvent);
        final EditText Description = findViewById(R.id.Describe_text);
        ImageButton next = findViewById(R.id.NextButton);
        final TimePicker picker = findViewById(R.id.TimerPicker);
        ImageButton CancelButton = findViewById(R.id.CancelButton);

        /**
         * Interfejs nasłuchujący przycisku sprawdzający i pobierający dane od użytkownika oraz uruchamia aktywność
         * @see Multiadds_date
         */
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(NameEvent.getText().toString().equals("")){
                    Toast.makeText(MultiAdds.this, "Incorrectly data, try again", Toast.LENGTH_SHORT).show();}
                else {

                    name = NameEvent.getText().toString();
                    description = Description.getText().toString();
                    hour = picker.getCurrentHour().toString();
                    minute =  picker.getCurrentMinute().toString();
                    Godzin = picker.getCurrentHour();
                    Minuta = picker.getCurrentMinute();

                    Intent intent = new Intent(MultiAdds.this, Multiadds_date.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        /**
         * Interfejs nasłuchujący przycisku odpowiedzialny za anulowanie operacji i zamknięcie okna
         */
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
    }

}