package com.kaminari.cars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final String TITLE = "CARS!!!";

	private JFrame frame;
	private Thread thread;
	private Image road1;
	private Image road2;
	private Random random;

	private boolean running = false;
	
	private float speed = 0;
	private float topSpeed = 30.0f;
	private float acc = 3.3f;
	private int road = 0;
	private boolean wrongLane = false;
	private Rectangle player;
	private int score;
	
	private int trafficY = 0;
	private Rectangle traffic;
	int lane = 0;
	private boolean passed = true;

	public Main() {
		frame = new JFrame(TITLE);

		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		try {
			road1 = ImageIO.read(Main.class.getResource("/road.png"));
			road2 = ImageIO.read(Main.class.getResource("/road.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP)
					speed += acc;
				else if(e.getKeyCode() == KeyEvent.VK_DOWN)
					speed -= acc;
				
				if(e.getKeyCode() == KeyEvent.VK_LEFT)
					wrongLane = true;
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
					wrongLane = false;
			}
		});
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000D / 60D;
		double delta = 0;

		int frames = 0;
		int updates = 0;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			while (delta >= 1) {
				update();
				updates++;
				delta--;
			}

			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			repaint();
			frames++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				frame.setTitle(TITLE + " | fps : " + frames + " | ups : "
						+ updates);
				frames = 0;
				updates = 0;
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.CYAN);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.BLACK);
		g.drawString("Speed : " + speed * 18f / 5f + " km/h", 1, 10);
		g.drawString("Score : " + score, getWidth() - 100, 10);
		
		g.drawImage(road1, getWidth() / 2 - 100, road, null);
		g.drawImage(road2, getWidth() / 2 - 100, road - getHeight() + 10, null);
		
		g.setColor(Color.RED);
		if(!wrongLane) {
			player = new Rectangle(getWidth() / 2 + 30, getHeight() - 100, 50, 50);
			g.fillRect(getWidth() / 2 + 30, getHeight() - 100, 50, 50);
		} else { 
			player = new Rectangle(getWidth() / 2 - 80, getHeight() - 100, 50, 50);
			g.fillRect(getWidth() / 2 - 80, getHeight() - 100, 50, 50);
		}
		
		g.setColor(Color.YELLOW);
		if(passed){
			passed = false;
			random = new Random();
			lane = random.nextInt(2);
		}
		
		if(lane == 0){
			traffic = new Rectangle(getWidth() / 2 + 30, trafficY, 50, 50);
			g.fillRect(getWidth() / 2 + 30, trafficY, 50, 50);
		}else if(lane == 1){
			traffic = new Rectangle(getWidth() / 2 - 80, trafficY, 50, 50);
			g.fillRect(getWidth() / 2 - 80, trafficY, 50, 50);
		}
		
	}

	private void update() {
		if(speed > topSpeed)
			speed = topSpeed;
		else if(speed < 0)
			speed = 0;
		
		road += speed;
		if(road >= getHeight())
			road -= getHeight();
		
		trafficY += speed;
		if(trafficY > getHeight()){
			score += speed * 10;
			passed = true;
			trafficY = 0;
		}
		
		if(player.intersects(traffic))
			stop();
	}

	public static void main(String[] args) {
		new Main().start();
	}

}
