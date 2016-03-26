package com.bsw.v2ex.api;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class SafeHandler {
    public static <E> void onFailure(HttpRequestHandler<E> handler, String error) {
        try {
            handler.onFailure(error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <E> void onSuccess(HttpRequestHandler<E> handler, E data) {
        try {
            handler.onSuccess(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static <E> void onSuccess(HttpRequestHandler<E> handler, E data, int totalPages, int currentPage) {
        try {
            handler.onSuccess(data, totalPages, currentPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
