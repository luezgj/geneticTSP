package lgimenez.genetictspapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.TSPInstance;

/**
 * @author luezg
 *
 */
public class Solucion{

	public boolean cruzado=false;
	public boolean isCruzado() {
		return cruzado;
	}

	public void setCruzado(boolean cruzado) {
		this.cruzado = cruzado;
	}

	public boolean isMutado() {
		return mutado;
	}

	public void setMutado(boolean mutado) {
		this.mutado = mutado;
	}

	public boolean mutado=false;
	
	public ArrayList<Integer> camino;
	public int costo=0;
	
	public Solucion() {
	}
	
	public Solucion(int dimension) {
		camino= new ArrayList<>(dimension);
		for(int i=0;i<dimension;i++) {
			camino.add(null);
		}
	}

	public static Solucion nuevaSolucionAleatoria(TSPInstance instanciaTSP) {
		Solucion sol= new Solucion(instanciaTSP.getDimension());
		ArrayList<Integer> auxiliar= new ArrayList<>(instanciaTSP.getDimension());
		
		for (int i = 2; i <= instanciaTSP.getDimension(); i++) {
			auxiliar.add(i);
		}
		
		int anterior=1;
		for (int i = 0; i < instanciaTSP.getDimension()-1; i++) {
			int proximo=auxiliar.remove(App.random.nextInt(auxiliar.size()));
			sol.camino.set(anterior-1,proximo);
			sol.costo+=instanciaTSP.getDistanceTable().getDistanceBetween(anterior, proximo);
			anterior=proximo;
		}
		sol.camino.set(anterior-1,1);// el último vuelve a empezar
				
		return sol;
	}
	
	//devuelve el costo del camino (lo calcula si no lo tiene calculado)
	public int getCostoTotal(DistanceTable costos){
		if (costo!=0) return costo;  //Si se calculó en la creación lo devuelvo
		int total=0;
		for (int i=1; i<=camino.size(); i++) {
			total+=costos.getDistanceBetween(i,camino.get(i-1));
		}
		return total;
	}
	
	//devuelve el costo que tenga guardado (no lo calcula)
	public int getCosto() {
		return costo;
	}
	
	public void setCosto(int costo) {
		this.costo=costo;
	}
	
	
	/**
	 * Devuelve cuál es el siguiente al nodo pasado como parametro
	 * @param nodo
	 * @return
	 */
	public Integer getSiguiente(int nodo) {
		return camino.get(nodo-1);
	}
	
	public void setSiguiente(int nodo, Integer siguiente, int costoViejo, int costoNuevo) {
		camino.set(nodo-1,siguiente);
		this.costo-=costoViejo;
		this.costo+=costoNuevo;
	}
	
	public int getDimension() {
		return camino.size();
	}
	
	public String printSolucion() {
		StringBuilder cadena= new StringBuilder(camino.size()*2+12);
		cadena.append("[");
		for (Integer nodo : camino) {
			cadena.append(" "+nodo);
		}
		cadena.append("] costo:"+costo);
		return cadena.toString();
	}
	
	public String printSolucionCamino() {
		StringBuilder cadena= new StringBuilder(camino.size()*2+12);
		cadena.append("[1");
		int nodo=1;
		nodo=camino.get(nodo-1);
		
		while(nodo!=1) {
			cadena.append(" "+nodo);
			nodo=camino.get(nodo-1);
		}
		
		cadena.append("] costo:"+costo);
		return cadena.toString();
	}
	
	public void imprimirArregloSolucion() {
		System.out.println("Es cruzado: "+cruzado);
		System.out.println("Es mutacion: "+mutado);
		for(int i=0;i<camino.size();i++) {
			System.out.println("["+(i+1)+"]"+camino.get(i));
		}
	}
	
	public boolean haveNulls() {
		for (Integer nodo : camino) {
			if(nodo==null) return true;
		}
		return false;
	}
	
	public boolean haveSelfConnections() {
		for (int i=0;i<camino.size();i++) {
			if(camino.get(i)!=null && camino.get(i).equals(i+1)) return true;
		}
		return false;
	}
}