package br.lucascarvalho.services;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import br.lucascarvalho.entidade.BancoDeDados;
import br.lucascarvalho.entidade.CSVUtils;
import br.lucascarvalho.entidade.Cidade;

/**
 * O objetivo dessa classe � simular uma base de dados. Atrav�s de configura��es no arquivo web.xml ela ser�
 * acionada no startup do Tomcat, permitindo que a aplica��o possua uma base de dados �nica durante a execu��o 
 * @author lucas carvalho
 *
 */
public class StartupServlet implements javax.servlet.ServletContextListener {
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		new BancoDeDados();
	}

}
