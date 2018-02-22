package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by iteach12 on 2018. 2. 22..
 */

public class SynsAsyncTask extends AsyncTask<Void, Void, String>{



    private String url;
    private ContentValues values;

    public SynsAsyncTask(String url, ContentValues values) {

        this.url = url;
        this.values = values;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result; // 요청 결과를 저장할 변수.
        RequestHttpUrlConnection requestHttpURLConnection = new RequestHttpUrlConnection();
        result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

        Log.i("Result of PHP", s);
    }
}
