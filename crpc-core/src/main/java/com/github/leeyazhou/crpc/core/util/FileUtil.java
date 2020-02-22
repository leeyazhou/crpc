/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.leeyazhou.crpc.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lee
 */
public class FileUtil {

  /**
   * create file
   * 
   * @param filePath filePath
   * @param content content
   * @throws IOException IOException
   */
  public static void createFile(String filePath, String content) throws IOException {
    FileWriter writer = null;
    try {
      writer = new FileWriter(filePath);
      writer.write(content);
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  /**
   * create multilevel folder
   * 
   * @param path path
   */
  public static void createFolder(String path) {
    File file = new File(path);
    file.mkdirs();
  }

  /**
   * move file
   * 
   * @param oldPath oldPath
   * @param newPath newPath
   */
  public static void moveFile(String oldPath, String newPath) {
    File fileOld = new File(oldPath);
    if (fileOld.exists()) {
      File fileNew = new File(newPath);
      fileOld.renameTo(fileNew);
    }
  }

  /**
   * delete file
   * 
   * @param path path
   */
  public static void deleteFile(String path) {
    File file = new File(path);
    file.deleteOnExit();
  }

  /**
   * get all file which in dir
   * 
   * @param dir dir
   * @param extension extension
   * @return List
   */
  public static List<File> getFiles(String dir, String... extension) {
    File file = new File(dir);
    if (!file.isDirectory()) {
      return null;
    }

    List<File> fileList = new ArrayList<File>();
    getFiles(file, fileList, extension);

    return fileList;
  }

  /**
   * 
   * @param file file
   * @param fileList fileList
   * @param extension extension
   */
  private static void getFiles(File file, List<File> fileList, String... extension) {
    File[] files = file.listFiles();
    for (int i = 0; i < files.length; i++) {
      if (files[i].isDirectory()) {
        getFiles(files[i], fileList, extension);
      } else if (files[i].isFile()) {

        String fileName = files[i].getName().toLowerCase();
        boolean isAdd = false;
        if (extension != null) {
          for (String ext : extension) {
            if (fileName.lastIndexOf(ext) == fileName.length() - ext.length()) {
              isAdd = true;
              break;
            }
          }
        }

        if (isAdd) {
          fileList.add(files[i]);
        }
      }
    }
  }

  /**
   * get all jar/war/ear which in dir
   * 
   * @param dirs dirList
   * @return List
   * @throws IOException IOException
   */
  public static List<String> getUniqueLibPath(String... dirs) throws IOException {

    List<String> jarList = new ArrayList<String>();
    List<String> fileNameList = new ArrayList<String>();

    for (String dir : dirs) {
      List<File> fileList = FileUtil.getFiles(dir, "rar", "jar", "war", "ear");
      if (fileList != null) {
        for (File file : fileList) {
          if (!fileNameList.contains(file.getName())) {
            jarList.add(file.getCanonicalPath());
            fileNameList.add(file.getName());
          }
        }
      }
    }

    return jarList;
  }

  /**
   * 按行读取文件
   * 
   * @param path path
   * @return String
   * @throws IOException IOException
   */
  public static String getContentByLines(String path) throws IOException {
    File file = new File(path);
    if (!file.exists()) {
      throw new IOException("file not exist:" + path);
    }
    BufferedReader reader = null;
    StringBuilder sbContent = new StringBuilder();
    try {
      reader = new BufferedReader(new FileReader(file));
      String line = null;
      while ((line = reader.readLine()) != null) {
        sbContent.append(line);
      }
    } catch (IOException err) {
      err.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException err) {
          err.printStackTrace();
        }
      }
    }

    return sbContent.toString();
  }
}