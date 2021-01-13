package id.thork.app.utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2


/**
 * Created by Dhimas Saputra on 12/01/21
 * Jakarta, Indonesia.
 */
object TransformationPagers {

    internal class CubeInDepthTransformation : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.cameraDistance = 20000f
            if (position < -1) {
                page.alpha = 0f
            } else if (position <= 0) {
                page.alpha = 1f
                page.pivotX = page.width.toFloat()
                page.rotationY = 90 * Math.abs(position)
            } else if (position <= 1) {
                page.alpha = 1f
                page.pivotX = 0f
                page.rotationY = -90 * Math.abs(position)
            } else {
                page.alpha = 0f
            }
            val max = Math.max(.4f, 1 - Math.abs(position))
            if (Math.abs(position) <= 0.5) {
                page.scaleY = max
            } else if (Math.abs(position) <= 1) {
                page.scaleY = max
            }
        }
    }

    internal class HorizontalFlipTransformation : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.translationX = -position * page.width
            page.cameraDistance = 12000f
            if (position < 0.5 && position > -0.5) {
                page.visibility = View.VISIBLE
            } else {
                page.visibility = View.INVISIBLE
            }
            if (position < -1) {     // [-Infinity,-1)
                page.alpha = 0f
            } else if (position <= 0) {    // [-1,0]
                page.alpha = 1f
                page.rotationY = 180 * (1 - Math.abs(position) + 1)
            } else if (position <= 1) {    // (0,1]
                page.alpha = 1f
                page.rotationY = -180 * (1 - Math.abs(position) + 1)
            } else {
                page.alpha = 0f
            }
        }
    }

    internal class ZoomInTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val scale = if (position < 0) position + 1f else Math.abs(1f - position)
            page.scaleX = scale
            page.scaleY = scale
            page.pivotX = page.width * 0.5f
            page.pivotY = page.height * 0.5f
            page.alpha = if (position < -1f || position > 1f) 0f else 1f - (scale - 1f)
        }

        companion object {
            const val MAX_ROTATION = 90.0f
        }
    }

    internal class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            val pageWidth = view.width
            val pageHeight = view.height
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.alpha = 0f
            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                val vertMargin = pageHeight * (1 - scaleFactor) / 2
                val horzMargin = pageWidth * (1 - scaleFactor) / 2
                if (position < 0) {
                    view.translationX = horzMargin - vertMargin / 2
                } else {
                    view.translationX = -horzMargin + vertMargin / 2
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor

                // Fade the page relative to its size.
                view.alpha = MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                        (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.alpha = 0f
            }
        }

        companion object {
            private const val MIN_SCALE = 0.85f
            private const val MIN_ALPHA = 0.5f
        }
    }

    internal class FidgetSpinTransformation : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.translationX = -position * page.width
            if (Math.abs(position) < 0.5) {
                page.visibility = View.VISIBLE
                page.scaleX = 1 - Math.abs(position)
                page.scaleY = 1 - Math.abs(position)
            } else if (Math.abs(position) > 0.5) {
                page.visibility = View.GONE
            }
            if (position < -1) {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0f
            } else if (position <= 0) {    // [-1,0]
                page.alpha = 1f
                page.rotation =
                    36000 * (Math.abs(position) * Math.abs(position) * Math.abs(position) * Math.abs(
                        position
                    ) * Math.abs(position) * Math.abs(position) * Math.abs(position))
            } else if (position <= 1) {    // (0,1]
                page.alpha = 1f
                page.rotation =
                    -36000 * (Math.abs(position) * Math.abs(position) * Math.abs(position) * Math.abs(
                        position
                    ) * Math.abs(position) * Math.abs(position) * Math.abs(position))
            } else {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0f
            }
        }
    }

    internal class TossTransformation : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.translationX = -position * page.width
            page.cameraDistance = 20000f
            if (position < 0.5 && position > -0.5) {
                page.visibility = View.VISIBLE
            } else {
                page.visibility = View.INVISIBLE
            }
            if (position < -1) {     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0f
            } else if (position <= 0) {    // [-1,0]
                page.alpha = 1f
                page.scaleX = Math.max(0.4f, 1 - Math.abs(position))
                page.scaleY = Math.max(0.4f, 1 - Math.abs(position))
                page.rotationX = 1080 * (1 - Math.abs(position) + 1)
                page.translationY = -1000 * Math.abs(position)
            } else if (position <= 1) {    // (0,1]
                page.alpha = 1f
                page.scaleX = Math.max(0.4f, 1 - Math.abs(position))
                page.scaleY = Math.max(0.4f, 1 - Math.abs(position))
                page.rotationX = -1080 * (1 - Math.abs(position) + 1)
                page.translationY = -1000 * Math.abs(position)
            } else {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0f
            }
        }
    }
}