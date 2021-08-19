package com.syinfotech.youtubedownloader.Task;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.syinfotech.youtubedownloader.R;

import java.io.File;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewholder> {
    private final Context context;
    private final List<ModelClass> list;

    public DownloadAdapter(Context context, List<ModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DownloadViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewholder holder, @SuppressLint("RecyclerView") int position) {
        if (list.get(position).getTitle() != null) {
            holder.title.setText(list.get(position).getTitle());
        }
        if (list.get(position).getFilename().endsWith(".mp3")) {
            holder.mp3.setVisibility(View.VISIBLE);
        } else if (list.get(position).getFilename().endsWith("mp4")) {
            holder.mp4.setVisibility(View.VISIBLE);
        } else if (list.get(position).getFilename().endsWith("m4a")) {
            holder.m4a.setVisibility(View.VISIBLE);
        }
        holder.layout.setOnLongClickListener(view -> {
            BottomSheetDialog sheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogBar);
            @SuppressLint("InflateParams") View bottomsheetview = LayoutInflater.from(context).inflate(R.layout.downloads_menubar, null);
            bottomsheetview.findViewById(R.id.downloads_delete_from_storage).setOnClickListener(view1 -> {
                Delete(position);
                sheetDialog.dismiss();
            });
            bottomsheetview.findViewById(R.id.downloads_remove_from_list).setOnClickListener(view13 -> {
                RemoveFromList(position);
                sheetDialog.dismiss();
            });
            bottomsheetview.findViewById(R.id.downloas_share).setOnClickListener(view14 -> {
                Share(position);
                sheetDialog.dismiss();
            });
            bottomsheetview.findViewById(R.id.downloads_details).setOnClickListener(view15 -> {
                Details(position);
                sheetDialog.dismiss();
            });
            bottomsheetview.findViewById(R.id.downloads_menu_down).setOnClickListener(view12 -> sheetDialog.dismiss());
            sheetDialog.setContentView(bottomsheetview);
            sheetDialog.show();
            return false;
        });
        holder.layout.setOnClickListener(view -> {
            if (list.get(position).getFilename().endsWith(".mp3")) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(list.get(position).getUri())));
                shareIntent.setType("audio/mpeg");
                context.startActivity(Intent.createChooser(shareIntent, "Play In"));
            } else if (list.get(position).getFilename().endsWith("mp4")) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(list.get(position).getUri())));
                shareIntent.setType("video/mp4");
                context.startActivity(Intent.createChooser(shareIntent, "Play In"));
            } else if (list.get(position).getFilename().endsWith("m4a")) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(list.get(position).getUri())));
                shareIntent.setType("audio/m4a");
                context.startActivity(Intent.createChooser(shareIntent, "Play In"));
            }
        });

    }

    private void Delete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete")
                .setMessage(list.get(position).getTitle())
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss()).setPositiveButton("Delete", (dialog, which) -> {
            File file = new File(list.get(position).getUri());
            boolean deleted = file.delete();
            if (deleted) {
                new DownloadDatabse(context).RemoveDownloadFromList(list.get(position).getTitle());
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position, list.size());
                ShowToast("File Deleted SuccessFully");
            } else {
                ShowToast("Failed To Delete ");

            }

        }).show();
    }

    private void ShowToast(String file_deleted_successFully) {
        Toast.makeText(context, file_deleted_successFully, Toast.LENGTH_SHORT).show();
    }

    private void Details(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.details_dialog_layout);
        String name = list.get(position).getTitle();
        String Path = list.get(position).getUri();
        File file = new File(list.get(position).getUri());
        int file_size_kb = Integer.parseInt(String.valueOf(file.length() / 1024));
        int file_size_mb = Integer.parseInt(String.valueOf(file_size_kb / 1024));
        String Size = (file_size_mb + " MB");

        TextView aname = dialog.findViewById(R.id.name_of_downloads);
        TextView apath = dialog.findViewById(R.id.path_of_downloads);
        TextView asize = dialog.findViewById(R.id.size_of_downloads);
        aname.setText(name);
        apath.setText(Path);
        asize.setText(Size);
        dialog.show();
    }

    private void Share(int position) {
        Uri uri = Uri.fromFile(new File(list.get(position).getUri()));
        Intent i = new Intent(Intent.ACTION_SEND);
        if (list.get(position).getUri().endsWith(".mp4")) {
            i.setType("video/*");
        } else {
            i.setType("audio/*");
        }
        i.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(i, "share"));
    }

    private void RemoveFromList(int position) {
        new DownloadDatabse(context).RemoveDownloadFromList(list.get(position).getTitle());
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
        ShowToast("Removed from Downloads");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DownloadViewholder extends RecyclerView.ViewHolder {
        TextView mp3, mp4, m4a, title;
        RelativeLayout layout;

        public DownloadViewholder(@NonNull View itemView) {
            super(itemView);
            mp3 = itemView.findViewById(R.id.downloaded_mp3);
            mp4 = itemView.findViewById(R.id.downloaded_mp4);
            m4a = itemView.findViewById(R.id.downloaded_m4a);
            title = itemView.findViewById(R.id.downloaded_title);
            layout = itemView.findViewById(R.id.rlayout);
        }
    }
}
