package com.rahul.donate_a_food.Views;
//
//public class CurvedTextView {
//}


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CurvedTextView extends View {

    private Paint paint;
    private String text = "Curved Text!";
    private Path path;
    private RectF oval;

    public CurvedTextView(Context context) {
        super(context);
        init();
    }

    public CurvedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurvedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFF000000); // Black color
        paint.setTextSize(100); // Set text size
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        // Create a path to curve the text
        path = new Path();

        // Oval shape for the arc
        oval = new RectF(200, 200, 1200, 1200); // Define the bounds for the arc

        // Add an arc to the path
        path.addArc(oval, 0, 180); // Start at 0 degree and sweep across 180 degrees
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw text along the path
        canvas.drawTextOnPath(text, path, 0, 0, paint);
    }

    public void setText(String newText) {
        this.text = newText;
        invalidate(); // Refresh the view
    }

}
