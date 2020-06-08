package Progra1;

import java.util.ArrayList;
import java.util.List;

public class Entidades {
	
	private int indice;
	private String nombre;
	private int cantidad;
	private long posicion;
	private byte[] bytesNombre;
	private int bytes = 1; 
	
	private List<Atributos> atributos;
	
	public int getIndice() {
		return indice;
	}
	
	public void setIndice(int indice) {
		this.indice = indice;
	}
	//////////////////////////////////////////////////////////
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
		bytesNombre = new byte[30]; 
		
		for (int i = 0; i < nombre.length(); i++) {
			bytesNombre[i] = (byte)nombre.charAt(i);
		}
	}
	//////////////////////////////////////////////////////////
	public byte[] getBytesNombre() {
		return bytesNombre;
	}
	
	public void setBytesNombre(byte[] bytesNombre) {
		this.bytesNombre = bytesNombre;
		nombre = new String(bytesNombre);
	}
	//////////////////////////////////////////////////////////
	public int getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	//////////////////////////////////////////////////////////
	public List<Atributos> getAtributos() {
		return atributos;
	}
	
	public void setAtributos(List<Atributos> atributos) {
		this.atributos = atributos;
	}
	
	public void setAtributos(Atributos atributo) {
		if (this.atributos == null) {
			this.atributos = new ArrayList<>();
		}
		this.atributos.add(atributo);
		this.cantidad = this.atributos.size();
	}
	//////////////////////////////////////////////////////////
	public void removeAtributo(Atributos atributo) {
		if (this.atributos != null) {
			if (this.atributos.size() > 0) {
				this.atributos.remove(atributo);
				this.cantidad = this.atributos.size();
			}
		}
	}
	//////////////////////////////////////////////////////////
	public long getPosicion() {
		return posicion;
	}
	
	public void setPosicion(long posicion) {
		this.posicion = posicion;
	}
	//////////////////////////////////////////////////////////
	public int getBytes() {	
		bytes = 1;
		for (Atributos atributo : atributos) {
			bytes += atributo.getBytes();
		}
		return bytes;
	}
	
	public void setBytes(int bytes) {
		this.bytes = bytes;
	}
}