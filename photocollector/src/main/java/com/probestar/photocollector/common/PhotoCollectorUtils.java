package com.probestar.photocollector.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.common.io.Files;
import com.probestar.photocollector.model.PhotoDescription;
import com.probestar.psutils.PSDate;
import com.probestar.psutils.PSFile;
import com.probestar.psutils.PSTracer;

public class PhotoCollectorUtils {
	private static PSTracer _tracer = PSTracer.getInstance(PhotoCollectorUtils.class);
	private static ArrayList<String> _formatters;
	private static final long Time1990 = 946656000l;

	static {
		_formatters = new ArrayList<String>();
		_formatters.add("yyyy:MM:dd HH:mm:ss");
		_formatters.add("EEE MMM dd HH:mm:ss z yyyy");
		_formatters.add("yyyy-MM-dd HHmmss");
		_formatters.add("yyyyMMdd_HHmmss");
		_formatters.add("yyyyMMddHHmm");
		_formatters.add("yyyyMMdd");
		_formatters.add("MMddyy");
	}

	public static PhotoDescription getPhotoDescription(File f) {
		PhotoDescription desc = new PhotoDescription();
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
				if (pictrueTime != null)
					desc.setPictureTime(PSDate.string2Date(pictrueTime, _formatters).getTime());
				String make = map.get("Make");
				if (make == null)
					make = "";
				desc.setMake(make);
			} catch (ParseException e) {
				_tracer.error("PhotoCollectorUtils.getPhotoDescription parse time error. ", e);
				System.exit(0);
			} catch (Exception e) {
				_tracer.error("PhotoCollectorUtils.getPhotoDescription error. FileName: " + f.getAbsolutePath(), e);
				System.exit(0);
			}
		}

		if (!f.isDirectory()) {
			if (desc.getPictureTime() == 0) {
				String s = desc.getFileName();
				if (s.startsWith("IMG_") && s.length() >= 23) {
					s = s.substring(4, 19);
					try {
						Date d = PSDate.string2Date(s, _formatters);
						desc.setPictureTime(d.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

			if (desc.getPictureTime() == 0) {
				String s = desc.getFileName();
				if (s.startsWith("Screenshot_")) {
					s = s.substring(11, 30);
					try {
						Date d = PSDate.string2Date(s, "yyyy-MM-dd-HH-mm-ss");
						desc.setPictureTime(d.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

			if (desc.getPictureTime() == 0) {
				String s = desc.getFileName();
				if (s.startsWith("Photo_")) {
					s = s.substring(6, 12);
					try {
						Date d = PSDate.string2Date(s, _formatters);
						desc.setPictureTime(d.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

			if (desc.getPictureTime() == 0) {
				String s = desc.getFileName();
				if (s.startsWith("VID_")) {
					s = s.substring(4, 19);
					try {
						Date d = PSDate.string2Date(s, _formatters);
						desc.setPictureTime(d.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

			if (desc.getPictureTime() == 0) {
				String s = Files.getNameWithoutExtension(desc.getFileName());
				if (s.length() == 12) {
					try {
						Date d = PSDate.string2Date(s, "MMddyyHHmmss");
						desc.setPictureTime(d.getTime());
					} catch (ParseException e) {
					}
				} else {
					try {
						Date d = PSDate.string2Date(s, _formatters);
						desc.setPictureTime(d.getTime());
					} catch (ParseException e) {
					}
				}
			}

			long now = System.currentTimeMillis();
			if (desc.getPictureTime() < Time1990 || desc.getPictureTime() > now) {
				String s = desc.getFileName();
				if (s.length() >= 13) {
					s = s.substring(0, 13);
					try {
						Date d = new Date(Long.parseLong(s));
						if (d.getTime() < Time1990 || d.getTime() > now)
							desc.setPictureTime(0);
						else
							desc.setPictureTime(d.getTime());
					} catch (NumberFormatException ex) {
						desc.setPictureTime(0);
					}
				}
			}

			if (PhotoCollectorConfig.getInstance().isLastModifiedTime()) {
				if (desc.getPictureTime() == 0) {
					if (f.lastModified() > Time1990 && f.lastModified() < now)
						desc.setPictureTime(f.lastModified());
				}
			}

			String s = desc.getFileName();
			if (s.startsWith("image")) {
				s = s.substring(5, 13);
				Date d = null;
				try {
					d = PSDate.string2Date(s, _formatters);
					desc.setPictureTime(d.getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}
		return desc;
	}

	private static HashMap<String, String> readExif(File file) throws ImageProcessingException, IOException {
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
		is.close();
		return map;
	}
}
