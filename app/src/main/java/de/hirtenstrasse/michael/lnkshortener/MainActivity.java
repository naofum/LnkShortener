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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
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
            ImageView imageViewError = (ImageView) findViewById(R.id.imageViewError);

            textViewError.setText(errorMessage);
            textViewError.setVisibility(View.VISIBLE);
            imageViewError.setVisibility(View.VISIBLE);
        }

    }

    public void shortenLink(View view) {



        Intent intent = new Intent(this, DisplayShortenedUrlActivity.class);

        EditText urlInput = (EditText) findViewById(R.id.urlInput);
        TextView textViewError = (TextView) findViewById(R.id.textViewError);
        ImageView imageViewError = (ImageView) findViewById(R.id.imageViewError);

        originalUrl = urlInput.getText().toString();

        if(validateURL(originalUrl)){
            originalUrl = guessUrl(originalUrl);
            intent.putExtra(EXTRA_MESSAGE, originalUrl);
            intent.putExtra(ACTIVITY_MESSAGE, true);

            urlInput.setText("");
            textViewError.setText("");
            textViewError.setVisibility(View.INVISIBLE);
            imageViewError.setVisibility(View.INVISIBLE);

            startActivity(intent);

        } else {


            textViewError.setText(getString(R.string.error_valid_url));
            textViewError.setVisibility(View.VISIBLE);
            imageViewError.setVisibility(View.VISIBLE);

        }


    }

    public boolean validateURL(String url){
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public String guessUrl(String url){
        String returnurl = URLUtil.guessUrl(url);
        Log.d("GuessedUrl:", returnurl);
        return returnurl;
    }




}
