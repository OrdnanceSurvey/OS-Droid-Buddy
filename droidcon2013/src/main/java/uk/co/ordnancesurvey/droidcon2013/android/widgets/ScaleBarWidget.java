package uk.co.ordnancesurvey.droidcon2013.android.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import uk.co.ordnancesurvey.droidcon2013.android.BuildConfig;
import uk.co.ordnancesurvey.droidcon2013.android.R;

public class ScaleBarWidget extends View {

    private static final String CLASS_TAG = ScaleBarWidget.class.getSimpleName();

    private static final float DELTA = 1E-8f;

    static ScaleUnit[] SCALE_UNITS = new ScaleUnit[]{
            new ScaleUnit(4480f, 448f, 100000, "100km"),
            new ScaleUnit(448f, 224f, 50000, "50km"),
            new ScaleUnit(244f, 112f, 20000, "20km"),
            new ScaleUnit(112f, 56f, 10000, "10km"),
            new ScaleUnit(56f, 28f, 5000, "5km"),
            new ScaleUnit(28f, 14f, 2000, "2km"),
            new ScaleUnit(14F, 7f, 1000, "1km"),
            new ScaleUnit(7F, 3.5f, 500, "500m"),
            new ScaleUnit(3.5f, 1.75F, 200, "200m"),
            new ScaleUnit(1.75F, 0.875f, 100, "100m"),
            new ScaleUnit(0.875F, 0.4375f, 50, "50m"),
            new ScaleUnit(0.4375F, 0.21875f, 20, "20m"),
            new ScaleUnit(0.21875f, 0.109375f, 10, "10m"),
            new ScaleUnit(0.109375f, 0.0546875f, 5, "5m"),
            new ScaleUnit(0.0546875f, 0, 2, "2m")
    };

    // cached when we get added to the view or our size changes
    private int width, height;
    private float halfWidth, halfHeight;

    private Paint textPaint;
    private Paint scaleBarPaint;

    private ScaleUnit active;
    private int scaleIndex;

    private final ReentrantReadWriteLock scalesLock = new ReentrantReadWriteLock();

    // current mpp
    private float mpp;

    public ScaleBarWidget(Context context) {
        super(context);
        init(null);
    }

    public ScaleBarWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScaleBarWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        textPaint = new Paint();
        textPaint.setColor(0xFF222222);
        textPaint.setAntiAlias(true);
        textPaint.setSubpixelText(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(10 * getResources().getDisplayMetrics().density);
        textPaint.setTypeface(Typeface.create((String)null, Typeface.BOLD));

        scaleBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleBarPaint.setColor(0xFF222222);
        scaleBarPaint.setStyle(Paint.Style.STROKE);
        scaleBarPaint.setStrokeWidth(1.25f * getResources().getDisplayMetrics().density);
        scaleBarPaint.setStrokeJoin(Paint.Join.ROUND);
        scaleBarPaint.setStrokeCap(Paint.Cap.ROUND);

        if (attrs == null) {
            return;
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScaleBarWidget);
        textPaint.setColor(a.getColor(R.styleable.ScaleBarWidget_textColour, 0xFF222222));
        scaleBarPaint.setColor(a.getColor(R.styleable.ScaleBarWidget_scaleBarColor, 0xFF222222));
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;

        halfWidth = w / 2f;
        halfHeight = h / 2f;

        if (BuildConfig.DEBUG) {
            Log.v(CLASS_TAG, String.format("onSizeChanged called: %d %d [%f %f]", w, h, halfWidth, halfHeight));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0 || active == null || mpp < 0.02) {
            return;
        }

        canvas.drawText(active.label, 36, 50, textPaint);

        float length = active.metres / mpp;
        float endx = 25 + length;

        canvas.drawLine(25, 64, endx, 64, scaleBarPaint);
        canvas.drawLine(25, 64, 25, 54, scaleBarPaint);
        canvas.drawLine(endx, 64, endx, 54, scaleBarPaint);


    }

    /**
     * Updates the latest mpp - <b> must be called from a UI thread</b>
     *
     * @param mpp
     */
    public void update(float mpp) {

        if (BuildConfig.DEBUG) {
            Log.v(CLASS_TAG, String.format("MPP update: current = %.7f, new = %.7f", this.mpp, mpp));
        }

        if (Math.abs(mpp - this.mpp) < DELTA && active != null) {

            if (BuildConfig.DEBUG) {
                Log.v(CLASS_TAG, String.format("MPP not changed: current = %.7f, new = %.7f", this.mpp, mpp));
            }
            return;
        }

        // set earlier in case we get updated during checks
        this.mpp = mpp;

        if (BuildConfig.DEBUG) {
            Log.v(CLASS_TAG, "Current ScaleUnit = " + active);
        }

        if (active == null) {
            scalesLock.writeLock().lock();

            try {
                for (int i = 0; i < SCALE_UNITS.length; i++) {
                    if (SCALE_UNITS[i].within(mpp)) {
                        active = SCALE_UNITS[i];
                        scaleIndex = i;
                        break;
                    }
                }
            } finally {
                scalesLock.writeLock().unlock();
            }
        } else if (!active.within(mpp)) {
            if (mpp > active.upper) {
                scalesLock.writeLock().lock();
                try {
                    for (int i = scaleIndex - 1; i >= 0; i--) {
                        active = SCALE_UNITS[i];
                        scaleIndex = i;
                        if (active.within(mpp)) {
                            break;
                        }
                    }
                } finally {
                    scalesLock.writeLock().unlock();
                }
            } else {
                scalesLock.writeLock().lock();
                try {
                    for (int i = scaleIndex + 1; i < SCALE_UNITS.length; i++) {
                        active = SCALE_UNITS[i];
                        scaleIndex = i;
                        if (active.within(mpp)) {
                            break;
                        }
                    }
                } finally {
                    scalesLock.writeLock().unlock();
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Log.v(CLASS_TAG, "New ScaleUnit = " + active);
        }

        invalidate();
    }

    /**
     * represents a value on the scale bar. This ScaleUnit is valid within the right open interval {@code [lower, upper)}
     */
    static class ScaleUnit {

        // upper mpp not inclusive
        float upper;

        // lower mpp inclusive
        float lower;

        // reported metres
        float metres;

        // formatted label value
        String label;

        // toString() value
        String tostr;

        ScaleUnit(float upper, float lower, float metres, String label) {
            this.upper = upper;
            this.lower = lower;
            this.metres = metres;
            this.label = label;
            this.tostr = "ScaleUnit{" +
                "upper=" + upper +
                ", lower=" + lower +
                ", metres=" + metres +
                ", label='" + label + '\'' +
                '}';
        }

        public boolean within(float mpp) {
            return mpp >= lower && mpp < upper;
        }

        @Override
        public String toString() {
            return tostr;
        }
    }
}
