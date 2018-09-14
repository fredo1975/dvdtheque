package fr.fredos.dvdtheque.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import fr.fredos.dvdtheque.enums.TypePersonne;

@FacesConverter("personneTypeConverter")
public class PersonneTypeConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if(value != null && value.trim().length() > 0) {
			return TypePersonne.valueOf(value);
        }
        else {
            return null;
        }
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if(object!=null) {
			return ((TypePersonne)object).name();
		}
		else
			return null;
	}

}
