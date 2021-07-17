package lgimenez.genetictspapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.moeaframework.problem.tsplib.TSPInstance;

public class AlgoritmoGenetico {
	private MetodoCruce cruce;
	private MetodoMutacion mutacion;
	private MetodoSeleccionPadres seleccionPadres;
	private MetodoSeleccionPoblacion seleccionProximaGeneracion;
	private CondicionCorte corte;
	
	private int popSize=0; //tamaño de la población
	private float probCruce;
	private float probMutacion;
	
	private int frecuenciaMigracion=0;
	private int cantidadNuevasSoluciones=0;
	
	private TSPInstance instanciaTSP;
	long startTime;
	long tiempoEjecucion;
	
	//uso una referencia para poder cambiarla desde alguno de los métodos que existen en otras clases
	private Reference<List<Solucion>> poblacion;
	int generacion=0;
	
	List<Integer> mejoresCostos;
	List<Float> promediosCostos;
	
	public CondicionCorte getCorte() {
		return corte;
	}

	public void setCorte(CondicionCorte corte) {
		corte.setAlgoritmo(this);
		this.corte = corte;
	}
	
	public int getCantidadNuevasSoluciones() {
		return cantidadNuevasSoluciones;
	}

	public void setCantidadNuevasSoluciones(int cantidadNuevasSoluciones) {
		this.cantidadNuevasSoluciones = cantidadNuevasSoluciones;
	}

	public int getFrecuenciaMigracion() {
		return frecuenciaMigracion;
	}

	public void setFrecuenciaMigracion(int frecuenciaMigracion) {
		this.frecuenciaMigracion = frecuenciaMigracion;
	}

	public float getProbCruce() {
		return probCruce;
	}

	public void setProbCruce(float probCruce) {
		this.probCruce = probCruce;
	}

	public float getProbMutacion() {
		return probMutacion;
	}

	public void setProbMutacion(float probMutacion) {
		this.probMutacion = probMutacion;
	}
	
	public int getGeneracion() {
		return generacion;
	}

