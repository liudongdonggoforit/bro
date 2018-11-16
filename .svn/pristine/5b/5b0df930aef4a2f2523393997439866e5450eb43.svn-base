package com.sh.browser.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

@SuppressWarnings("WeakerAccess")
public class SwipeToDismissListener implements View.OnTouchListener {
    public interface DismissCallback {
        void onDismiss();
    }

    private final View view;
    private final DismissCallback callback;

    private int viewWidth = 1;
    private final int slop;
    private final int minFlingVelocity;
    private final int maxFlingVelocity;
    private final long animTime;

    private float downX;
    private float translationX;
    private boolean swiping;
    private int swipingSlop;
    private VelocityTracker velocityTracker;

    public SwipeToDismissListener(View view, DismissCallback callback) {
        this.view = view;
        this.callback = callback;

        ViewConfiguration configuration = ViewConfiguration.get(view.getContext());
        this.slop = configuration.getScaledTouchSlop();
        this.minFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        this.maxFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        this.animTime = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        event.offsetLocation(translationX, 0);
        if (viewWidth < 2) {
            viewWidth = this.view.getWidth();
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getRawX();
                velocityTracker = VelocityTracker.obtain();
                velocityTracker.addMovement(event);

                return false;
            } case MotionEvent.ACTION_UP: {
                if (velocityTracker == null) {
                    break;
                }

                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);

                float deltaX = event.getRawX() - downX;
                float velocityX = velocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityTracker.getXVelocity());
                boolean dismiss = false;
                boolean dismissDown = false;

                if ((Math.abs(deltaX) > viewWidth / 2) && swiping) {
                    dismiss = true;
                    dismissDown = (deltaX > 0);
                } else if (minFlingVelocity <= absVelocityX && absVelocityX <= maxFlingVelocity && swiping) {
                    dismiss = ((velocityX < 0) == (deltaX < 0));
                    dismissDown = velocityTracker.getXVelocity() > 0;
                }

                if (dismiss) {
                    this.view.animate()
                            .translationX(dismissDown ? viewWidth : -viewWidth)
                            .alpha(0)
                            .setDuration(animTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performDismiss();
                                }
                            });
                } else if (swiping) {
                    this.view.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(animTime)
                            .setListener(null);
                }

                downX = 0;
                translationX = 0;
                swiping = false;
                velocityTracker.recycle();
                velocityTracker = null;

                break;
            } case MotionEvent.ACTION_CANCEL: {
                if (velocityTracker == null) {
                    break;
                }

                this.view.animate()
                        .translationX(0f)
                        .alpha(1f)
                        .setDuration(animTime)
                        .setListener(null);

                downX = 0;
                translationX = 0;
                swiping = false;
                velocityTracker.recycle();
                velocityTracker = null;

                break;
            } case MotionEvent.ACTION_MOVE: {
                if (velocityTracker == null) {
                    break;
                }
                velocityTracker.addMovement(event);

                float deltaX = event.getRawX() - downX;
                if (Math.abs(deltaX) > slop) {
                    swiping = true;
                    swipingSlop = (deltaX > 0 ? slop : -slop);
                    this.view.getParent().requestDisallowInterceptTouchEvent(true);

                    MotionEvent cancelEvent = MotionEvent.obtainNoHistory(event);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    this.view.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (swiping) {
                    translationX = deltaX;
                    this.view.setTranslationX(deltaX - swipingSlop);
                    this.view.setAlpha(Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaX) / viewWidth)));

                    return true;
                }

                break;
            }
        }

        return false;
    }

    private void performDismiss() {
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        final int originalWidth = view.getWidth();

        ValueAnimator animator = ValueAnimator.ofInt(originalWidth, 1).setDuration(animTime);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onDismiss();
                view.setAlpha(1f);
                view.setTranslationY(0f);
                layoutParams.width = originalWidth;
                view.setLayoutParams(layoutParams);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.width = (Integer) animation.getAnimatedValue();
                view.setLayoutParams(layoutParams);
            }
        });
        animator.start();
    }
}
