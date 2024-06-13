package id.jostudios.penielcommunity.Helpers

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils

object AnimationHelper {
    public fun animateView(context: Context,view: View, animID: Int) {
        var animation = AnimationUtils.loadAnimation(context, animID);
        view.startAnimation(animation);
    }
}