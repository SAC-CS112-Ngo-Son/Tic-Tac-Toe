
//Son Ngo
//Final Project

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Random;

public class Tic_Tac_Toe extends JApplet implements ChangeListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	JSlider slider;
	JButton oButton, xButton;
	Board board;
	int lineThickness = 4;
	Color oColor = Color.BLUE, xColor = Color.RED;
	static final char BLANK = ' ', O = 'O', X = 'X';
	char position[] =
	{ // Board position (BLANK, O, or X)
			BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK, BLANK };
	int wins = 0, losses = 0, draws = 0; // game count by user

	/**
	 * Initialize the layout
	 */
	public void init()
	{
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new JLabel("Line Thickness:"));
		topPanel.add(slider = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 4));
		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.addChangeListener(this);
		topPanel.add(oButton = new JButton("O Color"));
		topPanel.add(xButton = new JButton("X Color"));
		oButton.addActionListener(this);
		xButton.addActionListener(this);
		add(topPanel, BorderLayout.NORTH);
		add(board = new Board(), BorderLayout.CENTER);
		setVisible(true);
	}

	/**
	 * Change line thickness. Take in the value from the slider; set it equal to
	 * lineThickness; repaint board
	 */
	public void stateChanged(ChangeEvent e)
	{
		lineThickness = slider.getValue();
		board.repaint();
	}

	/**
	 * Change color of O or X. When user click on oButton or xButton, show a
	 * color palette for user to pick from, then apply that color to the
	 * respective button
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == oButton)
		{
			Color newColor = JColorChooser.showDialog(this, "Choose a new color for O", oColor);
			if (newColor != null)
				oColor = newColor;
		} else if (e.getSource() == xButton)
		{
			Color newColor = JColorChooser.showDialog(this, "Choose a new color for X", xColor);
			if (newColor != null)
				xColor = newColor;
		}
		board.repaint();
	}

	/**
	 * Board is what actually plays and displays the game. Extend from JPanel
	 * and implement MouseListener interface
	 */
	private class Board extends JPanel implements MouseListener
	{
		private static final long serialVersionUID = 1L;
		Random random = new Random();
		int rows[][] =
		{
				{ 0, 2 },
				{ 3, 5 },
				{ 6, 8 },
				{ 0, 6 },
				{ 1, 7 },
				{ 2, 8 },
				{ 0, 8 },
				{ 2, 6 } };// Endpoints of the 8 rows in position[] (across,
							// down, diagonally)

		public Board()
		{
			addMouseListener(this);
		}

		/**
		 * Redraw the board
		 */
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			int w = super.getWidth();
			int h = super.getHeight();
			Graphics2D g2d = (Graphics2D) g;

			// Draw the grid
			g2d.setPaint(Color.WHITE);
			g2d.fill(new Rectangle2D.Double(0, 0, w, h));
			g2d.setPaint(Color.BLACK);
			g2d.setStroke(new BasicStroke(lineThickness));
			g2d.draw(new Line2D.Double(0, h / 3, w, h / 3));
			g2d.draw(new Line2D.Double(0, h * 2 / 3, w, h * 2 / 3));
			g2d.draw(new Line2D.Double(w / 3, 0, w / 3, h));
			g2d.draw(new Line2D.Double(w * 2 / 3, 0, w * 2 / 3, h));

			// Draw the Os and Xs
			for (int i = 0; i < 9; i++)
			{
				double xpos = (i % 3 + 0.5) * w / 3.0;
				double ypos = (i / 3 + 0.5) * h / 3.0;
				double xr = w / 8.0;
				double yr = h / 8.0;
				if (position[i] == O)
				{
					g2d.setPaint(oColor);
					g2d.draw(new Ellipse2D.Double(xpos - xr, ypos - yr, xr * 2, yr * 2));
				} else if (position[i] == X)
				{
					g2d.setPaint(xColor);
					g2d.draw(new Line2D.Double(xpos - xr, ypos - yr, xpos + xr, ypos + yr));
					g2d.draw(new Line2D.Double(xpos - xr, ypos + yr, xpos + xr, ypos - yr));
				}
			}
		}

		/**
		 * Draw an O where the mouse is clicked
		 */
		@Override
		public void mouseClicked(MouseEvent e)
		{
			int xpos = e.getX() * 3 / getWidth();
			int ypos = e.getY() * 3 / getHeight();
			int pos = xpos + 3 * ypos;
			if (pos >= 0 && pos < 9 && position[pos] == BLANK)
			{
				position[pos] = O;
				repaint();
				putX(); // computer plays
				repaint();
			}
		}

		/**
		 * Ignore other mouse events
		 */
		public void mousePressed(MouseEvent e)
		{
		}

		public void mouseReleased(MouseEvent e)
		{
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

		/**
		 * Computer plays X
		 */
		void putX()
		{
			// Check if game is over
			if (won(O))
				newGame(O);
			else if (isDraw())
				newGame(BLANK);

			// Play X, possibly ending the game
			else
			{
				nextMove();
				if (won(X))
					newGame(X);
				else if (isDraw())
					newGame(BLANK);
			}
		}

		/**
		 * @param player:
		 *            X or O
		 * @return true if player has won
		 */
		boolean won(char player)
		{
			for (int i = 0; i < 8; i++)
				if (testRow(player, rows[i][0], rows[i][1]))
					return true;
			return false;
		}

		/**
		 * @param player:
		 *            X or O
		 * @param a:
		 *            first position[a]
		 * @param b:
		 *            second position[b]
		 * @return true if player has won in the row from position[a] to
		 *         position[b]
		 */
		boolean testRow(char player, int a, int b)
		{
			return position[a] == player && position[b] == player && position[(a + b) / 2] == player;
		}

		/**
		 * Play X in the best spot
		 */
		void nextMove()
		{
			int r = findRow(X); // complete a row of X and win if possible
			if (r < 0)
				r = findRow(O); // or try to block O from winning
			if (r < 0)
			{ // otherwise move randomly
				do
					r = random.nextInt(9);
				while (position[r] != BLANK);
			}
			position[r] = X;
		}

		/**
		 * @param player:
		 *            X or O
		 * @return 0-8 for the position of a blank spot in a row if the other 2
		 *         spots are occupied by player, or -1 if no spot exists
		 */
		int findRow(char player)
		{
			for (int i = 0; i < 8; i++)
			{
				int result = find1Row(player, rows[i][0], rows[i][1]);
				if (result >= 0)
					return result;
			}
			return -1;
		}

		/**
		 * @param player:
		 *            X or O
		 * @param a:
		 *            first position[a]
		 * @param b:
		 *            second position[b]
		 * @return the index of the blank spot if 2 of 3 spots in the row from
		 *         position[a] to position[b] are occupied by player and the
		 *         third is blank, else return -1
		 */
		int find1Row(char player, int a, int b)
		{
			int c = (a + b) / 2; // middle spot
			if (position[a] == player && position[b] == player && position[c] == BLANK)
				return c;
			if (position[a] == player && position[c] == player && position[b] == BLANK)
				return b;
			if (position[b] == player && position[c] == player && position[a] == BLANK)
				return a;
			return -1;
		}

		/**
		 * @return true if all 9 spots are filled
		 */
		boolean isDraw()
		{
			for (int i = 0; i < 9; i++)
				if (position[i] == BLANK)
					return false;
			return true;
		}

		/**
		 * Start a new game
		 * @param winner: X or O
		 */
		void newGame(char winner)
		{
			repaint();

			// Announce result of last game. Ask user to play again.
			String result;
			if (winner == O)
			{
				wins++;
				result = "You Win!";
			} else if (winner == X)
			{
				losses++;
				result = "I Win!";
			} else
			{
				result = "Tie";
				draws++;
			}
			int choice = JOptionPane.showConfirmDialog(null, String
					.format("%s\nYou have %d wins, %d losses, and %d draws.\nPlay again?", result, wins, losses, draws),
					null, JOptionPane.YES_NO_OPTION);
			if (choice != JOptionPane.YES_OPTION)
			{
				System.exit(0);
			}

			// Clear the board to start a new game
			for (int j = 0; j < 9; ++j)
				position[j] = BLANK;

			// Computer starts first every other game
			if ((wins + losses + draws) % 2 == 1)
				nextMove();
		}
	}
}