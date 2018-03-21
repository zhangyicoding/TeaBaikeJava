package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.estyle.teabaike.R;
import com.estyle.teabaike.databinding.ActivitySuggestionBinding;


public class SuggestionActivity extends BaseActivity implements View.OnFocusChangeListener {

    private ActivitySuggestionBinding binding;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SuggestionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_suggestion);

        initView();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.titleEditText.setOnFocusChangeListener(this);
        binding.contentEditText.setOnFocusChangeListener(this);
    }

    // 提交按钮
    public void commit(View v) {
        boolean isTitleEmpty = checkTitleIsEmpty();
        boolean isContentEmpty = checkContentIsEmpty();
        if (!isTitleEmpty && !isContentEmpty) {
            Toast.makeText(this, R.string.suggestion_suffessful, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 检查标题是否为空
    private boolean checkTitleIsEmpty() {
        if (TextUtils.isEmpty(binding.titleEditText.getText().toString())) {
            binding.titleTextInputLayout.setError(getString(R.string.suggestion_title_empty));
            binding.titleTextInputLayout.setErrorEnabled(true);
            return true;
        } else {
            binding.titleTextInputLayout.setErrorEnabled(false);
            return false;
        }
    }

    //     检查反馈内容是否为空
    private boolean checkContentIsEmpty() {
        if (TextUtils.isEmpty(binding.contentEditText.getText().toString())) {
            binding.contentTextInputLayout.setError(getString(R.string.suggestion_content_empty));
            binding.contentTextInputLayout.setErrorEnabled(true);
            return true;
        } else {
            binding.contentTextInputLayout.setErrorEnabled(false);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            switch (v.getId()) {
                case R.id.title_edit_text:
                    checkTitleIsEmpty();
                    break;
                case R.id.content_edit_text:
                    checkContentIsEmpty();
                    break;
            }
        }
    }
}
