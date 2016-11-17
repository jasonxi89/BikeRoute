package com.cs4521.bikeroute;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

/**
 * Created by EVA Unit 02 on 11/13/2016.
 */
public class BikerActivity  extends SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return new BikerFragment();
    }



}
