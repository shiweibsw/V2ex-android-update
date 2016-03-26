package com.bsw.v2ex.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsw.v2ex.Application;
import com.bsw.v2ex.R;
import com.bsw.v2ex.database.V2EXDataSource;
import com.bsw.v2ex.model.MemberModel;
import com.bsw.v2ex.model.NodeModel;
import com.bsw.v2ex.model.TopicModel;
import com.bsw.v2ex.model.V2EXDateModel;
import com.bsw.v2ex.utils.MessageUtils;
import com.bsw.v2ex.utils.OnScrollToBottomListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;

/**
 * Created by baishiwei on 2016/3/26.
 */
public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {
    private Context mContext;
    private OnScrollToBottomListener mListener;
    private ArrayList<TopicModel> mTopics = new ArrayList<TopicModel>();
    private V2EXDataSource mDataSource = Application.getDataSource();

    public TopicsAdapter(Context context, OnScrollToBottomListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TopicModel topic = mTopics.get(position);
        final MemberModel member = topic.member;
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageUtils.showToast(mContext, "card clicked! do somthing please!");
            }
        });
        if (member != null) {
            ImageLoader.getInstance().displayImage(member.avatar, holder.avatar);
            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageUtils.showToast(mContext, "头像被点击了！");
                }
            });
            holder.name.setText(member.username);
        }
        holder.title.setText(topic.title);

        boolean read = Application.getDataSource().isTopicRead(topic.id);
        holder.title.setTextColor(read ? mContext.getResources().getColor(R.color.list_item_read) :
                mContext.getResources().getColor(R.color.list_item_unread));
        holder.time.setText(V2EXDateModel.toString(topic.created));
        final NodeModel node = topic.node;
        holder.nodeTitle.setText(node.title);
        holder.nodeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageUtils.showToast(mContext, "节点标题被点击了！");
            }
        });

        if (topic.replies > 0) {
            int count_color = read ? mContext.getResources().getColor(R.color.topic_count_read) :
                    mContext.getResources().getColor(R.color.topic_count_unread);
            holder.replies.setVisibility(View.VISIBLE);
            holder.replies.setText(String.valueOf(topic.replies));
            holder.replies.setBadgeBackgroundColor(count_color);
        } else {
            holder.replies.setVisibility(View.INVISIBLE);
        }
        if (mTopics.size() - position <= 1 && mListener != null) {
            mListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mTopics.size();
    }

    public void insertAtBack(ArrayList<TopicModel> data, boolean merge) {
        if (merge) {
            mTopics.addAll(data);
        } else {
            mTopics = data;
        }
        notifyDataSetChanged();
    }

    public void update(ArrayList<TopicModel> data, boolean merge) {
        if (merge && mTopics.size() > 0) {
            for (int i = 0; i < mTopics.size(); i++) {
                TopicModel obj = mTopics.get(i);
                boolean exist = false;
                for (int j = 0; j < data.size(); j++) {
                    if (data.get(j).id == obj.id) {
                        exist = true;
                        break;
                    }
                }
                if (exist) continue;
                data.add(obj);
            }
        }
        mTopics = data;

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public ImageView avatar;
        public TextView title;
        public TextView nodeTitle;
        public TextView name;
        public TextView time;
        public BadgeView replies;

        public ViewHolder(View view) {
            super(view);
            card = (CardView) view.findViewById(R.id.card_container);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            title = (TextView) view.findViewById(R.id.title);
            nodeTitle = (TextView) view.findViewById(R.id.node_title);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            replies = (BadgeView) view.findViewById(R.id.txt_replies);

        }
    }
}
