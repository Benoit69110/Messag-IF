package gui.widgets;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import gui.widgets.style.CustomColors;
import gui.widgets.style.CustomFonts;

/**
 * TextArea customisée.
 * @author aleconte
 * */
public class TextArea extends JTextArea {
	
	/**
	 * Application d'un nouveau style à la textarea.
	 * */
	public TextArea() {
		this.setForeground(CustomColors.TEXT_COLOR);
		this.setBackground(CustomColors.BACKGROUND_MEDIUM_RED);
		this.setFont(CustomFonts.LOG_FONT);
		this.setEditable(false);
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		this.setFocusable(false);
		this.setBorder(BorderFactory.createLineBorder(CustomColors.BORDER_COLOR));
	}
	
}
