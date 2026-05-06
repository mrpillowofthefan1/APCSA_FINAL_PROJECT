package com.example.apcsa_final_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class StartGuideDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_CONTENT = "arg_content";

    public static StartGuideDialogFragment newInstance(String title, String content) {
        StartGuideDialogFragment fragment = new StartGuideDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_guide_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titleView = view.findViewById(R.id.detail_title);
        TextView contentView = view.findViewById(R.id.detail_content);
        Button closeButton = view.findViewById(R.id.btn_close);

        if (getArguments() != null) {
            titleView.setText(getArguments().getString(ARG_TITLE));
            contentView.setText(getArguments().getString(ARG_CONTENT));
        }

        closeButton.setOnClickListener(v -> dismiss());
    }
}