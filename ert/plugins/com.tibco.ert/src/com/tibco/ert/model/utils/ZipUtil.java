package com.tibco.ert.model.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static void unzip(File zipFile, File destDir) throws IOException {
        ZipInputStream unzip = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry;
        try {
            while ((entry = unzip.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    File ufile = new File(destDir, entry.getName());
                    ufile.getParentFile().mkdirs();
                    FileOutputStream tout = new FileOutputStream(ufile);
                    try {
                        fill(unzip, tout);
                    } finally {
                        tout.close();
                    }
                    ufile.setLastModified(entry.getTime());
                }
            }
        } finally {
            unzip.close();
        }
    }

    public static InputStream getInputStreamFromZip(File zipFile, String fileName) throws IOException {
        ZipInputStream unzip = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry;

        while ((entry = unzip.getNextEntry()) != null) {
            if (!entry.isDirectory() && entry.getName().equals(fileName)) {
                return unzip;
            }
        }

        unzip.close();
        return null;
    }

	public static void zip(File dir, String zipFile) throws IOException {
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			addToZip(dir.listFiles(), dir, zipFile);
		}
	}

    public static void addToZip(File[] files, File root, String zipFile) throws IOException {
        ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(zipFile));
        try {
            for (File file : files) {
                addToZip(file, root, zipOutput);
            }
        } finally {
            zipOutput.close();
        }
    }

    private static void addToZip(File inputFile, File root, ZipOutputStream zipOutput) throws IOException {
        // This is the name as it's entered in the zip file
        String zipname = inputFile.getAbsolutePath();
        if (root != null) {
            zipname = zipname.substring(root.getAbsolutePath().length());
        }

        zipname = zipname.replace('\\', '/');// Windows replace
        
        // The zip entry can't begin with any slashes
        // but because of the logic we use (and the listFiles method), there
        // could be two.
        while (zipname.startsWith("/")) {
            zipname = zipname.substring(1, zipname.length());
        }

        // Loop over files
        if (inputFile.isDirectory()) {
            File[] files = inputFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    addToZip(file, root, zipOutput);
                }
            }
        } else {
            ZipEntry entry = new ZipEntry(zipname);
            entry.setSize(inputFile.length());
            entry.setTime(inputFile.lastModified());
            FileInputStream fis = new FileInputStream(inputFile);
            zipOutput.putNextEntry(entry);
            try {
                fill(fis, zipOutput);
            } finally {
                fis.close();
            }
            zipOutput.closeEntry();
        }
    }
    
    private static void fill(InputStream in, OutputStream out) throws IOException {
        byte[] data = new byte[1024];

        while (true) {
            int len = in.read(data);
            if (len < 0) {
                break;
            }
            out.write(data, 0, len);
        }

        out.flush();
    }
}
