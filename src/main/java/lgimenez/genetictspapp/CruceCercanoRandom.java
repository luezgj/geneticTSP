package lgimenez.genetictspapp;


import java.util.List;

import org.moeaframework.problem.tsplib.TSPInstance;

public class CruceCercanoRandom implements MetodoCruce {

	@Override
	public Solucion cruzar(Solucion sol1, Solucion sol2, TSPInstance instanciaTSP) {
		
		/*
		System.out.println("Voy a cruzar esto");
		System.out.println(sol1.printSolucion());
		System.out.println("Costo: "+sol1.getCosto());
		
		System.out.println("Con esto");
		System.out.println(sol2.printSolucion());
		System.out.println("Costo: "+sol2.getCosto());
		*/
		
		Solucion hijo= new Solucion(instanciaTSP.getDimension());
		
		int actual= 1;   //podría ser random, pero haría que el buen fitness dependa de este random y no del camino
		int primero=actual;
		
		int elegido=-1;
		for(int i=1;i<instanciaTSP.getDimension();i++) {  //dimensión-1 veces
			//System.out.println("Eligiendo el siguiente de "+ actual);
			if (hijo.getSiguiente(sol1.getSiguiente(actual))!=null && hijo.getSiguiente(sol2.getSiguiente(actual))!=null) {
				//si ya se visitaron los dos, elijo uno random
				do {
					elegido=App.random.nextInt(instanciaTSP.getDimension())+1;
				}while(hijo.getSiguiente(elegido)!=null || elegido==actual);
				//System.out.println(" Elegido "+ elegido + " porque es el mas cercano de los que quedan");
			}else if (hijo.getSiguiente(sol2.getSiguiente(actual))!=null){
				//si ya se visitó el de sol2, uso el de sol1
				elegido=sol1.getSiguiente(actual);
				//System.out.println(" Elegido "+ elegido + " porque el otro ya estaba usado ("+sol2.getSiguiente(actual)+"),");
			}else if (hijo.getSiguiente(sol1.getSiguiente(actual))!=null){
				//si ya se visitó el de sol1, uso el de sol2
				elegido=sol2.getSiguiente(actual);
				//System.out.println(" Elegido "+ elegido + " porque el otro ya estaba usado ("+sol1.getSiguiente(actual)+").");
			}else {
				//si ninguno fue visitado me quedo con el más cercano
				elegido=(instanciaTSP.getDistanceTable().getDistanceBetween(actual, sol1.getSiguiente(actual))<instanciaTSP.getDistanceTable().getDistanceBetween(actual, sol2.getSiguiente(actual))? sol1.getSiguiente(actual):sol2.getSiguiente(actual));
				//System.out.println(" Entre  "+  sol1.getSiguiente(actual) + " y "+ sol2.getSiguiente(actual) +" elegí "+ elegido);
			}
			//System.out.println("Le pongo de siguiente al "+actual+" el "+elegido);
			hijo.setSiguiente(actual, elegido,0,(int) instanciaTSP.getDistanceTable().getDistanceBetween(actual, elegido));
			actual=hijo.getSiguiente(actual);
		}
		
		//System.out.println("Le pongo de siguiente al "+actual+" el primero "+primero);
		hijo.setSiguiente(actual, primero,0,(int) instanciaTSP.getDistanceTable().getDistanceBetween(actual, primero));
		
		//System.out.println("El hijo queda como");
		//System.out.println(hijo.printSolucion());
		
		//System.out.println("Costo: "+hijo.getCostoTotal(instanciaTSP.getDistanceTable()));
		
		return hijo;
	}
	
	@Override
	public String toString() {
		return "CruceCercanoRandom";
	}
	

}
