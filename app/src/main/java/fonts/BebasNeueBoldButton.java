package fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by nimaikrsna on 8/6/2015.
 */
public class BebasNeueBoldButton extends Button {

    public BebasNeueBoldButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BebasNeueBoldButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BebasNeueBoldButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "BebasNeue Bold.ttf");
        setTypeface(tf);
    }

}

