package com.probestar.photocollector.control;

import com.probestar.photocollector.common.PhotoCollectorConfig;
import com.probestar.photocollector.handler.RenameHandler;
import com.probestar.psutils.PSTracer;

public class PhotoCollectorEntry {
    private static PSTracer _tracer = PSTracer.getInstance(PhotoCollectorEntry.class);

    public static void main(String[] args) {
        try {
            PhotoCollectorConfig.getInstance();
            switch (args[0].toLowerCase()) {
                case "rename":
                    (new RenameHandler()).load();
                    break;
                default:
                    PhotoCollectorDirector director = new PhotoCollectorDirector();
                    director.start();
                    break;
            }
        } catch (Throwable t) {
            _tracer.error("PhotoCollectorEntry.main error.", t);
        }
    }

}
