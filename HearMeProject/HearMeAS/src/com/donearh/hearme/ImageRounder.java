package com.donearh.hearme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class ImageRounder {
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bmp,int roundPixelSize) { 
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPurgeable=true;
			Bitmap output = null;
	        output =  Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888); 
	        Canvas canvas2 = new Canvas(output); 
	        final Paint paint = new Paint(); 
	        final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight()); 
	        final RectF rectF = new RectF(rect); 
	        final float roundPx = roundPixelSize;
	        paint.setAntiAlias(true);
	        canvas2.drawRoundRect(rectF,roundPx,roundPx, paint);
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas2.drawBitmap(bmp, rect, rect, paint); 
	        return output; 
	    }
	
	public static Bitmap getRoundedCornerBitmap2(Bitmap bitmap, int pixels) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
	            .getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = pixels;
	
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	
	    return output;
	}
}
