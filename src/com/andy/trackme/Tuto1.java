package com.andy.trackme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Tuto1 extends Fragment{
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View android = inflater.inflate(R.layout.tuto1, container, false);
        ((TextView)android.findViewById(R.id.textView)).setText("Tuto 1");
        return android;
}
}
