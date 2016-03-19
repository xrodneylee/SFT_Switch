package sft.model;
/*
 * Copyright (c) 2005, Andowson Chang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *  3. Neither the name of the "Andowson Chang" nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.

 * This file creation date: 2005/01/31 18:33:36 
 * Reference:
 *      http://www.wakhok.ac.jp/~tatsuo/sen97/10shuu/UnZip.java.html
 */  
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import sft.view.ConfigPage;

public class UnZipBean {    
    public static final int EOF = -1;
    static final int BUFFER = 2048;

    private String zipFile;
    private String targetDirectory;
    private ZipFile zf;
    
    /** Constructor */
    public UnZipBean() {       
    }
    
    public UnZipBean(String zipFile, String targetDirectory) {
        this.zipFile = zipFile;
        this.targetDirectory = targetDirectory;
    }
    
    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }
    
    public String getZipFile() {
        return zipFile;
    }
    
    public void setTargetDirectory(String targetDirectory) {
    	this.targetDirectory = targetDirectory;
    }
    
    public String getTargetDirectory() {
        return targetDirectory;
    }
        
    public boolean unzip() {
    	boolean done = false;
    	if (zipFile != null) {    		
    		try {            
    			zf = new ZipFile(zipFile);
    			Enumeration enumeration = zf.entries();
    			while (enumeration.hasMoreElements()) {
    				ZipEntry target = (ZipEntry)enumeration.nextElement();
    				System.out.print(target.getName() + " .");
    				saveEntry(target);
    				System.out.println(". unpacked");
    			}
    			done = true;
    		}
    		catch (FileNotFoundException e){
    			ConfigPage.log(e.toString());
    			System.out.println("zipfile not found"+e.getMessage());
    		}
    		catch (ZipException e){
    			ConfigPage.log(e.toString());
    			System.out.println("zip error..."+e.getMessage());
    		}
    		catch (IOException e){
    			ConfigPage.log(e.toString());
    			System.out.println("IO error..."+e.getMessage());
    		} 
    		finally {
    			try {
    				zf.close();
    			} catch (IOException e) {
    				ConfigPage.log(e.toString());
    				System.out.println("IO error...Can't close zip file"+e.getMessage());
    			}
    		}
    	}
    	return done;
    }

    private void saveEntry(ZipEntry target)
                                   throws ZipException, IOException {
        try {
            File file = new File(targetDirectory + File.separator + target.getName());
            if (target.isDirectory()) {
                file.mkdirs();
            }
            else {
                InputStream is = zf.getInputStream(target);
                BufferedInputStream bis = new BufferedInputStream(is);
                File dir = new File(file.getParent());
                dir.mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                int c;
                byte[] data = new byte[BUFFER];
                while((c = bis.read(data, 0, BUFFER)) != EOF) {
                    bos.write(data, 0, c);
                }
                bos.flush();
                bos.close();
                fos.close();
            }
        }
        catch (ZipException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
    }

}
