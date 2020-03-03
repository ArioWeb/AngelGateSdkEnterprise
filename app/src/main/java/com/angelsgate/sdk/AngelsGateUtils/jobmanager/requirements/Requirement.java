
package com.angelsgate.sdk.AngelsGateUtils.jobmanager.requirements;


import androidx.annotation.NonNull;


import com.angelsgate.sdk.AngelsGateUtils.jobmanager.Job;

import java.io.Serializable;




/**
 * A Requirement that must be satisfied before a Job can run.
 */
public interface Requirement extends Serializable {
  /**
   * @return true if the requirement is satisfied, false otherwise.
   */
  boolean isPresent(@NonNull Job job);

  void onRetry(@NonNull Job job);
}
