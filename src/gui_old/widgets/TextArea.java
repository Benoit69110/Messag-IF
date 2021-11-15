package gui_old.widgets;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import gui_old.widgets.style.CustomColors;
import gui_old.widgets.style.CustomFonts;

/**
 * TextArea customis�e.
 * @author aleconte
 * */
public class TextArea extends JTextArea {
	
	/**
	 * Application d'un nouveau style � la textarea.
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
