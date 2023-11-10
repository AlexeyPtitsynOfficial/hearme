package com.donearh.imageloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.donearh.hearme.AsyncTask;
import com.donearh.hearme.Base64;
import com.donearh.hearme.util.ExifUtil;
import com.donearh.hearme.util.IOStreamUtil;

public class ImageUploader {
	
	public static final int REQUEST_CAMERA = 0;
	public static final int SELECT_FROM_GALLERY = 1;
	private Context mContext;
	protected HashMap<String, String> mTokenData = new HashMap<String, String>();
	private Bitmap mImageBitmap;
	private String mImagePath=null;
	private ArrayList<ImageUploadTask> mImageUploadTasks;
	private UploadCompleteListener mUploadCompleteListener;
	private DecodeCompleteListener mDecodeCompleteListener;
	
	private String mUploadUrl;
	private String mTag;

	private int mCropWH = 0;
	
	public interface UploadCompleteListener{
		public void getUploadCompleteState(HashMap<String, String> url);
	}
	
	public interface DecodeCompleteListener{
		public void getDecodeCompleteState(Bitmap bm);
	}
	
	public void setUploadListener(UploadCompleteListener listener){
		mUploadCompleteListener = listener;
	}
	
	public void setDecodeListener(DecodeCompleteListener listener){
		mDecodeCompleteListener = listener;
	}
	
	public ImageUploader(Context context, String tag){
		
		mImageUploadTasks = new ArrayList<ImageUploader.ImageUploadTask>();
		mContext = context;
		mTag = tag;
	}
	
	public void setCropWH(int wh){
		mCropWH = wh;
	}
	
	public void uploadBitmap(Bitmap bm, String old_url, String url, String access_token){
		//mImageBitmap = ExifUtil.rotateBitmap(file_path, mImageBitmap);
		mImageBitmap = bm;
		if(mCropWH != 0)
			mImageBitmap = cropImg(mImageBitmap);
		
		
		mImageUploadTasks.add(new ImageUploadTask());
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("url", url);
		params.put("old_pic_url", old_url);
		params.put("access_token", access_token);
		params.put("pos", String.valueOf(mImageUploadTasks.size()-1));
  	  	mImageUploadTasks.get(mImageUploadTasks.size()-1).execute(params);
	}
	
	public void repeatUpload(String url, String access_token){
		mImageUploadTasks.add(new ImageUploadTask());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("url", url);
		params.put("access_token", access_token);
		params.put("pos", String.valueOf(mImageUploadTasks.size()-1));
  	  	mImageUploadTasks.get(mImageUploadTasks.size()-1).execute(params);
	}
	
