package com.bsw.v2ex.api;

import android.content.Context;

import com.bsw.v2ex.model.BaseModel;
import com.bsw.v2ex.model.PersistenceHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by baishiwei on 2016/3/28.
 */
public class WrappedJsonHttpResponseHandler<T extends BaseModel> extends JsonHttpResponseHandler {
    HttpRequestHandler<ArrayList<T>> handler;
    Class c;
    Context context;
    String key;

    public WrappedJsonHttpResponseHandler(Context context, Class c, String key, HttpRequestHandler<ArrayList<T>> handler) {
        this.handler = handler;
        this.c = c;
        this.context = context;
        this.key = key;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        ArrayList<T> models = new ArrayList<T>();
        T obj = null;
        try {
            obj = (T) Class.forName(c.getName()).newInstance();
            obj.parse(response);
            if (obj != null) {
                models.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersistenceHelper.saveModelList(context, models, key);
        SafeHandler.onSuccess(handler, models);

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        ArrayList<T> models = new ArrayList<T>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                T obj = (T) Class.forName(c.getName()).newInstance();
                obj.parse(jsonObject);
                if (obj != null)
                    models.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PersistenceHelper.saveModelList(context, models, key);
        SafeHandler.onSuccess(handler, models);

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        handleFailure(statusCode, throwable.getMessage());
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        handleFailure(statusCode, throwable.getMessage());
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        handleFailure(statusCode, throwable.getMessage());
    }

    private void handleFailure(int statusCode, String error) {
        error = V2EXErrorType.errorMessage(context, V2EXErrorType.ErrorApiForbidden);
        SafeHandler.onFailure(handler, error);
    }


}
