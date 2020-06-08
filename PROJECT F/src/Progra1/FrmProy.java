package Progra1;

import java.awt.EventQueue;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import java.awt.Font;

public class FrmProy extends JFrame {
	Scanner sc = new Scanner(System.in);
	RandomAccessFile Archivo;
	RandomAccessFile Entidades;
	RandomAccessFile Atributos;
	private List<Entidades>lista_Entidades = new ArrayList<>();
	private final int totalBytes = 83 , bytesEntidades = 47 , bytesAtributos = 43;
	private final static String formatoFecha = "dd/MM/yyyy";
	static DateFormat format = new SimpleDateFormat(formatoFecha);
	private JPanel contentPane;
	private JTable tbData;
	private DefaultTableModel tableModel;	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmProy frame = new FrmProy();
					frame.setVisible(true);
					if(frame.Abrir_Archivo()) {
						frame.menuDefinicion(true);
					}else {
						frame.menuDefinicion(false);
					}
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private boolean Abrir_Archivo() {
		boolean respuesta = false;
		try {
			Entidades = new RandomAccessFile("Entidades.txt","rw");
			Atributos = new RandomAccessFile("Atributos.txt", "rw");
			
			long longitud = Entidades.length();
			if(longitud <= 0) {
				System.out.println("El archivo se encuentra vacio");
				respuesta = false;
			}
			if(longitud >= bytesEntidades) {
				
				Entidades.seek(0);
				Entidades e;
				while(longitud >= bytesEntidades) {
					e = new Entidades();
					e.setIndice(Entidades.readInt());
					byte[] bNombre = new byte[30];
					Entidades.read(bNombre);
					e.setBytesNombre(bNombre);
					e.setCantidad(Entidades.readInt());
					e.setBytes(Entidades.readInt());
					e.setPosicion(Entidades.readLong());
					Entidades.readByte();
					longitud -= bytesEntidades;
					
					long longitudAtributos = Atributos.length();
					if(longitudAtributos <=0) {
						System.out.println("No hay registros");
						respuesta = false;
						break;
					}
					Atributos.seek(e.getPosicion());
					Atributos a;
					longitudAtributos = e.getCantidad() * bytesAtributos;
					while(longitudAtributos >= bytesAtributos) {
						a = new Atributos();
						a.setIndice(Atributos.readInt());
						byte[]bNombreAtributo = new byte[30];
						Atributos.read(bNombreAtributo);
						a.setBytesNombre(bNombreAtributo);
						a.setValorTipoDato(Atributos.readInt());
						a.setLongitud(Atributos.readInt());
						a.setNombreTipoDato();
						Atributos.readByte();
						e.setAtributos(a);
						longitudAtributos -= bytesAtributos;
					}
					lista_Entidades.add(e);
				}
				if(lista_Entidades.size()>0) {
					respuesta = true;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return respuesta;
	}

	private void cerrarArchivo() throws Exception{
		if(Archivo != null && Entidades != null && Atributos != null) {
			Archivo.close();
			Entidades.close();
			Atributos.close();
		}
	}
	
	private boolean Grabar_Registro(Entidades entidad){
		boolean resultado = false;
		try {
			Archivo.seek(Archivo.length());
			boolean valido;
			byte[]bytesString;
			String tmpString = "";
			for(Atributos atributo : entidad.getAtributos()) {
				valido = false;
				System.out.println("Ingrese "+ atributo.getNombre().trim());
				while(!valido) {
					try {
						switch (atributo.getTipoDato()) {
						case INT:
							int tmpInt = sc.nextInt();
							Archivo.writeInt(tmpInt);
							sc.nextLine();
							break;
						case LONG:
							long tmpLong = sc.nextLong();
							Archivo.writeLong(tmpLong);
							break;
						case STRING:
							int longitud = 0;
							do {
								tmpString = sc.nextLine();
								longitud = tmpString.length();
								if(longitud <= 1| longitud > atributo.getLongitud()) {
									System.out.println("La longitud de " + atributo.getNombre().trim()
											+ " no es valida [1 - "+atributo.getLongitud()+"]");
								}
							}while(longitud <=0 || longitud > atributo.getLongitud());
							
							bytesString = new byte[atributo.getLongitud()];
							
							for(int i = 0; i < tmpString.length(); i++) {
								bytesString[i] = (byte) tmpString.charAt(i);
							}
							Archivo.write(bytesString);
							break;
						case DOUBLE:
							double tmpDouble = sc.nextDouble();
							Archivo.writeDouble(tmpDouble);
							break;
						case FLOAT:
							float tmpFloat = sc.nextFloat();
							Archivo.writeFloat(tmpFloat);
							break;
						case DATE:
							Date date = null;
							tmpString = "";
							while (date == null) {
								System.out.println("Formato de fecha: " +formatoFecha);
								tmpString = sc.nextLine();
								date = strintToDate(tmpString);
							}
							bytesString = new byte[atributo.getBytes()];
							for (int i = 0; i < tmpString.length(); i++) {
								bytesString[i] = (byte) tmpString.charAt(i);
							}
							Archivo.write(bytesString);
							break;
						case CHAR:
							do {
								tmpString = sc.nextLine();
								longitud = tmpString.length();
								if(longitud < 1 || longitud > 1) {
									System.out.println("Solo se permite un caracter");
								}
							}while(longitud < 1 || longitud > 1);
								byte caracter = (byte) tmpString.charAt(0);
								Archivo.writeByte(caracter);
								break;
							}
							valido = true;
						}catch(Exception e) {
							System.out.println(
									"Error "+e.getMessage()+ " al capturar tipo de dato, vuelva a ingresar el valor: ");
							sc.hasNextLine();
						}
					}
				}
				Archivo.write("\n".getBytes());
				resultado = true;
			}catch(Exception e) {
				resultado = false;
				System.out.println("Error al agregar el registro "+e.getMessage());
			}
			return resultado;
		}
	
	public Date strintToDate(String strFecha) {
		Date date = null;
		try {
			date = format.parse(strFecha);
		} catch (Exception e) {
			date = null;
			System.out.println("Error en fecha: " + e.getMessage());
		}
		return date;
	}
	
	public String dateToString(Date date) {
		String strFecha;
		strFecha = format.format(date);
		return strFecha;
	}
	
	private String formarNombreFichero(String nombre) {
		return nombre.trim() + ".dat";
	}
	
	private boolean Ingresar_Entidad() {
		boolean resultado = false;
		String auxNombre;
		int longitud = 0;
		//sc.nextLine();
		try {
			Entidades entidad = new Entidades();
			entidad.setIndice(lista_Entidades.size() +1);
			do {
				auxNombre = JOptionPane.showInputDialog(null,"Ingrese el nombre de la entidad");
				longitud = auxNombre.length();
				if(longitud<2 || longitud > 30) {
					JOptionPane.showMessageDialog(null, "La cantidad de los caracteres no es correcta(3 - 30)");
				}else {
					if(auxNombre.contains(" ")){
						JOptionPane.showMessageDialog(null, "El nombre no puede contener espacios, sustituya por guion bajo(underscore)");
						longitud = 0;
					}
				}
			}while (longitud < 2 || longitud > 30);
			entidad.setNombre(auxNombre);
			JOptionPane.showMessageDialog(null,"Atributos de la entidad");
			int bndDetener = 0;
			do {
				Atributos atributo = new Atributos();
				atributo.setIndice(entidad.getIndice());
				longitud = 0;
				auxNombre = JOptionPane.showInputDialog(null, "Escriba el nombre del atributo No. "+(entidad.getCantidad()+1));
				do {
					
					longitud = auxNombre.length();
					if(longitud < 2 || longitud > 30) {
						JOptionPane.showMessageDialog(null, "La cantida de los caracteres no es correcta (3-30)");
					}else {
						if(auxNombre.contains(" ")) {
							JOptionPane.showMessageDialog(null, "El nombre no puede contener espacios, sustituya por guion bajo");
							longitud = 0;
						}
					}
				}while ( longitud < 2 || longitud > 30);
				atributo.setNombre(auxNombre);
				int valor = Integer.parseInt(JOptionPane.showInputDialog(null, "Seleccione el tipo de dato:"
						+"\n"+TipoDato.INT.getValue()+".........."+TipoDato.INT.name()
						+"\n"+TipoDato.LONG.getValue()+".........."+TipoDato.LONG.name()
						+"\n"+TipoDato.STRING.getValue()+".........."+TipoDato.STRING.name()
						+"\n"+TipoDato.DOUBLE.getValue()+".........."+TipoDato.DOUBLE.name()
						+"\n"+TipoDato.FLOAT.getValue()+".........."+TipoDato.FLOAT.name()
						+"\n"+TipoDato.DATE.getValue()+".........."+TipoDato.DATE.name()
						+"\n"+TipoDato.CHAR.getValue()+".........."+TipoDato.CHAR.name()));
				atributo.setValorTipoDato(valor);
				if(atributo.isRequiereLongitud()) {
					int lg = Integer.parseInt(JOptionPane.showInputDialog(null,"Ingrese la longitud"));
					atributo.setLongitud(lg);
				}else {
					atributo.setLongitud(0);
				}
				atributo.setNombreTipoDato();
				entidad.setAtributos(atributo);
				bndDetener = Integer.parseInt(JOptionPane.showInputDialog(null," Si desea agregar otro atributo ingrese cualquier numero"
						+ " de lo cantrario 0"));
			}while(bndDetener != 0);
			JOptionPane.showMessageDialog(null,"Los datos a registrar son: ");
			Mostrar_Entidad(entidad);
			longitud = Integer.parseInt(JOptionPane.showInputDialog(null,"Presione 1 para guardar 0 para cancelar"));
			
			if (longitud ==1 ) {			
				entidad.setPosicion(Atributos.length());
				Atributos.seek(Atributos.length());
				for(Atributos atributo : entidad.getAtributos()) {
					Atributos.writeInt(atributo.getIndice());;
					Atributos.write(atributo.getBytesNombre());
					Atributos.writeInt(atributo.getValorTipoDato());
					Atributos.writeInt(atributo.getLongitud());
					Atributos.write("\n".getBytes());
				}
				Entidades.writeInt(entidad.getIndice());
				Entidades.write(entidad.getBytesNombre());
				Entidades.writeInt(entidad.getCantidad());
				Entidades.writeInt(entidad.getBytes());
				Entidades.writeLong(entidad.getPosicion());
				Entidades.write("\n".getBytes());
				lista_Entidades.add(entidad);
				resultado = true;
			} else {
				JOptionPane.showMessageDialog(null, "Entidad no guardada");
				resultado = false;
			}
		}catch(Exception e) {
			resultado = false;
			e.printStackTrace();
		}
		return resultado;
	}
	
	private void Modificar_Entidad() {
		try {
			int indice = 0;
			while (indice < 1 || indice > lista_Entidades.size()) {
				for(Entidades entidad : lista_Entidades) {
					JOptionPane.showInputDialog(null,entidad.getIndice()+ "........" + entidad.getNombre());
					//System.out.println(entidad.getIndice() +" ........ " + entidad.getNombre());
				}
				System.out.println("Seleccione la entidad que desea modificar");
				indice = sc.nextInt();
				sc.nextLine();
			}
			Entidades entidad = new Entidades();
			for(Entidades e : lista_Entidades) {
				if(indice == e.getIndice()) {
					entidad = e;
					break;
				}
			}
			String nombreFichero = formarNombreFichero(entidad.getNombre());
			Archivo = new RandomAccessFile(nombreFichero, "rw");
			long longitudDatos = Archivo.length();
			
			if(longitudDatos >0) {
				System.out.println("No es posible modificar la entidad debido a que ya tiene datos asociados");
			}else {
				
				boolean bndEncontrado = false, bndModificado = false;
				
				Entidades.seek(0);
				long longitud = Entidades.length();
				int registros = 0, salir = 0, i;
				Entidades e;
				byte[] tmpBytes;
				Archivo.close();
				while(longitud > totalBytes) {
					e= new Entidades();
					e.setIndice(Entidades.readInt());
					tmpBytes = new byte[30];
					Entidades.read(tmpBytes);
					e.setBytesNombre(tmpBytes);
					e.setCantidad(Entidades.readInt());
					e.setBytes(Entidades.readInt());
					e.setPosicion(Entidades.readLong());
					if(entidad.getIndice() == e.getIndice()) {
						System.out.println("Si no desea modificar el campo presione enter");
						System.out.println("Ingrese el nombre");
						String tmpStr;
						sc.nextLine();
						int len = 0;
						long posicion;
						do {
							tmpStr = sc.nextLine();
							len = tmpStr.length();
							if(len == 1 || len > 30) {
								System.out.println("La longitud del nombre no es valida [2-30");
							}
						}while (len == 1 || len > 30);
						if(len > 0) {
							e.setNombre(tmpStr);
							posicion = registros * totalBytes;
							Archivo.seek(posicion);
							
							
							Archivo.write(e.getBytesNombre());
							bndModificado = true;
						}
						for (Entidades el : lista_Entidades) {
							System.out.println("Modificando entidad" + e);
							System.out.println(el.getNombre().trim());
						}
						
						break;
					}
					registros++;
					
					longitud -= totalBytes;
				}
			}
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	private void Mostrar_Entidad(Entidades entidad) {
		
		System.out.println("Indice:" + entidad.getIndice());
		System.out.println("Nombre:" + entidad.getNombre());
		System.out.println("Cantidad de atributos: "+entidad.getCantidad());
		System.out.println("Atributos:");
		int i = 1;
		for(Atributos atributo : entidad.getAtributos()) {
			System.out.println("\tNo. " +i);
			System.out.println("\tNombre: "+atributo.getNombre());
			System.out.println("\tTipo de dato: " +atributo.getNombreTipoDato());
			
			System.out.println("\tLongitud: " + atributo.getLongitud());
			tableModel.addRow(new Object[] {i, entidad.getNombre(),atributo.getNombre(), atributo.getNombreTipoDato(),atributo.getLongitud()});
			
			
			i++;
		}
	}
	
	public void listar_Registros(Entidades entidad) {
		try {
			long longitud = Archivo.length();
			if(longitud <= 0) {
				JOptionPane.showMessageDialog(null,"No hay registros");
				return;
			}
			Archivo.seek(0);
			byte[]tmpArrayByte;
			String linea = "";
			for(Atributos atributo : entidad.getAtributos()) {
				linea += atributo.getNombre().toString().trim()+ "\t\t";
				tableModel = new DefaultTableModel();
				tableModel.addColumn(linea);
			}
			System.out.println(linea);
			while (longitud >= entidad.getBytes()) {
				linea = "";
				for (Atributos atributo : entidad.getAtributos()) {
					switch (atributo.getTipoDato()) {
					case INT:
						int tmpInt = Archivo.readInt();
						linea += String.valueOf(tmpInt) + "\t\t";
						break;
					case LONG:
						long tmpLong = Archivo.readLong();
						linea += String.valueOf(tmpLong) + "\t\t";
						break;
					case STRING:
						tmpArrayByte = new byte[atributo.getLongitud()];
						Archivo.read(tmpArrayByte);
						String tmpString = new String(tmpArrayByte);
						linea += tmpString.trim() + "\t\t";
						break;
					case DOUBLE:
						double tmpDouble = Archivo.readDouble();
						linea += String.valueOf(tmpDouble) + "\t\t";
						break;
					case FLOAT:
						float tmpFloat = Archivo.readFloat();
						linea += String.valueOf(tmpFloat) + "\t\t";
						break;
					case DATE:
						tmpArrayByte = new byte[atributo.getBytes()];
						Archivo.read(tmpArrayByte);
						tmpString = new String(tmpArrayByte);
						linea += tmpString.trim() + "\t\t";
						break;
					case CHAR:
						char tmpChar = (char) Archivo.readByte();
						linea += tmpChar + "\t\t";
						break;
					}
				}
				Archivo.readByte();
				tableModel.addRow(new Object[] {linea });
				longitud -= entidad.getBytes();
				System.out.println(linea+ " " +longitud);
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	private boolean Borrar_Archivos() {
		boolean res = false;
		try {
			File file;
			for(Entidades entidad : lista_Entidades) {
				file = new File(entidad.getNombre().trim() + ".dat");
				if(file.exists()) {
					file.delete();
				}
				file = null;
			}
			file = new File("Atributos.txt");
			if(file.exists()) {
				file.delete();
			}
			file = null;
			file = new File("Entidades.txt");
			if(file.exists()) {
				file.delete();
			}
			file = null;
			res = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private void iniciar(int indice) {
		int opcion = 0;
		String nombreFichero = "";
		try {
			Entidades entidad = null;
			for(Entidades e : lista_Entidades) {
				if (indice == e.getIndice()) {
					nombreFichero = (e.getNombre()+ ".data");
					entidad = e;
					break;
				}
			}
			Archivo = new RandomAccessFile(nombreFichero, "rw");
			System.out.println("Bienvenido (a)");
			Atributos a = entidad.getAtributos().get(0);
			do {
				try {
					System.out.println("Seleccione su opcion");
					System.out.println("1.\t\tAgregar");
					System.out.println("2.\t\tListar");
					System.out.println("3.\t\tBuscarr");
					System.out.println("4.\t\tModificar");
					System.out.println("0.\t\tMenu anterior");
					opcion = sc.nextInt();
					switch(opcion) {
					case 0:
						System.out.println("");
						break;
					case 1:
						Grabar_Registro(entidad);
						break;
					case 2:
						listar_Registros(entidad);
						break;
					case 3:
						System.out.println("Se hara la busqueda en la primera columna: ");
						System.out.println("Ingrese " + a.getNombre().trim() +"a buscar");
						break;
					case 4:
						System.out.println("Ingrese el carne a modificar: ");
						break;
						default:
							System.out.println("Opcion no valida");
							break;
					}
				}catch(Exception e) {
					System.out.println("Error: " +e.getMessage());
				}
			}while(opcion !=0);
			Archivo.close();
		}catch (Exception e) {
			System.out.println("Error: " +e.getMessage());
		}
	}

	private void menuDefinicion(boolean mostrarAgregarRegistro) throws Exception{
		int opcion = 1;
		while (opcion !=0) {
			opcion = Integer.parseInt(JOptionPane.showInputDialog(null, "Elija su opcion"
					+"\n1..........Agregar Entidad"
					+"\n2..........Modificar Entidad"
					+"\n3..........Listar Entidades"
					+"\n4..........Agregar Registros"
					+"\n5..........Borrar bases de datos"
					+"\n0..........Salir"));
			switch(opcion) {
			case 0:
				JOptionPane.showMessageDialog(null, "Gracias por usar la aplicacion");
				break;
			case 1:
				if(Ingresar_Entidad()) {
					JOptionPane.showMessageDialog(null, "Entidad agregada con exito");
					mostrarAgregarRegistro = true;
				}
				break;
			case 2:
				Modificar_Entidad();
				break;
			case 3:
				if(lista_Entidades.size() >0) {
					while(tableModel.getRowCount() > 0) {
						tableModel.removeRow(0);
					}
					int tmpInt = 0;
					tmpInt = Integer.parseInt(JOptionPane.showInputDialog(null, "Desea imprimir los detalles?"
							+ " Si, presione 1. No, presione 0?"));
					if(tmpInt == 1) {
						for (Entidades entidad : lista_Entidades) {
							Mostrar_Entidad(entidad);
						}
					}else {
						for (Entidades entidad : lista_Entidades) {
							System.out.println("Indice: " + entidad.getIndice());
							System.out.println("Nombre: " + entidad.getNombre());
							System.out.println("Cantidad de atributos: " + entidad.getCantidad());
						}
					}
				}else {
					System.out.println("No hay entidades registradas");
				}
				break;
			case 4:
				int indice = 0;
				while (indice < 1 || indice > lista_Entidades.size()) {
					for(Entidades entidad : lista_Entidades) {
						Integer.parseInt(JOptionPane.showInputDialog(null,entidad.getIndice()+"......." + entidad.getNombre()));
					}
					System.out.println("Seleccione la entidad que esea trabajar");
					indice = sc.nextInt();
				}
				iniciar(indice);
				break;
			case 5:
				int confirmar = 0;
				confirmar = Integer.parseInt(JOptionPane.showInputDialog(null, "Esta seguro de querer borrar la base de datos, de ser si presione 1 de lo contrario 0"));
				if(confirmar == 1) {
					cerrarArchivo();
					if(Borrar_Archivos()) {
						lista_Entidades = null;
						lista_Entidades = new ArrayList<>();
						mostrarAgregarRegistro = false;

						System.out.println("Archivos borrados");
					}
				}
				break;
				default:
				System.out.println("Opcion no valida");
				break;
			}
		}
	}
	
	public FrmProy() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 561, 344);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		tableModel = new DefaultTableModel();
		tableModel.addColumn("INDICE");
		tableModel.addColumn("ENTIDAD");
		tableModel.addColumn("NOMBRE");
		tableModel.addColumn("Dato");
		tableModel.addColumn("LONGITUD");
		tbData = new JTable();
		tbData.setModel(tableModel);
		JScrollPane scrollPane = new JScrollPane(tbData);
		scrollPane.setBounds(10, 81, 525, 213);
		contentPane.add(scrollPane);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(196, 46, 6, 20);
		contentPane.add(textPane);
		
		JLabel lblNewLabel = new JLabel("Datos Ingresados");
		lblNewLabel.setFont(new Font("Times New Roman", Font.ITALIC, 39));
		lblNewLabel.setBounds(143, 11, 293, 55);
		contentPane.add(lblNewLabel);
	}
}