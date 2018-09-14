package fr.dvdtheque.console.vue;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class HeaderPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Image header = null;
	public HeaderPanel(Image header){
		this.header=header;
		//setLayout(new FlowLayout(FlowLayout.CENTER));
		setOpaque( false );
	}
	public void paint( Graphics g ){
        if ( header != null )
            g.drawImage( header, 0, 0,getSize().width, getSize().height, this );
        super.paint( g );
    }
}
