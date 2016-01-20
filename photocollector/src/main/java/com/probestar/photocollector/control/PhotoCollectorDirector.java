package com.probestar.photocollector.control;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.io.Files;
import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.photocollector.common.PhotoCollectorUtils;
import com.probestar.photocollector.handler.DbLoadHandler;
import com.probestar.photocollector.handler.FileFindHandler;
import com.probestar.photocollector.model.PhotoDescription;
import com.probestar.psutils.PSDate;
import com.probestar.psutils.PSTracer;

public class PhotoCollectorDirector {
	private static PSTracer _tracer = PSTracer.getInstance(PhotoCollectorDirector.class);

	private ConcurrentHashMap<Integer, PhotoDescription> _db;
	private ConcurrentLinkedQueue<String> _files;

	public PhotoCollectorDirector() {
	}

	public void start() {
		load();
		startWatcher();
		process();
	}

	private void load() {
		_tracer.info("Start to load db.");
		_db = (new DbLoadHandler()).load();
		_tracer.info("Start to load search path");
		_files = (new FileFindHandler()).load();
		_tracer.info("Load finished. Db: " + _db.size() + "; SearchPath: " + _files.size());
	}

	private void startWatcher() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (!_files.isEmpty()) {
					_tracer.info(_files.size() + " items left.");
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				_tracer.info("Process done.");
			}
		});
		t.start();
	}

	private void process() {
		while (!_files.isEmpty()) {
			PhotoDescription desc = PhotoCollectorUtils.getPhotoDescription(_files.poll());
			_tracer.info("Get PhotoDescription.\r\n" + desc.toString());
			import2Db(desc);
		}
	}

	private void import2Db(PhotoDescription desc) {
		PhotoDescription descDb = _db.get(desc.hashCode());
		if (descDb != null) {
			_tracer.info("There is already file in the db.\r\nDbFile: " + descDb.getFileFullName() + "\r\nSearchFile: "
					+ desc.getFileFullName());
			return;
		}
		// String toPath = PhotoCollectorConfig.getInstance().getDbPath()
		// + PSDate.date2String(desc.getPictureTime(), "yyyyMMdd") + "/"
		// + PSDate.date2String(desc.getPictureTime(), "yyyyMMdd_HHmmss") + "."
		// + Files.getFileExtension(desc.getFileName());
		String toPath = PhotoCollectorConfig.getInstance().getDbPath()
				+ PSDate.date2String(desc.getPictureTime(), "yyyyMMdd") + "/" + desc.getFileName();
		_tracer.debug("Got to " + toPath);
		File from = new File(desc.getFileFullName());
		File to = new File(toPath);
		if (!to.getParentFile().exists())
			to.getParentFile().mkdirs();
		try {
			Files.copy(from, to);
			to.setLastModified(desc.getPictureTime());
			_db.put(desc.hashCode(), desc);
		} catch (IOException e) {
			_tracer.error("PhotoCollectorDirector.import2Db copy file error.", e);
		}
	}
}