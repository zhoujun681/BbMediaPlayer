package com.example.bbmediaplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.adpater.SearchAdpter;
import com.example.bbmediaplayer.domain.SearchBean;
import com.example.bbmediaplayer.utils.Constants;
import com.example.bbmediaplayer.utils.JsonParser;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.vov.vitamio.utils.Log;

public class SearchActivity extends Activity {
    private EditText etInput;
    private ImageView ivVoice;
    private TextView tvSearch;
    private ListView listview;
    private ProgressBar progressBar;
    private TextView tvNodata;
    private String url;

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private SharedPreferences mSharedPreferences;
    private EditText mResultText;
    private EditText showContacts;
    private Toast mToast;
    private SearchAdpter adapter;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;


    InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
//            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.e("Bb", "初始化失败，错误码：" + code);
//                ToastUtils.showToast(SpeechRecognizerActivity.this,"初始化失败，错误码：" + code);
            } else {
//                Log.d(TAG,"初始化成功");
//                ToastUtils.showToast(SpeechRecognizerActivity.this,"初始化成功");
            }
        }
    };
    private List<SearchBean.ResultBean.ListBean> items;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();

    }

    private void startWithBack() {
        mIat = SpeechRecognizer.createRecognizer(SearchActivity.this, mInitListener);
        initParam();
        mIat.startListening(new RecognizerListener() {
            @Override
            public void onVolumeChanged(int i, byte[] bytes) {

            }

            @Override
            public void onBeginOfSpeech() {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
                Log.e("BB", recognizerResult.getResultString());
            }

            @Override
            public void onError(SpeechError speechError) {

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }


    private void startWithDialog() {
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源


        this.mIatDialog = new RecognizerDialog(this, mInitListener);

//以下为dialog设置听写参数
        this.mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        this.mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin");

//开始识别并设置监听器
        this.mIatDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
                Log.e("BB", recognizerResult.getResultString());
            }

            @Override
            public void onError(SpeechError speechError) {
                Log.e("BB", speechError.toString());
            }
        });
//显示听写对话框
        this.mIatDialog.show();
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void initParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        /*mSpeechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mSpeechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory()+"/msc/iat.wav");*/
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-08-22 16:50:33 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        etInput = findViewById(R.id.et_input);
        ivVoice = findViewById(R.id.iv_voice);
        tvSearch = findViewById(R.id.tv_search);
        listview = findViewById(R.id.listview);
        progressBar = findViewById(R.id.progressBar);
        tvNodata = findViewById(R.id.tv_nodata);
//        mResultText = ((EditText) findViewById(R.id.iat_text));
//        showContacts = (EditText) findViewById(R.id.iat_contacts);

        //设置点击事件
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        ivVoice.setOnClickListener(myOnClickListener);
        tvSearch.setOnClickListener(myOnClickListener);
    }

    private class MyOnClickListener implements View.OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_voice: //语音输入
                    startWithDialog();
                    break;
                case R.id.tv_search: //点击搜索
//                    Toast.makeText(SearchActivity.this, "点击了搜索", Toast.LENGTH_SHORT).show();
                    searchText();
                    break;
            }
        }
    }

    private void searchText() {
        String text = etInput.getText().toString().trim();
        String result = "";
        String pattern5 = "([\\d\\w])+";
        Pattern r = Pattern.compile(pattern5);
        Matcher m = r.matcher(text);
        while (m.find( )) {
            result += m.group(0);
//            System.out.println("Found value: " + m.group(0) );
        }
        text = result;
        Toast.makeText(this, "您搜索的内容是:"+result, Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(text)) {
            if (items != null && items.size() > 0) {
                items.clear();
            }

            try {
                text = URLEncoder.encode(text, "UTF-8"); //不是必须
                url = Constants.SEARCH_URL_START + text + Constants.SEARCH_URL_END;
                getDataFromNet();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
            getDataFromNet();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        etInput.setText(resultBuffer.toString());
        etInput.setSelection(etInput.length());
    }

    private void getDataFromNet() {
        progressBar.setVisibility(View.VISIBLE);
        RequestParams parms = new RequestParams(url);
        x.http().get(parms, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFinished() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 解析请求结果
     *
     * @param result
     */
    private void processData(String result) {
        SearchBean searchBean = parsedJson(result);
        SearchBean.ResultBean result1 = searchBean.getResult();
        items = result1.getList();
        showData(items);
    }

    private void showData(List<SearchBean.ResultBean.ListBean> items) {
        if (items != null && items.size() > 0) {
            adapter = new SearchAdpter(this, items);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(items.get(position).getUrlX()));
                    startActivity(intent);
                }
            });
            tvNodata.setVisibility(View.GONE);
        } else {
            tvNodata.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }

        progressBar.setVisibility(View.GONE);
    }

    /**
     * 解析json到bean对象
     *
     * @param result
     * @return
     */
    private SearchBean parsedJson(String result) {
        Gson gson = new Gson();
        return gson.fromJson(result, SearchBean.class);
    }
}
