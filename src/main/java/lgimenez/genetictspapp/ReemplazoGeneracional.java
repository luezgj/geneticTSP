package lgimenez.genetictspapp;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.List;

public class ReemplazoGeneracional implements MetodoSeleccionPoblacion{

	@Override
	public void seleccionarPoblacion(Reference<List<Solucion>> poblacionAnterior, List<Solucion> descendencia) {
		// TODO Auto-generated method stub
		poblacionAnterior =new SoftReference<List<Solucion>>(descendencia);
	}

	@Override
	public String toString() {
		return "ReemplazoGeneracional";
	}
}
