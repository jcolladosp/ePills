/*
 * Copyright (C) 2015 Antonio Leiva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package devs.erasmus.epills.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class SquareImageView  extends android.support.v7.widget.AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        double screenCoverage = 0.44;

        if (getContext().getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE ) {
            screenCoverage = 0.33;
        }
        //The picture should cover a third of the screen by default.
        int height = (int)((double)Resources.getSystem().getDisplayMetrics().heightPixels * screenCoverage );
        setMeasuredDimension(width, height);
    }
}