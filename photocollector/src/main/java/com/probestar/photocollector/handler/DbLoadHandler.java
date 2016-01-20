package com.probestar.photocollector.handler;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.photocollector.common.PhotoCollectorUtils;
import com.probestar.photocollector.model.PhotoDescription;
import com.probestar.psutils.PSTracer;

public class DbLoadHandler {
	private static PSTracer _tracer = PSTracer.getInstance(DbLoadHandler.class);

	private ConcurrentHashMap<Integer, PhotoDescription> _db;

	public DbLoadHandler() {
		_db = new ConcurrentHashMap<Integer, PhotoDescription>();
	}

	public ConcurrentHashMap<Integer, PhotoDescription> load() {
		File f = new File(PhotoCollectorConfig.getInstance().getDbPath());
		if (!f.exists())
			f.mkdirs();
		handle(PhotoCollectorConfig.getInstance().getDbPath());
		return _db;
	}

	private void handle(String path) {
		String[] files = new File(path).list();
		for (String file : files) {
			File f = new File(path + "/" + file);
			String fullPath = f.getAbsolutePath();
			if (f.isDirectory()) {
				handle(fullPath);
			} else {
				PhotoDescription desc = PhotoCollectorUtils.getPhotoDescription(fullPath);
				if (_db.containsKey(desc.hashCode())) {
					_tracer.error("Duplicate photo in db. \r\n" + desc.toString() + "\r\n"
							+ _db.get(desc.hashCode()).toString());
				} else {
					_db.put(desc.hashCode(), desc);
				}
			}
		}
	}
}
