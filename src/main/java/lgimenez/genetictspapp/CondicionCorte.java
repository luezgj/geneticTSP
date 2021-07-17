package lgimenez.genetictspapp;

public abstract class CondicionCorte {
	protected AlgoritmoGenetico algoritmo=null;
	
	public abstract boolean alcanzada();
	
	public void setAlgoritmo(AlgoritmoGenetico algoritmo) {
		this.algoritmo = algoritmo;
	}
	
	public int getCantidadGeneraciones() {
		//Cuando no se sabe cuántas generaciones van a ser, se da un número fijo (para poder crear arreglos)
		return 1000;
	}
}
