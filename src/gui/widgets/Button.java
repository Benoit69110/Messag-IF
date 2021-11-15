package gui.widgets;

import gui.widgets.style.CustomColors;
import gui.widgets.style.CustomFonts;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Bouton customisé.
 * @author aleconte
 * */
public class Button extends JButton {
	
	/**
	 * Application d'un nouveau style au bouton.
	 * @param text Texte du bouton.
	 * @param background Couleur de fond du bouton.
	 * */
	public Button(String text, Color background) {
		super(text);
		this.setBackground(background);
		this.setForeground(CustomColors.TEXT_COLOR);
		this.setFont(CustomFonts.BUTTON_FONT);
		this.setFocusable(false);
		this.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		this.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {
				setBorder(BorderFactory.createLoweredSoftBevelBorder());
			}
			public void mouseReleased(MouseEvent e) {
				setBorder(BorderFactory.createRaisedSoftBevelBorder());
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
	}
	
	/**
	 * Application d'un nouveau style au bouton.
	 * @param text Texte du bouton.
	 * */
	public Button(String text) {
		this(text, CustomColors.BACKGROUND_GREY);
	}
}
