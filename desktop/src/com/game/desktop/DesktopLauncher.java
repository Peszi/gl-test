package com.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.game.GlMain;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 320; config.height = 240;
		final int offset = 32;
		config.x = Toolkit.getDefaultToolkit().getScreenSize().width - config.width - 80;
		config.y = Toolkit.getDefaultToolkit().getScreenSize().height - config.height - 80;
		config.backgroundFPS = 999;
		config.foregroundFPS = 999;
		config.vSyncEnabled = false;

//		config.width = Toolkit.getDefaultToolkit().getScreenSize().width;
//        config.height = Toolkit.getDefaultToolkit().getScreenSize().height;
//        config.fullscreen = true;
		new LwjglApplication(new GlMain(), config);
	}
}
