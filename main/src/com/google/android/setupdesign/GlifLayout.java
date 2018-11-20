/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.google.android.setupdesign;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.setupcompat.PartnerCustomizationLayout;
import com.google.android.setupcompat.template.StatusBarMixin;
import com.google.android.setupdesign.template.ButtonFooterMixin;
import com.google.android.setupdesign.template.ColoredHeaderMixin;
import com.google.android.setupdesign.template.HeaderMixin;
import com.google.android.setupdesign.template.IconMixin;
import com.google.android.setupdesign.template.ProgressBarMixin;
import com.google.android.setupdesign.template.RequireScrollMixin;
import com.google.android.setupdesign.template.ScrollViewScrollHandlingDelegate;

/**
 * Layout for the GLIF theme used in Setup Wizard for N.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * &lt;com.google.android.setupdesign.GlifLayout
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     xmlns:app="http://schemas.android.com/apk/res-auto"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:icon="@drawable/my_icon"
 *     app:suwHeaderText="@string/my_title">
 *
 *     &lt;!-- Content here -->
 *
 * &lt;/com.google.android.setupdesign.GlifLayout>
 * }</pre>
 */
public class GlifLayout extends PartnerCustomizationLayout {

  private static final String TAG = "GlifLayout";

  private ColorStateList primaryColor;

  private boolean backgroundPatterned = true;

  /** The color of the background. If null, the color will inherit from primaryColor. */
  @Nullable private ColorStateList backgroundBaseColor;

  public GlifLayout(Context context) {
    this(context, 0, 0);
  }

  public GlifLayout(Context context, int template) {
    this(context, template, 0);
  }

  public GlifLayout(Context context, int template, int containerId) {
    super(context, template, containerId);
    init(null, R.attr.suwLayoutTheme);
  }

