package com.hackm.famiryboard.view.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hackm.famiryboard.R;
import com.squareup.picasso.Picasso;


public class ModalImageFragment extends DialogFragment {

    private static String EXTRA_IMAGE_URL = "extra_image_url";
    private ImageView mImageView;

    public static ModalImageFragment newInstance(String imageUrl) {
        ModalImageFragment fragment = new ModalImageFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.fragment_modal_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mImageView = (ImageView) dialog.findViewById(R.id.modal_image_imageview);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            Picasso.with(getActivity()).load(args.getString(EXTRA_IMAGE_URL)).into(mImageView);
        } else {
            dismiss();
        }
        return dialog;
    }

}