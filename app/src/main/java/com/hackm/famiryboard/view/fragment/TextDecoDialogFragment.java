package com.hackm.famiryboard.view.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.hackm.famiryboard.R;
import com.hackm.famiryboard.controller.util.FontUtil;
import com.hackm.famiryboard.model.pojo.FontStyles;
import com.hackm.famiryboard.model.viewobject.AssetTypeface;
import com.hackm.famiryboard.view.adapter.TypefaceAdapter;
import com.hackm.famiryboard.view.widget.ColorSelectorView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shunhosaka on 2015/01/05.
 */
public class TextDecoDialogFragment extends DialogFragment implements View.OnClickListener , ColorSelectorView.OnColorChangeListener{

    private static final String EXTRA_FONT_STYLE_GSON = "extra_font_style_gson";
    private static final String EXTRA_VALUE_INDEX = "extra_value_index";


    public static TextDecoDialogFragment newInstance(FontStyles fontStyles) {
        return newInstance(fontStyles, -1);
    }

    public static TextDecoDialogFragment newInstance(FontStyles fontStyles, int index) {
        TextDecoDialogFragment fragment = new TextDecoDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_FONT_STYLE_GSON, new Gson().toJson(fontStyles, FontStyles.class));
        args.putInt(EXTRA_VALUE_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    private EditText mEditTextInput;
    private OnTextDecoratedListener mListener;

    //Font Customize Views
    private TextView mSizeTextView;

    private ImageButton mStyleBoldButton;
    private ImageButton mStyleItalicButton;
    private View mColorPalatteView;
    private ColorSelectorView mColorSelectorView;

    private Spinner mFontTypeSpinner;

    private int mFontSize = 24;
    private AssetTypeface mFontTypeface = null;
    private int mFontStyle = Typeface.NORMAL;
    private int mFontGravity = Gravity.LEFT;
    private int mFontColor = Color.BLACK;

    private int mObjectIndex = -1;

    private TypefaceAdapter mTypefaceAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.fragment_dialog_textdecolation);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mEditTextInput = (EditText) dialog.findViewById(R.id.textdeco_edittext);
        // Set Button(キャンセル・OK)
        dialog.findViewById(R.id.textdeco_button_positive).setOnClickListener(this);
        dialog.findViewById(R.id.textdeco_button_negative).setOnClickListener(this);

        /* Font custom view */
        //size
        mSizeTextView = (TextView) dialog.findViewById(R.id.font_textview_size);
        dialog.findViewById(R.id.font_imagebutton_size_plus).setOnClickListener(this);
        dialog.findViewById(R.id.font_imagebutton_size_minus).setOnClickListener(this);
        mStyleBoldButton = (ImageButton) dialog.findViewById(R.id.font_imagebutton_style_bold);
        mStyleBoldButton.setOnClickListener(this);
        mStyleItalicButton = (ImageButton) dialog.findViewById(R.id.font_imagebutton_style_italic);
        mStyleItalicButton.setOnClickListener(this);
        mColorPalatteView = dialog.findViewById(R.id.font_view_color_palette);
        mColorSelectorView = (ColorSelectorView) dialog.findViewById(R.id.font_colorselectorview);
        mColorSelectorView.setOnColorChangeListener(this);
        mFontTypeSpinner = (Spinner) dialog.findViewById(R.id.font_spinner_type);

        setFontTypeView();

        fontInitialize();
        //Set deco view object index.
        Bundle args = getArguments();
        mObjectIndex = args.getInt(EXTRA_VALUE_INDEX);

        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * フォントの初期化,渡されたパラメーターの挿入を行うメソッド
     */
    private void fontInitialize() {
        Bundle args = getArguments();
        if (args != null) {
            String fontStyleGson = args.getString(EXTRA_FONT_STYLE_GSON);
            FontStyles fontStyles = new Gson().fromJson(fontStyleGson, FontStyles.class);
            mEditTextInput.setText(fontStyles.text);
            mFontSize = fontStyles.size;
            mFontTypeface = new AssetTypeface(fontStyles.typefacePath, FontUtil.getFont("fonts/"+fontStyles.typefacePath, getActivity()));
            mFontStyle = fontStyles.style;
            mFontGravity = fontStyles.gravity;
            mFontColor = fontStyles.color;
        }
        mEditTextInput.setTextSize(mFontSize);
        mSizeTextView.setText(getString(R.string.font_textview_size, mFontSize));

        if (mFontStyle == Typeface.BOLD_ITALIC) {
            mStyleBoldButton.setTag(true);
            mStyleItalicButton.setTag(true);
        } else if (mFontStyle == Typeface.BOLD) {
            mStyleBoldButton.setTag(true);
            mStyleItalicButton.setTag(false);
        } else if (mFontStyle == Typeface.ITALIC) {
            mStyleBoldButton.setTag(false);
            mStyleItalicButton.setTag(true);
        } else {
            mStyleBoldButton.setTag(false);
            mStyleItalicButton.setTag(false);
        }
        mStyleBoldButton.setBackgroundResource((boolean) mStyleBoldButton.getTag() ? R.drawable.ic_font_style_true : R.drawable.ic_font_style_false);
        mStyleItalicButton.setBackgroundResource((boolean) mStyleItalicButton.getTag() ? R.drawable.ic_font_style_true : R.drawable.ic_font_style_false);
        setFontStyle();

        mColorSelectorView.setColor(mFontColor);
        onColorChange(mFontColor);

        int position = mTypefaceAdapter.getPosition(mFontTypeface);
        Log.d(getClass().getSimpleName(), "Get Position:" + Integer.toString(position));
        //Set Selected Font
        mFontTypeSpinner.setSelection(position, false);
    }

