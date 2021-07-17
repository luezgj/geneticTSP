package lgimenez.genetictspapp;

import org.moeaframework.problem.tsplib.TSPInstance;

public class MutacionInsercion implements MetodoMutacion {

	@Override
	public void mutar(Solucion sol, TSPInstance instancia) {
		//System.out.println("Antes de la mutación");
		//System.out.println(sol.printSolucionCamino());
		//System.out.println("Cuando entro a mutar. Camino:");
		//sol.imprimirArregloSolucion();
		
		int origen=App.random.nextInt(sol.getDimension()-1)+1; 
		int elegido=App.random.nextInt(sol.getDimension()-1)+1;
		
		while(origen==elegido || origen==sol.getSiguiente(elegido)) {elegido=App.random.nextInt(sol.getDimension()-1)+1;}
		
		int aPasar= sol.getSiguiente(elegido);
		
		/*System.out.println("Origen: "+origen+" Destino: "+elegido+" aPasar: "+aPasar);
		
		System.out.println(sol.getSiguiente(origen));
		System.out.println(sol.getSiguiente(aPasar));
		System.out.println(sol.getSiguiente(elegido));
		*/
		sol.setSiguiente(elegido, sol.getSiguiente(aPasar),(int) instancia.getDistanceTable().getDistanceBetween(elegido , sol.getSiguiente(elegido)),(int) instancia.getDistanceTable().getDistanceBetween(elegido ,sol.getSiguiente(aPasar) ) );
		sol.setSiguiente(aPasar, sol.getSiguiente(origen),(int) instancia.getDistanceTable().getDistanceBetween(aPasar , sol.getSiguiente(aPasar)),(int) instancia.getDistanceTable().getDistanceBetween(aPasar , sol.getSiguiente(origen)));
		sol.setSiguiente(origen, aPasar,(int) instancia.getDistanceTable().getDistanceBetween(origen , sol.getSiguiente(origen)),(int) instancia.getDistanceTable().getDistanceBetween(origen , aPasar));
		
		if(sol.haveNulls()) {
			System.out.println("Null generado por la mutación");
		}
		
		if(sol.haveSelfConnections()) {
			System.out.println("Self generado por la mutación");
		}
		//System.out.println("Despues de la mutacion");
		//System.out.println(sol.printSolucionCamino());
	}
	
	@Override
	public String toString() {
		return "MutacionInsercion";
	}
	

}
