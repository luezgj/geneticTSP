package lgimenez.genetictspapp;

import org.moeaframework.problem.tsplib.TSPInstance;

public interface MetodoMutacion{
	public void mutar(Solucion sol, TSPInstance instancia);
}