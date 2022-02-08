package Prueba;

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.util.Properties;  
import java.util.Vector;  
  
import org.apache.commons.io.IOUtils;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
  
import com.jcraft.jsch.Channel;  
import com.jcraft.jsch.ChannelSftp;  
import com.jcraft.jsch.JSch;  
import com.jcraft.jsch.JSchException;  
import com.jcraft.jsch.Session;  
import com.jcraft.jsch.SftpException; 
/** 
* Clase Descripción Herramientas SFTP
*/
public class SFTPUtil {
    private transient Logger log = LoggerFactory.getLogger(this.getClass());  
    
    private ChannelSftp sftp;  
        
    private Session session;  
         // ** Nombre de usuario de inicio de sesión SFTP * /    
    private String username; 
         // ** Contraseña de inicio de sesión SFTP * /    
    private String password;  
         // ** llave privada * /    
    private String privateKey;  
         // ** Dirección IP de la dirección del servidor SFTP * /    
    private String host;  
         // ** Puerto SFTP * /  
    private int port;  
        
    
    /**  
           * Construir un objeto SFTP basado en la autenticación de contraseña  
     */    
    public SFTPUtil(String username, String password, String host, int port) {  
        this.username = username;  
        this.password = password;  
        this.host = host;  
        this.port = port;  
    } 
    
    /**  
           * Construye objetos SFTP basados ​​en la autenticación de clave 
     */  
    public SFTPUtil(String username, String host, int port, String privateKey) {  
        this.username = username;  
        this.host = host;  
        this.port = port;  
        this.privateKey = privateKey;  
    }  
    
    public SFTPUtil(){}  
    
    
    /** 
           * Conecte el servidor SFTP 
     */  
    public void login(){  
        try {  
            JSch jsch = new JSch();  
            if (privateKey != null) {  
                                 jsch.addIdentity (privateKey); // Establecer la clave privada  
            }  
    
            session = jsch.getSession(username, host, port);  
           
            if (password != null) {  
                session.setPassword(password);    
            }  
            Properties config = new Properties();  
            config.put("StrictHostKeyChecking", "no");  
                
            session.setConfig(config);  
            session.connect();  
              
            Channel channel = session.openChannel("sftp");  
            channel.connect();  
    
            sftp = (ChannelSftp) channel;  
        } catch (JSchException e) {  
            e.printStackTrace();
        }  
    }    
    
    /** 
           * Cerrar conexión de conexión  
     */  
    public void logout(){  
        if (sftp != null) {  
            if (sftp.isConnected()) {  
                sftp.disconnect();  
            }  
        }  
        if (session != null) {  
            if (session.isConnected()) {  
                session.disconnect();  
            }  
        }  
    }  
 
    
    /**  
           * Cargue los datos del flujo de entrada a SFTP como un archivo. Patrimonio FOCAL = directorio BASEPATH +
           * El camino básico de @param Basepath Server 
           * @Param directorio Sube a este directorio  
           * @Param SFTPFILENAME SMFP FIN DE FINILA NOMBRE  
           * @Param en flujo de entrada  
     */  
    public void upload(String basePath,String directory, String sftpFileName, InputStream input) throws SftpException{  
        try {   
            sftp.cd(basePath);
            sftp.cd(directory);  
        } catch (SftpException e) { 
                          // el directorio no existe, luego crea una carpeta
            String [] dirs=directory.split("/");
            String tempPath=basePath;
            for(String dir:dirs){
            	if(null== dir || "".equals(dir)) continue;
            	tempPath+="/"+dir;
            	try{ 
            		sftp.cd(tempPath);
            	}catch(SftpException ex){
            		sftp.mkdir(tempPath);
            		sftp.cd(tempPath);
            	}
            }
        }  
                 sftp.put(input, sftpFileName); // Subir archivo
    } 
    
 
    /** 
           * descargar archivo.
           * @Param directorio de descarga de directorio  
           * @Param DownloadFile Descargar archivo 
           * @Param SaveFile existe camino local 
     */    
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException{  
        if (directory != null && !"".equals(directory)) {  
            sftp.cd(directory);  
        }  
        File file = new File(saveFile);  
        sftp.get(downloadFile, new FileOutputStream(file));  
    }  
    
    /**  
           * descargar archivo   
           * @Param directorio de descarga de directorio 
           * @Param DownloadFile Descargar Nombre del archivo 
           * @Return Byte Array 
     */  
    public byte[] download(String directory, String downloadFile) throws SftpException, IOException{  
        if (directory != null && !"".equals(directory)) {  
            sftp.cd(directory);  
        }  
        InputStream is = sftp.get(downloadFile);  
          
        byte[] fileData = IOUtils.toByteArray(is);  
          
        return fileData;  
    }  
    
    
    /** 
           * Borrar archivos   
           * @Param directorio para eliminar el archivo donde se encuentra el archivo 
           * @Param DeleteFile para eliminar archivos 
     */  
    public void delete(String directory, String deleteFile) throws SftpException{  
        sftp.cd(directory);  
        sftp.rm(deleteFile);  
    }  
    
    
    /** 
           * Enumere los archivos en el directorio. 
           * @Param directorio para enumerar el directorio 
     * @param sftp 
     */  
    public Vector<?> listFiles(String directory) throws SftpException {  
        return sftp.ls(directory);  
    }  
      
         // Subir prueba de archivo
    /*public static void main(String[] args) throws SftpException, IOException {  
                 SFTPUtil sftp = new SFTPUtil ("Nombre de usuario", "Contraseña", "Dirección IP", 22);  
        sftp.login();  
                 File file = new File ("D: \\imagen \\ t0124dd095ceb042322.jpg");  
        InputStream is = new FileInputStream(file);  
          
                 sftp.upload("Ruta básica", "Ruta de archivo", "test_sftp.jpg", is);  
        sftp.logout();  
    }  */
}