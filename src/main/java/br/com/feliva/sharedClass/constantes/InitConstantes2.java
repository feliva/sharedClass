package br.com.feliva.sharedClass.constantes;

import java.io.*;
import java.util.Properties;

public class InitConstantes2 {

	private static final Properties config = new Properties();

	static {
		String nameFileContantes = "config_sifw.properties";
		try {
			InputStream configfile = InitConstantes2.class.getClassLoader().getResourceAsStream(nameFileContantes);
	        if (configfile != null) {
				config.load(configfile);
	        }
		} catch (Exception e) {
			System.err.println("Não foi encontrado arquivo de configuração de constantes: " + nameFileContantes);
			System.exit(1);
		}
	}


	static public final String CONFIG_PATH 			= config.getProperty("PATH_INSTALL") + File.separator;
	static public final String IMAGEM_PATH 			= CONFIG_PATH + config.getProperty("imagem.path") + File.separator;
	static public final String IMAGEM_EXTENSAO 		= ".png";
	static public final int    OIDC_JWT_SIZE 		= 3072;
    static public final String OIDC_JWT_FILENAME 	= "pairKeyJWK.json";
    static public final String OIDC_JWK_PATH 		= CONFIG_PATH;
    static public final String OIDC_ISSUR 			= config.getProperty("oidc.issur");
}
