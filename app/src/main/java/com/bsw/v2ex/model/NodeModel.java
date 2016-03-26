package com.bsw.v2ex.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by baishiwei on 2016/3/26.
 */
public class NodeModel extends BaseModel implements Parcelable {
    private static final long serialVersionUID = 2015050103L;

    public int id;
    public String name;
    public String title;
    public String titleAlternative;
    public String url;
    public int topics;

    public String header;
    public String footer;

    public boolean isCollected = false;


    public NodeModel() {
    }

    private NodeModel(Parcel in) {
        int[] ints = new int[2];
        in.readIntArray(ints);
        id = ints[0];
        topics = ints[1];

        String[] strings = new String[6];
        in.readStringArray(strings);
        name = strings[0];
        title = strings[1];
        url = strings[2];
        if ((titleAlternative = strings[3]).equals("")) {
            titleAlternative = null;
        }
        if ((header = strings[4]).equals("")) {
            header = null;
        }
        if ((footer = strings[5]).equals("")) {
            footer = null;
        }
        boolean[] booleen = new boolean[1];
        in.readBooleanArray(booleen);
        isCollected = booleen[0];


    }

    public static final Creator<NodeModel> CREATOR = new Creator<NodeModel>() {
        @Override
        public NodeModel createFromParcel(Parcel in) {
            return new NodeModel(in);
        }

        @Override
        public NodeModel[] newArray(int size) {
            return new NodeModel[size];
        }
    };

    @Override
    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        name = jsonObject.getString("name");
        title = jsonObject.getString("title");
        url = jsonObject.getString("url");
        topics = jsonObject.getInt("topics");
        if (!jsonObject.optString("title_alternative").equals("null")) {
            titleAlternative = jsonObject.optString("title_alternative");
        }
        if (!jsonObject.optString("header").equals("null")) {
            header = jsonObject.optString("header");
        }
        if (!jsonObject.optString("footer").equals("null")) {
            footer = jsonObject.optString("footer");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(new int[]{
                id, topics
        });
        String[] strings = new String[6];
        strings[0] = name;
        strings[1] = title;
        strings[2] = url;
        if ((strings[3] = titleAlternative) == null) {
            strings[3] = "";
        }
        if ((strings[4] = header) == null) {
            strings[4] = "";
        }
        if ((strings[5] = footer) == null) {
            strings[5] = "";
        }
        dest.readStringArray(strings);
        dest.writeBooleanArray(new boolean[]{isCollected});
    }

    @Override
    public String toString() {
        return "NodeModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", titleAlternative='" + titleAlternative + '\'' +
                ", url='" + url + '\'' +
                ", topics=" + topics +
                ", header='" + header + '\'' +
                ", footer='" + footer + '\'' +
                ", isCollected=" + isCollected +
                '}';
    }

}
