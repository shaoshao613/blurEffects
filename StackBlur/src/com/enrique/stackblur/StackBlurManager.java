/**
 * StackBlur v1.0 for Android
 *
 * @Author: Enrique López Mañas <eenriquelopez@gmail.com>
 * http://www.lopez-manas.com
 *
 * Author of the original algorithm: Mario Klingemann <mario.quasimondo.com>
 *
 * This is a compromise between Gaussian Blur and Box blur
 * It creates much better looking blurs than Box Blur, but is
 * 7x faster than my Gaussian Blur implementation.
 *
 * I called it Stack Blur because this describes best how this
 * filter works internally: it creates a kind of moving stack
 * of colors whilst scanning through the image. Thereby it
 * just has to add one new block of color to the right side
 * of the stack and remove the leftmost color. The remaining
 * colors on the topmost layer of the stack are either added on
 * or reduced by one, depending on if they are on the right or
 * on the left side of the stack.
 *
 * @copyright: Enrique López Mañas
 * @license: Apache License 2.0
 */


package com.enrique.stackblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StackBlurManager {
	static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
	static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);

	private static volatile boolean hasRS = true;

	/**
	 * Original image
	 */
	private Bitmap _image;

	/**
	 * Most recent result of blurring
	 */
	private Bitmap _result;

	/**
	 * Method of blurring
	 */
	private final BlurProcess _blurProcess;

	/**
	 * Constructor method (basic initialization and construction of the pixel array)
	 * @param image The image that will be analyed
	 */
	public StackBlurManager(Bitmap image) {
		_image = image;
		_blurProcess = new JavaBlurProcess(1);
	}

	public StackBlurManager(Context mContext,int id) {
		_image= BitmapFactory.decodeResource(mContext.getResources(),id);
		_blurProcess = new JavaBlurProcess(1);
	}

	public void setBitmap(Bitmap image){
		_image = image;
	}

	/**
	 * Process the image on the given radius. Radius must be at least 1
	 * @param radius
	 */
	public Bitmap process(int radius) {
		_result = _blurProcess.blur(_image, radius);
		return _result;
	}

	public Bitmap processStarBlur(int radius) {
		BlurProcess blur=new StackJavaBlurProcess();
		_result = blur.blur(_image, radius);
		return _result;
	}

	public Bitmap processHorizontal(int radius) {
		BlurProcess blur=new JavaBlurProcess(2);
		_result = blur.blur(_image, radius);
		return _result;
	}
	public Bitmap processVertical(int radius) {
		BlurProcess blur=new JavaBlurProcess(3);
		_result = blur.blur(_image, radius);
		return _result;
	}

	/**
	 * Returns the blurred image as a bitmap
	 * @return blurred image
	 */
	public Bitmap returnBlurredImage() {
		return _result;
	}

	/**
	 * Save the image into the file system
	 * @param path The path where to save the image
	 */
	public void saveIntoFile(String path) {
		try {
			FileOutputStream out = new FileOutputStream(path);
			_result.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the original image as a bitmap
	 * @return the original bitmap image
	 */
	public Bitmap getImage() {
		return this._image;
	}

	/**
	 * Process the image using a native library
	 */
	public Bitmap processNatively(int radius) {
		NativeBlurProcess blur = new NativeBlurProcess();
		_result = blur.blur(_image, radius);
		return _result;
	}

	public Bitmap processRadial(int radius) {
		BlurProcess blur = new JavaRadialBlurProcess(JavaRadialBlurProcess.TYPE_RadialBlur);
		_result = blur.blur(_image, radius);
		return _result;
	}

	public Bitmap processRadial(int radius,int centerX,int centerY) {
		BlurProcess blur = new JavaRadialBlurProcess(JavaRadialBlurProcess.TYPE_RadialBlur,centerX,centerY);
		_result = blur.blur(_image, radius);
		return _result;
	}

	public Bitmap processCircular(int radius) {
		BlurProcess blur = new JavaRadialBlurProcess(JavaRadialBlurProcess.TYPE_CircularBlur );
		_result = blur.blur(_image, radius);
		return _result;
	}

	/**
	 * Process the image using a native library
	 */
	public Bitmap processBoxNatively(int radius) {
		NativeBlurProcess blur = new NativeBlurProcess(2);
		_result = blur.blur(_image, radius);
		return _result;
	}

}
