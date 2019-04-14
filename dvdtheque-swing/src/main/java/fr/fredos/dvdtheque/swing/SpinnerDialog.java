package fr.fredos.dvdtheque.swing;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SpinnerDialog {
	private JDialog d;
	
	private JFrame f;

	public SpinnerDialog() {
		d = new JDialog();
		ClassLoader cldr = SpinnerDialog.class.getClassLoader();
	    URL url = cldr.getResource("img/ajax-loader.gif");
	    
	    ImageIcon loading = new ImageIcon(url);
	    
		JPanel p1 = new JPanel(new GridBagLayout());
		p1.add(new JLabel("loading ...", loading, JLabel.CENTER));
		d.getContentPane().add(p1);
		d.setSize(130, 100);
		d.setLocationRelativeTo(this.f);
		d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		d.setModal(true);
		d.getRootPane().setOpaque(false);
		d.getContentPane ().setBackground (new Color (0, 0, 0, 0));
		d.setUndecorated(true);
		d.setBackground (new Color (0, 0, 0, 0));
	}
	public void dispose() {
		d.dispose();
	}
	public void setVisible() {
		d.setVisible(true);
	}
	public void setFrame(JFrame f) {
		this.f = f;
	}
}
