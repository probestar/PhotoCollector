package com.probestar.photocollector.handler;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.psutils.PSFile;
import com.probestar.psutils.PSTracer;

public class FileFindHandler {
	private static PSTracer _tracer = PSTracer.getInstance(FileFindHandler.class);

	private ConcurrentLinkedQueue<String> _files;

	public FileFindHandler() {
		_files = new ConcurrentLinkedQueue<String>();
	}

	public ConcurrentLinkedQueue<String> load() {
		handle(PhotoCollectorConfig.getInstance().getSearchPath());
		return _files;
	}

	private void handle(String path) {
		String[] files = new File(path).list();
		for (String file : files) {
			File f = new File(path + "/" + file);
			String fullPath = f.getAbsolutePath();
			if (f.isDirectory()) {
				handle(fullPath);
			} else {
				if (PSFile.isPicture(f) || PSFile.isMovie(f))
					_files.offer(fullPath);
				else {
					if (!f.getName().startsWith("."))
						_tracer.warn("Got non-pic files. " + f.getAbsolutePath());
				}
			}
		}
	}

}