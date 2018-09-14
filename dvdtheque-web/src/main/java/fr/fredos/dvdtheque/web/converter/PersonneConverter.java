package fr.fredos.dvdtheque.web.converter;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import fr.fredos.dvdtheque.dto.PersonneDto;
import fr.fredos.dvdtheque.web.bean.PersonneListBean;
@ManagedBean
@RequestScoped
public class PersonneConverter implements Converter {
	@ManagedProperty(value="#{personneListBean}")
	protected PersonneListBean personneListBean;
	public void setPersonneListBean(PersonneListBean personneListBean) {
		this.personneListBean = personneListBean;
	}

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if(value != null && value.trim().length() > 0) {
            try {
            	return personneListBean.getPersonneDtoByIdMap().get(Integer.parseInt(value));
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid personne id."));
            }
        }
        else {
            return null;
        }
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if(object!=null) {
			return ((PersonneDto)object).getId().toString();
		}
		else
			return null;
	}

}
