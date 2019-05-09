package fr.fredos.dvdtheque.swing.views;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import fr.fredos.dvdtheque.swing.view.listener.FilmAddViewListener;
import fr.fredos.dvdtheque.swing.view.listener.FilmListViewListener;
import fr.fredos.dvdtheque.swing.view.listener.MenuViewListener;

public abstract class AbstractViewListenerHolder {
	// A list of listeners subscribed to this view
	protected final List<MenuViewListener> menuListeners;
	protected final List<FilmListViewListener> filmListListeners;
	protected final List<FilmAddViewListener> filmAddListeners;

	public final static int IMAGE_HEIGHT_SIZE = 500;
	public final static int IMAGE_WIDTH_SIZE = 350;

	public AbstractViewListenerHolder() {
		this.menuListeners = new ArrayList<>();
		this.filmListListeners = new ArrayList<>();
		this.filmAddListeners = new ArrayList<>();
	}

	/**
	 * Iterates through the subscribed listeners notifying each listener
	 * individually. Note: the {@literal '<T>' in private <T> void} is a Bounded
	 * Type Parameter.
	 *
	 * @param          <T> Any Reference Type (basically a class).
	 * 
	 * @param consumer A method with two parameters and no return, the 1st parameter
	 *                 is a ViewListner, the 2nd parameter is value of type T.
	 * 
	 * @param data     The value used as parameter for the second argument of the
	 *                 method described by the parameter consumer.
	 */
	protected <T> void notifyMenuListeners(final BiConsumer<MenuViewListener, T> consumer, final T data) {
		// Iterate through the list, notifying each listener, java8 style
		this.menuListeners.forEach((listener) -> {

			// Calls the funcion described by the object consumer.
			consumer.accept(listener, data);

			// When this method is called using ViewListener::onButtonClicked
			// the line: consumer.accept(listener,data); can be read as:
			// void accept(ViewListener listener, ActionEvent data) {
			// listener.onButtonClicked(data);
			// }

		});
	}

	protected <T> void notifyFilmListListeners(final BiConsumer<FilmListViewListener, T> consumer, final T data) {
		// Iterate through the list, notifying each listener, java8 style
		this.filmListListeners.forEach((listener) -> {

			// Calls the function described by the object consumer.
			consumer.accept(listener, data);

		});
	}

	protected <T> void notifyFilmAddListeners(final BiConsumer<FilmAddViewListener, T> consumer, final T data) {
		// Iterate through the list, notifying each listener, java8 style
		this.filmAddListeners.forEach((listener) -> {

			// Calls the function described by the object consumer.
			consumer.accept(listener, data);

		});
	}

	// Subscribe a MenuViewListener
	public void addMenuViewListener(final MenuViewListener listener) {
		menuListeners.add(listener);
	}

	// Subscribe a FilmListViewListener
	public void addFilmListViewListener(final FilmListViewListener listener) {
		filmListListeners.add(listener);
	}

	// Subscribe a FilmAddViewListener
	public void addFilmAddViewListener(final FilmAddViewListener listener) {
		filmAddListeners.add(listener);
	}
}
