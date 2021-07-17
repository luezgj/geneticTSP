package lgimenez.genetictspapp;

public class CorteCosto extends CondicionCorte {
	private int costo=0;

	public int getCosto() {
		return costo;
	}

	public void setCosto(int costo) {
		this.costo = costo;
	}

	@Override
	public boolean alcanzada() {
		// TODO Auto-generated method stub
		return algoritmo.getMejor().getCosto()<=costo;
	}

	@Override
	public String toString() {
		return "CorteCosto[costo="+ costo + "]";
	}

}
