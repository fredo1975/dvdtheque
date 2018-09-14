package fr.dvdtheque.console;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Barriere {
	protected final Log logger = LogFactory.getLog(Barriere.class);
	
	// état overt - fermé de la barrière
	private boolean ouverte;

	public boolean isOuverte() {
		return ouverte;
	}
	public void setOuverte(boolean ouverte) {
		this.ouverte = ouverte;
	}
	// fermeture barrière
	public synchronized void reset() {
		String methodName = "reset ";
		logger.debug(methodName + "start");
		ouverte = false;
		logger.debug(methodName + "end ouverte="+ouverte);
	}
	// ouverture barrière
	public synchronized void set() {
		String methodName = "set ";
		logger.debug(methodName + "start");
		if (!ouverte) {
			ouverte = true;
			this.notify();
		}
		logger.debug(methodName + "end ouverte="+ouverte);
	}
	// attente barrière
	public synchronized void waitOne() {
		String methodName = "waitOne ";
		logger.debug(methodName + "start");
		if (!ouverte) {
			try {
				this.wait();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex.toString());
			}
		}
		logger.debug(methodName + "end ");
	}
}
