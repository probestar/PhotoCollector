package com.probestar.photocollector.control;

import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.psutils.PSTracer;

public class PhotoCollectorEntry {
	private static PSTracer _tracer = PSTracer.getInstance(PhotoCollectorEntry.class);

	public static void main(String[] args) {
		try {
			PhotoCollectorConfig.getInstance();
			PhotoCollectorDirector director = new PhotoCollectorDirector();
			director.start();
			System.in.read();
		} catch (Throwable t) {
			_tracer.error("PhotoCollectorEntry.main error.", t);
		}
	}

}