	public void selectFile(Uri selectedImageUri){
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
    	if (Build.VERSION.SDK_INT < 19) {
        	  Cursor cursor = mContext.getContentResolver().query(selectedImageUri,
        	    filePathColumn, null, null, null);
        	  cursor.moveToFirst();

        	  int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        	  String picturePath = cursor.getString(columnIndex);
        	  cursor.close();

        	  decodeFile(picturePath);
    	}
    	else
    	{
    		//String file_path = getRealPathFromURI(selectedImageUri);
    		String file_path = getPath(mContext, selectedImageUri);
    	    // Check for the freshest data.
    		ParcelFileDescriptor parcelFileDescriptor;
            try {
                parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(selectedImageUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                decodeFile2(fileDescriptor, file_path);
                parcelFileDescriptor.close();
                

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    	}
	}
	
	public void requestCamera(){
		
	}
	
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }

	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };

	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {

	        // Return the remote address
	        if (isGooglePhotosUri(uri))
	            return uri.getLastPathSegment();

	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}
	
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	
	public int getOrientation(Uri selectedImage) {
	    int orientation = 0;
	    final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};      
	    final Cursor cursor = mContext.getContentResolver().query(selectedImage, projection, null, null, null);
	    if(cursor != null) {
	        final int orientationColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
	        if(cursor.moveToFirst()) {
	            orientation = cursor.isNull(orientationColumnIndex) ? 0 : cursor.getInt(orientationColumnIndex);
	        }
	        cursor.close();
	    }
	    return orientation;
	}
	
	public void decodeFile(String filePath) {
		 
        // Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
        	if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
        		break;
        	width_tmp /= 2;
        	height_tmp /= 2;
        	scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        mImageBitmap = BitmapFactory.decodeFile(filePath, o2);
        /*ExifInterface ei;
		try {
			ei = new ExifInterface(filePath);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            mImageBitmap = RotateBitmap(mImageBitmap, orientation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mImageBitmap = ExifUtil.rotateBitmap(filePath, mImageBitmap);
		}*/
       

        
        mImageBitmap = ExifUtil.rotateBitmap(filePath, mImageBitmap);
        //decodeFile(mImagePath);
        if(mCropWH != 0)
        	mImageBitmap = cropImg(mImageBitmap);
        mDecodeCompleteListener.getDecodeCompleteState(mImageBitmap);
	}
	
	public void decodeFile2(FileDescriptor fileDescriptor, String file_path) {
		 
        // Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
        	if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
        		break;
        	width_tmp /= 2;
        	height_tmp /= 2;
        	scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        mImageBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o2);
        
        ExifInterface ei;
		try {
			ei = new ExifInterface(file_path);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            mImageBitmap = RotateBitmap(mImageBitmap, orientation);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mImageBitmap = ExifUtil.rotateBitmap(file_path, mImageBitmap);
		}
        mImageBitmap = ExifUtil.rotateBitmap(file_path, mImageBitmap);
        
        if(mCropWH != 0)
        	mImageBitmap = cropImg(mImageBitmap);
        mDecodeCompleteListener.getDecodeCompleteState(mImageBitmap);
	}
	
	
	public static Bitmap RotateBitmap(Bitmap source, int orientation)
	{
	      Matrix matrix = new Matrix();
	      //matrix.postRotate(angle);
	      switch (orientation) {
	      case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
	          matrix.setScale(-1, 1);
	          break;
	      case ExifInterface.ORIENTATION_ROTATE_180:
	          matrix.setRotate(180);
	          break;
	      case ExifInterface.ORIENTATION_FLIP_VERTICAL:
	          matrix.setRotate(180);
	          matrix.postScale(-1, 1);
	          break;
	      case ExifInterface.ORIENTATION_TRANSPOSE:
	          matrix.setRotate(90);
	          matrix.postScale(-1, 1);
	          break;
	      case ExifInterface.ORIENTATION_ROTATE_90:
	          matrix.setRotate(90);
	          break;
	      case ExifInterface.ORIENTATION_TRANSVERSE:
	          matrix.setRotate(-90);
	          matrix.postScale(-1, 1);
	          break;
	      case ExifInterface.ORIENTATION_ROTATE_270:
	          matrix.setRotate(-90);
	          break;
	      default:
	      }
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	class ImageUploadTask extends AsyncTask<HashMap<String, String>, Void, HashMap<String, String>> 
	{
		 private String result = null;
		 private InputStream is = null;
		 private StringBuilder sb = null;
		 private JSONArray mJSONArray;
		 public Integer mPos;

		 @Override
		 protected void onPreExecute() 
		 {
		 }

		 @Override
		 protected HashMap<String, String> doInBackground(HashMap<String, String>... params) 
		 {
			 mPos = Integer.valueOf(params[0].get("pos"));
			 HashMap<String, String> result = new HashMap<String, String>();
			 if(!isCancelled())
			 {
				HashMap<String, String> param = new HashMap<String, String>();
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
			    mImageBitmap.compress(CompressFormat.JPEG, 100, bos);
			    byte[] data = bos.toByteArray();
			    String file = Base64.encodeBytes(data);
				
			    param.put("tag", mTag);
			    if(params[0].get("access_token") != null)
			    	param.put("access_token", params[0].get("access_token"));
			    param.put("file", file);
				 if(params[0].get("old_pic_url") != null)
				 param.put("old_pic_url", params[0].get("old_pic_url"));
				String response = "";
				try{
					URL url = new URL(params[0].get("url"));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(10000);
					conn.setConnectTimeout(15000);
					conn.setRequestMethod("POST");
					conn.setDoInput(true);
					conn.setDoOutput(true);
					
					
					OutputStream os = conn.getOutputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					writer.write(IOStreamUtil.getPostDataString(param));
					writer.flush();
					writer.close();
					os.close();

					int responseCode = conn.getResponseCode();
					
					if (responseCode == HttpsURLConnection.HTTP_OK) {
						
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		                StringBuilder sb = new StringBuilder();
		                String line;
		                while ((line = br.readLine()) != null) {
		                    sb.append(line+"\n");
		                }
		                br.close();
		                response= sb.toString();
		                
		                JSONObject json = null;
		    			try {
		    				json = new JSONObject(response);
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				Log.e("JSON Parser", "Error parsing data " + e.toString());
		    				//Log.e("Error pars", result);
		    			}
		    			result = mTokenData;
		    			JSONObject json_data=null;
						if(!json.isNull("file_path")){
							mJSONArray = json.getJSONArray("file_path");
							
							for(int i=0;i<mJSONArray.length();i++)
						    {
								json_data = mJSONArray.getJSONObject(i);
								result.put("image_url", json_data.getString("file_path"));
						    }
						}
		            }
		            else {
		            	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		                StringBuilder sb = new StringBuilder();
		                String line;
		                while ((line = br.readLine()) != null) {
		                    sb.append(line+"\n");
		                }
		                br.close();
		                response= sb.toString();
		                
		                JSONObject json = null;
		    			try {
		    				json = new JSONObject(response);
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				Log.e("JSON Parser", "Error parsing data " + e.toString());
		    				//Log.e("Error pars", result);
		    			}
		    			
		                if(!json.isNull("error")){
							result.put("error", "repeat");
							return result;
						}
		                throw new HttpException(responseCode+"");
		            }
					
					//is = conn.getInputStream();
				}catch(Exception e){
					if (is != null) {
			            try {
							is.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			        } 
				}
			 }
					
			 return result;
		 }

		 @Override
		 protected void onPostExecute(HashMap<String, String> result)
		 {
			 mUploadCompleteListener.getUploadCompleteState(result);
		 }
	}
	
	public void cancelUpload(int pos){
		mImageUploadTasks.get(pos).cancel(true);
		mImageUploadTasks.remove((int)pos);
		
		for(int i=0; i<mImageUploadTasks.size(); i++)
		{
			if(mImageUploadTasks.get(i).mPos > pos)
			{
				mImageUploadTasks.get(i).mPos -= 1;
			}
		}
	}
	
	public Bitmap cropImg(Bitmap bm){
		
		if(bm.getWidth() < mCropWH)
			mCropWH = bm.getWidth();
		if(bm.getHeight() < mCropWH)
			mCropWH = bm.getHeight();
		if (bm.getWidth() >= bm.getHeight()){

			bm = Bitmap.createBitmap(
					bm, 
					bm.getWidth()/2 - bm.getHeight()/2,
				     0,
				     mCropWH, 
				     mCropWH
			     );

			}else{

				bm = Bitmap.createBitmap(
						bm,
						0, 
			     bm.getHeight()/2 - bm.getWidth()/2,
			     mCropWH,
			     mCropWH
			     );
			}
	    
		return bm;
	}
}
