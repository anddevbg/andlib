package com.anddevbg.image;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.anddevbg.WidthHeight;
import com.anddevbg.datatraffic.DataTrafficUtil;
import com.anddevbg.files.FileUtil;
import com.anddevbg.image.Params.ImageHolder;
import com.anddevbg.image.UrlImageProvider.UrlImageParams;
import com.anddevbg.log.LogWrapper;
import com.anddevbg.task.Task;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class UrlImageProvider extends BaseImageProvider<UrlImageParams> {

	private UrlImageParams mParams;

	@Override
	public String getCacheFolder() {
		return null;
	}

	@Override
	protected void prepare() {
		if (!DataTrafficUtil.hasInternetConnection(getContext())) {
			LogWrapper.d(this, "Internet connection is off.Cancelling download.");

			getCallback().onImageLoadError();
			cancel();

			return;
		}

		mParams = getParams();
	}

	@Override
	protected List<Task<Params>> getTasks() {
		List<Task<Params>> tasks = new ArrayList<Task<Params>>();
		tasks.add(new Task<Params>(0) {

			@Override
			public void execute(Params updatableObject) {
				final String id = mParams.getId();

				final int width = mParams.getWidthHeight().width;
				final int height = mParams.getWidthHeight().height;
				final String url = mParams.getUrl();
				final String cachePath = mParams.getCachePath();
				
				ImageHolder imageHolder = new ImageHolder();
				imageHolder.id = id;
				if (FileUtil.fileExist(cachePath)) {
					imageHolder.image = BitmapUtil.loadBitmapFromFile(cachePath, width, height);
				} else {
					imageHolder.image = BitmapUtil.loadBitmapFromURL(url, cachePath, width, height);
				}
				
				updatableObject.imageHolderMap.put(getId(), imageHolder);
			}

		});

		return tasks;
	}

	@Override
	protected void onImageLoaded(String id, Bitmap image) {
		if (image != null) {
			getCallback().onImageLoaded(id, image);
		} else {
			getCallback().onImageLoadError();
		}
	}

	public static class UrlImageParams {

		private final String mId;
		private final String mPath;
		private final String mUrl;
		private final WidthHeight mReqWidthHeight;

		public UrlImageParams(String id, String imagePath, String url, WidthHeight reqWidthHeight) {
			mId = id;
			mPath = imagePath;
			mUrl = url;
			mReqWidthHeight = reqWidthHeight;
		}

		public String getId() {
			return mId;
		}

		public String getUrl() {
			return mUrl;
		}

		public WidthHeight getWidthHeight() {
			return mReqWidthHeight;
		}

		public String getCachePath() {
			return mPath;
		}
	}
}
