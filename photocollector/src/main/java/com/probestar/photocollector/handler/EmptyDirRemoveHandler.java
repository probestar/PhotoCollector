package com.probestar.photocollector.handler;

import java.io.File;

import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.psutils.PSTracer;

public class EmptyDirRemoveHandler {
	private static PSTracer _tracer = PSTracer.getInstance(EmptyDirRemoveHandler.class);

	public EmptyDirRemoveHandler() {
	}

	public void load() {
		for (String s : PhotoCollectorConfig.getInstance().getSearchPath())
			handle(s);
	}

	private void handle(String path) {
		String[] files = new File(path).list();
		for (String file : files) {
			File f = new File(path + "/" + file);
			String fullPath = f.getAbsolutePath();
			if (f.isDirectory()) {
				handle(fullPath);
				if (f.listFiles().length == 0) {
					f.delete();
					_tracer.info("Delete empty directory. " + f.getAbsolutePath());
				}
			}
		}
	}
}
