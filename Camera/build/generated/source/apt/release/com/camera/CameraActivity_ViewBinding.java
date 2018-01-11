// Generated code from Butter Knife. Do not modify!
package com.camera;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CameraActivity_ViewBinding<T extends CameraActivity> implements Unbinder {
  protected T target;

  @UiThread
  public CameraActivity_ViewBinding(T target, View source) {
    this.target = target;

    target.mAutoFitTexture = Utils.findRequiredViewAsType(source, R.id.auto_fit_texture, "field 'mAutoFitTexture'", AutoFitTextureView.class);
    target.mBackImage = Utils.findRequiredViewAsType(source, R.id.camera_back, "field 'mBackImage'", ImageView.class);
    target.mCameraConversions = Utils.findRequiredViewAsType(source, R.id.camera_conversions, "field 'mCameraConversions'", ImageView.class);
    target.mTake = Utils.findRequiredViewAsType(source, R.id.camera_take, "field 'mTake'", ImageView.class);
    target.mSurfaceView = Utils.findRequiredViewAsType(source, R.id.surface_view, "field 'mSurfaceView'", SurfaceView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.mAutoFitTexture = null;
    target.mBackImage = null;
    target.mCameraConversions = null;
    target.mTake = null;
    target.mSurfaceView = null;

    this.target = null;
  }
}
