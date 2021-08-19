package com.syinfotech.youtubedownloader.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syinfotech.youtubedownloader.R;
import com.syinfotech.youtubedownloader.Task.DownloadAdapter;
import com.syinfotech.youtubedownloader.Task.DownloadDatabse;
import com.syinfotech.youtubedownloader.Task.ModelClass;

import java.util.List;

public class DownloadsFragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;
    private    TextView nod;
    public DownloadsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_downloads, container, false);
        List<ModelClass> list = new DownloadDatabse(context).getDownloadsitems();
         nod = v.findViewById(R.id.none_downloads);
        TextView clear = v.findViewById(R.id.clear_downloads);
        clear.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm");
            builder.setMessage("Are You Sure Clear ?");
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.setPositiveButton("Clear", (DialogInterface dialogInterface, int i) -> {
                new DownloadDatabse(context).RemoveAllDownloads();

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new DownloadsFragment())
                        .commit();
                clear.setVisibility(View.GONE);
            });
            builder.show();

        });
        recyclerView = v.findViewById(R.id.downloadsrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        SetData(list);
        return v;
    }

    private void SetData(List<ModelClass> list) {
        if (list !=null&&list.size()!=0){
            nod.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
        else {
            nod.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(new DownloadAdapter(context,list));
    }
}