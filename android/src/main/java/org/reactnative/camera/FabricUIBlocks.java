package org.reactnative.camera;

import android.view.View;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.UIManagerHelper;

/**
 * Arch-agnostic replacement for Paper's UIManagerModule.addUIBlock + NativeViewHierarchyManager,
 * neither of which exists under the New Architecture (UIManagerModule.getNativeModule() returns
 * null under bridgeless, which crashed every camera module method). Runs the block on the UI
 * thread and resolves views through UIManagerHelper, which routes to the correct UIManager for
 * the tag on either architecture.
 */
public class FabricUIBlocks {

  public interface UIBlock {
    void execute(ViewResolver viewResolver);
  }

  public static class ViewResolver {
    private final ReactContext context;

    ViewResolver(ReactContext context) {
      this.context = context;
    }

    public View resolveView(int tag) {
      com.facebook.react.bridge.UIManager uiManager = UIManagerHelper.getUIManagerForReactTag(context, tag);
      if (uiManager == null) throw new IllegalViewOperationException("No UIManager found for tag " + tag);
      return uiManager.resolveView(tag);
    }
  }

  public static void addUIBlock(final ReactContext context, final UIBlock block) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        block.execute(new ViewResolver(context));
      }
    });
  }
}
