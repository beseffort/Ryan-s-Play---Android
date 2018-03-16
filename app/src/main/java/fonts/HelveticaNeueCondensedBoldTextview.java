package fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by nimaikrsna on 5/2/2015.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.EditText;
public class HelveticaNeueCondensedBoldTextview extends TextView {
    public HelveticaNeueCondensedBoldTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HelveticaNeueCondensedBoldTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HelveticaNeueCondensedBoldTextview(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "helvetica_neue_condensed_bold.ttf");

        setTypeface(tf);
    }

}

