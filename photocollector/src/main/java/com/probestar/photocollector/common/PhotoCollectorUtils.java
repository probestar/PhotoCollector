package com.probestar.photocollector.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.probestar.photocollector.model.PhotoDescription;
import com.probestar.psutils.PSDate;
import com.probestar.psutils.PSFile;
import com.probestar.psutils.PSTracer;

public class PhotoCollectorUtils {
	private static PSTracer _tracer = PSTracer.getInstance(PhotoCollectorUtils.class);
	private static ArrayList<String> _formatters;

	static {
		_formatters = new ArrayList<String>();
		_formatters.add("yyyy:MM:dd HH:mm:ss");
		_formatters.add("EEE MMM dd HH:mm:ss z yyyy");
	}

	public static PhotoDescription getPhotoDescription(String fullName) {
		PhotoDescription desc = new PhotoDescription();
		File f = new File(fullName);
		desc.setFileName(f.getName());
		desc.setFileSize(f.length());
		desc.setFilePath(f.getParent());
		desc.setFileModifyTime(f.lastModified());
		if (PSFile.isPicture(f)) {
			try {
				HashMap<String, String> map = readExif(f);
				String pictrueTime = map.get("Date/Time Original");
				if (pictrueTime == null)
					pictrueTime = map.get("Date/Time");
				if (pictrueTime == null)
					desc.setPictureTime(f.lastModified());
				else
					desc.setPictureTime(PSDate.string2Date(pictrueTime, _formatters).getTime());
			} catch (ParseException e) {
				_tracer.error("PhotoCollectorUtils.getPhotoDescription parse time error. ", e);
				System.exit(0);
			} catch (Exception e) {
				_tracer.error("PhotoCollectorUtils.getPhotoDescription error. FileName: " + fullName, e);
				System.exit(0);
			}
		} else {
			desc.setPictureTime(f.lastModified());
		}
		return desc;
	}

	public static HashMap<String, String> readExif(File file) throws ImageProcessingException, IOException {
		HashMap<String, String> map = new HashMap<String, String>();
		InputStream is = null;
		is = new FileInputStream(file);

		Metadata metadata = ImageMetadataReader.readMetadata(is);
		Iterable<Directory> iterable = metadata.getDirectories();
		for (Iterator<Directory> iter = iterable.iterator(); iter.hasNext();) {
			Directory dr = iter.next();
			Collection<Tag> tags = dr.getTags();
			for (Tag tag : tags)
				map.put(tag.getTagName(), tag.getDescription());
		}
		_tracer.debug("Got Exif. " + file.getAbsolutePath() + "\r\n" + map.toString());
		return map;
	}
}
