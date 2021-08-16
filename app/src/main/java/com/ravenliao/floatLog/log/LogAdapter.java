package com.ravenliao.floatLog.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ravenliao.floatLog.R;
import com.ravenliao.floatLog.log.FLog.FLogPrinter;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> implements FLogPrinter {
    private static final int LOG_MAX_COUNT = 100;
    private static int COLOR_LOG_RED;
    private static int COLOR_LOG_WHITE;

    private final LinkedList<FLog.LogMsg> logs;

    public LogAdapter(Context context) {
        this(context, new LinkedList<>());
    }

    public LogAdapter(Context context, LinkedList<FLog.LogMsg> logs) {
        this.logs = new LinkedList<>(logs);
        COLOR_LOG_RED = context.getResources().getColor(R.color.log_red);
        COLOR_LOG_WHITE = context.getResources().getColor(R.color.log_white);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearLog() {
        logs.clear();
        notifyDataSetChanged();
    }

    @Override
    public void print(FLog.LogMsg log) {
        if (logs.size() >= LOG_MAX_COUNT) {
            logs.removeLast();
        }
        logs.addFirst(log);
        notifyItemInserted(0);
    }

    @NonNull
    @NotNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_log, viewGroup, false);
        return new LogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LogViewHolder viewHolder, int i) {
        viewHolder.setLog(logs.get(i));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        FLog.LogMsg msg;

        public LogViewHolder(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt_log);
        }

        public void setLog(FLog.LogMsg log) {
            msg = log;
            txt.setText(msg.toString());
            txt.setTextColor(msg.level >= Log.ERROR ? COLOR_LOG_RED : COLOR_LOG_WHITE);
        }
    }
}
