package com.probestar.photocollector.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.probestar.psutils.PSTracer;

public class PhotoCollectorConfig {
	private static PSTracer _tracer = PSTracer.getInstance(PhotoCollectorConfig.class);
	private static PhotoCollectorConfig _instance;

	private String _dbPath;
	private String _searchPath;

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
		Properties p = new Properties();
		p.load(new FileInputStream("PhotoCollector.properties"));
		_dbPath = p.getProperty("DbPath").endsWith("/") ? p.getProperty("DbPath") : p.getProperty("DbPath") + "/";
		_searchPath = p.getProperty("SearchPath");
	}

	public String getDbPath() {
		return _dbPath;
	}

	public String getSearchPath() {
		return _searchPath;
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