	public void setGeneracion(int generacion) {
		this.generacion = generacion;
	}

	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}

	public void setCruce(MetodoCruce cruce) {
		this.cruce = cruce;
	}

	public void setMutacion(MetodoMutacion mutacion) {
		this.mutacion = mutacion;
	}

	public void setSeleccionPadres(MetodoSeleccionPadres seleccionPadres) {
		this.seleccionPadres = seleccionPadres;
	}

	public void setSeleccionProximaGeneracion(MetodoSeleccionPoblacion seleccionProximaGeneracion) {
		this.seleccionProximaGeneracion = seleccionProximaGeneracion;
	}

	public void setInstanciaTSP(TSPInstance instanciaTSP) {
		this.instanciaTSP = instanciaTSP;
	}
	
	public Solucion getMejor() {
		if (poblacion!=null)
			return poblacion.get().get(0);
		else return null;
	}

	public boolean puedeEjecutar() {
		if ((cruce!=null || mutacion!=null) && seleccionPadres!=null && seleccionProximaGeneracion!=null && instanciaTSP!=null && popSize>0 && corte!=null) {
			return true;
		}
		return false;
	}
	
	public static Comparator<Solucion> ComparadorSoluciones = new Comparator<Solucion>() {
		public int compare(Solucion s1, Solucion s2) {
		   //descending order
		   return Integer.compare(s1.getCosto(), s2.getCosto());
	    }
	};
	
	public Solucion resolver() {	
		generacion=0;
		startTime = System.nanoTime();
		mejoresCostos=new ArrayList<Integer>(corte.getCantidadGeneraciones());
		promediosCostos=new ArrayList<Float>(corte.getCantidadGeneraciones());
		
		generarPoblacionInicial();
		//ordeno las soluciones descendentemente por costo
		poblacion.get().sort(ComparadorSoluciones);
		
		while (!corte.alcanzada()) {
			//System.out.println("Primer solucion");
			//poblacion.get().get(0).imprimirArregloSolucion();
			//A veces a la selección SUS le falta seleccionar un hijo (me pasa con Amax1.2, con el p43 con población de 200)
			
			ArrayList<Solucion> temporal=seleccionPadres.seleccionar(poblacion.get(),popSize);
			//System.out.println("seleccion realizada");
			/*for (Solucion solucion : poblacion) {
				System.out.println("Costo: " + solucion.getCostoTotal(instanciaTSP.getDistanceTable()));
			}*/
			
			for(int i=0;i<popSize;i=i+2) {
				Solucion sol1;
				Solucion sol2;
				if (App.random.nextFloat()<probCruce) {
					sol1=cruce.cruzar(temporal.get(i), temporal.get(i+1), instanciaTSP);
					sol1.setCruzado(true);
				}else {
					sol1=temporal.get(i);
					sol1.setCruzado(false);
				}
				if (App.random.nextFloat()<probCruce) {
					sol2=cruce.cruzar(temporal.get(i+1), temporal.get(i), instanciaTSP);
					sol2.setCruzado(true);
				}else {
					sol2=temporal.get(i+1);
					sol2.setCruzado(false);
				}
				temporal.set(i, sol1);
				temporal.set(i+1, sol2);
			}
			/*
			System.out.println("Hijos");
			for (Solucion solucion : temporal) {
				System.out.println("Costo: " + solucion.getCostoTotal(instanciaTSP.getDistanceTable()));
			}
			*/
			
			if (mutacion!=null) {
				for(int i=0;i<popSize;i++) {
					if(App.random.nextFloat()<probMutacion) {
						mutacion.mutar(temporal.get(i),instanciaTSP);
						temporal.get(i).setMutado(true);
					}else {
						temporal.get(i).setMutado(false);
					}
				}
			}
			
			//Creo que este ordenar no hace falta, ya está ordenado de la generación anterior
			poblacion.get().sort(ComparadorSoluciones);
			seleccionProximaGeneracion.seleccionarPoblacion(poblacion,temporal);
			poblacion.get().sort(ComparadorSoluciones);
			
			if (frecuenciaMigracion>0 && cantidadNuevasSoluciones>0 && generacion%frecuenciaMigracion==0 && generacion!=0) {
				migrar();
			}
			poblacion.get().sort(ComparadorSoluciones);
			System.out.println("Generacion :" +generacion+". Mejor: "+poblacion.get().get(0).getCosto());
			
			mejoresCostos.add(poblacion.get().get(0).getCosto());
			promediosCostos.add(getPromedioCostos());
			
			generacion++;
		}
		
		tiempoEjecucion= System.nanoTime()-startTime;
		//devuelvo la mejor solucion
		return poblacion.get().get(0);
	}
	
	private void migrar() {
		//System.out.println("Migración iniciada");
		for(int i=1;i<=cantidadNuevasSoluciones;i++) {
			//System.out.println("Meto nuevo");
			poblacion.get().set(poblacion.get().size()-i, Solucion.nuevaSolucionAleatoria(instanciaTSP));
		}
	}
	
	private void generarPoblacionInicial() {
		ArrayList<Solucion> poblacionArray= new ArrayList<>(popSize);
		for (int i = 0; i < popSize; i++) {
			poblacionArray.add(Solucion.nuevaSolucionAleatoria(instanciaTSP));
		}
		poblacion = new SoftReference<List<Solucion>>(poblacionArray);
	}
	
	private float getPromedioCostos() {
		int suma=0;
		for (Solucion individuo : poblacion.get()) {
			suma+=individuo.getCosto();
		}
		float promedio=((float)suma)/poblacion.get().size();
		return promedio;
	}
	
	public void printEjecucion(String filename) throws IOException {
		File archivo= new File(filename);
		if(archivo.getParentFile()!=null) {
			archivo.getParentFile().mkdirs();
		}
		archivo.createNewFile();
		FileWriter fw=new FileWriter(archivo);
		BufferedWriter writer = new BufferedWriter(fw);
		writer.write(instanciaTSP.getName());
		writer.newLine();
		writer.append("Tamaño de la población: " + popSize);
		writer.newLine();
		writer.append("Probabilidad cruce: "+probCruce+". Método de cruce: " + cruce.toString());
		writer.newLine();
		writer.append("Probabilidad mutación: "+probMutacion+". Método de mutación: "+mutacion.toString());
		writer.newLine();
		writer.append("Método de selección de padres: "+seleccionPadres.toString());
		writer.newLine();
		writer.append("Método de selección próxima generación: " + seleccionProximaGeneracion.toString());
		writer.newLine();
		writer.append("Condición de corte: "+corte.toString());
		writer.newLine();
		if(frecuenciaMigracion==0) {
			writer.append("Sin migración");
		}else {
			writer.append("Frecuencia migracion: "+ frecuenciaMigracion +". Nuevas soluciones: "+cantidadNuevasSoluciones);
		}
		writer.newLine();
		writer.append("Mejor costo: "+poblacion.get().get(0).getCosto());
		writer.newLine();
		writer.append("Mejor camino: "+poblacion.get().get(0).printSolucionCamino());
		writer.newLine();
		writer.append("Tiempo de ejecución: "+tiempoEjecucion  +" ns");
		writer.newLine();
		writer.append("Progreso");
		writer.newLine();
		writer.append("gen"+"\t"+"best"+"\t"+"avg");
		writer.newLine();
		for(int gen=0;gen<generacion;gen++) {
			writer.append(gen+"\t");
			writer.append(mejoresCostos.get(gen)+"\t");
			writer.append(promediosCostos.get(gen)+"\t");
			writer.newLine();
		}
		writer.close();
	}
	
	public void appendEjecucion(BufferedWriter writer) throws IOException{
		//Imprimo en un línea todos los datos para pasarlos a una planilla
		writer.append(popSize+" ");
		writer.append(probCruce+" ");
		writer.append(cruce.toString()+" ");
		writer.append(probMutacion+" ");
		writer.append(mutacion.toString()+" ");
		writer.append(seleccionPadres.toString()+" ");
		writer.append(seleccionProximaGeneracion.toString()+" ");
		writer.append(corte.toString()+" ");
		writer.append(frecuenciaMigracion+" ");
		writer.append(cantidadNuevasSoluciones+" ");
		writer.append(poblacion.get().get(0).getCosto()+" ");
		writer.append(tiempoEjecucion+" ");
		for(int gen=0;gen<generacion;gen++) {
			writer.append(mejoresCostos.get(gen)+" ");
		}
		writer.append("- ");
		for(int gen=0;gen<generacion;gen++) {
			writer.append(promediosCostos.get(gen)+" ");
		}
		writer.newLine();
		
	}
}
