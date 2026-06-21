package com.example.skynet.ui.support;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.tvMessage.setText(message.getText());
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(message.getTimestamp())));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layoutBubble.getLayoutParams();
        if (message.isUser()) {
            params.gravity = Gravity.END;
            holder.layoutBubble.setBackgroundResource(R.drawable.bg_bubble_user);
        } else {
            params.gravity = Gravity.START;
            holder.layoutBubble.setBackgroundResource(R.drawable.bg_bubble_bot);
        }
        holder.layoutBubble.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        View layoutBubble;

        ChatViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageText);
            tvTime = itemView.findViewById(R.id.tvMessageTime);
            layoutBubble = itemView.findViewById(R.id.layoutMessageBubble);
        }
    }
}
