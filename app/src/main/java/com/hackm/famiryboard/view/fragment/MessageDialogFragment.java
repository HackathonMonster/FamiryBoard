package com.hackm.famiryboard.view.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hackm.famiryboard.R;

/**
 * Created by shunhosaka on 2015/01/05.
 */
public class MessageDialogFragment extends DialogFragment{

    private static final String EXTRA_DIALOG_TITLE = "extra_dialog_title";
    private static final String EXTRA_DIALOG_MESSAGE = "extra_dialog_message";

    public static MessageDialogFragment newInstance(String title, String message) {
        MessageDialogFragment fragment = new MessageDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_DIALOG_TITLE, title);
        args.putString(EXTRA_DIALOG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTitleTextView;
    private TextView mMessageTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.fragment_dialog_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mTitleTextView = (TextView) dialog.findViewById(R.id.message_textview_title);
        mMessageTextView = (TextView) dialog.findViewById(R.id.message_textview_message);
        dialog.findViewById(R.id.message_button_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            mTitleTextView.setText(args.getString(EXTRA_DIALOG_TITLE));
            mMessageTextView.setText(args.getString(EXTRA_DIALOG_MESSAGE));
        } else {
            dismiss();
        }
        return dialog;
    }

}