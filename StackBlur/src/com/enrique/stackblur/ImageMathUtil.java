package com.enrique.stackblur;

import android.util.Log;

/**
 * Created by shaoting on 15/9/8.
 */
public class ImageMathUtil {

	public static void trans2Polor(int[] src, int w, int h,int r,int l,int[] srcOut){
		trans2Polor(src,w,h,srcOut,r,l,w/2,h/2);
	}
	public static void trans2Polor(int[] src, int w, int h,int[] srcOut,int r,int l,int center_x,int center_y){
		try {
			for(int j=0;j<=l-1;j++){

				double cita=2*Math.PI*(j+1)/(l*1.0);
				double sinCita=Math.sin(cita);
				double cosCita=Math.cos(cita);
				for(int i=0;i<=r-1;i++){
					int x= (int) (i*cosCita)+center_x;
					int y= center_y-(int) (i*sinCita);
					if(x<0||x>=w||y<0||y>=h){
						srcOut[i*l+j]=0xffffffff;
					}else
						srcOut[i*l+j]=src[y*w+x];
				}
			}
		} catch (Exception e) {
			Log.v("tinglog", e.toString());
			e.printStackTrace();
		}
	}
	public static void trans2Cartesian(int[] src, int w, int h,int[] srcOut,int r,int l){
		trans2Cartesian(src,w,h,srcOut,r,l,w/2,h/2);
	}

	public static void trans2Cartesian(int[] src, int w, int h,int[] srcOut,int r,int l,int center_x,int center_y){
		for(int x=0;x<=w-1;x++){
			for(int y=0;y<=h-1;y++){
				double cita=Math.PI/2;
				if(x!=center_x){
					cita=Math.atan2(center_y-y,x-center_x);
					if(cita<0)
						cita=2*Math.PI+cita;
				}else{
					if(y>center_y)
						cita=Math.PI*3/2;
				}
				int j= (int) (cita*l/2/Math.PI-1);
				int i= (int) Math.sqrt(Math.pow(x-center_x,2)+Math.pow(y-center_y,2));
				src[y*w+x]=srcOut[i*l+j];
			}
		}
	}

}
