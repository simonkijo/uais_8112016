package com.uais.uais.academicMaterials.common.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.uais.uais.academicMaterials.common.data.AbstractExpandableDataProvider;
import com.uais.uais.academicMaterials.common.data.ExampleExpandableDataProvider;


public class AssignmentDataProviderFragment extends Fragment {

    private ExampleExpandableDataProvider mDataProvider;

    private static String gt_[];
    private static String ct_[][];

    public static AssignmentDataProviderFragment dataPut(String gt[], String ct[][]){
        gt_ = gt;
        ct_ = ct;
        return new AssignmentDataProviderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new ExampleExpandableDataProvider(gt_, ct_);
    }

    public AbstractExpandableDataProvider getDataProvider() {
        return mDataProvider;
    }
}
