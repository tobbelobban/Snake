
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import java.awt.EventQueue;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.util.Random;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Game extends JPanel implements ActionListener {
  
  private class TAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      int keyCode = e.getKeyCode();
      switch(keyCode) {
        case KeyEvent.VK_UP:
          newdir = Direction.North;
          break;
        case KeyEvent.VK_RIGHT:
          newdir = Direction.East;
          break;
        case KeyEvent.VK_DOWN:
          newdir = Direction.South;
          break;
        case KeyEvent.VK_LEFT:
          newdir = Direction.West;
      }
    }
  }

  BufferedImage food, body;
  public Timer timer = new Timer(50, this);
  public int foodx, foody;
  public Random rand = new Random();
  public boolean[][] grid;
  public static final int SPEED = 1;
  public BodyElement head, tail;
  public Direction dir = Direction.East;
  public Direction newdir = dir;

  public Game() {
    try {
      food = ImageIO.read(new File("food.png"));
      body = ImageIO.read(new File("body.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setPreferredSize(new Dimension(600, 600));
    setOpaque(true);
    setFocusable(true);
    setBackground(Color.black);
    addKeyListener(new TAdapter());

    foodx = 0;
    foody = 0;
    grid = new boolean[40][40];

    BodyElement e = new BodyElement(39, 15);
    head = e;
    tail = e;
    for(int i = 0; i < 30; i++) {
      e = new BodyElement(tail.x-SPEED,tail.y);
      grid[e.x][e.y] = true;
      tail.next = e;
      e.previous = tail;
      tail = e;
    }
    e.next = head;
    head.previous = e;
    timer.start();
  }

	@Override
	public void render(Graphics g) {
		super.paint(g);
      BodyElement temp = head;
      while(temp.next != head) {
        g.drawImage(body, temp.x*15, temp.y*15, null);
        temp = temp.next;
      }
    g.drawImage(food, foodx*15, foody*15, null);
    Toolkit.getDefaultToolkit().sync();
	}

  public void placefood() {
    while(true) {
      foodx = rand.nextInt(40);
      foody = rand.nextInt(40);
      if(!grid[foodx][foody]) {
       break;
      }
    }
  }

  public void tailcollision() {
    if(grid[head.x][head.y]) {
      System.exit(0);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(dir != newdir) {
      if( (dir == Direction.North && newdir != Direction.South) || (dir == Direction.South && newdir != Direction.North) || (dir == Direction.East && newdir != Direction.West) || (dir == Direction.West && newdir != Direction.East) ) {
        dir = newdir;
      }
    }
    move();
    wallcollision();
    tailcollision();
    if(OnFood()) {
      growSnake();
      placefood();
    }
    repaint();
  }

  public void move() {
      
    grid[tail.x][tail.y] = false;

    switch(dir) {
      case North:
      tail.x = head.x;
      tail.y = head.y - SPEED;
      break;
      case East:
      tail.x = head.x + SPEED;
      tail.y = head.y;
      break;
      case South:
      tail.y = head.y + SPEED;
      tail.x = head.x;
      break;
      case West:
      tail.x = head.x - SPEED;
      tail.y = head.y;
    }
    grid[head.x][head.y] = true;
    head = tail;
    tail = tail.previous;
  }

  public void wallcollision() {
    if(head.x < 0) {
      head.x = 39;
    }
    if(head.x > 39) {
      head.x = 0;
    }
    if(head.y < 0) {
      head.y = 39;
    }
    if(head.y > 39) {
      head.y = 0;
    }
  }

  public boolean OnFood() {
    if(head.x == foodx && head.y == foody) {
      return true;
    }
    return false;
  }

  public void growSnake() {
    BodyElement newend = new BodyElement(tail.x, tail.y);
    tail.next = newend;
    head.previous = newend;
    newend.next = head;
    newend.previous = tail;
    tail = newend;
    switch(dir) {
      case North:
          tail.y += SPEED;
          break;
      case South:
          tail.y -= SPEED;
          break;
      case East:
          tail.x += SPEED;
          break;
      case West:
          tail.x -= SPEED;
    }
    if(tail.x < 0) {
      tail.x = 39;
    }
    if(tail.x > 39) {
      tail.x = 0;
    }
    if(tail.y < 0) {
      tail.y = 39;
    }
    if(tail.y > 39) {
      tail.y = 0;
    }
  }

	public static void main(String[] args) throws InterruptedException {
    JFrame f = new JFrame();
    f.getContentPane().add(new Game());
    f.setResizable(false);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.pack();
    f.setVisible(true);
  }
}
