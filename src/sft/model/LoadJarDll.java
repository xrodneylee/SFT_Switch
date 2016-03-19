package sft.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sft.view.Main;


public class LoadJarDll {
	public static void loadJarDll(String name) throws IOException {
		InputStream in = Main.class.getResourceAsStream(name);
		byte[] buffer = new byte[1024];
		int read = -1;
		File temp = new File(new File(System.getProperty("java.io.tmpdir")), name);
		FileOutputStream fos = new FileOutputStream(temp);

		while ((read = in.read(buffer)) != -1) {
			fos.write(buffer, 0, read);
		}
		fos.close();
		in.close();
		System.out.println(temp.getPath());
		System.load(temp.getAbsolutePath());
	}
}
