package lgimenez.genetictspapp;

import java.lang.ref.Reference;
import java.util.List;


//Este método de selección no es Steady State básico, permite elegir cúantos de los peores padres van a ser reemplazados con los mejores hijos
public class SteadyState implements MetodoSeleccionPoblacion {
	private int padresSobrevivientes;
	
	public SteadyState(int padresSobrevivientes) {
		super();
		this.padresSobrevivientes = padresSobrevivientes;
	}

	public int getPadresSobrevivientes() {
		return padresSobrevivientes;
	}

	public void setPadresSobrevivientes(int padresSobrevivientes) {
		this.padresSobrevivientes = padresSobrevivientes;
	}

	@Override
	public void seleccionarPoblacion(Reference<List<Solucion>> poblacionAnterior, List<Solucion> descendencia) {
		// TODO Auto-generated method stub
		int indicePadres=padresSobrevivientes;
		for(int i=0;i<poblacionAnterior.get().size()-padresSobrevivientes;i++) {
			poblacionAnterior.get().set(indicePadres, descendencia.get(i));
			indicePadres++;
		}
	}

	@Override
	public String toString() {
		return "SteadyState[padresSobrevivientes=" + padresSobrevivientes + "]";
	}

}
