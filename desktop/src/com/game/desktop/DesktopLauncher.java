package com.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.game.GlMain;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 720; config.height = 480;
		final int offset = 32;
		config.x = Toolkit.getDefaultToolkit().getScreenSize().width - config.width - 80;
		config.y = Toolkit.getDefaultToolkit().getScreenSize().height - config.height - 80;

//		config.width = Toolkit.getDefaultToolkit().getScreenSize().width;
//        config.height = Toolkit.getDefaultToolkit().getScreenSize().height;
//        config.fullscreen = true;
		new LwjglApplication(new GlMain(), config);
	}
}
