package com.sh.browser.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sh.browser.R;
import com.sh.browser.views.NinjaToast;
import com.sh.browser.views.flowlayout.FlowLayout;
import com.sh.browser.views.flowlayout.TagAdapter;
import com.sh.browser.views.flowlayout.TagFlowLayout;

/**
 * 搜素页面
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private EditText search;
    private TextView cancel;
    private String[] values = new String[]{"周冬雨想养个鸭子", "池子圈吴亦凡粉丝", "张一山耍大牌", "韩红斥山寨林俊杰"};

    private TagFlowLayout mFlowLayout;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews() {
        super.initViews();
        search = findViewById(R.id.search);
        cancel = findViewById(R.id.cancel);
        mFlowLayout = findViewById(R.id.flowlayout);
        search.setOnClickListener(this);
        cancel.setOnClickListener(this);
        showSoftInput(search);

        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Intent intent = new Intent(SearchActivity.this, MainActivity_F.class);
                intent.putExtra("query",values[position]);
                setResult(200,intent);
                hideSoftInput(search);
                finish();
                return false;
            }
        });

        mFlowLayout.setAdapter(new TagAdapter<String>(values) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_tv, mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String query = search.getText().toString().trim();
                if (query.isEmpty()) {
                    NinjaToast.show(SearchActivity.this, getString(R.string.toast_input_empty));
                    return true;
                }

                Intent intent = new Intent(SearchActivity.this, MainActivity_F.class);
                intent.putExtra("query",query);
                setResult(200,intent);
                hideSoftInput(search);
                finish();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                break;
            case R.id.cancel:
                hideSoftInput(search);
                finish();
                break;
        }
    }

    private void showSoftInput(final EditText view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }, 250);
    }

    private void hideSoftInput(final EditText view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
