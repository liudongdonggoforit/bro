package com.sh.browser.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sh.browser.R;
import com.sh.browser.interfaces.ItransactionFragment;
import com.sh.browser.views.NinjaToast;
import com.sh.browser.views.flowlayout.FlowLayout;
import com.sh.browser.views.flowlayout.TagAdapter;
import com.sh.browser.views.flowlayout.TagFlowLayout;

/**
 * 搜素页面
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener {

    private EditText search;
    private TextView cancel;
    private String[] values = new String[]{"周冬雨想养个鸭子", "池子圈吴亦凡粉丝", "张一山耍大牌", "韩红斥山寨林俊杰"};

    private TagFlowLayout mFlowLayout;
    private ItransactionFragment itransactionFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.activity_search, null);
    }

    @Override
    protected void initViews() {
        super.initViews();
        search = mView.findViewById(R.id.search);
        cancel = mView.findViewById(R.id.cancel);
        mFlowLayout = mView.findViewById(R.id.flowlayout);
        search.setOnClickListener(this);
        cancel.setOnClickListener(this);
        showSoftInput(search);

        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Bundle bundle = new Bundle();
                bundle.putString("query",values[position]);
                itransactionFragment.transcationFragment(5,bundle);
                hideSoftInput(search);
//                Intent intent = new Intent(SearchFragment.this, MainActivity_F.class);
//                intent.putExtra("query",values[position]);
//                setResult(200,intent);
//                hideSoftInput(search);
//                finish();
                return false;
            }
        });

        mFlowLayout.setAdapter(new TagAdapter<String>(values) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_tv, mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String query = search.getText().toString().trim();
                if (query.isEmpty()) {
                    NinjaToast.show(mContext, getString(R.string.toast_input_empty));
                    return true;
                }

//                Intent intent = new Intent(mContext, MainActivity_F.class);
//                intent.putExtra("query",query);
//                setResult(200,intent);
//                hideSoftInput(search);
//                finish();
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
                itransactionFragment.transcationFragment(4,null);
                hideSoftInput(search);
                break;
        }
    }

    private void showSoftInput(final EditText view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }, 250);
    }

    private void hideSoftInput(final EditText view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItransactionFragment) {
            itransactionFragment = (ItransactionFragment) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itransactionFragment = null;
    }
}