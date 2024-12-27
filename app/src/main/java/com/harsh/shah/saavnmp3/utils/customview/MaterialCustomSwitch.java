package com.harsh.shah.saavnmp3.utils.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.harsh.shah.saavnmp3.R;

public class MaterialCustomSwitch extends LinearLayout {

    private String textHead;
    private String textOn;
    private String textOff;
    private boolean checked;

    private TextView textHeadView;
    private TextView textDescView;
    private MaterialSwitch materialSwitch;

    public MaterialCustomSwitch(Context context) {
        super(context);
        init(null, 0);
    }

    public MaterialCustomSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MaterialCustomSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes

        inflate(getContext(), R.layout.material_custom_switch, this);

        if(attrs==null) return;

        try (TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialCustomSwitch, defStyle, 0)) {

            textHead = a.getString(R.styleable.MaterialCustomSwitch_textHead);
            textOn = a.getString(R.styleable.MaterialCustomSwitch_textOn);
            textOff = a.getString(R.styleable.MaterialCustomSwitch_textOff);
            checked = a.getBoolean(R.styleable.MaterialCustomSwitch_checked, false);

            textHeadView = findViewById(R.id.text_head);
            textDescView = findViewById(R.id.text_desc);
            materialSwitch = findViewById(R.id.materialSwitch);

            textHeadView.setText(textHead);
            textDescView.setText(checked ? textOn : textOff);
            materialSwitch.setChecked(checked);
            materialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> textDescView.setText(isChecked ? textOn : textOff));

            findViewById(R.id.root).setOnClickListener(v -> materialSwitch.toggle());

//            a.recycle();
        }
    }
}