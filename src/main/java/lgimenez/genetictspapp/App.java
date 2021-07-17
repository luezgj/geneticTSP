package lgimenez.genetictspapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import org.moeaframework.problem.tsplib.TSPInstance;


/**
 * JavaFX App
 */
public class App extends Application {
	final static Random random = new Random();
	
	final FileChooser fileChooser = new FileChooser();
	private StringProperty nombreArchivoPropery = new SimpleStringProperty();
	private StringProperty statusProperty = new SimpleStringProperty("App iniciada");
	
	@FXML public Label statusLabel;
	
	private AlgoritmoGenetico algoritmo;
	
	TSPInstance instanciaTSP;
	
	@FXML public TextField archivoSalida;
	
	@FXML public Label lblArchivoInstancia;
	
	@FXML public Spinner<Integer> spinnerPoblacion;
	
	@FXML public ComboBox<String> comboBoxCruce;
	
	@FXML public Spinner<Integer> tamañoTorneo;
	
	@FXML public Spinner<Integer> spinnerSobrevivientes;
	@FXML public Spinner<Double> spinnerProbCruce;
	@FXML public Spinner<Double> spinnerProbMutacion;
	
	@FXML public Spinner<Integer> spinnerFrecMigra;
	@FXML public Spinner<Integer> spinnerCantMigra;
	
	@FXML public Spinner<Integer> spinnerCorte;
	@FXML public ComboBox<String> comboBoxCorte;
	
    @Override
    public void start(Stage stage) {
    	
    	fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Asymmetric TSP", "*.atsp"),new FileChooser.ExtensionFilter("Symmetric TSP", "*.tsp"));
    	
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("VentanaPrincipal.fxml"));
        loader.setController(this);
        
