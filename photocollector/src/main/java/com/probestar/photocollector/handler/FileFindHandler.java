package com.probestar.photocollector.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.google.common.io.Files;
import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.psutils.PSFile;
import com.probestar.psutils.PSTracer;

public class FileFindHandler {
	private static PSTracer _tracer = PSTracer.getInstance(FileFindHandler.class);

	private ArrayList<String> _files;

	public FileFindHandler() {
		_files = new ArrayList<String>();
	}

	public ArrayList<String> load() {
		for (String path : PhotoCollectorConfig.getInstance().getSearchPath())
			handle(path);
		Collections.sort(_files);
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
				if (PSFile.isPicture(f) || PSFile.isMovie(f)
						|| Files.getFileExtension(f.getName()).equalsIgnoreCase("AAE")
						|| Files.getFileExtension(f.getName()).equalsIgnoreCase("PDF"))
					_files.add(fullPath);
				else {
					if (!f.getName().startsWith("."))
						_tracer.warn("Got non-pic files. " + f.getAbsolutePath());
				}
			}
		}
	}

}