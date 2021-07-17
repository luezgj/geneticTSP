package lgimenez.genetictspapp;

import org.moeaframework.problem.tsplib.TSPInstance;

public interface MetodoCruce{
	//genera una nueva solución tomando a las otras dos como base (no modifica ninguna de las soluciones anteriores)
	public Solucion cruzar(Solucion sol1, Solucion sol2, TSPInstance instanciaTSP);
}