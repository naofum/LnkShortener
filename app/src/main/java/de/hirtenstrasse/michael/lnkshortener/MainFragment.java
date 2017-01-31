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

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // MainFragment is the Start screen

        // Since might want to alter some values in the layout we need the Inflater as a variable
        View myInflater =  inflater.inflate(R.layout.fragment_main, container, false);

        // Adding the Github Buttons
        WebView webview = (WebView) myInflater.findViewById(R.id.webViewGithub);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setBackgroundColor(0x00000000);


        webview.loadUrl("file:///android_asset/github.html");


        // If errors have been passed to the Activity they can be injected into the Fragment at this
        // position. That's why we first check for errors and thereafter alter the Labels in the layout
            if(getArguments().getBoolean("error")){

                EditText urlInput = (EditText) myInflater.findViewById(R.id.urlInput);
                urlInput.setText(getArguments().getString("originalUrl"));

                TextView textViewError = (TextView) myInflater.findViewById(R.id.textViewError);
                ImageView imageViewError = (ImageView) myInflater.findViewById(R.id.imageViewError);

                textViewError.setText(getArguments().getString("errorMessage"));
                textViewError.setVisibility(View.VISIBLE);
                imageViewError.setVisibility(View.VISIBLE);
        }

        // Inflate the layout for this fragment
        return myInflater;
    }


}