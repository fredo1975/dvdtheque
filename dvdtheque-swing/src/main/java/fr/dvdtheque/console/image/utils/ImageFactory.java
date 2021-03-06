package fr.dvdtheque.console.image.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageFactory {
	protected final Log logger = LogFactory.getLog(ImageFactory.class);
	private JPanel panel;
	private static Image image;
	public JPanel getPanel() {
		return panel;
	}
	public void setPanel(JPanel panel) {
		this.panel = panel;
	}
	public ImageFactory(JPanel panel) {
		super();
		this.panel = panel;
	}
	private static class HeaderPanel extends JPanel{
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
	public JPanel getHeaderPan(){
		JPanel jPanel = new HeaderPanel(image);
		jPanel.setPreferredSize(new Dimension(770,200));
		return jPanel;
	}
	public static JPanel getIconePan(){
		JPanel jPanel = new HeaderPanel(image);
		jPanel.setPreferredSize(new Dimension(16,16));
		return jPanel;
	}
	public ImageFactory(JPanel panel, String imagePath) {
		super();
		this.panel = panel;
		try {
			URL url = getURL(imagePath);
			logger.debug("url.getPath()="+url.getPath());
			image = panel.getToolkit().getImage(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public URL getURL(String file) throws MalformedURLException {
		java.net.URL imageURL = this.getClass().getResource(file);
		return imageURL;
	}
	
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	
}
