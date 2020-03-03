
package com.angelsgate.sdk.AngelsGateUtils.jobmanager.requirements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * A requirement that is satisfied when a network connection is present.
 */
public class NetworkRequirement extends SimpleRequirement implements ContextDependent {

  private transient Context context;

  public NetworkRequirement(Context context) {
    this.context = context;
  }

  public NetworkRequirement() {}

  @Override
  public boolean isPresent() {
    ConnectivityManager cm      = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();

    return netInfo != null && netInfo.isConnected();
  }

  @Override
  public void setContext(Context context) {
    this.context = context;
  }
}
