package com.example.testedittext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText et;
    private static final String TAG = "jp--Tag";
    private boolean delete = false;
    private static int sLastLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.et_input);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        et.addTextChangedListener(new TextWatcher() {

            private int oldLength = 0;
            private boolean isChange = true;
            private int curLength = 0;
            private int emptyNumB = 0;  //初始空格数
            private int emptyNumA = 0;  //遍历添加空格后的空格数

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldLength = s.length();
                Log.i(TAG, "未改变长度: " + oldLength);
                emptyNumB = 0;
                for (int i = 0; i < s.toString().length(); i++) {
                    if (s.charAt(i) == ' ') emptyNumB++;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                curLength = s.length();
                Log.i(TAG, "当前长度: " + curLength);
                //优化处理,如果长度未改变或则改变后长度小于3就不需要添加空格
                if (curLength == oldLength || curLength <= 3) {
                    isChange = false;
                } else {
                    isChange = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChange) {
                    if (curLength - sLastLength < 0) {  //判断是editext中的字符串是在减少 还是在增加
                        delete = true;
                    } else {
                        delete = false;
                    }
                    sLastLength = curLength;
                    int selectIndex = et.getSelectionEnd();//获取光标位置
                    String content = s.toString().replaceAll(" ", "");
                    Log.i(TAG, "content:" + content);
                    StringBuffer sb = new StringBuffer(content);
                    //遍历加空格
                    int index = 1;
                    emptyNumA = 0;
                    for (int i = 0; i < content.length(); i++) {
                        if (i == 2) {

                            sb.insert(i + index, " ");
                            index++;
                            emptyNumA++;
                        } else if (i == 6) {
                            sb.insert(i + index, " ");
                            index++;
                            emptyNumA++;
                        }
                    }
                    /**
                     * 如果将上面的for循环改成以下注释掉的for循环部分代码 就是输入银行卡自动添加空格了
                     * 和支付宝上的添加银行的光标处理、删除与添加空格的策略一致
                     */
//                    for (int i = 0; i < content.length(); i++) {
//                        if ((i + 1) % 4 == 0) {
//                            sb.insert(i + index, " ");
//                            index++;
//                            emptyNumA++;
//                        }
//                    }


                    Log.i(TAG, "result content:" + sb.toString());
                    String result = sb.toString();
                    //遍历加空格后 如果发现最后一位是空格 就把这个空格去掉
                    if (result.endsWith(" ")) {
                        result = result.substring(0, result.length() - 1);
                        emptyNumA--;
                    }
                    /**
                     * 用遍历添加空格后的字符串 来替换editext变化后的字符串
                     */
                    s.replace(0, s.length(), result);

                    //处理光标位置
                    if (emptyNumA > emptyNumB) {
                        selectIndex = selectIndex + (emptyNumA - emptyNumB);
                    }
                    if (selectIndex > result.length()) {
                        selectIndex = result.length();
                    } else if (selectIndex < 0) {
                        selectIndex = 0;
                    }
                    // 例如"123 45"且光标在4后面 这时需要删除4 光标的处理
                    if (selectIndex > 1 && s.charAt(selectIndex - 1) == ' ') {
                        if (delete) {
                            selectIndex--;
                        } else {
                            selectIndex++;
                        }
                    }

                    et.setSelection(selectIndex);
                    isChange = false;
                }
            }
        });
    }
}