    /**
     * フォント選択用のレイアウトを用意する
     */
    private void setFontTypeView() {
        String[] fontList = null;
        try {
            fontList = getResources().getAssets().list("fonts");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<AssetTypeface> assetTypefaces = new ArrayList<AssetTypeface>();
        for (int i = 0; i < fontList.length; i++) {
            assetTypefaces.add(new AssetTypeface(fontList[i], FontUtil.getFont("fonts/"+fontList[i], getActivity())));
        }
        mTypefaceAdapter = new TypefaceAdapter(getActivity(), R.layout.item_font_typeface, assetTypefaces);
        mTypefaceAdapter.setDropDownViewResource(R.layout.item_font_typeface_dropdown);
        mFontTypeSpinner.setAdapter(mTypefaceAdapter);
        mFontTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFontTypeface = mTypefaceAdapter.getItem(position);
                mEditTextInput.setTypeface(mFontTypeface.typeface, mFontStyle);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textdeco_button_positive:
                if (mListener != null) {
                    FontStyles fontStyles = new FontStyles(mEditTextInput.getText().toString(), mFontSize, mFontTypeface.assetPath, mFontStyle, mFontGravity, mFontColor);
                    mListener.onTextDecorated(createBitmap(), fontStyles, mObjectIndex);
                }
                dismiss();
                break;
            case R.id.textdeco_button_negative:
                if (mListener != null) {
                    mListener.onCanceled();
                }
                dismiss();
                break;
            //Font
            case R.id.font_imagebutton_size_plus:
                //96以上の値にはしない。
                mFontSize = Math.min(96, mFontSize + 1);
                mSizeTextView.setText(getString(R.string.font_textview_size, mFontSize));
                mEditTextInput.setTextSize(mFontSize);
                break;
            case R.id.font_imagebutton_size_minus:
                //1以下の値にはしない。
                mFontSize = Math.max(1, mFontSize - 1);
                mSizeTextView.setText(getString(R.string.font_textview_size, mFontSize));
                mEditTextInput.setTextSize(mFontSize);
                break;
            case R.id.font_imagebutton_style_bold:
                boolean isBold = !(boolean) mStyleBoldButton.getTag();
                mStyleBoldButton.setTag(isBold);
                mStyleBoldButton.setBackgroundResource(isBold ? R.drawable.ic_font_style_true : R.drawable.ic_font_style_false);
                setFontStyle();
                break;
            case R.id.font_imagebutton_style_italic:
                boolean isItalic = !(boolean) mStyleItalicButton.getTag();
                mStyleItalicButton.setTag(isItalic);
                mStyleItalicButton.setBackgroundResource(isItalic ? R.drawable.ic_font_style_true : R.drawable.ic_font_style_false);
                setFontStyle();
                break;
        }
    }

    @Override
    public void onColorChange(int color) {
        mColorPalatteView.setBackgroundColor(color);
        mEditTextInput.setTextColor(color);
        mFontColor = color;
    }

    private void setFontStyle() {
        boolean isBold = false;
        boolean isItalic = false;
        try {
            if (mStyleBoldButton.getTag() != null) {
                isBold = (boolean) mStyleBoldButton.getTag();
            }
            if (mStyleItalicButton.getTag() != null) {
                isItalic = (boolean) mStyleItalicButton.getTag();
            }
        } catch (ClassCastException e) {
        }
        mFontStyle = Typeface.NORMAL;
        if (isBold && isItalic) {
            mFontStyle = Typeface.BOLD_ITALIC;
        } else if (isBold) {
            mFontStyle = Typeface.BOLD;
        } else if (isItalic) {
            mFontStyle = Typeface.ITALIC;
        }

        mEditTextInput.setTypeface(mFontTypeface.typeface, mFontStyle);
    }

    private Bitmap createBitmap() {
        //斜体にすると最後の部分が消えてしまうのでスペースを追加
        if (mFontStyle == Typeface.ITALIC || mFontStyle == Typeface.BOLD_ITALIC) {
            mEditTextInput.setText( " " + mEditTextInput.getText() + " ");
        }
        mEditTextInput.setCursorVisible(false);
        mEditTextInput.setDrawingCacheEnabled(false);
        mEditTextInput.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mEditTextInput.getDrawingCache());
        return bitmap;
    }

    public void setCallback(OnTextDecoratedListener callback) {
        this.mListener = callback;
    }

    public interface OnTextDecoratedListener {
        public void onTextDecorated(Bitmap bitmap, FontStyles fontStyles, int index);
        public void onCanceled();
    }
}