package fr.dvdtheque.console.handler;

import java.awt.datatransfer.DataFlavor;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import fr.fredos.dvdtheque.dao.model.object.Personne;

public class PersonneTransferHandler extends TransferHandler{

	private static final long serialVersionUID = -6349822068361266951L;

	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
		    return false;
		  }
		  return true;
	}

	@Override
	public boolean importData(TransferSupport support) {
		if (!support.isDrop()) {
            return false;
        }
        JList<Personne> list = (JList<Personne>)support.getComponent();
        DefaultListModel<Personne> listModel = (DefaultListModel<Personne>)list.getModel();
        
		return super.importData(support);
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

}
