package fr.dvdtheque.console.vue;

public interface IVue {

	// affiche la vue
	public void affiche();

	// cache la vue
	public void cache();

	// récupère le nom de l'action demandée par la vue
	public String getAction();

	// fixe le nom de la vue
	public void setNom(String nom);

	// récupère le nom de la vue
	public String getNom();

}
