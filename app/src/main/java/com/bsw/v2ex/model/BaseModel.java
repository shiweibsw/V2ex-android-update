package com.bsw.v2ex.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by baishiwei on 2016/3/25.
 */
public abstract class BaseModel implements Serializable {
    private static final long serialVersionUID = 2015050101L;

    public abstract void parse(JSONObject jsonObject) throws JSONException;
}
