package sft.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyFolder {
	String source,destination;
	public CopyFolder(){
		
	}

	public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果資料夾不存在，則建立新資料夾
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/"
                            + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果有子文件夾
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("複製錯誤");
            e.printStackTrace();
        }
    }
	
	public void deleteSubFile(File file) {
		String[] files = file.list();
		for (int i = 0; i < files.length; i++) {
			File subfile = new File(file, files[i]);
			if (subfile.isDirectory()) {
				deleteSubFile(subfile);
			}
			System.out.println("File : " + subfile.getName() + " delete...");
			subfile.delete();
		}
		System.out.println("Directory : " + file.getName() + " delete...");
		file.delete();
	}
	
	public void copyCmd(String src, String dest) throws IOException, InterruptedException{
		//Process copy = Runtime.getRuntime().exec("xcopy "+src+" "+dest+" /I /Y /E");
		System.out.println("copy  "+src+" "+dest);
		Process copy = Runtime.getRuntime().exec("xcopy "+src+" "+dest);
		copy.waitFor();
	}
}
