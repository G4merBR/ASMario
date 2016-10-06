import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JColorChooser;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Principal extends BasicGame
{
	java.awt.Color color;
	ArrayList<Tile> elements;
	boolean isErasing;
	int size;
	float scale;
	public class Tile{
		private int x,y,w,h;
		private Color c;
		
		public Tile(int x,int y,int w,int h,Color c){
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
			this.c=c;
		}
		
		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getW() {
			return w;
		}

		public void setW(int w) {
			this.w = w;
		}

		public int getH() {
			return h;
		}

		public void setH(int h) {
			this.h = h;
		}

		public Color getC() {
			return c;
		}

		public void setC(Color c) {
			this.c = c;
		}


		
	}
	public class customComparator implements Comparator<Tile>{

		@Override
		public int compare(Tile o1, Tile o2) {
		
			return o2.x-o1.x;
		}
		
	}
	public Principal(String gamename)
	{
		
		super(gamename);
		color=new java.awt.Color(1f,1f,1f);
		elements=new ArrayList<Tile>();
		isErasing=false;
		size=32;
		scale=100;
	}

	@Override
	public void init(GameContainer gc) throws SlickException {}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {}
	
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
	
		// Render your character and other stuff here! 
	

		Input input = gc.getInput();
		
		int xpos = (int)((Math.floor(input.getMouseX()/(int)size)*(int)size));
		int ypos = (int)((Math.floor(input.getMouseY()/(int)size)*(int)size));
		//ColorPicker a= new ColorPicker();
		
		if (input.isMousePressed(1)) {
			java.awt.Color newColor = JColorChooser.showDialog(null,"Selecione a Cor",color);
			if(newColor!=null)
				color=newColor;
		}
		Color brushcolor=new Color(color.getRed(),color.getGreen(),color.getBlue());
		
		//Desenha Mapa
		for(int i=0;i<elements.size();i++){
			Tile element=elements.get(i);
			g.setColor(element.getC());
			g.fillRect(element.getX(), element.getY(), element.getW(), element.getH());	
		}	
		//Liga/Desliga Borracha
		if (input.isKeyPressed(14)) 
			isErasing=!isErasing;

		
		//Gera Codigo
		if (input.isKeyPressed(Input.KEY_S)){
			String Final="";
			float scale=100;
			for(int i=0;i<elements.size();i++){
				Tile element=elements.get(i);
				Color cor=element.getC();
				if(i!=0)
					Final+="\n";
				else
					Final+="\n---->> Lista de Objetos <<---\n";
				Final+="GameObject "+element.getX()/scale+","+element.getY()/scale+","+element.getW()/scale+","+element.getY()/scale+",0";
					//","+cor.getRed()/255f+","+cor.getGreen()/255f+","+cor.getBlue()/255f;
			}
			System.out.print(Final);
		} 
			
		
		//Novo Tile
		if (input.isMouseButtonDown(0)) {
			if(isErasing){
				for(int i=0;i<elements.size();i++){
					Tile element=elements.get(i);
					if(element.getX()==xpos&&element.getY()==ypos)
						elements.remove(i);
				}
			}else{
			boolean existe=false;
			
			for(int i=0;i<elements.size();i++){
				Tile element=elements.get(i);
				if(element.getX()==xpos&&element.getY()==ypos){
					element.setC(brushcolor);
					existe=true;
					break;
				}
			}
			if(!existe){
				elements.add(new Tile(xpos, ypos, size, size,brushcolor));
				//elements.sort(new customComparator());
			}
			}

		}
		//Desenha Brush
		g.setColor(brushcolor);
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