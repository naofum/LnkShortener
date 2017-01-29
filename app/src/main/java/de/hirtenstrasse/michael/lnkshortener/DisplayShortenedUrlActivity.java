package de.hirtenstrasse.michael.lnkshortener;

// Copyright (C) 2017 Michael Achmann

//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class DisplayShortenedUrlActivity extends AppCompatActivity {
    // Setting up the Strings which will be used in the Intents
    public final static String EXTRA_MESSAGE = "de.hirtenstrasse.michael.lnkshortener.MESSAGE";
    public final static String ERROR_MESSAGE = "de.hirtenstrasse.michael.lnkshortener.ERROR";
    public final static String ERROR_BOOL = "de.hirtenstrasse.michael.lnkshortener.BERR";

    // Setting up Strings which will be used class-wide
    String shortUrl;
    String originalUrl;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Setting up Variables
        String encodedOriginalUrl = null;
        Intent intent = getIntent();

        PreferenceManager.setDefaultValues(this, R.xml.main_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String apiKey = sharedPref.getString("api_key", null);
        String apiUrl = sharedPref.getString("url", null);

        super.onCreate(savedInstanceState);

        // Setting Up Layout
        setContentView(R.layout.activity_display_shortened_url);

        // Setting up Variables for Layout
        final TextView viewShortenedUrl = (TextView) findViewById(R.id.textViewShortenedLink);
        final ImageButton shareButton = (ImageButton) findViewById(R.id.shareButton);
        final ImageButton openLinkButton = (ImageButton) findViewById(R.id.openLinkButton);
        final ImageButton copyLinkButton = (ImageButton) findViewById(R.id.copyLinkButton);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Setting up Toolbar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Instantiate Volley for Networking
        RequestQueue queue = Volley.newRequestQueue(this);


        // Checks where the Data is coming from (MainActivity or Intent from foreign app)
        if( intent.getBooleanExtra(MainActivity.ACTIVITY_MESSAGE,false)){
            // Received the Intent from MainActivity
            originalUrl = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);

        } else {
            // Received the Intent from a foreign App
            Log.d("OrigUrl From Intent", intent.getStringExtra(Intent.EXTRA_TEXT));
            originalUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        // Tries to encode the URL
        try {
            encodedOriginalUrl = URLEncoder.encode(originalUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Assembles the URL and starts the API-Request
        String url = apiUrl+"/api/v2/action/shorten?key="+apiKey+"&url=" + encodedOriginalUrl;
       // String url = " https://1n.pm/api/v2/action/shorten?key=8a4a2c54d582048c31aa85baaeb3f8&url=" + encodedOriginalUrl;
        Log.d("URL:", url);

        // Actual Request to the API
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // If we receive a proper Response we set the response as the shortened URL
                        shortUrl = response;

                        // We set the shortened URL as Label
                        viewShortenedUrl.setText(shortUrl);

                        // Now we hide the loading-spinner and set the Buttons and Texts as vissible
                        progressBar.setVisibility(View.GONE);
                        viewShortenedUrl.setVisibility(View.VISIBLE);
                        shareButton.setVisibility(View.VISIBLE);
                        openLinkButton.setVisibility(View.VISIBLE);
                        copyLinkButton.setVisibility(View.VISIBLE);

                        Log.d("Response:", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // This is called if some error happened
                // Here we set up the first vars
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context,  getString(R.string.error_toast), duration);

                // Logging..
                Log.d("Network Error:", error.toString());

                // Whatever happened we want to return to the start Activity. If possible with
                // some information about what went wrong. Therefore we can already set the Intent up
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(EXTRA_MESSAGE, originalUrl);
                intent.putExtra(ERROR_BOOL, true);

                // In some magical cases there occurs an error but there's no statusCode provided
                // therefore the try-catch is needed. If there is a statusCode it can tell us a little
                // more about the kind of the error which then is passed on in form of a human readable
                // String to the MainActivity.
                try{

                    if(error.networkResponse.statusCode != 200) {

                        switch (error.networkResponse.statusCode){
                            case 400:
                                Log.d("Error", "400");
                                intent.putExtra(ERROR_MESSAGE, getString(R.string.error_valid_url));
                                break;

                            case 401:
                                Log.d("Error", "401");
                                intent.putExtra(ERROR_MESSAGE, getString(R.string.error_quota));
                                break;

                            case 404:
                                Log.d("Error", "404");
                                intent.putExtra(ERROR_MESSAGE, getString(R.string.error_404));
                                break;

                            case 403:
                                Log.d("Error", "403");
                                intent.putExtra(ERROR_MESSAGE, getString(R.string.error_unauthorized));
                                break;

                            case 500:
                                Log.d("Error", "500");
                                intent.putExtra(ERROR_MESSAGE, getString(R.string.error_internal));
                                break;

                            default:
                                Log.d("Error", "Misc.");
                                intent.putExtra(ERROR_MESSAGE, getString(R.string.error_misc));

                        }



                    }

                } catch (Exception e){
                    // If this happened we are hopefully yet debugging and will receive some information
                    // about the kind of the error.
                    Log.d("Error", e.toString());

                }


                // We show the error toast
                toast.show();

                // And start the MainActivity
                startActivity(intent);

                // At the same time we finish() since the data in this Activity shall be purged
                // (The visible / gone / hidden settings need to be reset)
                finish();

            }
        });

        // For volley we need to add our request to the queue. The queue starts automatically
        // Do NOT add a queue.start(), it provokes errors.
        queue.add(stringRequest);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void shareLink(View view) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shortUrl);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getString(R.string.chooser_title)));

    }

    public void copyLink(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Short Link", shortUrl);
        clipboard.setPrimaryClip(clip);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, getString(R.string.copied_to_clipboard), duration);
        toast.show();


    }

    public void openLink(View view){
        Uri webpage = Uri.parse(shortUrl);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);

        // Create intent to show chooser
        Intent chooser = Intent.createChooser(webIntent, getString(R.string.open_chooser_title));

// Verify the intent will resolve to at least one activity
        if (webIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DisplayShortenedUrl Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
        finish();
    }


}
