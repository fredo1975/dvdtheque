package fr.dvdtheque.console.transferable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import fr.fredos.dvdtheque.dao.model.object.Personne;

public class PersonneTransferable implements Transferable{
	public static final DataFlavor LIST_PERSONNE_DATA_FLAVOR = new DataFlavor(Personne.class, "java/Personne");
	private Personne p;
	
	public PersonneTransferable(Personne p) {
		super();
		this.p = p;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{LIST_PERSONNE_DATA_FLAVOR};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(LIST_PERSONNE_DATA_FLAVOR);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return p;
	}

}
