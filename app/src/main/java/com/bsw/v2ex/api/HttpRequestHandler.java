package com.bsw.v2ex.api;

/**
 * Created by baishiwei on 2016/3/25.
 */
public interface HttpRequestHandler<E> {
    public void onSuccess(E data);

    public void onSuccess(E data, int totalPages, int currentPage);

    public void onFailure(String error);
}
