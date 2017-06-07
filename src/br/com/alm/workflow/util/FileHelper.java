package br.com.alm.workflow.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Utilitarios para gravação de arquivo
 * 
 * @author EYVEVU - Fabio Escobar
 *
 */
public class FileHelper {
	
	/** LOG */
	static final Logger LOG = Logger.getLogger(FileHelper.class);

	/**
	 * Escrever uma lista de String em arquivo de acordo com um tamanho de buffer (para controle de flush).
	 *
	 * @param path
	 *            caminho onde será salvo
	 * @param values
	 *            valores para o arquivo
	 * @param bufferInterval
	 *            intervalo para flush do buffer
	 * @throws IOException
	 *             erro generico
	 * @author EYVEVU
	 */
	public static void writeToFile(final String path, final List<String> values, final int bufferInterval)
			throws IOException {

		//try-with-resources (J7)
		try (FileWriter file = new FileWriter(path, true); BufferedWriter out = new BufferedWriter(file)) {

			int valorOriginal = 0;
			for (int contadorLista = 0; contadorLista < values.size(); contadorLista++) {
				
				valorOriginal = contadorLista;
				contadorLista = contadorLista * bufferInterval;
				
				for (int contadorBuffer = 0; contadorBuffer < bufferInterval; contadorBuffer++) {
					if (contadorBuffer + contadorLista >= values.size()) {
						break;
					}
					out.write(values.get(contadorBuffer + contadorLista));
					out.newLine();
				}

				out.flush();
				if (contadorLista + bufferInterval >= values.size()) {
					break;
				}
				contadorLista = valorOriginal;
			}

		} catch (IOException e) {
			LOG.error("Erro ao gravar arquivo: " + e.getMessage());
		}
	}

}
