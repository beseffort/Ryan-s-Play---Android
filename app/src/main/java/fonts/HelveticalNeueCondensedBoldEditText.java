package fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by nimaikrsna on 5/4/2015.
 */
public class HelveticalNeueCondensedBoldEditText extends EditText {
    public HelveticalNeueCondensedBoldEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HelveticalNeueCondensedBoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HelveticalNeueCondensedBoldEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "helvetica_neue_condensed_bold.ttf");
        setTypeface(tf);
    }

}
