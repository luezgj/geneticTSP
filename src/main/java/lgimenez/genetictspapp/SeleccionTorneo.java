package lgimenez.genetictspapp;

import java.util.ArrayList;
import java.util.List;

public class SeleccionTorneo implements MetodoSeleccionPadres{
	private int tamañoTorneo;
	
	public SeleccionTorneo(int tamañoTorneo) {
		super();
		this.tamañoTorneo = tamañoTorneo;
	}


	@Override
	public ArrayList<Solucion> seleccionar(List<Solucion> poblacion, int cantidadASeleccionar) {
		ArrayList<Solucion> seleccionados= new ArrayList<>(cantidadASeleccionar);
		
		for (int i = 0; i < cantidadASeleccionar; i++) {
			Solucion mejorParcial=new Solucion();
			mejorParcial.setCosto(Integer.MAX_VALUE);
			for (int j = 0; j < this.tamañoTorneo; j++) {
				Solucion participante=poblacion.get(App.random.nextInt(poblacion.size()));
				if(participante.getCosto()<mejorParcial.getCosto()) {
					mejorParcial=participante;
				}
			}
			seleccionados.add(mejorParcial);
		}
		
		return seleccionados;
		
	}


	@Override
	public String toString() {
		return "SeleccionTorneo[tamañoTorneo=" + tamañoTorneo + "]";
	}

}
