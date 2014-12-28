package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

public class FilesController extends Controller {
	
	public static Result uploadImages(String locationProperty) {
		  MultipartFormData body = request().body().asMultipartFormData();
		  List<FilePart> images = body.getFiles();
		  
		  if (images.size() > 0) {
			for (FilePart image: images) {
			    File file = image.getFile();
			    try {
					FileUtils.moveFile(file, new File(Play.application().configuration().getString(locationProperty), file.getName()));
				} catch (IOException e) {
					Logger.debug("Problem moving file {}", image.getFilename());
					Logger.error(e.getMessage());
					return internalServerError("Problem uploading file");
				}
			    Logger.debug("Uploading image {} at location {} with type {}", image.getFilename(), file.getAbsolutePath(), image.getContentType());
			}
		    return ok();
		  } else {
			Logger.debug("fail");
		    return badRequest();   
		  }
	}
}
