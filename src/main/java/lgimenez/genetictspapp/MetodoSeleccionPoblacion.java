package lgimenez.genetictspapp;

import java.lang.ref.Reference;
import java.util.List;

public interface MetodoSeleccionPoblacion {
	//deja en la poblacionAnterior la siguiente generación de acuerdo al criterio de selección
	public void seleccionarPoblacion(Reference<List<Solucion>> poblacionAnterior, List<Solucion> descendencia);
}
