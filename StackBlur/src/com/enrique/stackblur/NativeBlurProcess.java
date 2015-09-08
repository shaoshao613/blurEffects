package com.enrique.stackblur;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * @see JavaBlurProcess
 * Blur using the NDK and native code.
 */
class NativeBlurProcess implements BlurProcess {
	private static native void functionToBlur(Bitmap bitmapOut, int radius, int threadCount, int threadIndex, int round);
	private int _type=TYPE_StackBlur;
	static {
		System.loadLibrary("blur");
	}
	NativeBlurProcess(){}
	NativeBlurProcess(int type){
		_type=type;
	}
	@Override
	public Bitmap blur(Bitmap original, float radius) {
		Bitmap bitmapOut = original.copy(Bitmap.Config.ARGB_8888, true);
		int cores = StackBlurManager.EXECUTOR_THREADS;
		ArrayList<NativeTask> horizontal = new ArrayList<NativeTask>(cores);
		ArrayList<NativeTask> vertical = new ArrayList<NativeTask>(cores);
		for (int i = 0; i < cores; i++) {
			horizontal.add(new NativeTask(bitmapOut, (int) radius, cores, i, 1,_type));
			vertical.add(new NativeTask(bitmapOut, (int) radius, cores, i, 2,_type));
		}
		try {
			StackBlurManager.EXECUTOR.invokeAll(horizontal);
		} catch (InterruptedException e) {
			return bitmapOut;
		}

		try {
			StackBlurManager.EXECUTOR.invokeAll(vertical);
		} catch (InterruptedException e) {
			return bitmapOut;
		}
		return bitmapOut;
	}

	private static class NativeTask implements Callable<Void> {
		private final Bitmap _bitmapOut;
		private final int _radius;
		private final int _totalCores;
		private final int _coreIndex;
		private final int _round;
		private final int _type;

		private static int[] boxesForGauss(int sigma, int n)  // standard deviation, number of boxes
		{
			int wIdeal = (int) (Math.sqrt((12 * sigma * sigma / n) + 1));  // Ideal averaging filter width
			int wl = (int) (Math.floor(wIdeal));
			if (wl % 2 == 0) wl--;
			int wu = wl + 2;

			int mIdeal = (12 * sigma * sigma - n * wl * wl - 4 * n * wl - 3 * n) / (-4 * wl - 4);
			int m = Math.round(mIdeal);
			// var sigmaActual = Math.sqrt( (m*wl*wl + (n-m)*wu*wu - n)/12 );

			int[] sizes = new int[n];
			for (int i = 0; i < n; i++)
				sizes[i] = (i < m ? wl : wu);
			return sizes;
		}

		public NativeTask(Bitmap bitmapOut, int radius, int totalCores, int coreIndex, int round,int type) {
			_bitmapOut = bitmapOut;
			_radius = radius;
			_totalCores = totalCores;
			_coreIndex = coreIndex;
			_round = round;
			_type = type;
		}

		@Override public Void call() throws Exception {
			if(_type==1){
				functionToBlur(_bitmapOut, _radius, _totalCores, _coreIndex, _round);
			}else{
				int[] bxs = boxesForGauss(_radius, 3);
				functionToBlur(_bitmapOut, (bxs[0] - 1) / 2, _totalCores, _coreIndex, _round);
				functionToBlur(_bitmapOut, (bxs[1] - 1) / 2, _totalCores, _coreIndex, _round);
				functionToBlur(_bitmapOut, (bxs[2] - 1) / 2, _totalCores, _coreIndex, _round);
			}
			return null;
		}

	}
}
