package com.estyle.teabaike.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.estyle.teabaike.R;
import com.estyle.teabaike.activity.CollectionActivity;
import com.estyle.teabaike.activity.CopyrightActivity;
import com.estyle.teabaike.activity.SearchActivity;
import com.estyle.teabaike.activity.SuggestionActivity;
import com.estyle.teabaike.databinding.FragmentDrawerBinding;

public class DrawerFragment extends Fragment implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private EditText mKeywordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDrawerBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_drawer,
                container, false);
        NavigationView rootView = (NavigationView) binding.getRoot();
        rootView.setNavigationItemSelectedListener(this);
        View headerView = rootView.getHeaderView(0);
        mKeywordEditText = (EditText) headerView.findViewById(R.id.keyword_et);
        Button searchBtn = (Button) headerView.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_collection_item:
                CollectionActivity.startActivity(getContext());
                break;
            case R.id.copyright_info_item:
                CopyrightActivity.startActivity(getContext());
                break;
            case R.id.suggestion_item:
                SuggestionActivity.startActivity(getContext());
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        String keyword = mKeywordEditText.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            SearchActivity.startActivity(getContext(), keyword);
        }
    }
}
