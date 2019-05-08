package fr.dvdtheque.console.image.utils;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.swing.views.AbstractViewListenerHolder;

public class ImageUtils {

	public static Icon getResizedIcon(final Film film) throws MalformedURLException {
		ImageIcon imageIcon = new ImageIcon(new URL(film.getPosterPath()));
		Image image = imageIcon.getImage(); // transform it
		Image newimg = image.getScaledInstance(AbstractViewListenerHolder.IMAGE_WIDTH_SIZE,
				AbstractViewListenerHolder.IMAGE_HEIGHT_SIZE, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
		return new ImageIcon(newimg); // transform it back
	}
}
