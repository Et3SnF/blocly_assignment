package io.bloc.android.blocly.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import io.bloc.android.blocly.R;

public class RobotoTextView extends TextView {

    // Allocate the static map to track existing TypeFace (expensive to inflate)
    // Using this static map will allow the RobotoTextView instance to reuse existing
    // TypeFace rather than create a new one

    // This class has three constructors and one method, that pretty much does all of the work
    // to associate the mapping of the attrs.xml and arrays.xml to utilize the font

    private static Map<String, Typeface> sTypefaces = new HashMap<String, Typeface>();

    // Constructors

    public RobotoTextView(Context context) {
        super(context);
    }

    // #4
    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        extractFont(attrs);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractFont(attrs);
    }

    // This extractFont method is responsible for allowing xml content to be inflated into
    // programmatic Views.
    // LayoutInflator class finds the most appropriate constructor and passes in the attrs variable,
    // which contains every attribute for the View within XML

    void extractFont(AttributeSet attrs) {

        // If the attribute set is in edit mode, stop.
        // If the attribute set is null, stop.

        if (isInEditMode()) {
            return;
        }
        if (attrs == null) {
            return;
        }

        // The key method below is obtainStyledAttributes with 4 parameters in them
        // 1st parameter: base set of attribute values
        // 2nd parameter: desired attributes to be retrieved (in an integer array)
        // 3rd parameter: 0 b/c we are not using references to a style resource
        // 4th parameter: 0 b/c we are not using references to retrieve default values of TypedArray

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.Roboto, 0, 0);

        // Gets the index of the font values we assigned to the enumerated xml file from earlier

        boolean condensed = typedArray.getBoolean(R.styleable.Roboto_robotoFontCond, false);

        int robotoFontIndex = typedArray.getInteger(R.styleable.Roboto_robotoFont, -1);

        // Mandatory method after executing the obtainStyledAttributes method

        typedArray.recycle();

        // Where we INFLATE our resources into a string array. We got this from the arrays.xml file
        // from the line strings-array name = "roboto_font_file_names". We need to access this
        // file that starts from with resource tags by using getResources()!!
        // Then retrieve the array of strings

        String[] stringArray;

        if(condensed) {
            stringArray = getResources().getStringArray(R.array.roboto_font_file_names_condensed);
        }
        else {
            stringArray = getResources().getStringArray(R.array.roboto_font_file_names_regular);
        }

        // All of the mapping between arrays.xml and attrs.xml are all done. Now associate the
        // stylable attribute with the enumeration

        String robotoFont = stringArray[robotoFontIndex];

        // This line is important to make sure that no brand new typeface is suddenly inflated
        // Instead replace that ordinary Typeface with the one from the Map created at the beginning
        // of this class.

        Typeface robotoTypeface = null;

        if (sTypefaces.containsKey(robotoFont)) {
            robotoTypeface = sTypefaces.get(robotoFont);
        }
        else {
            robotoTypeface = Typeface.createFromAsset(getResources().getAssets(), "fonts/RobotoTTF/" + robotoFont);
            sTypefaces.put(robotoFont, robotoTypeface);
        }

        setTypeface(robotoTypeface);
    }
}