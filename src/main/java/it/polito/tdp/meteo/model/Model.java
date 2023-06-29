package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	List<Citta> soluzioneTotale;
	private double costoSoluzioneTotale;
	private MeteoDAO meteoDAO;
	List<Citta> listaCitta;
	Map<Citta, List<Integer>> mappaUmidita;
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		this.meteoDAO=new MeteoDAO();
		this.soluzioneTotale=new LinkedList<Citta>();
		this.mappaUmidita=new HashMap<>();
		this.costoSoluzioneTotale=Integer.MAX_VALUE;
		this.listaCitta=this.meteoDAO.getLocalita();

	}

	// of course you can change the String output with what you think works best
	public float getUmiditaMedia(int mese, String localita) {
		List<Integer> listaUmidita=meteoDAO.getAllRilevamentiLocalitaMese(mese, localita);
		int somma=0;
		int n=0;
		for(Integer i:listaUmidita) {
			somma=somma+i;
			n++;
		}
		float media=somma/n;
		
		return media;
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		int livello=0;
		List<Citta> soluzioneParziale= new LinkedList<>();
		for(Citta c:listaCitta) {
			mappaUmidita.put(c, this.meteoDAO.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		ricorsione(livello,mese, soluzioneParziale);
		System.out.println(soluzioneTotale);
		String risultato="";
		
		for(Citta c: soluzioneTotale) {
		risultato+=" "+c.getNome();	
		}
		
		return risultato;
	}
	private void ricorsione(int livello,int mese, List<Citta> soluzioneParziale) {
		if(livello>=15) {
			double costoParziale=costo(mese,soluzioneParziale);
			if(costoParziale<costoSoluzioneTotale) {
				costoSoluzioneTotale=costoParziale;
				soluzioneTotale=new ArrayList<>(soluzioneParziale);
				return;
			}
		}
			for(Citta c:listaCitta) {
				if(filtro(soluzioneParziale, c)) {
					
					soluzioneParziale.add(c);
					ricorsione(livello+1, mese, soluzioneParziale);
					soluzioneParziale.remove(soluzioneParziale.size()-1);
				}
			}
			
			
			
	}
	private double costo(int mese,List<Citta> soluzioneParziale) {
		double costo=0;
		for(int i=1; i<soluzioneParziale.size(); i++) {
			if(!soluzioneParziale.get(i).equals(soluzioneParziale.get(i-1))){
				costo+=COST;
			}
		}
		for(int i=0; i<soluzioneParziale.size(); i++) {
			List<Integer> umidita=mappaUmidita.get(soluzioneParziale.get(i));
			costo+=umidita.get(i);
		}
		return costo;
	}
	private boolean filtro(List<Citta> soluzioneParziale, Citta citta) {
		int contatore=0;
		for(Citta c:soluzioneParziale) {
			if(c.equals(citta))
				contatore++;
		}
		if(contatore>=6)
			return false;
		if(soluzioneParziale.size()==0) 
			return true;
		if(soluzioneParziale.size()==1 || soluzioneParziale.size()==2) {
			if(soluzioneParziale.get(soluzioneParziale.size()-1).equals(citta)) {
				return true;
			}else {
				return false;
			}
			
		}
		if(!soluzioneParziale.get(soluzioneParziale.size()-1).equals(citta)) {
			if(soluzioneParziale.get(soluzioneParziale.size()-1).equals(soluzioneParziale.get(soluzioneParziale.size()-2))
			    && soluzioneParziale.get(soluzioneParziale.size()-2).equals(soluzioneParziale.get(soluzioneParziale.size()-3))) {
				return true;
			}else {
				return false;
			}
			
		}
		return true;
			
		
		
	}

}
