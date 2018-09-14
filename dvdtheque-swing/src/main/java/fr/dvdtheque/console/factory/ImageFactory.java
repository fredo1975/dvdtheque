package fr.dvdtheque.console.factory;

import java.awt.Dimension;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dvdtheque.console.vue.HeaderPanel;

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
	public static JPanel getHeaderPan(){
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
			// TODO Auto-generated catch block
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
