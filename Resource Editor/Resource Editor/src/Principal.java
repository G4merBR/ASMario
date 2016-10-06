import org.newdawn.slick.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JColorChooser;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Principal extends BasicGame
{
	public Principal(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		Input input = gc.getInput();
		float size=32;
		int xpos = (int) Math.floor(input.getMouseX()/(int)size)*(int)size;
		int ypos = (int) Math.floor(input.getMouseY()/(int)size)*(int)size;
		float red=1,green=1,blue=1;
		//ColorPicker a= new ColorPicker();

		g.setColor(new Color(red,green,blue));
		g.fillRect(xpos, ypos, size, size);
		g.setColor(new Color(1.0f,0.0f,0.0f));
		g.drawRect(xpos, ypos, size, size);
		//g.drawString("\nHowdy!", xpos, ypos);
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Principal("Resource Editor"));
			appgc.setDisplayMode(640, 640, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}