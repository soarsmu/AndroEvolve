package com.android.parii.travcom;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

public class chatBot extends AppCompatActivity {


    // private int x=0;
    private TextView conversation;
    private EditText userInput;
    public static int x = 0;
    public String pop;
    private ConversationService myConversationService = null;


    //added
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //


    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        //TTS
        TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Log.d("TTS", "Text to speech engine started successfully.");
                    tts.setLanguage(Locale.US);
                } else {
                    Log.d("TTS", "Error starting the text to speech engine.");
                }
            }
        };
        tts = new TextToSpeech(this.getApplicationContext(), listener);


        //Ends

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        // hide the action bar
        // getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });


        conversation = (TextView) findViewById(R.id.conversation);
        userInput = (EditText) findViewById(R.id.user_input);


        myConversationService =
                new ConversationService(
                        "2017-05-26",
                        getString(R.string.username),
                        getString(R.string.password)
                );


        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView tv,
                                          int action, KeyEvent keyEvent) {
                if (action == EditorInfo.IME_ACTION_DONE) {

                    final String inputText = userInput.getText().toString();
                    conversation.append(
                            Html.fromHtml("<p><b>You:</b> " + inputText + "</p>")
                    );

                    userInput.setText("");

                    MessageRequest request = new MessageRequest.Builder()
                            .inputText(inputText)
                            .build();

                    myConversationService.
                            message(getString(R.string.workspace), request)
                            .enqueue(new ServiceCallback<MessageResponse>() {
                                @Override
                                public void onResponse(MessageResponse response) {

                                    final String outputText = response.getText().get(0);
                                    tts.speak(outputText, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            conversation.append(
                                                    Html.fromHtml("<p><b> Zara :</b> " +
                                                            outputText + "</p>")
                                            );
                                        }
                                    });


                                    if (response.getIntents().get(0).getIntent().endsWith("RequestQuote")) {

                                        String quotesURL =
                                                "https://api.forismatic.com/api/1.0/" +
                                                        "?method=getQuote&format=text&lang=en";

                                        Fuel.get(quotesURL)
                                                .responseString(new Handler<String>() {
                                                    @Override
                                                    public void success(Request request,
                                                                        Response response, String quote) {

                                                        Log.d("Success", "To ho gaya, ab kya");

                                                        tts.speak(quote, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                                                        conversation.append(
                                                                Html.fromHtml("<p><b>Zara :</b> " +
                                                                        quote + "</p>")
                                                        );
                                                    }

                                                    @Override
                                                    public void failure(Request request,
                                                                        Response response,
                                                                        FuelError fuelError) {
                                                    }
                                                });

                                    } else if (response.getIntents().get(0).getIntent().endsWith("PNR")) {
                                        if (x % 2 == 0) {
                                            String m = "Please Enter 10 DIGIT PNR";
                                            tts.speak(m, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                                            conversation.append(
                                                    Html.fromHtml("<p><b>Bot:</b> " +
                                                            m + "</p>")
                                            );
                                            x++;

                                        } else {
                                            x++;
                                            /*String m = "CNF/B2/44/DF";
                                            tts.speak(m, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                                            conversation.append(
                                                    Html.fromHtml("<p><b>Bot:</b> " +
                                                            m + "</p>")
                                            );*/

                                            Log.d("me", "" + inputText);
                                            String l = inputText;
                                            String requestURL = "https://api.railwayapi.com/v2/pnr-status/pnr/" + l + "/apikey/bmbsdt3g07/";
                                            Log.d("me :", "" + requestURL);
                                            URL url = createURL(requestURL);

                                            String jsonResponse = "";
                                            try {
                                                Log.d("me :", "" + requestURL);
                                                jsonResponse = makeHttprequest(url);
                                                Log.d("me :", "" + jsonResponse);
                                                Toast.makeText(chatBot.this, "" + jsonResponse, Toast.LENGTH_LONG).show();
                                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                                Log.d("jsonobject", "check parse 1" + jsonObject);
                                                JSONArray jsonArray = jsonObject.optJSONArray("passengers");
                                                Log.d("jsonArray", "check parse 2" + jsonArray);
                                                JSONObject jsonObject1 = jsonArray.optJSONObject(0);
                                                Log.d("jsonobject 1 ", "check parse 3" + jsonObject1);
                                                pop = jsonObject1.optString("current_status");
                                                Log.d("String", "check parse 4" + pop);
                                                tts.speak(pop, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                                                conversation.append(
                                                        Html.fromHtml("<p><b>Bot:</b> " +
                                                                pop + "</p>")
                                                );
                                            } catch (IOException e) {

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    }


                                }


                                @Override
                                public void onFailure(Exception e) {

                                    Log.d("Fail", "Ho gaya hai Parii.....debug :( ");
                                }


                            });

                }

                return false;
            }


        });


    }

    private String makeHttprequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
            Log.d("makeHTTPrequest :", "" + jsonResponse);

        } catch (IOException e) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }


        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }

        }
        return output.toString();
    }

    private URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return url;

    }


    //check


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //conversation.setText(result.get(0));

                    conversation.append(
                            Html.fromHtml("<p><b>You:</b> " +
                                    result.get(0) + "</p>"));


                    //dup start

                    String okay = result.get(0);

                    MessageRequest request = new MessageRequest.Builder()
                            .inputText(okay)
                            .build();

                    myConversationService.
                            message(getString(R.string.workspace), request)
                            .enqueue(new ServiceCallback<MessageResponse>() {
                                @Override
                                public void onResponse(MessageResponse response) {

                                    final String outputText = response.getText().get(0);
                                    tts.speak(outputText, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            conversation.append(
                                                    Html.fromHtml("<p><b>Zara:</b> " +
                                                            outputText + "</p>")
                                            );
                                        }
                                    });


                                    if (response.getIntents().get(0).getIntent().endsWith("RequestQuote")) {

                                        String quotesURL =
                                                "https://api.forismatic.com/api/1.0/" +
                                                        "?method=getQuote&format=text&lang=en";

                                        Fuel.get(quotesURL)
                                                .responseString(new Handler<String>() {
                                                    @Override
                                                    public void success(Request request,
                                                                        Response response, String quote) {

                                                        Log.d("Success", "To ho gaya, ab kya");

                                                        tts.speak(quote, TextToSpeech.QUEUE_ADD, null, "DEFAULT");

                                                        conversation.append(
                                                                Html.fromHtml("<p><b>Zara:</b> " +
                                                                        quote + "</p>")
                                                        );
                                                    }

                                                    @Override
                                                    public void failure(Request request,
                                                                        Response response,
                                                                        FuelError fuelError) {
                                                    }
                                                });

                                    }

                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.d("Fail", "Ho gaya hai Parii.....debug :( ");
                                }


                            });

                    //dup end


                }
                break;
            }

        }
    }
}


