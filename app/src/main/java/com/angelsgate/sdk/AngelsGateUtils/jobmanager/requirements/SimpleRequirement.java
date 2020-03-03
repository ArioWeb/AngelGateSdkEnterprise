package com.angelsgate.sdk.AngelsGateUtils.jobmanager.requirements;


import androidx.annotation.NonNull;

import com.angelsgate.sdk.AngelsGateUtils.jobmanager.Job;

public abstract class SimpleRequirement implements Requirement {

  @Override
  public boolean isPresent(@NonNull Job job) {
    return isPresent();
  }

  @Override
  public void onRetry(@NonNull Job job) {
  }

  public abstract boolean isPresent();
}
