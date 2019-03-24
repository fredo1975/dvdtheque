package fr.fredos.dvdtheque.swing.views;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.JFrame;

import fr.fredos.dvdtheque.swing.view.listener.ViewListener;

public abstract class AbstractViewListenerHolder {
	// A list of listeners subscribed to this view
	protected final List<ViewListener> listeners;
	public AbstractViewListenerHolder() {
		this.listeners = new ArrayList<>();
	}

	/**
	 * Iterates through the subscribed listeners notifying each listener
	 * individually. Note: the {@literal '<T>' in private <T> void} is a Bounded
	 * Type Parameter.
	 *
	 * @param          <T> Any Reference Type (basically a class).
	 * 
	 * @param consumer A method with two parameters and no return, the 1st parameter
	 *                 is a ViewListner, the 2nd parametre is value of type T.
	 * 
	 * @param data     The value used as parameter for the second argument of the
	 *                 method described by the parameter consumer.
	 */
	protected <T> void notifyListeners(final BiConsumer<ViewListener, T> consumer, final T data) {
		// Iterate through the list, notifying each listener, java8 style
		listeners.forEach((listener) -> {

			// Calls the funcion described by the object consumer.
			consumer.accept(listener, data);

			// When this method is called using ViewListener::onButtonClicked
			// the line: consumer.accept(listener,data); can be read as:
			// void accept(ViewListener listener, ActionEvent data) {
			// listener.onButtonClicked(data);
			// }

		});
	}

	// Iterate through the list, notifying each listner individualy
	protected void notifyListenersOnButtonClicked() {
		for (final ViewListener listener : listeners) {
			// listener.onButtonClicked();
		}
	}

	// Iterate through the list, notifying each listner individualy
	protected void notifyListenersOnMenuQuitClicked() {
		for (final ViewListener listener : listeners) {
			// listener.onButtonClicked();
		}
	}

	// Subscribe a listener
	protected void addListener(final ViewListener listener) {
		listeners.add(listener);
	}

}
