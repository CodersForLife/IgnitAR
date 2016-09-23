package com.vizy.ignitar.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vizy.ignitar.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompanyPageActivityFragment extends Fragment {

    public CompanyPageActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_company_page, container, false);
    }
}
