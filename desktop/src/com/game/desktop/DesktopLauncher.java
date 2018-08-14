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
		config.x = Toolkit.getDefaultToolkit().getScreenSize().width/2 - config.width;
		config.y = Toolkit.getDefaultToolkit().getScreenSize().height/2;
		new LwjglApplication(new GlMain(), config);
	}
}
