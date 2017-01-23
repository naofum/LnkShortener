package de.hirtenstrasse.michael.lnkshortener;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "de.hirtenstrasse.michael.lnkshortener.MESSAGE";
    public final static String ACTIVITY_MESSAGE = "de.hirtenstrasse.michael.lnkshortener.ACTIVITY";
    String originalUrl;
    String errorMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        originalUrl = intent.getStringExtra(DisplayShortenedUrlActivity.EXTRA_MESSAGE);
        errorMessage = intent.getStringExtra(DisplayShortenedUrlActivity.ERROR_MESSAGE);

        if (intent.getBooleanExtra(DisplayShortenedUrlActivity.ERROR_BOOL, false)) {
            EditText urlInput = (EditText) findViewById(R.id.urlInput);
            urlInput.setText(originalUrl);

            TextView textViewError = (TextView) findViewById(R.id.textViewError);
            textViewError.setText(errorMessage);
            textViewError.setVisibility(View.VISIBLE);
        }

    }

    public void shortenLink(View view) {

        Intent intent = new Intent(this, DisplayShortenedUrlActivity.class);
        EditText urlInput = (EditText) findViewById(R.id.urlInput);

        originalUrl = urlInput.getText().toString();

        if(!originalUrl.isEmpty() && originalUrl != null && !originalUrl.equals("null")){
            intent.putExtra(EXTRA_MESSAGE, originalUrl);
            intent.putExtra(ACTIVITY_MESSAGE, true);
            startActivity(intent);
        } else {
            TextView textViewError = (TextView) findViewById(R.id.textViewError);
            textViewError.setText("Please enter an URL");
            textViewError.setVisibility(View.VISIBLE);
        }


    }


}
