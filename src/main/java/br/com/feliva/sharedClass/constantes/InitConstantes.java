package br.com.feliva.sharedClass.constantes;

import java.io.*;
import java.util.Properties;

public class InitConstantes {

	//PATH é alterado por script de instalacao config/wildfly/install.sh rode ele

	
	private static Properties config = new Properties();

	static {
		String nameFileContantes = "config_sifw.properties";
		try {
			InputStream configfile = InitConstantes.class.getClassLoader().getResourceAsStream(nameFileContantes);
	        if (configfile != null) {
				config.load(configfile);
	        }
		} catch (Exception e) {
			System.err.println("Não foi encontrado arquivo de configuração de constantes: " + nameFileContantes);
			System.exit(1);
		}
	}


	static public final String CONFIG_PATH 			= config.getProperty("PATH_INSTALL");
	static public final String IMAGEM_PATH 			= CONFIG_PATH + config.getProperty("imagem.path") + File.separator;
	static public final String IMAGEM_EXTENSAO 		= config.getProperty("imagem.extencao");
	static public final int    OIDC_JWT_SIZE 		= Integer.parseInt(config.getProperty("oidc.jwk.size"));
    static public final String OIDC_JWT_FILENAME 	= config.getProperty("oidc.jwk.filename");
    static public final String OIDC_JWK_PATH 		= CONFIG_PATH;
    static public final String OIDC_ISSUR 			= config.getProperty("oidc.issur");
}
