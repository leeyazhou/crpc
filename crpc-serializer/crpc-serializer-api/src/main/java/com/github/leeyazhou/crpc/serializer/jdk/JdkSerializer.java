package com.github.leeyazhou.crpc.serializer.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.github.leeyazhou.crpc.serializer.Serializer;

public class JdkSerializer implements Serializer {

  @Override
  public Object decode(String className, byte[] bytes) throws Exception {
    ObjectInputStream objectIn = null;
    Object resultObject = null;
    try {
      objectIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
      resultObject = objectIn.readObject();
    } finally {
      if (null != objectIn) {
        objectIn.close();
      }
    }

    return resultObject;
  }

  @Override
  public byte[] encode(Object object) throws Exception {
    ByteArrayOutputStream byteArray = null;
    ObjectOutputStream output = null;
    try {
      byteArray = new ByteArrayOutputStream();
      output = new ObjectOutputStream(byteArray);
      output.writeObject(object);
      output.flush();
    } finally {
      if (null != output) {
        output.close();
      }
    }
    return byteArray.toByteArray();
  }
}
