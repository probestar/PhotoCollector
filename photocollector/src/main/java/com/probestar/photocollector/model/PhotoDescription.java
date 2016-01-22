package com.probestar.photocollector.model;

import com.probestar.psutils.PSDate;

public class PhotoDescription {
	private static String _dateFormat = "yyyyMMdd_HHmmss";

	private String _name;
	private long _size;
	private String _path;
	private long _modifiTime;

	private long _pictureTime;
	private String _make;

	public void setFileName(String name) {
		_name = name;
	}

	public String getFileName() {
		return _name;
	}

	public void setFileSize(long fileSize) {
		_size = fileSize;
	}

	public long getFileSize() {
		return _size;
	}

	public void setFilePath(String filePath) {
		if (filePath.endsWith("/"))
			_path = filePath;
		else
			_path = filePath + "/";
	}

	public String getFilePath() {
		return _path;
	}

	public void setFileModifyTime(long time) {
		_modifiTime = time;
	}

	public long getFileModifyTime() {
		return _modifiTime;
	}

	public void setPictureTime(long time) {
		if (time < 0)
			_pictureTime = 0;
		else
			_pictureTime = time;
	}

	public long getPictureTime() {
		return _pictureTime;
	}

	public String getFileFullName() {
		return _path + _name;
	}

	public void setMake(String make) {
		_make = make;
	}

	public String getMake() {
		return _make;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("FullName: ");
		s.append(getFileFullName());
		s.append("\r\n");
		s.append("FileName: ");
		s.append(getFileName());
		s.append("\r\n");
		s.append("FilePath: ");
		s.append(getFilePath());
		s.append("\r\n");
		s.append("FileSize: ");
		s.append(getFileSize());
		s.append("\r\n");
		s.append("LastModifyTime: ");
		s.append(PSDate.date2String(getFileModifyTime(), _dateFormat));
		s.append("\r\n");
		s.append("PictureTime: ");
		s.append(PSDate.date2String(getPictureTime(), _dateFormat));
		s.append("\r\n");
		s.append("Make: ");
		s.append(getMake());
		s.append("\r\n");
		return s.toString();
	}

	@Override
	public int hashCode() {
		return (int) (_size + _pictureTime);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PhotoDescription))
			return false;

		PhotoDescription desc = (PhotoDescription) obj;
		if (desc.hashCode() == hashCode())
			return true;
		else
			return false;
	}
}
