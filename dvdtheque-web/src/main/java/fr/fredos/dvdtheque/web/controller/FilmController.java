package fr.fredos.dvdtheque.web.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@ManagedBean
@Controller
@Scope("singleton")
public class FilmController implements Serializable{

	private static final long serialVersionUID = 1L;

}
