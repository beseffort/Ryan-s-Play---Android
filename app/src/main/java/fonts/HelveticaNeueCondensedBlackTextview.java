package fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by nimaikrsna on 5/2/2015.
 */

public class HelveticaNeueCondensedBlackTextview extends TextView {
    public HelveticaNeueCondensedBlackTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HelveticaNeueCondensedBlackTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HelveticaNeueCondensedBlackTextview(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "helvetica_neue_condensed_black.ttf");

        setTypeface(tf);
    }

}

