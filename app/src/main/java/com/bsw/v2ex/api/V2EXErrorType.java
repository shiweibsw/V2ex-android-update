package com.bsw.v2ex.api;

import android.content.Context;

import com.bsw.v2ex.Application;
import com.bsw.v2ex.R;
import com.bsw.v2ex.utils.NetWorkHelper;

/**
 * Created by baishiwei on 2016/3/25.
 */
public enum V2EXErrorType {
    ErrorSuccess,
    ErrorApiForbidden,
    ErrorNoOnceAndNext,
    ErrorLoginFailure,
    ErrorCommentFailure,
    ErrorGetTopicListFailure,
    ErrorGetTopicDetailsFailure,
    ErrorGetNotificationFailure,
    ErrorCreateNewFailure,
    ErrorFavNodeFailure,
    ErrorCheckInFailure,
    ErrorFavTopicFailure,
    ErrorGetProfileFailure;

    public static String errorMessage(Context context, V2EXErrorType type) {
        if (context == null) {
            context = Application.getContext();
        }
        boolean isNetAvailable = NetWorkHelper.isNetAvailable(context);
        if (!isNetAvailable)
            return context.getResources().getString(R.string.error_network_disconnect);
        switch (type) {
            case ErrorApiForbidden:
                return context.getResources().getString(R.string.error_network_exception);

            case ErrorNoOnceAndNext:
                return context.getResources().getString(R.string.error_obtain_once);

            case ErrorLoginFailure:
                return context.getResources().getString(R.string.error_login);

            case ErrorCommentFailure:
                return context.getResources().getString(R.string.error_reply);

            case ErrorGetNotificationFailure:
                return context.getResources().getString(R.string.error_get_notification);

            case ErrorCreateNewFailure:
                return context.getResources().getString(R.string.error_create_topic);

            case ErrorFavNodeFailure:
                return context.getResources().getString(R.string.error_fav_nodes);


            case ErrorGetTopicListFailure:
                return context.getResources().getString(R.string.error_get_topic_list);

            case ErrorGetTopicDetailsFailure:
                return context.getResources().getString(R.string.error_get_topic_details);

            case ErrorFavTopicFailure:
                return context.getResources().getString(R.string.error_fav_topic);

            case ErrorGetProfileFailure:
                return context.getResources().getString(R.string.error_get_profile);

            case ErrorCheckInFailure:
                return context.getResources().getString(R.string.error_check_in);

            default:
                return context.getResources().getString(R.string.error_unknown);
        }
    }

}
