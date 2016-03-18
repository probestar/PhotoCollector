package com.probestar.photocollector.handler;

import java.io.File;

import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.psutils.PSTracer;

public class Dir1970Handler {
	private static PSTracer _tracer = PSTracer.getInstance(Dir1970Handler.class);
	private static File _f = new File(PhotoCollectorConfig.getInstance().getDb1970Path());
	private static long FileSize = 1024 * 1024l;

	public void load() {
		for (String s : _f.list()) {
			File f = new File(_f.getAbsolutePath() + "/" + s);
			String name = f.getName();
			if ((name.endsWith(".jpg") || name.endsWith("JPG")) && f.length() < FileSize) {
				if ((name.startsWith("IMG_") && name.indexOf("_1024.") >= 0) || name.startsWith("p_large_")) {
					f.delete();
					_tracer.info("Del small files with prefix IMG_.\r\n" + f.getAbsolutePath());
				}
			}
		}
	}
}