  public GlifLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, R.attr.suwLayoutTheme);
  }

  @TargetApi(VERSION_CODES.HONEYCOMB)
  public GlifLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, defStyleAttr);
  }

  // All the constructors delegate to this init method. The 3-argument constructor is not
  // available in LinearLayout before v11, so call super with the exact same arguments.
  private void init(AttributeSet attrs, int defStyleAttr) {
    registerMixin(HeaderMixin.class, new ColoredHeaderMixin(this, attrs, defStyleAttr));
    registerMixin(IconMixin.class, new IconMixin(this, attrs, defStyleAttr));
    registerMixin(ProgressBarMixin.class, new ProgressBarMixin(this));
    registerMixin(ButtonFooterMixin.class, new ButtonFooterMixin(this));
    final RequireScrollMixin requireScrollMixin = new RequireScrollMixin(this);
    registerMixin(RequireScrollMixin.class, requireScrollMixin);

    final ScrollView scrollView = getScrollView();
    if (scrollView != null) {
      requireScrollMixin.setScrollHandlingDelegate(
          new ScrollViewScrollHandlingDelegate(requireScrollMixin, scrollView));
    }

    TypedArray a =
        getContext().obtainStyledAttributes(attrs, R.styleable.SuwGlifLayout, defStyleAttr, 0);

    ColorStateList primaryColor = a.getColorStateList(R.styleable.SuwGlifLayout_suwColorPrimary);
    if (primaryColor != null) {
      setPrimaryColor(primaryColor);
    }

    ColorStateList backgroundColor =
        a.getColorStateList(R.styleable.SuwGlifLayout_suwBackgroundBaseColor);
    setBackgroundBaseColor(backgroundColor);

    boolean backgroundPatterned =
        a.getBoolean(R.styleable.SuwGlifLayout_suwBackgroundPatterned, true);
    setBackgroundPatterned(backgroundPatterned);

    final int stickyHeader = a.getResourceId(R.styleable.SuwGlifLayout_suwStickyHeader, 0);
    if (stickyHeader != 0) {
      inflateStickyHeader(stickyHeader);
    }

  }

  @Override
  protected View onInflateTemplate(LayoutInflater inflater, @LayoutRes int template) {
    if (template == 0) {
      template = R.layout.suw_glif_template;
    }
    return inflateTemplate(inflater, R.style.SuwThemeGlif_Light, template);
  }

  @Override
  protected ViewGroup findContainer(int containerId) {
    if (containerId == 0) {
      containerId = R.id.suw_layout_content;
    }
    return super.findContainer(containerId);
  }

  /**
   * Sets the sticky header (i.e. header that doesn't scroll) of the layout, which is at the top of
   * the content area outside of the scrolling container. The header can only be inflated once per
   * instance of this layout.
   *
   * @param header The layout to be inflated as the header.
   * @return The root of the inflated header view.
   */
  public View inflateStickyHeader(@LayoutRes int header) {
    ViewStub stickyHeaderStub = findManagedViewById(R.id.suw_layout_sticky_header);
    stickyHeaderStub.setLayoutResource(header);
    return stickyHeaderStub.inflate();
  }

  public ScrollView getScrollView() {
    final View view = findManagedViewById(R.id.suw_scroll_view);
    return view instanceof ScrollView ? (ScrollView) view : null;
  }

  public TextView getHeaderTextView() {
    return getMixin(HeaderMixin.class).getTextView();
  }

  public void setHeaderText(int title) {
    getMixin(HeaderMixin.class).setText(title);
  }

  public void setHeaderText(CharSequence title) {
    getMixin(HeaderMixin.class).setText(title);
  }

  public CharSequence getHeaderText() {
    return getMixin(HeaderMixin.class).getText();
  }

  public void setHeaderColor(ColorStateList color) {
    final ColoredHeaderMixin mixin = (ColoredHeaderMixin) getMixin(HeaderMixin.class);
    mixin.setColor(color);
  }

  public ColorStateList getHeaderColor() {
    final ColoredHeaderMixin mixin = (ColoredHeaderMixin) getMixin(HeaderMixin.class);
    return mixin.getColor();
  }

  public void setIcon(Drawable icon) {
    getMixin(IconMixin.class).setIcon(icon);
  }

  public Drawable getIcon() {
    return getMixin(IconMixin.class).getIcon();
  }

  /**
   * Sets the primary color of this layout, which will be used to determine the color of the
   * progress bar and the background pattern.
   */
  public void setPrimaryColor(@NonNull ColorStateList color) {
    primaryColor = color;
    updateBackground();
    getMixin(ProgressBarMixin.class).setColor(color);
  }

  public ColorStateList getPrimaryColor() {
    return primaryColor;
  }

  /**
   * Sets the base color of the background view, which is the status bar for phones and the full-
   * screen background for tablets. If {@link #isBackgroundPatterned()} is true, the pattern will be
   * drawn with this color.
   *
   * @param color The color to use as the base color of the background. If {@code null}, {@link
   *     #getPrimaryColor()} will be used.
   */
  public void setBackgroundBaseColor(@Nullable ColorStateList color) {
    backgroundBaseColor = color;
    updateBackground();
  }

  /**
   * @return The base color of the background. {@code null} indicates the background will be drawn
   *     with {@link #getPrimaryColor()}.
   */
  @Nullable
  public ColorStateList getBackgroundBaseColor() {
    return backgroundBaseColor;
  }

  /**
   * Sets whether the background should be {@link GlifPatternDrawable}. If {@code false}, the
   * background will be a solid color.
   */
  public void setBackgroundPatterned(boolean patterned) {
    backgroundPatterned = patterned;
    updateBackground();
  }

  /** @return True if this view uses {@link GlifPatternDrawable} as background. */
  public boolean isBackgroundPatterned() {
    return backgroundPatterned;
  }

  private void updateBackground() {
    final View patternBg = findManagedViewById(R.id.suc_layout_status);
    if (patternBg != null) {
      int backgroundColor = 0;
      if (backgroundBaseColor != null) {
        backgroundColor = backgroundBaseColor.getDefaultColor();
      } else if (primaryColor != null) {
        backgroundColor = primaryColor.getDefaultColor();
      }
      Drawable background =
          backgroundPatterned
              ? new GlifPatternDrawable(backgroundColor)
              : new ColorDrawable(backgroundColor);
      getMixin(StatusBarMixin.class).setStatusBarBackground(background);
    }
  }

  public boolean isProgressBarShown() {
    return getMixin(ProgressBarMixin.class).isShown();
  }

  public void setProgressBarShown(boolean shown) {
    getMixin(ProgressBarMixin.class).setShown(shown);
  }

  public ProgressBar peekProgressBar() {
    return getMixin(ProgressBarMixin.class).peekProgressBar();
  }
}
