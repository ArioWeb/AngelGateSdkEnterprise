
package com.angelsgate.sdk.AngelsGateUtils.jobmanager.persistence;


import android.util.Base64;


import com.angelsgate.sdk.AngelsGateUtils.jobmanager.EncryptionKeys;
import com.angelsgate.sdk.AngelsGateUtils.jobmanager.Job;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * An implementation of  that uses
 * Java Serialization.
 *
 * NOTE: This {@link JobSerializer} does not support encryption. Jobs will be serialized normally,
 * but any corresponding {@link Job} encryption keys will be ignored.
 */
public class JavaJobSerializer implements JobSerializer {

  public JavaJobSerializer() {}

  @Override
  public String serialize(Job job) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos  = new ObjectOutputStream(baos);
    oos.writeObject(job);

    return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
  }

  @Override
  public Job deserialize(EncryptionKeys keys, boolean encrypted, String serialized) throws IOException {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(serialized, Base64.NO_WRAP));
      ObjectInputStream ois  = new ObjectInputStream(bais);

      return (Job)ois.readObject();
    } catch (ClassNotFoundException e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);

      throw new IOException(e.getMessage() + "\n" + sw.toString());
    }
  }
}
