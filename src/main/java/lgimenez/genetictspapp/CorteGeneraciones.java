package lgimenez.genetictspapp;

public class CorteGeneraciones extends CondicionCorte {
	private int numeroGeneracion=0;
	
	public int getNumeroGeneracion() {
		return numeroGeneracion;
	}

	public void setNumeroGeneracion(int numeroGeneracion) {
		this.numeroGeneracion = numeroGeneracion;
	}
	
	@Override
	public int getCantidadGeneraciones() {
		return numeroGeneracion;
	}
	
	@Override
	public boolean alcanzada() {
		// TODO Auto-generated method stub
		return algoritmo.getGeneracion()>=numeroGeneracion;
	}

	@Override
	public String toString() {
		return "CorteGeneraciones[Generacion=" + numeroGeneracion + "]";
	}

}
