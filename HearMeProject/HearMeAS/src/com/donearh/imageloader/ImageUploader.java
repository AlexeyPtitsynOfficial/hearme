package com.donearh.imageloader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.donearh.hearme.AsyncTask;
import com.donearh.hearme.Base64;
import com.donearh.hearme.ProfileActivity;
import com.donearh.hearme.Urls;
import com.donearh.hearme.util.ExifUtil;

public class ImageUploader {
	
	public static final int REQUEST_CAMERA = 0;
	public static final int SELECT_FROM_GALLERY = 1;
	private Context mContext;
	private String mUserID;
	private Bitmap mImageBitmap;
	private String mImagePath=null;
	private ArrayList<ImageUploadTask> mImageUploadTasks;
	private UploadCompleteListener mUploadCompleteListener;
	private DecodeCompleteListener mDecodeCompleteListener;
	
	private String mUploadUrl;
	private String mTag;
	
	public interface UploadCompleteListener{
		public void getUploadCompleteState(String url);
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
	
	public ImageUploader(Context context, String upload_url, String tag){
		
		mImageUploadTasks = new ArrayList<ImageUploader.ImageUploadTask>();
		mContext = context;
		mUploadUrl = upload_url;
		mTag = tag;
	}
	
	public void setUserId(String id){
		mUserID = id;
	}
	
	public void uploadBitmap(Bitmap bm){
		mImageBitmap = bm;
		mImageUploadTasks.add(new ImageUploadTask());
  	  	mImageUploadTasks.get(mImageUploadTasks.size()-1).execute(mImageUploadTasks.size()-1);
	}
	
	public void selectFile(Intent data){
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
    	Uri selectedImageUri = data.getData();
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
        mImageBitmap = ExifUtil.rotateBitmap(filePath, mImageBitmap);
        decodeFile(mImagePath);
        mDecodeCompleteListener.getDecodeCompleteState(mImageBitmap);
        mImageUploadTasks.add(new ImageUploadTask());
  	  	mImageUploadTasks.get(mImageUploadTasks.size()-1).execute(mImageUploadTasks.size()-1);
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
        mImageBitmap = ExifUtil.rotateBitmap(file_path, mImageBitmap);
        
        mDecodeCompleteListener.getDecodeCompleteState(mImageBitmap);
        mImageUploadTasks.add(new ImageUploadTask());
  	  	mImageUploadTasks.get(mImageUploadTasks.size()-1).execute(mImageUploadTasks.size()-1);
	}
	
	class ImageUploadTask extends AsyncTask<Integer, Void, String> 
	{
		 private String result = null;
		 private InputStream is = null;
		 private StringBuilder sb = null;
		 private JSONArray mJSONArray;
		 private String mUploadedImgUrl;
		 public Integer mPos;

		 @Override
		 protected void onPreExecute() 
		 {
		 }

		 @Override
		 protected String doInBackground(Integer... params) 
		 {
			 if(!isCancelled())
			 {
			  try 
			  {
				  mPos = params[0];
				   HttpClient httpClient = new DefaultHttpClient();
				   HttpContext localContext = new BasicHttpContext();
				   HttpPost httpPost = new HttpPost(mUploadUrl);
		
				   MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		
				   ByteArrayOutputStream bos = new ByteArrayOutputStream();
				   mImageBitmap.compress(CompressFormat.JPEG, 100, bos);
				   byte[] data = bos.toByteArray();
				   String file = Base64.encodeBytes(data);
		
				   entity.addPart("tag", new StringBody(mTag));
				   entity.addPart("user_id", new StringBody(mUserID));
				   entity.addPart("file", new StringBody(file));
				   entity.addPart("someOtherStringToSend", new StringBody("your string here"));
		
				   httpPost.setEntity(entity);
				   HttpResponse response = httpClient.execute(httpPost,localContext);
				   HttpEntity entity2 = response.getEntity();
				     is = entity2.getContent();
				   /*BufferedReader reader = new BufferedReader(new InputStreamReader(
				     response.getEntity().getContent(), "UTF-8"));
		
				   String sResponse = reader.readLine();*/
				  // return sResponse;
			  } catch (Exception e) {
			   // something went wrong. connection with the server error
				  return "no_connection";
			  }
			 
			 
			//convert response to string
				try
				{
				  BufferedReader reader = new BufferedReader(new InputStreamReader(is,HTTP.UTF_8));
				  sb = new StringBuilder();
				  sb.append(reader.readLine() + "\n");

				  String line="0";
				  while ((line = reader.readLine()) != null) 
				  {
				      sb.append(line + "\n");
				  }
				  is.close();
				  result=sb.toString();
				}
				catch(Exception e)
				{
				   Log.e("doni_error", "Error converting result "+e.toString());
				   return "error";
				}
				
				JSONObject json = null;
				try {
					json = new JSONObject(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("JSON Parser", "Error parsing data " + e.toString());
					Log.e("Error pars", result);
					return "error";
				}
				
				try 
				{
					JSONObject json_data=null;
					mJSONArray = json.getJSONArray("file_path");
					for(int i=0;i<mJSONArray.length();i++)
				    {
						json_data = mJSONArray.getJSONObject(i);
						mUploadedImgUrl = json_data.getString("file_path");
				    }
				}
				catch (JSONException e) {
		            e.printStackTrace();
		            Log.e("doni_error", "SQL ERROR RESULT = " + result);
		            return "error";
		        }
			 }
			  return mUploadedImgUrl;
		 }

		 @Override
		 protected void onPostExecute(String result)
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
}
