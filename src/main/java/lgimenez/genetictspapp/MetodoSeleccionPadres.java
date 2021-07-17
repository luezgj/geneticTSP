package lgimenez.genetictspapp;

import java.util.ArrayList;
import java.util.List;

public interface MetodoSeleccionPadres {
	public abstract ArrayList<Solucion> seleccionar(List<Solucion> poblacion, int cantidadASeleccionar);
}