        try {
        	AnchorPane pantalla=(AnchorPane) loader.load();
			Scene scene = new Scene(pantalla);
			lblArchivoInstancia.textProperty().bind(nombreArchivoPropery);
			statusLabel.textProperty().bind(statusProperty);
			
			stage.setTitle("Algoritmo genético TSP");
	        stage.setScene(scene);
	        stage.show();
	        
	        comboBoxCruce.getItems().addAll("Cruce cercano","Cruce cercano random");
	        comboBoxCorte.getItems().addAll("Generaciones", "Costo <=");
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    @FXML
    public void abrirInstancia(ActionEvent event) {
    	Node source=(Node) event.getSource();
    	File file = fileChooser.showOpenDialog(source.getScene().getWindow());
        if (file != null) {
        	statusProperty.set("Instancia elegida: "+file.getName());
            
            try {
				instanciaTSP= new TSPInstance(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            nombreArchivoPropery.setValue(file.getName());
        }
    }
    
    @FXML
    public void ejecutarGA(ActionEvent event) {
    	algoritmo= new AlgoritmoGenetico();
    	
    	if(spinnerPoblacion.getValue()!=null) {
    		algoritmo.setPopSize(spinnerPoblacion.getValue());
    	}else {
    		statusProperty.set("Debe ingresar un tamaño de población");
    	}
    	
    	if(instanciaTSP!=null) {
    		algoritmo.setInstanciaTSP(instanciaTSP);
    	}else {
    		statusProperty.set("Debe seleccionar una instancia del problema");
    	}
    	
    	algoritmo.setMutacion(new MutacionInsercion());
    	if(spinnerProbMutacion.getValue()!=null) {
    		algoritmo.setProbMutacion(spinnerProbMutacion.getValue().floatValue());
    	}else {
    		statusProperty.set("Debe ingresar una probabilidad de mutación");
    	}
    	
    	
    	MetodoCruce cruce=null;
    	MetodoSeleccionPadres seleccion=null;
    	CondicionCorte corte=null;
    	try {
    		if(tamañoTorneo.getValue()!=null) {
    			seleccion= new SeleccionTorneo(tamañoTorneo.getValue());
    		}else {
    			statusProperty.set("Debe ingresar un tamaño de torneo");
    		}
    		
    		
			if(comboBoxCruce.getValue().equals("Cruce cercano")) {
				cruce= new CruceCercano();
			}else if (comboBoxCruce.getValue().equals("Cruce cercano random")){
				cruce= new CruceCercanoRandom();
			}else{
				statusProperty.set("No hay un método de cruce seleccionado");
			}
			
			if(comboBoxCorte.getValue()==null) {
				statusProperty.set("No hay una condición de corte seleccionada");
			}
			else if(comboBoxCorte.getValue().equals("Generaciones")) {
				corte= new CorteGeneraciones();
				((CorteGeneraciones)corte).setNumeroGeneracion(spinnerCorte.getValue());
			}else if (comboBoxCorte.getValue().equals("Costo <=")){
				corte= new CorteCosto();
				((CorteCosto)corte).setCosto(spinnerCorte.getValue());
			}
		} catch ( IllegalArgumentException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	algoritmo.setCruce(cruce);
    	if(spinnerProbCruce.getValue()!=null) {
    		algoritmo.setProbCruce(spinnerProbCruce.getValue().floatValue());
    	}else {
    		statusProperty.set("Debe ingresar una probabilidad de cruce");
    	}
    	
    	algoritmo.setSeleccionPadres(seleccion);
    	
    	if(spinnerSobrevivientes.getValue().equals(0)) {
    		algoritmo.setSeleccionProximaGeneracion(new ReemplazoGeneracional());
    	}else if(spinnerSobrevivientes.getValue()!=null){
    		algoritmo.setSeleccionProximaGeneracion(new SteadyState(spinnerSobrevivientes.getValue()));
    	}else {
    		statusProperty.set("Debe ingresar una cantidad de padres sobrevivientes");
    	}
    	
    	algoritmo.setFrecuenciaMigracion(spinnerFrecMigra.getValue());
    	algoritmo.setCantidadNuevasSoluciones(spinnerCantMigra.getValue());
    	
    	algoritmo.setCorte(corte);
    	
    	if (algoritmo.puedeEjecutar()) {
    		statusProperty.setValue("Ejecutando");
    		algoritmo.resolver();
    		try {
    			statusProperty.setValue("Guardando archivo");
    			algoritmo.printEjecucion(archivoSalida.getText());
    			statusProperty.setValue("Ejecución finalizada");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				statusProperty.setValue("No se pudo guardar el archivo de salida");
				System.out.println("No se pudo guardar el archivo de salida");
			}
    	}else {
    		statusProperty.setValue("El algoritmo no se puede ejecutar");
    		System.out.println("El algoritmo no se puede ejecutar, por favor asegúrese de haber seleccionado todos los componentes necesarios");
    	}
    }
    
    private static void procesarUnaEjecucion(Scanner lector) {
    	try{
    		AlgoritmoGenetico algoritmo= new AlgoritmoGenetico();
    		System.out.println("Procesando una configuración del algoritmo");
    		File archivoInstancia= new File(lector.nextLine());
    		TSPInstance instanciaProblema= new TSPInstance(archivoInstancia);
    		algoritmo.setInstanciaTSP(instanciaProblema);
    		algoritmo.setPopSize(lector.nextInt());
    		lector.nextLine(); //Voy hasta la próxima línea
    		String metodoSeleccionPadres=lector.nextLine();
    		MetodoSeleccionPadres seleccion=null;
    		if(metodoSeleccionPadres.equals("SeleccionTorneo")) {
    			seleccion=new SeleccionTorneo(lector.nextInt());
    		}else {
    			System.err.println("Método de selección de padres inválido: "+metodoSeleccionPadres);
    		}
    		lector.nextLine(); //Voy hasta la próxima línea
    		algoritmo.setSeleccionPadres(seleccion);
    		
    		algoritmo.setProbCruce(lector.nextFloat());
    		lector.nextLine(); //Voy hasta la próxima línea
    		String metodoCruce=lector.nextLine();
    		MetodoCruce cruce=null;
    		if(metodoCruce.equals("CruceCercano")) {
    			cruce=new CruceCercano();
    		}else if(metodoCruce.equals("CruceCercanoRandom")){
    			cruce=new CruceCercanoRandom();
    		}else {
    			System.err.println("Método de cruce inválido: "+ metodoCruce);
    		}
    		algoritmo.setCruce(cruce);
    		
    		algoritmo.setMutacion(new MutacionInsercion());
    		algoritmo.setProbMutacion(lector.nextFloat());
    		lector.nextLine(); //Voy hasta la próxima línea
    		
    		//padres sobrevivientes
    		int padresSobrevivientes=lector.nextInt();
    		lector.nextLine(); //Voy hasta la próxima línea
    		if(padresSobrevivientes==0) {
    			algoritmo.setSeleccionProximaGeneracion(new ReemplazoGeneracional());
    		}else {
    			algoritmo.setSeleccionProximaGeneracion(new SteadyState(padresSobrevivientes));
    		}
    		
    		algoritmo.setFrecuenciaMigracion(lector.nextInt());
    		lector.nextLine(); //Voy hasta la próxima línea
    		algoritmo.setCantidadNuevasSoluciones(lector.nextInt());
    		lector.nextLine(); //Voy hasta la próxima línea
    		
    		String condicionCorte= lector.nextLine();
    		if(condicionCorte.equals("CorteGeneraciones")) {
    			CorteGeneraciones corte= new CorteGeneraciones();
    			corte.setNumeroGeneracion(lector.nextInt());
    			algoritmo.setCorte(corte);
    		}else if (condicionCorte.equals("CorteCosto")){
    			CorteCosto corte= new CorteCosto();
    			corte.setCosto(lector.nextInt());
    			algoritmo.setCorte(corte);
    		}else {
    			System.err.println("Condición de corte inválida: "+condicionCorte);
    		}
    		lector.nextLine(); //Voy hasta la próxima línea
    		
    		if (algoritmo.puedeEjecutar()) {
    			String carpetaSalida=lector.nextLine();
    			int cantidadExperimentos=lector.nextInt();
    			File archivoTodosExperimentos= new File(carpetaSalida+"\\todos.txt");
    			if(archivoTodosExperimentos.getParentFile()!=null) {
    				archivoTodosExperimentos.getParentFile().mkdirs();
    			}
    			archivoTodosExperimentos.createNewFile();
    			FileWriter fw=new FileWriter(archivoTodosExperimentos);
    			BufferedWriter writer = new BufferedWriter(fw);
    			for(int experimento=1;experimento<=cantidadExperimentos;experimento++) {
    				System.out.println("Experimento n°: "+experimento);
    				algoritmo.resolver();
            		try {
            			algoritmo.printEjecucion(carpetaSalida+"\\"+experimento+".txt");
            			algoritmo.appendEjecucion(writer);
        			} catch (IOException e) {
        				System.err.println("No se pudo guardar el archivo de salida");
        			}
    			}
    			writer.close();
        	}else {
        		System.err.println("El algoritmo seleccionado no se puede ejecutar");
        	}
    		
    	} catch(NullPointerException | IOException fileexcept) {
    		System.err.println("No se pudo abrir la instancia del problema indicada en la configuración del algoritmo");
    		fileexcept.printStackTrace();
    	} catch(NoSuchElementException dataexcept) {
    		System.err.println("Hubo un error leyendo los parámetros del algoritmo evolutivo. Asegúrese de proveer un archivo correcto");
    	}
    }
    
    private static void procesarArchivo(String rutaArchivo){
    	 File archivo = new File(rutaArchivo);
		try {
			Scanner lectorArchivo = new Scanner(archivo);
			 while (lectorArchivo.hasNextLine()) {
				 procesarUnaEjecucion(lectorArchivo);
		     }
			 lectorArchivo.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error. No se pudo abrir el archivo pasado como argumento");
		}
    }

    public static void main(String[] args) {
        if (args.length>0) { //Si se llama por línea de comandos ejecutar en modo script procesando el archivo de entrada
        	if (args.length>1) {
        		System.out.println("El programa sólo admite un parámetro, sólo procesará el primer archivo enviado como parámetro");
        	}
        	procesarArchivo(args[0]);
        	Platform.exit();
        	 System.exit(0);
        } else { //Sino ejecutar app grafica
        	launch();
        }
    }

}