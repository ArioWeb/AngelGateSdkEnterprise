
package com.angelsgate.sdk.AngelsGateUtils.jobmanager.requirements;

import android.content.Context;

/**
 * Any Job or Requirement that depends on {@link Context} can implement this
 * interface to receive a Context after being deserialized.
 */
public interface ContextDependent {
  public void setContext(Context context);
}
