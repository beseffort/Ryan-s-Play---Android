package fonts;

/**
 * Created by nimaikrsna on 5/1/2015.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class BebasNeueBoldTextView extends TextView {

    public BebasNeueBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BebasNeueBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BebasNeueBoldTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "BebasNeue Bold.ttf");
        setTypeface(tf);
    }

}
