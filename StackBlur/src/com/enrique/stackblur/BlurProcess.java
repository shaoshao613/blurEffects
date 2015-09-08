package com.enrique.stackblur;

import android.graphics.Bitmap;

interface BlurProcess {
	/**
	 * Process the given image, blurring by the supplied radius.
	 * If radius is 0, this will return original
	 * @param original the bitmap to be blurred
	 * @param radius the radius in pixels to blur the image
	 * @return the blurred version of the image.
	 */
	public static int TYPE_StackBlur=1;
	public static int TYPE_BoxStackBlur=2;
	public static int TYPE_horizontalBlur=3;
	public static int TYPE_Vertical=4;
	public static int TYPE_RadialBlur=5;
	public static int TYPE_CircularBlur=6;
    public Bitmap blur(Bitmap original, float radius);
}
