/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.im.uicommon.widgets.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatGridItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpanCount;
    private int mSpacing;
    private boolean mIncludeEdge;

    public ChatGridItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.mSpanCount = spanCount;
        this.mSpacing = spacing;
        this.mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) manager;
            if (mIncludeEdge) {
                int position = parent.getChildAdapterPosition(view); // item position
                int spanSize = layoutManager.getSpanSizeLookup().getSpanSize(position);
                if (spanSize == mSpanCount) {
                    outRect.left = 0;
                    outRect.right = 0;
                } else {
                    int column = (position) % mSpanCount; // item column
                    // spacing - column * ((1f / spanCount) * spacing)
                    //  outRect.left = mSpacing - column * mSpacing / mSpanCount;
                    outRect.left = (int) (mSpacing - column * ((1f / mSpanCount) * mSpacing));
                    // (column + 1) * ((1f / spanCount) * spacing)
                    outRect.right = (int) ((column + 1) * ((1f / mSpanCount) * mSpacing));
                    // outRect.right = (column + 1) * mSpacing / mSpanCount;
                }
                outRect.bottom = mSpacing;

            } else {
                int position = parent.getChildAdapterPosition(view); // item position
                int column = (position) % mSpanCount; // item column
                // column * ((1f / spanCount) * spacing)
                outRect.left = column * mSpacing / mSpanCount;
                // spacing - (column + 1) * ((1f / spanCount) * spacing)
                outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount;
                if (position >= mSpanCount) {
                    outRect.top = mSpacing; // item top
                }
            }
        }

    }
}
