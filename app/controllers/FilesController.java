package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

public class FilesController extends Controller {
	
	public static List<String> uploadImages(MultipartFormData body, String location) throws IOException {
		  List<FilePart> images = body.getFiles();
		  List<String> imageUrls = new ArrayList<>();
		  if (images.size() > 0) {
			for (FilePart image: images) {
			    File file = image.getFile();
			    try {
					FileUtils.moveFile(file, new File(location, file.getName()));
					imageUrls.add(location.concat("/").concat(file.getName()));
			    } catch (IOException e) {
					Logger.error("Problem moving file {}", image.getFilename());
					Logger.error(e.getMessage());
					throw new IOException("Problem occured while storing file");
				}
			    Logger.debug("Uploading image {} at location {} with type {}", image.getFilename(), file.getAbsolutePath(), image.getContentType());
			}
		    return imageUrls;
		  } else {
			Logger.error("No images among posted files");
		    throw new IllegalStateException("No images attached");  
		  }
	}
	
	public static List<String> removeImages(List<String> toRemove) throws IOException {
		List<String> removed = new ArrayList<String>();
		for (String remove: toRemove) {
			removeImage(remove);
			removed.add(remove);
		}
		return removed;
	}
	
	public static void removeImage(String remove) throws IOException {
		try {
			FileUtils.deleteDirectory(new File("/".concat(remove)));
			
		} catch (IOException e) {
			Logger.error(e.getMessage());
			throw new IOException("Problem while deleting image from filesystem");
		}
	}
}
