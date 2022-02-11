package Prueba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
 
/**
   * Clase de herramientas de descarga de descarga de FTP
 */
public class FtpUtil {
 
	/** 
	  * Descripción: Cargar archivos al servidor FTP 
	  * @Param Host FTP Server HostName 
	  * @Param Port FTP Server Port 
	  * @Param username FTP Cuenta de inicio de sesión 
	  * @Param Password FTP Iniciar sesión Contraseña 
	  * @Param BASEPATH FTP Server Base Directorio
	  * @Param FilePath FTP Server File Ruta de almacenamiento. El camino del archivo es BASEPATH + FILEPATH
	  * @Param filename Sube los nombres de archivos en el servidor FTP 
	  * @Param entrada de entrada flujo 
	  * @Return devuelve con éxito, de lo contrario devuelve FALSO 
	 */  
	public boolean uploadFile(String host, int puerto, String username, String password, String basePath,
			 String filename, InputStream input) throws IOException {
		boolean result = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			 ftp.connect (host, puerto); // Conecte el servidor FTP
			 // Si usa el puerto predeterminado, puede usar ftp.connect (host) para conectar directamente el servidor FTP.
			 ftp.login(username, password); // Iniciar sesión
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return result;
			}
			 // Cambia al directorio de carga
			if (!ftp.changeWorkingDirectory(basePath)) {
				 // Si el directorio no tiene un directorio de creación
				String[] dirs = basePath.split("/");
				String tempPath = basePath;
				for (String dir : dirs) {
					if (null == dir || "".equals(dir)) continue;
					tempPath += "/" + dir;
					 if (! ftp.changeWorkingDirectory (tempPath)) {// facturado al directorio, lo que indica que el directorio no existe
						 if (! ftp.makeDirectory (tempPath)) {// crea un directorio
							 // Si el directorio de archivo de creación falla, vuelve
							 System.out.println ("Crear directorio de archivos" + tempPath + "Fallo");
							return result;
						} else {
							 // existe el directorio, luego ingrese el directorio directamente
							ftp.changeWorkingDirectory(tempPath);	
						}
					}
				}
			}
			 // Establecer el tipo de archivo de carga al tipo binario
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			 			//subir archivos 
			if (!ftp.storeFile(filename, input)) {
				return result;
			}
			input.close();
			ftp.logout();
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
                            ftp.disconnect();
			}
		}
		return result;
	}
	
	/** 
	  * Descripción: Descargar archivo desde el servidor FTP 
	  * @Param Host FTP Server HostName 
	 * @Param Port FTP Server Port 
	  * @Param username FTP Cuenta de inicio de sesión 
	  * @Param Password FTP Iniciar sesión Contraseña 
	  * @Param REMOTEPATH FTP Server Ruta relativa 
	  * @Param filename para descargar el nombre del archivo 
	  * @Param localpath descargas a caminos locales 
	 * @return 
	 */  
	public boolean downloadFile(String host, int port, String username, String password, String remotePath,
			String fileName, String localPath) throws IOException {
		boolean result = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(host, port);
			 // Si usa el puerto predeterminado, puede usar ftp.connect (host) para conectar directamente el servidor FTP.
			 ftp.login (username, password); // Iniciar sesión
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return result;
			}
			 ftp.changeWorkingDirectory (remotePath); // Transferencia al directorio del servidor FTP
			FTPFile[] fs = ftp.listFiles();
			for (FTPFile ff : fs) {
				if (ff.getName().equals(fileName)) {
					File localFile = new File(localPath + "/" + ff.getName());
 
					OutputStream is = new FileOutputStream(localFile);
					ftp.retrieveFile(ff.getName(), is);
					is.close();
				}
			}
 
			ftp.logout();
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
                            ftp.disconnect();
			}
		}
		return result;
	}

}