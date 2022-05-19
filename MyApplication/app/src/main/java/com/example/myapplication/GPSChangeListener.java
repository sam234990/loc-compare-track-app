package com.example.myapplication;

import java.util.ArrayList;

public interface GPSChangeListener {
    void OnGPSChanged(String result);

    void OnFileChanged(ArrayList<Integer> checklist);
}
