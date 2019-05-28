package br.lucascarvalho.services;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import br.lucascarvalho.entidade.BancoDeDados;
import br.lucascarvalho.entidade.CSVUtils;
import br.lucascarvalho.entidade.Cidade;

/**
 * O objetivo dessa classe é simular uma base de dados. Através de configurações no arquivo web.xml ela será
 * acionada no startup do Tomcat, permitindo que a aplicação possua uma base de dados única durante a execução 
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
