package fr.fredos.dvdtheque.swing.views;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import fr.fredos.dvdtheque.swing.view.listener.FilmListViewListener;
import fr.fredos.dvdtheque.swing.view.listener.MenuViewListener;

public abstract class AbstractViewListenerHolder {
	// A list of listeners subscribed to this view
	protected final List<MenuViewListener> menuListeners;
	protected final List<FilmListViewListener> filmListListeners;

	public AbstractViewListenerHolder() {
		this.menuListeners = new ArrayList<>();
		this.filmListListeners = new ArrayList<>();
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

			// Calls the funcion described by the object consumer.
			consumer.accept(listener, data);

		});
	}

	// Iterate through the list, notifying each listener individualy
	protected void notifyListenersOnButtonClicked() {
		for (final MenuViewListener listener : menuListeners) {
			// listener.onButtonClicked();
		}
	}

	// Iterate through the list, notifying each listener individualy
	protected void notifyListenersOnMenuQuitClicked() {
		for (final MenuViewListener listener : menuListeners) {
			// listener.onButtonClicked();
		}
	}

	// Subscribe a MenuViewListener
	protected void addMenuViewListener(final MenuViewListener listener) {
		menuListeners.add(listener);
	}

	// Subscribe a FilmListViewListener
	protected void addFilmListViewListener(final FilmListViewListener listener) {
		filmListListeners.add(listener);
	}
}
