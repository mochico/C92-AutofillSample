package com.example.mochico.autofillsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStructure;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;

class CustomView extends View {
    private String text = "";
    Paint paint = new Paint();

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_YES);
        paint.setTextSize(80);
        paint.setColor(ContextCompat.getColor(getContext(), android.R.color.black));
    }

    @Override
    public void autofill(AutofillValue value) {
        String autofillText = value.getTextValue().toString();
        setText(autofillText);
    }

    @Override
    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        structure.setClassName(getClass().getName());
        String[] hints = {AUTOFILL_HINT_USERNAME};
        structure.setAutofillHints(hints);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        AutofillManager autofillManager = getContext().getSystemService(AutofillManager.class);
        autofillManager.notifyViewEntered(this);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(this.text, 20, 20, paint);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }
}
