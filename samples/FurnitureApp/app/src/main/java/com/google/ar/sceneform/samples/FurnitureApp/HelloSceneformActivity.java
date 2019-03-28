/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.FurnitureApp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
  private static final String TAG = HelloSceneformActivity.class.getSimpleName();
  private static final double MIN_OPENGL_VERSION = 3.0;

  private ArFragment arFragment;
  private ModelRenderable lampRenderable;
  private ModelRenderable chairRenderable;
  private ModelRenderable sofaRenderable;
  private ModelRenderable currentRenderable;
  boolean isTracking;
  boolean isHitting;

  @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // CompletableFuture requires api level 24
  // FutureReturnValueIgnored is not valid
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!checkIsSupportedDeviceOrFinish(this)) {
      return;
    }

    setContentView(R.layout.activity_ux);
    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

    // When you build a Renderable, Sceneform loads its resources in the background while returning
    // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
      CompletableFuture<Void> unable_to_load_lamp_renderable = ModelRenderable.builder()
              .setSource(this, Uri.parse("LampPost.sfb"))
              .build()
              .thenAccept(renderable -> lampRenderable = renderable)
              .exceptionally(
                      throwable -> {
                          Toast toast =
                                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                          return null;
                      });
      CompletableFuture<Void> unable_to_load_chair_renderable = ModelRenderable.builder()
              .setSource(this, Uri.parse("model.sfb"))
              .build()
              .thenAccept(renderable -> chairRenderable = renderable)
              .exceptionally(
                      throwable -> {
                          Toast toast =
                                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                          return null;
                      });
      CompletableFuture<Void> unable_to_load_sofa_renderable = ModelRenderable.builder()
              .setSource(this, Uri.parse("model2.sfb"))
              .build()
              .thenAccept(renderable -> sofaRenderable = renderable)
              .exceptionally(
                      throwable -> {
                          Toast toast =
                                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                          return null;
                      });

      arFragment.setOnTapArPlaneListener(
        (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
          if (currentRenderable == null) {
              Toast.makeText(getApplicationContext(), "Select a piece of furniture", Toast.LENGTH_LONG).show();
            return;
          }

          // Create the Anchor.
          Anchor anchor = hitResult.createAnchor();
          AnchorNode anchorNode = new AnchorNode(anchor);
          anchorNode.setParent(arFragment.getArSceneView().getScene());

          // Create the transformable andy and add it to the anchor.
          TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
          andy.setParent(anchorNode);
          andy.setRenderable(currentRenderable);
          andy.select();
        });

//      arFragment.getArSceneView().getScene().addOnUpdateListener(new frameTime(){
//          arFragment.onUpdate(frameTime);
//          onUpdate();
//      });
      initializeGallery();
  }

  /**
   * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
   * on this device.
   *
   * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
   *
   * <p>Finishes the activity if Sceneform can not run
   */
  public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
    if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
      Log.e(TAG, "Sceneform requires Android N or later");
      Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
      activity.finish();
      return false;
    }
    String openGlVersionString =
        ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo()
            .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
      Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
      Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
          .show();
      activity.finish();
      return false;
    }
    return true;
  }


  private void initializeGallery() {
      LinearLayout gallery = findViewById(R.id.gallery_layout);
      ImageView lamp = new ImageView(this);
      lamp.setImageResource(R.drawable.lamp);
      lamp.setContentDescription("lamp");
      lamp.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              currentRenderable = lampRenderable;
              Toast.makeText(getApplicationContext(), "Lamp selected", Toast.LENGTH_LONG).show();
          }
      });
      gallery.addView(lamp);
      ImageView chair = new ImageView(this);
      chair.setImageResource(R.drawable.chair);
      chair.setContentDescription("chair");
      chair.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              currentRenderable = chairRenderable;
              Toast.makeText(getApplicationContext(), "Chair selected", Toast.LENGTH_LONG).show();
          }
      });
      gallery.addView(chair);
      ImageView sofa = new ImageView(this);
      sofa.setImageResource(R.drawable.sofa);
      sofa.setContentDescription("sofa");
      sofa.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              currentRenderable = sofaRenderable;
              Toast.makeText(getApplicationContext(), "Sofa selected", Toast.LENGTH_LONG).show();
          }
      });
      gallery.addView(sofa);
  }


}
