package fonts;

/**
 * Created by nimaikrsna on 5/1/2015.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.EditText;



public class HelveticalNeueTextview extends TextView{

    public HelveticalNeueTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HelveticalNeueTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HelveticalNeueTextview(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "HelveticaNeue.ttf");
        setTypeface(tf);
    }

}
