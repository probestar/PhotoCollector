package com.probestar.photocollector.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.probestar.psutils.PSTracer;

public class PhotoCollectorConfig {
	private static PSTracer _tracer = PSTracer.getInstance(PhotoCollectorConfig.class);
	private static PhotoCollectorConfig _instance;

	private String _dbPath;
	private ArrayList<String> _searchPath;
	private boolean _delDuplicateFiles;
	private boolean _lastModifiedTime;

	static {
		try {
			_instance = new PhotoCollectorConfig();
			_tracer.info("Load Config from PhotoCollector.properties.\r\n" + _instance.toString());
		} catch (Throwable t) {
			_tracer.error("PhotoCollectorConfig.static error.", t);
		}
	}

	public static PhotoCollectorConfig getInstance() {
		return _instance;
	}

	private PhotoCollectorConfig() throws FileNotFoundException, IOException {
		load();
	}

	private void load() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		p.load(new FileInputStream("PhotoCollector.properties"));
		_dbPath = p.getProperty("DbPath").endsWith("/") ? p.getProperty("DbPath") : p.getProperty("DbPath") + "/";
		_searchPath = new ArrayList<String>();
		for (String s : p.getProperty("SearchPath").split(","))
			_searchPath.add(s);
		_delDuplicateFiles = Boolean.parseBoolean(p.getProperty("DelDuplicateFiles", "false"));
		_lastModifiedTime = Boolean.parseBoolean(p.getProperty("LastModifiedTime", "false"));
	}

	public String getDbPath() {
		return _dbPath;
	}

	public ArrayList<String> getSearchPath() {
		return _searchPath;
	}

	public boolean isDelDuplicateFiles() {
		return _delDuplicateFiles;
	}

	public boolean isLastModifiedTime() {
		return _lastModifiedTime;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("DbPath: ");
		s.append(_dbPath);
		s.append("\r\nSearchPath: ");
		s.append(_searchPath);
		return s.toString();
	}
}
