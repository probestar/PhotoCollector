package com.probestar.photocollector.control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
	private ArrayList<String> _files;
	private AtomicInteger _currentSize;
	private AtomicInteger _copyCount;

	public PhotoCollectorDirector() {
		load();
	}

	public void start() {
		startWatcher();
		process();
	}

	private void load() {
		_tracer.info("Start to load db.");
		_db = (new DbLoadHandler()).load();
		_tracer.info("Start to load search path");
		_files = (new FileFindHandler()).load();
		_currentSize = new AtomicInteger(_files.size());
		_copyCount = new AtomicInteger(0);
		_tracer.info("Load finished. Db: " + _db.size() + "; SearchPath: " + _currentSize);
	}

	private void startWatcher() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (_currentSize.intValue() > 0) {
					_tracer.info(_currentSize + " items left.");
					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				_tracer.info("Process done. " + _copyCount.intValue() + " files copied.");
			}
		});
		t.start();
	}

	private void process() {
		for (int i = _files.size() - 1; i >= 0; i--) {
			try {
				PhotoDescription desc = PhotoCollectorUtils.getPhotoDescription(_files.get(i));
				_tracer.debug("Get PhotoDescription.\r\n" + desc.toString());
				import2Db(desc);
				_currentSize.decrementAndGet();
			} catch (Throwable t) {
				_tracer.error("PhotoCollectorDirector.process error.\r\n" + _files.get(i).toString(), t);
			}
		}
	}

	private void import2Db(PhotoDescription desc) {
		PhotoDescription descDb = _db.get(desc.hashCode());
		if (descDb != null) {
			_tracer.info("There is already file in the db.\r\nDbFile: " + descDb.getFileFullName() + "\r\nSearchFile: "
					+ desc.getFileFullName());
			delFile(desc.getFileFullName());
			return;
		}
		String toPath = PhotoCollectorConfig.getInstance().getDbPath()
				+ PSDate.date2String(desc.getPictureTime(), "yyyy") + "/"
				+ PSDate.date2String(desc.getPictureTime(), "MM") + "/"
				+ PSDate.date2String(desc.getPictureTime(), "dd") + "/" + desc.getFileName();
		_tracer.debug("Got to " + toPath);
		File from = new File(desc.getFileFullName());
		File to = new File(toPath);
		if (!to.getParentFile().exists())
			to.getParentFile().mkdirs();
		try {
			Files.copy(from, to);
			to.setLastModified(desc.getPictureTime());
			_db.put(desc.hashCode(), desc);
			_copyCount.incrementAndGet();
			_tracer.info("Copy files complete.\r\nFrom: %s\r\nTo: %s", from.getAbsolutePath(), to.getAbsoluteFile());
			delFile(from.getAbsolutePath());
		} catch (IOException e) {
			_tracer.error("PhotoCollectorDirector.import2Db copy file error.", e);
		}
	}

	private void delFile(String fullFileName) {
		if (PhotoCollectorConfig.getInstance().isDelAfterImport()) {
			try {
				java.nio.file.Files.delete(Paths.get(fullFileName));
				_tracer.error(fullFileName + " is deleted.");
			} catch (IOException e) {
				_tracer.error("DbLoadHandler.handle delete error. " + fullFileName, e);
			}
		}
	}
}