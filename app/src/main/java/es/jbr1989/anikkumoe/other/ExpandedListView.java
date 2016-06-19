package es.jbr1989.anikkumoe.other;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by jbr1989 on 22/05/2016.
 */
public class ExpandedListView extends ListView {

    private android.view.ViewGroup.LayoutParams params;
    private int old_count = 0;

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedListView  (Context context) {
        super(context);
    }

    public ExpandedListView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
/*
    @Override
    protected void onDraw(Canvas canvas) {
        if (getCount() != old_count) {
            old_count = getCount();
            params = getLayoutParams();
            params.height = getCount() * (old_count > 0 ? getChildAt(0).getHeight() : 0);
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }
*/
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}