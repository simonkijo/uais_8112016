
package com.uais.uais.academicMaterials.common.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.uais.uais.academicMaterials.AdFragment;
import com.uais.uais.academicMaterials.common.data.AbstractExpandableDataProvider;
import com.uais.uais.academicMaterials.common.data.ExampleExpandableDataProvider;

public class ExampleExpandableDataProviderFragment extends Fragment {
    private ExampleExpandableDataProvider mDataProvider;

    public static String gt_[];
    public static String ct_[][];

    public static ExampleExpandableDataProviderFragment dataPut(String gt[], String ct[][]){
        gt_ = gt;
        ct_ = ct;
        return new ExampleExpandableDataProviderFragment();
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
