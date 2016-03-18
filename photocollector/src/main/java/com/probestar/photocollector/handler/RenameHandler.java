package com.probestar.photocollector.handler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.google.common.io.Files;
import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.psutils.PSDate;
import com.probestar.psutils.PSTracer;

/**
 * Created by probestar on 16/2/4.
 */
public class RenameHandler {
	private static PSTracer _tracer = PSTracer.getInstance(RenameHandler.class);
	private static ArrayList<String> _formatters;
	private static File out = new File("E:\\out\\");

	static {
		_formatters = new ArrayList<String>();
		_formatters.add("yyyyMMdd_HHmmss");
		_formatters.add("yyyyMMdd_HHmm");
	}

	public void load() {
		File f = new File(PhotoCollectorConfig.getInstance().getDbPath());
		if (!f.exists()) {
			_tracer.info("DB directory is not exist.");
			return;
		}
		handle(f.getAbsolutePath());
		_tracer.info("Rename done.");
	}

	private void handle(String path) {
		String[] files = new File(path).list();
		for (String file : files) {
			File f = new File(path + "/" + file);
			if (f.isDirectory()) {
				handle(f.getAbsolutePath());
			} else {
				String name = Files.getNameWithoutExtension(f.getName());
				if (name.startsWith("IMG_") || name.startsWith("VID_"))
					name = name.substring(4);
				try {
					Date date = PSDate.string2Date(name, _formatters);
					if (Math.abs(f.lastModified() - date.getTime()) > 24 * 60 * 60 * 1000) {
						_tracer.info("Change %s's lastModified from %s to %s", f.getName(),
								PSDate.date2String(f.lastModified(), "yyyyMMdd_HHmmss"),
								PSDate.date2String(date, "yyyyMMdd_HHmmss"));
						f.setLastModified(date.getTime());
						try {
							Files.move(f, new File(out.getAbsolutePath() + "\\" + f.getName()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (ParseException e) {
				}
			}
		}
	}
}
