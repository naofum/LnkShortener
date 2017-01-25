package de.hirtenstrasse.michael.lnkshortener;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    public final static String EXTRA_MESSAGE = "de.hirtenstrasse.michael.lnkshortener.MESSAGE";
    public final static String ERROR_MESSAGE = "de.hirtenstrasse.michael.lnkshortener.ERROR";
    public final static String ERROR_BOOL = "de.hirtenstrasse.michael.lnkshortener.BERR";


    String shortUrl;
    String originalUrl;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String encodedOriginalUrl = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_shortened_url);

        RequestQueue queue = Volley.newRequestQueue(this);


        Intent intent = getIntent();

        if( intent.getBooleanExtra(MainActivity.ACTIVITY_MESSAGE,false)){
            originalUrl = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        } else {
            Log.d("OrigUrl From Intent", intent.getStringExtra(Intent.EXTRA_TEXT));
            originalUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        final TextView viewShortenedUrl = (TextView) findViewById(R.id.textViewShortenedLink);
        final Button shareButton = (Button) findViewById(R.id.shareButton);
        final Button openLinkButton = (Button) findViewById(R.id.openLinkButton);
        final Button copyLinkButton = (Button) findViewById(R.id.copyLinkButton);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        try {
            encodedOriginalUrl = URLEncoder.encode(originalUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = " https://1n.pm/api/v2/action/shorten?key=8a4a2c54d582048c31aa85baaeb3f8&url=" + encodedOriginalUrl;
        Log.d("URL:", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        viewShortenedUrl.setText(response);
                        shortUrl = response;

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
                Context context = getApplicationContext();

                int duration = Toast.LENGTH_SHORT;

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(EXTRA_MESSAGE, originalUrl);
                intent.putExtra(ERROR_BOOL, true);

                try{

                    if(error.networkResponse.statusCode != 200) {


                        if (error.networkResponse.statusCode == 400) {
                            Log.d("Error", "400");
                            intent.putExtra(ERROR_MESSAGE, getResources().getText(R.string.error_valid_url));
                        }
                        if (error.networkResponse.statusCode == 401) {
                            Log.d("Error", "401");
                            intent.putExtra(ERROR_MESSAGE, getResources().getText(R.string.error_unauthorized));

                        }
                        if (error.networkResponse.statusCode == 404) {
                            Log.d("Error", "404");
                            intent.putExtra(ERROR_MESSAGE, getResources().getText(R.string.error_404));
                        }
                        if (error.networkResponse.statusCode == 403) {
                            Log.d("Error", "403");
                            intent.putExtra(ERROR_MESSAGE, getResources().getText(R.string.error_quota));

                        }
                        if (error.networkResponse.statusCode == 500) {
                            Log.d("Error", "500");
                            intent.putExtra(ERROR_MESSAGE, getResources().getText(R.string.error_internal));

                        }


                    }

                } catch (Exception e){
                    Log.d("Error", e.toString());

                }

                Toast toast = Toast.makeText(context,  R.string.error_toast, duration);
                toast.show();

                startActivity(intent);
                finish();

            }
        });

        queue.add(stringRequest);
        queue.start();


        //     viewShortenedUrl.setText(originalUrl);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void shareLink(View view) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shortUrl);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.chooser_title)));

    }

    public void copyLink(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Short Link", shortUrl);
        clipboard.setPrimaryClip(clip);

        Context context = getApplicationContext();
        CharSequence text = "Link copied to Clipboard.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }

    public void openLink(View view){
        Uri webpage = Uri.parse(shortUrl);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);

        // Create intent to show chooser
        Intent chooser = Intent.createChooser(webIntent, getResources().getString(R.string.open_chooser_title));

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
